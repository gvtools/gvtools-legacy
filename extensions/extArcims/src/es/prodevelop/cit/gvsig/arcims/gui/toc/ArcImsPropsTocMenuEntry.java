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
package es.prodevelop.cit.gvsig.arcims.gui.toc;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.toc.TocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.gui.FPopupMenu;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FRasterLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.gui.dialogs.ArcImsPropsDialog;

/**
 * This class implements the layer properties menu item that is added to the
 * ArcIMS layer's popup menu.
 * 
 * @author jldominguez
 */
public class ArcImsPropsTocMenuEntry extends TocMenuEntry {
	private JMenuItem propsMenuItem;
	FLayer lyr = null;
	private ArcImsPropsDialog propsDialog;

	public void initialize(FPopupMenu m) {
		super.initialize(m);

		if (isTocItemBranch()) {
			lyr = getNodeLayer();

			// ArcIMS layer
			if ((lyr instanceof FRasterLyrArcIMS)) {
				propsMenuItem = new JMenuItem(PluginServices.getText(this,
						"arcims_properties"));
				getMenu().add(propsMenuItem);
				propsMenuItem.setFont(FPopupMenu.theFont);
				getMenu().setEnabled(true);
				propsMenuItem.addActionListener(this);
			}

			if ((lyr instanceof FFeatureLyrArcIMS)) {
				propsMenuItem = new JMenuItem(PluginServices.getText(this,
						"arcims_properties"));
				getMenu().add(propsMenuItem);
				propsMenuItem.setFont(FPopupMenu.theFont);
				getMenu().setEnabled(true);
				propsMenuItem.addActionListener(this);
			}
		}
	}

	/**
	 * Creates an ArcImsPropsDialog object and adds it to the MDIManager.
	 */
	public void actionPerformed(ActionEvent e) {
		lyr = getNodeLayer();

		if (lyr instanceof FRasterLyrArcIMS) {
			propsDialog = new ArcImsPropsDialog((FRasterLyrArcIMS) lyr);
			PluginServices.getMDIManager().addWindow(propsDialog);
		}

		if (lyr instanceof FFeatureLyrArcIMS) {
			propsDialog = new ArcImsPropsDialog((FFeatureLyrArcIMS) lyr);
			PluginServices.getMDIManager().addWindow(propsDialog);
		}
	}
}
