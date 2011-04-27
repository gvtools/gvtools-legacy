package es.iver.derivedGeom.gui;

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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.specificcaretposition.JTextFieldWithSCP;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toc.TocItemBranch;
import com.iver.utiles.SimpleFileFilter;

import es.iver.derivedGeom.process.DerivedGeometryProcessParameters;
import es.iver.derivedGeom.utils.FShapeTypeNames;

/**
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class LayerAndProcessSelectionPanel extends JPanel implements IWindow {
	private WindowInfo viewInfo = null;
	private View view = null;
	private ImageIcon bIcon = null;
	private JLabel layersLabel = null;
	private JLabel labelName = null;
	private JLabel labelPath = null;
	private JLabel destProcessLabel = null;
	private JLabel destShapeTypeLabel = null;
	private JPanel northPanel = null;
	private JPanel destProcessPanel = null;
	private JPanel optionsPanel = null;
    private JPanel layersPanel = null;
    private JPanel destLayerNamePanel = null;
    private JPanel destPathPanel = null;
    private JPanel destLayerTypePanel = null;
	private JComboBox layersComboBox = null;
	private JTextFieldWithSCP destLayerName = null;
	private JComboBox destProcessCombo = null;
	private JTextFieldWithSCP destLayerPath = null;
	private JComboBox destShapeTypeCombo = null;
	private JButton jButtonSelectPath = null;
    private final int layersComboBox_Width = 390;
    private final int layersComboBox_Height = 22;
    private final short Window_Width = 480;
    private final short Window_Height = 280;
	private AdaptedAcceptCancelPanel acceptCancelPanel = null;
	private DerivedGeometryProcessParameters parameters = null;
	private final String SHAPE_EXTENSION = "shp";
	private ProcessType pointsToLine, pointsToPolygon, closeMultiLine, pointsToLineOrPolygon;
	private OutputSHPType line, polygon, multi;
	private boolean started = false;

	
	public LayerAndProcessSelectionPanel(View view) {
		this.view = view;

		initialize();
	}

	/**
	 * <p>
	 * Initializes this component.
	 * </p>
	 */
	private void initialize() {
		bIcon = PluginServices.getIconTheme().get("layerGroup");
		parameters = new DerivedGeometryProcessParameters(null, null, null, null, DerivedGeometryProcessParameters.UNDEFINED_TYPE, view, DerivedGeometryProcessParameters.UNDEFINED_TYPE);
		
		pointsToLine = new ProcessType(DerivedGeometryProcessParameters.POINTS_TO_LINE_PROCESS_NAME, DerivedGeometryProcessParameters.POINTS_TO_LINE_PROCESS_ID);
		pointsToPolygon = new ProcessType(DerivedGeometryProcessParameters.POINTS_TO_POLYGON_PROCESS_NAME, DerivedGeometryProcessParameters.POINTS_TO_POLYGON_PROCESS_ID);
		closeMultiLine = new ProcessType(DerivedGeometryProcessParameters.CLOSE_MULTILINE_PROCESS_NAME, DerivedGeometryProcessParameters.CLOSE_MULTILINE_PROCESS_ID);
		pointsToLineOrPolygon = new ProcessType(DerivedGeometryProcessParameters.POINTS_TO_LINE_OR_POLYGON_NAME, DerivedGeometryProcessParameters.POINTS_TO_LINE_OR_POLYGON_ID);

		line = new OutputSHPType(PluginServices.getText(null, "Lines"), FShape.LINE);
		polygon = new OutputSHPType(PluginServices.getText(null, "Polygons"), FShape.POLYGON);
		multi = new OutputSHPType(PluginServices.getText(null, "Multi"), FShape.MULTI);

		setLayout(new FlowLayout());
		add(getNorthPanel());
		add(getDestProcessPanel());
		add(getOptionsPanel());
		add(getAdaptedAcceptCancelPanel());
		getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);

    	refreshVisibleVectorLayers();

		setVisible(false);

    	if (! refreshVisibleVectorLayers()) {
    		JOptionPane.showMessageDialog(this, PluginServices.getText(this, "No_vector_layer_can_be_save_changes"), PluginServices.getText(this, "Warning"), JOptionPane.WARNING_MESSAGE);
    	} else {
    		setVisible(true);
    		started = true;
    		PluginServices.getMDIManager().addWindow(this);
    	}
	}

    /**
	 * <p>This method initializes northPanel.</p>	
	 * 	
	 * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
    	if (northPanel == null) {
    		northPanel = new JPanel();
    		northPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "Source_layer")));
    		northPanel.setLayout(new FlowLayout());
    		northPanel.setPreferredSize(new Dimension(470, 70));
    		northPanel.add(getLayersPanel());
    	}
    	
    	return northPanel;
     }
    
	private JPanel getDestProcessPanel() {
		if (destProcessPanel == null) {
			destProcessPanel = new JPanel();
			destProcessPanel.setLayout(new FlowLayout());
			destProcessPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "Output_layer")));
			destProcessPanel.setPreferredSize(new Dimension(470, 136));
			destProcessPanel.add(getDestLayerNamePanel());
			destProcessPanel.add(getDestPathPanel());
			destProcessPanel.add(getDestLayerTypePanel());
		}
		
		return destProcessPanel;
	}

	private JPanel getDestLayerNamePanel() {
		if (destLayerNamePanel == null) {
			destLayerNamePanel = new JPanel();
			destLayerNamePanel.setPreferredSize(new Dimension(450, 30));
			destLayerNamePanel.setLayout(new FlowLayout());
			destLayerNamePanel.add(getLabelName());
			destLayerNamePanel.add(getDestLayerName());
		}
		
		return destLayerNamePanel;
	}

	private JLabel getLabelName() {
		if (labelName == null) {
			labelName = new JLabel(PluginServices.getText(null, "Name"));
			labelName.setPreferredSize(new Dimension(70, 20));
			labelName.setToolTipText(PluginServices.getText(null, "Name"));
		}

		return labelName;
	}
	
	private JPanel getDestPathPanel() {
		if (destPathPanel == null) {
			destPathPanel = new JPanel();
			destPathPanel.setLayout(new FlowLayout());
			destPathPanel.setPreferredSize(new Dimension(450, 30));
			destPathPanel.add(getLabelPath());
			destPathPanel.add(getDestLayerPath());
			destPathPanel.add(getJButtonSelectPath());
		}
		
		return destPathPanel;
	}
	
	private JPanel getDestLayerTypePanel() {
		if (destLayerTypePanel == null) {
			destLayerTypePanel = new JPanel();
			destLayerTypePanel.setPreferredSize(new Dimension(450, 30));
			destLayerTypePanel.add(getDestShapeTypeLabel());
			destLayerTypePanel.add(getDestShapeTypeCombo());
		}
		
		return destLayerTypePanel;
	}

	private JLabel getDestShapeTypeLabel() {
		if (destShapeTypeLabel == null) {
			destShapeTypeLabel = new JLabel(PluginServices.getText(null, "Type"));
			destShapeTypeLabel.setPreferredSize(new Dimension(178, 20));
			destShapeTypeLabel.setToolTipText(PluginServices.getText(null, "Type"));
		}
		
		return destShapeTypeLabel;
	}

	private JComboBox getDestShapeTypeCombo() {
		if (destShapeTypeCombo == null) {
			destShapeTypeCombo = new JComboBox();
			destShapeTypeCombo.setToolTipText(PluginServices.getText(null, "Destination_layer_type"));
    		destShapeTypeCombo.setPreferredSize(new Dimension(254, 20));
			destShapeTypeCombo.addItemListener(new ItemListener() {
				/*
				 * (non-Javadoc)
				 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
				 */
				public void itemStateChanged(ItemEvent e) {
					JComboBox source = (JComboBox) e.getSource();

					if (source.getItemCount() == 0) {
						parameters.setDestinationLayerShapeType(DerivedGeometryProcessParameters.UNDEFINED_TYPE);
					}
					else {
						parameters.setDestinationLayerShapeType(((OutputSHPType)source.getSelectedItem()).getOutputSHPType());
					}
				}
			});
		}
		
		return destShapeTypeCombo;
	}
	
	private JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel();
			optionsPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "Options")));
			optionsPanel.setLayout(new FlowLayout());
			optionsPanel.setPreferredSize(new Dimension(470, 60));
			optionsPanel.add(getProcessLabel());
			optionsPanel.add(getProcessCombo());
		}
		
		return optionsPanel;
	}

	private JLabel getLabelPath() {
		if (labelPath == null) {
			labelPath = new JLabel(PluginServices.getText(null, "Path"));
			labelPath.setPreferredSize(new Dimension(70, 20));
			labelPath.setToolTipText(PluginServices.getText(null, "Path"));
		}
		
		return labelPath;
	}
    
	private JTextFieldWithSCP getDestLayerPath() {
		if (destLayerPath == null) {
			destLayerPath = new JTextFieldWithSCP();
			destLayerPath.setEditable(false);
			destLayerPath.setBackground(Color.white);
			destLayerPath.setPreferredSize(new Dimension(300, 20));
			destLayerPath.setToolTipText(PluginServices.getText(null, "Path_where_create_the_new_layer_files"));
		}

		return destLayerPath;
	}

	/**
	 * <p>This method initializes <code>jButtonSelectPath</code>.</p>
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonSelectPath() {
		if (jButtonSelectPath == null) {
			jButtonSelectPath = new JButton();
			jButtonSelectPath.setToolTipText(PluginServices.getText(this, "Select_the_path"));
			jButtonSelectPath.setText("...");
			jButtonSelectPath.setPreferredSize(new Dimension(60, 20));
			jButtonSelectPath.addActionListener(new java.awt.event.ActionListener() {
				/*
				 * (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(java.awt.event.ActionEvent e) {
		            JFileChooser jfc = new JFileChooser();

		            // Removes the default choosable file filter
		            FileFilter[] filters = jfc.getChoosableFileFilters();

		            for (int i = 0; i < filters.length; i++)
		            	jfc.removeChoosableFileFilter(filters[i]);
		            
		            // Displays only directories
		            //jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		            // Only SHP
		            SimpleFileFilter filterSHP = new SimpleFileFilter(SHAPE_EXTENSION, PluginServices.getText(this, "Ficheros_SHP"));
		            jfc.addChoosableFileFilter(filterSHP);
		            jfc.setFileFilter(filterSHP);
		            
		            jfc.addActionListener(new AbstractAction() {
		            	/*
		            	 * (non-Javadoc)
		            	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		            	 */
						public void actionPerformed(ActionEvent evt) {
		                	JFileChooser jfc = (JFileChooser)evt.getSource();

		                    if (JFileChooser.APPROVE_SELECTION.equals(evt.getActionCommand())) {
		                    	File destFile = null;
		                    	
		                    	if (jfc.getSelectedFile() == null) {
		                    		getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
		                    		return;
		                    	}

		                    	if (jfc.getSelectedFile().getAbsoluteFile().getAbsolutePath().endsWith("." + SHAPE_EXTENSION)) {
		                    		destFile = jfc.getSelectedFile().getAbsoluteFile();
		                    	}
		                    	else {
		                    		destFile = new File(jfc.getSelectedFile().getAbsoluteFile().getAbsolutePath() + "." + SHAPE_EXTENSION);
		                    	}

		                    	parameters.setDestinationFile(destFile);
	                    		getDestLayerPath().setText(destFile.toString());
	                    		
	                    		if (getDestLayerName().getText().length() == 0)
	                    			getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
	                    		else {
	                    			getAdaptedAcceptCancelPanel().setOkButtonEnabled(true);
	                    		}
		                    } else if (JFileChooser.CANCEL_SELECTION.equals(evt.getActionCommand())) {
		                    	parameters.setDestinationFile(null);
		                    	getDestLayerPath().setText("");
		                    	getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
		                    }
		                }
		            });

		           jfc.showOpenDialog((Component) PluginServices.getMainFrame());
				}
			});
		}

		return jButtonSelectPath;
	}
    
    /**
	 * <p>This method initializes layersPanel.</p>	
	 * 	
	 * @return javax.swing.JPanel
     */
    private JPanel getLayersPanel() {
    	if (layersPanel == null) {
    		layersPanel = new JPanel();
    		layersPanel.setLayout(new FlowLayout());
    		layersPanel.setPreferredSize(new Dimension(460, 28));
    		layersPanel.add(getLayersLabel());
    		layersPanel.add(getLayersComboBox());
    	}
    	
    	return layersPanel;
    }

    /**
	 * <p>This method initializes layersLabel.</p>	
	 * 	
	 * @return javax.swing.JLabel
     */
    private JLabel getLayersLabel() {
    	if (layersLabel == null) {
    		layersLabel = new JLabel(PluginServices.getText(this, "Layer"));
    		layersLabel.setPreferredSize(new Dimension(40, 20));
    	}
    	
    	return layersLabel;
    }

	private JTextFieldWithSCP getDestLayerName() {
		if (destLayerName == null) {
			destLayerName = new JTextFieldWithSCP();
			destLayerName.setPreferredSize(new Dimension(364, 20));
			destLayerName.setToolTipText(PluginServices.getText(null, "Name_of_the_new_layer"));
			destLayerName.addKeyListener(new KeyAdapter() {
				/*
				 * (non-Javadoc)
				 * @see java.awt.event.KeyAdapter#keyReleased(java.awt.event.KeyEvent)
				 */
				public void keyReleased(KeyEvent e) {
					if ((getDestLayerName().getText().length() == 0) || ((JTextFieldWithSCP)e.getSource()).getText().length() == 0) {
						parameters.setDestinationLayerName(null);
						getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
					} else {
						parameters.setDestinationLayerName(((JTextFieldWithSCP)e.getSource()).getText());
						
						if (getDestLayerPath().getText().length() == 0) {
							getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
						}
						else {
							getAdaptedAcceptCancelPanel().setOkButtonEnabled(true);
						}
					}
				}
			});
		}

		return destLayerName;
	}

	private JLabel getProcessLabel() {
		if (destProcessLabel == null) {
			destProcessLabel = new JLabel(PluginServices.getText(null, "Process_type"));
			destProcessLabel.setPreferredSize(new Dimension(180, 20));
			//destShapeTypeLabel.setEnabled(false);
			destProcessLabel.setToolTipText(PluginServices.getText(null, "Process_type"));
		}

		return destProcessLabel;
	}

	private JComboBox getProcessCombo() {
		if (destProcessCombo == null) {
			destProcessCombo = new JComboBox();
    		destProcessCombo.setToolTipText(PluginServices.getText(null, "Process_type"));
			destProcessCombo.setPreferredSize(new Dimension(254, 20));
			destProcessCombo.addItemListener(new ItemListener() {
				/*
				 * (non-Javadoc)
				 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
				 */
				public void itemStateChanged(ItemEvent e) {
					JComboBox source = (JComboBox) e.getSource();

					if (source.getItemCount() == 0) {
						parameters.setProcessID(DerivedGeometryProcessParameters.UNDEFINED_TYPE);
					}
					else {
						short processID = ((ProcessType)source.getSelectedItem()).getProcessID();

						parameters.setProcessID(processID);

						// Updates the other combo
						getDestShapeTypeCombo().removeAllItems();
						
						switch(processID) {
							case DerivedGeometryProcessParameters.POINTS_TO_LINE_PROCESS_ID:
								getDestShapeTypeCombo().addItem(line);
								//getDestShapeTypeCombo().addItem(multi);
								break;
							case DerivedGeometryProcessParameters.POINTS_TO_POLYGON_PROCESS_ID:
								getDestShapeTypeCombo().addItem(polygon);
								//getDestShapeTypeCombo().addItem(multi);
								break;
							case DerivedGeometryProcessParameters.CLOSE_MULTILINE_PROCESS_ID:
								getDestShapeTypeCombo().addItem(polygon);
								//getDestShapeTypeCombo().addItem(multi);
								break;
							case DerivedGeometryProcessParameters.POINTS_TO_LINE_OR_POLYGON_ID:
								getDestShapeTypeCombo().addItem(multi);
						}
					}
				}
			});
		}
		
		return destProcessCombo;
	}
    
    
    /**
	 * <p>This method initializes layersComboBox.</p>	
	 * 	
	 * @return javax.swing.JComboBox
     */
    private JComboBox getLayersComboBox() {
    	if (layersComboBox == null) {
    		layersComboBox = new JComboBox();
    		layersComboBox.setPreferredSize(new Dimension(layersComboBox_Width, layersComboBox_Height));
    		layersComboBox.addItemListener(new ItemListener() {
    			/*
    			 * (non-Javadoc)
    			 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
    			 */
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
					//	infoLayerSelected = null;
					//	selectedFields = 0;
						
						try {
							FLayerWrapper fW = (FLayerWrapper)e.getItem();

							if (fW != null) {
								FLayer layer = fW.getLayer();

								if (layer != null) {
//									infoLayerSelected = null;
//									infoLayerSelected = new QuickInfoFLayerSelected(layer);
//									getFieldsModeButton().setToolTipText(PluginServices.getText(this, "Ascending"));
									layersComboBox.setToolTipText("<html>" + PluginServices.getText(this, "Layer") + ": " + layer.getName() + "<br>" + 
										PluginServices.getText(this, "Type") + ": " + FShapeTypeNames.getFShapeTypeName(((FLyrVect)layer).getShapeType()) + "</html>");

//									refreshSelectedLayerFields();

									// Sets the layer selected
									parameters.setSourceLayer((FLyrVect)layer);

									// Removes the values of the destination file
									getDestLayerName().setText("");
									getDestLayerPath().setText("");

									parameters.setDestinationFile(null);
									parameters.setDestinationLayerName(null);
									parameters.setDestinationLayerShapeType(DerivedGeometryProcessParameters.UNDEFINED_TYPE);

									getDestShapeTypeCombo().removeAllItems();
									getProcessCombo().removeAllItems();
									
									switch(((FLyrVect)layer).getShapeType()) {
										case FShape.POINT:
											setSelectableComponentsEnabled(true);
											getProcessCombo().addItem(pointsToLine);
											getProcessCombo().addItem(pointsToPolygon);
											//getProcessCombo().addItem(pointsToLineOrPolygon);
											break;
										case FShape.LINE:
											setSelectableComponentsEnabled(true);
											getProcessCombo().addItem(closeMultiLine);
											break;
										default:
											setSelectableComponentsEnabled(false);

											if (started)
												JOptionPane.showMessageDialog(layersComboBox, PluginServices.getText(null, "Cant_process_this_layer"));
    								}
									
									getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
								}
							}
							
//							getFieldsModeButton().setText(PluginServices.getText(this, "Ascending"));
//							getFieldsModeButton().setToolTipText(PluginServices.getText(this, "Ascending"));
							getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
						}
						catch(ReadDriverException dE) {
							NotificationManager.showMessageError(PluginServices.getText(null, "problem_loading_layers"), dE);
						}
					}
				}
    		});
    	}
    	
    	return layersComboBox;
    }
	
    /**
     * <p>This method initializes acceptCancelPanel.</p>	
     * 
     * @return an adapted {@link AcceptCancelPanel AcceptCancelPanel}
     */
    private AdaptedAcceptCancelPanel getAdaptedAcceptCancelPanel() {
    	if (acceptCancelPanel == null) {
    		acceptCancelPanel = new AdaptedAcceptCancelPanel();
    	}
    	
    	return acceptCancelPanel;
    }
	
	/**
	 * <p>Updates "layersComboBox" with the name of visible layers of the associated <code>MapControl</code> instance.</p>
	 * 
	 * @see #refreshVisibleVectorLayers(FLayer, int)
	 * 
	 * @return <code>true</code> if there is any layer added that can be written, otherwise <code>false</code>
	 */
	private boolean refreshVisibleVectorLayers() {
		FLayer rootNode = parameters.getMapControl().getMapContext().getLayers();

		getLayersComboBox().removeAllItems();
		
		boolean b = refreshVisibleVectorLayers(rootNode, -1);

		// The selectedLayer will be, by default, the first being added.
		if (getLayersComboBox().getItemCount() > 0) {
			getLayersComboBox().setRenderer(new LayersComboBoxCellRenderer());
			getLayersComboBox().setSelectedIndex(0);
		}
		
		// If there is only one layer -> disable the possibility of selection by the user
		if (getLayersComboBox().getItemCount() <= 1) {
			getLayersComboBox().setEnabled(false);
		}
		
		return b;
	}
	
	/**
	 * <p>Updates "layersComboBox" with the name of visible layers down <code>node</code>.</p>
	 * 
	 * @param node parent node
	 * @param level <code>node</code> level from the root
	 * 
	 * @return <code>true</code> if some layer added can be edited, otherwise <code>false</code>
	 */
	private boolean refreshVisibleVectorLayers(FLayer node, int level) {
		if (node instanceof FLayers) {
			FLayers root = (FLayers)node;
			boolean b = false;

			for (int i = root.getLayersCount() - 1; i >= 0 ; i--) {
				if (root.getLayer(i).isVisible()) {
					b |= refreshVisibleVectorLayers(root.getLayer(i), level + 1);
				}
			}
			
			return b;
		}
		else {
			String layerName = node.getName();

			if ((!node.isEditing()) && (node.isVisible()) && (node instanceof FLyrVect) && (layerName != null)) {
				getLayersComboBox().addItem(new FLayerWrapper((FLyrVect)node, level, bIcon, getLayerIcon(node)));
				
				return node.isWritable();
			}
		}
		
		return false;
	}

	private void setSelectableComponentsEnabled(boolean b) {
		getDestLayerPath().setEnabled(b);
		getDestLayerName().setEnabled(b);
		getJButtonSelectPath().setEnabled(b);
		getProcessCombo().setEnabled(b);
	}
	
	/**
	 * <p>Returns the icon that represents the layer in the current active view's TOC.</p>
	 * 
	 * @param layer the layer
	 * @return the layer's icon in the current active view's TOC
	 */
	private Icon getLayerIcon(FLayer layer) {
	    if (layer.getTocImageIcon() != null) {
	    	return layer.getTocImageIcon();
	    }
	    else {
	    	TocItemBranch branch = new TocItemBranch(layer);

	    	return branch.getIcon();
	    }
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.ui.mdiManager.IWindow#getWindowInfo()
	 */
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this, "Select_layers_and_process"));
			viewInfo.setWidth(Window_Width);
			viewInfo.setHeight(Window_Height);
		}

		return viewInfo;
	}
	

//	/**
//	 * <p>Enables / disables the <i>accept</i> button.</p>
//	 * 
//	 * <p>If all required parameters are defined and valid, will be enabled, otherwise disabled.</p>
//	 */
//	private void updateAcceptButton() {
//		if ((parameters != null) && (parameters.requiredDefined())) {
//			getAdaptedAcceptCancelPanel().setOkButtonEnabled(true);
//		}
//		else {
//			getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
//		}
//	}

    /**
     * <p>Closes this window.</p>
     */
	private void closeThis() {
		PluginServices.getMDIManager().closeWindow(this);
	}

    /**
     * <p>Adapts {@link AcceptCancelPanel AcceptCancelPanel} to be used as a component of the <code>QInfoDataSelectionPanel</code> panel.</p>
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class AdaptedAcceptCancelPanel extends AcceptCancelPanel {
 		private static final long serialVersionUID = -6112053411624411449L;

		public AdaptedAcceptCancelPanel () {
    		super();

    		addOkButtonActionListener(getOKAction());
    		addCancelButtonActionListener(getCancelAction());
    		setPreferredSize(new Dimension(474, 28));
    	}
    	
    	/**
     	 * <p>Create the action that will be executed when user pressed the <i>ok</i> button.</p>
    	 * 
    	 * @return action that will be executed when user pressed the <i>cancel</i> button
    	 */
    	private ActionListener getOKAction() {
    		// OK button action
    		return new ActionListener() {
    			/*
    			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    			 */
				public void actionPerformed(ActionEvent e) {
					closeThis();

					/* If the layer exits, asks the user if wants to overwrite it */
					if(parameters.getDestinationFile().exists()){
						int resp = JOptionPane.showConfirmDialog((Component) PluginServices.getMainFrame(),PluginServices.getText(this,"fichero_ya_existe_seguro_desea_guardarlo"),	PluginServices.getText(this,"guardar"), JOptionPane.YES_NO_OPTION);

						if (resp != JOptionPane.YES_OPTION) {
							return;
						}
					}
					
					PluginServices.getMDIManager().addWindow(new DerivedGeometryControlPanel(parameters));
				}
    		};
    	}

    	/**
    	 * <p>Create the action that will be executed when user pressed the <i>cancel</i> button.</p>
    	 * 
    	 * @return action that will be executed when user pressed the <i>cancel</i> button
    	 */
    	private ActionListener getCancelAction() {
    		// Cancel button action
    		return new ActionListener() {
    			/*
    			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    			 */
				public void actionPerformed(ActionEvent e) {
					closeThis();
				}
    		};
    	}
    }


    /**
     * <p>Wrappers a <code>FLayer</code> overwriting the method <code>toString()</code> in order to would
     *  return the name of the layer.</code>
     * 
     * <p>Also displays icons and label</p>
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class FLayerWrapper extends JPanel {
		private static final long serialVersionUID = -160586150559061104L;

		private FLyrVect layer;
    	private int level;
    	ImageIcon branchIcon;

    	/**
    	 * <p>Creates a new <code>FLayerWrapper</code>.</p>
    	 * 
    	 * @param layer the vector layer to be wrappered
    	 * @param level the level in the tree of the layer wrappered
    	 * @param branchIcon icon that represents the branch
    	 * @param leafIcon icon that represents the leaf
    	 */
    	public FLayerWrapper(FLyrVect layer, int level, ImageIcon branchIcon, Icon leafIcon) {
    		super();

    		this.layer = layer;
    		this.level = level;
    		this.branchIcon = branchIcon;

    		setLayout(new SpringLayout());
    		
    		if ((level > 0) && (branchIcon != null)) {
    			for (int i = 0; i < level; i++) {
	    			add(new JLabel(branchIcon));
    			}
    		}

    		JLabel layerLabel;
	    	
    		if (layer.getName() != null)
    			layerLabel = new JLabel(layer.getName());
    		else
    			layerLabel = new JLabel("");
    		
    		if (leafIcon != null)
    			layerLabel.setIcon(leafIcon);

    		layerLabel.setFont(new Font("Helvetica", Font.BOLD, 12));
    		
    		add(layerLabel);
    		
    		if (layer instanceof FLyrVect) {
        		JLabel layerTypeLabel; 
    			try {
    				layerTypeLabel = new JLabel(PluginServices.getText(null, "Type") + ": " + FShapeTypeNames.getFShapeTypeName(((FLyrVect) layer).getShapeType()));
				} catch (ReadDriverException e) {
					layerTypeLabel = new JLabel(PluginServices.getText(null, "Type") + ": " + PluginServices.getText("", "UNKNOWN"));
					NotificationManager.showMessageError("Driver exception", e);
				}

				layerTypeLabel.setFont(new Font("Helvetica", Font.ITALIC, 12));

	    		add(layerTypeLabel);
    		}
			
			doSpringLayout();
    		setPreferredSize(new Dimension(340, 16));
    	}

    	/**
    	 * <p>Creates the <code>Spring</code> layout of this component.</p>
    	 */
    	private void doSpringLayout() {
			Component[] components = getComponents();
	        SpringLayout layout = (SpringLayout)getLayout();
	        Spring xPad = Spring.constant(5);
	        Spring ySpring = Spring.constant(0);
	        Spring xSpring = xPad;

	        // Make every component 5 pixels away from the component to its left.
	        for (int i = 0; i < components.length; i++) {
	            SpringLayout.Constraints cons = layout.getConstraints(components[i]);
	            cons.setX(xSpring);
	            xSpring = Spring.sum(xPad, cons.getConstraint("East"));

	            cons.setY(ySpring);
	        }
    	}
    	
    	/**
    	 * <p>Gets the layer wrappered.</p> 
    	 * 
    	 * @return the layer wrappered
    	 */
    	public FLyrVect getLayer() {
    		return layer;
    	}
    	
    	/**
    	 * <p>Gets the level in the tree of the layer wrappered.</p> 
    	 * 
    	 * @return the level in the tree of the layer wrappered
    	 */
    	public int getLevel() {
    		return level;
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
    	 */
    	public void setForeground(Color fg) {
    		super.setForeground(fg);
       	}

    	/*
    	 * (non-Javadoc)
    	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
    	 */
    	public void setBackground(Color bg) {
    		super.setBackground(bg);
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Object#toString()
    	 */
    	public String toString() {
    		return layer.getName();
    	}
    }


    /**
     * <p>Cell renderer of the combo box with information of the visible vector layers in the current active view.</p>
     * 
     * @see ListCellRenderer
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class LayersComboBoxCellRenderer implements ListCellRenderer {
    	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    			FLayerWrapper renderer = (FLayerWrapper)value;
    			
    			if (isSelected) {
    				renderer.setForeground(UIManager.getColor( "ComboBox.selectionForeground" ));
    				renderer.setBackground(UIManager.getColor( "ComboBox.selectionBackground" ));
    			}
    			else
    				renderer.setBackground(Color.WHITE);

    		    return renderer;
    	}
    }

    /**
     *
     *
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class ProcessType {
    	private String processName;
    	private short processID;
    	
    	public ProcessType(String processName, short processID) {
    		this.processName = processName;
    		this.processID = processID;
    	}

		public short getProcessID() {
			return processID;
		}
		
		public String getProcessName() {
			return processName;
		}

		public String toString() {
			return processName;
		}
    }

    /**
     * 
     *
     *
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class OutputSHPType {
    	private String outputSHPTypeName;
    	private int outputSHPType;

    	public OutputSHPType(String name, int type) {
    		outputSHPTypeName = name;
    		outputSHPType = type;
    	}

		public String getOutputSHPName() {
			return outputSHPTypeName;
		}
		
		public int getOutputSHPType() {
			return outputSHPType;
		}
		
		public String toString() {
			return outputSHPTypeName;
		}
    }

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
