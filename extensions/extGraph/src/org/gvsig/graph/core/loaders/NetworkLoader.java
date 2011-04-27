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
package org.gvsig.graph.core.loaders;

import java.awt.geom.Point2D;
import java.io.File;

import org.gvsig.graph.core.EdgeWeightLabeller;
import org.gvsig.graph.core.FEdge;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvGraph;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.INetworkLoader;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;
import com.hardcode.driverManager.DriverValidation;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.DBDriver;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.DefaultSettableVertexLocationFunction;
import edu.uci.ics.jung.visualization.SettableVertexLocationFunction;

public class NetworkLoader implements INetworkLoader {

	private String fieldNodeId = "NODEID";
	private String fieldNodeX = "X";
	private String fieldNodeY = "Y";

	private String fieldArcId = "ARCID";
	private String fieldDirection = "DIRECTION";
	private String fieldNodeOrig = "NODEORIGIN";
	private String fieldNodeEnd = "NODEEND";
	private String fieldType = "TYPE";
	private String fieldDist = "DIST";
	private String fieldCost = "COST";

	private SelectableDataSource nodeReader;
	private SelectableDataSource edgeReader;

	private Indexer indexer;

	private boolean bUseCostField;

	/**
     * The user data key used to retrieve the vertex locations (if any) defined by this class.
     */
    public static final String LOCATIONS = "jung.io.GvSIGNetReader.LOCATIONS";

	protected SettableVertexLocationFunction v_locations = new DefaultSettableVertexLocationFunction();


	/**
	 * By default, the fields are:
	 * private String fieldNodeId = "NODEID";
	 * 	 fieldNodeX = "X";
	 * 	 fieldNodeY = "Y";
	 *
	 * 	 fieldArcId = "NODEID";
	 * 	 fieldDirection = "X";
	 * 	 fieldNodeOrig = "Y";
	 * 	 fieldNodeEnd = "NODEORIGIN";
	 * 	 fieldType = "NODEEND";
	 * 	 fieldDist = "DIST";
	 *
	 * If you need to change any, use the correspondant "setFieldXXXX"
	 * BEFORE calling loadNetwork. And remember to set nodeReader and
	 * edgeReader also.
	 */
	public NetworkLoader(boolean bUseCostField) {
		this.bUseCostField = bUseCostField;
	}


	public String getFieldArcId() {
		return fieldArcId;
	}

	public void setFieldArcId(String fieldArcId) {
		this.fieldArcId = fieldArcId;
	}

	public String getFieldDirection() {
		return fieldDirection;
	}

	public void setFieldDirection(String fieldDirection) {
		this.fieldDirection = fieldDirection;
	}

	public String getFieldDist() {
		return fieldDist;
	}

	public void setFieldDist(String fieldDist) {
		this.fieldDist = fieldDist;
	}

	public String getFieldNodeEnd() {
		return fieldNodeEnd;
	}

	public void setFieldNodeEnd(String fieldNodeEnd) {
		this.fieldNodeEnd = fieldNodeEnd;
	}

	public String getFieldNodeId() {
		return fieldNodeId;
	}

	public void setFieldNodeId(String fieldNodeId) {
		this.fieldNodeId = fieldNodeId;
	}

	public String getFieldNodeOrig() {
		return fieldNodeOrig;
	}

	public void setFieldNodeOrig(String fieldNodeOrig) {
		this.fieldNodeOrig = fieldNodeOrig;
	}

	public String getFieldNodeX() {
		return fieldNodeX;
	}

	public void setFieldNodeX(String fieldNodeX) {
		this.fieldNodeX = fieldNodeX;
	}

	public String getFieldNodeY() {
		return fieldNodeY;
	}

	public void setFieldNodeY(String fieldNodeY) {
		this.fieldNodeY = fieldNodeY;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setEdgeReader(SelectableDataSource edgeReader) {
		this.edgeReader = edgeReader;
	}

	public void setNodeReader(SelectableDataSource nodeReader) {
		this.nodeReader = nodeReader;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkLoader netLoader = new NetworkLoader(false);

		//Setup de los drivers
		DriverManager dm = new DriverManager();
		dm.setValidation(new DriverValidation() {
				public boolean validate(Driver d) {
					return ((d instanceof ObjectDriver) ||
					(d instanceof FileDriver) ||
					(d instanceof DBDriver));
				}
			});
		dm.loadDrivers(new File("../_fwAndami/gvSIG/extensiones/org.gvsig/drivers"));

		//Setup del factory de DataSources
        DataSourceFactory dsf = LayerFactory.getDataSourceFactory();
		dsf.setDriverManager(dm);

		//Setup de las tablas
		dsf.addFileDataSource("gdbms dbf driver", "nodes", "c:/nodes.dbf");
		dsf.addFileDataSource("gdbms dbf driver", "edges", "c:/edges.dbf");

        DataSource dsNodes;
		try {
			dsNodes = dsf.createRandomDataSource("nodes",
			        DataSourceFactory.AUTOMATIC_OPENING);//MANUAL_OPENING);

	        DataSource dsEdges = dsf.createRandomDataSource("edges",
	                DataSourceFactory.AUTOMATIC_OPENING);//MANUAL_OPENING);


			SelectableDataSource sdsNodes = new SelectableDataSource(dsNodes);
			SelectableDataSource sdsEdges = new SelectableDataSource(dsEdges);

			netLoader.setNodeReader(sdsNodes);
			netLoader.setEdgeReader(sdsEdges);


			Graph g = netLoader.loadJungNetwork();

			System.out.println("Num nodos=" + g.numVertices() + " numEdges = " + g.numEdges());

			// Probamos la algoritmia: distancia entre nodo 1 y nodo 1000
			DijkstraDistance distCalculator = new DijkstraDistance(g, new EdgeWeightLabeller());
			ArchetypeVertex vOrig = netLoader.indexer.getVertex(1);
			ArchetypeVertex vEnd = netLoader.indexer.getVertex(1000);
			long t1 = System.currentTimeMillis();
			Number dist = distCalculator.getDistance(vOrig, vEnd);
			long t2 = System.currentTimeMillis();


			System.out.println("dist =" + dist + " meters. msecs: " + (t2-t1));
			System.out.println(vOrig + " - " + vEnd);
			System.out.println(vOrig.getUserDatum("X") + ", " + vOrig.getUserDatum("Y")
							+ " - " + vEnd.getUserDatum("X") + ", " + vEnd.getUserDatum("Y"));

		} catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Graph loadJungNetwork() {
		try {
			int fieldIndexIdNode = nodeReader.getFieldIndexByName(fieldNodeId);
			int fieldIndexX = nodeReader.getFieldIndexByName(fieldNodeX);;
			int fieldIndexY = nodeReader.getFieldIndexByName(fieldNodeY);;

			int fieldIndexArcID = edgeReader.getFieldIndexByName(fieldArcId);
			int fieldIndexDirection = edgeReader.getFieldIndexByName(fieldDirection);
			int fieldIndexNodeOrig = edgeReader.getFieldIndexByName(fieldNodeOrig);
			int fieldIndexNodeEnd = edgeReader.getFieldIndexByName(fieldNodeEnd);
			int fieldIndexType = edgeReader.getFieldIndexByName(fieldType);
			int fieldIndexDist = edgeReader.getFieldIndexByName(fieldDist);


			SparseGraph g = new SparseGraph();
//			g.getEdgeConstraints().clear();
			long t1 = System.currentTimeMillis();

			// Mirar NumberEdgeVAlue e Indexer
			for (int i=0; i < nodeReader.getRowCount(); i++)
			{
				NumericValue id = (NumericValue) nodeReader.getFieldValue(i, fieldIndexIdNode);
				NumericValue x = (NumericValue) nodeReader.getFieldValue(i, fieldIndexX);
				NumericValue y = (NumericValue) nodeReader.getFieldValue(i, fieldIndexY);
				Vertex v = new DirectedSparseVertex();
				v.addUserDatum("ID", id, UserData.CLONE);
				v.addUserDatum("X", x, UserData.CLONE);
				v.addUserDatum("Y", y, UserData.CLONE);
				v_locations.setLocation(v, new Point2D.Double(x.doubleValue(),y.doubleValue()));
				g.addVertex(v);
			}
			indexer = Indexer.getIndexer(g);

			for (int i=0; i < edgeReader.getRowCount(); i++)
			{
				NumericValue arcID = (NumericValue) edgeReader.getFieldValue(i, fieldIndexArcID);
				NumericValue direc = (NumericValue) edgeReader.getFieldValue(i, fieldIndexDirection);
				NumericValue nodeOrig = (NumericValue) edgeReader.getFieldValue(i, fieldIndexNodeOrig);
				NumericValue nodeEnd = (NumericValue) edgeReader.getFieldValue(i, fieldIndexNodeEnd);
				NumericValue type = (NumericValue) edgeReader.getFieldValue(i, fieldIndexType);
				NumericValue dist = (NumericValue) edgeReader.getFieldValue(i, fieldIndexDist);

				Vertex vFrom = (Vertex) indexer.getVertex(nodeOrig.intValue());
				Vertex vTo = (Vertex) indexer.getVertex(nodeEnd.intValue());

				FEdge edge = new FEdge(vFrom, vTo);
				edge.setArcID(arcID.intValue());
				edge.setDirection(direc.intValue());
				edge.setIdNodeOrig(nodeOrig.intValue());
				edge.setIdNodeEnd(nodeEnd.intValue());
				edge.setType(type.intValue());
				edge.setWeight(dist.doubleValue());

				g.addEdge(edge);
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Tiempo de carga desde nodes.dbf y edges.dbf y generando JUNG network: " + (t2-t1) + " msecs");
			return g;
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public IGraph loadNetwork() {
		try {
			int fieldIndexIdNode = nodeReader.getFieldIndexByName(fieldNodeId);
			int fieldIndexX = nodeReader.getFieldIndexByName(fieldNodeX);;
			int fieldIndexY = nodeReader.getFieldIndexByName(fieldNodeY);;

			int fieldIndexArcID = edgeReader.getFieldIndexByName(fieldArcId);
			int fieldIndexDirection = edgeReader.getFieldIndexByName(fieldDirection);
			int fieldIndexNodeOrig = edgeReader.getFieldIndexByName(fieldNodeOrig);
			int fieldIndexNodeEnd = edgeReader.getFieldIndexByName(fieldNodeEnd);
			int fieldIndexType = edgeReader.getFieldIndexByName(fieldType);
			int fieldIndexDist = edgeReader.getFieldIndexByName(fieldDist);
			int fieldIndexCost = edgeReader.getFieldIndexByName(fieldCost);


			GvGraph g = new GvGraph();
			long t1 = System.currentTimeMillis();

			// Mirar NumberEdgeVAlue e Indexer
			for (int i=0; i < nodeReader.getRowCount(); i++)
			{
				NumericValue id = (NumericValue) nodeReader.getFieldValue(i, fieldIndexIdNode);
				NumericValue x = (NumericValue) nodeReader.getFieldValue(i, fieldIndexX);
				NumericValue y = (NumericValue) nodeReader.getFieldValue(i, fieldIndexY);
				GvNode node = new GvNode();
				node.setX(x.doubleValue());
				node.setY(y.doubleValue());
				node.setIdNode(i);
				g.addNode(node);
			}

			for (int i=0; i < edgeReader.getRowCount(); i++)
			{
				NumericValue arcID = (NumericValue) edgeReader.getFieldValue(i, fieldIndexArcID);
				NumericValue direc = (NumericValue) edgeReader.getFieldValue(i, fieldIndexDirection);
				NumericValue nodeOrig = (NumericValue) edgeReader.getFieldValue(i, fieldIndexNodeOrig);
				NumericValue nodeEnd = (NumericValue) edgeReader.getFieldValue(i, fieldIndexNodeEnd);
				NumericValue type = (NumericValue) edgeReader.getFieldValue(i, fieldIndexType);
				NumericValue dist = (NumericValue) edgeReader.getFieldValue(i, fieldIndexDist);

				GvEdge edge = new GvEdge();
				edge.setIdArc(arcID.intValue());
				edge.setIdEdge(i);
				edge.setDirec(direc.intValue());
				edge.setIdNodeOrig(nodeOrig.intValue());
				edge.setIdNodeEnd(nodeEnd.intValue());
				edge.setType(type.intValue());
				edge.setDistance(dist.doubleValue());
				if (bUseCostField)
				{
					NumericValue cost = (NumericValue) edgeReader.getFieldValue(i, fieldIndexCost);
					edge.setWeight(cost.doubleValue());
				}

				g.addEdge(edge);
			}
			long t2 = System.currentTimeMillis();
			System.out.println("Tiempo de carga desde nodes.dbf y edges.dbf y generando JUNG network: " + (t2-t1) + " msecs");
			return g;
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}


	public String getFieldCost() {
		return fieldCost;
	}


	public void setFieldCost(String fieldCost) {
		this.fieldCost = fieldCost;
	}


	public Indexer getIndexer() {
		return indexer;
	}

}


