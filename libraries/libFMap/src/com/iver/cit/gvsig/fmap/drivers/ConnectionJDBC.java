
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

package com.iver.cit.gvsig.fmap.drivers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ConnectionJDBC implements IConnection {
    private Connection connection;
	private String connectionStr;
	private String user;
	private String _pw;

	// to cache the quote string to avoid make a query each time
	private String identifierQuoteString = null;

	public ConnectionJDBC() {

	}

	public Connection getConnection() {
		try {
			// try to getConnection if is closed
			if (connection != null && connection.isClosed()){
				Connection tmpCon = null;
				tmpCon = DriverManager.getConnection(connectionStr, user, _pw);
				if (tmpCon != null && !tmpCon.isClosed()){
					connection = tmpCon;
					connection.setAutoCommit(false);
				}
			}
		} catch (SQLException e) {
			// FIXME
			e.printStackTrace();
		}
		return connection;
	}

	public void close() throws DBException {
		try {
			connection.close();
		} catch (SQLException e) {
			throw new DBException(e);
		}

	}

	public boolean isClosed() throws DBException {
		try {
			return connection.isClosed();
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	public String getCatalogName() throws DBException {
		try {
			return connection.getCatalog();
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	public String getNameServer() throws DBException {
		try {
			return connection.getMetaData().getDatabaseProductName();
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	public String getURL() throws DBException {
		try {
			return connection.getMetaData().getURL();
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	public void setDataConnection(String connectionStr, String user, String _pw) throws DBException {
		try {
			connection = DriverManager.getConnection(connectionStr, user, _pw);
			connection.setAutoCommit(false);
			this.connectionStr = connectionStr;
			this.user = user;
			this._pw = _pw;
		} catch (SQLException e) {
			throw new DBException(e);
		}

	}

	/**
	 *
	 * @return the quote string for this gbdms. A empty string is returned if there are any error
	 * or if quoting is not supported
	 */
	public String getIdentifierQuoteString() {
		/* if there is not an error the quote string is cached, if there is an error an empty string
		 * is returned to avoid obligate the user to check this method.
		 */

		String quote = identifierQuoteString;
		if (quote == null) {
			try {
				quote = connection.getMetaData().getIdentifierQuoteString().trim();
				identifierQuoteString = quote;

			} catch (SQLException e) {
				quote = "";
			}
		}
		return quote;
	}

	public String getTypeConnection() {
		return "jdbc";
	}
}
