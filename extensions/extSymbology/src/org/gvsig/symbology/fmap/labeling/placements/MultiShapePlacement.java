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

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;
import com.iver.utiles.swing.threads.Cancellable;

/**
 *
 * MultiShapePlacementConstraints.java
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Apr 1, 2008
 *
 */
public class MultiShapePlacement implements ILabelPlacement {
	private ILabelPlacement pointPlacement;
	private ILabelPlacement linePlacement;
	private ILabelPlacement polygonPlacement;


	/**
	 * Creates a new instance of MultiShapePlacement initializing the respective
	 * placements to those passed as parameters. Null values are allowed for
	 * the parameters and will cause that no label will be placed when the
	 * geometry belongs to such null values.
	 *
	 * @param pointPlacement, the placement for points
	 * @param linePlacement, the placement for lines
	 * @param polygonPlacement, the placement for polygons
	 */
	public MultiShapePlacement(
			ILabelPlacement pointPlacement,
			ILabelPlacement linePlacement,
			ILabelPlacement polygonPlacement) {
		this.pointPlacement = pointPlacement;
		this.linePlacement = linePlacement;
		this.polygonPlacement = polygonPlacement;
	}


	public ArrayList<LabelLocationMetrics> guess(LabelClass lc, IGeometry geom, IPlacementConstraints placementConstraints, double cartographicSymbolSize, Cancellable cancel, ViewPort vp) {
		MultiShapePlacementConstraints pc = (MultiShapePlacementConstraints) placementConstraints;

		FShape shp = (FShape)geom.getInternalShape();
		switch (shp.getShapeType() % FShape.Z) {
		case FShape.POINT:
			if (pointPlacement != null) {
				return pointPlacement.guess(lc, geom, pc.getPointConstraints(), cartographicSymbolSize, cancel,vp);
			}
			break;
		case FShape.LINE:
			if (linePlacement != null) {
				return linePlacement.guess(lc, geom, pc.getLineConstraints(), cartographicSymbolSize, cancel,vp);
			}
			break;
		case FShape.POLYGON:
			if (polygonPlacement != null) {
				return polygonPlacement.guess(lc, geom, pc.getPolygonConstraints(), cartographicSymbolSize, cancel,vp);
			}
			break;
		}
		return CannotPlaceLabel.NO_PLACES;
	}

	public boolean isSuitableFor(IPlacementConstraints placementConstraints,
			int shapeType) {
		// TODO shoud I also ask to each placement if it is suitable for these constraints????
		return (shapeType % FShape.Z) == FShape.MULTI;
	}




}
