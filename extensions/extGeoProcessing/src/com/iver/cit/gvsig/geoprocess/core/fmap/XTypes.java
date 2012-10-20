/*
 * Created on 07-feb-2006
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
 * $Id: XTypes.java 12943 2007-08-07 15:06:12Z azabala $
 * $Log$
 * Revision 1.2  2007-08-07 15:06:12  azabala
 * added getNumericFields method
 *
 * Revision 1.1  2006/05/24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.4  2006/02/19 20:56:07  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/17 16:34:00  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/13 18:37:41  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/09 15:59:48  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.FloatValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.LongValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

/**
 * This is a similar try to com.iver.cit.gvsig.jdbc_spatial.XTypes to do a Types
 * management in GvSig
 * 
 * @author azabala
 * 
 */
public abstract class XTypes {
	// Geometry types
	public final static int SHAPE_NULL = 0;
	public final static int POINT = 1;
	public final static int LINE = 2;
	public final static int POLYGON = 4;
	public final static int TEXT = 8;
	public final static int MULTI = 16;
	public final static int MULTIPOINT = 32;
	public final static int CIRCLE = 64;
	public final static int ARC = 128;
	public final static int ELLIPSE = 256;
	public final static int Z = 512;

	public static final int BIT = Types.BIT;
	public static final int TINYINT = Types.TINYINT;
	public static final int SMALLINT = Types.SMALLINT;
	public static final int INTEGER = Types.INTEGER;
	public static final int BIGINT = Types.BIGINT;
	public static final int FLOAT = Types.FLOAT;
	public static final int REAL = Types.REAL;
	public static final int DOUBLE = Types.DOUBLE;
	public static final int NUMERIC = Types.NUMERIC;
	public static final int DECIMAL = Types.DECIMAL;
	public static final int CHAR = Types.CHAR;
	public static final int VARCHAR = Types.VARCHAR;
	public static final int LONGVARCHAR = Types.LONGVARCHAR;
	public static final int DATE = Types.DATE;
	public static final int TIME = Types.TIME;
	public static final int TIMESTAMP = Types.TIMESTAMP;
	public static final int BINARY = Types.BINARY;
	public static final int VARBINARY = Types.VARBINARY;
	public static final int LONGVARBINARY = Types.LONGVARBINARY;
	public static final int NULL = Types.NULL;
	public static final int OTHER = Types.OTHER;
	public static final int JAVA_OBJECT = Types.JAVA_OBJECT;
	public static final int DISTINCT = Types.DISTINCT;
	public static final int STRUCT = Types.STRUCT;
	public static final int ARRAY = Types.ARRAY;
	public static final int BLOB = Types.BLOB;
	public static final int CLOB = Types.CLOB;
	public static final int REF = Types.REF;
	public static final int DATALINK = Types.DATALINK;
	public static final int BOOLEAN = Types.BOOLEAN;

	public static boolean isNumeric(int fieldType) {
		switch (fieldType) {
		case DOUBLE:
		case FLOAT:
		case INTEGER:
		case SMALLINT:
		case BIGINT:
		case NUMERIC:
		case REAL:
		case TINYINT:
		case DECIMAL:
			return true;
		}
		return false;
	}

	/**
	 * Convert an int data type in a text sql type. Is abstract because it will
	 * be strongly dependant of sql rdbms.
	 * 
	 * @param fieldType
	 * @return
	 */
	public abstract String fieldTypeToString(int fieldType);

	public static Object getObject(Value value, int fieldType) {
		Object solution = null;
		switch (fieldType) {
		case Types.VARCHAR:
			StringValue str = (StringValue) value;
			solution = str.getValue();
			break;
		case Types.FLOAT:
			FloatValue fval = (FloatValue) value;
			solution = new Float(fval.floatValue());
			break;
		case Types.DOUBLE:
			DoubleValue dval = (DoubleValue) value;
			solution = new Double(dval.doubleValue());
			break;
		case Types.INTEGER:
			IntValue intval = (IntValue) value;
			solution = new Integer(intval.intValue());
			break;
		case Types.BIGINT:
			LongValue lval = (LongValue) value;
			solution = new Long(lval.longValue());
			break;
		case Types.BIT:
			BooleanValue bval = (BooleanValue) value;
			solution = new Boolean(bval.getValue());
			break;
		case Types.DATE:
			DateValue dtval = (DateValue) value;
			solution = dtval.getValue();
			break;
		}
		return solution;
	}

	/**
	 * Returns a Value from its data type (fieldType) and an Object with its
	 * value
	 * 
	 * TODO Move to ValueFactory
	 * 
	 * @param plainObject
	 * @param fieldType
	 * @return
	 */
	public static Value getValue(Object object, int fieldType) {
		if (object == null)
			return ValueFactory.createNullValue();
		Value solution = null;
		switch (fieldType) {
		case Types.VARCHAR:
			String str = (String) object;
			solution = ValueFactory.createValue(str);
			break;
		case Types.FLOAT:
			float fval = ((Float) object).floatValue();
			solution = ValueFactory.createValue(fval);
			break;
		case Types.DOUBLE:
			double dval = ((Double) object).doubleValue();
			solution = ValueFactory.createValue(dval);
			break;
		case Types.INTEGER:
			int ival = ((Integer) object).intValue();
			solution = ValueFactory.createValue(ival);
			break;
		case Types.BIGINT:
			long lval = ((Long) object).longValue();
			solution = ValueFactory.createValue(lval);
			break;
		case Types.BIT:
			boolean bval = ((Boolean) object).booleanValue();
			solution = ValueFactory.createValue(bval);
			break;
		case Types.DATE:
			Date dtval = (Date) object;
			solution = ValueFactory.createValue(dtval);
			break;
		default:
			solution = ValueFactory.createNullValue();
		}
		return solution;
	}

	public static String[] getNumericFieldsNames(FLyrVect layer) {
		String[] solution = null;
		if (layer == null)
			return null;
		ArrayList list = new ArrayList();
		try {
			SelectableDataSource recordset = layer.getRecordset();
			int numFields = recordset.getFieldCount();
			for (int i = 0; i < numFields; i++) {
				if (XTypes.isNumeric(recordset.getFieldType(i))) {
					list.add(recordset.getFieldName(i));
				}
			}// for
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}
}
