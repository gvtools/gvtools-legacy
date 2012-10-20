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
package com.iver.cit.gvsig.fmap.core.rendering;

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.rendering.ILegend;

/**
 * 
 * Your legend test itself must extend this. </p>
 * <p>
 * You must not add this to the TestILegend. What you have to do is to add your
 * subclass like follows. TestILegend.addLegendToTest(new
 * SingleSymbolLegendTest());
 * </p>
 * 
 * AbstractLegendTestCase.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 11, 2008
 * 
 */
public abstract class AbstractLegendTestCase extends TestCase {
	protected Class<? extends ILegend> legClazz;

	/**
	 * 
	 * Your legend test itself must extend this. </p>
	 * <p>
	 * You must not add this to the TestILegend. What you have to do is to add
	 * your subclass like follows. TestILegend.addLegendToTest(new
	 * SingleSymbolLegendTest());
	 * </p>
	 * 
	 * @param legClazz
	 */
	public AbstractLegendTestCase(Class<? extends ILegend> legClazz) {
		this.legClazz = legClazz;
	}

	public ILegend newInstance() {
		try {
			ILegend leg = (ILegend) legClazz.newInstance();
			initLegend(leg);
			return leg;
		} catch (InstantiationException ex) {
			// TODO Auto-generated catch block
			fail("Instantiating class, cannot test a non-instantiable legend '"
					+ TestILegend.shortClassName(legClazz) + "'");
		} catch (IllegalAccessException ex) {
			// TODO Auto-generated catch block
			fail("Class not instantiable '"
					+ TestILegend.shortClassName(legClazz) + "'");
		} catch (ClassCastException ccEx) {
			fail("Cannot test a non legend class '"
					+ TestILegend.shortClassName(legClazz) + "'");
		}
		return null;
	}

	public abstract void initLegend(ILegend leg);

	public boolean equals(Object o) {
		return (o.getClass().equals(getClass()));
	}

	/**
	 * <p>
	 * As each legend type requires different value types (Value, FInterval, and
	 * others) this method will provide the classification values of the
	 * required types.
	 * </p>
	 * <p>
	 * Please try to keep a minimum consictency between values provided here and
	 * the values in the MockDataSource used in the test
	 * </p>
	 * 
	 * @return
	 */
	public abstract Object[] getTestSampleValues();
}
