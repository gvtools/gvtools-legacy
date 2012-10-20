/**********************************************************************
 * $Id: JNIBase.java 3538 2006-01-09 11:56:54Z nacho $
 *
 * Name:     JNIBase.java
 * Project:  
 * Purpose:  Base class for classes that use JNI.
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

package es.gva.cit.jecwcompress;

/**
 * Clase base para todas las funcionalidades jni. Contiene operaciones comunes
 * para todas ellas.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR>
 *         Equipo de desarrollo gvSIG.<BR>
 *         http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class JNIBase {

	protected long cPtr;

	// private native int getRasterBandXSizeNat(long cPtr);

	/**
	 * Función que sirve como base para funcionalidades de gdal que admiten como
	 * parámetro un entero y devuelven un entero.
	 * 
	 * @throws GdalException.
	 * @param msg1
	 *            Mensaje de error que se muestra cuando el puntero a objeto
	 *            pasado es vacio.
	 * @param msg2
	 *            Mensaje de error que se muestra cuando el resultado de la
	 *            llamada a la función de gdal es menor o igual que 0.
	 */

	protected int baseSimpleFunctions(int n, String msg1, String msg2)
			throws EcwException {

		int res = 0;
		if (cPtr == 0)
			throw new EcwException(msg1);

		switch (n) {
		case 0: /* res = getRasterBandXSizeNat(cPtr); */
			break;
		}

		if (res < 0)
			throw new EcwException(msg2);
		else
			return res;
	}

	public long getPtro() {
		return cPtr;
	}

	static {

		System.loadLibrary("jecw");
	}

}