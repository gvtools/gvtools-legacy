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

import com.iver.andami.PluginServices;

import es.prodevelop.cit.gvsig.arcims.gui.panels.ServiceNamesPanel;

import org.apache.log4j.Logger;

import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableColumnModel;


/**
 * This class detects changes on the available services list.
 *
 * @author jldominguez
 */
public class ServicesTableSelectionListener extends DefaultTableColumnModel {
    private static Logger logger = Logger.getLogger(ServicesTableSelectionListener.class.getName());
    private static final long serialVersionUID = 0;
    private ServiceNamesPanel thePanel;

    public ServicesTableSelectionListener(ServiceNamesPanel panel) {
        super();
        thePanel = panel;
    }

    /**
     * Updates local variables which keep the service's name and type. The
     * service name must be in the first column of the table; the service type
     * must be in the second column.
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        if (thePanel == null) {
            return;
        }

        if (thePanel.getServicesTable() == null) {
            return;
        }

        int i = thePanel.getServicesTable().getSelectedRow();

        if (i == -1) {
            return; // no selection
        }

        thePanel.setSelectedServiceType(ServicesTableModel.getColumnValueOfRow(
                thePanel.getServicesTable(),
                PluginServices.getText(this, "arcims_server_type_col_name"), i));

        thePanel.setSelectedServiceName(ServicesTableModel.getColumnValueOfRow(
                thePanel.getServicesTable(),
                PluginServices.getText(this, "name"), i));

        thePanel.getNextButton().setEnabled(true);

        logger.info("New selection in services table: type = " +
            thePanel.getSelectedServiceType() + ", name = " +
            thePanel.getSelectedServiceName());
    }
}
