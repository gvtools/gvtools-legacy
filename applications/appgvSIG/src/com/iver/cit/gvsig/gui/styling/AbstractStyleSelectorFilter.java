package com.iver.cit.gvsig.gui.styling;

import com.iver.cit.gvsig.fmap.core.styles.IStyle;

public class AbstractStyleSelectorFilter implements StyleSelectorFilter {

	IStyle allowedStyle = null;

	public AbstractStyleSelectorFilter(IStyle sty) {
		this.allowedStyle = sty;
	}

	public boolean accepts(Object obj) {
		return obj.getClass().equals(allowedStyle.getClass());
	}

	public IStyle getAllowedObject() {
		return allowedStyle;
	}

	public void setAllowedObject(IStyle allowedObject) {
		this.allowedStyle = allowedObject;
	}

}
