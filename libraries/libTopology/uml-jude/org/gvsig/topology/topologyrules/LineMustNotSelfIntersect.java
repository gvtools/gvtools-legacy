package org.gvsig.topology.topologyrules;

import org.gvsig.topology.AbstractTopologyRule;
import org.gvsig.topology.IOneLyrRule;


/**
 *Lines of a layer must not have self intersections.
 *JTS allows this. This is one of the restrictions that must
 *not have pseudonodos checks.
 *
 */
public class LineMustNotSelfIntersect extends AbstractTopologyRule implements IOneLyrRule {
 
}
 
