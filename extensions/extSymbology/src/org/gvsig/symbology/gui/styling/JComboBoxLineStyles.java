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

package org.gvsig.symbology.gui.styling;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import com.iver.cit.gvsig.fmap.core.styles.ILineStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.gui.styling.StylePreviewer;
import com.iver.utiles.swing.JComboBox;

/**
 * ComboBox that shows the different styles of line that can be selected by the
 * user in order to modify a symbol composed by lines .
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 * 
 */
public class JComboBoxLineStyles extends JComboBox {
	private static final long serialVersionUID = -4468684151503515142L;
	private static final int PREDEFINED_STYLE_COUNT = 6;
	private static final int LINE_WIDTH = 10;
	private static final int DOT_WIDTH = 2;
	private int endCap;
	private int lineJoin;
	private float miterlimit;
	private int width;

	public static float[][] dash = new float[PREDEFINED_STYLE_COUNT][];
	{

		dash[0] = new float[] { 1 }; // no dash, line

		dash[1] = new float[] { LINE_WIDTH }; // lines

		dash[2] = new float[] { DOT_WIDTH }; // dots

		dash[3] = new float[] { LINE_WIDTH, DOT_WIDTH }; // line + dot

		dash[4] = new float[] { LINE_WIDTH, DOT_WIDTH, DOT_WIDTH }; // line +
																	// dot + dot
		dash[5] = new float[] { LINE_WIDTH, LINE_WIDTH, DOT_WIDTH, DOT_WIDTH }; // line
																				// +
																				// line
																				// +
																				// dot
																				// +
																				// dot
	}

	{
		BasicStroke dummy = new BasicStroke();
		endCap = dummy.getEndCap();
		lineJoin = dummy.getLineJoin();
		miterlimit = dummy.getMiterLimit();
	}

	ILineStyle[] styles = new ILineStyle[PREDEFINED_STYLE_COUNT];

	private ListCellRenderer renderer = new ListCellRenderer() {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			SimpleLineStyle style = null;
			if (value instanceof float[]) {
				float[] d = (float[]) value;
				style = new SimpleLineStyle(width, endCap, lineJoin,
						miterlimit, d, 0);
			} else if (value instanceof SimpleLineStyle) {
				style = (SimpleLineStyle) value;
			}
			StylePreviewer sp = new StylePreviewer();
			Dimension sz = new Dimension(80, 20);
			sp.setSize(50, 25);
			sp.setPreferredSize(sz);
			sp.setStyle(style);
			sp.setForeground(UIManager
					.getColor(isSelected ? "ComboBox.selectionForeground"
							: "ComboBox.foreground"));
			sp.setBackground(UIManager
					.getColor(isSelected ? "ComboBox.selectionBackground"
							: "ComboBox.background"));
			return sp;
		};
	};

	private void refreshStyles() {
		for (int i = 0; i < styles.length; i++) {
			styles[i] = new SimpleLineStyle(width, endCap, lineJoin,
					miterlimit, dash[i], width * 5);
		}

		removeAllItems();
		for (int i = 0; i < styles.length; i++) {
			addItem(styles[i]);
		}
	}

	/**
	 * Constructor method
	 * 
	 */
	public JComboBoxLineStyles() {
		super();
		initialize();

	}

	/**
	 * Initializes this
	 * 
	 */
	private void initialize() {
		removeAllItems();
		for (int i = 0; i < dash.length; i++) {
			addItem(dash[i]);
		}
		setEditable(false);
		setRenderer(renderer);
		refreshStyles();
	}

	/**
	 * Sets the width of the line
	 * 
	 * @param width
	 */
	public void setLineWidth(int width) {
		this.width = width;
		refreshStyles();
	}

}
