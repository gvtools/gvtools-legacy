package org.gvsig.layerLoadingOrder;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.plugins.IExtension;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.cit.gvsig.fmap.layers.order.DefaultOrderManager;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class SmartOrderExtension extends Extension implements
		IPreferenceExtension {
	protected OrderConfig globalConfig = null;
	private SmartOrderPage prefPage = null;

	public void execute(String actionCommand) {
	}

	public void initialize() {
		registerIcons();
		initGlobalConfig();
		registerOrderManager();
		// registerPrefPage();
	}

	private void registerOrderManager() {
		ExtensionPoint ep = (ExtensionPoint) ExtensionPointsSingleton
				.getInstance().get(DefaultOrderManager.getExtensionPointName());
		if (ep == null) {
			ep = new ExtensionPoint(DefaultOrderManager.getExtensionPointName());
			ExtensionPointsSingleton.getInstance().put(ep);
		}
		SmartOrderManager manager = new SmartOrderManager();
		ep.put(manager.getCode(), manager);

		// if there is no default Order Manager, set our manager as default
		IExtension ext = PluginServices
				.getExtension(com.iver.cit.gvsig.LayerOrderExtension.class);
		com.iver.cit.gvsig.LayerOrderExtension orderExt = (com.iver.cit.gvsig.LayerOrderExtension) ext;
		if (!orderExt.existsDefaultOrderManager()) {
			orderExt.setDefaultOrderManager(manager);
		}
	}

	// private void registerPrefPage() {
	// ExtensionPointsSingleton.getInstance().add("AplicationPreferences",
	// SmartOrderPage.class.getName(), new SmartOrderPage());
	// }

	private void initGlobalConfig() {
		globalConfig = new GlobalOrderConfig();
	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isVisible() {
		return false;
	}

	public OrderConfig getConfig() {
		if (globalConfig == null) {
			initGlobalConfig();
		}
		return globalConfig;
	}

	public IPreference getPreferencesPage() {
		if (prefPage == null) {
			prefPage = new SmartOrderPage();
		}
		return prefPage;
	}

	public IPreference[] getPreferencesPages() {
		IPreference[] preferences = new IPreference[1];
		preferences[0] = getPreferencesPage();
		return preferences;
	}

	protected void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"smart-order-manager",
				this.getClass().getClassLoader()
						.getResource("images/smartOrderManager.png"));
	}
}
