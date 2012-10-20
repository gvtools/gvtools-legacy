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

package com.iver.cit.gvsig.project.documents.view.gui;

/**
 * Font options.
 * 
 * @author Vicente Caballero Navarro
 */
public class FontOptions {
	public static String ARIAL = "Arial";
	public static String DIALOG = "Dialog";
	public static String DIALOGINPUT = "DialogInput";
	public static String SERIF = "Serif";
	public static String SANSSERIF = "SansSerif";
	public static String MONOSPACED = "Monospaced";
	public static String COURIER = "Courier";
	public static String TIMESROMAN = "TimesRoman";
	public static String HELVETICA = "Helvetica";
	public static String PLAIN = "Plain";
	public static String ITALIC = "Italic";
	public static String BOLD = "Bold";
	private static int size = 9;

	/**
	 * Returns font types.
	 * 
	 * @return String[] of font types.
	 */
	public static String[] getFontTypes() {
		String[] types = new String[size];
		types[0] = ARIAL;
		types[1] = DIALOG;
		types[2] = DIALOGINPUT;
		types[3] = SERIF;
		types[4] = SANSSERIF;
		types[5] = MONOSPACED;
		types[6] = COURIER;
		types[7] = TIMESROMAN;
		types[8] = HELVETICA;

		return types;
	}

	/**
	 * Returns font styles.
	 * 
	 * @return String[] of font styles.
	 */
	public static String[] getFontStyles() {
		String[] styles = new String[3];
		styles[0] = PLAIN;
		styles[1] = BOLD;
		styles[2] = ITALIC;
		return styles;
	}
}
