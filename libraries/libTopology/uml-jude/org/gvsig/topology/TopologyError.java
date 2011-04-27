package org.gvsig.topology;

import com.iver.cit.gvsig.fmap.core.IFeature;


/**
 *Error produced when one or many features ç
 *violates a topology rule.
 */
public class TopologyError implements IFeature {
 
	/**
	 *rule which has been violated
	 */
	private AbstractTopologyRule violatedRule;
	 
	/**
	 *features of the source layer that
	 *violates the rule (if the rule is a self-rule,
	 *the error will only have source layer features)
	 */
	private IFeature[] sourceLyrFeatures;
	 
	/**
	 *features of the destination layer
	 *that violate the rule (only in rules between
	 *two layers)
	 */
	private IFeature[] destinationLyrFeatures;
	 
	private boolean exception;
	 
	public void setViolatedRule(AbstractTopologyRule violatedRule) {
	}
	 
	public AbstractTopologyRule getViolatedRule() {
		return null;
	}
	 
	public void setSourceLyrFeatures(IFeature[] sourceLyrFeatures) {
	}
	 
	public IFeature[] getSourceLyrFeatures() {
		return null;
	}
	 
	public void setDestinationLyrFeatures(IFeature[] destinationLyrFeatures) {
	}
	 
	public IFeature[] getDestinationLyrFeatures() {
		return null;
	}
	 
	/**
	 *Ruturns the type of geometry of the error
	 */
	public int getShapeType() {
		return 0;
	}
	 
	public void setException(boolean exception) {
	}
	 
	public boolean isException() {
		return false;
	}
	 
}
 
