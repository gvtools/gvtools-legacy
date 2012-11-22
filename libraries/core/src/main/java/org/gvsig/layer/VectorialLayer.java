package org.gvsig.layer;

/**
 * Interface that provides methods that vectorial layers should implement
 * 
 * @author Fernando González Cortés
 * @author Víctor González Cortés
 */
public interface VectorialLayer {

	/**
	 * Return true if the layer is in edition
	 * 
	 * @return
	 */
	boolean isEditing();

}
