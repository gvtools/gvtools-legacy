/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.rastertools.vectorizacion.process;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.sql.Types;

import org.cresques.cts.ProjectionPool;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.RasterProcess;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.process.RasterTask;
import org.gvsig.raster.process.RasterTaskQueue;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.raster.vectorization.VectorizationBinding;
import org.gvsig.rastertools.vectorizacion.fmap.BezierPathX;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DXFLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfFieldsMapping;
import com.iver.cit.gvsig.fmap.edition.writers.dxf.DxfWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
/**
 * Este proceso vectoriza la capa de entrada que debe estar ya preprocesada.
 * Usa la libreria de potrace por debajo.
 * 
 * @version 15/09/2008
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class PotraceProcess extends RasterProcess {
	private double       percent               = 0;
	private FLyrRasterSE lyr                   = null;
	private String       fileName              = null;
	private IWriter      writer                = null;
	private Value        values[]              = null;
	private int          m_iGeometry           = 0;

	// Default Values
	private int          bezierPoints          = 7;
	private int          policy                = VectorizationBinding.POLICY_MINORITY;
	private int          despeckle             = 0;
	private double       cornerThreshold       = 1.0;
	private double       optimizationTolerance = 0.2;
	private int          outputQuantization    = 10;
	private boolean      curveOptimization     = true;


	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.RasterProcess#init()
	 */
	public void init() {
		lyr = getLayerParam("layer");
		fileName = getStringParam("filename");
		policy = getIntParam("policy");
		bezierPoints = getIntParam("points");
		despeckle = getIntParam("despeckle");
		cornerThreshold = getDoubleParam("cornerThreshold");
		optimizationTolerance = getDoubleParam("optimizationTolerance");
		outputQuantization = getIntParam("outputQuantization");
		curveOptimization = getBooleanParam("curveoptimization");
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.RasterProcess#process()
	 */
	public void process() throws InterruptedException {
		try {
			insertLineLog("Potrace");
			generatePotrace();
		} catch (VisitorException e) {
			RasterToolsUtil.messageBoxError("error_closing_grid", this, e);
		} catch (InitializeWriterException e) {
			RasterToolsUtil.messageBoxError("error_closing_grid", this, e);
		} catch (RasterDriverException e) {
			RasterToolsUtil.messageBoxError("error_closing_grid", this, e);
		} finally {
			if (incrementableTask != null) {
				incrementableTask.processFinalize();
				incrementableTask = null;
			}
		}
		if (externalActions != null)
			externalActions.end(fileName);
	}

	/**
	 * Genera la capa vectorial a partir del raster de entrada
	 * 
	 * @param lyr
	 * @param fileOut
	 * @param bezierPoints
	 * @throws RasterDriverException 
	 * @throws InterruptedException 
	 * @throws VisitorException 
	 * @throws InitializeWriterException 
	 * @throws Exception 
	 */
	public void generatePotrace() throws InterruptedException, RasterDriverException, VisitorException, InitializeWriterException {
		VectorizationBinding binding = new VectorizationBinding(lyr.getBufferFactory());
		binding.setPolicy(policy);
		binding.setDespeckle(despeckle);
		binding.setCornerThreshold(cornerThreshold);
		binding.setOptimizationTolerance(optimizationTolerance);
		binding.setOutputQuantization(outputQuantization);
		binding.setEnabledCurveOptimization(curveOptimization);
		
		// binding.setCornerThreshold(-1);
		double geometrias[] = binding.VectorizeBuffer();

		String sFields[] = new String[2];
		sFields[0] = "ID";
		sFields[1] = fileName + "";

		LayerDefinition tableDef = null;
		if (fileName.endsWith(".dxf")) {
			writer = new DxfWriter();
			((DxfWriter) writer).setFile(new File(fileName));
			ProjectionPool pool = new ProjectionPool();
			((DxfWriter) writer).setProjection(pool.get("EPSG:23030"));
			tableDef = new DXFLayerDefinition();

			DxfFieldsMapping fieldsMapping = new DxfFieldsMapping();
			((DxfWriter) writer).setFieldMapping(fieldsMapping);
		}
		if (fileName.endsWith(".shp")) {
			writer = new ShpWriter();
			((ShpWriter) writer).setFile(new File(fileName));
			tableDef = new SHPLayerDefinition();
		}
		tableDef.setShapeType(FShape.LINE);

		int types[] = { Types.DOUBLE, Types.DOUBLE };
		FieldDescription[] fields = new FieldDescription[sFields.length];
		for (int i = 0; i < fields.length; i++) {
			fields[i] = new FieldDescription();
			fields[i].setFieldName(sFields[i]);
			fields[i].setFieldType(types[i]);
			fields[i].setFieldLength(getDataTypeLength(types[i]));
			fields[i].setFieldDecimalCount(5);
		}
		tableDef.setFieldsDesc(fields);
		tableDef.setName(fileName);

		writer.initialize(tableDef);
		writer.preProcess();

		values = new Value[2];
		values[0] = ValueFactory.createValue(0);
		values[1] = ValueFactory.createValue(0);

		showPotrace(geometrias, bezierPoints);

		writer.postProcess();
	}

	public void addShape(FShape shape, Value[] value) throws VisitorException {
		if (shape == null) 
			return;
		IGeometry geom = ShapeFactory.createGeometry(shape);
		addGeometry(geom, value);
	}
	
	public void addGeometry(IGeometry geom, Value[] value) throws VisitorException {
		DefaultFeature feat = new DefaultFeature(geom, value, Integer.toString(m_iGeometry));
		IRowEdited editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, m_iGeometry);
		m_iGeometry++;
		writer.process(editFeat);
	}
	
	private Point2D getTransformPixel(double x, double y) {
		AffineTransform at = lyr.getAffineTransform();
		Point2D ptSrc = new Point2D.Double(x, lyr.getPxHeight() - y);
		at.transform(ptSrc, ptSrc);
		return ptSrc;
	}
	
	private void showPotrace(double[] potraceX, int trozos) throws InterruptedException, VisitorException {
		RasterTask task = RasterTaskQueue.get(Thread.currentThread().toString());
		BezierPathX pathX = new BezierPathX(trozos);

		double increment = (100 / (double)potraceX.length);
		int cont = 1;
		while (true) {
			if ((cont >= potraceX.length) || (cont >= potraceX[0]))
				return;
			switch ((int) potraceX[cont]) {
				case 0: // MoveTo
					pathX.moveTo(getTransformPixel(potraceX[cont + 1], potraceX[cont + 2]));
					cont += 3;
					percent += (increment * 3);
					break;
				case 1: // LineTo
					pathX.lineTo(getTransformPixel(potraceX[cont + 1], potraceX[cont + 2]));
					cont += 3;
					percent += (increment * 3);
					break;
				case 2: // CurveTo
					pathX.curveTo(getTransformPixel(potraceX[cont + 1], potraceX[cont + 2]), getTransformPixel(potraceX[cont + 3], potraceX[cont + 4]), getTransformPixel(potraceX[cont + 5], potraceX[cont + 6]));
					cont += 7;
					percent += (increment * 7);
					break;
				case 3: // closePath
					FPolyline2D line =  new FPolyline2D(pathX);
					addShape(line, values);
					pathX = new BezierPathX(trozos);
					cont ++;
					percent += increment;
					break;
			}
			if(task.getEvent() != null)
				task.manageEvent(task.getEvent());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getPercent()
	 */
	public int getPercent() {
		return (int) percent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getTitle()
	 */
	public String getTitle() {
		return RasterToolsUtil.getText(this, "vectorization");
	}
	
	/**
	 * Returns the length of field
	 * @param dataType
	 * @return length of field
	 */
	public int getDataTypeLength(int dataType) {
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
}