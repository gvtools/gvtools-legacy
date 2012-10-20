package org.gvsig.layerLoadingOrder;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.order.DefaultOrderManager;
import com.iver.utiles.XMLEntity;

/**
 * Stores Stores the SmartOrderManager global settings in memory, and keeps
 * these settings synchronized with the plugin persistence.
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
 * 
 */
public class GlobalOrderConfig extends OrderConfig {
	XMLEntity xmlConfig = null;
	protected static final String configName = "smart.order.config";

	public GlobalOrderConfig() {
		super();
		initGlobalConfig();
	}

	public void setVectorBehaviour(int behaviour) {
		super.setVectorBehaviour(behaviour);
		xmlConfig.putProperty("vector-behaviour", behaviour);
	}

	public void setRasterBehaviour(int behaviour) {
		raster_behaviour = behaviour;
		xmlConfig.putProperty("raster-behaviour", behaviour);
	}

	public void setOtherLayersBehaviour(int behaviour) {
		other_behaviour = behaviour;
		xmlConfig.putProperty("other-behaviour", behaviour);
	}

	public void setVectorPosition(int position) {
		super.setVectorPosition(position);
		xmlConfig.putProperty("vector-position", position);
	}

	public void setRasterPosition(int position) {
		super.setRasterPosition(position);
		xmlConfig.putProperty("raster-position", position);
	}

	public void setOtherLayersPosition(int position) {
		super.setOtherLayersPosition(position);
		xmlConfig.putProperty("other-position", position);
	}

	private void initGlobalConfig() {
		PluginServices ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();
		XMLEntity child;
		boolean found = false;
		for (int i = 0; i < xml.getChildrenCount(); i++) {
			child = xml.getChild(i);
			if (child.contains("name")
					&& child.getStringProperty("name").equals(
							DefaultOrderManager.getExtensionPointName())
					&& child.contains("configName")
					&& child.getStringProperty("configName").equals(configName)) {
				setXMLEntity(child);
				found = true;
				xmlConfig = child;
			}
		}
		if (!found) {
			child = getXMLEntity();
			xml.addChild(child);
			xmlConfig = child;
		}
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = super.getXMLEntity();
		xml.putProperty("name", DefaultOrderManager.getExtensionPointName());
		xml.putProperty("configName", configName);
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		if (xml.contains("configName")
				&& xml.getStringProperty("configName").equals(configName)) {
			// ok, right node
			super.setXMLEntity(xml);
		}
	}
}
