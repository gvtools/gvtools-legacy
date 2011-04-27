package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.gui.panels.FPanelAbout;

public class Annotation_AboutExtension extends Extension {

	public void execute(String actionCommand) {

	}

	public void initialize() {

	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isVisible() {
		return false;
	}

	public void postInitialize() {
		super.postInitialize();
		About about = (About) PluginServices.getExtension(About.class);
		FPanelAbout panelAbout = about.getAboutPanel();
		java.net.URL aboutURL = this.getClass().getResource("/about.htm");
		panelAbout.addAboutUrl(PluginServices.getText(this, "annotations"),
				aboutURL);

	}
}
