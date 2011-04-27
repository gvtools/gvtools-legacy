/**********************************************************************
 * $Id: LTIGeoCoord.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIGeoCoord.java
 * Project:  JMRSID. Interface java to mrsid (Lizardtech).
 * Purpose:   
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
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

package es.gva.cit.jmrsid;

import java.io.*;

/**
 * Clase que representa a la informaci�n de georeferenciaci�n. Los datos que contiene son:
 * <UL>
 * <LI>esquina superior izquierda (X)</LI>
 * <LI>esquina superior izquierda (y)</LI>
 * <LI>resoluci�n en X</LI>
 * <LI>resoluci�n en Y</LI>
 * <LI>rotaci�n en X</LI>
 * <LI>rotaci�n en Y</LI>
 * </UL>
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIGeoCoord {
	
	private double xUL;		//esquina superior izquierda (X)
	private double yUL;		//esquina superior izquierda (y)
	private double xRes;	//resoluci�n en X
	private double yRes;	//resoluci�n en Y
	private double xRot;	//rotaci�n en X
	private double yRot;	//rotaci�n en Y
	
	
	/**
	 * Devuelve la esquina superior izquierda (X)
	 */
	public double getX(){
		return xUL;
	}
	
	/**
	 * Devuelve la esquina superior izquierda (Y)
	 */
	public double getY(){
		return yUL;
	}
	
	/**
	 * Devuelve la resolucion en X
	 */
	public double getXRes(){
		return xRes;
	}
	
	/**
	 * Devuelve la resoluci�n en Y
	 */
	public double getYRes(){
		return yRes;
	}
	
	/**
	 * Devuelve la rotaci�n en X
	 */
	public double getXRot(){
		return xRot;
	}
	
	/**
	 * Devuelve la rotaci�n en Y
	 */
	public double getYRot(){
		return yRot;
	}
}