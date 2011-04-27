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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.IWindowListener;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.Annotation_Mapping;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.tool.gui.TextPropertiesPanel;

/**
 *
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es> Oct 2008
 *
 */
public class SingleLabelingToolUI extends JPanel implements IWindow, IWindowListener, SingletonWindow {
	private static final long serialVersionUID = 1L;
	public static final String TOOL_CLOSED_PROP = "toolClosed";
	public static final String TARGET_LAYER_CHANGED_PROP = "targetLayerChanged";

	private WindowInfo wInfo = null;
	private BaseView view;
	private JTextField targetLayerField;
	private Annotation_Layer targetLayer;
	private TextPropertiesPanel textPropPanel = null;

	public SingleLabelingToolUI(BaseView view) {
		super();
		this.view = view;
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		JLabel label = new JLabel(
				PluginServices.getText(this, "Target_annotation_layer"));
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.insets = new Insets(4, 4, 4, 4);
		this.add(label, constraints);

		targetLayerField = new JTextField(9);
		targetLayerField.setEditable(false);
		constraints.gridy = 0;
		constraints.gridx = 1;
		targetLayerField.setMinimumSize(new Dimension(20,6));
		this.add(targetLayerField, constraints);

		JButton labExpressionBtn = new JButton("...");
		labExpressionBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				PluginServices.getMDIManager().addWindow(new ConfigTargetLayer(view, SingleLabelingToolUI.this));
			}
		});
		constraints.gridy = 0;
		constraints.gridx = 2;
		this.add(labExpressionBtn, constraints);

		constraints.gridy = 0;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		JPanel panel = new JPanel(); // required to give some insets to the textProp panel
		panel.add(getTextPropertiesPanel(), constraints);

		constraints.gridy = 1;
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		Border border = BorderFactory.createTitledBorder(PluginServices.getText(this, "Label_properties"));
		panel.setBorder(border);
		this.add(panel, constraints);

		labExpressionBtn = new JButton(PluginServices.getText(this, "Set_labeling_expression"));
		labExpressionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PluginServices.getMDIManager().addCentredWindow(new ConfigLabelingExpression(view));

			}
		});
		constraints.gridy = 2;
		constraints.gridx = 0;
		constraints.gridwidth = 3;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		this.add(labExpressionBtn, constraints);

	}

	public WindowInfo getWindowInfo() {
		if (wInfo==null) {
			wInfo = new WindowInfo(WindowInfo.PALETTE | WindowInfo.RESIZABLE);
			wInfo.setWidth(330);
			wInfo.setHeight(280);
			wInfo.setTitle(PluginServices.getText(this, "SingleLabelingTool")+" -- "+
					PluginServices.getMDIManager().getWindowInfo(view).getTitle());
			wInfo.setY(200);
		}
		return wInfo;
	}

	public void windowActivated() {
	}

	public void windowClosed() {
		firePropertyChange(TOOL_CLOSED_PROP, null, "closed");
	}

	public Object getWindowModel() {
		return view;
	}

	public TextPropertiesPanel getTextPropertiesPanel() {
		if (textPropPanel==null) {
			textPropPanel = new TextPropertiesPanel();
			applySystemWideSettings();
		}
		return textPropPanel;
	}

	private void applySystemWideSettings() {
		Annotation_Mapping mapping = new Annotation_Mapping(); // WEIRD, I KNOW!! Necessary because the defaults get initialized on object creation...

		textPropPanel.setFontType(Annotation_Mapping.DEFAULTTYPEFONT);
		textPropPanel.setFontStyle(Annotation_Mapping.DEFAULTSTYLEFONT);
		textPropPanel.setTextHeight(Annotation_Mapping.DEFAULTHEIGHT);
		Color color=new Color(Annotation_Mapping.DEFAULTCOLOR);
		textPropPanel.setColor(color);
		textPropPanel.setRotation(Annotation_Mapping.DEFAULTROTATE);
	}

	public void setTargetLayer(Annotation_Layer newTargetLayer) {
		if (this.targetLayer!=newTargetLayer) {
			Annotation_Layer oldLayer = this.targetLayer;
			this.targetLayer = newTargetLayer;
			if (newTargetLayer!=null) {
				targetLayerField.setText(newTargetLayer.getName());
			}
			else {
				targetLayerField.setText("");
			}
			firePropertyChange(TARGET_LAYER_CHANGED_PROP, oldLayer, newTargetLayer);
		}
	}

	public Annotation_Layer getTargetLayer() {
		return targetLayer;
	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

	public void activateWindow() {
		if (PluginServices.getMDIManager().getActiveWindow() != this){
			PluginServices.getMDIManager().addWindow(this);
		}

	}
}
