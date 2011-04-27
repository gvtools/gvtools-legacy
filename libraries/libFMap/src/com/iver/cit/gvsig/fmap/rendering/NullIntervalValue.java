package com.iver.cit.gvsig.fmap.rendering;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class NullIntervalValue implements IInterval {

	public boolean isInInterval(Value v) {
		return (v instanceof NullValue);
	}

	/**
	 * Crea un nuevo NullInterval.
	 */
	public NullIntervalValue() {
	}
	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.IInterval#toString()
	 */
	public String toString() {
		return "Default";
	}
}
