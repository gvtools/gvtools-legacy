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
* $Id: MySQLSpatialWriter.java 13881 2007-09-19 16:22:04Z jaume $
* $Log$
* Revision 1.5  2007-09-19 16:11:32  jaume
* removed unnecessary imports
*
* Revision 1.4  2007/06/04 07:10:07  caballero
* connections refactoring
*
* Revision 1.3  2007/03/06 16:49:54  caballero
* Exceptions
*
* Revision 1.2  2007/01/16 20:03:36  azabala
* change of the writer name
*
* Revision 1.1  2007/01/16 13:11:20  azabala
* first version in cvs
*
* Revision 1.1  2007/01/15 20:15:35  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.fmap.drivers.jdbc.mysql;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IFieldManager;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.JdbcFieldManager;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;

public class MySQLSpatialWriter extends AbstractWriter
		implements ISpatialWriter, IFieldManager {

	private int numRows;

	private DBLayerDefinition lyrDef;

	private IConnection conex;

	private Statement st;

	/**
	 * flag to mark if writer must create the table or it must
	 * add records to an existing one
	 *
	 */
	private boolean bCreateTable;

	private boolean bWriteAll;

	private MySql mySql = new MySql();

	/*
	 * TODO
	 * Ver si en MySQL los tipos de datos (numeric, etc.)
	 * se nombran así también
	 * */
	private JdbcFieldManager fieldManager;


//	public void initialize(Connection conex)throws SQLException{
//		this.conex = conex;
//		init();
//	}



	public void initialize(ITableDefinition lyrD) throws InitializeWriterException {
		super.initialize(lyrD);
		this.lyrDef = (DBLayerDefinition) lyrD;
		conex = lyrDef.getConnection();
		try {
			init();
		} catch (SQLException e) {
			throw new InitializeWriterException(getName(),e);
		}
	}

	private void init() throws SQLException{

			st = ((ConnectionJDBC)conex).getConnection().createStatement();

			/*
			 * y en caso de que sea false usar "CREATE TABLE IF NOT EXISTS" en el
			 * else
			 * (AZO)
			 *
			 * */
			if (bCreateTable) {
				try {
					st.execute("DROP TABLE " + lyrDef.getTableName() + ";");
				} catch (SQLException e1) {
				}
				//In MySQL you can add geometry column in CREATE TABLE statement
				String sqlCreate = mySql.getSqlCreateSpatialTable(lyrDef,
												lyrDef.getFieldsDesc(),
												true);
				st.execute(sqlCreate);
				((ConnectionJDBC)conex).getConnection().commit();
			}//if
			((ConnectionJDBC)conex).getConnection().setAutoCommit(false);
			fieldManager = new JdbcFieldManager(((ConnectionJDBC)conex).getConnection(), lyrDef.getTableName());
	}


	public void preProcess() throws StartWriterVisitorException {
		numRows = 0;

        // ATENTION: We will transform (in PostGIS class; doubleQuote())
        // to UTF-8 strings. Then, we tell the PostgreSQL server
        // that we will use UTF-8, and it can translate
        // to its charset
        // Note: we have to translate to UTF-8 because
        // the server cannot manage UTF-16
		try {
			((ConnectionJDBC)conex).getConnection().setAutoCommit(false);
			((ConnectionJDBC)conex).getConnection().rollback();
			alterTable();
		} catch (SQLException e) {
			throw new StartWriterVisitorException(getName(),e);
		} catch (WriteDriverException e) {
			throw new StartWriterVisitorException(getName(),e);
		}
	}

	public void process(IRowEdited row) throws ProcessWriterVisitorException{

		String sqlInsert;
		try {
			switch (row.getStatus()) {
			case IRowEdited.STATUS_ADDED:
				IFeature feat = (IFeature) row.getLinkedRow();
				sqlInsert = mySql.getSqlInsertFeature(lyrDef, feat);
				st.execute(sqlInsert);
				break;

			case IRowEdited.STATUS_MODIFIED:
				IFeature featM = (IFeature) row.getLinkedRow();
				if (bWriteAll) {
					sqlInsert = mySql.getSqlInsertFeature(lyrDef, featM);
					System.out.println("sql = " + sqlInsert);
					st.execute(sqlInsert);
				} else {
					String sqlModify = mySql.getSqlModifyFeature(lyrDef, featM);
					st.execute(sqlModify);
				}
				break;

			case IRowEdited.STATUS_ORIGINAL:
				IFeature featO = (IFeature) row.getLinkedRow();
				if (bWriteAll) {
					sqlInsert = mySql.getSqlInsertFeature(lyrDef, featO);
					st.execute(sqlInsert);
				}
				break;

			case IRowEdited.STATUS_DELETED:
				String sqlDelete = mySql.getSqlDeleteFeature(lyrDef, row);
				System.out.println("sql = " + sqlDelete);
				st.execute(sqlDelete);

				break;
			}

			numRows++;
		} catch (SQLException e) {
			throw new ProcessWriterVisitorException(getName(),e);
		}

	}

	public void postProcess() throws StopWriterVisitorException{
		try {
			((ConnectionJDBC)conex).getConnection().setAutoCommit(true);
		} catch (SQLException e) {
			throw new StopWriterVisitorException(getName(),e);
		}
	}

	public String getName() {
		return "MySQL Spatial Writer";
	}

	public boolean canWriteGeometry(int gvSIGgeometryType) {
		switch (gvSIGgeometryType) {
		case FShape.POINT:
			return true;
		case FShape.LINE:
			return true;
		case FShape.POLYGON:
			return true;
		case FShape.ARC:
			return false;
		case FShape.ELLIPSE:
			return false;
		case FShape.MULTIPOINT:
			return true;
		case FShape.TEXT:
			return false;
		}
		return false;
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


	public FieldDescription[] getOriginalFields() {
		return lyrDef.getFieldsDesc();
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

	public boolean alterTable() throws WriteDriverException {
		return fieldManager.alterTable();
	}

	public FieldDescription[] getFields() {
		return fieldManager.getFields();
	}

	public boolean canAlterTable() {
		return true;
	}

	public boolean canSaveEdits() {
		// TODO: Revisar los permisos de la tabla en cuestión.
		try {
			return !((ConnectionJDBC)conex).getConnection().isReadOnly();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
