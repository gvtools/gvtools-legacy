package org.gvsig.fmap.raster.grid.roi;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;

/**
 * Clase dedicada a la escritura de ROIs en formato en disco (formato shape).
 * 
 * Si las ROIs a escribir incluyen distintos tipos de geometrías la escritura se
 * realizará en distintos shape files, uno para poligonos, otro para polilíneas y
 * otro para puntos.
 * 
 * @author Diego Guerrero (diego.guerrero@uclm.es)
 *
 */
public class VectorialROIsWriter {
	private String 			baseFilename 		= null;
	private IWriter 		pointsWriter 		= null;
	private IWriter 		linesWriter 		= null;
	private IWriter 		polygonsWriter 		= null;
	private CoordinateReferenceSystem crs = null;
	private int 			iPolygon;
	private int 			iPoint;
	private int 			iPolyline;

	
	/**
	 * Constructor.
	 * 
	 * @param baseFilename ruta base para formar los nombres de fichero (ruta/prefijo)
	 * @param projection CRS de las geometrías.
	 */
	public VectorialROIsWriter(String baseFilename,
			CoordinateReferenceSystem crs) {
		this.baseFilename = baseFilename;
		this.crs = crs;
	}

	/** 
	 * Escribe el Array de ROIs pasado como parámetro.
	 * 
	 * @param rois Array de VectorialROI
	 */
	public void write (VectorialROI rois[]){
		boolean monoType = true;
		int geometryType = -1;
		ArrayList geometries = null;
		
		Value values[] = new Value[4];
		
		if (baseFilename.endsWith(".shp"))
			baseFilename = baseFilename.replaceAll(".shp", "");
		
		for (int i = 0; i < rois.length; i++) {
			geometries = rois[i].getGeometries();
			for (Iterator iterator = geometries.iterator(); iterator.hasNext();) {
				if (geometryType < 0)
					geometryType = ((IGeometry) iterator.next()).getGeometryType();
				else
					if (geometryType != ((IGeometry) iterator.next()).getGeometryType()){
						monoType = false;
						break;
					}
			}
		}
		
		if (monoType)
			switch (geometryType){
			case FShape.POLYGON:
				create(baseFilename+".shp", crs, FShape.POLYGON);
				break;
				
			case FShape.POINT:
				create(baseFilename+".shp", crs, FShape.POINT);
				break;
				
			case FShape.LINE:
				create(baseFilename+".shp", crs, FShape.LINE);
				break;
			}
		
		for (int i = 0; i < rois.length; i++) {
			geometries = rois[i].getGeometries();
			values[0] = ValueFactory.createValue(rois[i].getName());
			values[1] = ValueFactory.createValue(rois[i].getColor().getRed());
			values[2] = ValueFactory.createValue(rois[i].getColor().getGreen());
			values[3] = ValueFactory.createValue(rois[i].getColor().getBlue());
			
			
			for (Iterator iterator = geometries.iterator(); iterator.hasNext();) {
				IGeometry geometry = (IGeometry) iterator.next();
				switch (geometry.getGeometryType()) {
				case FShape.POLYGON:
					if (polygonsWriter == null)
						create(baseFilename+"_polygons"+".shp", crs, FShape.POLYGON);
					break;
					
				case FShape.POINT:
					if (pointsWriter == null)
						create(baseFilename+"_points"+".shp", crs, FShape.POINT);
					break;
					
				case FShape.LINE:
					if (linesWriter == null)
						create(baseFilename+"_polylines"+".shp", crs, FShape.LINE);
					break;
		
				default:
					return;
				}
				addFeature(geometry, values);
			}
		}
		try {
			if (polygonsWriter != null)
				polygonsWriter.postProcess();
			if (pointsWriter != null)
				pointsWriter.postProcess();
			if (linesWriter != null)
				linesWriter.postProcess();
		} catch (StopWriterVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addFeature(IGeometry geom, Value[] values) {

		DefaultFeature feat = null;
		IRowEdited editFeat = null;
		try {
			switch (geom.getGeometryType()) {
			case FShape.POLYGON:
				if (polygonsWriter != null){
					feat = new DefaultFeature(geom, values, Integer.toString(iPolygon));
					editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, iPolygon);
					iPolygon++;
					polygonsWriter.process(editFeat);
				}
				break;
				
			case FShape.POINT:
				if (pointsWriter != null){
					feat = new DefaultFeature(geom, values, Integer.toString(iPoint));
					editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, iPoint);
					iPoint++;
					pointsWriter.process(editFeat);
				}
				break;
				
			case FShape.LINE:
				if (linesWriter != null){
					feat = new DefaultFeature(geom, values, Integer.toString(iPolyline));
					editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, iPolyline);
					iPolyline++;
					linesWriter.process(editFeat);
				}
				break;
	
			default:
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void create(String filename, CoordinateReferenceSystem crs, int shapeType) {

		LayerDefinition tableDef;

		iPoint = 0;
		iPolygon = 0;
		iPolyline = 0;
		this.crs = (CoordinateReferenceSystem) crs;
		IWriter writer;
		switch (shapeType) {
			case FShape.POLYGON:
				polygonsWriter = new ShpWriter();
				writer = polygonsWriter;
				break;
				
			case FShape.POINT:
				pointsWriter = new ShpWriter();
				writer = pointsWriter;
				break;
				
			case FShape.LINE:
				linesWriter = new ShpWriter();
				writer = linesWriter;
				break;
	
			default:
				return;
		}

		try {
			((ShpWriter)writer).setFile(new File(filename));
			tableDef = new SHPLayerDefinition();
			tableDef.setShapeType(shapeType);

			tableDef.setFieldsDesc(getFields());
			tableDef.setName(filename);

			writer.initialize(tableDef);
			writer.preProcess();

		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	private FieldDescription[] getFields(){ 
		FieldDescription[] fields = new FieldDescription[4];
		
		fields[0] = new FieldDescription();
		fields[0].setFieldName("name");
		fields[0].setFieldType(Types.VARCHAR);
		fields[0].setFieldLength(20);
		fields[0].setFieldDecimalCount(5);
		
		fields[1] = new FieldDescription();
		fields[1].setFieldName("R");
		fields[1].setFieldType(Types.INTEGER);
		fields[1].setFieldLength(10);
		fields[1].setFieldDecimalCount(5);
		
		fields[2] = new FieldDescription();
		fields[2].setFieldName("G");
		fields[2].setFieldType(Types.INTEGER);
		fields[2].setFieldLength(10);
		fields[2].setFieldDecimalCount(5);
		
		fields[3] = new FieldDescription();
		fields[3].setFieldName("B");
		fields[3].setFieldType(Types.INTEGER);
		fields[3].setFieldLength(10);
		fields[3].setFieldDecimalCount(5);
		
		return fields;
	}
}
