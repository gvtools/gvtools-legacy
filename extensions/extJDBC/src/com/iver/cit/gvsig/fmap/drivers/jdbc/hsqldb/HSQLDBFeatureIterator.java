/*
 * Created on 11-mar-2005
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
package com.iver.cit.gvsig.fmap.drivers.jdbc.hsqldb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WKBParser2;

/**
 * @author FJP
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class HSQLDBFeatureIterator implements IFeatureIterator {
	private WKBParser2 parser = new WKBParser2();

	ResultSet rs;

	String strAux;

	IGeometry geom;

	int numColumns;

	private ResultSetMetaData metaData = null;

	Value[] regAtt;

	/**
	 * @throws SQLException
	 * 
	 */
	public HSQLDBFeatureIterator(ResultSet rs) {
		// Debe ser forward only
		this.rs = rs;
		try {
			numColumns = rs.getMetaData().getColumnCount();
			regAtt = new Value[numColumns - 1];
			metaData = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#hasNext()
	 */
	public boolean hasNext() throws ReadDriverException {
		try {
			if (rs.isLast()) {
				rs.close();
				return false;
			}
			return true;
		} catch (SQLException e) {
			// SqlDriveExceptionType type = new SqlDriveExceptionType();
			// type.setDriverName("HSQLDB Driver");
			// try {
			// type.setSql(rs.getStatement().toString());
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			throw new ReadDriverException("HSQLDB Driver", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#next()
	 */
	public IFeature next() throws ReadDriverException {
		try {
			rs.next();
			byte[] data = rs.getBytes(1);
			geom = parser.parse(data);
			for (int fieldId = 2; fieldId <= numColumns; fieldId++) {
				if (metaData.getColumnType(fieldId) == Types.VARCHAR) {
					String strAux = rs.getString(fieldId);
					if (strAux == null)
						strAux = "";
					regAtt[fieldId - 2] = ValueFactory.createValue(strAux);
				}
				if (metaData.getColumnType(fieldId) == Types.FLOAT)
					regAtt[fieldId - 2] = ValueFactory.createValue(rs
							.getFloat(fieldId));
				if (metaData.getColumnType(fieldId) == Types.DOUBLE)
					regAtt[fieldId - 2] = ValueFactory.createValue(rs
							.getDouble(fieldId));
				if (metaData.getColumnType(fieldId) == Types.INTEGER)
					regAtt[fieldId - 2] = ValueFactory.createValue(rs
							.getInt(fieldId));
				if (metaData.getColumnType(fieldId) == Types.BIGINT)
					regAtt[fieldId - 2] = ValueFactory.createValue(rs
							.getLong(fieldId));
				if (metaData.getColumnType(fieldId) == Types.BIT)
					regAtt[fieldId - 2] = ValueFactory.createValue(rs
							.getBoolean(fieldId));
				if (metaData.getColumnType(fieldId) == Types.DATE)
					regAtt[fieldId - 2] = ValueFactory.createValue(rs
							.getDate(fieldId));

			}

			// TODO: Aquí habría que usar una Factoría.

			IFeature feat = new DefaultFeature(geom, regAtt);

			return feat;
		} catch (SQLException e) {
			// SqlDriveExceptionType type = new SqlDriveExceptionType();
			// type.setDriverName("HSQLDB Driver");
			// try {
			// type.setSql(rs.getStatement().toString());
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			throw new ReadDriverException("HSQLDB Driver", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.IFeatureIterator#closeIterator()
	 */
	public void closeIterator() throws ReadDriverException {
		try {
			rs.close();
		} catch (SQLException e) {
			// SqlDriveExceptionType type = new SqlDriveExceptionType();
			// type.setDriverName("HSQLDB Driver");
			// try {
			// type.setSql(rs.getStatement().toString());
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			throw new ReadDriverException("HSQLDB Driver", e);
			// throw new DriverException(e);
		}
	}

}
