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
package org.gvsig.graph.core.writers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.NodeGv;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class NetworkJdbcWriter extends AbstractNetworkWriter {

	private Connection conn;
	private ResultSet rsNodes;
	private ResultSet rsEdges;

	private void createTables() throws SQLException {
		Statement stCreate = conn.createStatement();
		int indexPoint = lyr.getName().indexOf('.');
		String layerName;
		if (indexPoint == -1)
			layerName = lyr.getName();
		else
			layerName = lyr.getName().substring(0, indexPoint);

		try {
			stCreate.execute("DROP TABLE " + layerName + "_nodes;");
		} catch (SQLException e1) {
			// Si no existe la tabla, no hay que borrarla.
		}
		try {
			stCreate.execute("DROP TABLE " + layerName + "_edges;");
		} catch (SQLException e1) {
			// Si no existe la tabla, no hay que borrarla.
		}

		stCreate.execute("CREATE TABLE " + layerName
				+ "_nodes (NODEID int4, X float8, Y float8);");
		stCreate.execute("ALTER TABLE "
				+ layerName
				+ "_nodes MODIFY COLUMN NODEID INTEGER, ADD PRIMARY KEY(NODEID);");
		stCreate.execute("CREATE TABLE "
				+ layerName
				+ "_edges (EdgeId int4, ArcID int4, Direction int2, NodeOrigin int4, "
				+ "NodeEnd int4, Type int4, Dist float8, Cost float8);");
		stCreate.execute("ALTER TABLE "
				+ layerName
				+ "_edges MODIFY COLUMN EdgeId INTEGER, ADD PRIMARY KEY(EdgeId);");

		conn.setAutoCommit(false);
		conn.commit();
		// conn.setAutoCommit(true);

		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		rsNodes = st.executeQuery("SELECT * FROM " + layerName + "_nodes;");
		if (rsNodes.getConcurrency() != ResultSet.CONCUR_UPDATABLE) {
			System.err.println("Error: No se puede editar la tabla nodes");
			return;
		}
		Statement st2 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);

		rsEdges = st2.executeQuery("SELECT * FROM " + layerName + "_edges;");

	}

	public void writeNetwork() throws BaseException {
		try {
			createTables();

			conn.setAutoCommit(false);

			double distance;
			double cost;
			short arcType;
			int direction;
			int i;
			int idNodo1, idNodo2, nodeCount, edgeCount;
			short sentidoDigit; // => 1 en esa dirección. 0=> Al contrario. SOLO
			// SE UTILIZA PARA LOS CALCULOS POR IDTRAMO Y
			// PORCENTAJE
			// PARA SABER SI ESTÁ MÁS CERCA DE UN NODO O DEL OTRO.

			VectorialAdapter adapter = (VectorialAdapter) lyr.getSource();

			int numEntities = adapter.getShapeCount();
			Hashtable nodeHash = new Hashtable();
			SelectableDataSource sds = lyr.getRecordset();

			int senseFieldIndex = -1;
			int distFieldIndex = -1;
			int typeFieldIndex = -1;
			int costFieldIndex = -1;

			if (fieldSense != null)
				senseFieldIndex = sds.getFieldIndexByName(fieldSense);
			if (fieldDist != null)
				distFieldIndex = sds.getFieldIndexByName(fieldDist);
			if (fieldType != null)
				typeFieldIndex = sds.getFieldIndexByName(fieldType);
			if (fieldCost != null)
				costFieldIndex = sds.getFieldIndexByName(fieldCost);

			edgeCount = 0;
			nodeCount = 0;

			NumericValue valAux = null;

			for (i = 0; i < numEntities; i++) {
				IGeometry geom = adapter.getShape(i);
				Geometry jtsGeom = geom.toJTSGeometry();
				Coordinate[] coords = jtsGeom.getCoordinates();
				Coordinate c1 = coords[0];
				Coordinate c2 = coords[coords.length - 1];

				NodeGv nodeAux;
				if (!nodeHash.containsKey(c1)) // No está.
				{
					idNodo1 = nodeCount++;
					nodeAux = new NodeGv(c1, idNodo1);
					nodeHash.put(c1, nodeAux);
					writeNode(nodeAux);
				} else {
					nodeAux = (NodeGv) nodeHash.get(c1);
				}
				idNodo1 = nodeAux.getId().intValue();

				if (!nodeHash.containsKey(c2)) // No está.
				{
					idNodo2 = nodeCount++;
					nodeAux = new NodeGv(c2, idNodo2);
					nodeHash.put(c2, nodeAux);
					writeNode(nodeAux);
				} else {
					nodeAux = (NodeGv) nodeHash.get(c2);
				}
				idNodo2 = nodeAux.getId().intValue();

				if (typeFieldIndex != -1)
					valAux = (NumericValue) sds
							.getFieldValue(i, typeFieldIndex);
				else
					valAux = ValueFactory.createValue(0); // no hay tipo
				arcType = valAux.shortValue();
				// TipoTramo = DBFReadIntegerAttribute(hDBF, i, indiceCampo1);

				if (distFieldIndex != -1)
					valAux = (NumericValue) sds
							.getFieldValue(i, distFieldIndex);
				else
					valAux = ValueFactory.createValue(jtsGeom.getLength());
				distance = valAux.floatValue();
				// Distancia = (float) DBFReadDoubleAttribute(hDBF, i,
				// indiceCampo2);
				if (costFieldIndex != -1) {
					valAux = (NumericValue) sds
							.getFieldValue(i, costFieldIndex);
					cost = valAux.doubleValue();
				} else
					cost = distance;

				direction = -1;

				if (senseFieldIndex == -1)
					direction = 3; // 3-> Doble sentido, 1-> según viene, 2 ->
				// al revés, cualquier otro valor-> No hay
				// arco
				else {
					valAux = (NumericValue) sds.getFieldValue(i,
							senseFieldIndex);
					direction = valAux.shortValue();
				}

				if (direction == 3) {
					sentidoDigit = 1; // En esa dirección
					writeEdge(edgeCount, i, sentidoDigit, idNodo1, idNodo2,
							arcType, distance, cost);
					edgeCount++;

					sentidoDigit = 0;
					writeEdge(edgeCount, i, sentidoDigit, idNodo2, idNodo1,
							arcType, distance, cost);
					edgeCount++;

				}
				if (direction == 1) {
					sentidoDigit = 1; // En esa dirección
					writeEdge(edgeCount, i, sentidoDigit, idNodo1, idNodo2,
							arcType, distance, cost);
					edgeCount++;
				}
				if (direction == 2) {
					sentidoDigit = 0;
					writeEdge(edgeCount, i, sentidoDigit, idNodo2, idNodo1,
							arcType, distance, cost);
					edgeCount++;

				}

			}
			conn.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeEdge(int edgeId, int id, short sense, int idNodeOrig,
			int idNodeEnd, short tipoTramo, double dist, double cost)
			throws SQLException {
		rsEdges.moveToInsertRow();
		rsEdges.updateInt(1, edgeId);
		rsEdges.updateInt(2, id);
		rsEdges.updateInt(3, sense);
		rsEdges.updateInt(4, idNodeOrig);
		rsEdges.updateInt(5, idNodeEnd);
		rsEdges.updateInt(6, tipoTramo);
		rsEdges.updateDouble(7, dist);
		rsEdges.updateDouble(8, cost);
		rsEdges.insertRow();
		// System.out.println("writing edge " + edgeId);
	}

	private void writeNode(NodeGv node) throws SQLException {
		rsNodes.moveToInsertRow();
		int id = node.getId().intValue();
		rsNodes.updateInt(1, id);
		rsNodes.updateDouble(2, node.getCoordinate().x);
		rsNodes.updateDouble(3, node.getCoordinate().y);
		rsNodes.insertRow();
		// System.out.println("writing node " + id);

	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

}
