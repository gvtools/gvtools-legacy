package org.gvsig.topology.topologyrules;

import org.gvsig.topology.AbstractTopologyRule;
import org.gvsig.topology.IOneLyrRule;


/**
 *All end points of a line must touch at least another line.
 *(if only touch one line, its a pseudonode, not a dangle)
 *This rule is checked for the two ends of a line.
 *
 */
public class LineMustNotHaveDangles extends AbstractTopologyRule implements IOneLyrRule {
 
}
 
