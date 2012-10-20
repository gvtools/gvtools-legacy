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

import java.awt.geom.Point2D;

import org.apache.batik.ext.awt.geom.PathLength;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * 
 * LinePlacementInTheMiddle.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Dec 17, 2007
 * 
 */
public class LinePlacementInTheMiddle extends AbstractLinePlacement {

	public boolean isSuitableFor(IPlacementConstraints placementConstraints,
			int shapeType) {
		if ((shapeType % FShape.Z) == FShape.LINE) {
			return placementConstraints != null
					&& placementConstraints.isInTheMiddleOfLine()
			/* && !placementConstraints.isFollowingLine() */;
		}
		return false;
	}

	@Override
	LabelLocationMetrics initialLocation(LabelClass lc,
			IPlacementConstraints pc, PathLength pathLen, Cancellable cancel) {
		if (cancel.isCanceled())
			return null;

		float length = pathLen.lengthOfPath();
		float distance = (float) (length * 0.5);

		double theta = 0;
		if (pc.isParallel()) {
			// get the line theta and apply it
			theta = pathLen.angleAtLength(distance);

		} else if (pc.isPerpendicular()) {
			// get the line theta with 90 degrees
			theta = pathLen.angleAtLength(distance)
					+ AbstractLinePlacement.HALF_PI;
		}

		Point2D p = pathLen.pointAtLength(distance);

		/*
		 * Offset the point to a distance of the height of the label class's
		 * height to make the label appear to be on the line.
		 */
		double x = p.getX();
		double y = p.getY();
		double halfHeight = lc.getBounds().getHeight() * .5;
		double halfWidth = lc.getBounds().getWidth() * .5;

		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);

		double xOffset = halfHeight * sinTheta;
		double yOffset = halfHeight * cosTheta;

		/*
		 * now, offset the anchor point as much as need to make the center of
		 * the label be the middle of the line
		 */
		xOffset -= halfWidth * cosTheta;
		yOffset += halfWidth * sinTheta;

		p.setLocation(x + xOffset, y - yOffset);

		return new LabelLocationMetrics(p, theta, true);
	}

}
