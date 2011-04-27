package org.gvsig.topology.topologyrules;

import org.gvsig.topology.IOneLyrRule;
import org.gvsig.topology.AbstractTopologyRule;
import org.gvsig.topology.Topology;


/**
 *For lines o polygons. The lenght of a line or the perimeter
 *of a polygon must be larger than the cluster tolerance
 *
 */
public class MustBeLargerThanClusterTolerance extends AbstractTopologyRule implements IOneLyrRule {
 
	private Topology topology;
	 
}
 
