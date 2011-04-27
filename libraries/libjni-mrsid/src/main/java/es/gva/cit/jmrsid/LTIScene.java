/**********************************************************************
 * $Id: LTIScene.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIScene.java
 * Project:  JMRSID. Interface java to mrsid (Lizardtech).
 * Purpose:   
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
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

package es.gva.cit.jmrsid;


/**
 * Clase que representa a una escena de la imágen
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIScene extends JNIBase{
	
	private native long LTISceneNat(double xinic,double yinic,double tamx,double tamy, double magnification);
	private native void FreeLTISceneNat(long cPtr);
	
	public LTIScene(){}
	
	/**
	 * Constructor
	 * @param cPtr	dirección de memoria al objeto LTIScene de C. 
	 */
	
	public LTIScene(long cPtr){
		this.cPtr=cPtr;
	}
	
	/**
	 * Destructor 
	 */
	
	public void finalize(){
		//System.out.println("Finalizando LTIscene ..."+cPtr);
		if(cPtr != 0)
			FreeLTISceneNat(cPtr);
	}
	
	/**
	 * Este constructor produce una escena con los parámetros dados usando doble precision.
	 * 
	 * @param xinic	Esquina superior izquierda en la escena
	 * @param yinic	Esquina superior derecha en la escena
	 * @param width	ancho de la escena
	 * @param height	alto de la escena
	 */
	
	public LTIScene(double xinic,double yinic,double tamx,double tamy, double magnification)throws MrSIDException{
		
		cPtr=LTISceneNat(xinic, yinic, tamx, tamy, magnification);
	   	 
		if(cPtr == 0)
				throw new MrSIDException("Error in native constructor LTIScene");
	}
}