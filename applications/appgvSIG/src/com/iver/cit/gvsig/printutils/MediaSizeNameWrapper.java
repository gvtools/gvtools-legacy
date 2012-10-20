package com.iver.cit.gvsig.printutils;

import javax.print.attribute.standard.MediaSizeName;

import com.iver.andami.PluginServices;

public class MediaSizeNameWrapper {

	private MediaSizeName msn = null;
	private String nice_name = "[Empty]";

	public MediaSizeNameWrapper(MediaSizeName _msn) {
		nice_name = PluginServices.getText(MediaSizeNameWrapper.class,
				_msn.toString());
		msn = _msn;
	}

	public MediaSizeName getMediaSizeName() {
		return msn;
	}

	public String toString() {
		return nice_name;
	}

}

// [eiel-add-print-formats]
