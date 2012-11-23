package com.iver.cit.gvsig.project.documents.view.gui;

import org.gvsig.NotAvailableLabel;
import org.gvsig.map.MapContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.map.ColorEvent;
import com.iver.cit.gvsig.map.ExtentEvent;
import com.iver.cit.gvsig.map.MapControl;
import com.iver.cit.gvsig.map.ProjectionEvent;
import com.iver.cit.gvsig.map.ViewPortListener;

public class MapOverview extends NotAvailableLabel implements ViewPortListener {

	public MapOverview(MapControl m_MapControl) {
	}

	public void setModel(MapContext mapOverViewContext) {
	}

	public void setCrs(CoordinateReferenceSystem newCrs) {

	}

	@Override
	public void extentChanged(ExtentEvent e) {
		assert false;
	}

	@Override
	public void backColorChanged(ColorEvent e) {
		assert false;
	}

	@Override
	public void projectionChanged(ProjectionEvent e) {
		assert false;
	}
}
