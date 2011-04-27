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
package org.gvsig.remoteClient.sld.styles;

import java.io.IOException;
import java.util.ArrayList;

import org.gvsig.remoteClient.gml.schemas.XMLSchemaParser;
import org.gvsig.remoteClient.sld.SLDFeatureTypeStyle;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
/**
 * UserStyle allows map styling to be defined externally from a system and 
 * to be passed around in an interoperable format.
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDUserStyle extends AbstractSLDStyle {
	
	protected String title;
	protected String uStyleAbstract;
	protected boolean isDefault = false;
	private ArrayList<SLDFeatureTypeStyle> featureTypeStyle = new ArrayList<SLDFeatureTypeStyle>();


	public abstract void parse(XMLSchemaParser parser)throws IOException, XmlPullParserException, LegendDriverException;

	public abstract String toXML();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUStyleAbstract() {
		return uStyleAbstract;
	}

	public void setUStyleAbstract(String styleAbstract) {
		uStyleAbstract = styleAbstract;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public ArrayList<SLDFeatureTypeStyle> getFeatureTypeStyle() {
		return featureTypeStyle;
	}

	public void setFeatureTypeStyle(ArrayList<SLDFeatureTypeStyle> featureTypeStyle) {
		this.featureTypeStyle = featureTypeStyle;
	}
	
	public void addFeatureTypeStyle(SLDFeatureTypeStyle typeStyle) {
		this.featureTypeStyle.add(typeStyle);
		
	}
}
