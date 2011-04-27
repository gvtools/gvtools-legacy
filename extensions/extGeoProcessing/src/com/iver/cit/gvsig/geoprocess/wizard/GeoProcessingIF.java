package com.iver.cit.gvsig.geoprocess.wizard;
/**
 * Models Geoprocessing functionality of GeoProcessingPanel
 * GUI component.
 * 
 * @author azabala
 *
 */
public interface GeoProcessingIF {
	public boolean doBuffer();
	public boolean doClip();
	public boolean doDissolve();
	public boolean doMerge();
	public boolean doIntersect();
	public boolean doUnion();
	public boolean doSpatialJoin();
	public boolean doConvexHull();
	public boolean doDifference();
}
