/*
 * Created on 23-jun-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
/* CVS MESSAGES:
 *
 * $Id: GeoprocessManager.java 24989 2008-11-12 12:28:13Z jcampos $
 * $Log$
 * Revision 1.13  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.12  2006/11/30 18:26:04  azabala
 * bug fixed (this component must be SingletonView)
 *
 * Revision 1.11  2006/11/14 18:46:36  azabala
 * *** empty log message ***
 *
 * Revision 1.10  2006/10/23 10:30:09  caballero
 * solución refresco selección por segunda vez un mismo control
 *
 * Revision 1.9  2006/09/21 18:17:48  azabala
 * fixed bug in JSplitPanel
 *
 * Revision 1.8  2006/08/29 07:56:30  cesar
 * Rename the *View* family of classes to *Window* (ie: SingletonView to SingletonWindow, ViewInfo to WindowInfo, etc)
 *
 * Revision 1.7  2006/08/29 07:13:56  cesar
 * Rename class com.iver.andami.ui.mdiManager.View to com.iver.andami.ui.mdiManager.IWindow
 *
 * Revision 1.6  2006/08/11 16:30:38  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/07/21 09:32:01  azabala
 * fixed bug 644: disabling ok button untill user select a geoprocess
 *
 * Revision 1.4  2006/07/04 16:42:37  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/06/27 16:16:49  azabala
 * imports organization
 *
 * Revision 1.2  2006/06/27 16:15:08  azabala
 * solved bug of packages replication (when added multiple geoprocesses to them)
 *
 * Revision 1.1  2006/06/23 19:03:52  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoprocessPaneContainer;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.manager.GeoprocessTree.GeoprocessTreeDirectory;

/**
 * Main panel of the Geoprocess Manager gui component.
 * 
 * It has a tree where shows all geoprocesses structure, and buttons to open
 * geoprocess' panel, and to close itself.
 * 
 * It also listens for tree selection events.
 * 
 * @author azabala
 * 
 */
public class GeoprocessManager extends JPanel implements SingletonWindow,
		TreeSelectionListener {

	/**
	 * Central panel wich has the geoprocess tree on left and the description
	 * panel on right
	 */
	JSplitPane jSplitPaneCenter;

	/**
	 * Tree wich shows all registered geoprocesses
	 */
	private GeoprocessTree tree;

	/**
	 * It wraps htmlPane
	 */
	private JScrollPane scrollHtml;

	/**
	 * Panel wich shows description of selected packages or geoprocesess
	 */
	private MyJEditorPane htmlPane;

	/**
	 * It has the button "open geoprocess" and "close view"
	 */
	private JPanel jPanelButtons;

	/**
	 * Button to close the andami view
	 */
	private JButton jButtonClose;

	/**
	 * Button to open the selected geoprocess panel
	 */
	private JButton openGeoprocess;

	/**
	 * ViewInfo of andami
	 */
	private WindowInfo viewInfo;

	/**
	 * It has all package descriptions (new plugins must register package
	 * descriptions here)
	 */
	private static TreeMap packageDescriptions = new TreeMap();

	public static void registerPackageDescription(String pkgName,
			String description) {
		packageDescriptions.put(pkgName, description);
	}

	public static String getDescriptionFor(String pkgName) {
		return (String) packageDescriptions.get(pkgName);
	}

	/**
	 * This is the default constructor
	 */
	public GeoprocessManager() {
		super();
		initialize();
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG
					| WindowInfo.RESIZABLE | WindowInfo.PALETTE);
			viewInfo.setTitle(PluginServices.getText(this,
					"Gestor_de_Geoprocesos"));
			viewInfo.setWidth(700);
			viewInfo.setHeight(465);
		}
		return viewInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		add(getJSplitPaneCenter(), BorderLayout.CENTER);
		getJSplitPaneCenter().setLeftComponent(getGeoprocessTree());
		getJSplitPaneCenter().setRightComponent(getHtmlPane());
		add(getJPanelButtons(), BorderLayout.SOUTH);
		this.setSize(700, 525);
		/*
		 * see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4182558
		 * JSplitPane doesnt care setDividerLocation calls before its container
		 * would be visible
		 */
		getJSplitPaneCenter().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent event) {
				getJSplitPaneCenter().setDividerLocation(0.35);
				getJSplitPaneCenter().removeComponentListener(this);
			}
		});

	}

	private JSplitPane getJSplitPaneCenter() {
		if (jSplitPaneCenter == null) {
			jSplitPaneCenter = new JSplitPane();
		}
		return jSplitPaneCenter;
	}

	private GeoprocessTree getGeoprocessTree() {
		if (tree == null) {
			tree = new GeoprocessTree();
			tree.addTreeSelectionListener(this);
			tree.addMouseListener(new GeopTreeMouseListener());
		}
		return tree;
	}

	class GeopTreeMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			JTree tree = (JTree) e.getSource();
			TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
			if (tp == null)
				return;
			int clicks = e.getClickCount();
			if (clicks == 2) {
				Object o = ((DefaultMutableTreeNode) tp.getLastPathComponent())
						.getUserObject();
				if (!(o instanceof IGeoprocessPlugin))
					return;
				IGeoprocessPlugin geoprocess = (IGeoprocessPlugin) o;
				IGeoprocessUserEntries panel = geoprocess.getGeoprocessPanel();
				GeoprocessPaneContainer container = new GeoprocessPaneContainer(
						(JPanel) panel);
				IGeoprocessController controller = geoprocess.getGpController();
				controller.setView(panel);
				container.setCommand(controller);
				container.validate();
				container.repaint();
				PluginServices.getMDIManager().addWindow(container);
			}// if
		}
	}

	private JScrollPane getHtmlPane() {
		if (scrollHtml == null) {
			scrollHtml = new JScrollPane();
			htmlPane = new MyJEditorPane();
			htmlPane.setEditable(false);
			htmlPane.setEditorKit(new HTMLEditorKit());
			scrollHtml.setViewportView(htmlPane);

		}
		return scrollHtml;
	}

	private JPanel getJPanelButtons() {
		if (jPanelButtons == null) {
			jPanelButtons = new JPanel(new BorderLayout());
			JPanel jPanelAux = new JPanel();
			JLabel l = new JLabel();
			l.setPreferredSize(new Dimension(40, 20));
			jPanelButtons.add(new JSeparator(JSeparator.HORIZONTAL),
					BorderLayout.NORTH);
			jPanelAux.add(getJButtonOpenGeop(), BorderLayout.WEST);
			jPanelAux.add(l, BorderLayout.CENTER);
			jPanelAux.add(getJButtonClose(), BorderLayout.EAST);

			jPanelButtons.add(jPanelAux);
		}
		return jPanelButtons;
	}

	public void openGeoprocessPanel() {
		IGeoprocessPlugin geoprocess = tree.getGeoprocess();
		if (geoprocess == null) {
			String error = PluginServices.getText(this, "Error_seleccionar_gp");
			String errorDescription = PluginServices.getText(this,
					"Error_seleccionar_gp_desc");
			JOptionPane.showMessageDialog(this, errorDescription, error,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		IGeoprocessUserEntries panel = geoprocess.getGeoprocessPanel();
		GeoprocessPaneContainer container = new GeoprocessPaneContainer(
				(JPanel) panel);
		IGeoprocessController controller = geoprocess.getGpController();
		controller.setView(panel);
		container.setCommand(controller);
		container.validate();
		container.repaint();
		PluginServices.getMDIManager().addWindow(container);
	}

	private JButton getJButtonOpenGeop() {
		if (openGeoprocess == null) {
			openGeoprocess = new JButton();
			openGeoprocess.setText(PluginServices.getText(this,
					"Abrir_Geoproceso"));
			openGeoprocess
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							openGeoprocessPanel();
						}
					});
			// it will be disabled until user select a geoprocess
			// in the tree
			openGeoprocess.setEnabled(false);
		}
		return openGeoprocess;
	}

	private JButton getJButtonClose() {
		if (jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setText(PluginServices.getText(this, "Cerrar"));
			jButtonClose.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PluginServices.getMDIManager().closeWindow(
							GeoprocessManager.this);

				}
			});
		}
		return jButtonClose;
	}

	/**
	 * processes tree selection events.
	 */
	public void valueChanged(TreeSelectionEvent event) {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) event
				.getPath().getLastPathComponent();
		Object userObject = selectedNode.getUserObject();
		if (userObject instanceof IGeoprocessPlugin) {
			IGeoprocessPlugin metadata = (IGeoprocessPlugin) userObject;
			try {
				htmlPane.setPage(metadata.getHtmlDescription().toString());
			} catch (Exception e) {
				htmlPane.setText("<p>Descripcion no disponible</p>");
			}
			getJButtonOpenGeop().setEnabled(true);
		} else {
			GeoprocessTreeDirectory directory = (GeoprocessTreeDirectory) userObject;
			htmlPane.setText("<p>" + directory.getDescription() + "</p>");
			getJButtonOpenGeop().setEnabled(false);
		}
	}

	private class MyJEditorPane extends JEditorPane {

		public URL getPage() {
			return null;
		}

	}

	public Object getWindowModel() {
		return this.getClass();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
