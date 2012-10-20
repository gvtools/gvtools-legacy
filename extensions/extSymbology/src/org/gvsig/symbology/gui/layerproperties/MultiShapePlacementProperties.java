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
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.gvsig.symbology.fmap.labeling.PlacementManager;
import org.gvsig.symbology.fmap.labeling.placements.MultiShapePlacementConstraints;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;

public class MultiShapePlacementProperties extends JPanel implements
		IPlacementProperties {
	private static final long serialVersionUID = 2935114466845029008L;
	private PlacementProperties pointProperties;
	private PlacementProperties lineProperties;
	private PlacementProperties polygonProperties;
	private DuplicateLayersMode dupMode;

	public MultiShapePlacementProperties(
			MultiShapePlacementConstraints constraints)
			throws ReadDriverException {
		constraints = (MultiShapePlacementConstraints) ((constraints != null) ? PlacementManager
				.createPlacementConstraints(constraints.getXMLEntity())
				: PlacementManager.createPlacementConstraints(FShape.MULTI));

		this.pointProperties = new PlacementProperties(
				constraints.getPointConstraints(), FShape.POINT,
				getDuplicatesMode());
		this.lineProperties = new PlacementProperties(
				constraints.getLineConstraints(), FShape.LINE,
				getDuplicatesMode());
		this.polygonProperties = new PlacementProperties(
				constraints.getPolygonConstraints(), FShape.POLYGON,
				getDuplicatesMode());
		initialize();
	}

	private DuplicateLayersMode getDuplicatesMode() {
		if (dupMode == null) {
			dupMode = new DuplicateLayersMode();
		}
		return dupMode;
	}

	private void initialize() {
		setLayout(new BorderLayout());
		JPanel aux = new JPanel(new BorderLayout());
		JTabbedPane p = new JTabbedPane();
		p.addTab(PluginServices.getText(this, "points"), pointProperties);
		p.addTab(PluginServices.getText(this, "lines"), lineProperties);
		p.addTab(PluginServices.getText(this, "polygon"), polygonProperties);
		setLayout(new BorderLayout());
		aux.add(p, BorderLayout.CENTER);
		aux.add(getDuplicatesMode(), BorderLayout.SOUTH);
		add(aux, BorderLayout.CENTER);
	}

	public IPlacementConstraints getPlacementConstraints() {
		return new MultiShapePlacementConstraints(
				pointProperties.getPlacementConstraints(),
				lineProperties.getPlacementConstraints(),
				polygonProperties.getPlacementConstraints());

	}

	public WindowInfo getWindowInfo() {
		return pointProperties.getWindowInfo();
	}

	public Object getWindowProfile() {
		return pointProperties.getWindowProfile();
	}

	public void actionPerformed(ActionEvent e) {
		boolean okPressed = "OK".equals(e.getActionCommand());
		boolean cancelPressed = "CANCEL".equals(e.getActionCommand());
		if (okPressed || cancelPressed) {
			if (okPressed) {
				pointProperties.applyConstraints();
				lineProperties.applyConstraints();
				polygonProperties.applyConstraints();
			}

			if ("CANCEL".equals(e.getActionCommand())) {
				pointProperties.constraints = pointProperties.oldConstraints;
				lineProperties.constraints = lineProperties.oldConstraints;
				polygonProperties.constraints = polygonProperties.oldConstraints;
			}

			PluginServices.getMDIManager().closeWindow(this);

			return;
		}
	}
}
