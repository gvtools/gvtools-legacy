package com.iver.cit.gvsig.fmap.layers;

import java.net.URL;

import com.iver.cit.gvsig.fmap.Source;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;

public class DefaultSource implements Source {

	private URL url;

	public DefaultSource(URL url) {
		this.url = url;
	}
	
	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public WithDefaultLegend getDefaultLegend() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DefaultSource) {
			return ((DefaultSource) obj).getURL().equals(this.url);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}
	
}
