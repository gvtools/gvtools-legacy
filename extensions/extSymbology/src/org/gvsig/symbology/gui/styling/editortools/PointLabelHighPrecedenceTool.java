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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.gvsig.symbology.fmap.styles.PointLabelPositioneer;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.StyleEditor;

/**
 * Implements an editor tool which can be used to select the position for the
 * label when the user is performing a layer of points. There will be 4
 * different precedence levels for the 8 different places where the text for the
 * specified point will be placed (see PointLabelPositioneer.java). This class
 * implements the highest one, but the user can select a normal, a low or a
 * forbidden one.
 * 
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 * 
 */
public class PointLabelHighPrecedenceTool extends EditorTool {
	protected String buttonIcon = "set-high-precedence-point-label-icon";
	protected byte precedenceValue = PointLabelPositioneer.PREFERENCE_HIGH;
	private final Cursor cursor = Cursor.getDefaultCursor();

	private JToggleButton btnNewPrecedence;

	private PointLabelPositioneer positioneer;

	/**
	 * Constructor
	 * 
	 * @param targetEditor
	 */
	public PointLabelHighPrecedenceTool(JComponent targetEditor) {
		super(targetEditor);
	}

	/**
	 * Implements the button for the tool
	 * 
	 * @return
	 */
	private JToggleButton getBtnNewPrecedence() {
		if (btnNewPrecedence == null) {
			btnNewPrecedence = new JToggleButton();
			btnNewPrecedence.setSize(EditorTool.SMALL_BTN_SIZE);
			btnNewPrecedence.setIcon(getIconButton());
			btnNewPrecedence.setToolTipText(PluginServices.getText(this,
					"set_high_precedence"));

		}
		return btnNewPrecedence;
	}

	/**
	 * Obtains the image for the button of the tool
	 * 
	 * @return
	 */
	protected ImageIcon getIconButton() {
		return PluginServices.getIconTheme().get(buttonIcon);
	}

	@Override
	public AbstractButton getButton() {
		return getBtnNewPrecedence();
	}

	@Override
	public Cursor getCursor() {
		return cursor;
	}

	@Override
	public String getID() {
		return "1";
	}

	@Override
	public boolean isSuitableFor(Object obj) {
		return obj instanceof PointLabelPositioneer;
	}

	@Override
	public void setModel(Object objectToBeEdited) {
		positioneer = (PointLabelPositioneer) objectToBeEdited;
	}

	/**
	 * Obtains the position
	 * 
	 * @param p
	 * @return
	 */
	private int getPositionerCellIndex(Point p) {
		Dimension dim = ((StyleEditor) owner).getStylePreviewer().getSize();
		int size = Math.min(dim.width, dim.height);
		if (p.getX() > size || p.getY() > size)
			return -1;
		int cellSize = size / 3;
		int row = (int) p.getY() / cellSize;
		int col = (int) p.getX() / cellSize;
		int arrayPosition = (3 * row) + col;
		if (arrayPosition == 4)
			return -1;
		if (arrayPosition > 4)
			return arrayPosition - 1;
		return arrayPosition;

	}

	public void mousePressed(MouseEvent e) {
		byte[] pv = positioneer.getPreferenceVector();
		int cellIndex = getPositionerCellIndex(e.getPoint());
		if (cellIndex != -1) {
			pv[cellIndex] = precedenceValue;
		}
		owner.repaint();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}
}
