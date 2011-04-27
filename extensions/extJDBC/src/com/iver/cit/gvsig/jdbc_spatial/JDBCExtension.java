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
package com.iver.cit.gvsig.jdbc_spatial;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.utiles.connections.ConnectionTransInit;



/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class JDBCExtension extends Extension {
    /**
     * DOCUMENT ME!
     */
    public void initialize() {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param actionCommand DOCUMENT ME!
     */
    public void execute(String actionCommand) {
    	ConnectionTransInit[] connDrivers=new ConnectionTransInit[2];
    	connDrivers[0]=new ConnectionTransInit();
    	connDrivers[0].setHost("localhost");
    	connDrivers[0].setName("MYSQL DataBase");
    	connDrivers[0].setConnBegining("jdbc:mysql:");
    	connDrivers[0].setPort("3306");
    	
    	connDrivers[1]=new ConnectionTransInit();
    	connDrivers[1].setHost("localhost");
    	connDrivers[1].setName("POSTGRES DataBase");
    	connDrivers[1].setConnBegining("jdbc:postgresql:");
    	connDrivers[1].setPort("5432");
    	
    	JDBCManagerView jdbcManager=new JDBCManagerView(connDrivers);
    	
    	
    	PluginServices.getMDIManager().addWindow(jdbcManager);
    		//Connection conn=null;
			//	conn = jdbcManager.getConnection();
		//	try {
		//		System.out.println(conn.getMetaData());
		//	} catch (SQLException e) {
				// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
    	
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isEnabled() {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isVisible() {
        return true;
    }
}
