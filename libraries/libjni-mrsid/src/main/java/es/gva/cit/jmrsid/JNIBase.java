/**********************************************************************
 * $Id: JNIBase.java 3539 2006-01-09 12:23:20Z nacho $
 *
 * Name:     JNIBase.java
 * Project:  JMRSID. Interfaz java to MrSID (Lizardtech).
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

package es.gva.cit.jmrsid;

/**
 * Clase base para todas las clases que contienen funcionalidades JNI.
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR>
 *         Equipo de desarrollo gvSIG.<BR>
 *         http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class JNIBase {

	protected long cPtr;

	private native int getIndexCountNat(long cPtr);

	private native int initializeNat(long cPtr);

	private native int getNumLevelsNat(long cPtr);

	private native int getWidthNat(long cPtr);

	private native int getHeightNat(long cPtr);

	private native int getStripHeightNat(long cPtr);

	private native int getNumBandsNat(long cPtr);

	private native int getColorSpaceNat(long cPtr);

	private native int getDataTypeNat(long cPtr);

	/**
	 * Función que sirve como base para funcionalidades de mrsid que admiten
	 * como parámetro un entero y devuelven un entero.
	 * 
	 * @throws MrSIDException.
	 * @param msg1
	 *            Mensaje de error que se muestra cuando el puntero a objeto
	 *            pasado es vacio.
	 * @param msg2
	 *            Mensaje de error que se muestra cuando el resultado de la
	 *            llamada a la función de gdal es menor o igual que 0.
	 */

	protected int baseSimpleFunction(int n, String msg1, String msg2)
			throws MrSIDException {

		int res = 0;

		if (cPtr <= 0)
			throw new MrSIDException(msg1);

		switch (n) {
		case 0:
			res = getIndexCountNat(cPtr);
			break;
		case 1:
			res = initializeNat(cPtr);
			break;
		case 2:
			res = getNumLevelsNat(cPtr);
			break;
		case 3:
			res = getWidthNat(cPtr);
			break;
		case 4:
			res = getHeightNat(cPtr);
			break;
		case 5:
			res = getStripHeightNat(cPtr);
			break;
		case 6:
			res = getNumBandsNat(cPtr);
			break;
		case 7:
			res = getColorSpaceNat(cPtr);
			break;
		case 8:
			res = getDataTypeNat(cPtr);
			break;
		}

		if (res < 0)
			throw new MrSIDException(msg2);
		else
			return res;
	}

	/**
	 * Devuelve el puntero a memoria del objeto en C.
	 */

	public long getPtr() {
		return cPtr;
	}

	/**
	 * Carga de la libreria jmrsid
	 */

	static {

		System.loadLibrary("jmrsid");

	}

}