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


import java.util.HashMap;
import java.util.Hashtable;

import com.hardcode.gdbms.engine.values.Value;

/**
 * Implements methods that allow the user to create new operators to be
 * included in an expression to be parsed
 *
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */
public class OperatorsFactory  {

	private static OperatorsFactory instance = new OperatorsFactory();
    public HashMap<String,Class> functions = new HashMap<String,Class>();
	Class myClass;

    public static OperatorsFactory getInstance()
    {
        return instance;
    }

    private OperatorsFactory()
    {

        // logical functions
        addOperator(OrOperator.class);
        addOperator(AndOperator.class);
        addOperator(NotOperator.class);
        addOperator(EqOperator.class);
        addOperator(NeOperator.class);
        addOperator(LessThanOperator.class);
        addOperator(LessThanOrEqualsOperator.class);
        addOperator(GreaterThanOperator.class);
        addOperator(GreaterThanOrEqualsOperator.class);
        addOperator(AddOperator.class);
        addOperator(MinusOperator.class);
        addOperator(MultOperator.class);
        addOperator(DivOperator.class);
        addOperator(IsBetweenOperator.class);
        addOperator(IsNullOperator.class);
        addOperator(ReplaceOperator.class);

    }

    public void addOperator(Class<? extends Expression> newClass)
    {
        if (!Expression.class.isAssignableFrom(newClass))
            throw new RuntimeException("Tried to register an operator which does not implement the Expression inteface");

        try {
			functions.put(((Expression) newClass.
					getConstructor(new Class[] { Hashtable.class }).newInstance( new Hashtable<String, Value>())).getName(), newClass);

		} catch (Exception e) {
			 throw new RuntimeException("Tried to register an operator which does not implement the Expression inteface");
		}

    }


    public Expression getOperator(String name) throws Exception {

        return (Expression)functions.get(name).newInstance();

    }

    public Expression createOperator(String name) throws Exception
    {
    	myClass = (Class)functions.get(name);
        if (myClass == null)
            return null;

        return (Expression)myClass.newInstance();
    }



}
