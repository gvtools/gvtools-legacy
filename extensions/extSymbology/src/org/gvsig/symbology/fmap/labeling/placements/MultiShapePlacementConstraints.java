/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.symbology.fmap.labeling.placements;

import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;
import com.iver.utiles.XMLEntity;

/**
 *
 * MultiShapePlacementConstraints.java
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Apr 1, 2008
 *
 */
public class MultiShapePlacementConstraints extends AbstractPlacementConstraints {

	private IPlacementConstraints polygonConstraints;
	private IPlacementConstraints lineConstraints;
	private IPlacementConstraints pointConstraints;


	public MultiShapePlacementConstraints() { }

	public MultiShapePlacementConstraints(
			IPlacementConstraints pointConstraints,
			IPlacementConstraints lineConstraints,
			IPlacementConstraints polygonConstraints) {
		this.pointConstraints = pointConstraints;
		this.lineConstraints = lineConstraints;
		this.polygonConstraints = polygonConstraints;

	}

	public void setPolygonConstraints(IPlacementConstraints polygonConstraints) {
		this.polygonConstraints = polygonConstraints;
	}

	public void setLineConstraints(IPlacementConstraints lineConstraints) {
		this.lineConstraints = lineConstraints;
	}

	public void setPointConstraints(IPlacementConstraints pointConstraints) {
		this.pointConstraints = pointConstraints;
	}

	public IPlacementConstraints getPolygonConstraints() {
		return polygonConstraints;
	}

	public IPlacementConstraints getLineConstraints() {
		return lineConstraints;
	}

	public IPlacementConstraints getPointConstraints() {
		return pointConstraints;
	}


//	@Override
//	public boolean isAboveTheLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isAroundThePoint() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isAtBestOfLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isAtTheBeginingOfLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isAtTheEndOfLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isBelowTheLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isFitInsidePolygon() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isFollowingLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isHorizontal() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isInTheMiddleOfLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isOnTheLine() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isOnTopOfThePoint() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isPageOriented() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isParallel() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public boolean isPerpendicular() {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public void setAboveTheLine(boolean b) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public void setBelowTheLine(boolean b) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//
//	@Override
//	public void setFitInsidePolygon(boolean b) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public void setLocationAlongTheLine(int location) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public void setOnTheLine(boolean b) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public void setPageOriented(boolean b) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}
//
//	@Override
//	public void setPlacementMode(int mode) {
//		throw new Error("Operation delegated to its members. Access them directly");
//	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());

		// point constraints
		if (pointConstraints != null) {
			XMLEntity points = pointConstraints.getXMLEntity();
			points.putProperty("id", "PointConstraints");
			xml.addChild(points);
		}

		// line constraints
		if (lineConstraints != null) {
			XMLEntity lines = lineConstraints.getXMLEntity();
			lines.putProperty("id", "LineConstraints");
			xml.addChild(lines);
		}

		// point constraints
		if (polygonConstraints != null) {
			XMLEntity polygons = polygonConstraints.getXMLEntity();
			polygons.putProperty("id", "PolygonConstraints");
			xml.addChild(polygons);
		}

		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		XMLEntity aux;

		// points
		if ( ((aux = xml.firstChild("id", "PointConstraints")) != null) ) {
			pointConstraints = LabelingFactory.createPlacementConstraintsFromXML(aux);
		}

		// lines
		if ( ((aux = xml.firstChild("id", "LineConstraints")) != null) ) {
			lineConstraints = LabelingFactory.createPlacementConstraintsFromXML(aux);
		}

		// polygons
		if ( ((aux = xml.firstChild("id", "PolygonConstraints")) != null) ) {
			polygonConstraints = LabelingFactory.createPlacementConstraintsFromXML(aux);
		}

	}



}
