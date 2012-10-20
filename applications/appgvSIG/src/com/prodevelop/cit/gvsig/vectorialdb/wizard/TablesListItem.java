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

import javax.swing.JOptionPane;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;

/**
 * Utility class that represents a table list item as a selectable check box.
 * 
 * @author jldominguez
 * 
 */
public class TablesListItem extends TablesListItemSimple {

	private UserSelectedFieldsPanel selectedFieldsPanel = null;
	private UserTableSettingsPanel tableSettingsPanel = null;
	private IVectorialDatabaseDriver driver = null;

	private MapControl mc;
	private WizardVectorialDB parent = null;
	private boolean activated = false;
	private CRSSelectPanel jPanelProj;

	public TablesListItem(String name, IVectorialDatabaseDriver drv,
			IConnection _conn, MapControl _mc, WizardVectorialDB _parent) {

		super(name, null, _conn);
		setText(name);
		driver = drv;
		mc = _mc;
		parent = _parent;
	}

	public void activate() {
		activated = true;
		selectedFieldsPanel.loadValues();
		tableSettingsPanel.loadValues();
	}

	public boolean isActivated() {
		return activated;
	}

	/**
	 * Tells whether this item prevents the wizard from being in a valid final
	 * state.
	 * 
	 * @return whether this item prevents the wizard from being in a valid final
	 *         state.
	 */
	public boolean disturbsWizardValidity() {
		if (isSelected()) {
			return (!hasValidValues());
		} else {
			return false;
		}
	}

	private boolean hasValidValues() {
		return tableSettingsPanel.hasValidValues();
	}

	public void setEnabledPanels(boolean b) {
		selectedFieldsPanel.enableControls(b);
		tableSettingsPanel.enableControls(b);
	}

	public UserSelectedFieldsPanel getUserSelectedFieldsPanel()
			throws DBException {
		if (selectedFieldsPanel == null) {
			String[] allf = driver.getAllFields(conn, tableName);
			String[] allt = driver.getAllFieldTypeNames(conn, tableName);
			selectedFieldsPanel = new UserSelectedFieldsPanel(allf, allt, true,
					parent);
		}

		return selectedFieldsPanel;
	}

	public UserTableSettingsPanel getUserTableSettingsPanel(String espView)
			throws DBException {
		if (tableSettingsPanel == null) {

			String[] ids = new String[0];
			try {
				ids = driver.getIdFieldsCandidates(conn, tableName);
			} catch (DBException se) {
				String msg = PluginServices.getText(this, "id_not_available")
						+ " " + tableName + ":\n" + se.getMessage();
				String title = PluginServices.getText(this, "id_error");
				JOptionPane.showMessageDialog(parent, msg, title,
						JOptionPane.ERROR_MESSAGE);
				setSelected(false);
				throw se;
			}

			String[] geos = new String[0];
			try {
				geos = driver.getGeometryFieldsCandidates(conn, tableName);
			} catch (DBException se) {
				String msg = PluginServices.getText(this,
						"geo_field_not_available")
						+ ":\n"
						+ PluginServices.getText(this, se.getMessage())
						+ ": "
						+ tableName;
				String title = PluginServices.getText(this, "geo_field_error");
				JOptionPane.showMessageDialog(parent, msg, title,
						JOptionPane.ERROR_MESSAGE);
				setSelected(false);
				throw se;
			}

			int ids_size = ids.length;
			FieldComboItem[] ids_ci = new FieldComboItem[ids_size];

			for (int i = 0; i < ids_size; i++)
				ids_ci[i] = new FieldComboItem(ids[i]);

			int geos_size = geos.length;
			FieldComboItem[] geos_ci = new FieldComboItem[geos_size];

			for (int i = 0; i < geos_size; i++)
				geos_ci[i] = new FieldComboItem(geos[i]);

			tableSettingsPanel = new UserTableSettingsPanel(ids_ci, geos_ci,
					tableName, mc, true, parent, projectionPanel(espView,
							ids_ci, geos_ci));
		}

		return tableSettingsPanel;
	}

	public CRSSelectPanel projectionPanel(String espView,
			FieldComboItem[] ids_ci, FieldComboItem[] geos_ci) {
		CoordinateReferenceSystem crs = AddLayerDialog.getLastCrs();
		if (driver instanceof ICanReproject) {
			try {
				DBLayerDefinition lyrDef = new DBLayerDefinition();
				lyrDef.setName(tableName);
				String[] tokens = tableName.split("\\u002E", 2);
				String schema = null;
				String tableName;
				if (tokens.length > 1) {
					schema = tokens[0];
					tableName = tokens[1];
				} else {
					tableName = tokens[0];
				}
				lyrDef.setSchema(schema);
				lyrDef.setTableName(tableName);
				if (ids_ci.length > 0) {
					String fidField = ids_ci[0].toString();
					lyrDef.setFieldID(fidField);
				}
				if (geos_ci.length > 0) {
					String geomField = geos_ci[0].toString();
					lyrDef.setFieldGeometry(geomField);
				}

				String strEPSG = espView;
				lyrDef.setSRID_EPSG(strEPSG);
				((ICanReproject) driver).setDestProjection(strEPSG);

				String srcProjection = ((ICanReproject) driver)
						.getSourceProjection(conn, lyrDef);
				if (srcProjection != null && srcProjection.length() > 0
						&& !srcProjection.equals("-1")) {

					crs = ProjectionUtils.getCRS("EPSG:" + srcProjection);
				} else {
					crs = null;
				}
			} catch (Exception e) {
				NotificationManager.addInfo("Incorrect projection", e);
			}
		}
		return getJPanelProj(crs);
	}

	private CRSSelectPanel getJPanelProj(CoordinateReferenceSystem crs) {
		// if (jPanelProj == null) {
		// IProjection proj = CRSFactory.getCRS("EPSG:23030");
		// if (PluginServices.getMainFrame() != null) {
		// proj = FOpenDialog.getLastProjection();
		// }
		jPanelProj = CRSSelectPanel.getPanel(crs);

		jPanelProj.setTransPanelActive(true);
		jPanelProj.setBounds(new java.awt.Rectangle(210, 22, 280, 32));
		jPanelProj.setPreferredSize(new java.awt.Dimension(448, 35));
		jPanelProj.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (jPanelProj.isOkPressed()) {
					AddLayerDialog.setLastCrs(jPanelProj.getCurrentCrs());
				}
			}
		});

		// }

		return jPanelProj;
	}
}

// [eiel-gestion-conexiones]