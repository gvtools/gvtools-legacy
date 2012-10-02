/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2008 IVER T.I. and Generalitat Valenciana.
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
 *   Lerida 20
 *   46009 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gvsig.tools.annotations.labeling.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelEvent;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelListener;
import org.gvsig.gui.beans.defaultbuttonspanel.DefaultButtonsPanel;
import org.gvsig.gui.beans.openfile.FileFilter;
import org.gvsig.gui.beans.openfile.FileTextField;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.Annotation_LayerFactory;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

/**
 *
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es> 12:00:04
 *
 */
public class ConfigTargetLayer extends DefaultButtonsPanel
	implements IWindow, ActionListener, ButtonsPanelListener {
	private WindowInfo wInfo = null;
	private static final long serialVersionUID = 1L;
	private BaseView sourceView;
	private JComboBox jcb_layers = null;
	private JRadioButton jrb_file = null;
	private JRadioButton jrb_layer = null;
	private FileTextField ftf_fileName = null;
	private SingleLabelingToolUI toolUI;

	public ConfigTargetLayer(BaseView sourceView, SingleLabelingToolUI toolUI) {
		super(ButtonsPanel.BUTTONS_ACCEPTCANCEL);
		this.sourceView = sourceView;
		this.toolUI = toolUI;
		initialize();
	}

	private void initialize() {
		this.addButtonPressedListener(this);
		getContent().setLayout(new GridBagLayout());
		JLabel lbl_header = new JLabel(PluginServices.getText(this, "Select_the_annotation_layer_to_store_the_labels_"));
		Font font = lbl_header.getFont();
		lbl_header.setFont(font.deriveFont(Font.BOLD));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4,10,8,4);
		getContent().add(lbl_header, constraints);

		ButtonGroup group = new ButtonGroup();
		group.add(getFileButton());
		group.add(getLayerButton());
		getFileButton().addActionListener(this);
		getLayerButton().addActionListener(this);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4,10,4,6);
		getContent().add(getFileButton(), constraints);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.weighty = 0.0;
		getContent().add(getFileNameField(), constraints);

		JPanel detailLine = new JPanel(new GridBagLayout());

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 0.5;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(3,20,10,6);
		getContent().add(detailLine, constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4,10,4,6);
		getContent().add(getLayerButton(), constraints);

		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.weightx = 0.1;
		constraints.weighty = 0.0;
		getContent().add(getLayersCombo(), constraints);


		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.weightx = 1.0;
		constraints.weighty = 0.2;
		getContent().add(new JPanel(), constraints); // empty panel

		Annotation_Layer lyr = toolUI.getTargetLayer();
		if (lyr!=null) {
			getLayersCombo().setSelectedItem(
					new ComboItem(lyr));
			selectExistingLayerMode();
		}
		else {
			selectFileMode();
		}
	}

	private void selectFileMode() {
		getFileButton().setSelected(true);
		getFileNameField().setEnabled(true);
		getLayersCombo().setEnabled(false);

	}

	private void selectExistingLayerMode() {
		getLayerButton().setSelected(true);
		getFileNameField().setEnabled(false);
		getLayersCombo().setEnabled(true);
	}

	private JRadioButton getFileButton() {
		if (jrb_file==null) {
			jrb_file = new JRadioButton(
					PluginServices.getText(this, "Create_Open_layer"));
		}
		return jrb_file;
	}

	private JRadioButton getLayerButton(){
		if (jrb_layer == null) {
			jrb_layer = new JRadioButton(
					PluginServices.getText(this, "Layer_from_active_view"));
		}
		return jrb_layer;
	}

	private FileTextField getFileNameField() {
		if (ftf_fileName==null) {
			ftf_fileName = new FileTextField(this.getClass().getName());
			ftf_fileName.setAcceptAllFileFilterUsed(false);
			ftf_fileName.addChoosableFileFilter(new FileFilter() {

				public boolean accept(File f) {
					if (f.isDirectory()
							|| f.getName().toLowerCase().endsWith("gva")) {
						return true;
					}
					else {
						return false;
					}
				}

				public String getDescription() {
					return PluginServices.getText(this, "Annotation_layers");
				}

				public String getDefaultExtension() {
					return "gva";
				}
			});
		}
		return ftf_fileName;
	}

	public JComboBox getLayersCombo() {
		if (jcb_layers==null) {
			jcb_layers = new JComboBox();
			FLayers layers = sourceView.getMapControl().getMapContext().getLayers();
			for (int i=0; i<layers.getLayersCount(); i++) {
				if (layers.getLayer(i) instanceof Annotation_Layer) {
					jcb_layers.addItem(new ComboItem((Annotation_Layer)layers.getLayer(i)));
				}
			}
		}
		return jcb_layers;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==getLayerButton()) {
			selectExistingLayerMode();
		}
		else if (e.getSource()==getFileButton()) {
			selectFileMode();
		}
	}

	private class ComboItem {
		private Annotation_Layer layer;
		public ComboItem(Annotation_Layer layer) {
			this.layer = layer;
		}

		public String toString() {
			return layer.getName();
		}

		public Annotation_Layer getLayer() {
			return layer;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ComboItem) {
				return this.layer==((ComboItem)obj).getLayer();
			}
			return super.equals(obj);
		}
	}

	public WindowInfo getWindowInfo() {
		if (wInfo==null) {
			wInfo = new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.RESIZABLE);
			wInfo.setWidth(500);
			wInfo.setHeight(155);
			wInfo.setTitle(PluginServices.getText(this, "Set_target_layer"));
		}
		return wInfo;
	}

	private boolean apply() {
		if (getLayerButton().isSelected()) {
			ComboItem item = (ComboItem) getLayersCombo().getSelectedItem();
			if (item!=null) {
				toolUI.setTargetLayer(item.getLayer());
				return true;
			}
			else {
				NotificationManager.showMessageError(
						PluginServices.getText(this, "Annotation_layer_not_valid"),
						null);
			}
		}
		else {
			File file = getFileNameField().getSelectedFile();
			if (file!=null) {
				file = gvaToShpExtension(file);
				if (!file.exists()) {
					try {
						// create new layer
						Annotation_LayerFactory.createEmptyLayer(file, sourceView.getCrs());
					} catch (StartWriterVisitorException e) {
						NotificationManager.showMessageError(
								PluginServices.getText(this, "Error_creating_annotation_layer"),
								e);
						return false;
					} catch (StopWriterVisitorException e) {
						NotificationManager.showMessageError(
								PluginServices.getText(this, "Error_creating_annotation_layer"),
								e);
						return false;
					} catch (DriverLoadException e) {
						NotificationManager.showMessageError(
								PluginServices.getText(this, "Error_opening_annotation_layer"),
								e);
						return false;
					} catch (InitializeWriterException e) {
						NotificationManager.showMessageError(
								PluginServices.getText(this, "Error_creating_annotation_layer"),
								e);
						return false;
					} catch (LoadLayerException e) {
						NotificationManager.showMessageError(
								PluginServices.getText(this, "Error_opening_annotation_layer"),
								e);
						return false;
					}
				}
				// open the layer
				try {
					Annotation_Layer layer = Annotation_LayerFactory.createLayer(file.getName(), file, sourceView.getCrs(), CartographicSupportToolkit.DefaultMeasureUnit);
					if (layer!=null) {
						sourceView.getMapControl().getMapContext().getLayers().addLayer(layer);
						toolUI.setTargetLayer(layer);
						return true;
					}
				} catch (Exception ex) {
					NotificationManager.showMessageError(
							PluginServices.getText(this, "Error_opening_annotation_layer"),
							ex);
					return false;
				}
			}
		}
		NotificationManager.showMessageError(
				PluginServices.getText(this, "Error_opening_annotation_layer"),
				null);
		return false;
	}

	private File gvaToShpExtension(File file) {
		String path = file.getPath();
		int pos = path.toLowerCase().lastIndexOf(".gva");
		if (pos!=-1) {
			file = new File(path.substring(0, pos)+".shp");
		}
		return file;
	}

	public void actionButtonPressed(ButtonsPanelEvent e) {
		switch (e.getButton()) {
			case ButtonsPanel.BUTTON_ACCEPT:
				if (apply()) {
					PluginServices.getMDIManager().closeWindow(this);
					getLayersCombo().removeAllItems(); // forget the available layers
				}
				break;
			case ButtonsPanel.BUTTON_APPLY:
				apply();
				break;
			case ButtonsPanel.BUTTON_CANCEL:
				PluginServices.getMDIManager().closeWindow(this);
				getLayersCombo().removeAllItems(); // forget the available layers
				break;

		}
	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

}
