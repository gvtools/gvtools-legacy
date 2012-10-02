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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.algorithm.contouring.ContourCalculator;
import org.gvsig.fmap.algorithm.triangulation.OrbisGisTriangulator;
import org.gvsig.fmap.algorithm.triangulation.TIN;
import org.gvsig.fmap.algorithm.triangulation.Triangle;
import org.gvsig.fmap.algorithm.triangulation.Triangulator;
import org.gvsig.fmap.algorithm.triangulation.Vertex;
import org.gvsig.fmap.algorithm.triangulation.WatsonTriangulator;
import org.gvsig.graph.core.EdgePair;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

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
public class ServiceAreaExtractor2 implements IDijkstraListener {
	private static String tempDirectoryPath = System
			.getProperty("java.io.tmpdir");

	static FieldDescription[] fields = new FieldDescription[7];
	static {
		FieldDescription fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDARC");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[0] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDEDGE");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[1] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COSTORIG");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[2] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("DISTORIG");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[3] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COSTEND");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[4] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("DISTEND");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[5] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDFLAG");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[6] = fieldDesc;

	}
	
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
	
	static GeometryFactory gf = new GeometryFactory();

	private class VisitedEdge {
		private GvEdge edge;
		private double percentcost;
		public VisitedEdge(GvEdge edge) {
			this.edge = edge;
			IGraph g = net.getGraph();
			GvNode nOrig = g.getNodeByID(edge.getIdNodeOrig());
			double maxCost = costs[costs .length-1];
			double costCalculated = nOrig.getBestCost() + edge.getWeight();
			
			if (costCalculated < maxCost)
				percentcost = 1.0;
			else
			{
				double percentCostCalculated = (maxCost - nOrig.getBestCost())/ edge.getWeight();
				percentcost = percentCostCalculated;
			}
			
		}
		public GvEdge getEdge() {
			return edge;
		}
		public double getPercentcost() {
			return percentcost;
		}
		public void setPercentCost(double d) {
			this.percentcost = d;
			
		}
	}

	private Network net;

	private ShpWriter shpWriter;
	private ShpWriter shpWriterPol;
//	private ShpWriter shpWriterTri;
	private File fTempPol;
	private File fTempTri;
	private SHPLayerDefinition layerDefPol;
	private SHPLayerDefinition layerDefTri;
	
	
	private HashMap<String, VisitedEdge> visitedEdges = new HashMap();

	private File fTemp;

	private SHPLayerDefinition layerDef;

	private int idFlag;

	private ReadableVectorial adapter;

//	private double maxCost;

	private Geometry serviceArea;
	private ArrayList <Geometry> serviceAreaPolygons;
	
//	private Hashtable<Integer, EdgePair> smallSegments = new Hashtable();

	private double[] costs = null;	
	
	private boolean bDoCompactArea = false;
	
	private HashSet<Coordinate> borderCoords = new HashSet<Coordinate>();

//	private HashSet<Coordinate> nodes;
//	DelaunayFast tri2;
//	DelaunayWatson tri2;
//	PirolTriangulator triangulator = new PirolTriangulator();
//	Triangulator triangulator = new WatsonTriangulator();
	Triangulator triangulator = new OrbisGisTriangulator();

	private TIN tin;

	private Rectangle2D.Double fullExtent = null;

	private double[] distances;
	/**
	 * @param net
	 * @throws InitializeWriterException
	 * @throws ReadDriverException
	 * @throws InitializeDriverException
	 */
	public ServiceAreaExtractor2(Network net) throws BaseException {
		this.net = net;
		int aux = (int) (Math.random() * 1000);
		
//		nodes = new HashSet<Coordinate>();
		
		
		String nameLine = "tmpServiceAreaLine" + aux + ".shp";
		String namePol = "tmpServiceAreaPol" + aux + ".shp";
		String nameTri = "tmpTri" + aux + ".shp";
		fTemp = new File(tempDirectoryPath + "/" + nameLine );
		fTempPol = new File(tempDirectoryPath + "/" + namePol );
		fTempTri = new File(tempDirectoryPath + "/" + nameTri );
		
		layerDef = new SHPLayerDefinition();
		layerDef.setFile(fTemp);
		layerDef.setName(nameLine);		
		layerDef.setFieldsDesc(fields);
		layerDef.setShapeType(FShape.LINE);

		layerDefPol = new SHPLayerDefinition();
		layerDefPol.setFile(fTempPol);
		layerDefPol.setName(namePol);		
		layerDefPol.setFieldsDesc(fieldsPol);
		layerDefPol.setShapeType(FShape.POLYGON);

		layerDefTri = new SHPLayerDefinition();
		layerDefTri.setFile(fTempTri);
		layerDefTri.setName(nameTri);		
		layerDefTri.setFieldsDesc(fieldsPol);
		layerDefTri.setShapeType(FShape.POLYGON);
		
		shpWriter = new ShpWriter();
		shpWriter.setFile(fTemp);
		shpWriter.initialize(layerDef);

		shpWriterPol = new ShpWriter();
		shpWriterPol.setFile(fTempPol);
		shpWriterPol.initialize(layerDefPol);
		
//		shpWriterTri = new ShpWriter();
//		shpWriterTri.setFile(fTempTri);
//		shpWriterTri.initialize(layerDefTri);
//		shpWriterTri.preProcess();
		
		shpWriter.preProcess();
		shpWriterPol.preProcess();
		
		
		FLyrVect lyr = net.getLayer();
		adapter = lyr.getSource();
		adapter.start();
		
		serviceAreaPolygons = new ArrayList<Geometry>();

	}

	/**
	 * Devuelve el índice del intervalo más alto que contiene a ese valor.
	 * @param bestCost
	 * @param costs
	 * @return
	 */
	private int getCostInterval(double bestCost, double[] costs) {
		int ret = 0;
		if (bestCost > costs[costs.length-1])
			return -1;
		for (int i=costs.length-1; i>=0; i--) {
			if (bestCost > costs[i])
			{
				ret = i+1;
				break;
			}
		}
		return ret;
	}

	/**
	 * We process each edge and prepare a list of polygons, classified by
	 * cost
	 * @param edge
	 * @param nodeOrig
	 * @param nodeEnd
	 * @param geom
	 * @param costs
	 * @throws ProcessWriterVisitorException 
	 */
	private void processEdgeForPolygon(GvEdge edge, GvNode nodeOrig, GvNode nodeEnd, IGeometry geom, double[] costs) throws ProcessWriterVisitorException {
		if (nodeEnd.getBestCost() > nodeOrig.getBestCost())
		{		
			// miramos en qué polígono cae ese edge POR COMPLETO 
			// El coste de su punto final es menor que uno de los costes.
			double costAux = nodeOrig.getBestCost() + edge.getWeight();
			int indexInterval = getCostInterval(costAux, costs);
			// Un polígono por cada zona
			Geometry jtsGeom = geom.toJTSGeometry();
			if (indexInterval != -1)
			{
				for (int i=costs.length-1; i >= indexInterval; i--) {
					calculateConvexHull(jtsGeom, i);
				}
				writeTotalEdge(edge.getIdArc(), geom, edge, nodeOrig, nodeEnd, idFlag);
			}
			double maxCost = costs[costs.length-1];
			// Es -1 si caso límite externo
			if (indexInterval < costs.length-1)
			{
				// Caso límite externo
				if ((costAux > maxCost) &&						
						(nodeOrig.getBestCost() < maxCost))
				{
					double pct = (maxCost - nodeOrig.getBestCost())/ edge.getWeight();
					if (edge.getDirec() == 0) // Sentido inverso
						pct = 1-pct;
					
					LineString partial = null;
					if (nodeOrig.getBestCost() == 0) {
						// Caso particular: empezamos en un tramo y terminamos en ese mismo tramo
						if (edge.getDirec() == 0) // ponemos el pct como estaba. En el nuevo getPartialString ya lo tiene en cuenta
							pct = 1-pct;

						GvFlag f = net.getFlags()[idFlag];
						double pct1 = f.getPct();
						double Ltotal = jtsGeom.getLength();
						double Lb = pct * edge.getDistance();
						double pct2 = Lb / Ltotal; 
						partial = NetworkUtils.getPartialLineString(jtsGeom, pct1, pct2, edge.getDirec());
						writePartialEdge2(edge.getIdArc(), partial, edge, nodeOrig, nodeEnd, idFlag, maxCost);
					}
					else {
						partial = NetworkUtils.getPartialLineString(jtsGeom, pct, edge.getDirec());
						writePartialEdge(edge.getIdArc(), partial, edge, nodeOrig, nodeEnd, idFlag, maxCost);	
					}					
					calculateConvexHull(partial, costs.length-1);
					return;
				}
				// Parcial interno
				maxCost = costs[indexInterval+1];
				if ((nodeOrig.getBestCost() < maxCost) &&
						(costAux > maxCost)) 
				{
					// A ese tramo hemos llegado parcialmente
					 
					double pct = (maxCost - nodeOrig.getBestCost())/ edge.getWeight();
					try {
						LineString partial = NetworkUtils.getPartialLineString(jtsGeom, pct, edge.getDirec());
						writePartialEdge(edge.getIdArc(), partial, edge, nodeOrig, nodeEnd, idFlag, maxCost);	
						
						calculateConvexHull(partial, indexInterval+1);							
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
			}
		} 

		
	}

	/**
	 * @param jtsGeom
	 * @param i
	 */
	private void calculateConvexHull(Geometry jtsGeom, int i) {
		if (serviceAreaPolygons.size() <= i) { // se crea por primera vez
			Geometry gIni = jtsGeom.convexHull(); 
			serviceAreaPolygons.add(i, gIni);
		}
		else
		{
			Geometry antG = serviceAreaPolygons.get(i);
			if (antG == null)
				antG = jtsGeom.convexHull();
			else
			{
				antG = antG.union(jtsGeom.convexHull());				
			}
			antG = antG.convexHull();
			serviceAreaPolygons.set(i, antG);
		}
	}


	private void writePartialEdge(int i, LineString partial, GvEdge edge, GvNode nodeOrig, GvNode nodeEnd, int idFlag, double maxCost) throws ProcessWriterVisitorException {
//		Geometry jtsGeom = geom.toJTSGeometry();
		double pct = (maxCost - nodeOrig.getBestCost())/ edge.getWeight();
		if (edge.getDirec() == 0) // Sentido inverso
			pct = 1-pct;
//		LineString partial = NetworkUtils.getPartialLineString(jtsGeom, pct, edge.getDirec());
//		if (serviceArea == null)
//			serviceArea = partial;
//		else
//		{
//			serviceArea = serviceArea.union(partial);
//			serviceArea = serviceArea.convexHull();
//		}
		
		IGeometry newGeom = FConverter.jts_to_igeometry(partial);

		Value[] values = new Value[7];
		values[0] = ValueFactory.createValue(i);
		values[1] = ValueFactory.createValue(edge.getIdEdge());
		values[2] = ValueFactory.createValue(nodeOrig.getBestCost());
		values[3] = ValueFactory.createValue(nodeOrig.getAccumulatedLength());
		values[4] = ValueFactory.createValue(maxCost);
		values[5] = ValueFactory.createValue(nodeOrig.getAccumulatedLength() + edge.getDistance()*pct);
		values[6] = ValueFactory.createValue(idFlag);
		DefaultFeature feat = new DefaultFeature(newGeom, values);
		IRowEdited row = new DefaultRowEdited(feat, DefaultRowEdited.STATUS_ADDED, i);
		shpWriter.process(row);
		
//		// TODO: TROZO DEL RESTO
//		int direc = 0;
//		pct = pct+0.02;
//		if (edge.getDirec() == 0)
//		{
//			direc = 1;
//			pct = pct - 0.04;
//		}
//		LineString partial2 = NetworkUtils.getPartialLineString(jtsGeom, pct, direc);		
//		IGeometry newGeom2 = FConverter.jts_to_igeometry(partial2);
//		values[6] = ValueFactory.createValue(-2);
//		DefaultFeature feat2 = new DefaultFeature(newGeom2, values);
//		IRowEdited row2 = new DefaultRowEdited(feat2, DefaultRowEdited.STATUS_ADDED, i);
//		shpWriter.process(row2);

		// before
		addUniqueNode(nodeOrig.getX(), nodeOrig.getY(), nodeOrig.getBestCost());
		// ON
		Coordinate cAux = null;
		if (edge.getDirec() == 0) // Sentido inverso
			cAux = partial.getCoordinateN(0);
		else
			cAux = partial.getCoordinateN(partial.getNumPoints()-1);
		addUniqueNode(cAux.x, cAux.y, maxCost);
		
		// after
		addUniqueNode(nodeEnd.getX(), nodeEnd.getY(), nodeEnd.getBestCost());
		
//		if (bDoCompactArea) {
//			Coordinate cLimit = null;
//			if (edge.getDirec() == 0) // Sentido inverso
//				cLimit = partial.getCoordinateN(0);
//			else
//				cLimit = partial.getCoordinateN(partial.getNumPoints()-1);
//			processCompact(cLimit.x, cLimit.y);
//		}
		
	}

	private void writePartialEdge2(int i, LineString jtsGeom, GvEdge edge, GvNode nodeOrig, GvNode nodeEnd, int idFlag, double maxCost) throws ProcessWriterVisitorException {
		
		double pct = (maxCost - nodeOrig.getBestCost())/ edge.getWeight();
		if (edge.getDirec() == 0) // Sentido inverso
			pct = 1-pct;

		IGeometry newGeom = FConverter.jts_to_igeometry(jtsGeom);

		Value[] values = new Value[7];
		values[0] = ValueFactory.createValue(i);
		values[1] = ValueFactory.createValue(edge.getIdEdge());
		values[2] = ValueFactory.createValue(nodeOrig.getBestCost());
		values[3] = ValueFactory.createValue(nodeOrig.getAccumulatedLength());
		values[4] = ValueFactory.createValue(maxCost);
		values[5] = ValueFactory.createValue(nodeOrig.getAccumulatedLength() + edge.getDistance()*pct);
		values[6] = ValueFactory.createValue(idFlag);
		DefaultFeature feat = new DefaultFeature(newGeom, values);
		IRowEdited row = new DefaultRowEdited(feat, DefaultRowEdited.STATUS_ADDED, i);
		shpWriter.process(row);
		

		// before
		addUniqueNode(nodeOrig.getX(), nodeOrig.getY(), nodeOrig.getBestCost());
		// ON
		Coordinate cAux = null;
		if (edge.getDirec() == 0) // Sentido inverso
			cAux = jtsGeom.getCoordinateN(0);
		else
			cAux = jtsGeom.getCoordinateN(jtsGeom.getNumPoints()-1);
		addUniqueNode(cAux.x, cAux.y, maxCost);
		
		// after
		addUniqueNode(nodeEnd.getX(), nodeEnd.getY(), nodeEnd.getBestCost());
		
		
	}

	private void addUniqueNode(double x, double y, double z) {
		Coordinate c = new Coordinate(x,y,z);
		if (!borderCoords.contains(c)) {
			borderCoords.add(c);
			if (borderCoords.size() == 2) {
				Iterator<Coordinate> it = borderCoords.iterator();
				int i=0;
				Coordinate cAuxF1 = null;
				Coordinate cAuxF2 = null;
				while (it.hasNext()) {
					if (i==0)
						cAuxF1 = it.next();
					else
						cAuxF2 = it.next();
					i++;
				}
				fullExtent = new Rectangle2D.Double();
				fullExtent.setFrameFromDiagonal(cAuxF1.x,cAuxF1.y, cAuxF2.x, cAuxF2.y);
			}
			else
				if (fullExtent != null)
					fullExtent.add(new Point2D.Double(x,y));
		}
	}
	
	/**
	 * FIXME: CHANGE THE NAME OF THIS METHOD. Now, it writes al nodes with cost < maxCost
	 * @return
	 * @throws BaseException
	 */
	public FLyrVect getBorderPoints() throws BaseException {
		File fTempPoints = new File(tempDirectoryPath + "/borderPoints.shp");
		
		FieldDescription[] fieldsPoints = new FieldDescription[1];
		FieldDescription fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COST");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fieldsPoints[0] = fieldDesc;

		SHPLayerDefinition layerDef = new SHPLayerDefinition();
		layerDef.setFile(fTempPoints);
		layerDef.setName("BorderPoints");		
		layerDef.setFieldsDesc(fieldsPoints);
		layerDef.setShapeType(FShape.POINT);

		
		ShpWriter shpWriter = new ShpWriter();
		shpWriter.setFile(fTempPoints);
		shpWriter.initialize(layerDef);

		int i=0;
		shpWriter.preProcess();
		for (Iterator it = borderCoords.iterator(); it.hasNext();) {
			Coordinate c = (Coordinate) it.next();
			Value[] values = new Value[1];			
			values[0] = ValueFactory.createValue(c.z);
			IGeometry geom = ShapeFactory.createPoint2D(c.x, c.y);
			DefaultFeature feat = new DefaultFeature(geom, values);
			IRowEdited row = new DefaultRowEdited(feat,
					DefaultRowEdited.STATUS_ADDED, i++);
			shpWriter.process(row);
			
		}
		shpWriter.postProcess();
		
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer(layerDef.getName(), "gvSIG shp driver", 
				layerDef.getFile(), null);
		return lyr;

		
	}
	
	public void reset() {
		borderCoords.clear();
		visitedEdges.clear();
		serviceAreaPolygons.clear();
		triangulator = new OrbisGisTriangulator();
		tin = new TIN();
		fullExtent = null;
	}

//	private void processCompact(LineString partial) {
//		Coordinate cIni = partial.getCoordinateN(0);
//		Coordinate cEnd = partial.getCoordinateN(partial.getNumPoints()-1);
////		System.out.println("PARTIAL c1=" + cIni + " cEnd=" + cEnd);
//		processCompact(cIni.x, cIni.y);
//		processCompact(cEnd.x, cEnd.y);
//	}

	private void writeTotalEdge(int i, IGeometry geom, GvEdge edge,
			GvNode nodeOrig, GvNode nodeEnd, int idFlag)
			throws ProcessWriterVisitorException {
		
		Geometry jtsGeom = geom.toJTSGeometry();
		if (serviceArea == null)
			serviceArea = jtsGeom;
		else
		{
			serviceArea = serviceArea.union(jtsGeom);
			serviceArea = serviceArea.convexHull();
		}

		
		Value[] values = new Value[7];
		values[0] = ValueFactory.createValue(i);
		values[1] = ValueFactory.createValue(edge.getIdEdge());
		values[2] = ValueFactory.createValue(nodeOrig.getBestCost());
		values[3] = ValueFactory.createValue(nodeOrig.getAccumulatedLength());
		values[4] = ValueFactory.createValue(nodeOrig.getBestCost() + edge.getWeight());
		values[5] = ValueFactory.createValue(nodeOrig.getAccumulatedLength() + edge.getDistance());
		values[6] = ValueFactory.createValue(idFlag);
		
		if (bDoCompactArea) {
			// This code is necessary ONLY if you want to triangulate also with interior points
			// (To draw a 3D view, for example.
			// TODO: Use borderPoints in triangulation instead of "nodes"
			// Origin
			addUniqueNode(nodeOrig.getX(), nodeOrig.getY(), nodeOrig.getBestCost());
			
			// end
			addUniqueNode(nodeEnd.getX(), nodeEnd.getY(), nodeEnd.getBestCost());
			
//			System.out.println(" c1=" + cIni + " cEnd=" + cEnd);
//			processCompact(nodeOrig.getX(), nodeOrig.getY());
//			processCompact(nodeEnd.getX(), nodeEnd.getY());
		}


		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat,
				DefaultRowEdited.STATUS_ADDED, i);
		shpWriter.process(row);
	}

//	private void processCompact(double x, double y) {
////		FPoint2D p = new FPoint2D(x,y);
//		Coordinate c = new Coordinate(x,y);
////		System.out.println("PARTIAL c1=" + cIni + " cEnd=" + cEnd);
//		if (!nodes.contains(c))
//			nodes.add(c);
//		else
//		{
//			System.out.print("Nodo ya contenido");
//		}
//		
//	}

	public boolean adjacentEdgeVisited(GvNode fromNode, GvEdge edge) {
		insertVisitedEdge(edge);
		
		return false;
	}

	/**
	 * Si el coste mínimo del edge > costemax, salimos del método.
	 * Miramos si edge está ya en la lista.
	 * Si no está, lo añadimos.
	 * Si está, hay que mirar el porcentaje recorrido sobre ese tramo.
	 * Casos posibles:
	 * EdgeA al 100 % => No se añade este.
	 * EdgeA.percentCost < 1.0. 
	 * 		Comprobamos el percent de el nuevo. Si entre los dos suman > 1.0
	 * 		marcamos el antiguo al 1.0 para que se escriba el tramo completo.
	 * 
	 * 		Si no suman 1.0, hay que añadir este nuevo Edge, con el porcentaje
	 * 		correspondiente.
	 * @param edge
	 */
	private void insertVisitedEdge(GvEdge edge) {
		IGraph g = net.getGraph();
		GvNode n1 = g.getNodeByID(edge.getIdNodeOrig());
		GvNode n2 = g.getNodeByID(edge.getIdNodeEnd());
		double maxCost = costs[costs .length-1]*1.2;
		if (Math.min(n1.getBestCost(), n2.getBestCost()) > maxCost)
			return; // edge outside service area.
		
		String key = "" + edge.getIdArc();
		if (!visitedEdges.containsKey(key))	
		{
			// En el constructor calculamos el porcentaje recorrido y lo guardamos
			visitedEdges.put(key, new VisitedEdge(edge));
//			System.out.println("idEdge adjacent= " + edge.getIdEdge());
		}
		else
		{
			// Recuperamos el visiteEdge que habíamos metido y miramos sus porcentajes
			// recorridos. Si entre los dos porcentajes NO suman más de uno, quiere decir
			// que ahí se queda un tramo central al que no llegamos ni desde un lado ni
			// desde el otro. Por eso guardamos cada trocito al que hemos llegado por separado, 
			// uno con la clave idArc y otro con la clave IdArc_.
			VisitedEdge edgeAnt = visitedEdges.get(key);
//			GvEdge savedEdge = edgeAnt.getEdge();
			if (edgeAnt.getPercentcost() == 1.0)
				return; // Ya está completo, no añadimos nada.
			
			double percentCostCalculated = (maxCost - n1.getBestCost())/ edge.getWeight();
			if ((percentCostCalculated + edgeAnt.getPercentcost()) >= 1.0)
				edgeAnt.setPercentCost(1.0);
			else
			{
				visitedEdges.put(key + "_", new VisitedEdge(edge));
			}

		}
	}

	public boolean minimumCostNodeSelected(GvNode node) {
//		IGraph g = net.getGraph();
//		int idEdge = node.getFromLink();
//		if (idEdge == -1) 
//			return false;
//		GvEdge edge = g.getEdgeByID(idEdge);
//		insertVisitedEdge(edge);
		return false; // true if we want to stop Dijkstra
	}

	public void setIdFlag(int idFlag) {
		this.idFlag = idFlag;
	}
	
	/**
	 * Write edges and polygons associated with active flag and costs
	 * @param costs
	 * @throws BaseException
	 */
	public void writeServiceArea() throws BaseException {
		Set<Map.Entry<String, VisitedEdge>> keySet = visitedEdges.entrySet();
		
		GvEdge edge;
		IGraph g = net.getGraph();
//		Integer idEdge;
		double maxCost = costs[costs .length-1];
		serviceAreaPolygons = new ArrayList<Geometry>(costs.length);
		for (int i=0; i < costs.length-1; i++)
			serviceAreaPolygons.add(null);
		
		for (Map.Entry<String, VisitedEdge> entry : keySet) {
//			idEdge = entry.getKey();
			VisitedEdge visitedEdge = entry.getValue(); 
			edge = visitedEdge.getEdge();
			GvNode nodeEnd = g.getNodeByID(edge.getIdNodeEnd());
			GvNode nodeOrig = g.getNodeByID(edge.getIdNodeOrig());
			IGeometry geom;
			try {
				geom = adapter.getShape(edge.getIdArc());
				FLyrVect lyr = net.getLayer();
			    if (lyr.getCrsTransform() != null) {
					if (!lyr.getCrs()
							.getName()
							.equals(lyr.getMapContext().getViewPort().getCrs()
									.getName())) {
			    		geom.reProject(lyr.getCrsTransform());
			    	}
			    }			
				
				processEdgeForPolygon(edge, nodeOrig, nodeEnd, geom, costs);
//				double costAux = nodeOrig.getBestCost() + edge.getWeight();
////				if (nodeEnd.getBestCost() > nodeOrig.getBestCost())
//				{
//					if (visitedEdge.getPercentcost() == 1.0) {
//						// A ese tramo hemos llegado por completo
//						// Recuperamos su distancia y etiquetamos.
//						writeTotalEdge(edge.getIdArc(), geom, edge, nodeOrig, nodeEnd, idFlag);	
//					}
//					else
//					{
//						// El CASO EN EL QUE HAS LLEGADO POR LOS
//						// 2 LADOS PERO HAY UN TRAMO INALCANZABLE ENMEDIO SE
//						// SOLUCIONA EN writePartialEdge
//						if (nodeOrig.getBestCost() < maxCost) { // FIXME: Creo que este if no tiene sentido.
//							// A ese tramo hemos llegado parcialmente 
//							// Recuperamos su distancia y etiquetamos.
//							writePartialEdge(edge.getIdArc(), geom, edge, nodeOrig, nodeEnd, idFlag, maxCost);	
//							
//						}
//					} // else
//				} // if nodeEnd > nodeOrig
			
			} catch (BaseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		} // for
		if (bDoCompactArea) {
			addExteriorPoints();
			calculateTriangulation();
			getBorderPoints();
		}
		for (int j=serviceAreaPolygons.size()-1; j>=0; j--) {
			Geometry jtsGeom = null;
			if (bDoCompactArea) {
				ContourCalculator contourCalculator = new ContourCalculator(tin);
				Polygonizer pol = new Polygonizer();
				pol.add(contourCalculator.getContour(costs[j]+0.1));
				Collection<Polygon> polygons = pol.getPolygons();
				Iterator<Polygon> it = polygons.iterator();
				double maxArea = 0;
				double area;
				while (it.hasNext()) {
					Polygon auxPol = it.next();
					area = auxPol.getArea(); 
					if ( area > maxArea) {						
						jtsGeom = auxPol;
						maxArea = area;
					} // if
				} // while
				// TODO: Optimizar este polígono. Quitar bucles y comprobar smallSegments,
				// insideLines y outsideLines
				
			}
			else
			{
				jtsGeom = serviceAreaPolygons.get(j);
			}
			if (jtsGeom != null)
				writePolygon(idFlag, costs[j], jtsGeom);
		}

		
	}

	/**
	 * We add points from fullExtent to allow better contour calculations 
	 */
	private void addExteriorPoints() {
		int numSteps = 30; // por ejemplo, 30 puntos por cada lado del rectángulo
		double stepX = fullExtent.width / numSteps;
		double stepY = fullExtent.height / numSteps;
		for (int i=0; i < numSteps; i++) {
			double x = fullExtent.getMinX() + stepX*i;
			double y = fullExtent.getMinY() + stepY*i;
			
			Coordinate cUp = new Coordinate(x, fullExtent.getMaxY() + 10.0, Double.MAX_VALUE);
			Coordinate cDown = new Coordinate(x, fullExtent.getMinY() - 10.0, Double.MAX_VALUE);
			Coordinate cLeft = new Coordinate(fullExtent.getMinX() - 10.0, y, Double.MAX_VALUE);
			Coordinate cRight = new Coordinate(fullExtent.getMaxX() + 10.0, y, Double.MAX_VALUE);
			
			borderCoords.add(cUp);
			borderCoords.add(cDown);
			borderCoords.add(cLeft);
			borderCoords.add(cRight);
		}
		
		
	}

	/**
	 * @param d 
	 * 
	 */
	private void calculateTriangulation() {
		
		int numPoints = borderCoords.size();
	    Iterator it = borderCoords.iterator();
	    for (int i=0; i<numPoints; i++) {
	    	Coordinate node = (Coordinate) it.next();
    		Vertex v = new Vertex(node.x, node.y, node.z);
    		triangulator.addVertex(v);
	    }

	    tin = triangulator.calculateTriangulation();
		System.out.println("Fin de trayecto. Num. triángulos=" + tin.getTriangles().size());
		
		// ========= ONLY TO DEBUG
//		for (int i=0; i< tin.getTriangles().size(); i++) {
//		      try {
//				writeTri(tin.getTriangles().get(i));
//			} catch (ProcessWriterVisitorException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    }
		// ==========

		
	}
	private void writeTri(Triangle t) throws ProcessWriterVisitorException {
		Value[] values = new Value[2];
		values[0] = ValueFactory.createValue(2.0);
		values[1] = ValueFactory.createValue(1);
		
		Coordinate c1 = new Coordinate(t.getV1().getX(), t.getV1().getY());
		Coordinate c2 = new Coordinate(t.getV2().getX(), t.getV2().getY());
		Coordinate c3 = new Coordinate(t.getV3().getX(), t.getV3().getY());
		Coordinate[] c = new Coordinate[4];
		c[0] = c1;
		c[1] = c3;
		c[2] = c2;
		c[3] = c1;
		LinearRing linRing = null;
		if (CGAlgorithms.isCCW(c))
		{
			Coordinate[] ccw = new Coordinate[4];
			ccw[0] = c1;
			ccw[1] = c2;
			ccw[2] = c3;
			ccw[3] = c1;
			linRing = gf.createLinearRing(ccw);
		}
		else
		{
			linRing = gf.createLinearRing(c);
//			return;
		}
		 
		Polygon pol = gf.createPolygon(linRing, null);
		
		IGeometry geom = FConverter.jts_to_igeometry(pol);
		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat, DefaultRowEdited.STATUS_ADDED, idFlag);
//		shpWriterPol.process(row);
//		shpWriterTri.process(row);
		
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

	/**
	 * Close writers.
	 * @throws BaseException
	 */
	public void closeFiles() throws BaseException {
//		for (int j=serviceAreaPolygons.size()-1; j>=0; j--) {
//			Geometry jtsGeom = serviceAreaPolygons.get(j);
//			writePolygon(idFlag, costs[j], jtsGeom);
//		}

		shpWriter.postProcess();
		shpWriterPol.postProcess();
//		shpWriterTri.postProcess();
		
		adapter.stop();
		
		

	}
	public double[] getCosts() {
		return costs;
	}

	public void setCosts(double[] costs) {
		this.costs = costs;
	}

	public FLyrVect getPolygonLayer() throws LoadLayerException {
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer(layerDefPol.getName(), "gvSIG shp driver", 
				layerDefPol.getFile(), null);
		return lyr;
	}

	public FLyrVect getLineLayer() throws LoadLayerException {
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer(layerDef.getName(), "gvSIG shp driver", 
				layerDef.getFile(), null);
		return lyr;
	}

	public boolean isDoCompactArea() {
		return bDoCompactArea;
	}

	public void setDoCompactArea(boolean doCompactArea) {
		bDoCompactArea = doCompactArea;
	}

	public void setDistances(double[] distances) {
		this.distances = distances;
	}

}
