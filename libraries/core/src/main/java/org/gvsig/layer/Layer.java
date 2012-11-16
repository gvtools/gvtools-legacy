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

}
