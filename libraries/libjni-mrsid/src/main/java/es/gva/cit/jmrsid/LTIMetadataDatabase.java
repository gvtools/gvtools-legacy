/**********************************************************************
 * $Id: LTIMetadataDatabase.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIMetadataDatabase.java
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
 * Almacena los metadatos asociados a la imágen.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */

public class LTIMetadataDatabase extends JNIBase{
	
	private native long getDataByIndexNat(long cPtr, int i);
	private native void FreeLTIMetadataDatabaseNat(long cPtr);
	
	public LTIMetadataDatabase(){}
	
	/**
	 * Constructor
	 * @param cPtr	dirección de memoria al objeto LTIMetadataDatabase de C. 
	 */
	public LTIMetadataDatabase(long cPtr){
		
		super.cPtr=cPtr;
	}
	
	/**
	 * Destructor 
	 */
	protected void finalize(){
		
		if(cPtr != 0)
			FreeLTIMetadataDatabaseNat(cPtr);
	}
		
		
	/**
	 * Devuelve el número de entradas.
	 * @throws MrSIDException
	 * @return número de metadatos
	 */
	public int getIndexCount()throws MrSIDException{
		
		String msg1="Error en getIndexCount. No se ha obtenido un puntero valido a LTIMetadataDatabase";
		String msg2="La llamada nativa a getIndexCount ha devuelto un código de error";
		return baseSimpleFunction(0,msg1,msg2);
		
	}
	
	/**
	 * Obtiene una entrada de metadatos a partir de su índice.
	 * @throws MrSIDException
	 * @param i	índice del metadato obtenido
	 * @return objeto que contiene la entrada seleccionada
	 */
	public LTIMetadataRecord getDataByIndex(int i)throws MrSIDException{
		
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getDataByIndex. La referencia al objeto no es valida.");

		LTIMetadataRecord metadata=new LTIMetadataRecord(getDataByIndexNat(cPtr,i));
				
		if(metadata==null)
		 	throw new MrSIDException("Objeto LTIMetadataRecord nulo");
		
		return metadata;
		
	}
	
	
}
