package com.iver.gvsig.measure;

import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class utilized to contain the operations responsible for calculate the area
 * and the perimeter.
 * 
 * 
 * @author Vicente Caballero Navarro
 */
public class Operations {
	private static final int NUM_DECIMALS = 5;

	/**
	 * Returns the perimeter of geometry from geometry's points
	 * 
	 * @param x
	 *            Array of X.
	 * @param y
	 *            Array of Y.
	 * @param vp
	 *            ViewPort
	 * 
	 * @return Perímeter of the geometry.
	 */
	public double getPerimeter(IGeometry geom, ViewPort vp) {
		Geometry geometry = FConverter.java2d_to_jts((FShape) geom
				.getInternalShape());
		return geometry.getLength();
		/*
		 * Double[][] xy=getXY(geom); Double[] x=xy[0]; Double[] y=xy[1]; double
		 * distAll = 0;
		 * 
		 * for (int i = 0; i < (x.length - 1); i++) { double dist = 0;
		 * 
		 * Point2D p = new Point(x[i].intValue(), y[i].intValue());
		 * //vp.toMapPoint(new Point(event.getXs()[i].intValue(),
		 * event.getYs()[i].intValue())); Point2D p2 = new Point(x[i +
		 * 1].intValue(), y[i + 1].intValue()); //vp.toMapPoint(new
		 * Point(event.getXs()[i + 1].intValue(), event.getYs()[i +
		 * 1].intValue())); dist = vp.distanceWorld(p, p2); distAll += dist; }
		 * 
		 * return distAll;
		 */
	}

	/**
	 * Returns the points that configure the geometry from this.
	 * 
	 * @param geometry
	 *            IGeometry
	 * 
	 * @return Array of Array of X and Y.
	 */
	/*
	 * private Double[][] getXY(IGeometry geometry) { ArrayList xs = new
	 * ArrayList(); ArrayList ys = new ArrayList(); double[] theData = new
	 * double[6];
	 * 
	 * //double[] aux = new double[6]; PathIterator theIterator; int theType;
	 * int numParts = 0;
	 * 
	 * // boolean bFirst = true; // int xInt, yInt, antX = -1, antY = -1;
	 * theIterator = geometry.getPathIterator(null,FConverter.flatness); //,
	 * flatness);
	 * 
	 * // int numSegmentsAdded = 0; while (!theIterator.isDone()) { theType =
	 * theIterator.currentSegment(theData);
	 * 
	 * switch (theType) { case PathIterator.SEG_MOVETO: numParts++; xs.add(new
	 * Double(theData[0])); ys.add(new Double(theData[1]));
	 * 
	 * 
	 * break;
	 * 
	 * case PathIterator.SEG_LINETO: xs.add(new Double(theData[0])); ys.add(new
	 * Double(theData[1]));
	 * 
	 * 
	 * break;
	 * 
	 * 
	 * case PathIterator.SEG_CLOSE: xs.add(new Double(theData[0])); ys.add(new
	 * Double(theData[1]));
	 * 
	 * break; } //end switch
	 * 
	 * theIterator.next(); } //end while loop
	 * 
	 * Double[] x = (Double[]) xs.toArray(new Double[0]); Double[] y =
	 * (Double[]) ys.toArray(new Double[0]);
	 * 
	 * return new Double[][] { x, y };
	 * 
	 * }
	 */
	/**
	 * Returns the area of the geometry from the points that configure it.
	 * 
	 * @param x
	 *            Array of X.
	 * @param y
	 *            Array of Y.
	 * 
	 * @return Area of geometry.
	 */
	public double getArea(IGeometry geom) {
		Geometry geometry = (Geometry) FConverter.java2d_to_jts((FShape) geom
				.getInternalShape());
		return geometry.getArea();
		/*
		 * Double[][] xy=getXY(geom); Double[] x=xy[0]; Double[] y=xy[1]; double
		 * elArea; Point2D pPixel; Point2D p = new Point2D.Double();
		 * Point2D.Double pAnt = new Point2D.Double(); elArea = 0.0;
		 * 
		 * Point2D aux = new Point2D.Double(x[x.length - 1].doubleValue(),
		 * y[y.length - 1].doubleValue());
		 * 
		 * for (int pos = 0; pos < (x.length - 1); pos++) { pPixel = new
		 * Point2D.Double(((Double) x[pos]).doubleValue(), ((Double)
		 * y[pos]).doubleValue()); p = pPixel; if (pos == 0) { pAnt.x =
		 * aux.getX(); pAnt.y = aux.getY(); }
		 * 
		 * elArea = elArea + ((pAnt.x - p.getX()) * (pAnt.y + p.getY()));
		 * pAnt.setLocation(p); }
		 * 
		 * elArea = elArea + ((pAnt.x - aux.getX()) * (pAnt.y + aux.getY()));
		 * elArea = Math.abs(elArea / 2.0);
		 * 
		 * return elArea;
		 */
	}

	/**
	 * It builds a LayerDefinition from a LyrVect
	 * 
	 * @param datasource
	 * @param shapeType
	 * @return
	 * @throws DriverException
	 * @throws DriverException
	 * @throws com.iver.cit.gvsig.fmap.DriverException
	 * @throws com.hardcode.gdbms.engine.data.driver.DriverException
	 */
	public static SHPLayerDefinition createLayerDefinition(FLyrVect layer)
			throws com.hardcode.gdbms.engine.data.driver.DriverException {
		SHPLayerDefinition solution = new SHPLayerDefinition();
		solution.setName(layer.getName());
		try {
			solution.setShapeType(layer.getShapeType());
			SelectableDataSource datasource = layer.getRecordset();
			int numFields = datasource.getFieldCount();
			FieldDescription[] fields = new FieldDescription[numFields];
			FieldDescription fieldDesc = null;
			for (int i = 0; i < numFields; i++) {
				fieldDesc = new FieldDescription();
				fieldDesc.setFieldName(datasource.getFieldName(i));
				int fieldType = datasource.getFieldType(i);
				fieldDesc.setFieldType(fieldType);
				int fieldLength = getDataTypeLength(fieldType);
				fieldDesc.setFieldLength(fieldLength);
				fieldDesc.setFieldDecimalCount(NUM_DECIMALS);
				fields[i] = fieldDesc;
			}
			solution.setFieldsDesc(fields);
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		return solution;
	}

	/**
	 * Returns the length of field
	 * 
	 * @param dataType
	 * @return length of field
	 */
	public static int getDataTypeLength(int dataType) {
		switch (dataType) {
		case Types.NUMERIC:
		case Types.DOUBLE:
		case Types.REAL:
		case Types.FLOAT:
		case Types.BIGINT:
		case Types.INTEGER:
		case Types.DECIMAL:
			return 20;
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return 254;
		case Types.DATE:
			return 8;
		case Types.BOOLEAN:
		case Types.BIT:
			return 1;
		}
		return 0;
	}

	/**
	 * Returns the array of FieldDescription with two new fields, X and Y.
	 * 
	 * @param lyrDef
	 * @return New Array of FieldDescription
	 * @throws DriverException
	 * @throws com.hardcode.gdbms.engine.data.driver.DriverException
	 */
	public FieldDescription[] getXYFields(SHPLayerDefinition lyrDef)
			throws com.hardcode.gdbms.engine.data.driver.DriverException {

		int type = Types.DOUBLE;
		FieldDescription[] fD = lyrDef.getFieldsDesc();
		FieldDescription fDX = new FieldDescription();
		fDX.setFieldDecimalCount(NUM_DECIMALS);
		fDX.setFieldLength(getDataTypeLength(type));
		fDX.setFieldName("X");
		fDX.setFieldType(type);

		FieldDescription fDY = new FieldDescription();
		fDY.setFieldDecimalCount(NUM_DECIMALS);
		fDY.setFieldLength(getDataTypeLength(type));
		fDY.setFieldName("Y");
		fDY.setFieldType(type);

		FieldDescription[] newfD = new FieldDescription[fD.length + 2];

		for (int i = 0; i < fD.length; i++) {
			newfD[i] = fD[i];
		}
		newfD[newfD.length - 2] = fDX;
		newfD[newfD.length - 1] = fDY;
		return newfD;
	}

	/**
	 * Returns the Array of FieldDescription with a new FieldDescription AREA.
	 * 
	 * @param fD
	 * @return New Array of FieldDescription.
	 */
	public FieldDescription[] getAreaFields(FieldDescription[] fD) {
		int type = Types.DOUBLE;

		FieldDescription fDArea = new FieldDescription();
		fDArea.setFieldDecimalCount(NUM_DECIMALS);
		fDArea.setFieldLength(getDataTypeLength(type));
		fDArea.setFieldName("AREA");
		fDArea.setFieldType(type);

		FieldDescription[] newfD = new FieldDescription[fD.length + 1];

		for (int i = 0; i < fD.length; i++) {
			newfD[i] = fD[i];
		}
		newfD[fD.length] = fDArea;
		return newfD;
	}

	/**
	 * Returns the Array of FieldDescription with a new FieldDescription
	 * PERIMETER.
	 * 
	 * @param fD
	 * @return New Array of FieldDescription
	 */
	public FieldDescription[] getPerimeterFields(FieldDescription[] fD) {
		int type = Types.DOUBLE;
		FieldDescription fDPerimeter = new FieldDescription();
		fDPerimeter.setFieldDecimalCount(NUM_DECIMALS);
		fDPerimeter.setFieldLength(getDataTypeLength(type));
		fDPerimeter.setFieldName("PERIMETER");
		fDPerimeter.setFieldType(type);

		FieldDescription[] newfD = new FieldDescription[fD.length + 1];

		for (int i = 0; i < fD.length; i++) {
			newfD[i] = fD[i];
		}
		newfD[fD.length] = fDPerimeter;
		return newfD;
	}

}
