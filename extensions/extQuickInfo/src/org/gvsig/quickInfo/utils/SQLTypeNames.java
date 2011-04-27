package org.gvsig.quickInfo.utils;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.sql.Types;

import com.iver.andami.PluginServices;

/**
 * <p>Utility to convert an SQL data type in its representation in characters.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class SQLTypeNames {
	/**
	 * <p>Returns an <code>String</code> that represents a SQL type.</p>
	 * 
	 * @param type a type defined in {@link Types Types}
	 * @return an <code>String</code> that represents the type
	 */
	public static String getSQLTypeName(int type) {
		switch(type) {
			case Types.ARRAY:
				return "Array";
			case Types.BIGINT:
				return "BigInteger";
			case Types.BINARY:
				return "Binary";
			case Types.BIT:
				return "Bit";
			case Types.BLOB:
				return "Blob";
			case Types.BOOLEAN:
				return "Boolean";
			case Types.CHAR:
				return "Char";
			case Types.CLOB:
				return "Clob";
			case Types.DATALINK:
				return "Datalink";
			case Types.DATE:
				return "Date";
			case Types.DECIMAL:
				return "Decimal";
			case Types.DISTINCT:
				return "Distinct";
			case Types.DOUBLE:
				return "Double";
			case Types.FLOAT:
				return "Float";
			case Types.INTEGER:
				return "Integer";
			case Types.JAVA_OBJECT:
				return "Java Object";
			case Types.LONGVARBINARY:
				return "LongVarBinary";
			case Types.LONGVARCHAR:
				return "LongVarChar";
			case Types.NULL:
				return "Null";
			case Types.NUMERIC:
				return "Numeric";
			case Types.OTHER:
				return "Other";
			case Types.REAL:
				return "Real";
			case Types.REF:
				return "Ref";
			case Types.SMALLINT:
				return "SmallInt";
			case Types.STRUCT:
				return "Struct";
			case Types.TIME:
				return "Time";
			case Types.TIMESTAMP:
				return "TimeStamp";
			case Types.TINYINT:
				return "TinyInt";
			case Types.VARBINARY:
				return "VarBinary";
			case Types.VARCHAR:
				return "VarChar";				
			default:
				return PluginServices.getText(null, "Unknown");
		}
	}
}
