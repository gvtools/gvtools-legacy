/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcImsProtocolHandler;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;

import com.iver.andami.PluginServices;


/**
* This class is used to get and use the data model of the
* available services table.

* @author jldominguez
*/
public class ServicesTableDataSource {
    protected static Logger logger = Logger.getLogger(ServicesTableDataSource.class.getName());
    private Vector colNames;
    private Vector data; // vector of vectors
    private String nameColString = PluginServices.getText(this, "name");
    private String typeColString = PluginServices.getText(this,
            "arcims_server_type_col_name");
    private String statusColString = PluginServices.getText(this,
            "arcims_server_status_col_name");

    /**
    * The constructor is called with the server's URL as a parameter.
    * Sets the columns names to "Name", "Type" and
    * "Status" and gets the table data by sending a request to the server.
     * @throws ArcImsException
    */
    public ServicesTableDataSource(URL svrURL, boolean overrride)
        throws ArcImsException {
        colNames = new Vector();
        loadDataAndColNames(svrURL, overrride);

        if (data.size() > 0) {
            colNames.addElement(nameColString);
            colNames.addElement(typeColString);
            colNames.addElement(statusColString);
        }
    }

    /**
    * Gets a vector with the column names.
    *
    * @return the vector with the column names.
    */
    public Vector getColNamesVector() {
        return colNames;
    }

    /**
    * Gets a vector with the table's data.
    *
    * @return the table's data is a vector of vectors
    */
    public Vector getDataVector() {
        return data;
    }

    /**
    * Sends a request to the server (ServiceName=catalog)
    * and loads the private vectors (data and column names)
     * @param tmpDriver
    *
    * @param serverURL server's URL
     * @throws ArcImsException
    */
    private void loadDataAndColNames(URL serverURL, boolean override)
        throws ArcImsException {
        ArrayList _services = ArcImsProtocolHandler.getCatalog(serverURL,
                override);

        ArrayList services = leaveKnownServices(_services);

        data = new Vector();

        TreeSet auxTree = new TreeSet();

        for (int i = 0; i < services.size(); i++) {
            ArrayList item = (ArrayList) services.get(i);
            auxTree.add(item.get(0));
        }

        Iterator iter = auxTree.iterator();

        while (iter.hasNext()) {
            String name = (String) iter.next();
            data.add(getItemWithName(services, name));
        }

        //		for (int i=0; i<services.size(); i++) {
        //			ArrayList item = (ArrayList) services.get(i);
        //			Vector vv = new Vector();
        //			vv.addElement( item.get(0) );
        //			vv.addElement( item.get(1) );
        //			vv.addElement( item.get(2) );
        //			// int insert = getInsertPosition(String str, data);
        //			// data.insertElementAt(vv); //, insert);
        //		}
    }

    private ArrayList leaveKnownServices(ArrayList list) {
        ArrayList resp = new ArrayList();
        ArrayList item;
        String type;
        String enabled;

        for (int i = 0; i < list.size(); i++) {
            item = (ArrayList) list.get(i);
            type = (String) item.get(1);
            enabled = (String) item.get(2);

            if (isKnownServiceType(type) && (isEnabled(enabled))) {
                resp.add((ArrayList) item.clone());
            }
        }

        return resp;
    }

    private boolean isEnabled(String str) {
        if (str.compareToIgnoreCase("enabled") == 0) {
            return true;
        }

        return false;
    }

    private boolean isKnownServiceType(String type) {
        if (type.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
            return true;
        }

        if (type.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
            return true;
        }

        return false;
    }

    private Vector getItemWithName(ArrayList allitems, String name) {
        for (int i = 0; i < allitems.size(); i++) {
            if (((String) ((ArrayList) allitems.get(i)).get(0)).compareToIgnoreCase(
                        name) == 0) {
                Vector vv = new Vector();
                vv.addElement(((ArrayList) allitems.get(i)).get(0));
                vv.addElement(((ArrayList) allitems.get(i)).get(1));
                vv.addElement(((ArrayList) allitems.get(i)).get(2));

                return vv;
            }
        }

        logger.error("Service name not found ");

        return null;
    }

    private int getInsertPosition(String str, Vector data) {
        for (int i = 0; i < data.size(); i++) {
            String aux = (String) ((ArrayList) data.get(i)).get(0);

            if (aux.compareToIgnoreCase(str) > 0) {
                return i;
            }
        }

        return data.size();
    }

    /**
    * Gets the name of the <i>n</i>th column (<i>n</i> = columnIndex)
    *
    * @return column name
    */
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return nameColString;
        }

        if (columnIndex == 1) {
            return typeColString;
        }

        if (columnIndex == 2) {
            return statusColString;
        }

        return "(Columna desconocida)";
    }
}
