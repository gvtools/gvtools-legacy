
/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
*   Av. Blasco Ib��ez, 50
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
package es.gva.cit.gazetteer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import es.gva.cit.gazetteer.adl.drivers.ADLGazetteerServiceDriver;
import es.gva.cit.gazetteer.geonames.GeonamesServiceDriver;
import es.gva.cit.gazetteer.idec.drivers.IDECGazetteerServiceDriver;
import es.gva.cit.gazetteer.ui.serverconnect.ServerConnectDialog;
import es.gva.cit.gazetteer.utils.GazetteerDriverRegister;
import es.gva.cit.gazetteer.wfs.drivers.WFSServiceDriver;
import es.gva.cit.gazetteer.wfsg.drivers.WFSGServiceDriver;

/**
 * This class is the launcher application for the Gazetteer Client
 * 
 * 
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class GazetteerAP {

/**
 * 
 * 
 * 
 * @param args 
 */
    public static void main(String[] args) {        
        GazetteerDriverRegister register = GazetteerDriverRegister.getInstance();
        register.register(new ADLGazetteerServiceDriver());
        register.register(new WFSServiceDriver());
        register.register(new IDECGazetteerServiceDriver());
        register.register(new WFSGServiceDriver());
        register.register(new GeonamesServiceDriver());
        //Get the currently installed look and feel
        UIManager.getLookAndFeel();
        // Install a different look and feel; specifically, the Windows look and feel
        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (InstantiationException e) {
        } catch (ClassNotFoundException e) {
        } catch (UnsupportedLookAndFeelException e) {
        } catch (IllegalAccessException e) {
        }
        
        new ServerConnectDialog();
    } 
 }
