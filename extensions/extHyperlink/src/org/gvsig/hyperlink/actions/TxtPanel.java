package org.gvsig.hyperlink.actions;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;

import org.gvsig.hyperlink.AbstractHyperLinkPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.utiles.BrowserControl;


/**
 * This class extends AbstractHyperLinkPanel. And provides support to open txt files and
 * WWW. Implements methods from IExtensionBuilder to make it extending.
 *
 */
public class TxtPanel extends AbstractHyperLinkPanel{
	private static final long serialVersionUID = 1408583183372898110L;
	private JTextPane textPane;

	/**
	 * Default constructor.
	 */
	public TxtPanel(URI doc){
		super(doc);
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	void initialize(){
		this.setLayout(new BorderLayout());
		showDocument();
	}


	/**
	 * Implements the necessary code to show the content of the URI in this panel. The
	 * content of the URI is a TXT or a WWW.
	 * @param URI
	 */
	protected void showDocument() {
		textPane = new JTextPane();
		textPane.setEditable(false);

		if (!checkAndNormalizeURI()) {
			return;
		}

		URL url=null;
		try {
			url=document.normalize().toURL();
		} catch (MalformedURLException e1) {
			NotificationManager.addWarning(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"), e1);
			return;
		}
		try {
			textPane.setPage(url);
			textPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
				public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
					if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						System.out.println("hyperlinkUpdate()");
						BrowserControl.displayURL(e.getURL().toString());
					}
				}
			});
		} catch (IOException e) {
			NotificationManager.addWarning(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"), e);
			return;
		}
		this.add(textPane, BorderLayout.CENTER);
	}
}
