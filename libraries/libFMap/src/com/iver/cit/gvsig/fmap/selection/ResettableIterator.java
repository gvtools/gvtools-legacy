package com.iver.cit.gvsig.fmap.selection;

import java.util.Iterator;

/**
 * <p>
 * Extends the Iterator Interface, allowing to reset the Iterator. Calling
 * <code>next();</code> after a <code>reset();</code> will get the first element
 * in the iterator.
 * </p>
 * 
 * @author IVER T.I. <http://www.iver.es> 23/02/2009
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es> 23/02/2009
 * 
 * @param <E>
 */
public interface ResettableIterator<E> extends Iterator<E> {
	/**
	 * <p>
	 * Sets the iterator to its initial state. Calling <code>next();</code>
	 * after a <code>reset();</code> will get the first element in the iterator.
	 * </p>
	 * .
	 */
	public void reset();
}
