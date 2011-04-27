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

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.TestISymbol;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.ZSort;
/**
 * This test checks the different properties that the zSort class has to keep.
 *
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 *
 */
public class TestZSort extends TestCase {
	private ILegend legends[];
	private ISymbol[] symbols ;
	private IMultiLayerSymbol[] multiLayerSymbols;

	@Override
	protected void setUp() throws Exception {
		legends = TestILegend.getNewLegendInstances();
		symbols = TestISymbol.getNewSymbolInstances();
		multiLayerSymbols = getMultiLayerSymbols();

		for (int i = 0; i < legends.length; i++) {
			for (int j = 0; j < symbols.length; j++) {
				addSymbol2Legend(legends[i], symbols[j]);
			}
			for (int j = 0; j < multiLayerSymbols.length; j++) {
				if(multiLayerSymbols[j] != null)
					addSymbol2Legend(legends[i], multiLayerSymbols[j]);
			}
		}
	}
	/**
	 * Creates an array with different kinds of IMultiLayerSymbol to be tested by the different methods
	 * @return
	 */
	private IMultiLayerSymbol[] getMultiLayerSymbols() {

		IMultiLayerSymbol[] myMultiLayerSymbols = new IMultiLayerSymbol[symbols.length];

		for (int i = 0; i < symbols.length; i++) {
			//MultiLayerSymbol made of ITextSymbols are not allowed for the moment
			if(! (symbols[i] instanceof ITextSymbol)) {
				IMultiLayerSymbol myMultiLayer = SymbologyFactory.createEmptyMultiLayerSymbol(symbols[i].getSymbolType());
				myMultiLayer.addLayer(SymbologyFactory.createDefaultSymbolByShapeType(symbols[i].getSymbolType()));
				myMultiLayerSymbols[i] = myMultiLayer;
			}
		}
		return myMultiLayerSymbols;
	}
	/**
	 * This test ensures that if a legend has a ZSort then, when we do changes in a symbol,
	 * these will be reflected in the ZSort.
	 */
	public void testInitializationOnClassifiedVectorLegends() {

		for (int i = 0; i < legends.length; i++) {
			if(legends[i] instanceof IClassifiedVectorLegend) {
				ISymbol sym = SymbologyFactory.createDefaultFillSymbol();
				IClassifiedVectorLegend icvl = (IClassifiedVectorLegend) legends[i];
				addSymbol2Legend(icvl, sym);
				ZSort myZSort = new ZSort(icvl);
				icvl.setZSort(myZSort);
				assertTrue("the symbols contained in the ZSort for legend "+ legends[i].getClassName()+
						" are not correct ", sym == myZSort.getSymbols()[0]);
			}
		}
	}

	/**
	 * Will test the consistency of the symbol set handled by the ZSort after
	 * the legend is edited.... for the ZSort purposes, the edit that has to
	 * be kept in account is the change of any of the symbols contained in
	 * the legend. (Changes on values have nothing to do with ZSorts)
	 */
	public void testSymbolReplacement() {

		ISymbol[] zsortSymbols;
		ISymbol[] legendSymbols;

		for (int i = 0; i < legends.length; i++) {

			ZSort myZSort = new ZSort(legends[i]);
			zsortSymbols = myZSort.getSymbols();
			if(legends[i] instanceof IClassifiedLegend) {
				legendSymbols = ((IClassifiedLegend) legends[i]).getSymbols();
				for (int j = 0; j < legendSymbols.length; j++) {

					assertTrue("the symbols contained in the ZSort for the legend "+ legends[i].getClassName()+
							" are not correct", zsortSymbols[j].equals(legendSymbols[j]));

					replaceSymbol2Legend(legends[i],legendSymbols[j]);

					assertTrue("the symbols contained in the ZSort for the legend "+ legends[i].getClassName()+
							" are not correct after the modification of one of its symbols", myZSort.getSymbols()[j].equals(((IClassifiedLegend) legends[i]).getSymbols()[j]));

				}
			}
		}
	}

	/**
	 * If testSymbolReplacement passes then it is sure that the symbols are
	 * replaced in ZSort aswell. This test ensures that the levels set in
	 * the ZSort for a given symbol (MultiLayerSymbol) are kept after the
	 * replacement of the symbol.
	 *
	 * In case the new symbol has less levels than the previous, then the
	 * levels are kept until the max layer of the new multilayer symbol.
	 * ([0,2,3] -> [0,2])
	 *
	 * If the new symbol has more levels than the previous, then the level
	 * used for further levels is the same than the one in the last
	 * multilayersymbol's layer's symbol
	 * ([0,2,3] -> [0,2,3,3,3,3......])
	 * ([0,3,2] -> [0,3,2,2,2,2......])
	 */
	public void testSymbolLevelReplacementConsistency() {

		for (int i = 0; i < legends.length; i++) {
			ZSort myZSort = new ZSort(legends[i]);
			if(legends[i] instanceof IClassifiedLegend) {
				ISymbol[] legendSymbols = ((IClassifiedLegend) legends[i]).getSymbols();
				for (int j = 0; j < legendSymbols.length; j++) {
					if(legendSymbols[j] instanceof IMultiLayerSymbol) {

						IMultiLayerSymbol myMultiLayer = (IMultiLayerSymbol)legendSymbols[j];

						// case 1: the replaced symbol has the same amount of layers
						MultiLayerLineSymbol testSymbol = new MultiLayerLineSymbol();
						int[] levels = myZSort.getLevels(myMultiLayer);
						for (int k = 0; k < myMultiLayer.getLayerCount(); k++) {
							testSymbol.addLayer(SymbologyFactory.createDefaultLineSymbol());
						}
						//Replacement of the symbol in the legend
						((IClassifiedVectorLegend)legends[i]).replace(myMultiLayer, testSymbol);

						int[] newLevels = myZSort.getLevels(testSymbol);
						for (int k = 0; k < testSymbol.getLayerCount(); k++) {
							assertTrue("does not keep symbol level (CASE symbol size are equal)in the legend" +
									legends[i].getClassName(),levels[k] == newLevels[k]);
						}
					}
				}
			}
		}
	}


	/**
	 * Implements the addition of a new symbol to a specific legend
	 *
	 * @param leg legend where the symbol will be added
	 * @param sym symbol to be added
	 */
	private void addSymbol2Legend(ILegend leg, ISymbol sym) {

		if (leg instanceof IClassifiedLegend) {
			Object[] values = TestILegend.getLegendTestCaseByLegend(leg).getTestSampleValues();
			if (values.length>1) {
				IClassifiedVectorLegend ivul =  (IClassifiedVectorLegend) leg;
				ivul.addSymbol(values[0], sym);
			} else {
				fail("test "+TestILegend.getLegendTestCaseByLegend(leg).getClass().getName()+" does not provide test data values, please fix or tests don't give any warranty.");
			}
		}
	}

	/**
	 * Implements the replacement of a symbol in a specific legend
	 *
	 * @param leg legend where the symbol will be replaced
	 * @param sym symbol to be replaced
	 */
	private void replaceSymbol2Legend(ILegend leg, ISymbol sym) {

		if (leg instanceof IClassifiedVectorLegend) {

			if(sym instanceof IMultiLayerSymbol) {
				ISymbol oldSymbol = sym;
				ISymbol newSym = SymbologyFactory.createEmptyMultiLayerSymbol(sym.getSymbolType());
				newSym.setDescription("New MultiLayerSymbol");
				((IClassifiedVectorLegend)leg).replace(oldSymbol, newSym);
			}
			else {
				ISymbol newSymbol = SymbologyFactory.createDefaultSymbolByShapeType(sym.getSymbolType());
				newSymbol.setDescription("New Symbol");
				((IClassifiedVectorLegend)leg).replace(sym, newSymbol);
			}

		}
	}

}
