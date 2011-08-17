package org.gvsig.hyperlink.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.gvsig.hyperlink.AbstractActionManager;
import org.gvsig.hyperlink.AbstractHyperLinkPanel;
import org.gvsig.hyperlink.LinkControls;

import com.iver.andami.PluginServices;

/**
 * This class manages the opening of the system file explorer pointing to the path
 * in the hyperlink field. 
 * 
 * @author Pablo Sanxiao <psanxiao@icarto.es>
 */

public class FolderFormat extends AbstractActionManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String actionCode = "Folder_format";
	private static Logger logger = Logger.getLogger(FolderFormat.class.getName());


	public String getActionCode() {
		return actionCode;
	}

	public boolean hasPanel() {
		return false;
	}

	public void showDocument(URI doc) {
		File folder = new File(doc.getPath());
		if (folder.exists()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.open(folder);
			} catch (IOException e1) {
				logger.error(PluginServices.getText(this, "Cant_open_folder"), e1);
				JOptionPane.showMessageDialog(null, PluginServices.getText(this, "Cant_open_folder") + " : " + folder.getAbsolutePath());
			}

		}else {
			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "Bad_path") + " : " + folder.getAbsolutePath());
		}
	}

	public String getDescription() {
		return PluginServices.getText(this, "Shows_Folders_in_gvSIG");
	}

	public String getName() {
		return PluginServices.getText(this, "Folder_formats");
	}

	public AbstractHyperLinkPanel createPanel(URI doc)
			throws UnsupportedOperationException {
		return null;
	}

}

