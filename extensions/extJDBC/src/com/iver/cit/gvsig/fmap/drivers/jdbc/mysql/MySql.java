/*
 * Created on 15-ene-2007
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
 * $Id: MySql.java 17343 2007-12-12 08:30:48Z vcaballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:49:54  caballero
 * Exceptions
 *
 * Revision 1.1  2007/01/15 20:15:35  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.fmap.drivers.jdbc.mysql;

import java.sql.Types;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DefaultJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.XTypes;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * It builds sql sintax for Create, Delete, Update operations in MySQL
 * 
 * */
public class MySql {
	/**
	 * Converts a JTS geometry to WKT
	 * */
	private static WKTWriter geometryWriter = new WKTWriter();

	/**
	 * Mover esto a IverUtiles
	 * 
	 * @param val
	 * @return
	 */
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
	 * It builds MySQL "CREATE TABLE" statement.
	 * 
	 * In PostGIS, its usual first create alphanumeric table, and after that add
	 * a geometry column for geometries.
	 * 
	 * 
	 * @param dbLayerDef
	 *            data of the new table (name, etc)
	 * @param fieldsDescr
	 *            schema of the new table
	 * @param bCreateGID
	 *            @DEPRECATED
	 * @return SQL statement with MySQL sintax
	 */
	public String getSqlCreateSpatialTable(DBLayerDefinition dbLayerDef,
			FieldDescription[] fieldsDescr, boolean bCreateGID) {

		String result;
		result = "CREATE TABLE " + dbLayerDef.getTableName()
				+ " (gid serial PRIMARY KEY ";
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

		String geometryFieldName = dbLayerDef.getFieldGeometry();

		// TODO Ver si MySQL se traga los tipos geometricos que devuelve XTypes
		String geometryFieldType = "GEOMETRY";
		switch (dbLayerDef.getShapeType()) {
		case FShape.POINT:
			geometryFieldType = XTypes.fieldTypeToString(XTypes.POINT2D);
			break;
		case FShape.LINE:
			geometryFieldType = XTypes.fieldTypeToString(XTypes.LINE2D);
			break;
		case FShape.POLYGON:
			geometryFieldType = XTypes.fieldTypeToString(XTypes.POLYGON2D);
			break;
		case FShape.MULTI:
			geometryFieldType = XTypes.fieldTypeToString(XTypes.MULTI2D);
			break;
		}
		result += "," + geometryFieldName + " " + geometryFieldType
				+ " NOT NULL, SPATIAL INDEX(" + geometryFieldName + "))";
		return result;
	}

	protected String format(Object value) {
		String retString = null;
		if (value != null) {
			if (value instanceof NullValue)
				retString = "null";
			else {
				// retString = "'" + doubleQuote(value) + "'";
				retString += ("'" + value.toString().trim() + "',");
			}
		} else {
			retString = "null";
		}
		return retString;
	}

	// private String doubleQuote(Object obj) {
	// String aux = obj.toString().replaceAll("'", "''");
	// StringBuffer strBuf = new StringBuffer(aux);
	// ByteArrayOutputStream out = new ByteArrayOutputStream(strBuf.length());
	// PrintStream printStream = new PrintStream(out);
	// printStream.print(aux);
	// String aux2 = "ERROR";
	// try {
	// aux2 = out.toString(toEncode);
	// System.out.println(aux + " " + aux2);
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// return aux2;
	// }

	/**
	 * Based in code from JUMP (VividSolutions) and Geotools Things to be aware:
	 * We always will use Spatial Tables with Unique ID. IFeature has the same
	 * field order than dbLayerDef.getFieldNames()
	 * 
	 * @param dbLayerDef
	 * @param feat
	 * @return
	 */
	public String getSqlInsertFeature(DBLayerDefinition dbLayerDef,
			IFeature feat) {
		StringBuffer sqlBuf = new StringBuffer("INSERT INTO "
				+ dbLayerDef.getTableName() + " (");
		String sql = null;
		int numAlphanumericFields = dbLayerDef.getFieldNames().length;

		for (int i = 0; i < numAlphanumericFields; i++) {
			String name = dbLayerDef.getFieldsDesc()[i].getFieldName();
			if (name.equals(dbLayerDef.getFieldID()))
				continue;
			sqlBuf.append(" " + name + ",");
		}
		sqlBuf.append(" " + dbLayerDef.getFieldGeometry());
		sqlBuf.append(" ) VALUES (");

		String insertQueryHead = sqlBuf.toString();
		sqlBuf = new StringBuffer(insertQueryHead);

		for (int j = 0; j < numAlphanumericFields; j++) {
			String name = dbLayerDef.getFieldsDesc()[j].getFieldName();
			if (name.equals(dbLayerDef.getFieldID()))
				continue;

			if (isNumeric(feat.getAttribute(j)))
				sqlBuf.append(feat.getAttribute(j) + ", ");
			else {
				sqlBuf.append(format(feat.getAttribute(j)) + ", ");
			}
		}// for
		sqlBuf.append(" GeometryFromText( '"
				+ geometryWriter.write(feat.getGeometry().toJTSGeometry())
				+ "', "
				+ DefaultJDBCDriver.removePrefix(dbLayerDef.getSRID_EPSG())
				+ ")");
		sqlBuf.append(" ) ");
		sql = sqlBuf.toString();
		return sql;
	}

	// public String getSqlCreateIndex(DBLayerDefinition lyrDef) {
	// String indexName = lyrDef.getTableName() + "_"
	// + lyrDef.getFieldGeometry() + "_gist";
	// String sql = "CREATE INDEX \"" + indexName + "\" ON \""
	// + lyrDef.getTableName() + "\" USING GIST (\""
	// + lyrDef.getFieldGeometry() + "\" GIST_GEOMETRY_OPS)";
	//
	// return sql;
	// }

	public String getSqlModifyFeature(DBLayerDefinition dbLayerDef,
			IFeature feat) {
		StringBuffer sqlBuf = new StringBuffer("UPDATE "
				+ dbLayerDef.getTableName() + " SET");
		String sql = null;
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
					sqlBuf.append(" " + name + " = " + strAux + " ,");
				}
			}
		}
		sqlBuf.deleteCharAt(sqlBuf.lastIndexOf(","));
		if (feat.getGeometry() != null) {
			sqlBuf.append(", " + dbLayerDef.getFieldGeometry());
			sqlBuf.append(" = ");
			sqlBuf.append(" GeometryFromText( '"
					+ geometryWriter.write(feat.getGeometry().toJTSGeometry())
					+ "', "
					+ DefaultJDBCDriver.removePrefix(dbLayerDef.getSRID_EPSG())
					+ ")");
		}
		sqlBuf.append(" WHERE ");

		// TODO El feature.getID() funciona? (AZO)
		sqlBuf.append(dbLayerDef.getFieldID() + " = " + feat.getID());
		sql = sqlBuf.toString();
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
		// TODO: NECESITAMOS OTRO MÉTODO PARA BORRAR CORRECTAMENTE.
		// Esto provocará errores, ya que getID que viene en un row no
		// nos sirve dentro de un writer para modificar y/o borrar entidades
		// Por ahora, cojo el ID del campo que me indica el dbLayerDev
		StringBuffer sqlBuf = new StringBuffer("DELETE FROM "
				+ dbLayerDef.getTableName() + " WHERE ");
		String sql = null;
		int indexFieldId = dbLayerDef.getIdFieldID();
		sqlBuf.append(dbLayerDef.getFieldID() + " = "
				+ row.getAttribute(indexFieldId));
		sql = sqlBuf.toString();

		return sql;
	}

}
