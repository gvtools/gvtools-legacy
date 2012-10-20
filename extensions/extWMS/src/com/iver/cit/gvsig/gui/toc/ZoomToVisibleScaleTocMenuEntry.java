/* gvSIG. Sistema de Informaci?n Geogr?fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib??ez, 50
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
package com.iver.cit.gvsig.gui.toc;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrWMS;
import com.iver.cit.gvsig.project.documents.view.toc.TocMenuEntry;
import com.iver.cit.gvsig.project.documents.view.toc.gui.FPopupMenu;

public class ZoomToVisibleScaleTocMenuEntry extends TocMenuEntry {
	private JMenuItem propsMenuItem;
	FLayer lyr = null;

	public void initialize(FPopupMenu m) {
		super.initialize(m);

		if (isTocItemBranch()) {
			lyr = getNodeLayer();
			if ((lyr instanceof FLyrWMS)) {
				propsMenuItem = new JMenuItem(PluginServices.getText(this,
						"zoom_to_visible_scale"));
				getMenu().add(propsMenuItem);
				propsMenuItem.setFont(FPopupMenu.theFont);
				getMenu().setEnabled(true);
				propsMenuItem.addActionListener(this);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		lyr = getNodeLayer();
		if (lyr.isAvailable()) {
			if ((lyr instanceof FLyrWMS)) {
				FLyrWMS wmsLayer = (FLyrWMS) lyr;
				MapContext mp = getMapContext();
				double mapScale = (double) mp.getScaleView();
				double correctedMinScale = wmsLayer
						.getCorrectedServerMinScale();
				double correctedMaxScale = wmsLayer
						.getCorrectedServerMaxScale();

				if (correctedMinScale > 0 // -1 and 0 are invalid values for
											// scale
						&& mapScale < correctedMinScale) {
					mp.setScaleView((long) correctedMinScale);
				} else if (correctedMaxScale > 0
						&& mapScale >= correctedMaxScale) {
					/**
					 * As the WMS 1.3 standard defines the MAX DENOMINATOR SCALE
					 * as a non-inclusive interval limit, we have to subtract a
					 * constant to MAX_SCALE in order to move to the "safe side"
					 * of the limit.
					 */
					mp.setScaleView((long) correctedMaxScale - 1);
				}
			}
		}
	}

}
