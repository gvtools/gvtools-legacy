package com.iver.cit.gvsig.project.documents.view.toc.actions;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.FiltroExtension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;

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

/**
 * Select features in active layers using a WHERE filter on attribute data.
 * 
 * @author Benjamin Ducke
 */
public class AttFilterTocMenuEntry extends AbstractTocContextMenuAction {
	protected SelectableDataSource dataSource = null;
	private String filterTitle;

	public String getGroup() {
		return "attTableTOCtools";
	}

	public int getGroupOrder() {
		return 1;
	}

	public int getOrder() {
		return 2;
	}

	public String getText() {
		return PluginServices.getText(this, "filtro");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		if (isTocItemBranch(item)) {
			FLayer lyr = getNodeLayer(item);
			if (lyr.isAvailable()) {
				return selectedItems.length == 1;
			}
		}
		return false;
	}

	public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
		if (isTocItemBranch(item)) {
			FLayer lyr = getNodeLayer(item);
			if ((lyr instanceof ClassifiableVectorial)) {
				if (!((lyr instanceof FLyrVect) && !((FLyrVect) lyr)
						.isPropertiesMenuVisible())) {
					if (!(lyr instanceof FLyrAnnotation))
						return true;
				}

			}
		}
		return false;

	}

	public void execute(ITocItem item, FLayer[] selectedItems) {

		/* simply call the Filter extension with the right action */
		FiltroExtension FE = null;
		FE = new FiltroExtension();
		FE.execute("FILTRO");

	}
}
