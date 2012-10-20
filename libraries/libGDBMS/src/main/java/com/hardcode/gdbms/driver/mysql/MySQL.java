/*
 * Created on 16-ene-2007
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
 * $Id: MySQL.java 9799 2007-01-17 19:39:59Z azabala $
 * $Log$
 * Revision 1.2  2007-01-17 19:39:59  azabala
 * bug solved in getInsertInto...(the rest of methods wont work well, because GDBMS is not thinked to work with schemas)
 *
 * Revision 1.1  2007/01/16 20:06:02  azabala
 * changes to allow edition with MySQL drivers
 *
 *
 */
package com.hardcode.gdbms.driver.mysql;

import java.sql.Types;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.XTypes;

public class MySQL {
	public boolean isNumeric(Value val) {

		switch (val.getSQLType()) {
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.BIGINT:
		case Types.NUMERIC:
		case Types.REAL:
		case Types.TINYINT:
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param dbLayerDef
	 * @param fieldsDescr
	 * @param bCreateGID
	 * @return
	 */
	public String getSqlCreateSpatialTable(DBLayerDefinition dbLayerDef,
			FieldDescription[] fieldsDescr, boolean bCreateGID) {
		String result = "CREATE TABLE " + dbLayerDef.getTableName()
				+ " (gid int(10) unsigned NOT NULL auto_increment,";
		int j = 0;
		for (int i = 0; i < dbLayerDef.getFieldNames().length; i++) {
			int fieldType = fieldsDescr[i].getFieldType();
			// TODO ver si XTypes me los devuelve con la sintaxis MySQL
			String strType = XTypes.fieldTypeToString(fieldType);

			// We dont allow GID field. It is a reserved field name
			if (fieldsDescr[i].getFieldName().equalsIgnoreCase("gid"))
				continue;
			result += ", " + dbLayerDef.getFieldNames()[i] + " " + strType;
			j++;
		}
		result = result.substring(0, result.length() - 1);
		result += ", PRIMARY KEY(GID))";
		return result;
	}

	protected String format(Object value) {
		String retString = null;
		if (value != null) {
			if (value instanceof NullValue)
				retString = "null";
			else {
				retString = ("'" + value.toString().trim() + "'");
			}
		} else {
			retString = "null";
		}
		return retString;
	}

	/**
	 * Based in code from JUMP (VividSolutions) and Geotools Things to be aware:
	 * We always will use Spatial Tables with Unique ID. IFeature has the same
	 * field order than dbLayerDef.getFieldNames()
	 * 
	 * @param dbLayerDef
	 * @param feat
	 * @return
	 */
	public String getSqlInsertFeature(DBLayerDefinition dbLayerDef, IRow feat) {
		String sql = "INSERT INTO " + dbLayerDef.getTableName() + " (";
		int numAlphanumericFields = dbLayerDef.getFieldNames().length;
		for (int i = 0; i < numAlphanumericFields; i++) {
			String name = dbLayerDef.getFieldsDesc()[i].getFieldName();
			if (name.equals(dbLayerDef.getFieldID()))
				continue;
			sql += " " + name + ",";
		}// for
		sql = sql.substring(0, sql.length() - 1);
		sql += " ) VALUES (";
		for (int j = 0; j < numAlphanumericFields; j++) {
			String name = dbLayerDef.getFieldsDesc()[j].getFieldName();
			if (name.equals(dbLayerDef.getFieldID()))
				continue;

			if (isNumeric(feat.getAttribute(j)))
				sql += feat.getAttribute(j) + ", ";
			else {
				sql += format(feat.getAttribute(j)) + ", ";
			}
		}// for
		sql = sql.substring(0, sql.length() - 2);
		sql += " )";
		return sql;
	}

	public String getSqlModifyFeature(DBLayerDefinition dbLayerDef, IRow feat) {
		String sql = "UPDATE " + dbLayerDef.getTableName() + " SET";
		int numAlphanumericFields = dbLayerDef.getFieldsDesc().length;
		for (int i = 0; i < numAlphanumericFields; i++) {
			FieldDescription fldDesc = dbLayerDef.getFieldsDesc()[i];
			if (fldDesc != null) {
				String name = fldDesc.getFieldName();
				if (name.equalsIgnoreCase(dbLayerDef.getFieldID()))
					continue;
				Value val = feat.getAttribute(i);
				if (val != null) {
					String strAux = val
							.getStringValue(ValueWriter.internalValueWriter);
					sql += " " + name + " = " + strAux + " ,";
				}// if
			}// if
		}// for
		sql = sql.substring(0, sql.length() - 1);
		sql += " WHERE ";
		// TODO El feature.getID() funciona? (AZO)
		sql += dbLayerDef.getFieldID() + " = " + feat.getID();
		return sql;

	}

	/**
	 * It builds MySQL's delete statement
	 * 
	 * @param dbLayerDef
	 * @param row
	 * @return
	 */
	public String getSqlDeleteFeature(DBLayerDefinition dbLayerDef, IRow row) {
		String sql = "DELETE FROM " + dbLayerDef.getTableName() + " WHERE ";
		int indexFieldId = dbLayerDef.getIdFieldID();
		sql += dbLayerDef.getFieldID() + " = " + row.getAttribute(indexFieldId);
		return sql;
	}

}
