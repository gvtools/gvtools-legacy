package org.gvsig.topology;

import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.core.IFeature;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public abstract class AbstractTopologyRule implements ITopologyRule {
 
	private FLyrVect originLyr;
	 
	private FLyrVect destinationLyr;
	 
	private Topology topology;
	 
	public void setOriginLyr(FLyrVect originLyr) {
	}
	 
	public FLyrVect getOriginLyr() {
		return null;
	}
	 
	public void setDestinationLyr(FLyrVect destinationLyr) {
	}
	 
	public FLyrVect getDestinationLyr() {
		return null;
	}
	 
	public String getName() {
		return null;
	}
	 
	/**
	 *Checks if the rule's parameters 
	 *(sourceLyr, destinationLyr) verify 
	 *rule preconditions (geometry type, etc.)
	 */
	public void checkPreconditions() {
	}
	 
	public void checkRule() {
	}
	 
	void validateFeature(IFeature feature) {
	}
	 
	public void checkRule(Rectangle2D rect) {
	}
	 
	public Collection getTopologyErrors() {
		return null;
	}
	 
}
 
