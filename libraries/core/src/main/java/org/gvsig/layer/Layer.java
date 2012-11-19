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
	 * Replaces the source this layer access to.
	 * 
	 * @param es
	 * @throws InvalidSourceException
	 *             If the source is not valid. This can be due to several causes
	 *             depending on the nature of the layer. In general, the new
	 *             contents are not compatible with any of the data structures
	 *             built at the layer level. In particular, the most common case
	 *             is when there is a legend on a field that the new source data
	 *             do not have
	 */
	void setSource(Source es) throws InvalidSourceException;

	Source getSource();

}
