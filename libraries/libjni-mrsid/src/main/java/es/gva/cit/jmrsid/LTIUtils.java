/**********************************************************************
 * $Id: LTIUtils.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIUtils.java
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

import java.io.*;

/**
 * Utilidades 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIUtils extends JNIBase{
	
	private native static double levelToMagNat(int level);
	private native void FreeLTIUtilsNat(long cPtr);
	
	public LTIUtils(){}
	
	/**
	 * Constructor
	 * @param cPtr	dirección de memoria al objeto OGRCurve de C. 
	 */
	
	public LTIUtils(long cPtr){
		this.cPtr=cPtr;
	}
	
	/**
	 * Destructor 
	 */
	
	protected void finalize(){
		if(cPtr != 0)
			FreeLTIUtilsNat(cPtr);
	}
	
	/**
	 * Convierte un valor de nivel en valor de ampliación
	 * @throws MrSIDException
	 * @param level	el valor de nivel a convertir
	 * @return valor de ampliación
	 */
	public static double levelToMag(int level)throws MrSIDException{
		
		 double mag = levelToMagNat(level);
		 
		 if(mag < 0)
		 	throw new MrSIDException("Error LTIUtils::levelToMag. Se obtuvo un valor erroneoen la llamada nativa levelToMag.");
		 else 
		 	return mag;
	}
	  
}