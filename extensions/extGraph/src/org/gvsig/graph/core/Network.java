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
package org.gvsig.graph.core;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.cresques.cts.IProjection;
import org.gvsig.exceptions.BaseException;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.MultiLineString;

public class Network {
	protected FLyrVect lyrVect;

	protected IGraph graph;

	protected ArrayList flags = new ArrayList();

	protected int numOriginalEdges;

	protected int numOriginalNodes;

	private ArrayList modifiedCosts = new ArrayList();
	private ArrayList flagListeners = new ArrayList();
	private boolean dispatching = true;

	private Hashtable velocities = null;

	private ArrayList<GvTurn> turnCosts = new ArrayList();
	
	private IFeatureExtractor featExtractor = null;

	public void reconstruyeTramo(int idArc) {
		GvNode pN1, pN2;
		int i;
		
		// Si encontramos un enlace con idEdge >= numOriginalEdges, lo cambiamos.
		// Y CON ESE IDarc!!
		// Si hay varios, no pasa nada, volvemos a llamar a esta función con IdTramo

		EdgePair edgePair = graph.getEdgesByIdArc(idArc);
		if (edgePair.getIdEdge() != -1)
		{
			// Restauramos los enlaces de los nodos de ese tramo.
//			pN1 = &Nodos[Arcos[IndiceArcos[idTramo].idArco].idNodo1];
//			pN2 = &Nodos[Arcos[IndiceArcos[idTramo].idArco].idNodo2];
			GvEdge edge = graph.getEdgeByID(edgePair.getIdEdge());
			pN1 = graph.getNodeByID(edge.getIdNodeOrig());
			pN2 = graph.getNodeByID(edge.getIdNodeEnd());

			// Metemos idArco en los enlaces de Nodo1
//			for (i=0; i< pN1.getOutputLinks().size(); i++)
//			{
//				GvEdge auxEdge = (GvEdge) pN1.getOutputLinks().get(i);
//				if (auxEdge.getIdArc() == idArc)
//				{
//					if (auxEdge.getIdEdge() >= numOriginalEdges) 
//					{
//						pN1.getOutputLinks().set(i, graph.getEdgeByID(edgePair.getIdEdge()));
//						break;
//					}
//				}
//			}
			restoreConnectors(edge);
			
		}

		if (edgePair.idInverseEdge != -1)
		{
//			pN1 = &Nodos[Arcos[IndiceArcos[idTramo].idContraArco].idNodo1];
//			pN2 = &Nodos[Arcos[IndiceArcos[idTramo].idContraArco].idNodo2];
			GvEdge edge = graph.getEdgeByID(edgePair.getIdInverseEdge());
			pN1 = graph.getNodeByID(edge.getIdNodeOrig());

//			for (i=0; i< pN1.getOutputLinks().size(); i++)
//			{
//				if (edge.getIdArc() == idArc)
//				{
//					GvEdge auxEdge = (GvEdge) pN1.getOutputLinks().get(i);
//					if (auxEdge.getIdEdge() >= numOriginalEdges) 
//					{
//						pN1.getOutputLinks().set(i, graph.getEdgeByID(edgePair.getIdInverseEdge()));
//						break;
//					}
//				}								
//			}
			restoreConnectors(edge);
		}

		int numEdges = graph.numEdges();
		int numNodes = graph.numVertices();
		for (int idEdge = numEdges-1; idEdge >= numOriginalEdges; idEdge--)
		{
			graph.removeEdge(idEdge);
		}
		for (int idNode = numNodes-1; idNode >= numOriginalNodes; idNode--)
		{
			graph.removeNode(idNode);
		}

	}

	private void restoreConnectors(GvEdge edge) {
		GvNode pN1 = graph.getNodeByID(edge.getIdNodeOrig());
		GvNode pN2 = graph.getNodeByID(edge.getIdNodeEnd());
		for (int iCon = 0; iCon < pN1.getConnectors().size(); iCon++) {
			GvConnector c = pN1.getConnectors().get(iCon);
			if (c.getEdgeOut() != null) {
				if ((c.getEdgeOut().getIdEdge() >= numOriginalEdges) && (c.getEdgeOut().getIdArc() == edge.getIdArc())) {
					c.setEdgeOut(edge);
				}
			}
		}
		for (int iCon = 0; iCon < pN2.getConnectors().size(); iCon++) {
			GvConnector c = pN2.getConnectors().get(iCon);
			if (c.getEdgeIn() != null) {
				if ((c.getEdgeIn().getIdEdge() >= numOriginalEdges) && (c.getEdgeIn().getIdArc() == edge.getIdArc())) {
					c.setEdgeIn(edge);
				}
			}
		}
	}

	/**
	 * Closest ID to this point. -1 if out from tolerance.
	 * @param x
	 * @param y
	 * @param tolerance
	 * @param nearest. Point to receive the nearest point ON arc.
	 * @return
	 */
	public int findClosestArc(double x, double y, double tolerance, Point2D nearestPoint) {
		Point2D p = new Point2D.Double(x, y);
		
		if (featExtractor != null)
		{
			if (! (featExtractor instanceof DefaultFeatureExtractor)) {
				return findClosestArcWithFeatExtractor(p, tolerance, nearestPoint);
			}
		}
		FBitSet bitSet;
		try {
			bitSet = lyrVect.queryByPoint(p, tolerance);
			VectorialAdapter va = (VectorialAdapter) lyrVect.getSource();
			va.start();
			double minDist = tolerance;
			int foundGeom = -1;
			for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet
					.nextSetBit(i + 1)) {
				IGeometry geom;
				geom = va.getShape(i);
				IProjection proj = lyrVect.getProjection();
			    if (proj != null) {
			    	if (!proj.getAbrev().equals(lyrVect.getMapContext().getViewPort().getProjection().getAbrev())){
			    		geom.reProject(lyrVect.getCoordTrans());
			    	}
			    }
				
				Point2D nearest = getNearestPoint(p, geom, tolerance);
				if (nearest != null) {
					double dist = nearest.distance(p);
					if (dist < minDist) {
						minDist = dist;
						foundGeom = i;
						nearestPoint.setLocation(nearest);
					}
				}
			}
			va.stop();
			return foundGeom;
		} catch (BaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return -1;

	}

	private int findClosestArcWithFeatExtractor(Point2D p, double tolerance,
			Point2D nearestPoint) {
		double minDist = tolerance;
		int foundGeom = -1;
		for (int i = 0; i < featExtractor.getNumFeatures(); i++) {
			IGeometry geom = featExtractor.getGeometry(i);
			Point2D nearest = getNearestPoint(p, geom, tolerance);
			if (nearest != null) {
				double dist = nearest.distance(p);
				if (dist < minDist) {
					minDist = dist;
					foundGeom = i;
					nearestPoint.setLocation(nearest);
				}
			}
		}
		return foundGeom;

	}

	protected Point2D getNearestPoint(Point2D point, IGeometry geom,
			double tolerance) {
		Point2D resul = null;
		Coordinate c = new Coordinate(point.getX(), point.getY());

		PathIterator theIterator = geom.getPathIterator(null,
				FConverter.FLATNESS); // polyLine.getPathIterator(null,
										// flatness);
		double[] theData = new double[6];
		double minDist = tolerance;
		Coordinate from = null, first = null;
		while (!theIterator.isDone()) {
			// while not done
			int theType = theIterator.currentSegment(theData);

			switch (theType) {
			case PathIterator.SEG_MOVETO:
				from = new Coordinate(theData[0], theData[1]);
				first = from;
				break;

			case PathIterator.SEG_LINETO:

				// System.out.println("SEG_LINETO");
				Coordinate to = new Coordinate(theData[0], theData[1]);
				LineSegment line = new LineSegment(from, to);
				Coordinate closestPoint = line.closestPoint(c);
				double dist = c.distance(closestPoint);
				if ((dist < minDist)) {
					resul = new Point2D.Double(closestPoint.x, closestPoint.y);
					minDist = dist;
				}

				from = to;
				break;
			case PathIterator.SEG_CLOSE:
				line = new LineSegment(from, first);
				closestPoint = line.closestPoint(c);
				dist = c.distance(closestPoint);
				if ((dist < minDist)) {
					resul = new Point2D.Double(closestPoint.x, closestPoint.y);
					minDist = dist;
				}

				from = first;
				break;

			} // end switch

			theIterator.next();
		}

		return resul;
	}

	/**
	 * TODO: POR TERMINAR!!!
	 * 
	 * @param flag
	 * @return
	 */
	public int creaArcosVirtuales(GvFlag flag) {
		// Devuelve el idNodo del nodo virtual creado.
		/*
		 * 0.- Creamos el nuevo Nodo virtual. 1.- Recorremos los arcos nuevos
		 * mirando su idTramo. 2.- Si existe ese idtramo=> Ya hemos partido
		 * antes ese idTramo. Buscamos el arco virtual que contiene ese nodo y
		 * lo partimos. Ojo, recorrer hasta el final los tramos para asegurarnos
		 * de que es el trozo más pequeño. 3.- Si NO existe, utilizamos el
		 * IndiceArcos para coger los arcos que toca y partirlos.
		 * 
		 * 4.- OJO: Si el porcentaje es 0 ó 100, no partimos el arco, devolvemos
		 * el id del nodo que toca.
		 */
		// NUEVO: 20/7/2004:
		// Cuando trabajamos con sentidos, al partir un arco no podemos insertar
		// 2 nuevos sin mirar
		// si es o no de un único sentido.) (Mirar idArco. Si es -1, no partimos
		// el arco).
		// FIN NUEVO
		int idNodo1, idNodo2;
		int idArco, elIdArco, elIdContraArco;
		boolean encontrado;
		GvNode newNode;

		// Sacamos los idNodos del tramo
		EdgePair edgePair = graph.getEdgesByIdArc(flag.getIdArc());
		if (edgePair.getIdEdge() != -1) {
			// idNodo1 = Arcos[IndiceArcos[idTramo].idArco].idNodo1;
			// idNodo2 = Arcos[IndiceArcos[idTramo].idArco].idNodo2;
			idNodo1 = graph.getEdgeByID(edgePair.getIdEdge()).getIdNodeOrig();
			idNodo2 = graph.getEdgeByID(edgePair.getIdEdge()).getIdNodeEnd();

		} else {
			// idNodo2 = Arcos[IndiceArcos[idTramo].idContraArco].idNodo1;
			// idNodo1 = Arcos[IndiceArcos[idTramo].idContraArco].idNodo2;
			idNodo2 = graph.getEdgeByID(edgePair.getIdInverseEdge())
					.getIdNodeOrig();
			idNodo1 = graph.getEdgeByID(edgePair.getIdInverseEdge())
					.getIdNodeEnd();

		}

		if (flag.getPct() == 0)
			return idNodo1;
		if (flag.getPct() == 1)
			return idNodo2;

		// Creamos el nodo de enmedio

		// if (numNodos == maxNodos) // La jodimos, Tórtola, hay que usar
		// reallocate
		// {
		// // NOTA: ESTO EN DEBUG HACE QUE FALLE AL USAR DESPUES EnlacesSTL. ES
		// POR NO SÉ QUÉ HISTORIA
		// // DEL HEAP. EN RELEASE NO FALLA. (TAMPOCO SÉ SI FASTIDIA ALGO).
		// Nodos = (CNode *) realloc(Nodos,(numNodos + MAX_RESERVA_NODOS) *
		// sizeof(CNode)); // Deberíamos chequear que devuelve algo correcto
		// maxNodos = numNodos + MAX_RESERVA_NODOS;
		// }

		newNode = new GvNode();
		// Nodo = &Nodos[numNodos];

		// pNuevoNodo->idNodo = numNodos;
		newNode.setIdNode(graph.numVertices());

		// OJO: Las coordenadas estas puede que no tengan que ver con la
		// realidad. Algo más correcto
		// sería tener en cuenta el shape de verdad, pero creo que no influye en
		// el resultado final.
		// pNuevoNodo->x = Nodos[idNodo1].x + (Nodos[idNodo2].x -
		// Nodos[idNodo1].x) * Porcentaje;
		// pNuevoNodo->y = Nodos[idNodo1].y + (Nodos[idNodo2].y -
		// Nodos[idNodo1].y) * Porcentaje;
		GvNode node1 = graph.getNodeByID(idNodo1);
		GvNode node2 = graph.getNodeByID(idNodo2);
		newNode.setX(node1.getX() + (node2.getX() - node1.getX())
				* flag.getPct());
		newNode.setY(node1.getY() + (node2.getY() - node1.getY())
				* flag.getPct());
		graph.addNode(newNode);
		Coordinate newC = new Coordinate(newNode.getX(), newNode.getY());

		encontrado = false;

		elIdArco = -1;
		elIdContraArco = -1;

		boolean bIdTramoYaPartido = false;

		// TODO: POR AQUI VOY
		for (idArco = numOriginalEdges; idArco < graph.numEdges(); idArco++) {
			GvEdge addedEdge = graph.getEdgeByID(idArco);
			if (addedEdge.getIdArc() == flag.getIdArc()) {
				bIdTramoYaPartido = true;

				idNodo1 = addedEdge.getIdNodeOrig();
				idNodo2 = addedEdge.getIdNodeEnd();

				// Comprobamos si está enmedio
				GvNode n1 = graph.getNodeByID(idNodo1);
				GvNode n2 = graph.getNodeByID(idNodo2);
				Coordinate c1 = new Coordinate(n1.getX(), n1.getY());
				Coordinate c2 = new Coordinate(n2.getX(), n2.getY());
				LineSegment line = new LineSegment(c1, c2);
				double t = line.projectionFactor(newC);

				// Si la proyección es positiva y menor que la magnitud d, está
				// en medio
				if ((t >= 0) && (t <= 1)) {
					encontrado = true;
					if (t == 0)
						return idNodo1; // No partimos
					if (t == 1)
						return idNodo2; // Tampoco partimos

					if (addedEdge.getDirec() == 1)
						elIdArco = idArco;
					else
						elIdContraArco = idArco;

				} // if está enmedio
			} // if idTramo encontrado
		} // for idArco
		if (bIdTramoYaPartido && (!encontrado))
			throw new RuntimeException(
					"Algo va mal con lo del producto escalar");

		if (encontrado) {
			// sprintf(Mensaje,"Voy a partir el idTramo= %ld (idArco
			// %ld)",idTramo,elIdArco);
			// MessageBox(NULL,Mensaje,"",MB_OK);
			if (elIdArco != -1)
				PartirArco(elIdArco, newNode.getIdNode());

			if (elIdContraArco != -1)
				PartirArco(elIdContraArco, newNode.getIdNode());
		} else {
			// Creamos 2 Arcos por cada arco que teníamos antes.
			if (edgePair.getIdEdge() != -1)
				PartirArco(edgePair.getIdEdge(), newNode.getIdNode());

			if (edgePair.getIdInverseEdge() != -1)
				PartirArco(edgePair.getIdInverseEdge(), newNode.getIdNode());

		} // else encontrado

		return newNode.getIdNode();

	}

	/**
	 * Cogemos el nodo más cercano y ponemos el pct a ese flag.
	 * 
	 * @param flag
	 * @return
	 */
	public int getClosestIdNode(GvFlag flag) {
		EdgePair pair = graph.getEdgesByIdArc(flag.getIdArc());
		if (pair.getIdEdge() != -1) {
			GvEdge edge = graph.getEdgeByID(pair.getIdEdge());
			GvNode from = graph.getNodeByID(edge.getIdNodeOrig());
			GvNode to = graph.getNodeByID(edge.getIdNodeEnd());

			double dist1 = flag.getOriginalPoint().distance(from.getX(),
					from.getY());
			double dist2 = flag.getOriginalPoint().distance(to.getX(),
					to.getY());
			if (dist1 < dist2) {
				flag.setPct(0);
				return from.getIdNode();
			}
			else
			{
				flag.setPct(1.0);
				return to.getIdNode();
			}
		} else {
			GvEdge edge = graph.getEdgeByID(pair.getIdInverseEdge());
			GvNode from = graph.getNodeByID(edge.getIdNodeOrig());
			GvNode to = graph.getNodeByID(edge.getIdNodeEnd());

			double dist1 = flag.getOriginalPoint().distance(from.getX(),
					from.getY());
			double dist2 = flag.getOriginalPoint().distance(to.getX(),
					to.getY());
			if (dist1 < dist2)
			{
				flag.setPct(0);
				return from.getIdNode();
			}
			else
			{
				flag.setPct(1.0);
				return to.getIdNode();
			}
			// if (flag.getPct() < 0.5)
			// return to.getIdNode();
			// else
			// return from.getIdNode();
		}
	}

	/**
	 * @param idArc
	 * @param x
	 * @param y
	 * @return entre 0.0 y 1.0
	 * @throws DriverIOException
	 */
	private double percentAlong(int idArc, double x, double y)
			throws BaseException {
		// Le pasamos el idTramo, la coordenada X de donde hemos pulsado y la
		// coordenada Y
		// Primero calculamos la longitud total del shape.
		// Luego calculamos el punto más cercano y su distancia para cada
		// segmento del shape.
		// Nos quedamos con el que esté más cerca y luego recorremos hasta él
		// acumulando distancia.
		// Finalmente, dividimos esa distancia por la longitud total.
//		lyrVect.getSource().start();
//		IGeometry geom = lyrVect.getSource().getShape(idArc);
		IGeometry geom = featExtractor.getGeometry(idArc);
		MultiLineString jtsGeom = (MultiLineString) geom.toJTSGeometry();

		Coordinate[] coords = jtsGeom.getCoordinates();

		Coordinate userCoord = new Coordinate(x, y);

		double longReal = 0;
		// Le pegamos una primera pasada para saber su longitud real.
		// OJO, NO TRABAJAMOS CON SHAPES MULTIPARTE, NO TIENE SENTIDO CON LAS
		// REDES (CREO)
		// POR ESO SUPONEMOS UNA ÚNICA PARTE (LÍNEA CONTÍNUA)
		// A la vez calculamos el punto más cercano y su distancia para cada
		// segmento.
		double minDist = Double.MAX_VALUE;
		double distTo = 0;
		double dist = 0;
		Coordinate cOrig = null;
		Coordinate closestPoint = null;
		for (int j = 0; j < coords.length - 1; j++) {
			Coordinate c1 = coords[j];
			Coordinate c2 = coords[j + 1];
			LineSegment line = new LineSegment(c1, c2);

			Coordinate auxPoint = line.closestPoint(userCoord);
			dist = userCoord.distance(auxPoint);
			if ((dist < minDist)) {
				minDist = dist;
				cOrig = c1;
				closestPoint = auxPoint;
				distTo = longReal;
			}
			longReal += line.getLength();
		}
//		lyrVect.getSource().stop();
		dist = cOrig.distance(closestPoint);
		double longBuscada = distTo + dist;

		double pct;
		if (longReal > 0)
			pct = longBuscada / longReal;
		else
			pct = 0.0;

		return pct;
	}

	/**
	 * Adds a flag on a network. flagDirection set if the flag must be on left
	 * or right edge.
	 * 
	 * @param x
	 * @param y
	 * @param flagDirection
	 * @param tol
	 *            tolerance in map units
	 * @return null if there is no place to add flag. You can increase the
	 *         tolerance, then.
	 * @throws GraphException
	 */
	public GvFlag addFlag(double x, double y, int flagDirection, double tol)
			throws GraphException {
		try {
			Point2D nearestPoint = new Point2D.Double();
			int idArc = findClosestArc(x, y, tol, nearestPoint);
			if (idArc == -1)
				return null;
			GvFlag flag = new GvFlag(x, y);
			flag.setIdArc(idArc);

			flag.setPct(percentAlong(idArc, x, y));
			flag.setDirec(flagDirection);
			flag.setIdFlag(flags.size());
			callFlagsChanged(IFlagListener.FLAG_ADDED);
			return flag;
		} catch (BaseException e) {
			e.printStackTrace();
			throw new GraphException(e);
		}

	}

	/**
	 * Adds 2 flags on a network. (On both sides of an arc)
	 * 
	 * @param x
	 * @param y
	 * @param tol
	 *            tolerance in map units
	 * @return null if there is no place to add flag. You can increase the
	 *         tolerance, then.
	 * @throws GraphException 
	 */
	public GvFlag addFlag(double x, double y, double tol) throws GraphException {
		try {
			Point2D nearestPoint = new Point2D.Double();
			int idArc = findClosestArc(x, y, tol, nearestPoint);
			if (idArc == -1)
				return null;

			GvFlag flag = new GvFlag(x, y);
			flag.setIdArc(idArc);
			EdgePair edgePair = graph.getEdgesByIdArc(idArc);
			flag.setDirec(GvFlag.BOTH_DIRECTIONS);

			flag.setPct(percentAlong(idArc, x, y));
			flag.setIdFlag(flags.size());
			flags.add(flag);
			callFlagsChanged(IFlagListener.FLAG_ADDED);
			return flag;
		} catch (BaseException e) {
			e.printStackTrace();
			throw new GraphException(e);
		}

	}

	/**
	 * Create a flag in both directions, but NOT add it to the Network.
	 * We use it on onetomany solver
	 * @param x
	 * @param y
	 * @param tol
	 * @return
	 * @throws GraphException
	 */
	public GvFlag createFlag(double x, double y, double tol) throws GraphException {
		try {
			Point2D nearestPoint = new Point2D.Double();
			int idArc = findClosestArc(x, y, tol, nearestPoint);
			if (idArc == -1)
				return null;

			GvFlag flag = new GvFlag(x, y);
			flag.setIdArc(idArc);
//			EdgePair edgePair = graph.getEdgesByIdArc(idArc);
			flag.setDirec(GvFlag.BOTH_DIRECTIONS);

			flag.setPct(percentAlong(idArc, x, y));
			flag.setIdFlag(flags.size());
			return flag;
		} catch (BaseException e) {
			e.printStackTrace();
			throw new GraphException(e);
		}

	}

	public GvFlag addFlagToNode(double x, double y, double tol) throws GraphException {
		Point2D nearestPoint = new Point2D.Double();
		int idArc = findClosestArc(x, y, tol, nearestPoint);
		if (idArc == -1)
			return null;

		GvFlag flag = new GvFlag(x, y);
		flag.setIdArc(idArc);
		flag.setDirec(GvFlag.BOTH_DIRECTIONS);
		int idNode = getClosestIdNode(flag);
		
		GvNode node = graph.getNodeByID(idNode);
		flag.setOriginalPoint(node.getX(), node.getY());
		flag.setIdFlag(flags.size());
		flags.add(flag);
		callFlagsChanged(IFlagListener.FLAG_ADDED);
		return flag;

	}


	public void addFlag(GvFlag flag) {
		flags.add(flag);
		callFlagsChanged(IFlagListener.FLAG_ADDED);
	}

	public GvFlag[] getFlags() {
		ArrayList aux = new ArrayList();
		for (int i=0; i < getOriginaFlags().size(); i++)
		{
			GvFlag flag = (GvFlag) getOriginaFlags().get(i);
			if (flag.isEnabled()) aux.add(flag);
		}

		return (GvFlag[]) aux.toArray(new GvFlag[0]);
	}
	
	public int getFlagsCount(){
		return this.getOriginaFlags().size();
	}
	
	/**
	 * Suitable to change directly the flags collection
	 * @return
	 */
	public ArrayList getOriginaFlags() {
		return flags;
	}


	public void setFlags(ArrayList flags) {
		this.flags = flags;
	}

	public IGraph getGraph() {
		return graph;
	}

	public void setGraph(IGraph graph) {
		this.graph = graph;
		numOriginalEdges = graph.numEdges();
		numOriginalNodes = graph.numVertices();
	}

	public FLyrVect getLayer() {
		return lyrVect;
	}

	public void setLayer(FLyrVect lyr) {
		this.lyrVect = lyr;
		this.featExtractor = new DefaultFeatureExtractor(lyr);
		// FJP: Workarround to avoid using SpatialIndex with reprojected layers
	    if (lyrVect.getCoordTrans() != null) {
	    	if (!lyrVect.getProjection().getAbrev().equals(lyrVect.getMapContext().getViewPort().getProjection().getAbrev()))
	    		lyrVect.setISpatialIndex(null);
		}
		
		// fin 
		
	}

	public void removeFlags() {
		flags = new ArrayList();
		callFlagsChanged(IFlagListener.FLAG_REMOVED);
	}
	
	public void removeFlag(GvFlag flag){
		flags.remove(flag);
		callFlagsChanged(IFlagListener.FLAG_REMOVED);
	}

	void PartirArco(int idEdge, int idNode) {
		// Se supone que el nuevo Nodo YA está creado. Aqui dentro se coge el
		// arco viejo y se le pega un tajo.
		// (Se modifican los enlaces de los nodos de ese arco y se crean los
		// arcos nuevos, fijando sus costes).
		// Para sacar el porcentaje nos aprovechamos de que el nuevo nodo está
		// puesto en base a ese porcentaje
		// en distancia de los extremos.
		GvEdge oldEdge;
		GvNode pN1, pN2;
		double pct;

		oldEdge = graph.getEdgeByID(idEdge);

		// OJO, controlando los ceros por si acaso la recta es horizontal o
		// vertical (Y si mide cero???)

		// pN1 = &Nodos[Arcos[idArco].idNodo1];
		// pN2 = &Nodos[Arcos[idArco].idNodo2];
		pN1 = graph.getNodeByID(graph.getEdgeByID(idEdge).getIdNodeOrig());
		pN2 = graph.getNodeByID(graph.getEdgeByID(idEdge).getIdNodeEnd());
		GvNode newNode = graph.getNodeByID(idNode);

		if (newNode.getX() != pN1.getX())
			pct = Math.abs((newNode.getX() - pN1.getX())
					/ (pN2.getX() - pN1.getX()));
		else
			pct = Math.abs((newNode.getY() - pN1.getY())
					/ (pN2.getY() - pN1.getY()));

		GvEdge first = new GvEdge();
		first.setIdEdge(graph.numEdges());
		first.setIdArc(oldEdge.getIdArc());
		first.setDistance(oldEdge.getDistance() * pct);
		first.setWeight(oldEdge.getWeight() * pct);

		first.setDirec(oldEdge.getDirec());
		first.setIdNodeOrig(oldEdge.getIdNodeOrig());
		first.setType(oldEdge.getType());
		first.setIdNodeEnd(idNode);
		graph.addEdge(first);

		GvEdge second = new GvEdge();
		second.setIdEdge(graph.numEdges());
		second.setDistance(oldEdge.getDistance() * (1.0 - pct));
		second.setWeight(oldEdge.getWeight() * (1.0 - pct));
		second.setIdArc(oldEdge.getIdArc());
		second.setDirec(oldEdge.getDirec());
		second.setType(oldEdge.getType());
		second.setIdNodeOrig(idNode);
		second.setIdNodeEnd(oldEdge.getIdNodeEnd());
		graph.addEdge(second);

		// ////////////////////////////////////////////////////
		// Ahora retocamos los enlaces que salen de cada nodo
		// ////////////////////////////////////////////////////
		int i;
		// boolean encontrado = false;
//		for (i = 0; i < pN1.getOutputLinks().size(); i++) {
//			GvEdge aux = (GvEdge) pN1.getOutputLinks().get(i);
//			if (aux.getIdEdge() == idEdge) {
//				pN1.getOutputLinks().set(i, first);
//				// encontrado = true;
//				break;
//			}
//		} // for
		for (i = 0; i < pN1.getConnectors().size(); i++) {
			GvConnector c = pN1.getConnectors().get(i);
			if ((c.getEdgeOut() != null) && (c.getEdgeOut().getIdEdge()== idEdge)) {
				c.setEdgeOut(first);
				// encontrado = true;
				break;
			}
		} // for

		// Conector de entrada 
//		GvConnector newCon = new GvConnector();
//		newCon.setEdgeIn(first);
//		newNode.getConnectors().add(newCon);
		addInputLink(newNode, first);

		// Conector de salida 
//		GvConnector conOut = new GvConnector();
//		conOut.setEdgeOut(second);
//		newNode.getConnectors().add(conOut);
		addOutputLink(newNode, second);
		
		// Y hacemos que el conector de entrada del idNodo2 tenga el arco nuevo
		// log("Y hacemos que el conector de entrada del idNodo2 tenga el arco nuevo");
		for (i = 0; i < pN2.getConnectors().size(); i++) {
			GvConnector c = pN2.getConnectors().get(i);
			if ((c.getEdgeIn() != null) && (c.getEdgeIn().getIdEdge()== idEdge)) {
				c.setEdgeIn(second);
				// encontrado = true;
				break;
			}
		} // for
	}
	
	/**
	 * Add an edge out to this node. This function takes care of creating the needed connectors
	 * @param edge
	 */
	private void addOutputLink(GvNode n, GvEdge edge) {
//		outputLinks.add(edge);
		// Create connectors
		// First, search the connector if it is already created
		GvConnector c;
		boolean bFound = false;
		GvConnector cFound = null;
		for (int iConec=0; iConec< n.getConnectors().size();  iConec++)
		{
			c = n.getConnectors().get(iConec);
			if ((c.getEdgeIn() != null) && (c.getEdgeIn().getIdNodeOrig() == edge.getIdNodeEnd())
				&& (c.getEdgeIn().getIdNodeEnd() == edge.getIdNodeOrig())) {
				// Found. This connector has been originated before by the same edge
				bFound = true;
				cFound = c;
				break;
			}
		}
		if (!bFound) {
			GvConnector newCon = new GvConnector();
			newCon.setEdgeOut(edge);
			n.getConnectors().add(newCon);
		}
		else
		{
			cFound.setEdgeOut(edge);
		}
		
	}

	/**
	 * Add an input edge to this node. This function takes care of creating the needed connectors
	 * @param edge
	 */
	private void addInputLink(GvNode n, GvEdge edge) {
//		inputLinks.add(edge);
		// First, search the connector if it is already created
		GvConnector c;
		boolean bFound = false;
		GvConnector cFound = null;
		for (int iConec=0; iConec< n.getConnectors().size();  iConec++)
		{
			c = n.getConnectors().get(iConec);
			if ((c.getEdgeOut() != null) && (c.getEdgeOut().getIdNodeOrig() == edge.getIdNodeEnd())
					&& (c.getEdgeOut().getIdNodeEnd() == edge.getIdNodeOrig())) {
				// Found. This connector has been originated before by the same arc
				bFound = true;
				cFound = c;
				break;
			}
		}
		if (!bFound) {
			GvConnector newCon = new GvConnector();
			newCon.setEdgeIn(edge);
			n.getConnectors().add(newCon);
		}
		else
		{
			cFound.setEdgeIn(edge);
		}
		
	}
	

	public ArrayList getModifiedCosts() {
		return modifiedCosts;
		
	}
	
	private int[] BuscaNodosDeTramo(EdgePair pair) {
		int[] resul = new int[2];
		if ((pair.idEdge == -1) && (pair.idInverseEdge == -1))
		{
			return null; // Error: No existen esos arcos.
		}

		if (pair.idEdge != -1)
		{
			resul[0] = graph.getEdgeByID(pair.idEdge).getIdNodeOrig();
			resul[1]= graph.getEdgeByID(pair.idEdge).getIdNodeEnd();
		}
		else
		{
			resul[0] = graph.getEdgeByID(pair.idInverseEdge).getIdNodeOrig();
			resul[1]= graph.getEdgeByID(pair.idInverseEdge).getIdNodeEnd();
		}
		return resul;

	}

	public GvTurn addTurnCost(int idArcOrigin, int idArcDestination, double newCost) {
		GvTurn turnCost = new GvTurn(idArcOrigin, idArcDestination, newCost);		
		EdgePair edgePairFrom = getGraph().getEdgesByIdArc(idArcOrigin);
		EdgePair edgePairTo = getGraph().getEdgesByIdArc(idArcDestination);
		
		// We found the node that connects these arcs, 
		// and add the new turnCost to its list of turnCosts.
		GvNode searchedNode;
		int idNa1, idNa2, idNb1, idNb2, idNodoBuscado;
		int[] A, B;

		A = BuscaNodosDeTramo(edgePairFrom);
		if (A == null) return null; // No existen arcos para ese idTramo
		

		B = BuscaNodosDeTramo(edgePairTo);
		if (B == null) return null; // No existen arcos para ese idTramo

		idNa1 = A[0]; idNa2 = A[1];
		idNb1 = B[0]; idNb2 = B[1];

		// Buscamos el nodo que está entre fromIdTramo y toIdTramo
		// y el arco. Al arco hay que cambiarle el destino
		if (idNa1 == idNb1)
			idNodoBuscado = idNa1;
		else
			if (idNa1 == idNb2)
				idNodoBuscado = idNa1;
			else
				if (idNa2 == idNb1)
					idNodoBuscado = idNa2;
				else
					if (idNa2 == idNb2)
						idNodoBuscado = idNa2;
					else // ERROR
						return null; // esos tramos no conectan.

		// Podemos funcionar con idTramo en lugar de idArco porque cada CLink lleva dentro
		// el idTramo que lo originó, y en el algoritmo podemos mirarlo.

		searchedNode = graph.getNodeByID(idNodoBuscado);
		searchedNode.addTurnCost(turnCost);			
		turnCosts.add(turnCost); //useful to remove them one by one without iterating the whole graph.
		
		return turnCost; // everything is fine
	}
	/**
	 * Create, add and apply a new modified cost to the graph. 
	 * @param idArc where the cost will be applied.
	 * @param newCost. -1 if you want tu put a BARRIER.
	 * @param direction. 1-> edge of digitalized direction. 2-> inverse edge. 3-> Both directions
	 */
	public GvModifiedCost addModifiedCost(int idArc, double newCost, int direction) {
		GvModifiedCost modifiedCost = new GvModifiedCost(idArc, newCost, direction);
		EdgePair edgePair = getGraph().getEdgesByIdArc(idArc);
		modifiedCost.setIdEdge(edgePair.idEdge);
		modifiedCost.setIdInverseEdge(edgePair.idInverseEdge);
		if (direction == 3)
		{
			if (edgePair.getIdEdge() != -1)
			{
				GvEdge edge = getGraph().getEdgeByID(edgePair.getIdEdge());
				modifiedCost.setOldCost(edge.getWeight());
				edge.setWeight(-1.0);
			}
			if (edgePair.getIdInverseEdge() != -1)
			{
				GvEdge inverseEdge = getGraph().getEdgeByID(edgePair.getIdInverseEdge());
				modifiedCost.setOldInverseCost(inverseEdge.getWeight());
				inverseEdge.setWeight(-1.0);
			}
		}
		if (direction == 1)
		{
			if (edgePair.getIdEdge() != -1)
			{
				GvEdge edge = getGraph().getEdgeByID(edgePair.getIdEdge());
				modifiedCost.setOldCost(edge.getWeight());
				edge.setWeight(-1.0);
			}
		}
		if (direction == 2)
		{
			if (edgePair.getIdInverseEdge() != -1)
			{
				GvEdge inverseEdge = getGraph().getEdgeByID(edgePair.getIdInverseEdge());
				modifiedCost.setOldInverseCost(inverseEdge.getWeight());
				inverseEdge.setWeight(-1.0);
			}
		}
		modifiedCosts.add(modifiedCost);
		modifiedCost.setApplied(true);
		return modifiedCost;
	}
	
	/**
	 * Remove ALL turn costs
	 */
	public void removeTurnCosts() {
		for (int i=0; i < turnCosts.size(); i++) {
			GvTurn turn = turnCosts.get(i);
//			turn.getNode().getTurnCosts().remove(turn);
			if (turn.getNode() == null)
			{
				System.err.println("El turnCost " + i + " no tiene nodo asociado.");
				continue;
			}
			turn.getNode().removeTurnCosts();
		}
		turnCosts = new ArrayList<GvTurn>();
	}

	/**
	 * Be careful about the ORDER!!!!
	 * @param modifiedCost
	 */
	public boolean removeModifiedCost(GvModifiedCost modifiedCost) {
		if (!modifiedCosts.remove(modifiedCost))
			return false;
		int idArc = modifiedCost.getIdArc();
		int direction = modifiedCost.getDirection();
		EdgePair edgePair = getGraph().getEdgesByIdArc(idArc);
		if (direction == 3)
		{
			if (edgePair.getIdEdge() != -1)
			{
				GvEdge edge = getGraph().getEdgeByID(edgePair.getIdEdge());
				edge.setWeight(modifiedCost.getOldCost());
			}
			if (edgePair.getIdInverseEdge() != -1)
			{
				GvEdge inverseEdge = getGraph().getEdgeByID(edgePair.getIdInverseEdge());
				inverseEdge.setWeight(modifiedCost.getOldInverseCost());
			}
		}
		if (direction == 1)
		{
			if (edgePair.getIdEdge() != -1)
			{
				GvEdge edge = getGraph().getEdgeByID(edgePair.getIdEdge());
				edge.setWeight(modifiedCost.getOldCost());
			}
		}
		if (direction == 2)
		{
			if (edgePair.getIdInverseEdge() != -1)
			{
				GvEdge inverseEdge = getGraph().getEdgeByID(edgePair.getIdInverseEdge());
				inverseEdge.setWeight(modifiedCost.getOldInverseCost());
			}
		}
		return true;
	}

	public void addFlagListener(IFlagListener listener) {
		if (!flagListeners.contains(listener)) {
			flagListeners.add(listener);
		}
	}
	
	public void removeFlagListener(IFlagListener listener){
		flagListeners.remove(listener);
	}
	
	private void callFlagsChanged(int reason) {
		if (dispatching) {
			for (int i=0; i < flagListeners.size(); i++)
			{
				IFlagListener listener = (IFlagListener) flagListeners.get(i);
				listener.flagsChanged(reason);
			}
		}
	}
	
	/**
	 * Useful to do batch modifies. (For example, add lot of flags
	 * and when finished (endModifying), throw event.
	 */
	public void beginModifyingFlags() {
		dispatching = false;
	}

	public void endModifyingFlags() {
		dispatching = true;
		callFlagsChanged(IFlagListener.FLAG_MANY_CHANGES);
	}
	/**
	 * Mueve un flag de la posición from a la posición to.
	 *
	 * @param from origen.
	 * @param to destino.
	 * 
	 */
	public void moveTo(int from, int to) throws CancelationException {
		int newfrom=flags.size()-from-1;
		int newto=flags.size()-to-1;
		if ( newfrom < 0 || newfrom >=flags.size() || newto < 0 || newto >= flags.size()) return;
		GvFlag aux = (GvFlag) flags.get(newfrom);
		flags.remove(newfrom);
		flags.add(newto, aux);
		callFlagsChanged(IFlagListener.FLAG_REORDER);
	}

	public ArrayList getEdgeTypes() {
		// TODO: Tener esto precalculado
		TreeMap map = new TreeMap();
		ArrayList ret = new ArrayList();
		for (int i = 0; i < graph.numEdges(); i++)
		{
			GvEdge edge = graph.getEdgeByID(i);
			Integer type = new Integer(edge.getType());
			if (!map.containsKey(type))
			{
				map.put(type, type);				
			}
		}
		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry) it.next();
			Integer type = (Integer) entry.getKey();
			ret.add(type);
		}
		return ret;
	}

	public Hashtable getVelocities() {
		return velocities ;
	}

	public void setVelocities(Hashtable veloMeters) {
		for (int i=0; i < getGraph().numEdges(); i++)
		{
			GvEdge edge = getGraph().getEdgeByID(i);
			
			Integer key = new Integer(edge.getType());
			Double vel = (Double) veloMeters.get(key);
			edge.setWeight(edge.getDistance() / vel.doubleValue()); // segundos
		}
		this.velocities = veloMeters;

		
	}

	public ArrayList<GvTurn> getTurnCosts() {
		return turnCosts;
	}

	public IFeatureExtractor getFeatExtractor() {
		return featExtractor;
	}

	public void setFeatExtractor(IFeatureExtractor featExtractor) {
		this.featExtractor = featExtractor;
		if (featExtractor instanceof DefaultFeatureExtractor) {
			setLayer(((DefaultFeatureExtractor) featExtractor).getLyrVect());
		}
	}

}
