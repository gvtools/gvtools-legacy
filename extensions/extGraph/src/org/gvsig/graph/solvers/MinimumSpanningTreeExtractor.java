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

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.GvEdge;
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
 * This class can label nodes with distances and costs to a flag. You will
 * obtain a temp shp layer with fields IdArc, IdEdge, CostOrig, DistOrig,
 * CostEnd, DistEnd, IdFlag
 * 
 * You will obtain a temp shp layer with the minimum spanning tree
 * starting from Flag idFlag. MinimumSpanningTree (MST) can help to
 * design networks (water networks, cable networks), because you can reach
 * all node points with minimum length cost. (Offer service to the whole
 * area, but expending less money on cable and holes).
 * 
 */
public class MinimumSpanningTreeExtractor implements IDijkstraListener {
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

	private Network net;

	private ShpWriter shpWriter;

	private File fTemp;

	private SHPLayerDefinition layerDef;

	private int idFlag;

	private ReadableVectorial adapter;

	/**
	 * @param net
	 * @throws InitializeWriterException
	 * @throws ReadDriverException
	 * @throws InitializeDriverException
	 */
	public MinimumSpanningTreeExtractor(Network net) throws BaseException {
		this.net = net;
		int aux = (int) (Math.random() * 1000);
		fTemp = new File(tempDirectoryPath + "/tmpShp" + aux + ".shp");

		layerDef = new SHPLayerDefinition();
		layerDef.setFile(fTemp);
		layerDef.setName("MST_" + fTemp.getName());
		layerDef.setFieldsDesc(fields);
		layerDef.setShapeType(FShape.LINE);

		shpWriter = new ShpWriter();
		shpWriter.setFile(fTemp);
		shpWriter.initialize(layerDef);
		FLyrVect lyr = net.getLayer();
		adapter = lyr.getSource();
		adapter.start();
		shpWriter.preProcess();

	}


	private void writePartialEdge(int i, IGeometry geom, GvEdge edge, GvNode nodeOrig, GvNode nodeEnd, int idFlag, double maxCost) throws ProcessWriterVisitorException {
		Geometry jtsGeom = geom.toJTSGeometry();
		double pct = (maxCost - nodeOrig.getBestCost())/ edge.getWeight();
		LineString partial = NetworkUtils.getPartialLineString(jtsGeom, pct, edge.getDirec());

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
		
	}

	private void writeTotalEdge(int i, IGeometry geom, GvEdge edge,
			GvNode nodeOrig, GvNode nodeEnd, int idFlag)
			throws ProcessWriterVisitorException {
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

	public boolean adjacentEdgeVisited(GvNode fromNode, GvEdge edge) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean minimumCostNodeSelected(GvNode node) {
		IGraph g = net.getGraph();
		int idEdge = node.get_best_from_link();
		if (idEdge == -1) 
			return false;
		GvEdge edge = g.getEdgeByID(idEdge);
		GvNode nodeEnd = g.getNodeByID(edge.getIdNodeEnd());
		GvNode nodeOrig = g.getNodeByID(edge.getIdNodeOrig());
		IGeometry geom;
		try {
			geom = adapter.getShape(edge.getIdArc());
			FLyrVect lyr = net.getLayer();
		    if (lyr.getCoordTrans() != null) {
		    	if (!lyr.getProjection().getAbrev().equals(lyr.getMapContext().getViewPort().getProjection().getAbrev())){
		    		geom.reProject(lyr.getCoordTrans());
		    	}
		    }			
			
			if (nodeEnd.getBestCost() < Double.MAX_VALUE) {
				// A ese tramo hemos llegado por completo
				// Recuperamos su distancia y etiquetamos.
				writeTotalEdge(idEdge, geom, edge, nodeOrig, nodeEnd, idFlag);	
			}
			else
			{
				if (nodeOrig.getBestCost() < Double.MAX_VALUE) {
					// A ese tramo hemos llegado parcialmente
					// Recuperamos su distancia y etiquetamos.
					// TODO: ESCRIBIR EL TRAMO PARCIAL!!!
					// writePartialEdge(idEdge, geom, edge, nodeOrig, nodeEnd, idFlag, edge.);	
					
				}
			} // else

		} catch (BaseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return false; // true if we want to stop Dijkstra
	}

	public int getIdFlag() {
		return idFlag;
	}

	public void setIdFlag(int idFlag) {
		this.idFlag = idFlag;
	}
	
	public void endExtraction() throws BaseException {
		shpWriter.postProcess();
		adapter.stop();
	}
	
	public FLyrVect getLineLayer() throws LoadLayerException {
		FLyrVect lyr = (FLyrVect) LayerFactory.createLayer(layerDef.getName(), "gvSIG shp driver", 
				layerDef.getFile(), null);
		return lyr;
	}
	

}
