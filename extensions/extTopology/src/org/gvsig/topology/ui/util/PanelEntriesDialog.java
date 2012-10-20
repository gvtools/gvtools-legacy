/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package org.gvsig.topology.ui.util;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.util.GFeatureParameter;
import org.gvsig.util.GNumberParameter;
import org.gvsig.util.GParameter;
import org.gvsig.util.GParameterChangeListener;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toolListeners.StatusBarListener;

/**
 * A panel to introduce user params.
 * 
 * It is used by certain automatic error fixes that needs user interaction.
 * 
 * @author Alvaro Zabala
 * 
 */
public class PanelEntriesDialog extends IWindowBoxLayoutPanel {
	private static final long serialVersionUID = -8832524833359738672L;

	/**
	 * Set of GParameter that this component is going to fill.
	 */
	private GParameter[] parameters;

	/**
	 * Ok button
	 */
	private JButton acceptButton;

	/**
	 * Parent of the dialog
	 */
	private Window parent;

	/**
	 * Flag to mark if the accept button was pressed
	 */
	private boolean accepted = false;

	/**
	 * Collection of GParameterChangeListener, to notify when a GParameter value
	 * has changed.
	 */
	List<GParameterChangeListener> paramsListener;

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            title of the dialog
	 * @param w
	 * @param h
	 * @param parameters
	 * @param container
	 */
	public PanelEntriesDialog(String title, int w, int h,
			GParameter[] parameters, Window container) {
		super(title, w, h);
		this.parameters = parameters;
		this.parent = container;
		initialize();
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	private void initialize() {
		addRow(new JComponent[] { new JLabel(PluginServices.getText(this,
				"Parametros_De_Entrada_Para_La_Correccion")) });

		paramsListener = new ArrayList<GParameterChangeListener>();

		for (int i = 0; i < parameters.length; i++) {
			GParameter param = parameters[i];
			GParameterChangeListener listener = addComponentForParameter(param);
			paramsListener.add(listener);
		}

		acceptButton = new JButton(PluginServices.getText(this, "Aceptar"));
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				accept();
			}

		});

		addRow(new JComponent[] { acceptButton }, getWindowInfo().getWidth(),
				getWindowInfo().getHeight());
	}

	public GParameterChangeListener addComponentForParameter(GParameter param) {
		if (param instanceof GNumberParameter) {
			return addNumericalTextField((GNumberParameter) param);
		} else if (param instanceof GFeatureParameter)
			return addFeatureField((GFeatureParameter) param);
		return null;
	}

	/**
	 * Adds a text field to receive numerical values.
	 * 
	 * Based in SEXTANTE's StdExParametersPanel
	 * 
	 * @param parameter
	 * @return JTextField to add
	 * 
	 */
	public GParameterChangeListener addNumericalTextField(
			final GNumberParameter parameter) {
		final JTextField textField = new JTextField();
		addTitleLabel(PluginServices.getText(this, parameter.getParamName()),
				false);
		textField.setText(parameter.getDefaultValue().toString());

		textField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				JTextField textField = (JTextField) e.getSource();
				String content = textField.getText();
				Object newValue = null;
				if (content.length() != 0) {
					try {
						if (parameter.isInteger()) {
							int i = Integer.parseInt(content);
							newValue = new Integer(i);
						} else {
							double d = Double.parseDouble(content);
							newValue = new Double(d);

						}
						parameter.setValue(newValue);
					} catch (NumberFormatException nfe) {
						textField.requestFocus();
					}

				}// content.length
			}
		});

		addRow(new JComponent[] { textField });

		return new GParameterChangeListener() {
			public void parameterChange() {
				String content = textField.getText();
				Object newValue = null;
				if (content.length() != 0) {
					try {
						if (parameter.isInteger()) {
							int i = Integer.parseInt(content);
							newValue = new Integer(i);
						} else {
							double d = Double.parseDouble(content);
							newValue = new Double(d);

						}
						parameter.setValue(newValue);
					} catch (NumberFormatException nfe) {
						textField.requestFocus();
					}

				}// content.length

			}
		};
	}

	/**
	 * Adds a component to this dialog wich allows to select features with the
	 * mouse in the map control, and pass this selection to the specified
	 * GFeatureParameter.
	 * 
	 * 
	 * @param param
	 * @return
	 */
	private GParameterChangeListener addFeatureField(
			final GFeatureParameter param) {

		// TODO Add a JLabel in the same row that the select button, to show fid
		// and lyr of the selected feature
		addTitleLabel(param.getParamName(), false);

		JButton button = new JButton();
		final JLabel featureIdLabel = new JLabel();
		button.setIcon(new ImageIcon(getClass()
				.getResource("images/target.png")));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				IWindow f = PluginServices.getMDIManager().getActiveWindow();
				if (f != null && f instanceof View) {
					View view = (View) f;
					MapControl mapCtrl = view.getMapControl();
					if (mapCtrl != null) {
						String sTool = "featureSelector";
						StatusBarListener sbl = new StatusBarListener(mapCtrl);

						final Window thisContainer = GUIUtil.getInstance()
								.getParentWindow(PanelEntriesDialog.this);
						FeatureSelectionListener fsl = new FeatureSelectionListener(
								view, param, new FeatureSelectionCallBack() {
									public void featureSelected() {
										IFeature feature = (IFeature) param
												.getValue();
										featureIdLabel.setText("fid:"
												+ feature.getID());
										if (!thisContainer.isVisible()) {
											thisContainer.setVisible(true);
										}
									}
								});
						mapCtrl.addMapTool(sTool, new Behavior[] {
								new PointBehavior(fsl),
								new MouseMovementBehavior(sbl) });
						mapCtrl.setTool(sTool);

						// This dialog has a dependant parent (in topology
						// project topologypropertiespanel)
						// we put it invisible
						if (parent != null) {
							parent.setVisible(false);
						}

						// and we put invisible the dialog itself
						thisContainer.setVisible(false);

					}// if mapCtrl != null
				}// if f!=null
			}// actionPerformed
		});

		addRow(new JComponent[] { button, featureIdLabel });

		return new GParameterChangeListener() {
			public void parameterChange() {
			}
		};

	}

	public void accept() {
		for (int i = 0; i < paramsListener.size(); i++) {
			paramsListener.get(i).parameterChange();
		}

		Window thisContainer = GUIUtil.getInstance().getParentWindow(
				PanelEntriesDialog.this);
		thisContainer.setVisible(false);

		setAccepted(true);
		if (parent != null) {
			if (!parent.isVisible())
				parent.setVisible(true);
		}

	}

	public void addOkActionListener(ActionListener actionListener) {
		acceptButton.addActionListener(actionListener);
	}

}
