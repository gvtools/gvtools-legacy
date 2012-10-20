package org.gvsig.graph;

import org.gvsig.graph.core.GvFlag;

public interface IClosestFacilityListener {

	public void eventAdded(GvFlag event);

	public void eventRemoved(GvFlag event);

	public void allEventsRemoved();

	public void eventModified(GvFlag oldEvent, GvFlag modifiedEvent);

	public void facilityAdded(GvFlag facility);

	public void facilityRemoved(GvFlag facility);

	public void allFacilitiesRemoved();

	public void facilityModified(GvFlag oldFacility, GvFlag modifiedFacility);

	public void addedSolvedFacility(GvFlag solvedFacility);

	public void removedSolvedFacilities();
}
