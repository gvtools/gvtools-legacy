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
package org.gvsig.symbology.fmap.rendering.filter.operations;
/**
 * Exception that can be caused by the incorrect creation of an Expression
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */
public class ExpressionException extends Exception {

	public static final int CLASS_CASTING_EXCEPTION = 1;
	public static final int INCORRECT_NUMBER_OF_ARGUMENTS = 2;
	public static final int ARGUMENT_ADDED_TO_CONSTANT = 4;
	public static final int DIVIDED_BY_CERO = 8;
	public static final int NO_CLASSIF_NAME = 16;
	
	private int type;
	
	public ExpressionException(int type) {
		super();
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
}
