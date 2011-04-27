package es.iver.derivedGeom.utils;

import com.iver.utiles.StringUtilities;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/**
* <p>Extends {@link StringUtilities StringUtilities} for adding support to replace some problematic
*  letters to their most equivalent.</p>
* 
* @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
*/
public class StringUtilitiesExtended extends StringUtilities {
	/**
	 * <p>Replaces all letters with accent by their equivalent without it:
	 *  <ul>
	 *   <li><b>à</b>,<b>á</b>,<b>â</b>,<b>ä</b> by <b><i>a</i></b></li>
	 *   <li><b>è</b>,<b>é</b>,<b>ê</b>,<b>ë</b> by <b><i>e</i></b></li>
	 *   <li><b>ì</b>,<b>í</b>,<b>î</b>,<b>ï</b> by <b><i>i</i></b></li>
	 *   <li><b>ò</b>,<b>ó</b>,<b>ô</b>,<b>ö</b> by <b><i>o</i></b></li>
	 *   <li><b>ù</b>,<b>ú</b>,<b>û</b>,<b>ü</b> by <b><i>u</i></b></li>
	 *   <li><b>À</b>,<b>Á</b>,<b>Â</b>,<b>Ä</b> by <b><i>A</i></b></li>
	 *   <li><b>È</b>,<b>É</b>,<b>Ê</b>,<b>Ë</b> by <b><i>E</i></b></li>
	 *   <li><b>Ì</b>,<b>Í</b>,<b>Î</b>,<b>Ï</b> by <b><i>I</i></b></li>
	 *   <li><b>Ò</b>,<b>Ó</b>,<b>Ô</b>,<b>Ö</b> by <b><i>O</i></b></li>
	 *   <li><b>Ù</b>,<b>Ú</b>,<b>Û</b>,<b>Ü</b> by <b><i>U</i></b></li>
	 *  </ul>
	 * </p>
	 * 
	 * @param s text to be formatted
	 * @return text formatted
	 */
	public static String replaceAllAccents(String s) {
		return s.replaceAll("[àáâä]","a").replaceAll("[èéêë]","e").replaceAll("[ìíîï]","i").
			replaceAll("[òóôö]","o").replaceAll("[ùúûü]","u").
			replaceAll("[ÀÁÂÄ]", "A").replaceAll("[ÈÉÊË]", "E").replaceAll("[ÌÍÎÏ]", "I").
			replaceAll("[ÒÓÔÖ]", "O").replaceAll("[ÙÚÛÜ]", "U");
	}
	
	/**
	 * <p>Replaces all cedilla letters by the letter c:
	 *  <ul>
	 *   <li><b>ç</b> by <b><i>c</i></b></li>
	 *   <li><b>Ç</b> by <b><i>C</i></b></li>
	 *  </ul>
	 * </p>
	 * 
	 * @param s text to be formatted
	 * @return text formatted
	 */
	public static String replaceAllCedilla(String s) {
		return s.replaceAll("[ç]", "c").replaceAll("[Ç]", "C");
	}
	
	/**
	 * <p>Replaces all n with tilde letters by n:
	 *  <ul>
	 *   <li><b>ñ</b> by <b><i>n</i></b></li>
	 *   <li><b>Ñ</b> by <b><i>N</i></b></li>
	 *  </ul>
	 * </p>
	 * 
	 * @param s text to be formatted
	 * @return text formatted
	 */
	public static String replaceAllNWithTilde(String s) {
		return s.replaceAll("[ñ]", "n").replaceAll("[Ñ]", "N");
	}
}
