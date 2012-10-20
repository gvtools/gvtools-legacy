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
package com.iver.cit.gvsig.fmap.edition;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.XTypes;

public class DbSchemaManager implements IDbSchemaManager {

	private Connection conn = null;

	public DbSchemaManager(Connection conn) {
		this.conn = conn;
	}

	public Connection getConnection() {
		return conn;
	}

	public void createSchema(ITableDefinition dbLayerDef)
			throws SchemaEditionException {
		String sqlCreate = "CREATE TABLE " + dbLayerDef.getName() + " ( ";
		int j = 0;
		FieldDescription[] fieldsDescr = dbLayerDef.getFieldsDesc();
		for (int i = 0; i < fieldsDescr.length; i++) {
			int fieldType = fieldsDescr[i].getFieldType();
			String strType = XTypes.fieldTypeToString(fieldType);

			if (j == 0)
				sqlCreate = sqlCreate + fieldsDescr[i].getFieldName() + " "
						+ strType;
			else
				sqlCreate = sqlCreate + ", " + fieldsDescr[i].getFieldName()
						+ " " + strType;
			j++;
		}
		sqlCreate = sqlCreate + ");";

		try {
			Statement st = conn.createStatement();
			st.execute(sqlCreate);
		} catch (SQLException e) {
			throw new SchemaEditionException(dbLayerDef.getName(), e);
		}

	}

	public void removeSchema(String name) throws SchemaEditionException {
		String sqlDrop = "DROP TABLE " + name;
		try {
			Statement st = conn.createStatement();
			st.execute(sqlDrop);
		} catch (SQLException e) {
			throw new SchemaEditionException(name, e);
		}

	}

	public void renameSchema(String antName, String newName)
			throws SchemaEditionException {
		String sqlAlter = "ALTER TABLE " + antName + " RENAME TO " + newName;
		try {
			Statement st = conn.createStatement();
			st.execute(sqlAlter);
		} catch (SQLException e) {
			throw new SchemaEditionException(antName, e);
		}

	}

}
