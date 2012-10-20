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

import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

import es.gva.cit.catalog.schemas.Resource;

/**
 * This class has to be inherited by all the classes that have to load a layer
 * in the current view
 * 
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public abstract class LayerLoader {
	private static TreeMap loadersPool = new TreeMap();

	static {
		LayerLoader.addLoader(Resource.WMS, WMSLayerLoader.class);
		LayerLoader.addLoader(Resource.POSTGIS, PostgisLayerLoader.class);
		LayerLoader.addLoader(Resource.WCS, WCSLayerLoader.class);
		LayerLoader.addLoader(Resource.WEBSITE, LinkLoader.class);
		LayerLoader.addLoader(Resource.DOWNLOAD, LinkLoader.class);
		LayerLoader.addLoader(Resource.WFS, WFSLayerLoader.class);
		LayerLoader.addLoader(Resource.ARCIMS_IMAGE, ARCIMSLayerLoader.class);
		LayerLoader.addLoader(Resource.ARCIMS_VECTORIAL,
				ARCIMSLayerLoader.class);
	}

	public static void addLoader(String key, Class loader) {
		LayerLoader.loadersPool.put(key, loader);
	}

	public static LayerLoader getLoader(Resource resource)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (LayerLoader.loadersPool.containsKey(resource.getType())) {
			Class llClass = (Class) LayerLoader.loadersPool.get(resource
					.getType());
			Class[] args = { Resource.class };
			Object[] params = { resource };
			return (LayerLoader) llClass.getConstructor(args).newInstance(
					params);
		}
		return null;
	}

	private Resource resource = null;

	public LayerLoader(Resource resource) {
		this.resource = resource;
	}

	abstract public void loadLayer() throws LayerLoaderException;

	/**
	 * It returns the error message
	 * 
	 * @return Error Message
	 */
	abstract protected String getErrorMessage();

	/**
	 * It returns the window title for an window error message
	 * 
	 * @return Window title
	 */
	abstract protected String getWindowMessage();

	/**
	 * It adds a new layer to the current view
	 * 
	 * @param lyr
	 *            Layer lo load
	 */
	protected void addLayerToView(FLayer lyr) {
		BaseView theView = (BaseView) PluginServices.getMDIManager()
				.getActiveWindow();

		if (lyr != null) {
			theView.getMapControl().getMapContext().beginAtomicEvent();
			theView.getMapControl().getMapContext().getLayers().addLayer(lyr);
			theView.getMapControl().getMapContext().endAtomicEvent();
			PluginServices.getMainFrame().enableControls();
		}
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
