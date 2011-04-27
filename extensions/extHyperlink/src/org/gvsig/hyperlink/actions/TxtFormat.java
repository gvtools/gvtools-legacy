package org.gvsig.hyperlink.actions;

import java.io.Serializable;
import java.net.URI;

import org.gvsig.hyperlink.AbstractActionManager;
import org.gvsig.hyperlink.AbstractHyperLinkPanel;

import com.iver.andami.PluginServices;


public class TxtFormat extends AbstractActionManager implements Serializable{
	public static final String actionCode = "Txt_format";

	public AbstractHyperLinkPanel createPanel(URI doc)
			throws UnsupportedOperationException {
		return new TxtPanel(doc);
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
		return PluginServices.getText(this, "Shows_HTML_or_text_files_in_gvSIG");
	}

	public String getName() {
		return PluginServices.getText(this, "HTML_and_text_formats");
	}

}
