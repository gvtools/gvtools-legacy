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
package org.gvsig.topology.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.gvsig.gui.GridBagJWizardPanel;
import org.gvsig.topology.IRuleWithClusterTolerance;
import org.gvsig.topology.ITopologyRule;
import org.gvsig.topology.RuleNotAllowedException;
import org.gvsig.topology.Topology;
import org.gvsig.topology.TopologyRuleDefinitionException;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.impl.snapandcrack.fmap.SnapAndCrackGeoprocess;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * Wizard to create new topologies.
 * 
 * @author Alvaro Zabala
 * 
 */
public class NewTopologyWizard extends WizardAndami {

	private static final long serialVersionUID = 8668545841566876812L;

	private static ImageIcon icon = PluginServices.getIconTheme().get(
			"introductory-step-wizard");

	private static final Insets DEFAULT_INSETS = new Insets(4, 4, 4, 4);

	/**
	 * Panel to add layers to a topology, and specify topology properties
	 */
	private TopologyPropertiesAndLayersPanel propertiesPanel;
	/**
	 * Panel to add topology rules to the new topology
	 */
	private TopologyRulesPanel topologyRulesPanel;

	private WindowInfo viewInfo;

	/**
	 * MapContext of the active view which will have the topology
	 */
	private MapContext mapContext;

	/**
	 * Default Constructor
	 * 
	 * @param mapContext
	 *            mapContext of the active view.
	 */
	public NewTopologyWizard(MapContext mapContext) {
		super(icon);
		this.mapContext = mapContext;
		JWizardComponents wizardComponents = getWizardComponents();
		final MapContext tempMapContext = mapContext;
		getWizardComponents().setFinishAction(
				new FinishAction(wizardComponents) {
					public void performAction() {
						String topologyName = propertiesPanel.getTopologyName();
						// TODO Dar de alta un bug para FormattingTextField
						// cuando no se introduce ningun valor
						// y se llama a getDouble
						double clusterTolerance = propertiesPanel
								.getClusterTolerance();

						if (topologyName == "" || clusterTolerance < 0) {
							GUIUtil.getInstance().messageBox(
									"Message_topology_name_cluster",
									"Message_error_creating_topology");
							getWizardComponents().setCurrentIndex(1);
							// Meter antes un JOptionPane
							return;
						}

						final Topology newTopology = new Topology(
								tempMapContext, tempMapContext.getLayers());

						// trick to update TOC component
						newTopology
								.addLayerCollectionListener(new UpdateTopologyInTocLayerCollectionListener(
										tempMapContext, newTopology));
						int maxNumberOfErrors = propertiesPanel
								.getMaxNumberOfErrors();
						if (maxNumberOfErrors >= 0) {
							newTopology.setMaxNumberOfErrors(maxNumberOfErrors);
						} else {
							GUIUtil.getInstance().messageBox(
									"Message_max_number_of_errors",
									"Message_error_creating_topology");
							getWizardComponents().setCurrentIndex(1);
							// Meter antes un JOptionPane
							return;
						}
						newTopology.setName(topologyName);

						List<FLyrVect> lyrs = propertiesPanel.getLayers();
						if (lyrs.size() == 0) {
							GUIUtil.getInstance().messageBox(
									"Message_topology_layers",
									"Message_error_creating_topology");
							getWizardComponents().setCurrentIndex(1);
							return;
						}

						newTopology.setClusterTolerance(clusterTolerance);
						if (clusterTolerance != 0d) {
							// boolean applyCrackAndSnap = GUIUtil.getInstance()
							// .optionMessage(
							// "Message_apply_crack_process",
							// "Message_crack_title");
							boolean applyCrackAndSnap = false;
							// muy complejo, y seguramente requiera de
							// iteraciones.
							// lo dejamos para otras versiones
							if (applyCrackAndSnap) {
								boolean geoprocessSucessful = true;

								List<FLyrVect> crackedLyrs = new ArrayList<FLyrVect>();

								for (int i = 0; i < lyrs.size(); i++) {
									FLyrVect lyr = (FLyrVect) lyrs.get(i);

									ArrayList<FLayer> crackingLyrs = new ArrayList<FLayer>();
									for (int j = 0; j < lyrs.size(); j++) {
										if (i == j)
											continue;
										FLyrVect lyr2 = (FLyrVect) lyrs.get(j);
										crackingLyrs.add(lyr2);
									}// for j

									SnapAndCrackGeoprocess geoprocess = new SnapAndCrackGeoprocess(
											lyr, crackingLyrs);

									// FIXME Cuando todo esto esté funcionando
									// bien, la capa temporal
									// debe machacar a la capa original. El
									// problema es que los geoprocesos
									// solo pueden guardar sus resultados en
									// formato SHP
									String temp = System
											.getProperty("java.io.tmpdir")
											+ lyr.getName()
											+ System.currentTimeMillis()
											+ ".shp";
									File newFile = new File(temp);
									SHPLayerDefinition definition = (SHPLayerDefinition) geoprocess
											.createLayerDefinition();
									definition.setFile(newFile);
									ShpSchemaManager schemaManager = new ShpSchemaManager(
											newFile.getAbsolutePath());
									IWriter writer = null;
									try {
										int shapeType = definition
												.getShapeType();
										if (shapeType != XTypes.MULTI) {
											writer = new ShpWriter();
											((ShpWriter) writer)
													.setFile(definition
															.getFile());

										} else {
											writer = new MultiShpWriter();
											((MultiShpWriter) writer)
													.setFile(definition
															.getFile());
										}
										writer.initialize(definition);
									} catch (Exception e1) {
										String error = PluginServices.getText(
												this,
												"Error_escritura_resultados");
										String errorDescription = PluginServices
												.getText(this,
														"Error_preparar_escritura_resultados");
										GUIUtil.getInstance().messageBox(error,
												errorDescription);
										geoprocessSucessful = false;
										break;
									}

									geoprocess.setResultLayerProperties(writer,
											schemaManager);
									HashMap params = new HashMap();
									Double snapTolerance = new Double(
											clusterTolerance);
									params.put("snap_tolerance", snapTolerance);

									try {
										geoprocess.setParameters(params);
										geoprocess.checkPreconditions();
										IMonitorableTask task = geoprocess
												.createTask();

										task.run();

										// AddResultLayerTask task2 = new
										// AddResultLayerTask(geoprocess);
										// task2.setLayers(layers);
										// MonitorableDecoratorMainFirst
										// globalTask = new
										// MonitorableDecoratorMainFirst(task1,
										// task2);
										// if (globalTask.preprocess())
										// PluginServices.cancelableBackgroundExecution(globalTask);

										crackedLyrs.add((FLyrVect) geoprocess
												.getResult());

									} catch (GeoprocessException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
										geoprocessSucessful = false;
										break;
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										geoprocessSucessful = false;
										break;
									}
								}// for

								// FIXME ESTOY HAY QUE REFINARLO, PORQUE EN EL
								// TOC SE SEGUIRIAN
								// MOSTRANDO LAS CAPAS ANTIGUAS MIENTRAS QUE EN
								// LA TOPOLOGIA LO QUE
								// SE AÑADEN SON LAS CAPAS CRACKEADAS
								lyrs = crackedLyrs;

							}// if
						}// if

						for (int i = 0; i < lyrs.size(); i++) {
							FLyrVect lyr = lyrs.get(i);
							newTopology.addLayer(lyr);
						}
						// finally we add the rules to the layer
						List<ITopologyRule> rules = topologyRulesPanel
								.getRules();
						try {
							for (int i = 0; i < rules.size(); i++) {
								ITopologyRule rule = rules.get(i);
								if (rule instanceof IRuleWithClusterTolerance) {
									IRuleWithClusterTolerance withCluster = (IRuleWithClusterTolerance) rule;
									withCluster
											.setClusterTolerance(clusterTolerance);
								}
								newTopology.addRule(rule);

							}// for
						} catch (RuleNotAllowedException e) {
							GUIUtil.getInstance().messageBox(
									"Message_rule_not_allowed",
									"Message_error_creating_topology");
							getWizardComponents().setCurrentIndex(2);
							return;

						} catch (TopologyRuleDefinitionException e) {
							GUIUtil.getInstance().messageBox(
									"Message_rule_definition",
									"Message_error_creating_topology");
							getWizardComponents().setCurrentIndex(2);
							return;
						}

						GUIUtil.getInstance().addTopologyToTOC(tempMapContext,
								lyrs, newTopology);

						getWizardComponents().getCancelAction().performAction();
					}
				});

		IntroductoryWizardPanel introductoryPanel = new IntroductoryWizardPanel(
				this.getWizardComponents());
		getWizardComponents().addWizardPanel(introductoryPanel);

		propertiesPanel = new TopologyPropertiesAndLayersPanel(
				this.getWizardComponents());
		getWizardComponents().addWizardPanel(propertiesPanel);

		topologyRulesPanel = new TopologyRulesPanel(this.getWizardComponents(),
				propertiesPanel);
		getWizardComponents().addWizardPanel(topologyRulesPanel);
	}

	static String title1 = PluginServices.getText(null, "Create_Topology");

	/**
	 * First step of the wizard. It shows an explanatory text about topology
	 * creation process.
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	class IntroductoryWizardPanel extends JWizardPanel {
		private static final long serialVersionUID = 6401035685044739900L;

		public IntroductoryWizardPanel(JWizardComponents wizardComponents) {
			super(wizardComponents, title1);
			initialize();
		}

		public void initialize() {
			setLayout(new java.awt.BorderLayout());

			String text1 = PluginServices.getText(this, "Introductory_text_1");
			String text2 = PluginServices.getText(this, "Introductory_text_2");
			String text3 = PluginServices.getText(this, "Introductory_text_3");

			JLabel text = new JLabel(
					"<html><body><p valign='top' halign='left'>" + text1
							+ "<br><br>" + text2 + "<br><br>" + text3
							+ "</p></body></html>");
			text.setHorizontalAlignment(JLabel.LEFT);
			text.setVerticalAlignment(JLabel.TOP);
			add(text);
		}
	}

	static String title2 = PluginServices.getText(null, "Topology_properties");

	class TopologyPropertiesAndLayersPanel extends GridBagJWizardPanel
			implements ITopologyContentProvider {

		private static final long serialVersionUID = 3023623366578284892L;
		JTextField nameTextField;
		JTextField clusterTolTextField;
		JTextField maxNumberOfErrorsTextField;
		LayersInTocPanel lyrsPanel;

		/**
		 * Constructor
		 * 
		 * @param wizardComponents
		 */
		public TopologyPropertiesAndLayersPanel(
				JWizardComponents wizardComponents) {
			super(wizardComponents, title2);
			initialize();
		}

		private void initialize() {
			nameTextField = new JTextField();
			addComponent(PluginServices.getText(null, "topology_name"),
					nameTextField);
			addBlank();

			clusterTolTextField = new JTextField(8);
			addComponent(PluginServices.getText(null, "cluster_tolerance"),
					clusterTolTextField);

			maxNumberOfErrorsTextField = new JTextField(8);
			addComponent(PluginServices.getText(null, "max_number_of_errors"),
					maxNumberOfErrorsTextField);

			addComponent(new JLabel(PluginServices.getText(null,
					"lyr_selection_text")));

			// JPanel lyrsPanel = new JPanel();
			// lyrsPanel.setLayout(new BoxLayout(lyrsPanel, BoxLayout.Y_AXIS ));
			// LayersIterator lyrIt = new
			// LayersIterator(mapContext.getLayers());
			// while (lyrIt.hasNext()) {
			// FLayer lyr = lyrIt.nextLayer();
			// if (!lyr.isInTOC())
			// continue;
			// if (lyr instanceof FLyrVect) {
			// FLyrVect lyrVect = (FLyrVect) lyr;
			// JCheckBox checkBoxLyr = new JCheckBox(lyrVect.getName());
			// lyrsPanel.add(checkBoxLyr);
			// lyr_checkbox.put(lyrVect, checkBoxLyr);
			// }// if
			// }// while

			lyrsPanel = new LayersInTocPanel(mapContext.getLayers());
			JScrollPane scrollPane = new JScrollPane(lyrsPanel);
			addComponent(scrollPane, DEFAULT_INSETS, 2, GridBagConstraints.BOTH);
			setBorder(javax.swing.BorderFactory
					.createLineBorder(java.awt.Color.BLACK));
		}

		public String getTopologyName() {
			return nameTextField.getText();
		}

		public int getMaxNumberOfErrors() {
			try {
				return Integer.parseInt(maxNumberOfErrorsTextField.getText());
			} catch (NumberFormatException e) {
				// GUIUtil.getInstance().optionMessage(PluginServices.getText(this,
				// "Max_number_Errors_not_numeric"),
				// PluginServices.getText(this, "Message_error_in_data_input"));
				return -1;
			}
		}

		public double getClusterTolerance() {
			try {
				return Double.parseDouble(clusterTolTextField.getText());
			} catch (NumberFormatException e) {
				// GUIUtil.getInstance().optionMessage(PluginServices.getText(this,
				// "Message_cluster_tol_not_numeric"),
				// PluginServices.getText(this, "Message_error_in_data_input"));
				return -1;
			}
		}

		/**
		 * Implementation of layerprovider (this component provides layers to
		 * other components by the user selection with many checkboxes)
		 * 
		 */
		public List<FLyrVect> getLayers() {
			// List<FLyrVect> solution = new ArrayList<FLyrVect>();
			// LayersIterator lyrIt = new
			// LayersIterator(mapContext.getLayers());
			// while (lyrIt.hasNext()) {
			// FLayer lyr = lyrIt.nextLayer();
			// if (!lyr.isInTOC())
			// continue;
			// if (lyr instanceof FLyrVect) {
			// FLyrVect lyrVect = (FLyrVect) lyr;
			// JCheckBox checkBox = lyr_checkbox.get(lyr);
			// if (checkBox.isSelected())
			// solution.add(lyrVect);
			// }
			// }
			// return solution;
			return lyrsPanel.getSelectedLyrs();
		}

		public MapContext getMapContext() {
			return mapContext;
		}

		public List<ITopologyRule> getRules() {
			// this class doesnt provide rules
			return null;
		}
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG
					| WindowInfo.RESIZABLE);
		}
		return viewInfo;
	}

	public Object getWindowProfile() {
		// TODO Auto-generated method stub
		return WindowInfo.TOOL_PROFILE;
	}

}
