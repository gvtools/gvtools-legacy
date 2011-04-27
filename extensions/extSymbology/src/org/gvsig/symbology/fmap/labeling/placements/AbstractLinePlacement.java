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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.apache.batik.ext.awt.geom.PathLength;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;
import com.iver.utiles.swing.threads.Cancellable;

/**
 *
 * AbstractLinePlacement.java
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Dec 17, 2007
 *
 */
public abstract class AbstractLinePlacement implements ILabelPlacement {
	public static final double PI = Math.PI;
	public static final double HALF_PI = PI * 0.5;
	private ArrayList<LabelLocationMetrics> guessed = new ArrayList<LabelLocationMetrics>();
	private static final double TOLERANCE = 1E-2;


	public ArrayList<LabelLocationMetrics> guess(LabelClass lc, IGeometry geom,
			IPlacementConstraints pc, double cartographicSymbolSize, Cancellable cancel,ViewPort vp) {
		guessed.clear();

		FShape shp = FConverter.transformToInts(geom, vp.getAffineTransform());

		PathLength pathLen = new PathLength(shp);

		LabelLocationMetrics initial = initialLocation(lc, pc, pathLen, cancel);

		if (cancel.isCanceled()) return CannotPlaceLabel.NO_PLACES;

		double theta = initial.getRotation();


		double xOffset = 0;
		double yOffset = 0;
		Rectangle2D bounds = lc.getBounds();

		if (pc.isPageOriented()) {
			if(theta >  HALF_PI) { // from origin to left-down
				theta = (theta - Math.PI);

				Point2D anchor = initial.getAnchor();
//				double cosTheta = Math.cos(theta);
				double sinTheta = Math.sin(theta);
				double width = bounds.getWidth();
				double height = bounds.getHeight()*1.1;
				initial.getAnchor().setLocation(
						anchor.getX()+ (sinTheta*width /* + cosTheta*height*/),
						anchor.getY()- (/*cosTheta*width*/ + sinTheta*height));
			} else if (theta < -HALF_PI ) { // from origin to left-up
				if (Math.abs(Math.abs(theta) - HALF_PI) -TOLERANCE > 0)
					// the condition avoids errors when segment is almost up-down vertical
				{
					theta = (theta + Math.PI);
					Point2D anchor = initial.getAnchor();
					double cosTheta = Math.cos(theta);
					double sinTheta = Math.sin(theta);
					double width = bounds.getWidth();
					double height = bounds.getHeight()*1.1;
					initial.getAnchor().setLocation(
							anchor.getX()- (cosTheta*width/* + sinTheta*height*/),
							anchor.getY()- (sinTheta*width + cosTheta*height));
				}
			}


		}

		if (pc.isBelowTheLine() ||
				pc.isOnTheLine() ||
				pc.isAboveTheLine()) {

			double h = bounds.getHeight()*0.5;

			xOffset += h * Math.sin(theta);
			yOffset += h * Math.cos(theta);


			Point2D initialAnchor = initial.getAnchor();
			// calculate the possibles in the inverse
			// order to the preferred order and add it
			// always in the first position
			if (pc.isBelowTheLine()) {
				Point2D anchor = new Point2D.Double(
						initialAnchor.getX() +	-xOffset,
						initialAnchor.getY() +	+yOffset);
				guessed.add(0, new LabelLocationMetrics(anchor, theta, false));
			}

			if (pc.isOnTheLine()) {
				guessed.add(0, new LabelLocationMetrics(initial.getAnchor(), theta, false));
			}

			if (pc.isAboveTheLine()) {
				Point2D anchor = new Point2D.Double(
						initialAnchor.getX() +	+xOffset,
						initialAnchor.getY() +  -yOffset);
				guessed.add(0, new LabelLocationMetrics(anchor, theta, false));
			}
		} else {
			// will say that on the line is legal
			guessed.add(0, initial);
		}
		return guessed;
	}

	abstract LabelLocationMetrics initialLocation(LabelClass lc, IPlacementConstraints pc, PathLength pathLen, Cancellable cancel);

}
