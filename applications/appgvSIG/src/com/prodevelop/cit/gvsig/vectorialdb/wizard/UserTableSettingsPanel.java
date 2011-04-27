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
package com.prodevelop.cit.gvsig.vectorialdb.wizard;

import com.iver.andami.PluginServices;

import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;

import org.apache.log4j.Logger;
import org.cresques.cts.IProjection;

import org.gvsig.gui.beans.swing.JButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * Utility class that holds a single table settings controls.
 *
 * @author jldominguez
 *
 */
public class UserTableSettingsPanel extends JPanel implements ActionListener,
    KeyListener {
    private static Logger logger = Logger.getLogger(UserTableSettingsPanel.class.getName());
    private FieldComboItem[] ids;
    private FieldComboItem[] geos;
    private String initLayerName = "";
    private JComboBox idComboBox = null;
    private JComboBox geomComboBox = null;
    private JTextArea sqlTextArea = null;
    private JLabel idLabel = null;
    private JLabel geomLabel = null;
    private JLabel sqlLabel = null;
    private JLabel waLabel = null;
    private JLabel topLabel = null;
    private JTextField topTextField = null;
    private JTextField bottomTextField = null;
    private JTextField rightTextField = null;
    private JTextField leftTextField = null;
    private JLabel bottomLabel = null;
    private JLabel rightLabel = null;
    private JLabel leftLabel = null;
    private JButton getviewButton = null;
    private JCheckBox activateWACheckBox = null;
    private JCheckBox activateSQLCheckBox = null;
    private JLabel tableNamejLabel = null;
    private JTextField layerNameTextField = null;
    private JScrollPane sqlTextAreaScroll = null;
    private MapControl mControl = null;
    private WizardVectorialDB parent = null;
    private CRSSelectPanel panelProj;

    public UserTableSettingsPanel(FieldComboItem[] idComboItems,
        FieldComboItem[] geoComboItems, String initialLayerName,
        MapControl mapc, boolean empty, WizardVectorialDB _p, CRSSelectPanel panel) {
        parent = _p;

        mControl = mapc;
        initLayerName = initialLayerName;
        ids = idComboItems;
        geos = geoComboItems;
        panelProj=panel;
        initialize(empty);
    }

    public boolean hasValidValues() {
        if (!combosHaveItems()) {
            return false;
        }

        if ((activateWACheckBox.isSelected()) && (getWorkingArea() == null)) {
            return false;
        }

        if ((activateSQLCheckBox.isSelected()) &&
                (getSqlTextArea().getText().trim().length() == 0)) {
            return false;
        }

        if (getLayerNameTextField().getText().trim().length() == 0) {
            return false;
        }

        return true;
    }

    private void initialize(boolean _empty) {
        tableNamejLabel = new JLabel();
        tableNamejLabel.setText(PluginServices.getText(this, "layer_name"));
        tableNamejLabel.setSize(new java.awt.Dimension(100,21));
        tableNamejLabel.setLocation(new java.awt.Point(15,25));
        leftLabel = new JLabel();
        leftLabel.setBounds(new java.awt.Rectangle(375, 175, 111, 16));
        leftLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        leftLabel.setText(PluginServices.getText(this, "xmin"));
        rightLabel = new JLabel();
        rightLabel.setBounds(new java.awt.Rectangle(260, 175, 111, 16));
        rightLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        rightLabel.setText(PluginServices.getText(this, "xmax"));
        bottomLabel = new JLabel();
        bottomLabel.setBounds(new java.awt.Rectangle(130, 175, 111, 16));
        bottomLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        bottomLabel.setText(PluginServices.getText(this, "ymin"));
        topLabel = new JLabel();
        topLabel.setBounds(new java.awt.Rectangle(15, 175, 111, 16));
        topLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        topLabel.setText(PluginServices.getText(this, "ymax"));
        waLabel = new JLabel();
        waLabel.setBounds(new java.awt.Rectangle(40, 145, 131, 21));
        waLabel.setText(PluginServices.getText(this, "working_area"));
        sqlLabel = new JLabel();
        sqlLabel.setBounds(new java.awt.Rectangle(40, 90, 116, 21));
        sqlLabel.setText(PluginServices.getText(this, "sql_restriction"));
        geomLabel = new JLabel();
        geomLabel.setBounds(new java.awt.Rectangle(240, 55, 111, 21));
        geomLabel.setText(PluginServices.getText(this, "geo_field"));
        idLabel = new JLabel();
        idLabel.setBounds(new java.awt.Rectangle(15, 55, 86, 21));
        idLabel.setText(PluginServices.getText(this, "id_field"));

        setLayout(null);
        setBounds(new java.awt.Rectangle(5, 250, 501, 221));
        setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                PluginServices.getText(this, "specify_table_settings"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        add(getIdComboBox(), null);
        add(getGeomComboBox(), null);
        add(getSqlTextAreaScroll(), null);
        add(idLabel, null);
        add(geomLabel, null);
        add(sqlLabel, null);
        add(waLabel, null);
        add(topLabel, null);
        add(getTopTextField(), null);
        add(getBottomTextField(), null);
        add(getRightTextField(), null);
        add(getLeftTextField(), null);
        add(bottomLabel, null);
        add(rightLabel, null);
        add(leftLabel, null);
        add(getGetviewButton(), null);
        add(getActivateWACheckBox(), null);
        add(getActivateSQLCheckBox(), null);
        add(getLayerNameTextField(), null);
        add(tableNamejLabel, null);

        loadValues(_empty);
    }

    public void enableControls(boolean enable) {
        getIdComboBox().setEnabled(enable);
        getGeomComboBox().setEnabled(enable);
        getLayerNameTextField().setEnabled(enable);
        JCheckBox activateSQLCheckBox = getActivateSQLCheckBox();
        activateSQLCheckBox.setEnabled(enable);
        getSqlTextArea().setEnabled(activateSQLCheckBox.isSelected());

        getActivateWACheckBox().setEnabled(enable);

        boolean there_is_view = ((mControl != null) &&
            (mControl.getViewPort().getAdjustedExtent() != null));

        getGetviewButton().setEnabled(enable && there_is_view);
        getTopTextField().setEnabled(enable);
        getBottomTextField().setEnabled(enable);
        getRightTextField().setEnabled(enable);
        getLeftTextField().setEnabled(enable);
    }

    public void loadValues() {
        loadValues(false);
    }

    private void loadValues(boolean is_empty) {
        if (is_empty) {
            enableControls(false);
            getLayerNameTextField().setText("");
            getActivateSQLCheckBox().setSelected(false);
            getSqlTextArea().setEnabled(false);
            getActivateWACheckBox().setSelected(false);
        }
        else {
            getIdComboBox().removeAllItems();

            for (int i = 0; i < ids.length; i++) {
                getIdComboBox().addItem(ids[i]);
            }

            getGeomComboBox().removeAllItems();

            for (int i = 0; i < geos.length; i++) {
                getGeomComboBox().addItem(geos[i]);
            }

            getLayerNameTextField().setText(initLayerName);

            getSqlTextArea().setEnabled(getActivateSQLCheckBox().isSelected());

            add(panelProj,null);
        }
    }

    private JComboBox getIdComboBox() {
        if (idComboBox == null) {
            idComboBox = new JComboBox();

            idComboBox.setBounds(new java.awt.Rectangle(105, 55, 126, 21));
        }

        return idComboBox;
    }

    private JComboBox getGeomComboBox() {
        if (geomComboBox == null) {
            geomComboBox = new JComboBox();
            geomComboBox.setBounds(new java.awt.Rectangle(355, 55, 131, 21));
        }

        return geomComboBox;
    }

    private JTextField getTopTextField() {
        if (topTextField == null) {
            topTextField = new JTextField();
            topTextField.addKeyListener(this);
            topTextField.setBounds(new java.awt.Rectangle(15, 190, 111, 21));
        }

        return topTextField;
    }

    private JTextField getBottomTextField() {
        if (bottomTextField == null) {
            bottomTextField = new JTextField();
            bottomTextField.addKeyListener(this);
            bottomTextField.setBounds(new java.awt.Rectangle(130, 190, 111, 21));
        }

        return bottomTextField;
    }

    private JTextField getRightTextField() {
        if (rightTextField == null) {
            rightTextField = new JTextField();
            rightTextField.addKeyListener(this);
            rightTextField.setBounds(new java.awt.Rectangle(260, 190, 111, 21));
        }

        return rightTextField;
    }

    private JTextField getLeftTextField() {
        if (leftTextField == null) {
            leftTextField = new JTextField();
            leftTextField.addKeyListener(this);
            leftTextField.setBounds(new java.awt.Rectangle(375, 190, 111, 21));
        }

        return leftTextField;
    }

    private JButton getGetviewButton() {
        if (getviewButton == null) {
            getviewButton = new JButton();
            getviewButton.addActionListener(this);
            getviewButton.setBounds(new java.awt.Rectangle(195, 145, 111, 26));
            getviewButton.setForeground(java.awt.Color.black);
            getviewButton.setText(PluginServices.getText(this, "get_view"));
        }

        return getviewButton;
    }

    private JCheckBox getActivateWACheckBox() {
        if (activateWACheckBox == null) {
            activateWACheckBox = new JCheckBox();
            activateWACheckBox.addActionListener(this);
            activateWACheckBox.setBounds(new java.awt.Rectangle(15, 145, 21, 21));
        }

        return activateWACheckBox;
    }

    private JCheckBox getActivateSQLCheckBox() {
        if (activateSQLCheckBox == null) {
            activateSQLCheckBox = new JCheckBox();
            activateSQLCheckBox.addActionListener(this);
            activateSQLCheckBox.setBounds(new java.awt.Rectangle(15, 90, 21, 21));
        }

        return activateSQLCheckBox;
    }

    private JTextField getLayerNameTextField() {
        if (layerNameTextField == null) {
            layerNameTextField = new JTextField();
            layerNameTextField.setSize(new java.awt.Dimension(98,21));
            layerNameTextField.setLocation(new java.awt.Point(122,25));
            layerNameTextField.addKeyListener(this);
        }

        return layerNameTextField;
    }

    private JTextArea getSqlTextArea() {
        if (sqlTextArea == null) {
            sqlTextArea = new JTextArea();
            sqlTextArea.setLineWrap(true);
            sqlTextArea.setWrapStyleWord(true);
            sqlTextArea.addKeyListener(this);

            // sqlTextArea.setBounds(new java.awt.Rectangle(160,90,326,41));
        }

        return sqlTextArea;
    }

    private JScrollPane getSqlTextAreaScroll() {
        if (sqlTextAreaScroll == null) {
            sqlTextAreaScroll = new JScrollPane();
            sqlTextAreaScroll.setBounds(new java.awt.Rectangle(160, 90, 326, 41));
            sqlTextAreaScroll.setViewportView(getSqlTextArea());
            sqlTextAreaScroll.updateUI();
        }

        return sqlTextAreaScroll;
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == getviewButton) {
            getViewIntoFourBounds();
            parent.checkFinishable();
        }

        if (src == activateSQLCheckBox) {
            enableSQLSettings(activateSQLCheckBox.isSelected());
            parent.checkFinishable();
        }

        if (src == activateWACheckBox) {
            enableWASettings(activateWACheckBox.isSelected());
            parent.checkFinishable();
        }
    }

    private void enableWASettings(boolean b) {
        getviewButton.setEnabled(b &&
            (mControl.getViewPort().getAdjustedExtent() != null));
        rightTextField.setEnabled(b);
        leftTextField.setEnabled(b);
        topTextField.setEnabled(b);
        bottomTextField.setEnabled(b);
    }

    private void enableSQLSettings(boolean b) {
        sqlTextArea.setEnabled(b);
    }

    private void getViewIntoFourBounds() {
        Rectangle2D rect = mControl.getViewPort().getAdjustedExtent();
        topTextField.setText(getFormattedDouble(rect.getMaxY()));
        bottomTextField.setText(getFormattedDouble(rect.getMinY()));
        rightTextField.setText(getFormattedDouble(rect.getMaxX()));
        leftTextField.setText(getFormattedDouble(rect.getMinX()));
    }

    private String getFormattedDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.###");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(d);
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        parent.checkFinishable();
    }

    public void keyTyped(KeyEvent e) {
    }

    public Rectangle2D getWorkingArea() {
        if (!activateWACheckBox.isSelected()) {
            return null;
        }

        double maxx;
        double maxy;
        double minx;
        double miny;

        try {
            maxx = Double.parseDouble(rightTextField.getText());
            miny = Double.parseDouble(bottomTextField.getText());
            minx = Double.parseDouble(leftTextField.getText());
            maxy = Double.parseDouble(topTextField.getText());
        }
        catch (NumberFormatException nfe) {
            logger.error("Not valid value: " + nfe.getMessage());

            return null;
        }

        return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
    }

    public String getUserLayerName() {
        return getLayerNameTextField().getText();
    }

    public boolean combosHaveItems() {
        if (getIdComboBox().getItemCount() == 0) {
            return false;
        }

        if (getGeomComboBox().getItemCount() == 0) {
            return false;
        }

        return true;
    }

    public void repaint() {
        super.repaint();
        getIdComboBox().updateUI();
        getGeomComboBox().updateUI();
    }

    public String getIdFieldName() {
        return getIdComboBox().getSelectedItem().toString();
    }

    public String getGeoFieldName() {
    	if (getGeomComboBox().getSelectedItem() ==null)
    		return null;
    	return getGeomComboBox().getSelectedItem().toString();
    }

    public String getWhereClause() {
        return getSqlTextArea().getText();
    }

    public boolean isSqlActive() {
        return getActivateSQLCheckBox().isSelected();
    }
    public IProjection getProjection() {
		return panelProj.getCurProj();
	}
}
