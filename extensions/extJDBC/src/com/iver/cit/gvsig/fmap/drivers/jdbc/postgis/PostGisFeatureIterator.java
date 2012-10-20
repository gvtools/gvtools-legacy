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
package com.iver.cit.gvsig.fmap.drivers.jdbc.postgis;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WKBParser3;

/**
 * @author FJP
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PostGisFeatureIterator implements IFeatureIterator {
	private static int FETCH_SIZE = 5000;

	private WKBParser3 parser = new WKBParser3();

	private ResultSetMetaData metaData = null;

	ResultSet rs;

	Statement st;

	String strAux;

	IGeometry geom;

	int numColumns;

	Value[] regAtt;

	/**
	 * Array con la correspondencia entre un campo de la consulta y el campo
	 * dentro de regAtt
	 */
	int[] relIds;

	private DBLayerDefinition lyrDef;

	int numReg = 0;

	int idFieldID = -1;

	String cursorName;

	/**
	 * @throws SQLException
	 * @throws SQLException
	 * 
	 */
	public PostGisFeatureIterator(Connection conn, String cursorName, String sql)
			throws SQLException {

		// Basado en 0, es decir, no cuenta el campo
		// de geometria
		// Debe ser forward only
		st = conn.createStatement();

		// CodeSprint 2010 (Manuel López Sánchez)
		try {
			st.execute("BEGIN");
		} catch (SQLException e) {
			st.execute("END"); // Cerramos la transacción para anular los
								// cursores binarios
								// que pueden quedar colgados (from CodeSprint
								// 2010)
			st.execute("BEGIN"); // Si salta otra excepción, no la capturamos
		}
		// End CodeSprint 2010
		st.execute("declare " + cursorName + " binary cursor for " + sql);

		this.rs = st.executeQuery("fetch forward " + FETCH_SIZE + " in "
				+ cursorName);

		this.cursorName = cursorName;
		numColumns = rs.getMetaData().getColumnCount();
		metaData = rs.getMetaData();
		numReg = 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#hasNext()
	 */
	public boolean hasNext() throws ReadDriverException {
		try {
			if (numReg > 0)
				if ((numReg % FETCH_SIZE) == 0) {
					rs = st.executeQuery("fetch forward " + FETCH_SIZE + " in "
							+ cursorName);
					// System.out.println("ejecutando la query otra vez");
				}
			// System.out.println("hasNext con numReg=" + numReg);
			if (rs.next())
				return true;
			else {
				closeIterator();
				return false;
			}
		} catch (SQLException e) {
			// SqlDriveExceptionType type = new SqlDriveExceptionType();
			// type.setDriverName("PostGIS Driver");
			// try {
			// type.setSql(rs.getStatement().toString());
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			throw new ReadDriverException("PostGIS Driver", e);
			// throw new DriverException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#next()
	 */
	public IFeature next() throws ReadDriverException {
		byte[] data;
		try {
			data = rs.getBytes(1);
			geom = parser.parse(data);
			for (int fieldId = 2; fieldId <= numColumns; fieldId++) {
				Value val = PostGisDriver.getFieldValue(rs, fieldId);
				regAtt[relIds[fieldId - 2]] = val;
			}

			// TODO: Aquí habría que usar una Factoría.
			IFeature feat = null;
			if (idFieldID != -1) {
				String theID = regAtt[lyrDef.getIdFieldID()].toString();
				feat = new DefaultFeature(geom, regAtt.clone(), theID);
			} else {
				// // feat = new DefaultFeature(geom, regAtt);
				// FeatureWithoutIdExceptionType type = new
				// FeatureWithoutIdExceptionType();
				// type.setSchema(lyrDef);
				throw new ReadDriverException("PostGIS Driver", null);
			}
			numReg++;
			return feat;
		} catch (SQLException e) {
			// SqlDriveExceptionType type = new SqlDriveExceptionType();
			// type.setDriverName("PostGIS Driver");
			// try {
			// type.setSql(rs.getStatement().toString());
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			throw new ReadDriverException("PostGIS Driver", e);
			// throw new DriverException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.IFeatureIterator#closeIterator()
	 */
	public void closeIterator() throws ReadDriverException {
		try {
			numReg = 0;

			// st.execute("CLOSE " + cursorName);
			// st.execute("COMMIT");
			rs.close();
		} catch (SQLException e) {
			// SqlDriveExceptionType type = new SqlDriveExceptionType();
			// type.setDriverName("PostGIS Driver");
			// try {
			// type.setSql(rs.getStatement().toString());
			// } catch (SQLException e1) {
			// e1.printStackTrace();
			// }
			throw new ReadDriverException("PostGIS Driver", e);
			// throw new DriverException(e);
		}
		// st.execute("COMMIT");
		// st.close();

	}

	public void setLyrDef(DBLayerDefinition lyrDef) {
		this.lyrDef = lyrDef;
		// Aunque no nos hayan pedido todos los campos, devolveremos
		// tantos atributos como la capa tiene. Eso sí, puestos a null
		regAtt = new Value[lyrDef.getFieldNames().length];
		relIds = new int[numColumns - 1];

		try {
			for (int i = 2; i <= metaData.getColumnCount(); i++) {
				int idRel = lyrDef.getFieldIdByName(metaData.getColumnName(i));
				if (idRel == -1) {
					throw new RuntimeException(
							"No se ha encontrado el nombre de campo "
									+ metaData.getColumnName(i));
				}
				relIds[i - 2] = idRel;
				if (lyrDef.getFieldID().equals(metaData.getColumnName(i))) {
					idFieldID = i;
					// break;
				}
			}
		} catch (SQLException e) {
			// Si no está, no pasa nada
			e.printStackTrace();
		}

	}

}
