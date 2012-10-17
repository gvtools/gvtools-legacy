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
package org.gvsig.symbology;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.gvsig.symbology.fmap.symbols.LineFillSymbol;
import org.gvsig.symbology.fmap.symbols.PictureMarkerSymbol;

import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.AbstractSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiShapeSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;

public class Utils {
//	public static ISymbol deriveFSymbol(FSymbol fSymbol) {
//		ISymbol derivedSymbol;
//
//		int symbolType = fSymbol.getSymbolType();
//		Color color = fSymbol.getColor();
//		double size = fSymbol.getSize();
//		int unit = fSymbol.isSizeInPixels() ? -1 : 1; // only meters or pixels
//														// were supported in
//														// FSymbol
//		if (symbolType == FShape.LINE) {
//			ILineSymbol line = SymbologyFactory.createDefaultLineSymbol();
//			line.setLineColor(color);
//
//			SimpleLineStyle lineStyle = new SimpleLineStyle();
//			lineStyle.setUnit(unit);
//			lineStyle.setStroke(fSymbol.getStroke());
//			lineStyle.setOffset(0);
//			lineStyle.setReferenceSystem(CartographicSupport.WORLD);
//			lineStyle.setLineWidth((float) size);
//			line.setLineStyle(lineStyle);
//			derivedSymbol = line;
//
//		} else if (symbolType == FShape.POINT) {
//			int style = fSymbol.getStyle();
//			IMarkerSymbol marker;
//			if (style == FConstant.SYMBOL_STYLE_MARKER_IMAGEN) {
//				marker = new PictureMarkerSymbol();
//				PictureMarkerSymbol pic = (PictureMarkerSymbol) marker;
//				try {
//					pic.setImage(new File(fSymbol.getIconURI().getPath()).toURL());
//				} catch (IOException e) {
//					// image could not be restored,
//					// will use a regular point as symbol
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_MARKER_CIRCLE);
//					return deriveFSymbol(fSymbol);
//				}
//			} else {
//				marker = new SimpleMarkerSymbol();
//				SimpleMarkerSymbol sms = (SimpleMarkerSymbol) marker;
//				if (style == FConstant.SYMBOL_STYLE_MARKER_CIRCLE) {
//					sms.setStyle(SimpleMarkerSymbol.CIRCLE_STYLE);
//				} else if (style == FConstant.SYMBOL_STYLE_MARKER_CROSS) {
//					sms.setStyle(SimpleMarkerSymbol.CROSS_STYLE);
//				} else if (style == FConstant.SYMBOL_STYLE_MARKER_SQUARE) {
//					sms.setStyle(SimpleMarkerSymbol.SQUARE_STYLE);
//				} else if (style == FConstant.SYMBOL_STYLE_MARKER_TRIANGLE) {
//					sms.setStyle(SimpleMarkerSymbol.TRIANGLE_STYLE);
//				}
//				Color outlineColor = fSymbol.getOutlineColor();
//				if (outlineColor != null) {
//					sms.setOutlined(true);
//					sms.setOutlineColor(outlineColor);
//				}
//			}
//			marker.setColor(color);
//			marker.setSize(size);
//			marker.setRotation(fSymbol.getRotation());
//			derivedSymbol = marker;
//
//		} else if (symbolType == FShape.POLYGON) {
//			IFillSymbol fill;
//			int fSymbolStyle = fSymbol.getStyle();
//			color = null;
//			if (fSymbolStyle == FConstant.SYMBOL_STYLE_FILL_SOLID) {
//				fill = SymbologyFactory.createDefaultFillSymbol();
//				color = fSymbol.getColor();
//			} else if (fSymbolStyle == FConstant.SYMBOL_STYLE_FILL_TRANSPARENT) {
//				fill = SymbologyFactory.createDefaultFillSymbol();
//			} else {
//				// lets see how to derive FSymbol with fill patterns
//				if (fSymbolStyle == FConstant.SYMBOL_STYLE_FILL_CROSS) {
//					// the cross will be substituted by two line fill symbols
//					// with
//					// perpendicular line angles mixed into a multilayer symbol
//					IMultiLayerSymbol mfs = SymbologyFactory.createEmptyMultiLayerSymbol(FShape.POLYGON);
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_VERTICAL);
//					LineFillSymbol firstLayer = (LineFillSymbol) deriveFSymbol(fSymbol);
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_HORIZONTAL);
//					LineFillSymbol secondLayer = (LineFillSymbol) deriveFSymbol(fSymbol);
//					mfs.addLayer(firstLayer);
//					mfs.addLayer(secondLayer);
//					fill = (IFillSymbol) mfs;
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_CROSS); // restore
//																			// old
//																			// style
//																			// (just
//																			// in
//																			// case)
//				} else if (fSymbolStyle == FConstant.SYMBOL_STYLE_FILL_CROSS_DIAGONAL ) {
//					// the cross will be substituted by two line fill symbols
//					// with
//					// perpendicular line angles mixed into a multilayer symbol
//					IMultiLayerSymbol mfs = SymbologyFactory.createEmptyMultiLayerSymbol(FShape.POLYGON);
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_UPWARD_DIAGONAL);
//					LineFillSymbol firstLayer = (LineFillSymbol) deriveFSymbol(fSymbol);
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_DOWNWARD_DIAGONAL);
//					LineFillSymbol secondLayer = (LineFillSymbol) deriveFSymbol(fSymbol);
//					mfs.addLayer(firstLayer);
//					mfs.addLayer(secondLayer);
//					fill = (IFillSymbol) mfs;
//					fSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_CROSS_DIAGONAL); // restore
//																			// old
//																			// style
//																			// (just
//																			// in
//																			// case)
//				} else {
//					LineFillSymbol lfs = new LineFillSymbol();
//					// Let's create the filling line symbol
//					fSymbol.setSymbolType(FShape.LINE);
//					ILineSymbol lineSymbol = (ILineSymbol) deriveFSymbol(fSymbol);
//					SimpleLineStyle lineStyle = new SimpleLineStyle();
//					lineStyle.setLineWidth(1f);
//					lineSymbol.setLineStyle(lineStyle);
//
//					// restore the old value for symbol type (should be always
//					// FShape.POLYGON)
//					assert symbolType == FShape.POLYGON;
//					fSymbol.setSymbolType(symbolType);
//					double angle = 0;
//					switch (fSymbolStyle) {
//					case FConstant.SYMBOL_STYLE_FILL_UPWARD_DIAGONAL:
//						angle = 45;
//						break;
//					case FConstant.SYMBOL_STYLE_FILL_VERTICAL:
//						angle = 90;
//						break;
//					case FConstant.SYMBOL_STYLE_FILL_HORIZONTAL:
//						angle = 0;
//						break;
//					case FConstant.SYMBOL_STYLE_FILL_DOWNWARD_DIAGONAL:
//						angle = -45;
//						break;
//					}
//					lfs.setSeparation(10);
//					lfs.setAngle(angle*FConstant.DEGREE_TO_RADIANS);
//					lfs.setLineSymbol(lineSymbol);
//					fill = lfs;
//				}
//			}
//
//			fill.setFillColor(color);
//
//			if (fSymbol.isOutlined()) {
//				// Let's create the outline line symbol
//				fSymbol.setSymbolType(FShape.LINE);
//				ILineSymbol outline = (ILineSymbol) deriveFSymbol(fSymbol);
//
//				// restore the old value for symbol type (should be always
//				// FShape.POLYGON)
//				assert symbolType == FShape.POLYGON;
//				fSymbol.setSymbolType(symbolType);
//				outline.setLineColor(fSymbol.getOutlineColor());
//				fill.setOutline(outline);
//			}
//			derivedSymbol = fill;
//		} else if (symbolType == FShape.MULTI) {
//			fSymbol.setSymbolType(FShape.LINE);
//			ILineSymbol line = (ILineSymbol) deriveFSymbol(fSymbol);
//			fSymbol.setSymbolType(FShape.POINT);
//			IMarkerSymbol marker = (IMarkerSymbol) deriveFSymbol(fSymbol);
//			fSymbol.setSymbolType(FShape.POLYGON);
//			IFillSymbol fill = (IFillSymbol) deriveFSymbol(fSymbol);
//			assert symbolType == FShape.MULTI;
//			fSymbol.setSymbolType(symbolType);
//			MultiShapeSymbol multiShapeSymbol = new MultiShapeSymbol();
//			multiShapeSymbol.setMarkerSymbol(marker);
//			multiShapeSymbol.setLineSymbol(line);
//			multiShapeSymbol.setFillSymbol(fill);
//
//			derivedSymbol = multiShapeSymbol;
//		} else if (symbolType == FShape.TEXT) {
//			ITextSymbol textSym = SymbologyFactory.createDefaultTextSymbol();
//			textSym.setTextColor(color);
//			derivedSymbol = textSym;
//		} else {
//			throw new Error("FSymbol of type "+symbolType+" cannot be imported yet!");
//		}
//
//		// establish the general description;
//		if (derivedSymbol instanceof AbstractSymbol) {
//			AbstractSymbol symbol = (AbstractSymbol) derivedSymbol;
//			symbol.setIsShapeVisible(true);
//			symbol.setDescription(fSymbol.getDescription());
//		}
//
//
//
//		if (derivedSymbol instanceof CartographicSupport) {
//			CartographicSupport cs = (CartographicSupport) derivedSymbol;
//			// the only options allowed by the old FSymbol class
//			cs.setUnit(unit);
//			cs.setReferenceSystem(CartographicSupport.WORLD);
//		}
//
//		return derivedSymbol;
//	}
}
