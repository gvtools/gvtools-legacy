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
 * $Id: DotDensityLegend.java 14420 2007-10-04 11:23:36Z jvidal $
 * $Log$
 * Revision 1.6  2007-09-19 16:25:39  jaume
 * ReadExpansionFileException removed from this context and removed unnecessary imports
 *
 * Revision 1.5  2007/07/25 07:13:34  jaume
 * code style
 *
 * Revision 1.4  2007/05/17 09:32:06  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/09 11:20:56  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.2.2.5  2007/02/21 07:34:09  jaume
 * labeling starts working
 *
 * Revision 1.2.2.4  2007/02/15 16:23:44  jaume
 * *** empty log message ***
 *
 * Revision 1.2.2.3  2007/02/12 15:15:20  jaume
 * refactored interval legend and added graduated symbol legend
 *
 * Revision 1.2.2.2  2007/02/09 07:47:04  jaume
 * Isymbol moved
 *
 * Revision 1.2.2.1  2007/01/26 13:48:17  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/16 11:50:50  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2007/01/10 16:39:41  jaume
 * ISymbol now belongs to com.iver.cit.gvsig.fmap.core.symbols package
 *
 * Revision 1.2  2006/11/17 12:49:58  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 12:30:26  jaume
 * *** empty log message ***
 *
 *
 */
package org.gvsig.symbology.fmap.rendering;

import java.awt.Color;

import org.gvsig.symbology.fmap.symbols.DotDensityFillSymbol;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerFillSymbol;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;

/**
 * 
 * Implements a legend where the magnitudes of a specific area of the map are
 * represented by the density of the points that are distributed in the surface.
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class DotDensityLegend extends VectorialUniqueValueLegend {

	private double dotValue;
	private Color dotColor;
	private Color backgroundColor;
	private static final int SIMPLE_FILL_LAYER_INDEX = 0;
	private static final int DOT_DENSITY_LAYER_INDEX = 1;

	@Override
	public ISymbol getSymbolByValue(Value key) {
		MultiLayerFillSymbol sym = (MultiLayerFillSymbol) getDefaultSymbol();
		DotDensityFillSymbol densitySym = (DotDensityFillSymbol) sym
				.getLayer(DOT_DENSITY_LAYER_INDEX);
		densitySym
				.setDotCount((int) (((NumericValue) key).doubleValue() / dotValue));
		return sym;
	}

	@Override
	public ISymbol getSymbolByFeature(IFeature feat) {
		// return getSymbolByValue(feat.getAttribute(
		// FLyrVect.forTestOnlyVariableUseIterators_REMOVE_THIS_FIELD
		// ? 0 :fieldId));

		return getSymbolByValue(feat.getAttribute(0));
	}

	/**
	 * Establishes the value for the dot used in the dot density legend
	 * 
	 * @param dotValue
	 */
	public void setDotValue(double dotValue) {
		this.dotValue = dotValue;
	}

	/**
	 * <p>
	 * Returns an XML entity with all needed information about the class to
	 * de-serialize and create instances of it.
	 * </p>
	 * 
	 * @return the XML entity of the class that persists
	 */
	@Override
	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClass().getName());
		xml.putProperty("dotValue", dotValue);
		xml.putProperty("fieldName", getClassifyingFieldNames());
		xml.putProperty("dotColor", StringUtilities.color2String(getDotColor()));
		xml.putProperty("bgColor", StringUtilities.color2String(getBGColor()));
		xml.addChild(getDefaultSymbol().getXMLEntity());
		return xml;
	}

	/**
	 * <p>
	 * Sets an XML entity with all needed information about the class to
	 * de-serialize and create instances of it.
	 * </p>
	 * 
	 * @param xml
	 *            the XML entity of the class that persists
	 */
	@Override
	public void setXMLEntity(XMLEntity xml) {
		dotValue = xml.getDoubleProperty("dotValue");
		setClassifyingFieldNames(xml.getStringArrayProperty("fieldName"));
		setDefaultSymbol(SymbologyFactory.createSymbolFromXML(xml.getChild(0),
				null));
		if (xml.contains("dotColor"))
			setDotColor(StringUtilities.string2Color(xml
					.getStringProperty("dotColor")));
		if (xml.contains("bgColor"))
			setBGColor(StringUtilities.string2Color(xml
					.getStringProperty("bgColor")));
	}

	/**
	 * Returns the outline
	 * 
	 * @return
	 */
	public ILineSymbol getOutline() {
		// defined by the SimpleFillSymbol layer
		ISymbol symbol = getDefaultSymbol();
		if (!(symbol instanceof IFillSymbol)) {
			return null;
		}
		IFillSymbol fillsym = (IFillSymbol) symbol;
		if (fillsym instanceof MultiLayerFillSymbol) {
			fillsym = (IFillSymbol) ((MultiLayerFillSymbol) fillsym)
					.getLayer(SIMPLE_FILL_LAYER_INDEX);
		}
		if (fillsym == null) {
			return null;
		}

		return fillsym.getOutline();
	}

	/**
	 * Returns the color for the dot used in the dot density legend.
	 * 
	 * @return
	 */
	public Color getDotColor() {
		// try {
		// // defined by the DotDensitySymbol layer
		// DotDensityFillSymbol sym = (DotDensityFillSymbol)
		// ((MultiLayerFillSymbol) getDefaultSymbol()).
		// getLayer(DOT_DENSITY_LAYER_INDEX);
		// return sym.getDotColor();
		// } catch (NullPointerException npE) {
		// return null;
		// }
		return dotColor;
	}

	/**
	 * Sets the color for the dot used in the dot density legend.
	 * 
	 * @return
	 */
	public void setDotColor(Color color) {

		// DotDensityFillSymbol sym = (DotDensityFillSymbol)
		// ((MultiLayerFillSymbol) getDefaultSymbol()).
		// getLayer(DOT_DENSITY_LAYER_INDEX);
		// sym.setDotColor(color);

		this.dotColor = color;

	}

	/**
	 * Obtains the background color for the dot density legend
	 * 
	 * @return
	 */
	public Color getBGColor() {
		// try {
		// // defined by the SimpleFillSymbol layer
		// IFillSymbol symbol = (IFillSymbol) ((MultiLayerFillSymbol)
		// getDefaultSymbol()).
		// getLayer(SIMPLE_FILL_LAYER_INDEX);
		// return symbol.getFillColor();
		// } catch (NullPointerException npE) {
		// return null;
		// }
		return backgroundColor;
	}

	/**
	 * Sets the background color for the dot density legend
	 * 
	 * @return
	 */
	public void setBGColor(Color color) {
		this.backgroundColor = color;
	}

	/**
	 * Returns the value for the dot that is used in the dot density legend
	 * 
	 * @return
	 */
	public double getDotValue() {
		try {
			XMLEntity xml = getXMLEntity();
			return xml.getDoubleProperty("dotValue");
		} catch (NullPointerException npE) {
			return 0;
		}
	}

	/**
	 * Obtains the size of the dot that is used in the dot density legend
	 * 
	 * @return
	 */
	public double getDotSize() {

		// defined by the SimpleFillSymbol layer
		ISymbol symbol = getDefaultSymbol();
		if (symbol == null) {
			return -1;
		}
		if (symbol instanceof DotDensityFillSymbol) {
			return ((DotDensityFillSymbol) symbol).getDotSize();
		}
		if (!(symbol instanceof IFillSymbol)) {
			return -1;
		}
		IFillSymbol fillsym = (IFillSymbol) symbol;
		if (fillsym instanceof MultiLayerFillSymbol) {
			fillsym = (DotDensityFillSymbol) ((MultiLayerFillSymbol) fillsym)
					.getLayer(DOT_DENSITY_LAYER_INDEX);
		}
		if (fillsym instanceof DotDensityFillSymbol) {
			return ((DotDensityFillSymbol) fillsym).getDotSize();
		}
		return -1;

	}

	/**
	 * Sets the size of the dot that is used in the dot density legend
	 */
	public void setDotSize(double value) {

		DotDensityFillSymbol sym = (DotDensityFillSymbol) ((MultiLayerFillSymbol) getDefaultSymbol())
				.getLayer(DOT_DENSITY_LAYER_INDEX);
		sym.setDotSize(value);

	}

}
