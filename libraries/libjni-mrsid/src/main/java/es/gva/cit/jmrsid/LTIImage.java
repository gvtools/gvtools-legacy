/**********************************************************************
 * $Id: LTIImage.java 8460 2006-10-31 16:26:34Z nacho $
 *
 * Name:     LTIImage.java
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

/**
 * Clase base que representa a una im�gen.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR>
 *         Equipo de desarrollo gvSIG.<BR>
 *         http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class LTIImage extends JNIBase {

	private int[] magsize = new int[2];

	private native long getMetadataNat(long cPtr);

	private native int getDimsAtMagWidthNat(long cPtr, double mag);

	private native int getDimsAtMagHeightNat(long cPtr, double mag);

	private native long getPixelPropsNat(long cPtr);

	private native long getBackgroundPixelNat(long cPtr);

	private native long getNoDataPixelNat(long cPtr);

	private native int getGeoCoordNat(long cPtr, LTIGeoCoord geocoord);

	private native void FreeLTIImageNat(long cPtr);

	private native double getMinMagnificationNat(long cPtr);

	private native double getMaxMagnificationNat(long cPtr);

	LTIGeoCoord geocoord = null;

	public LTIImage() {
	}

	/**
	 * Constructor
	 * 
	 * @param cPtr
	 *            direcci�n de memoria al objeto LTIImage de C.
	 */
	public LTIImage(long cPtr) {
		this.cPtr = cPtr;
	}

	/**
	 * Destructor
	 */
	protected void finalize() {
		if (cPtr != 0) {
			FreeLTIImageNat(cPtr);
			cPtr = -1;
		}
	}

	/**
	 * Inicializa el objeto im�gen.
	 * 
	 * @throws MrSIDException
	 *             , IOException
	 */
	public void initialize() throws MrSIDException {

		String msg1 = "Error en initialize. El Open del MrSID no tuvo �xito";
		String msg2 = "La llamada nativa a initialize ha devuelto un c�digo de error";
		int codigo = super.baseSimpleFunction(1, msg1, msg2);

		if (codigo > 0)
			throw new MrSIDException(
					"La llamada nativa a initialize ha devuelto el c�digo de error "
							+ codigo);

	}

	/**
	 * Obtiene los metadatos de la im�gen.
	 * 
	 * @throws MrSIDException
	 *             , IOException
	 * @return Objeto LTIMetadataDatabase que contiene los metadatos de la
	 *         im�gen
	 */
	public LTIMetadataDatabase getMetadata() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getMetadataDatabase. El Open del MrSID no tuvo �xito");

		LTIMetadataDatabase metadata = new LTIMetadataDatabase(
				getMetadataNat(cPtr));
		return metadata;
	}

	/**
	 * Obtiene el ancho de la im�gen.
	 * 
	 * @throws MrSIDException
	 * @return entero con el ancho de la im�gen
	 */
	public int getWidth() throws MrSIDException {

		String msg1 = "Error en getNumLevels. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getNumLevels ha devuelto un c�digo de error";
		return baseSimpleFunction(3, msg1, msg2);

	}

	/**
	 * Obtiene el alto de la im�gen.
	 * 
	 * @throws MrSIDException
	 * @return alto de la im�gen
	 */
	public int getHeight() throws MrSIDException {

		String msg1 = "Error en getNumLevels. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getNumLevels ha devuelto un c�digo de error";
		return baseSimpleFunction(4, msg1, msg2);

	}

	/**
	 * Obtiene el n�mero de bandas.
	 * 
	 * @throws MrSIDException
	 * @return n�mero de bandas
	 */
	public int getNumBands() throws MrSIDException {

		String msg1 = "Error en getNumBands. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getNumBands ha devuelto un c�digo de error";
		return baseSimpleFunction(6, msg1, msg2);

	}

	/**
	 * Obtiene el tipo de espacio de color usado en la im�gen.
	 * 
	 * @throws MrSIDException
	 * @return entero que representa una constante de la clase LTIColorSpace.
	 */
	public int getColorSpace() throws MrSIDException {

		String msg1 = "Error en getColorSpace. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getColorSpace ha devuelto un c�digo de error";
		return baseSimpleFunction(7, msg1, msg2);

	}

	/**
	 * Obtiene el tipo de datos usado en la im�gen.
	 * 
	 * @throws MrSIDException
	 * @return entero que representa una constante de la clase LTIDataType.
	 */
	public int getDataType() throws MrSIDException {

		String msg1 = "Error en getDataType. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getDataType ha devuelto un c�digo de error";
		return baseSimpleFunction(8, msg1, msg2);

	}

	/**
	 * Obtiene el ancho y alto de la im�gen a partir de una valor de ampliaci�n
	 * dado.
	 * 
	 * @throws MrSIDException
	 * @return el ancho y alto de la im�gen
	 */
	public int[] getDimsAtMag(double mag) throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getDimsAtMag. El Open del MrSID no tuvo �xito");

		magsize[0] = getDimsAtMagWidthNat(cPtr, mag);
		magsize[1] = getDimsAtMagHeightNat(cPtr, mag);

		if (magsize[0] < 0 || magsize[1] < 0)
			throw new MrSIDException(
					"Error en getDimsAtMag. El Open del MrSID no tuvo �xito");

		return magsize;
	}

	/**
	 * Obtiene las propiedades del pixel de la im�gen.
	 * 
	 * @throws MrSIDException
	 * @return un objeto de tipo LTIPixel conteniendo las propiedades
	 */
	public LTIPixel getPixelProps() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getPixelProps. El Open del MrSID no tuvo �xito");

		LTIPixel props = new LTIPixel(getPixelPropsNat(cPtr));
		return props;
	}

	/**
	 * Obtiene la informaci�n de georeferenciaci�n de la im�gen si la tiene.
	 * 
	 * @throws MrSIDException
	 * @return un objeto de tipo LTIGeoCoord con la informaci�n de
	 *         georeferenciaci�n
	 */
	public LTIGeoCoord getGeoCoord() throws MrSIDException {

		int res = 0;

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getGeoCoord. El Open del MrSID no tuvo �xito");

		if (geocoord == null) {
			geocoord = new LTIGeoCoord();
			res = getGeoCoordNat(cPtr, geocoord);
		}

		if (res < 0)
			throw new MrSIDException(
					"Error en getGeoCoord. No se ha podido obtener un LTIGeoCoord valido");

		return geocoord;

	}

	/**
	 * Obtiene el valor del fondo del pixel. Si devuelve null es que no ha sido
	 * definido un valor para el fondo.
	 * 
	 * @throws MrSIDException
	 * @return un objeto del tipo LTIPixel con los valores del fondo del pixel
	 */
	public LTIPixel getBackgroundPixel() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getBackgroundPixel. El Open del MrSID no tuvo �xito");

		LTIPixel pixel = new LTIPixel(getBackgroundPixelNat(cPtr));

		if (pixel.getPtr() == 0)
			return null;
		else
			return pixel;

	}

	/**
	 * Obtiene los valores de transparencia de un pixel. Si devuelve null es que
	 * la im�gen no tiene definido un valor de transparencia.
	 * 
	 * @throws MrSIDException
	 * @return un objeto de tipo LTIPixel
	 */
	public LTIPixel getNoDataPixel() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getNoData. El Open del MrSID no tuvo �xito");

		LTIPixel pixel = new LTIPixel(getNoDataPixelNat(cPtr));

		if (pixel.getPtr() == 0)
			return null;
		else
			return pixel;

	}

	/**
	 * This function returns the minimum magnification of the image. Images that
	 * do not support "zooming out" will return 1.0; images that contain
	 * "overviews"
	 * 
	 * @throws MrSIDException
	 * @return the minimum magnification
	 */
	public double getMinMagnification() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getMinMagnification(). El Open del MrSID no tuvo �xito");

		return this.getMinMagnificationNat(cPtr);

	}

	/**
	 * This function returns the minimum magnification of the image. Images that
	 * do not support "zooming out" will return 1.0; images that contain
	 * "overviews"
	 * 
	 * @throws MrSIDException
	 * @return the minimum magnification
	 */
	public double getMaxMagnification() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getMaxMagnification(). El Open del MrSID no tuvo �xito");

		return this.getMaxMagnificationNat(cPtr);

	}

}