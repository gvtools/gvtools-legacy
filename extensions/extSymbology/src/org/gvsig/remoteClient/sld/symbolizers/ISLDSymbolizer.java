/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *   Av. Blasco Ib��ez, 50
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
package org.gvsig.remoteClient.sld.symbolizers;

import java.io.IOException;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements an interface for SLD symbolizers in order to define methods for
 * them.
 * <p>
 * Embedded inside of Rules (<code>SLDRule</code>), which group conditions for
 * styling features, are Symbolizers. A Symbolizer describes how a feature is to
 * appears on a map. The symbolizer describes not just the shape that should
 * appear but also such graphical properties as color and opacity. A symbol is
 * obtained by specifying one of a small number of different types of symbolizer
 * and then supplying parameters to override its default behaviour.
 * <p>
 * For the moment three types of symbolizers are defined for gvSIG:
 * <p>
 * -Line -> (<code>SLDLineSymbolizer</code>).
 * <p>
 * -Point -> (<code>SLDPointSymbolizer</code>).
 * <p>
 * -Polygon -> (<code>SLDPolygonSymbolizer</code>).
 * <p>
 * 
 * @see SLDLineSymbolizer
 * @see SLDPointSymbolizer
 * @see SLDPolygonSymbolizer
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public interface ISLDSymbolizer {

	public void parse(XMLSchemaParser parser) throws IOException,
			XmlPullParserException, LegendDriverException;

	/**
	 * Returns the shape type of the SLD symbolizer
	 * 
	 * @return the shape type of the symbolizer
	 */
	int getShapeType();

	/**
	 * Creates an Specification in SLD for a symbolizer when it is going to be
	 * exported.
	 * 
	 * @return the specification in SLD for the symbolizer.
	 */
	abstract String toXML();

	/**
	 * Sets the minScaleDenominator element for a symbolizer. This element
	 * defines the low range of map-rendering scales for which the rule where
	 * the symbolizer appears should be applied.
	 * 
	 * @param maxScaleDenominator
	 */
	void setMinScaleDenominator(double minScaleDenominator);

	/**
	 * Sets the maxScaleDenominator element for a symbolizer. This element
	 * defines the upper range of map-rendering scales for which the rule where
	 * the symbolizer appears should be applied.
	 * 
	 * @param maxScaleDenominator
	 */
	void setMaxScaleDenominator(double maxScaleDenominator);

	public double getMinScaleDenominator();

	public double getMaxScaleDenominator();

	public String getGeometry();

	public void setGeometry(String geometry);

}
