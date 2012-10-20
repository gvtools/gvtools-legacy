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
package com.iver.cit.gvsig.geoprocess.impl.polytolines.fmap;

import java.awt.geom.Rectangle2D;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.jts.JtsUtil;
import org.gvsig.topology.Messages;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.drivers.featureiterators.FeatureBitsetIterator;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
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
import com.iver.cit.gvsig.geoprocess.core.util.JTSFacade;
import com.iver.utiles.swing.threads.CancellableProgressTask;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Geoprocess to convert polygonal vectorial layers in linear vectorial layers.
 * 
 * @author Alvaro Zabala
 * 
 */
public class PolyToLinesGeoprocess extends AbstractMonitorableGeoprocess {

	private static Logger logger = Logger.getLogger(PolyToLinesGeoprocess.class
			.getName());

	private LayerDefinition resultLayerDefinition;

	// removed for problems with equals - hashCode use of HashMap. TODO: REVIEW
	// IT
	// private Map<PairOfFeatures, PairOfFeatures> processedPairs = new
	// HashMap<PairOfFeatures, PairOfFeatures>();

	private FBitSet processedFeatures = new FBitSet();

	public PolyToLinesGeoprocess(FLyrVect inputLayer) {
		this.firstLayer = inputLayer;
	}

	public void checkPreconditions() throws GeoprocessException {
		int lyrDimensions;
		try {
			lyrDimensions = FGeometryUtil.getDimensions(firstLayer
					.getShapeType());

			if (lyrDimensions != 2)
				throw new GeoprocessException(
						"Geoproceso poligonos a lineas con capa que no es de poligonos");

		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error intentando acceder al tipo de geometria de capa vectorial");
		}

	}

	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			ILayerDefinition resultLayerDefinition = new SHPLayerDefinition();
			resultLayerDefinition.setShapeType(XTypes.LINE);
			FieldDescription[] fields = new FieldDescription[2];
			fields[0] = new FieldDescription();
			fields[0].setFieldLength(10);
			fields[0].setFieldName(PluginServices.getText(this, "fid1"));
			fields[0].setFieldType(XTypes.BIGINT);

			fields[1] = new FieldDescription();
			fields[1].setFieldLength(10);
			fields[1].setFieldName(PluginServices.getText(this, "fid2"));
			fields[1].setFieldType(XTypes.BIGINT);

			resultLayerDefinition.setFieldsDesc(fields);
			return resultLayerDefinition;
		}
		return resultLayerDefinition;
	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean onlySelection = (Boolean) params.get("layer_selection");
		if (onlySelection != null)
			this.operateOnlyWithSelection = onlySelection.booleanValue();
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
				String fid = feature.getID();
				int idFirstFeature;
				try {
					idFirstFeature = Integer.parseInt(feature.getID());
				} catch (NumberFormatException e) {
					logger.error("Feature " + fid
							+ " tiene un ID que no refleja el orden", e);
					continue;
				}

				processedFeatures.set(idFirstFeature);
				Geometry geometry = NewFConverter.toJtsGeometry(fmapGeo);
				Polygon[] polygons = JtsUtil.extractPolygons(geometry);

				List<LineString> rings = new ArrayList<LineString>();
				for (int i = 0; i < polygons.length; i++) {
					rings.addAll(Arrays.asList(JtsUtil
							.extractRings(polygons[i])));
				}

				// for each original linestring we calculated the overlapped
				// regions
				Geometry[] overlappedGeometries = new Geometry[rings.size()];

				Rectangle2D bounds = fmapGeo.getBounds2D();
				IFeatureIterator neighbourhood = firstLayer.getSource()
						.getFeatureIterator(bounds, null, null, false);

				while (neighbourhood.hasNext()) {
					IFeature neighbour = null;
					String idStr = null;
					int id;
					try {
						neighbour = neighbourhood.next();
						idStr = neighbour.getID();

						if (idStr.equals(fid))
							continue;
						id = Integer.parseInt(idStr);
						// PairOfFeatures pairOfFeatures = new PairOfFeatures(
						// firstLayer, idFirstFeature, firstLayer, id);
						//
						// if(processedPairs.containsValue(pairOfFeatures))
						// continue;
						//
						// if (processedPairs.get(pairOfFeatures) != null) {
						// continue;
						// }

						Geometry geometry2 = NewFConverter
								.toJtsGeometry(neighbour.getGeometry());
						Polygon[] polygons2 = JtsUtil
								.extractPolygons(geometry2);
						List<LineString> rings2 = new ArrayList<LineString>();
						for (int i = 0; i < polygons2.length; i++) {
							rings2.addAll(Arrays.asList(JtsUtil
									.extractRings(polygons2[i])));
						}

						for (int i = 0; i < rings.size(); i++) {

							LineString line = rings.get(i);

							for (int j = 0; j < rings2.size(); j++) {

								LineString line2 = rings2.get(j);

								if (line.overlaps(line2)) {
									Geometry intersection = JTSFacade
											.intersection(line, line2);
									if (!JTSFacade.checkNull(geometry)) {

										if (intersection instanceof GeometryCollection) {
											intersection = JtsUtil
													.convertToMultiLineString((GeometryCollection) intersection);
										}

										if (!processedFeatures.get(id)) {
											// If id feature was processed yet,
											// these intersections (shared
											// geometry) were already generated.
											IGeometry fmapInters = NewFConverter
													.toFMap(intersection);
											Value fid1 = ValueFactory
													.createValue(idFirstFeature);
											Value fid2 = ValueFactory
													.createValue(id);
											Value[] newValues = new Value[] {
													fid1, fid2 };
											DefaultFeature newFeature = new DefaultFeature(
													fmapInters, newValues,
													new UID().toString());
											featureProcessor
													.processFeature(newFeature);
										}

										if (overlappedGeometries[i] == null) {
											overlappedGeometries[i] = intersection;
										} else {
											try {
												overlappedGeometries[i] = JTSFacade
														.union(overlappedGeometries[i],
																intersection);
											} catch (IllegalArgumentException e) {
												e.printStackTrace();
											}
										}
									}// checkNull

								}// if line overlaps line2
							}// for j
						}// for i

						// processedPairs.put(pairOfFeatures, pairOfFeatures);
					} catch (NumberFormatException e) {
						logger.error("Feature " + idStr
								+ " tiene un ID que no refleja el orden", e);
						continue;
					}
				}// while neighbours features

				// at the end, we save all those linear fragment from first
				// feature that
				// werent covered by neighbours feature
				for (int i = 0; i < rings.size(); i++) {
					LineString ringN = rings.get(i);
					Geometry overlapN = overlappedGeometries[i];
					Geometry nonOverlapped = null;
					if (overlapN != null)
						nonOverlapped = JTSFacade.difference(ringN, overlapN);
					else
						nonOverlapped = ringN;

					if (!JTSFacade.checkNull(nonOverlapped)) {
						Value id1 = ValueFactory.createValue(idFirstFeature);
						Value id2 = ValueFactory.createValue(0);
						Value[] newValues = new Value[] { id1, id2 };

						DefaultFeature newFeature = new DefaultFeature(
								NewFConverter.toFMap(nonOverlapped), newValues,
								new UID().toString());
						featureProcessor.processFeature(newFeature);
					}

				}// for i

				if (progressMonitor != null)
					progressMonitor.reportStep();

			}// while featureIterator.hasNext

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
			progressMonitor.setNote(Messages.getText("polygon_to_lines_note"));
			progressMonitor.setStatusMessage(Messages
					.getText("polygon_to_lines_message"));
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"error accediendo al numero de features de una layer", e);
		}
	}

}
