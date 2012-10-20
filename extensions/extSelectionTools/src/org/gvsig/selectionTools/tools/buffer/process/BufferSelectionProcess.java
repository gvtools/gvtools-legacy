package org.gvsig.selectionTools.tools.buffer.process;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.incrementabletask.IncrementableProcess;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.selectionTools.tools.buffer.gui.BufferConfigurationPanel;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.impl.buffer.fmap.BufferGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.buffer.fmap.BufferVisitor;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * 
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class BufferSelectionProcess extends IncrementableProcess {
	// private boolean layerWasBeingEdited = false;

	private MapControl mapControl = null;
	private byte pol_side = -1;
	private byte line_side = -1;
	private byte point_side = -1;
	private byte multi_point_side = -1;
	private short selectedDistanceUnit = -1;
	private FLyrVect[] layers = null;
	private final double f_width;
	// private boolean showBufferLayers = false;
	private boolean showInfluenceAreasLayers = false;
	private boolean multiLayerSelection = false;
	private IMonitorableTask task1 = null;

	// /**
	// * Creates a new <p>BufferSelectionProcess</p>.
	// *
	// * @param title of the progress dialog
	// * @param label the label that explains the process
	// * @param mapControl reference to the current active view's
	// <code>MapControl</code>.
	// * @param pol_side side of the buffer in a polyline layer: {@link
	// BufferConfigurationPanel#OUTSIDE BufferConfigurationPanel#OUTSIDE},
	// * {@link BufferConfigurationPanel#INSIDE
	// BufferConfigurationPanel#INSIDE}, or {@link
	// BufferConfigurationPanel#OUTSIDE_AND_INSIDE
	// BufferConfigurationPanel#OUTSIDE_AND_INSIDE}
	// * @param line_side side of the buffer in a line layer: {@link
	// BufferConfigurationPanel#OUTSIDE_AND_INSIDE
	// BufferConfigurationPanel#OUTSIDE_AND_INSIDE}
	// * @param point_side side of the buffer in a point layer: {@link
	// BufferConfigurationPanel#OUTSIDE BufferConfigurationPanel#OUTSIDE}
	// * @param multi_point_side side of the buffer in a multi point layer:
	// {@link BufferConfigurationPanel#OUTSIDE BufferConfigurationPanel#OUTSIDE}
	// * @param width buffer's width
	// * @param selectedDistanceUnit distance unit selected
	// * @param activeLayers current active view's active layers
	// * @param showBufferLayers determines if will show the layers with the
	// buffers as new temporal layers
	// * @param showInfluenceAreasLayers determines if will show the layers with
	// the influence areas as new temporal layers
	// * @param multiLayerSelection determines if the selection in each active
	// layer affects the other
	// */
	/**
	 * Creates a new
	 * <p>
	 * BufferSelectionProcess
	 * </p>
	 * .
	 * 
	 * @param title
	 *            of the progress dialog
	 * @param label
	 *            the label that explains the process
	 * @param mapControl
	 *            reference to the current active view's <code>MapControl</code>
	 *            .
	 * @param pol_side
	 *            side of the buffer in a polyline layer:
	 *            {@link BufferConfigurationPanel#OUTSIDE
	 *            BufferConfigurationPanel#OUTSIDE},
	 *            {@link BufferConfigurationPanel#INSIDE
	 *            BufferConfigurationPanel#INSIDE}, or
	 *            {@link BufferConfigurationPanel#OUTSIDE_AND_INSIDE
	 *            BufferConfigurationPanel#OUTSIDE_AND_INSIDE}
	 * @param line_side
	 *            side of the buffer in a line layer:
	 *            {@link BufferConfigurationPanel#OUTSIDE_AND_INSIDE
	 *            BufferConfigurationPanel#OUTSIDE_AND_INSIDE}
	 * @param point_side
	 *            side of the buffer in a point layer:
	 *            {@link BufferConfigurationPanel#OUTSIDE
	 *            BufferConfigurationPanel#OUTSIDE}
	 * @param multi_point_side
	 *            side of the buffer in a multi point layer:
	 *            {@link BufferConfigurationPanel#OUTSIDE
	 *            BufferConfigurationPanel#OUTSIDE}
	 * @param width
	 *            buffer's width
	 * @param selectedDistanceUnit
	 *            distance unit selected
	 * @param activeLayers
	 *            current active view's active layers
	 * @param showBufferLayers
	 *            determines if will show the layers with the buffers as new
	 *            temporal layers
	 * @param multiLayerSelection
	 *            determines if the selection in each active layer affects the
	 *            other
	 */
	// public BufferSelectionProcess(String title, String label, MapControl
	// mapControl, byte pol_side, byte line_side, byte point_side, byte
	// multi_point_side, double width, short selectedDistanceUnit, FLyrVect[]
	// activeLayers, boolean showBufferLayers, boolean showInfluenceAreasLayers,
	// boolean multiLayerSelection) {
	public BufferSelectionProcess(String title, String label,
			MapControl mapControl, byte pol_side, byte line_side,
			byte point_side, byte multi_point_side, double width,
			short selectedDistanceUnit, FLyrVect[] activeLayers,
			boolean showInfluenceAreasLayers, boolean multiLayerSelection) {
		super(title);

		this.label = label;
		this.mapControl = mapControl;
		this.pol_side = pol_side;
		this.line_side = line_side;
		this.point_side = point_side;
		this.multi_point_side = multi_point_side;
		this.f_width = width;
		this.selectedDistanceUnit = selectedDistanceUnit;
		this.layers = activeLayers;
		// this.showBufferLayers = showBufferLayers;
		this.showInfluenceAreasLayers = showInfluenceAreasLayers;
		this.multiLayerSelection = multiLayerSelection;
		this.isPausable = true;
	}

	/**
	 * Sets the object that will display the evolution of this loading process
	 * as a progress dialog.
	 * 
	 * @param iTask
	 *            the object that will display the evolution of this loading
	 *            process
	 */
	public void setIncrementableTask(IncrementableTask iTask) {
		this.iTask = iTask;
		iTask.setAskCancel(true);
		iTask.getButtonsPanel().addAccept();
		iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, false);

		JButton jButton = iTask.getButtonsPanel().getButton(
				ButtonsPanel.BUTTON_ACCEPT);
		jButton.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent
			 * )
			 */
			public void mouseClicked(MouseEvent e) {
				processFinalize();
			}
		});
	}

	//
	// /*
	// * (non-Javadoc)
	// * @see java.lang.Runnable#run()
	// */
	// public synchronized void run() {
	// String text = null;
	//
	// try {
	// process();
	// while (! ended) {
	// t0 += 500;
	// Thread.currentThread().sleep(150);
	// }
	// } catch (InterruptedException ie) {
	// iTask.getProgressPanel().setLabel(PluginServices.getText(null,
	// "Process_canceled"));
	//
	// if (cancel == false) {
	// text = PluginServices.getText(null, "Failed_the_process");
	// }
	// else {
	// log.addLine(PluginServices.getText(null, "Process_cancelled"));
	// JOptionPane.showMessageDialog(iTask.getButtonsPanel(),
	// PluginServices.getText(null, "Process_cancelled"),
	// PluginServices.getText(this, "Information"),
	// JOptionPane.INFORMATION_MESSAGE);
	// }
	// }
	// finally {
	// iTask.setAskCancel(false);
	// iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, true);
	// iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_CANCEL, false);
	// // iTask.stop();
	// // ended = true;
	//
	// if (text != null) {
	// log.addLine(PluginServices.getText(null, "Percent") + ": " +
	// getPercent());
	// log.addLine(text);
	// JOptionPane.showMessageDialog(iTask.getButtonsPanel(), text,
	// PluginServices.getText(this, "Error"), JOptionPane.ERROR_MESSAGE);
	// }
	//
	// if (percentage == 100)
	// iTask.getProgressPanel().setLabel(PluginServices.getText(null,
	// "Process_finished"));
	// // iTask.getProgressPanel().setPercent(100); // Forces setting the
	// progress bar at 100 %
	//
	// // Ends this process
	// ended = true;
	//
	// // Ends the progress panel
	// iTask.stop();
	// }
	// }

	/**
	 * Importation process.
	 * 
	 * @throws InterruptedException
	 *             if fails the process
	 */
	public void process() throws InterruptedException {
		percentage = 5;
		try {

			/* 2- Gets the distance relation */
			double relationDistanceUnitsToMeters = MapContext.CHANGEM[selectedDistanceUnit];
			percentage = 6;

			/* 3- Stores the new selections */
			FLyrVect layer = null; // , auxLayer = null;
			FBitSet bitsets[] = new FBitSet[layers.length];
			FBitSet copy_bitsets[] = new FBitSet[layers.length];
			FBitSet multiLayerbitsets[] = new FBitSet[layers.length];

			int size;

			for (int i = 0; i < layers.length; i++) {
				layer = layers[i];

				size = layer.getSource().getRecordset().getSelection().size();

				bitsets[i] = new FBitSet();
				bitsets[i].clear();
				bitsets[i].or(layer.getSource().getRecordset().getSelection());

				copy_bitsets[i] = new FBitSet();
				copy_bitsets[i].clear(0, size);
				copy_bitsets[i].or(layer.getSource().getRecordset()
						.getSelection());

				multiLayerbitsets[i] = new FBitSet();
				multiLayerbitsets[i].clear(0, size);
			}
			percentage = 9;

			/* 4- Gets the buffer and intersects to select new geometries */
			ReadableVectorial rv = null;
			IGeometry aux_geometry = null;
			double width = 1; // default value (not used)
			BufferGeoprocess bufferGeoprocess;
			// DifferenceGeoprocess differenceGeoprocess;
			HashMap params = null;
			File outputFile = null; // , outputFile2 = null;
			FLayers tocLayers = mapControl.getMapContext().getLayers();
			FLyrVect layerWithInfluenceAreas = null; // , layerWithBuffers =
														// null;
			CoordinateReferenceSystem tempCrs = null;
			double inc;
			byte side;
			long number = 0;
			ArrayList layersAdded = new ArrayList(); // Used to cancel the
														// process

			final short LAYER_NOT_REPROJECTED = 0;
			final short LAYER_REPROJECTED_TO_PLAIN_COORDINATES = 1;
			final short LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES = 2;

			short typeReprojection = LAYER_NOT_REPROJECTED;
			percentage = 11;

			params = new HashMap();
			params.put("layer_selection", new Boolean(true)); // Only selected
																// geometries
			params.put("dissolve_buffers", new Boolean(true)); // Default value
			params.put("strategy_flag", new Byte(
					BufferGeoprocess.CONSTANT_DISTANCE_STRATEGY));
			params.put("numRings", new Integer(1));
			// params.put("buffer_distance", new Double(width));
			params.put("cap", new Byte(BufferVisitor.CAP_ROUND));

			// Sets projection
			CoordinateReferenceSystem crs = ((View) PluginServices
					.getMDIManager().getActiveWindow()).getMapControl()
					.getViewPort().getCrs();
			params.put("projection", crs);

			// Sets distance units
			int distanceUnits = ((View) PluginServices.getMDIManager()
					.getActiveWindow()).getMapControl().getViewPort()
					.getDistanceUnits();
			params.put("distanceunits", new Integer(distanceUnits));

			// Sets map units
			int mapUnits = -1;
			if (crs instanceof ProjectedCRS) {
				mapUnits = ((View) PluginServices.getMDIManager()
						.getActiveWindow()).getMapControl().getViewPort()
						.getMapUnits();
			} else {
				mapUnits = 1;
			}
			params.put("mapunits", new Integer(mapUnits));

			percentage = 14;

			inc = (100 - percentage) / layers.length;

			/* 4.1- For each vector layer with geometries selected */
			for (int i = 0; i < layers.length; i++) {
				try {
					if (cancelProcess.isCanceled()) {
						throw new InterruptedException();
					}

					layer = layers[i];

					log.addLine(PluginServices.getText(null,
							"Starting_selection_of_layer")
							+ " \""
							+ layer.getName() + "\"");

					switch (layer.getShapeType()) {
					case FShape.POINT:
						side = point_side;
						break;
					case FShape.LINE:
						side = line_side;
						break;
					case FShape.POLYGON:
						side = pol_side;
						break;
					case FShape.MULTIPOINT:
						side = multi_point_side;
						break;
					case FShape.MULTI:
					case FShape.Z:
					case FShape.NULL:
					case FShape.TEXT:
					case FShape.CIRCLE:
					case FShape.ARC:
					case FShape.ELLIPSE:
						// UNSUPPORTED
						log.addLine(PluginServices.getText(null,
								"Layer_with_unsupported_geometries_type"));
						percentage += inc;
						continue;
					default: // UNDEFINED
						// UNSUPPORTED
						log.addLine(PluginServices.getText(null,
								"Layer_with_unsupported_geometries_type"));
						percentage += inc;
						continue;
					}

					/* 4.2- Calculates the width */
					if (cancelProcess.isCanceled()) {
						throw new InterruptedException();
					}

					// width = f_width * relationDistanceUnitsToMeters;
					CoordinateReferenceSystem layerCrs = layer.getCrs();
					CoordinateReferenceSystem mapCrs = mapControl.getViewPort()
							.getCrs();
					if (layerCrs.getName().equals(mapCrs.getName())) {
						// if (layer.getProjection() ==
						// mapControl.getViewPort().getProjection()) {
						/*
						 * 4.2.1- If the layer isn't projected -> Geographic
						 * coordinates (the default units are grades)
						 */
						if (!(layerCrs instanceof ProjectedCRS)) {
							log.addLine(PluginServices
									.getText(null,
											"Wont_select_geometries_on_the_layer_because_has_incompatible_projection")
									+ ": " + layer.getName());
							continue;
							// UNSUPORTED
							// width = (f_width *
							// relationDistanceUnitsToMeters);
							// width =
							// mapControl.getViewPort().fromMapDistance(width);
							// width = Geo.getDecimalDegrees(width);
							//
							// width = (f_width * relationDistanceUnitsToMeters)
							// * MapContext.CHANGEM[8]; // (units selected to
							// meters) * conversion to º
						} else {
							width = f_width * relationDistanceUnitsToMeters;
						}

						typeReprojection = LAYER_NOT_REPROJECTED;
					} else {
						/*
						 * 4.2.1.1- If the layer isn't projected -> Geographic
						 * coordinates (the default units are grades)
						 */
						/* 4.2.1.2- If the layer has been re-projected */
						if (!(mapControl.getCrs() instanceof ProjectedCRS)) {
							// UNSUPORTED
							if (!(layerCrs instanceof ProjectedCRS)) {
								log.addLine(PluginServices
										.getText(null,
												"Wont_select_geometries_on_the_layer_because_has_incompatible_projection")
										+ ": " + layer.getName());
								continue;
							}
							//
							// width = (f_width * relationDistanceUnitsToMeters)
							// * MapContext.CHANGEM[8]; // (units selected to
							// meters) * conversion to º
							// width = f_width * relationDistanceUnitsToMeters;

							// width = (f_width *
							// relationDistanceUnitsToMeters);
							// width =
							// mapControl.getViewPort().fromMapDistance(width);
							// width = Geo.getDecimalDegrees(width);

							typeReprojection = LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES;
						} else {
							width = f_width * relationDistanceUnitsToMeters;
							typeReprojection = LAYER_REPROJECTED_TO_PLAIN_COORDINATES;
						}

						/*
						 * 4.3- Fixes a bug with the geoprocesses when the layer
						 * is reprojected
						 */
						if (cancelProcess.isCanceled()) {
							throw new InterruptedException();
						}

						tempCrs = layerCrs;
						layer.setCrs(mapControl.getCrs());
					}

					/* 4.4- Sets the buffer width */
					params.put("buffer_distance", new Double(width));
					log.addLine(PluginServices.getText(null,
							"Buffer_information") + ":");

					/* 4.5- Shows width information */
					if (cancelProcess.isCanceled()) {
						if (tempCrs != null)
							layer.setCrs(tempCrs);

						throw new InterruptedException();
					}

					if (mapControl.getCrs() instanceof ProjectedCRS) {
						log.addLine("    "
								+ PluginServices.getText(null, "Buffer_width")
								+ ": " + width + " m.");
					} else {
						log.addLine("    "
								+ PluginServices.getText(null, "Buffer_width")
								+ ": " + width + " m");
						log.addLine("    "
								+ PluginServices.getText(null, "Buffer_width")
								+ ": " + width / MapContext.CHANGEM[8] + " º");
					}

					log.addLine("    "
							+ PluginServices.getText(null, "Buffer_cap") + ": "
							+ PluginServices.getText(null, "Round"));

					params.put("typePolBuffer", new Byte(side));

					switch (side) {
					case BufferVisitor.BUFFER_OUTSIDE_POLY:
						log.addLine("    "
								+ PluginServices.getText(null, "Side") + ": "
								+ PluginServices.getText(null, "Outside"));
						break;
					case BufferVisitor.BUFFER_INSIDE_POLY:
						log.addLine("    "
								+ PluginServices.getText(null, "Side") + ": "
								+ PluginServices.getText(null, "Inside"));
						break;
					case BufferVisitor.BUFFER_INSIDE_OUTSIDE_POLY:
						log.addLine("    "
								+ PluginServices.getText(null, "Side")
								+ ": "
								+ PluginServices.getText(null,
										"Outside_and_inside"));
						break;
					}

					/*
					 * 4.3- Creates the influence area using the
					 * BufferGeoprocess
					 */
					if (cancelProcess.isCanceled()) {
						if (tempCrs != null)
							layer.setCrs(tempCrs);

						throw new InterruptedException();
					}

					bufferGeoprocess = new BufferGeoprocess(layer);

					try {
						/* 4.3.1- Temporal file */
						number = 0;
						do {
							int index = layer.getName().lastIndexOf(".");

							if (index == -1) {
								outputFile = new File(
										System.getProperty("java.io.tmpdir")
												+ "/influence_areas_"
												+ layer.getName() + "_"
												+ number + ".shp");
							} else {
								outputFile = new File(
										System.getProperty("java.io.tmpdir")
												+ "/influence_areas_"
												+ layer.getName().substring(0,
														index) + "_" + number
												+ ".shp");
							}

							number++;
						} while (outputFile.exists());

						log.addLine(PluginServices.getText(null,
								"Creating_temp_file")
								+ ": \""
								+ outputFile.getAbsolutePath() + "\"");
					} catch (Exception e0) {
						/* Notifies the exception in the log */
						if (!cancelProcess.isCanceled()) {
							NotificationManager
									.showMessageError(
											PluginServices
													.getText(this,
															"failed_creating_the_temporal_layer"),
											e0);
							log.addLine(PluginServices.getText(null,
									"Failed_creating_the_temporal_layer")
									+ ": " + outputFile.getName());
							;
						}

						throw new InterruptedException();
					}

					if (cancelProcess.isCanceled()) {
						if (tempCrs != null)
							layer.setCrs(tempCrs);

						throw new InterruptedException();
					}

					try {
						/*
						 * 4.3.2- Sets the parameters of the new influence area
						 * geoProcess
						 */
						bufferGeoprocess.setParameters(params);
					} catch (GeoprocessException e1) {
						/* Notifies the exception in the log */
						if (!cancelProcess.isCanceled()) {
							NotificationManager.showMessageError(PluginServices
									.getText(this, "Error_fallo_geoproceso"),
									e1);
							log.addLine(PluginServices.getText(null,
									"Error_fallo_geoproceso"));
						}

						throw new InterruptedException();
					}

					/*
					 * 4.3.3- Creates the temporal shape layer with the
					 * influence areas
					 */
					if (cancelProcess.isCanceled()) {
						if (tempCrs != null)
							layer.setCrs(tempCrs);

						throw new InterruptedException();
					}

					SHPLayerDefinition definition = (SHPLayerDefinition) bufferGeoprocess
							.createLayerDefinition();
					definition.setFile(outputFile);
					ShpSchemaManager schemaManager = new ShpSchemaManager(
							outputFile.getAbsolutePath());
					IWriter writer = null;

					try {
						writer = getShpWriter(definition);
					} catch (Exception e2) {
						/* Notifies the exception in the log */
						if (!cancelProcess.isCanceled()) {
							NotificationManager.showMessageError(
									"Error_preparar_escritura_resultados", e2);
							log.addLine(PluginServices.getText(null,
									"Error_preparar_escritura_resultados"));
						}

						throw new InterruptedException();
					}

					/* 4.3.4- Sets the properties to create the result layer */
					if (cancelProcess.isCanceled()) {
						if (tempCrs != null)
							layer.setCrs(tempCrs);

						throw new InterruptedException();
					}

					bufferGeoprocess.setResultLayerProperties(writer,
							schemaManager);

					try {
						/*
						 * 4.3.5- Task of the geoProcess, creates an influence
						 * area with the parameters
						 */
						bufferGeoprocess.checkPreconditions();
						log.addLine(PluginServices.getText(null,
								"Creating_layer_with_influence_areas"));
						task1 = bufferGeoprocess.createTask();
						task1.run();
						layerWithInfluenceAreas = (FLyrVect) bufferGeoprocess
								.getResult();

						/* 4.3.6- Sets transparency */
						Color c = ((SimpleFillSymbol) ((SingleSymbolLegend) layerWithInfluenceAreas
								.getLegend()).getDefaultSymbol())
								.getFillColor();
						((SimpleFillSymbol) ((SingleSymbolLegend) layerWithInfluenceAreas
								.getLegend()).getDefaultSymbol())
								.setFillColor(new Color(c.getRed(), c
										.getGreen(), c.getBlue(), 180));

						log.addLine(PluginServices.getText(null,
								"Layer_with_influence_areas_created"));

						switch (typeReprojection) {
						case LAYER_NOT_REPROJECTED:
							break;
						case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES:
						case LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
							layer.setCrs(tempCrs);

							// Reprojects the layer
							// layerWithInfluenceAreas.reProject(mapControl);
							ViewPort vPort = mapControl.getViewPort();
							MathTransform transform = ProjectionUtils
									.getCrsTransform(layerCrs, vPort.getCrs());
							layer.setCrsTransform(transform);

							log.addLine(PluginServices.getText(null,
									"Layer_with_influence_areas_reprojected"));
							break;
						}

						/*
						 * 4.3.7- (Opcional) Adds the temporal layers with the
						 * influence areas
						 */
						if (showInfluenceAreasLayers) {
							tocLayers.addLayer(layerWithInfluenceAreas);
							layersAdded.add(layerWithInfluenceAreas); // Used to
																		// cancel
																		// the
																		// process
							log.addLine(PluginServices.getText(null,
									"Added_layer_with_influence_areas_to_TOC"));
						}
					} catch (Exception e3) {
						/* Notifies the exception in the log */
						if (!cancelProcess.isCanceled()) {
							NotificationManager.showMessageError(PluginServices
									.getText(null, "Error_fallo_geoproceso"),
									e3);
							log.addLine(PluginServices.getText(null,
									"Error_fallo_geoproceso"));
						}
						// else {
						// // Cancellation process: removes the layers added
						// for (int k = 0; k < layersAdded.size(); k++) {
						// tocLayers.removeLayer((FLayer) layersAdded.get(k));
						// }
						// }

						throw new InterruptedException();
					}
					//
					// /* 4.4- Creates the buffers using the
					// DifferenceGeoprocess */
					// differenceGeoprocess = new
					// DifferenceGeoprocess(layerWithInfluenceAreas);
					// differenceGeoprocess.setSecondOperand(layer);
					//
					// try {
					// /* 4.4.1- Temporal file */
					// number = 0;
					// do {
					// outputFile2 = new
					// File(System.getProperty("java.io.tmpdir") + "/buffers_" +
					// layer.getName() + "_" + number);
					// number ++;
					// }
					// while (outputFile2.exists());
					//
					//
					// log.addLine(PluginServices.getText(null,
					// "Creating_temp_file") + ": \"" +
					// outputFile2.getAbsolutePath() + "\"");
					// }
					// catch(Exception e0) {
					// /* Notifies the exception in the log */
					// NotificationManager.showMessageError(PluginServices.getText(this,
					// "failed_creating_the_temporal_layer"), e0);
					// log.addLine(PluginServices.getText(null,
					// "Failed_creating_the_temporal_layer") + ": " +
					// outputFile2.getName());;
					// throw new InterruptedException();
					// }
					//
					// try {
					// /* 4.4.2- Sets the parameters of the new influence area
					// geoProcess */
					// differenceGeoprocess.setParameters(params);
					// } catch (GeoprocessException e1) {
					// /* Notifies the exception in the log */
					// NotificationManager.showMessageError(PluginServices.getText(this,
					// "Error_fallo_geoproceso"), e1);
					// log.addLine(PluginServices.getText(null,
					// "Error_fallo_geoproceso"));
					// throw new InterruptedException();
					// }
					//
					// /* 4.4.3- Creates the temporal shape layer with the
					// influence areas */
					// definition = (SHPLayerDefinition)
					// differenceGeoprocess.createLayerDefinition();
					// definition.setFile(outputFile2);
					// schemaManager = new
					// ShpSchemaManager(outputFile2.getAbsolutePath());
					// writer = null;
					//
					// try {
					// writer = getShpWriter(definition);
					// } catch (Exception e2) {
					// /* Notifies the exception in the log */
					// NotificationManager.showMessageError("Error_preparar_escritura_resultados",
					// e2);
					// log.addLine(PluginServices.getText(null,
					// "Error_preparar_escritura_resultados"));
					// throw new InterruptedException();
					// }
					//
					// /* 4.4.4- Sets the properties to create the result layer
					// */
					// differenceGeoprocess.setResultLayerProperties(writer,
					// schemaManager);
					//
					// params.put("firstlayerselection", new Boolean(false));
					// params.put("secondlayerselection", new Boolean(true));
					//
					// try {
					// differenceGeoprocess.setParameters(params);
					//
					// /* 4.4.5- Task of the geoProcess, creates a buffer with
					// the parameters */
					// differenceGeoprocess.checkPreconditions();
					// log.addLine(PluginServices.getText(null,
					// "Creating_layer_with_buffers"));
					// task1 = differenceGeoprocess.createTask();
					// task1.run();
					// layerWithBuffers = (FLyrVect)
					// differenceGeoprocess.getResult();
					//
					// /* 4.4.6- Sets transparency */
					// Color c =
					// ((FSymbol)((SingleSymbolLegend)layerWithBuffers.getLegend()).getDefaultSymbol()).getColor();
					// ((FSymbol)((SingleSymbolLegend)layerWithBuffers.getLegend()).getDefaultSymbol()).setColor(new
					// Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
					//
					// log.addLine(PluginServices.getText(null,
					// "Layer_with_buffers_created"));
					//
					// /* 4.4.7- Fixes a bug with the geoprocesses when the
					// layer is reprojected */
					// switch (typeReprojection) {
					// case LAYER_NOT_REPROJECTED:
					// break;
					// case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES: case
					// LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
					// layer.setProjection(tempProjection);
					//
					// if (showInfluenceAreasLayers)
					// layerWithInfluenceAreas.reProject(mapControl);
					//
					// layerWithBuffers.reProject(mapControl);
					//
					// log.addLine(PluginServices.getText(null,
					// "Layer_with_buffers_reprojected"));
					// break;
					// }
					//
					// /* 4.4.8- (Opcional) Adds the temporal layers with the
					// influence areas */
					// if (showBufferLayers) {
					// tocLayers.addLayer(layerWithBuffers);
					// log.addLine(PluginServices.getText(null,
					// "Added_buffer_areas_to_TOC"));
					// }
					// }
					// catch(Exception e3) {
					// /* Notifies the exception in the log */
					// NotificationManager.showMessageError("Error_fallo_geoproceso",
					// e3);
					// throw new InterruptedException();
					// }

					/* 4.5- Gets the geometries that are contained in the layer */
					if (cancelProcess.isCanceled()) {
						throw new InterruptedException();
					}

					// rv = layerWithBuffers.getSource();
					rv = layerWithInfluenceAreas.getSource();

					/* 4.6- For each buffer */
					log.addLine(PluginServices.getText(null,
							"Starting_selection_process"));

					for (int k = 0; k < rv.getShapeCount(); k++) {
						if (cancelProcess.isCanceled()) {
							throw new InterruptedException();
						}

						rv.start();
						aux_geometry = (IGeometry) rv.getShape(k);
						rv.stop();

						switch (typeReprojection) {
						case LAYER_NOT_REPROJECTED:
							break;
						case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES:
						case LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
							aux_geometry.reProject(layer.getCrsTransform());
							break;
						}

						/*
						 * 4.6.1- Adds new geometries that intersect the new
						 * buffer
						 */
						bitsets[i].or(layer.queryByShape(aux_geometry,
								DefaultStrategy.INTERSECTS));

						/* 4.6.1.1- (Opcional) Multi-layer selection */
						if (multiLayerSelection) {
							for (int j = 0; j < layers.length; j++) {
								// Only in different layers
								if (i != j) {
									if (!(layers[j].getCrs() instanceof ProjectedCRS)) {
										log.addLine(PluginServices
												.getText(null,
														"Wont_select_geometries_on_the_layer_because_has_incompatible_projection")
												+ ": " + layer.getName());
										continue;
									}

									multiLayerbitsets[j]
											.or(layers[j].queryByShape(
													aux_geometry,
													DefaultStrategy.INTERSECTS));

									log.addLine(PluginServices
											.getText(null,
													"Multi_layer_selection_applied_for_influence_area"));
								}
							}
						}
					}

					log.addLine(PluginServices.getText(null,
							"Selection_process_finished_succesfully"));
					log.addLine(""); // Empty line

				} catch (Exception de) {
					/* 5- Notifies the exception in the log */
					if (!cancelProcess.isCanceled()) {
						NotificationManager.showMessageError(
								PluginServices.getText(null,
										"Failed_selecting_geometries_in_layer")
										+ " " + layer.getName(), de);
						log.addLine(PluginServices.getText(null,
								"Failed_selecting_geometries_in_layer"));
					}

					/* C- Cancellation process */
					int k;

					/* C.1- Removes the layers added */
					for (k = 0; k < layersAdded.size(); k++) {
						log.addLine(PluginServices.getText(null,
								"Removed_layer_with_influence_areas_to_TOC"));
						tocLayers.removeLayer((FLayer) layersAdded.get(k));
					}

					/* C.2- Restores the selection of the layers */
					for (k = 0; k < layers.length; k++) {
						layer = layers[k];

						layer.getSource().getRecordset()
								.setSelection(copy_bitsets[k]);
					}

					// Forces to reload the layers
					mapControl.drawMap(false);

					log.addLine(PluginServices.getText(null,
							"Selection_restored"));

					throw new InterruptedException();
				}

				percentage += inc;
			}

			/* 6- Sets the new selections */
			for (int i = 0; i < layers.length; i++) {
				layer = layers[i];

				/*
				 * 6.1- All geometries selected, included because of multi-layer
				 * selections
				 */
				bitsets[i].or(multiLayerbitsets[i]);

				/* 6.2- Adds the new geometries selected to the layer's */
				try {
					layer.getSource().getRecordset().setSelection(bitsets[i]);
				} catch (RuntimeException rE) {
					// Skips the runtime exception launched when this thread
					// tries to update the main frame
					if (rE.getMessage().compareTo("No Event Dispatch Thread") == 0) {
						log.addLine(PluginServices
								.getText(this,
										"Runtime_exception_refreshing_the_main_frame_by_a_Swing_thread"));
					}
				}
				// layer.getSource().getRecordset().getSelection().or(bitsets[i]);
			}

			percentage = 100;
		} catch (Exception ex) {
			if (!cancelProcess.isCanceled()) {
				NotificationManager.showMessageError(PluginServices.getText(
						null, "Failed_selecting_geometries"), ex);
				log.addLine(PluginServices.getText(null,
						"Failed_selecting_geometries"));
			}

			throw new InterruptedException();
		}
	}

	/**
	 * Returns a ShpWriter from a SHPLayerDefinition. TODO Independize Writer
	 * and LayerDefinition of implementation (by now we are only saving in SHP
	 * format)
	 * 
	 * @param definition
	 * @return
	 * @throws Exception
	 */
	public IWriter getShpWriter(SHPLayerDefinition definition) throws Exception {
		int shapeType = definition.getShapeType();

		if (shapeType != XTypes.MULTI) {
			ShpWriter writer = new ShpWriter();
			writer.setFile(definition.getFile());
			writer.initialize(definition);
			return writer;
		} else {
			MultiShpWriter writer = new MultiShpWriter();
			writer.setFile(definition.getFile());
			writer.initialize(definition);
			return writer;
		}
	}

	// /*
	// * (non-Javadoc)
	// * @see
	// org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionCanceled(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	// */
	// public void actionCanceled(IncrementableEvent e) {
	// if (percentage < 100) {
	// ended = true;
	//
	// cancelProcess.setCanceled(true);
	// // if ((task1 != null) && (!task1.isCanceled()) && (!task1.isFinished()))
	// {
	// // try {
	// // task1.cancel();
	// // }
	// // catch (Exception ex) {
	// // NotificationManager.showMessageError(PluginServices.getText(null,
	// "Failed_canceling_the_current_task_of_creation_a_buffer_layer"), ex);
	// // log.addLine(PluginServices.getText(null,
	// "Failed_canceling_the_current_task_of_creation_a_buffer_layer"));
	// // }
	// // }
	// //
	// // blinker.interrupt();
	// }
	// else {
	// JOptionPane.showMessageDialog(null,
	// Messages.getText("Process_finished_wont_be_cancelled"),
	// Messages.getText("Warning"), JOptionPane.WARNING_MESSAGE);
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see
	// org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionResumed(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	// */
	// public void actionResumed(IncrementableEvent e) {
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see
	// org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionSuspended(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	// */
	// public void actionSuspended(IncrementableEvent e) {
	// }
}
