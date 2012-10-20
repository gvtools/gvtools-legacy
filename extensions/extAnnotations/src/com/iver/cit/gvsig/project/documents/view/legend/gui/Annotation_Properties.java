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

package com.iver.cit.gvsig.project.documents.view.legend.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.project.documents.gui.Annotation_ConfigureLabel;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class Annotation_Properties extends JPanel {
	private JPanel pNorth = null;
	private JPanel pCenter = null;
	private JPanel pSouth = null;
	private Annotation_Layer al;

	/**
	 * This is the default constructor
	 * 
	 * @param al
	 *            DOCUMENT ME!
	 */
	public Annotation_Properties(Annotation_Layer al) {
		super();
		this.al = al;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300, 200);
		this.add(getPNorth(), java.awt.BorderLayout.NORTH);
		this.add(getPCenter(), java.awt.BorderLayout.CENTER);
		this.add(getPSouth(), java.awt.BorderLayout.SOUTH);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public WindowInfo getWindowInfo() {
		WindowInfo viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG);
		viewInfo.setWidth(500);
		viewInfo.setHeight(400);
		viewInfo.setTitle(PluginServices
				.getText(this, "propiedades_de_la_capa"));

		return viewInfo;
	}

	/**
	 * This method initializes pNorth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPNorth() {
		if (pNorth == null) {
			pNorth = new JPanel();
		}

		return pNorth;
	}

	/**
	 * This method initializes pCenter
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPCenter() {
		if (pCenter == null) {
			pCenter = new JPanel();
			pCenter.add(new Annotation_ConfigureLabel(null, al));
		}

		return pCenter;
	}

	/**
	 * This method initializes pSouth
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPSouth() {
		if (pSouth == null) {
			pSouth = new JPanel();
		}

		return pSouth;
	}
}
