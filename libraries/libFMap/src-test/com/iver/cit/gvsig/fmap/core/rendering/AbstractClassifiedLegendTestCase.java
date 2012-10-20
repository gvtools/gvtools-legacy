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

import com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend;

/**
 * <p>
 * This a handy class to ease the use of integration tests.
 * </p>
 * 
 * <p>
 * Your legend test itself must extend this if the legend being tested is an
 * instance of IClassifiedLegend.
 * </p>
 * <p>
 * You must not add this to the TestILegend. What you have to do is to add your
 * subclass like follows. TestILegend.addLegendToTest(new
 * VectorialUniqueValueLegendTest());
 * </p>
 * 
 * NOTE: Although it has no abstract components, the class is abstract in
 * purpose since it does not test any concrete legend, but initializes legends
 * of this kind. Subclass it to test any concrete Legend: For example:
 * SingleLegendTestCase extends AbstractVectorLegendTestCase
 * 
 * AbstractClassifiedLegendTestCase.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 12, 2008
 * 
 */
public abstract/* <-MUST BE ABSTRACT!! */class AbstractClassifiedLegendTestCase
		extends AbstractLegendTestCase {
	private Object[] sampleValues;

	/**
	 * <p>
	 * This a handy class to ease the use of integration tests.
	 * </p>
	 * 
	 * <p>
	 * Your legend test itself must extend this if the legend being tested is an
	 * instance of IClassifiedLegend.
	 * </p>
	 * <p>
	 * You must not add this to the TestILegend. What you have to do is to add
	 * your subclass like follows. TestILegend.addLegendToTest(new
	 * VectorialUniqueValueLegendTest());
	 * </p>
	 * 
	 */
	public AbstractClassifiedLegendTestCase(
			Class<? extends IClassifiedLegend> legClazz, Object[] sampleValues) {
		super(legClazz);
		this.sampleValues = sampleValues;
	}

	@Override
	public Object[] getTestSampleValues() {
		return sampleValues;
	}

}
