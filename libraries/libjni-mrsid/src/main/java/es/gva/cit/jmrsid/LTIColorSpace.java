/**********************************************************************
 * $Id: LTIColorSpace.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIColorSpace.java
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
 * Clase con las constantes que representan el espacio de color utilizado
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIColorSpace{
	
	  public static final int LTI_COLORSPACE_INVALID=0;
	  public static final int LTI_COLORSPACE_RGB=1;
	  public static final int LTI_COLORSPACE_RGBK=2;
	  public static final int LTI_COLORSPACE_CMYK=3;
	  public static final int LTI_COLORSPACE_GRAYSCALE=4;
	  public static final int LTI_COLORSPACE_PALETTE=5;
	  public static final int LTI_COLORSPACE_YIQ=6;
	  public static final int LTI_COLORSPACE_YIQK=7;
	  public static final int LTI_COLORSPACE_MULTISPECTRAL=8;
	  
}