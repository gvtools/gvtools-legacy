/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig.project.documents.view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JSplitPane;

import org.geotools.referencing.CRS;
import org.gvsig.layer.Layer;
import org.gvsig.layer.LayerFilter;
import org.gvsig.map.MapContext;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.map.ColorEvent;
import com.iver.cit.gvsig.map.ExtentEvent;
import com.iver.cit.gvsig.map.MapControl;
import com.iver.cit.gvsig.map.ProjectionEvent;
import com.iver.cit.gvsig.map.ViewPortListener;
import com.iver.cit.gvsig.project.documents.view.ProjectViewBase;
import com.iver.cit.gvsig.project.documents.view.toolListeners.PanListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.ZoomInListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.ZoomOutListener;
import com.iver.cit.gvsig.tools.behavior.Behavior;
import com.iver.cit.gvsig.tools.behavior.MouseMovementBehavior;
import com.iver.cit.gvsig.tools.behavior.PointBehavior;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.console.JConsole;
import com.iver.utiles.console.JDockPanel;
import com.iver.utiles.console.ResponseListener;
import com.iver.utiles.console.jedit.JEditTextArea;

/**
 * <p>
 * <b>Class View</b>. This class represents the gvSIG specific internal window
 * where the maps are displayed and where the events coming from the user are
 * captured.
 * </p>
 * <p>
 * It is composed by three main visual areas:
 * </p>
 * <ol>
 * <li>
 * <b>Map control</b>: the map area located in the right area of the window. It
 * takes up the biggest part of the window.</li>
 * <li>
 * <b>Table of contents (TOC)</b>: is a list of layers displayed in the view.
 * The TOC is located on the left-top corner of the View and is the place where
 * the user can modify the order, the legends, the visibility and other
 * properties of the layers.</li>
 * <li>
 * <b>Map overview</b>: is a small MapControl located in the left-bottom corner
 * of the View where the user can put some layers which summarizes the view. It
 * is used to make the navigation easier and faster.</li>
 * </ol>
 * 
 * @see com.iver.cit.gvsig.fmap.MapControl.java <br>
 *      com.iver.cit.gvsig.gui.toc.TOC.java <br>
 *      com.iver.cit.gvsig.gui.MapOverview.java <br>
 * @author vcn
 */
public class View extends BaseView {
	static private Color defaultViewBackColor = Color.WHITE;

	/** DOCUMENT ME! */

	private JConsole console;
	private JDockPanel dockConsole = null;
	protected ResponseAdapter consoleResponseAdapter = new ResponseAdapter();
	protected boolean isShowConsole = false;
	private ViewPortListener viewPortListener;

	/**
	 * Creates a new View object. Before being used, the object must be
	 * initialized.
	 * 
	 * @see initialize()
	 */
	public View() {
		super();
		this.setName("View");
	}

	/**
	 * Create the internal componentes and populate the window with them. If the
	 * layout properties were set using the
	 * <code>setWindowData(WindowData)</code> method, the window will be
	 * populated according to this properties.
	 */
	public void initialize() {
		super.initialize();
		initComponents();
		hideConsole();
		getConsolePanel().addResponseListener(consoleResponseAdapter);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param model
	 *            DOCUMENT ME!
	 */
	public void setModel(ProjectViewBase model) {
		this.modelo = model;
		// Se registra como listener de cambios en FMap
		MapContext fmap = modelo.getMapContext();

		Layer root = fmap.getRootLayer();
		Layer[] vectorialInEdition = root.filter(LayerFilter.VECTORIAL_EDITING);

		if (vectorialInEdition.length > 0) {
			this.showConsole();
		}

		// Se configura el mapControl
		m_MapControl.setMapContext(fmap);
		m_TOC.setMapContext(fmap);

		m_MapControl.setBackground(new Color(255, 255, 255));
		if (modelo.getMapOverViewContext() != null) {
			m_MapLoc.setModel(modelo.getMapOverViewContext());
		}
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("name")) {
					PluginServices.getMDIManager().getWindowInfo(View.this)
							.setTitle("Vista: " + (String) evt.getNewValue());
				}
			}
		});
		if (m_MapControl.getViewPort() != null) {
			viewPortListener = new ViewPortListener() {
				public void extentChanged(ExtentEvent e) {
					if (PluginServices.getMainFrame() != null) {
						PluginServices
								.getMainFrame()
								.getStatusBar()
								.setControlValue(
										"scale",
										String.valueOf(m_MapControl
												.getViewPort().getScaleView()));
						PluginServices
								.getMainFrame()
								.getStatusBar()
								.setMessage(
										"projection",
										CRS.toSRS(getMapControl().getViewPort()
												.getCrs()));
					}
				}

				public void backColorChanged(ColorEvent e) {
				}

				public void projectionChanged(ProjectionEvent e) {
					m_MapLoc.setCrs(e.getNewCrs());
				}
			};
			m_MapControl.getViewPort().addViewPortListener(viewPortListener);
		}
	}

	public JConsole getConsolePanel() {
		if (console == null) {
			console = new JConsole();
			// Para distinguir cuando se est� escribiendo sobre la consola y
			// cuando no.
			console.setJTextName("CADConsole");
		}
		return console;
	}

	private JDockPanel getDockConsole() {
		if (dockConsole == null) {
			dockConsole = new JDockPanel(getConsolePanel());
		}
		return dockConsole;
	}

	public void addConsoleListener(String prefix, ResponseListener listener) {
		consoleResponseAdapter.putSpaceListener(prefix, listener);

	}

	public void removeConsoleListener(ResponseListener listener) {
		consoleResponseAdapter.deleteListener(listener);

	}

	public void focusConsole(String text) {
		getConsolePanel().addResponseText(text);
		// TODO: HACE ESTO CON EL KEYBOARDFOCUSMANAGER
		// KeyboardFocusManager kfm =
		// KeyboardFocusManager.getCurrentKeyboardFocusManager();
		// kfm.focusNextComponent(getConsolePanel());
		System.err.println("Asigno el foco a la consola");

		JEditTextArea jeta = getConsolePanel().getTxt();
		jeta.requestFocusInWindow();
		jeta.setCaretPosition(jeta.getText().length());

		// FocusManager fm=FocusManager.getCurrentManager();
		// fm.focusNextComponent(getConsolePanel());

	}

	public void hideConsole() {
		isShowConsole = false;
		/*
		 * removeAll(); //JSplitPane split = new
		 * JSplitPane(JSplitPane.VERTICAL_SPLIT); JSplitPane tempMainSplit = new
		 * JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		 * tempMainSplit.setPreferredSize(new Dimension(500, 300)); if
		 * (!isPalette()){ JSplitPane tempSplitToc = new
		 * JSplitPane(JSplitPane.VERTICAL_SPLIT);
		 * tempSplitToc.setTopComponent((TOC) m_TOC);
		 * tempSplitToc.setBottomComponent(m_MapLoc);
		 * tempSplitToc.setResizeWeight(0.7);
		 * tempMainSplit.setLeftComponent(tempSplitToc); }else{
		 * tempMainSplit.setLeftComponent(m_TOC); } m_TOC.setVisible(true);
		 * tempMainSplit.setRightComponent(m_MapControl);
		 * //split.setBottomComponent(getConsolePanel());
		 * //split.setTopComponent(tempMainSplit); //
		 * split.setResizeWeight(0.9); this.setLayout(new BorderLayout());
		 * this.add(tempMainSplit, BorderLayout.CENTER);
		 */
		getDockConsole().setVisible(false);

	}

	public void showConsole() {
		if (isShowConsole)
			return;
		isShowConsole = true;
		/*
		 * removeAll(); JSplitPane split = new
		 * JSplitPane(JSplitPane.VERTICAL_SPLIT); JSplitPane tempMainSplit = new
		 * JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		 * tempMainSplit.setPreferredSize(new Dimension(500, 300)); if
		 * (!isPalette()){ JSplitPane tempSplitToc = new
		 * JSplitPane(JSplitPane.VERTICAL_SPLIT);
		 * tempSplitToc.setTopComponent((TOC) m_TOC);
		 * tempSplitToc.setBottomComponent(m_MapLoc);
		 * tempSplitToc.setResizeWeight(0.7);
		 * tempMainSplit.setLeftComponent(tempSplitToc); }else{
		 * tempMainSplit.setLeftComponent(m_TOC); } m_TOC.setVisible(true);
		 * tempMainSplit.setRightComponent(m_MapControl);
		 * split.setBottomComponent(getConsolePanel());
		 * split.setTopComponent(tempMainSplit); split.setResizeWeight(0.9);
		 * this.setLayout(new BorderLayout()); this.add(split,
		 * BorderLayout.CENTER);
		 */
		getMapControl().remove(getDockConsole());
		// getConsolePanel().setPreferredSize(new Dimension(200, 150));
		getMapControl().setLayout(new BorderLayout());
		getMapControl().add(getDockConsole(), BorderLayout.SOUTH);
		getDockConsole().setVisible(true);

	}

	private class ResponseAdapter implements ResponseListener {

		private HashMap spaceListener = new HashMap();

		public void putSpaceListener(String namespace, ResponseListener listener) {
			spaceListener.put(namespace, listener);
		}

		/**
		 * @see com.iver.utiles.console.ResponseListener#acceptResponse(java.lang.String)
		 */
		public void acceptResponse(String response) {
			boolean nameSpace = false;
			int n = -1;
			if (response != null) {
				if ((n = response.indexOf(':')) != -1) {
					nameSpace = true;
				}
			}

			if (nameSpace) {
				ResponseListener listener = (ResponseListener) spaceListener
						.get(response.substring(0, n));
				if (listener != null)
					listener.acceptResponse(response.substring(n + 1));
			} else {
				Iterator i = spaceListener.values().iterator();
				while (i.hasNext()) {
					ResponseListener listener = (ResponseListener) i.next();
					listener.acceptResponse(response);
				}
			}
		}

		/**
		 * @param listener
		 */
		public void deleteListener(ResponseListener listener) {
			Iterator i = spaceListener.keySet().iterator();
			while (i.hasNext()) {
				String namespace = (String) i.next();
				ResponseListener l = (ResponseListener) spaceListener
						.get(namespace);
				if (l == listener) {
					spaceListener.remove(namespace);
				}
			}
		}

	}

	/**
	 * DOCUMENT ME!
	 */
	protected void initComponents() { // GEN-BEGIN:initComponents
		m_MapControl = new MapControl(); // Default is paintEnabled = false.
											// Remember to activate it

		m_MapControl.addExceptionListener(mapControlExceptionListener);
		m_TOC = new TOC();

		// Ponemos el localizador
		m_MapLoc = new MapOverview(m_MapControl);
		removeAll();
		tempMainSplit = new ViewSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		if (windowData == null) {
			m_MapLoc.setPreferredSize(new Dimension(150, 200));
			tempMainSplit.setPreferredSize(new Dimension(500, 300));
		}

		if (!isPalette()) {
			tempSplitToc = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			tempSplitToc.setTopComponent(m_TOC);
			tempSplitToc.setBottomComponent(m_MapLoc);
			tempSplitToc.setResizeWeight(0.7);
			tempMainSplit.setLeftComponent(tempSplitToc);
		} else {
			tempMainSplit.setLeftComponent(m_TOC);
		}
		m_TOC.setVisible(true);
		tempMainSplit.setRightComponent(m_MapControl);
		// split.setBottomComponent(getConsolePanel());
		// split.setTopComponent(tempMainSplit);
		// split.setResizeWeight(0.9);
		this.setLayout(new BorderLayout());
		this.add(tempMainSplit, BorderLayout.CENTER);

		if (windowData != null) {
			try {
				tempMainSplit.setDividerLocation(Integer.valueOf(
						windowData.get("MainDivider.Location")).intValue());
				if (windowData.get("TOCDivider.Location") != null) {
					tempSplitToc.setDividerLocation(Integer.valueOf(
							windowData.get("TOCDivider.Location")).intValue());
				}
			} catch (NumberFormatException ex) {
				PluginServices.getLogger().error(
						"Error restoring View properties");
			}
		}

		ZoomOutListener zol = new ZoomOutListener(m_MapControl);
		m_MapControl.addMapTool("zoomOut", new Behavior[] {
				new PointBehavior(zol), new MouseMovementBehavior(sbl) });

		// pan

		PanListener pl = new PanListener(m_MapControl);
		m_MapControl.addMapTool("pan", new Behavior[] { new MoveBehavior(pl),
				new MouseMovementBehavior(sbl) });

		// Zoom por rect�ngulo
		ZoomInListener zil = new ZoomInListener(m_MapControl);
		m_MapControl.addMapTool("zoomIn", new Behavior[] {
				new RectangleBehavior(zil), new PointBehavior(zoil),
				new MouseMovementBehavior(sbl) });

		m_MapControl.setTool("zoomIn"); // Por defecto
	}

	/**
	 * DOCUMENT ME!
	 */
	/*
	 * public void openPropertiesWindow() { }
	 */
	/**
	 * DOCUMENT ME!
	 */
	/*
	 * public void openQueryWindow() { }
	 */

	/**
	 * @see com.iver.mdiApp.ui.MDIManager.IWindow#windowActivated()
	 */
	public void windowActivated() {
		PluginServices
				.getMainFrame()
				.getStatusBar()
				.setMessage(
						"units",
						PluginServices.getText(this, getMapControl()
								.getViewPort().getDistanceUnits().name));
		PluginServices
				.getMainFrame()
				.getStatusBar()
				.setControlValue(
						"scale",
						String.valueOf(m_MapControl.getViewPort()
								.getScaleView()));
		PluginServices
				.getMainFrame()
				.getStatusBar()
				.setMessage("projection",
						CRS.toSRS(getMapControl().getViewPort().getCrs()));
	}

	/**
	 * @see com.iver.andami.ui.mdiManager.IWindowListener#windowClosed()
	 */
	public void windowClosed() {
		super.windowClosed();
		if (viewPortListener != null)
			getMapControl().getViewPort().removeViewPortListener(
					viewPortListener);
		if (getMapOverview() != null)
			getMapOverview().getViewPort().removeViewPortListener(
					getMapOverview());

	}

	public void toPalette() {
		isPalette = true;
		m_MapLoc.setPreferredSize(new Dimension(200, 150));
		m_MapLoc.setSize(new Dimension(200, 150));
		movp = new MapOverViewPalette(m_MapLoc, this);
		PluginServices.getMDIManager().addWindow(movp);
		FLayer[] layers = getModel().getMapContext().getLayers().getActives();
		if (layers.length > 0 && layers[0] instanceof FLyrVect) {
			if (((FLyrVect) layers[0]).isEditing()) {
				showConsole();
				return;
			}
		}
		hideConsole();

	}

	public void restore() {
		isPalette = false;
		PluginServices.getMDIManager().closeWindow(movp);
		FLayer[] layers = getModel().getMapContext().getLayers().getActives();
		if (layers.length > 0 && layers[0] instanceof FLyrVect) {
			if (((FLyrVect) layers[0]).isEditing()) {
				showConsole();
				return;
			}
		}
		hideConsole();
		JSplitPane tempSplitToc = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		tempSplitToc.setTopComponent(m_TOC);
		tempSplitToc.setBottomComponent(m_MapLoc);
		tempSplitToc.setResizeWeight(0.7);
		tempMainSplit.setLeftComponent(tempSplitToc);
	}

	/**
	 * Sets the default map overview background color that will be used in
	 * subsequent projects.
	 * 
	 * @param color
	 */
	public static void setDefaultMapOverViewBackColor(Color color) {
		throw new Error(
				"support for map overview back color not yet implemented");
	}

	/**
	 * Returns the current default map overview background color defined which
	 * is the color defined when the user does not define any other one
	 * 
	 * @return java.awt.Color
	 */
	public static Color getDefaultMapOverViewBackColor() {
		throw new Error(
				"support for map overview back color not yet implemented");
	}

	/**
	 * Returns the current default view background color defined which is the
	 * color defined when the user does not define any other one
	 * 
	 * @return java.awt.Color
	 */
	public static Color getDefaultBackColor() {
		// TODO es millorable?
		XMLEntity xml = PluginServices.getPluginServices("com.iver.cit.gvsig")
				.getPersistentXML();
		if (xml.contains("DefaultViewBackColor"))
			defaultViewBackColor = StringUtilities.string2Color(xml
					.getStringProperty("DefaultViewBackColor"));
		return defaultViewBackColor;
	}

	/**
	 * Sets the default view background color that will be used in subsequent
	 * projects.
	 * 
	 * @param color
	 */
	public static void setDefaultBackColor(Color color) {
		defaultViewBackColor = color;
	}

	public Object getWindowProfile() {
		return WindowInfo.EDITOR_PROFILE;
	}

}
