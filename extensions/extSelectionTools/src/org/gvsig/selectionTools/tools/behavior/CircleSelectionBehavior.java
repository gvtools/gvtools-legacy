package org.gvsig.selectionTools.tools.behavior;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Behavior.CircleBehavior;
import com.iver.cit.gvsig.fmap.tools.Events.MeasureEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.CircleListener;

/**
 * 
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class CircleSelectionBehavior extends CircleBehavior {
	/**
	 * 
	 */
	private CircleListener listener;

	/**
	 * Auxiliary point that represents a corner selected in image coordinates.
	 */
	private Point2D m_PointAnt;

	public CircleSelectionBehavior(CircleListener zili) {
		super(zili);

		listener = zili;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt
	 * .Graphics)
	 */
	public void paintComponent(Graphics g) {
		double radio;
		BufferedImage img = getMapControl().getImage();
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.black);
		g.setXORMode(Color.white);
		if ((m_FirstPoint != null) && (m_LastPoint != null)) {
			radio = m_LastPoint.distance(m_FirstPoint);
			Arc2D.Double arc = new Arc2D.Double(m_FirstPoint.getX() - radio,
					m_FirstPoint.getY() - radio, 2 * radio, 2 * radio, 0, 360,
					Arc2D.OPEN);

			((Graphics2D) g).draw(arc);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#mousePressed(java.awt
	 * .event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {

		Point pScreen = e.getPoint();
		m_PointAnt = pScreen;

		if ((!isClicked) && (e.getButton() == MouseEvent.BUTTON1)) {
			m_PointAnt = pScreen;
			m_FirstPoint = m_PointAnt;
			isClicked = true;
		}

		if (listener.cancelDrawing()) {
			getMapControl().cancelDrawing();
			isClicked = false;
		}
		getMapControl().repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#mouseReleased(java.awt
	 * .event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) throws BehaviorException {
		if ((m_FirstPoint == null) || (m_LastPoint == null))
			return;

		Point2D p1;
		Point2D p2;
		Point pScreen = e.getPoint();

		ViewPort vp = getMapControl().getMapContext().getViewPort();

		p1 = vp.toMapPoint(m_FirstPoint);
		p2 = vp.toMapPoint(pScreen);

		// Fijamos el nuevo extent
		Rectangle2D.Double r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(p1, p2);

		Rectangle2D rectPixel = new Rectangle();
		rectPixel.setFrameFromDiagonal(m_FirstPoint, pScreen);

		Double[] x = new Double[2];
		Double[] y = new Double[2];
		x[0] = new Double(p1.getX());
		x[1] = new Double(p2.getX());
		y[0] = new Double(p1.getY());
		y[1] = new Double(p2.getY());
		MeasureEvent event = new MeasureEvent(x, y, e);
		listener.circle(event);
		getMapControl().repaint();

		m_FirstPoint = null;
		m_LastPoint = null;
		isClicked = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#mouseDragged(java.awt
	 * .event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent e) throws BehaviorException {
		mouseMoved(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#mouseMoved(java.awt.event
	 * .MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) throws BehaviorException {
		if (!isClicked)
			return;

		m_LastPoint = e.getPoint();

		if (m_FirstPoint == null)
			return;
		Point2D p1;
		Point2D p2;
		Point pScreen = e.getPoint();

		ViewPort vp = getMapControl().getMapContext().getViewPort();

		p1 = vp.toMapPoint(m_FirstPoint);
		p2 = vp.toMapPoint(pScreen);

		// Fijamos el nuevo extent
		Rectangle2D.Double r = new Rectangle2D.Double();
		r.setFrameFromDiagonal(p1, p2);

		Rectangle2D rectPixel = new Rectangle();
		rectPixel.setFrameFromDiagonal(m_FirstPoint, pScreen);

		Double[] x = new Double[2];
		Double[] y = new Double[2];
		x[0] = new Double(p1.getX());
		x[1] = new Double(p2.getX());
		y[0] = new Double(p1.getY());
		y[1] = new Double(p2.getY());
		MeasureEvent event = new MeasureEvent(x, y, e);
		listener.circle(event);
		getMapControl().repaint();
	}
}
