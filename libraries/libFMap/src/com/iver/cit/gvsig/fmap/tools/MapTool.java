/*
 * Created on 28-oct-2004
 */
package com.iver.cit.gvsig.fmap.tools;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.geom.Point2D;

import com.iver.cit.gvsig.fmap.MapControl;

/**
 * Herramienta del MapControl.
 * Ejecuta acciones respondiendo a eventos, por delegación desde MapControl.
 * 
 * @author Luis W. Sevilla <sevilla_lui@gva.es>
 */
public abstract class MapTool {
	protected MapControl mc = null;
	protected Cursor cursor = null;
	
	public MapTool(MapControl mc) {
		this.mc = mc;
	}
	/**
	 * Recibe los eventos del ratón.
	 */
	abstract public void cmd(Point2D pt, int bt, int mouseEvent);
	
	
    abstract public void paintComponent(Graphics g);
	/**
	 * Devuelve un Bitset con los eventos de raton que requiere.
	 * @return eventsWanted (BitSet)
	 * /
	public int getEventsWanted() {
		return eventsWanted;
	}*/
	
	public Cursor getCursor() {
		return cursor;
	}
}
