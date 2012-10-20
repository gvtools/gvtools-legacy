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
package org.gvsig.symbology.gui.styling.editortools;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.StyleEditor;

public class LabelStyleRemoveLastTextField extends EditorTool {

	public LabelStyleRemoveLastTextField(JComponent targetEditor) {
		super(targetEditor);
	}

	private ILabelStyle style;
	private JButton btnTextField;

	@Override
	public AbstractButton getButton() {
		return getBtnRemoveTextField();
	}

	private JButton getBtnRemoveTextField() {
		if (btnTextField == null) {
			btnTextField = new JButton(PluginServices.getIconTheme().get(
					"remove-text-icon"));
			btnTextField.setSize(EditorTool.SMALL_BTN_SIZE);
			btnTextField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int fCount = style.getFieldCount();
					if (fCount > 0)
						style.deleteTextFieldArea(style.getFieldCount() - 1);
					((StyleEditor) owner).restorePreviousTool();
					owner.repaint();
				}
			});
		}

		return btnTextField;
	}

	@Override
	public Cursor getCursor() {
		return Cursor.getDefaultCursor();
	}

	@Override
	public String getID() {
		return "1";
	}

	@Override
	public boolean isSuitableFor(Object obj) {
		return obj instanceof ILabelStyle;
	}

	@Override
	public void setModel(Object objectToBeEdited) {
		style = (ILabelStyle) objectToBeEdited;
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

}
