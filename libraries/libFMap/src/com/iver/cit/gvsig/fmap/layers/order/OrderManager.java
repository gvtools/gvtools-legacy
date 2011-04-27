package com.iver.cit.gvsig.fmap.layers.order;

import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.utiles.IPersistence;
import com.iver.utiles.extensionPoints.IExtensionBuilder;

/**
 * <p>The classes implementing this interface will decide the right position
 * to add a new layer (FLayer) to a layer collection (FLayers) according with
 * its own criterion.</p>
 * <p>Some order managers may study the existing layers in the collection and
 * the new layer's type in order to apply its criterion.</p>
 * <p>For example, an OrderManager
 * may decide that VectorLayers should be placed over Raster Layers.
 * Another OrderManager may decide that a new layer is placed just
 * over the existing layers (this had always been the default gvSIG
 * behaviour).</p>
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
 *
 */
public interface OrderManager extends IPersistence, Cloneable, IExtensionBuilder {

	/**
	 * <p>Gets the proposed position for the newLayer in the target layer
	 * collection. The OrderManager will study the arrangement of the
	 * target layer collection and will decide the right position for
	 * the new layer, according to its own criterion.</p>
	 * 
	 * @param target The target collection to which <code>newLayer</code>
	 * will be added
	 * @param newLayer The layer to be inserted in the layer collection
	 * 
	 * @return The proposed position for the new layer
	 */
	public int getPosition(FLayers target, FLayer newLayer);

	/**
	 * <p>Gets the name. The name should identify the
	 * policy followed by this OrderManager to sort the layers.</p>
	 * 
	 * @return The name of the OrderManager
	 */
	public String getName();

	/**
	 * <p>Gets the description. The description should be enough to get
	 * an idea of the policy and features of this OrderManager.</p>
	 * 
	 * @return The description of the OrderManager
	 */
	public String getDescription();

	/**
	 * <p>Gets the code used to register this OrderManager
	 * in the ExtensionPoint.</p>
	 * 
	 * @return The code used to register this orderManager in the
	 * ExtensionPoint. 
	 */
	public String getCode();

	public Object clone();
}
