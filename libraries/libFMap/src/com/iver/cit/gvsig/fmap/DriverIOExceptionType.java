/*
 * Created on 05-sep-2006
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: DriverIOExceptionType.java 7454 2006-09-21 17:18:31Z azabala $
 * $Log$
 * Revision 1.1  2006-09-21 17:18:31  azabala
 * First version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap;

import java.io.File;

import com.iver.utiles.ExceptionDescription;

public class DriverIOExceptionType extends ExceptionDescription {

	private File file;
	private String name;

	public DriverIOExceptionType() {
		super();
		setCode(2);
		setDescription("error de IO con un driver basado en fichero");
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getHtmlErrorMessage() {
		String message = "";
		message += "<b>Error al acceder al fichero del driver " + "</b><br>"
				+ "Error de acceso al fichero " + file.getAbsolutePath()
				+ " del driver " + name;
		return message;
	}

	public void setDriverName(String name) {
		this.name = name;
	}
}