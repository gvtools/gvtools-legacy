package com.hardcode.gdbms.driver.foodriver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

import com.hardcode.gdbms.driver.exceptions.BadFieldDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.AlphanumericDBDriver;
import com.hardcode.gdbms.engine.spatial.GeneralPath;
import com.hardcode.gdbms.engine.spatial.GeometryImpl;
import com.hardcode.gdbms.engine.spatial.PTTypes;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;

public class FooDriver implements AlphanumericDBDriver {

	public Connection getConnection(String host, int port, String dbName, String user, String password) throws SQLException {
		return new FooConnection();
	}

	public void open(Connection con, String sql) throws SQLException {
	}

	public void execute(Connection con, String sql) throws SQLException {
	}

	public void close() throws SQLException {
	}

	public String getInternalTableName(String tablename) {
		return tablename;
	}

	public Value getFieldValue(long rowIndex, int fieldId) throws ReadDriverException {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(10.0, 10.0);
		gp.lineTo(10.0, 20.0);
		gp.lineTo(20.0, 20.0);
		gp.lineTo(20.0, 10.0);
		gp.closePath();
		Value v = new GeometryImpl(gp);
		return v;
	}

	public int getFieldCount() throws ReadDriverException {
		return 1;
	}

	public String getFieldName(int fieldId) throws ReadDriverException {
		if (fieldId != 1) throw new BadFieldDriverException(getName(),null,String.valueOf(fieldId));
		return "Geometría";
	}

	public long getRowCount() throws ReadDriverException {
		return 1;
	}

	public int getFieldType(int i) throws ReadDriverException {
		if (i != 1) throw new BadFieldDriverException(getName(),null,String.valueOf(i));
		return PTTypes.GEOMETRY;
	}

	public String getName() {
		return "FooDriver";
	}

	public String getStatementString(long i) {
		throw new RuntimeException();
	}

	public String getStatementString(int i, int sqlType) {
		throw new RuntimeException();
	}

	public String getStatementString(double d, int sqlType) {
		throw new RuntimeException();
	}

	public String getStatementString(String str, int sqlType) {
		throw new RuntimeException();
	}

	public String getStatementString(Date d) {
		throw new RuntimeException();
	}

	public String getStatementString(Time t) {
		throw new RuntimeException();
	}

	public String getStatementString(Timestamp ts) {
		throw new RuntimeException();
	}

	public String getStatementString(byte[] binary) {
		throw new RuntimeException();
	}

	public String getStatementString(boolean b) {
		throw new RuntimeException();
	}

	public String getNullStatementString() {
		throw new RuntimeException();
	}

	public HashMap getDriverProperties() {
		return null;
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {

	}

	public int getFieldWidth(int i) throws ReadDriverException {
		if (i != 1) throw new BadFieldDriverException(getName(),null,String.valueOf(i));
		return 1;
	}

	public ITableDefinition getTableDefinition() throws ReadDriverException {
		return null;
	}

	public String getDefaultPort() {
		// TODO Auto-generated method stub
		return null;
	}

}
