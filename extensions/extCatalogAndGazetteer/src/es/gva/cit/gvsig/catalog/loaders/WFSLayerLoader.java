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
package es.gva.cit.gvsig.catalog.loaders;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.i18n.Messages;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

import es.gva.cit.catalog.schemas.Resource;

/**
 * This class is used to load a WFS layer in gvSIG
 * 
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class WFSLayerLoader extends LayerLoader {

	public WFSLayerLoader(Resource resource) {
		super(resource);
	}

	/**
	 * This function loads a WFS resource
	 * 
	 * @param host
	 *            URL where the server is located
	 * @param layer
	 *            Layer name
	 * @throws LayerLoaderException
	 */

	public void loadLayer() throws LayerLoaderException {
		FLayer flayer = null;
		String host = getResource().getLinkage();
		String layer = getResource().getName();
		try {
			flayer = createWFSLayer(host, layer);
			addLayerToView(flayer);
		} catch (MalformedURLException e) {
			throw new LayerLoaderException(e.getMessage(), getWindowMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new LayerLoaderException(e.getMessage(), getWindowMessage());
		}
	}

	private FLayer createWFSLayer(String host, String sLayer) throws Exception {
		ExtensionPoint extensionPoint = (ExtensionPoint) ExtensionPointsSingleton
				.getInstance().get("CatalogLayers");
		Map args = new HashMap();
		args.put("host", host);
		String layerName[] = new String[1];
		layerName[0] = sLayer;
		args.put("layer", layerName);
		args.put("user", "");
		args.put("pwd", "");
		BaseView activeView = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();
		args.put("projection", ProjectionUtils.getAbrev(activeView.getCrs()));
		try {
			return (FLayer) extensionPoint.create("OGC:WFS", args);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LayerLoaderException(getErrorMessage(),
					getWindowMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.gva.cit.gvsig.catalogClient.loaders.LayerLoader#getErrorMessage()
	 */
	protected String getErrorMessage() {
		return Messages.getText("wfsError") + ".\n"
				+ Messages.getText("server") + ": "
				+ getResource().getLinkage() + "\n" + Messages.getText("layer")
				+ ": " + getResource().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.gva.cit.gvsig.catalogClient.loaders.LayerLoader#getWindowMessage()
	 */
	protected String getWindowMessage() {
		return Messages.getText("wfsLoad");
	}

}
