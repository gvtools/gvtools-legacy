package org.gvsig.topology.topologyrules;

import org.gvsig.topology.AbstractTopologyRule;
import org.gvsig.topology.IOneLyrRule;


/**
 *The polygons of a given layer must not overlaps
 *each other (their intersection must be a line, not an area)
 *
 */
public class PolygonsMustNotOverlap extends AbstractTopologyRule implements IOneLyrRule {
 
}
 
