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
package com.iver.cit.gvsig.project.documents.layout.tools.behavior;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.VolatileImage;

import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.project.documents.layout.LayoutControl;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrameUseFMap;
import com.iver.cit.gvsig.project.documents.layout.tools.listener.LayoutMoveListener;
import com.iver.cit.gvsig.project.documents.layout.tools.listener.LayoutToolListener;

/**
 * Behaviour que espera un listener de tipo MoveListener.
 * 
 * @author Vicente Caballero Navarro
 */
public class LayoutViewMoveBehavior extends LayoutBehavior {
	private LayoutMoveListener listener;
	private LayoutViewZoomBehavior lvzb;

	/**
	 * Crea un nuevo MoveBehavior.
	 * 
	 * @param pli
	 *            listener.
	 */
	public LayoutViewMoveBehavior(LayoutMoveListener lpl) {
		listener = lpl;
		lvzb = new LayoutViewZoomBehavior(lpl);
	}

	public void setLayoutControl(LayoutControl lc) {
		super.setLayoutControl(lc);
		lvzb.setLayoutControl(lc);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		lvzb.paintComponent(g);
		IFFrame[] fframes = getLayoutControl().getLayoutContext().getFFrames();
		for (int i = 0; i < fframes.length; i++) {
			if (fframes[i] instanceof IFFrameUseFMap) {
				IFFrameUseFMap fframe = (IFFrameUseFMap) fframes[i];

				if (((IFFrame) fframe).getSelected() != IFFrame.NOSELECT) {
					Rectangle2D.Double rec = ((IFFrame) fframe)
							.getBoundingBox(getLayoutControl().getAT());

					if (getLayoutControl().getImage() != null) {
						rec = (Rectangle2D.Double) rec
								.createIntersection(getLayoutControl()
										.getVisibleRect());
					}

					if (fframe.getBufferedImage() != null) {
						GraphicsEnvironment ge = GraphicsEnvironment
								.getLocalGraphicsEnvironment();
						GraphicsConfiguration gc = ge.getDefaultScreenDevice()
								.getDefaultConfiguration();
						VolatileImage image = null;
						int transparency = getLayoutControl().getImage()
								.getTransparency();
						int width = (int) rec.getWidth();
						int height = (int) rec.getHeight();
						image = gc.createCompatibleVolatileImage(width, height,
								transparency);
						Graphics gh = image.createGraphics();
						gh.setColor(Color.white);
						gh.fillRect(0, 0, image.getWidth(), image.getHeight());
						gh.drawImage(fframe.getBufferedImage(),
								getLayoutControl().getLastPoint().x
										- getLayoutControl().getPointAnt().x,
								getLayoutControl().getLastPoint().y
										- getLayoutControl().getPointAnt().y,
								getLayoutControl());
						getLayoutControl().getLayoutDraw().drawGrid(
								(Graphics2D) gh);
						fframe.refresh();
						g.drawImage(image, (int) rec.getX(), (int) rec.getY(),
								null);
					}
				}
			}
		}
	}

	/**
	 * @throws BehaviorException
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) throws BehaviorException {
		super.mousePressed(e);
		PointEvent event = new PointEvent(e.getPoint(), e);
		listener.press(event);
	}

	/**
	 * Reimplementación del método mouseReleased de Behavior.
	 * 
	 * @param e
	 *            MouseEvent
	 * 
	 * @throws BehaviorException
	 *             Excepción lanzada cuando el Behavior.
	 */
	public void mouseReleased(MouseEvent e) throws BehaviorException {
		super.mouseReleased(e);
		PointEvent event = new PointEvent(e.getPoint(), e);
		listener.release(event);

	}

	/**
	 * Reimplementación del método mouseDragged de Behavior.
	 * 
	 * @param e
	 *            MouseEvent
	 * @throws BehaviorException
	 */
	public void mouseDragged(MouseEvent e) throws BehaviorException {
		super.mouseDragged(e);
		PointEvent event = new PointEvent(e.getPoint(), e);
		listener.drag(event);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#setListener(com.iver.cit.gvsig.fmap.tools.ToolListener)
	 */
	public void setListener(LayoutToolListener listener) {
		this.listener = (LayoutMoveListener) listener;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#getListener()
	 */
	public LayoutToolListener getListener() {
		return listener;
	}
}
