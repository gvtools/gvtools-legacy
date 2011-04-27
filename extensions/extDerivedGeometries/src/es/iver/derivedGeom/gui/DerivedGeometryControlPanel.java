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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.gui.beans.progresspanel.ProgressPanel;
import org.gvsig.gui.beans.specificcaretposition.JTextFieldWithSCP;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import es.iver.derivedGeom.process.AddDerivedGeometrySHPProcess;
import es.iver.derivedGeom.process.DerivedGeometryProcessParameters;

/**
 *
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class DerivedGeometryControlPanel extends JPanel implements IWindow, LayerListener {
	private static final long serialVersionUID = 79868642843290024L;

	protected WindowInfo viewInfo = null;
    protected final short Window_Width = 400;
    protected final short Window_Height = 466;
	protected DerivedGeometryProcessParameters parameters;
	protected JPanel summaryPanel = null;
	protected JPanel featuresPanel = null;
	protected JPanel newFeatureSelectionPanel = null;
	protected JPanel motionButtonsPanel = null;
	protected JPanel centerPanel = null;
	protected JSplitPane horizontalSplitPane = null;
//	protected JButton snappingCBox = null;
	protected JScrollPane allFeaturesScrollPane = null;
	protected JScrollPane selectedFeaturesScrollPane = null;
	protected JLabel sourceLayerNameLabel = null;
	protected JTextFieldWithSCP sourceLayerNameTextField = null;
	protected JLabel destLayerNameLabel = null;
	protected JTextFieldWithSCP destLayerNameTextField = null;
	protected JTable allFeaturesTable = null;
	protected TableModel allFeaturesTableModel = null;
	protected JTable selectedFeaturesTable = null;
	protected TableModel selectedFeaturesTableModel = null;
	protected AdaptedAcceptCancelPanel acceptCancelPanel = null;
	protected Color bgIDColor = new Color(204, 204, 204);
	protected boolean isShown = false;
	protected LayerCollectionListener layerCollectionListener = null;
	protected Behavior combinedTool = null;
	protected boolean mustRestoreWindow = false;
	protected ComponentListener viewListener = null;
	protected LayerCollectionListener newLayerCollectionListener = null;
//	protected SnappingTool sTool = null;
	
	protected final short TABLE_COLUMN_PREF_WIDTH = 80;
	
//	protected static final byte ADD_ALL_FEATURES = 0;
//	protected static final byte REMOVE_ALL_FEATURES = 1;
//	protected static final byte ADD_SELECTED_FEATURES = 2;
//	protected static final byte REMOVE_SELECTED_FEATURES = 3;
	

	public DerivedGeometryControlPanel(DerivedGeometryProcessParameters parameters) {
		super();

		mustRestoreWindow = false;
		this.parameters = parameters;
		
		registerListeners();
		
		// Registers the snapping tool
//		registerSnappingTool();

		initialize();

		isShown = true;
	}

	protected void registerListeners() {
		/* Replaces any previous control panel with the new one */
		FLyrVect sourceLayer = parameters.getSourceLayer();
   		LayerListener[] listeners = sourceLayer.getLayerListeners();

		// Calls to unregister any previous "DerivativeGeometryControlPanel" associated to the same source layer
		for (int i = listeners.length-1; i >= 0; i--) {
			if (listeners[i] instanceof DerivedGeometryControlPanel) {
				((DerivedGeometryControlPanel)listeners[i]).doUnregistration();
				((DerivedGeometryControlPanel)listeners[i]).closeWindow();
			}
		}

		/* Registers the new listeners */
		parameters.getSourceLayer().addLayerListener(this);
        parameters.getMapControl().getMapContext().getLayers().addLayerCollectionListener(getAssociatedLayerCollectionListener());

        /* Combined tool -> Simple geometry selection by point */
//        parameters.getMapControl().addCombinedTool(getCombinedTool());

        /* View listener */
        registerViewListener();
	}
	
	/**
	 */
	protected void initialize() {
		setLayout(new BorderLayout());
		add(getSummaryPanel(), BorderLayout.PAGE_START);
		add(getCenterPanel(), BorderLayout.CENTER);
		add(getAdaptedAcceptCancelPanel(), BorderLayout.PAGE_END);
		
		refreshGeometries();
		updateOkButtons();
	}
	
	protected JPanel getSummaryPanel() {
		if (summaryPanel == null) {
			summaryPanel = new JPanel();
			summaryPanel.setLayout(new BorderLayout());
			summaryPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "Layers")));
			summaryPanel.setPreferredSize(new Dimension(390, 80));

			JPanel pageStartPanel = new JPanel();
			pageStartPanel.setLayout(new BorderLayout());
			pageStartPanel.setPreferredSize(new Dimension(380, 22));
			pageStartPanel.add(getSourceLayerNameLabel(), BorderLayout.LINE_START);
			pageStartPanel.add(getSourceLayerNameTextField(), BorderLayout.LINE_END);
			summaryPanel.add(pageStartPanel, BorderLayout.PAGE_START);
			
			JPanel pageEndPanel = new JPanel();
			pageEndPanel.setLayout(new BorderLayout());
			pageEndPanel.setPreferredSize(new Dimension(380, 22));
			pageEndPanel.add(getOutputLayerNameLabel(), BorderLayout.LINE_START);
			pageEndPanel.add(getOutputLayerNameTextField(), BorderLayout.LINE_END);
			summaryPanel.add(pageEndPanel, BorderLayout.PAGE_END);
		}

		return summaryPanel;
	}
	
	protected JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			centerPanel.setPreferredSize(new Dimension(390, 340));
			centerPanel.add(getHorizontalSplitPane(), BorderLayout.CENTER);
//			centerPanel.add(getFeaturesPanel(), BorderLayout.CENTER);
//			centerPanel.add(getNewFeaturePanel(), BorderLayout.PAGE_END);
		}
		
		return centerPanel;
	}
	
	protected JSplitPane getHorizontalSplitPane() {
		if (horizontalSplitPane == null) {
			horizontalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			horizontalSplitPane.setPreferredSize(new Dimension(390, 340));
//			horizontalSplitPane.setMinimumSize(new Dimension(390, 340));
			horizontalSplitPane.setOneTouchExpandable(true);

			horizontalSplitPane.setTopComponent(getFeaturesPanel());
			horizontalSplitPane.setBottomComponent(getNewFeaturePanel());
			
			// Sets the split pane's divider to the 50 %
			horizontalSplitPane.setDividerLocation((int)(horizontalSplitPane.getPreferredSize().getHeight() * 0.50));
		}

		return horizontalSplitPane;
	}
	
	protected JLabel getSourceLayerNameLabel() {
		if (sourceLayerNameLabel == null) {
			sourceLayerNameLabel = new JLabel(PluginServices.getText(null, "Source_layer"));
			sourceLayerNameLabel.setToolTipText(PluginServices.getText(null, "Source_layer"));
			sourceLayerNameLabel.setPreferredSize(new Dimension(100, 20));
		}
		
		return sourceLayerNameLabel;
	}
	
	protected JTextFieldWithSCP getSourceLayerNameTextField() {
		if (sourceLayerNameTextField == null) {
			sourceLayerNameTextField = new JTextFieldWithSCP(parameters.getSourceLayer().getName());
			sourceLayerNameTextField.setToolTipText(parameters.getSourceLayer().getName());
			sourceLayerNameTextField.setPreferredSize(new Dimension(270, 20));
			sourceLayerNameTextField.setEnabled(false);
		}
		
		return sourceLayerNameTextField;
	}
	
	
	protected JLabel getOutputLayerNameLabel() {
		if (destLayerNameLabel == null) {
			destLayerNameLabel = new JLabel(PluginServices.getText(null, "Output_layer"));
			destLayerNameLabel.setToolTipText(PluginServices.getText(null, "Output_layer"));
			destLayerNameLabel.setPreferredSize(new Dimension(100, 20));
		}
		
		return destLayerNameLabel;
	}
	
	protected JTextFieldWithSCP getOutputLayerNameTextField() {
		if (destLayerNameTextField == null) {
			destLayerNameTextField = new JTextFieldWithSCP(parameters.getDestinationLayerName());
			destLayerNameTextField.setToolTipText(parameters.getDestinationLayerName());
			destLayerNameTextField.setPreferredSize(new Dimension(270, 20));
			destLayerNameTextField.setEnabled(false);
		}
		
		return destLayerNameTextField;
	}	
	
	protected JPanel getFeaturesPanel() {
		if (featuresPanel == null) {
			featuresPanel = new JPanel();
			featuresPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "Features")));
			featuresPanel.setLayout(new BorderLayout());
			featuresPanel.setPreferredSize(new Dimension(380, 130));
			featuresPanel.add(getAllFeaturesScrollPane(), BorderLayout.CENTER);
		}
		
		return featuresPanel;
	}


	protected JPanel getNewFeaturePanel() {
		if (newFeatureSelectionPanel == null) {
			newFeatureSelectionPanel = new JPanel();
			newFeatureSelectionPanel.setBorder(BorderFactory.createTitledBorder(PluginServices.getText(this, "New_features")));
			newFeatureSelectionPanel.setPreferredSize(new Dimension(390, 200));
			newFeatureSelectionPanel.setLayout(new BorderLayout());
			
			// Icons
			ImageIcon removeAllIcon = PluginServices.getIconTheme().get("remove-all-icon");
			ImageIcon removeIcon = PluginServices.getIconTheme().get("remove-icon");
			ImageIcon addAllIcon = PluginServices.getIconTheme().get("add-all-icon");
			ImageIcon addIcon = PluginServices.getIconTheme().get("add_v2-icon");

			// Add all button 
     		JButton addAllIconButton = new JButton(addAllIcon);
     		addAllIconButton.setToolTipText(PluginServices.getText(this, "add_all_button_TOOLTIP_HTML_explanation"));
     		addAllIconButton.setPreferredSize(new Dimension(40, 40));
     		addAllIconButton.addActionListener(new ActionListener() {
     			/*
     			 * (non-Javadoc)
     			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     			 */
				public void actionPerformed(ActionEvent e) {
			    	try {
			    		// Adds the new columns and rows

			    		// Columns (only if weren't added)
			    		TableModel sourceTableModel = getAllFeaturesTableModel();
			    		TableModel destTableModel = getSelectedFeaturesTableModel();
			    		
			    		if (getSelectedFeaturesTable().getColumnCount() == 0) {
			    			// Order column
			    			((DefaultTableModel)destTableModel).addColumn(PluginServices.getText(null, "Order"), new JLabel[] {} );
			    			
			    			// ID column:
			    			((DefaultTableModel)destTableModel).addColumn(PluginServices.getText(null, "ID"), new JLabel[] {} );

			    			// The other columns:			    			
			    			for (int i = 0; i < sourceTableModel.getColumnCount(); i++) {
				    			((DefaultTableModel)destTableModel).addColumn(sourceTableModel.getColumnName(i), new Object[] {} );
				    		}

			    			// Define the renderer (a JLabel) for the first table's column
			    		    getSelectedFeaturesTable().getColumnModel().getColumn(0).setCellRenderer(new JTableColumnLabelRenderer());

			    		    // Define the renderer (a JLabel) for the second table's column
			    		    getSelectedFeaturesTable().getColumnModel().getColumn(1).setCellRenderer(new JTableColumnLabelRenderer());
				    		
				    		// Sets the least column width to all table's columns
				    		for (int cIndex = 0; cIndex < getSelectedFeaturesTable().getColumnModel().getColumnCount(); cIndex++) {
				    			getSelectedFeaturesTable().getColumnModel().getColumn(cIndex).setPreferredWidth(TABLE_COLUMN_PREF_WIDTH);
				    			getSelectedFeaturesTable().getColumnModel().getColumn(cIndex).setMinWidth(TABLE_COLUMN_PREF_WIDTH);
				    		}
				    	}

						// Rows
			    		Object[] rowValues = new Object[sourceTableModel.getColumnCount() + 2];
			    		JTable sourceTable = getAllFeaturesTable();
			    		int base = getSelectedFeaturesTable().getRowCount() + 1; // + 1 -> the counter starts in 1

			    		for (int i = 0; i < sourceTable.getRowCount(); i++) {
			    			// Order column:
			    			rowValues[0] = new JLabel(Integer.toString(base + i));
			    			
			    			// ID column:
			    			rowValues[1] = new JLabel(Integer.toString(i));
			    			
			    			// The other columns:
			    			for (int j = 0; j < sourceTableModel.getColumnCount(); j++) {
			    				rowValues[j + 2] = sourceTable.getValueAt(i, j).toString();
			    			}

			    			((DefaultTableModel)destTableModel).addRow(rowValues);
			    		}

			    		updateOkButtons();
			    		
			    		// Clears the selection
			    		getAllFeaturesTable().clearSelection();
			    	}
			    	catch (Exception ex) {
			    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_updating_features"), ex);

			    		// Forces to remove all columns and rows of the table
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setColumnCount(0);
			    		updateOkButtons();
			    	}
				}
     		});

     		// Remove all button 
     		JButton removeAllIconButton = new JButton(removeAllIcon);
     		removeAllIconButton.setToolTipText(PluginServices.getText(this, "remove_all_button_TOOLTIP_HTML_explanation"));
     		removeAllIconButton.setPreferredSize(new Dimension(40, 40));
     		removeAllIconButton.addActionListener(new ActionListener() {
     			/*
     			 * (non-Javadoc)
     			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     			 */
				public void actionPerformed(ActionEvent e) {
					try {
			    		// Removes all rows of the table
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
			    		//((DefaultTableModel)getSelectedFeaturesTable().getModel()).setColumnCount(0);
			    		
			    		updateOkButtons();
					}
			    	catch (Exception ex) {
			    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_updating_features"), ex);
			    		
			    		updateOkButtons();
			    	}					
				}
     		});

			// Left panel
			JPanel leftPanel = new JPanel();
			//leftPanel.setLayout(new BorderLayout());
			leftPanel.setPreferredSize(new Dimension(110, 44));
//     		leftPanel.add(addAllIconButton, BorderLayout.CENTER);
//     		leftPanel.add(removeAllIconButton, BorderLayout.CENTER);
//     		newFeatureSelectionPanel.add(leftPanel, BorderLayout.LINE_START);
     		leftPanel.add(addAllIconButton);
     		leftPanel.add(removeAllIconButton);
     	//	newFeatureSelectionPanel.add(leftPanel, BorderLayout.LINE_START);
     		
     		// Center-top panel
//     		JPanel centerTopPanel = new JPanel();
//     		centerTopPanel.setPreferredSize(new Dimension(130, 44));
//     		centerTopPanel.add(getSnappingCBox());
     		//newFeatureSelectionPanel.add(centerTopPanel, BorderLayout.CENTER);

			// Add button 
     		JButton addIconButton = new JButton(addIcon);
     		addIconButton.setToolTipText(PluginServices.getText(this, "add_selected_button_TOOLTIP_HTML_explanation"));
     		addIconButton.setPreferredSize(new Dimension(40, 40));
     		addIconButton.addActionListener(new ActionListener() {
     			/*
     			 * (non-Javadoc)
     			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     			 */
				public void actionPerformed(ActionEvent e) {
			    	try {
			    		// Adds the new columns and rows

			    		// Columns (only if weren't added)
			    		TableModel sourceTableModel = getAllFeaturesTableModel();
			    		TableModel destTableModel = getSelectedFeaturesTableModel();
			    		
			    		if (getSelectedFeaturesTable().getColumnCount() == 0) {
			    			// Order column
			    			((DefaultTableModel)destTableModel).addColumn(PluginServices.getText(null, "Order"), new JLabel[] {} );
			    			
			    			// ID column:
			    			((DefaultTableModel)destTableModel).addColumn(PluginServices.getText(null, "ID"), new JLabel[] {} );

			    			// The other columns:			    			
			    			for (int i = 0; i < sourceTableModel.getColumnCount(); i++) {
				    			((DefaultTableModel)destTableModel).addColumn(sourceTableModel.getColumnName(i), new Object[] {} );
				    		}

			    			// Define the renderer (a JLabel) for the first table's column
			    		    getSelectedFeaturesTable().getColumnModel().getColumn(0).setCellRenderer(new JTableColumnLabelRenderer());

			    		    // Define the renderer (a JLabel) for the second table's column
			    		    getSelectedFeaturesTable().getColumnModel().getColumn(1).setCellRenderer(new JTableColumnLabelRenderer());

				    		// Sets the least column width to all table's columns
				    		for (int cIndex = 0; cIndex < getSelectedFeaturesTable().getColumnModel().getColumnCount(); cIndex++) {
				    			getSelectedFeaturesTable().getColumnModel().getColumn(cIndex).setPreferredWidth(TABLE_COLUMN_PREF_WIDTH);
				    			getSelectedFeaturesTable().getColumnModel().getColumn(cIndex).setMinWidth(TABLE_COLUMN_PREF_WIDTH);
				    		}
			    		}
			    		
						// Rows
			    		Object[] rowValues = new Object[sourceTableModel.getColumnCount() + 2];
			    		JTable sourceTable = getAllFeaturesTable();
			    		int base = getSelectedFeaturesTable().getRowCount() + 1; // + 1 -> the counter starts in 1
			    		int[] sRows = getAllFeaturesTable().getSelectedRows();

			    		for (int i = 0; i < sRows.length; i++) {
			    			// Order column:
			    			rowValues[0] = new JLabel(Integer.toString(base + i));
			    			
			    			// ID column:
			    			rowValues[1] = new JLabel(Integer.toString(sRows[i]));
			    			
			    			// The other columns:
			    			for (int j = 0; j < sourceTableModel.getColumnCount(); j++) {
			    				rowValues[j + 2] = sourceTable.getValueAt(sRows[i], j).toString();
			    			}

			    			((DefaultTableModel)destTableModel).addRow(rowValues);
			    		}
			    		
			    		updateOkButtons();

			    		// Clears the selection
			    		getAllFeaturesTable().clearSelection();
			    	}
			    	catch (Exception ex) {
			    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_updating_features"), ex);

			    		// Forces to remove all columns and rows of the table
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setColumnCount(0);
			    		
			    		updateOkButtons();
			    	}
				}
     		});

			// Remove button 
     		JButton removeIconButton = new JButton(removeIcon);
     		removeIconButton.setToolTipText(PluginServices.getText(this, "remove_selected_button_TOOLTIP_HTML_explanation"));
     		removeIconButton.setPreferredSize(new Dimension(40, 40));
     		removeIconButton.addActionListener(new ActionListener() {
     			/*
     			 * (non-Javadoc)
     			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     			 */
				public void actionPerformed(ActionEvent e) {
			    	try {
			    		// Removes the selected rows

						// Rows
			    		TableModel model = getSelectedFeaturesTableModel();
			    		int[] sRows = getSelectedFeaturesTable().getSelectedRows();

			    		for (int i = (sRows.length - 1); i >= 0; i--) {
			    			((DefaultTableModel)model).removeRow(sRows[i]);
			    		}
			    		
			    		// Updates the order
			    		for (int i = 0; i < model.getRowCount(); i++) {
			    			model.setValueAt(new JLabel(Integer.toString(i + 1)), i, 0);
			    		}
			    		
			    		updateOkButtons();
			    	}
			    	catch (Exception ex) {
			    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_updating_features"), ex);

			    		// Forces to remove all columns and rows of the table
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setColumnCount(0);
			    		
			    		updateOkButtons();
			    	}
				}
     		});

			// Right panel
			JPanel rightPanel = new JPanel();
			//rightPanel.setLayout(new BorderLayout());
			rightPanel.setPreferredSize(new Dimension(110, 44));
//			rightPanel.add(addIconButton, BorderLayout.CENTER);
//     		rightPanel.add(removeIconButton, BorderLayout.CENTER);
//     		newFeatureSelectionPanel.add(rightPanel, BorderLayout.LINE_END);
			rightPanel.add(addIconButton);
     		rightPanel.add(removeIconButton);

     		// North panel
     		JPanel northPanel = new JPanel();
     		northPanel.setLayout(new BorderLayout());
     		northPanel.setPreferredSize(new Dimension(390, 50));
     		northPanel.add(leftPanel, BorderLayout.LINE_START);
//     		northPanel.add(centerTopPanel, BorderLayout.CENTER);
     		northPanel.add(rightPanel, BorderLayout.LINE_END);
     		newFeatureSelectionPanel.add(northPanel, BorderLayout.PAGE_START);

     		// South panel
     		JPanel southPanel = new JPanel();
     		southPanel.setLayout(new BorderLayout());
     		southPanel.setPreferredSize(new Dimension(390, 125));

     		// Selected features table
//     		JPanel southLeftPanel = new JPanel();
//     		southLeftPanel.setLayout(new BorderLayout());
//     		southLeftPanel.setPreferredSize(new Dimension(320, 120));
//     		southLeftPanel.add(getSelectedFeaturesScrollPane(), BorderLayout.CENTER);
//
//     		southPanel.add(southLeftPanel, BorderLayout.LINE_START);
//     		southPanel.add(getMotionButtonsPanel(), BorderLayout.LINE_END);
     		
     		southPanel.add(getSelectedFeaturesScrollPane(), BorderLayout.CENTER);
     		southPanel.add(getMotionButtonsPanel(), BorderLayout.LINE_END);
     		
     		newFeatureSelectionPanel.add(southPanel, BorderLayout.CENTER);	
		}

		return newFeatureSelectionPanel;
	}
	
	/**
	 * 
	 * 
     * @see TableCellRenderer
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    protected class JTableColumnLabelRenderer implements TableCellRenderer {
    	/*
    	 * (non-Javadoc)
    	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
    	 */
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel renderer = (JLabel)value;
			
			// Enable the change of the foreground and background color
			renderer.setOpaque(true);
			
			if (isSelected) {
				renderer.setForeground(UIManager.getColor( "Table.selectionForeground" ));
				renderer.setBackground(UIManager.getColor( "Table.selectionBackground" ));
			}
			else
				renderer.setBackground(bgIDColor);

		    return renderer;
		}
    }
	
	protected JScrollPane getAllFeaturesScrollPane() {
		if (allFeaturesScrollPane == null) {
			allFeaturesScrollPane = new JScrollPane();
			allFeaturesScrollPane.setPreferredSize(new Dimension(370, 120));
			allFeaturesScrollPane.setViewportView(getAllFeaturesTable());
			allFeaturesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}
		
		return allFeaturesScrollPane;
	}
	
	protected JTable getAllFeaturesTable() {
		if (allFeaturesTable == null) {
			allFeaturesTable = new JTable(getAllFeaturesTableModel()) {
				/*
				 * (non-Javadoc)
				 * @see javax.swing.JTable#isCellEditable(int, int)
				 */
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			allFeaturesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			allFeaturesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

			allFeaturesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					try {
						SelectableDataSource sds = parameters.getSourceLayer().getRecordset();
						FBitSet newSelection = (FBitSet) sds.getSelection().clone();
						newSelection.clear();
						
						int[] sRows = getAllFeaturesTable().getSelectedRows();

						for (int i = 0; i < sRows.length; i++) {
							newSelection.set( sRows[i] );
						}

						sds.setSelection(newSelection);
					}
					catch (Exception ex) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_selecting_geometries_in_layer"), ex);
					}
				}
			});

		}

		return allFeaturesTable;
	}

	protected JScrollPane getSelectedFeaturesScrollPane() {
		if (selectedFeaturesScrollPane == null) {
			selectedFeaturesScrollPane = new JScrollPane();
			selectedFeaturesScrollPane.setPreferredSize(new Dimension(320, 120));
			selectedFeaturesScrollPane.setViewportView(getSelectedFeaturesTable());
			selectedFeaturesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}
		
		return selectedFeaturesScrollPane;
	}
	
	protected JTable getSelectedFeaturesTable() {
		if (selectedFeaturesTable == null) {
			selectedFeaturesTable = new JTable(getSelectedFeaturesTableModel()) {
				/*
				 * (non-Javadoc)
				 * @see javax.swing.JTable#isCellEditable(int, int)
				 */
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			selectedFeaturesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			selectedFeaturesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			selectedFeaturesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					try {
						SelectableDataSource sds = parameters.getSourceLayer().getRecordset();
						FBitSet newSelection = (FBitSet) sds.getSelection().clone();
						newSelection.clear();
						
						int[] sRows = getSelectedFeaturesTable().getSelectedRows();

						for (int i = 0; i < sRows.length; i++) {
							newSelection.set( Integer.parseInt(((JLabel)getSelectedFeaturesTable().getValueAt(sRows[i], 1)).getText()) );
						}

						sds.setSelection(newSelection);
					}
					catch (Exception ex) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_selecting_geometries_in_layer"), ex);
					}
				}
			});
		}
		
		return selectedFeaturesTable;
	}
	
	protected JPanel getMotionButtonsPanel() {
		if (motionButtonsPanel == null) {
			motionButtonsPanel = new JPanel();
			motionButtonsPanel.setLayout(new BorderLayout());
			motionButtonsPanel.setPreferredSize(new Dimension(50, 120));
			
			// Icons
			ImageIcon moveUpIcon = PluginServices.getIconTheme().get("up-arrow-icon");
			ImageIcon moveDownIcon = PluginServices.getIconTheme().get("down-arrow-icon");
			
			// Move up button
     		JButton moveUp = new JButton(moveUpIcon);
     		moveUp.setToolTipText(PluginServices.getText(this, "move_up_button_TOOLTIP_HTML_explanation"));
     		moveUp.setPreferredSize(new Dimension(40, 40));
     		moveUp.addActionListener(new ActionListener() {
     			/*
     			 * (non-Javadoc)
     			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     			 */
				public void actionPerformed(ActionEvent e) {
			    	try {
			    		// Moves up the selected rows

			    		TableModel model = getSelectedFeaturesTableModel();
			    		int[] sRows = getSelectedFeaturesTable().getSelectedRows();

			    		for (int i = 0; i < sRows.length; i++) {
			    			if (sRows[i] > 0)
			    				((DefaultTableModel)model).moveRow(sRows[i], sRows[i], sRows[i] - 1);
			    		}
			    		
			    		// Updates the order
			    		for (int i = 0; i < model.getRowCount(); i++) {
			    			model.setValueAt(new JLabel(Integer.toString(i + 1)), i, 0);
			    		}
			    	}
			    	catch (Exception ex) {
			    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_updating_features"), ex);

			    		// Forces to remove all columns and rows of the table
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setColumnCount(0);
			    	}
				}
     		});

     		JPanel upPanel = new JPanel();
//     		upPanel.setLayout(new BorderLayout());
     		upPanel.setPreferredSize(new Dimension(44, 50));
     		upPanel.add(moveUp);
			motionButtonsPanel.add(upPanel, BorderLayout.PAGE_START);
			
			// Move down button
     		JButton moveDown = new JButton(moveDownIcon);
     		moveDown.setToolTipText(PluginServices.getText(this, "move_down_button_TOOLTIP_HTML_explanation"));
     		moveDown.setPreferredSize(new Dimension(40, 40));
     		moveDown.addActionListener(new ActionListener() {
     			/*
     			 * (non-Javadoc)
     			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     			 */
				public void actionPerformed(ActionEvent e) {
			    	try {
			    		// Moves up the selected rows

			    		TableModel model = getSelectedFeaturesTableModel();
			    		int[] sRows = getSelectedFeaturesTable().getSelectedRows();

			    		for (int i = sRows.length - 1; i >= 0; i--) {
			    			if (sRows[i] < getSelectedFeaturesTable().getRowCount() - 1)
			    				((DefaultTableModel)model).moveRow(sRows[i], sRows[i], sRows[i] + 1);
			    		}
			    		
			    		// Updates the order
			    		for (int i = 0; i < model.getRowCount(); i++) {
			    			model.setValueAt(new JLabel(Integer.toString(i + 1)), i, 0);
			    		}
			    	}
			    	catch (Exception ex) {
			    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_updating_features"), ex);

			    		// Forces to remove all columns and rows of the table
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
			    		((DefaultTableModel)getSelectedFeaturesTable().getModel()).setColumnCount(0);
			    	}
				}
     		});


     		JPanel downPanel = new JPanel();
     		downPanel.setPreferredSize(new Dimension(44, 50));
//     		downPanel.setLayout(new BorderLayout());
     		downPanel.add(moveDown);
			motionButtonsPanel.add(downPanel, BorderLayout.PAGE_END);
		}
		
		return motionButtonsPanel;
	}
	
//	protected JButton getSnappingCBox() {
//		if (snappingCBox == null) {
//			snappingCBox = new JButton(PluginServices.getText(null, "Snapping"));
//			snappingCBox.setToolTipText(PluginServices.getText(null, "Start_snapping_tool"));
//			snappingCBox.setPreferredSize(new Dimension(100, 40));
//			snappingCBox.addActionListener(new ActionListener() {
//				/*
//				 * (non-Javadoc)
//				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//				 */
//				public void actionPerformed(ActionEvent e) {
//					// Sets the snapping tool as current tool
//					//parameters.getMapControl().setTool(AddDerivativeGeometrySHPExtension.ToolID);
//					
////					// Sets as CAD tool the "simple selection tool"
////					CADTool[] cadTools = CADExtension.getCADTools();
////					
////					boolean found = false;
////					
////					for (int i = 0; i < cadTools.length; i++) {
////						if (cadTools[i].getName().equals("_selection")) {
////							found = true;
////							break;
////						}
////					}
////					
////					if (! found)
////						CADExtension.setCADTool("_selection",false);
//
////					try {
////						parameters.getSourceLayer().setEditing(true);
////					} catch (Exception e1) {
////						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_starting_edition_of_layer"), e1);
////						return;
////					}
//
////					parameters.getMapControl().setTool("cadtooladapter");
////					((CADExtension)cadExtension).getCADToolAdapter().get
//					
//					CADTool cadTool = sTool.getCADTool("_nonEditedLayerSelection");
//
//					if (cadTool == null) {
//						cadTool = new NonEditedLayerSelectionCADTool(parameters.getSourceLayer());
//						SnappingTool.addCADTool("_nonEditedLayerSelection", cadTool);
//					}
//					else {
//						((NonEditedLayerSelectionCADTool)cadTool).setAssociatedLayer(parameters.getSourceLayer());
//					}
//
//					sTool.setMapControl(parameters.getMapControl());
//					sTool.setSnappedLayer(parameters.getSourceLayer());
//					sTool.setCadTool(cadTool);
//					
////					CADExtension.setCADTool("_nonEditedLayerSelection", false);
//					parameters.getMapControl().setTool(SnappingTool.TOOL_ID);
//
//					// Force to repaint the layer
//					parameters.getSourceLayer().setSpatialCacheEnabled(true);
//					parameters.getSourceLayer().setDirty(true);
//					parameters.getMapControl().rePaintDirtyLayers();
//					
//					
////					try {
////						parameters.getSourceLayer().setEditing(true);
////					} catch (EditionException e1) {
////						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_starting_edition_of_layer"), e1);
////						return;
////					}
//					
//					sTool.initializeFlatness();
//					sTool.initializeGrid();
//					sTool.setCadTool(cadTool);
//					cadTool.init();
//				}
//			});
//		}
//		
//		return snappingCBox;
//	}
//	
//	public boolean isEnabledSnapping() {
//		return getSnappingCBox().isSelected();
//	}
	
    /**
     * <p>This method initializes acceptCancelPanel.</p>	
     * 
     * @return an adapted {@link AcceptCancelPanel AcceptCancelPanel}
     */
    protected AdaptedAcceptCancelPanel getAdaptedAcceptCancelPanel() {
    	if (acceptCancelPanel == null) {
    		acceptCancelPanel = new AdaptedAcceptCancelPanel();
    	}
    	
    	return acceptCancelPanel;
    }

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.ui.mdiManager.IWindow#getWindowInfo()
	 */
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG | WindowInfo.RESIZABLE | WindowInfo.MAXIMIZABLE | WindowInfo.ICONIFIABLE);
			viewInfo.setTitle(parameters.getProcessName());
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
    protected class AdaptedAcceptCancelPanel extends JPanel {
    	private JButton btnOk = null;
    	private JButton btnCancel = null;
    	private JButton btnOk2 = null;
    	
    	protected final static String BUTTON_OK_ACTION = "OK";
    	protected final static String BUTTON_OK2_ACTION = "OK2";
    	protected final static String BUTTON_CANCEL_ACTION = "CANCEL";

		public AdaptedAcceptCancelPanel () {
				super();
				initialize();
		}
		
		protected void initialize() {
	        this.setLayout(new BorderLayout());
	        JPanel aux = new JPanel();
			
    		if (parameters.getProcessID() == DerivedGeometryProcessParameters.POINTS_TO_LINE_OR_POLYGON_ID) {
    			aux.add(getBtnOk(getOKAction()), java.awt.BorderLayout.LINE_END);
    			aux.add(getBtn2Ok(getOKAction()), java.awt.BorderLayout.LINE_END);
    			aux.add(getCancelButton(getCancelAction()), java.awt.BorderLayout.EAST);
	    		getOkButton().setText(PluginServices.getText(null, "Generate_line")); // Changes the text associated to the OK button
	    		getOkButton2().setText(PluginServices.getText(null, "Generate_polygon")); // Changes the text associated to the OK button
    		}
    		else {
    			aux.add(getBtnOk(getOKAction()), java.awt.BorderLayout.LINE_END);
    			aux.add(getCancelButton(getCancelAction()), java.awt.BorderLayout.LINE_END);
	    		getOkButton().setText(PluginServices.getText(null, "Generate")); // Changes the text associated to the OK button
    		}
    		
	        this.add(aux, java.awt.BorderLayout.LINE_END);
    		setPreferredSize(new Dimension(390, 28));    			
    	}
		
		/**
		 * This method initializes btnOk
		 *
		 * @return javax.swing.JButton
		 */
		protected JButton getBtnOk(ActionListener okAction) {
			if (btnOk == null) {
				btnOk = new JButton();
				btnOk.setText(PluginServices.getText(null, "ok"));
				btnOk.setActionCommand(BUTTON_OK_ACTION);
				if (okAction != null)
					btnOk.addActionListener(okAction);
			}
			return btnOk;
		}

		/**
		 * This method initializes btnOk
		 *
		 * @return javax.swing.JButton
		 */
		protected JButton getBtn2Ok(ActionListener okAction) {
			if (btnOk2 == null) {
				btnOk2 = new JButton();
				btnOk2.setActionCommand(BUTTON_OK2_ACTION);
				if (okAction != null)
					btnOk2.addActionListener(okAction);
			}
			return btnOk2;
		}

		/**
		 * This method initializes btnCancel
		 *
		 * @return javax.swing.JButton
		 */
		protected JButton getCancelButton(ActionListener cancelAction) {
			if (btnCancel == null) {
				btnCancel = new JButton();
				btnCancel.setText(PluginServices.getText(null, "cancel"));
				btnCancel.setActionCommand(BUTTON_CANCEL_ACTION);
				if (cancelAction != null)
					btnCancel.addActionListener(cancelAction);
			}
			return btnCancel;
		}

		/**
		 * Sets the ActionListener to the <b>OK</b> button removing any other previous one.
		 * @param l
		 */
		public void setOkButtonActionListener(ActionListener l) {
			ActionListener[] listeners = btnOk.getActionListeners();
			for (int i = 0; i < listeners.length; i++) {
				btnOk.removeActionListener(listeners[i]);
			}
			btnOk.addActionListener(l);
		}


		/**
		 * Sets the ActionListener to the second <b>OK</b> button removing any other previous one.
		 * @param l
		 */
		public void setOkButton2ActionListener(ActionListener l) {
			ActionListener[] listeners = btnOk2.getActionListeners();
			for (int i = 0; i < listeners.length; i++) {
				btnOk2.removeActionListener(listeners[i]);
			}
			btnOk2.addActionListener(l);
		}

		
		/**
		 * Sets the ActionListener to the <b>cancel</b> button removing any other previous one.
		 * @param l
		 */
		public void setCancelButtonActionListener(ActionListener l) {
			ActionListener[] listeners = btnCancel.getActionListeners();
			for (int i = 0; i < listeners.length; i++) {
				btnCancel.removeActionListener(listeners[i]);
			}
			btnCancel.addActionListener(l);
		}

		/**
		 * Returns the ok button contained by this panel since resizing issues should be
		 * automatically handled by the layout manager. The use of this method is discouraged,
		 * it is kept only for compatibility issues. Try using specific button properties
		 * access methods contained by this class instead.
		 * @return the Ok button
		 * @deprecated
		 */
		public JButton getOkButton() {
			return btnOk;
		}
		
		/**
		 * Returns the second ok button contained by this panel since resizing issues should be
		 * automatically handled by the layout manager. The use of this method is discouraged,
		 * it is kept only for compatibility issues. Try using specific button properties
		 * access methods contained by this class instead.
		 * @return the second Ok button
		 * @deprecated
		 */
		public JButton getOkButton2() {
			return btnOk2;
		}
		
		/**
		 * Returns the cancel button contained by this panel since resizing issues should be
		 * automatically handled by the layout manager. The use of this method is discouraged,
		 * it is kept only for compatibility issues. Try using specific button properties
		 * access methods contained by this class instead.
		 * @return the cancel button
		 * @deprecated
		 */
		public JButton getCancelButton() {
			return btnCancel;
		}

		public boolean isOkButtonEnabled() {
			return btnOk.isEnabled();
		}
		
		public boolean isOkButton2Enabled() {
			return btnOk2.isEnabled();
		}

		public boolean isCancelButtonEnabled() {
			return btnCancel.isEnabled();
		}

		public void setOkButtonEnabled(boolean b) {
			btnOk.setEnabled(b);
		}

		public void setOkButton2Enabled(boolean b) {
			if (btnOk2 != null)
				btnOk2.setEnabled(b);
		}

		public void setCancelButtonEnabled(boolean b) {
			btnCancel.setEnabled(b);
		}

		/**
		 * Adds an ActionListener to the <b>OK</b> button.
		 * @param l
		 */
		public void addOkButtonActionListener(ActionListener l) {
			btnOk.addActionListener(l);
		}

		/**
		 * Adds an ActionListener to the second <b>OK</b> button.
		 * @param l
		 */
		public void addOkButton2ActionListener(ActionListener l) {
			btnOk2.addActionListener(l);
		}

		/**
		 * Adds an ActionListener to the <b>cancel</b> button.
		 * @param l
		 */
		public void addCancelButtonActionListener(ActionListener l) {
			btnCancel.addActionListener(l);
		}
		
    	/**
     	 * <p>Create the action that will be executed when user pressed the <i>ok</i> button.</p>
    	 * 
    	 * @return action that will be executed when user pressed the <i>cancel</i> button
    	 */
    	protected ActionListener getOKAction() {
    		// OK button action
    		return new ActionListener() {
    			/*
    			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    			 */
				public void actionPerformed(ActionEvent e) {
					boolean restoreProcessMultiple = false;
					/* Tests */
					/*File newFilePath = new File("/home/pablo/Pruebas crear SHP de geometrías derivadas/prueba/prueba.shp");

					long geomIndexes[][] = {{1, 2, 3}, {2, 3, 5, 6, 8}, {12, 14, 15, 16}, {2, 4, 5}, {1}, {10, 11}, {4, 6, 7, 9}}; // Points to Lines
					long geomIndexes[][] = {{1, 2, 3}, {2, 3, 5, 6, 8}, {12, 14, 15, 16}, {2, 4, 5}, {1}, {10, 11}, {4, 6, 7, 9}}; // Points to Polygons
					long geomIndexes[][] = {{1, 2, 3, 5, 7, 8, 9, 10}}; // Lines to Polygons

					FLyrVect sourceLayer = (FLyrVect)((View)view).getMapControl().getMapContext().getLayers().getLayer(0);
					
					//					parameters.setGeometryIndexes(new long[][]{{1, 2, 3}, {2, 3, 5, 6, 8}, {12, 14, 15, 16}, {2, 4, 5}, {1}, {10, 11}, {4, 6, 7, 9}});
					
					DerivativeGeometryProcessParameters parameters = new DerivativeGeometryProcessParameters (
							sourceLayer, // A cambiar
							newFilePath, // A cambiar
							"Copy of " + ((View)view).getMapControl().getMapContext().getLayers().getLayer(0).getName(), // A cambiar
							geomIndexes,
							//FShape.LINE, // A línea
							(short)FShape.POLYGON, // A polígono
							(View)view
							);
					 */
					
					if (parameters.getProcessID() == DerivedGeometryProcessParameters.POINTS_TO_LINE_OR_POLYGON_ID) {
						restoreProcessMultiple = true;

						if (e.getActionCommand().equals(BUTTON_OK_ACTION)) {
							parameters.setProcessID(DerivedGeometryProcessParameters.POINTS_TO_LINE_PROCESS_ID);
						}
						else {
							parameters.setProcessID(DerivedGeometryProcessParameters.POINTS_TO_POLYGON_PROCESS_ID);
						}
					}

					// Sets the feature indexes
					try {
						if (parameters.getProcessID() == DerivedGeometryProcessParameters.CLOSE_MULTILINE_PROCESS_ID) {
							long[][] indexes = new long[1][getSelectedFeaturesTable().getRowCount()];
	
							for (int i = 0; i < getSelectedFeaturesTable().getRowCount(); i++) {
								indexes[0][i] = Long.parseLong(((JLabel)getSelectedFeaturesTable().getValueAt(i, 1)).getText());
							}

							parameters.setGeometryIndexes(indexes);
						}
						else {
							// It's prepared to define multiple sets of points, but in isn't used in that way because I don't have enought time
							long[][] indexes = new long[1][getSelectedFeaturesTable().getRowCount()];

							for (int i = 0; i < getSelectedFeaturesTable().getRowCount(); i++) {
								indexes[0][i] = Long.parseLong(((JLabel)getSelectedFeaturesTable().getValueAt(i, 1)).getText());
							}
	
							parameters.setGeometryIndexes(indexes);
						}
					}
					catch(Exception ex) {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_getting_the_features_indexes"), ex);
						return;
					}

					// Hides this panel and sets the focus to the associated view
//					removeCombinedTool(); 
					closeWindow();
					PluginServices.getMDIManager().addWindow(parameters.getView());
					
					// Removes the geometries selected
					((DefaultTableModel)getSelectedFeaturesTable().getModel()).setRowCount(0);
					updateOkButtons();

					AddDerivedGeometrySHPProcess iprocess = new AddDerivedGeometrySHPProcess(
							PluginServices.getText(null, "Creation_derivative_geometry_layer_process"),
							PluginServices.getText(this, "Ongoing_process_please_wait"),
							parameters);

					IncrementableTask iTask = new IncrementableTask(iprocess, new ProgressPanel(false));
					iTask.addIncrementableListener(iprocess);
					iprocess.setIncrementableTask(iTask);
					final AddDerivedGeometrySHPProcess f_iprocess = iprocess;
					final View f_view = (View)parameters.getView();
					final boolean f_restoreProcessMultiple = restoreProcessMultiple;
					final boolean f_layer_was_created = (parameters.getDestLayer() != null);
					final IncrementableTask f_iTask = iTask;
					
					iTask.getProgressPanel().addComponentListener(new ComponentAdapter() {
						/*
						 * (non-Javadoc)
						 * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
						 */
						public void componentHidden(ComponentEvent e) {
							/* Forces to refresh the TOC */
							f_view.getTOC().setVisible(false);
							f_view.getTOC().setVisible(true);
							f_iTask.getProgressPanel().dispose();
							
							/* Adds listener to be notified by a layer removing */
							if (! f_layer_was_created) {
								if (parameters.getDestLayer() != null) {
									parameters.getMapControl().getMapContext().getLayers().addLayerCollectionListener(getNewLayerCollectionListener());
								}
							}
							
							if (f_restoreProcessMultiple == true) {
								parameters.setProcessID(DerivedGeometryProcessParameters.POINTS_TO_LINE_OR_POLYGON_ID);
							}
							
							showWindow();
//							parameters.getMapControl().addCombinedTool(getCombinedTool());

							/* Writes in the gvSIG log the results of the process */
							String text = "\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n" +
								PluginServices.getText(this, "Summary_of_the_new_shape_with_derivative_geometries_process") + ":\n" +
								f_iprocess.getLog() +
								"\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -\n";
							Logger.getLogger(getClass().getName()).debug(text);
						}
					});

					/* Starts the process */
					iprocess.start();
					iTask.start();
				}
    		};
    	}

    	/**
    	 * <p>Create the action that will be executed when user pressed the <i>cancel</i> button.</p>
    	 * 
    	 * @return action that will be executed when user pressed the <i>cancel</i> button
    	 */
    	protected ActionListener getCancelAction() {
    		// Cancel button action
    		return new ActionListener() {
    			/*
    			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    			 */
				public void actionPerformed(ActionEvent e) {
					doUnregistration();
					closeWindow();
				}
    		};
    	}
    }
    
    protected void refreshGeometries() {
    	try {
    		// Removes the previous table model
    		((DefaultTableModel)getAllFeaturesTable().getModel()).setRowCount(0);
    		((DefaultTableModel)getAllFeaturesTable().getModel()).setColumnCount(0);

    		// Adds the new columns and rows
    		SelectableDataSource sds = parameters.getSourceLayer().getRecordset();

    		// Columns
    		FieldDescription[] fields = sds.getFieldsDescription();
    		TableModel tableModel = getAllFeaturesTableModel();
    		
    		for (int i = 0; i < fields.length; i++) {
    			((DefaultTableModel)tableModel).addColumn(fields[i].getFieldName(), new Object[] {} );

//    			switch(fields[i].getFieldType()) {
//    				case Types.VARCHAR:
//    					break;
//    				case Types.DOUBLE:
//    					break;
//    				case Types.INTEGER:
//    					break;
//    				case Types.BOOLEAN:
//    					getAllFeaturesTable().getColumnModel().getColumn(i).setMaxWidth(12);
//    					break;
//    				case Types.DATE:
//    					break;
//    			}
    		}
    		
    		// Sets the least column width to all table's columns
    		for (int cIndex = 0; cIndex < getAllFeaturesTable().getColumnModel().getColumnCount(); cIndex++) {
    			getAllFeaturesTable().getColumnModel().getColumn(cIndex).setPreferredWidth(TABLE_COLUMN_PREF_WIDTH);
    			getAllFeaturesTable().getColumnModel().getColumn(cIndex).setMinWidth(TABLE_COLUMN_PREF_WIDTH);
    		}

			// Rows
    		com.hardcode.gdbms.engine.values.Value values[];
    		String[] rowValues; 

    		for (long i = 0; i < sds.getRowCount(); i++) {
    			values = sds.getRow(i);
    			rowValues = new String[fields.length];

    			for (int j = 0; j < rowValues.length; j++) {
    				rowValues[j] = values[j].toString();
    			}

    			((DefaultTableModel)tableModel).addRow(rowValues);
    		}
    	}
    	catch (Exception ex) {
    		NotificationManager.showMessageError(PluginServices.getText(null, "Failed_loading_features"), ex);
    		
    		// Forces to remove all columns and rows of the table
    		((DefaultTableModel)getAllFeaturesTable().getModel()).setRowCount(0);
    		((DefaultTableModel)getAllFeaturesTable().getModel()).setColumnCount(0);
    	}
    }
    
    protected TableModel getAllFeaturesTableModel() {
    	if (allFeaturesTableModel == null) {
    		allFeaturesTableModel = new ViewsTableModel();
    	}
    	
    	return allFeaturesTableModel;
    }

    protected TableModel getSelectedFeaturesTableModel() {
    	if (selectedFeaturesTableModel == null) {
    		selectedFeaturesTableModel = new ViewsTableModel();
    	}
    	
    	return selectedFeaturesTableModel;
    }

    /**
     * <p>Inherits from {@link DefaultTableModel DefaultTableModel} adding support for having different
     *  column renderers.</p>
     * 
     * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    protected class ViewsTableModel extends DefaultTableModel {

		/**
    	 * <p>Instances <code>LayerTypesTableModel</code>.</p>
    	 */
    	public ViewsTableModel() {
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
     * <p>Closes this window.</p>
     */
	public void closeWindow() {
		PluginServices.getMDIManager().closeWindow(this);
		isShown = false;
	}
	
	protected void showWindow() {
		PluginServices.getMDIManager().addWindow(this);
		isShown = true;
	}

	protected void updateOkButtons() {
		if (getSelectedFeaturesTableModel().getRowCount() == 0) {
			getAdaptedAcceptCancelPanel().setOkButtonEnabled(false);
			getAdaptedAcceptCancelPanel().setOkButton2Enabled(false);
		}
		else {
			getAdaptedAcceptCancelPanel().setOkButtonEnabled(true);
			getAdaptedAcceptCancelPanel().setOkButton2Enabled(true);
		}
	}

	public void activationChanged(LayerEvent e) {
		if ((e.getSource().isVisible()) && (e.getSource().isActive()) && (! e.getSource().isEditing())) {
			if (! isShown) {
//				parameters.getMapControl().addCombinedTool(getCombinedTool());
				showWindow();
				refreshSelectedGeometries();
				updateViewListener(); // If wasn't registered -> registers, otherwise no
			}
		} else {
			if (isShown) {
//				removeCombinedTool();
				unregisterSnappingTool();
				closeWindow();
			}
		}
	}

	public void editionChanged(LayerEvent e) {
		if ((e.getSource().isVisible()) && (e.getSource().isActive()) && (! e.getSource().isEditing())) {
			if (! isShown) {
				// The compound behavior must invoke the paintComponent of the new combined tool
//				CompoundBehavior cBehavior = new CompoundBehavior(new Behavior[0]);
//				cBehavior.addMapBehavior(getCombinedTool(), true);

//				parameters.getMapControl().addCombinedTool(getCombinedTool());
				showWindow();
				refreshSelectedGeometries();
				updateViewListener(); // If wasn't registered -> registers, otherwise no
			}
		} else {
			if (isShown) {
//				removeCombinedTool();
				unregisterSnappingTool();
				closeWindow();
			}
		}
	}

	public void nameChanged(LayerEvent e) {
	}

	public void visibilityChanged(LayerEvent e) {
		if ((e.getSource().isVisible()) && (e.getSource().isActive()) && (! e.getSource().isEditing())) {
			if (! isShown) {
				// The compound behavior must invoke the paintComponent of the new combined tool
//				CompoundBehavior cBehavior = new CompoundBehavior(new Behavior[0]);
//				cBehavior.addMapBehavior(getCombinedTool(), true);

//				parameters.getMapControl().addCombinedTool(getCombinedTool());
				showWindow();
				refreshSelectedGeometries();
				updateViewListener(); // If wasn't registered -> registers, otherwise no
			}
		} else {
			if (isShown) {
//				removeCombinedTool();
				unregisterSnappingTool();
				closeWindow();
			}
		}
	}
	
	public LayerCollectionListener getAssociatedLayerCollectionListener() {
		if (layerCollectionListener == null) {
			layerCollectionListener = new LayerCollectionListener() {

				public void layerAdded(LayerCollectionEvent e) {
				}

				public void layerAdding(LayerCollectionEvent e)	throws CancelationException {
				}

				public void layerMoved(LayerPositionEvent e) {
				}

				public void layerMoving(LayerPositionEvent e) throws CancelationException {
				}

				public void layerRemoved(LayerCollectionEvent e) {
				}

				public void layerRemoving(LayerCollectionEvent e) throws CancelationException {
					// Removes all registered listeners when layer has been removed
					if (e.getAffectedLayer() == parameters.getSourceLayer()) {
						// Asks user to remove this control panel
						// (this avoids removed the panel when the layer is removed updating the TOC)
						//if (JOptionPane.showConfirmDialog(parameters.getView(), PluginServices.getText(null, "Really_want_to_remove_the_control_panel_of_the_layer") + " " + parameters.getSourceLayer().getName(), PluginServices.getText(null, "Confirmation"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							doUnregistration();
							closeWindow();
						//}
					}
				}

				public void visibilityChanged(LayerCollectionEvent e) throws CancelationException {
				}
	        };
		}
		
		return layerCollectionListener;
	}
	
	public boolean doUnregistration() {
		try {
			FLyrVect sourceLayer = parameters.getSourceLayer();
			FLayers layers = parameters.getMapControl().getMapContext().getLayers();

			// Removes the associated source layer collection listener
			layers.removeLayerCollectionListener(getAssociatedLayerCollectionListener());
			
			// Removes the associated destination layer collection listener
			if (parameters.getDestLayer() != null)
				layers.removeLayerCollectionListener(getNewLayerCollectionListener());
	
			// Removes the source layer listener
			sourceLayer.removeLayerListener(this);

			// Removes the combined tool
//			removeCombinedTool();

			// Unregisters the snapping tool
			unregisterSnappingTool();
			
			// Removes the associate View's listener
			unregisterViewListener();

			PluginServices.getLogger().debug(PluginServices.getText(this, "Derivative_geometries_control_panel_listener_removed"));
		}
		catch(Exception ex) {
			NotificationManager.showMessageError(PluginServices.getText(null, "Failed_unregistering_derivative_geometry_control_panel_listener"), ex);
			
			return false;
		}

		return true;
	}

	protected void unregisterSnappingTool() {
//		// Changes to the default tool -> zoom
//		if (parameters.getMapControl().getCurrentTool().equals(SnappingTool.TOOL_ID)) {
//			// Restores to the default tool: zoom in
//			parameters.getMapControl().setTool("zoomIn");
//			PluginServices.getMainFrame().setSelectedTool("ZOOM_IN");
//
//			parameters.getMapControl().drawMap(false);
//		}
	}
	
//	protected Behavior getCombinedTool() {
//		if (combinedTool == null) {
//			combinedTool = new AdaptedPointBehavior(new AddDerivativeGeometriesListener(this));
//		}
//
//		return combinedTool;
//	}

	public DerivedGeometryProcessParameters getParameters() {
		return parameters;
	}

	public void selectGeometry(int geometryIndex) {
		// Selects in the table with all geometries
		getAllFeaturesTable().getSelectionModel().addSelectionInterval(geometryIndex, geometryIndex);
	}
	
//	protected void removeCombinedTool() {
//		Behavior cTool = (Behavior) parameters.getMapControl().getCombinedTool();
//
//		if (cTool == null)
//			return;
//
//		if (cTool instanceof CompoundBehavior)
//			((CompoundBehavior)cTool).removeMapBehavior(getCombinedTool());
//		else {
//			if (cTool.equals(getCombinedTool()))
//				parameters.getMapControl().removeCombinedTool();
//		}
		
//		parameters.getMapControl().removeCombinedTool(getCombinedTool());
//	}

	protected void refreshSelectedGeometries() {
		try {
			SelectableDataSource sds = parameters.getSourceLayer().getRecordset();
			FBitSet newSelection = (FBitSet) sds.getSelection().clone();
			newSelection.clear();
			
			int[] sRows = getAllFeaturesTable().getSelectedRows();

			for (int i = 0; i < sRows.length; i++) {
				newSelection.set( sRows[i] );
			}

			sds.setSelection(newSelection);
		}
		catch (Exception ex) {
			NotificationManager.showMessageError(PluginServices.getText(null, "Failed_selecting_geometries_in_layer"), ex);
		}
	}

	protected void registerViewListener() {
        /* Window listener -> hides this control panel when the view looses the window is closed */
        Container container = parameters.getView();
        
        if (container == null)
        	return;
        
        while (! (container instanceof JInternalFrame) ) {
        	container = container.getParent();
        	
        	if (container == null)
        		return;
        }

        container.addComponentListener(getViewListener());
	}
	
	protected void unregisterViewListener() {
        /* Window listener -> hides this control panel when the view looses the window is closed */
        Container container = parameters.getView();

        if (container == null)
        	return;
        
        while (! (container instanceof JInternalFrame) ) {
        	container = container.getParent();
        	
        	if (container == null)
        		return;
        }

        container.removeComponentListener(getViewListener());
	}
	
	protected void updateViewListener() {
		// Forces to set the view listener to the associated view
		// (When a view is closed, next time is opened, the container (JInternalFrame) is another object)
		unregisterViewListener();
		registerViewListener();
	}

	protected ComponentListener getViewListener() {
		if (viewListener == null) {
			viewListener = new ComponentAdapter() {
				/*
				 * (non-Javadoc)
				 * @see java.awt.event.ComponentAdapter#componentHidden(java.awt.event.ComponentEvent)
				 */
				public void componentHidden(ComponentEvent e) {
					mustRestoreWindow = true;

					// Hides the control panel
					closeWindow();
					
					isShown = false;
				}
	        };
		}

		return viewListener;
	}

	protected LayerCollectionListener getNewLayerCollectionListener() {
		if (newLayerCollectionListener == null) {
			newLayerCollectionListener = new LayerCollectionListener() {

				public void layerAdded(LayerCollectionEvent e) {
				}

				public void layerAdding(LayerCollectionEvent e)
						throws CancelationException {
					
				}

				public void layerMoved(LayerPositionEvent e) {
					
				}

				public void layerMoving(LayerPositionEvent e)
						throws CancelationException {
					
				}

				public void layerRemoved(LayerCollectionEvent e) {
					
				}

				public void layerRemoving(LayerCollectionEvent e) throws CancelationException {
					// Asks user to confirm removing the destination layer (that will also remove this control panel)
					//if ((parameters.getDestLayer() != null) && (e.getAffectedLayer() == parameters.getDestLayer()) && (JOptionPane.showConfirmDialog(parameters.getView(), PluginServices.getText(null, "Really_want_remove_to_the_layer") + " " + parameters.getDestLayer().getName(), PluginServices.getText(null, "Confirmation"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
					try {
						doUnregistration();
						closeWindow();
						JOptionPane.showMessageDialog(parameters.getMapControl(), PluginServices.getText(null, "Finished_the_derivative_geometries_tool_in_layers") + ": " + parameters.getSourceLayer().getName() + ", " + e.getAffectedLayer().getName(), PluginServices.getText(null, "Information"), JOptionPane.INFORMATION_MESSAGE);
					}
					catch(Exception ex)  {
						NotificationManager.showMessageError(PluginServices.getText(null, "Failed_finishing_the_derivative_geometries_tool_in_layer") + ": " + e.getAffectedLayer().getName(), ex);
					}
					//}
				}

				public void visibilityChanged(LayerCollectionEvent e)
						throws CancelationException {
					
				}
			};
		}
		
		return newLayerCollectionListener;
	}

//	public ComponentListener getViewListener() {
//		if (viewListener == null) {
//			viewListener = new ComponentAdapter() {
//
//				public void componentHidden(ComponentEvent e) {
//					mustRestoreWindow = true;
//					closeWindow();
//				}
//
//				public void componentMoved(ComponentEvent e) {
//				}
//
//				public void componentResized(ComponentEvent e) {
//				}
//
//				public void componentShown(ComponentEvent e) {
//					if (mustRestoreWindow) {
//						showWindow();
//						mustRestoreWindow = false;
//					}
//				}
//	        };
//		}
//		
//		return viewListener;
//	}
	
    
    /**
     * As combined tool, in a CompoundBehavior its "paintComponent" method is always called. The idea is painting nothing to
     * accelerate the MapControl painting process.
     * 
     * @see PointBehavior
     * 
	 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
     */
    protected class AdaptedPointBehavior extends PointBehavior {
    	/**
    	 * @see PointBehavior#PointBehavior(PointListener)
    	 */
		public AdaptedPointBehavior(PointListener mli) {
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

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public void drawValueChanged(LayerEvent e) {
	}
}
