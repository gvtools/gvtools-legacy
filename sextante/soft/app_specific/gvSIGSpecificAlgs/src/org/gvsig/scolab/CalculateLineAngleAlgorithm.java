/*******************************************************************************
 LinkPointsToLinesAlgorithm.java
 Autor: Fco. Jos� Pe�arrubia (fjp@scolab.es)
 Copyright (C) SCOLAB Software Colaborativo S.L.
 
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *******************************************************************************/

package org.gvsig.scolab;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.quadtree.Quadtree;

import es.unex.sextante.additionalInfo.AdditionalInfoNumericalValue;
import es.unex.sextante.additionalInfo.AdditionalInfoVectorLayer;
import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.core.Sextante;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.dataObjects.IVectorLayer;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.exceptions.OptionalParentParameterException;
import es.unex.sextante.exceptions.RepeatedParameterNameException;
import es.unex.sextante.exceptions.UndefinedParentParameterNameException;

/**
 * @author Francisco Jos� Pe�arrubia (fjp@scolab.es)
 * First param: Line layer with a field in deegrees
 * Second param: Field Name to be calculated (must exist)
 * Calculates angle from first point to last point of the line
 *  
 */
public class CalculateLineAngleAlgorithm extends GeoAlgorithm {
   public static final String RESULT = "RESULT";
   public static final String FIELD_ANGLE = "FIELD_ANGLE";
   public static final String LAYER  = "LAYER";

	GeometryFactory geomFact = new GeometryFactory();

	/* (non-Javadoc)
	 * @see es.unex.sextante.core.GeoAlgorithm#defineCharacteristics()
	 */
	public void defineCharacteristics() {

		this.setName(Sextante.getText("calculate_segment_angle")); //$NON-NLS-1$
		this.setGroup(Sextante.getText("Tools_for_line_layers")); //$NON-NLS-1$
		try {
			m_Parameters.addInputVectorLayer(LAYER, Sextante.getText("original_layer"), //$NON-NLS-1$ //$NON-NLS-2$
					AdditionalInfoVectorLayer.SHAPE_TYPE_LINE, true);
			
			m_Parameters.addTableField(FIELD_ANGLE, Sextante.getText("angle_field_in_deegrees"),
					"LAYER", true);
			
			addOutputVectorLayer("RESULT", Sextante.getText("new_layer"), AdditionalInfoVectorLayer.SHAPE_TYPE_LINE); //$NON-NLS-1$
			
		} catch (RepeatedParameterNameException e) {
			Sextante.addErrorToLog(e);
		} catch (UndefinedParentParameterNameException e) {
			Sextante.addErrorToLog(e);
		} catch (OptionalParentParameterException e) {
			Sextante.addErrorToLog(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.unex.sextante.StandardExtension.core.StandardExtensionGeoAlgorithm
	 * #processAlgorithm()
	 */
	public boolean processAlgorithm() {

		int i = 0;

		try {
			IVectorLayer layer = m_Parameters
					.getParameterValueAsVectorLayer(LAYER); //$NON-NLS-1$

			IVectorLayer result = getNewVectorLayer(RESULT, Sextante.getText("new_layer"), layer.SHAPE_TYPE_LINE, //$NON-NLS-1$
					layer.getFieldTypes(), layer.getFieldNames());
						
			int fieldIndex = m_Parameters.getParameterValueAsInt(FIELD_ANGLE);
			
			int iShapeCount = layer.getShapesCount();
			m_Task.setProgressText(Sextante.getText("exporting")); //$NON-NLS-1$
			IFeatureIterator iter = layer.iterator();
			while (iter.hasNext() && setProgress(i, iShapeCount)) {
				try {
					IFeature feat = iter.next();
					Geometry geom = feat.getGeometry();
					Coordinate[] coordinates = geom.getCoordinates();
					Coordinate c = coordinates[0];
					Coordinate lastPoint = coordinates[coordinates.length-1];
					LineSegment segment = new LineSegment(c, lastPoint);
					double angRad = segment.angle();
					double angle = Math.toDegrees(angRad);
					Object[] values = feat.getRecord().getValues();
					values[fieldIndex] = new Double(angle);
					result.addFeature(geom, values);
				}
				catch (IteratorException ex) {
					ex.printStackTrace();
				}
				i++;
					
			}
			
			
		} catch (GeoAlgorithmExecutionException e) {
			e.printStackTrace();
			return false;
		}

		return !m_Task.isCanceled();
	}

}