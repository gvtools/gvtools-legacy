/*
 * Created on 02-oct-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
*
* $Id: SnappingEdge.java 10627 2007-03-06 17:10:21Z caballero $
* $Log$
* Revision 1.2  2007-03-06 17:08:55  caballero
* Exceptions
*
* Revision 1.1  2006/12/04 19:30:23  azabala
* *** empty log message ***
*
* Revision 1.2  2006/10/19 16:06:48  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/17 18:25:53  azabala
* *** empty log message ***
*
* Revision 1.1  2006/10/05 19:20:57  azabala
* first version in cvs
*
* Revision 1.1  2006/10/02 19:06:39  azabala
* *** empty log message ***
*
*
*/
package com.vividsolutions.jts.geomgraph;

import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geomgraph.index.MonotoneChainEdge;
import com.vividsolutions.jts.geomgraph.index.SnapMonotoneChainEdge;

public class SnappingEdge extends Edge {

	  SnapEdgeIntersectionList eiList;
	  private double snapTolerance;
	  private SnapMonotoneChainEdge mce;
	  
	  /**
	   * TODO->Esto es una cagada????
	   * El problema es que no estoy aplicando SNAP
	   * en el compute de intersecciones colineales
	   * 
	   * 
	   * 
	   * Nos permite snapear las coordenadas del Edge, para
	   * que antes de añadir un SnapEdgeIntersection se verifique
	   * si coincide con un vertice (y  snapearlo al vertice
	   * para evitar que salgan cosas raras)
	   * */
	  private SnappingNodeMap nodes; 
	 
	  public SnappingEdge(Coordinate[] pts, Label label, double tolerance)
	  {
		super(pts, label);
		this.snapTolerance = tolerance;
		eiList = new SnapEdgeIntersectionList(this);
		
	  }
	  
	  public double getSnapTolerance(){
		  return snapTolerance;
	  }
	  
	  public SnappingEdge(Coordinate[] pts, double snapTolerance)
	  {
	    this(pts, null, snapTolerance);
	  }

	 
	  public EdgeIntersectionList getEdgeIntersectionList() { 
		  return eiList; 
	  }

	  
	  public MonotoneChainEdge getMonotoneChainEdge()
	  {
	    if (mce == null) 
	    	mce = new SnapMonotoneChainEdge(this, snapTolerance);
	    return  mce;
	  }
	  
	  public boolean isPointwiseEqual(Edge e)
	  {
		Coordinate[] thisCoordinates = this.getCoordinates();
	    Coordinate[] otherCoordinates = e.getCoordinates();
	    if (thisCoordinates.length != otherCoordinates.length) 
	    	return false;

	    for (int i = 0; i < thisCoordinates.length; i++) {
	      if (! (thisCoordinates[i].distance(otherCoordinates[i]) <= snapTolerance)) {
	         return false;
	      }
	    }
	    return true;
	  }
	  
	  public boolean equals(Object o)
	  {
	    if (! (o instanceof Edge)) return false;
	    Edge e = (Edge) o;
        Coordinate[] thisCoordinates = this.getCoordinates();
        Coordinate[] otherCoordinates = e.getCoordinates();
	    if (thisCoordinates.length != otherCoordinates.length) 
	    	return false;

	    boolean isEqualForward = true;
	    boolean isEqualReverse = true;
	    int iRev = thisCoordinates.length;
	    for (int i = 0; i < thisCoordinates.length; i++) {
	      if (! (thisCoordinates[i].distance(otherCoordinates[i])
	    		  <= snapTolerance)) {
	         isEqualForward = false;
	      }
	      if (!(thisCoordinates[i].distance(otherCoordinates[--iRev]) <= snapTolerance)) {
	         isEqualReverse = false;
	      }
	      if (! isEqualForward && ! isEqualReverse) return false;
	    }
	    return true;
	  }
	 
	  
	  /**
	   * Adds EdgeIntersections for one or both
	   * intersections found for a segment of an edge to the edge intersection list.
	   */
	  public void addIntersections(LineIntersector li, 
			  int segmentIndex,
			  int geomIndex){
		  
		  //se supone que si es colineal, aquí se añaden los
		  // dos puntos
		  
		    for (int i = 0; i < li.getIntersectionNum(); i++) {
		      addIntersection(li, segmentIndex, geomIndex, i);
		    }
	  }
	  
	  /**
	   * Add an EdgeIntersection for intersection intIndex.
	   * An intersection that falls exactly on a vertex of the edge is normalized
	   * to use the higher of the two possible segmentIndexes
	   */
	  public void addIntersection(LineIntersector li, 
			  int segmentIndex, 
			  int geomIndex, 
			  int intIndex)
	  {
	      Coordinate intPt = new Coordinate(li.getIntersection(intIndex));
	      
	      
//	      AQUI ESTA OTRA DE LAS CLAVES
//	      intPt es una coordenada, que puede estar muy cerca de alguna coordenada
//	      del EDGE. ¿Como gestionar esto?
//	      CREO QUE LO MEJOR ES SNAPEAR
	    
	      int normalizedSegmentIndex = segmentIndex;
	      double dist = li.getEdgeDistance(geomIndex, intIndex);
	      // normalize the intersection point location
	      int nextSegIndex = normalizedSegmentIndex + 1;
	      if (nextSegIndex < super.getNumPoints()) {
	        Coordinate nextPt = super.getCoordinate(nextSegIndex);
	        
	        //TODO VER ESTO DEL 0.0
//	        AQUI PASA ALGO RARO TAMBIEN
//	        PARA EL TEST Nº 1, TUVE QUE AÑADIR EL ELSE
//	        PERO PARA EL SIGUIENTE TESTE CREO QUE ME ESTÁ PERDIENDO
//	        LOS PUNTOS INTERMEDIOS
	        
	        
	        //if (intPt.distance(nextPt) == 0.0) {
	        if (intPt.distance(nextPt) <= snapTolerance) {
	            normalizedSegmentIndex = nextSegIndex;
	            dist = 0.0;
	            intPt = nextPt;
	            
	        }else{
	        	Coordinate previousPoint = this.getCoordinate(normalizedSegmentIndex);
	        	if(intPt.distance(previousPoint) <= snapTolerance){
	        		dist = 0.0;
	        		intPt = previousPoint;
	        	}
	        }
	        
	        /*
	        TODO
	        Esto lo añado porque cuando la interseccion es un snapping, me conserva
	        todos los puntos y me salen dos puntos seguidos iguales (probar)
	        y luego la clase Quadrant me tira excepciones
	        */
//	        else if (intPt.distance(nextPt) <= snapTolerance) {
//	            normalizedSegmentIndex = segmentIndex;
//	            dist = 0.0;
//				//TODO PRUEBA 
//				intPt = nextPt;	            
//	        }
	      }
	      
	      eiList.add(intPt, normalizedSegmentIndex, dist);
	  }	 
	  
	  /**
	   * Util para volver a calcular intersecciones de este Edge
	   * con los Edges de un geometrygraph distinto al original
	   * 
	   * */
	  public void clearIntersections(){
		  eiList = new SnapEdgeIntersectionList(this);
	  }
}

