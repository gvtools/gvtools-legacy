package com.iver.cit.gvsig.fmap.rendering;

import com.iver.cit.gvsig.fmap.FMapEvent;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.layers.LegendEvent;
/**
 * <p>The class <code>LegendClearEvent</code> stores all necessary information of an event 
 * produced when a legend is cleared.</p>
 * 
 * @see FMapEvent
 * @author Vicente Caballero Navarro
 */
public class LegendClearEvent extends ClassificationLegendEvent {
	private ISymbol[] oldSymbols;
	/**
	 * Constructor method
	 * @param oldSymbols
	 */
	public LegendClearEvent(ISymbol[] oldSymbols) {
		this.oldSymbols = oldSymbols;
	}
	/**
	 * Obtains the old symbols of the legend
	 * @return
	 */
	public ISymbol[] getOldSymbols() {
		return oldSymbols;
	}
	/**
	 * Returns the type of the event
	 */
	public int getEventType() {
		return LegendEvent.LEGEND_CLEARED;
	}
}