/*
 * Created on 06-sep-2005
 */
package org.cresques.remoteMap;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.TreeMap;
import java.util.Vector;

/**
 * @author Luis W. Sevilla (sevilla_lui@gva.es)
 */
public abstract class RemoteMapClient {
	private String cacheDir = "/data/cache/";
	private String host;
	private String version = "1.1.1";
	private TreeMap layers = new TreeMap();
	private Vector layerVector = new Vector();
	// private TreeMap srsTable = new TreeMap();

	int rightMargin = 0, bottomMargin = 0;
	int leftMargin = 0, topMargin = 0;

	/**
	 * Ancho mínimo de la petición al servidor.
	 */
	int minWidth = 0, maxWidth = 1024, minHeight = 0, maxHeight = 1024;
	/**
	 * Numero de tiles que se deben pedir en una sola llamada al servidor.
	 */
	Dimension requestSize = new Dimension(1, 1);

	public static class Layer {
		private TreeMap props = new TreeMap();
		private Rectangle2D boundingBox;
		private Layer parent = null;
		private TreeMap layers = new TreeMap();
		private Vector layerVector = new Vector();

		/**
		 * Crea una capa sin parámetros
		 */
		public Layer() {
			setProperty("name", "");
		}

		/**
		 * Crea una capa con los parámetros básicos.
		 * 
		 * @param name
		 * @param title
		 * @param boundingBox
		 */
		public Layer(String name, String title, Rectangle2D boundingBox) {
			super();
			setProperty("name", name);
			setProperty("title", title);
			setLatLonBoundingBox(boundingBox);
		}

		/**
		 * @return Returns the name.
		 */
		public String getName() {
			return (String) getProperty("name");
		}

		/**
		 * @param name
		 *            The name to set.
		 */
		public void setName(String name) {
			setProperty("name", name);
		}

		/**
		 * @return Returns the title.
		 */
		public String getTitle() {
			return (String) getProperty("title");
		}

		/**
		 * @param title
		 *            The title to set.
		 */
		public void setTitle(String title) {
			setProperty("title", title);
		}

		/**
		 * @return Returns the boundingBox.
		 */
		public Rectangle2D getLatLonBoundingBox() {
			return boundingBox;
		}

		/**
		 * @param boundingBox
		 *            The boundingBox to set.
		 */
		public void setLatLonBoundingBox(Rectangle2D boundingBox) {
			this.boundingBox = boundingBox;
		}

		public void setProperty(String propName, Object value) {
			props.put(propName, value);
		}

		public Object getProperty(String propName) {
			return props.get(propName);
		}

		/**
		 * @return Returns the layerTable.
		 */
		public TreeMap getLayerMap() {
			return layers;
		}

		public Vector getLayerVector() {
			return layerVector;
		}

		public boolean hasLayers() {
			return layers.size() > 0;
		}

		public void addLayer(Layer layer) {
			layerVector.add(layer);
			if (layer.getName().length() > 0)
				layers.put(layer.getName(), layer);
		}

		public Layer getLayer(String layerName) {
			return (Layer) layers.get(layerName);
		}

		/**
		 * @return Returns the parent.
		 */
		public Layer getParent() {
			return parent;
		}

		/**
		 * @param parent
		 *            The parent to set.
		 */
		public void setParent(Layer parent) {
			this.parent = parent;
		}
	}

	/**
	 * 
	 */
	public RemoteMapClient(String host) {
		super();
		this.host = host;
	}

	public abstract File getCapabilities();

	public abstract Image getImage(Dimension sz, Rectangle2D box)
			throws MalformedURLException, IOException;

	/**
	 * @return Returns the cacheDir.
	 */
	public String getCacheDir() {
		return cacheDir;
	}

	/**
	 * @param cacheDir
	 *            The cacheDir to set.
	 */
	public void setCacheDir(String cacheDir) {
		String separator = "/";
		this.cacheDir = cacheDir;
		if (cacheDir.substring(cacheDir.length() - 1).compareTo(separator) != 0)
			this.cacheDir += separator;
		this.cacheDir = this.cacheDir.replace(':', '_');
		File f = new File(cacheDir);
		if (!f.exists())
			f.mkdirs();
	}

	/**
	 * @return Returns the host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            The host to set.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return Returns the layerTable.
	 */
	public TreeMap getLayerMap() {
		return layers;
	}

	public Vector getLayerVector() {
		return layerVector;
	}

	public boolean hasLayers() {
		return layers.size() > 0;
	}

	public void addLayer(Layer layer) {
		layerVector.add(layer);
		if (layer.getName().length() > 0)
			layers.put(layer.getName(), layer);
	}

	public Layer getLayer(String layerName) {
		return (Layer) layers.get(layerName);
	}
}
