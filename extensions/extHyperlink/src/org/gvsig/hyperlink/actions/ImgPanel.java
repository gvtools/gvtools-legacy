package org.gvsig.hyperlink.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.gvsig.hyperlink.AbstractHyperLinkPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.sun.jimi.core.Jimi;

/**
 * This class extends AbstractHyperLink, and provides suppot to open images of many formats.
 * The common supported formats are JPG, ICO, BMP, TIFF, GIF and PNG. Implements methods from
 * IExtensionBuilder to make it extending.   
 * 
 * @author Eustaquio Vercher  (IVER)
 * @author Cesar Martinez Izquierdo (IVER)
 */
public class ImgPanel extends AbstractHyperLinkPanel{
	private static final long serialVersionUID = -5200841105188251551L;

	/**
	 * Default constructor.
	 */
	public ImgPanel(URI doc){
		super(doc);
		initialize();
	}
	/**
	 * Initializes this panel.
	 */
	void initialize(){
		this.setLayout(new BorderLayout());
		showDocument();
		//this.setSize(600, 400);
	}

	/**
	 * Implements the necessary code to open images in this panel.
	 */
	protected void showDocument() {
		if (!checkAndNormalizeURI()) {
			return;
		}
		ImageIcon image = null;
		String iString=document.toString();
		iString=iString.toLowerCase();

		if (iString.endsWith("jpg") || iString.endsWith("jpeg") ||
                iString.endsWith("gif") || iString.endsWith("jp2")) {
			// note: it seems jimi si not able to load .jp2 (jpeg2000) 
            try {
				image = new ImageIcon(Jimi.getImage(document.toURL()));
			} catch (MalformedURLException e) {
				NotificationManager.addWarning(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"), e);
			}
        } else
        	if (iString.endsWith("png") ||
        			iString.endsWith("tiff") ||	iString.endsWith("tif") ||
        			iString.endsWith("jpg") ||
                iString.endsWith("ico") || iString.endsWith("xpm") ||
                iString.endsWith("bmp")) {
        		//note: it seems jimi si not able to load .tiff images
        		try {
        			image = new ImageIcon(Jimi.getImage(document.toURL()));
        		} catch (MalformedURLException e) {
        			NotificationManager.addWarning(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"), e);
        		}
        	}else {
        		try {
        			image=new ImageIcon(document.toURL());
        		} catch (MalformedURLException e) {
        			NotificationManager.addWarning(PluginServices.getText(this, "Hyperlink_linked_field_doesnot_exist"), e);
        		}
        	}
		if (image==null); //Incluir error
		this.setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
		this.setSize(new Dimension(image.getIconWidth(),
				image.getIconHeight()));
		JLabel label = new JLabel(image);
		this.add(label);
	}

}
