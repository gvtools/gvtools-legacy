package org.gvsig.layer;

public interface Layer {

	/**
	 * If the layer is a single layer returns true if the <code>layer</code>
	 * parameter is equal to this.
	 * 
	 * If the layer is a collection, returns true in case the tree contains the
	 * specified instance.
	 * 
	 * False otherwise
	 * 
	 * @param layer
	 * @return
	 */
	boolean contains(Layer layer);

	/**
	 * Getter for the name property
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns this layer along with all the children layers, if any
	 * 
	 * @return
	 */
	Layer[] getAllLayers();

	/**
	 * Obtains an array of all layers that all match the filter condition. This
	 * layer and all the descendant are tested
	 * 
	 * @param filter
	 * @return
	 */
	Layer[] filter(LayerFilter filter);

	boolean isEditing();
	
	boolean isVectorial();
}
