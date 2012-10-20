/*
 * Created on 05-sep-2006
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
 * $Id: LegendDriverExceptionType.java 13879 2007-09-19 16:18:59Z jaume $
 * $Log$
 * Revision 1.3  2007-09-19 16:18:59  jaume
 * nothing important
 *
 * Revision 1.2  2007/05/28 15:36:42  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/09/21 17:18:31  azabala
 * First version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap;

import com.iver.utiles.ExceptionDescription;

public class LegendDriverExceptionType extends ExceptionDescription {

	String driverName;
	String legendLabelField;
	String legendHeightField;
	String legendRotationField;

	public LegendDriverExceptionType(String errorDescription) {
		super();
		setCode(1);
		setDescription("error al crear la leyenda de una capa "
				+ errorDescription != null ? errorDescription : "");
	}

	public String getHtmlErrorMessage() {
		String message = "";
		message += "<b>Error al construir la leyenda:</b><br>"
				+ "Se ha producido un error al tratar de generar la leyenda con el driver "
				+ driverName + "<br>";

		message += "Los campos de la capa empleados para construir la leyenda son:<br>";
		message += "<ol>" + "<li> Texto: " + legendLabelField + "</li>"
				+ "<li> Angulo: " + legendRotationField + "</li>"
				+ "<li> Altura de texto: " + legendHeightField + "</li>";
		return message;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

}