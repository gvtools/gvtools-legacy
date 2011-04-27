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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.html.HTMLEditorKit;

import org.gvsig.exceptions.BaseException;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.topology.IOneLyrRule;
import org.gvsig.topology.ITopologyErrorContainer;
import org.gvsig.topology.ITopologyErrorFix;
import org.gvsig.topology.ITopologyRule;
import org.gvsig.topology.ITopologyRuleWithExclusiveFix;
import org.gvsig.topology.ITwoLyrRule;
import org.gvsig.topology.SimpleTopologyErrorContainer;
import org.gvsig.topology.TopologyError;
import org.gvsig.topology.errorfixes.ITopologyErrorFixWithParameters;
import org.gvsig.topology.ui.util.BoxLayoutPanel;
import org.gvsig.topology.ui.util.GUIUtil;
import org.gvsig.topology.ui.util.PanelEntriesDialog;
import org.gvsig.util.GParameter;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiFrame.MDIFrame;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.threads.TopologyBatchCorrectionTask;

/**
 * Panel to show topology errors stored in a ITopologyErrorContainer.
 * 
 * Each topology error in the panel will have associated a popup menu which
 * allows to manage the error (view it in a gvSIG view, correct it, etc)
 * 
 * @author Alvaro Zabala
 * 
 */
public class TopologyErrorPanel extends BoxLayoutPanel {

	/**
	 * Table model to show topology errors in a table.
	 * @author Alvaro Zabala
	 *
	 */
	private final class TopologyErrorTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -5615838036054987191L;

		private ITopologyErrorContainer errorContainer;
		
		public void setErrorContainer(ITopologyErrorContainer errorContainer) {
			this.errorContainer = errorContainer;
		}

		public int getColumnCount() {
			return 7;
		}

		public int getRowCount() {
			return errorContainer.getNumberOfErrors();
		}

		public Object getValueAt(int row, int col) {
			Object solution = null;
			TopologyError error = errorContainer.getTopologyError(row);
			ITopologyRule violatedRule = null;

			switch (col) {
			case 0:
				violatedRule = error.getViolatedRule();
				solution = violatedRule.getName();
				break;
			case 1:
				violatedRule = error.getViolatedRule();
				solution = ((IOneLyrRule) violatedRule).getOriginLyr()
						.getName();
				break;
			case 2:
				violatedRule = error.getViolatedRule();
				if (violatedRule instanceof ITwoLyrRule) {
					ITwoLyrRule twoLyrRule = (ITwoLyrRule) violatedRule;
					solution = twoLyrRule.getDestinationLyr().getName();
				} else {
					solution = "";
				}

				break;
			case 3:
				int shapeType = error.getShapeType();
				switch (shapeType) {
				case FShape.POINT:
				case FShape.TEXT:
					solution = PluginServices.getText(this, "POINT");
					break;
				case FShape.POLYGON:
					solution = PluginServices.getText(this, "POLYGON");
					break;
				case FShape.LINE:
				case FShape.ARC:
				case FShape.CIRCLE:
				case FShape.ELLIPSE:
					//We have found that FGeometryCollection returns FShape.LINE
					//Are there any reason for this?
					if(error.getGeometry() instanceof FGeometryCollection)
						solution = PluginServices.getText(this, "MULTIGEOMETRY");
					else
						solution = PluginServices.getText(this, "LINE");
					break;
				case FShape.MULTI:
					solution = PluginServices.getText(this, "MULTI");
					break;
				case FShape.MULTIPOINT:
					solution = PluginServices.getText(this, "MULTIPOINT");
					break;
				}
				break;

			case 4:
				IFeature lyr1Feature = error.getFeature1();
				if(lyr1Feature != null)
					solution = lyr1Feature.getID();
				else
					solution = "";
				break;

			case 5:
				IFeature lyr2Feature = error.getFeature2();
				if (lyr2Feature != null)
					solution = lyr2Feature.getID();
				else
					solution = "";
				break;

			case 6:
				if (error.isException())
					solution = PluginServices.getText(this, "SI");
				else
					solution = PluginServices.getText(this, "NO");
				break;
			}
			return solution;
		}
	}

	private static final long serialVersionUID = 768284651667411053L;

	/**
	 * Has topology errors to show in this component (component's model)
	 */
	private ITopologyErrorContainer errorContainer;

	/*
	 * GUI components
	 */
	private JTable errorInspectorTable;
	private TopologyErrorTableModel dataModel;
	private JComboBox ruleFilterComboBox;
	private JCheckBox showErrors;
	private JCheckBox showExceptions;
	private JCheckBox showViewExtent;

	private final static String ALL_RULES_OPTION = PluginServices.getText(null,
			"ERROR_INSPECTOR_ALL_RULES");

	/**
	 * links the text showed in the combobox with the filter option of the
	 * topology error table
	 */
	private Map<String, ITopologyRule> ruleOption_rule;

//	private List<FGraphic> selectedErrors;
	
	/**
	 * Code of the symbol used to mark selections
	 */
//	int symbolSelectionCode;
	
	
	/**
	 * Constructor
	 * 
	 * @param errorContainer
	 */
	public TopologyErrorPanel(ITopologyErrorContainer errorContainer) {
		this.errorContainer = errorContainer;
//		this.selectedErrors = new ArrayList<FGraphic>();
		
//		ISymbol selectionSymbol = new SimpleFillSymbol().getSymbolForSelection();
//		IWindow f = PluginServices.getMDIManager().getActiveWindow();
//		View vista = (View)f;
//		symbolSelectionCode = vista.getMapControl().
//									getMapContext().
//									getGraphicsLayer().
//									addSymbol(selectionSymbol);
		
		initialize();
	}

	private void initialize() {
		this.addRow(new JComponent[] { new JLabel(PluginServices.getText(this,
				"ERROR_INSPECTOR_TITLE")) });

		this.addComponent(PluginServices.getText(this, "MOSTRAR"),
				getRuleFilterComboBox());

		JComponent[] row = new JComponent[] { getShowErrorsCb(),
				getShowExceptionsCb(), getShowViewExtentCb() };
		this.addRow(row, 600, DEFAULT_HEIGHT);

		this.addRow(new JComponent[] { getUpdateButton(), getBatchFixButton() }, 600,
						DEFAULT_HEIGHT);

		this.addRow(new JComponent[] { new JScrollPane(getErrorInspectorTable()) },
				600, 300);
	}

	private JComboBox getRuleFilterComboBox() {
		if (ruleFilterComboBox == null) {
			ruleFilterComboBox = new JComboBox();
			ruleOption_rule = new HashMap<String, ITopologyRule>();
			ruleFilterComboBox.addItem(ALL_RULES_OPTION);
			ruleOption_rule.put(ALL_RULES_OPTION, null);

			Map<ITopologyRule, ITopologyRule> violatedRules = new HashMap<ITopologyRule, ITopologyRule>();
			int errorCount = this.errorContainer.getNumberOfErrors();
			for (int i = 0; i < errorCount; i++) {
				TopologyError topologyError = errorContainer
						.getTopologyError(i);
				ITopologyRule violatedRule = topologyError.getViolatedRule();
				if (violatedRules.get(violatedRule) != null)
					continue;

				violatedRules.put(violatedRule, violatedRule);
				String rule_option = ((IOneLyrRule) violatedRule)
						.getOriginLyr().getName()
						+ " - ";
				if (violatedRule instanceof ITwoLyrRule) {
					ITwoLyrRule twoLyrRule = (ITwoLyrRule) violatedRule;
					rule_option += twoLyrRule.getDestinationLyr().getName() + " - ";
				}
				rule_option += violatedRule.getName();

				ruleOption_rule.put(rule_option, violatedRule);
				ruleFilterComboBox.addItem(rule_option);
			}// for
		}// if
		return ruleFilterComboBox;
	}

	private JCheckBox getShowErrorsCb() {
		if (showErrors == null) {
			showErrors = new JCheckBox(PluginServices.getText(this, "ERRORS"));
			showErrors.setSelected(true);
		}
		return showErrors;
	}

	private JCheckBox getShowExceptionsCb() {
		if (showExceptions == null) {
			showExceptions = new JCheckBox(PluginServices.getText(this,
					"EXCEPTIONS"));
			showExceptions.setSelected(true);
		}
		return showExceptions;
	}

	private JCheckBox getShowViewExtentCb() {
		showViewExtent = new JCheckBox(PluginServices.getText(this,
				"VIEW_EXTENT"));
		return showViewExtent;
	}

	private JButton getUpdateButton() {
		JButton updateButton = new JButton(PluginServices.getText(this,
				"UPDATE"));
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateErrorTable();
			}
		});
		return updateButton;
	}
	
	private JButton getBatchFixButton(){
		JButton batchFixButton = new JButton(PluginServices.getText(this, "BATCH_FIX"));
		batchFixButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				batchFix();
			}

			
		});
		return batchFixButton;
	}
	
	private void batchFix() {
		ITopologyRule selectedRule = getSelectedRule();
		if(selectedRule == null){
			GUIUtil.getInstance().messageBox(PluginServices.getText(this, "Must_select_a_rule_to_correct"), PluginServices.getText(this, "ERROR"));
			return;
		}
		ITopologyErrorFix errorFix = selectedRule.getDefaultFixFor(null);
		if(errorFix instanceof ITopologyErrorFixWithParameters){
			GUIUtil.getInstance().messageBox(PluginServices.getText(this, "correction_needs_user_interaction"),
					PluginServices.getText(this, "could_not_launch_process"));
			return;
		}
		
		TopologyBatchCorrectionTask task = new TopologyBatchCorrectionTask(this.errorContainer, selectedRule);
		PluginServices.cancelableBackgroundExecution(task);
		
	}

	private JTable getErrorInspectorTable() {
		if (errorInspectorTable == null) {
			errorInspectorTable = new JTable();
			dataModel = new TopologyErrorTableModel();
			dataModel.setErrorContainer(errorContainer);
			errorInspectorTable.setModel(dataModel);
			errorInspectorTable.getColumnModel().getColumn(0).setHeaderValue(
					PluginServices.getText(this, "Rule_type"));

			errorInspectorTable.getColumnModel().getColumn(1).setHeaderValue(
					PluginServices.getText(this, "Layer_1"));

			errorInspectorTable.getColumnModel().getColumn(2).setHeaderValue(
					PluginServices.getText(this, "Layer_2"));

			errorInspectorTable.getColumnModel().getColumn(3).setHeaderValue(
					PluginServices.getText(this, "Shape_Type"));

			errorInspectorTable.getColumnModel().getColumn(4).setHeaderValue(
					PluginServices.getText(this, "Feature_1"));

			errorInspectorTable.getColumnModel().getColumn(5).setHeaderValue(
					PluginServices.getText(this, "Feature_2"));

			errorInspectorTable.getColumnModel().getColumn(6).setHeaderValue(
					PluginServices.getText(this, "Exception"));
		}

		errorInspectorTable.addMouseListener(new MouseAdapter() {
			
			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger() && errorInspectorTable.isEnabled()) {
					Point p = new Point(e.getX(), e.getY());
					int row = errorInspectorTable.rowAtPoint(p);
					if (row >= 0 && row < errorInspectorTable.getRowCount()) {
						JPopupMenu contextMenu = createContextMenu(row);
						if (contextMenu != null
								&& contextMenu.getComponentCount() > 0) {
							contextMenu.show(errorInspectorTable, p.x, p.y);
						}//if
					}//if
				}//if
			}

			private JPopupMenu createContextMenu(final int row) {
				IWindow f = PluginServices.getMDIManager().getActiveWindow();
				if(! (f instanceof View))
					return null;
				final View vista = (View)f;
				final TopologyError error = errorContainer.getTopologyError(row);
				
				JPopupMenu contextMenu = new JPopupMenu();
				JMenuItem panMenu = new JMenuItem();
				panMenu.setText(PluginServices.getText(this, "PAN_TO"));
				panMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						IProjectView model = vista.getModel();
						MapContext mapa = model.getMapContext();
						Rectangle2D currentExtent = mapa.getViewPort().getExtent();
						double width = currentExtent.getWidth();
						double height = currentExtent.getHeight();
						
						Rectangle2D errorBounds = error.getGeometry().getBounds2D();
						double centerX = errorBounds.getCenterX();
						double centerY = errorBounds.getCenterY();
						
						Rectangle2D newExtent = new Rectangle2D.Double(centerX - (width / 2),
																	  centerY - (height / 2), 
																		width, 
																	    height);
						mapa.getViewPort().setExtent(newExtent);
						((ProjectDocument)vista.getModel()).setModified(true);
						
					}
					
				});
				contextMenu.add(panMenu);
				
				
				JMenuItem zoomMenu = new JMenuItem();
				zoomMenu.setText(PluginServices.getText(this, "ZOOM_TO"));
				zoomMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						IProjectView model = vista.getModel();
						MapContext mapa = model.getMapContext();
						Rectangle2D errorBounds = null;
						
						errorBounds = error.getGeometry().getBounds2D();
//						if(error.getGeometry() instanceof FGeometryCollection){
//							errorBounds = ((FGeometryCollection) error.getGeometry()).getGeometries()[0].getBounds2D();
//						}else
//							errorBounds = error.getGeometry().getBounds2D();
//						
//						double w = 600d;
//						double h = 400d;
//						
//						if(errorBounds.getWidth() > w)
//							w = errorBounds.getWidth();
//						if(errorBounds.getHeight() > h)
//							h = errorBounds.getHeight();
//						
//						double minx = errorBounds.getMinX();
//						double miny = errorBounds.getMinY();
//						
//						Rectangle2D newExtent = new Rectangle2D.Double(minx, 
//																	   miny, 
//																	   w / 2, 
//																	   h / 2);
//						//we select the error to make easy its localization in map
						FLyrVect errorLyr = (FLyrVect) error.getTopology().getErrorLayer();
						try {
							errorLyr.getRecordset().clearSelection();
							FBitSet newSelection = new FBitSet();
							newSelection.set(row);
							errorLyr.getRecordset().setSelection(newSelection);
						} catch (ReadDriverException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						Rectangle2D newExtent = errorBounds;
						mapa.getViewPort().setExtent(newExtent);
						((ProjectDocument)vista.getModel()).setModified(true);
					}
				});
				contextMenu.add(zoomMenu);
				contextMenu.addSeparator();
				
				JMenuItem viewDescriptionMenu = new JMenuItem();
				viewDescriptionMenu.setText(PluginServices.getText(this, "VIEW_RULE_DESCRIPTION"));
				viewDescriptionMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showViolatedRuleDescriptionDialog(error);
					}

					private void showViolatedRuleDescriptionDialog(
							final TopologyError error) {
						
						
						class DescriptionPanel extends JPanel implements IWindow{
							public WindowInfo getWindowInfo() {
								WindowInfo solution = new WindowInfo(WindowInfo.MODALDIALOG|
													  WindowInfo.PALETTE|
													  WindowInfo.ICONIFIABLE|
													  WindowInfo.RESIZABLE|WindowInfo.MAXIMIZABLE);
								solution.setWidth(300);
								solution.setHeight(300);
								return solution;
							}
							public Object getWindowProfile() {
								return WindowInfo.DIALOG_PROFILE;
							}
							
						}
						
						final DescriptionPanel descriptionPanel =
							new DescriptionPanel();
						descriptionPanel.setLayout(new BorderLayout());
						
						JScrollPane rightPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
						JEditorPane htmlViewer = new JEditorPane(){
							public URL getPage() {
								return null;
							}
						};
						htmlViewer.setEditable(false);
						htmlViewer.setEditorKit(new HTMLEditorKit());
						rightPanel.setViewportView(htmlViewer);
						rightPanel.setPreferredSize(new Dimension(250, 140));
						try {
							ITopologyRule rule = error.getViolatedRule();
							URL description = rule.getDescription();
							htmlViewer.setPage(description);	 
						} catch (Exception ex) {
							htmlViewer.setText("<p>"+PluginServices.getText(this, "UNAVAILABLE_DESCRIPTION")+"</p>");
						}
						
						JPanel southPanel = new JPanel();
						JButton okButton = new JButton(PluginServices.getText(this, "OK"));
						okButton.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent arg0) {
								Window parentWindow = GUIUtil.getInstance().getParentWindow(descriptionPanel);
								parentWindow.setVisible(false);
								parentWindow.dispose();
							}});
						
						southPanel.setLayout(new BorderLayout());
						JPanel aux = new JPanel();
						aux.add(okButton, BorderLayout.EAST);
						southPanel.add(aux, BorderLayout.EAST);
						
						descriptionPanel.add(rightPanel, BorderLayout.CENTER);
						descriptionPanel.add(southPanel, BorderLayout.SOUTH);
						descriptionPanel.setSize(new Dimension(300, 300));
						PluginServices.getMDIManager().addWindow(descriptionPanel);
					}
				});
				contextMenu.add(viewDescriptionMenu);
				contextMenu.addSeparator();
				
				
				JMenuItem markAsExceptionMenu = new JMenuItem();
				markAsExceptionMenu.setText(PluginServices.getText(this, "MARK_AS_EXCEPTION"));
				markAsExceptionMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						error.getTopology().markAsTopologyException(error);
						updateErrorTable();
					}
				});
				contextMenu.add(markAsExceptionMenu);
				
				contextMenu.addSeparator();
				
				ITopologyRule violatedRule = error.getViolatedRule();
				List<ITopologyErrorFix> errorFixes = null;
				if(error.getViolatedRule() instanceof ITopologyRuleWithExclusiveFix){
					errorFixes = ((ITopologyRuleWithExclusiveFix)violatedRule).getExclusiveErrorFixes(error);
				}else{
					errorFixes = violatedRule.getAutomaticErrorFixes();
				}
				
				for(int i = 0; i < errorFixes.size(); i++){
					final ITopologyErrorFix errorFix = errorFixes.get(i);
					JMenuItem fixMenuItem = new JMenuItem();
					fixMenuItem.setText(errorFix.getEditionDescription());
					fixMenuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(errorFix instanceof ITopologyErrorFixWithParameters){
								ITopologyErrorFixWithParameters fix =
									(ITopologyErrorFixWithParameters) errorFix;
								fix.initialize(error);
								final GParameter[] parameters = fix.getParameters();
								
								Window topologyErrorPanelContainer = (Window)GUIUtil.
																				getInstance().
																				getParentOfType(TopologyErrorPanel.this, Window.class);
								
								PanelEntriesDialog panelEntriesDialog = new PanelEntriesDialog(PluginServices.getText(this, "Fix_parameters"), 
																		   400, 130, parameters, topologyErrorPanelContainer);
								panelEntriesDialog.addOkActionListener(new ActionListener(){
									public void actionPerformed(ActionEvent arg0) {
										try {
											//previously we check if user selected a param
											for(int z = 0; z < parameters.length; z++){
												GParameter parameter = parameters[z];
												Object value = parameter.getValue();
												Object defaultValue = parameter.getDefaultValue();
												if(  value == null || value.equals(defaultValue))
													parameter.setValue(defaultValue);
												
											}//for z
											errorFix.fix(error);
											GUIUtil.getInstance().messageBox(PluginServices.getText(this, "ERROR_FIX_SUCCESS"),
																			 PluginServices.getText(this, "INFO"));
											//now we update the error panel and the error layer
											updateErrorTable();
											vista.getMapControl().repaint();
											
										} catch (BaseException e1) {
											GUIUtil.getInstance().messageBox(PluginServices.getText(this, "ERROR_FIX_ERROR")+e1.getFormatString(),
																			 PluginServices.getText(this, "ERROR"));
										}
									}});
							   
								JDialog entriesParent = new JDialog((Frame)PluginServices.getMainFrame(), true);
								entriesParent.setContentPane(panelEntriesDialog);
								entriesParent.setTitle(PluginServices.getText(this, "Parametros_De_Entrada_Para_La_Correccion"));
								entriesParent.setSize(290, 220);
								GUIUtil.getInstance().centerDialog(entriesParent, (MDIFrame) PluginServices.getMainFrame());
								entriesParent.setVisible(true);
							}else{
								try {
									errorFix.fix(error);
									JOptionPane.showMessageDialog(null, PluginServices.getText(this, "ERROR_FIX_SUCCESS"));
								} catch (BaseException e1) {
									JOptionPane.showMessageDialog(null, PluginServices.getText(this, "ERROR_FIX_ERROR")+e1.getFormatString());
								}
							}
						}
					});
					contextMenu.add(fixMenuItem);
				}
				return contextMenu;
			}

			
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
		});
		/*
		errorInspectorTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {
			if (e.getSource() == errorInspectorTable.getSelectionModel() && errorInspectorTable.getRowSelectionAllowed()) {
				IWindow f = PluginServices.getMDIManager().getActiveWindow();
				final View vista = (View)f; 
				MapControl mapControl = vista.getMapControl();
				MapContext map = mapControl.getMapContext();
				
				//first of all we delete the graphics of the previous selection
//				for(int i = 0; i < selectedErrors.size(); i++){
//					FGraphic oldGraphic = selectedErrors.get(i);
//					map.getGraphicsLayer().removeGraphic(oldGraphic);
//				}
//				mapControl.drawGraphics();
//				mapControl.repaint();
//				selectedErrors.clear();
				
				int first = e.getFirstIndex();
                int last = e.getLastIndex();
//                for(int i = first; i <= last; i++){
//                	TopologyError error = errorContainer.getTopologyError(i);
//                	IGeometry errorGeometry = error.getGeometry();
//                	FGraphic newGraphic = 
//                		new FGraphic(errorGeometry, symbolSelectionCode);
//                	map.getGraphicsLayer().addGraphic(newGraphic);
//                	selectedErrors.add(newGraphic);
//                }//for
//            	mapControl.drawGraphics();
//            	mapControl.repaint();
		      }//if 
			}});
			*/
		return errorInspectorTable;
	}
	
//	private JPanel getFixUserEntriesPanel(GParameter[] parameters){
//		BoxLayoutPanel solution = new BoxLayoutPanel();
//		solution.addRow(new JComponent[] { new JLabel(PluginServices.getText(this,
//				"Parametros_De_Entrada_Para_La_Correccion")) });
//		
//		final List<GParameterChangeListener> paramsListener
//		 = new ArrayList<GParameterChangeListener>();
//		
//		for(int i = 0; i < parameters.length; i++){
//			GParameter param = parameters[i];
//			GParameterChangeListener listener = solution.addComponentForParameter(param);
//			paramsListener.add(listener);
//		}
//
//		JButton bton = new JButton(PluginServices.getText(this, "Aceptar"));
//		bton.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent arg0) {
//				for(int i = 0; i < paramsListener.size(); i++){
//					paramsListener.get(i).parameterChange();
//				}
//			}});
//
//		solution.addRow(new JComponent[] { bton }, 600, DEFAULT_HEIGHT);
//		
//		
//		return solution;
//		
//	}

	public void updateErrorTable() {
		ITopologyRule filterRule = getSelectedRule();
		boolean showExceptions = isExceptionsSelected();
		boolean showErrors = isErrorsSelected();
		// show all topology errors
		if (filterRule == null && showExceptions && showErrors) {
			dataModel.setErrorContainer(errorContainer);
			getErrorInspectorTable().revalidate();
			return;
		}
		boolean filterByViewExtent = isOnlyViewExtentSelected();
		SimpleTopologyErrorContainer filteredErrorContainer = new SimpleTopologyErrorContainer();
		for (int i = 0; i < errorContainer.getNumberOfErrors(); i++) {
			TopologyError error = errorContainer.getTopologyError(i);

			if (filterByViewExtent) {
				Rectangle2D errorExtent = error.getGeometry().getBounds2D();
				View activeView = (View) PluginServices.getMDIManager()
						.getActiveWindow();
				Rectangle2D viewExtent = null;
				try {
					viewExtent = activeView.getMapControl().getMapContext()
							.getFullExtent();

					if (!errorExtent.intersects(viewExtent))
						continue;
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
			}
			if (error.isException() && !showExceptions) {
				continue;
			} else {
				if (!error.isException() && !showErrors) {
					continue;
				}
			}
			if(filterRule != null &&  error.getViolatedRule() != filterRule)
				continue;
			
			filteredErrorContainer.addTopologyError(error);
		}// for

		dataModel.setErrorContainer(filteredErrorContainer);
		getErrorInspectorTable().revalidate();
		this.repaint();

	}

	public boolean isErrorsSelected() {
		return showErrors.isSelected();
	}

	public boolean isExceptionsSelected() {
		return showExceptions.isSelected();
	}

	public boolean isOnlyViewExtentSelected() {
		return showViewExtent.isSelected();
	}

	public ITopologyRule getSelectedRule() {
		return ruleOption_rule.get(ruleFilterComboBox.getSelectedItem());
	}

}
