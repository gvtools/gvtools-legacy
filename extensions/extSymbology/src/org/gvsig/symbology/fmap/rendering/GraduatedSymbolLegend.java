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
 * $Id: GraduatedSymbolLegend.java 16187 2007-11-08 16:13:44Z jdominguez $
 * $Log$
 * Revision 1.3  2007-05-17 09:32:06  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/09 11:20:56  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.1.2.4  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.3  2007/02/14 15:53:35  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.2  2007/02/13 16:19:19  jaume
 * graduated symbol legends (start commiting)
 *
 * Revision 1.1.2.1  2007/02/12 15:15:20  jaume
 * refactored interval legend and added graduated symbol legend
 *
 *
 */
package org.gvsig.symbology.fmap.rendering;

import org.gvsig.symbology.fmap.styles.SimpleMarkerFillPropertiesStyle;
import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerFillSymbol;
import com.iver.cit.gvsig.fmap.rendering.IInterval;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;


/**
 * <p>GraduatedSymbolLegend is a legend that allows to classify ranges of
 * values using a value with symbol sizes.<b>
 * </p>
 * <p>
 * The symbol size will be calculated according the min size, the
 * max size and the amount of intervals. So for a min size of 1, a max size
 * of 5, and 5 intervals, symbol sizes will be [1, 2, 3, 4, 5].
 * </p>
 * @author  jaume dominguez faus - jaume.dominguez@iver.es
 */
public class GraduatedSymbolLegend extends VectorialIntervalLegend {
	private double maxSymbolSize;
	private double minSymbolSize;
	private ISymbol backgroundSymbol;
	private int templateShapeType;
	/**
	 * DefaultConstructor
	 */
	public GraduatedSymbolLegend() {
		super();
	}

	/**
	 * Convenience constructor.
	 *
	 * Creates a new instance of GraduatedSymbolLegend from a VectorialIntervalLegend by
	 * taking the same fields.
	 * @param leg
	 */
	public GraduatedSymbolLegend(VectorialIntervalLegend leg) {
		this();
		VectorialIntervalLegend.initializeVectorialIntervalLegend(leg, this);

	}

	/**
	 * Returns the max symbol size. Combined with the min symbol size and
	 * the interval count, will produce the symbol sizes.
	 * @return double, the max size.
	 */
	public double getMaxSymbolSize() {
		return maxSymbolSize;
	}

	/**
	 * Returns the min symbol size. Combined with the max symbol size and
	 * the interval count, will produce the symbol sizes.
	 * @return double, the min size.
	 */
	public double getMinSymbolSize() {
		return minSymbolSize;
	}

	/**
	 * Sets the max symbol size. Combined with the min symbol size and
	 * the interval count, will produce the symbol sizes.
	 */
	public void setMaxSymbolSize(double size) {
		this.maxSymbolSize = size;
	}

	/**
	 * Sets the min symbol size. Combined with the max symbol size and
	 * the interval count, will produce the symbol sizes.
	 */
	public void setMinSymbolSize(double size) {
		this.minSymbolSize = size;
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = super.getXMLEntity();

		xml.putProperty("className", getClass().getName());
		xml.putProperty("templateShapeType", getTemplateShapeType());
		xml.putProperty("maxSymbolSize", maxSymbolSize);
		xml.putProperty("minSymbolSize", minSymbolSize);

		if (getDefaultSymbol() == null) {
	        xml.putProperty("useDefaultSymbol", 0);
	    } else {
	        xml.putProperty("useDefaultSymbol", 1);
	        xml.addChild(getDefaultSymbol().getXMLEntity());
	    }

		if (backgroundSymbol != null) {
			XMLEntity backgroundXML = backgroundSymbol.getXMLEntity();
			backgroundXML.putProperty("idB", "backgroundSymbol");
			xml.addChild(backgroundXML);
		}

		// remove unuseful properties from supertype
		xml.remove("startColor");
		xml.remove("endColor");
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {

		xml.putProperty("startColor",
				StringUtilities.color2String(super.getStartColor()));
		xml.putProperty("endColor",
				StringUtilities.color2String(super.getEndColor()));

		super.setXMLEntity(xml);
		templateShapeType = xml.getIntProperty("templateShapeType");
		maxSymbolSize = xml.getDoubleProperty("maxSymbolSize");
		minSymbolSize = xml.getDoubleProperty("minSymbolSize");

//		if (xml.firstChild("id", "defaultSymbol") != null) {
//			XMLEntity defaultXML = xml.firstChild("id", "defaultSymbol");
//			setDefaultSymbol(SymbologyFactory.createSymbolFromXML(defaultXML, "defaultSymbol"));
//		}

		int useDefaultSymbol = xml.getIntProperty("useDefaultSymbol");

		if (useDefaultSymbol == 1) {
			setDefaultSymbol( SymbologyFactory.createSymbolFromXML(xml.getChild(0), null));
		} else {
			setDefaultSymbol(null);
		}

		if (xml.firstChild("idB", "backgroundSymbol") != null) {
			XMLEntity backgroundXML = xml.firstChild("idB", "backgroundSymbol");
			backgroundSymbol = SymbologyFactory.createSymbolFromXML(backgroundXML, "backgroundSymbol");
		}
	}

	public void setShapeType(int shapeType) {
		if (this.shapeType != shapeType) {
			this.shapeType = shapeType;
		}
	}

	@Override
	public ISymbol getSymbolByFeature(IFeature feat) {
		ISymbol theSymbol = super.getSymbolByFeature(feat);

		if ((shapeType % FShape.Z) == FShape.POLYGON && theSymbol instanceof IMarkerSymbol) {
			// transform it to a fill symbol
			MarkerFillSymbol aux = new MarkerFillSymbol();
			// tell the fill style to draw the IMarkerSymbol
			// as a IFillSymbol centering it in the shape polygon
			// centroid and applying offset (if any).
			aux.setMarker((IMarkerSymbol) theSymbol);
			SimpleMarkerFillPropertiesStyle p = new SimpleMarkerFillPropertiesStyle();
			p.setFillStyle(SimpleMarkerFillPropertiesStyle.SINGLE_CENTERED_SYMBOL);
			aux.setMarkerFillProperties(p);
			theSymbol = aux;
		}



		if (backgroundSymbol != null) {
			MultiLayerFillSymbol multi = new MultiLayerFillSymbol() ;
			multi.addLayer(backgroundSymbol);
			multi.addLayer(theSymbol);
			return multi;
		}

		return theSymbol;


	}


	@Override
	public ISymbol getSymbolByInterval(IInterval key) {
		ISymbol theSymbol = super.getSymbolByInterval(key);

		if ((shapeType % FShape.Z) == FShape.POLYGON && theSymbol instanceof IMarkerSymbol) {
			// transform it to a fill symbol
			MarkerFillSymbol aux = new MarkerFillSymbol();
			// tell the fill style to draw the IMarkerSymbol
			// as a IFillSymbol centering it in the shape polygon
			// centroid and applying offset (if any).
			aux.setMarker((IMarkerSymbol) theSymbol);
			SimpleMarkerFillPropertiesStyle p = new SimpleMarkerFillPropertiesStyle();
			p.setFillStyle(SimpleMarkerFillPropertiesStyle.SINGLE_CENTERED_SYMBOL);
			aux.setMarkerFillProperties(p);
			theSymbol = aux;
		}

		return theSymbol;
	}

	/**
	 * Obtains the background symbol
	 */
	public ISymbol getBackgroundSymbol() {
		return backgroundSymbol;
	}

	/**
	 * Sets the background symbol.
	 * @param symbol
	 */
	public void setBackgroundSymbol(ISymbol symbol) {
		this.backgroundSymbol = symbol;
	}

	public ISymbol getDefaultSymbol() {

		return defaultSymbol;

	}

	@Override
	public boolean isSuitableForShapeType(int shapeType) {
		return (getShapeType() % FShape.Z) == (shapeType % FShape.Z) || ((getTemplateShapeType()%FShape.Z) == FShape.POINT && (shapeType % FShape.Z) == FShape.POLYGON);
	}


	/**
	 * Obtains the shapetype of the background symbol
	 */
	public int getTemplateShapeType() {
		return templateShapeType;
	}
	/**
	 * Sets the shapetype of the background symbol
	 */
	public void setTemplateShapeType(int templateShapeType) {
		if((shapeType % FShape.Z) == FShape.POLYGON ) {
			if((templateShapeType % FShape.Z)== FShape.POINT)
				this.templateShapeType = templateShapeType;
		}
		else if((templateShapeType % FShape.Z) == (shapeType % FShape.Z)) {
			this.templateShapeType = templateShapeType;
		}
	}

}
