package org.gvsig.hyperlink;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JPanel;

import com.iver.andami.PluginServices;

/**
 * This class extends JPanel and implements IExtensioBuilder. Provides the methods that will
 * be reimplemented by the descendant class. Creates a panel that shows the content of a
 * URI. The necessary code that allows to show the content of the URI is provided by the
 * descendant class. Implmenting IExtenssionBuilder this class and its the descendant
 *  provides a point of extension for other extensions.
 */
public abstract class AbstractHyperLinkPanel extends JPanel {
	protected URI document;
	public AbstractHyperLinkPanel(URI doc) {
		super();
		document = doc;
	}

	public URI getURI() {
		return document;
	}
	
	/**
	 * Tries to make an absolute url from a relative one, 
	 * and returns true if the URL is valid.
	 * false otherwise
	 * @return
	 */
	protected boolean checkAndNormalizeURI() {
		if (document==null) {
			PluginServices.getLogger().warn(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"));
			return false;
		}
		else if (!document.isAbsolute()) {
			try {
				// try as a relative path
				File file = new File(document.toString()).getCanonicalFile();
				if (!file.exists()) {
					PluginServices.getLogger().warn(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"));
					return false;
				}
				document = file.toURI();
			} catch (IOException e) {
				PluginServices.getLogger().warn(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"));
				return false;
			}
		}
		return true;
	}
}
