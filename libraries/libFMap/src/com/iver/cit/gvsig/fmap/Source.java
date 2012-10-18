package com.iver.cit.gvsig.fmap;

import java.net.URL;

import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;

/**
 * Abstraction of a data source that is accessed through layers. Any
 * implementation must implement the {@link #equals(Object)} method (and
 * therefore, implement {@link #hashCode()} accordingly)
 * 
 * @author fergonco
 */
public interface Source {

	URL getURL();

	/**
	 * Returns a {@link WithDefaultLegend} instance if this source provides one.
	 * Null if the layers created from this source should have a default
	 * implementation
	 * 
	 * @return
	 */
	WithDefaultLegend getDefaultLegend();

}
