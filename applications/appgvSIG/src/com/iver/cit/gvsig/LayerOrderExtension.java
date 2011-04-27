package com.iver.cit.gvsig;

import java.security.KeyException;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.order.DefaultOrderManager;
import com.iver.cit.gvsig.fmap.layers.order.OrderManager;
import com.iver.cit.gvsig.gui.preferencespage.LayerOrderPage;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class LayerOrderExtension extends Extension 
	implements IPreferenceExtension	{
	LayerOrderPage page = null;

	public void execute(String actionCommand) {}

	public void initialize() {
		registerIcons();
//		registerPrefPage();
		registerDefaultManager();
		initDefaultOrderManager();
		
	}

	public boolean isEnabled() {
		return false;
	}
	public boolean isVisible() {
		return false;
	}

	public IPreference getPreferencesPage() {
		if (page==null) {
			page = new LayerOrderPage();
		}
		return page;
	}
	
//  Not necessary: the page gets automatically registered by implemented IPreferenceExtension 
//	private void registerPrefPage() {
//		ExtensionPointsSingleton.getInstance().add("AplicationPreferences",
//				LayerOrderPage.class.getName(), new LayerOrderPage());
//	}

	private void registerDefaultManager() {
		DefaultOrderManager manager = new DefaultOrderManager();
		ExtensionPointsSingleton.getInstance().
			add(DefaultOrderManager.getExtensionPointName(),
				manager.getCode(),
				manager);

	}

	private void initDefaultOrderManager() {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		if (xml!=null && xml.contains(DefaultOrderManager.getExtensionPointName())) {
			String code = xml.getStringProperty(DefaultOrderManager.getExtensionPointName());
			try {
				ExtensionPoint ep = 
					(ExtensionPoint) ExtensionPointsSingleton.getInstance().
					get(DefaultOrderManager.getExtensionPointName());
				if (ep.containsKey(code)) {
					ep.addAlias(code, DefaultOrderManager.getDefaultManagerKey());
				}
			}
			catch (Exception ex) {
				Logger.getLogger(MapContext.class).warn("Error getting default layer order manager", ex);
			}
		}
	}

	/**
	 * Sets as system default OrderManager the provided order manager. 
	 */
	public void setDefaultOrderManager(OrderManager manager) {
		if (manager==null)
			return;
		ExtensionPoint ep = 
			(ExtensionPoint) ExtensionPointsSingleton.getInstance().
			get(DefaultOrderManager.getExtensionPointName());
		if (ep!=null) {
			try {
				if (!ep.containsKey(manager.getCode())) {
					ep.put(manager.getCode(), manager);
				}
				ep.addAlias(manager.getCode(), DefaultOrderManager.getDefaultManagerKey());
			} catch (KeyException e) {} // this can't happen, we have ensured the key is present
		}

		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		xml.putProperty(DefaultOrderManager.getExtensionPointName(), manager.getCode());
	}

	/**
	 * Gets the default OrderManager, as configured in the extension point.
	 * 
	 * @return The default OrderManager, as configured in the extension point,
	 * or null if no manager was configured. 
	 */
	public OrderManager getDefaultOrderManager() {
		return DefaultOrderManager.getDefaultOrderManager();
	}

	/**
	 * Checks whether an OrderManager was configured as default in the
	 * extension point.
	 * 
	 * @return <code>true</code> if there is a default OrderManager configured
	 * in the extension point, or <code>false</code> otherwise.
	 */
	public boolean existsDefaultOrderManager() {
		try {
			ExtensionPoint ep = 
				(ExtensionPoint) ExtensionPointsSingleton.getInstance().
				get(DefaultOrderManager.getExtensionPointName());
			if (ep!=null) {
				Object obj = ep.create(DefaultOrderManager.getDefaultManagerKey());
				if (obj!=null) {
					return true;				
				}
			}
		}
		catch (Exception ex) {
			Logger.getLogger(MapContext.class).warn("Error getting default layer order manager", ex);
		}
		return false;
	}
	
	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"layer-order-manager",
				InfoToolExtension.class.getClassLoader().getResource("images/orderManager.png")
		);
	}

	public IPreference[] getPreferencesPages() {
		IPreference[] preferences=new IPreference[1];
		preferences[0]=getPreferencesPage();
		return preferences;
	}
}
