/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package com.iver.cit.gvsig.fmap.drivers;

import java.awt.geom.Rectangle2D;
import java.sql.SQLException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;

/**
 * Vectorial Database Driver.
 * 
 * @author Vicente Caballero Navarro
 */
public interface IVectorialDatabaseDriver extends VectorialDriver {
	/**
	 * Obtains the boundingbox of layer.
	 * 
	 * @return Rectangle2D Boundingbox of layer.
	 * 
	 * @throws ReadDriverException
	 * @throws ExpansionFileReadException
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException,
			ExpansionFileReadException;

	/**
	 * Return shape type of layer.
	 * 
	 * @return int Shape type.
	 */
	public int getShapeType();

	/**
	 * Returns a feature iterator with all features that are contained into a
	 * rectangle.
	 * 
	 * @param r
	 *            Rectangle to query.
	 * @param strEPSG
	 *            Projection to query.
	 * 
	 * @return IFeatureIterator.
	 * 
	 * @throws ReadDriverException
	 */
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG)
			throws ReadDriverException;

	/**
	 * Returns a feature iterator with all features that are contained into a
	 * rectangle and this features with determinates fields.
	 * 
	 * @param r
	 *            Rectangle to query.
	 * @param strEPSG
	 *            Projection to query.
	 * @param alphaNumericFieldsNeeded
	 *            Fields to query.
	 * 
	 * @return IFeatureIterator
	 * 
	 * @throws ReadDriverException
	 */
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
			String[] alphaNumericFieldsNeeded) throws ReadDriverException;

	/**
	 * Returns all the fields of layer.
	 * 
	 * @return Array of field names.
	 */
	public String[] getFields();

	/**
	 * Return the where clause.
	 * 
	 * @return String with the where clause.
	 */
	public String getWhereClause();

	/**
	 * Returns table name.
	 * 
	 * @return Table name.
	 */
	public String getTableName();

	/**
	 * Close Driver.
	 */
	public void close();

	/**
	 * Open Driver.
	 */
	public void open();

	/**
	 * Returns a row index by feature.
	 * 
	 * @param FID
	 *            IFeature
	 * 
	 * @return el número de registro asociado a ese FID. Se usa dentro del
	 *         DBStrategy para averiguar si un Feature está seleccionado o no.
	 */
	public int getRowIndexByFID(IFeature FID);

	/**
	 * Returns the expression to query a geometry's field.
	 * 
	 * @param fieldName
	 *            Geometry's field name.
	 * 
	 * @return Expression to query a geometry's field
	 */
	public String getGeometryField(String fieldName);

	/**
	 * Returns a XMLEntity with all properties to save the object.
	 * 
	 * @return XMLEntity.
	 */
	public XMLEntity getXMLEntity();

	/**
	 * Para evitar que una clase no se pueda instanciar, el setXMLEntity debería
	 * devolver una referencia a la clase que crea. Ya vorem.
	 * 
	 * @param xml
	 *            XMLEntity with all properties to create again the object.
	 * 
	 * @throws XMLException
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException;

	/**
	 * Returns the layer definition.
	 * 
	 * @return information about catalog, fields, tablename, etc
	 */
	public DBLayerDefinition getLyrDef();

	/**
	 * This method is called by FLyrVect when its goig to be removed from the
	 * view. The driver can then do what it has to do.
	 */
	public void remove();

	/**
	 * Load the layer.
	 * 
	 * @throws ReadDriverException
	 */
	public void load() throws ReadDriverException;

	/**
	 * Returns the connection of this Driver.
	 * 
	 * @return devuelve la Conexión a la base de datos, para que el usuario
	 *         pueda hacer la consulta que quiera, si lo desea. Por ejemplo,
	 *         esto puede ser útil para abrir un cuadro de dialogo avanazado y
	 *         lanzar peticiones del tipo "Devuelveme un buffer a las
	 *         autopistas", y con el resultset que te venga, escribir un shape,
	 *         o cosas así.
	 */
	public IConnection getConnection();

	/**
	 * Gets the drivers connection string given its parameters (this can be
	 * different for different driver, so it should be overwritten in that
	 * case.)
	 * 
	 * @param _host
	 *            Host
	 * @param _port
	 *            Port
	 * @param _db
	 *            Database
	 * @param _user
	 *            User
	 * @param _pw
	 *            Password
	 * 
	 * @return Key compound of properties's connection.
	 */
	public String getConnectionString(String _host, String _port, String _db,
			String _user, String _pw);

	/**
	 * Returns the default port.
	 * 
	 * @return int Default port of connection.
	 */
	public int getDefaultPort();

	/**
	 * Returns the beginning string connection.
	 * 
	 * @return String
	 */
	public String getConnectionStringBeginning();

	/**
	 * Initialize the parameters of Driver with the connection and the
	 * DBLayerDefinition.
	 * 
	 * @param conn
	 *            Connection`s driver
	 * @param lyrDef
	 *            DBLayerDefinition
	 * 
	 * @throws DBException
	 *             !
	 */
	public void setData(IConnection conn, DBLayerDefinition lyrDef)
			throws DBException;

	/**
	 * Gets all field names of a given table.
	 * 
	 * @param conn
	 *            Connection
	 * @param tableName
	 *            Name of table.
	 * 
	 * @return Array of field names.
	 * 
	 * @throws DBException
	 */
	public String[] getAllFields(IConnection conn, String tableName)
			throws DBException;

	/**
	 * Gets all field type names of a given table.
	 * 
	 * @param conn
	 *            Connection
	 * @param tableName
	 *            Name of table
	 * 
	 * @return Array of field type names.
	 * 
	 * @throws DBException
	 */
	public String[] getAllFieldTypeNames(IConnection conn, String tableName)
			throws DBException;

	/**
	 * Gets the table's possible id fields. By default, all fields can be id. It
	 * should be overwritten by subclasses.
	 * 
	 * @param conn
	 *            Connection
	 * @param tableName
	 *            Name of table.
	 * 
	 * @return Array of Id field candidates.
	 * 
	 * @throws DBException
	 */
	public String[] getIdFieldsCandidates(IConnection conn, String tableName)
			throws DBException;

	/**
	 * Gets the table's possible geometry fields. By default, all fields can be
	 * geometry fields. It should be overwritten by subclasses.
	 * 
	 * @param conn
	 *            conenction object
	 * @param table_name
	 *            table name
	 * @return the table's possible geometry fields
	 * @throws SQLException
	 */
	public String[] getGeometryFieldsCandidates(IConnection conn,
			String tableName) throws DBException;

	/**
	 * Insert the working area of layer and the features that not are contained
	 * don't show it.
	 * 
	 * @param _wa
	 *            Rectangle of area.
	 */
	public void setWorkingArea(Rectangle2D _wa);

	/**
	 * Gets available table names. Should be overwritten by subclasses if its
	 * not compatible or if it can be refined.
	 * 
	 * @param conex
	 *            Connection
	 * @param dbName
	 *            Name of Database
	 * 
	 * @return Array of table names.
	 * 
	 * @throws DBException
	 */
	public String[] getTableNames(IConnection conex, String dbName)
			throws DBException;

	/**
	 * Returns working area of layer.
	 * 
	 * @return Rectangle of area.
	 */
	public Rectangle2D getWorkingArea();

	/**
	 * Tells if user can read contents of the layer.
	 * 
	 * @return true if can read, either false.
	 * @throws SQLException
	 */
	public boolean canRead(IConnection iconn, String tablename)
			throws SQLException;
}
