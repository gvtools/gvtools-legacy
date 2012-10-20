package org.gvsig.hyperlink.actions;

import java.io.Serializable;
import java.net.URI;

import org.gvsig.hyperlink.AbstractActionManager;
import org.gvsig.hyperlink.AbstractHyperLinkPanel;

import com.iver.andami.PluginServices;

public class ImgFormat extends AbstractActionManager implements Serializable {
	public static final String actionCode = "Image_format";

	public AbstractHyperLinkPanel createPanel(URI doc)
			throws UnsupportedOperationException {
		return new ImgPanel(doc);
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
		return PluginServices.getText(this, "Shows_image_files_in_gvSIG");
	}

	public String getName() {
		return PluginServices.getText(this, "Image_format");
	}
}
