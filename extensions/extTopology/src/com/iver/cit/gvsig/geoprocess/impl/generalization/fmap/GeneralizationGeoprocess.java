/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.geoprocess.impl.generalization.fmap;

import java.rmi.server.UID;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.jts.JtsUtil;
import org.gvsig.topology.Messages;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.drivers.featureiterators.FeatureBitsetIterator;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.AbstractMonitorableGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.utiles.swing.threads.CancellableProgressTask;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Geoprocess to generalize linear or poligonal vectorial layers.
 * 
 * @author Alvaro Zabala
 * 
 */
public class GeneralizationGeoprocess extends AbstractMonitorableGeoprocess {

	private static Logger logger = Logger
			.getLogger(GeneralizationGeoprocess.class.getName());

	private LayerDefinition resultLayerDefinition;

	private boolean computeDouglasPeucker;

	private boolean computeTopologyPreservingSimplify;

	private double distTolerance = 0d;

	public GeneralizationGeoprocess(FLyrVect inputLayer) {
		this.firstLayer = inputLayer;
	}

	public void checkPreconditions() throws GeoprocessException {
		int lyrDimensions;
		try {
			lyrDimensions = FGeometryUtil.getDimensions(firstLayer
					.getShapeType());

			if (lyrDimensions == 0)
				throw new GeoprocessException(
						"Geoproceso generalizacion con capa de puntos");

		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error intentando acceder al tipo de geometria de capa vectorial");
		}

	}

	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			try {
				resultLayerDefinition = DefinitionUtils
						.createLayerDefinition(firstLayer);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return resultLayerDefinition;
	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean onlySelection = (Boolean) params.get("layer_selection");
		if (onlySelection != null)
			this.operateOnlyWithSelection = onlySelection.booleanValue();

		Boolean dp = (Boolean) params.get("dp");
		if (dp != null)
			computeDouglasPeucker = dp.booleanValue();

		Boolean tp = (Boolean) params.get("topologyPreserving");
		if (tp != null)
			computeTopologyPreservingSimplify = tp.booleanValue();

		Double dist = (Double) params.get("distTolerance");
		if (dist != null)
			distTolerance = dist.doubleValue();
	}

	public void process(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		if (progressMonitor != null) {
			initialize(progressMonitor);
		}

		// Prepare the result
		try {
			writer.preProcess();
		} catch (StartWriterVisitorException e) {
			throw new GeoprocessException(e);
		}
		FeaturePersisterProcessor2 featureProcessor = new FeaturePersisterProcessor2(
				writer);
		try {
			featureProcessor.start();
		} catch (StartVisitorException e1) {
			throw new GeoprocessException(e1);
		}

		try {
			IFeatureIterator featureIterator = null;
			if (this.operateOnlyWithSelection) {
				FBitSet selection = firstLayer.getRecordset().getSelection();
				featureIterator = new FeatureBitsetIterator(selection,
						firstLayer.getSource());
			} else {
				featureIterator = firstLayer.getSource().getFeatureIterator();
			}

			while (featureIterator.hasNext()) {
				IFeature feature = featureIterator.next();
				IGeometry fmapGeo = feature.getGeometry();
				Value[] values = feature.getAttributes();
				Geometry geometry = NewFConverter.toJtsGeometry(fmapGeo);
				Geometry simplifiedGeometry = null;
				if (this.computeDouglasPeucker) {
					simplifiedGeometry = JtsUtil.douglasPeuckerSimplify(
							geometry, distTolerance);
				} else if (this.computeTopologyPreservingSimplify) {
					simplifiedGeometry = JtsUtil.topologyPreservingSimplify(
							geometry, distTolerance);
				}

				if (progressMonitor != null)
					progressMonitor.reportStep();

				if (simplifiedGeometry != null) {
					IGeometry simplifiedFMap = NewFConverter
							.toFMap(simplifiedGeometry);
					DefaultFeature newFeature = new DefaultFeature(
							simplifiedFMap, values, new UID().toString());
					featureProcessor.processFeature(newFeature);
				}
			}
			featureProcessor.finish();
			if (progressMonitor != null) {
				progressMonitor.finished();
			}
		} catch (StopVisitorException e) {
			throw new GeoprocessException(
					"Error al finalizar el guardado de los resultados del geoproceso",
					e);
		} catch (BaseException e) {
			throw new GeoprocessException(
					"Error al acceder a la informacion del driver dentro del geoproceso",
					e);
		}
	}

	public void initialize(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		try {
			progressMonitor.setInitialStep(0);
			int numOfSteps = firstLayer.getSource().getShapeCount();
			progressMonitor.setFinalStep(numOfSteps);
			progressMonitor.setDeterminatedProcess(true);
			progressMonitor.setNote(Messages.getText("voronoi_diagram_note"));
			progressMonitor.setStatusMessage(Messages
					.getText("voronoi_diagram_layer_message"));
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"error accediendo al numero de features de una layer", e);
		}
	}

}
