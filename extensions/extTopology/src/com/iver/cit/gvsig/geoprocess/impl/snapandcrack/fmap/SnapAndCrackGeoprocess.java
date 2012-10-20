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
package com.iver.cit.gvsig.geoprocess.impl.snapandcrack.fmap;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.jts.GeometryCollapsedException;
import org.gvsig.jts.GeometryCracker;
import org.gvsig.jts.GeometrySnapper;
import org.gvsig.topology.Messages;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.AbstractMonitorableGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.utiles.swing.threads.CancellableProgressTask;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This geoprocess snaps and cracks all the features of a vectorial layers with
 * the rest of features of the same layers and different layers. <br>
 * By 'snap' we mean move a vertex to make coincident with other vertices. <br>
 * By 'crack' we mean insert new vertices in a geometry if these vertices are in
 * other geometries and the distance with the geometry is less than snap
 * tolerance.
 * 
 * @author Alvaro Zabala
 * 
 */
public class SnapAndCrackGeoprocess extends AbstractMonitorableGeoprocess {

	private List<FLyrVect> crackingLayers;

	private double snapTolerance;
	/**
	 * Snaps points of a same geometry and between geometries
	 */
	private GeometrySnapper snapper;

	private GeometryCracker cracker;

	private static Logger logger = Logger
			.getLogger(SnapAndCrackGeoprocess.class.getName());

	public SnapAndCrackGeoprocess(FLyrVect inputLayer, List<FLayer> layers) {
		super();
		this.firstLayer = inputLayer;

		this.crackingLayers = new ArrayList<FLyrVect>();
		for (int i = 0; i < layers.size(); i++) {
			FLayer layer = (FLayer) layers.get(i);
			if (layer instanceof FLyrVect) {
				crackingLayers.add((FLyrVect) layer);
			}
		}// for

		// we add the input layer to the cracking layers collection to snap and
		// crack a geometry
		// with the neighbours geometries of its same layer.
		crackingLayers.add(this.firstLayer);

	}

	public void checkPreconditions() throws GeoprocessException {
		if (crackingLayers.size() == 0)
			throw new GeoprocessException("Crack y snap sin capas de entrada");

	}

	public ILayerDefinition createLayerDefinition() {
		try {
			return DefinitionUtils.createLayerDefinition(firstLayer);
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void setParameters(Map params) throws GeoprocessException {
		Double snapTol = (Double) params.get("snap_tolerance");
		if (snapTol != null) {
			this.snapTolerance = snapTol.doubleValue();
			snapper = new GeometrySnapper(snapTolerance);
			cracker = new GeometryCracker(snapTolerance);
		} else {
			throw new GeoprocessException(
					"Buffer por dist constante sin distancia");
		}
	}

	public void process(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		if (progressMonitor != null) {
			initialize(progressMonitor);
		}

		// Prepare the result
		// schemaManager.createSchema(createLayerDefinition());
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

		IFeatureIterator iterator;
		try {
			iterator = firstLayer.getSource().getFeatureIterator();
		} catch (ReadDriverException e) {
			throw new GeoprocessException(e);
		}

		try {
			while (iterator.hasNext()) {
				if (progressMonitor != null) {
					if (progressMonitor.isCanceled()) {
						this.cancel();
						break;
					}
					progressMonitor.reportStep();
				}// if

				IFeature feature = iterator.next();
				IGeometry geomA = feature.getGeometry();
				if (geomA == null)// ignore features without geometry
					continue;
				Rectangle2D envA = geomA.getBounds2D();
				Geometry jtsGeom = NewFConverter.toJtsGeometry(geomA);

				int nPointsA = jtsGeom.getNumPoints();

				// first of all: we snap the geometry with itself
				try {
					jtsGeom = snapper.snap(jtsGeom);
				} catch (GeometryCollapsedException e) {
					logger.error(e);
					/*
					 * GeometrySnapper launchs this exception when the geometry
					 * is collapsed by applying snap tolerance.
					 * 
					 * Crack and snap geoprocess doesnt solve this
					 */
					// featureProcessor.processFeature(feature);
					// continue;
				}
				ArrayList<Geometry> crackingGeometries = new ArrayList<Geometry>();
				for (int i = 0; i < crackingLayers.size(); i++) {
					FLyrVect crackLyr = crackingLayers.get(i);
					IFeatureIterator iterator2 = crackLyr.getSource()
							.getFeatureIterator(envA, null, null, false);
					while (iterator2.hasNext()) {
						IFeature feature2 = iterator2.next();
						IGeometry geom2 = feature2.getGeometry();
						if (geom2 == null)// ignore features without geometry
							continue;
						Geometry jts2 = geom2.toJTSGeometry();
						crackingGeometries.add(jts2);
					}// while iterator2
				}// for cracking layers

				Geometry[] crackingGeoms = new Geometry[crackingGeometries
						.size()];
				crackingGeometries.toArray(crackingGeoms);

				// then, we snap the geometry with the rest of geometries
				// FIXME Usar los weights (ultimos dos parametros)
				jtsGeom = snapper.snapWith(jtsGeom, crackingGeoms);

				// after that, we apply a crack process
				for (int i = 0; i < crackingGeoms.length; i++) {
					jtsGeom = cracker
							.crackGeometries(jtsGeom, crackingGeoms[i]);
				}

				if (jtsGeom == null)
					throw new GeoprocessException("Error!!!!");

				feature.setGeometry(NewFConverter.toFMap(jtsGeom));

				featureProcessor.processFeature(feature);

			}// while

			featureProcessor.finish();
		} catch (ReadDriverException e) {
			throw new GeoprocessException(e);
		} catch (VisitorException e) {
			throw new GeoprocessException(e);
		}

	}

	public void initialize(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		try {
			progressMonitor.setInitialStep(0);
			int numOfSteps = firstLayer.getSource().getShapeCount();
			progressMonitor.setFinalStep(numOfSteps);
			progressMonitor.setDeterminatedProcess(true);
			progressMonitor.setNote(Messages
					.getText("snap_and_crack_layer_note"));
			progressMonitor.setStatusMessage(Messages
					.getText("snap_and_crack_layer_message"));
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"error accediendo al numero de features de una layer", e);
		}
	}

}
