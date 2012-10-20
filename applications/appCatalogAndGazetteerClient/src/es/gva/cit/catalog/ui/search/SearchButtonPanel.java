package es.gva.cit.catalog.ui.search;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.gvsig.i18n.Messages;

import es.gva.cit.catalog.utils.CatalogConstants;

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
/* CVS MESSAGES:
 *
 * $Id: SearchButtonPanel.java 561 2007-07-27 06:49:30 +0000 (Fri, 27 Jul 2007) jpiera $
 * $Log$
 * Revision 1.1.2.4  2007/07/24 09:45:52  jorpiell
 * Fix some interface bugs
 *
 * Revision 1.1.2.3  2007/07/23 07:14:24  jorpiell
 * Catalog refactoring
 *
 * Revision 1.1.2.2  2007/07/13 12:00:35  jorpiell
 * Add the posibility to add a new panel
 *
 * Revision 1.1.2.1  2007/07/11 13:01:50  jorpiell
 * Catalog UI updated
 *
 * Revision 1.1.2.1  2007/07/10 11:18:03  jorpiell
 * Added the registers
 *
 *
 */
/**
 * @author Jorge Piera LLodr� (jorge.piera@iver.es)
 */
public class SearchButtonPanel extends JPanel {
	private javax.swing.JButton cancelButton;
	private javax.swing.JButton closeButton;
	private javax.swing.JButton lastButton;
	private javax.swing.JButton searchButton;

	/** Creates new form SearchButtonsPanel */
	public SearchButtonPanel() {
		initComponents();
		initLabels();
		initButtonSize();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" C�digo Generado  ">
	private void initComponents() {
		searchButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		lastButton = new javax.swing.JButton();
		closeButton = new javax.swing.JButton();

		setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

		setPreferredSize(new java.awt.Dimension(200, 35));
		searchButton.setText("jButton1");
		add(searchButton);

		cancelButton.setText("jButton2");
		add(cancelButton);

		lastButton.setText("jButton3");
		add(lastButton);

		closeButton.setText("jButton4");
		add(closeButton);
	}// </editor-fold>

	/**
	 * Rewrite the labels
	 */
	private void initLabels() {
		searchButton.setText(Messages.getText("searchButton"));
		closeButton.setText(Messages.getText("close"));
		lastButton.setText(Messages.getText("last"));
		cancelButton.setText(Messages.getText("cancel"));
	}

	/**
	 * Initialize the buttons size
	 */
	private void initButtonSize() {
		searchButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
		closeButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
		lastButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
		cancelButton.setPreferredSize(CatalogConstants.BUTTON_SIZE);
	}

	/**
	 * Add a listener for the buttons
	 * 
	 * @param listener
	 *            Listener to add
	 */
	public void addActionListener(ActionListener listener) {
		cancelButton.addActionListener(listener);
		cancelButton
				.setActionCommand(CatalogConstants.CANCEL_BUTTON_ACTION_COMMAND);
		closeButton.addActionListener(listener);
		closeButton
				.setActionCommand(CatalogConstants.CLOSE_BUTTON_ACTION_COMMAND);
		lastButton.addActionListener(listener);
		lastButton
				.setActionCommand(CatalogConstants.LAST_BUTTON_ACTION_COMMAND);
		searchButton.addActionListener(listener);
		searchButton
				.setActionCommand(CatalogConstants.SEARCH_BUTTON_ACTION_COMMAND);
	}

}
