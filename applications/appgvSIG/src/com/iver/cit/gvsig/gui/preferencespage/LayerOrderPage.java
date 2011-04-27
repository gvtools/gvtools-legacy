package com.iver.cit.gvsig.gui.preferencespage;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.IExtension;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.fmap.layers.order.DefaultOrderManager;
import com.iver.cit.gvsig.fmap.layers.order.OrderManager;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class LayerOrderPage extends AbstractPreferencePage implements ItemListener {
	private static final long serialVersionUID = 1L;
	private ImageIcon icon = null;
	private boolean initialized = false;
	private JComboBox managerCombo = null;
	protected boolean changed = false;

	public void setChangesApplied() {
	}

	public LayerOrderPage() {
		super();
		setParentID(ViewPage.class.getName());
	}

	public void storeValues() throws StoreException {
		Object object = managerCombo.getSelectedItem();
		if (object instanceof OrderManager) {
			OrderManager manager = (OrderManager) object;
			IExtension ext = PluginServices.getExtension(com.iver.cit.gvsig.LayerOrderExtension.class);
			com.iver.cit.gvsig.LayerOrderExtension orderExt = (com.iver.cit.gvsig.LayerOrderExtension) ext;
			orderExt.setDefaultOrderManager(manager);
		}
		changed = false;
	}

	public String getID() {
		return getClass().getName();
	}

	public ImageIcon getIcon() {
		if (icon==null) {
//			icon = new ImageIcon(this.getClass().getClassLoader().getResource("images/orderManager.png"));
			icon = PluginServices.getIconTheme().get("layer-order-manager");
		}
		return icon;
	}

	public JPanel getPanel() {
		return this;
	}

	public String getTitle() {
		return PluginServices.getText(this, "Layer_loading_order");
	}

	public void initializeDefaults() {
	}

	public void initializeValues() {
		if (!initialized) {
			initialized = true;
			createUI();
		}
	}

	private void createUI() {
		JLabel label = new JLabel(PluginServices.getText(this, "Default_order_manager"));
		managerCombo = new JComboBox();
		try {
			ExtensionPoint ep = 
				(ExtensionPoint) ExtensionPointsSingleton.getInstance().
				get(DefaultOrderManager.getExtensionPointName());
			IExtension ext = PluginServices.getExtension(com.iver.cit.gvsig.LayerOrderExtension.class);
			com.iver.cit.gvsig.LayerOrderExtension orderExt = (com.iver.cit.gvsig.LayerOrderExtension) ext;
			String defaultManager = null;
			if (orderExt.existsDefaultOrderManager()) {
				// only set the item if there is a configured default order manager,
				// otherwise we would set DefaultOrderManager() as user preference
				defaultManager = orderExt.getDefaultOrderManager().getCode();
				
			}
			Iterator iterator = ep.values().iterator();
			while (iterator.hasNext()) {
				Object object = iterator.next();
				if (object instanceof OrderManager) {
					OrderManager manager = (OrderManager) object;
					managerCombo.addItem(object);
					if (defaultManager!=null
							&& manager.getCode().equals(defaultManager)) {
						managerCombo.setSelectedItem(object);
					}
				}
			}
		}
		catch (Exception ex) {
			PluginServices.getLogger().warn("Error getting layer order managers", ex);
		}
		managerCombo.addItemListener(this);
		this.addComponent(label, managerCombo, GridBagConstraints.NONE, new Insets(8, 8, 8, 8));
	}

	public boolean isValueChanged() {
		return changed;
	}

	public void itemStateChanged(ItemEvent e) {
		changed = true;
	}

}
