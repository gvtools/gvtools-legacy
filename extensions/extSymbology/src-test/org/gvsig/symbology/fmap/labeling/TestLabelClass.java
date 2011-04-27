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
package org.gvsig.symbology.fmap.labeling;

import java.io.ByteArrayInputStream;

import org.gvsig.symbology.fmap.styles.SimpleLabelStyle;

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.xml.XMLEncodingUtils;
import com.iver.utiles.xmlEntity.generate.XmlTag;

public class TestLabelClass extends com.iver.cit.gvsig.fmap.core.rendering.styling.labeling.TestLabelClass{
	private static final String xmlSVGStyleString = 
	"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + 
	"<xml-tag xmlns=\"http://www.gvsig.gva.es\">\n" + 
	"    <property key=\"className\" value=\"org.gvsig.symbology.fmap.styles.SimpleLabelStyle\"/>\n" + 
	"    <property key=\"desc\" value=\"Placa Carrer VLC\"/>\n" + 
	"    <property key=\"text\" value=\"C FINLANDIA \"/>\n" + 
	"    <property key=\"markerPointX\" value=\"0.0\"/>\n" + 
	"    <property key=\"markerPointY\" value=\"0.0\"/>\n" + 
	"    <property key=\"minXArray\" value=\"0.35 ,0.25\"/>\n" + 
	"    <property key=\"minYArray\" value=\"0.15 ,0.5\"/>\n" + 
	"    <property key=\"widthArray\" value=\"0.5 ,0.6\"/>\n" + 
	"    <property key=\"heightArray\" value=\"0.27 ,0.37\"/>\n" + 
	"    <xml-tag>\n" + 
	"        <property key=\"className\" value=\"org.gvsig.symbology.fmap.styles.ImageStyle\"/>\n" + 
	"        <property key=\"source\" value=\"src-test/test-data/styles/vlc_street.png\"/>\n" + 
	"        <property key=\"desc\"/>\n" + 
	"        <property key=\"id\" value=\"LabelStyle\"/>\n" + 
	"    </xml-tag>\n" + 
	"</xml-tag>\n" + 
	"";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		if (com.iver.cit.gvsig.fmap.core.
				rendering.styling.labeling.TestLabelClass.
				testLabelStyles !=null)
			com.iver.cit.gvsig.fmap.core.
			rendering.styling.labeling.
			TestLabelClass.testLabelStyles.clear();

		try {
			String xmlString =  xmlSVGStyleString;
			
			XMLEntity xml = new XMLEntity((XmlTag) XmlTag.unmarshal(
					XMLEncodingUtils.getReader(new ByteArrayInputStream(xmlString.getBytes()))));
			SimpleLabelStyle sty = (SimpleLabelStyle) SymbologyFactory.createStyleFromXML(xml, null); 

			addLabelStyleToTest(sty);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
//
//		try {
//			String xmlString =  
//				"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" + 
//				"<xml-tag xmlns=\"http://www.gvsig.gva.es\">\n" + 
//				"    <property key=\"className\" value=\"org.gvsig.symbology.fmap.styles.SimpleLabelStyle\"/>\n" + 
//				"    <property key=\"desc\" value=\"Two-fielded Text Globe\"/>\n" + 
//				"    <property key=\"text\" value=\"blah\"/>\n" + 
//				"    <property key=\"markerPointX\" value=\"0.10451306413301663\"/>\n" + 
//				"    <property key=\"markerPointY\" value=\"0.040372670807453416\"/>\n" + 
//				"    <property key=\"minXArray\" value=\"0.05463182897862233 ,0.05938242280285035\"/>\n" + 
//				"    <property key=\"minYArray\" value=\"0.2981366459627329 ,0.4192546583850932\"/>\n" + 
//				"    <property key=\"widthArray\" value=\"0.8954869358669834 ,0.8931116389548693\"/>\n" + 
//				"    <property key=\"heightArray\" value=\"0.055900621118012424 ,0.10869565217391304\"/>\n" + 
//				"    <xml-tag>\n" + 
//				"        <property key=\"id\" value=\"LabelStyle\"/>\n" + 
//				"        <property key=\"className\" value=\"org.gvsig.symbology.fmap.styles.SVGStyle\"/>\n" + 
//				"        <property key=\"source\" value=\"/home/jaume/gvSIG/Styles/Text Globe.svg\"/>\n" + 
//				"        <property key=\"desc\"/>\n" + 
//				"    </xml-tag>\n" + 
//				"</xml-tag>\n" + 
//				"";
//			XMLEntity xml = new XMLEntity((XmlTag) XmlTag.unmarshal(
//					XMLEncodingUtils.getReader(new ByteArrayInputStream(xmlString.getBytes()))));
//			SimpleLabelStyle sty = (SimpleLabelStyle) SymbologyFactory.createStyleFromXML(xml, null); 
//
//			addLabelStyleToTest(sty);
//
//		} catch (Exception e) {
//			fail(e.getLocalizedMessage());
//		}

	}
}
