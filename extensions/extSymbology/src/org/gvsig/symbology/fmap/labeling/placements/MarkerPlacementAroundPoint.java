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

import java.awt.Rectangle;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

import org.gvsig.symbology.fmap.styles.PointLabelPositioneer;

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

public class MarkerPlacementAroundPoint implements ILabelPlacement {
//	private static final MarkerCenteredAtPoint pos = new MarkerCenteredAtPoint();
	private static final MarkerPlacementOnPoint pos = new MarkerPlacementOnPoint();

	public ArrayList<LabelLocationMetrics> guess(LabelClass lc, IGeometry geom, IPlacementConstraints placementConstraints, double cartographicSymbolSize, Cancellable cancel, ViewPort vp) {
	if (cancel.isCanceled()) return CannotPlaceLabel.NO_PLACES;

	FShape shp = FConverter.transformToInts(geom, vp.getAffineTransform());

		if (placementConstraints instanceof PointPlacementConstraints) {
			PointPlacementConstraints ppc = (PointPlacementConstraints) placementConstraints;
			PointLabelPositioneer plp = ppc.getPositioneer();

			if (plp != null) {
				FPoint2D p = (FPoint2D) shp;
				byte[] preferredPositions = plp.getPreferenceVector();
				ArrayList<LabelLocationMetrics> highPreference = new ArrayList<LabelLocationMetrics>();
				ArrayList<LabelLocationMetrics> normalPreference = new ArrayList<LabelLocationMetrics>();
				ArrayList<LabelLocationMetrics> lowPreference = new ArrayList<LabelLocationMetrics>();
				Rectangle bounds = lc.getBounds();
				double width = bounds.getWidth()*.5; // + 2; //¿por qué el +2?
				double heigth = bounds.getHeight()*.5;

				double offsetX = 0, offsetY = 0;
				for (int i = 0; i < preferredPositions.length; i++) {
					switch (i) {
					case 0:
					case 3:
					case 5:
						// left
						offsetX = -width;
						break;
					case 2:
					case 4:
					case 7:
						// rigth
						offsetX = width;
						break;
					case 1:
					case 6:
					default:
						// horizontally centered
						offsetX = 0;
						break;
					}
					switch (i) {
					case 0:
					case 1:
					case 2:
						// top
						offsetY = -heigth;
						break;
					case 5:
					case 6:
					case 7:
						// below
						offsetY = heigth;
						break;
					case 3:
					case 4:
					default:
						// vertically centered
						offsetY = 0;
						break;

					}
//					// xOffset
//					switch (i % 3) {
//					case 0:
//						// left
//						offsetX = -width;
//						break;
//					case 2:
//						// rigth
//						offsetX = width;
//						break;
//					case 1:
//					default:
//						// horizontally centered
//						offsetX = 0;
//						break;
//
//					}
//
//					switch (i / 3) {
//					case 0:
//						// top
//						offsetY = -heigth;
//						break;
//					case 2:
//						// below
//						offsetY = heigth;
//						break;
//					case 1:
//					default:
//						// vertically centered
//						offsetY = 0;
//						break;
//
//					}
					FPoint2D aux = new FPoint2D(p.getX() + offsetX, p.getY()+offsetY);

					IGeometry g=ShapeFactory.createPoint2D(aux);
					try {
							g.transform(vp.getAffineTransform().createInverse());
						} catch (NoninvertibleTransformException e) {
							e.printStackTrace();
						}

					switch (preferredPositions[i]) {
					case PointLabelPositioneer.FORBIDDEN:
						break;
					case PointLabelPositioneer.PREFERENCE_HIGH:
						highPreference.addAll(
								pos.guess(lc, g, placementConstraints, cartographicSymbolSize, cancel,vp));
						break;
					case PointLabelPositioneer.PREFERENCE_NORMAL:

						normalPreference.addAll(
								pos.guess(lc, g, placementConstraints, cartographicSymbolSize, cancel,vp));
						break;
					case PointLabelPositioneer.PREFERENCE_LOW:
						lowPreference.addAll(
								pos.guess(lc, g, placementConstraints, cartographicSymbolSize, cancel,vp));
						break;

					default:
						throw new Error("unrecognised label position preference value: "+preferredPositions[i]);
					}
				}

				ArrayList<LabelLocationMetrics> guessed = new ArrayList<LabelLocationMetrics>();
				for (int j = 0; j < highPreference.size(); j++)		guessed.add(highPreference.get(j));
				for (int j = 0; j < normalPreference.size(); j++)	guessed.add(normalPreference.get(j));
				for (int j = 0; j < lowPreference.size(); j++)		guessed.add(lowPreference.get(j));
				return guessed;
			}
		}

		return CannotPlaceLabel.NO_PLACES;
	}



	public boolean isSuitableFor(IPlacementConstraints placementConstraints,
			int shapeType) {
		if ((shapeType%FShape.Z) == FShape.POINT || (shapeType%FShape.Z) == FShape.MULTIPOINT) {// TODO (09/01/08) is this correct? if not fix it also in PlacementManager, PlacementProperties
			return placementConstraints.isAroundThePoint();
		}
		return false;
	}



}
