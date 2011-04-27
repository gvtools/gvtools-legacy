package org.gvsig.topology;

import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.vividsolutions.jts.geom.Envelope;
import java.awt.geom.Rectangle2D;
import org.gvsig.topology.topologyrules.MustBeLargerThanClusterTolerance;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class Topology extends FLayers implements ITopologyErrorContainer, ITopologyStatus {
 
	private double clusterTolerance;
	 
	private int status;
	 
	/**
	 *If during validation process a topoly
	 *exceeds this parameters, validation will be stopped.
	 *
	 */
	private int maxNumberOfErrors = -1;
	 
	private AbstractTopologyRule[] abstractTopologyRule;
	 
	private Envelope[] envelope;
	 
	private Rectangle2D[] dirtyZones;
	 
	private MustBeLargerThanClusterTolerance[] clusterTolRule;
	 
	public double getClusterTolerance() {
		return 0;
	}
	 
	/**
	 *Adds a new topology rule to the topology.
	 */
	public void addRule(AbstractTopologyRule rule) {
	}
	 
	/**
	 *Adds a layer to the topology. If the topology has been validated, changes
	 *topology status to NON-VALIDATED and adds a dirty zone equals to
	 *the layer extent.
	 */
	public void addLayer(FLyrVect layer, int xyRank, int zRank) {
	}
	 
	public void setStatus(int status) {
	}
	 
	public int getStatus() {
		return 0;
	}
	 
	/**
	 *Adds a dirty zone to the topology
	 *(usually whe a feature of a layer
	 *of the topology has been edited)
	 */
	public void addDirtyZone(Rectangle2D dirtyZone) {
	}
	 
	public Rectangle2D getDirtyZone() {
		return null;
	}
	 
	public void validate() {
	}
	 
	public int getLayerCount() {
		return 0;
	}
	 
	public int getRuleCount() {
		return 0;
	}
	 
	public FLyrVect getLyr(int lyrIndex) {
		return null;
	}
	 
	public AbstractTopologyRule getRule(int ruleIndex) {
		return null;
	}
	 
	/**
	 *Returns if a specified rectangle touch one
	 *of the existing dirty zones. If not, probably
	 *is needed to add to the dirty zones collection.
	 *If true, maybe it could modify the dirty zone.
	 */
	public boolean isInDirtyZone() {
		return false;
	}
	 
	/**
	 *Modify the dirty zone of the specified index
	 */
	public void updateDirtyZone(int dirtyZoneIndex, Rectangle2D dirtyZone) {
	}
	 
	public void setMaxNumberOfErrors(int maxNumberOfErrors) {
	}
	 
	public int getMaxNumberOfErrors() {
		return 0;
	}
	 
}
 
