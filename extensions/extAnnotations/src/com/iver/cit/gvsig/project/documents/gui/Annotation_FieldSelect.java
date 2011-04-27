
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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;


/**
 * Panel to select the text field.
 *
 * @author Vicente Caballero Navarro
 */
public class Annotation_FieldSelect extends JWizardPanel {
    private static final Rectangle lblDescriptionPosition = new Rectangle(4, 14,
            355, 100);

    private static final Rectangle lblDuplicatePosition = new Rectangle(30, 115,
            355, 30);
    private static final Rectangle cmbDuplicatePosition = new Rectangle(30,
            150, 170, 18);
    private static final Rectangle lblStep2Position = new Rectangle(4, 170, 15,
            12);
    private static final Rectangle lblFieldPosition = new Rectangle(30, 170,
            355, 30);
    private static final Rectangle cmbFieldPosition = new Rectangle(30, 204,
            170, 18);
    private Annotation_Layer layer;
    private JLabel lblDescription = null;

    private JLabel lblStep2 = null;
    private JLabel lblField = null;

    private JComboBox cmbField = null;
    private EventsListener eventsListener = new EventsListener();
    private JComboBox cmbDuplicate;
    private JLabel lblDuplicate = null;

    public Annotation_FieldSelect(JWizardComponents arg0, Annotation_Layer layer) {
        super(arg0);
        this.layer = layer;
        this.initialize();
    }

    /**
     * DOCUMENT ME!
     */
    private void updateButtonsState() {
        try {
            if (getWizardComponents().getCurrentIndex() == 0) {
                setBackButtonEnabled(false);

                boolean enabled = checkIsOkPanelData();
                setNextButtonEnabled(enabled);
                setFinishButtonEnabled(enabled);
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
        if (((String) cmbField.getSelectedItem()).trim().length() < 1) {
            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     */
    protected void initialize() {
        this.setLayout(null);
        this.setSize(new Dimension(358, 263));
        this.addLabels();

        this.add(getCmbField(), null);
        this.add(getCmbDuplicate(), null);
        checkIsOkPanelData();
    }

    private JComboBox getCmbField() {
		if (this.cmbField == null) {
			this.cmbField = new JComboBox();
			this.cmbField.setEditable(false);
			this.cmbField.setBounds(cmbFieldPosition);
			this.cmbField.addItemListener(this.eventsListener);
			this.cmbField.addItem("");
			try {
				SelectableDataSource dataSource;

				dataSource = this.layer.getRecordset();

				String[] fieldsNames = dataSource.getFieldNames();
				for (int i = 0; i < fieldsNames.length; i++) {
					this.cmbField.addItem(fieldsNames[i]);
				}
				if (layer.getAnnotatonMapping() != null) {
					String fieldSelect = dataSource.getFieldName(layer
							.getAnnotatonMapping().getColumnText());
					this.cmbField.setSelectedItem(fieldSelect);
					setNextButtonEnabled(true);
				}
			} catch (ReadDriverException e) {
				NotificationManager.addError(layer.getName(), e);
			}
		}

		return this.cmbField;
	}

    /**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
    private JComboBox getCmbDuplicate() {
        if (cmbDuplicate == null) {
            cmbDuplicate = new JComboBox();
            cmbDuplicate.setBounds(cmbDuplicatePosition);
            cmbDuplicate.addItem(PluginServices.getText(this, "duplicate.none"));
            cmbDuplicate.addItem(PluginServices.getText(this, "centered"));
            cmbDuplicate.setSelectedItem(PluginServices.getText(this, "centered"));
        }

        return cmbDuplicate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getField() {
        return (String) this.getCmbField().getSelectedItem();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDuplicate() {
        return (String) this.getCmbDuplicate().getSelectedItem();
    }

    /**
     * DOCUMENT ME!
     */
    protected void addLabels() {
        this.lblDescription = new JLabel();

        this.lblStep2 = new JLabel();
        this.lblField = new JLabel();
        this.lblDuplicate = new JLabel();

        this.lblDuplicate.setText(PluginServices.getText(this, "duplicate"));
        this.lblDuplicate.setBounds(lblDuplicatePosition);
        this.lblDescription.setText(PluginServices.getText(this,
                "descripcion_de_crear_capa_de_anotaciones_nueva"));

        this.lblField.setText(PluginServices.getText(this,
                "seleccione_el_campo_de_texto_que_desea_que_se_utilize_para_mostrar_la_nueva_capa"));

        this.lblDescription.setBounds(lblDescriptionPosition);
        this.lblStep2.setBounds(lblStep2Position);
        this.lblField.setBounds(lblFieldPosition);

        this.add(lblDescription, null);
        this.add(lblStep2, null);
        this.add(lblField, null);
        this.add(lblDuplicate, null);
    }

    private class EventsListener implements CaretListener, ItemListener {
        public void caretUpdate(CaretEvent arg0) {
            updateButtonsState();
        }

        public void itemStateChanged(ItemEvent e) {
            updateButtonsState();
        }
    }
}
