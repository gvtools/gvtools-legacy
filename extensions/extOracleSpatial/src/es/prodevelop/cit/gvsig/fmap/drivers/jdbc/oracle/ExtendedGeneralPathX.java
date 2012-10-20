package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import com.iver.cit.gvsig.fmap.core.GeneralPathX;

/**
 * Utility class to keep info about QUAD arcs being linearized or not.
 * 
 * @author jldominguez
 * 
 */
public class ExtendedGeneralPathX extends GeneralPathX {

	private boolean linearized = false;

	public boolean isLinearized() {
		return linearized;
	}

	public void setLinearized(boolean v) {
		linearized = v;
	}

}
