package com.iver.cit.gvsig.fmap.core.symbols;

public interface IDrawFillSymbol {

	IFillSymbol makeSymbolTransparent(IFillSymbol newSymbol);

	boolean isSuitableFor(IFillSymbol newSymbol);

}
