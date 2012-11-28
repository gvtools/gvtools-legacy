package org.gvsig.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.geotools.styling.Style;
import org.gvsig.layer.filter.LayerFilter;
import org.gvsig.util.ProcessContext;

public interface Layer {

	/**
	 * If the layer is a single layer returns true if the <code>layer</code>
	 * parameter is this.
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

	/**
	 * Determines if the layer is being edited. A layer is being edited if there
	 * are any editing tasks being performed. In any other case (including
	 * composite layers), this method returns <code>false</code>.
	 * 
	 * @return <code>true</code> if the layer is being edited,
	 *         <code>false</code> otherwise.
	 */
	boolean isEditing();

	/**
	 * Determines if this layer is vectorial.
	 * 
	 * @return <code>true</code> if the layer is vectorial, <code>false</code>
	 *         otherwise (including composite layers).
	 */
	boolean isVectorial();

	/**
	 * Determines if the layer is active. A composite layer is never active.
	 * 
	 * @return <code>true</code> if the layer is active, <code>false</code>
	 *         otherwise.
	 */
	boolean isActive();

	void draw(BufferedImage image, Graphics2D g, long scaleDenominator,
			ProcessContext processContext);

	/**
	 * Adds a layer as a child of this one.
	 * 
	 * @param testLayer
	 * @throws UnsupportedOperationException
	 *             If this layer is not a collection
	 */
	void addLayer(Layer testLayer) throws UnsupportedOperationException;

	/**
	 * Getter for the layer style
	 * 
	 * @return
	 * @throws UnsupportedOperationException
	 *             If the layer does not support the style property, e.g.: layer
	 *             groups
	 */
	Style getStyle() throws UnsupportedOperationException;

	/**
	 * Setter for the layer style
	 * 
	 * @param style
	 * @throws UnsupportedOperationException
	 *             If the layer does not support the style property, e.g.: layer
	 *             groups
	 */
	void setStyle(Style style) throws UnsupportedOperationException;
}
