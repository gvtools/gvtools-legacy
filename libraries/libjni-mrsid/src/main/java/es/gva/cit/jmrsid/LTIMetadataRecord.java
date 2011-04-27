/**********************************************************************
 * $Id: LTIMetadataRecord.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTIMetadataRecord.java
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
 * Representa una entrada de LTIMetadataDatabase correspondiente a un metadato
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIMetadataRecord extends JNIBase{
	
	public static final int LTI_METADATA_DATATYPE_INT = 0;
	public static final int LTI_METADATA_DATATYPE_FLOAT = 1;
	public static final int LTI_METADATA_DATATYPE_STRING = 2;
		
	private native String getTagNameNat(long cPtr);
	private native int isScalarNat(long cPtr);
	private native int isVectorNat(long cPtr);
	private native int isArrayNat(long cPtr);
	private native int getDataTypeNat(long cPtr);
	private native String getScalarDataNat(long cPtr);
	private native StringArray getVectorDataNat(long cPtr);
	private native StringArray getArrayDataNat(long cPtr);
	private native int getNumDimsNat(long cPtr);
	private native int[] getDimsNat(long cPtr);
	private native void FreeLTIMetadataRecordNat(long cPtr);
	
	
	/**
	 * Destructor 
	 */
	protected void finalize(){
		if(cPtr != 0)
			FreeLTIMetadataRecordNat(cPtr);
	}
	
	public LTIMetadataRecord(){}
	
	/**
	 * Constructor
	 */
	public LTIMetadataRecord(long cPtr){
		
		super.cPtr=cPtr;
	}

	/**
	 * Obtiene el nombre de la entrada
	 * @throws MrSIDException
	 * @return nombre de la entrada
	 */
	public String getTagName()throws MrSIDException{
		
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getTagName. La referencia al objeto no es valida.");

		String tagname = getTagNameNat(cPtr);
				
		if(tagname == null)
		 	throw new MrSIDException("No se ha devuelto un nombre de tag valido");
		
		return tagname;
	}
	
	/**
	 * Dice si el metadato es escalar o no.
	 * @throws MrSIDException
	 * @return true si es escalar y false si no lo es
	 */
	public boolean isScalar()throws MrSIDException{
		
		if (cPtr == 0)
		 	throw new MrSIDException("Error en getTagName. La referencia al objeto no es valida.");
		
		int res = isScalarNat(cPtr);
		
		if (res == 1)return true;
		else return false;
	
	}
	
	/**
	 * Dice si el metadato es vector o no.
	 * @throws MrSIDException
	 * @return true si es vector y false si no lo es
	 */
	public boolean isVector()throws MrSIDException{
		if(cPtr == 0)
		 	throw new MrSIDException("Error en isVector. La referencia al objeto no es valida.");

		int res = isVectorNat(cPtr);
		
		if(res==1)return true;
		else return false;
		
	}
	
	/**
	 * Dice si el metadato es array o no.
	 * @throws MrSIDException
	 * @return true si es array y false si no lo es
	 */
	public boolean isArray()throws MrSIDException{
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getTagName. La referencia al objeto no es valida.");

		int res = isArrayNat(cPtr);
		
		if(res==1)return true;
		else return false;
		
	}
	
	/**
	 * Obtiene el tipo de datos
	 * @throws MrSIDException
	 * @return tipo de datos 
	 *<UL>    
	 *<LI>LTI_METADATA_DATATYPE_INVALID 0</LI> 
	 *<LI>LTI_METADATA_DATATYPE_UINT8 1</LI>
	 *<LI>LTI_METADATA_DATATYPE_SINT8 2</LI>
	 *<LI>LTI_METADATA_DATATYPE_UINT16 3</LI>
	 *<LI>LTI_METADATA_DATATYPE_SINT16 4</LI>
	 *<LI>LTI_METADATA_DATATYPE_UINT32 5</LI>
	 *<LI>LTI_METADATA_DATATYPE_SINT32 6</LI>
	 *<LI>LTI_METADATA_DATATYPE_UINT64 7</LI>
	 *<LI>LTI_METADATA_DATATYPE_SINT64 8</LI>
	 *<LI>LTI_METADATA_DATATYPE_FLOAT32 9</LI>
	 *<LI>LTI_METADATA_DATATYPE_FLOAT64 10</LI>
	 *<LI>LTI_METADATA_DATATYPE_ASCII 11</LI>
	 *</UL>
	 * 
	 */
	public int getDataType()throws MrSIDException{
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getDataType. La referencia al objeto no es valida.");
		
		int res = getDataTypeNat(cPtr);
		
		if(res < 0)
			throw new MrSIDException("Error en getDataType. No se ha obtenido un tipo de datos valido");
		
		return res;
	}
	
	/**
	 * Obtiene el valor de un datos escalar en forma de String. La conversión a float, entero, ... 
	 * si fuera necesario es a cargo del cliente  
	 * @throws MrSIDException
	 * @return dato escalar
	 */
	public String getScalarData()throws MrSIDException{
		
		String datos=null;
		
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getScalarData. La referencia al objeto no es valida.");
		
		datos = getScalarDataNat(cPtr);
		
		return datos;
	
	}
	
	/**
	 * Obtiene el valor del metadato de tipo vector en forma de vector de strings
	 * @throws MrSIDException
	 * @return vector de strings con los valores del metadato
	 */
	public String[] getVectorData()throws MrSIDException{
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getVectorData. La referencia al objeto no es valida.");
		
		StringArray res = getVectorDataNat(cPtr);
		
		if(res != null)return res.array;
		else return null;
	}
	
	/**
	 * Obtiene el valor del metadato de tipo array en forma de vector de strings. Esta función 
	 * colocará todas las dimensiones del array en forma de vector
	 * @throws MrSIDException
	 * @return vector de strings con los valores del metadato
	 */
	public String[] getArrayData()throws MrSIDException{
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getArrayData. La referencia al objeto no es valida.");

		StringArray res = getArrayDataNat(cPtr);
		
		if(res != null)return res.array;
		else return null;
	}
	
	/**
	 * Devuelve el número de dimensiones del dataset
	 * @throws MrSIDException
	 * @return número de dimensiones del dataset
	 */
	
	public int getNumDims()throws MrSIDException{
		
		if(cPtr == 0)
		 	throw new MrSIDException("Error en getNumDims. La referencia al objeto no es valida.");
		
		int res = getNumDimsNat(cPtr);
		
		if(res < 0)
		 	throw new MrSIDException("Error en getNumDims. El número de dimensiones obtenido no es valido.");
		
		return res;
	}
	
	/**
	 * Obtiene la longitud de cada dimensión del dataset
	 * @throws MrSIDException
	 * @return array de enteros con la dimension de cada longitud
	 */
	 public int[] getDims()throws MrSIDException{
	 	
	 	if(cPtr == 0)
		 	throw new MrSIDException("Error en getDims. La referencia al objeto no es valida.");
	 	
	 	int[] res =  getDimsNat(cPtr);
	 	
	 	if(res==null || res.length==0)
	 		throw new MrSIDException("Error en getDims. No se ha podido obtener la longitud de las dimensiones.");
	 	
	 	return res;
	 }

}