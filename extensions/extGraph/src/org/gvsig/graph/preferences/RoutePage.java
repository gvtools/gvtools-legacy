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

/* CVS MESSAGES:
 *
 * $Id: RoutePage.java 22182 2008-07-10 07:20:11Z fpenarrubia $
 * $Log$
 * Revision 1.11  2007-09-19 14:28:27  fjp
 * Test case para ServiceArea y los ficheros de prueba con los ejes de Madrid.
 *
 * Revision 1.10  2007/09/07 11:29:47  fjp
 * Casi compila. Falta arreglar lo de FArrowSymbol y retocar el graphiclist de FMap.
 *
 * Revision 1.9  2007/01/16 11:51:07  jaume
 * *** empty log message ***
 *
 * Revision 1.8  2007/01/10 16:49:23  jaume
 * *** empty log message ***
 *
 * Revision 1.7  2006/10/30 19:30:35  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2006/10/26 16:31:21  jaume
 * GUI
 *
 * Revision 1.5  2006/10/26 07:46:58  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2006/10/25 10:50:41  jaume
 * movement of classes and gui stuff
 *
 * Revision 1.3  2006/10/24 16:31:40  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/23 16:00:20  jaume
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/23 08:05:39  jaume
 * GUI
 *
 *
 */
package org.gvsig.graph.preferences;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.gui.styling.SymbolPreviewer;
import com.iver.cit.gvsig.gui.styling.SymbolSelector;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ISymbolSelector;

public class RoutePage extends AbstractPreferencePage {
	private ImageIcon icon;
	private SymbolPreviewer symbolPreview;
	private JButton btnChangeSymbol;

	public RoutePage() {
		icon = new ImageIcon(this.getClass().getClassLoader().getResource(
				"images/net-analyst-icon.png"));
		symbolPreview = new SymbolPreviewer();
		symbolPreview.setPreferredSize(new Dimension(100, 100));
		symbolPreview.setBorder(BorderFactory.createBevelBorder(1));

		JPanel aux = new JPanel();
		aux.add(symbolPreview);
		addComponent(PluginServices.getText(this, "use_symbol") +":", aux);
		aux = new JPanel();
		aux.add(btnChangeSymbol = new JButton(PluginServices.getText(this, "change")));
		btnChangeSymbol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ISymbolSelector symbSelec = SymbolSelector.createSymbolSelector(null, FShape.LINE);
				PluginServices.getMDIManager().addWindow(symbSelec);
				ISymbol sym = (ISymbol) symbSelec.getSelectedObject();
				if (sym!=null)
					symbolPreview.setSymbol(sym);
			}
		});
		addComponent("", aux);
	}

	public void storeValues() throws StoreException {
		// TODO Auto-generated method stub

	}

	public void setChangesApplied() {
		// TODO Auto-generated method stub

	}

	public String getID() {
		return getClass().getName();
	}

	public String getTitle() {
		return PluginServices.getText(this, "net_analyst");
	}

	public JPanel getPanel() {
		return this;
	}

	public void initializeValues() {
		// TODO Auto-generated method stub

	}

	public void initializeDefaults() {
		// TODO Auto-generated method stub

	}

	public ImageIcon getIcon() {
		return icon;
	}

	public boolean isValueChanged() {
		// TODO Auto-generated method stub
		return false;
	}

}
