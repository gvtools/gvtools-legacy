package org.gvsig.hyperlink.config.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.gvsig.hyperlink.ILinkActionManager;
import org.gvsig.hyperlink.LinkControls;
import org.gvsig.hyperlink.config.LayerLinkConfig;
import org.gvsig.hyperlink.config.LinkConfig;
import org.gvsig.hyperlink.layers.ILinkLayerManager;
import org.gvsig.hyperlink.layers.IncompatibleLayerException;
import org.gvsig.hyperlink.layers.ManagerRegistry;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.legend.gui.AbstractThemeManagerPage;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class ConfigTab extends AbstractThemeManagerPage implements IWindow, ActionListener, ItemListener {
	FLayer layer;
	ArrayList rows = new ArrayList();
	JPanel rowList = null;
	GridBagLayout listLayout = null;
	String[] actionCodes = null;
	String[] actionNames = null;
	WindowInfo _windowInfo = null;
	JButton jbt_accept = null, jbt_cancel = null;
	JPanel emptyRow;
	JCheckBox jcb_enabled = null;
	JPanel borderPanel = null;
	JButton jbt_addAction = null, jbt_removeAction=null;
	
	public ConfigTab() {
		super();
		initialize();
	}

	private  void initialize() {
		this.setLayout(new GridBagLayout());
		
		Insets insets = new Insets(8, 8, 8, 8);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		this.add(getEnabledCheckBox(), constraints);

		borderPanel = new JPanel(new GridBagLayout());
		borderPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				PluginServices.getText(this, "Actions"),
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null)
		);

		insets = new Insets(8, 8, 8, 8);
		constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		borderPanel.add(getAddRemoveActionsPanel(), constraints);
		
		listLayout = new GridBagLayout();
		rowList = new JPanel(listLayout);
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.SOUTH;
		emptyRow = new JPanel();
		rowList.add(emptyRow, constraints);
		
		JScrollPane scrolledList = new JScrollPane();
		scrolledList.setBorder(null);
		scrolledList.setViewportView(rowList);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.CENTER;
		borderPanel.add(scrolledList, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		this.add(borderPanel, constraints);
		
//		initAcceptCancelButtons(); uncomment this if you want to use this window outside the ThemeManager
	}
	
	private void initAcceptCancelButtons() {
		JPanel acceptCancelButtons = new JPanel(new GridBagLayout());
		Insets insets = new Insets(14, 4, 8, 8);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.EAST;
		acceptCancelButtons.add(getAcceptButton(), constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.EAST;
		acceptCancelButtons.add(getCancelButton(), constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.SOUTHEAST;
		this.add(acceptCancelButtons, constraints);
	}
  
    private String[] getActionNames() {
    	if (actionNames==null) {
    		ExtensionPoint actions = (ExtensionPoint) ExtensionPointsSingleton.getInstance().get("HyperLinkAction");
    		String[] actionArray = new String[actions.size()];
    		Iterator actionsIter = actions.values().iterator();
    		int i = 0;
    		while (actionsIter.hasNext()) {
    			ILinkActionManager action = (ILinkActionManager) actionsIter.next();
    			actionArray[i++] = action.getName();
    		}
    		actionNames = actionArray;
    	}
    	return actionNames;
    }

    private String[] getActionCodes() {
    	if (actionCodes==null) {
    		ExtensionPoint actions = (ExtensionPoint) ExtensionPointsSingleton.getInstance().get("HyperLinkAction");
    		String[] actionArray = new String[actions.size()];
    		Iterator actionsIter = actions.values().iterator();
    		int i = 0;
    		while (actionsIter.hasNext()) {
    			ILinkActionManager action = (ILinkActionManager) actionsIter.next();
    			actionArray[i++] = action.getActionCode();
    		}
    		actionCodes = actionArray;
    	}
    	return actionCodes;
    }

    private String[] getCandidateFields() {
    	LinkControls ext = (LinkControls) PluginServices.getExtension(LinkControls.class);
    	ManagerRegistry registry  = ext.getLayerManager();
    	try {
    		ILinkLayerManager manager;
    		manager = registry.get(layer);
    		manager.setLayer(layer);
    		return manager.getFieldCandidates();
    	} catch (InstantiationException e) {
    		PluginServices.getLogger().warn("Hyperlink: error getting candidate fields", e);
    	} catch (IllegalAccessException e) {
    		PluginServices.getLogger().warn("Hyperlink: error getting candidate fields", e);
    	} catch (ClassNotFoundException e) {
    		PluginServices.getLogger().warn("Hyperlink: error getting candidate fields", e);
    	} catch (IncompatibleLayerException e) {
    		PluginServices.getLogger().warn("Hyperlink: error getting candidate fields", e);
		}
    	return new String[0];
    }
   
	public void acceptAction() {
		applyAction();
	}

	public void applyAction() {
		LayerLinkConfig config = new LayerLinkConfig();
		for (int i=0; i<rows.size(); i++) {
			LinkRow row = (LinkRow) rows.get(i);
			config.addLink(getActionCodes()[row.getSelectedAction()], row.getSelectedField(), row.getExtension());
		}
		config.setEnabled(getEnabledCheckBox().isSelected());
		layer.setProperty(LinkControls.LAYERPROPERTYNAME, config);
	}

	public void cancelAction() {
	}


	public String getName() {
		return PluginServices.getText(this,"Hyperlink");
	}

	public void setModel(FLayer layer) {
		this.layer = layer;
		synchronized (rows) {
			LinkControls ext = (LinkControls) PluginServices.getExtension(LinkControls.class);
			LayerLinkConfig layerConfig = ext.loadLegacyConfig(layer);
			for (int i=rows.size()-1; i>=0; i--) { // clean rows
				LinkRow row = (LinkRow) rows.remove(i);
				rowList.remove(row);
			}

			if (layerConfig!=null) {
				for (int i=0; i<layerConfig.linkCount(); i++) {
					LinkRow row = addRow();
					LinkConfig config = layerConfig.getLink(i);
					String selectedAction = config.getActionCode();
					String[] actions = getActionCodes();
					for (int j=0; j<actions.length; j++) {
						if (actions[j].equals(selectedAction)) {
							row.setSelectedAction(j);
						}
					}
					row.setSelectedField(config.getFieldName());
					row.setExtension(config.getExtension());
				}
				if (layerConfig.linkCount()<1) {
					addRow(); //just one row by default				
				}
				setLinkEnabled(layerConfig.isEnabled());
			}
			else {
				addRow(); //just one row by default
				setLinkEnabled(false);
			}
		}
	}

	protected JPanel getAddRemoveActionsPanel(){
		JPanel container = new JPanel();
		container.add(getAddActionButton());
		container.add(getRemoveActionButton());
		return container;
	}
	
	protected LinkRow addRow(){
		LinkRow row = new LinkRow();
		row.setFields(getCandidateFields());
		row.setActions(getActionNames());
		synchronized (rows) {
			rows.add(row);

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = rows.size()-1;
			constraints.weightx = 1.0;
			constraints.weighty = 0.0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.insets = new Insets(2,4,2,4);
			constraints.anchor = GridBagConstraints.NORTH;
			rowList.add(row, constraints);

			constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = rows.size();
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.CENTER;
			listLayout.setConstraints(emptyRow, constraints);
		}
		validate();
		return row;
	}

	protected boolean removeBottomRow() {
		LinkRow row;
		synchronized (rows) {
			if (rows.size()<=1)
				return false;
			row = (LinkRow) rows.get(rows.size()-1);
			rows.remove(rows.size()-1);
		}
		rowList.remove(row);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = rows.size();
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.CENTER;
		listLayout.setConstraints(emptyRow, constraints);
		
		validate();
		repaint();
		return true;
	}

	protected JButton getAddActionButton() {
		if (jbt_addAction==null) {
			jbt_addAction = new JButton(PluginServices.getText(this, "Add_action"));
			jbt_addAction.setSize(150, 70);
			jbt_addAction.setActionCommand("addButton");
			jbt_addAction.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals("addButton")) {
						addRow();					
					}
				}
			}
			);
		}
		return jbt_addAction;
	}

	protected JButton getRemoveActionButton() {
		if (jbt_removeAction==null) {
			jbt_removeAction = new JButton(PluginServices.getText(this, "Remove_action"));
			jbt_removeAction.setActionCommand("removeButton");
			jbt_removeAction.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals("removeButton")) {
						removeBottomRow();					
					}
				}
			}
			);
			return jbt_removeAction;
		}
		return jbt_removeAction;
	}

	public WindowInfo getWindowInfo() {
		if (_windowInfo == null) {
			_windowInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.MAXIMIZABLE | WindowInfo.RESIZABLE);
			_windowInfo.setWidth(680);
			_windowInfo.setHeight(500);
			_windowInfo.setTitle(PluginServices.getText(this, "Hyperlink_Settings"));
		}
		return _windowInfo;
	}

	protected JButton getAcceptButton() {
		if (jbt_accept==null) {
			jbt_accept = new org.gvsig.gui.beans.swing.JButton(PluginServices.getText(this, "Aceptar"));
			jbt_accept.addActionListener(this);
		}
		return jbt_accept;
	}

	protected JButton getCancelButton() {
		if (jbt_cancel==null) {
			jbt_cancel = new org.gvsig.gui.beans.swing.JButton(PluginServices.getText(this, "Cancel"));
			jbt_cancel.addActionListener(this);
		}
		return jbt_cancel;
	}

	protected JCheckBox getEnabledCheckBox() {
		if (jcb_enabled==null) {
			jcb_enabled = new JCheckBox(PluginServices.getText(this, "Enable_hyperlink"), true);
			jcb_enabled.addItemListener(this);
		}
		return jcb_enabled;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==getAcceptButton()) {
			acceptAction();
			PluginServices.getMDIManager().closeWindow(this);

		}
		else if (e.getSource()==getCancelButton()) {
			cancelAction();
			PluginServices.getMDIManager().closeWindow(this);
		}
	}

	protected void setListEnabled(boolean enabled) {
		getAddActionButton().setEnabled(enabled);
		getRemoveActionButton().setEnabled(enabled);
		synchronized (rows) {
			for (int i=0; i<rows.size(); i++) {
				LinkRow row = (LinkRow)rows.get(i);
				row.setEnabled(enabled);
			}
		}
	}

	protected void setLinkEnabled(boolean enabled) {
		getEnabledCheckBox().setSelected(enabled);
		setListEnabled(enabled);
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource()==getEnabledCheckBox()) {
			if (e.getStateChange()==ItemEvent.DESELECTED) {
				setListEnabled(false);
			}
			else {
				setListEnabled(true);
			}
		}
	}


	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
