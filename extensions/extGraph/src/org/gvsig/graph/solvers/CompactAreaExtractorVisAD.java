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
import java.util.HashSet;
import java.util.Iterator;

import org.gvsig.exceptions.BaseException;
import org.gvsig.fmap.algorithm.triangulation.visad.DelaunayWatson;
import org.gvsig.fmap.algorithm.triangulation.visad.VisADException;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author fjp
 * 
 *         This class can label nodes with distances and costs to a flag. You
 *         will obtain a temp shp layer with fields IdArc, IdEdge, CostOrig,
 *         DistOrig, CostEnd, DistEnd, IdFlag
 * 
 *         La diferencia con ServiceAreaExtractor es que esta versión escucha al
 *         algoritmo Dijkstra, y va montando el shp de líneas conforme va siendo
 *         explorada la red. La gran ventaja de hacerlo así es que no dependes
 *         del tamaño de la red. Solo recorres los tramos y nodos que exploras,
 *         de forma que si limitas el área de servicio a una distancia máxima,
 *         la red solo se explora hasta esa distancia / coste.
 * 
 */
public class CompactAreaExtractorVisAD implements IDijkstraListener {
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

	// private double maxCost;

	private Geometry serviceArea;
	private ArrayList<Geometry> serviceAreaPolygons;

	private double[] costs = null;

	private HashSet<GvNode> nodes = new HashSet();
	DelaunayWatson tri2;
	double max_value = 1e38;

	/**
	 * @param net
	 * @throws InitializeWriterException
	 * @throws ReadDriverException
	 * @throws InitializeDriverException
	 */
	public CompactAreaExtractorVisAD(Network net) throws BaseException {
		int aux = (int) (Math.random() * 1000);

		this.net = net;
		nodes = new HashSet<GvNode>();

		String namePol = "tmpCompactAreaPol" + aux + ".shp";
		fTempPol = new File(tempDirectoryPath + "/" + namePol);

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

	}

	public boolean adjacentEdgeVisited(GvNode fromNode, GvEdge edge) {
		IGraph g = net.getGraph();
		GvNode orig = g.getNodeByID(edge.getIdNodeOrig());
		GvNode end = g.getNodeByID(edge.getIdNodeEnd());
		nodes.add(orig);
		nodes.add(end);

		return false;
	}

	public boolean minimumCostNodeSelected(GvNode node) {
		IGraph g = net.getGraph();
		int idEdge = node.get_best_from_link();
		if (idEdge == -1)
			return false;
		GvEdge edge = g.getEdgeByID(idEdge);
		GvNode orig = g.getNodeByID(edge.getIdNodeOrig());
		GvNode end = g.getNodeByID(edge.getIdNodeEnd());
		nodes.add(orig);
		nodes.add(end);

		return false; // true if we want to stop Dijkstra
	}

	public void writeServiceArea() throws ProcessWriterVisitorException {
		int numPoints = nodes.size();
		double[][] samples = new double[2][numPoints];
		double[] samp0 = samples[0];
		double[] samp1 = samples[1];
		Iterator it = nodes.iterator();
		for (int i = 0; i < numPoints; i++) {
			GvNode node = (GvNode) it.next();
			samp0[i] = node.getX();
			samp1[i] = node.getY();
		}

		try {
			tri2 = new DelaunayWatson(samples);
			// tri2.improve(samples, 10);

			System.out.println("Fin de trayecto. Num. triángulos="
					+ tri2.Tri.length);
			for (int i = 0; i < tri2.Tri.length; i++) {
				writeTri(tri2.Tri[i], samples);
			}
		} catch (VisADException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeTri(int[] vertex, double[][] points)
			throws ProcessWriterVisitorException {
		Value[] values = new Value[2];
		values[0] = ValueFactory.createValue(2.0);
		values[1] = ValueFactory.createValue(1);

		GeneralPathX gp = new GeneralPathX();
		FPoint2D p1 = new FPoint2D(points[0][vertex[0]], points[1][vertex[0]]);
		FPoint2D p2 = new FPoint2D(points[0][vertex[1]], points[1][vertex[1]]);
		FPoint2D p3 = new FPoint2D(points[0][vertex[2]], points[1][vertex[2]]);
		gp.moveTo(p1.getX(), p1.getY());
		gp.lineTo(p2.getX(), p2.getY());
		gp.lineTo(p3.getX(), p3.getY());
		gp.lineTo(p1.getX(), p1.getY());

		IGeometry geom = ShapeFactory.createPolygon2D(gp);
		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat,
				DefaultRowEdited.STATUS_ADDED, idFlag);
		shpWriterPol.process(row);

	}

	public void closeFiles() throws StopWriterVisitorException,
			ReadDriverException {
		shpWriterPol.postProcess();

		adapter.stop();

	}

}
