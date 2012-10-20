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

/* CVS MESSAGES:
 *
 * $Id: AbstractPlacementConstraints.java 13884 2007-09-19 16:26:04Z jaume $
 * $Log$
 * Revision 1.8  2007-09-19 16:25:39  jaume
 * ReadExpansionFileException removed from this context and removed unnecessary imports
 *
 * Revision 1.7  2007/04/18 15:35:11  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2007/04/13 12:42:45  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2007/04/13 11:59:30  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2007/04/12 16:01:11  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2007/04/11 16:01:08  jaume
 * maybe a label placer refactor
 *
 * Revision 1.2  2007/03/09 08:33:43  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.2  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2007/02/09 07:47:05  jaume
 * Isymbol moved
 *
 * Revision 1.1.2.3  2007/02/02 16:21:24  jaume
 * start commiting labeling stuff
 *
 * Revision 1.1.2.2  2007/02/01 11:42:47  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2007/01/30 18:10:45  jaume
 * start commiting labeling stuff
 *
 * Revision 1.1.2.1  2007/01/26 13:49:03  jaume
 * *** empty log message ***
 *
 *
 */

package org.gvsig.symbology.fmap.labeling.placements;

import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.utiles.XMLEntity;

/**
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public abstract class AbstractPlacementConstraints implements Cloneable,
		IPlacementConstraints {
	// CAUTION, THIS IS A CLONEABLE OBJECT, DON'T FORGET TO
	// UPDATE clone() METHOD WHEN ADDING FIELDS
	private int duplicateLabelsMode = ONE_LABEL_PER_FEATURE_PART; // default
																	// duplicate
																	// treatment
	private int placementMode;
	private boolean belowTheLine;
	private boolean aboveTheLine;
	private boolean onTheLine;
	private boolean pageOriented;
	private int locationAlongLine;
	private boolean fitInsidePolygon;

	// CAUTION, THIS IS A CLONEABLE OBJECT, DON'T FORGET TO
	// UPDATE clone() METHOD WHEN ADDING FIELDS

	public void setDuplicateLabelsMode(int mode) {
		if (mode != REMOVE_DUPLICATE_LABELS && mode != ONE_LABEL_PER_FEATURE
				&& mode != ONE_LABEL_PER_FEATURE_PART)
			throw new IllegalArgumentException("Only REMOVE_DUPLICATE_LABELS, "
					+ "ONE_LABEL_PER_FEATURE "
					+ "or ONE_LABEL_PER_FEATURE_PARTS allowed");
		this.duplicateLabelsMode = mode;
	}

	public void setPlacementMode(int mode) {
		if (this instanceof PointPlacementConstraints) {
			if (mode != OFFSET_HORIZONTALY_AROUND_THE_POINT
					&& mode != ON_TOP_OF_THE_POINT
					&& mode != AT_SPECIFIED_ANGLE
					&& mode != AT_ANGLE_SPECIFIED_BY_A_FIELD)
				throw new IllegalArgumentException(
						"Only OFFSET_HORIZONTALY_AROUND_THE_POINT, "
								+ "ON_TOP_OF_THE_POINT, "
								+ "AT_SPECIFIED_ANGLE "
								+ "or AT_ANGLE_SPECIFIED_BY_A_FIELD allowed for points: "
								+ mode);
		}

		if (this instanceof PolygonPlacementConstraints) {
			if (mode != HORIZONTAL && mode != PARALLEL)
				throw new IllegalArgumentException("Only HORIZONTAL, "
						+ "or PARALLEL allowed for polygons: " + mode);
		}

		if (this instanceof LinePlacementConstraints) {
			if (mode != HORIZONTAL && mode != PARALLEL
					&& mode != FOLLOWING_LINE && mode != PERPENDICULAR)
				throw new IllegalArgumentException(
						"Only HORIZONTAL, PARALLEL,"
								+ "FOLLOWING_LINE, or PERPENDICULAR allowed for lines: "
								+ mode);
		}
		this.placementMode = mode;
	}

	public boolean isHorizontal() {
		return placementMode == HORIZONTAL;
	}

	public boolean isPerpendicular() {
		return placementMode == PERPENDICULAR;
	}

	public boolean isFollowingLine() {
		return placementMode == FOLLOWING_LINE;
	}

	public boolean isParallel() {
		return placementMode == PARALLEL;
	}

	public int getDuplicateLabelsMode() {
		return duplicateLabelsMode;
	}

	/**
	 * Tells if the place mode selected is to put the label over the
	 * <b>POINT</b>
	 * 
	 * @return boolean
	 */
	public boolean isOnTopOfThePoint() {
		return placementMode == ON_TOP_OF_THE_POINT;
	}

	public boolean isBelowTheLine() {
		return belowTheLine;
	}

	public boolean isAboveTheLine() {
		return aboveTheLine;
	}

	public boolean isOnTheLine() {
		return onTheLine;
	}

	public boolean isPageOriented() {
		return pageOriented;
	}

	public void setOnTheLine(boolean b) {
		this.onTheLine = b;
	}

	public void setPageOriented(boolean b) {
		this.pageOriented = b;
	}

	public void setBelowTheLine(boolean b) {
		this.belowTheLine = b;
	}

	public void setAboveTheLine(boolean b) {
		this.aboveTheLine = b;
	}

	public boolean isAtTheEndOfLine() {
		return locationAlongLine == AT_THE_END_OF_THE_LINE;
	}

	public boolean isAtTheBeginingOfLine() {
		return locationAlongLine == AT_THE_BEGINING_OF_THE_LINE;
	}

	public boolean isInTheMiddleOfLine() {
		return locationAlongLine == AT_THE_MIDDLE_OF_THE_LINE;
	}

	public boolean isAtBestOfLine() {
		return locationAlongLine == AT_BEST_OF_LINE;
	}

	public boolean isAroundThePoint() {
		return placementMode == OFFSET_HORIZONTALY_AROUND_THE_POINT;
	}

	public boolean isFitInsidePolygon() {
		return fitInsidePolygon;
	}

	public void setFitInsidePolygon(boolean b) {
		fitInsidePolygon = b;
	}

	public void setLocationAlongTheLine(int location) {
		if (location != IPlacementConstraints.AT_THE_MIDDLE_OF_THE_LINE
				&& location != IPlacementConstraints.AT_THE_BEGINING_OF_THE_LINE
				&& location != IPlacementConstraints.AT_THE_END_OF_THE_LINE
				&& location != IPlacementConstraints.AT_BEST_OF_LINE) {
			throw new IllegalArgumentException(
					"Only IPlacementConstraints.AT_THE_MIDDLE_OF_THE_LINE, "
							+ "IPlacementConstraints.AT_THE_BEGINING_OF_THE_LINE, or "
							+ "IPlacementConstraints.AT_THE_END_OF_THE_LINE, or "
							+ "IPlacementConstraints.AT_BEST_OF_LINE values are allowed");
		}
		this.locationAlongLine = location;
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("duplicateLabelsMode", getDuplicateLabelsMode());
		xml.putProperty("placementMode", placementMode);
		xml.putProperty("belowTheLine", isBelowTheLine());
		xml.putProperty("aboveTheLine", isAboveTheLine());
		xml.putProperty("onTheLine", isOnTheLine());
		xml.putProperty("pageOriented", isPageOriented());
		xml.putProperty("locationAlongLine", locationAlongLine);
		xml.putProperty("fitInsidePolygon", isFitInsidePolygon());
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		setDuplicateLabelsMode(xml.getIntProperty("duplicateLabelsMode"));
		setPlacementMode(xml.getIntProperty("placementMode"));
		setBelowTheLine(xml.getBooleanProperty("belowTheLine"));
		setAboveTheLine(xml.getBooleanProperty("aboveTheLine"));
		setOnTheLine(xml.getBooleanProperty("onTheLine"));
		setPageOriented(xml.getBooleanProperty("pageOriented"));
		locationAlongLine = xml.getIntProperty("locationAlongLine");
		setFitInsidePolygon(xml.getBooleanProperty("fitInsidePolygon"));
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		try {
			AbstractPlacementConstraints clone = getClass().newInstance();
			clone.aboveTheLine = this.aboveTheLine;
			clone.belowTheLine = this.belowTheLine;
			clone.duplicateLabelsMode = this.duplicateLabelsMode;
			clone.fitInsidePolygon = this.fitInsidePolygon;
			clone.locationAlongLine = this.locationAlongLine;
			clone.onTheLine = this.onTheLine;
			clone.pageOriented = this.pageOriented;
			clone.placementMode = this.placementMode;
			return clone;
		} catch (Exception e) {
			throw new CloneNotSupportedException(e.getMessage());
		}
	}
}
