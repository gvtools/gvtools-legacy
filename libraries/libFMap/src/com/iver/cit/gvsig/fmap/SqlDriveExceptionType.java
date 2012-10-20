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
 * $Id: SqlDriveExceptionType.java 7454 2006-09-21 17:18:31Z azabala $
 * $Log$
 * Revision 1.1  2006-09-21 17:18:31  azabala
 * First version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap;

import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.utiles.ExceptionDescription;

public class SqlDriveExceptionType extends ExceptionDescription {

	DBLayerDefinition schema;
	String sql;
	private String layerName;
	private String driverName;

	public SqlDriveExceptionType() {
		super(200, "Error de SQL");
	}

	public DBLayerDefinition getSchema() {
		return schema;
	}

	public void setSchema(DBLayerDefinition schema) {
		this.schema = schema;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getHtmlErrorMessage() {
		String message = "";
		message += "<b>Error de SQL" + "</b><br>"
				+ "Los datos de la capa son:<br><ul>";

		message += "<li>Tabla: " + schema.getTableName() + "</li>";
		message += "<li>Catalogo: " + schema.getCatalogName() + "</li>";
		message += "</ul>";
		message += "<br> La sentencia SQL que causó el error es:" + sql;

		return message;
	}

	public void setLayerName(String tableName) {
		this.layerName = tableName;
	}

	public String getLayerName() {
		return layerName;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String name) {
		this.driverName = name;
	}

}
