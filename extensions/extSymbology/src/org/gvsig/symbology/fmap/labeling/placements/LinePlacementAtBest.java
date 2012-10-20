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

import org.apache.log4j.Logger;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * 
 * LinePlacementAtBest.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Dec 17, 2007
 * 
 */
public class LinePlacementAtBest extends CompoundLabelPlacement {
	static private ILabelPlacement[] pl = new ILabelPlacement[] {
			new LinePlacementInTheMiddle(), new LinePlacementAtExtremities() };

	public LinePlacementAtBest() {
		super(pl);
		// a patch just to keep in mind the end of the lines
	}

	public ArrayList<LabelLocationMetrics> guess(LabelClass lc, IGeometry geom,
			IPlacementConstraints constraints, double cartographicSymbolSize,
			Cancellable cancel, ViewPort vp) {
		if (cancel.isCanceled())
			return CannotPlaceLabel.NO_PLACES;

		ArrayList<LabelLocationMetrics> llc = super.guess(lc, geom,
				constraints, cartographicSymbolSize, cancel, vp);
		if (constraints instanceof AbstractPlacementConstraints) {
			AbstractPlacementConstraints clone;
			try {
				clone = (AbstractPlacementConstraints) ((AbstractPlacementConstraints) constraints)
						.clone();
				clone.setLocationAlongTheLine(IPlacementConstraints.AT_THE_END_OF_THE_LINE);
				llc.addAll(pl[1].guess(lc, geom, clone, cartographicSymbolSize,
						cancel, vp));
			} catch (CloneNotSupportedException e) {
				// this should never happen but
				// anyway a warning does not hurt anyone
				Logger.getLogger(getClass()).info(
						"Couldn't clone " + constraints.getClassName(), e);
			}

		}
		return llc;
	}

	public boolean isSuitableFor(IPlacementConstraints placementConstraints,
			int shapeType) {
		if ((shapeType % FShape.Z) == FShape.LINE) {
			return placementConstraints != null
					&& placementConstraints.isAtBestOfLine();
		}
		return false;
	}

}