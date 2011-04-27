package es.iver.derivedGeom.process;

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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.cresques.cts.IProjection;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.incrementabletask.IncrementableEvent;
import org.gvsig.gui.beans.incrementabletask.IncrementableProcess;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.EditionManager;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeometryUtilities;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.rules.IRule;
import com.iver.cit.gvsig.fmap.edition.rules.RulePolygon;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.legend.CreateSpatialIndexMonitorableTask;

import es.iver.derivedGeom.utils.LayerUtilities;



/**
 * Process that adds a layer with derivative geometries, according the configuration. 
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class AddDerivedGeometrySHPProcess extends IncrementableProcess {
	private DerivedGeometryProcessParameters 	parameters		= null;
	
	public AddDerivedGeometrySHPProcess(String title, String label, DerivedGeometryProcessParameters parameters) {
		super(title);

		this.label = label;
		this.parameters = parameters;
		this.isPausable = true;
	}

	/**
	 * Sets the object that will display the evolution of this loading process as a progress dialog.
	 * 
	 * @param iTask the object that will display the evolution of this loading process
	 */
	public void setIncrementableTask(IncrementableTask iTask) {
		this.iTask = iTask;
		iTask.setAskCancel(true);
		iTask.getButtonsPanel().addAccept();
		iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, false);

		JButton jButton = iTask.getButtonsPanel().getButton(ButtonsPanel.BUTTON_ACCEPT);
		jButton.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				processFinalize();
			}
		});
	}

	/**
	 * Importation process.
	 * 
	 * @throws InterruptedException if fails the process
	 */
	public void process() throws InterruptedException {
		percentage = 5;

		FLyrVect layerAdded = null, sourceLayer = null;
		long numGeoms = 0;
		long numGeomsFailed = 0;
		MapControl mapControl = parameters.getMapControl();
		String previousTool_ID;
		
		try {
			// Saves the current tool
			previousTool_ID = mapControl.getCurrentTool();
			
			/* Creates the new layer */
			if (parameters.getDestLayer() == null) {
				try {
					mapControl.getMapContext().beginAtomicEvent();
	
					/* Copies the fields of the source layer */
					FieldDescription[] fieldDescriptions = parameters.getSourceLayer().getRecordset().getFieldsDescription();
		
					for (int i = 0; i < fieldDescriptions.length; i++) {				
						fieldDescriptions[i] = fieldDescriptions[i].cloneField();
					}
	
					/* Creates the layer */
					layerAdded = LayerUtilities.createShapeLayer (
							parameters.getMapControl().getMapContext(),
							parameters.getDestinationFile(),
							parameters.getDestinationLayerName(),
							parameters.getDestinationLayerShapeType(),
							parameters.getSourceLayer().getProjection(),
							fieldDescriptions); // ¿A cambiar?
		
					if (layerAdded == null)
						throw new InterruptedException();
					
					parameters.setDestLayer(layerAdded);
				}
				catch (Exception ex1) {
					if (! cancelProcess.isCanceled()) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_creating_the_new_layer"), ex1);
						log.addLine(PluginServices.getText(null, "Failed_creating_the_new_layer"));
					}
		
					throw new InterruptedException();
				}
				
				try {
					log.addLine(PluginServices.getText(null, "Layer_created_successfully"));
		
					/* .- Adds the layer to TOC */
					mapControl.getMapContext().beginAtomicEvent();
		
					mapControl.getMapContext().getLayers().addLayer(layerAdded); //0, layerAdded); // 0 -> Adds the layer as the first in the TOC
			        log.addLine(PluginServices.getText(null, "Layer_added_to_TOC"));
		
			        mapControl.getMapContext().endAtomicEvent();
				}
				catch (Exception ex2) {
					if (! cancelProcess.isCanceled()) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_adding_the_layer_to_TOC"), ex2);
						log.addLine(PluginServices.getText(null, "Failed_adding_the_layer_to_TOC"));
					}
		
					throw new InterruptedException();
				}
			}
			else {
				layerAdded = parameters.getDestLayer();
			}


			try {
				sourceLayer = parameters.getSourceLayer();
				CADExtension cad_extension = null;
				VectorialAdapter source_rv = (VectorialAdapter) sourceLayer.getSource();
				VectorialEditableAdapter dest_rv = null;
				EditionManager editionManager = null;

				log.addLine(PluginServices.getText(null, "Source_layer") + " : " + sourceLayer.getName());
				log.addLine(PluginServices.getText(null, "Output_layer") + " : " + parameters.getDestLayer().getName());
				
				/* 3- Starts layer in edition */
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				log.addLine(PluginServices.getText(null, "Starting_the_layer_in_edition_mode"));
//				mapControl.getMapContext().clearAllCachingImageDrawnLayers();
				percentage = 12;

				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				cad_extension = (CADExtension) PluginServices.getExtension(CADExtension.class);
				editionManager = cad_extension.getEditionManager();
				editionManager.setMapControl(mapControl);

				layerAdded.addLayerListener(editionManager);

				ILegend legendOriginal = layerAdded.getLegend();

				if (! layerAdded.isWritable()) {
					JOptionPane.showMessageDialog((Component) PluginServices.getMDIManager().getActiveWindow(),
						PluginServices.getText(this, "this_layer_is_not_self_editable"),
						PluginServices.getText(this, "warning_title"),
						JOptionPane.WARNING_MESSAGE);

					throw new InterruptedException();
				}

				/* 3.1- Sets the cad tool adapter if wasn't added */
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				CADToolAdapter cta = CADExtension.getCADToolAdapter();

				if (! mapControl.getNamesMapTools().containsKey("cadtooladapter")) {
					mapControl.addMapTool("cadtooladapter", cta);
				}

				layerAdded.setEditing(true);
				percentage = 20;
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				dest_rv = (VectorialEditableAdapter) layerAdded.getSource();

				dest_rv.getRules().clear();

				if (dest_rv.getShapeType() == FShape.POLYGON) {
					IRule rulePol = new RulePolygon();
					dest_rv.getRules().add(rulePol);
				}

				if (! (layerAdded.getSource().getDriver() instanceof IndexedShpDriver)) {
					VectorialLayerEdited vle=(VectorialLayerEdited)editionManager.getLayerEdited(layerAdded);
					vle.setLegend(legendOriginal);
				}

				dest_rv.getCommandRecord().addCommandListener(mapControl);

				/* 3.2- If exits any layer associated, changes its model by the VectorialEditableAdapter's one */
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				ProjectExtension pe = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
				percentage = 25;

				ProjectTable dest_pt = pe.getProject().getTable(layerAdded);
				if (dest_pt != null){
					dest_pt.setModel(dest_rv);

					/* 3.3- If there is any view with the table associated to this vector layer -> updates the table */
					// This step is executed after finishing the process, to avoid problems with Swing threads
				}

				/* 3.4- Repaints the view */
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				mapControl.drawMap(false);

				percentage = 28;

				String operationName;
				IGeometry source_geometry = null, dest_geometry = null;
				Value[] source_row_values;
				IProjection tempProjection = null;
				DefaultFeature newFeature;
				String source_id;
				final short LAYER_NOT_REPROJECTED = 0;
				final short LAYER_REPROJECTED_TO_PLAIN_COORDINATES = 1;
				final short LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES = 2;
				short typeReprojection = LAYER_NOT_REPROJECTED;
				long index;
				short inc;

				if (sourceLayer.getProjection().getAbrev().equals(mapControl.getViewPort().getProjection().getAbrev())) {
					/* 4.2.1- If the layer isn't projected -> Geographic coordinates (the default units are grades) */
					if (! sourceLayer.getProjection().isProjected()) {
						log.addLine(PluginServices.getText(null, "Incompatible_projection") + ": " + sourceLayer.getName());
						// UNSUPORTED
						new InterruptedException();
					}
					else {
					}

					typeReprojection = LAYER_NOT_REPROJECTED;
				}
				else {
					/* 4.2.1.1- If the layer isn't projected -> Geographic coordinates (the default units are grades) */
					/* 4.2.1.2- If the layer has been re-projected */
					if (! mapControl.getProjection().isProjected()) {
						// UNSUPORTED
						if (! sourceLayer.getProjection().isProjected()) {
							log.addLine(PluginServices.getText(null, "Incompatible_projection") + ": " + sourceLayer.getName());
							new InterruptedException();
						}

						typeReprojection = LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES;
					}
					else {
						typeReprojection = LAYER_REPROJECTED_TO_PLAIN_COORDINATES;
					}
					
					/* 4.3- Fixes a bug with the geoprocesses when the layer is reprojected */
					if (cancelProcess.isCanceled()) {
						throw new InterruptedException();
					}

					tempProjection = sourceLayer.getProjection();
					sourceLayer.setProjection(mapControl.getProjection());
					
				}

				percentage = 33;

				/* 4- For each new geometry */
				inc = (short) ((75 - 33) / parameters.getGeometryIndexes().length);
				
				switch(sourceLayer.getShapeType()) {
					case FShape.POINT:
						// The alphanumeric data will be lost
						numGeoms = parameters.getGeometryIndexes().length;
						
						long[] indexes;
						ArrayList points = new ArrayList();
						source_id = "";
					
						switch(parameters.getProcessID()) {
							case DerivedGeometryProcessParameters.POINTS_TO_LINE_PROCESS_ID:
								operationName = PluginServices.getText(this, "polyline_");

								// For each new line, the the points
								for (int i = 0; i < parameters.getGeometryIndexes().length; i++) {
									if (cancelProcess.isCanceled()) {
										throw new InterruptedException();
									}

									// Gets each geometry of the source
									try {
										indexes = parameters.getGeometryIndexes()[i];

										for (int j = 0; j < indexes.length; j++) {
											if (cancelProcess.isCanceled()) {
												throw new InterruptedException();
											}

											index = indexes[j];

											source_rv.start();
											source_id += " " + source_rv.getFeature((int)index).getID();
											source_geometry = (IGeometry) source_rv.getShape((int)index);
											source_rv.stop();

											switch (typeReprojection) {
												case LAYER_NOT_REPROJECTED:
													break;
												case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES: case LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
													source_geometry.reProject(sourceLayer.getCoordTrans());
												break;
											}

											points.add(source_geometry.getInternalShape());
										}
									}
									catch(Exception ex) {
										if (cancelProcess.isCanceled()) {
											throw new InterruptedException();
										}

										NotificationManager.showMessageError(PluginServices.getText(null, "Failed_getting_geometry"), ex);
										log.addLine(PluginServices.getText(null, "Failed_getting_a_geometry_a_line_wont_be_created"));

										numGeomsFailed ++;
										points.clear();
										percentage += inc;

										continue;
									}

									/* Tries to create the new geometry */
									try {
										if (cancelProcess.isCanceled()) {
											throw new InterruptedException();
										}

										dest_geometry = GeometryUtilities.getPolyLine2D((FPoint2D[])points.toArray(new FPoint2D[0]));

										// If has only one or two points the polygon couldn't be closed
										if (dest_geometry == null) {
											try {
												log.addLine(PluginServices.getText(null, "Impossible_convert_to_polyline") + " : " + Arrays.toString(indexes));
											}
											catch (Exception except) {
												NotificationManager.showMessageError(PluginServices.getText(null, "Impossible_convert_to_polyline"), except);
												log.addLine(PluginServices.getText(null, "Impossible_convert_to_polyline"));
											}
											numGeomsFailed ++;
											points.clear();
											percentage += inc;

											continue;
										}
										
										/* Each geometry will only have one column, with a null value */
						            	Value[] values = new Value[1];
						            	values[0] = ValueFactory.createNullValue();
								
										/* Creates the new row feature with the new geometry and the same alphanumeric values, and id */
										newFeature = new DefaultFeature(dest_geometry, values, source_id.trim());
								        
						            	/* Adds the new row with the new polyline */
										dest_rv.startComplexRow();

										/* Adds the new feature */
										dest_rv.addRow(newFeature, operationName, EditionEvent.GRAPHIC);

										/* Disables the edition of the row */
										dest_rv.endComplexRow(operationName);

										/* Clears the points */
										points.clear();

										percentage += inc;
									}
									catch(Exception ex) {
										if (cancelProcess.isCanceled()) {
											throw new InterruptedException();
										}

										NotificationManager.showMessageError(PluginServices.getText(null, "Failed_creating_geometry"), ex);
										log.addLine(PluginServices.getText(null, "Failed_creating_geometry_a_line_wont_be_created"));

										numGeomsFailed ++;
										points.clear();
										percentage += inc;

										continue;
									}
								}
								break;
							case DerivedGeometryProcessParameters.POINTS_TO_POLYGON_PROCESS_ID:
								operationName = PluginServices.getText(this, "polygon_");

								// For each new line, the the points
								for (int i = 0; i < parameters.getGeometryIndexes().length; i++) {
									if (cancelProcess.isCanceled()) {
										throw new InterruptedException();
									}

									// Gets each geometry of the source
									try {
										indexes = parameters.getGeometryIndexes()[i];

										for (int j = 0; j < indexes.length; j++) {
											if (cancelProcess.isCanceled()) {
												throw new InterruptedException();
											}

											index = indexes[j];

											source_rv.start();
											source_id += " " + source_rv.getFeature((int)index).getID();
											source_geometry = (IGeometry) source_rv.getShape((int)index);
											source_rv.stop();

											switch (typeReprojection) {
												case LAYER_NOT_REPROJECTED:
													break;
												case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES: case LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
													source_geometry.reProject(sourceLayer.getCoordTrans());
												break;
											}

											points.add(source_geometry.getInternalShape());
										}
									}
									catch(Exception ex) {
										if (cancelProcess.isCanceled()) {
											throw new InterruptedException();
										}

										NotificationManager.showMessageError(PluginServices.getText(null, "Failed_getting_geometry"), ex);
										log.addLine(PluginServices.getText(null, "Failed_getting_a_geometry_a_polygon_wont_be_created"));

										numGeomsFailed ++;
										points.clear();
										percentage += inc;

										continue;
									}

									/* Tries to create the new geometry */
									try {
										if (cancelProcess.isCanceled()) {
											throw new InterruptedException();
										}

										dest_geometry = GeometryUtilities.getPolygon2D((FPoint2D[])points.toArray(new FPoint2D[0]));

										// If has only one or two points the polygon couldn't be closed
										if (dest_geometry == null) {
											try {
												log.addLine(PluginServices.getText(null, "Impossible_convert_to_polygon") + " : " + Arrays.toString(indexes));
											}
											catch (Exception except) {
												NotificationManager.showMessageError(PluginServices.getText(null, "Impossible_convert_to_polygon"), except);
												log.addLine(PluginServices.getText(null, "Impossible_convert_to_polygon"));
											}
											numGeomsFailed ++;
											points.clear();
											percentage += inc;

											continue;
										}
										
										/* Each geometry will only have one column, with a null value */
						            	Value[] values = new Value[1];
						            	values[0] = ValueFactory.createNullValue();
								
										/* Creates the new row feature with the new geometry and the same alphanumeric values, and id */
										newFeature = new DefaultFeature(dest_geometry, values, source_id.trim());
								        
						            	/* Adds the new row with the new polyline */
										dest_rv.startComplexRow();

										/* Adds the new feature */
										dest_rv.addRow(newFeature, operationName, EditionEvent.GRAPHIC);

										/* Disables the edition of the row */
										dest_rv.endComplexRow(operationName);

										/* Clears the points */
										points.clear();

										percentage += inc;
									}
									catch(Exception ex) {
										if (cancelProcess.isCanceled()) {
											throw new InterruptedException();
										}

										NotificationManager.showMessageError(PluginServices.getText(null, "Failed_creating_geometry"), ex);
										log.addLine(PluginServices.getText(null, "Failed_creating_geometry_a_line_wont_be_created"));

										numGeomsFailed ++;
										points.clear();
										percentage += inc;

										continue;
									}
								}
								break;
						}
						break;
					case FShape.LINE:
						numGeoms = parameters.getGeometryIndexes()[0].length;
						operationName = PluginServices.getText(this, "polygon_");

						// For each row (geometry) -> copies it to the new layer
						for (int i = 0; i < parameters.getGeometryIndexes()[0].length; i++) {
							if (cancelProcess.isCanceled()) {
								throw new InterruptedException();
							}

							try {
								// Gets each geometry of the source
								index = parameters.getGeometryIndexes()[0][i];
								
								source_rv.start();
								source_id = source_rv.getFeature((int)index).getID();
								source_geometry = (IGeometry) source_rv.getShape((int)index);
								source_row_values = source_rv.getFeature((int)index).getAttributes();
								source_rv.stop();

								switch (typeReprojection) {
									case LAYER_NOT_REPROJECTED:
										break;
									case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES: case LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
										source_geometry.reProject(sourceLayer.getCoordTrans());
									break;
								}

								// Geometry conversion according the source layer geometry type and the option selection

								// Closes the lines -> converting to polygon
								if (cancelProcess.isCanceled()) {
									throw new InterruptedException();
								}

								dest_geometry = GeometryUtilities.closeLine(source_geometry);

								// If has only one or two points the polygon couldn't be closed
								if (dest_geometry == null) {
									log.addLine(PluginServices.getText(null, "Impossible_convert_to_polygon_the_geometry") + " : " + source_id);
									numGeomsFailed ++;
									
									continue;
								}

								dest_rv.startComplexRow();

								// Tries to process all the selected geometries
								if (cancelProcess.isCanceled()) {
									throw new InterruptedException();
								}

								// Creates the new row feature with the new geometry and the same alphanumeric values, and id
								newFeature = new DefaultFeature(dest_geometry, source_row_values, source_id);

								/* Adds the new feature */
								dest_rv.addRow(newFeature, operationName, EditionEvent.GRAPHIC); //EditionEvent.ALPHANUMERIC);
							}
							catch(Exception exG) {
								if (cancelProcess.isCanceled()) {
									throw new InterruptedException();
								}

								NotificationManager.showMessageError(PluginServices.getText(null, "Failed_creating_geometry"), exG);
								log.addLine(PluginServices.getText(null, "Failed_creating_geometry"));

								numGeomsFailed ++;
								
								/* Forces to disable the edition of the row */
								dest_rv.endComplexRow(operationName);
								
								// And continues with the next geometry
								continue;
							}
		
							/* Disables the edition of the row */
							dest_rv.endComplexRow(operationName);
						}

						percentage += inc;
						break;
				}
				
				switch (typeReprojection) {
					case LAYER_NOT_REPROJECTED:
						break;
					case LAYER_REPROJECTED_TO_GEOGRAPHIC_COORDINATES: case LAYER_REPROJECTED_TO_PLAIN_COORDINATES:
							sourceLayer.setProjection(tempProjection);
						break;
				}
				

				/* 6- Ends layer in edition */
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}

				percentage = 75;
				log.addLine(PluginServices.getText(null, "Stopping_the_layer_of_edition_mode"));
				mapControl.getCanceldraw().setCanceled(true);

				VectorialLayerEdited lyrEd = (VectorialLayerEdited)	editionManager.getActiveLayerEdited();
				if (lyrEd != null)
					lyrEd.clearSelection(false);

				percentage = 80;
				
				/* 6.1- Saves the layer */
				/* This part can't be canceled */
				if (layerAdded.isWritable()) {
					try {
						saveLayer(layerAdded);
					}
					catch (Exception e) {
						log.addLine(PluginServices.getText(null, "Failed_saving_the_layer"));
						throw e;
					}

					percentage = 90;

					/* 6.2- Only finish the edition mode if wasn't being edited */
					dest_rv.getCommandRecord().removeCommandListener(mapControl);
					layerAdded.setEditing(false);
					if (layerAdded.isSpatiallyIndexed()) {
		            	if (layerAdded.getISpatialIndex() != null) {
							PluginServices.cancelableBackgroundExecution(new CreateSpatialIndexMonitorableTask((FLyrVect)layerAdded));
		                }
					}

					/* 6.3- If has ended successfully the editing */
					layerAdded.removeLayerListener(editionManager);
					if (layerAdded instanceof FLyrAnnotation) {
						FLyrAnnotation lva = (FLyrAnnotation)layerAdded;
			            lva.setMapping(lva.getMapping());
					}

					/* 6.4.b- Restores the previous tool */
					mapControl.setTool(previousTool_ID);
					percentage = 91;

					parameters.getView().hideConsole();
					percentage = 94;
					
					mapControl.drawMap(false);
					percentage = 98;
					CADExtension.clearView();
				}
				
				if (cancelProcess.isCanceled()) {
					throw new InterruptedException();
				}
				
				percentage = 100;
				log.addLine(PluginServices.getText(null, "Process_finished_successfully"));
				return;
			}
			catch(Exception ex3) {
				if (! cancelProcess.isCanceled()) {
					NotificationManager.showMessageError(PluginServices.getText(null, "Failed_processing_the_geometries"), ex3);
					log.addLine(PluginServices.getText(null, "Failed_processing_the_geometries"));
				}

				// CANCELS THE EDITION OF THE LAYER
				try {
					// Emergency restore
					// This code can't be cancelled
					// Only finish the edition mode if wasn't being edited
					cancelEdition(layerAdded);
					layerAdded.setEditing(false);

					mapControl.setTool(previousTool_ID);
					parameters.getView().hideConsole();
					mapControl.drawMap(false);
					CADExtension.clearView();
				}
				catch (Exception ex) {
					NotificationManager.showMessageError(PluginServices.getText(null, "Failed_restoring_layer_in_edition_mode"), ex);
					log.addLine(PluginServices.getText(null, "Failed_restoring_layer_in_edition_mode"));
				}

				throw new InterruptedException();
			}
		}
		catch (Exception ex4) {
			if (! cancelProcess.isCanceled()) {
				NotificationManager.showMessageError(PluginServices.getText(null, "Failed_the_process"), ex4);
				log.addLine(PluginServices.getText(null, "Failed_the_process"));
			}

			/* CANCELLATION PROCESS */

			// Forces to end the atomic transaction
			parameters.getMapControl().getMapContext().endAtomicEvent();

			/* Removes the layer added */
			try {
				parameters.getMapControl().getMapContext().getLayers().removeLayer(layerAdded);
			}
			catch (RuntimeException rE) {
				// Skips the runtime exception launched when this thread tries to update the main frame
				if ((rE.getMessage() != null) && (rE.getMessage().compareTo("No Event Dispatch Thread") == 0)) {
					log.addLine(PluginServices.getText(this, "Runtime_exception_refreshing_the_main_frame_by_a_Swing_thread"));
				}
				else {
					log.addLine(PluginServices.getText(this, "Runtime_exception_refreshing_the_main_frame_by_a_Swing_thread"));
				}
			}

			layerAdded = null;
			parameters.setDestLayer(null);

			throw new InterruptedException();
		}
		finally {
			/* Summary of geometries processed */
			log.addLine("    " + PluginServices.getText(null, "Number_of_geometries_to_create") + " : " + numGeoms);
			log.addLine("    " + PluginServices.getText(null, "Number_of_geometries_that_couldnt_be_created") + " : " + numGeomsFailed);
			log.addLine("    " + PluginServices.getText(null, "Number_of_geometries_created_successfully") + " : " + (numGeoms - numGeomsFailed));
			
			// Ends the progress panel
			iTask.getButtonsPanel().getButton(ButtonsPanel.BUTTON_ACCEPT).doClick();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionCanceled(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	 */
	public void actionCanceled(IncrementableEvent e) {
		if (percentage < 100) {
			ended = true;
			
			cancelProcess.setCanceled(true);
			
			blinker.interrupt();
		}
		else {
			JOptionPane.showMessageDialog(null, PluginServices.getText(null, "Process_finished_wont_be_cancelled"), PluginServices.getText(null, "Warning"), JOptionPane.WARNING_MESSAGE);
		}
	}

//	/**
//	 * <p>Starts layer in edition mode.</p>
//	 * 
//	 * @param mapControl the <code>MapControl</code> object that contains the layer
//	 * @param cad_extension extension that allows edit a layer
//	 * @param editionManager manager for editing layers
//	 * @param vea adapter of the editable vector layers
//	 * @param layer the layer to start in edition mode
//	 * 
//	 * @throws Exception any exception produced starting in edition the layer
//	 */
//	private void startLayerInEdition(MapControl mapControl, CADExtension cad_extension, EditionManager editionManager, VectorialEditableAdapter vea, FLyrVect layer) throws Exception {
//		log.addLine(PluginServices.getText(null, "Starting_the_layer_in_edition_mode"));
//		mapControl.getMapContext().clearAllCachingImageDrawnLayers();
//		cad_extension = (CADExtension) PluginServices.getExtension(CADExtension.class);
//		editionManager = cad_extension.getEditionManager();
//		editionManager.setMapControl(mapControl);
//
//		layer.addLayerListener(editionManager);
//
//		Legend legendOriginal = layer.getLegend();
//
//		if (! layer.isWritable()) {
//			JOptionPane.showMessageDialog((Component) PluginServices.getMDIManager().getActiveWindow(),
//				PluginServices.getText(this, "this_layer_is_not_self_editable"),
//				PluginServices.getText(this, "warning_title"),
//				JOptionPane.WARNING_MESSAGE);
//
//			throw new InterruptedException();
//		}
//
//		/* N.1- Sets the cad tool adapter if wasn't added */
//		CADToolAdapter cta = CADExtension.getCADToolAdapter();
//		if (! mapControl.getNamesMapTools().containsKey("cadtooladapter")) {
//			mapControl.addMapTool("cadtooladapter", cta);
//		}
//
//		layer.setEditing(true);
//		vea = (VectorialEditableAdapter) layer.getSource();
//
//		vea.getRules().clear();
//		if (vea.getShapeType() == FShape.POLYGON) {
//			IRule rulePol = new RulePolygon();
//			vea.getRules().add(rulePol);
//		}
//
//		if (! (layer.getSource().getDriver() instanceof IndexedShpDriver)) {
//			VectorialLayerEdited vle=(VectorialLayerEdited)editionManager.getLayerEdited(layer);
//			vle.setLegend(legendOriginal);
//		}
//
//		vea.getCommandRecord().addCommandListener(mapControl);
//
//		/* N.2- If exits any layer associated, changes its model by the VectorialEditableAdapter's one */
//		ProjectExtension pe = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//
//		ProjectTable pt = pe.getProject().getTable(layer);
//		//this.layerProjectTable = pt;
//
//		if (pt != null){
//			pt.setModel(vea);
//
//			/* N.3- If there is any view with the table associated to this vector layer -> updates the table */
//			// This step is executed after finishing the process, to avoid problems with Swing threads
////		   	com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices.getMDIManager().getAllWindows();
////
////			for (int i = 0 ; i < views.length ; i++) {
////				if (views[i] instanceof Table) {
////					Table table = (Table)views[i];
////					ProjectTable model = table.getModel();
////
////					if (model.equals(pt)) {
////						table.setModel(pt);
////						vea.getCommandRecord().addCommandListener(table);
////					}
////				}
////			}
//		}
//
//		/* N.4- Repaints the view */
//		mapControl.drawMap(false);
//	}

	/**
	 * <p>Saves and stops the edition of a vector layer.</p>
	 * 
	 * @param layer the vector layer to save
	 * 
	 * @throws Exception if fails saving the layer
	 */
	private void saveLayer(FLyrVect layer) throws Exception {
		try {
			layer.setProperty("stoppingEditing", new Boolean(true));
			VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
			
			ISpatialWriter writer = (ISpatialWriter) vea.getWriter();
			com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices.getMDIManager().getAllWindows();
			for (int j = 0; j < views.length; j++) {
				if (views[j] instanceof Table) {
					Table table = (Table) views[j];
					if (table.getModel().getAssociatedTable() != null
							&& table.getModel().getAssociatedTable().equals(layer)) {
						table.stopEditingCell();
					}
				}
			}
			vea.cleanSelectableDatasource();
			layer.setRecordset(vea.getRecordset());
	
			// Queremos que el recordset del layer
			// refleje los cambios en los campos.
			ILayerDefinition lyrDef = EditionUtilities.createLayerDefinition(layer);
			String aux = "FIELDS:";
			FieldDescription[] flds = lyrDef.getFieldsDesc();
			for (int i=0; i < flds.length; i++)	{
				aux = aux + ", " + flds[i].getFieldAlias();
			}
	
			System.err.println("Escribiendo la capa " + lyrDef.getName() + " con los campos " + aux);
			lyrDef.setShapeType(layer.getShapeType());
			writer.initialize(lyrDef);
			vea.stopEdition(writer, EditionEvent.GRAPHIC);
			layer.setProperty("stoppingEditing", new Boolean(false));
		}
		catch (Exception e) {
			log.addLine(PluginServices.getText(null, "Failed_saving_the_layer"));
			throw e;			
		}
	}

	/**
	 * <p>Cancels the edition process without saving.</p>
	 * 
	 * @param layer the layer being edited
	 * 
	 * @throws Exception if fails canceling the layer
	 */
	private void cancelEdition(FLyrVect layer) throws Exception {
		try {
			layer.setProperty("stoppingEditing",new Boolean(true));
			com.iver.andami.ui.mdiManager.IWindow[] views = PluginServices.getMDIManager().getAllWindows();
			VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
			vea.cancelEdition(EditionEvent.GRAPHIC);
	
			for (int j = 0; j < views.length; j++) {
				if (views[j] instanceof Table) {
					Table table = (Table) views[j];
					if ((table.getModel().getAssociatedTable() != null) && (table.getModel().getAssociatedTable().equals(layer))) {
						// Avoid conflicts with the Swing threads
				    	table.cancelEditingCell();
				        table.getModel().getModelo().cancelEdition(EditionEvent.ALPHANUMERIC);
						//table.cancelEditing();
					}
				}
			}
	
			layer.setProperty("stoppingEditing", new Boolean(false));
		}
		catch (Exception e) {
			log.addLine(PluginServices.getText(null, "Failed_canceling_the_layer"));
			throw e;
		}
	}
}
