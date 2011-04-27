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
* 2008 {{Company}}   {{Task}}
*/
 
package com.iver.cit.gvsig.graphtests;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.algorithm.contouring.LoopRemover;

import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.linemerge.LineSequencer;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

public class TestCreatePolygon extends TestCase {

	FLyrVect lyr;

	protected void setUp() throws Exception {
		super.setUp();
		// Setup de los drivers
		LayerFactory
				.setDriversPath("../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");

		// Setup del factory de DataSources

		IProjection prj = CRSFactory.getCRS("EPSG:23030");
		File shpFile = new File("test_files/contour3.shp");
		lyr = (FLyrVect) LayerFactory.createLayer("Contour", "gvSIG shp driver",
				shpFile, prj);
		
	}

	public void testCalculatePolygon() throws BaseException {

		ReadableVectorial rv = lyr.getSource();
		
		long t1 = System.currentTimeMillis();
		LoopRemover sequencer = new LoopRemover();
		Polygonizer pol = new Polygonizer();
		rv.start();
		for (int i=0; i < rv.getShapeCount(); i++) {
			IFeature feat = rv.getFeature(i);
			IGeometry geom = feat.getGeometry();
			Geometry jtsGeom = geom.toJTSGeometry();
			sequencer.add(jtsGeom);
			pol.add(jtsGeom);
//			if (jtsGeom instanceof LineString)
//			{
//				LineString lineString = (LineString) jtsGeom;
//				graph.addEdge(lineString);
//			}
		}
		rv.stop();
		
//		Iterator it = graph.edgeIterator();
//		while (it.hasNext()) {
//			Edge edge = (Edge) it.next();
//			edge.
//		}
		Collection<Polygon> polygons = pol.getPolygons();
		
		for (Polygon p : polygons) {
			System.out.println(p.toText());
		}
		
		Geometry newJtsGeom = sequencer.getSequencedLineStrings();
		System.out.println(newJtsGeom.toText());
		long t2 = System.currentTimeMillis();
		System.out.println("tiempo:" + (t2-t1));
		
		// assertEquals(dist.doubleValue(), 8887, 0);

		
		


	}
//	public void testCalculateContour() throws BaseException {
//
//		ReadableVectorial rv = lyr.getSource();
//		
//		long t1 = System.currentTimeMillis();
////		WatsonTriangulator triangulator = new WatsonTriangulator();
////		ChewTriangulator triangulator = new ChewTriangulator();
//		FJenettTriangulator triangulator = new FJenettTriangulator();
//		rv.start();
//		for (int i=0; i < rv.getShapeCount(); i++) {
//			IFeature feat = rv.getFeature(i);
//			IGeometry geom = feat.getGeometry();
//			Object shp = geom.getInternalShape();
//			if (shp instanceof FPoint2D)
//			{
//				FPoint2D p = (FPoint2D) geom.getInternalShape();
//				// 	Leer la Z para hacer el contour. Para la trianqulaci�n no hace falta.
//				triangulator.addVertex(new Vertex(p.getX(), p.getY()));
//			}
//			if (shp instanceof FMultiPoint2D)
//			{
//				FMultiPoint2D multi = (FMultiPoint2D) shp;
//				for (int j=0; j < multi.getNumPoints(); j++) {
//					FPoint2D p = multi.getPoint(j);
//					NumericValue val = (NumericValue) feat.getAttribute(0);
//					triangulator.addVertex(new Vertex(p.getX(), p.getY(), val.doubleValue()));
//				}
//			}
//			
//		}
//		rv.stop();
//		
//		TIN tin = triangulator.calculateTriangulation();
//		ContourCalculator contourCalculator = new ContourCalculator(tin);
//		Collection<LineString> contour = contourCalculator.getContour(4.7);
//		System.out.println("Contour = " + contour.toString());
//		
//		Collection<LineString> contour2 = contourCalculator.getContour_Complex_BAD(4.7);
//		System.out.println("Contour2 = " + contour2.toString());
//		
//		List <Triangle> triangles = triangulator.getTriangles();
//		
//		long t2 = System.currentTimeMillis();
//		System.out.println("tiempo:" + (t2-t1) + ". NumTriangles = " + triangles.size());
//		
//		// assertEquals(dist.doubleValue(), 8887, 0);
//
//		
//		
//
//
//	}

}
