package org.gvsig.topology;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import org.cresques.cts.IProjection;


/**
 *All the classes that contains TopologyError must 
 *implement this interface.
 */
public interface ITopologyErrorContainer {
 
	public static final int ONLY_ERRORS = 0;
	 
	public static final int ONLY_EXCEPTIONS = 1;
	 
	public static final int BOTH_ERROR_EXCEPTIONS = 2;
	 
	public void addTopologyError(TopologyError topologyError);
	/**
	 *Returns the errors contained. In function
	 *of the specified params, returned errors 
	 *will be reprojected, filtered by rule, geometry type,
	 *etc.
	 */
	public TopologyError[] getTopologyErrors(String ruleName, int shapeType, FLyrVect sourceLayer, IProjection desiredProjection, int includeExceptions);
	/**
	 *Marks the specified feature as the
	 *specified status (exception or not exception)
	 */
	public void markAsTopologyException(TopologyError topologyError, boolean markAsException);
}
 
