package com.iver.cit.gvsig.project.documents.table;

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.GeometryUtilities;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * @author Vicente Caballero Navarro
 */
public abstract class GraphicOperator extends AbstractOperator {
	private FLyrVect lv = null;

	public void setLayer(FLyrVect lv) {
		this.lv = lv;
	}

	public FLyrVect getLayer() {
		return lv;
	}

	public abstract double process(Index index) throws DriverIOException;

	protected ArrayList getXY(IGeometry geometry) {
		return GeometryUtilities.getParts(geometry);
	}

}
