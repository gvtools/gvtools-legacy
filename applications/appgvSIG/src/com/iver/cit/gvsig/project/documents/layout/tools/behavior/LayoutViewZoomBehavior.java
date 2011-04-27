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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.VolatileImage;

import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;
import com.iver.cit.gvsig.project.documents.layout.tools.listener.LayoutMoveListener;
import com.iver.cit.gvsig.project.documents.layout.tools.listener.LayoutToolListener;


/**
 * Behaviour que espera un listener de tipo MoveListener.
 *
 * @author Vicente Caballero Navarro
 */
public class LayoutViewZoomBehavior extends LayoutBehavior {
	private LayoutMoveListener listener;
	private boolean dragged=false;
	/**
	 * Crea un nuevo MoveBehavior.
	 *
	 * @param pli listener.
	 */
	public LayoutViewZoomBehavior(LayoutMoveListener lpl) {
		listener = lpl;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {

		VolatileImage image = createVolatileImage();
		Graphics gh = image.createGraphics();
		gh.setColor(getLayoutControl().getBackground());
		gh.fillRect(0, 0, image.getWidth(), image.getHeight());

		getLayoutControl().getLayoutDraw().drawRectangle((Graphics2D) gh);
		gh.drawImage(getLayoutControl().getImgRuler(),0,0,null);
		gh.drawImage(getLayoutControl().getImage(),0,0,null);
		getLayoutControl().getLayoutDraw().drawGrid((Graphics2D) gh);
		gh.setColor(Color.black);
		gh.setXORMode(Color.white);

		Rectangle r = new Rectangle();

		// Dibujamos el actual
		if (dragged && (getLayoutControl().getFirstPoint() != null) && (getLayoutControl().getLastPoint() != null)) {
			r.setFrameFromDiagonal(getLayoutControl().getFirstPoint(), getLayoutControl().getLastPoint());
			gh.drawRect(r.x, r.y, r.width, r.height);
		}
		 IFFrame[] frames = getLayoutControl().getLayoutContext().getFFrameSelected();
	        for (int i = 0; i < frames.length; i++) {
	            gh.setColor(Color.black);
	            frames[i].drawHandlers((Graphics2D) gh);
	        }

//		long t2 = System.currentTimeMillis();
		gh.setPaintMode();
		getLayoutControl().getLayoutDraw().drawRuler((Graphics2D) gh, Color.black);
		g.drawImage(image,0,0,null);
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
	 * @param e MouseEvent
	 *
	 * @throws BehaviorException Excepción lanzada cuando el Behavior.
	 */
	public void mouseReleased(MouseEvent e) throws BehaviorException {
		super.mouseReleased(e);
		PointEvent event = new PointEvent(e.getPoint(), e);
		listener.release(event);
		dragged=false;
	}

	/**
	 * Reimplementación del método mouseDragged de Behavior.
	 *
	 * @param e MouseEvent
	 * @throws BehaviorException
	 */
	public void mouseDragged(MouseEvent e) throws BehaviorException {
		super.mouseDragged(e);
		PointEvent event = new PointEvent(e.getPoint(), e);
		listener.drag(event);
		dragged=true;
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
