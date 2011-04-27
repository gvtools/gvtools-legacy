/*
 * Created on 20-feb-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: Crono.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/05/24 21:12:36  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/02/20 19:43:27  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Component to see time (used to monitoring long processes).
 * 
 * @author azabala
 * 
 */
public class Crono extends JComponent implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7534000624022602134L;

	private final int ox = 4;

	private final int oy = 4;

	private int tam;

	private long start;

	private long end;

	private boolean counting;

	private Timer timer;

	/**
	 * @roseuid 3CA83ED3006E
	 */
	public Crono() {
		this(50);
	}

	/**
	 * @roseuid 3CA83ED3006F
	 */
	public Crono(int tam) {
		this.tam = tam;
		start = end = System.currentTimeMillis();
		timer = new Timer(1000, this);
		setPreferredSize(new Dimension(tam + 8, tam + 30));
		counting = false;
	}

	/**
	 * @roseuid 3CA83ED3007E
	 */
	public void actionPerformed(ActionEvent e) {
		end = System.currentTimeMillis();
		repaint();
	}

	/**
	 * @roseuid 3CA83ED30080
	 */
	private float posx(int radio, int grados) {
		return (float) (radio * Math.cos(Math.toRadians(grados - 90)));
	}

	/**
	 * @roseuid 3CA83ED3008D
	 */
	private float posy(int radio, int grados) {
		return (float) (radio * Math.sin(Math.toRadians(grados - 90)));
	}

	/**
	 * @roseuid 3CA83ED30090
	 */
	public void paintComponent(Graphics gr) {
		this.paintBorder(gr);
		Graphics2D g = (Graphics2D) gr;
		if (counting)
			g.setColor(Color.red);
		else
			g.setColor(Color.green);
		g.setStroke(new BasicStroke(3));
		g.fillArc(ox, oy, tam, tam, 0, 360);
		g.setColor(getForeground());
		g.drawArc(ox, oy, tam, tam, 0, 360);
		int radio = tam / 2;
		int cx = radio + ox;
		int cy = radio + oy;
		for (int i = 0; i < 360; i += 30) {
			float x0 = cx + posx(radio, i);
			float y0 = cy + posy(radio, i);
			float x1 = cx + posx(radio - 5, i);
			float y1 = cy + posy(radio - 5, i);
			g.draw(new Line2D.Double(x0, y0, x1, y1));
		}

		long v = end - start;
		long m = v / 1000;
		long mili = v - m * 1000;
		long s = m / 60;
		long secs = m - s * 60;
		long mi = s / 60;
		long mins = s - mi * 60;

		float mx = cx + posx(radio - 10, (int) secs * 6);
		float my = cy + posy(radio - 10, (int) secs * 6);
		g.draw(new Line2D.Float(cx, cy, mx, my));
		mx = cx + posx(radio - 20, (int) mins * 6);
		my = cy + posy(radio - 20, (int) mins * 6);
		g.draw(new Line2D.Float(cx, cy, mx, my));

		String str = mins + " mins " + secs + " secs";
		g.drawString(str, 0, tam + 20);
	}

	/**
	 * Para el reloj y vuelve a la situacion inicial.
	 * 
	 * @roseuid 3CA83ED3009C
	 */
	public void reset() {
		timer.stop();
		counting = false;
		start = end = System.currentTimeMillis();
		repaint();
	}

	/**
	 * Comienza a contar.
	 * 
	 * @roseuid 3CA83ED3009D
	 */
	public void start() {
		timer.start();
		counting = true;
		start = System.currentTimeMillis();
	}

	/**
	 * Para de contar
	 * 
	 * @roseuid 3CA83ED3009E
	 */
	public void stop() {
		end = System.currentTimeMillis();
		timer.stop();
		counting = false;
		repaint();
	}

	/**
	 * retorna el tiempo transcurrido en mili-segundos.
	 * 
	 * @roseuid 3CA83ED3009F
	 */
	public long getElapsed() {
		if (counting)
			return System.currentTimeMillis() - start;
		else
			return (end - start);
	}
}
