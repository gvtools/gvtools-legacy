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
package com.iver.cit.gvsig.fmap.core.rendering;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;

/**
 * <p>
 * This is the integration test for all the legends implementing
 * the interface IClassifiedVectorLegend.<br>
 * </p>
 * <p>
 * It ensures that the legend tested will behave as it is expected
 * within the app in all the places where symbol classification 
 * <br>
 * </p>
 * <p>
 * Notice that the classification tested here is not linked to 
 * vector datasources so it can be used to test raster datasources
 * as well.
 * </p>
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class TestIClassifiedLegend extends TestCase {
	
	private IClassifiedLegend[] legends;


	protected void setUp() throws Exception {
		super.setUp();
		ILegend[] allLegends = TestILegend.getNewLegendInstances();
		// Filter the marker ones
		ArrayList<IClassifiedLegend> legends = new ArrayList<IClassifiedLegend>();

		for (int i = 0; i < allLegends.length; i++) {
			if (allLegends[i] instanceof IClassifiedLegend) {
				legends.add((IClassifiedLegend) allLegends[i]);

			}
		}
		this.legends = (IClassifiedLegend[]) legends.toArray(new IClassifiedLegend[legends.size()]);
	}

	
	/**
	 * ensures that the getSymbols method is returning al the symbols added
	 * to the legend.
	 *
	 * @return DOCUMENT ME!
	 */
	public void testGetSymbols() {
		System.out.println("TestIClassifiedLegend.testGetSymbols()");
		System.out.println("\t not yet implemented");
	}
	
	/**
	 * ensures that all the descriptions returned in the getDescriptions() are
	 * the same and in the same order than the descriptions of the symbols
	 * contained by the legend.
	 */
	public void testGetDescriptions() {
		System.out.println("TestIClassifiedLegend.testGetDescriptions()");
		System.out.println("\t not yet implemented");
	}

	

	/**
	 * ensures that all the descriptions returned in the testGetValues() are
	 * the same and in the same order than the values associated to each
	 * of the symbols. This is a must when you are serializing/persisting the
	 * legend and you serialize/persist each of the fields independently.
	 */
	public void testGetValues() {
		System.out.println("TestIClassifiedLegend.testGetValues()");
		System.out.println("\t not yet implemented");
	}

	
}
