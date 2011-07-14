/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.jpotrace;
/**
 * La clase <code>Potrace</code> contiene los metodos que comunican la libreria
 * nativa con Java
 *  
 * @version 31/07/2008
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class Potrace extends JNIBase {
	private static native double[] vectorizeBufferRasterNat(int[] bufferIn, int width, int height, String[] params);

	/**
	 * Vectoriza un buffer pasado por parametro y es devuelto en forma de array
	 * de doubles. Hay que especificar el ancho y alto del buffer y el buffer ha
	 * de ser pasado en el formato que soporta potrace, que es en forma de bits.
	 * 
	 * El parametro params es un array de Strings como se usaria en el tipico
	 * main(char[]) para expresar los parametros de potrace, es una forma de poder
	 * aprovechar todos los parametros del comando potrace desde Java. Algunos 
	 * no funcionan, como especificar el fichero origen o destino
	 * 
	 * @param bufferIn
	 * @param width
	 * @param height
	 * @param params
	 * @return
	 * @throws PotraceException
	 */
	public static double[] vectorizeBufferRaster(int[] bufferIn, int width, int height, String[] params) throws PotraceException {
		if (bufferIn == null)
			throw new PotraceException("El parametro Buffer no puede estar vacio");

		if (width <= 0)
			throw new PotraceException("El ancho del buffer ha de ser mayor a 0");

		if (height <= 0)
			throw new PotraceException("El alto del buffer ha de ser mayor a 0");

		return vectorizeBufferRasterNat(bufferIn, width, height, params);
	}
}