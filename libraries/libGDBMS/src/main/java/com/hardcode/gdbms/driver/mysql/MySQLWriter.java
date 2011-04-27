/*
 * Created on 16-ene-2007 by azabala
 *
 */
package com.hardcode.gdbms.driver.mysql;

import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.InnerDBUtils;
import com.hardcode.gdbms.engine.data.db.DBDataWare;
import com.hardcode.gdbms.engine.data.db.DBTableSourceInfo;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueCollection;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.JdbcFieldManager;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;

/**
 *
 * MySQL alphanumeric writer.
 * It is needed because MySQL JDBC driver doesnt allow updatables recordsets.
 * @author alzabord
 *
 * @see MySQLSpatialWriter from extJDBC project
 */
public class MySQLWriter extends AbstractWriter{

	private int numRows;
	private Connection conn;
	private Statement st;
	private ResultSetMetaData metaData;
	private boolean bCreateTable;
	private MySQL mySql = new MySQL();
	private JdbcFieldManager fieldManager;
	private DBDataWare directDataWare;

	public void initialize(Connection conn){
		this.conn = conn;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#initialize(com.iver.cit.gvsig.fmap.drivers.ITableDefinition)
	 */
	public void initialize(ITableDefinition tableDefinition) throws InitializeWriterException{
		super.initialize(tableDefinition);
		try {
			createTableIfNeeded();
		} catch (SQLException e) {
			throw new InitializeWriterException(getName(),e);
		}
	}


	private void createTableIfNeeded() throws SQLException{
		st = conn.createStatement();
		if (bCreateTable) {
			try {
				st.execute("DROP TABLE " +
						((DBLayerDefinition)tableDef).getTableName() + ";");
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			//In MySQL you can add geometry column in CREATE TABLE statement
			String sqlCreate = mySql.getSqlCreateSpatialTable((DBLayerDefinition) tableDef,
											tableDef.getFieldsDesc(),
											true);
			st.execute(sqlCreate);
			conn.commit();
		}//if
		conn.setAutoCommit(false);
		fieldManager = new JdbcFieldManager(conn,
				((DBLayerDefinition) tableDef).getTableName());
}


	public boolean canWriteAttribute(int sqlType) {
		switch (sqlType) {
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.INTEGER:
		case Types.BIGINT:
			return true;
		case Types.DATE:
			return true;
		case Types.BIT:
		case Types.BOOLEAN:
			return true;
		case Types.VARCHAR:
		case Types.CHAR:
		case Types.LONGVARCHAR:
			return true;
		}
		return false;
	}


	public void preProcess() throws StartWriterVisitorException {
		numRows = 0;
		try {
			conn.setAutoCommit(false);
			conn.rollback();
			alterTable();
		} catch (SQLException e) {
			throw new StartWriterVisitorException(getName(),e);
		} catch (WriteDriverException e) {
			throw new StartWriterVisitorException(getName(),e);
		}
	}

	public boolean alterTable() throws WriteDriverException {
		return fieldManager.alterTable();
	}

    private ValueCollection getPKValue(Value[] rec) throws ReadDriverException {
        int[] fieldsId = directDataWare.getPrimaryKeys();
        Value[] pks = new Value[fieldsId.length];

        for (int i = 0; i < pks.length; i++) {
            pks[i] = rec[fieldsId[i]];
        }

        return ValueFactory.createValue(pks);
    }


	public void process(IRowEdited row) throws ProcessWriterVisitorException {
//		String sqlInsert;
		try {
			switch (row.getStatus()) {
			case IRowEdited.STATUS_ADDED:
				IRow record =  row.getLinkedRow();
				directDataWare.insertFilledRow(record.getAttributes());
//				sqlInsert = mySql.getSqlInsertFeature((DBLayerDefinition) tableDef, record);
//				st.execute(sqlInsert);
				break;

			case IRowEdited.STATUS_MODIFIED:
				IRow featM =  row.getLinkedRow();
				if (bWriteAll) {
					directDataWare.insertFilledRow(featM.getAttributes());
//					sqlInsert = mySql.getSqlInsertFeature((DBLayerDefinition) tableDef, featM);
//					System.out.println("sql = " + sqlInsert);
//					st.execute(sqlInsert);
				} else {
	    			Value[] rec =row.getAttributes();
    				String sql = InnerDBUtils.createUpdateStatement(((DBTableSourceInfo) directDataWare.getSourceInfo()).tableName,
	    						getPKValue(rec).getValues(), directDataWare.getPKNames(),
				                directDataWare.getFieldNames(), featM.getAttributes(), ((ValueWriter)directDataWare.getDriver()));

    				st.execute(sql);
//					String sqlModify = mySql.getSqlModifyFeature((DBLayerDefinition) tableDef, featM);
//					st.execute(sqlModify);
				}
				break;

			case IRowEdited.STATUS_ORIGINAL:
				IRow featO = row.getLinkedRow();
				if (bWriteAll) {
					directDataWare.insertFilledRow(featO.getAttributes());
//					sqlInsert = mySql.getSqlInsertFeature((DBLayerDefinition) tableDef, featO);
//					st.execute(sqlInsert);
				}
				break;

			case IRowEdited.STATUS_DELETED:
    			Value[] rec =row.getAttributes();
				String sqlDelete = InnerDBUtils.createDeleteStatement(
						getPKValue(rec).getValues(),
    					directDataWare.getPKNames(),
    					((DBTableSourceInfo) directDataWare.getSourceInfo()).tableName,
    					((ValueWriter)directDataWare.getDriver()));

				st.execute(sqlDelete);

//				String sqlDelete = mySql.getSqlDeleteFeature((DBLayerDefinition) tableDef, row);
				System.out.println("sql = " + sqlDelete);
				st.execute(sqlDelete);
				break;
			}
			numRows++;
		} catch (SQLException e) {
			throw new ProcessWriterVisitorException(getName(),e);
		} catch (WriteDriverException e) {
			throw new ProcessWriterVisitorException(getName(),e);
		} catch (ReadDriverException e) {
			throw new ProcessWriterVisitorException(getName(),e);
		}
	}


	public void postProcess() throws StopWriterVisitorException {
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new StopWriterVisitorException(getName(),e);
		}
	}


	public boolean canAlterTable() {
		return true;
	}


	public boolean canSaveEdits() {
		try {
			return !conn.isReadOnly();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getName() {
		return "MySQL Alphanumeric Writer";
	}

	public void setCreateTable(boolean createTable) {
		bCreateTable = createTable;
	}

	public boolean isCreateTable() {
		return bCreateTable;
	}

	public boolean isWriteAll() {
		return bWriteAll;
	}

	public void setWriteAll(boolean writeAll) {
		bWriteAll = writeAll;
	}

/*
 * TODO
 * Esta parte no se si es necesaria, o si se usa (revisar)
 *
 * */
	public FieldDescription[] getFields() {
		return fieldManager.getFields();
	}

	public FieldDescription[] getOriginalFields() {
		return tableDef.getFieldsDesc();
	}

	public void addField(FieldDescription fieldDesc) {
		fieldManager.addField(fieldDesc);

	}

	public FieldDescription removeField(String fieldName) {
		return fieldManager.removeField(fieldName);

	}

	public void renameField(String antName, String newName) {
		fieldManager.renameField(antName, newName);
	}

	public DBDataWare getDirectDataWare() {
		return directDataWare;
	}

	public void setDirectDataWare(DBDataWare directDataWare) {
		this.directDataWare = directDataWare;
	}

}
