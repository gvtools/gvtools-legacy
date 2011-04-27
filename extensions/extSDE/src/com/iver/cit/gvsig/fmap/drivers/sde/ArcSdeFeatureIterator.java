/*
 * Created on 11-mar-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.fmap.drivers.sde;

import java.awt.geom.PathIterator;
import java.sql.SQLException;
import java.util.ArrayList;

import com.esri.sde.sdk.client.SDEPoint;
import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeCoordinateReference;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FNullGeometry;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;

/**
 * @author   FJP
 */
public class ArcSdeFeatureIterator implements IFeatureIterator {
    IGeometry geom;
    int numColumns;
    private SeQuery query = null;
    private boolean bFirst;
    Value[] regAtt;
    SeRow row;
    private  int index=0;

    static GeneralPathX convertSeShapeToGeneralPathX(SeShape spVal) throws SeException
    {
        double[][][] points = spVal.getAllCoords(SeShape.TURN_RIGHT);
        GeneralPathX gpx = new GeneralPathX();
        // Display the X and Y values
        boolean bStartPart;
        for( int partNo = 0 ; partNo < points.length ; partNo++)
        {
            for( int subPartNo = 0 ; subPartNo < points[partNo].length ; subPartNo++)
            {
                bStartPart = true;
                for( int pointNo = 0 ; pointNo < points[partNo][subPartNo].length ; pointNo+=2)
                {
                    if (bStartPart)
                    {
                        bStartPart = false;
                        gpx.moveTo(points[partNo][subPartNo][pointNo],
                                points[partNo][subPartNo][(pointNo+1)]);
                    }
                    else
                        gpx.lineTo(points[partNo][subPartNo][pointNo],
                                points[partNo][subPartNo][(pointNo+1)]);

                }
            }
        }
        return gpx;
    }

    public static IGeometry getGeometry( SeShape shape ) {

        try {
            /*
             *   Retrieve the shape type.
             */
            int type = -1;
            type = shape.getType();

            // Display the X and Y values
            /* for( int partNo = 0 ; partNo < points.length ; partNo++, System.out.println("") )
                for( int subPartNo = 0 ; subPartNo < points[partNo].length ; subPartNo++, System.out.println("") )
                    for( int pointNo = 0 ; pointNo < points[partNo][subPartNo].length ; pointNo+=2)
                        System.out.println("X: " + points[partNo][subPartNo][pointNo] + "\tY: "
                                                 + points[partNo][subPartNo][(pointNo+1)] ); */
            switch( type )
            {
                case SeShape.TYPE_POINT:
                    double[][][] points = shape.getAllCoords();
                    FPoint2D p =  new FPoint2D(points[0][0][0],points[0][0][1]);
                    return ShapeFactory.createPoint2D(p);

                case SeShape.TYPE_LINE:
                case SeShape.TYPE_MULTI_LINE:
                case SeShape.TYPE_MULTI_SIMPLE_LINE:
                case SeShape.TYPE_SIMPLE_LINE:
                    GeneralPathX gpx = new GeneralPathX(shape.toGeneralPath());
                    return ShapeFactory.createPolyline2D(gpx);

                case SeShape.TYPE_MULTI_POINT:
                    break;

                case SeShape.TYPE_NIL:
                    return new FNullGeometry();
                case SeShape.TYPE_POLYGON:
                case SeShape.TYPE_MULTI_POLYGON:
                    // GeneralPathX gpx2 = new GeneralPathX(shape.toGeneralPath());
                    GeneralPathX gpx2 = convertSeShapeToGeneralPathX(shape);
                    /* SeExtent r = shape.getExtent();
                    GeneralPathX gpx2 = new GeneralPathX();
                    gpx2.moveTo(r.getMinX(), r.getMinY());
                    gpx2.lineTo(r.getMaxX(), r.getMinY());
                    gpx2.lineTo(r.getMaxX(), r.getMaxY());
                    gpx2.lineTo(r.getMinX(), r.getMaxY());
                    gpx2.closePath(); */
                    return ShapeFactory.createPolygon2D(gpx2);

            } // End switch
        }
        catch (SeException e)
        {
        	NotificationManager.addError(e);
        }
        return new FNullGeometry();
    }


    public static SeShape constructShape(IGeometry geometry, SeCoordinateReference seSrs) {
		SeShape shape = null;

		try {
			shape = new SeShape(seSrs);
		} catch (SeException ex) {

		}

//		if (geometry.isEmpty()) {
//			return shape;
//		}

		int numParts=1;
//		GeometryCollection gcol = null;
//
//		if (geometry instanceof GeometryCollection) {
//			gcol = (GeometryCollection) geometry;
//		} else {
//			Geometry[] geoms = { geometry };
//			gcol = new GeometryFactory().createGeometryCollection(geoms);
//		}

//		List allPoints = new ArrayList();
//		numParts = gcol.getNumGeometries();

		int[] partOffsets = new int[numParts];
//		Geometry geom;
//		Coordinate[] coords;
//		Coordinate c;
		SDEPoint[] points = getPoints(geometry);
		partOffsets[0]=points.length;
//		for (int currGeom = 0; currGeom < numParts; currGeom++) {
//			partOffsets[currGeom] = allPoints.size();
//			geom = gcol.getGeometryN(currGeom);
//
//			coords = geom.getCoordinates();
//
//			for (int i = 0; i < coords.length; i++) {
//				c = coords[i];
//				allPoints.add(new SDEPoint(c.x, c.y));
//			}
//		}

//		SDEPoint[] points = new SDEPoint[allPoints.size()];
//		allPoints.toArray(points);

		try {
			if (geometry.getGeometryType()==FShape.POINT || geometry instanceof FMultiPoint2D) {
				shape.generatePoint(points.length, points);
			} else if (geometry.getGeometryType()==FShape.LINE) {
				shape
						.generateLine(points.length, numParts, partOffsets,
								points);
			} else {
				shape.generatePolygon(points.length, numParts, partOffsets,
						points);
			}
		} catch (SeException e) {
			NotificationManager.addError(e);
		}

		return shape;
	}

    private static SDEPoint[] getPoints(IGeometry g) {
//		if (FConstant.SHAPE_TYPE_MULTIPOINTZ == m_type){
//			zs=((IGeometry3D)g).getZs();
//		}
		PathIterator theIterator = g.getPathIterator(null); //polyLine.getPathIterator(null, flatness);
		double[] theData = new double[6];
		ArrayList ps=new ArrayList();
		while (!theIterator.isDone()) {
			//while not done
			int theType = theIterator.currentSegment(theData);

			ps.add(new SDEPoint(theData[0], theData[1]));
			theIterator.next();
		} //end while loop
		SDEPoint[] points = (SDEPoint[])ps.toArray(new SDEPoint[0]);
		return points;
    }




    /**
     * @throws SQLException
     *
     */
    public ArcSdeFeatureIterator(SeQuery query, String idField) {
        // Debe ser forward only
        this.query = query;
        try {
            row = query.fetch();
			if (row == null)
            {
                bFirst = true;
                return;
            }
            numColumns = row.getNumColumns();
            regAtt = new Value[numColumns-1];
            bFirst = true;
            SeColumnDefinition[] colDefs = row.getColumns();
        	for (int i=0; i<colDefs.length;i++){
        		if (colDefs[i].getName().equals(idField)){
        			index=i;
        			break;
        		}
        	}
        } catch (SeException e) {

        	NotificationManager.addError(e);
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#hasNext()
     */
    public boolean hasNext() {
        try {
            if (bFirst)
                bFirst = false;
            else
                row = query.fetch();
            if (row == null)
            {
                query.close();
                return false;
            }

            return true;
        }
        catch (SeException e) {
        	NotificationManager.addError(e);

        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#next()
     */
    public IFeature next(){
        SeShape spVal = null;
        SeColumnDefinition[] colDefs = row.getColumns();
        IFeature feat = null;
        try
        {

            if ( row != null )
            {
            	int hasgeom=0;
                for (int colNum = 0; colNum < colDefs.length; colNum++)
                {
                    SeColumnDefinition colDef = colDefs[colNum];
                    int dataType = colDef.getType();
                    if ( row.getIndicator((short)colNum) != SeRow.SE_IS_NULL_VALUE)
                    {
                        switch( dataType )
                        {
                        	case SeColumnDefinition.TYPE_SHAPE:
                        		spVal = row.getShape(colNum);
                        		geom = getGeometry(spVal);
                        		hasgeom=-1;
                        		break;
                            case SeColumnDefinition.TYPE_INT16:
                                regAtt[colNum+hasgeom] =  ValueFactory.createValue(row.getShort(colNum).intValue());
                                break;
                            case SeColumnDefinition.TYPE_DATE:
                                regAtt[colNum+hasgeom] =  ValueFactory.createValue(row.getTime(colNum).getTime());
                                break;

                            case SeColumnDefinition.TYPE_INT32:
                            case SeColumnDefinition.TYPE_INT64:
                                regAtt[colNum+hasgeom] =  ValueFactory.createValue(row.getInteger(colNum).intValue());
                                break;

                            case SeColumnDefinition.TYPE_FLOAT32:
                                regAtt[colNum+hasgeom] =  ValueFactory.createValue(row.getFloat(colNum).floatValue());
                                break;

                            case SeColumnDefinition.TYPE_FLOAT64:
                                regAtt[colNum+hasgeom] =  ValueFactory.createValue(row.getDouble(colNum).doubleValue());
                                break;

                            case SeColumnDefinition.TYPE_STRING:
                            case SeColumnDefinition.TYPE_NSTRING:
                                regAtt[colNum+hasgeom] =  ValueFactory.createValue(row.getString(colNum));
                                break;


                        } // End switch
                    } // End if
                } // for
                //System.out.println("Dentro de next(): " + spVal.getFeatureId().longValue() + " " + regAtt[0]);

                feat = new DefaultFeature(geom, regAtt,String.valueOf(regAtt[index-1]));//""+ spVal.getFeatureId().longValue());
            } // if


        } catch (SeException e)
        {
        	NotificationManager.addError(e);
        }


        return feat;
    }

	public void closeIterator() throws ReadDriverException {
		try {
			bFirst=false;
			query.close();
		} catch (SeException e) {
			throw new ReadDriverException("ArcSDE",e);
		}

	}

}
