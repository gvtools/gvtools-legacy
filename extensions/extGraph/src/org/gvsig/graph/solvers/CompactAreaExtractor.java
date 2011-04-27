/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
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

// 18/09/2007 fjp
// @author: Fco. José Peñarrubia	fpenarru@gmail.com
package org.gvsig.graph.solvers;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.algorithm.triangulation.paul_chew.Pnt;
import org.gvsig.fmap.algorithm.triangulation.paul_chew.Triangle;
import org.gvsig.fmap.algorithm.triangulation.paul_chew.Triangulation;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.Network;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.topology.triangulation.DelaunayTriangulation;
import com.iver.cit.gvsig.topology.triangulation.Simplex;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author fjp
 * 
 * This class can label nodes with distances and costs to a flag. You will
 * obtain a temp shp layer with fields IdArc, IdEdge, CostOrig, DistOrig,
 * CostEnd, DistEnd, IdFlag
 * 
 * La diferencia con ServiceAreaExtractor es que esta versión escucha al
 * algoritmo Dijkstra, y va montando el shp de líneas conforme va siendo
 * explorada la red. La gran ventaja de hacerlo así es que no dependes del
 * tamaño de la red. Solo recorres los tramos y nodos que exploras, de forma que
 * si limitas el área de servicio a una distancia máxima, la red solo se explora
 * hasta esa distancia / coste.
 * 
 */
public class CompactAreaExtractor implements IDijkstraListener {
	private static String tempDirectoryPath = System
			.getProperty("java.io.tmpdir");

	static FieldDescription[] fieldsPol = new FieldDescription[2];
	static {
		FieldDescription fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COST");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fieldsPol[0] = fieldDesc;
		
		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDFLAG");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fieldsPol[1] = fieldDesc;

	}

	
	private Network net;

	private ShpWriter shpWriterPol;
	private File fTempPol;
	private SHPLayerDefinition layerDefPol;
	
	
	private HashMap<Integer, GvEdge> visitedEdges = new HashMap();

	private File fTemp;

	private SHPLayerDefinition layerDef;

	private int idFlag;

	private ReadableVectorial adapter;

//	private double maxCost;

	private Geometry serviceArea;
	private ArrayList <Geometry> serviceAreaPolygons;

	private double[] costs = null;	

	Triangulation tri;
	DelaunayTriangulation tri2;
	double max_value = 1e38;

	/**
	 * @param net
	 * @throws InitializeWriterException
	 * @throws ReadDriverException
	 * @throws InitializeDriverException
	 */
	public CompactAreaExtractor(Network net) throws BaseException {
		int aux = (int) (Math.random() * 1000);
		
		
		String namePol = "tmpCompactAreaPol" + aux + ".shp";
		fTempPol = new File(tempDirectoryPath + "/" + namePol );
		
		layerDefPol = new SHPLayerDefinition();
		layerDefPol.setFile(fTempPol);
		layerDefPol.setName(namePol);		
		layerDefPol.setFieldsDesc(fieldsPol);
		layerDefPol.setShapeType(FShape.POLYGON);
		
		shpWriterPol = new ShpWriter();
		shpWriterPol.setFile(fTempPol);
		shpWriterPol.initialize(layerDefPol);
		shpWriterPol.preProcess();
		FLyrVect lyr = net.getLayer();
		adapter = lyr.getSource();
		adapter.start();

		
		Triangle initTri =
            new Triangle(new Pnt(-max_value, max_value), 
            		new Pnt(max_value, max_value),
            		new Pnt(0, -max_value));

		tri = new Triangulation(initTri);
        Simplex initTri2 = new Simplex(new com.iver.cit.gvsig.topology.triangulation.Pnt[] {
    	            new com.iver.cit.gvsig.topology.triangulation.Pnt(-max_value, max_value),
    	            new com.iver.cit.gvsig.topology.triangulation.Pnt(max_value, max_value),
    	            new com.iver.cit.gvsig.topology.triangulation.Pnt(0,  -max_value)});

		tri2 = new DelaunayTriangulation(initTri2);
		

	}

	public boolean adjacentEdgeVisited(GvNode fromNode, GvEdge edge) {
		
		return false;
	}

	public boolean minimumCostNodeSelected(GvNode node) {
		Pnt p = new Pnt(node.getX(), node.getY());
		com.iver.cit.gvsig.topology.triangulation.Pnt p2 = new com.iver.cit.gvsig.topology.triangulation.Pnt(node.getX(), node.getY()); 
//		tri.delaunayPlace(p);
		tri2.delaunayPlace(p2);
//		tri2.printStuff();
		return false; // true if we want to stop Dijkstra
	}

	public void writeServiceArea() {
		
		System.out.println("Fin de trayecto. Num. triángulos=" + tri2.size());
		Iterator it = tri2.iterator(); 
		while (it.hasNext())
		{
			Simplex s = (Simplex) it.next();
			try {
				writeSimplex(s);
			} catch (ProcessWriterVisitorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void writePolygon(int idFlag, double maxCost, Geometry jtsGeom) throws ProcessWriterVisitorException {
		Value[] values = new Value[2];
		values[0] = ValueFactory.createValue(maxCost);
		values[1] = ValueFactory.createValue(idFlag);
		
		IGeometry geom = FConverter.jts_to_igeometry(jtsGeom);
		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat, DefaultRowEdited.STATUS_ADDED, idFlag);
		shpWriterPol.process(row);
	}

	private void writeSimplex(Simplex s) throws ProcessWriterVisitorException {
		Value[] values = new Value[2];
		values[0] = ValueFactory.createValue(2.0);
		values[1] = ValueFactory.createValue(1);
		
		GeneralPathX gp = new GeneralPathX();
		Iterator vertexIt = s.iterator();
		com.iver.cit.gvsig.topology.triangulation.Pnt p1 = (com.iver.cit.gvsig.topology.triangulation.Pnt) vertexIt.next();
		com.iver.cit.gvsig.topology.triangulation.Pnt p2 = (com.iver.cit.gvsig.topology.triangulation.Pnt) vertexIt.next();
		com.iver.cit.gvsig.topology.triangulation.Pnt p3 = (com.iver.cit.gvsig.topology.triangulation.Pnt) vertexIt.next();
		gp.moveTo(p1.coord(0), p1.coord(1));
		gp.lineTo(p2.coord(0), p2.coord(1));
		gp.lineTo(p3.coord(0), p3.coord(1));
		gp.lineTo(p1.coord(0), p1.coord(1));
		
		IGeometry geom = ShapeFactory.createPolygon2D(gp);
		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat, DefaultRowEdited.STATUS_ADDED, idFlag);
		shpWriterPol.process(row);
		
	}

	public void closeFiles() throws StopWriterVisitorException, ReadDriverException {
			shpWriterPol.postProcess();
			
			adapter.stop();

		
	}

	
}
