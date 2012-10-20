/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.gvsig.graph.core.EdgePair;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvGraph;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.INetworkLoader;

import com.iver.utiles.bigfile.BigByteBuffer2;

/* import edu.uci.ics.jung.graph.Graph;
 import edu.uci.ics.jung.graph.Vertex;
 import edu.uci.ics.jung.graph.decorators.Indexer;
 import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
 import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
 import edu.uci.ics.jung.graph.impl.SparseGraph; */

/**
 * @author fjp
 * 
 *         Primero vienen los arcos, y luego los nodos. En la cabecera, 3
 *         enteros con el numero de tramos, el de arcos y el de nodos.
 * 
 */
public class NetworkRedLoader implements INetworkLoader {

	private File netFile = new File("c:/ejes.red");

	public IGraph loadNetwork() {

		long t1 = System.currentTimeMillis();

		int numArcs;
		int numEdges;
		int numNodes;

		short sentidoDigit; // => 1 en esa direcci�n. 0=> Al contrario. SOLO
		// SE UTILIZA PARA LOS CALCULOS POR IDTRAMO Y
		// PORCENTAJE
		// PARA SABER SI EST� M�S CERCA DE UN NODO O DEL OTRO.

		RandomAccessFile file;
		try {
			file = new RandomAccessFile(netFile.getPath(), "r");
			FileChannel channel = file.getChannel();
			// MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,
			// 0, channel.size());
			BigByteBuffer2 buf = new BigByteBuffer2(channel,
					FileChannel.MapMode.READ_ONLY);
			buf.order(ByteOrder.LITTLE_ENDIAN);

			numArcs = buf.getInt();
			numEdges = buf.getInt();
			numNodes = buf.getInt();

			GvGraph g = new GvGraph(numArcs, numEdges, numNodes);

			// Nodes

			// NOTE: EDGES ARE WRITEN BEFORE. LOOK TO NetworkFileRedWriter.
			// output.writeInt(id); 4
			// output.writeInt(sense); 4
			//
			// output.writeInt(idNodeOrig); 4
			// output.writeInt(idNodeEnd); 4
			// output.writeInt(tipoTramo); 4
			// output.writeDouble(dist); 8
			// output.writeDouble(cost); 8
			// TOTAL = 5x4 + 2x8 = 20 + 16 = 36 bytes /edge

			buf.position(36 * numEdges + 12);
			System.out.println("Loading " + numNodes + " nodes...");
			for (int i = 0; i < numNodes; i++) {
				GvNode node = readNode(buf);
				g.addNode(node);
			}
			System.gc();
			// Arcos
			buf.position(12);
			System.out.println("Loading " + numEdges + " edges...");
			for (int i = 0; i < numEdges; i++) {
				if ((i % 100000) == 0)
					System.out.println("Loading edge " + i + " of " + numEdges);
				GvEdge edge = readEdge(buf);
				edge.setIdEdge(i);
				g.addEdge(edge);
				GvNode nodeOrig = g.getNodeByID(edge.getIdNodeOrig());
				nodeOrig.addOutputLink(edge);
				GvNode nodeEnd = g.getNodeByID(edge.getIdNodeEnd());
				nodeEnd.addInputLink(edge);

				EdgePair edgePair = g.getEdgesByIdArc(edge.getIdArc());
				if (edgePair == null) {
					edgePair = new EdgePair();
					g.addEdgePair(edge.getIdArc(), edgePair);
				}
				if (edge.getDirec() == 1)
					edgePair.setIdEdge(i);
				else
					edgePair.setIdInverseEdge(i);

			}

			long t2 = System.currentTimeMillis();
			System.out.println("Tiempo de carga: " + (t2 - t1) + " msecs");
			System.out.println("NumEdges = " + g.numEdges());
			return g;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * public Graph loadJungNetwork() { SparseGraph g = new SparseGraph(); long
	 * t1 = System.currentTimeMillis();
	 * 
	 * RandomAccessFile file; try { file = new
	 * RandomAccessFile(netFile.getPath(), "r"); FileChannel channel =
	 * file.getChannel(); MappedByteBuffer buf =
	 * channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	 * buf.order(ByteOrder.LITTLE_ENDIAN);
	 * 
	 * int numArcs = buf.getInt(); int numEdges = buf.getInt(); int numNodes =
	 * buf.getInt();
	 * 
	 * // Nodes buf.position(24*numEdges + 12); for (int i=0; i < numNodes; i++)
	 * { GvNode node = readNode(buf);
	 * 
	 * Vertex v = new DirectedSparseVertex(); // v.addUserDatum("ID",
	 * node.idNode, UserData.CLONE); // v.addUserDatum("X", node.x,
	 * UserData.CLONE); // v.addUserDatum("Y", node.y, UserData.CLONE); //
	 * v_locations.setLocation(v, new
	 * Point2D.Double(x.doubleValue(),y.doubleValue())); g.addVertex(v); }
	 * Indexer indexer = Indexer.getIndexer(g);
	 * 
	 * buf.position(12); for (int i=0; i < numEdges; i++) { GvEdge edge =
	 * readEdge(buf);
	 * 
	 * int nodeOrig = edge.getIdNodeOrig(); int nodeEnd = edge.getIdNodeEnd();
	 * 
	 * Vertex vFrom = (Vertex) indexer.getVertex(nodeOrig); Vertex vTo =
	 * (Vertex) indexer.getVertex(nodeEnd);
	 * 
	 * DirectedSparseEdge edgeJ = new DirectedSparseEdge(vFrom, vTo);
	 * g.addEdge(edgeJ); } long t2 = System.currentTimeMillis();
	 * System.out.println("Tiempo de carga: " + (t2-t1) + " msecs"); return g; }
	 * catch (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } return null; }
	 */

	private GvNode readNode(BigByteBuffer2 buf) {
		GvNode node = new GvNode();
		node.setIdNode(buf.getInt());
		node.setX(buf.getDouble());
		node.setY(buf.getDouble());
		return node;
	}

	private GvEdge readEdge(BigByteBuffer2 buf) {
		GvEdge edge = new GvEdge();
		// memcpy(&Arcos[link_num].idTramo,puntero,sizeof(long));
		edge.setIdArc(buf.getInt());

		// Sentido de digitalizaci�n.Un 1 indica que va en ese sentido, un cero
		// al contrario.
		// memcpy(&Arcos[link_num].sentido,puntero,sizeof(int));
		edge.setDirec(buf.getInt());

		// idNodeOrig
		edge.setIdNodeOrig(buf.getInt());
		// memcpy(&node_num1,puntero,sizeof(long));

		// idNodeEnd
		edge.setIdNodeEnd(buf.getInt());
		// memcpy(&node_num2,puntero,sizeof(long));

		// Read the link costs.
		// Type
		edge.setType(buf.getInt());
		// memcpy(&Arcos[link_num].TipoTramo,puntero,sizeof(int));

		// Distance
		edge.setDistance(buf.getDouble());
		edge.setWeight(buf.getDouble());

		// memcpy(&Arcos[link_num].Coste2,puntero,sizeof(float));

		// pNodo1 = &Nodos[node_num1];
		// Arcos[link_num].idNodo1 = node_num1;
		//
		// Arcos[link_num].idNodo2 = node_num2;
		// pNodo2->Enlaces.Add(link_num);

		// // NUEVO 11-JUL-2002
		// if (Arcos[link_num].sentido)
		// IndiceArcos[Arcos[link_num].idTramo].idArco = link_num;
		// else
		// IndiceArcos[Arcos[link_num].idTramo].idContraArco = link_num;
		//
		// // NUEVO 27-JUL-2003
		// Arcos[link_num].numSoluc = 0;
		//
		// // NUEVO 23_2_2005
		// CreaConectores(link_num);
		return edge;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NetworkRedLoader redLoader = new NetworkRedLoader();

		redLoader.loadNetwork();
		// redLoader.loadJungNetwork();

	}

	public File getNetFile() {
		return netFile;
	}

	public void setNetFile(File netFile) {
		this.netFile = netFile;
	}

}
