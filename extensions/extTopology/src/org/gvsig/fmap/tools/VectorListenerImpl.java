/*
 * Created on 10-abr-2006
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
* $Id: 
* $Log: 
*/
package org.gvsig.fmap.tools;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.gvsig.fmap.tools.listeners.VectorListener;
import org.gvsig.referencing.DisactivableMappedPosition;
import org.gvsig.referencing.MappedPositionContainer;
import org.gvsig.referencing.ReferencingUtil;
import org.opengis.geometry.DirectPosition;

import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.MoveEvent;

/**
 * Vector listener impl that creates vector errors to compute vectorial layers
 * transformations.
 * 
 * For each digitized vector error in screen, it creates a geotools MappedPosition
 * instance.
 * */
public class VectorListenerImpl implements VectorListener {

//	private final Image img = new ImageIcon(MapControl.class.
//												getResource("images/PointSelectCursor.gif")).
//												getImage();
	
//	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(img,
//													new Point(16, 16), "");
	
	private Cursor cur = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	/**
	 * MapControl creator of digitizing events.
	 */
	protected MapControl mapCtrl;
	
	/**
	 * Collection of existing digitized links, to add new vector error.
	 */
	MappedPositionContainer linksList;
	
	protected boolean isZooming = false;
	
	
	public VectorListenerImpl(MapControl mapCtrl, MappedPositionContainer linksList) {
		super();
		this.mapCtrl = mapCtrl;
		this.linksList = linksList;
	}
	
	public void vector(MoveEvent event) throws BehaviorException {
		ViewPort vp = mapCtrl.getMapContext().getViewPort();
		int modifiers = event.getEvent().getModifiers();
		int modifiersEx = event.getEvent().getModifiersEx();
		
		int ctrlDownMask = modifiersEx & MouseEvent.CTRL_DOWN_MASK;
		int button1Mask = modifiers & MouseEvent.BUTTON1_MASK;
		int button2Mask = modifiers & MouseEvent.BUTTON3_MASK;
	
		if(ctrlDownMask == MouseEvent.CTRL_DOWN_MASK && vp.getExtent()!=null){
			isZooming = true;
			Rectangle2D.Double r = new Rectangle2D.Double();
			Rectangle2D rect = vp.getExtent();
			double factor = 1;
			if(button1Mask == MouseEvent.BUTTON1_MASK){
				//zoom +
				factor = 1/1.5d;
			}else if(button2Mask == MouseEvent.BUTTON3_MASK){
				//zoom -
				factor = 1*1.5d;
			}else{
				System.err.println("Tecla ctrl pulsada, pero pulsacion de boton sin boton izquierdo o derecho");
				return;
			}
			
			double nuevoX = rect.getMaxX() -
				((vp.getExtent().getWidth() * factor) / 2.0);
			double nuevoY = rect.getMaxY() -
				((vp.getExtent().getHeight() * factor) / 2.0);
			r.x = nuevoX;
			r.y = nuevoY;
			r.width = vp.getExtent().getWidth() * factor;
			r.height = vp.getExtent().getHeight() * factor;
			vp.setExtent(r);
//			mapCtrl.getMapContext().clearAllCachingImageDrawnLayers();
			
		}else{
			isZooming = false;
//			Point2D from = vp.toMapPoint(event.getFrom());
//			Point2D to = vp.toMapPoint(event.getTo());
			
			Point2D from = event.getFrom();
			Point2D to = event.getTo();
			
			ReferencingUtil referencing = ReferencingUtil.getInstance();
			
			//TODO Ver como pasar a GeoAPI la proyeccion del mapControl (libJCRS)	
			//de momento estamos pasando null
			DirectPosition source = 
				referencing.create(new double[]{from.getX(), from.getY()}, null);
			
			DirectPosition destination = 
				referencing.create(new double[]{to.getX(), to.getY()}, null);
			
			DisactivableMappedPosition mappedPosition
				= new DisactivableMappedPosition(source, destination);
			
			this.linksList.addMappedPosition(mappedPosition);
		}
		
		

	}

	public boolean cancelDrawing() {
		return false;
	}

	public Cursor getCursor() {
		return cur;
	}
	
}
