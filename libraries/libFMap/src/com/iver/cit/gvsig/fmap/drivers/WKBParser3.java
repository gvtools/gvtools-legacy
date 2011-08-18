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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FGeometryCollection;
import com.iver.cit.gvsig.fmap.core.FGeometryM;
import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FMultipoint3D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2DM;
import com.iver.cit.gvsig.fmap.core.FPoint3D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolygon2DM;
import com.iver.cit.gvsig.fmap.core.FPolygon3D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2DM;
import com.iver.cit.gvsig.fmap.core.FPolyline3D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.FShapeM;
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
// jomarlla
// Read 3D and build 3D geometries using gvSIG objects.
// XY, XYZ and XYM supported.
// MultiPoint2DM is not supported by gvSIG.
// When gvSIg edit a polygon it makes the polygon 2d, so the update
// operations will just write 2d geometries even though the postgis
// driver is ready for writing 3D geometries.

public class WKBParser3 {

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

	protected int parseTypeAndSRID(ByteBuffer data) {
		byte endian = data.get(); // skip and test endian flag
		if (endian == 1) {
			data.order(ByteOrder.LITTLE_ENDIAN);
		}
		int typeword = data.getInt();

		int realtype = typeword & 0x1FFFFFFF; // cut off high flag bits

		gHaveZ = (typeword & 0x80000000) != 0;
		gHaveM = (typeword & 0x40000000) != 0;
		gHaveS = (typeword & 0x20000000) != 0;

		// not used
		int srid = -1;

		if (gHaveS) {
			srid = data.getInt();
		}

		return realtype;

	}

	/** Parse a geometry starting at offset. */
	protected IGeometry parseGeometry(ByteBuffer data) {
		int realtype = parseTypeAndSRID(data);

		IGeometry result1;
		switch (realtype) {
		case WKBConstants.wkbPoint:
			result1 = createGeometry(parsePoint(data, gHaveZ, gHaveM));
			break;
		case WKBConstants.wkbLineString:
			result1 = createGeometry(parseLineString(data, gHaveZ, gHaveM));
			break;
		case WKBConstants.wkbPolygon:
			result1 = createGeometry(parsePolygon(data, gHaveZ, gHaveM));
			break;
		case WKBConstants.wkbMultiPoint:
			result1 = parseMultiPoint(data);
			break;
		case WKBConstants.wkbMultiLineString:
			result1 = createGeometry(parseMultiLineString(data));
			return result1;
		case WKBConstants.wkbMultiPolygon:
			result1 = createGeometry(parseMultiPolygon(data));
			break;
		case WKBConstants.wkbGeometryCollection:
			result1 = parseCollection(data);
			break;
		default:
			throw new IllegalArgumentException("Unknown Geometry Type!");
		}

		/*
		 * Geometry result = result1;
		 * 
		 * if (haveS) { result.setSrid(srid); }
		 */
		return result1;
	}

	private FPoint2D parsePoint(ByteBuffer data, boolean haveZ, boolean haveM) {
		double X = data.getDouble();
		double Y = data.getDouble();
		FPoint2D result = new FPoint2D(X, Y);

		if ((haveZ) && (haveM)) {
			// TODO: SUPPORT ZM (POR AHORA, LA Z TIENE PREFERENCIA
			double Z = data.getDouble();
			result = new FPoint3D(X, Y, Z);
			double M = data.getDouble();
			// TODO: create future FPoint3DM
			
			return result;
			
		}
		if (haveM) {
			double m = data.getDouble();
			result = new FPoint2DM(X, Y, m);
		}

		if (haveZ) {
			double Z = data.getDouble();
			result = new FPoint3D(X, Y, Z);
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
	private FPoint2D[] parsePointArray(ByteBuffer data, boolean haveZ,
			boolean haveM) {
		int count = data.getInt();
		FPoint2D[] result = new FPoint2D[count];
		for (int i = 0; i < count; i++) {
			result[i] = parsePoint(data, haveZ, haveM);
		}
		return result;
	}

	private FMultiPoint2D parseMultiPoint(ByteBuffer data) {
		FPoint2D[] points = new FPoint2D[data.getInt()];

		double zs[] = null;
		if (gHaveZ)
			zs = new double[points.length];

		for (int i = 0; i < points.length; i++) {
			parseTypeAndSRID(data);
			points[i] = parsePoint(data, gHaveZ, gHaveM);

			if (gHaveZ)
				zs[i] = ((FPoint3D) points[i]).getZ();
		}

		if (gHaveZ)
			return new FMultipoint3D(points, zs);

		return new FMultiPoint2D(points);
	}

	private FPolyline2D parseLineString(ByteBuffer data, boolean haveZ,
			boolean haveM) {
		FPoint2D[] points = parsePointArray(data, haveZ, haveM);
		GeneralPathX gp = new GeneralPathX();

		List<Double> d3 = null;

		int nDims = 2;
		if (gHaveZ || gHaveM)
			nDims = 3;

		if (nDims == 3)
			d3 = new ArrayList<Double>();

		for (int i = 0; i < points.length; i++) {
			// parent has 3 dimensions
			if (nDims == 3) {
				if (gHaveZ)
					d3.add(((FPoint3D) points[i]).getZ());
				else if (gHaveM)
					d3.add(((FPoint2DM) points[i]).getM());
				else
					d3.add(0.0); // child does not have 3 dimensions
			}
			if (i == 0)
				gp.moveTo(points[i].getX(), points[i].getY());
			else
				gp.lineTo(points[i].getX(), points[i].getY());
		}

		if (nDims == 3) {
			double ad3[] = new double[d3.size()];
			for (int i = 0; i < d3.size(); i++) {
				ad3[i] = ((Double) d3.get(i)).doubleValue();
			}

			if (gHaveZ)
				return new FPolyline3D(gp, ad3);
			else
				return new FPolyline2DM(gp, ad3);
		}
		return new FPolyline2D(gp);
	}

	private FShape parsePolygon(ByteBuffer data, boolean haveZ, boolean haveM) {
		GeneralPathX gp = new GeneralPathX();

		List<Double> d3 = null;

		int nDims = 2;
		if (gHaveZ || gHaveM)
			nDims = 3;

		if (nDims == 3)
			d3 = new ArrayList<Double>();

		int countRings = data.getInt();
		for (int j = 0; j < countRings; j++) {
			FPoint2D[] points = parsePointArray(data, gHaveZ, gHaveM);
			for (int k = 0; k < points.length; k++) {
				if (k == points.length - 1) {
					gp.closePath();
					if (nDims == 3) {
						if (gHaveZ)
							d3.add(((FPoint3D) points[0]).getZ());
						else if (gHaveM)
							d3.add(((FPoint2DM) points[0]).getM());
						else
							d3.add(0.0); // child does not have 3 dimensions
					}					
				} else {
					// parent has 3 dimensions
					if (nDims == 3) {
						if (gHaveZ)
							d3.add(((FPoint3D) points[k]).getZ());
						else if (gHaveM)
							d3.add(((FPoint2DM) points[k]).getM());
						else
							d3.add(0.0); // child does not have 3 dimensions
					}
					if (k == 0)
						gp.moveTo(points[k].getX(), points[k].getY());
					else
						gp.lineTo(points[k].getX(), points[k].getY());
				}
			}

		}

		if (nDims == 3) {
			double ad3[] = new double[d3.size()];
			for (int i = 0; i < d3.size(); i++) {
				ad3[i] = ((Double) d3.get(i)).doubleValue();
			}

			if (gHaveZ)
				return new FPolygon3D(gp, ad3);
			else
				return new FPolygon2DM(gp, ad3);
		}
		return new FPolygon2D(gp);
	}

	private FPolyline2D parseMultiLineString(ByteBuffer data) {
		int count = data.getInt();
		GeneralPathX gp = new GeneralPathX();

		List<Double> d3 = null;

		int nDims = 2;
		if (gHaveZ || gHaveM)
			nDims = 3;

		if (nDims == 3)
			d3 = new ArrayList<Double>();

		for (int i = 0; i < count; i++) {
			parseTypeAndSRID(data);
			FPoint2D[] points = parsePointArray(data, gHaveZ, gHaveM);

			for (int j = 0; j < points.length; j++) {
				// parent has 3 dimensions
				if (nDims == 3) {
					if (gHaveZ)
						d3.add(((FPoint3D) points[j]).getZ());
					else if (gHaveM)
						d3.add(((FPoint2DM) points[j]).getM());
					else
						d3.add(0.0); // child does not have 3 dimensions
				}
				if (j == 0)
					gp.moveTo(points[j].getX(), points[j].getY());
				else
					gp.lineTo(points[j].getX(), points[j].getY());
			}

		}

		if (nDims == 3) {
			double ad3[] = new double[d3.size()];
			for (int i = 0; i < d3.size(); i++) {
				ad3[i] = ((Double) d3.get(i)).doubleValue();
			}

			if (gHaveZ)
				return new FPolyline3D(gp, ad3);
			else
				return new FPolyline2DM(gp, ad3);
		}
		return new FPolyline2D(gp);
	}

	private FGeometry createGeometry(FShape shp) {
		if (shp instanceof FShapeM)
			return new FGeometryM((FShapeM) shp);
		return ShapeFactory.createGeometry(shp);
	}

	// Its FShape and not FPolygon2D because FPolygon2DM returns a FPolyline
	private FShape parseMultiPolygon(ByteBuffer data) {
		// it was expected not to find polygons with different srid or
		// coordiante dimension as subelements of the multipolygon
		// PostGIS avoid this behaviour, but OGC says it is allow.

		int count = data.getInt();
		GeneralPathX gp = new GeneralPathX();

		List<Double> d3 = null;

		int nDims = 2;
		if (gHaveZ || gHaveM)
			nDims = 3;

		if (nDims == 3)
			d3 = new ArrayList<Double>();

		for (int i = 0; i < count; i++) {
			parseTypeAndSRID(data);
			int countRings = data.getInt();
			for (int j = 0; j < countRings; j++) {
				FPoint2D[] points = parsePointArray(data, gHaveZ, gHaveM);

				for (int k = 0; k < points.length; k++) {
					if (k == points.length - 1) {
						gp.closePath();
						if (nDims == 3) {
							if (gHaveZ)
								d3.add(((FPoint3D) points[0]).getZ());
							else if (gHaveM)
								d3.add(((FPoint2DM) points[0]).getM());
							else
								d3.add(0.0); // child does not have 3 dimensions
						}											
					} else {
						// parent has 3 dimensions
						if (nDims == 3) {
							if (gHaveZ)
								d3.add(((FPoint3D) points[k]).getZ());
							else if (gHaveM)
								d3.add(((FPoint2DM) points[k]).getM());
							else
								d3.add(0.0); // child does not have 3 dimensions
						}
						if (k == 0)
							gp.moveTo(points[k].getX(), points[k].getY());
						else
							gp.lineTo(points[k].getX(), points[k].getY());
					}
				}

			}

		}

		if (nDims == 3) {
			double ad3[] = new double[d3.size()];
			for (int i = 0; i < d3.size(); i++) {
				ad3[i] = ((Double) d3.get(i)).doubleValue();
			}

			if (gHaveZ)
				return new FPolygon3D(gp, ad3);
			else
				return new FPolygon2DM(gp, ad3);
		}
		return new FPolygon2D(gp);

	}

	private FGeometryCollection parseCollection(ByteBuffer data) {
		int count = data.getInt();
		IGeometry[] geoms = new IGeometry[count];
		parseGeometryArray(data, geoms);
		return new FGeometryCollection(geoms);
	}

}