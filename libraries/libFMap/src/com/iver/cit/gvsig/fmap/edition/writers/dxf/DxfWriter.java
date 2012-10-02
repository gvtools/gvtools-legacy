package com.iver.cit.gvsig.fmap.edition.writers.dxf;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.cresques.geo.Point3D;
import org.cresques.io.DxfFile;
import org.cresques.io.DxfGroup;
import org.cresques.io.DxfGroupVector;
import org.cresques.px.dxf.DxfEntityMaker;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.FArc2D;
import com.iver.cit.gvsig.fmap.core.FCircle2D;
import com.iver.cit.gvsig.fmap.core.FEllipse2D;
import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint3D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class DxfWriter extends AbstractWriter implements ISpatialWriter {

	private String DEFAULT_LAYER="default";
	private Integer DEFAULT_COLOR=new Integer(0);
	private Double DEFAULT_ELEVATION=new Double(0);
	private Double DEFAULT_THICKNESS=new Double(0);
	private String DEFAULT_TEXT="";
	private Double DEFAULT_HEIGHTTEXT=new Double(10);
	private Double DEFAULT_ROTATIONTEXT=new Double(0);

	private DxfFieldsMapping fieldMapping = null;

	private File file;

	private FieldDescription[] fieldsDesc = null;

	private DxfFile.EntityFactory entityMaker;

	private CoordinateReferenceSystem crs = null;

	private DxfFile dxfFile;

	int handle = 40; // Revisar porqué es 40.

	int k = 0;

	boolean dxf3DFile = false;
	private int indexText=-1;//En DXF es 7.
	private int indexHeightText=-1;//En DXF es 8.
	private int indexRotationText=-1;//En DXF es 9.

	public void setFile(File f) {
		file = f;
	}

	public boolean canWriteAttribute(int sqlType) {
		return false;
	}

	public boolean canAlterTable() {
		return false;
	}

	public boolean canWriteGeometry(int gvSIGgeometryType) {
		return true; // I guess all geometries can be here...
	}

	public void initialize(FLayer layer) throws InitializeWriterException {
		try {
			SelectableDataSource sds = ((FLyrVect) layer).getRecordset();
			// Aquí hay que revisar los campos de sds y compararlos con los
			// que podemos escribir. (Layer, Color, etc).
			fieldsDesc = sds.getFieldsDescription();
		} catch (ReadDriverException e) {
			throw new InitializeWriterException(getName(),e);
		}

	}

	/**
	 * Useful to create a layer from scratch Call setFile before using this
	 * function
	 *
	 * @param lyrDef
	 * @throws EditionException
	 */
	public void initialize(ITableDefinition lyrDef) throws InitializeWriterException {
		super.initialize(lyrDef);
		fieldsDesc = lyrDef.getFieldsDesc();
	}
public void preProcess() throws StartWriterVisitorException {
		if (fieldMapping == null) {
			throw new StartWriterVisitorException (getName(),null);
		}
		// NOTA: La proyección no se usa absolutamente para nada (al menos
		// por ahora). Las entidades se escribirán con las coordenadas con
		// las que se crean.
		if (crs == null) {
			throw new StartWriterVisitorException (getName(),null);
		}

		entityMaker = new DxfEntityMaker(crs);
		FieldDescription[] fieldDescriptions=getTableDefinition().getFieldsDesc();
		for (int i=0;i<fieldDescriptions.length;i++){
			if (fieldDescriptions[i].getFieldName().equals("Text")){
				indexText=i;
			}else if (fieldDescriptions[i].getFieldName().equals("HeightText")){
				indexHeightText=i;
			}else if (fieldDescriptions[i].getFieldName().equals("RotationText")){
				indexRotationText=i;
			}
		}
	}

	public void process(IRowEdited row) throws ProcessWriterVisitorException {
		if (row.getStatus() == IRowEdited.STATUS_DELETED) return;

		try
		{
			IFeature feat = (IFeature) row.getLinkedRow();
	    	IGeometry geom = feat.getGeometry();
	    	Value[] values=row.getAttributes();
	    	// TODO: Tratamiento de los campos
	    	// y modificar los createXXX para que acepten como parámetro
	    	// los datos de LAYER, COLOR, ELEVATION, THICKNESS, TEXT
	    	// HEIGHTTEXT, ROTATIONTEXT y el resto que puedan hacer
	    	// falta.


	    	// ////////////////
	        if (geom.getGeometryType()==FShape.POINT) {
	            k=createPoint2D(handle, k, geom, values);
	        } else if (geom.getGeometryType()==(FShape.POINT | FShape.Z)) {
	            dxf3DFile = true;
	            k=createPoint3D(handle, k, geom, values);
	        } else if (geom.getGeometryType()==FShape.LINE) {
	            k=createLwPolyline2D(handle, k, geom, false,values);
	        } else if (geom.getGeometryType()==(FShape.LINE | FShape.Z)) {
	            dxf3DFile = true;
	            k = createPolyline3D(handle, k, geom, values);
	        } else if (geom.getGeometryType()==FShape.POLYGON) {
	            // createPolygon2D(handle, k, geom);
	            k=createLwPolyline2D(handle, k, geom, true,values);
	        } else if (geom.getGeometryType()==(FShape.POLYGON | FShape.Z)) {
	            dxf3DFile = true;
	            k = createPolyline3D(handle, k, geom, values);
	            // k = createPolygon3D(handle, k, geom);
	        } else if (geom.getGeometryType()==FShape.CIRCLE) {
	        	FCircle2D circle = (FCircle2D) geom.getInternalShape();
	        	k=createCircle2D(handle, k, circle, values);
	        } else if (geom.getGeometryType()==FShape.ARC) {
	        	FArc2D arc = (FArc2D) geom.getInternalShape();
	        	k=createArc2D(handle, k, arc, values);
	        } else if (geom.getGeometryType()==FShape.ELLIPSE) {
	        	FEllipse2D ellipse = (FEllipse2D) geom.getInternalShape();
	        	k=createEllipse2D(handle, k, ellipse, values);
	       /* } else if (geom instanceof FGeometryCollection) {
				// System.out.println("Polilínea encontrada (Solución
				// provisional).");
				FGeometryCollection gc = (FGeometryCollection)geom;
				IGeometry[] geoms = gc.getGeometries();
				// double[] lineCoords = new double[6];
				// GeneralPathXIterator polylineIt =
				// geoms[i].getGeneralPathXIterator();
				DxfGroupVector plv = new DxfGroupVector();
				DxfGroup polylineLayer = new DxfGroup(8, "default");
				DxfGroup vNum = new DxfGroup();
				vNum.setCode(90);
				vNum.setData(new Integer(fShapes.length+1));
				plv.add(polylineLayer);
				plv.add(vNum);
				Point2D first = new Point2D.Double();
				Point2D last = new Point2D.Double();
				for (int j=0;j<geoms.length;j++) {
					if (geom.getInternalShape() instanceof FPolyline2D && !(geom.getInternalShape() instanceof FArc2D)) {
						// System.out.println("Línea encontrada dentro de la
						// polilínea.");
						FPolyline2D fLine = (FPolyline2D)geom.getInternalShape();
						double[] lineCoords = new double[6];
						PathIterator lineIt = fLine.getPathIterator(new AffineTransform());
						int k = 0;
						Point2D[] pts = new Point2D[2];
						while (!lineIt.isDone()) {
							int type = lineIt.currentSegment(lineCoords);
							pts[k] = new Point2D.Double(lineCoords[0], lineCoords[1]);
							k++;
							lineIt.next();
						}
						DxfGroup vx = new DxfGroup();
						DxfGroup vy = new DxfGroup();
						vx.setCode(10);
						vx.setData(new Double(pts[0].getX()));
						vy.setCode(20);
						vy.setData(new Double(pts[0].getY()));
						plv.add(vx);
						plv.add(vy);
						if (j==0) {
							first = new Point2D.Double(pts[0].getX(), pts[0].getY());
						}
						if (j==fShapes.length-1) {
							last = new Point2D.Double(pts[1].getX(), pts[1].getY());
							if (first.getX()==last.getX() && first.getY()==last.getY()) {
								// Polilínea cerrada.
							} else {
								DxfGroup vxf = new DxfGroup();
								DxfGroup vyf = new DxfGroup();
								vxf.setCode(10);
								vxf.setData(new Double(pts[1].getX()));
								vyf.setCode(20);
								vyf.setData(new Double(pts[1].getY()));
								plv.add(vxf);
								plv.add(vyf);
							}
						}
					} else if (fShapes[j] instanceof FArc2D) {
						// System.out.println("Arco encontrada dentro de la
						// polilínea.");
						FArc2D fArc = (FArc2D)fShapes[j];
						double[] lineCoords = new double[6];
						Point2D[] pts = new Point2D[3];
						pts[0] = fArc.getInit();
						pts[1] = fArc.getMid();
						pts[2] = fArc.getEnd();
						Point2D center = fArc.getCenter(); // TrigonometricalFunctions.getCenter(pts[0],
															// pts[1], pts[2]);
						// System.out.println("pts[0] = " + pts[0]);
						// System.out.println("pts[1] = " + pts[1]);
						// System.out.println("center = " + center);
						// System.out.println("pts[2] = " + pts[2]);
						double initAngRad = TrigonometricalFunctions.getAngle(center, pts[0]);
						double endAngRad = TrigonometricalFunctions.getAngle(center, pts[2]);
						double angleRad = endAngRad-initAngRad;
						if (angleRad<0) angleRad = angleRad+2*Math.PI;
						//
						// boolean bulgeIsNegative = true;
						double bulge = 0;
						if (TrigonometricalFunctions.isCCW(pts[0], pts[1], pts[2])) {
							double angleRad2 = angleRad/4.0;
							bulge = Math.tan(angleRad2);
						} else {
							angleRad = 2*Math.PI-angleRad;
							double angleRad2 = angleRad/4.0;
							bulge = -1*Math.tan(angleRad2);
						}
						DxfGroup vx = new DxfGroup();
						DxfGroup vy = new DxfGroup();
						DxfGroup vb = new DxfGroup();
						vx.setCode(10);
						vx.setData(new Double(pts[0].getX()));
						vy.setCode(20);
						vy.setData(new Double(pts[0].getY()));
						vb.setCode(42);
						vb.setData(new Double(bulge));
						plv.add(vx);
						plv.add(vy);
						plv.add(vb);
						if (j==0) {
							first = new Point2D.Double(pts[0].getX(), pts[0].getY());
						}
						if (j==fShapes.length-1) {
							last = new Point2D.Double(pts[2].getX(), pts[2].getY());
							if (first.getX()==last.getX() && first.getY()==last.getY()) {
								// Polilínea cerrada.
							} else {
								DxfGroup vxf = new DxfGroup();
								DxfGroup vyf = new DxfGroup();
								vxf.setCode(10);
								vxf.setData(new Double(pts[2].getX()));
								vyf.setCode(20);
								vyf.setData(new Double(pts[2].getY()));
								plv.add(vxf);
								plv.add(vyf);
							}
						}
					}
				} */
	        } else {
	            System.out.println("IGeometry not supported yet");
	            k++;
	        }
		}
		catch(Exception e){
			throw new ProcessWriterVisitorException(getName(),e);
		}


	}
	private int createArc2D(int handle, int k,FArc2D fArc,Value[] values) throws Exception {
		Point2D[] pts = new Point2D[3];
		pts[0] = fArc.getInit();
		pts[1] = fArc.getMid();
		pts[2] = fArc.getEnd();
		Point2D center = fArc.getCenter();
		double radius = center.distance(pts[0]);
		double initAngle = UtilFunctions.getAngle(center, pts[0]);
		initAngle = Math.toDegrees(initAngle);
		// System.out.println("initAngle = " + initAngle);
		double midAngle = UtilFunctions.getAngle(center, pts[1]);
		midAngle = Math.toDegrees(midAngle);
		// System.out.println("midAngle = " + midAngle);
		double endAngle = UtilFunctions.getAngle(center, pts[2]);
		endAngle = Math.toDegrees(endAngle);
		// System.out.println("endAngle = " + endAngle);

		// 050307, jmorell: Resolución de un bug sobre el sentido de
		// los arcos.

		 // if (!UtilFunctions.isCCW(pts[0],pts[1],pts[2])){
		if (!FConverter.isCCW(pts)){
			 double aux=initAngle;
			 initAngle=endAngle;
			 endAngle=aux;
		}


		/*
		 * FArc2D arc = (FArc2D)(shapes[0]); Point2D center = arc.getCenter();
		 * Point2D init = arc.getInit(); Point2D end = arc.getEnd(); // Cálculo
		 * del radio: double radius =
		 * Math.sqrt(Math.pow(init.getX()-center.getX(),2)+Math.pow(init.getY()-center.getY(),2)); //
		 * double initAngle=TrigonometricalFunctions.getAngle(center, init);
		 * initAngle = Math.toDegrees(initAngle); double
		 * endAngle=TrigonometricalFunctions.getAngle(center, end); endAngle =
		 * Math.toDegrees(endAngle);
		 */
//		DxfGroup arcLayer = new DxfGroup(8, "default");
		DxfGroup ax = new DxfGroup();
		DxfGroup ay = new DxfGroup();
		DxfGroup ac = new DxfGroup();
		DxfGroup ai = new DxfGroup();
		DxfGroup ae = new DxfGroup();
		ax.setCode(10);
		ax.setData(new Double(center.getX()));
		ay.setCode(20);
		ay.setData(new Double(center.getY()));
		ac.setCode(40);
		ac.setData(new Double(radius));
		ai.setCode(50);
		ai.setData(new Double(initAngle));
		ae.setCode(51);
		ae.setData(new Double(endAngle));
		DxfGroupVector av = updateProperties(values,k);
//		av.add(arcLayer);
		av.add(ax);
		av.add(ay);
		av.add(ac);
		av.add(ai);
		av.add(ae);
		entityMaker.createArc(av);
		k++;
		return k;
	}

	private int createCircle2D(int handle, int k, FCircle2D geom,Value[] values)
			throws Exception {
		DxfGroupVector polv = updateProperties(values,k);
//		DxfGroup polylineLayer = new DxfGroup(8, "default");
//		polv.add(polylineLayer);
//		DxfGroup handleGroup = new DxfGroup();
//		handleGroup.setCode(5);
//		handleGroup.setData(new Integer(handle + k).toString());
//		polv.add(handleGroup);
		DxfGroup circleFlag = new DxfGroup();
		circleFlag.setCode(100);
		polv.add(circleFlag);

		DxfGroup xvertex = new DxfGroup();
		xvertex.setCode(10);
		xvertex.setData(new Double(geom.getCenter().getX()));
		DxfGroup yvertex = new DxfGroup();
		yvertex.setCode(20);
		yvertex.setData(new Double(geom.getCenter().getY()));
		DxfGroup zvertex = new DxfGroup();
		zvertex.setCode(30);
		zvertex.setData(new Double(0)); // TODO: COORDENADA Z. REVISAR ESTO PARA
										// ENTIDADES 3D

		DxfGroup radius = new DxfGroup();
		radius.setCode(40);
		radius.setData(new Double(geom.getRadio()));

		polv.add(xvertex);
		polv.add(yvertex);
		polv.add(zvertex);
		polv.add(radius);

		entityMaker.createCircle(polv);
		k++;
		return k;
	}

	private int createEllipse2D(int handle, int k,FEllipse2D fElip, Value[] values) throws Exception {
		Point2D center = new Point2D.Double((fElip.getInit().getX() + fElip
				.getEnd().getX()) / 2, (fElip.getInit().getY() + fElip.getEnd()
				.getY()) / 2);
		double mAxisL = fElip.getDist() * 2;
		// System.out.println("mAxisL = " + mAxisL);
		/*
		 * System.out.println("mAxisL/(center.distance(fElip.getEnd()))*2 = " +
		 * mAxisL/(center.distance(fElip.getEnd()))*2); minToMaj.setData(new
		 * Double(mAxisL/()*2));
		 */
		double maAxisL = fElip.getInit().distance(fElip.getEnd());

		Point2D endPointOfMajorAxis = fElip.getEnd();
		double azimut = Math.atan2(endPointOfMajorAxis.getX() - center.getX(),
				endPointOfMajorAxis.getY() - center.getY());
		double azimut2 = azimut + Math.PI / 2.0;
		if (azimut2 >= Math.PI * 2)
			azimut2 = azimut2 - Math.PI * 2;
		Point2D endPointOfMinorAxis = new Point2D.Double(center.getX()
				+ (fElip.getDist() * Math.sin(azimut2)), center.getY()
				+ (fElip.getDist() * Math.cos(azimut2)));

		if (mAxisL >= maAxisL) {
			// El menor debe ser menor que el mayor. Los cambiamos.
			double aux = mAxisL;
			mAxisL = maAxisL;
			maAxisL = aux;
			// También cambiamos los puntos finales de los ejes.
			Point2D pAux = endPointOfMinorAxis;
			endPointOfMinorAxis = endPointOfMajorAxis;
			endPointOfMajorAxis = pAux;
		}
		double mToMAR = mAxisL / maAxisL;
		// System.out.println("mToMar = " + mToMAR);
//		DxfGroup arcLayer = new DxfGroup(8, "default");
		DxfGroup x = new DxfGroup();
		DxfGroup y = new DxfGroup();
		DxfGroup xc = new DxfGroup();
		DxfGroup yc = new DxfGroup();
		DxfGroup minToMaj = new DxfGroup();
		// DxfGroup start = new DxfGroup();
		// DxfGroup end = new DxfGroup();
		x.setCode(10);
		x.setData(new Double(center.getX()));
		y.setCode(20);
		y.setData(new Double(center.getY()));
		xc.setCode(11);
		xc.setData(new Double(endPointOfMajorAxis.getX() - center.getX()));
		yc.setCode(21);
		yc.setData(new Double(endPointOfMajorAxis.getY() - center.getY()));
		minToMaj.setCode(40);
		minToMaj.setData(new Double(mToMAR));
		DxfGroupVector av = updateProperties(values,k);
//		av.add(arcLayer);
		av.add(x);
		av.add(y);
		av.add(xc);
		av.add(yc);
		av.add(minToMaj);
		entityMaker.createEllipse(av);
		k++;
		return k;
	}

	public void postProcess() throws StopWriterVisitorException{
		// Escribimos realmente lo que hemos montado en memoria.
		dxfFile = new DxfFile(null, file.getAbsolutePath(), entityMaker);
		dxfFile.setCadFlag(true);
		if (dxf3DFile)
			dxfFile.setDxf3DFlag(true);
		try {
			dxfFile.save(file.getAbsolutePath());
		} catch (IOException e) {
			throw new StopWriterVisitorException(getName(),e);
		}

	}

	public String getName() {
		return "DXF Writer";
	}

	/**
	 * @return Returns the fieldMapping.
	 */
	public DxfFieldsMapping getFieldMapping() {
		return fieldMapping;
	}

	/**
	 * Use this method BEFORE preProcess.
	 *
	 * @param fieldMapping
	 *            The fieldMapping to set.
	 */
	public void setFieldMapping(DxfFieldsMapping fieldMapping) {
		this.fieldMapping = fieldMapping;
	}

	public void write(IGeometry[] geometries, File file) throws Exception {
		int handle = 40; // Revisar porqué es 40.
		int k = 0;
		boolean dxf3DFile = false;
		for (int i = 0; i < geometries.length; i++) {
			IGeometry geom = geometries[i];
			if (geom.getGeometryType() == FShape.POINT) {
				k=createPoint2D(handle, k, geom, null);
			} else if (geom.getGeometryType() == (FShape.POINT | FShape.Z)) {
				dxf3DFile = true;
				k=createPoint3D(handle, k, geom, null);
			} else if (geom.getGeometryType() == FShape.LINE) {
				k=createLwPolyline2D(handle, k, geom, false, null);
			} else if (geom.getGeometryType() == (FShape.LINE | FShape.Z)) {
				dxf3DFile = true;
				k = createPolyline3D(handle, k, geom, null);
			} else if (geom.getGeometryType() == FShape.POLYGON) {
				// createPolygon2D(handle, k, geom);
				k=createLwPolyline2D(handle, k, geom, true,null);
			} else if (geom.getGeometryType() == (FShape.POLYGON | FShape.Z)) {
				dxf3DFile = true;
				k = createPolygon3D(handle, k, geom, null);
			} else {
				System.out.println("IGeometry not supported yet");
				k++;
			}
		}
		dxfFile = new DxfFile(null, file.getAbsolutePath(), entityMaker);
		dxfFile.setCadFlag(true);
		if (dxf3DFile)
			dxfFile.setDxf3DFlag(true);
		dxfFile.save(file.getAbsolutePath());
	}

	/**
	 * @param handle
	 * @param k
	 * @param geom
	 * @return
	 * @throws Exception
	 */
	private int createPolygon3D(int handle, int k, IGeometry geom, Value[] values)
			throws Exception {
		DxfGroupVector polv = updateProperties(values,k);
//		DxfGroup polylineLayer = new DxfGroup(8, "default");
//		polv.add(polylineLayer);
//		DxfGroup handleGroup = new DxfGroup();
//		handleGroup.setCode(5);
//		handleGroup.setData(new Integer(handle + k).toString());
//		polv.add(handleGroup);
		Vector vpoints = new Vector();
		PathIterator theIterator = geom.getPathIterator(null); // polyLine.getPathIterator(null,
																// flatness);
		double[] theData = new double[6];
		double[] velev = ((FGeometry) geom).getZs();
		while (!theIterator.isDone()) {
			int theType = theIterator.currentSegment(theData);
			switch (theType) {
			case PathIterator.SEG_MOVETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			case PathIterator.SEG_LINETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			}
			theIterator.next();
		}
		if (constantElevation(velev)) {
			DxfGroup polylineFlag = new DxfGroup();
			polylineFlag.setCode(70);
			polylineFlag.setData(new Integer(1));
			polv.add(polylineFlag);
			DxfGroup elevation = new DxfGroup();
			elevation.setCode(38);
			elevation.setData(new Double(velev[0]));
			polv.add(elevation);
			for (int j = 0; j < vpoints.size(); j++) {
				DxfGroup xvertex = new DxfGroup();
				xvertex.setCode(10);
				xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
				DxfGroup yvertex = new DxfGroup();
				yvertex.setCode(20);
				yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
				polv.add(xvertex);
				polv.add(yvertex);
			}
			entityMaker.createLwPolyline(polv);
			k++;
		} else {
			DxfGroup polylineFlag = new DxfGroup();
			polylineFlag.setCode(70);
			polylineFlag.setData(new Integer(9));
			polv.add(polylineFlag);
			DxfGroup xgroup = new DxfGroup();
			xgroup.setCode(10);
			xgroup.setData(new Double(0.0));
			polv.add(xgroup);
			DxfGroup ygroup = new DxfGroup();
			ygroup.setCode(20);
			ygroup.setData(new Double(0.0));
			polv.add(ygroup);
			DxfGroup elevation = new DxfGroup();
			elevation.setCode(30);
			elevation.setData(new Double(0.0));
			polv.add(elevation);
			DxfGroup subclassMarker = new DxfGroup(100, "AcDb3dPolyline");
			polv.add(subclassMarker);
			entityMaker.createPolyline(polv);
			k++;
			for (int j = 0; j < vpoints.size(); j++) {
				DxfGroupVector verv = new DxfGroupVector();
				DxfGroup entityType = new DxfGroup(0, "VERTEX");
				verv.add(entityType);
				DxfGroup generalSubclassMarker = new DxfGroup(100, "AcDbEntity");
				verv.add(generalSubclassMarker);
				DxfGroup layerName = new DxfGroup(8, "default");
				verv.add(layerName);
				DxfGroup vertexSubclassMarker = new DxfGroup(100, "AcDbVertex");
				verv.add(vertexSubclassMarker);
				// DxfGroup vertex3DSubclassMarker = new DxfGroup(100,
				// "AcDb3dPolylineVertex");
				// verv.add(vertex3DSubclassMarker);
				DxfGroup xvertex = new DxfGroup();
				xvertex.setCode(10);
				xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
				DxfGroup yvertex = new DxfGroup();
				yvertex.setCode(20);
				yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
				DxfGroup zvertex = new DxfGroup();
				zvertex.setCode(30);
				zvertex.setData(new Double(velev[j]));
				verv.add(xvertex);
				verv.add(yvertex);
				verv.add(zvertex);
				entityMaker.addVertex(verv);
				k++;
			}
			DxfGroupVector seqv = new DxfGroupVector();
			DxfGroup entityType = new DxfGroup(0, "SEQEND");
			seqv.add(entityType);
			// DxfGroup handle = new DxfGroup();
			// elevation.setCode(5);
			// elevation.setData(new Integer(getHandle()));
			// seqv.add(handle);
			DxfGroup generalSubclassMarker = new DxfGroup(100, "AcDbEntity");
			seqv.add(generalSubclassMarker);
			DxfGroup layerName = new DxfGroup(8, "default");
			seqv.add(layerName);
			DxfGroup handleSeqGroup = new DxfGroup();
			handleSeqGroup.setCode(5);
			handleSeqGroup.setData(new Integer(handle + k).toString());
			seqv.add(handleSeqGroup);
			entityMaker.endSeq();
			k++;
		}
		return k;
	}

	/**
	 * @deprecated
	 * @param handle
	 * @param k
	 * @param geom
	 * @throws Exception
	 */
	private void createPolygon2D(int handle, int k, IGeometry geom,Value[] values)
			throws Exception {
		DxfGroupVector polv = updateProperties(values,k);
//		DxfGroup polylineLayer = new DxfGroup(8, "default");
//		polv.add(polylineLayer);
//		DxfGroup handleGroup = new DxfGroup();
//		handleGroup.setCode(5);
//		handleGroup.setData(new Integer(handle + k).toString());
//		polv.add(handleGroup);
		DxfGroup polylineFlag = new DxfGroup();
		polylineFlag.setCode(70);
		polylineFlag.setData(new Integer(1));
		polv.add(polylineFlag);

		Vector vpoints = new Vector();
		PathIterator theIterator = geom.getPathIterator(null); // polyLine.getPathIterator(null,
																// flatness);
		double[] theData = new double[6];
		while (!theIterator.isDone()) {
			int theType = theIterator.currentSegment(theData);
			switch (theType) {
			case PathIterator.SEG_MOVETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			case PathIterator.SEG_LINETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			}
			theIterator.next();
		}
		for (int j = 0; j < vpoints.size(); j++) {
			DxfGroup xvertex = new DxfGroup();
			xvertex.setCode(10);
			xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
			DxfGroup yvertex = new DxfGroup();
			yvertex.setCode(20);
			yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
			polv.add(xvertex);
			polv.add(yvertex);
		}
		entityMaker.createLwPolyline(polv);
	}

	/**
	 * @param handle
	 * @param k
	 * @param geom
	 * @return
	 * @throws Exception
	 */
	private int createPolyline3D(int handle, int k, IGeometry geom, Value[] values)
			throws Exception {
		DxfGroupVector polv = updateProperties(values,k);
//		DxfGroup polylineLayer = new DxfGroup(8, "default");
//		polv.add(polylineLayer);
//		DxfGroup handleGroup = new DxfGroup();
//		handleGroup.setCode(5);
//		handleGroup.setData(new Integer(handle + k).toString());
//		polv.add(handleGroup);
		Vector vpoints = new Vector();
		PathIterator theIterator = geom.getPathIterator(null, FConverter.FLATNESS); // polyLine.getPathIterator(null,
																// flatness);
		double[] theData = new double[6];
		double[] velev = ((FGeometry) geom).getZs();
		while (!theIterator.isDone()) {
			int theType = theIterator.currentSegment(theData);
			switch (theType) {
			case PathIterator.SEG_MOVETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			case PathIterator.SEG_LINETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			}
			theIterator.next();
		}
		if (constantElevation(velev)) {
			DxfGroup polylineFlag = new DxfGroup();
			polylineFlag.setCode(70);
			polylineFlag.setData(new Integer(0));
			polv.add(polylineFlag);
			DxfGroup elevation = new DxfGroup();
			elevation.setCode(38);
			elevation.setData(new Double(velev[0]));
			polv.add(elevation);
			for (int j = 0; j < vpoints.size(); j++) {
				DxfGroup xvertex = new DxfGroup();
				xvertex.setCode(10);
				xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
				DxfGroup yvertex = new DxfGroup();
				yvertex.setCode(20);
				yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
				polv.add(xvertex);
				polv.add(yvertex);
			}
			entityMaker.createLwPolyline(polv);
			k++;
		} else {
			DxfGroup polylineFlag = new DxfGroup();
			polylineFlag.setCode(70);
			polylineFlag.setData(new Integer(8));
			polv.add(polylineFlag);
			DxfGroup xgroup = new DxfGroup();
			xgroup.setCode(10);
			xgroup.setData(new Double(0.0));
			polv.add(xgroup);
			DxfGroup ygroup = new DxfGroup();
			ygroup.setCode(20);
			ygroup.setData(new Double(0.0));
			polv.add(ygroup);
			DxfGroup elevation = new DxfGroup();
			elevation.setCode(30);
			elevation.setData(new Double(0.0));
			polv.add(elevation);
			DxfGroup subclassMarker = new DxfGroup(100, "AcDb3dPolyline");
			polv.add(subclassMarker);
			entityMaker.createPolyline(polv);
			k++;
			for (int j = 0; j < vpoints.size(); j++) {
				DxfGroupVector verv = new DxfGroupVector();
				DxfGroup entityType = new DxfGroup(0, "VERTEX");
				verv.add(entityType);
				DxfGroup generalSubclassMarker = new DxfGroup(100, "AcDbEntity");
				verv.add(generalSubclassMarker);
				DxfGroup layerName = new DxfGroup(8, "default");
				verv.add(layerName);
				DxfGroup vertexSubclassMarker = new DxfGroup(100, "AcDbVertex");
				verv.add(vertexSubclassMarker);
				// DxfGroup vertex3DSubclassMarker = new DxfGroup(100,
				// "AcDb3dPolylineVertex");
				// verv.add(vertex3DSubclassMarker);
				DxfGroup xvertex = new DxfGroup();
				xvertex.setCode(10);
				xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
				DxfGroup yvertex = new DxfGroup();
				yvertex.setCode(20);
				yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
				DxfGroup zvertex = new DxfGroup();
				zvertex.setCode(30);
				zvertex.setData(new Double(velev[j]));
				verv.add(xvertex);
				verv.add(yvertex);
				verv.add(zvertex);
				entityMaker.addVertex(verv);
				k++;
			}
			DxfGroupVector seqv = new DxfGroupVector();
			DxfGroup entityType = new DxfGroup(0, "SEQEND");
			seqv.add(entityType);
			// DxfGroup handle = new DxfGroup();
			// elevation.setCode(5);
			// elevation.setData(new Integer(getHandle()));
			// seqv.add(handle);
			DxfGroup generalSubclassMarker = new DxfGroup(100, "AcDbEntity");
			seqv.add(generalSubclassMarker);
			DxfGroup layerName = new DxfGroup(8, "default");
			seqv.add(layerName);
			DxfGroup handleSeqGroup = new DxfGroup();
			handleSeqGroup.setCode(5);
			handleSeqGroup.setData(new Integer(handle + k).toString());
			seqv.add(handleSeqGroup);
			entityMaker.endSeq();
			k++;
		}
		return k;
	}

	private DxfGroupVector updateProperties(Value[] values,int k){
		DxfGroupVector polv = new DxfGroupVector();
		String layer=DEFAULT_LAYER;
		Integer color=DEFAULT_COLOR;
//		Double elevation=DEFAULT_ELEVATION;
		Double thickness=DEFAULT_THICKNESS;
		if (fieldsDesc.length > 6) {
			if (fieldsDesc[3].getFieldName().equals("Layer") &&
					!(values[3] instanceof NullValue) &&
					values[3] instanceof StringValue) {
				layer = values[3].toString();
			}
			if (fieldsDesc[4].getFieldName().equals("Color")
					&& !(values[4] instanceof NullValue) &&
					values[4] instanceof IntValue) {
				color = new Integer(((NumericValue) values[4]).intValue());
			}
			// if (!(values[5] instanceof NullValue)){
			// elevation= new Double(((NumericValue)values[5]).doubleValue());
			// }
			if (fieldsDesc[6].getFieldName().equals("Thickness") &&
					!(values[6] instanceof NullValue) &&
					values[6] instanceof DoubleValue) {
				thickness = new Double(((NumericValue) values[6]).doubleValue());
			}
		}
		DxfGroup geometryLayer = new DxfGroup(8, layer);

		DxfGroup handleGroup = new DxfGroup();
		handleGroup.setCode(5);
		handleGroup.setData(new Integer(handle + k).toString());

		DxfGroup handleColor = new DxfGroup();
		handleColor.setCode(62);
		handleColor.setData(color);

//		DxfGroup handleElevation = new DxfGroup();
//		handleElevation.setCode(38);
//		handleElevation.setData(elevation);

		DxfGroup handleThickness = new DxfGroup();
		handleThickness.setCode(39);
		handleThickness.setData(thickness);


		polv.add(geometryLayer);
//		polv.add(handleElevation);
		polv.add(handleGroup);
		polv.add(handleColor);
		return polv;
	}
	/**
	 * @param handle
	 * @param k
	 * @param geom
	 * @throws Exception
	 */
	private int createLwPolyline2D(int handle, int k, IGeometry geom, boolean isPolygon, Value[] values)
			throws Exception {
		boolean first=true;
		DxfGroupVector polv = updateProperties(values,k);
		Vector vpoints = new Vector();

		DxfGroup polylineFlag = new DxfGroup();
		polylineFlag.setCode(70);
		if (isPolygon)
			polylineFlag.setData(new Integer(1)); // cerrada
		else
			polylineFlag.setData(new Integer(0)); // abierta

//		DxfGroup handleColor = new DxfGroup();
//		handleColor.setCode(62);
//		handleColor.setData(color);
//
//		DxfGroup handleElevation = new DxfGroup();
//		handleElevation.setCode(38);
//		handleElevation.setData(elevation);
//
//		DxfGroup handleThickness = new DxfGroup();
//		handleThickness.setCode(39);
//		handleThickness.setData(thickness);

		PathIterator theIterator = geom.getPathIterator(null, FConverter.FLATNESS); // polyLine.getPathIterator(null,
																// flatness);

		double[] theData = new double[6];
		while (!theIterator.isDone()) {
			int theType = theIterator.currentSegment(theData);
			switch (theType) {
			case PathIterator.SEG_MOVETO:
				if (!first)
				{
					for (int j = 0; j < vpoints.size(); j++) {
						DxfGroup xvertex = new DxfGroup();
						xvertex.setCode(10);
						xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
						DxfGroup yvertex = new DxfGroup();
						yvertex.setCode(20);
						yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
						polv.add(xvertex);
						polv.add(yvertex);
					}

					entityMaker.createLwPolyline(polv);
					k++;
					polv = updateProperties(values,k);

				}
				first=false;
				polv.add(polylineFlag);
				vpoints.clear();
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			case PathIterator.SEG_LINETO:
				vpoints.add(new FPoint2D(theData[0], theData[1]));
				break;
			case PathIterator.SEG_QUADTO:
//				vpoints.add(new FPoint2D(theData[0], theData[1]));
//				vpoints.add(new FPoint2D(theData[2], theData[3]));
				break;
			case PathIterator.SEG_CUBICTO:
//				vpoints.add(new FPoint2D(theData[0], theData[1]));
//				vpoints.add(new FPoint2D(theData[2], theData[3]));
//				vpoints.add(new FPoint2D(theData[4], theData[5]));
				break;
			case PathIterator.SEG_CLOSE:
				polylineFlag.setData(new Integer(1)); // cerrada
				break;

			}
			theIterator.next();
		}

		for (int j = 0; j < vpoints.size(); j++) {
			DxfGroup xvertex = new DxfGroup();
			xvertex.setCode(10);
			xvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getX()));
			DxfGroup yvertex = new DxfGroup();
			yvertex.setCode(20);
			yvertex.setData(new Double(((FPoint2D) vpoints.get(j)).getY()));
			polv.add(xvertex);
			polv.add(yvertex);
		}

		entityMaker.createLwPolyline(polv);
		k++;
		return k;
	}

	/**
	 * @param handle
	 * @param k
	 * @param geom
	 * @throws Exception
	 */
	private int createPoint3D(int handle, int k, IGeometry geom, Value[] values)
			throws Exception {
		if (indexText!=-1 && !(values[indexText] instanceof NullValue)){
			return createText3D(handle,k,geom,values);
		}
		FPoint3D point = new FPoint3D(0, 0, 0);
		double[] pointCoords = new double[6];
		PathIterator pointIt = geom.getPathIterator(null);
		while (!pointIt.isDone()) {
			pointIt.currentSegment(pointCoords);
			point = new FPoint3D(pointCoords[0], pointCoords[1], pointCoords[2]);
			pointIt.next();
		}
		Point3D pto = new Point3D(point.getX(), point.getY(), point.getZs()[0]);
//		DxfGroup pointLayer = new DxfGroup(8, "default");
//		DxfGroup handleGroup = new DxfGroup();
//		handleGroup.setCode(5);
//		handleGroup.setData(new Integer(handle + k).toString());
		DxfGroup px = new DxfGroup();
		DxfGroup py = new DxfGroup();
		DxfGroup pz = new DxfGroup();
		px.setCode(10);
		px.setData(new Double(pto.getX()));
		py.setCode(20);
		py.setData(new Double(pto.getY()));
		pz.setCode(30);
		pz.setData(new Double(pto.getZ()));
		double[] velev = ((FGeometry) geom).getZs();
		Double elevation= DEFAULT_ELEVATION;
		elevation = new Double(velev[0]);
		DxfGroup handleElevation = new DxfGroup();
		handleElevation.setCode(38);
		handleElevation.setData(elevation);

		DxfGroupVector pv = updateProperties(values,k);
		pv.add(handleElevation);
//		pv.add(pointLayer);
//		pv.add(handleGroup);
		pv.add(px);
		pv.add(py);
		pv.add(pz);
		entityMaker.createPoint(pv);
		k++;
		return k;
	}

	/**
	 * @param handle
	 * @param k
	 * @param geom
	 * @throws Exception
	 */
	private int createPoint2D(int handle, int k, IGeometry geom, Value[] values)
			throws Exception {

		if (indexText!= -1 && !(values[indexText] instanceof NullValue)){
			return createText2D(handle,k,geom,values);
		}
//		String layer=DEFAULT_LAYER;
//		Integer color=DEFAULT_COLOR;
//		Double elevation=DEFAULT_ELEVATION;
//		Double thickness=DEFAULT_THICKNESS;
//		if (!(values[3] instanceof NullValue)){
//			layer = values[3].toString();
//		}
//		if (!(values[4] instanceof NullValue)){
//			color = new Integer(((NumericValue)values[4]).intValue());
//		}
//		if (!(values[5] instanceof NullValue)){
//			elevation= new Double(((NumericValue)values[5]).doubleValue());
//		}
//		if (!(values[6] instanceof NullValue)){
//			thickness= new Double(((NumericValue)values[6]).doubleValue());
//		}

		FPoint2D point = new FPoint2D(0, 0);
		double[] pointCoords = new double[6];
		PathIterator pointIt = geom.getPathIterator(null);
		while (!pointIt.isDone()) {
			pointIt.currentSegment(pointCoords);
			point = new FPoint2D(pointCoords[0], pointCoords[1]);
			pointIt.next();
		}
		Point2D pto = new Point2D.Double(point.getX(), point.getY());
//		DxfGroup pointLayer = new DxfGroup(8, layer);
//		DxfGroup handleGroup = new DxfGroup();
//		handleGroup.setCode(5);
//		handleGroup.setData(new Integer(handle + k).toString());
//
//		DxfGroup handleColor = new DxfGroup();
//		handleColor.setCode(62);
//		handleColor.setData(color);
//
//		DxfGroup handleElevation = new DxfGroup();
//		handleElevation.setCode(38);
//		handleElevation.setData(elevation);
//
//		DxfGroup handleThickness = new DxfGroup();
//		handleThickness.setCode(39);
//		handleThickness.setData(thickness);

		DxfGroup px = new DxfGroup();
		DxfGroup py = new DxfGroup();
		DxfGroup pz = new DxfGroup();
		px.setCode(10);
		px.setData(new Double(pto.getX()));
		py.setCode(20);
		py.setData(new Double(pto.getY()));
		pz.setCode(30);
		// POINT del DXF tiene cota. Le asigno cero arbitrariamente.
		pz.setData(new Double(0.0));
		DxfGroupVector pv = updateProperties(values,k);
//		pv.add(pointLayer);
//		pv.add(handleGroup);
//		pv.add(handleColor);
//		pv.add(handleElevation);
//		pv.add(handleThickness);
		pv.add(px);
		pv.add(py);
		pv.add(pz);
		entityMaker.createPoint(pv);
		k++;
		return k;
	}
	/**
	 * @param handle
	 * @param k
	 * @param geom
	 * @throws Exception
	 */
	private int createText2D(int handle, int k, IGeometry geom, Value[] values)
			throws Exception {
//		String layer=DEFAULT_LAYER;
//		Integer color=DEFAULT_COLOR;
//		Double elevation=DEFAULT_ELEVATION;
//		Double thickness=DEFAULT_THICKNESS;
		String text=DEFAULT_TEXT;
		Double heightText=DEFAULT_HEIGHTTEXT;
		Double rotationText=DEFAULT_ROTATIONTEXT;

//		if (!(values[3] instanceof NullValue)){
//			layer = values[3].toString();
//		}
//		if (!(values[4] instanceof NullValue)){
//			color = new Integer(((NumericValue)values[4]).intValue());
//		}
//		double[] velev = ((FGeometry) geom).getZs();
//		elevation = new Double(velev[0]);

//		if (!(values[6] instanceof NullValue)){
//			thickness= new Double(((NumericValue)values[6]).doubleValue());
//		}
//		if (fieldsDesc.length > 9) {
			if (indexText!=-1 && !(values[indexText] instanceof NullValue)) {
				text = values[indexText].toString();
			}
			if (indexHeightText!=-1 && !(values[indexHeightText] instanceof NumericValue)) {
				heightText = new Double(((NumericValue) values[indexHeightText])
						.doubleValue());
			}
			if (indexRotationText!=-1 && !(values[indexRotationText] instanceof NumericValue)) {
				rotationText = new Double(((NumericValue) values[indexRotationText])
						.doubleValue());
			}
//		}
		DxfGroup handleText = new DxfGroup();
		handleText.setCode(1);
		handleText.setData(text);

		DxfGroup handleHeightText = new DxfGroup();
		handleHeightText.setCode(40);
		handleHeightText.setData(heightText);

		DxfGroup handleRotationText = new DxfGroup();
		handleRotationText.setCode(50);
		handleRotationText.setData(rotationText);

//		DxfGroup handleColor = new DxfGroup();
//		handleColor.setCode(62);
//		handleColor.setData(color);

//		DxfGroup handleElevation = new DxfGroup();
//		handleElevation.setCode(38);
//		handleElevation.setData(elevation);

//		DxfGroup handleThickness = new DxfGroup();
//		handleThickness.setCode(39);
//		handleThickness.setData(thickness);


		FPoint2D point = new FPoint2D(0, 0);
		double[] pointCoords = new double[6];
		PathIterator pointIt = geom.getPathIterator(null);
		while (!pointIt.isDone()) {
			pointIt.currentSegment(pointCoords);
			point = new FPoint2D(pointCoords[0], pointCoords[1]);
			pointIt.next();
		}
		Point2D pto = new Point2D.Double(point.getX(), point.getY());
//		DxfGroup pointLayer = new DxfGroup(8, layer);
		DxfGroup handleGroup = new DxfGroup();
		handleGroup.setCode(5);
		handleGroup.setData(new Integer(handle + k).toString());
		DxfGroup px = new DxfGroup();
		DxfGroup py = new DxfGroup();
		DxfGroup pz = new DxfGroup();
		px.setCode(10);
		px.setData(new Double(pto.getX()));
		py.setCode(20);
		py.setData(new Double(pto.getY()));
		pz.setCode(30);
		// POINT del DXF tiene cota. Le asigno cero arbitrariamente.
		pz.setData(new Double(0.0));
		DxfGroupVector pv = updateProperties(values,k);
//		pv.add(pointLayer);
//		pv.add(handleColor);
//		pv.add(handleElevation);
//		pv.add(handleThickness);
		pv.add(handleText);
		pv.add(handleHeightText);
		pv.add(handleRotationText);
		pv.add(handleGroup);
		pv.add(px);
		pv.add(py);
		pv.add(pz);
		entityMaker.createText(pv);
		k++;
		return k;
	}
	private int createText3D(int handle, int k, IGeometry geom, Value[] values)
			throws Exception {
		// String layer=DEFAULT_LAYER;
		// Integer color=DEFAULT_COLOR;
		Double elevation = DEFAULT_ELEVATION;
		// Double thickness=DEFAULT_THICKNESS;
		String text = DEFAULT_TEXT;
		Double heightText = DEFAULT_HEIGHTTEXT;
		Double rotationText = DEFAULT_ROTATIONTEXT;

		// if (!(values[3] instanceof NullValue)){
		// layer = values[3].toString();
		// }
		// if (!(values[4] instanceof NullValue)){
		// color = new Integer(((NumericValue)values[4]).intValue());
		// }
		double[] velev = ((FGeometry) geom).getZs();
		elevation = new Double(velev[0]);

		// if (!(values[6] instanceof NullValue)){
		// thickness= new Double(((NumericValue)values[6]).doubleValue());
		// }
		if (indexText!=-1 && !(values[indexText] instanceof NullValue)) {
			text = values[indexText].toString();
		}
		if (indexHeightText!=-1 && values[indexHeightText] instanceof NumericValue) {
			heightText = new Double(((NumericValue) values[indexHeightText]).doubleValue());
		}
		if (indexRotationText!=-1 && values[indexRotationText] instanceof NumericValue) {
			rotationText = new Double(((NumericValue) values[indexRotationText]).doubleValue());
		}

		DxfGroup handleText = new DxfGroup();
		handleText.setCode(1);
		handleText.setData(text);

		DxfGroup handleHeightText = new DxfGroup();
		handleHeightText.setCode(40);
		handleHeightText.setData(heightText);

		DxfGroup handleRotationText = new DxfGroup();
		handleRotationText.setCode(50);
		handleRotationText.setData(rotationText);

		// DxfGroup handleColor = new DxfGroup();
		// handleColor.setCode(62);
		// handleColor.setData(color);

		DxfGroup handleElevation = new DxfGroup();
		handleElevation.setCode(38);
		handleElevation.setData(elevation);

		// DxfGroup handleThickness = new DxfGroup();
		// handleThickness.setCode(39);
		// handleThickness.setData(thickness);

		//FPoint3D point = new FPoint3D(0, 0, 0);
//		double[] pointCoords = new double[6];
		FPoint3D point = (FPoint3D)geom.getInternalShape();
//		while (!pointIt.isDone()) {
//			pointIt.currentSegment(pointCoords);
//			point = new FPoint3D(pointCoords[0], pointCoords[1]);
//			pointIt.next();
//		}
//		Point2D pto = new Point2D.Double(point.getX(), point.getY());
		// DxfGroup pointLayer = new DxfGroup(8, layer);
		DxfGroup handleGroup = new DxfGroup();
		handleGroup.setCode(5);
		handleGroup.setData(new Integer(handle + k).toString());
		DxfGroup px = new DxfGroup();
		DxfGroup py = new DxfGroup();
		DxfGroup pz = new DxfGroup();
		px.setCode(10);
		px.setData(new Double(point.getX()));
		py.setCode(20);
		py.setData(new Double(point.getY()));
		pz.setCode(30);
		pz.setData(new Double(point.getZs()[0]));
		DxfGroupVector pv = updateProperties(values,k);
		// pv.add(pointLayer);
		// pv.add(handleColor);
		pv.add(handleElevation);
		// pv.add(handleThickness);
		pv.add(handleText);
		pv.add(handleHeightText);
		pv.add(handleRotationText);
		pv.add(handleGroup);
		pv.add(px);
		pv.add(py);
		pv.add(pz);
		entityMaker.createText(pv);
		k++;
		return k;
	}
	private boolean constantElevation(double[] velev) {
		boolean constant = true;
		for (int i = 0; i < velev.length; i++) {
			for (int j = 0; j < velev.length; j++) {
				if (j > i) {
					if (velev[i] != velev[j]) {
						constant = false;
						break;
					}
				}
			}
			break;
		}
		return constant;
	}

	/**
	 * @return Returns the proj.
	 */
	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	/**
	 * Util solo para el entity maker. Yo creo que esto no es necesario pero por
	 * ahora lo necesito para que funcione. TODO: Hablar con Luis para que lo
	 * aclare.
	 *
	 * @param crs
	 *            The proj to set.
	 */
	public void setCrs(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

	public boolean canSaveEdits() {
		if (file.canWrite()) return true;
		return false;
	}

}
