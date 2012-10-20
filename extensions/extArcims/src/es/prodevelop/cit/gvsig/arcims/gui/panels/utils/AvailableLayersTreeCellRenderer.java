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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * This class overrides the jtree cell renderer to allow a different tool tip
 * for each node.
 * 
 * @author jldominguez
 * 
 */
public class AvailableLayersTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 0;

	/**
	 * Colors used to indicate whether a node has been added or not. This has a
	 * sense in the FeatureServicePanel:
	 * 
	 * @see es.prodevelop.cit.gvsig.arcims.gui.panels.FeatureServicePanel
	 */
	private Color addedLeafForeground = Color.LIGHT_GRAY;
	private Color notAddedLeafForeground = Color.BLACK;

	/**
	 * This method sets the specific tool tip for each node and the root node
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Component c = super.getTreeCellRendererComponent(tree, value, selected,
				expanded, leaf, row, hasFocus);
		JComponent jc;

		DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) value;
		Object innerObj = dmtn.getUserObject();

		if (innerObj instanceof LayersListElement) {
			// tree nodes, with the LayersListElement's tool tip
			LayersListElement lle = (LayersListElement) dmtn.getUserObject();
			c.setForeground(this.getLeafForegroundColor(leaf, lle.isAdded(),
					selected));
			jc = (JComponent) c;
			jc.setToolTipText(lle.toolTipText());
		}

		if (innerObj instanceof ServiceNamesObject) {
			// tree root node, with the ServiceNamesObject's tool tip
			ServiceNamesObject sno = (ServiceNamesObject) dmtn.getUserObject();
			jc = (JComponent) c;
			jc.setToolTipText(sno.toolTipText());
		}

		return c;
	}

	/**
	 * Gets the tree node's font color.
	 * 
	 * @param lf
	 *            whether it is a leaf or not.
	 * @param added
	 *            whether the node has been added or not.
	 * @param sel
	 *            whether the node is selected or not.
	 * @return the node's font color.
	 */
	private Color getLeafForegroundColor(boolean lf, boolean added, boolean sel) {
		if (!lf) {
			return Color.BLACK;
		}

		if (sel) {
			return Color.WHITE;
		}

		if (added) {
			return addedLeafForeground;
		} else {
			return notAddedLeafForeground;
		}
	}
}
