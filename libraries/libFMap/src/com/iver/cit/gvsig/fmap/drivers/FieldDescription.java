/*
 * Created on 27-oct-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.fmap.drivers;

import java.sql.Types;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;

public class FieldDescription {

	public String toString() {
		return getFieldAlias();
	}

	public static int stringToType(String strType) {
		int type = -1;
		if (strType.equalsIgnoreCase("String") || strType.equalsIgnoreCase("Varchar"))
			type = Types.VARCHAR;
		if (strType.equalsIgnoreCase("Double"))
			type = Types.DOUBLE;
		if (strType.equalsIgnoreCase("Integer"))
			type = Types.INTEGER;
		if (strType.equalsIgnoreCase("Boolean"))
			type = Types.BOOLEAN;
		if (strType.equalsIgnoreCase("Date"))
			type = Types.DATE;

		if (type == -1) {
			throw new RuntimeException("Type not recognized: " + strType);
		}
		return type;
	}

	public static String typeToString(int sqlType) {
		switch (sqlType) {
		case Types.NUMERIC:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.SMALLINT:
		case Types.TINYINT:
			return "Integer";

		case Types.BIT:
		case Types.BOOLEAN:
			return "Boolean";

		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return "String";

		case Types.DATE:
			return "Date";

		case Types.FLOAT:
		case Types.DOUBLE:
		case Types.DECIMAL:
		case Types.REAL:
			return "Double";

		case Types.BINARY:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			return "Binary";

		case Types.TIMESTAMP:
			return "Timestamp";

		case Types.TIME:
			return "Time";

		case Types.OTHER:
		default:
			throw new RuntimeException("Type not recognized: " + sqlType);
		}

	}

	/**
	 * Internal field name.
	 */
	private String fieldName;

	private String fieldAlias;

	private int fieldType;

	private Value defaultValue = new NullValue();

	/**
	 * En campos numéricos, numero de dígitos a la izquierda del punto En campos
	 * de texto, numero de caracteres.
	 */
	private int fieldLength = 8;

	/**
	 * En campos numéricos, numero de dígitos a la derecha del punto.
	 */
	private int fieldDecimalCount = 0;

	/**
	 * @return Returns the fieldDecimalCount.
	 */
	public int getFieldDecimalCount() {
		return fieldDecimalCount;
	}

	/**
	 * @param fieldDecimalCount
	 *            The fieldDecimalCount to set.
	 */
	public void setFieldDecimalCount(int fieldDecimalCount) {
		this.fieldDecimalCount = fieldDecimalCount;
	}

	/**
	 * @return Returns the fieldLength.
	 */
	public int getFieldLength() {
		return fieldLength;
	}

	/**
	 * @param fieldLength
	 *            The fieldLength to set.
	 */
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	/**
	 * @return Returns the fieldName.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            The fieldName to set.
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		this.fieldAlias = fieldName;
	}

	/**
	 * @return Returns the fieldType.
	 */
	public int getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType
	 *            The fieldType to set.
	 */
	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		this.fieldAlias = fieldAlias;
	}

	public FieldDescription cloneField() {
		FieldDescription resul = new FieldDescription();
		resul.fieldAlias = fieldAlias;
		resul.fieldName = fieldName;
		resul.fieldDecimalCount = fieldDecimalCount;
		resul.fieldType = fieldType;
		return resul;
	}

	public Value getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Value defaultValue) {
		this.defaultValue = defaultValue;
	}

}
