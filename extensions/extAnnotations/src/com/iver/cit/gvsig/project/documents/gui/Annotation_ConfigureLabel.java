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

package com.iver.cit.gvsig.project.documents.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.JComboBoxUnits;
import com.iver.cit.gvsig.project.Project;

/**
 * Configure annotation mapping.
 * 
 * @author Vicente Caballero Navarro
 */
public class Annotation_ConfigureLabel extends JWizardPanel {
	public static final String TEXT_FOR_DEFAULT_VALUE = "- Default -";
	private static final Rectangle lblDescriptionPosition = new Rectangle(4, 4,
			355, 60);
	private static final Rectangle lblStep1Position = new Rectangle(4, 90, 15,
			15);
	private static final Rectangle lblAnglePosition = new Rectangle(30, 90,
			355, 30);
	private static final Rectangle cmbAnglePosition = new Rectangle(30, 124,
			170, 18);
	private static final Rectangle lblStep2Position = new Rectangle(4, 150, 15,
			15);
	private static final Rectangle lblColorPosition = new Rectangle(30, 150,
			355, 30);
	private static final Rectangle cmbColorPosition = new Rectangle(30, 184,
			170, 18);
	private static final Rectangle lblStep3Position = new Rectangle(4, 210, 15,
			15);
	private static final Rectangle lblSizePosition = new Rectangle(30, 210,
			355, 30);
	private static final Rectangle cmbSizePosition = new Rectangle(30, 244,
			170, 18);
	private static final Rectangle lblSizeUnitsPosition = new Rectangle(204,
			244, 80, 15);
	private static final Rectangle cmbSizeUnitsPosition = new Rectangle(305,
			244, 80, 18);
	private static final Rectangle lblStep4Position = new Rectangle(4, 270, 15,
			15);
	private static final Rectangle lblFontPosition = new Rectangle(30, 270,
			355, 30);
	private static final Rectangle cmbFontPosition = new Rectangle(30, 304,
			170, 18);
	private Annotation_Layer layer;
	private HashMap fieldsNames = new HashMap();
	private JLabel lblDescription;
	private JLabel lblAngle;
	private JLabel lblColor;
	private JLabel lblSize;
	private JLabel lblSizeUnits;
	private JLabel lblFont;
	private JLabel lblStep1;
	private JLabel lblStep2;
	private JLabel lblStep3;
	private JLabel lblStep4;
	private JComboBox cmbAngle = null;
	private JComboBox cmbColor = null;
	private JComboBox cmbSize = null;
	private JComboBox cmbFont = null;
	private EventsListener eventsListener = new EventsListener();
	private SelectableDataSource sds;
	private JComboBoxUnits cmbUnits;

	public Annotation_ConfigureLabel(JWizardComponents arg0,
			Annotation_Layer layer) {
		super(arg0);
		this.layer = layer;

		try {
			if (layer.getRecordset() != null) {
				sds = layer.getRecordset();
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}

		this.initialize();
	}

	/**
	 * DOCUMENT ME!
	 */
	private void updateButtonsState() {
		try {
			if (getWizardComponents().getCurrentIndex() == 1) {
				setBackButtonEnabled(true);
				setNextButtonEnabled(false);
				setFinishButtonEnabled(checkIsOkPanelData());
			}
		} catch (Exception e) {
			NotificationManager.addError(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	protected boolean checkIsOkPanelData() {
		// if
		// (!((String)this.getCmbSize().getSelectedItem()).equals(Annotation_ConfigureLabel.TEXT_FOR_DEFAULT_VALUE))
		// {
		// return
		// (((String)this.getCmbSizeUnits().getSelectedItem()).trim().length() >
		// 0);
		// }
		return true;
	}

	/**
	 * DOCUMENT ME!
	 */
	protected void initialize() {
		this.setLayout(null);
		this.setSize(new Dimension(358, 263));
		this.addLabels();

		this.add(getCmbAngle(), null);
		this.add(getCmbColor(), null);
		this.add(getCmbSize(), null);

		this.add(getCmbUnits(), null);
		this.add(getCmbFont(), null);

		checkIsOkPanelData();
	}

	/**
	 * DOCUMENT ME!
	 */
	protected void addLabels() {
		this.lblDescription = new JLabel();
		this.lblStep1 = new JLabel();
		this.lblAngle = new JLabel();
		this.lblStep2 = new JLabel();
		this.lblColor = new JLabel();
		this.lblStep3 = new JLabel();
		this.lblSize = new JLabel();

		this.lblSizeUnits = new JLabel();
		this.lblStep4 = new JLabel();
		this.lblFont = new JLabel();

		this.lblDescription.setText(PluginServices.getText(this,
				"descripcion_de_configuracion_capa_de_anotaciones"));
		this.lblStep1.setText("1.");
		this.lblAngle.setText(PluginServices.getText(this,
				"seleccione_el_campo_angulo_de_la_capa_de_anotaciones"));
		this.lblStep2.setText("2.");
		this.lblColor.setText(PluginServices.getText(this,
				"seleccione_el_campo_color_de_la_capa_de_anotaciones"));
		this.lblStep3.setText("3.");
		this.lblSize.setText(PluginServices.getText(this,
				"seleccione_el_campo_tamano_de_la_capa_de_anotaciones"));

		this.lblSizeUnits.setText(PluginServices.getText(this, "en_unidades"));
		this.lblStep4.setText("4.");
		this.lblFont.setText(PluginServices.getText(this,
				"seleccione_el_campo_fuente_de_la_capa_de_anotaciones"));

		// TODO: Posicionar
		this.lblDescription.setBounds(lblDescriptionPosition);
		this.lblStep1.setBounds(lblStep1Position);
		this.lblAngle.setBounds(lblAnglePosition);
		this.lblStep2.setBounds(lblStep2Position);
		this.lblColor.setBounds(lblColorPosition);
		this.lblStep3.setBounds(lblStep3Position);
		this.lblSize.setBounds(lblSizePosition);

		this.lblSizeUnits.setBounds(lblSizeUnitsPosition);
		this.lblStep4.setBounds(lblStep4Position);
		this.lblFont.setBounds(lblFontPosition);

		this.add(lblDescription, null);
		this.add(lblStep1, null);
		this.add(lblAngle, null);
		this.add(lblStep2, null);
		this.add(lblColor, null);
		this.add(lblStep3, null);
		this.add(lblSize, null);

		this.add(lblSizeUnits, null);
		this.add(lblStep4, null);
		this.add(lblFont, null);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param cmb
	 *            DOCUMENT ME!
	 * @param types
	 *            DOCUMENT ME!
	 */
	private void fillFieldsNames(JComboBox cmb, int[] types) {
		cmb.addItem(Annotation_ConfigureLabel.TEXT_FOR_DEFAULT_VALUE);

		Collection names;

		if (types == null) {
			String[] allNames = (String[]) this.fieldsNames.get("ALL");

			if (allNames == null) {
				try {
					SelectableDataSource dataSource = this.layer.getRecordset();

					allNames = dataSource.getFieldNames();
					this.fieldsNames.put("ALL", allNames);
				} catch (ReadDriverException e) {
					NotificationManager.addError(e);
				}
			}

			for (int i = 0; i < allNames.length; i++) {
				cmb.addItem(allNames[i]);
			}

			return;
		}

		Integer typeKey;

		for (int i = 0; i < types.length; i++) {
			typeKey = new Integer(types[i]);

			if (!this.fieldsNames.containsKey(typeKey)) {
				names = this.getFieldsFromType(types[i]);
				this.fieldsNames.put(typeKey, names);
			} else {
				names = (Collection) this.fieldsNames.get(typeKey);
			}

			if (names != null) {
				Iterator name = names.iterator();

				while (name.hasNext()) {
					cmb.addItem(name.next());
				}
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param type
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	private Collection getFieldsFromType(int type) {
		ArrayList result = new ArrayList();

		try {
			SelectableDataSource dataSource = this.layer.getRecordset();
			int fieldCount = dataSource.getFieldCount();
			for (int i = 0; i < fieldCount; i++) {
				if (dataSource.getFieldType(i) == type) {
					result.add(dataSource.getFieldName(i));
				}
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}

		if (result.size() == 0) {
			return null;
		}

		return result;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	private JComboBox getCmbAngle() {
		if (this.cmbAngle == null) {
			this.cmbAngle = new JComboBox();
			this.cmbAngle.setEditable(false);
			this.cmbAngle.setBounds(cmbAnglePosition);
			this.fillFieldsNames(this.cmbAngle, new int[] { Types.INTEGER,
					Types.DOUBLE });
			this.cmbAngle.addItemListener(this.eventsListener);

			if ((sds != null) && (layer.getAnnotatonMapping() != null)) {
				int index = layer.getAnnotatonMapping().getColumnRotate();
				setSelectedItem(index, cmbAngle);
				updateButtonsState();
			}
		}

		return this.cmbAngle;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getAngleFieldName() {
		return (String) this.getCmbAngle().getSelectedItem();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	private JComboBox getCmbColor() {
		if (this.cmbColor == null) {
			this.cmbColor = new JComboBox();
			this.cmbColor.setEditable(false);
			this.cmbColor.setBounds(cmbColorPosition);
			this.fillFieldsNames(this.cmbColor, new int[] { Types.INTEGER });
			this.cmbColor.addItemListener(this.eventsListener);

			if ((sds != null) && (layer.getAnnotatonMapping() != null)) {
				int index = layer.getAnnotatonMapping().getColumnColor();
				setSelectedItem(index, cmbColor);
			}
		}

		return this.cmbColor;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param index
	 *            DOCUMENT ME!
	 * @param cmb
	 *            DOCUMENT ME!
	 */
	private void setSelectedItem(int index, JComboBox cmb) {
		if (sds != null) {
			String field = null;

			try {
				field = sds.getFieldName(index);
			} catch (ReadDriverException e) {
				NotificationManager.addError(e);
			}

			cmb.setSelectedItem(field);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getColorFieldName() {
		return (String) this.getCmbColor().getSelectedItem();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	private JComboBox getCmbSize() {
		if (this.cmbSize == null) {
			this.cmbSize = new JComboBox();
			this.cmbSize.setEditable(false);
			this.cmbSize.setBounds(cmbSizePosition);
			this.fillFieldsNames(this.cmbSize, new int[] { Types.INTEGER,
					Types.DOUBLE });
			this.cmbSize.addItemListener(this.eventsListener);

			if ((sds != null) && (layer.getAnnotatonMapping() != null)) {
				int index = layer.getAnnotatonMapping().getColumnHeight();
				setSelectedItem(index, cmbSize);
			}
		}

		return this.cmbSize;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getSizeFieldName() {
		return (String) this.getCmbSize().getSelectedItem();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public JComboBoxUnits getCmbUnits() {
		if (cmbUnits == null) {
			cmbUnits = new JComboBoxUnits();
			cmbUnits.setSelectedIndex(Project.getDefaultDistanceUnits());
			cmbUnits.setName("CMBUNITS");
		}

		return cmbUnits;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	// public boolean sizeUnitsInPixels() {
	// return (this.getCmbUnits().getSelectedUnitIndex() == 0);
	// }

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	private JComboBox getCmbFont() {
		if (this.cmbFont == null) {
			this.cmbFont = new JComboBox();
			this.cmbFont.setEditable(false);
			this.cmbFont.setBounds(cmbFontPosition);
			this.fillFieldsNames(this.cmbFont, new int[] { Types.VARCHAR,
					Types.LONGVARCHAR });
			this.cmbFont.addItemListener(this.eventsListener);

			if ((sds != null) && (layer.getAnnotatonMapping() != null)) {
				int index = layer.getAnnotatonMapping().getColumnTypeFont();
				setSelectedItem(index, cmbFont);
			}
		}

		return this.cmbFont;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getFontFieldName() {
		return (String) this.getCmbFont().getSelectedItem();
	}

	private class EventsListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			updateButtonsState();
		}
	}
}
