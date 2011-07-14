/**********************************************************************
 * $Id: LTIDataType.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIDataType.java
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
 * Clase que contiene las constantes asociadas a cada tipo de dato.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIDataType{
	
	  public static final int LTI_DATATYPE_INVALID  = 0;
	  public static final int LTI_DATATYPE_UINT8    = 1;
	  public static final int LTI_DATATYPE_SINT8    = 2;
	  public static final int LTI_DATATYPE_UINT16   = 3;
	  public static final int LTI_DATATYPE_SINT16   = 4;
	  public static final int LTI_DATATYPE_UINT32   = 5;
	  public static final int LTI_DATATYPE_SINT32   = 6;
	  public static final int LTI_DATATYPE_FLOAT32  = 7;
	  public static final int LTI_DATATYPE_FLOAT64  = 8;
	  
}