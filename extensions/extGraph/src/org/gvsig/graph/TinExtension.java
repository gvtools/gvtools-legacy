/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
* MA  02110-1301, USA.
* 
*/

/*
* AUTHORS (In addition to CIT):
* 2008 Software Colaborativo (www.scolab.es)   development
*/
 
package org.gvsig.graph;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.algorithm.contouring.ContourCalculator;
import org.gvsig.fmap.algorithm.triangulation.OrbisGisTriangulator;
import org.gvsig.fmap.algorithm.triangulation.PirolTriangulator;
import org.gvsig.fmap.algorithm.triangulation.TIN;
import org.gvsig.fmap.algorithm.triangulation.Triangle;
import org.gvsig.fmap.algorithm.triangulation.Vertex;
import org.gvsig.fmap.algorithm.triangulation.WatsonTriangulator;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.gui.beans.swing.JFileChooser;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.ConcreteMemoryDriver;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.LayersIterator;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.GenericFileFilter;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

public class TinExtension extends Extension {
	
	private static String     lastPath        = null;
	private PluginServices ps=PluginServices.getPluginServices(this);

	private ArrayList<FLyrVect> pointLayers = null;
	
	private GeometryFactory jtsFactory = new GeometryFactory();
	
	public void execute(String actionCommand) {
		for (FLyrVect lyr : pointLayers) {
			try {
				doTIN(lyr);
			} catch (BaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private void doTIN(FLyrVect lyrVect) throws BaseException {
		ReadableVectorial rv = lyrVect.getSource();
		
		JFileChooser fileChooser = new JFileChooser("OPEN_LAYER_FILE_CHOOSER_ID", lastPath);
		fileChooser.setFileFilter(new GenericFileFilter(".shp", "Shape files", true));
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(ps.getText("choose_file_to_save_triangles"));
		int result = fileChooser.showSaveDialog((Component) PluginServices.getMDIManager().getActiveWindow());
		if (result == JFileChooser.CANCEL_OPTION)
			return;
		File triShpFile = fileChooser.getSelectedFile();
		try {
			if (!triShpFile.getCanonicalPath().toLowerCase().endsWith(".shp"))
				triShpFile = new File(triShpFile.getAbsolutePath() + ".shp");
		} catch (IOException e) {
			e.printStackTrace();
			throw new GraphException(e);
		}

//		fileChooser.setDialogTitle(PluginServices.getPluginServices(TinExtension.class)
//				.getText("choose_file_to_save_contours"));
//		result = fileChooser.showSaveDialog((Component) PluginServices.getMDIManager());
//		if (result == JFileChooser.CANCEL_OPTION)
//			return;
//		File contourShpFile = fileChooser.getSelectedFile();
		SelectableDataSource rs = lyrVect.getRecordset();
		String[] selecValues = rs.getFieldNames();
		String strField = (String) JOptionPane.showInputDialog(null, "choose_field", "choose_field", JOptionPane.QUESTION_MESSAGE, null, selecValues, null);
		
		int idField = rs.getFieldIndexByName(strField);
		String aux = JOptionPane.showInputDialog("input_contour_desired_heights_separated_by_commas (;)");
		if (aux == null)
			return;
		double[] heights = NetworkUtils.string2doubleArray(aux, ";");
		Arrays.sort(heights);

		ShpWriter triShpWriter = createShapeTriangles(triShpFile);
//		ShpWriter contourShpWriter = createShapeContours(triShpFile);
		
		long t1 = System.currentTimeMillis();
		WatsonTriangulator triangulator = new WatsonTriangulator(); // [1]
//		ChewTriangulator triangulator = new ChewTriangulator();
//		PirolTriangulator triangulator = new PirolTriangulator();
//		OrbisGisTriangulator triangulator = new OrbisGisTriangulator();
		rv.start();
		for (int i=0; i < rv.getShapeCount(); i++) {
			IFeature feat = rv.getFeature(i);
			IGeometry geom = feat.getGeometry();
			Object shp = geom.getInternalShape();
			if (shp instanceof FPoint2D)
			{
				FPoint2D p = (FPoint2D) geom.getInternalShape();
				NumericValue val = (NumericValue) feat.getAttribute(idField);
				triangulator.addVertex(new Vertex(p.getX(), p.getY(), val.doubleValue()));
			}
			if (shp instanceof FMultiPoint2D)
			{
				FMultiPoint2D multi = (FMultiPoint2D) shp;
				for (int j=0; j < multi.getNumPoints(); j++) {
					FPoint2D p = multi.getPoint(j);
					NumericValue val = (NumericValue) feat.getAttribute(idField);
					triangulator.addVertex(new Vertex(p.getX(), p.getY(), val.doubleValue()));
				}
			}
			
		}
		rv.stop();
		
		TIN tin = triangulator.calculateTriangulation();
		
		
		GeometryFactory geomFact = new GeometryFactory();
		
		View view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapContext mapContext = view.getMapControl().getMapContext();
//		GraphicLayer graphicLayer = view.getMapControl().getMapContext().getGraphicsLayer();
//		FSymbol symbol = new FSymbol(FShape.LINE);
//		int idSym = graphicLayer.addSymbol(symbol);
		
		ConcreteMemoryDriver memDriver = new ConcreteMemoryDriver();
		ArrayList<String> arrayFields = new ArrayList<String>();
		arrayFields.add("ID");
		arrayFields.add("VALUE");

		memDriver.getTableModel().setColumnIdentifiers(arrayFields.toArray());

//		memDriver.getf
		memDriver.setShapeType(FShape.LINE);
		
		int i = 0;
		ContourCalculator contourCalculator = new ContourCalculator(tin);
		for (int indexContour = 0; indexContour < heights.length; indexContour++) {			
			double height = heights[indexContour];
			Collection<LineString> contour = contourCalculator.getContour(height);

			IGeometry geom = null;
			geom = FConverter.jts_to_igeometry(jtsFactory.buildGeometry(contour));
			Value[] row = new Value[2];
			row[0] = ValueFactory.createValue(i++);
			row[1] = ValueFactory.createValue(height);
			memDriver.addGeometry(geom, row);
			
			Polygonizer pol = new Polygonizer();
			pol.add(contour);
			Collection<Polygon> polygons = pol.getPolygons();
			
			for (Polygon p : polygons) {				
				geom = FConverter.jts_to_igeometry(p);
				row = new Value[2];
				row[0] = ValueFactory.createValue(i++);
				row[1] = ValueFactory.createValue(height);
				memDriver.addGeometry(geom, row);
				
	//			FGraphic graf = new FGraphic(geom, idSym);
	//			graphicLayer.addGraphic(graf);
			}
		} // indexContour
		FLayer lyrContour = LayerFactory.createLayer("Contours", memDriver, mapContext.getProjection());
		mapContext.getLayers().addLayer(lyrContour);
		
		Value[] att = new Value[1];
		i=0;
		triShpWriter.preProcess();
		for (Triangle tri: tin.getTriangles()) {
			CoordinateList auxC = new CoordinateList();
			auxC.add(tri.getV1(), true);
			auxC.add(tri.getV2(), true);
			auxC.add(tri.getV3(), true);
			auxC.add(tri.getV1(), true);
			LinearRing ring = geomFact.createLinearRing(auxC.toCoordinateArray());
			IGeometry geom = FConverter.jts_to_igeometry(ring);
//			FGraphic graf = new FGraphic(geom, idSym);
//			graphicLayer.addGraphic(graf);
			att[0] = ValueFactory.createValue(i);
			DefaultFeature feat = new DefaultFeature(geom, att, i + "");

			triShpWriter.process(new DefaultRowEdited(feat, IRowEdited.STATUS_ADDED, i++));
			
			
		}
		triShpWriter.postProcess();
		
		
		VectorialFileDriver shpDriver = new IndexedShpDriver();
		FLyrVect lyrTri = (FLyrVect) LayerFactory.createLayer("Triangles", shpDriver, triShpFile, mapContext.getProjection());
		mapContext.getLayers().addLayer(lyrTri);
		

	}
	
	private ShpWriter createShapeTriangles(File file) throws BaseException {
	
		
		FieldDescription[] fieldsDescrip = new FieldDescription[1];
		
		FieldDescription f1 = new FieldDescription();
		f1.setFieldName("Id");
		f1.setFieldType(Types.INTEGER);
		f1.setFieldLength(8);
		f1.setFieldDecimalCount(0);
		fieldsDescrip[0] = f1;

		SHPLayerDefinition lyrDefPolygon = new SHPLayerDefinition();
		lyrDefPolygon.setFieldsDesc(fieldsDescrip);

		lyrDefPolygon.setFile(file);
		lyrDefPolygon.setName(file.getName());
		lyrDefPolygon.setShapeType(FShape.LINE | FShape.Z);
		ShpWriter writer = new ShpWriter();
		writer.setFile(file);
		writer.initialize(lyrDefPolygon);
		writer.preProcess();
		writer.postProcess();
		return writer;
	}

	private ShpWriter createShapeContours(File file) throws BaseException {
	
		
		FieldDescription[] fieldsDescrip = new FieldDescription[2];
		
		FieldDescription f1 = new FieldDescription();
		f1.setFieldName("Id");
		f1.setFieldType(Types.INTEGER);
		f1.setFieldLength(8);
		f1.setFieldDecimalCount(0);
		fieldsDescrip[0] = f1;

		FieldDescription f5 = new FieldDescription();
		f5.setFieldName("Height");
		f5.setFieldType(Types.DOUBLE);
		f5.setFieldDecimalCount(3);
		f5.setFieldLength(16);
		fieldsDescrip[1] = f5;


	
		SHPLayerDefinition lyrDef = new SHPLayerDefinition();
		lyrDef.setFieldsDesc(fieldsDescrip);

		lyrDef.setFile(file);
		lyrDef.setName(file.getName());
		lyrDef.setShapeType(FShape.LINE);
		ShpWriter writer = new ShpWriter();
		writer.setFile(file);
		writer.initialize(lyrDef);
		writer.preProcess();
		writer.postProcess();
		return writer;
	}


	public void initialize() {
		// TODO Auto-generated method stub

	}

	public boolean isEnabled() {
		IWindow wnd = PluginServices.getMDIManager().getActiveWindow();
		View view = (View) wnd;
		FLayers lyrs = view.getMapControl().getMapContext().getLayers();
		LayersIterator it = new LayersIterator(lyrs) {
			public boolean evaluate(FLayer layer) {
				return layer.isActive();
			}
		};
		pointLayers = new ArrayList<FLyrVect>();
		while (it.hasNext()) {
			FLayer lyr = it.nextLayer();
			if (lyr instanceof FLyrVect) {
				FLyrVect lyrVect = (FLyrVect) lyr;
				try {
					if ((lyrVect.getShapeType() == FShape.POINT) || (lyrVect.getShapeType() == FShape.MULTIPOINT)) {
						pointLayers.add(lyrVect);
					}
				} catch (ReadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (pointLayers.size() >= 1)
			return true;
		return false;
	}

	public boolean isVisible() {
		IWindow wnd = PluginServices.getMDIManager().getActiveWindow();
		if (wnd instanceof View)
			return true;
		return false;
	}

}

