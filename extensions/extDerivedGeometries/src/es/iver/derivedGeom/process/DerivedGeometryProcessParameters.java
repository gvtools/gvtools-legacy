package es.iver.derivedGeom.process;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.io.File;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 *
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class DerivedGeometryProcessParameters {
	public static final short UNDEFINED_TYPE = -1;
	
	public static final String POINTS_TO_LINE_PROCESS_NAME = PluginServices.getText(null, "Points_to_line");
	public static final String POINTS_TO_POLYGON_PROCESS_NAME = PluginServices.getText(null, "Points_to_polygon");
	public static final String CLOSE_MULTILINE_PROCESS_NAME = PluginServices.getText(null, "Close_multiline");
	public static final String POINTS_TO_LINE_OR_POLYGON_NAME = PluginServices.getText(null, "PointsToLineOrPolygon");
	public static final short POINTS_TO_LINE_PROCESS_ID = 0;
	public static final short POINTS_TO_POLYGON_PROCESS_ID = 1;
	public static final short CLOSE_MULTILINE_PROCESS_ID = 2;
	public static final short POINTS_TO_LINE_OR_POLYGON_ID = 3;

	
	private MapControl mapControl;
	private FLyrVect sourceLayer;
	private File destinationFile;
	private String destinationLayerName;
	private long[][] geometryIndexes;
	private int destinationLayerShapeType = UNDEFINED_TYPE;
	private short processID = UNDEFINED_TYPE;
	private View view;
	private FLyrVect destLayer;
	
	public DerivedGeometryProcessParameters(FLyrVect source, File destinationFile, String destinationLayerName, long[][] geometryIndexes, short dType, View view, short processID) {
		this.sourceLayer = source;
		this.destinationFile = destinationFile;
		this.destinationLayerName = destinationLayerName;
		this.geometryIndexes = geometryIndexes;
		this.destinationLayerShapeType = dType;
		this.view = view;
		this.mapControl = view.getMapControl();
		this.processID = processID;
		this.destLayer = null;
	}
	
	public MapControl getMapControl() {
		return mapControl;
	}

	public FLyrVect getSourceLayer() {
		return sourceLayer;
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public String getDestinationLayerName() {
		return destinationLayerName;
	}
	
	/**
	 * Lines to polygons -> only one row
	 * Points to lines or polygons -> one row for each new geometry, with the points selected
	 * 
	 * @return
	 */
	public long[][] getGeometryIndexes() {
		return geometryIndexes;
	}

	public int getDestinationLayerShapeType() {
		return destinationLayerShapeType;
	}
	
	public View getView() {
		return view;
	}
	
	public short getProcessID() {
		return processID;
	}
	
	public FLyrVect getDestLayer() {
		return destLayer;
	}

	public void setSourceLayer(FLyrVect sourceLayer) {
		this.sourceLayer = sourceLayer;
	}

	public void setDestinationFile(File destinationFile) {
		this.destinationFile = destinationFile;
	}

	public void setDestinationLayerName(String destinationLayerName) {
		this.destinationLayerName = destinationLayerName;
	}

	public void setGeometryIndexes(long[][] geometryIndexes) {
		this.geometryIndexes = geometryIndexes;
	}

	public void setDestinationLayerShapeType(int destinationLayerShapeType) {
		this.destinationLayerShapeType = destinationLayerShapeType;
	}

	public void setView(View view) {
		this.view = view;
	}
	
	public void setProcessID(short processID) {
		this.processID = processID;
	}
	
	public void setDestLayer(FLyrVect layer) {
		destLayer = layer;
	}
	
	public boolean requiredDefined() {
		return (sourceLayer != null) && (destinationFile != null) && (destinationLayerName != null) 
		&& (destinationLayerShapeType != -1) && (view != null) && (mapControl != null);
	}
	
	public static String getProcessName(short processID) {
		switch (processID) {
			case POINTS_TO_LINE_PROCESS_ID:
				return POINTS_TO_LINE_PROCESS_NAME;
			case POINTS_TO_POLYGON_PROCESS_ID:
				return POINTS_TO_POLYGON_PROCESS_NAME;
			case CLOSE_MULTILINE_PROCESS_ID:
				return CLOSE_MULTILINE_PROCESS_NAME;
			case POINTS_TO_LINE_OR_POLYGON_ID:
				return POINTS_TO_LINE_OR_POLYGON_NAME;
		}
		
		return null;
	}
	
	public String getProcessName() {
		if (processID == UNDEFINED_TYPE)
			return null;
		
		switch (processID) {
			case POINTS_TO_LINE_PROCESS_ID:
				return POINTS_TO_LINE_PROCESS_NAME;
			case POINTS_TO_POLYGON_PROCESS_ID:
				return POINTS_TO_POLYGON_PROCESS_NAME;
			case CLOSE_MULTILINE_PROCESS_ID:
				return CLOSE_MULTILINE_PROCESS_NAME;
			case POINTS_TO_LINE_OR_POLYGON_ID:
				return POINTS_TO_LINE_OR_POLYGON_NAME;
		}
		
		return null;
	}
}
