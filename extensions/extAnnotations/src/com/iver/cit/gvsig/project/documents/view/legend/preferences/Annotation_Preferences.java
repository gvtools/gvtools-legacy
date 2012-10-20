/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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

package com.iver.cit.gvsig.project.documents.view.legend.preferences;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.layers.Annotation_Mapping;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.cit.gvsig.project.documents.view.gui.FontOptions;
import com.iver.cit.gvsig.project.documents.view.tool.gui.TextPropertiesPanel;
import com.iver.utiles.swing.JComboBox;

/**
 * Default configuration page. <b><b> Here the user can establish what settings
 * wants to use by default annotations.
 * 
 * 
 * @author Vicente Caballero Navarro
 */
public class Annotation_Preferences extends AbstractPreferencePage {

	protected String id;
	private ImageIcon icon;
	private JTextField txtDefaultText;
	private JComboBox cmbDefaultTypeFont;
	private JComboBox cmbDefaultStyleFont;
	private JTextField txtDefaultRotate;
	private JTextField txtDefaultHeight;
	private ColorChooserPanel jccDefaultColor;
	private TextPropertiesPanel panel = null;

	/**
	 * Creates a new panel containing View preferences settings.
	 * 
	 */
	public Annotation_Preferences() {
		super();
		id = this.getClass().getName();
	}

	public void initializeValues() {
		getPanel(); // init UI
		txtDefaultText.setText(Annotation_Mapping.DEFAULTTEXT);
		panel.setFontType(Annotation_Mapping.DEFAULTTYPEFONT);
		panel.setFontStyle(Annotation_Mapping.DEFAULTSTYLEFONT);
		panel.setTextHeight(Annotation_Mapping.DEFAULTHEIGHT);
		Color color = new Color(Annotation_Mapping.DEFAULTCOLOR);
		panel.setColor(color);
		panel.setRotation(Annotation_Mapping.DEFAULTROTATE);
	}

	public String getID() {
		return id;
	}

	public String getTitle() {
		return PluginServices.getText(this, "annotation_preferences");
	}

	public JPanel getPanel() {
		if (panel == null) {
			addComponent(new JLabel(PluginServices.getText(this, "text")),
					txtDefaultText = new JTextField(), GridBagConstraints.BOTH,
					new Insets(4, 0, 4, 8));
			panel = new TextPropertiesPanel();
			addComponent(panel);
		}
		return this;
	}

	public void storeValues() throws StoreException {
		Color fontColor = panel.getColor();
		String text = txtDefaultText.getText();
		String fontType = panel.getFontType();
		int fontStyle = panel.getFontStyle();
		int fontHeight = (int) panel.getTextHeight();
		int fontRotate = (int) panel.getRotation();
		Annotation_Mapping.storeValues(fontColor, text, fontType, fontStyle,
				fontHeight, fontRotate);
	}

	public void initializeDefaults() {
		String fontType = FontOptions.ARIAL;
		int fontStyle = Font.PLAIN;
		int fontHeight = 10;
		Color fontColor = Color.black;
		int fontRotate = 0;
		String text = "";

		panel.setFontType(fontType);
		panel.setFontStyle(fontStyle);
		panel.setTextHeight(fontHeight);
		panel.setColor(fontColor);
		panel.setRotation(fontRotate);
		txtDefaultText.setText(text);

		Annotation_Mapping.DEFAULTCOLOR = fontColor.getRGB();
		Annotation_Mapping.DEFAULTTEXT = text;
		Annotation_Mapping.DEFAULTTYPEFONT = fontType;
		Annotation_Mapping.DEFAULTSTYLEFONT = fontStyle;
		Annotation_Mapping.DEFAULTHEIGHT = fontHeight;
		Annotation_Mapping.DEFAULTROTATE = fontRotate;
	}

	public ImageIcon getIcon() {
		if (icon == null)
			icon = PluginServices.getIconTheme().get("annotation-properties");
		return icon;
	}

	public boolean isValueChanged() {
		return super.hasChanged();
	}

	public void setChangesApplied() {
		setChanged(false);
	}
}
