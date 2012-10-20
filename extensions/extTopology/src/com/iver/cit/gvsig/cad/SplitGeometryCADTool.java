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
package com.iver.cit.gvsig.cad;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.jts.JtsUtil;

import statemap.State;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.cad.sm.SplitGeometryCADToolContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;

/**
 * CAD Tool which splits the selected geometries of a vectorial editing layer
 * with a digitized polyline.
 * 
 * 
 * @author Alvaro Zabala
 * 
 */
public class SplitGeometryCADTool extends DefaultCADTool {

	private static Logger logger = Logger.getLogger(SplitGeometryCADTool.class
			.getName());

	/**
	 * String representation of this tool (used for example to active the tool
	 * in mapcontrol)
	 */
	public static final String SPLIT_GEOMETRY_TOOL_NAME = "_split_geometry";

	/**
	 * finite state machine for this CAD tool
	 */
	protected SplitGeometryCADToolContext _fsm;

	/**
	 * Flag to mark if the digitized line has been finished.
	 */
	protected boolean digitizingFinished = false;

	/**
	 * Collection of digitized geometries
	 */
	protected List<Point2D> clickedPoints;

	/**
	 * Default Constructor
	 */
	public SplitGeometryCADTool() {

	}

	/**
	 * Initialization method.
	 */
	public void init() {
		digitizingFinished = false;
		_fsm = new SplitGeometryCADToolContext(this);
		setNextTool(SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME);
	}

	public boolean isDigitizingFinished() {
		return digitizingFinished;
	}

	public String toString() {
		return SplitGeometryCADTool.SPLIT_GEOMETRY_TOOL_NAME;
	}

	public void finishDigitizedLine() {
	}

	public ArrayList getSelectedRows() {
		return (ArrayList) CADUtil.getSelectedFeatures(getVLE());
	}

	public void splitSelectedGeometryWithDigitizedLine() {
		Point2D[] clickedPts = new Point2D[this.clickedPoints.size()];
		clickedPoints.toArray(clickedPts);
		Coordinate[] digitizedCoords = JtsUtil
				.getPoint2DAsCoordinates(clickedPts);
		LineString splittingLs = JtsUtil.GEOMETRY_FACTORY
				.createLineString(digitizedCoords);

		ArrayList selectedRows = getSelectedRows();
		// ArrayList<IRowEdited> splitSelectionGeoms = new
		// ArrayList<IRowEdited>();
		IRowEdited editedRow = null;
		VectorialLayerEdited vle = getVLE();
		VectorialEditableAdapter vea = vle.getVEA();
		getCadToolAdapter().getMapControl().getMapContext().beginAtomicEvent();
		vea.startComplexRow();
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < selectedRows.size(); i++) {
			editedRow = (IRowEdited) selectedRows.get(i);
			IFeature feat = (IFeature) editedRow.getLinkedRow().cloneRow();
			IGeometry ig = feat.getGeometry();
			Geometry jtsGeo = NewFConverter.toJtsGeometry(ig);
			try {
				Geometry splitGeo = JtsUtil.split(jtsGeo, splittingLs);
				if (splitGeo instanceof GeometryCollection
						&& ((GeometryCollection) splitGeo).getNumGeometries() > 1) {

					// Saving originals polygons index
					indices.add(new Integer(editedRow.getIndex()));
					// try {
					// vle.clearSelection(false);
					// } catch (ReadDriverException e) {
					// PluginServices.getLogger().error("Error clearing selection",
					// e);
					// }
					// and then, we add new features for each split geometry
					GeometryCollection gc = (GeometryCollection) splitGeo;
					for (int j = 0; j < gc.getNumGeometries(); j++) {
						Geometry g = gc.getGeometryN(j);
						IGeometry fmapGeo = NewFConverter.toFMap(g);
						String newFID = vea.getNewFID();
						DefaultFeature df = new DefaultFeature(fmapGeo,
								feat.getAttributes(), newFID);
						int newIdx = 0;
						if (j == 0) {
							newIdx = editedRow.getIndex();
							CADUtil.modifyFeature(vle, newIdx, getName(), df);
						} else {
							newIdx = CADUtil.addFeature(vle, df, getName());
						}
						DefaultRowEdited newRowEdited = new DefaultRowEdited(
								df, IRowEdited.STATUS_ADDED, newIdx);
						// splitSelectionGeoms.add(newRowEdited);
						vle.addSelectionCache(newRowEdited);
					}// for j
				}// if splitGeo
			} catch (Exception ex) {
				PluginServices.getLogger().error(
						"Error splitting geom " + editedRow.getIndex(), ex);
			}
		}

		// Vector index ordered highest to lowest
		// Collections.sort(indices, Collections.reverseOrder());
		// // Clear polygon erasing their lines in the table
		// for(int i = 0; i< indices.size();i++) {
		//
		// try {
		// vea.removeRow(((Integer)indices.get(i)).intValue(),
		// PluginServices.getText(this,"deleted_feature"),
		// EditionEvent.GRAPHIC);
		// } catch (ExpansionFileReadException e) {
		// logger.error("Error ExpansionFileReadException en la funcion split");
		// } catch (ReadDriverException e) {
		// logger.error("Error ReadDriverException en la funcion split");
		// }
		// }
		vea.endComplexRow(getName());

		getCadToolAdapter().getMapControl().getMapContext().endAtomicEvent();
	}

	public void end() {
		getCadToolAdapter().refreshEditedLayer();
		init();
	}

	public void addOption(String s) {
		State actualState = _fsm.getPreviousState();
		String status = actualState.getName();
		if (s.equals(PluginServices.getText(this, "cancel"))) {
			init();
			return;
		}
		if (status.equals("TopologicalEdition.FirstPoint")) {
			return;
		}
		init();

	}

	public void addPoint(double x, double y, InputEvent event) {

		State actualState = _fsm.getPreviousState();
		String status = actualState.getName();
		if (status.equals("SplitGeometry.FirstPoint")) {
			clickedPoints = new ArrayList<Point2D>();
			clickedPoints.add(new Point2D.Double(x, y));
		} else if (status.equals("SplitGeometry.DigitizingLine")) {
			clickedPoints.add(new Point2D.Double(x, y));
			if (event != null && ((MouseEvent) event).getClickCount() == 2) {
				digitizingFinished = true;
				finishDigitizedLine();
				splitSelectedGeometryWithDigitizedLine();
				end();
			}
		}
	}

	public void addValue(double d) {
	}

	/**
	 * Draws a polyline with the clicked digitized points in the specified
	 * graphics.
	 * 
	 * @param g2
	 *            graphics on to draw the polyline
	 * @param x
	 *            last x mouse pointer position
	 * @param y
	 *            last y mouse pointer position
	 */
	protected void drawPolyLine(Graphics2D g, double x, double y) {
		GeneralPathX gpx = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
				clickedPoints.size());
		Point2D firstPoint = clickedPoints.get(0);
		gpx.moveTo(firstPoint.getX(), firstPoint.getY());
		for (int i = 1; i < clickedPoints.size(); i++) {
			Point2D clickedPoint = clickedPoints.get(i);
			gpx.lineTo(clickedPoint.getX(), clickedPoint.getY());

		}
		gpx.lineTo(x, y);
		ShapeFactory.createPolyline2D(gpx).draw((Graphics2D) g,
				getCadToolAdapter().getMapControl().getViewPort(),
				DefaultCADTool.geometrySelectSymbol);
	}

	public void drawOperation(Graphics g, double x, double y) {
		State actualState = _fsm.getState();
		String status = actualState.getName();

		// draw splitting line
		if ((status.equals("SplitGeometry.DigitizingLine"))) {
			drawPolyLine((Graphics2D) g, x, y);
		}

		// draw selection
		try {
			Image imgSel = getVLE().getSelectionImage();
			if (imgSel != null)
				g.drawImage(imgSel, 0, 0, null);
		} catch (Exception e) {
			PluginServices.getLogger().error("Error drawing Editing Selection",
					e);
		}
	}

	public String getName() {
		return PluginServices.getText(this, "split_geometry_shell");
	}

	public void transition(double x, double y, InputEvent event) {
		try {
			_fsm.addPoint(x, y, event);
		} catch (Exception e) {
			init();
		}

	}

	public void transition(double d) {
		_fsm.addValue(d);
	}

	public void transition(String s) throws CommandException {
		if (!super.changeCommand(s)) {
			_fsm.addOption(s);
		}
	}

}
