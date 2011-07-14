/**********************************************************************
 * $Id: LTISceneBuffer.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     LTISceneBuffer.java
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
 * Buffer que contiene los datos de una escena.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR> Equipo de desarrollo gvSIG.<BR> http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTISceneBuffer extends JNIBase{
	
	private native long LTISceneBufferNat(long pixel,int tamx,int tamy, int flag);
	private native long LTISceneBuffer1Nat(long pixel, int totalNumCols, int totalNumRows, int colOffset, int rowOffset, int windowNumCols, int windowNumRows, int flag);
	private native void FreeLTISceneBufferNat(long cPtr_LTISceneBuffer, long cPtr_tbuffer);
	
	public int size;
	public byte buf1[];
	public byte buf2[];
	public byte buf3[];
	public long cPtrbuffer;
	public boolean flag;
	
	
	/**
	 * Constructor 
	 * 
	 * @param pixel	propiedades del pixel que serán usadas en el buffer
	 * @param totalNumCols	ancho del buffer
	 * @param totalNumRows	alto del buffer
	 * @param flag	Pone a null el puntero de datos si es false y llena el buffer si es true
	 * @throws MrSIDException	 
	 */
	public LTISceneBuffer(LTIPixel pixel, int totalNumCols, int totalNumRows, boolean flag)throws MrSIDException{
		 
		if(totalNumCols<0 || totalNumRows<0 || pixel==null)
			throw new MrSIDException("Valores no validos para el tamaño de ventana.");
		
		this.cPtrbuffer=-1;
		this.flag=flag;
		
		//Si el flag es true reservamos memoria para el buffer
		
		if(flag==true){			
			size=totalNumCols*totalNumRows;
			buf1=new byte[size];
			buf2=new byte[size];
			buf3=new byte[size];
			
		}
		
		if(flag)cPtr=LTISceneBufferNat(pixel.cPtr, totalNumCols, totalNumRows, 1);
		else cPtr=LTISceneBufferNat(pixel.cPtr, totalNumCols, totalNumRows, 0);
	   	 
		if(cPtr == 0)
			throw new MrSIDException("Error en el constructor nativo LTIScene.");
	}
	
	/**
	 * Constructor 
	 * 
	 * @param pixel	propiedades del pixel que serán usadas en el buffer
	 * @param totalNumCols	ancho del buffer
	 * @param totalNumRows	alto del buffer
	 * @param colOffset posición X de la ventana
	 * @param rowOffset posición Y de la ventana
	 * @param windowNumCols Ancho de la ventana
	 * @param windowNumRows Alto de la ventana
	 * @param flag	Pone a null el puntero de datos si es false y llena el buffer si es true
	 * @throws MrSIDException	 
	 */
	public LTISceneBuffer(LTIPixel pixel, int totalNumCols, int totalNumRows, int colOffset, int rowOffset, int windowNumCols, int windowNumRows, boolean flag)throws MrSIDException{
		
		/*if ((colOffset < 0) || (rowOffset < 0) || (colOffset > baseSimpleFunction(3, "", "")) || (colOffset > baseSimpleFunction(4, "", "")))
			throw new MrSIDException("Posicion inicial de la escena no valida");
		
		if ((windowNumCols < 1) || (windowNumRows < 1) || (windowNumCols > baseSimpleFunction(3, "", "")) || (windowNumRows > baseSimpleFunction(4, "", "")))
			throw new MrSIDException("Tamaño de la escena no valido");
		
		if (((colOffset + windowNumCols) > baseSimpleFunction(3, "", "")) || ((rowOffset + windowNumRows) > baseSimpleFunction(4, "", "")))
			throw new MrSIDException("Valores de la escena no validos");
		
		if(totalNumCols<0 || totalNumRows<0 || pixel==null)
			throw new MrSIDException("Valores no validos para el tamaño de ventana.");
		*/
		this.cPtrbuffer=-1;
		this.flag=flag;
		
		//Si el flag es 1 reservamos memoria para el buffer
		
		if(flag==true){			
			size=totalNumCols*totalNumRows;
			buf1=new byte[size];
			buf2=new byte[size];
			buf3=new byte[size];
			
		}
		
		if(flag)cPtr=LTISceneBuffer1Nat(pixel.cPtr, totalNumCols, totalNumRows, colOffset, rowOffset, windowNumCols, windowNumRows, 1);
		else cPtr=LTISceneBuffer1Nat(pixel.cPtr, totalNumCols, totalNumRows, colOffset, rowOffset, windowNumCols, windowNumRows, 0);
	   	 
		if(cPtr == 0)
			throw new MrSIDException("Error en el constructor nativo LTIScene.");
	}
	
	/**
	 * Destructor 
	 */
	public void finalize(){
		//System.out.println("Finalizando LTIsceneBuffer ..."+cPtrbuffer);
		if(cPtr != 0)
			FreeLTISceneBufferNat(cPtr, cPtrbuffer);
	}
	
}