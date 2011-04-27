
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

import java.util.ArrayList;
import java.util.Iterator;

import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/**
 * Connection Factory
 *
 * @author Vicente Caballero Navarro
 */
public class ConnectionFactory {
    private static ArrayList connections = new ArrayList();
//    private static HashMap lastConnections=new HashMap();
    /**
     * DOCUMENT ME!
     *
     * @param connectionStr DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param _pw DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws DBException DOCUMENT ME!
     */
    public static IConnection createConnection(String connectionStr,
        String user, String _pw) throws DBException {
        IConnection connection = null;
        try{
        if (connectionStr.startsWith("jdbc")) {
            connection = new ConnectionJDBC();
            connection.setDataConnection(connectionStr, user, _pw);
            return connection;
        } else {
//        	if (lastConnections.containsKey(connectionStr))
//        		return (IConnection)lastConnections.get(connectionStr);

            refreshExtensionPointsConnections();

            IConnection[] conns = (IConnection[]) connections.toArray(new IConnection[0]);
            String type = connectionStr.substring(0,connectionStr.indexOf(":"));

            for (int i = 0; i < conns.length; i++) {
                if (conns[i].getTypeConnection().equals(type)) {
                    connection=conns[i];
                	connection.setDataConnection(connectionStr, user, _pw);
//                	lastConnections.put(connectionStr,connection);
                	return connection;
                }
            }
        }
        }catch (Exception e) {
			throw new DBException(e);
		}
        return connection;
    }

    /**
     * DOCUMENT ME!
     */
    private static void refreshExtensionPointsConnections() {
        connections.clear();

        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
        ExtensionPoint extensionPoint = (ExtensionPoint) extensionPoints.get(
                "databaseconnections");
        Iterator iterator = extensionPoint.keySet().iterator();

        while (iterator.hasNext()) {
            try {
                IConnection obj = (IConnection) extensionPoint.create((String) iterator.next());
                connections.add(obj);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }
}
