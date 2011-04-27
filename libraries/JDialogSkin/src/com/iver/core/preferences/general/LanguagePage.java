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
package com.iver.core.preferences.general;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.iver.andami.Launcher;
import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;

public class LanguagePage extends AbstractPreferencePage {
	private static LanguageItem DEFAULT_LANGUAGE;
	private ImageIcon icon;

	private String id;

	private JPanel pN = null;

	private JPanel pC = null;

	private JPanel jPanel = null;

	private JComboBox cmbIdioma = null;

	private int langIndex;
	private boolean changed = false;

	public LanguagePage() {
		super();
		initialize();
		id = this.getClass().getName();
		setParentID(GeneralPage.class.getName());
	}

	private void initialize() {
		icon = new ImageIcon(this.getClass().getClassLoader().getResource(
				"images/babel.png"));
		this.setLayout(new BorderLayout());
		this.setSize(new java.awt.Dimension(386, 177));
		this.add(getPN(), java.awt.BorderLayout.NORTH);
		this.add(getPC(), java.awt.BorderLayout.CENTER);
		langIndex = getJComboBox().getSelectedIndex();
	}

	public String getID() {
		return id;
	}

	public String getTitle() {
		return PluginServices.getText(this, "idioma");
	}

	public JPanel getPanel() {
		return this;
	}

	public void initializeValues() {
	}

	private class LanguageItem {
		public Locale locale;

		public String description;

		public LanguageItem(Locale loc, String str) {
			locale = loc;
			description = str;
		}

		public String toString() {
			return description;
		}
	}

	public void storeValues() {
		// Se escribe el idioma
		LanguageItem sel = (LanguageItem) cmbIdioma.getSelectedItem();
		Launcher.getAndamiConfig().setLocaleLanguage(sel.locale.getLanguage());
		Launcher.getAndamiConfig().setLocaleCountry(sel.locale.getCountry());
		Launcher.getAndamiConfig().setLocaleVariant(sel.locale.getVariant());
		langIndex = getJComboBox().getSelectedIndex();

	}

	public void initializeDefaults() {
		getJComboBox().setSelectedItem(DEFAULT_LANGUAGE);
	}

	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * This method initializes pN
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPN() {
		if (pN == null) {
			pN = new JPanel();
		}
		return pN;
	}

	/**
	 * This method initializes pC
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPC() {
		if (pC == null) {
			pC = new JPanel();
			pC.add(getJPanel(), null);
		}
		return pC;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					PluginServices.getText(this,"idioma"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			jPanel.add(getJComboBox(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jComboBox
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox() {
		if (cmbIdioma == null) {
			cmbIdioma = new JComboBox();
			cmbIdioma.setPreferredSize(new java.awt.Dimension(195, 20));

			Locale esp = new Locale("es");
			Locale eng = new Locale("en");
			Locale fra = new Locale("fr");
			Locale ita = new Locale("it");
			Locale val = new Locale("ca");
			Locale cs = new Locale("cs"); // Checo
			Locale eu = new Locale("eu"); // euskera
			Locale brasil = new Locale("pt", "BR");
			Locale de = new Locale("de"); // Alemán
			Locale gr = new Locale("el", "GR"); // Griego
			Locale gl = new Locale("gl", "GL"); // Griego
			Locale zh = new Locale("zh", "ZH"); // Chino

			// Default language

			// Parche para valenciano/catalán valencià/català
			String strValenciano = PluginServices.getText(this, "__catalan");
			// Parche para euskera

			Locale localeActual = Locale.getDefault(); // Se configura en la
														// clase Launcher
			String strEuskera;
			if (eu.getDisplayLanguage().compareTo("vascuence") == 0)
				strEuskera = "Euskera";
			else
				strEuskera = eu.getDisplayLanguage();

			// English as default language
			DEFAULT_LANGUAGE = new LanguageItem(eng, eng.getDisplayLanguage());

			LanguageItem[] lenguajes = new LanguageItem[] {
					new LanguageItem(esp, esp.getDisplayLanguage()),
					DEFAULT_LANGUAGE,
					new LanguageItem(fra, fra.getDisplayLanguage()),
					new LanguageItem(ita, ita.getDisplayLanguage()),
					new LanguageItem(val, strValenciano),
					new LanguageItem(cs, cs.getDisplayLanguage()),
					new LanguageItem(eu, strEuskera),
					new LanguageItem(brasil, brasil.getDisplayLanguage()),
					new LanguageItem(de, de.getDisplayLanguage()),
					new LanguageItem(gr, gr.getDisplayLanguage()),
					new LanguageItem(gl, gl.getDisplayLanguage()),
					new LanguageItem(zh, zh.getDisplayLanguage())};

			DefaultComboBoxModel model = new DefaultComboBoxModel(lenguajes);

			for (int i = 0; i < lenguajes.length; i++) {
				if (lenguajes[i].locale.equals(Locale.getDefault())) {
					model.setSelectedItem(lenguajes[i]);
				}
			}

			cmbIdioma.setModel(model);
			cmbIdioma.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changed = true;
				}
			});
		}
		return cmbIdioma;
	}

	public boolean isValueChanged() {
		return changed;
	}

	public void setChangesApplied() {
		changed = false;

	}
} // @jve:decl-index=0:visual-constraint="10,10"
