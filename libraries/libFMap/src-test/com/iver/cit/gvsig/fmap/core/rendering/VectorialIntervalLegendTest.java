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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.rendering.FInterval;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;

/**
 * 
 * this is an initializer for the VectorialIntervalLegend legend for the
 * integration tests.
 * 
 * VectorialIntervalLegendTest.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 12, 2008
 * 
 */
public class VectorialIntervalLegendTest extends
		AbstractClassifiedLegendTestCase {

	public VectorialIntervalLegendTest() {
		super(VectorialIntervalLegend.class, (Object[]) new FInterval[] {
				TestIntervalLegend.interval0, TestIntervalLegend.interval1,
				TestIntervalLegend.interval2, TestIntervalLegend.interval3, });
	}

	@Override
	public void initLegend(ILegend leg) {
		VectorialIntervalLegend vil = (VectorialIntervalLegend) leg;
		int classificationFieldIndex = 0; // any within mockdatasource field
											// count range should be ok
		try {
			vil.setClassifyingFieldTypes(new int[] { TestClassifiedVectorLegend.mockDataSource
					.getFieldType(classificationFieldIndex) });
		} catch (ReadDriverException e) {
			fail("this shouldn't happen. This does not necessary mean that the legend does not pass the test"
					+ "but rather theres is a bug in the test itself. Please have a look to the data source used in"
					+ "the test");
		}
		vil.setClassifyingFieldNames(new String[] { TestClassifiedVectorLegend.mockDataSource.fieldNames[classificationFieldIndex] });
	}

}
