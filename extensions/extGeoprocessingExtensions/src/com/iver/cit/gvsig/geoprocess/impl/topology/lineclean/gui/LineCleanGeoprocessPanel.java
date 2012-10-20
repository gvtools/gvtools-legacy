/*
 * Created on 10-oct-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: LineCleanGeoprocessPanel.java 24094 2008-10-19 07:44:04Z azabala $
 * $Log$
 * Revision 1.1  2006-12-21 17:23:27  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/12/04 19:42:23  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/17 18:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/10 18:50:17  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;

import javax.swing.JCheckBox;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.gui.AbstractGeoprocessGridbagPanel;
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.ILineCleanGeoprocessUserEntries;

public class LineCleanGeoprocessPanel extends AbstractGeoprocessGridbagPanel
		implements ILineCleanGeoprocessUserEntries {

	private static final long serialVersionUID = 6485409632799083097L;

	private JCheckBox addGroupOfLyrsCb;

	public LineCleanGeoprocessPanel(FLayers arg0) {
		super(arg0, PluginServices.getText(null, "Clean_de_lineas"));
	}

	protected void addSpecificDesign() {

		this.addGroupOfLyrsCb = new JCheckBox();
		this.addGroupOfLyrsCb.setText(PluginServices.getText(this,
				"Añadir_al_TOC_geometrias_erroneas"));
		addComponent(addGroupOfLyrsCb, GridBagConstraints.NONE, new Insets(5,
				5, 5, 5));
		initSelectedItemsJCheckBox();
		updateNumSelectedFeaturesLabel();
	}

	protected void processLayerComboBoxStateChange(ItemEvent arg0) {
	}

	public boolean cleanOnlySelection() {
		return isFirstOnlySelected();
	}

	public boolean createLyrsWithErrorGeometries() {
		return addGroupOfLyrsCb.isSelected();
	}
}
