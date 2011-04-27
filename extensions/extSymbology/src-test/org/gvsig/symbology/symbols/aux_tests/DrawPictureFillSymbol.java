package org.gvsig.symbology.symbols.aux_tests;

import java.io.IOException;

import org.gvsig.symbology.fmap.symbols.PictureFillSymbol;

import com.iver.cit.gvsig.fmap.core.symbols.IDrawFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;

public class DrawPictureFillSymbol implements IDrawFillSymbol {

	public boolean isSuitableFor(IFillSymbol newSymbol) {
		return (newSymbol instanceof PictureFillSymbol);
	}

	public IFillSymbol makeSymbolTransparent(IFillSymbol newSymbol) {
		if (newSymbol instanceof PictureFillSymbol) {
			PictureFillSymbol mySymbol = (PictureFillSymbol) newSymbol;
			try {
				((PictureFillSymbol) mySymbol).setImage(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return mySymbol;
		}
		return newSymbol;
	}
}
