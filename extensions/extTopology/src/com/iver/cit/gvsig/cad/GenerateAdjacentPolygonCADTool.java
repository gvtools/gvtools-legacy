/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.cad;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.jts.JtsUtil;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.LinearComponentExtracter;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;

/**
 * CAD tool which generates and adjacent polygon to a given one of a edition
 * layer by digitizing a line touching the original polygon.
 * 
 * @author Alvaro Zabala
 * 
 */
public class GenerateAdjacentPolygonCADTool extends SplitGeometryCADTool {

	/**
	 * String representation of this tool (used for example to active the tool
	 * in mapcontrol)
	 */
	public static final String ADJACENT_POLYGON_TOOL_NAME = "_adjacent_polygon";

	public String toString() {
		return ADJACENT_POLYGON_TOOL_NAME;
	}

	/**
	 * Initialization method.
	 */
	public void init() {
		super.init();
		setNextTool(ADJACENT_POLYGON_TOOL_NAME);
	}

	public void splitSelectedGeometryWithDigitizedLine() {
		try {
			VectorialLayerEdited layer = getVLE();
			VectorialEditableAdapter adapter = layer.getVEA();
			Point2D[] clickedPts = new Point2D[this.clickedPoints.size()];
			clickedPoints.toArray(clickedPts);
			Coordinate[] digitizedCoords = JtsUtil
					.getPoint2DAsCoordinates(clickedPts);
			LineString digitizedLine = JtsUtil.GEOMETRY_FACTORY
					.createLineString(digitizedCoords);

			List<Geometry> lineNeighbours = new ArrayList<Geometry>();
			List<LineString> neigboursBorders = new ArrayList<LineString>();

			Rectangle2D rect = FGeometryUtil.envelopeToRectangle2D(digitizedLine
					.getEnvelopeInternal());

			CoordinateReferenceSystem crs = layer.getLayer().getMapContext().getViewPort()
					.getCrs();
			IRowEdited[] features = adapter.getFeatures(rect,
					ProjectionUtils.getAbrev(crs));
			for (int i = 0; i < features.length; i++) {
				IRowEdited row = features[i];
				IFeature feature = (IFeature) row.getLinkedRow().cloneRow();
				Geometry jtsGeo = NewFConverter
						.toJtsGeometry(feature.getGeometry());
				if(jtsGeo.distance(digitizedLine) != 0)
					continue;
				lineNeighbours.add(jtsGeo);
				neigboursBorders.addAll(LinearComponentExtracter.getLines(jtsGeo));
			}

			Geometry nodedLine = digitizedLine;
			for (int i = 0; i < neigboursBorders.size(); i++) {
				LineString line = neigboursBorders.get(i);
				nodedLine = EnhancedPrecisionOp.union(nodedLine, line);
			}

			Polygonizer polygonizer = new Polygonizer();
			polygonizer.add(nodedLine);

			Collection createdPolygons = polygonizer.getPolygons();
			List<Geometry> solution = new ArrayList<Geometry>();

			Iterator it = createdPolygons.iterator();
			while (it.hasNext()) {
				Geometry poly = (Polygon) it.next();
				if (poly.distance(digitizedLine) == 0d) {
					//now we check that this polygon doesnt overlap neighbours polygons
					if(overlapsNewPolygonWithExisting(poly, lineNeighbours))
					{	
						Geometry difference = JtsUtil.difference(poly, lineNeighbours);
						if(difference instanceof GeometryCollection)
							if(((GeometryCollection)difference).isEmpty())
								continue;
						poly = difference;
					}
					
					Polygon[] newPolygons = JtsUtil.extractPolygons(poly);
					for(int j = 0; j < newPolygons.length; j++){
						if (EnhancedPrecisionOp.intersection(newPolygons[j], digitizedLine).getDimension() == 1) {
							solution.add(newPolygons[j]);
						}// if
					}//for j
				}// if
			}// while
			
			

			// finally, we create a new feature for each new resulting polygon
			FLyrVect editionLyr = (FLyrVect) layer.getLayer();
			int numFields = editionLyr.getRecordset().getFieldCount();
			Value[] newValues = new Value[numFields];
			for(int i = 0; i < numFields; i++){
				newValues[i] = ValueFactory.createNullValue();
			}
			
			 adapter.startComplexRow();
			 for(int i = 0; i < solution.size(); i++){
				 Geometry geo = solution.get(i);
				 IGeometry igeo = NewFConverter.toFMap(geo);
				 String newId = editionLyr.getSource().getShapeCount()+"";
				 DefaultFeature newFeature = 
					 new DefaultFeature(igeo, newValues, newId );
				 adapter.doAddRow(newFeature, EditionEvent.GRAPHIC); 
			 }
			 adapter.endComplexRow(getName());
		} catch (BaseException e) {
			e.printStackTrace();
		} 
		 
	}
	
	private boolean overlapsNewPolygonWithExisting(Geometry poly, List<Geometry> lineNeighbours){
		for(int i = 0; i < lineNeighbours.size(); i++){
			if(poly.overlaps(lineNeighbours.get(i)))
				return true;
		}
		return false;
	}
	
	public String getName() {
		return PluginServices.getText(this, ADJACENT_POLYGON_TOOL_NAME);
	}

}
