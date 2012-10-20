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

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.EdgePair;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * @author fjp
 * 
 *         This class can label nodes with distances and costs to a flag Use
 *         first doLabelling() with every source flag and call doExtract() to
 *         obtain a new layer with fields IdArc, IdEdge, CostOrig, DistOrig,
 *         CostEnd, DistEnd, IdFlag
 * 
 */
public class ServiceAreaExtractor {
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

	private Network net;
	private ShpWriter shpWriter;
	private ShpWriter shpWriterPol;
	private File fTemp;
	private File fTempPol;
	private SHPLayerDefinition layerDef;
	private SHPLayerDefinition layerDefPol;
	private Geometry serviceArea = null;
	private ArrayList<Geometry> serviceAreaPolygons;

	public ServiceAreaExtractor(Network net) throws BaseException {
		this.net = net;
		int aux = (int) (Math.random() * 1000);
		String nameLine = "tmpServiceAreaLine" + aux + ".shp";
		String namePol = "tmpServiceAreaPol" + aux + ".shp";
		fTemp = new File(tempDirectoryPath + "/" + nameLine);
		fTempPol = new File(tempDirectoryPath + "/" + namePol);

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

		shpWriter = new ShpWriter();
		shpWriter.setFile(fTemp);
		shpWriter.initialize(layerDef);

		shpWriterPol = new ShpWriter();
		shpWriterPol.setFile(fTempPol);
		shpWriterPol.initialize(layerDefPol);
		shpWriter.preProcess();
		shpWriterPol.preProcess();

	}

	public void doExtract(int idFlag, double[] costs) {
		// if (maxCost == -1)
		// {
		// throw new
		// RuntimeException("ServiceAreaExtactor: You need to set maxCost.");
		// }
		double maxCost = costs[costs.length - 1];
		serviceAreaPolygons = new ArrayList<Geometry>(costs.length);
		for (int i = 0; i < costs.length - 1; i++)
			serviceAreaPolygons.add(null);
		FLyrVect lyr = net.getLayer();
		IGraph g = net.getGraph();
		ReadableVectorial adapter = lyr.getSource();
		try {

			adapter.start();

			for (int i = 0; i < adapter.getShapeCount(); i++) {
				IGeometry geom = adapter.getShape(i);
				EdgePair edgePair = g.getEdgesByIdArc(i);
				if (edgePair.getIdEdge() != -1) {
					GvEdge edge = g.getEdgeByID(edgePair.getIdEdge());
					GvNode nodeEnd = g.getNodeByID(edge.getIdNodeEnd());
					GvNode nodeOrig = g.getNodeByID(edge.getIdNodeOrig());
					processEdgeForPolygon(edge, nodeOrig, nodeEnd, geom, costs);
					if (nodeEnd.getBestCost() > nodeOrig.getBestCost()) {
						if (nodeEnd.getBestCost() < maxCost) {
							// A ese tramo hemos llegado por completo
							// Recuperamos su distancia y etiquetamos.
							writeTotalEdge(i, geom, edge, nodeOrig, nodeEnd,
									idFlag);
						} else {
							if (nodeOrig.getBestCost() < maxCost) {
								// A ese tramo hemos llegado parcialmente
								// Recuperamos su distancia y etiquetamos.
								writePartialEdge(i, geom, edge, nodeOrig,
										nodeEnd, idFlag, maxCost);

							}
						} // else
					} // if nodeEnd > nodeOrig
				}
				if (edgePair.getIdInverseEdge() != -1) {
					GvEdge inversedEdge = g.getEdgeByID(edgePair
							.getIdInverseEdge());
					GvNode nodeEnd = g.getNodeByID(inversedEdge.getIdNodeEnd());
					GvNode nodeOrig = g.getNodeByID(inversedEdge
							.getIdNodeOrig());
					processEdgeForPolygon(inversedEdge, nodeOrig, nodeEnd,
							geom, costs);
					if (nodeEnd.getBestCost() > nodeOrig.getBestCost()) {
						if (nodeEnd.getBestCost() < maxCost) {
							// A ese tramo hemos llegado por completo
							// Recuperamos su distancia y etiquetamos.
							writeTotalEdge(i, geom, inversedEdge, nodeOrig,
									nodeEnd, idFlag);
						} else {
							if (nodeOrig.getBestCost() < maxCost) {
								// A ese tramo hemos llegado parcialmente
								// Recuperamos su distancia y etiquetamos.
								writePartialEdge(i, geom, inversedEdge,
										nodeOrig, nodeEnd, idFlag, maxCost);

							}
						} // else
					} // if nodeEnd > nodeOrig
				}

			}
			for (int j = serviceAreaPolygons.size() - 1; j >= 0; j--) {
				Geometry jtsGeom = serviceAreaPolygons.get(j);
				writePolygon(idFlag, costs[j], jtsGeom);
			}
			adapter.stop();
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Write data and close files.
	 * 
	 * @throws BaseException
	 */
	public void endExtraction() throws BaseException {
		shpWriter.postProcess();
		shpWriterPol.postProcess();
	}

	/**
	 * We process each edge and prepare a list of polygons, classified by cost
	 * 
	 * @param edge
	 * @param nodeOrig
	 * @param nodeEnd
	 * @param geom
	 * @param costs
	 */
	private void processEdgeForPolygon(GvEdge edge, GvNode nodeOrig,
			GvNode nodeEnd, IGeometry geom, double[] costs) {
		if (nodeEnd.getBestCost() > nodeOrig.getBestCost()) {
			// miramos en qué polígono cae ese edge POR COMPLETO
			// El coste de su punto final es menor que uno de los costes.
			int indexInterval = getCostInterval(nodeEnd.getBestCost(), costs);
			// Un polígono por cada zona
			Geometry jtsGeom = geom.toJTSGeometry();
			if (indexInterval != -1) {
				for (int i = costs.length - 1; i >= indexInterval; i--) {
					calculateConvexHull(jtsGeom, i);
				}
			}
			double maxCost = costs[costs.length - 1];
			// Es -1 si caso límite externo
			if (indexInterval < costs.length - 1) {
				// Caso límite externo
				if ((nodeEnd.getBestCost() > maxCost)
						&& (nodeOrig.getBestCost() < maxCost)) {
					double pct = (maxCost - nodeOrig.getBestCost())
							/ edge.getWeight();
					LineString partial = NetworkUtils.getPartialLineString(
							jtsGeom, pct, edge.getDirec());
					calculateConvexHull(partial, costs.length - 1);
					return;
				}
				// Parcial interno
				maxCost = costs[indexInterval + 1];
				if ((nodeOrig.getBestCost() < maxCost)
						&& (nodeEnd.getBestCost() > maxCost)) {
					// A ese tramo hemos llegado parcialmente

					double pct = (maxCost - nodeOrig.getBestCost())
							/ edge.getWeight();
					try {
						LineString partial = NetworkUtils.getPartialLineString(
								jtsGeom, pct, edge.getDirec());
						calculateConvexHull(partial, indexInterval + 1);
					} catch (Exception e) {
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
			Geometry gIni = jtsGeom;
			serviceAreaPolygons.add(i, gIni);
		} else {
			Geometry antG = serviceAreaPolygons.get(i);
			if (antG == null)
				antG = jtsGeom;
			else {
				antG = antG.union(jtsGeom);
			}
			antG = antG.convexHull();
			serviceAreaPolygons.set(i, antG);
		}
	}

	/**
	 * Devuelve el índice del intervalo más alto que contiene a ese valor.
	 * 
	 * @param bestCost
	 * @param costs
	 * @return
	 */
	private int getCostInterval(double bestCost, double[] costs) {
		int ret = 0;
		if (bestCost > costs[costs.length - 1])
			return -1;
		for (int i = costs.length - 1; i >= 0; i--) {
			if (bestCost > costs[i]) {
				ret = i + 1;
				break;
			}
		}
		// if (ret > 0)
		// System.out.println(costs[ret-1] + " < " + bestCost + " < " +
		// costs[ret]);
		return ret;
	}

	private void writePartialEdge(int i, IGeometry geom, GvEdge edge,
			GvNode nodeOrig, GvNode nodeEnd, int idFlag, double maxCost)
			throws ProcessWriterVisitorException {
		Geometry jtsGeom = geom.toJTSGeometry();
		double pct = (maxCost - nodeOrig.getBestCost()) / edge.getWeight();
		LineString partial = NetworkUtils.getPartialLineString(jtsGeom, pct,
				edge.getDirec());
		if (serviceArea == null)
			serviceArea = partial;
		else {
			serviceArea = serviceArea.union(partial);
			serviceArea = serviceArea.convexHull();
		}

		IGeometry newGeom = FConverter.jts_to_igeometry(partial);

		Value[] values = new Value[7];
		values[0] = ValueFactory.createValue(i);
		values[1] = ValueFactory.createValue(edge.getIdEdge());
		values[2] = ValueFactory.createValue(nodeOrig.getBestCost());
		values[3] = ValueFactory.createValue(nodeOrig.getAccumulatedLength());
		values[4] = ValueFactory.createValue(maxCost);
		values[5] = ValueFactory.createValue(nodeOrig.getAccumulatedLength()
				+ edge.getDistance() * pct);
		values[6] = ValueFactory.createValue(idFlag);

		DefaultFeature feat = new DefaultFeature(newGeom, values);
		IRowEdited row = new DefaultRowEdited(feat,
				DefaultRowEdited.STATUS_ADDED, i);
		shpWriter.process(row);

	}

	private void writeTotalEdge(int i, IGeometry geom, GvEdge edge,
			GvNode nodeOrig, GvNode nodeEnd, int idFlag)
			throws ProcessWriterVisitorException {
		Geometry jtsGeom = geom.toJTSGeometry();
		if (serviceArea == null)
			serviceArea = jtsGeom;
		else {
			serviceArea = serviceArea.union(jtsGeom);
			serviceArea = serviceArea.convexHull();
		}

		Value[] values = new Value[7];
		values[0] = ValueFactory.createValue(i);
		values[1] = ValueFactory.createValue(edge.getIdEdge());
		values[2] = ValueFactory.createValue(nodeOrig.getBestCost());
		values[3] = ValueFactory.createValue(nodeOrig.getAccumulatedLength());
		values[4] = ValueFactory.createValue(nodeEnd.getBestCost());
		values[5] = ValueFactory.createValue(nodeEnd.getAccumulatedLength());
		values[6] = ValueFactory.createValue(idFlag);

		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat,
				DefaultRowEdited.STATUS_ADDED, i);
		shpWriter.process(row);
	}

	private void writePolygon(int idFlag, double maxCost, Geometry jtsGeom)
			throws ProcessWriterVisitorException {
		Value[] values = new Value[2];
		values[0] = ValueFactory.createValue(maxCost);
		values[1] = ValueFactory.createValue(idFlag);

		IGeometry geom = FConverter.jts_to_igeometry(jtsGeom);
		DefaultFeature feat = new DefaultFeature(geom, values);
		IRowEdited row = new DefaultRowEdited(feat,
				DefaultRowEdited.STATUS_ADDED, idFlag);
		shpWriterPol.process(row);
	}

	public FLyrVect getPolygonLayer() throws LoadLayerException {
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer(
				layerDefPol.getName(), "gvSIG shp driver",
				layerDefPol.getFile(), null);
		return lyr;
	}

	public FLyrVect getLineLayer() throws LoadLayerException {
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer(layerDef.getName(),
				"gvSIG shp driver", layerDef.getFile(), null);
		return lyr;
	}

}
