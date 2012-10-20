package org.gvsig.quickInfo.tools;

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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.gvsig.quickInfo.FormatDecimalNumber;
import org.gvsig.quickInfo.GeometryIDInfo;
import org.gvsig.quickInfo.QuickInfoFLayerSelected;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FMultipoint3D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint3D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolygon3D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FPolyline3D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeometryUtilities;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.InfoByPoint;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toc.TocItemBranch;

/**
 * <p>
 * Tool listener that updates <code>MapControl</code>'s tool tip with the
 * information selected that the cursor of the mouse points on
 * <code>MapControl</code>.
 * </p>
 * 
 * <p>
 * This tool only apply changes when a single point is indicated.
 * </p>
 * 
 * @author Vicente Caballero Navarro (vicente.caballero@iver.es)
 * @author Jaume Domínguez Faus (jaume.dominguez@iver.es)
 * @author César Martínez Izquierdo (cesar.martinez@iver.es)
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class QuickInfoListener implements PointListener {
	private MapControl mapControl;
	private QuickInfoFLayerSelected infoSelected;
	public static final int DEFAULT_PIXEL_TOLERANCE = 2;
	private int pixel_tolerance;
	private TreeModelListener treeModelListener;
	private MouseAdapter treeMouseAdapter;
	private int oldInitialDelay;
	private int oldReshowDelay;
	// private int oldDismissDelay;
	private View view;
	public static final short DEFAULT_EVENTS_TO_IGNORE = 0;
	public static final short DEFAULT_EVENTS_TO_IGNORE_IN_EDITION_MODE = 2;
	private short eventsToIgnore = DEFAULT_EVENTS_TO_IGNORE;
	private short eventsToIgnoreInEditionMode = DEFAULT_EVENTS_TO_IGNORE_IN_EDITION_MODE;
	private short counter = 0;

	// private Double[] xs_ar, ys_ar;
	// private Integer[] pls_n_points;

	/**
	 * <p>
	 * Creates a new <code>QuickInfoListener</code>. Tool that gets the
	 * previously selected alfanumeric information of a feature identified by a
	 * point, and displays it formatted in a <code>MapControl</code>'s <i>tool
	 * tip text</i>.
	 * </p>
	 * 
	 * @param mc
	 *            a <code>MapControl</code> instance
	 * @param infoSelected
	 *            the information to display
	 * @param pixel_tolerance
	 *            pixel tolerance (should be greater in points)
	 */
	public QuickInfoListener(MapControl mc,
			QuickInfoFLayerSelected infoSelected, int pixel_tolerance) {
		this.mapControl = mc;
		this.infoSelected = infoSelected;
		this.pixel_tolerance = pixel_tolerance;
		final QuickInfoFLayerSelected qil = infoSelected;

		oldInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
		ToolTipManager.sharedInstance().setInitialDelay(300); // By default is
																// 750

		oldReshowDelay = ToolTipManager.sharedInstance().getReshowDelay();
		ToolTipManager.sharedInstance().setReshowDelay(200); // By default is
																// 500

		// oldDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
		// ToolTipManager.sharedInstance().setInitialDelay(100000); // By
		// default is 60000

		view = (View) PluginServices.getMDIManager().getActiveWindow();
		view.getTOC().getTree().getModel()
				.addTreeModelListener(getTreeModelListener(view));

		// Adds new listener to remove this tool when the visibility of the
		// layer has changed to not visible
		view.getTOC().getTree()
				.addMouseListener(getTreeMouseAdapter(view, qil));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.
	 * cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void point(PointEvent event) throws BehaviorException {
		if (infoSelected.getLayer().isEditing()) {
			if (counter < eventsToIgnoreInEditionMode) {
				counter++;
				return;
			} else {
				counter = 0;
			}
		} else {
			if (counter < eventsToIgnore) {
				counter++;
				return;
			} else {
				counter = 0;
			}
		}

		if ((infoSelected.getLayer().isVisible())
				&& (infoSelected.getLayer() instanceof InfoByPoint)) {
			try {
				mapControl.setToolTipText(null);

				double tolerance = mapControl.getViewPort().toMapDistance(
						pixel_tolerance);

				Point imagePoint = new Point((int) event.getPoint().getX(),
						(int) event.getPoint().getY());

				Point2D pReal = null;

				// Get the point in the map
				pReal = mapControl.getViewPort().toMapPoint(imagePoint);

				// Disable the spatial index to do the query by point
				FLyrVect layer = (FLyrVect) infoSelected.getLayer();

				ISpatialIndex iSI = layer.getISpatialIndex();
				layer.setISpatialIndex(null);
				FBitSet bs = layer.queryByPoint(pReal, tolerance);

				// Enable the spatial index after do the query by point
				layer.setISpatialIndex(iSI);

				DataSource ds = ((AlphanumericData) layer).getRecordset();

				infoSelected.clearValues();

				// Layer Fields Values:
				Set lFields = infoSelected.getLayerFields().keySet();
				// Set cFields =
				// infoSelected.getCalculatedLayerFields().keySet();

				String nameDistanceUnits = "";

				switch (mapControl.getViewPort().getDistanceUnits()) {
				case 0:
					nameDistanceUnits = "km";
					break;
				case 1:
					nameDistanceUnits = "m";
					break;
				case 2:
					nameDistanceUnits = "cm";
					break;
				case 3:
					nameDistanceUnits = "mm";
					break;
				case 4: // Millas estatutarias internacionales:
					nameDistanceUnits = "mi";
					break;
				case 5:
					nameDistanceUnits = "Ya";
					break;
				case 6:
					nameDistanceUnits = "ft";
					break;
				case 7:
					nameDistanceUnits = "in";
					break;
				case 8:
					nameDistanceUnits = "º";
					break;
				}

				String nameAreaUnits = "";

				switch (mapControl.getViewPort().getDistanceArea()) {
				case 0:
					nameAreaUnits = "km\u00B2";
					break;
				case 1:
					nameAreaUnits = "m\u00B2";
					break;
				case 2:
					nameAreaUnits = "a";
					break;
				case 3:
					nameAreaUnits = "ha";
					break;
				case 4:
					nameAreaUnits = "hgV";
					break;
				case 5:
					nameAreaUnits = "hgC";
					break;
				case 6:
					nameAreaUnits = "dm\u00B2";
					break;
				case 7:
					nameAreaUnits = "cm\u00B2";
					break;
				case 8:
					nameAreaUnits = "mm\u00B2";
					break;
				case 9:
					nameAreaUnits = "in\u00B2";
					break;
				case 10:
					nameAreaUnits = "ft\u00B2";
					break;
				case 11: // Acre
					nameAreaUnits = "ac\u00B2";
					break;
				case 12: // Millas estatutarias internacionales:
					nameAreaUnits = "mi\u00B2";
					break;
				case 13:
					nameAreaUnits = "Ya\u00B2";
					break;
				}

				double relationDistanceUnitsToMeters = MapContext.CHANGEM[mapControl
						.getViewPort().getDistanceUnits()];
				double relationAreaUnitsToMeters2 = ((Double) MapContext.AREATRANS2METER
						.get(mapControl.getViewPort().getDistanceArea()))
						.doubleValue();
				relationAreaUnitsToMeters2 = relationAreaUnitsToMeters2
						* relationAreaUnitsToMeters2;
				double area, perimeter;

				Vector cFieldsToRestore = null;
				GeometryIDInfo gIDInfo = null;

				for (int j = bs.nextSetBit(0); j >= 0; j = bs.nextSetBit(j + 1)) {
					if (infoSelected.getLayerFields().size() > 0) {
						for (int k = 0; k < ds.getFieldCount(); k++) {
							if (lFields.contains(ds.getFieldName(k))) {
								((Vector) infoSelected.getLayerFields().get(
										ds.getFieldName(k))).add(ds
										.getFieldValue(j, k).toString());
								infoSelected.setAnyLayerFieldAdded(true);
							}
						}
					}

					String geom_id = String.valueOf(j);

					if (!infoSelected.getGeometryIDs().contains(geom_id))
						infoSelected.getGeometryIDs().add(
								gIDInfo = new GeometryIDInfo(geom_id));

					// if ((infoSelected.getCalculatedLayerFields().size() > 0)
					// && (addedValues) &&
					// (!infoSelected.getGeometryIDs().contains(geom_id))) {
					if (infoSelected.getCalculatedLayerFields().size() > 0) {
						// infoSelected.getGeometryIDs().add(geom_id);

						// Calculated Layer Fields Values:
						try {
							ReadableVectorial rv = ((FLyrVect) layer)
									.getSource();

							rv.start();
							IGeometry geometry = rv.getShape(j);
							rv.stop();

							Shape shape = null;

							if (geometry != null) {
								shape = geometry.getInternalShape();
							}

							// If there is no internal shape in the geometry ->
							// unknown info about its fields
							if (shape == null) {
								gIDInfo.setHasInfo(false);
								continue;
							}

							String key;
							Vector values;

							switch (layer.getShapeType()) {
							case FShape.NULL:
								break;
							case FShape.POINT:
								// Coordinates
								key = PluginServices.getText(this,
										"Coordinates");
								values = ((Vector) infoSelected
										.getCalculatedLayerFields().get(key));
								if (values != null) {
									if (shape instanceof FPoint2D) {
										FPoint2D point2D = (FPoint2D) shape;
										values.add(PluginServices.getText(this,
												"X")
												+ "= "
												+ FormatDecimalNumber
														.formatDecimal(point2D
																.getX())
												+ ", "
												+ PluginServices.getText(this,
														"Y")
												+ "="
												+ FormatDecimalNumber
														.formatDecimal(point2D
																.getY()));
										// values.add(PluginServices.getText(this,
										// "X") + "= " +
										// FormatDecimalNumber.formatDecimal(point2D.getX(),
										// "·10", true) + ", " +
										// PluginServices.getText(this, "Y") +
										// "=" +
										// FormatDecimalNumber.formatDecimal(point2D.getY(),
										// "·10", true));
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
										continue;
									}

									if (shape instanceof FPoint3D) {
										FPoint3D point3D = (FPoint3D) shape;
										values.add(PluginServices.getText(this,
												"X")
												+ "= "
												+ FormatDecimalNumber
														.formatDecimal(point3D
																.getX())
												+ ", "
												+ PluginServices.getText(this,
														"Y")
												+ "="
												+ FormatDecimalNumber
														.formatDecimal(point3D
																.getY())
												+ ", "
												+ PluginServices.getText(this,
														"Z")
												+ "="
												+ FormatDecimalNumber
														.formatDecimal(point3D
																.getZs()[0]));
										// values.add(PluginServices.getText(this,
										// "X") + "= " +
										// FormatDecimalNumber.formatDecimal(point3D.getX(),
										// "·10", true) + ", " +
										// PluginServices.getText(this, "Y") +
										// "=" +
										// FormatDecimalNumber.formatDecimal(point3D.getY(),
										// "·10", true) + ", " +
										// PluginServices.getText(this, "Z") +
										// "=" +
										// FormatDecimalNumber.formatDecimal(point3D.getZs()[0],
										// "·10", true));
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
										continue;
									}
								}
								break;
							case FShape.LINE:
								// Disabled:
								// // First point coordinate
								// key = PluginServices.getText(this,
								// "First_point_coordinates");
								// values =
								// ((Vector)infoSelected.getCalculatedLayerFields().get(key));
								// if (values != null) {
								// if (shape == null) {
								// values.add("");
								// }
								// else {
								// GeneralPathX g;
								// g.
								//
								// if (shape instanceof FPolyline2D) {
								// FPolyline2D polyline2D = (FPolyline2D) shape;
								// values.add(PluginServices.getText(this, "X")
								// + "= " + polyline2D.getX() + ", " +
								// PluginServices.getText(this, "Y") + "=" +
								// point3D.getY() + ", " +
								// PluginServices.getText(this, "Z") + "=" +
								// point3D.getZs());
								// }
								//
								// if (shape instanceof FPolyline3D) {
								// FPolyline3D polyline3D = (FPolyline3D) shape;
								// values.add(PluginServices.getText(this, "X")
								// + "= " + point3D.getX() + ", " +
								// PluginServices.getText(this, "Y") + "=" +
								// point3D.getY() + ", " +
								// PluginServices.getText(this, "Z") + "=" +
								// point3D.getZs());
								// }
								// }
								// }
								//
								// // Second point coordinate
								// if
								// (cFields.contains(PluginServices.getText(this,
								// "Second_point_coordinates"))) {
								// if (shape == null) {
								// values.add("");
								// }
								// else {
								// if (shape instanceof F??2D) {
								// FPoint2D point2D = (FPoint2D) shape;
								// System.out.println("Second_point_coordinates: "
								// + point2D.getX() + ", " + point2D.getY());
								// }
								//
								// if (shape instanceof F??3D) {
								// FPoint3D point3D = (FPoint3D) shape;
								// System.out.println("Second_point_coordinates: "
								// + point3D.getX() + ", " + point3D.getY());
								// }
								// }
								// }
								//
								// Length
								key = PluginServices.getText(this, "Length");
								values = ((Vector) infoSelected
										.getCalculatedLayerFields().get(key));
								if (values != null) {
									try {
										perimeter = GeometryUtilities
												.getLength(infoSelected
														.getLayer()
														.getMapContext()
														.getViewPort(),
														geometry);

										values.add(FormatDecimalNumber
												.formatDecimal(perimeter
														/ relationDistanceUnitsToMeters)
												+ " " + nameDistanceUnits);
										// values.add(FormatDecimalNumber.formatDecimal(perimeter
										// / relationDistanceUnitsToMeters,
										// "·10", true) + " " +
										// nameDistanceUnits);
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
									} catch (Exception e) {
										NotificationManager
												.showMessageError(
														PluginServices
																.getText(null,
																		"Failed_calculating_perimeter_of_geometry"),
														e);
										mapControl.setToolTipText(null);
										return;
									}

									// values.add(FormatDecimalNumber.formatDecimal(FConverter.java2d_to_jts((FShape)
									// shape).getLength() /
									// relationDistanceUnitsToMeters, "·10",
									// true) + " " + nameDistanceUnits);
									// infoSelected.setAnyCalculatedLayerFieldsAdded(true);
									//
									// xs_ar = ys_ar = null;
									// pls_n_points = null;
								}
								break;
							case FShape.POLYGON:
								// Area
								key = PluginServices.getText(this, "Area");
								values = ((Vector) infoSelected
										.getCalculatedLayerFields().get(key));
								if (values != null) {
									try {
										area = GeometryUtilities.getArea(
												infoSelected.getLayer(),
												geometry);

										values.add(FormatDecimalNumber
												.formatDecimal(area
														/ relationAreaUnitsToMeters2)
												+ " " + nameAreaUnits);
										// values.add(FormatDecimalNumber.formatDecimal(area
										// / relationAreaUnitsToMeters2, "·10",
										// true) + " " + nameAreaUnits);
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
									} catch (Exception e) {
										NotificationManager
												.showMessageError(
														PluginServices
																.getText(null,
																		"Failed_calculating_area_of_geometry"),
														e);
										mapControl.setToolTipText(null);
										return;
									}
								}

								// Perimeter
								key = PluginServices.getText(this, "Perimeter");
								values = ((Vector) infoSelected
										.getCalculatedLayerFields().get(key));
								if (values != null) {
									try {
										perimeter = GeometryUtilities
												.getLength(infoSelected
														.getLayer()
														.getMapContext()
														.getViewPort(),
														geometry);

										values.add(FormatDecimalNumber
												.formatDecimal(perimeter
														/ relationDistanceUnitsToMeters)
												+ " " + nameDistanceUnits);
										// values.add(FormatDecimalNumber.formatDecimal(perimeter
										// / relationDistanceUnitsToMeters,
										// "·10", true) + " " +
										// nameDistanceUnits);
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
									} catch (Exception e) {
										NotificationManager
												.showMessageError(
														PluginServices
																.getText(null,
																		"Failed_calculating_perimeter_of_geometry"),
														e);
										mapControl.setToolTipText(null);
										return;
									}
								}
								break;
							case FShape.TEXT:
								break;
							case FShape.MULTI: // Can have points, lines and
												// polygons
								// POINTS:
								if ((shape instanceof FPoint2D)
										|| (shape instanceof FPoint3D)) {
									// Points -> Coordinates
									key = PluginServices.getText(this,
											"Coordinates");
									values = ((Vector) infoSelected
											.getCalculatedLayerFields()
											.get(key));
									if (values != null) {
										if (shape instanceof FPoint2D) {
											FPoint2D point2D = (FPoint2D) shape;
											values.add(PluginServices.getText(
													this, "X")
													+ "= "
													+ FormatDecimalNumber
															.formatDecimal(point2D
																	.getX())
													+ ", "
													+ PluginServices.getText(
															this, "Y")
													+ "="
													+ FormatDecimalNumber
															.formatDecimal(point2D
																	.getY()));
											// values.add(PluginServices.getText(this,
											// "X") + "= " +
											// FormatDecimalNumber.formatDecimal(point2D.getX(),
											// "·10", true) + ", " +
											// PluginServices.getText(this, "Y")
											// + "=" +
											// FormatDecimalNumber.formatDecimal(point2D.getY(),
											// "·10", true));
											infoSelected
													.setAnyCalculatedLayerFieldsAdded(true);
										} else if (shape instanceof FPoint3D) {
											FPoint3D point3D = (FPoint3D) shape;
											values.add(PluginServices.getText(
													this, "X")
													+ "= "
													+ FormatDecimalNumber
															.formatDecimal(point3D
																	.getX())
													+ ", "
													+ PluginServices.getText(
															this, "Y")
													+ "="
													+ FormatDecimalNumber
															.formatDecimal(point3D
																	.getY())
													+ ", "
													+ PluginServices.getText(
															this, "Z")
													+ "="
													+ FormatDecimalNumber
															.formatDecimal(point3D
																	.getZs()[0]));
											// values.add(PluginServices.getText(this,
											// "X") + "= " +
											// FormatDecimalNumber.formatDecimal(point3D.getX(),
											// "·10", true) + ", " +
											// PluginServices.getText(this, "Y")
											// + "=" +
											// FormatDecimalNumber.formatDecimal(point3D.getY(),
											// "·10", true) + ", " +
											// PluginServices.getText(this, "Z")
											// + "=" +
											// FormatDecimalNumber.formatDecimal(point3D.getZs()[0],
											// "·10", true));
											infoSelected
													.setAnyCalculatedLayerFieldsAdded(true);
										}
									}

									// Removes other selected calculated fields
									// that are for other kind of geometries
									HashMap map = infoSelected
											.getCalculatedLayerFields();
									cFieldsToRestore = new Vector();

									String text = PluginServices.getText(this,
											"Area");
									cFieldsToRestore.add(text);
									map.remove(text);

									text = PluginServices.getText(this,
											"Perimeter");
									cFieldsToRestore.add(text);
									map.remove(text);

									text = PluginServices.getText(this,
											"Length");
									cFieldsToRestore.add(text);
									map.remove(text);

									text = PluginServices.getText(this,
											"Number_of_points");
									cFieldsToRestore.add(text);
									map.remove(text);

									continue;
								} else {
									// POLIGONS:
									if ((shape instanceof FPolygon2D)
											|| (shape instanceof FPolygon3D)) {
										// Polygon -> Area
										key = PluginServices.getText(this,
												"Area");
										values = ((Vector) infoSelected
												.getCalculatedLayerFields()
												.get(key));
										if (values != null) {

											try {
												area = GeometryUtilities
														.getArea(infoSelected
																.getLayer(),
																geometry);

												values.add(FormatDecimalNumber
														.formatDecimal(area
																/ relationAreaUnitsToMeters2)
														+ " " + nameAreaUnits);
												// values.add(FormatDecimalNumber.formatDecimal(area
												// / relationAreaUnitsToMeters2,
												// "·10", true) + " " +
												// nameAreaUnits);
												infoSelected
														.setAnyCalculatedLayerFieldsAdded(true);
											} catch (Exception e) {
												NotificationManager
														.showMessageError(
																PluginServices
																		.getText(
																				null,
																				"Failed_calculating_area_of_geometry"),
																e);
												mapControl.setToolTipText(null);
												return;
											}
											// values.add(FormatDecimalNumber.formatDecimal(FConverter.java2d_to_jts((FShape)
											// shape).getArea() /
											// relationAreaUnitsToMeters2,
											// "·10", true) + " " +
											// nameAreaUnits);
											// infoSelected.setAnyCalculatedLayerFieldsAdded(true);
										}

										// Polygon -> Perimeter
										key = PluginServices.getText(this,
												"Perimeter");
										values = ((Vector) infoSelected
												.getCalculatedLayerFields()
												.get(key));
										if (values != null) {

											try {
												perimeter = GeometryUtilities
														.getLength(
																infoSelected
																		.getLayer()
																		.getMapContext()
																		.getViewPort(),
																geometry);

												values.add(FormatDecimalNumber
														.formatDecimal(perimeter
																/ relationDistanceUnitsToMeters)
														+ " "
														+ nameDistanceUnits);
												// values.add(FormatDecimalNumber.formatDecimal(perimeter
												// /
												// relationDistanceUnitsToMeters,
												// "·10", true) + " " +
												// nameDistanceUnits);
												infoSelected
														.setAnyCalculatedLayerFieldsAdded(true);
											} catch (Exception e) {
												NotificationManager
														.showMessageError(
																PluginServices
																		.getText(
																				null,
																				"Failed_calculating_perimeter_of_geometry"),
																e);
												mapControl.setToolTipText(null);
												return;
											}
											// if (shape == null) {
											// values.add("");
											// }
											// else {
											// values.add(FormatDecimalNumber.formatDecimal(FConverter.java2d_to_jts((FShape)
											// shape).getLength() /
											// relationDistanceUnitsToMeters,
											// "·10", true) + " " +
											// nameDistanceUnits);
											// infoSelected.setAnyCalculatedLayerFieldsAdded(true);
											// }
										}

										// Removes other selected calculated
										// fields that are for other kind of
										// geometries
										HashMap map = infoSelected
												.getCalculatedLayerFields();
										cFieldsToRestore = new Vector();

										String text = PluginServices.getText(
												this, "Coordinates");
										cFieldsToRestore.add(text);
										map.remove(text);

										text = PluginServices.getText(this,
												"Length");
										cFieldsToRestore.add(text);
										map.remove(text);

										text = PluginServices.getText(this,
												"Number_of_points");
										cFieldsToRestore.add(text);
										map.remove(text);

										continue;
									} else {
										// LINES:
										if ((shape instanceof FPolyline2D)
												|| (shape instanceof FPolyline3D)) {
											// Lines -> Length
											key = PluginServices.getText(this,
													"Length");
											values = ((Vector) infoSelected
													.getCalculatedLayerFields()
													.get(key));
											if (values != null) {

												try {
													perimeter = GeometryUtilities
															.getLength(
																	infoSelected
																			.getLayer()
																			.getMapContext()
																			.getViewPort(),
																	geometry);

													values.add(FormatDecimalNumber
															.formatDecimal(perimeter
																	/ relationDistanceUnitsToMeters)
															+ " "
															+ nameDistanceUnits);
													// values.add(FormatDecimalNumber.formatDecimal(perimeter
													// /
													// relationDistanceUnitsToMeters,
													// "·10", true) + " " +
													// nameDistanceUnits);
													infoSelected
															.setAnyCalculatedLayerFieldsAdded(true);
												} catch (Exception e) {
													NotificationManager
															.showMessageError(
																	PluginServices
																			.getText(
																					null,
																					"Failed_calculating_perimeter_of_geometry"),
																	e);
													mapControl
															.setToolTipText(null);
													return;
												}

												// values.add(FormatDecimalNumber.formatDecimal(FConverter.java2d_to_jts((FShape)
												// shape).getLength() /
												// relationDistanceUnitsToMeters,
												// "·10", true) + " " +
												// nameDistanceUnits);
												// infoSelected.setAnyCalculatedLayerFieldsAdded(true);
											}

											// Removes other selected calculated
											// fields that are for other kind of
											// geometries
											HashMap map = infoSelected
													.getCalculatedLayerFields();
											cFieldsToRestore = new Vector();

											String text = PluginServices
													.getText(this,
															"Coordinates");
											cFieldsToRestore.add(text);
											map.remove(text);

											text = PluginServices.getText(this,
													"Area");
											cFieldsToRestore.add(text);
											map.remove(text);

											text = PluginServices.getText(this,
													"Perimeter");
											cFieldsToRestore.add(text);
											map.remove(text);

											text = PluginServices.getText(this,
													"Number_of_points");
											cFieldsToRestore.add(text);
											map.remove(text);

											continue;
										} else {
											// MULTIPOINTS:
											if ((shape instanceof FMultiPoint2D)
													|| (shape instanceof FMultipoint3D)) {
												// MultiPoint -> Number of
												// points
												key = PluginServices.getText(
														this,
														"Number_of_points");
												values = ((Vector) infoSelected
														.getCalculatedLayerFields()
														.get(key));
												if (values != null) {
													if (shape instanceof FMultiPoint2D) {
														values.add(Integer
																.toString(((FMultiPoint2D) shape)
																		.getNumPoints()));
														infoSelected
																.setAnyCalculatedLayerFieldsAdded(true);
														continue;
													}

													if (shape instanceof FMultipoint3D) {
														values.add(Integer
																.toString(((FMultipoint3D) shape)
																		.getNumPoints()));
														infoSelected
																.setAnyCalculatedLayerFieldsAdded(true);
														continue;
													}
												}

												// Removes other selected
												// calculated fields that are
												// for other kind of geometries
												HashMap map = infoSelected
														.getCalculatedLayerFields();
												cFieldsToRestore = new Vector();

												String text = PluginServices
														.getText(this,
																"Coordinates");
												cFieldsToRestore.add(text);
												map.remove(text);

												text = PluginServices.getText(
														this, "Area");
												cFieldsToRestore.add(text);
												map.remove(text);

												text = PluginServices.getText(
														this, "Perimeter");
												cFieldsToRestore.add(text);
												map.remove(text);

												text = PluginServices.getText(
														this, "Length");
												cFieldsToRestore.add(text);
												map.remove(text);
											}
											// OTHERS
										}
									}
								}
								break;
							case FShape.MULTIPOINT:
								// Number of points
								key = PluginServices.getText(this,
										"Number_of_points");
								values = ((Vector) infoSelected
										.getCalculatedLayerFields().get(key));
								if (values != null) {
									if (shape instanceof FMultiPoint2D) {
										values.add(Integer
												.toString(((FMultiPoint2D) shape)
														.getNumPoints()));
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
										continue;
									}

									if (shape instanceof FMultipoint3D) {
										values.add(Integer
												.toString(((FMultipoint3D) shape)
														.getNumPoints()));
										infoSelected
												.setAnyCalculatedLayerFieldsAdded(true);
										continue;
									}
								}

								break;
							case FShape.CIRCLE:
								// Disabled:
								// // Coordinates of the centre
								// if
								// (cFields.contains(PluginServices.getText(this,
								// "Coordinates_of_the_centre"))) {
								// if (shape == null) {
								// // AGREGAR ENTRADA con valor ""
								//
								// }
								// else {
								// if (shape instanceof F??2D) {
								// FPoint2D point2D = (FPoint2D) shape;
								// System.out.println("Coordinates_of_the_centre: "
								// + point2D.getX() + ", " + point2D.getY());
								// }
								//
								// if (shape instanceof F??3D) {
								// FPoint3D point3D = (FPoint3D) shape;
								// System.out.println("Coordinates_of_the_centre: "
								// + point3D.getX() + ", " + point3D.getY());
								// }
								// }
								// }
								//
								// // Radius
								// if
								// (cFields.contains(PluginServices.getText(this,
								// "Radius"))) {
								// if (shape == null) {
								// // AGREGAR ENTRADA con valor ""
								//
								// }
								// else {
								// if (shape instanceof F??2D) {
								// FPoint2D point2D = (FPoint2D) shape;
								// System.out.println("Radius: " +
								// point2D.getX() + ", " + point2D.getY());
								// }
								//
								// if (shape instanceof F??3D) {
								// FPoint3D point3D = (FPoint3D) shape;
								// System.out.println("Radius: " +
								// point3D.getX() + ", " + point3D.getY());
								// }
								// }
								// }
								//
								// // Circumference
								// if
								// (cFields.contains(PluginServices.getText(this,
								// "Circumference"))) {
								// if (shape == null) {
								// // AGREGAR ENTRADA con valor ""
								//
								// }
								// else {
								// if (shape instanceof F??2D) {
								// FPoint2D point2D = (FPoint2D) shape;
								// System.out.println("Circumference: " +
								// point2D.getX() + ", " + point2D.getY());
								// }
								//
								// if (shape instanceof F??3D) {
								// FPoint3D point3D = (FPoint3D) shape;
								// System.out.println("Circumference: " +
								// point3D.getX() + ", " + point3D.getY());
								// }
								// }
								// }
								break;
							case FShape.ARC:
								break;
							case FShape.ELLIPSE:
								break;
							case FShape.Z:
								break;
							default: // UNDEFINED
							}
						} catch (ReadDriverException dioe) {
							NotificationManager.showMessageError(
									PluginServices.getText(null,
											"Failed_getting_geometries"), dioe);
						}
					}
				}

				// Sets the new tool tip text to the current active view's
				// MapControl
				mapControl.setToolTipText(infoSelected.getToolTipText());

				if (cFieldsToRestore != null) {
					HashMap map = infoSelected.getCalculatedLayerFields();

					for (int i = 0; i < cFieldsToRestore.size(); i++) {
						map.put(cFieldsToRestore.elementAt(i), new Vector());
					}

					cFieldsToRestore = null;
				}
			} catch (Exception e) {
				throw new BehaviorException("Fallo al consultar "
						+ infoSelected.getLayer().getName());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#pointDoubleClick
	 * (com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		// Nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		// Uses the cursor of the previous selected tool. Or any that can be
		// selected in the future.
		return null;
	}

	/**
	 * <p>
	 * Creates a new <code>TreeModelListener</code> to remove this tool when the
	 * layer node is removed from the TOC of <code>view</code>.
	 * </p>
	 * 
	 * @param vista
	 *            the view
	 * @return javax.swing.event.TreeModelListener
	 */
	private TreeModelListener getTreeModelListener(final View view) {
		if (treeModelListener == null) {
			treeModelListener = new TreeModelListener() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * javax.swing.event.TreeModelListener#treeNodesChanged(javax
				 * .swing.event.TreeModelEvent)
				 */
				public void treeNodesChanged(TreeModelEvent e) {
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * javax.swing.event.TreeModelListener#treeNodesInserted(javax
				 * .swing.event.TreeModelEvent)
				 */
				public void treeNodesInserted(TreeModelEvent e) {
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * javax.swing.event.TreeModelListener#treeNodesRemoved(javax
				 * .swing.event.TreeModelEvent)
				 */
				public void treeNodesRemoved(TreeModelEvent e) {
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * javax.swing.event.TreeModelListener#treeStructureChanged(
				 * javax.swing.event.TreeModelEvent)
				 */
				public void treeStructureChanged(TreeModelEvent e) {
					if (!findLayer(view.getTOC().getTree())) {
						view.getTOC().getTree()
								.removeMouseListener(treeMouseAdapter);
						view.getTOC().getTree().getModel()
								.removeTreeModelListener(treeModelListener);
						mapControl.removeCombinedTool();
						mapControl.setToolTipText(null);
						ToolTipManager.sharedInstance().setInitialDelay(
								oldInitialDelay);
						ToolTipManager.sharedInstance().setReshowDelay(
								oldReshowDelay);
						// ToolTipManager.sharedInstance().setDismissDelay(oldDismissDelay);
					}
				}
			};
		}

		return treeModelListener;
	}

	/**
	 * <p>
	 * Creates a new <code>MouseAdapter</code> to remove this tool when the
	 * layer changes its visibility to <i>invisible</i> in the TOC of the
	 * <code>view</code>.
	 * </p>
	 * 
	 * @param view
	 *            the view
	 * @param qil
	 *            data stored to display by this tool
	 * @return java.awt.event.MouseAdapter
	 */
	private MouseAdapter getTreeMouseAdapter(final View view,
			final QuickInfoFLayerSelected qil) {
		if (treeMouseAdapter == null) {
			treeMouseAdapter = new MouseAdapter() {
				/*
				 * (non-Javadoc)
				 * 
				 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.
				 * MouseEvent)
				 */
				public void mousePressed(MouseEvent e) {
					TreePath tp = ((JTree) e.getSource()).getPathForLocation(
							e.getX(), e.getY());

					if (tp != null) {
						DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) tp
								.getLastPathComponent();

						if (dmtn != null) {
							TocItemBranch tib = (TocItemBranch) dmtn
									.getUserObject();

							if (tib != null) {

								if ((tib.getLayer() != null)
										&& (tib.getLayer().equals(qil
												.getLayer()))
										&& (!tib.getLayer().isVisible())) {
									view.getTOC()
											.getTree()
											.removeMouseListener(
													treeMouseAdapter);
									view.getTOC()
											.getTree()
											.getModel()
											.removeTreeModelListener(
													treeModelListener);
									mapControl.removeCombinedTool();
									mapControl.setToolTipText(null);
									ToolTipManager.sharedInstance()
											.setInitialDelay(oldInitialDelay);
									ToolTipManager.sharedInstance()
											.setReshowDelay(oldReshowDelay);
									// ToolTipManager.sharedInstance().setDismissDelay(oldDismissDelay);
								}
							}
						}
					}
				}
			};
		}

		return treeMouseAdapter;
	}

	/**
	 * <p>
	 * Gets the pixel tolerance used by this tool.
	 * </p>
	 * 
	 * @return the pixel tolerance used by this tool
	 */
	public int getPixelTolerance() {
		return pixel_tolerance;
	}

	/**
	 * <p>
	 * Sets the pixel tolerance to be used by this tool.
	 * </p>
	 * 
	 * @return the pixel tolerance to be used by this tool
	 */
	public void setPixelTolerance(int pixel_tolerance) {
		this.pixel_tolerance = pixel_tolerance;
	}

	/**
	 * <p>
	 * Finds recursively if the layer is found and is visible in the tree.
	 * </p>
	 * 
	 * @param tree
	 *            the <code>JTree</code> where find
	 * @return <code>true</code> if the layer is found and is visible in the
	 *         tree; otherwise <code>false</code>
	 */
	public boolean findLayer(JTree tree) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		return findLayerInAllNodes(root);
	}

	/**
	 * <p>
	 * Finds recursively if the layer is found and is visible in the tree
	 * <code>node</code>.
	 * </p>
	 * 
	 * @param tree
	 *            the node where find
	 * @return <code>true</code> if the layer is found and is visible in the
	 *         tree node; otherwise <code>false</code>
	 */
	public boolean findLayerInAllNodes(TreeNode node) {
		boolean b1 = false, b2 = false;

		if (node != null) {
			Object userObject = ((DefaultMutableTreeNode) node).getUserObject();

			if (userObject instanceof TocItemBranch) {
				TocItemBranch tib = (TocItemBranch) userObject;

				if (tib != null) {
					if ((tib.getLayer() != null)
							&& (tib.getLayer().equals(infoSelected.getLayer()))
							&& (tib.getLayer().isVisible())) {
						b1 = true;
					}
				}
			}

			if (node.getChildCount() >= 0) {
				for (Enumeration e = node.children(); e.hasMoreElements();) {
					TreeNode n = (TreeNode) e.nextElement();
					b2 = b2 | findLayerInAllNodes(n);
				}
			}
		}

		return b1 | b2;
	}

	/**
	 * <p>
	 * Gets the number of events that will ignore.
	 * </p>
	 * 
	 * <p>
	 * This is very useful to accelerate the this tool when the layer is being
	 * edited.
	 * </p>
	 * 
	 * @return the number of events that will ignore
	 */
	public short getNumberOfEventsToIgnore() {
		return eventsToIgnore;
	}

	/**
	 * <p>
	 * Sets the number of events that will ignore.
	 * </p>
	 * 
	 * <p>
	 * This is very useful to accelerate the this tool when the layer is being
	 * edited.
	 * </p>
	 * 
	 * @param n
	 *            the number of events that will ignore
	 */
	public void setNumberOfEventsToIgnore(short n) {
		eventsToIgnore = n;
	}

	/**
	 * <p>
	 * Gets the number of events that will ignore if the layer is being edited.
	 * </p>
	 * 
	 * <p>
	 * This is very useful to accelerate the this tool when the layer is being
	 * edited.
	 * </p>
	 * 
	 * @return the number of events that will ignore if the layer is being
	 *         edited
	 */
	public short getNumberOfEventsToIgnoreInEditionMode() {
		return eventsToIgnoreInEditionMode;
	}

	/**
	 * <p>
	 * Sets the number of events that will ignore if the layer is being edited.
	 * </p>
	 * 
	 * <p>
	 * This is very useful to accelerate the this tool when the layer is being
	 * edited.
	 * </p>
	 * 
	 * @param n
	 *            the number of events that will ignore if the layer is being
	 *            edited
	 */
	public void setNumberOfEventsToIgnoreInEditionMode(short n) {
		eventsToIgnoreInEditionMode = n;
	}
}