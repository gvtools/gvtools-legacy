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

package org.gvsig.graph.solvers;

import java.util.ArrayList;
import java.util.Stack;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.AbstractNetSolver;
import org.gvsig.graph.core.DefaultFeatureExtractor;
import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvEdge;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.GvNode;
import org.gvsig.graph.core.IFeatureExtractor;
import org.gvsig.graph.core.IGraph;
import org.gvsig.graph.core.InfoShp;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

public abstract class AbstractShortestPathSolver extends AbstractNetSolver {

	private GeometryFactory geomFactory = new GeometryFactory();
	protected Route route = new Route();
	private int fieldIndexStreetName;
	private IFeatureExtractor featExtractor = null;

	public abstract Route calculateRoute() throws GraphException;

	public void setFielStreetName(String name) {
		try {
			if (net.getLayer() != null) {
				int aux = net.getLayer().getRecordset().getFieldIndexByName(
						name);
				if (aux == -1)
					throw new RuntimeException("Field " + name + " not found.");
				fieldIndexStreetName = aux;
			} else {
				fieldIndexStreetName = 0;
			}
		} catch (BaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void populateRouteSimple(int idStart, int idEnd)
			throws BaseException {
		int idEnlace;
		GvNode node;
		GvEdge link;
		double costeEntrada;

		// Trazar el camino desde idEnd hasta idStart hacia atrás marcando los
		// Enlaces
		double Coste = 0;
		double Coste2 = 0;
		IGraph graph = net.getGraph();
		node = graph.getNodeByID(idEnd);
		int from_link = node.get_best_from_link();
		VectorialAdapter va = (VectorialAdapter) net.getLayer().getSource();
		while (node.getIdNode() != idStart) {
			if (from_link == -1) {
				throw new RuntimeException(
						"Fallo al recorrer de manera inversa la solución. Encontrado arco con -1");
				// break; // FALLO!!
			}
			link = graph.getEdgeByID(from_link);
			IFeature feat = va.getFeature(link.getIdArc());
			route.addRouteFeature(feat.getGeometry(), link.getIdArc(), link
					.getWeight(), link.getDistance(), feat.getAttribute(
					getFieldIndexStreetName()).toString());

			node = graph.getNodeByID(link.getIdNodeOrig());

			Coste = Coste + link.getWeight();
			Coste2 = Coste2 + link.getDistance();

			// TODO:
			// from_link = node.get_from_link(idEnlace, &costeEntrada);
			from_link = node.get_from_link(link.getIdEdge());

		}
		System.out.println("Salgo con node = " + node.getIdNode());
	}

	protected void populateRoute(GvFlag origin, GvFlag dest, int idStart,
			int idEnd) throws BaseException {
		int idEnlace;
		GvNode node;
		GvEdge link;
		double costeEntrada;

		// Trazar el camino desde idEnd hasta idStart hacia atrás marcando los
		// Enlaces
		IGraph graph = net.getGraph();
		node = graph.getNodeByID(idEnd);
		int from_link = node.get_best_from_link();

		if (featExtractor == null)
			featExtractor = new DefaultFeatureExtractor(net.getLayer());

//		VectorialAdapter va = (VectorialAdapter) net.getLayer().getSource();
//		va.start();

		/*
		 * Miramos los nodos de los tramos inicio y final, y cogemos el nodo que
		 * tenga el from_link rellenado. E IGUAL A NUMSOLUCGLOBAL!!!! A partir
		 * de ahí, recorremos hacia atrás y tenemos el cuerpo principal del
		 * shape. Luego, para las puntas hay que tener en cuenta los
		 * porcentajes, viendo el trozo que está pegando a los nodos que sabemos
		 * que están en el path
		 */

		/*
		 * 22/9/2003 Corregimos el fallo que había de escribir el shape de atrás
		 * adelante. Guardamos lo que necesitamos en listaShapes y recorremos
		 * esa lista guardando los tramos con el sentido adecuado.
		 */

		/*
		 * 3/Febrero/2005 Limpieza de código superfluo y correción de un fallo
		 * raro. Ahora es más simple y parece que no falla. A ver si dura. IDEA:
		 * quizás se necesite meter el porcentaje en los arcos partidos.
		 */

		// Define a template class for a vector of IDs.
		InfoShp infoShp;
		Stack pilaShapes = new Stack();
		// typedef stack<CInfoShp> PILAINFO;
		// PILAINFO pilaShapes;

		double costeTramoFinal = -1;
		GvNode nodeEnd = graph.getNodeByID(idEnd);
		GvEdge finalEdge = graph.getEdgeByID(nodeEnd.get_best_from_link());
		costeTramoFinal = finalEdge.getWeight();

		GvNode pNodo;
		GvEdge pEnlace;

		boolean bFlipearShape = false;

		double pctMax, pctMin;

		// ////////////////////////////////////////////////////////////////////////////////////
		// Trozo del final
		// El shape va de idStop1 a idStop2, y el porcentaje viene en ese
		// sentido.
		// Si el idEnd es idStop1, quiere decir que el tramo que falta va en ese
		// sentido también,
		// así que recorremos ese shape desde idStop1 hasta que rebasemos o
		// igualemos ese porcentaje.
		// Si no, hemos pasado por idStop2 y la parte que hay que meter es desde
		// el pto interior a idStop2
		// /////////////////////////////////////////////////////////////////////////////////////
		// IFeature feat = va.getFeature(finalEdge.getIdArc());
		IGeometry g = featExtractor.getGeometry(finalEdge.getIdArc());
		Value nameStreet = featExtractor.getFieldValue(finalEdge.getIdArc(),
				getFieldIndexStreetName());

		MultiLineString jtsGeom = (MultiLineString) g.toJTSGeometry();
		// CoordinateFilter removeDuplicates = new
		// UniqueCoordinateArrayFilter();
		// jtsGeom.apply(removeDuplicates);
		// jtsGeom.geometryChanged();

		// SI ESTAMOS SOBRE EL MISMO TRAMO, CASO PARTICULAR
		// y el sentido de circulación es correcto
		if ((origin.getIdArc() == dest.getIdArc())
				&& (nodeEnd.getBestCost() <= costeTramoFinal)) {
			if (dest.getPct() > origin.getPct()) {
				pctMax = dest.getPct();
				pctMin = origin.getPct();
			} else {
				pctMax = origin.getPct();
				pctMin = dest.getPct();
				bFlipearShape = true;
			}

			LineString line = NetworkUtils.getPartialLineString(jtsGeom,
					pctMax, 1);

			pctMin = pctMin / pctMax; // Porque ha cambiado la longitud
			// del shape

			line = NetworkUtils.getPartialLineString(line, pctMin, 0);

			if (bFlipearShape)
				line = line.reverse();

			IGeometry geom = FConverter.jts_to_igeometry(line);
			// TODO: Calcular bien el length de este arco,
			// basandonos en el porcentaje costeTramoFinal / costeOriginal
			route.addRouteFeature(geom, origin.getIdArc(), nodeEnd
					.getBestCost(), nodeEnd.getAccumulatedLength(), nameStreet
					.toString());

			return; // Debería sacar el coste
		}

		// Trazar el camino desde idEnd hasta idStart hacia atrás marcando los
		// Enlaces
		pNodo = graph.getNodeByID(idEnd);

		from_link = pNodo.get_best_from_link();

		long t1 = System.currentTimeMillis();

		while ((pNodo.getIdNode() != idStart)) {
			idEnlace = from_link;

			pEnlace = graph.getEdgeByID(idEnlace);
			// System.err.println("from_link=" + from_link + " idTramo=" +
			// pEnlace.getIdArc());

			pNodo = graph.getNodeByID(pEnlace.getIdNodeOrig());

			infoShp = new InfoShp();
			infoShp.distance = pEnlace.getDistance();
			infoShp.cost = pEnlace.getWeight();

			if ((pEnlace.getIdArc() == origin.getIdArc())
					|| (pEnlace.getIdArc() == dest.getIdArc())) {
				if (pEnlace.getIdArc() == origin.getIdArc()) {
					infoShp.pct = origin.getPct();
					infoShp.idArc = origin.getIdArc();
					if (pEnlace.getDirec() == 0) {
						infoShp.direction = 1;
						infoShp.bFlip = true;
					} else // Hemos pasado por el 2
					{
						infoShp.direction = 0;
						infoShp.bFlip = false;
					} // if else */
				} else {
					infoShp.pct = dest.getPct();
					infoShp.idArc = dest.getIdArc();
					if (pEnlace.getDirec() == 0) {
						infoShp.direction = 0;
						infoShp.bFlip = true;
					} else {
						infoShp.direction = 1;
						infoShp.bFlip = false;
					} // if else */
				}
			} else {
				infoShp.pct = 1.0;
				infoShp.idArc = pEnlace.getIdArc();

				infoShp.direction = 1;
				if (pEnlace.getDirec() == 1)
					infoShp.bFlip = false;
				else
					infoShp.bFlip = true;
			}

			pilaShapes.push(infoShp);
			if (pNodo.getIdNode() != idStart)
				from_link = pNodo.get_from_link(idEnlace);
		}
		long t2 = System.currentTimeMillis();
		System.out.println("T populate 1 = " + (t2 - t1));

		// Y ahora recorremos hacia atrás el vector y escribimos los shapes.
		// VECTORINFO::iterator theIterator;
		int auxC = 0;

		t1 = System.currentTimeMillis();

		while (!pilaShapes.empty()) {
			infoShp = (InfoShp) pilaShapes.peek();
			g = featExtractor.getGeometry(infoShp.idArc);
			nameStreet = featExtractor.getFieldValue(infoShp.idArc,
					getFieldIndexStreetName());

			MultiLineString line = (MultiLineString) g.toJTSGeometry();

			LineString aux = null;
			if (infoShp.pct < 1.0)
				aux = NetworkUtils.getPartialLineString(line, infoShp.pct,
						infoShp.direction);

			IGeometry geom = null;
			if (aux == null) {
				if (infoShp.bFlip)
					line = line.reverse();
				geom = FConverter.jts_to_igeometry(line);
			} else {
				if (infoShp.bFlip)
					aux = aux.reverse();
				geom = FConverter.jts_to_igeometry(aux);
			}

			route.addRouteFeature(geom, infoShp.idArc, infoShp.cost,
					infoShp.distance, nameStreet.toString());

			pilaShapes.pop();
			auxC++;

		}
//		va.stop();
		t2 = System.currentTimeMillis();
		System.out.println("T populate 2 = " + (t2 - t1));
		return;

	}

	LineString SituaSobreTramo(Geometry geom, int idArc, double pct, int parte) {
		int j, numVertices;
		double longAcum, longReal, longBuscada, distSobre, miniPorcentaje;
		double nuevaX, nuevaY; // Por cuestiones de claridad al programar
		double dist = 0;

		longAcum = 0;
		longReal = geom.getLength();
		longBuscada = longReal * pct;
		Coordinate[] coords = geom.getCoordinates();
		Coordinate c1 = null, c2 = null;
		ArrayList savedCoords = new ArrayList();

		if (parte > 0) // Hemos entrado por el 1 hacia el 2 (al 2 no llegamos)
		{
			for (j = 0; j < coords.length - 1; j++) {
				c1 = coords[j];
				c2 = coords[j + 1];
				dist = c1.distance(c2);
				longAcum += dist;
				savedCoords.add(c1);
				if (longAcum >= longBuscada) {
					// Hasta aquí. Ahora ahi que poner el punto sobre el tramo
					distSobre = dist - (longAcum - longBuscada);
					miniPorcentaje = distSobre / dist;

					nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
					nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

					savedCoords.add(new Coordinate(nuevaX, nuevaY));
					break;
				} // if longAcum >= longBuscada
			} // for j

		} else // Hemos entrado por el 2 hacia el 1
		{
			numVertices = 0;
			for (j = 0; j < coords.length; j++) {
				// //////////////////////////////////////////////////////////////
				// 13_ene_2005: Si el último punto es el último punto no
				// podemos acceder al elemento j+1 porque nos salimos del shape
				// ///////////////////////////////////////////////////////////////
				c1 = coords[j];
				if (j < coords.length - 1) {
					c2 = coords[j + 1];

					dist = c1.distance(c2);
					longAcum += dist;
				}

				if (longAcum >= longBuscada) {
					// Hasta aquí. Empezamos a meter puntos

					if (numVertices == 0) {
						distSobre = dist - (longAcum - longBuscada);
						miniPorcentaje = distSobre / dist;
						nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
						nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

						savedCoords.add(new Coordinate(nuevaX, nuevaY));
					} else {
						savedCoords.add(c2);
					}
					numVertices++;
					// break;
				} // if longAcum >= longBuscada
			} // for j

			// savedCoords.add(c2);

		} // if else

		return geomFactory.createLineString((Coordinate[]) savedCoords
				.toArray(new Coordinate[0]));
	}

	private int getFieldIndexStreetName() {
		return fieldIndexStreetName;
	}

	public IFeatureExtractor getFeatExtractor() {
		return featExtractor;
	}

	public void setFeatExtractor(IFeatureExtractor featExtractor) {
		this.featExtractor = featExtractor;
	}

}
