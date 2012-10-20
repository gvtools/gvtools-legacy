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
package org.gvsig.symbology.gui.styling.editortools;

import javax.swing.JComponent;

import org.gvsig.symbology.fmap.styles.PointLabelPositioneer;

import com.iver.andami.PluginServices;

/**
 * Implements an editor tool which can be used to select the position for the
 * label when the user is performing a layer of points. There will be 4
 * different precedence levels for the 8 different places where the text for the
 * specified point will be placed (see PointLabelPositioneer.java). This class
 * implements the forbidden one, but the user can select a normal, a low or a
 * high one.
 * 
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */

public class PointLabelForbiddenPrecedenceTool extends
		PointLabelHighPrecedenceTool {
	public PointLabelForbiddenPrecedenceTool(JComponent targetEditor) {
		super(targetEditor);
		buttonIcon = "set-forbidden-precedence-point-label-icon";
		precedenceValue = PointLabelPositioneer.FORBIDDEN;
		this.getButton().setToolTipText(
				PluginServices.getText(this, "set_forbidden_precedence"));
	}

	@Override
	public String getID() {
		return "4";
	}
}
