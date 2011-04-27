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
package com.iver.cit.gvsig.gui.filter;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class DefaultExpressionDataSource implements ExpressionDataSource {
    private SelectableDataSource tabla = null;

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     */
    public void setTable(SelectableDataSource table) {
        this.tabla = table;
    }
    public void start() throws ReadDriverException
    {
        tabla.start();
    }
    public void stop() throws ReadDriverException
    {
        tabla.stop();
    }

    /**
     * DOCUMENT ME!
     *
     * @param idField
     *
     * @return
     * @throws FilterException
     */
    public String getFieldName(int idField) throws FilterException { 
        try {
    		return tabla.getFieldName(idField);
		} catch (ReadDriverException e) {
			throw new FilterException();
		}
		
    }

    /**
     * DOCUMENT ME!
     *
     * @return
     * @throws FilterException
     */
    public int getFieldCount() throws FilterException {
        try {
			return tabla.getFieldCount();
		} catch (ReadDriverException e) {
			throw new FilterException();
		}
    }

    /**
     * DOCUMENT ME!
     *
     * @param field
     *
     * @return
     * @throws FilterException
     */
    public Value getFieldValue(int row, int field) throws FilterException {
    	try {
			return tabla.getFieldValue(row, field);
		} catch (ReadDriverException e) {
			throw new FilterException();
		}
    }

	/**
	 * @see com.iver.cit.gvsig.gui.filter.ExpressionDataSource#getRowCount()
	 */
	public int getRowCount() throws FilterException {
		try {
			return (int) tabla.getRowCount();

		} catch (ReadDriverException e) {
			throw new FilterException();
		}
	}

	/**
	 * @see com.iver.cit.gvsig.gui.filter.ExpressionDataSource#getDataSourceName()
	 */
	public String getDataSourceName() {
		return tabla.getName();
	}
	public String getFieldAlias(int idField) throws FilterException {
		return tabla.getFieldAlias(idField);
	}
}
