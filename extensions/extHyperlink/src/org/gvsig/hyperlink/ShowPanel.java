package org.gvsig.hyperlink;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

/**
 * This class extends JPanel. This class implements a Panel to show the content of the URI
 * that the constructor of the class receives. This panel invokes a new one with the content
 * of the URI. The type of the supported URI should be added like extension point in the
 * initialization of the extension.
 *
 * @author Vicente Caballero Navarro
 * @author Eustaquio Vercher
 *
 */
public class ShowPanel extends JPanel implements IWindow, ComponentListener{
	private static Logger logger = Logger.getLogger(ShowPanel.class.getName());
	private JScrollPane jScrollPane = null;
	private WindowInfo m_ViewInfo = null;
	private AbstractHyperLinkPanel contents = null;
	private static int xpos = 0;
	private static int ypos = 0;
	

	public ShowPanel(AbstractHyperLinkPanel contents) {
		super();
		this.contents = contents;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		getJScrollPane().setViewportView(contents);
	}


	
	/**
	 * Returns a Scroll Pane with the content of the HyperLink
	 * @return jScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			//jScrollPane.setPreferredSize(new java.awt.Dimension(300, 400));
		}
		return jScrollPane;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.andami.ui.mdiManager.IWindow#getWindowInfo()
	 */
    public WindowInfo getWindowInfo() {
    	if (m_ViewInfo==null) {
    		m_ViewInfo = new WindowInfo(WindowInfo.RESIZABLE |
    				WindowInfo.MAXIMIZABLE |
    				WindowInfo.ICONIFIABLE |
    				WindowInfo.PALETTE);
    		if (contents.getURI().toString().startsWith("file:") && contents.getURI().isAbsolute()) {
    			try {
    				File file = new File(contents.getURI().toURL().getFile());
					m_ViewInfo.setTitle(PluginServices.getText(this,"Hyperlink")+" - "+ file.getName());
				} catch (MalformedURLException e) {
					m_ViewInfo.setTitle(PluginServices.getText(this,"Hyperlink")+" - "+ contents.getURI().toString());
				} catch (NullPointerException e) {
					m_ViewInfo.setTitle(PluginServices.getText(this,"Hyperlink")+" - "+ contents.getURI().toString());
				}
    		}
    		else {
    			m_ViewInfo.setTitle(PluginServices.getText(this,"Hyperlink")+" - "+ contents.getURI().toString());
    		}
    		int height = (int)contents.getPreferredSize().getHeight()+15;
    		if (height>650)
    			height = 650;
    		else if (height<450)
    			height = 450;
    		int width = (int)contents.getPreferredSize().getWidth()+20;
    		if (width>800)
    			width = 800;
    		else if (width<450)
    			width = 450;
    		m_ViewInfo.setWidth(width);
    		m_ViewInfo.setHeight(height);
    		m_ViewInfo.setX(xpos);
    		xpos = (xpos + 20)%270;
    		m_ViewInfo.setY(ypos);
    		ypos = (ypos + 15)%150;
    	}
		return m_ViewInfo;
	}
    
    /*
     *  (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {

	}
    
    /*
     *  (non-Javadoc)
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
	public void componentMoved(ComponentEvent e) {

	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {

	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {

	}


	public Object getWindowProfile() {
		return WindowInfo.EDITOR_PROFILE;
	}
}
