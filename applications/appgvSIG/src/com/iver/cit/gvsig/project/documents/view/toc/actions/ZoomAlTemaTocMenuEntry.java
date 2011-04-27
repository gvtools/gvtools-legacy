package com.iver.cit.gvsig.project.documents.view.toc.actions;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.Project;
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
/* CVS MESSAGES:
 *
 * $Id: ZoomAlTemaTocMenuEntry.java 13869 2007-09-19 16:00:32Z jaume $
 * $Log$
 * Revision 1.6  2007-09-19 15:52:16  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.5  2007/03/06 16:37:08  caballero
 * Exceptions
 *
 * Revision 1.4  2007/01/04 07:24:31  caballero
 * isModified
 *
 * Revision 1.3  2006/10/18 16:01:13  sbayarri
 * Added zoomToExtent method to MapContext.
 *
 * Revision 1.2  2006/10/02 13:52:34  jaume
 * organize impots
 *
 * Revision 1.1  2006/09/15 10:41:30  caballero
 * extensibilidad de documentos
 *
 * Revision 1.1  2006/09/12 15:58:14  jorpiell
 * "Sacadas" las opcines del menú de FPopupMenu
 *
 *
 */
public class ZoomAlTemaTocMenuEntry extends AbstractTocContextMenuAction {
	public String getGroup() {
		return "group2"; //FIXME
	}

	public int getGroupOrder() {
		return 20;
	}

	public int getOrder() {
		return 1;
	}

	public String getText() {
		return PluginServices.getText(this, "Zoom_a_la_capa");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		return true;
	}

	public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
		if (isTocItemBranch(item) && ! (selectedItems == null || selectedItems.length <= 0)) {
			return true;
		}
		return false;

	}


	public void execute(ITocItem item, FLayer[] selectedItems) {


		// 050209, jmorell: Para que haga un zoom a un grupo de capas seleccionadas.

		if (selectedItems.length==1) {
	        try {
	        	if (!selectedItems[0].isAvailable()) return;
	        	getMapContext().zoomToExtent(selectedItems[0].getFullExtent());
			} catch (ReadDriverException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				Rectangle2D maxExtent = setMaxExtent(selectedItems);
				getMapContext().zoomToExtent(maxExtent);
			} catch (ReadDriverException e1) {
				e1.printStackTrace();
			}
		}
		Project project=((ProjectExtension)PluginServices.getExtension(ProjectExtension.class)).getProject();
		project.setModified(true);
	}

	private Rectangle2D setMaxExtent(FLayer[] actives)
			throws ReadDriverException {
		Rectangle2D extRef = null;
		try {

			extRef = actives[0].getFullExtent();

			double minXRef = extRef.getMinX();
			double maxYRef = extRef.getMaxY();
			double maxXRef = extRef.getMaxX();
			double minYRef = extRef.getMinY();
			for (int i = 0; i < actives.length; i++) {
				if (actives[i].isAvailable()) {
					Rectangle2D extVar = actives[i].getFullExtent();
					double minXVar = extVar.getMinX();
					double maxYVar = extVar.getMaxY();
					double maxXVar = extVar.getMaxX();
					double minYVar = extVar.getMinY();
					if (minXVar <= minXRef)
						minXRef = minXVar;
					if (maxYVar >= maxYRef)
						maxYRef = maxYVar;
					if (maxXVar >= maxXRef)
						maxXRef = maxXVar;
					if (minYVar <= minYRef)
						minYRef = minYVar;
					extRef.setRect(minXRef, minYRef, maxXRef - minXRef, maxYRef
							- minYRef);
				}
			}
		} catch (ExpansionFileReadException e) {
			e.printStackTrace();
		}
		return extRef;
	}
}
