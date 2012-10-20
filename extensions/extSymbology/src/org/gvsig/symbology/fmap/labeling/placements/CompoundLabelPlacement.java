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
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelLocationMetrics;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * 
 * CompoundLabelPlacement.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Dec 17, 2007
 * 
 */
public class CompoundLabelPlacement implements ILabelPlacement {
	private ILabelPlacement[] placements;

	public CompoundLabelPlacement(ILabelPlacement[] suitablePlacements) {
		this.placements = suitablePlacements;
	}

	public boolean isSuitableFor(IPlacementConstraints placementConstraints,
			int shapeType) {
		return true;
	}

	public ArrayList<LabelLocationMetrics> guess(LabelClass lc, IGeometry geom,
			IPlacementConstraints constraints, double cartographicSymbolSize,
			Cancellable cancel, ViewPort vp) {
		ArrayList<LabelLocationMetrics> guessed = new ArrayList<LabelLocationMetrics>();
		for (int i = 0; cancel != null && !cancel.isCanceled()
				&& i < placements.length; i++) {
			guessed.addAll(placements[i].guess(lc, geom, constraints,
					cartographicSymbolSize, cancel, vp));
		}
		return guessed;
	}
}
