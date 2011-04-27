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
package com.iver.cit.gvsig.fmap.drivers.sde;

import java.awt.geom.Rectangle2D;

import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public interface IVectorialSDEDriver extends IVectorialDatabaseDriver {
    //public Connection getConnection();
    public IFeatureIterator getFeatureIterator(String sql);

    //public String getConnectionStringBeginning();
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#open()
     */
    public void open();

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getDefaultPort()
     */
    public int getDefaultPort();

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#setWorkingArea(java.awt.geom.Rectangle2D)
     */
    public void setWorkingArea(Rectangle2D rect);

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getWorkingArea()
     */
    public Rectangle2D getWorkingArea();

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getConnectionString(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String getConnectionString(String _host, String _port, String _db,
        String _user, String _pw);

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getTableName()
	 */
	public String getTableName();

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getXMLEntity()
	 */
	public XMLEntity getXMLEntity();

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	public void setXMLEntity(XMLEntity entity) throws XMLException;

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#close()
	 */
	public void close();

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getRowIndexByFID(com.iver.cit.gvsig.fmap.core.IFeature)
	 */
	public int getRowIndexByFID(IFeature feat);

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getLyrDef()
	 */
	public DBLayerDefinition getLyrDef();

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getWhereClause()
	 */
	public String getWhereClause();

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getFields()
	 */
	public String[] getFields();

	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG, String[] alphaNumericFieldsNeeded);

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver#getFeatureIterator(java.awt.geom.Rectangle2D, java.lang.String)
	 */
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG);

}
