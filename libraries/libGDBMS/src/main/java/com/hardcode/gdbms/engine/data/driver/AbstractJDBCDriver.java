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
package com.hardcode.gdbms.engine.data.driver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.db.JDBCSupport;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.JdbcWriter;
import com.iver.utiles.NumberUtilities;

public abstract class AbstractJDBCDriver implements AlphanumericDBDriver,
		IWriteable {

	protected JDBCSupport jdbcSupport;

	protected JdbcWriter jdbcWriter = new JdbcWriter();

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static DateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private ValueWriter vWriter = ValueWriter.internalValueWriter;

	/**
	 * @throws OpenDriverException
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#executeSQL(java.sql.Connection,
	 *      java.lang.String)
	 */
	public void open(Connection con, String sql) throws SQLException,
			OpenDriverException {
		jdbcSupport = JDBCSupport.newJDBCSupport(con, sql);
		Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet res = st.executeQuery(sql);
		if (res.getConcurrency() != ResultSet.CONCUR_UPDATABLE) {
			System.err.println("Error: No se puede editar la tabla " + sql);
			jdbcWriter = null;
		} else {
			jdbcWriter.initialize(con, res);
			jdbcWriter.setCreateTable(false);
			jdbcWriter.setWriteAll(false);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getFieldCount() throws ReadDriverException {
		return jdbcSupport.getFieldCount();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return jdbcSupport.getFieldName(fieldId);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param i
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return jdbcSupport.getFieldType(i);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return jdbcSupport.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public long getRowCount() throws ReadDriverException {
		return jdbcSupport.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#close()
	 */
	public void close() throws SQLException {
		jdbcSupport.close();
		jdbcWriter.close();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DriverCommons#getDriverProperties()
	 */
	public HashMap getDriverProperties() {
		return null;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DriverCommons#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#execute(java.sql.Connection,
	 *      java.lang.String, com.hardcode.gdbms.engine.data.HasProperties)
	 */
	public void execute(Connection con, String sql) throws SQLException {
		JDBCSupport.execute(con, sql);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getInternalTableName(java.lang.String)
	 */
	public String getInternalTableName(String tableName) {
		return tableName;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(long)
	 */
	public String getStatementString(long i) {
		return vWriter.getStatementString(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(int,
	 *      int)
	 */
	public String getStatementString(int i, int sqlType) {
		return vWriter.getStatementString(i, sqlType);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(double,
	 *      int)
	 */
	public String getStatementString(double d, int sqlType) {
		return vWriter.getStatementString(d, sqlType);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(java.lang.String,
	 *      int)
	 */
	public String getStatementString(String str, int sqlType) {
		return vWriter.getStatementString(str, sqlType);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(java.sql.Date)
	 */
	public String getStatementString(Date d) {
		return dateFormat.format(d);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(java.sql.Time)
	 */
	public String getStatementString(Time t) {
		return timeFormat.format(t);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(java.sql.Timestamp)
	 */
	public String getStatementString(Timestamp ts) {
		return timeFormat.format(ts);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(byte[])
	 */
	public String getStatementString(byte[] binary) {
		return "x" + vWriter.getStatementString(binary);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.DBDriver#getStatementString(boolean)
	 */
	public String getStatementString(boolean b) {
		return vWriter.getStatementString(b);
	}

	/**
	 * @see com.hardcode.gdbms.engine.values.ValueWriter#getNullStatementString()
	 */
	public String getNullStatementString() {
		return "null";
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return jdbcSupport.getFieldWidth(i);
	}

	public IWriter getWriter() {
		return jdbcWriter;
	}

	/**
	 * Return the default tabledefinition (only has field description values).
	 * Each driver that inheret this class must overwrite this.
	 * 
	 * */
	public ITableDefinition getTableDefinition() throws ReadDriverException {
		TableDefinition tableDef = new TableDefinition();
		tableDef.setFieldsDesc(getFieldsDescription());
		return tableDef;
	}

	/*
	 * azabala TODO Copypasteado de SelectableDataSource. ¿No estaría mejor
	 * aquí?
	 */
	private FieldDescription[] getFieldsDescription()
			throws ReadDriverException {
		int numFields = getFieldCount();
		FieldDescription[] fieldsDescrip = new FieldDescription[numFields];
		for (int i = 0; i < numFields; i++) {
			fieldsDescrip[i] = new FieldDescription();
			int type = getFieldType(i);
			fieldsDescrip[i].setFieldType(type);
			fieldsDescrip[i].setFieldName(getFieldName(i));
			fieldsDescrip[i].setFieldLength(getFieldWidth(i));
			if (NumberUtilities.isNumeric(type)) {
				if (!NumberUtilities.isNumericInteger(type))
					// TODO: If there is a lost in precision, this should be
					// changed.
					fieldsDescrip[i].setFieldDecimalCount(6);
			} else
				fieldsDescrip[i].setFieldDecimalCount(0);
			// TODO: ¿DEFAULTVALUE?
			// fieldsDescrip[i].setDefaultValue(get)
		}
		return fieldsDescrip;
	}
}
