package org.gvsig.hyperlink;

import java.util.Map;

public abstract class AbstractActionManager implements ILinkActionManager {
	public boolean hasPanel() {
		return false;
	}

	public Object create() {
		return this;
	}

	public Object create(Object[] args) {
		return this;
	}

	public Object create(Map args) {
		return this;
	}

}
