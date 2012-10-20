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
package com.iver.cit.gvsig.geoprocess.impl.referencing.fmap;

import java.rmi.server.UID;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.referencing.ReferencingUtil;
import org.gvsig.topology.Messages;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.drivers.featureiterators.FeatureBitsetIterator;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.AbstractMonitorableGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.threads.CancellableProgressTask;

/**
 * Geoprocess to spatial adjust or reference a vectorial layer
 * 
 * @author Alvaro Zabala
 * 
 */
public class ReferencingGeoprocess extends AbstractMonitorableGeoprocess {
	private static Logger logger = Logger.getLogger(ReferencingGeoprocess.class
			.getName());

	private LayerDefinition resultLayerDefinition;

	private MathTransform mathTransform;

	private boolean hasAuxiliarLyrs = false;

	private FLyrVect[] auxiliarLyrs = null;

	public ReferencingGeoprocess(FLyrVect inputLyr) {
		firstLayer = inputLyr;
	}

	@Override
	public void checkPreconditions() throws GeoprocessException {
		try {
			int numShapes = firstLayer.getSource().getShapeCount();
			if (numShapes == 0)
				throw new GeoprocessException("capa de entrada sin features");
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error de driver al tratar de acceder al numero de elementos",
					e);
		}

		if (mathTransform == null)
			throw new GeoprocessException(
					"No se ha especificado un metodo de transformacion espacial");
	}

	@Override
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

	@Override
	public void setParameters(Map params) throws GeoprocessException {
		mathTransform = (MathTransform) params.get("mathTransform");
		hasAuxiliarLyrs = ((Boolean) params.get("hasAuxiliar")).booleanValue();
		if (hasAuxiliarLyrs) {
			this.auxiliarLyrs = (FLyrVect[]) params.get("auxiliarLyrs");
		}
	}

	@Override
	public void initialize(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		try {
			progressMonitor.setInitialStep(0);
			int numOfSteps = firstLayer.getSource().getShapeCount();
			progressMonitor.setFinalStep(numOfSteps);
			progressMonitor.setDeterminatedProcess(true);
			progressMonitor.setNote(Messages
					.getText("referencing_geoprocess_note"));
			progressMonitor.setStatusMessage(Messages
					.getText("referencing_geoprocess_layer_message"));
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"error accediendo al numero de features de una layer", e);
		}

	}

	@Override
	public void process(CancellableProgressTask progressMonitor)
			throws GeoprocessException {

		/*
		 * ++++++++++++++++++++++++++ FIXME: MOVER A CLASE ABSTRACTA
		 */
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

		/*
		 * +++++++++++++++++
		 */

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
				IGeometry transformedGeometry = ReferencingUtil.getInstance()
						.createTransformedGeometry(fmapGeo, null, null,
								this.mathTransform);
				if (progressMonitor != null)
					progressMonitor.reportStep();

				if (transformedGeometry != null) {
					DefaultFeature newFeature = new DefaultFeature(
							transformedGeometry, values, new UID().toString());
					featureProcessor.processFeature(newFeature);
				}
			}// while
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
		} catch (TransformException e) {
			throw new GeoprocessException(
					"Error de transformacion de coordenadas", e);
		} catch (FactoryException e) {
			throw new GeoprocessException(
					"Error al tratar de construir una transformacion de coordenadas",
					e);
		}

	}

	@Override
	public FLayer getResult() throws GeoprocessException {
		MapContext map = ((View) PluginServices.getMDIManager()
				.getActiveWindow()).getModel().getMapContext();

		// first of all, we look for an existing flayers container
		FLayers rootLyrs = map.getLayers();
		String folderName = PluginServices.getText(this,
				"SPATIAL_ADJUST_SESSION")
				+ " "
				+ ReferencingUtil.getInstance()
						.getNumberOfSpatialAdjustSessions();
		FLayers adjustSessionLyrs = (FLayers) rootLyrs.getLayer(folderName);
		if (adjustSessionLyrs == null) {
			adjustSessionLyrs = new FLayers();
			adjustSessionLyrs.setMapContext(map);
			adjustSessionLyrs.setParentLayer(map.getLayers());
			adjustSessionLyrs.setName(folderName);
			System.err
					.println("Error, geoproceso ajuste espacial sin capa de links");
		}
		if (hasAuxiliarLyrs && auxiliarLyrs != null) {
			int position = 0;
			for (int i = 0; i < auxiliarLyrs.length; i++) {
				adjustSessionLyrs.addLayer(position, auxiliarLyrs[i]);
				position++;
			}
		}

		adjustSessionLyrs.addLayer(super.getResult());

		map.invalidate();
		return adjustSessionLyrs;
	}

}
