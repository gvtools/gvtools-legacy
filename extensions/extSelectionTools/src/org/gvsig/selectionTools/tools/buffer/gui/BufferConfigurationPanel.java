package org.gvsig.selectionTools.tools.buffer.gui;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 */

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.gui.beans.progresspanel.ProgressPanel;
import org.gvsig.gui.beans.specificcaretposition.JFormattedTextFieldSCP;
import org.gvsig.selectionTools.tools.buffer.process.BufferSelectionProcess;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.impl.buffer.fmap.BufferVisitor;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.JComboBox;

/**
 * 
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class BufferConfigurationPanel extends JPanel implements IWindow {
	private WindowInfo viewInfo = null;
	private final short Window_Width = 354;
	private final short Window_Height = 315; // 35;
	private JFormattedTextFieldSCP distanceTextField = null;
	private JComboBox distanceUnitsCombo = null;
	private JComboBox polygonSidesCombo = null;
	private JComboBox lineSidesCombo = null;
	private JComboBox pointSidesCombo = null;
	private JComboBox multiPointSidesCombo = null;
	private JLabel distanceUnitsLabel = null;
	private JLabel polygonSideLabel = null;
	private JLabel lineSideLabel = null;
	private JLabel pointSideLabel = null;
	private JLabel multiPointSideLabel = null;
	private JPanel polygonSidePanel = null;
	private JPanel lineSidePanel = null;
	private JPanel pointSidePanel = null;
	private JPanel multiPointSidePanel = null;
	private JLabel widthLabel = null;
	private JPanel widthPanel = null;
	// private JPanel distanceUnits = null;
	private JPanel sidePanel = null;
	private JPanel optionsPanel = null;
	private AdaptedAcceptCancelPanel acceptCancelPanel = null;
	private FLyrVect[] layers;
	private MapControl mapControl;
	private View view;
	private JCheckBox multiLayerSelectionCBox;
	// private JCheckBox addBufferLayersCBox;
	private JCheckBox addInfluenceAreasLayersCBox;
	private SideInfo outside, inside, out_in_side;

	/**
	 * <p>
	 * Creates a new form where user can define the option of the buffer.
	 * </p>
	 */
	public BufferConfigurationPanel(FLyrVect[] array, View view) {
		super();

		layers = array;
		this.view = view;
		mapControl = view.getMapControl();

		initialize();
	}

	/**
	 * <p>
	 * Initializes this component.
	 * </p>
	 */
	private void initialize() {
		outside = new SideInfo(BufferVisitor.BUFFER_OUTSIDE_POLY,
				PluginServices.getText(null, "Outside"));
		inside = new SideInfo(BufferVisitor.BUFFER_INSIDE_POLY,
				PluginServices.getText(null, "Inside"));
		out_in_side = new SideInfo(BufferVisitor.BUFFER_INSIDE_OUTSIDE_POLY,
				PluginServices.getText(null, "Both"));

		setLayout(new FlowLayout());
		add(getWidthPanel());
		add(getSidePanel());
		add(getOptionsPanel());
		add(getAdaptedAcceptCancelPanel());
	}

	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setLayout(new FlowLayout());
			optionsPanel.setPreferredSize(new Dimension(344, 80)); // 106));
			optionsPanel.setBorder(BorderFactory
					.createTitledBorder(PluginServices.getText(optionsPanel,
							"Options")));
			optionsPanel.add(getMultiLayerSelectionCBox());
			// optionsPanel.add(getAddBufferLayersCBox());
			optionsPanel.add(getAddInfluenceAreaLayersCBox());
		}

		return optionsPanel;
	}

	private JCheckBox getMultiLayerSelectionCBox() {
		if (multiLayerSelectionCBox == null) {
			multiLayerSelectionCBox = new JCheckBox();
			multiLayerSelectionCBox.setPreferredSize(new Dimension(330, 22));
			multiLayerSelectionCBox.setText(PluginServices.getText(
					multiLayerSelectionCBox, "multiLayer_selection"));
			multiLayerSelectionCBox.setSelected(true);
			multiLayerSelectionCBox.setToolTipText(PluginServices.getText(null,
					"multiLayerSelection_checkbox_TOOLTIP_HTML_explanation"));
		}

		return multiLayerSelectionCBox;
	}

	// private JCheckBox getAddBufferLayersCBox() {
	// if (addBufferLayersCBox == null) {
	// addBufferLayersCBox = new JCheckBox();
	// addBufferLayersCBox.setPreferredSize(new Dimension(330, 22));
	// addBufferLayersCBox.setText(PluginServices.getText(addBufferLayersCBox,
	// "add_buffer_layers"));
	// addBufferLayersCBox.setSelected(false);
	// addBufferLayersCBox.setToolTipText(PluginServices.getText(null,
	// "addBufferLayers_checkbox_TOOLTIP_HTML_explanation"));
	// }
	//
	// return addBufferLayersCBox;
	// }

	private JCheckBox getAddInfluenceAreaLayersCBox() {
		if (addInfluenceAreasLayersCBox == null) {
			addInfluenceAreasLayersCBox = new JCheckBox();
			addInfluenceAreasLayersCBox
					.setPreferredSize(new Dimension(330, 22));
			addInfluenceAreasLayersCBox.setText(PluginServices.getText(
					addInfluenceAreasLayersCBox, "add_influence_areas_layers"));
			addInfluenceAreasLayersCBox.setSelected(false);
			addInfluenceAreasLayersCBox
					.setToolTipText(PluginServices
							.getText(null,
									"addInfluenceAreasLayers_checkbox_TOOLTIP_HTML_explanation"));
		}

		return addInfluenceAreasLayersCBox;
	}

	private JPanel getWidthPanel() {
		if (widthPanel == null) {
			widthPanel = new JPanel();
			widthPanel.setPreferredSize(new Dimension(344, 55));
			widthPanel.setBorder(BorderFactory
					.createTitledBorder(PluginServices.getText(widthPanel,
							"Width")));
			widthPanel.setLayout(new FlowLayout());
			widthPanel.add(getWidthLabel());
			widthPanel.add(getWidthTextField());
			widthPanel.add(getDistanceUnitsLabel());
			widthPanel.add(getDistanceUnitsCombo());
		}

		return widthPanel;
	}

	private JLabel getWidthLabel() {
		if (widthLabel == null) {
			widthLabel = new JLabel();
			widthLabel.setPreferredSize(new Dimension(68, 22));
			widthLabel.setText(PluginServices.getText(widthLabel, "Width"));
			widthLabel.setToolTipText(PluginServices.getText(null,
					"bufferWidth_TOOLTIP_HTML_explanation"));
		}

		return widthLabel;
	}

	private JFormattedTextFieldSCP getWidthTextField() {
		if (distanceTextField == null) {
			DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setDecimalSeparatorAlwaysShown(true);
			decimalFormat.setMaximumIntegerDigits(12);
			decimalFormat.setMinimumIntegerDigits(1);
			decimalFormat.setMinimumFractionDigits(2);
			decimalFormat.setMaximumFractionDigits(4);

			NumberFormatter numberFormatter = new NumberFormatter();
			numberFormatter.setAllowsInvalid(false);
			numberFormatter.setOverwriteMode(false);
			numberFormatter.setCommitsOnValidEdit(true);
			numberFormatter.setMinimum(new Double(1));
			numberFormatter.setFormat(decimalFormat);

			// numberFormatter.setFormat(new
			// DecimalFormat("([+]|[-])?[0-9]+([.][0-9]+)?"));

			distanceTextField = new JFormattedTextFieldSCP(numberFormatter);
			distanceTextField.setPreferredSize(new Dimension(85, 22));
			distanceTextField.setValue(new Double(100.00));
			distanceTextField.setToolTipText(PluginServices.getText(null,
					"bufferWidth_TOOLTIP_HTML_explanation"));
		}

		return distanceTextField;
	}

	private JLabel getDistanceUnitsLabel() {
		if (distanceUnitsLabel == null) {
			distanceUnitsLabel = new JLabel(PluginServices.getText(
					distanceUnitsLabel, "Unit"));
			distanceUnitsLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
			distanceUnitsLabel.setPreferredSize(new Dimension(68, 22));
			distanceUnitsLabel.setToolTipText(PluginServices.getText(null,
					"distanceUnitsLabel_TOOLTIP_HTML_explanation"));
		}

		return distanceUnitsLabel;
	}

	private JComboBox getDistanceUnitsCombo() {
		if (distanceUnitsCombo == null) {
			distanceUnitsCombo = new JComboBox();
			distanceUnitsCombo.setPreferredSize(new Dimension(85, 22));
			distanceUnitsCombo.addItem("km");
			distanceUnitsCombo.addItem("m");
			distanceUnitsCombo.addItem("cm");
			distanceUnitsCombo.addItem("mm");
			distanceUnitsCombo.addItem("mi");
			distanceUnitsCombo.addItem("Ya");
			distanceUnitsCombo.addItem("ft");
			distanceUnitsCombo.addItem("in");
			distanceUnitsCombo.addItem("º");
			distanceUnitsCombo.setSelectedIndex(1); // By default in meters
			distanceUnitsCombo.setToolTipText(PluginServices.getText(null,
					"distanceUnitsLabel_TOOLTIP_HTML_explanation"));
		}

		return distanceUnitsCombo;
	}

	private JPanel getSidePanel() {
		if (sidePanel == null) {
			sidePanel = new JPanel();
			sidePanel.setLayout(new FlowLayout());
			sidePanel.setPreferredSize(new Dimension(344, 166)); // 160));
			sidePanel.setBorder(BorderFactory.createTitledBorder(PluginServices
					.getText(sidePanel, "Side")));
			sidePanel.setToolTipText(PluginServices.getText(null,
					"sideLabel_TOOLTIP_HTML_explanation"));
			sidePanel.add(getPolygonSidePanel());
			sidePanel.add(getLineSidePanel());
			sidePanel.add(getPointSidePanel());
			sidePanel.add(getMultiPointSidePanel());
		}

		return sidePanel;
	}

	private JPanel getPolygonSidePanel() {
		if (polygonSidePanel == null) {
			polygonSidePanel = new JPanel();
			polygonSidePanel.setPreferredSize(new Dimension(336, 28));
			polygonSidePanel.add(getPolygonSideLabel());
			polygonSidePanel.add(getPolygonSidesCombo());
		}

		return polygonSidePanel;
	}

	private JLabel getPolygonSideLabel() {
		if (polygonSideLabel == null) {
			polygonSideLabel = new JLabel(PluginServices.getText(
					polygonSideLabel, "Polygon"));
			polygonSideLabel.setPreferredSize(new Dimension(90, 22));
			polygonSideLabel.setToolTipText(PluginServices.getText(null,
					"polygonSideLabel_TOOLTIP_HTML_explanation"));
		}

		return polygonSideLabel;
	}

	private JComboBox getPolygonSidesCombo() {
		if (polygonSidesCombo == null) {
			polygonSidesCombo = new JComboBox();
			polygonSidesCombo.setPreferredSize(new Dimension(230, 22));
			polygonSidesCombo.addItem(outside);
			polygonSidesCombo.addItem(inside);
			// polygonSidesCombo.addItem(out_in_side); // Disabled because fails
			// quite often
			polygonSidesCombo.setToolTipText(PluginServices.getText(null,
					"polygonSideLabel_TOOLTIP_HTML_explanation"));
		}

		return polygonSidesCombo;
	}

	private JPanel getLineSidePanel() {
		if (lineSidePanel == null) {
			lineSidePanel = new JPanel();
			lineSidePanel.setPreferredSize(new Dimension(336, 28));
			lineSidePanel.add(getLineSideLabel());
			lineSidePanel.add(getLineSidesCombo());
		}

		return lineSidePanel;
	}

	private JLabel getLineSideLabel() {
		if (lineSideLabel == null) {
			lineSideLabel = new JLabel(PluginServices.getText(lineSideLabel,
					"Line"));
			lineSideLabel.setPreferredSize(new Dimension(90, 22));
			lineSideLabel.setToolTipText(PluginServices.getText(null,
					"lineSideLabel_TOOLTIP_HTML_explanation"));
		}

		return lineSideLabel;
	}

	private JComboBox getLineSidesCombo() {
		if (lineSidesCombo == null) {
			lineSidesCombo = new JComboBox();
			lineSidesCombo.setPreferredSize(new Dimension(230, 22));
			lineSidesCombo.addItem(outside);
			lineSidesCombo.setToolTipText(PluginServices.getText(null,
					"lineSideLabel_TOOLTIP_HTML_explanation"));
			// lineSidesCombo.setEnabled(false);
		}

		return lineSidesCombo;
	}

	private JPanel getPointSidePanel() {
		if (pointSidePanel == null) {
			pointSidePanel = new JPanel();
			pointSidePanel.setPreferredSize(new Dimension(336, 28));
			pointSidePanel.add(getPointSideLabel());
			pointSidePanel.add(getPointSidesCombo());
		}

		return pointSidePanel;
	}

	private JLabel getPointSideLabel() {
		if (pointSideLabel == null) {
			pointSideLabel = new JLabel(PluginServices.getText(pointSideLabel,
					"Point"));
			pointSideLabel.setPreferredSize(new Dimension(90, 22));
			pointSideLabel.setToolTipText(PluginServices.getText(null,
					"pointSideLabel_TOOLTIP_HTML_explanation"));
		}

		return pointSideLabel;
	}

	private JComboBox getPointSidesCombo() {
		if (pointSidesCombo == null) {
			pointSidesCombo = new JComboBox();
			pointSidesCombo.setPreferredSize(new Dimension(230, 22));
			pointSidesCombo.addItem(outside);
			pointSidesCombo.setToolTipText(PluginServices.getText(null,
					"pointSideLabel_TOOLTIP_HTML_explanation"));
			// pointSidesCombo.setEnabled(false);
		}

		return pointSidesCombo;
	}

	private JPanel getMultiPointSidePanel() {
		if (multiPointSidePanel == null) {
			multiPointSidePanel = new JPanel();
			multiPointSidePanel.setPreferredSize(new Dimension(336, 28));
			multiPointSidePanel.add(getMultiPointSideLabel());
			multiPointSidePanel.add(getMultiPointSidesCombo());
		}

		return multiPointSidePanel;
	}

	private JLabel getMultiPointSideLabel() {
		if (multiPointSideLabel == null) {
			multiPointSideLabel = new JLabel(PluginServices.getText(
					multiPointSideLabel, "MultiPoint"));
			multiPointSideLabel.setPreferredSize(new Dimension(90, 22));
			multiPointSideLabel.setToolTipText(PluginServices.getText(null,
					"multiPointSideLabel_TOOLTIP_HTML_explanation"));
		}

		return multiPointSideLabel;
	}

	private JComboBox getMultiPointSidesCombo() {
		if (multiPointSidesCombo == null) {
			multiPointSidesCombo = new JComboBox();
			multiPointSidesCombo.setPreferredSize(new Dimension(230, 22));
			multiPointSidesCombo.addItem(outside);
			multiPointSidesCombo.setToolTipText(PluginServices.getText(null,
					"multiPointSideLabel_TOOLTIP_HTML_explanation"));
			// multiPointSidesCombo.setEnabled(false);
		}

		return multiPointSidesCombo;
	}

	/**
	 * <p>
	 * This method initializes acceptCancelPanel.
	 * </p>
	 * 
	 * @return an adapted {@link AcceptCancelPanel AcceptCancelPanel}
	 */
	private AdaptedAcceptCancelPanel getAdaptedAcceptCancelPanel() {
		if (acceptCancelPanel == null) {
			acceptCancelPanel = new AdaptedAcceptCancelPanel();
		}

		return acceptCancelPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.ui.mdiManager.IWindow#getWindowInfo()
	 */
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this, "configuration"));
			viewInfo.setWidth(Window_Width);
			viewInfo.setHeight(Window_Height);
		}

		return viewInfo;
	}

	/**
	 * <p>
	 * Adapts {@link AcceptCancelPanel AcceptCancelPanel} to be used as a
	 * component of the <code>BufferConfigurationPanel</code> panel.
	 * </p>
	 * 
	 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
	 */
	private class AdaptedAcceptCancelPanel extends AcceptCancelPanel {

		public AdaptedAcceptCancelPanel() {
			super();

			addOkButtonActionListener(getOKAction());
			addCancelButtonActionListener(getCancelAction());
			setPreferredSize(new Dimension(350, 30));
		}

		/**
		 * <p>
		 * Create the action that will be executed when user pressed the
		 * <i>ok</i> button.
		 * </p>
		 * 
		 * @return action that will be executed when user pressed the
		 *         <i>cancel</i> button
		 */
		private ActionListener getOKAction() {
			// OK button action
			return new ActionListener() {
				/*
				 * @see
				 * java.awt.event.ActionListener#actionPerformed(java.awt.event
				 * .ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					/* 1- Closes this window */
					closeThis();

					/* 2- Validates the buffer width */
					double width;

					try {
						width = Double.parseDouble(getWidthTextField()
								.getText().replaceAll("(\\.)?", "")
								.replace(",", ".")); // Formats the decimal
														// number to be parsed
					} catch (Exception ex) {
						NotificationManager.showMessageError(
								PluginServices.getText(null, "Invalid_width"),
								ex);
						return;
					}

					/* 3- Creates the process */
					// checks layers to proccess if multilayer is not selected
					ArrayList tmpLayersToProccess = new ArrayList(layers.length);
					tmpLayersToProccess.addAll(Arrays.asList(layers));
					if (!multiLayerSelectionCBox.isSelected()) {
						Iterator iter = tmpLayersToProccess.iterator();
						FLyrVect curLayer;
						while (iter.hasNext()) {
							curLayer = (FLyrVect) iter.next();
							try {
								if (curLayer.getRecordset().getSelection()
										.cardinality() == 0) {
									iter.remove();
								}
							} catch (ReadDriverException e1) {
								NotificationManager.showMessageError(
										PluginServices.getText(null,
												"Failed_selecting_layer"), e1);
								return;

							}
						}
					}
					FLyrVect[] layersToProcess = (FLyrVect[]) tmpLayersToProccess
							.toArray(new FLyrVect[tmpLayersToProccess.size()]);

					BufferSelectionProcess iprocess = new BufferSelectionProcess(
							PluginServices.getText(this,
									"Selection_by_buffer_process"),
							PluginServices.getText(this,
									"Ongoing_process_please_wait"), mapControl,
							((SideInfo) getPolygonSidesCombo()
									.getSelectedItem()).getSide(),
							((SideInfo) getLineSidesCombo().getSelectedItem())
									.getSide(),
							((SideInfo) getPointSidesCombo().getSelectedItem())
									.getSide(),
							((SideInfo) getMultiPointSidesCombo()
									.getSelectedItem()).getSide(), width,
							(short) getDistanceUnitsCombo().getSelectedIndex(),
							layersToProcess, getAddInfluenceAreaLayersCBox()
									.isSelected(), getMultiLayerSelectionCBox()
									.isSelected());// getAddBufferLayersCBox().isSelected(),
													// getAddInfluenceAreaLayersCBox().isSelected(),
													// getMultiLayerSelectionCBox().isSelected());
					IncrementableTask iTask = new IncrementableTask(iprocess,
							new ProgressPanel(false));
					iTask.addIncrementableListener(iprocess);
					iprocess.setIncrementableTask(iTask);
					final BufferSelectionProcess f_iprocess = iprocess;
					final IncrementableTask f_iTask = iTask;

					iTask.getProgressPanel().addComponentListener(
							new ComponentAdapter() {
								/*
								 * (non-Javadoc)
								 * 
								 * @see
								 * java.awt.event.ComponentAdapter#componentHidden
								 * (java.awt.event.ComponentEvent)
								 */
								public void componentHidden(ComponentEvent e) {
									/*
									 * 5- If the process has failed, tries to
									 * reload the layers
									 */
									if (f_iprocess.getPercent() < 100) {
										/*
										 * 5.1- Forces to reload the active
										 * layers
										 */
										mapControl.drawMap(false);
										f_iTask.getProgressPanel().dispose();

										for (int i = 0; i < layers.length; i++) {
											try {
												layers[i].reload();
											} catch (Exception ex) {
												try {
													NotificationManager.showMessageError(
															PluginServices
																	.getText(
																			null,
																			"Failed_reloading_the_layer")
																	+ " "
																	+ layers[i]
																			.getName(),
															ex);
												} catch (Exception ex2) {
													NotificationManager.showMessageError(
															PluginServices
																	.getText(
																			null,
																			"Undefined_layer"),
															ex);
												}
											}
										}

										view.getTOC().setVisible(false);
										view.getTOC().setVisible(true);
									}

									/*
									 * 6- Writes in the gvSIG log the results of
									 * the process
									 */
									String text = "\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n"
											+ PluginServices
													.getText(this,
															"Summary_of_the_process_of_selecting_by_buffer")
											+ ":\n"
											+ f_iprocess.getLog()
											+ "\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n";
									PluginServices.getLogger().info(text);
								}
							});

					/* 4- Starts the process */
					iprocess.start();
					iTask.start();
				}
			};
		}

		/**
		 * <p>
		 * Create the action that will be executed when user pressed the
		 * <i>cancel</i> button.
		 * </p>
		 * 
		 * @return action that will be executed when user pressed the
		 *         <i>cancel</i> button
		 */
		private ActionListener getCancelAction() {
			// Cancel button action
			return new ActionListener() {
				/*
				 * @see
				 * java.awt.event.ActionListener#actionPerformed(java.awt.event
				 * .ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					closeThis();
				}
			};
		}
	}

	/**
	 * <p>
	 * Closes this window.
	 * </p>
	 */
	private void closeThis() {
		PluginServices.getMDIManager().closeWindow(this);
	}

	/**
	 * 
	 * 
	 * 
	 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
	 */
	private class SideInfo {
		private byte side;
		private String name;

		public SideInfo(byte side, String name) {
			this.side = side;
			this.name = name;
		}

		public String toString() {
			return name;
		}

		public byte getSide() {
			return side;
		}
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
