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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

import org.gvsig.symbology.fmap.styles.SimpleLabelStyle;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.fmap.core.styles.IStyle;
import com.iver.cit.gvsig.gui.styling.EditorTool;
import com.iver.cit.gvsig.gui.styling.StyleEditor;
import com.iver.cit.gvsig.gui.styling.StylePreviewer;

public class LabelStyleNewTextFieldTool extends EditorTool {
	private Point pIni, pEnd;
	private final Cursor cursor = Cursor.getDefaultCursor();
	private ILabelStyle style;
	private JToggleButton btnNewTextArea;

	public LabelStyleNewTextFieldTool(JComponent targetEditor) {
		super(targetEditor);
		// TODO Auto-generated constructor stub
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void mousePressed(MouseEvent e) {
		pIni = e.getPoint();
		pEnd = e.getPoint();
		Rectangle2D rect = screenPointsToLabelRect(pIni, pEnd);
		style.addTextFieldArea(rect);
	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		Dimension bounds = ((StyleEditor) owner).getStylePreviewer().getSize();
		pEnd = e.getPoint();

		if (pEnd.getX() < 0)
			pEnd.x = 0;
		if (pEnd.getX() > bounds.getWidth())
			pEnd.x = (int) bounds.getWidth();

		if (pEnd.getY() < 0)
			pEnd.y = 0;
		if (pEnd.getY() > bounds.getHeight())
			pEnd.y = (int) bounds.getHeight();

		Rectangle2D rect = screenPointsToLabelRect(pIni, pEnd);
		style.setTextFieldArea(style.getFieldCount() - 1, rect);
		owner.repaint();
	}

	private Rectangle2D screenPointsToLabelRect(Point pIni, Point pEnd) {
		int minx = pIni.x;
		int miny = pIni.y;

		int width = pEnd.x - pIni.x;
		int height = pEnd.y - pIni.y;
		if (width < 0) {
			minx += width;
			width = -width;
		}
		if (height < 0) {
			miny += height;
			height = -height;
		}

		StylePreviewer sp = ((StyleEditor) owner).getStylePreviewer();
		Dimension bounds = sp.getSize();

		IStyle style = sp.getStyle();
		Dimension backgroundBounds = null;
		if (style instanceof SimpleLabelStyle) {
			backgroundBounds = ((SimpleLabelStyle) style).getSize();
		}
		// FIXME: Esto es un parche, habría que cambiar la API de los estilos y
		// simbolos
		// pero mientras tanto
		Rectangle2D rect;
		if (backgroundBounds == null) {
			rect = new Rectangle2D.Double(minx
					/ (bounds.getWidth() - sp.getHGap() / 2), // OJO, aquí ponía
																// cuatro 10's a
																// piñon fijo
					miny / (bounds.getHeight() - sp.getVGap() / 2), width
							/ (bounds.getWidth() - sp.getHGap()), height
							/ (bounds.getHeight() - sp.getVGap()));
		} else {

			double xOffset = 0;
			double yOffset = 0;
			double scale = 1;
			if (backgroundBounds.getWidth() > backgroundBounds.getHeight()) {
				scale = (bounds.getWidth() - sp.getHGap())
						/ backgroundBounds.getWidth();
				yOffset = 0.5 * (bounds.getHeight() - sp.getVGap() - backgroundBounds
						.getHeight() * scale);
			} else {
				scale = (bounds.getHeight() - sp.getVGap())
						/ backgroundBounds.getHeight();
				xOffset = 0.5 * (bounds.getWidth() - sp.getHGap() - backgroundBounds
						.getWidth() * scale);
			}

			rect = new Rectangle2D.Double(
					((minx - (sp.getHGap() / 2) - xOffset) / scale)
							/ backgroundBounds.getWidth(), ((miny
							- (sp.getVGap() / 2) - yOffset) / scale)
							/ backgroundBounds.getHeight(), (width / scale)
							/ backgroundBounds.getWidth(), (height / scale)
							/ backgroundBounds.getHeight());
		}

		return rect;
	}

	public AbstractButton getButton() {
		return getBtnNewTextArea();
	}

	@Override
	public String getID() {
		return "2";
	}

	@Override
	public boolean isSuitableFor(Object obj) {
		return obj instanceof ILabelStyle;
	}

	@Override
	public void setModel(Object objectToBeEdited) {
		style = (ILabelStyle) objectToBeEdited;
	}

	private JToggleButton getBtnNewTextArea() {
		if (btnNewTextArea == null) {
			btnNewTextArea = new JToggleButton(PluginServices.getIconTheme()
					.get("add-text-icon"));
			btnNewTextArea.setToolTipText(PluginServices.getText(this,
					"add_text_area"));
			btnNewTextArea.setSize(EditorTool.SMALL_BTN_SIZE);
		}
		return btnNewTextArea;
	}
}
