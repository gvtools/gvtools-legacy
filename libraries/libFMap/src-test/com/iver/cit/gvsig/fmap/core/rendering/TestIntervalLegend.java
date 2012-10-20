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
import java.util.Hashtable;

import junit.framework.TestCase;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.TestISymbol;
import com.iver.cit.gvsig.fmap.rendering.AbstractIntervalLegend;
import com.iver.cit.gvsig.fmap.rendering.FInterval;
import com.iver.cit.gvsig.fmap.rendering.ILegend;

/**
 * Integration test to ensure that the legends which implements the
 * IVectorialIntervalLegend interface (and extend AbstractIntervalLegend) follow
 * the rules that manage them by the application.
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class TestIntervalLegend extends TestCase {
	private AbstractIntervalLegend[] legends;

	protected void setUp() throws Exception {
		super.setUp();
		ILegend[] allLegends = TestILegend.getNewLegendInstances();
		// Filter the marker ones
		ArrayList<AbstractIntervalLegend> legends = new ArrayList<AbstractIntervalLegend>();

		for (int i = 0; i < allLegends.length; i++) {
			if (allLegends[i] instanceof AbstractIntervalLegend) {
				legends.add((AbstractIntervalLegend) allLegends[i]);

			}
		}
		this.legends = (AbstractIntervalLegend[]) legends
				.toArray(new AbstractIntervalLegend[legends.size()]);
	}

	public static final FInterval interval0 = new FInterval(0, 2);
	public static final FInterval interval1 = new FInterval(3, 5);
	public static final FInterval interval2 = new FInterval(6, 8);
	public static final FInterval interval3 = new FInterval(9, 11);
	private static final int FIELDID = 0;

	private Hashtable<FInterval, ISymbol> symTable;

	private ISymbol[] symbols = TestISymbol.getNewSymbolInstances();
	private FInterval[] sampleIntervals = new FInterval[] { interval0,
			interval1, interval2, interval3, };
	private IFeature[] features = AbstractVectorLegendTestCase.getFeatures();

	public void testGetSymbolByFeature() {
		ISymbol tableSym = null;

		// fills the legends
		for (int i = 0; i < legends.length; i++) {
			fillClassifiedIntervalLegend(legends[i], sampleIntervals);
		}

		for (int i = 0; i < legends.length; i++) {
			// For each feature
			for (int j = 0; j < features.length; j++) {
				IFeature myFeature = features[i];
				// takes the value of the field that identifies the feature
				Value val = myFeature.getAttributes()[FIELDID];
				// the last value is used to access to the hash table to obtain
				// a symbol

				if (interval0.isInInterval(val))
					tableSym = symTable.get(interval0);
				else if (interval1.isInInterval(val))
					tableSym = symTable.get(interval1);
				else if (interval2.isInInterval(val))
					tableSym = symTable.get(interval2);
				else if (interval3.isInInterval(val))
					tableSym = symTable.get(interval3);

				// takes the symbol from a legend using the feature
				ISymbol legendSym = legends[i].getSymbolByFeature(myFeature);
				// compares that both symbols are the same
				assertEquals(legendSym.getClassName()
						+ " fails with the comparation of the class symbols",
						legendSym, tableSym);
			}
		}
	}

	/**
	 * This method is used to add symbols to a legend.That is, it takes an array
	 * of AbstractIntervalLegend which is empty and, using a second array of
	 * FIntervals(values), the first one is filled.Also, a hash table is filled
	 * using the array of FIntervals (it will be useful in some tests to check
	 * that a symbol can be taken using a feature) .
	 * 
	 * @param legend
	 * @return
	 */
	private void fillClassifiedIntervalLegend(AbstractIntervalLegend legend,
			FInterval[] values) {
		// initialize the hash table
		symTable = new Hashtable();

		// to add symbols to the legend and the hash table
		for (int j = 0; j < values.length; j++) {

			ISymbol sym = symbols[j % symbols.length];
			legend.addSymbol(values[j], sym);
			symTable.put(values[j], sym);
		}
	}

	// public IInterval getInterval(Value v) ;
	// public int getIntervalType();
	//
	//
	// /**
	// *
	// * Returns the symbol starting from an interval
	// *
	// * @param key interval.
	// *
	// * @return symbol.
	// */
	// public ISymbol getSymbolByInterval(IInterval key);
	//
	// /**
	// * Inserts the type of the classification of the intervals.
	// *
	// * @param tipoClasificacion type of the classification.
	// */
	// public void setIntervalType(int tipoClasificacion);
	/**
	 * This test ensures that when a legend is filled, the number of symbols
	 * added is correct. To do it, is checked that the number of symbols of a
	 * legend is the same as the length of the array of example values that we
	 * have.
	 * 
	 * @throws ReadDriverException
	 */
	public void testICLAdittion() throws ReadDriverException {

		// Fills the legend
		for (int i = 0; i < legends.length; i++) {
			fillClassifiedIntervalLegend(legends[i], sampleIntervals);
		}

		for (int i = 0; i < legends.length; i++)
			assertEquals(legends[i].getClassName()
					+ " fails with the comparation of the number of symbols",
					legends[i].getSymbols().length, sampleIntervals.length);

	}

	/**
	 * This test ensures that the symbols that we have previously added to a
	 * legend are accessible using its features.To do it, this test compares the
	 * symbol taken from the legend with the symbol taken from the hashTable
	 * (using the same feature).
	 * 
	 * @throws ReadDriverException
	 */

	public void testICLCheckValueSymbols() throws ReadDriverException {
		ISymbol tableSym = null;

		// fills the legends
		for (int i = 0; i < legends.length; i++) {
			fillClassifiedIntervalLegend(legends[i], sampleIntervals);
		}

		for (int i = 0; i < legends.length; i++) {
			// For each feature
			for (int j = 0; j < features.length; j++) {
				IFeature myFeature = features[i];
				// takes the value of the field that identifies the feature
				Value val = myFeature.getAttributes()[FIELDID];
				// the last value is used to access to the hash table to obtain
				// a symbol

				if (interval0.isInInterval(val))
					tableSym = (ISymbol) symTable.get(interval0);
				else if (interval1.isInInterval(val))
					tableSym = (ISymbol) symTable.get(interval1);
				else if (interval2.isInInterval(val))
					tableSym = (ISymbol) symTable.get(interval2);
				else if (interval3.isInInterval(val))
					tableSym = (ISymbol) symTable.get(interval3);

				AbstractIntervalLegend leg = (AbstractIntervalLegend) legends[i];
				// takes the symbol from a legend using the feature
				ISymbol legendSym = leg.getSymbolByFeature(myFeature);
				// compares that both symbols are the same
				assertEquals(legendSym.getClassName()
						+ " fails with the comparation of the class symbols",
						legendSym, tableSym);
			}
		}

	}

}
