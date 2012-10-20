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

import java.util.ArrayList;

/**
 * Interface that all the operators of an expression to be parsed has to
 * implement
 * 
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */

public interface Expression {

	public String getName();

	/**
	 * Returns true if the expression is correctly evaluated using the HashTable
	 * for that
	 * 
	 * @param symbol_table
	 * @return
	 * @throws ExpressionException
	 */
	public Object evaluate() throws ExpressionException;

	/**
	 * Returns an String containing the pattern for the operator. That is, the
	 * type of its arguments and its solution
	 * 
	 * @return String with the pattern
	 */
	public String getPattern();

	/**
	 * Adds an argument (which is an Expression) to the operator in the ith
	 * position.
	 * 
	 * @param i
	 *            position where the argument will be added
	 * @param arg
	 *            argument to be added
	 */
	public void addArgument(int i, Expression arg);

	/**
	 * Obtains the arguments of the Expression
	 * 
	 * @return ArrayList with the arguments
	 */
	public ArrayList<Expression> getArguments();

	/**
	 * Sets the arguments of the Expression
	 * 
	 * @param arguments
	 */
	public void setArguments(ArrayList<Expression> arguments);

	/**
	 * Checks if the semantic of the Expression is correct using for that the
	 * values of the HashTable if it is necessary
	 * 
	 * @param symbol_table
	 *            which contains the symbols
	 * @throws ExpressionException
	 */
	public void check() throws ExpressionException;
}
