/**********************************************************************
 * $Id: LTIImage.java 8460 2006-10-31 16:26:34Z nacho $
 *
 * Name:     LTIImage.java
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
 * Clase base que representa a una imágen.
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
	 *            dirección de memoria al objeto LTIImage de C.
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
	 * Inicializa el objeto imágen.
	 * 
	 * @throws MrSIDException
	 *             , IOException
	 */
	public void initialize() throws MrSIDException {

		String msg1 = "Error en initialize. El Open del MrSID no tuvo éxito";
		String msg2 = "La llamada nativa a initialize ha devuelto un código de error";
		int codigo = super.baseSimpleFunction(1, msg1, msg2);

		if (codigo > 0)
			throw new MrSIDException(
					"La llamada nativa a initialize ha devuelto el código de error "
							+ codigo);

	}

	/**
	 * Obtiene los metadatos de la imágen.
	 * 
	 * @throws MrSIDException
	 *             , IOException
	 * @return Objeto LTIMetadataDatabase que contiene los metadatos de la
	 *         imágen
	 */
	public LTIMetadataDatabase getMetadata() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getMetadataDatabase. El Open del MrSID no tuvo éxito");

		LTIMetadataDatabase metadata = new LTIMetadataDatabase(
				getMetadataNat(cPtr));
		return metadata;
	}

	/**
	 * Obtiene el ancho de la imágen.
	 * 
	 * @throws MrSIDException
	 * @return entero con el ancho de la imágen
	 */
	public int getWidth() throws MrSIDException {

		String msg1 = "Error en getNumLevels. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getNumLevels ha devuelto un código de error";
		return baseSimpleFunction(3, msg1, msg2);

	}

	/**
	 * Obtiene el alto de la imágen.
	 * 
	 * @throws MrSIDException
	 * @return alto de la imágen
	 */
	public int getHeight() throws MrSIDException {

		String msg1 = "Error en getNumLevels. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getNumLevels ha devuelto un código de error";
		return baseSimpleFunction(4, msg1, msg2);

	}

	/**
	 * Obtiene el número de bandas.
	 * 
	 * @throws MrSIDException
	 * @return número de bandas
	 */
	public int getNumBands() throws MrSIDException {

		String msg1 = "Error en getNumBands. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getNumBands ha devuelto un código de error";
		return baseSimpleFunction(6, msg1, msg2);

	}

	/**
	 * Obtiene el tipo de espacio de color usado en la imágen.
	 * 
	 * @throws MrSIDException
	 * @return entero que representa una constante de la clase LTIColorSpace.
	 */
	public int getColorSpace() throws MrSIDException {

		String msg1 = "Error en getColorSpace. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getColorSpace ha devuelto un código de error";
		return baseSimpleFunction(7, msg1, msg2);

	}

	/**
	 * Obtiene el tipo de datos usado en la imágen.
	 * 
	 * @throws MrSIDException
	 * @return entero que representa una constante de la clase LTIDataType.
	 */
	public int getDataType() throws MrSIDException {

		String msg1 = "Error en getDataType. No se ha obtenido un puntero valido a LTIImage";
		String msg2 = "La llamada nativa a getDataType ha devuelto un código de error";
		return baseSimpleFunction(8, msg1, msg2);

	}

	/**
	 * Obtiene el ancho y alto de la imágen a partir de una valor de ampliación
	 * dado.
	 * 
	 * @throws MrSIDException
	 * @return el ancho y alto de la imágen
	 */
	public int[] getDimsAtMag(double mag) throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getDimsAtMag. El Open del MrSID no tuvo éxito");

		magsize[0] = getDimsAtMagWidthNat(cPtr, mag);
		magsize[1] = getDimsAtMagHeightNat(cPtr, mag);

		if (magsize[0] < 0 || magsize[1] < 0)
			throw new MrSIDException(
					"Error en getDimsAtMag. El Open del MrSID no tuvo éxito");

		return magsize;
	}

	/**
	 * Obtiene las propiedades del pixel de la imágen.
	 * 
	 * @throws MrSIDException
	 * @return un objeto de tipo LTIPixel conteniendo las propiedades
	 */
	public LTIPixel getPixelProps() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getPixelProps. El Open del MrSID no tuvo éxito");

		LTIPixel props = new LTIPixel(getPixelPropsNat(cPtr));
		return props;
	}

	/**
	 * Obtiene la información de georeferenciación de la imágen si la tiene.
	 * 
	 * @throws MrSIDException
	 * @return un objeto de tipo LTIGeoCoord con la información de
	 *         georeferenciación
	 */
	public LTIGeoCoord getGeoCoord() throws MrSIDException {

		int res = 0;

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getGeoCoord. El Open del MrSID no tuvo éxito");

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
					"Error en getBackgroundPixel. El Open del MrSID no tuvo éxito");

		LTIPixel pixel = new LTIPixel(getBackgroundPixelNat(cPtr));

		if (pixel.getPtr() == 0)
			return null;
		else
			return pixel;

	}

	/**
	 * Obtiene los valores de transparencia de un pixel. Si devuelve null es que
	 * la imágen no tiene definido un valor de transparencia.
	 * 
	 * @throws MrSIDException
	 * @return un objeto de tipo LTIPixel
	 */
	public LTIPixel getNoDataPixel() throws MrSIDException {

		if (cPtr == 0)
			throw new MrSIDException(
					"Error en getNoData. El Open del MrSID no tuvo éxito");

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
					"Error en getMinMagnification(). El Open del MrSID no tuvo éxito");

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
					"Error en getMaxMagnification(). El Open del MrSID no tuvo éxito");

		return this.getMaxMagnificationNat(cPtr);

	}

}