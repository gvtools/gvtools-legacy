package com.iver.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.iver.andami.Launcher;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.plugins.ExtensionDecorator;
import com.iver.andami.plugins.config.generate.Extensions;
import com.iver.andami.plugins.config.generate.PluginConfig;
import com.iver.andami.preferences.DlgPreferences;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.core.preferences.general.AppearancePage;
import com.iver.core.preferences.general.DirExtensionsPage;
import com.iver.core.preferences.general.ExtensionPage;
import com.iver.core.preferences.general.ExtensionsPage;
import com.iver.core.preferences.general.FolderingPage;
import com.iver.core.preferences.general.GeneralPage;
import com.iver.core.preferences.general.LanguagePage;
import com.iver.core.preferences.general.ResolutionPage;
import com.iver.core.preferences.network.FirewallPage;
import com.iver.core.preferences.network.NetworkPage;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 *
 * <p>
 * Extension that provides support for visual application configuration
 * through a Preferences dialog where the user can specify its own settings for
 * general purpose aspects.
 * </p>
 * <p>
 * Adding new preference pages is made through ExtensionPoints by invoking
 * <b>
 * 	this.extensionPoints.add("AplicationPreferences","YourIPreferencesClassName", yourIPreferencesPage);
 * </b>
 * and then call <b>DlgPreferences.refreshExtensionPoints();</b>
 * </p>
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 *
 */
public class PreferencesExtension extends Extension{
	private ExtensionPoints extensionPoints =
		ExtensionPointsSingleton.getInstance();

	private boolean initilizedExtensions=false;

	public void initialize() {
		initializeCoreExtensions();
	}

	public void execute(String actionCommand) {
		if (!this.initilizedExtensions) {
			initializeExtensions();
			initializeExtensionsConfig();
			this.initilizedExtensions = true;
		}

		DlgPreferences dlgPreferences=PluginServices.getDlgPreferences();
		dlgPreferences.refreshExtensionPoints();
		PluginServices.getMDIManager().addWindow(dlgPreferences);
	}
	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}

	private void initializeCoreExtensions() {
		this.extensionPoints.add("AplicationPreferences","GeneralPage", new GeneralPage());
		this.extensionPoints.add("AplicationPreferences","NetworkPage", new NetworkPage());
		this.extensionPoints.add("AplicationPreferences","FirewallPage", new FirewallPage());
		this.extensionPoints.add("AplicationPreferences","DirExtensionsPage", new DirExtensionsPage());
		this.extensionPoints.add("AplicationPreferences","LanguagePage", new LanguagePage());
		this.extensionPoints.add("AplicationPreferences","ExtensionsPage", new ExtensionsPage());
		this.extensionPoints.add("AplicationPreferences","AppearancePage", new AppearancePage());
		this.extensionPoints.add("AplicationPreferences","FolderingPage", new FolderingPage());
		this.extensionPoints.add("AplicationPreferences","ResolutionPage", new ResolutionPage());

		//Falta los plugin
	}

	private void initializeExtensionsConfig() {
		HashMap pc = Launcher.getPluginConfig();
		ArrayList array = new ArrayList();
		Iterator iter = pc.values().iterator();

		while (iter.hasNext()) {
			array.add(((PluginConfig) iter.next()).getExtensions());
		}

		Extensions[] exts = (Extensions[]) array.toArray(new Extensions[0]);
		for (int i = 0; i < exts.length; i++) {
			for (int j = 0; j < exts[i].getExtensionCount(); j++) {
				com.iver.andami.plugins.config.generate.Extension ext = exts[i]
						.getExtension(j);
				String sExt = ext.getClassName().toString();
				// String pn = null;
				// pn = sExt.substring(0, sExt.lastIndexOf("."));
				// dlgPrefs.addPreferencePage(new PluginsPage(pn));
				// dlgPrefs.addPreferencePage(new ExtensionPage(ext));
				this.extensionPoints.add("AplicationPreferences",sExt, new ExtensionPage(ext));
			}
		}
	}
	/**
	 *
	 */
	private void initializeExtensions() {


		Iterator i =PluginServices.getExtensions();
		while (i.hasNext()) {
			ExtensionDecorator extension = (ExtensionDecorator) i.next();

			if (extension.getExtension() instanceof IPreferenceExtension) {
				IPreferenceExtension pe=(IPreferenceExtension)extension.getExtension();
				IPreference[] pp=pe.getPreferencesPages();
				//dlgPrefs.addPreferencePage(pe.getPreferencesPage());
				for (int j=0;j<pp.length;j++) {
					this.extensionPoints.add("AplicationPreferences",pp[j].getID(), pp[j]);
				}
			}
		}
	}
}
