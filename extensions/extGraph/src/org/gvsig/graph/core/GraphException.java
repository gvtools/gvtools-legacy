package org.gvsig.graph.core;

import java.util.Map;

import org.gvsig.exceptions.BaseException;

public class GraphException extends BaseException {

	public static final long FLAG_OUT_NETWORK = 23;

	public GraphException(Throwable e) {
		super();
		this.initCause(e);
	}

	public GraphException(String string) {
		super();
		this.setFormatString(string);
	}

	protected Map values() {
		return null;
	}

}
