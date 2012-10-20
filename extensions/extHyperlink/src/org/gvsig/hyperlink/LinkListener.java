/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 *   Av. Blasco Ibï¿½ï¿½ez, 50
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
package org.gvsig.hyperlink;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.net.URI;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.gvsig.hyperlink.config.LayerLinkConfig;
import org.gvsig.hyperlink.config.LinkConfig;
import org.gvsig.hyperlink.layers.ILinkLayerManager;
import org.gvsig.hyperlink.layers.IncompatibleLayerException;
import org.gvsig.hyperlink.layers.ManagerRegistry;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.toolListeners.InfoListener;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * Listener that implements PointListener to show the information of the
 * HyperLink of the layer, if the actual layer allows it. When the user selects
 * the tool and make one clic with the mouse throws an event to create Panels
 * with the information of the associated HyperLink to the layer. Creates one
 * panel for URI provided, to load the information.
 * 
 * @author Vicente Caballero Navarro
 * @author Cesar Martinez Izqueirdo
 * 
 */
public class LinkListener implements PointListener {
	private static Logger logger = Logger.getLogger(InfoListener.class
			.getName());
	private Cursor cur = null;
	private MapControl mapCtrl;
	private ManagerRegistry registry;
	public static final int TYPELINKIMAGE = 0;
	public static final int TYPELINKTEXT = 1;
	public ExtensionPoint formatManagers;

	/**
	 * Creates a new LinkListener
	 * 
	 * @param mc
	 *            mapControl
	 */
	public LinkListener(MapControl mc, ManagerRegistry registry) {
		this.mapCtrl = mc;
		this.registry = registry;
		formatManagers = (ExtensionPoint) ExtensionPointsSingleton
				.getInstance().get(LinkControls.ACTIONSEXTENSIONPOINT);
		initialize();
	}

	private void initialize() {
		URL url = PluginServices.getPluginServices(this).getClassLoader()
				.getResource("images/LinkCursor.gif");
		if (url != null) {
			ImageIcon img = new ImageIcon(url);
			cur = Toolkit.getDefaultToolkit().createCustomCursor(
					img.getImage(), new Point(16, 16), "Hyperlink");
		} else {
			PluginServices.getLogger().error(
					"Icon not found: images/LinkCursor.gif");
		}
	}

	/**
	 * Creates one LinkPanel for URI provided. Gets the active layers and
	 * invokes getLink with the catched point and the allowed tolerance if these
	 * layers allows HyperLinks. getLink provides an array of URIs, this method
	 * is invoked for all active layers.
	 * 
	 * @param event
	 *            PointEvent
	 * @throws BehaviorException
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void point(PointEvent event) throws BehaviorException {
		Point2D pReal = mapCtrl.getMapContext().getViewPort()
				.toMapPoint(event.getPoint());
		FLayer[] sel = mapCtrl.getMapContext().getLayers().getVisibles();

		URI[] links = null;
		LinkConfig linkConfig;
		ILinkLayerManager layerManager;
		ILinkActionManager actionManager;

		for (int layerCount = 0; layerCount < sel.length; layerCount++) {
			FLayer theLayer = sel[layerCount];
			if (!registry.hasManager(theLayer)) {
				continue;
			}
			try {
				layerManager = registry.get(theLayer);
				if (theLayer.getProperty(LinkControls.LAYERPROPERTYNAME) != null
						&& theLayer.getProperty(LinkControls.LAYERPROPERTYNAME) instanceof LayerLinkConfig) {
					LayerLinkConfig layerLink = (LayerLinkConfig) theLayer
							.getProperty(LinkControls.LAYERPROPERTYNAME);
					if (layerLink.isEnabled() && layerLink.linkCount() > 0) { // there
																				// is
																				// some
																				// link
																				// configured
																				// for
																				// this
																				// layer

						double tol = mapCtrl.getViewPort().toMapDistance(3);
						for (int actionCount = 0; actionCount < layerLink
								.linkCount(); actionCount++) {
							linkConfig = layerLink.getLink(actionCount);
							links = layerManager.getLink(pReal, tol,
									linkConfig.getFieldName(),
									linkConfig.getExtension());
							for (int i = 0; i < links.length; i++) {
								if (links[i] != null) {
									actionManager = (ILinkActionManager) formatManagers
											.get(linkConfig.getActionCode());
									if (actionManager == null) {
										logger.warn("Hyperlink error -- invalid action code: "
												+ linkConfig.getActionCode());
										continue;
									}
									if (actionManager.hasPanel()) {
										ShowPanel lpanel = new ShowPanel(
												actionManager
														.createPanel(links[i]));
										// show the panel
										PluginServices.getMDIManager()
												.addWindow(lpanel);
									} else { // delegate in the format manager
												// to show the file
										actionManager.showDocument(links[i]);
									}
								}
							}
						}
					}
				}
			} catch (ClassNotFoundException e) {
				throw new BehaviorException(
						"Hyperlink: There is no manager for this layer type", e);
			} catch (InstantiationException e) {
				logger.error("Hyperlink error: " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.error("Hyperlink error: " + e.getMessage(), e);
			} catch (IncompatibleLayerException e) {
				throw new BehaviorException(
						"Hyperlink: There is no manager for this layer type", e);
			}
		}
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return cur;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return false;
	}

	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		// TODO Auto-generated method stub

	}
}
