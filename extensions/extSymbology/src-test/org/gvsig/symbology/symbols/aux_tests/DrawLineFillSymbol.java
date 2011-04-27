package org.gvsig.symbology.symbols.aux_tests;

import org.gvsig.symbology.fmap.symbols.LineFillSymbol;

import com.iver.cit.gvsig.fmap.core.symbols.IDrawFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;

public class DrawLineFillSymbol implements IDrawFillSymbol {
	
	
	
	public IFillSymbol makeSymbolTransparent(IFillSymbol newSymbol) {
		
		if (newSymbol instanceof LineFillSymbol) {
			LineFillSymbol mySymbol = (LineFillSymbol) newSymbol;
			((LineFillSymbol) mySymbol).setLineSymbol(null);
			return mySymbol;
		}
		return newSymbol;
		
	}

	public boolean isSuitableFor(IFillSymbol newSymbol) {
		return (newSymbol instanceof LineFillSymbol);
	}




}
