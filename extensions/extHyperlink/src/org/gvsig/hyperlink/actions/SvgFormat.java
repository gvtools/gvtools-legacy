package org.gvsig.hyperlink.actions;

import java.io.Serializable;
import java.net.URI;

import org.gvsig.hyperlink.AbstractActionManager;
import org.gvsig.hyperlink.AbstractHyperLinkPanel;

import com.iver.andami.PluginServices;


public class SvgFormat extends AbstractActionManager implements Serializable{
	public static final String actionCode = "SVG_format";

	public AbstractHyperLinkPanel createPanel(URI doc)
			throws UnsupportedOperationException {
		return new SvgPanel(doc);
	}

	public String getActionCode() {
		return actionCode;
	}

	public boolean hasPanel() {
		return true;
	}

	public void showDocument(URI doc) {
		throw new UnsupportedOperationException();
	}

	public String getDescription() {
		return PluginServices.getText(this, "Shows_SVG_files_in_gvSIG");
	}

	public String getName() {
		return PluginServices.getText(this, "SVG_format");
	}
}
