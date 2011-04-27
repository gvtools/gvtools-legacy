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
package com.iver.cit.gvsig.fmap.edition.fieldmanagers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.fmap.drivers.XTypes;

public class JdbcFieldManager extends AbstractFieldManager {
	private static Logger logger = Logger.getLogger(JdbcFieldManager.class.getName());
	Connection conn;

	/**
	 * Name of the table with schema if it has (schema.tablename)
	 */
	String tableName;


	public JdbcFieldManager(Connection conn, String schema_tablename) {
		this.conn = conn;
		this.tableName = schema_tablename;
	}

	public boolean alterTable() throws WriteDriverException{
		String sql = "";
		Statement st;
		try {
			st = conn.createStatement();

			for (int i = 0; i < fieldCommands.size(); i++) {
				FieldCommand fc = (FieldCommand) fieldCommands.get(i);
				if (fc instanceof AddFieldCommand) {
					AddFieldCommand addFC = (AddFieldCommand) fc;

					sql = "ALTER TABLE "
						+ tableName
						+ " ADD COLUMN "
						+ "\""+addFC.getFieldDesc().getFieldName()+"\""
						+ " "
						+ XTypes.fieldTypeToString(addFC.getFieldDesc()
								.getFieldType())
								+ " "
								+ "DEFAULT " + addFC.getFieldDesc().getDefaultValue().getStringValue(ValueWriter.internalValueWriter)
								+ ";";
					st.execute(sql);
				}
				if (fc instanceof RemoveFieldCommand) {
					RemoveFieldCommand deleteFC = (RemoveFieldCommand) fc;
					sql = "ALTER TABLE " + tableName + " DROP COLUMN "
					+ "\""+deleteFC.getFieldName()+"\"" + ";";
					st.execute(sql);
				}
				if (fc instanceof RenameFieldCommand) {
					RenameFieldCommand renFC = (RenameFieldCommand) fc;
					sql = "ALTER TABLE " + tableName + " RENAME COLUMN "
					+ "\""+renFC.getAntName()+"\"" + " TO " + "\""+renFC.getNewName()+"\"" + ";";
					st.execute(sql);
				}
				logger.debug("Alter Table: " + sql);
			}
			conn.commit();
		} catch (SQLException e) {
			logger.error("Alter Table: " + sql);
			e.printStackTrace();
			try {
				conn.rollback();
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new WriteDriverException("JDBC",e);
		}

		return false;
	}

}
