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

import java.util.ArrayList;

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.TestISymbol;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;

/**
 * Integration tests for the legends implementing IVectorLegend
 * 
 * it tests all the operations supplied by this interface
 * 
 * TestVectorLegend.java
 *
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Jun 12, 2008
 *
 */
public class TestVectorLegend extends TestCase {
	private IVectorLegend[] legends;
	private ISymbol[] symbols;

	protected void setUp() throws Exception {
		super.setUp();
		ILegend[] allLegends = TestILegend.getNewLegendInstances();
		// Filter the marker ones
		ArrayList<IVectorLegend> legends = new ArrayList<IVectorLegend>();

		for (int i = 0; i < allLegends.length; i++) {
			if (allLegends[i] instanceof IVectorLegend) {
				IVectorLegend leg = (IVectorLegend) allLegends[i];
				legends.add(leg);

			}
		}
		this.legends = legends.toArray(new IVectorLegend[legends.size()]);
		symbols = TestISymbol.getNewSymbolInstances();
	}

	public void testGetShapeType() {
		System.out.println("TestVectorLegend.testGetShapeType()");
		System.out.println("\t not yet implemented");
		
	}

	public void testSetShapeType() {
		System.out.println("TestVectorLegend.testSetShapeType()");
		System.out.println("\t not yet implemented");
	}

	public void testSetDefaultSymbol() {
		System.out.println("TestVectorLegend.testSetDefaultSymbol()");
		System.out.println("\t not yet implemented");
		
	}

	
	public void testIsUseDefaultSymbol() {
		System.out.println("TestVectorLegend.testIsUseDefaultSymbol()");
		System.out.println("\t not yet implemented");
	}

	public void testUseDefaultSymbol() {
		System.out.println("TestVectorLegend.testUseDefaultSymbol()");
		System.out.println("\t not yet implemented");
	}

}
