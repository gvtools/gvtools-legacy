package org.gvsig.hyperlink.layers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class ManagerRegistry {
	public static final String EXTENSIONPOINTNAME = "hyperlink.layer.manager";
	private ExtensionPoint extensionPoint;
	/**
	 * We will cache the proper manager for each class, so that we don't
	 * calculate the right one everytime. This assumes that no manager will be
	 * added after extensions' initialize() method, otherwise the cached values
	 * will be incorrect.
	 */
	private HashMap<Class, String> cachedManagers;
	/**
	 * We will also cache the unmanaged layers (layers without managers).
	 */
	private HashSet<Class> cachedUnmanagedLayers;

	public ManagerRegistry() {
		extensionPoint = new ExtensionPoint(EXTENSIONPOINTNAME,
				"Registers ILinkToolManagers that are able to manage specific layer types.");
		ExtensionPointsSingleton.getInstance().put(extensionPoint);
		cachedManagers = new HashMap<Class, String>();
		cachedUnmanagedLayers = new HashSet<Class>();
	}

	public void put(Class layerType, Class manager) {
		if (layerType.isInterface()) {
			throw new RuntimeException("Interfaces are not supported");
		}
		if (!ILinkLayerManager.class.isAssignableFrom(manager)) {
			throw new RuntimeException(
					"Managers must be of type ILinkLayerManager");
		}
		extensionPoint.put(layerType.getName(), manager);
	}

	public ILinkLayerManager get(FLayer layer) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IncompatibleLayerException {
		if (cachedManagers.containsKey(layer.getClass())) {
			String layerType = cachedManagers.get(layer.getClass());
			ILinkLayerManager manager = (ILinkLayerManager) extensionPoint
					.create(layerType);
			manager.setLayer(layer);
			return manager;
		} else if (cachedUnmanagedLayers.contains(layer.getClass())) {
			return null;
		}
		// search for proper manager for this class
		Iterator<String> iterator = extensionPoint.keySet().iterator();
		TreeSet<Class> classList = new TreeSet<Class>(new ClassComparator());
		while (iterator.hasNext()) {
			String layerType = iterator.next();
			Class layerClass = Class.forName(layerType);
			if (layerClass.isInstance(layer)) {
				classList.add(layerClass);
			}
		}
		if (!classList.isEmpty()) {
			ILinkLayerManager manager = (ILinkLayerManager) extensionPoint
					.create(classList.first().getName());
			cachedManagers.put(layer.getClass(), classList.first().getName());
			manager.setLayer(layer);
			return manager;
		} else {
			cachedUnmanagedLayers.add(layer.getClass());
			return null;
		}
	}

	public boolean hasManager(FLayer layer) {
		if (cachedManagers.containsKey(layer.getClass())) {
			return true;
		} else if (cachedUnmanagedLayers.contains(layer.getClass())) {
			return false;
		}
		Iterator<String> iterator = extensionPoint.keySet().iterator();
		while (iterator.hasNext()) {
			String layerType = iterator.next();
			try {
				Class layerClass = Class.forName(layerType);
				if (layerClass.isInstance(layer)) {
					// there is a manager for this layer class
					return true;
				}
			} catch (ClassNotFoundException ex) {
				PluginServices.getLogger().error(ex);
			}
		}
		cachedUnmanagedLayers.add(layer.getClass());
		return false;
	}

	private class ClassComparator implements Comparator<Class> {

		public int compare(Class class1, Class class2) {
			if (class1.equals(class2))
				return 0;
			if (class1.isAssignableFrom(class2)) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
