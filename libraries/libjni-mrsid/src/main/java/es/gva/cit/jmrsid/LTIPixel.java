/**********************************************************************
 * $Id: LTIPixel.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIPixel.java
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
 * Propiedades básicas de un pixel.
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIPixel extends JNIBase{
	
   private native long LTIPixelNat(int eColorSpace, int nBands, int SampleType);	
   private native void FreeLTIPixelNat(long cPtr);
	
	/**
	 * Destructor 
	 */
	
	protected void finalize(){
		if(cPtr != 0)
			FreeLTIPixelNat(cPtr);
	}
	
	public LTIPixel(){}
	
   /**
    * Constructor a partir de su identificador de C.
    * @param cPtr	puntero al objeto creado en C.
    */
   
   public LTIPixel(long cPtr){
		
		super.cPtr=cPtr;
   }
	
   /**
    * Constructor
    * @param eColorSpace	Espacio de color
    * @param nBands	Número de bandas
    * @param eSampleType	
    */
   public LTIPixel(int eColorSpace, int nBands, int eSampleType)throws MrSIDException{
   	
   	 cPtr = LTIPixelNat(eColorSpace, nBands, eSampleType);
   	 
	 if(cPtr == 0)
			throw new MrSIDException("Error in native constructor LTIPixel");
   	 
   }
}