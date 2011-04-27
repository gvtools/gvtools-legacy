
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

package com.iver.cit.gvsig.fmap.drivers.sde;

import java.util.StringTokenizer;

import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeException;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.IConnection;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ConnectionSDE implements IConnection {
    private SeConnection connection;

	public SeConnection getConnection() {
		return connection;
	}

	public void close() throws DBException {
		try {
			connection.close();
		} catch (SeException e) {
			throw new DBException(e);
		}

	}

	public boolean isClosed() {
		return connection.isClosed();
	}

	public String getCatalogName() throws DBException {
		try {
			return connection.getSdeDbaName();
		} catch (SeException e) {
			throw new DBException(e);
		}
	}

	public String getNameServer() throws DBException {
		try {
			return connection.getDatabaseName();
		} catch (SeException e) {
			throw new DBException(e);
		}
	}

	public String getURL() throws DBException {
		return connection.getServer();
	}

	public void setDataConnection(String connectionStr, String user, String _pw) throws DBException {
		try {
			String database="";
			StringTokenizer st=new StringTokenizer(connectionStr,":");
			String driver=st.nextToken();
			String server=st.nextToken();
			int instance=Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
				database=st.nextToken();
			connection=new SeConnection(server, instance, database, user, _pw);
		} catch (SeException e) {
			throw new DBException(e);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public String getTypeConnection() {
		return "sde";
	}

}
