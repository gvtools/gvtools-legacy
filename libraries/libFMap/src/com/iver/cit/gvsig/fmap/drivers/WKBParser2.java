/*
 * WKBParser.java
 * Based in
 * PostGIS extension for PostgreSQL JDBC driver - Binary Parser
 *
 * NOTA: Es posible que lo mejor sea crear un PostGisGeometry que implemente
 * la interfaz IGeometry, y así nos sirve de base para tener IGeometries
 * que encapsulan otras posibles geometrías. Por ejemplo, un JTSGeometry.
 * De esta forma, un driver no necesitaría reescribirse.
 *
 * (C) 2005 Markus Schaber, schabios@logi-track.com
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or visit the web at
 * http://www.gnu.org.
 *
 * $Id: WKBParser2.java 24154 2008-10-21 10:01:05Z jpiera $
 */
package com.iver.cit.gvsig.fmap.drivers;

import java.awt.geom.PathIterator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2DM;
import com.iver.cit.gvsig.fmap.core.FPoint3D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.ShapeMFactory;
import com.vividsolutions.jts.io.WKBConstants;

/**
 * Parse binary representation of geometries. Currently, only text rep (hexed)
 * implementation is tested.
 *
 * It should be easy to add char[] and CharSequence ByteGetter instances,
 * although the latter one is not compatible with older jdks.
 *
 * I did not implement real unsigned 32-bit integers or emulate them with long,
 * as both java Arrays and Strings currently can have only 2^31-1 elements
 * (bytes), so we cannot even get or build Geometries with more than approx.
 * 2^28 coordinates (8 bytes each).
 *
 * @author markus.schaber@logi-track.com
 *
 */
public class WKBParser2 {

    private boolean gHaveM, gHaveZ, gHaveS; // M, Z y SRID


    /**
     * Parse a binary encoded geometry.
     *
     * Is synchronized to protect offset counter. (Unfortunately, Java does not
     * have neither call by reference nor multiple return values.)
     */
    public synchronized IGeometry parse(byte[] value) {
        // BinaryByteGetter bytes = new ByteGetter.BinaryByteGetter(value);
        ByteBuffer buf = ByteBuffer.wrap(value);
        return parseGeometry(buf);
    }

    protected void parseTypeAndSRID(ByteBuffer data)
    {
        byte endian = data.get(); //skip and test endian flag
        /* if (endian != data.endian) {
            throw new IllegalArgumentException("Endian inconsistency!");
        } */
        int typeword = data.getInt();

        int realtype = typeword & 0x1FFFFFFF; //cut off high flag bits

        gHaveZ = (typeword & 0x80000000) != 0;
        gHaveM = (typeword & 0x40000000) != 0;
        gHaveS = (typeword & 0x20000000) != 0;

        int srid = -1;

        if (gHaveS) {
            srid = data.getInt();
        }

    }


    /** Parse a geometry starting at offset. */
    protected IGeometry parseGeometry(ByteBuffer data) {
        byte endian = data.get(); //skip and test endian flag
        if (endian == 1)
        {
        	data.order(ByteOrder.LITTLE_ENDIAN);
        }
        /* if (endian != data.endian) {
            throw new IllegalArgumentException("Endian inconsistency!");
        } */
        int typeword = data.getInt();

        int realtype = typeword & 0x1FFFFFFF; //cut off high flag bits

        boolean haveZ = (typeword & 0x80000000) != 0;
        boolean haveM = (typeword & 0x40000000) != 0;
        boolean haveS = (typeword & 0x20000000) != 0;

        int srid = -1;

        if (haveS) {
            srid = data.getInt();
        }
        IGeometry result1;
        switch (realtype) {
        case WKBConstants.wkbPoint :
            result1 = ShapeFactory.createPoint2D(parsePoint(data, haveZ, haveM));
            break;
        case WKBConstants.wkbLineString :
            result1 = ShapeFactory.createGeometry(parseLineString(data, haveZ, haveM));
            break;
        case WKBConstants.wkbPolygon :
            result1 = ShapeFactory.createGeometry(parsePolygon(data, haveZ, haveM));
            break;
        case WKBConstants.wkbMultiPoint:
            result1 = parseMultiPoint(data);
            break;
        case WKBConstants.wkbMultiLineString:
        	if (haveM) {
        		result1 = ShapeMFactory.createPolyline2DM(data);
        	} else {
        		result1 = ShapeFactory.createGeometry(parseMultiLineString(data));
        	}
            break;
        case WKBConstants.wkbMultiPolygon:
            result1 = ShapeFactory.createGeometry(parseMultiPolygon(data));
            break;
        case WKBConstants.wkbGeometryCollection :
            result1 = parseCollection(data);
            break;
        default :
            throw new IllegalArgumentException("Unknown Geometry Type!");
        }

        /*Geometry result = result1;

        if (haveS) {
            result.setSrid(srid);
        } */
        return result1;
    }

    private FPoint2D parsePoint(ByteBuffer data, boolean haveZ, boolean haveM) {
        double X = data.getDouble();
        double Y = data.getDouble();
        FPoint2D result;
        if (haveZ) {
            double Z = data.getDouble();
            result = new FPoint3D(X, Y, Z);
        } else {
            result = new FPoint2D(X, Y);
        }

        if (haveM) {            
            double m = data.getDouble();
            result = new FPoint2DM(X, Y, m);            
        }

        return result;
    }

    /** Parse an Array of "full" Geometries */
    private void parseGeometryArray(ByteBuffer data, IGeometry[] container) {
        for (int i = 0; i < container.length; i++) {
            container[i] = parseGeometry(data);
        }
    }

    /**
     * Parse an Array of "slim" Points (without endianness and type, part of
     * LinearRing and Linestring, but not MultiPoint!
     *
     * @param haveZ
     * @param haveM
     */
    private FPoint2D[] parsePointArray(ByteBuffer data, boolean haveZ, boolean haveM) {
        int count = data.getInt();
        FPoint2D[] result = new FPoint2D[count];
        for (int i = 0; i < count; i++) {
            result[i] = parsePoint(data, haveZ, haveM);
        }
        return result;
    }

    private FMultiPoint2D parseMultiPoint(ByteBuffer data) {
        FPoint2D[] points = new FPoint2D[data.getInt()];
        for (int i=0; i < points.length; i++)
        {
        	parseTypeAndSRID(data);
        	points[i] = parsePoint(data, gHaveZ, gHaveM);
        }
        return new FMultiPoint2D(points);
    }

    private FPolyline2D parseLineString(ByteBuffer data, boolean haveZ, boolean haveM) {
        FPoint2D[] points = parsePointArray(data, haveZ, haveM);
        GeneralPathX gp = new GeneralPathX();
        gp.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i< points.length; i++)
        {
            gp.lineTo(points[i].getX(), points[i].getY());
        }
        return new FPolyline2D(gp);
    }

    private FPolygon2D parseLinearRing(ByteBuffer data, boolean haveZ, boolean haveM) {
        FPoint2D[] points = parsePointArray(data, haveZ, haveM);
        GeneralPathX gp = new GeneralPathX();
        gp.moveTo(points[0].getX(), points[0].getY());
        for (int i = 1; i< points.length; i++)
        {
            gp.lineTo(points[i].getX(), points[i].getY());
        }
        return new FPolygon2D(gp);
    }

    private FPolygon2D parsePolygon(ByteBuffer data, boolean haveZ, boolean haveM) {
	        int count = data.getInt();
	        FPolygon2D[] rings = new FPolygon2D[count];
	        for (int i = 0; i < count; i++) {
	            rings[i] = parseLinearRing(data, haveZ, haveM);

	        }
	        GeneralPathX shape=(GeneralPathX)getGeneralPathX(rings);
	        shape.closePath();
			return new FPolygon2D(shape);
	 }
    private FPolyline2D parseMultiLineString(ByteBuffer data) {
        GeneralPathX gp = parseGeneralPath(data);
        // GeneralPathX shape=getGeneralPathX(strings);

        // parseGeometryArray(data, strings);
        return new FPolyline2D(gp);//strings[0]; //new MultiLineString(strings);
    }

	/**
	 * @param data
	 * @return
	 */
	private GeneralPathX parseGeneralPath(ByteBuffer data) {
		int count = data.getInt();
        // FPolyline2D[] strings = new FPolyline2D[count];
        GeneralPathX gp = new GeneralPathX();
        for (int i=0; i < count; i++)
        {
            parseTypeAndSRID(data);
            FPoint2D[] points = parsePointArray(data, gHaveZ, gHaveM);
            // int count2 = data.getInt();
            /* FPoint2D[] result = new FPoint2D[count2];
            for (int i = 0; i < count; i++) {
                result[i] = parsePoint(data, haveZ, haveM);
            }
            return result; */
            /* FPoint2D p = parsePoint(data, gHaveZ, gHaveM);
            gp.moveTo(p.getX(), p.getY());
            for (int j = 1; j < count2; j++) {
                p = parsePoint(data, gHaveZ, gHaveM);
                gp.lineTo(p.getX(), p.getY());
            } */

            gp.moveTo(points[0].getX(), points[0].getY());
            for (int j = 1; j< points.length; j++)
            {
                gp.lineTo(points[j].getX(), points[j].getY());
            }

            // strings[i] = parseLineString(data, gHaveZ, gHaveM);
        }
		return gp;
	}

    private FPolygon2D parseMultiPolygon(ByteBuffer data) {
        int count = data.getInt();
        // FPolygon2D[] polys = new FPolygon2D[count];
        GeneralPathX gp = new GeneralPathX();
        for (int i=0; i < count; i++)
        {
            parseTypeAndSRID(data);
            // polys[i] = parsePolygon(data, gHaveZ, gHaveM);
	        int countRings = data.getInt();
	        // FPolygon2D[] rings = new FPolygon2D[countRings];
	        for (int j = 0; j < countRings; j++) {
	            // rings[j] = parseLinearRing(data, gHaveZ, gHaveM);
	            FPoint2D[] points = parsePointArray(data, gHaveZ, gHaveM);

	            gp.moveTo(points[0].getX(), points[0].getY());
	            for (int k = 1; k< points.length; k++)
	            {
	            	if (k==points.length-1){
	            		gp.closePath();
	            	}else{
	            		gp.lineTo(points[k].getX(), points[k].getY());
	            	}

	            }

	        }
	        // GeneralPathX shape=(GeneralPathX)getGeneralPathX(rings);

        }
		 // GeneralPathX shape=getGeneralPathX(polys);

        return new FPolygon2D(gp);
    	// return new FPolygon2D(parseGeneralPath(data));
    }

    private FGeometryCollection parseCollection(ByteBuffer data) {
        int count = data.getInt();
        IGeometry[] geoms = new IGeometry[count];
        parseGeometryArray(data, geoms);
        return new FGeometryCollection(geoms);
    }
	/**
	 * Devuelve el GeneralPathX compuesto por todos los GeneralPath de todas
	 * las geometrías que componen el elemento.
	 *
	 * @param geometries Lista de geometrías.
	 *
	 * @return GeneralPath completo.
	 */
	private GeneralPathX getGeneralPathX(FShape[] geometries) {
		GeneralPathX shape = new GeneralPathX();

		for (int i = 0; i < geometries.length; i++) {
			FShape shp = (FShape) geometries[i];
			PathIterator theIterator = shp.getPathIterator(null); //, flatness);
			double[] theData = new double[6];
			int theType;
			ArrayList arrayCoords;

			while (!theIterator.isDone()) {
				//while not done
				theType = theIterator.currentSegment(theData);

				//Populate a segment of the new
				// GeneralPathX object.
				//Process the current segment to populate a new
				// segment of the new GeneralPathX object.
				switch (theType) {
					case PathIterator.SEG_MOVETO:
						shape.moveTo(theData[0], theData[1]);

						break;

					case PathIterator.SEG_LINETO:
						shape.lineTo(theData[0], theData[1]);

						break;

					case PathIterator.SEG_QUADTO:
						shape.quadTo(theData[0], theData[1], theData[2],
							theData[3]);

						break;

					case PathIterator.SEG_CUBICTO:
						shape.curveTo(theData[0], theData[1], theData[2],
							theData[3], theData[4], theData[5]);

						break;

					case PathIterator.SEG_CLOSE:

						if (i == (geometries.length - 1)) {
							shape.closePath();
						}

						break;
				} //end switch

				theIterator.next();
			} //end while loop
		}

		return shape;
	}
}