package org.gvsig.quickInfo.gui;

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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.incrementabletask.IncrementableEvent;
import org.gvsig.gui.beans.incrementabletask.IncrementableProcess;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.gui.beans.progresspanel.ProgressPanel;
import org.gvsig.quickInfo.QuickInfoFLayerSelected;
import org.gvsig.quickInfo.tools.QuickInfoListener;
import org.gvsig.quickInfo.utils.FShapeTypeNames;
import org.gvsig.quickInfo.utils.SQLTypeNames;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.tools.CompoundBehavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.toc.TocItemBranch;

/**
 * <p>Panel where user will select the layer and its fields to display as a <i>quick information tool</i>
 *  on a <code>MapControl</code> object.</p>
 * 
 * @version 06/03/08
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class QuickInfoDataSelectionPanel extends JPanel implements IWindow {

	private MapControl mapControl;
	private JLabel layersLabel = null;
    private JComboBox layersComboBox = null;
    private javax.swing.JScrollPane layerFieldsScrollPane = null;
    private JPanel northPanel = null;
    private JPanel layersPanel = null;
    private JPanel layerFieldsPanel = null;
    private JPanel optionalFieldsPanel = null;
    private JPanel optionButtonsPanel = null;
    private JButton fieldsModeButton = null;
    private JButton selectAllButton = null;
    private JButton unselectAllButton = null;
	private AdaptedAcceptCancelPanel acceptCancelPanel = null;
	
	private JTable fieldsTable = null;
	private TableModel fieldsTableModel = null;
	
	private ImageIcon leafIcon;
	private ImageIcon complexIcon;
	private ImageIcon childIcon;	
	private ImageIcon bIcon;
	private JLabel childIconLabel;	
//	private JLabel bIconLabel;

	private QuickInfoListener qIListener;
	private QuickInfoFLayerSelected infoLayerSelected;

    private WindowInfo viewInfo = null;
    private final short Window_Width = 480;
    private final short Window_Height = 440;
    private final int layersComboBox_Width = 390;
    private final int layersComboBox_Height = 22;
    
    private int selectedFields = 0;

    // Layer types:
    private final short UNDEFINED = -1;
    // End Layer types
    
    private int previous_Type = UNDEFINED;
    
    private boolean cancelled = false;
     
    /**
     * <p>Creates a new form where user could select the quick information to display.</p>
     */
    public QuickInfoDataSelectionPanel(MapControl mapControl) {
    	super();

    	this.mapControl = mapControl;
    	
    	// Removes any previous combinated tool
    	removeQuickInfoTool(mapControl);
		mapControl.setToolTipText(null);
    	
    	initialize();
    }
    
    /**
     * <p>Initializes this component.</p>
     */                        
    private void initialize() {
		IncrementableTask iTask;
		InitQIPanelProcess iprocess = new InitQIPanelProcess(PluginServices.getText(this, "Initializing_configuration_quick_info_tool"), PluginServices.getText(this, "loading_layers_please_wait"), this);
		final InitQIPanelProcess f_iprocess = iprocess; 
		
		iTask = new IncrementableTask(iprocess, new ProgressPanel(false));
		iTask.addIncrementableListener(iprocess);
		iprocess.setIncrementableTask(iTask);

		/* Opens maximized the window created */
		final QuickInfoDataSelectionPanel f_qIPanel = this;
		final IncrementableTask f_iTask = iTask;

		iTask.getProgressPanel().addComponentListener(new ComponentAdapter() {
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
			 */
			public void componentHidden(ComponentEvent e) {
				if ((! cancelled) && (f_iprocess.getPercent() == 100)) {
					f_qIPanel.setVisible(true);
					f_iTask.getProgressPanel().dispose();
					PluginServices.getMDIManager().addWindow(f_qIPanel);
				}
			}
		});

		iprocess.start();
		iTask.start();
//		setLayout(new FlowLayout());
//		
//		bIcon = new ImageIcon(QuickInfoDataSelectionPanel.class.getClassLoader().getResource("images/layerGroup.png"));
// 		leafIcon = new ImageIcon(QuickInfoDataSelectionPanel.class.getClassLoader().getResource("images/field-leaf-icon.png"));
//		childIcon = new ImageIcon(QuickInfoDataSelectionPanel.class.getClassLoader().getResource("images/field-child-icon.png"));
//		complexIcon = new ImageIcon(QuickInfoDataSelectionPanel.class.getClassLoader().getResource("images/field-complex-icon.png"));
//		
//    	add(getNorthPanel());
//    	add(getAdaptedAcceptCancelPanel());
//
//		acceptCancelPanel.setOkButtonEnabled(false);
//    	
//    	refreshVisibleVectorLayers();
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
	 * <p>This method initializes northPanel.</p>	
	 * 	
	 * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
    	if (northPanel == null) {
    		northPanel = new JPanel();
    		northPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "select_layer_and_fields")));

    		northPanel.setPreferredSize(new Dimension(470, 435));
    		northPanel.add(getLayersPanel());
    		northPanel.add(getLayerFieldsPanel());
        	northPanel.add(getOptionalFieldsPanel());
        	northPanel.add(getOptionButtonsPanel());
    	}
    	
    	return northPanel;
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
						infoLayerSelected = null;
						selectedFields = 0;
						
						try {
							FLayerWrapper fW = (FLayerWrapper)e.getItem();
							
							if (fW != null) {
								FLayer layer = fW.getLayer();
								
								if (layer != null) {
									infoLayerSelected = null;
									infoLayerSelected = new QuickInfoFLayerSelected(layer);
									getFieldsModeButton().setToolTipText(PluginServices.getText(this, "Ascending"));
									layersComboBox.setToolTipText("<html>" + PluginServices.getText(this, "Layer") + ": " + layer.getName() + "<br>" + 
										PluginServices.getText(this, "Type") + ": " + FShapeTypeNames.getFShapeTypeName(((FLyrVect)layer).getShapeType()) + "</html>");
									
									refreshSelectedLayerFields();
								}
							}
							
							getFieldsModeButton().setText(PluginServices.getText(this, "Ascending"));
							getFieldsModeButton().setToolTipText(PluginServices.getText(this, "Ascending"));
							updateAcceptButton();
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
	 * <p>This method initializes layersPanel.</p>	
	 * 	
	 * @return javax.swing.JPanel
     */
    private JPanel getLayerFieldsPanel() {
       	if (layerFieldsPanel == null) {
       		layerFieldsPanel = new JPanel();
       		layerFieldsPanel.setLayout(new FlowLayout());
       		layerFieldsPanel.setPreferredSize(new Dimension(460, 230));
       		layerFieldsPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "Fields")));
       		layerFieldsPanel.add(getLayerFieldsScrollPane());
    	}
    	
    	return layerFieldsPanel;
    }
    
    /**
	 * <p>This method initializes layerFieldsScrollPane.</p>	
	 * 	
	 * @return javax.swing.JScrollPane
     */
	private JScrollPane getLayerFieldsScrollPane() {
		if (layerFieldsScrollPane == null) {
			layerFieldsScrollPane = new JScrollPane();
			layerFieldsScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			layerFieldsScrollPane.setPreferredSize(new java.awt.Dimension(446, 196));
			layerFieldsScrollPane.setViewportView(getFieldsTable());
		}
		
		return layerFieldsScrollPane;
	}
	
    /**
	 * <p>This method initializes optionButtonsPanel.</p>	
	 * 	
	 * @return javax.swing.JPanel
     */
	private JPanel getOptionButtonsPanel() {
		if (optionButtonsPanel == null) {
			optionButtonsPanel = new JPanel();
			
			optionButtonsPanel.setLayout(new FlowLayout());
			optionButtonsPanel.setPreferredSize(new Dimension(420, 32));
			optionButtonsPanel.add(getSelectAllButton());
			optionButtonsPanel.add(getFieldsModeButton());
			optionButtonsPanel.add(getUnselectAllButton());
		}
		
		return optionButtonsPanel;
	}
	
    /**
	 * <p>This method initializes selectAllButton.</p>	
	 * 	
	 * @return javax.swing.JButton
     */
	private JButton getSelectAllButton() {
		if (selectAllButton == null) {
			selectAllButton = new JButton(PluginServices.getText(this, "Select_all"));
			selectAllButton.setToolTipText(PluginServices.getText(this, "Select_all"));
			selectAllButton.setPreferredSize(new Dimension(150, 22));
			selectAllButton.setLocation(0, 10);
			selectAllButton.addMouseListener(new MouseAdapter() {
				/*
				 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
				 */
				public void mouseReleased(MouseEvent e) {
					for (int i = 0; i < getFieldsTable().getRowCount(); i++) {
						fieldsTable.getModel().setValueAt(Boolean.TRUE, i, 0);
						infoLayerSelected.getLayerFields().put(((FieldNameWrapper)fieldsTable.getModel().getValueAt(i, 1)).getField(), new Vector());
					}
					
					selectedFields = fieldsTable.getRowCount();
					
					for (int i = 0; i < getOptionalFieldsPanel().getComponentCount(); i++) {
						Component child = getOptionalFieldsPanel().getComponent(i);
						if (child instanceof JCheckBox) {
							((JCheckBox)child).setSelected(true);
							infoLayerSelected.getCalculatedLayerFields().put(((JCheckBox)child).getText(), new Vector());
							selectedFields ++;
						}
					}
					
					updateAcceptButton();
				}
			});
		}
		
		return selectAllButton;
	}

	/**
	 * <p>This method initializes fieldsModeButton.</p>	
	 * 	
	 * @return javax.swing.JButton
	 */
	private JButton getFieldsModeButton() {
		if (fieldsModeButton == null) {
			fieldsModeButton = new JButton(PluginServices.getText(this, "Ascending"));
			fieldsModeButton.setToolTipText(PluginServices.getText(this, "Ascending"));
			fieldsModeButton.setPreferredSize(new Dimension(100, 22));
			fieldsModeButton.addMouseListener(new MouseAdapter() {
				/*
				 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
				 */
				public void mouseReleased(MouseEvent e) {
					if (fieldsModeButton.getText().compareTo(PluginServices.getText(this, "Ascending")) == 0) {
					    // Sort the values in the second column of the model
					    // in descending order
						sortFieldColumns(((DefaultTableModel)getFieldsTable().getModel()), ColumnSorter.ASCENDING);
						fieldsModeButton.setText(PluginServices.getText(this, "Descending"));
						fieldsModeButton.setToolTipText(PluginServices.getText(this, "Descending"));
					}
					else {
						if (fieldsModeButton.getText().compareTo(PluginServices.getText(this, "Descending")) == 0) {
							sortFieldColumns(((DefaultTableModel)getFieldsTable().getModel()), ColumnSorter.DESCENDING);
							fieldsModeButton.setText(PluginServices.getText(this, "Reload"));
							fieldsModeButton.setToolTipText(PluginServices.getText(this, "Reload"));
						}
						else {
							if (fieldsModeButton.getText().compareTo(PluginServices.getText(this, "Reload")) == 0) {
								int index = getLayersComboBox().getSelectedIndex();
								getLayersComboBox().setSelectedIndex(-1);
								getLayersComboBox().setSelectedIndex(index);
								fieldsModeButton.setText(PluginServices.getText(this, "Ascending"));
								fieldsModeButton.setToolTipText(PluginServices.getText(this, "Ascending"));
							}
						}
					}
				}
			});
		}
		
		return fieldsModeButton;
	}
	
    /**
	 * <p>This method initializes unselectAllButton.</p>	
	 * 	
	 * @return javax.swing.JButton
     */
	private JButton getUnselectAllButton() {
		if (unselectAllButton == null) {
			unselectAllButton = new JButton(PluginServices.getText(this, "Clean_selection"));
			unselectAllButton.setToolTipText(PluginServices.getText(this, "Clean_selection"));
			unselectAllButton.setPreferredSize(new Dimension(150, 22));
			unselectAllButton.addMouseListener(new MouseAdapter() {
				/*
				 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
				 */
				public void mouseReleased(MouseEvent e) {
					for (int i = 0; i < getFieldsTable().getRowCount(); i++) {
						fieldsTable.getModel().setValueAt(Boolean.FALSE, i, 0);
						infoLayerSelected.getLayerFields().remove(((FieldNameWrapper)fieldsTable.getModel().getValueAt(i, 1)).getField());
					}
					
					for (int i = 0; i < getOptionalFieldsPanel().getComponentCount(); i++) {
						Component child = getOptionalFieldsPanel().getComponent(i);
						if (child instanceof JCheckBox) {
							((JCheckBox)child).setSelected(false);
							infoLayerSelected.getCalculatedLayerFields().remove(((JCheckBox)child).getText());
							selectedFields --;
						}
					}
					
					selectedFields = 0;
					updateAcceptButton();
				}
			});
		}
		
		return unselectAllButton;
	}
	
    /**
	 * <p>This method initializes optionalFieldsPanel.</p>	
	 * 	
	 * @return javax.swing.JPanel
     */
	private JPanel getOptionalFieldsPanel() {
		if (optionalFieldsPanel == null) {
			optionalFieldsPanel = new JPanel();

			optionalFieldsPanel.setPreferredSize(new Dimension(460, 100));
			optionalFieldsPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "calculated_fields")));
			
			GridLayout layout = new GridLayout(4, 4, 5, 5);
			optionalFieldsPanel.setLayout(layout);
		}
		
		return optionalFieldsPanel;
	}

	/**
	 * <p>Returns a reference to the table that stores the fields.</p>
	 * 
	 * @return reference to the table that stores the fields
	 */
	private JTable getFieldsTable() {
		if (fieldsTable == null) {
			fieldsTable = new JTable(getFieldsTableModel()) {
				private static final long serialVersionUID = 6100282856104273588L;

				/*
				 * (non-Javadoc)
				 * @see javax.swing.JTable#isCellEditable(int, int)
				 */
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			
			fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			fieldsTable.getColumnModel().getColumn(0).setMaxWidth(15);
			
			JLabel l=(JLabel)fieldsTable.getTableHeader().getDefaultRenderer();
			l.setPreferredSize(new Dimension(15,20));
			
			fieldsTable.getColumnModel().getColumn(1).setCellRenderer(new FieldNameRenderer());
			
			fieldsTable.addMouseListener(new MouseAdapter() {
			
				public void mouseReleased(MouseEvent e) {
					int row = fieldsTable.rowAtPoint(e.getPoint());
					
					if (row >= 0) {
						 Boolean b = ((Boolean)fieldsTable.getModel().getValueAt(row, 0));

						 if (b.booleanValue() == true) {
							 fieldsTable.getModel().setValueAt(Boolean.FALSE, row, 0);
							 infoLayerSelected.getLayerFields().remove(((FieldNameWrapper)fieldsTable.getModel().getValueAt(row, 1)).getField());
							 selectedFields --;
							 updateAcceptButton();
						 }
						 else {
							 fieldsTable.getModel().setValueAt(Boolean.TRUE, row, 0);
							 infoLayerSelected.getLayerFields().put(((FieldNameWrapper)fieldsTable.getModel().getValueAt(row, 1)).getField(), new Vector());
							 selectedFields ++;
							 updateAcceptButton();
						 }
					}					
				}				
			});
		}
		
		return fieldsTable;
	}

	/**
	 * <p>Enables / disables the <i>accept</i> button according the number of selected fields.</p>
	 * 
	 * <p>If there is no field selected, disables that button, is there is any selected, enables it.</p>
	 */
	private void updateAcceptButton() {
		if (selectedFields > 0) {
			getAdaptedAcceptCancelPanel().setOkButtonEnabled(true);
		}
		else {
			getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
		}
	}

	/**
	 * <p>Gets the table model that lists the fields of the selected layer.</p>
	 * 
	 * @return the table model that lists the fields of the selected layer
	 */
	private TableModel getFieldsTableModel() {
		if (fieldsTableModel == null) {
			fieldsTableModel = new LayerTypesTableModel();
			((DefaultTableModel)fieldsTableModel).addColumn("", new Object[] {});
			((DefaultTableModel)fieldsTableModel).addColumn(PluginServices.getText(this, "Name"), new Object[] {} );
			((DefaultTableModel)fieldsTableModel).addColumn(PluginServices.getText(this, "Type"), new Object[] {});
		}
		
		return fieldsTableModel;
	}
	
	/**
	 * <p>Updates "layersComboBox" with the name of visible layers of the associated <code>MapControl</code> instance.</p>
	 * 
	 * @see #refreshVisibleVectorLayers(FLayer, int)
	 */
	private void refreshVisibleVectorLayers() {
		FLayer rootNode = mapControl.getMapContext().getLayers();

		getLayersComboBox().removeAllItems();
		
		refreshVisibleVectorLayers(rootNode, -1);

		// The selectedLayer will be, by default, the first being added.
		if (getLayersComboBox().getItemCount() > 0) {
			getLayersComboBox().setRenderer(new LayersComboBoxCellRenderer());
			getLayersComboBox().setSelectedIndex(0);
		}
	}
	
	/**
	 * <p>Updates recursively the combo box with the layers name and property.</p>
	 * 
	 * @param node parent node
	 * @param level <code>node</code> level from the root
	 */
	private void refreshVisibleVectorLayers(FLayer node, int level) {
		if (node instanceof FLayers) {
			FLayers root = (FLayers)node;

			for (int i = root.getLayersCount() - 1; i >= 0 ; i--) {
				if (root.getLayer(i).isVisible()) {
					refreshVisibleVectorLayers(root.getLayer(i), level + 1);
				}
			}
		}
		else {
			String layerName = node.getName();
			
			if ((node.isVisible()) && (node instanceof FLyrVect) && (layerName != null)) { 
				getLayersComboBox().addItem(new FLayerWrapper(node, level, getLayerIcon(node)));
			}
		}
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
	
	/**
	 * <p>Updates "layerFieldsList" with the fields of the selected vector layer.</p>
	 */
	private void refreshSelectedLayerFields() {
		Object item = getLayersComboBox().getSelectedItem();

		if (item != null) {
			try {
				FLyrVect selectedLayer = (FLyrVect)((FLayerWrapper)item).getLayer();
				
				SelectableDataSource sds = selectedLayer.getRecordset();
				
				((DefaultTableModel)getFieldsTable().getModel()).setRowCount(0);
				fillTable(sds);
				
				refreshOptionalLayerFields(selectedLayer);
				getFieldsModeButton().getText().compareTo(PluginServices.getText(this, "Ascending"));
			} catch (Exception e) {
				NotificationManager.showMessageError(PluginServices.getText(null, "problem_loading_fields_of_the_layer"), e);
			}
		}
	 }

	/**
	 * <p>Fills the table with the fields of the layer.</p>
	 * 
	 * @param sds object with the selected data of the data source
	 * @throws com.hardcode.gdbms.engine.data.driver.DriverException throws DriverException
	 */
	private void fillTable(SelectableDataSource sds) throws ReadDriverException {
		DefaultTableModel tableModel = (DefaultTableModel)getFieldsTableModel();
		
		for (int i = 0; i < sds.getFieldCount(); i ++) {
			if (sds.getFieldType(i) == Types.STRUCT)
				tableModel.addRow(new Object[] {Boolean.FALSE, new FieldNameWrapper(sds.getFieldName(i), null, true), SQLTypeNames.getSQLTypeName(sds.getFieldType(i))});
			else
				tableModel.addRow(new Object[] {Boolean.FALSE, new FieldNameWrapper(sds.getFieldName(i), null, false), SQLTypeNames.getSQLTypeName(sds.getFieldType(i))});
		}
	}

	/**
	 * <p>Updates "optionalFieldsPanel" with the check boxes that are optional fields for the selected layer,
	 *  according its type.</p>
	 *
	 * @throws DriverException if fails working with the data driver
	 */
	private void refreshOptionalLayerFields(FLyrVect seletedLayer) throws ReadDriverException {
		previous_Type = seletedLayer.getShapeType();
		
		getOptionalFieldsPanel().removeAll();
		
		switch (previous_Type) {
			case FShape.NULL:
				break;
			case FShape.POINT:
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Coordinates"));
				break;
			case FShape.LINE:
				// Disabled:
				// getOptionalFieldsPanel().add(getNewOptionalCheckBox("First_point_coordinates"));
				// getOptionalFieldsPanel().add(getNewOptionalCheckBox("Second_point_coordinates"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Length"));
				break;
			case FShape.POLYGON:
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Area"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Perimeter"));
				break;
			case FShape.TEXT:
				break;
			case FShape.MULTI: // Can have points, lines and polygons
				getOptionalFieldsPanel().add(getNewLabel(PluginServices.getText(this, "Points") + ":"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Coordinates"));
				getOptionalFieldsPanel().add(getNewLabel("  "));
				getOptionalFieldsPanel().add(getNewLabel(PluginServices.getText(this, "Lines") + ":"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Length"));
				getOptionalFieldsPanel().add(getNewLabel("  "));
				getOptionalFieldsPanel().add(getNewLabel(PluginServices.getText(this, "Polygons") + ":"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Area"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Perimeter"));
				getOptionalFieldsPanel().add(getNewLabel(PluginServices.getText(this, "Multipoints") + ":"));
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Number_of_points"));
				break;
			case FShape.MULTIPOINT:
				getOptionalFieldsPanel().add(getNewOptionalCheckBox("Number_of_points"));
				break;
			case FShape.CIRCLE:
				// Disabled:
				// getOptionalFieldsPanel().add(getNewOptionalCheckBox("Coordinates_of_the_centre"));
				// getOptionalFieldsPanel().add(getNewOptionalCheckBox("Radius"));
				// getOptionalFieldsPanel().add(getNewOptionalCheckBox("Circumference"));
				break;
			case FShape.ARC:
				break;
			case FShape.ELLIPSE:
				break;
			case FShape.Z:
				break;
			default : // UNDEFINED
		}

		updateUI();
	}

	/**
	 * <p>Creates a <code>JCheckBox</code> with <code>text</code> as a <i>text</i> and <i>tool tip text</i> of that
	 *  component.</p>
	 * 
	 * @param text text to assign
	 * @return the new component
	 */
	private JCheckBox getNewOptionalCheckBox(String text) {
		JCheckBox cb = new JCheckBox(PluginServices.getText(this, text));
		cb.setToolTipText(PluginServices.getText(this, text));
		cb.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent ev) {
				JCheckBox source = (JCheckBox)ev.getSource(); 
				if (source.isSelected()) {
					infoLayerSelected.getCalculatedLayerFields().put(source.getText(), new Vector());
					selectedFields ++;
					updateAcceptButton();
				}
				else {
					infoLayerSelected.getCalculatedLayerFields().remove(source.getText());
					selectedFields --;
					updateAcceptButton();
				}
			}
		});
		
		return cb;
	}
	
	/**
	 * <p>Creates a <code>JLabel</code> with <code>text</code> as a <i>text</i> and <i>tool tip text</i> of that
	 *  component.</p>
	 * 
	 * @param text text to assign
	 * @return the new component
	 */
	private JLabel getNewLabel(String text) {
		JLabel cl = new JLabel("<html><body><p><i>" + PluginServices.getText(this, text) + "</i></p></body></html>");
		cl.setToolTipText("<html><body><p><i>" + PluginServices.getText(this, text) + "</i></p></body></html>");
		
		return cl;
	}

	/*
     * @see com.iver.andami.ui.mdiManager.View#getViewInfo()
     */
    public WindowInfo getWindowInfo() {
        if (viewInfo == null) {
            viewInfo=new WindowInfo(WindowInfo.MODALDIALOG);
            viewInfo.setTitle(PluginServices.getText(this, "quick_information"));
            viewInfo.setWidth(Window_Width);
            viewInfo.setHeight(Window_Height);
        }
        return viewInfo;
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

					try {
						int type = ((FLyrVect)infoLayerSelected.getLayer()).getShapeType();

						switch (type) {
							case FShape.POINT: case FShape.MULTIPOINT:
								getQuickInfoListener().setPixelTolerance(20);
								break;
							default:
								getQuickInfoListener().setPixelTolerance(QuickInfoListener.DEFAULT_PIXEL_TOLERANCE);
						}
					}
					catch (Exception ex) {
						getQuickInfoListener().setPixelTolerance(15); 
					}

			        // Quick Information by point
			        mapControl.addCombinedTool(new AdaptedMouseMovementBehavior(getQuickInfoListener()));
				}
    		};
    	}

    	private QuickInfoListener getQuickInfoListener() {
    		if (qIListener == null) {
    			qIListener = new QuickInfoListener(mapControl, infoLayerSelected, QuickInfoListener.DEFAULT_PIXEL_TOLERANCE);
    		}
    		
    		return qIListener;
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
     * <p>Wrappers a <code>FLayer</code> field.</code>
     * <p>Represents a leaf as a green icon, a complex feature as a red icon, an other kind of nodes with a blue icon.</p>
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class FieldNameWrapper extends JPanel {
 		private static final long serialVersionUID = -2241805610138310550L;
		private String field;
    	private String[] ancestors;
    	private boolean hasChildren;

    	/**
    	 * <p>Creates a new <code>FieldNameWrapper.</p>
    	 * 
    	 * @param field a layer field to be wrappered
    	 * @param ancestors ancestors of that layer field
    	 * @param hasChildren <code>true</code> if has children; otherwise <code>false</code>
    	 */
    	public FieldNameWrapper(String field, String[] ancestors, boolean hasChildren) {
    		super();
    		
    		this.field = field;
    		this.ancestors = ancestors;
    		this.hasChildren = hasChildren;
    		setLayout(new SpringLayout());
    		
    		if ((ancestors != null) && (ancestors.length > 0) && (childIconLabel != null)) {
    			for (int i = 0; i < ancestors.length; i++)
	    			add(childIconLabel);
    		}
	    			
	    	JLabel name = new JLabel(field);
	    		    			
	    	if (hasChildren) {
		    	if (complexIcon != null)
		    		name.setIcon(complexIcon);
	    	}
	    	else {
		    	if (leafIcon != null)
		    		name.setIcon(leafIcon);
	    	}
	    	
	    	add(name);
    	}

    	/**
    	 * <p>Determines if this field has children.</p>
     	 * <p>This is prepared to <i>complex features</i>, but meanwhile that functionality isn't supported.</p>
     	 * 
    	 * @return <code>true</code> if this field has children; otherwise <code>false</code>
    	 */
    	public boolean hasChildren() {
    		return hasChildren;
    	}

    	/**
    	 * <p>Gets the name of the field wrappered.</p>
    	 * 
    	 * @return name of the field wrappered
    	 */
    	public String getField() {
    		return field;
    	}
    	
    	/**
    	 * <p>Gets the level of this field in the <i>TOC</i> tree.</p>
    	 * <p>This is prepared to <i>complex features</i>, but meanwhile that functionality isn't supported.</p>
    	 * 
    	 * @return level of this layer in the <i>TOC</i> tree, 0 if it's the root
    	 */
    	public int getLevel() {
    		if (ancestors == null)
    			return 0;
    		else
    			return ancestors.length;
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.awt.Component#toString()
    	 */
    	public String toString() {
    		return field;
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
 		private static final long serialVersionUID = -2392779605211085414L;
		private FLayer layer;
    	private int level;
//    	JLabel branchIconLabel;
//    	private JPanel leftPanel;

    	/**
    	 * <p>Creates a new <code>FLayerWrapper</code>.</p>
    	 * 
    	 * @param layer the layer to be wrappered
    	 * @param level the level in the tree of the layer wrappered
    	 * @param leafIcon icon that represents the leaf
    	 */
    	public FLayerWrapper(FLayer layer, int level,Icon leafIcon) {
    		super();

    		this.layer = layer;
    		this.level = level;

    		setLayout(new SpringLayout());
    		
    		if ((level > 0) && (bIcon != null)) {
    			for (int i = 0; i < level; i++) {
	    			add(new JLabel(bIcon));
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
    	public FLayer getLayer() {
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
     * <p>Cell renderer of the table with information of the fields of a layer.</p>
     * 
     * @see TableCellRenderer
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class FieldNameRenderer implements TableCellRenderer {
    	/*
    	 * (non-Javadoc)
    	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
    	 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			FieldNameWrapper renderer = (FieldNameWrapper)value;
			
			if (isSelected) {
				renderer.setForeground(UIManager.getColor( "Table.selectionForeground" ));
				renderer.setBackground(UIManager.getColor( "Table.selectionBackground" ));
			}
			else
				renderer.setBackground(Color.WHITE);

		    return renderer;
		}
    }
    
    /**
     * <p>Closes this window.</p>
     */
	private void closeThis() {
		PluginServices.getMDIManager().closeWindow(this);
	}

	/**
	 * <p>Sorts the three columns of the <code>model</code> bearing in mind the <code>ascending</code> order
	 *  according the second column, which has the field names.</p>
	 * 
	 * @param model the model to be sort ordered
	 * @param ascending <code>true</code> if it's an ascending order, <code>false</code> if its descending
	 */
	public void sortFieldColumns(DefaultTableModel model, boolean ascending) {
	  Vector data = model.getDataVector();
	  Object[] firstColData = new Object[model.getRowCount()];
      Object[] secondColData = new Object[model.getRowCount()];
      Object[] thirdColData = new Object[model.getRowCount()];
      
      // Copy the column data in an array
      for (int i=0; i<secondColData.length; i++) {
    	  firstColData[i] = ((Vector)data.get(i)).get(0);
    	  secondColData[i] = ((Vector)data.get(i)).get(1);
    	  thirdColData[i] = ((Vector)data.get(i)).get(2);
      }
      
      Vector oldSecondColData = new Vector(Arrays.asList(secondColData));

      // Sort the array of column data
      Collections.sort(Arrays.asList(secondColData), new ColumnSorter(ascending, ColumnSorter.WITHOUT_CASE_SENSITIVE));
  
      // Store the previous positions of the other columns
      int index;
      
      for (int i=0; i<secondColData.length; i++) {
    	  index = oldSecondColData.indexOf(secondColData[i]);

    	  // First Column
    	  ((Vector)data.get(i)).set(0, firstColData[index]);

    	  // Second Column
    	  ((Vector)data.get(i)).set(1, secondColData[i]);

    	  // Third Column
    	  ((Vector)data.get(i)).set(2, thirdColData[index]);
      }

      model.fireTableStructureChanged();
      
      getFieldsTable().getColumnModel().getColumn(0).setMaxWidth(15);
      getFieldsTable().getColumnModel().getColumn(1).setCellRenderer(new FieldNameRenderer());
	}

	/**
	 * <p>Compares two <code>String</code> bearing in mind <i>ascending order</i> and <i>case sensitive</i>.</p>
	 * 
	 * @see Comparator
	 * 
	 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
	 */
    public class ColumnSorter implements Comparator {
    	public static final boolean ASCENDING = true;
    	public static final boolean DESCENDING = false;
    	public static final boolean WITH_CASE_SENSITIVE = true;
    	public static final boolean WITHOUT_CASE_SENSITIVE = false;
    	
        private boolean ascending;
        private boolean caseSensitive;

        ColumnSorter(boolean ascending, boolean caseSensitive) {
            this.ascending = ascending;
            this.caseSensitive = caseSensitive;
        }

        public int compare(Object a, Object b) {
            // Treat empty strains like nulls
            if (a instanceof String && ((String)a).length() == 0) {
                a = null;
            }
            if (b instanceof String && ((String)b).length() == 0) {
                b = null;
            }
    
            // Sort nulls so they appear last, regardless
            // of sort order
            if (a == null && b == null) {
                return 0;
            } else if (a == null) {
                return 1;
            } else if (b == null) {
                return -1;
            } else {
            	if (caseSensitive) {
            		if (ascending)
            			return a.toString().compareTo(b.toString());
            		else
            			return b.toString().compareTo(a.toString());
            	}
            	else
            		if (ascending)
            			return a.toString().toLowerCase().compareTo(b.toString().toLowerCase());
            		else
            			return b.toString().toLowerCase().compareTo(a.toString().toLowerCase());
            }
        }
    }
    
    /**
     * <p>Inherits from {@link DefaultTableModel DefaultTableModel} adding support for having different
     *  column renderers.</p>
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class LayerTypesTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 6583028930017616307L;

		/**
    	 * <p>Instances <code>LayerTypesTableModel</code>.</p>
    	 */
    	public LayerTypesTableModel() {
    		super();
    	}
    	
        /*
         * <p>JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.</p>
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
    
    /**
     * Process that initializes this component, loading the fields of all visible vector layers in the
     *  current active view. 
     *
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class InitQIPanelProcess extends IncrementableProcess {
    	private QuickInfoDataSelectionPanel qIPanel		 = null;

//    	protected ActionListener      openWindowListener  = null; 

    	/**
    	 * Creates a new <p>InitQIPanelProcess</p>.
    	 * 
    	 * @param title title for the dialog that displays the evolution of the importation
    	 * @param label brief of this process, that will be displayed in the dialog
    	 * @param qIPanel reference to the panel that will initialize
    	 */
    	public InitQIPanelProcess(String title, String label, QuickInfoDataSelectionPanel qIPanel) {
    		super(title);

    		this.label = label;
    		this.qIPanel = qIPanel;
    		this.isPausable = true;
    	}

    	/**
    	 * Sets the object that will display the evolution of this loading process as a progress dialog.
    	 * 
    	 * @param iTask the object that will display the evolution of this loading process
    	 */
    	public void setIncrementableTask(IncrementableTask iTask) {
    		this.iTask = iTask;
    		iTask.setAskCancel(true);
    		iTask.getButtonsPanel().addAccept();
    		iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, false);

    		JButton jButton = iTask.getButtonsPanel().getButton(ButtonsPanel.BUTTON_ACCEPT);
    		jButton.addMouseListener(new MouseAdapter() {
    			/*
    			 * (non-Javadoc)
    			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
    			 */
    			public void mouseClicked(MouseEvent e) {
    				processFinalize();
    			}
    		});
    	}

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Runnable#run()
    	 */
    	public synchronized void run() {
    		String text = null;

    		try {
    			percentage = 5;
    			qIPanel.setVisible(false);
    			process();
    			while (! ended) {
    				t0 += 500;
	                Thread.currentThread().sleep(150);
    			}
    			log.addLine(PluginServices.getText(null, "Load_successful"));
    		} catch (InterruptedException ie) {
    			PluginServices.getLogger().error(ie);
    			label = PluginServices.getText(null, "Process_canceled");
    			text = PluginServices.getText(null, "Failed_the_load");
    		}
    		finally {
    			iTask.setAskCancel(false);
    			iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, true);
    			iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_CANCEL, false);
    			ended = true;

    			if (text != null) {
    				log.addLine(PluginServices.getText(null, "Percent_of_load") + ": " + getPercent());
    				log.addLine(text);
    				JOptionPane.showMessageDialog(iTask.getButtonsPanel(), text, PluginServices.getText(this, "Error"), JOptionPane.ERROR_MESSAGE);
    			}
    			
    			if (percentage == 100) {
    				iTask.getProgressPanel().setPercent(100); // Forces setting the progress bar at 100 %
    				iTask.getProgressPanel().setLabel(PluginServices.getText(null, "Process_finished"));
    				iTask.hideWindow();
    			}

    			iTask.stop();
    		}
    	}

    	/**
    	 * Importation process.
    	 * 
    	 * @throws InterruptedException any exception produced initializing this component
    	 */
    	public void process() throws InterruptedException {
//    		GeoVisorProject project = null;

    		try {
    			setLayout(new FlowLayout());
    			
    			bIcon = PluginServices.getIconTheme().get("layerGroup");
//    			if (bIcon != null)
//    				bIconLabel = new JLabel(bIcon);

    			leafIcon = PluginServices.getIconTheme().get("field-leaf-icon");
    			childIcon = PluginServices.getIconTheme().get("field-child-icon");
    			if (childIcon != null)
    				childIconLabel = new JLabel(childIcon);

    			complexIcon = PluginServices.getIconTheme().get("field-complex-icon");
	    		percentage = 15;

	        	log.addLine(PluginServices.getText(null, "Layers_fields_loaded_successfully"));
	    		percentage = 60;
	    		add(getNorthPanel());

	    		add(getAdaptedAcceptCancelPanel());
	    		percentage = 65;
	    		acceptCancelPanel.setOkButtonEnabled(false);

	        	refreshVisibleVectorLayers();
	    		percentage = 95;	 
	    		log.addLine(PluginServices.getText(null, "Optional_layers_fields_loaded_successfully"));

	    		percentage = 100;
    		}
    		catch (Exception e) {
    			PluginServices.getLogger().error(PluginServices.getText(null, "Failed_loding_quick_info_tool_data_selection_panel"), e);

//    			log.addLine(PluginServices.getText(null, "Failed_loading_the_project"));
//    			ended = true;
    			throw new InterruptedException();
    		}
    	}
    //
//    	public void setOpenWindowListener(ActionListener openWindowListener) {
//    		this.openWindowListener = openWindowListener;
//    	}

    	/*
    	 * (non-Javadoc)
    	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionCanceled(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
    	 */
    	public void actionCanceled(IncrementableEvent e) {
    		if (percentage < 100) {
    	   		ended = true;
        		blinker.interrupt();
        		
        		// Forces to cancel
        		cancelled = true;

        		log.addLine(PluginServices.getText(null, "Process_cancelled"));
       		}
    		else {
    			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "Process_finished_wont_be_cancelled"), PluginServices.getText(this, "Warning"), JOptionPane.WARNING_MESSAGE);
    		}
    	}
    	
    	/*
    	 * (non-Javadoc)
    	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionResumed(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
    	 */
    	public void actionResumed(IncrementableEvent e) {
    	}
    	
    	/*
    	 * (non-Javadoc)
    	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionSuspended(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
    	 */
    	public void actionSuspended(IncrementableEvent e) {
    	}
    }
    
    /**
     * As combined tool, in a CompoundBehavior its "paintComponent" method is always called. The idea is painting nothing to
     * accelerate the MapControl painting process.
     * 
     * @see MouseMovementBehavior
     * 
	 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    private class AdaptedMouseMovementBehavior extends MouseMovementBehavior {
    	/**
    	 * @see MouseMovementBehavior#MouseMovementBehavior(PointListener)
    	 */
		public AdaptedMouseMovementBehavior(PointListener mli) {
			super(mli);
		}
    	
		/*
		 * (non-Javadoc)
		 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt.Graphics)
		 */
		public void paintComponent(Graphics g) {
			//super.paintComponent(g);
		}
    }
    
    public static void removeQuickInfoTool(MapControl mapControl) {
    	Behavior ctools = mapControl.getCurrentMapTool();
    	
    	if (ctools instanceof CompoundBehavior) {
    		CompoundBehavior cb_ctools = (CompoundBehavior)ctools;
    		Behavior behavior;
    		
    		for (int i = 0; i < cb_ctools.size(); i++) {
    			behavior = cb_ctools.getBehavior(i);

    			if (behavior instanceof AdaptedMouseMovementBehavior) {
    				mapControl.removeCombinedTool(behavior);
    				return;
    			}
    			else {
    				if (behavior instanceof CompoundBehavior) {
    					if (removeQuickInfoTool(mapControl, (CompoundBehavior)behavior))
    						return;
    				}
    			}
    		}
    	}
    }
    
    private static boolean removeQuickInfoTool(MapControl mapControl, CompoundBehavior compoundBehavior) {
    	if (compoundBehavior != null) {
    		Behavior behavior;
    		
    		for (int i = 0; i < compoundBehavior.size(); i++) {
    			behavior = compoundBehavior.getBehavior(i);

    			if (behavior instanceof AdaptedMouseMovementBehavior) {
    				mapControl.removeCombinedTool(behavior);
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
