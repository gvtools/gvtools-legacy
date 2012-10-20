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
package com.iver.cit.gvsig.geoprocess.impl.lineorpolygontopoints.fmap;

import java.awt.geom.Point2D;
import java.rmi.server.UID;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.ShapePointExtractor;
import org.gvsig.topology.Messages;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.drivers.featureiterators.FeatureBitsetIterator;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.AbstractMonitorableGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.util.SnappingCoordinateMap;
import com.iver.utiles.swing.threads.CancellableProgressTask;
import com.vividsolutions.jts.geom.Coordinate;

/**
 * Geoprocess to convert a linear vectorial layer in a point vectorial layer.
 * 
 * @author Alvaro Zabala
 * 
 */
public class LineOrPolygonToPointsGeoprocess extends
		AbstractMonitorableGeoprocess {

	private static Logger logger = Logger
			.getLogger(LineOrPolygonToPointsGeoprocess.class.getName());

	private LayerDefinition resultLayerDefinition;

	private SnappingCoordinateMap coordinateMap;

	public LineOrPolygonToPointsGeoprocess(FLyrVect inputLayer) {
		this.firstLayer = inputLayer;
	}

	public void checkPreconditions() throws GeoprocessException {
		int lyrDimensions;
		try {
			lyrDimensions = FGeometryUtil.getDimensions(firstLayer
					.getShapeType());

			if (lyrDimensions < 1)
				throw new GeoprocessException(
						"Geoproceso convertir polígonos o lineas a puntos con capa de puntos");

		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error intentando acceder al tipo de geometria de capa vectorial");
		}

	}

	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			ILayerDefinition resultLayerDefinition = new SHPLayerDefinition();
			resultLayerDefinition.setShapeType(XTypes.POINT);
			FieldDescription[] fields = new FieldDescription[1];
			fields[0] = new FieldDescription();
			fields[0].setFieldLength(10);
			fields[0].setFieldName("fid");
			fields[0].setFieldType(XTypes.BIGINT);

			resultLayerDefinition.setFieldsDesc(fields);
			return resultLayerDefinition;
		}
		return resultLayerDefinition;
	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean onlySelection = (Boolean) params.get("layer_selection");
		if (onlySelection != null)
			this.operateOnlyWithSelection = onlySelection.booleanValue();

		double clusterTolerance = 0d;
		Double clusterToleranceD = (Double) params.get("cluster_tolerance");
		if (clusterToleranceD != null)
			clusterTolerance = clusterToleranceD.doubleValue();
		this.coordinateMap = new SnappingCoordinateMap(clusterTolerance);
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

			int numNewFeatures = 0;
			while (featureIterator.hasNext()) {
				IFeature feature = featureIterator.next();
				IGeometry fmapGeo = feature.getGeometry();

				List<Point2D[]> pointsParts = ShapePointExtractor
						.extractPoints(fmapGeo);
				for (int i = 0; i < pointsParts.size(); i++) {
					Point2D[] points = pointsParts.get(i);
					for (int j = 0; j < points.length; j++) {
						Point2D pt = points[j];
						Coordinate coord = new Coordinate(pt.getX(), pt.getY());
						if (coordinateMap.containsKey(coord))
							continue;
						else {
							coordinateMap.put(coord, coord);
							IGeometry newGeometry = ShapeFactory.createPoint2D(
									coord.x, coord.y);
							Value fid = ValueFactory
									.createValue(numNewFeatures);
							Value relatedFid = ValueFactory.createValue(Integer
									.parseInt(feature.getID()));
							Value[] attrs = new Value[] { fid, relatedFid };
							DefaultFeature newFeature = new DefaultFeature(
									newGeometry, attrs, new UID().toString());
							featureProcessor.processFeature(newFeature);
							numNewFeatures++;
						}
					}// for j
				}// for i
				if (progressMonitor != null)
					progressMonitor.reportStep();
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
		} catch (Exception e) {
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
			progressMonitor.setNote(Messages.getText("lineToPoints_note"));
			progressMonitor.setStatusMessage(Messages
					.getText("LineToPoints_layer_message"));
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"error accediendo al numero de features de una layer", e);
		}
	}

}
