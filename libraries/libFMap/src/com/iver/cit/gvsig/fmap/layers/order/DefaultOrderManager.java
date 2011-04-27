package com.iver.cit.gvsig.fmap.layers.order;

import java.util.Map;

import org.apache.log4j.Logger;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * Implements the default gvSIG behaviour when adding a new layer
 * to a collection.
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
 *
 */
public class DefaultOrderManager implements OrderManager {
	protected static final String extensionPointName = "layer.order.manager";
	protected static final String configuredManagerKey = "default.manager";
	protected static final String name = "DefaultOrderManager";
	protected static final String description = "New_layers_are_always_placed_on_top.";
	public static final String CODE = DefaultOrderManager.class.getName();
	private static ExtensionPoint extensionPoint = null;

	public int getPosition(FLayers target, FLayer newLayer) {
		return target.getLayersCount();
	}

	public static String getExtensionPointName() {
		return extensionPointName;
	}

	public static String getDefaultManagerKey() {
		return configuredManagerKey;
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		return null;
	}

	public void setXMLEntity(XMLEntity xml) {
	}

	public String getDescription() {
		return Messages.getString(description);
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

	public String getCode() {
		return CODE;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {}
		return null;
	}

	public Object create() {
		return this;
	}

	public Object create(Object[] args) {
		return this;
	}

	public Object create(Map args) {
		return this;
	}
	
	public static ExtensionPoint getExtensionPoint() {
		if (extensionPoint==null) {
			extensionPoint = 
				(ExtensionPoint) ExtensionPointsSingleton.getInstance().
				get(DefaultOrderManager.getExtensionPointName());
		}
		return extensionPoint;
	}

	/**
	 * Gets the default OrderManager, as configured in the extension point.
	 * 
	 * @return The default OrderManager, as configured in the extension point,
	 * or null if no manager was configured. 
	 */
	public static OrderManager getDefaultOrderManager(){
		try {
			ExtensionPoint ep = getExtensionPoint();
			if (ep!=null) {
				Object obj = ep.create(DefaultOrderManager.getDefaultManagerKey());
				if (obj!=null) {
					return (OrderManager) obj;				
				}
			}
		}
		catch (Exception ex) {
			Logger.getLogger(MapContext.class).warn("Error getting default layer order manager", ex);
		}
		// if something goes wrong, just return the fallback default order manager
		return new DefaultOrderManager();
	}

	/**
	 * Gets the OrderManager registered as <code>managerCode</code> in the
	 * extension point.
	 * 
	 * @return the OrderManager registered as <code>managerCode</code> in the
	 * extension point, or an instance of DefaultOrderManager if not found.
	 */
	public static OrderManager getOrderManager(String managerCode){
		try {
			ExtensionPoint ep = 
				(ExtensionPoint) ExtensionPointsSingleton.getInstance().
				get(DefaultOrderManager.getExtensionPointName());
			if (ep!=null) {
				Object obj = ep.create(managerCode);
				if (obj!=null && obj instanceof OrderManager) {
					return (OrderManager) obj;				
				}
			}
		}
		catch (InstantiationException e) { // it may happen if the OrderManager is not installed in the system, that's OK
		} catch (IllegalAccessException e) {
		}
		// if something goes wrong, just return the fallback default order manager
		return  new DefaultOrderManager();
	}
}
