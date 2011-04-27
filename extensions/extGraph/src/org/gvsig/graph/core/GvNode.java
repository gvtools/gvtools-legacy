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

import java.util.ArrayList;

public class GvNode {
	public final static int statNotInList = 0;
	public final static int statNowInList = 1;
	public final static int statWasInList = 2;
	private int idNode;
	private double x;
	private double y;
	
//	int from_link = -1; // id del Arco desde el que hemos llegado
	int   numSoluc = 0; // Empezamos con esto a cero en toda la red. 
					// De manera global, habrá una variable numSolucGlobal que indica el nº de cada petición de ruta.
					// Sirve para no tener que inicializar siempre tooooda la red. Lo que hacemos es comparar el 
					// nº de petición global con este. Si no coinciden, antes de hacer nada hay que inicializar su
					// best_cost a infinito.

//	double best_cost = Double.MAX_VALUE;
	double accumulatedLength = 0;
	double stimation = Double.MAX_VALUE; // bestCost + something related to destiny's euclidean length
	int status;
//	ArrayList<GvEdge> outputLinks  = new ArrayList<GvEdge>(); // Neighbors links
//	ArrayList<GvEdge> inputLinks  = new ArrayList<GvEdge>(); // links with end node in this node.
	
	ArrayList<GvConnector> connectors = new ArrayList<GvConnector>(2);
	
	ArrayList<GvTurn> turnCosts = new ArrayList<GvTurn>(0); // Turn costs. If a GvTurnCost exists, we add its cost.
						  // If the cost is < 0, it is a prohibited cost.

	public GvNode() {
		initialize();
	}
	
	public void initialize() {
		numSoluc = GlobalCounter.numSolucGlobal;
//		from_link = -1;
//		best_cost = Double.MAX_VALUE;
		stimation = Double.MAX_VALUE;
		accumulatedLength = 0;
		status = statNotInList;
		GvConnector c;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);

			c.setFrom_link_c(-1);
			c.setBestCostOut(Double.MAX_VALUE);
			c.setBestCostIn(Double.MAX_VALUE);
			c.setMustBeRevised(true);
		}
		

	}
	
	public int getIdNode() {
		return idNode;
	}
	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
//	public double getBestCost() {
//		if (numSoluc != GlobalCounter.getGlobalSolutionNumber())
//			return Double.MAX_VALUE;
//		return best_cost;
//	}
//	public void setBestCost(double best_cost) {
//		this.best_cost = best_cost;
//	}
//	public ArrayList getOutputLinks() {
//		return outputLinks;
//	}
	public double getStimation() {
		return stimation;
	}
	public void setStimation(double estimacion) {
		this.stimation = estimacion;
	}
//	public int getFromLink() {
//		return from_link;
//	}
//	public void setFromLink(int from_link) {
//		this.from_link = from_link;
//	}
	public ArrayList<GvTurn> getTurnCosts() {
		return turnCosts;
	}
	
	public void setTurnCosts(ArrayList<GvTurn> turns) {
		this.turnCosts = turns;
	}
	public int getNumSoluc() {
		return numSoluc;
	}
	public void setNumSoluc(int numSoluc) {
		this.numSoluc = numSoluc;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public void calculateStimation(GvNode finalNode, double newCost) {
		double DeltaX = ((finalNode.getX() - x)/1000.0)*((finalNode.getX() - x)/1000.0);
		double DeltaY = ((finalNode.getY() - y)/1000.0)*((finalNode.getY() - y)/1000.0);
		double distLineaRecta = Math.sqrt(DeltaX + DeltaY); // En Km
		stimation = newCost + (distLineaRecta* 30.0);  // Segundos que tardamos en recorrer esos Km a 120 Km/hora

		
	}

	public double getAccumulatedLength() {
		return accumulatedLength;
	}

	public void setAccumulatedLength(double accumulatedLength) {
		this.accumulatedLength = accumulatedLength;
	}
	
	public int getOutputDegree() {
		int numOutLink = 0;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			GvConnector c = connectors.get(iConec);

			if (c.getEdgeOut() == null) continue;
			numOutLink++;			
		}
		return numOutLink;
	}

	public int getInputDegree() {
		int numInLink = 0;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			GvConnector c = connectors.get(iConec);

			if (c.getEdgeIn() == null) continue;
			numInLink++;			
		}
		return numInLink;

	}

	public EdgeOutIterator getOutputEdgeIterator() {
		return new EdgeOutIterator(this);
	}
	
	public EdgeInIterator getInputEdgeIterator() {
		return new EdgeInIterator(this);
	}


	/**
	 * Returns the link that we have used to reach idEdgeOut. Important for turn costs.
	 * @param idArcoS
	 * @return
	 */
	public int get_from_link(long idEdgeOut)
	{
		// Queremos saber desde qué arco hemos entrado para llegar a este arco.
		for (int iConec =0; iConec < connectors.size(); iConec++) {
			GvConnector c = connectors.get(iConec);
			if (c.getEdgeOut() != null) {
				if (c.getEdgeOut().getIdEdge() == idEdgeOut)
				{
					return c.getFrom_link_c();
				}
			}
			
		}
		// TODO: throw exception??
		return -1;
				
	}
	public int get_best_from_link()
	{
		// Queremos saber desde qué arco hemos entrado para llegar a este arco.
		GvConnector c;
		int best_link = -1;
		double bestCost = Double.MAX_VALUE;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			if (c.getBestCostOut() < bestCost)
			{
				bestCost = c.getBestCostOut();
				best_link = c.getFrom_link_c();
			}
			
		}
		return best_link;
				
	}

	public boolean existeMejora(GvEdge edge, double newCost)
	{
		// Si entrando desde ese arco mejoramos el coste de algún conector, devolvemos que 
		// hay mejora
		// NO QUEREMOS TOCAR EL CONECTOR PROPIO!!!! (Para evitar que en un nodo hagamos un U-turn) (Pero sí hay que marcarlo como con mejora, si existe)
		// Y tampoco hay que tener en cuenta los costes de giros para los conectores que toque.
		boolean hayMejora = false;
		GvConnector c;
		double auxCost;
		boolean bGiroProhibido;
		double costeGiro;
		// char Msg[200];

		// log("Dentro de existeMejora");
	
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			// sprintf(Msg,"Conector iConec=%ld de %ld. Capacity=%ld", iConec, Conectores.size(), Conectores.capacity());
			// log(Msg);
			auxCost = newCost;
			c = connectors.get(iConec);
			// sprintf(Msg,"pC->idArcoS=%ld, pc-idArcoE=%ld", pC->idArcoS, pC->idArcoE);
			// log(Msg);

			if (c.getEdgeOut() == null) continue;

			// Miramos la lista de Giros de ese nodo
			bGiroProhibido = false;
			costeGiro = 0;
			for (int idGiro=0; idGiro < getTurnCosts().size(); idGiro++)
			{
				// Si está prohibido, a por otro
				GvTurn elGiro = getTurnCosts().get(idGiro);
				// log("ANTES del if");
				if ((elGiro.getIdArcFrom() == edge.getIdArc()) && 
					(elGiro.getIdArcTo() == c.getEdgeOut().getIdArc()))
				{
					if (elGiro.getCost() < 0)
					{
						bGiroProhibido = true;
					}
					else
						costeGiro = elGiro.getCost();

					break; // Salimos del for porque ya hemos encontrado el giro
				}
				// log("DESPUES del if");
			}

			auxCost = newCost + costeGiro;
//			if (c.getEdgeIn() == edge) // Para evitar los U-turn
//			{
//				if (c.getBestCostIn() > newCost)
//				{
//					c.setBestCostIn(newCost);
//					c.setFrom_link_c(edge.getIdEdge());
//					hayMejora = true;
//				}
//				continue; // Miramos solo los conectores distintos al de entrada
//			}
			// Si está prohibido, vamos a por otro enlace
			if (bGiroProhibido)
			{
				c.setMustBeRevised(true);
				// sprintf(Msg, "Encontrado giro prohibido en conector %ld del nodo %ld", iConec, idNodo);
				// log(Msg);
				continue;					
			}
			
			c.setMustBeRevised(false);															
			if (c.getBestCostOut() > auxCost)
			{
				hayMejora = true;
				c.setFrom_link_c(edge.getIdEdge());
				c.setBestCostOut(auxCost);
				c.setMustBeRevised(true);
				// sprintf(Msg, "HAY MEJORA: idNodo=%ld, iConec=%ld, idArcoEntrada=%ld, auxCost= %lf", idNodo, iConec, idArcoEntrada, auxCost);
			}
		}
		return hayMejora;
				
	}
	public boolean reverseFoundBetterPath(GvEdge edge, double newCost)
	{
		// Si entrando desde ese arco mejoramos el coste de algún conector, devolvemos que 
		// hay mejora
		// NO QUEREMOS TOCAR EL CONECTOR PROPIO!!!!
		// Y tampoco hay que tener en cuenta los costes de giros para los conectores que toque.
		boolean hayMejora = false;
		GvConnector c;
		double auxCost;
		boolean bGiroProhibido;
		double costeGiro;
		// char Msg[200];

		// log("Dentro de existeMejora");
	
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			// sprintf(Msg,"Conector iConec=%ld de %ld. Capacity=%ld", iConec, Conectores.size(), Conectores.capacity());
			// log(Msg);
			auxCost = newCost;
			c = connectors.get(iConec);
			// sprintf(Msg,"pC->idArcoS=%ld, pc-idArcoE=%ld", pC->idArcoS, pC->idArcoE);
			// log(Msg);

			if (c.getEdgeIn() == null) continue;

			// Miramos la lista de Giros de ese nodo
			bGiroProhibido = false;
			costeGiro = 0;
			for (int idGiro=0; idGiro < getTurnCosts().size(); idGiro++)
			{
				// Si está prohibido, a por otro
				GvTurn elGiro = getTurnCosts().get(idGiro);
				// log("ANTES del if");
				if ((elGiro.getIdArcTo() == edge.getIdArc()) && 
					(elGiro.getIdArcFrom() == c.getEdgeIn().getIdArc()))
				{
					if (elGiro.getCost() < 0)
					{
						bGiroProhibido = true;
					}
					else
						costeGiro = elGiro.getCost();

					break; // Salimos del for porque ya hemos encontrado el giro
				}
				// log("DESPUES del if");
			}

			auxCost = newCost + costeGiro;
			if (c.getEdgeOut() == edge)
			{
				if (c.getBestCostIn() > newCost)
				{
					c.setBestCostIn(newCost);
					// pC->from_link_c = idArcoEntrada;
				}
				continue; // Miramos solo los conectores distintos al de entrada
			}
			// Si está prohibido, vamos a por otro enlace
			if (bGiroProhibido)
			{
				c.setMustBeRevised(true);
				// sprintf(Msg, "Encontrado giro prohibido en conector %ld del nodo %ld", iConec, idNodo);
				// log(Msg);
				continue;					
			}
			
			c.setMustBeRevised(false);															
			if (c.getBestCostOut() > auxCost)
			{
				hayMejora = true;
				c.setFrom_link_c(edge.getIdEdge());
				c.setBestCostOut(auxCost);
				c.setMustBeRevised(true);
				// sprintf(Msg, "HAY MEJORA: idNodo=%ld, iConec=%ld, idArcoEntrada=%ld, auxCost= %lf", idNodo, iConec, idArcoEntrada, auxCost);
			}
		}
		return hayMejora;
				
	}
	
	
	public double getBestCost()
	{
		if (numSoluc != GlobalCounter.getGlobalSolutionNumber())
			return Double.MAX_VALUE;

		double bestCost = Double.MAX_VALUE;
		GvConnector c;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			
			if (c.getBestCostOut() < bestCost)
			{
				bestCost = c.getBestCostOut();
			}
		}
		return bestCost;
				
	}

	public double getBestCostIn()
	{
		if (numSoluc != GlobalCounter.getGlobalSolutionNumber())
			return Double.MAX_VALUE;

		double bestCost = Double.MAX_VALUE;
		GvConnector c;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			
			if (c.getBestCostIn() < bestCost)
			{
				bestCost = c.getBestCostIn();
			}
		}
		return bestCost;
				
	}

	public double getBestCostOfDirty()
	{
		if (numSoluc != GlobalCounter.getGlobalSolutionNumber())
			return Double.MAX_VALUE;

		double bestCost = Double.MAX_VALUE;
		GvConnector c;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			
			if ((c.isMustBeRevised()) && (c.getBestCostOut() < bestCost))
			{
				bestCost = c.getBestCostOut();
			}
		}
		return bestCost;
				
	}

	public void setCostZero()
	{
		GvConnector c;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			c.setBestCostOut(0);
			c.setBestCostIn(0);
		}
		
		// TODO: provisional. QUITAR bestCost como atributo
//		best_cost = 0;
				
	}

	/**
	 * Add an edge out to this node. This function takes care of creating the needed connectors
	 * @param edge
	 */
	public void addOutputLink(GvEdge edge) {
//		outputLinks.add(edge);
		// Create connectors
		// First, search the connector if it is already created
		GvConnector c;
		boolean bFound = false;
		GvConnector cFound = null;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			if ((c.getEdgeIn() != null) && (c.getEdgeIn().getIdArc() == edge.getIdArc())) {
				// Found. This connector has been originated before by the same arc
				bFound = true;
				cFound = c;
				break;
			}
		}
		if (!bFound) {
			GvConnector newCon = new GvConnector();
			newCon.setEdgeOut(edge);
			connectors.add(newCon);
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
	public void addInputLink(GvEdge edge) {
//		inputLinks.add(edge);
		// First, search the connector if it is already created
		GvConnector c;
		boolean bFound = false;
		GvConnector cFound = null;
		for (int iConec=0; iConec< connectors.size();  iConec++)
		{
			c = connectors.get(iConec);
			if ((c.getEdgeOut() != null) && (c.getEdgeOut().getIdArc() == edge.getIdArc())) {
				// Found. This connector has been originated before by the same arc
				bFound = true;
				cFound = c;
				break;
			}
		}
		if (!bFound) {
			GvConnector newCon = new GvConnector();
			newCon.setEdgeIn(edge);
			connectors.add(newCon);
		}
		else
		{
			cFound.setEdgeIn(edge);
		}
		
	}

	public ArrayList<GvConnector> getConnectors() {
		return connectors;
	}

	/**
	 * Adds turnCost and setNode to turnCost. Useful to clear turncosts
	 * @param turnCost
	 */
	public void addTurnCost(GvTurn turnCost) {
		turnCost.setNode(this);
		turnCosts.add(turnCost);
		
	}

	public void removeTurnCosts() {
		turnCosts = new ArrayList<GvTurn>();
		
	}
}


