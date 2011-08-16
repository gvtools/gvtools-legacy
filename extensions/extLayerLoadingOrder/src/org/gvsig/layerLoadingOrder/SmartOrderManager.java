package org.gvsig.layerLoadingOrder;

import java.util.Map;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.IExtension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.order.OrderManager;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.extensionPoints.IExtensionBuilder;

public class SmartOrderManager implements OrderManager, IExtensionBuilder {
	protected static final String name = "SmartOrderManager";
	protected static final String description = "Allows_to_set_different_behaviours_for_raster_and_vector_layers";

	/**
	 * Stores a reference to the global config (the plugin level config), to
	 * be able to quickly compute the positions.
	 */
	protected OrderConfig globalConfig = null;

	/**
	 * Local config. If present, it will override the global config settings.
	 */
	protected OrderConfig config = null;

	public SmartOrderManager() {
		IExtension ext = PluginServices.getExtension(SmartOrderExtension.class);
		if (ext instanceof SmartOrderExtension) {
			SmartOrderExtension extension =(SmartOrderExtension) ext;
			globalConfig = (OrderConfig) extension.getConfig();
		};
	}
	
	public int getPosition(FLayers target, FLayer newLayer) {
		OrderConfig auxConfig;
		if (config!=null) {
			auxConfig = config;
		}
		else if (globalConfig!=null) {
			// there is no local config, use globalConfig
			auxConfig = globalConfig;
		}
		else { // there is no config at all, return default behaviour
			return target.getLayersCount();
		}
		if (newLayer instanceof FLyrVect) {
			if (auxConfig.getVectorBehaviour()==OrderConfig.ON_TOP) {
				return target.getLayersCount();
			}
			else if (auxConfig.getVectorBehaviour()==OrderConfig.AT_THE_BOTTOM) {
				return 0;
			}
			else if (auxConfig.getVectorBehaviour()==OrderConfig.OVER_RASTER) {
				int maxRaster = 0;
				for (int i=0; i<target.getLayersCount(); i++) {
					if (target.getLayer(i) instanceof FLyrRasterSE) {
						maxRaster = i;
					}
				}
				return maxRaster+1;
			}
			else if (auxConfig.getVectorBehaviour()==OrderConfig.UNDER_RASTER) {
				for (int i=0; i<target.getLayersCount(); i++) {
					if (target.getLayer(i) instanceof FLyrRasterSE) {
						return i;
					}
				}
				return target.getLayersCount();
			}
			else if (auxConfig.getVectorBehaviour()==OrderConfig.FROM_TOP) {
				int pos = target.getLayersCount()-auxConfig.getVectorPosition();
				if (pos<0) return 0;
				return pos;
			}
			else if (auxConfig.getVectorBehaviour()==OrderConfig.FROM_BOTTOM) {
				if (auxConfig.getVectorPosition()>target.getLayersCount()) {
					return target.getLayersCount();
				}
				else {
					return auxConfig.getVectorPosition();
				}
			}
		}
		else if (newLayer instanceof FLyrRasterSE) {
			if (auxConfig.getRasterBehaviour()==OrderConfig.ON_TOP) {
				return target.getLayersCount();
			}
			else if (auxConfig.getRasterBehaviour()==OrderConfig.AT_THE_BOTTOM) {
				return 0;
			}
			else if (auxConfig.getRasterBehaviour()==OrderConfig.OVER_VECTOR) {
				int maxVector = 0;
				for (int i=0; i<target.getLayersCount(); i++) {
					if (target.getLayer(i) instanceof FLyrVect) {
						maxVector++;
					}
				}
				return maxVector;
			}
			else if (auxConfig.getRasterBehaviour()==OrderConfig.UNDER_VECTOR) {
				for (int i=0; i<target.getLayersCount(); i++) {
					if (target.getLayer(i) instanceof FLyrVect) {
						return i;
					}
				}
				return target.getLayersCount();
			}
			else if (auxConfig.getRasterBehaviour()==OrderConfig.FROM_TOP) {
				int pos = target.getLayersCount()-auxConfig.getRasterPosition();
				if (pos<0) return 0;
				return pos;
			}
			else if (auxConfig.getRasterBehaviour()==OrderConfig.FROM_BOTTOM) {
				if (auxConfig.getRasterPosition()>target.getLayersCount()) {
					return target.getLayersCount();
				}
				else {
					return auxConfig.getRasterPosition();
				}
			}
		}
		else {
			if (auxConfig.getOtherLayersBehaviour()==OrderConfig.ON_TOP) {
				return target.getLayersCount();
			}
			else if (auxConfig.getOtherLayersBehaviour()==OrderConfig.AT_THE_BOTTOM) {
				return 0;
			}
			else if (auxConfig.getOtherLayersBehaviour()==OrderConfig.FROM_TOP) {
				int pos = target.getLayersCount()-auxConfig.getOtherLayersPosition();
				if (pos<0) return 0;
				return pos;
			}
			else if (auxConfig.getOtherLayersBehaviour()==OrderConfig.FROM_BOTTOM) {
				if (auxConfig.getOtherLayersPosition()>target.getLayersCount()) {
					return target.getLayersCount();
				}
				else {
					return auxConfig.getOtherLayersPosition();
				}
			}
		}
		return target.getLayersCount();
	}

	public Object create() {
		return new SmartOrderManager();
	}

	public Object create(Object[] args) {
		return create();
	}

	public Object create(Map args) {
		return create();
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		if (config!=null) {
			XMLEntity xml = globalConfig.getXMLEntity();
			xml.putProperty("code", getCode());
			return xml;
		}
		return null;
	}

	public void setXMLEntity(XMLEntity xml) {
		// check the right manager ID before processing it 
		if (xml!=null
				&& xml.contains("code")
				&& xml.getStringProperty("code").equals(getCode())) {
			//ok, process the XML
			config = new OrderConfig();
			config.setXMLEntity(xml);
		}
	}

	public String getDescription() {
		return PluginServices.getText(this, description);
	}

	public String getName() {
		return PluginServices.getText(this, name);
	}

	public String toString() {
		return getName();
	}

	public String getCode() {
		return getClassName();
	}

	public void setConfig(OrderConfig config) {
		this.config = config;
	}

	public OrderConfig getConfig() {
		return this.config;
	}
	
	public Object clone() {
		OrderManager om;
		try {
			om = (OrderManager) super.clone();
			om.setXMLEntity(this.getXMLEntity());
			return om;
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
}
