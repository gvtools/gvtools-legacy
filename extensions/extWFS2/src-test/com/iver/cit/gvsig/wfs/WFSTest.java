package com.iver.cit.gvsig.wfs;

import java.io.File;

import junit.framework.TestCase;

import org.gvsig.remoteClient.wfs.WFSClient;
import org.gvsig.remoteClient.wfs.WFSStatus;

import com.iver.cit.gvsig.fmap.layers.WFSLayerNode;
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id: WFSTest.java 18381 2008-01-30 12:00:25Z jpiera $
 * $Log$
 * Revision 1.1.2.3  2006-11-17 11:28:45  ppiqueras
 * Corregidos bugs y aÃ±adida nueva funcionalidad.
 *
 * Revision 1.4  2006/10/10 12:55:06  jorpiell
 * Se ha añadido el soporte de features complejas
 *
 * Revision 1.3  2006/10/02 09:09:45  jorpiell
 * Cambios del 10 copiados al head
 *
 * Revision 1.1.2.1  2006/09/19 12:27:08  jorpiell
 * Ya no se depende de geotools
 *
 * Revision 1.2  2006/09/18 12:07:31  jorpiell
 * Se ha sustituido geotools por el driver de remoteservices
 *
 * Revision 1.1  2006/06/14 07:56:36  jorpiell
 * Test de parseo de getfeature
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class WFSTest extends TestCase {
	private String sUrl;
	private String feature;
	private String fields;
	private String fieldTypes;
	
	protected void setUp() throws Exception {
//		sUrl = "http://sercartlin:8080/geoserver/wfs?REQUEST=getCapabilities&SERVICE=WFS";
//		feature = "topp:states";
//		fields="tipo,municipio,the_geom";	
//		sUrl = "http://simon.coput.gva.es:2000/cgi-bin/mapserv483?map=/etc/mapserver/interno/wfs.map&REQUEST=getCapabilities&SERVICE=WFS";
//		feature = "cit:hidro_lin_300k";
//		fields="color,codigo";
		sUrl = "http://www2.dmsolutions.ca/cgi-bin/mswfs_gmap";
		feature = "grid";
		fields = "msGeometry,F_CODE";
		fieldTypes = "gml:GeometryPropertyType,string";
		

	}	
	
	
	public void testWFS() throws Exception{
		WFSClient remoteServicesClient = new WFSClient(sUrl);
		File file = remoteServicesClient.getFeature(getStatus(), false, null);
		System.out.println("Fin descarga");
//		WFSFeatureLayer fl = new WFSFeatureLayer(file,
//				getNode(),
//				getStatus());
//		IFeatureIterator iterator = fl.getIterator();		
//		System.out.println("Fin parseo");
//		int i=0;
//		while (iterator.hasNext()){
//			IFeature feat = iterator.next();
////			IGeometry geom = feat.getGeometry();
//			i++;
//		}
//		System.out.println("Encontradas " + i + " Features");
	}
	
	private WFSStatus getStatus(){
		WFSStatus status = new WFSStatus(feature);
		status.setFields(fields.split(","));
		status.setUserName("");
		status.setPassword("");
		status.setTimeout(100000);
		status.setBuffer(1000);
		return status;
	}
	
	private WFSLayerNode[] getNode(){
		WFSLayerNode[] layers = new WFSLayerNode[1];
		layers[0] = new WFSLayerNode();
		layers[0].setName(feature);
		String[] vfields = fields.split(",");
		String[] vfieldTypes = fieldTypes.split(",");
//		for (int i=0 ; i<vfields.length ; i++){
//			IXMLType attribute = new IXMLType();
//			attribute.setName(vfields[i]);
//			attribute.setType(vfieldTypes[i]);
//			layers[0].getFields().add(attribute);
//		}
		return layers;
	}
}
