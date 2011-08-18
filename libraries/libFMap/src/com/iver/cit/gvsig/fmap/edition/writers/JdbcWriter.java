package com.iver.cit.gvsig.fmap.edition.writers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import sun.jdbc.odbc.JdbcOdbcConnection;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.XTypes;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class JdbcWriter extends AbstractWriter {
	
	private static Logger logger = Logger.getLogger(JdbcWriter.class.getName());
	
	Connection conn;
	ResultSet rs;
	Value[] record;
	int numRecord;

	private boolean bCreateTable;

	private ResultSetMetaData metaData = null;

	public JdbcWriter(){
	}
	public void initialize(Connection conn, ResultSet rs) throws SQLException{
		this.conn = conn;
		this.rs = rs;
		metaData = rs.getMetaData();
		System.out.println("INICIO CONEXIÓN DE ESCRITURA");
	}
	public void preProcess() throws StartWriterVisitorException {
			numRecord = 0;
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				if (conn instanceof JdbcOdbcConnection) {
					logger.warn("Driver does not allow autocommit method: " + conn.getClass().getName());
				} else {
					throw new StartWriterVisitorException(getName(),e);
				}
			}
			/* Statement st = conn.createStatement();

			if (bCreateTable) {
				try {
					st.execute("DROP TABLE " + lyrDef.getTableName() + ";");
				} catch (SQLException e1) {
					// Si no existe la tabla, no hay que borrarla.
				}

				String sqlCreate = PostGIS.getSqlCreateSpatialTable(lyrDef,
						lyrDef.getFieldsDesc(), true);
				System.out.println("sqlCreate =" + sqlCreate);
				st.execute(sqlCreate);
			} */

	}

	public void process(IRowEdited editedRow) throws ProcessWriterVisitorException {
		IRow row = editedRow.getLinkedRow();

		try {
			System.out.println("Intento escribir el registro " +
					numRecord + " de la capa " + metaData.getTableName(1));
			switch (editedRow.getStatus())
			{
    		case IRowEdited.STATUS_ADDED:
    			record=row.getAttributes();
    			rs.moveToInsertRow();
    			for (int i=0; i < record.length; i++)
    				XTypes.updateValue(rs, i, record[i]);
    			rs.insertRow();
    			break;
    		case IRowEdited.STATUS_MODIFIED:
    			record=row.getAttributes();
    			rs.absolute(editedRow.getIndex()+1);
    			for (int i=0; i < record.length; i++)
    				XTypes.updateValue(rs, i, record[i]);
    			rs.updateRow();
    			break;
    		case IRowEdited.STATUS_ORIGINAL:
    			if (bWriteAll)
    			{
        			record=row.getAttributes();
        			rs.moveToInsertRow();
        			for (int i=0; i < record.length; i++)
        				XTypes.updateValue(rs, i, record[i]);
        			rs.insertRow();
    			}
    			break;
    		case IRowEdited.STATUS_DELETED:
    			rs.absolute(editedRow.getIndex()+1);
        		rs.deleteRow();
    			break;
			}
			numRecord++;
			rs.next();

		} catch (SQLException e) {
			System.out.println(e.getSQLState() + " " + e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new ProcessWriterVisitorException(getName(),e1);
			}

			throw new ProcessWriterVisitorException(getName(),e);
		}

	}

	public void postProcess() throws StopWriterVisitorException {
		try {
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new StopWriterVisitorException(getName(),e1);
			}
			throw new StopWriterVisitorException(getName(),e);
		}
	}

	public boolean canWriteAttribute(int sqlType) {
		return true;
	}

	public String getName() {
		return "JDBC Writer";
	}
	/**
	 * @return Returns the bCreateTable.
	 */
	public boolean isCreateTable() {
		return bCreateTable;
	}

	/**
	 * @param createTable
	 *            The bCreateTable to set.
	 */
	public void setCreateTable(boolean createTable) {
		bCreateTable = createTable;
	}

	/**
	 * @return Returns the bWriteAll.
	 */
	public boolean isWriteAll() {
		return bWriteAll;
	}

	/**
	 * @param writeAll
	 *            The bWriteAll to set.
	 */
	public void setWriteAll(boolean writeAll) {
		bWriteAll = writeAll;
	}

	public void close() throws SQLException
	{
		rs.close();
		// conn.close();
		System.out.println("CIERRO CONEXIÓN DE ESCRITURA");
	}
	public boolean canAlterTable() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean canSaveEdits() {
		try {
			return (rs.getConcurrency() == ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}


// [eiel-gestion-conexiones]
