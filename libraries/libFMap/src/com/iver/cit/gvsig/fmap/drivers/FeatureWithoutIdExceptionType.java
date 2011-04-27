/*
 * Created on 06-sep-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
* $Id: FeatureWithoutIdExceptionType.java 8484 2006-11-02 12:11:26Z fjp $
* $Log$
* Revision 1.1  2006-11-02 12:11:26  fjp
* bug 1271
*
* Revision 1.1  2006/09/21 18:05:47  azabala
* First version in cvs
*
*
*/
package com.iver.cit.gvsig.fmap.drivers;

import com.iver.utiles.ExceptionDescription;

public class FeatureWithoutIdExceptionType extends ExceptionDescription {

   DBLayerDefinition schema;
	
	
	public FeatureWithoutIdExceptionType(){
		super(10, "Feature sin ID en Driver JDBC");
	}
	
	public void setSchema(DBLayerDefinition schema){
		this.schema = schema;
	}
	public String getHtmlErrorMessage() {
		String message = "";
		message += "<b>Se ha encontrado un feature sin ID en una capa de bbdd" + 
					"</b><br>" +
		"Los datos de la capa son:<br><ul>" ;
		
		message += "<li>Tabla: "+schema.getTableName() +"</li>";
		message += "<li>Catalogo: "+schema.getCatalogName()+"</li>";
		message += "</ul>";
		
		return message;
	}

}

