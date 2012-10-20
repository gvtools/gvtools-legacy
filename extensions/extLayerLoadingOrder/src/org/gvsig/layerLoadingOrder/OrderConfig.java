package org.gvsig.layerLoadingOrder;

import com.iver.utiles.IPersistence;
import com.iver.utiles.XMLEntity;

/**
 * Stores the SmartOrderManager settings in memory, and offers a simple
 * interface to change them.
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
 * 
 */
public class OrderConfig implements IPersistence {
	/**
	 * To place the layer over the existing layers.
	 */
	public static final int ON_TOP = 0;

	/**
	 * To place the layer under the existing layers
	 */
	public static final int AT_THE_BOTTOM = 1;

	/**
	 * To place the layer on a fixed position counting from top.
	 */
	public static final int FROM_TOP = 2;

	/**
	 * To place the layer on a fixed position counting from the bottom.
	 */
	public static final int FROM_BOTTOM = 3;

	/**
	 * To place the layer over existing raster layers
	 */
	public static final int OVER_RASTER = 4;

	/**
	 * To place the layer over existing vector layers
	 */
	public static final int OVER_VECTOR = 4;

	/**
	 * To place the layer under existing raster layers
	 */
	public static final int UNDER_RASTER = 5;

	/**
	 * To place the layer under existing raster layers
	 */
	public static final int UNDER_VECTOR = 5;

	protected int raster_behaviour = ON_TOP;
	protected int vector_behaviour = ON_TOP;
	protected int other_behaviour = ON_TOP;

	protected int raster_position = 0;
	protected int vector_position = 0;
	protected int other_position = 0;

	/**
	 * <p>
	 * Sets the behaviour of the order manager when adding Vector Layers.
	 * </p>
	 * <p>
	 * Allowed values: {@link OrderConfig#ON_TOP},
	 * {@link OrderConfig#AT_THE_BOTTOM}, {@link OrderConfig#FROM_TOP},
	 * {@link OrderConfig#FROM_BOTTOM}, {@link OrderConfig#OVER_RASTER},
	 * {@link OrderConfig#UNDER_RASTER}
	 * </p>
	 * 
	 * @param behaviour
	 */
	public void setVectorBehaviour(int behaviour) {
		vector_behaviour = behaviour;
	}

	/**
	 * <p>
	 * Returns the behaviour of the order manager when adding Vector Layers.
	 * </p>
	 * <p>
	 * Allowed values: {@link OrderConfig#ON_TOP},
	 * {@link OrderConfig#AT_THE_BOTTOM}, {@link OrderConfig#FROM_TOP},
	 * {@link OrderConfig#FROM_BOTTOM}, {@link OrderConfig#OVER_RASTER},
	 * {@link OrderConfig#UNDER_RASTER}
	 * </p>
	 */
	public int getVectorBehaviour() {
		return vector_behaviour;
	}

	/**
	 * <p>
	 * Returns the behaviour of the order manager when adding Raster Layers.
	 * </p>
	 * <p>
	 * Allowed values: {@link OrderConfig#ON_TOP},
	 * {@link OrderConfig#AT_THE_BOTTOM}, {@link OrderConfig#FROM_TOP},
	 * {@link OrderConfig#FROM_BOTTOM}, {@link OrderConfig#OVER_RASTER},
	 * {@link OrderConfig#UNDER_RASTER}
	 * </p>
	 * 
	 * @param behaviour
	 */
	public void setRasterBehaviour(int behaviour) {
		raster_behaviour = behaviour;
	}

	/**
	 * <p>
	 * Returns the behaviour of the order manager when adding Raster Layers.
	 * </p>
	 * <p>
	 * Allowed values: {@link OrderConfig#ON_TOP},
	 * {@link OrderConfig#AT_THE_BOTTOM}, {@link OrderConfig#FROM_TOP},
	 * {@link OrderConfig#FROM_BOTTOM}, {@link OrderConfig#OVER_RASTER},
	 * {@link OrderConfig#UNDER_RASTER}
	 * </p>
	 */
	public int getRasterBehaviour() {
		return raster_behaviour;
	}

	/**
	 * <p>
	 * Sets the behaviour of the order manager when adding other type of Layers
	 * (not raster, not vector).
	 * </p>
	 * <p>
	 * Allowed values: {@link OrderConfig#ON_TOP},
	 * {@link OrderConfig#AT_THE_BOTTOM}, {@link OrderConfig#FROM_TOP},
	 * {@link OrderConfig#FROM_BOTTOM}, {@link OrderConfig#OVER_RASTER},
	 * {@link OrderConfig#UNDER_RASTER}
	 * </p>
	 * 
	 * @param behaviour
	 */
	public void setOtherLayersBehaviour(int behaviour) {
		other_behaviour = behaviour;
	}

	/**
	 * <p>
	 * Returns the behaviour of the order manager when adding other type of
	 * Layers (not raster, not vector).
	 * </p>
	 * <p>
	 * Allowed values: {@link OrderConfig#ON_TOP},
	 * {@link OrderConfig#AT_THE_BOTTOM}, {@link OrderConfig#FROM_TOP},
	 * {@link OrderConfig#FROM_BOTTOM}, {@link OrderConfig#OVER_RASTER},
	 * {@link OrderConfig#UNDER_RASTER}
	 * </p>
	 */
	public int getOtherLayersBehaviour() {
		return other_behaviour;
	}

	/**
	 * Sets a fixed position in which vector layers will be added. This method
	 * only makes sense when the behaviour is {@link OrderConfig#FROM_TOP} or
	 * {@link OrderConfig#FROM_BOTTOM} .
	 * 
	 * @param position
	 */
	public void setVectorPosition(int position) {
		vector_position = position;
	}

	/**
	 * Gets the fixed position in which vector layers will be added. This method
	 * only makes sense when the behaviour is {@link OrderConfig#FROM_TOP} or
	 * {@link OrderConfig#FROM_BOTTOM} .
	 * 
	 * @param position
	 */
	public int getVectorPosition() {
		return vector_position;
	}

	/**
	 * Sets a fixed position in which raster layers will be added. This method
	 * only makes sense when the behaviour is {@link OrderConfig#FROM_TOP} or
	 * {@link OrderConfig#FROM_BOTTOM} .
	 * 
	 * @param position
	 */
	public void setRasterPosition(int position) {
		raster_position = position;
	}

	/**
	 * Gets the fixed position in which raster layers will be added. This method
	 * only makes sense when the behaviour is {@link OrderConfig#FROM_TOP} or
	 * {@link OrderConfig#FROM_BOTTOM} .
	 * 
	 * @param position
	 */
	public int getRasterPosition() {
		return raster_position;
	}

	/**
	 * Sets a fixed position in which other (not vector, not raster) layers will
	 * be added. This method only makes sense when the behaviour is
	 * {@link OrderConfig#FROM_TOP} or {@link OrderConfig#FROM_BOTTOM} .
	 * 
	 * @param position
	 */
	public void setOtherLayersPosition(int position) {
		other_position = position;
	}

	/**
	 * Gets the fixed position in which other (not vector, not raster) layers
	 * will be added. This method only makes sense when the behaviour is
	 * {@link OrderConfig#FROM_TOP} or {@link OrderConfig#FROM_BOTTOM} .
	 * 
	 * @param position
	 */
	public int getOtherLayersPosition() {
		return other_position;
	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("vector-behaviour", vector_behaviour);
		xml.putProperty("raster-behaviour", raster_behaviour);
		xml.putProperty("other-behaviour", other_behaviour);
		if (vector_behaviour == FROM_BOTTOM || vector_behaviour == FROM_BOTTOM) {
			xml.putProperty("vector-position", vector_position);
		}
		if (raster_behaviour == FROM_BOTTOM || raster_behaviour == FROM_BOTTOM) {
			xml.putProperty("raster-position", raster_position);
		}
		if (other_behaviour == FROM_BOTTOM || other_behaviour == FROM_BOTTOM) {
			xml.putProperty("other-position", other_position);
		}
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		if (xml.contains("vector-behaviour")) {
			vector_behaviour = xml.getIntProperty("vector-behaviour");
			if (vector_behaviour > 5 || vector_behaviour < 0) {
				vector_behaviour = ON_TOP;
			}
			if (vector_behaviour == FROM_BOTTOM || vector_behaviour == FROM_TOP) {
				if (xml.contains("vector-position")) {
					vector_position = xml.getIntProperty("vector-position");
				}
			}
		}
		if (xml.contains("raster-behaviour")) {
			raster_behaviour = xml.getIntProperty("raster-behaviour");
			if (raster_behaviour > 5 || raster_behaviour < 0) {
				raster_behaviour = ON_TOP;
			}
			if (raster_behaviour == FROM_BOTTOM || raster_behaviour == FROM_TOP) {
				if (xml.contains("raster-position")) {
					raster_position = xml.getIntProperty("raster-position");
				}
			}
		}
		if (xml.contains("other-behaviour")) {
			other_behaviour = xml.getIntProperty("other-behaviour");
			if (other_behaviour > 5 || other_behaviour < 0) {
				other_behaviour = ON_TOP;
			}
			if (other_behaviour == FROM_BOTTOM || other_behaviour == FROM_TOP) {
				if (xml.contains("other-position")) {
					other_position = xml.getIntProperty("other-position");
				}
			}
		}
	}
}
