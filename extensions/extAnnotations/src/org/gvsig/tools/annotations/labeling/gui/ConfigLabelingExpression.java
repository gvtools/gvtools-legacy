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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.WeakHashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelEvent;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelListener;
import org.gvsig.gui.beans.defaultbuttonspanel.DefaultButtonsPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

/**
 *
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
 *
 */
public class ConfigLabelingExpression extends DefaultButtonsPanel
 implements IWindow, ButtonsPanelListener {
	private WindowInfo wInfo = null;
	private static final long serialVersionUID = 1L;
	private BaseView view;
	private WeakHashMap<FLyrVect, JTextField> layerMap = new WeakHashMap<FLyrVect, JTextField>();
	public static String PROPERTYNAME = "SingleLabeling.LabelingExpression";
	private JScrollPane scrollPane;
	private JPanel scrollContent;


	public ConfigLabelingExpression(BaseView view) {
		super();
		this.view = view;
		addButtonPressedListener(this);
		initialize();
	}

	private void initialize() {
		getContent().setLayout(new GridBagLayout());
		JLabel label = new JLabel("<html><b>"+
				PluginServices.getText(this, "Define_the_labeling_expression_")
				+"</b></html>");
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(8,4,4,8);
		this.getContent().add(label, constraints);

		scrollContent = new JPanel(new GridBagLayout());
		scrollPane = new JScrollPane(scrollContent);

		FLayers layers = view.getMapControl().getMapContext().getLayers();
		addLayers(layers);

		constraints.gridy = layerMap.size()+1;
		constraints.gridx = 0;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.SOUTH;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		scrollContent.add(new JPanel(), constraints); // empty panel

		constraints.gridy = 1;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(8,4,4,8);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		this.getContent().add(panel, constraints);

	}

	private void addLayers(FLayers layers) {
		for (int i=layers.getLayersCount()-1; i>=0; i--) {
			FLayer layer = layers.getLayer(i);
			if (layer instanceof FLyrVect) {
				addRow((FLyrVect) layer);
			}
			else if (layer instanceof FLayers) {
				addLayers((FLayers)layer);
			}
		}
	}

	private void addRow(FLyrVect layer) {
		JTextField tf = new JTextField(20);
		layerMap.put(layer, tf);

		JLabel label = new JLabel("<html><b>"+
				PluginServices.getText(this, "Layer_")+"</b>"+
				" "+layer.getName()+"</html>");
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = layerMap.size();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(8,4,4,8);
		scrollContent.add(label, constraints);

		constraints.gridy = layerMap.size();
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.WEST;
		Object prop = layer.getProperty(PROPERTYNAME);
		if (prop!=null && prop instanceof String) {
			tf.setText((String)prop);
		}
		else {
			tf.setText("");
		}
		scrollContent.add(tf, constraints);
	}

	public WindowInfo getWindowInfo() {
		if (wInfo==null) {
			wInfo = new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.RESIZABLE);
			wInfo.setWidth(500);
			wInfo.setHeight(155);
			wInfo.setTitle(PluginServices.getText(this, "Set_labeling_expression"));
		}
		return wInfo;
	}

	private boolean apply() {
		Iterator<FLyrVect> iterator = layerMap.keySet().iterator();
		while (iterator.hasNext()) {
			FLyrVect layer = iterator.next();
			JTextField tf = layerMap.get(layer);
			if (tf!=null) {
				layer.setProperty(PROPERTYNAME, tf.getText());
			}
		}
		return true;
	}

	public void actionButtonPressed(ButtonsPanelEvent e) {
		switch (e.getButton()) {
		case ButtonsPanel.BUTTON_ACCEPT:
			if (apply()) {
				PluginServices.getMDIManager().closeWindow(this);
			}
			break;
		case ButtonsPanel.BUTTON_APPLY:
			apply();
			break;
		case ButtonsPanel.BUTTON_CANCEL:
			PluginServices.getMDIManager().closeWindow(this);
			break;
		}

	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

}
