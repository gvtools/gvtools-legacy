package org.cresques.cts;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class ProjectionUtils {
	public static CoordinateReferenceSystem getCRS(String code) {
		try {
			return CRS.decode(code);
		} catch (FactoryException e) {
			// TODO geotools refactoring: throw the exception and manage it
			// outside this method
			throw new RuntimeException("Cannot get CRS: " + code, e);
		}
	}

	public static MathTransform getCrsTransform(CoordinateReferenceSystem src,
			CoordinateReferenceSystem target) {
		try {
			return CRS.findMathTransform(src, target);
		} catch (FactoryException e) {
			// TODO geotools refactoring: throw the exception and manage it
			// outside this method
			throw new RuntimeException("Cannot get transform from "
					+ src.getName().getCode() + " to "
					+ target.getName().getCode(), e);
		}
	}

	public static Rectangle2D transform(Rectangle2D source,
			MathTransform transform) {
		try {
			double[] srcCoords = new double[] { source.getMinX(),
					source.getMinY(), source.getMaxX(), source.getMaxY() };
			double[] targetCoords = new double[srcCoords.length];
			transform.transform(srcCoords, 0, targetCoords, 0,
					srcCoords.length / 2);
			return new Rectangle2D.Double(targetCoords[0], targetCoords[1],
					targetCoords[2], targetCoords[3]);
		} catch (TransformException e) {
			// TODO geotools refactoring: throw the exception and manage it
			// outside this method
			throw new RuntimeException("Cannot transform rectangle: " + source
					+ " with transform " + transform, e);
		}
	}

	public static Point2D transform(Point2D source, MathTransform transform) {
		try {
			double[] srcCoords = new double[] { source.getX(), source.getY() };
			double[] targetCoords = new double[srcCoords.length];
			transform.transform(srcCoords, 0, targetCoords, 0,
					srcCoords.length / 2);
			return new Point2D.Double(targetCoords[0], targetCoords[1]);
		} catch (TransformException e) {
			// TODO geotools refactoring: throw the exception and manage it
			// outside this method
			throw new RuntimeException("Cannot transform point: " + source
					+ " with transform " + transform, e);
		}
	}

	public static CoordinateReferenceSystem parseWKT(String wkt) {
		try {
			return CRS.parseWKT(wkt);
		} catch (FactoryException e) {
			// TODO geotools refactoring: throw the exception and manage it
			// outside this method
			throw new RuntimeException("Cannot parse wkt: " + wkt, e);
		}
	}

	public static Rectangle2D getExtent(CoordinateReferenceSystem crs,
			Rectangle2D extent, double scale, double wImage, double hImage,
			double mapUnits, double distanceUnits, double dpi) {
		// TODO geotools refactoring: what should this method do? "Inherited"
		// from deleted IProjection
		return extent;
	}

	public static double getScale(CoordinateReferenceSystem crs, double minX,
			double maxX, double width, double dpi) {
		// TODO geotools refactoring: what should this method do? "Inherited"
		// from deleted IProjection
		return 1;
	}

	public static String getAbrev(CoordinateReferenceSystem crs) {
		// TODO geotools refactoring: is this method correct?
		return CRS.toSRS(crs);
	}

}
