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
package org.gvsig.fmap.tools.behavior;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.ShapePointExtractor;
import org.gvsig.fmap.tools.listeners.VectorListener;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Events.MoveEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener;
import com.iver.cit.gvsig.project.documents.view.snapping.SnappingVisitor;
import com.iver.cit.gvsig.project.documents.view.snapping.snappers.NearestPointSnapper;

/**
 * Behavior to digitize two points vectors in mapcontrol
 * @author azabala
 */
public class VectorBehavior extends Behavior {

	NearestPointSnapper snapper = new NearestPointSnapper();
	PointSnapper pointSnapper = new PointSnapper();
	
	FLyrVect snappingLyr = null;
	
	
	private boolean isZooming = false;
	private Point2D m_FirstPoint;
	private Point2D m_LastPoint;
	private VectorListener listener;
	
	protected int lenghtArrow = 15;
	protected int widthArrow = 10;
	protected Color arrowColor = java.awt.Color.RED;
	protected int rgb;
	private Stroke stroke;
	protected static BufferedImage img = new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_ARGB);
	protected static Rectangle rect = new Rectangle(0, 0, 1, 1);

	
	class PointSnapper extends NearestPointSnapper {

		public Point2D getSnapPoint(Point2D queryPoint, IGeometry geomToSnap,
				double tolerance, Point2D lastPointEntered) {
			
			Point2D solution = null;
			double minDistance = tolerance;
			
			List<Point2D[]> pointsParts =
				ShapePointExtractor.extractPoints(geomToSnap);
			for(int i = 0; i < pointsParts.size(); i++){
				Point2D[] points = pointsParts.get(i);
				for(int j = 0; j < points.length; j++){
					Point2D point = points[j];
					double dist = point.distance(queryPoint);
					if(dist <= minDistance){
						solution = point;
						minDistance = dist;
					}//if
				}//for j
			}//for i
			return solution;
		}

		public String getToolTipText() {
			return PluginServices.getText(this, "nearest_point_for_point_layers");
		}
		
	}

	
	/**
	 * Crea un nuevo RectangleBehavior.
	 *
	 * @param zili listener.
	 */
	public VectorBehavior(VectorListener zili, FLyrVect snappingLyr) {
		listener = zili;
		this.snappingLyr = snappingLyr;
		Graphics2D g2 = img.createGraphics();
		drawInsideRectangle(g2, g2.getTransform(), rect);
		rgb = img.getRGB(0, 0);

	}

	private void drawInsideRectangle(Graphics2D g, AffineTransform scale,
			Rectangle r) {
		FShape shp;
		AffineTransform mT = new AffineTransform();
		mT.setToIdentity();

		Rectangle rect = mT.createTransformedShape(r).getBounds();
		GeneralPathX line = new GeneralPathX();
		
		
		
		line.moveTo(rect.x, rect.y + (rect.height / 2));
		line.curveTo(rect.x + (rect.width / 3),
			rect.y + (2 * rect.height),
			rect.x + ((2 * rect.width) / 3), rect.y - rect.height,
			rect.x + rect.width, rect.y + (rect.height / 2));

		shp = new FPolyline2D(line);
		drawLineWithArrow(g, mT, shp);

		
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		BufferedImage img = getMapControl().getImage();
		g.drawImage(img, 0, 0, null);
		g.setColor(Color.black);
		g.setXORMode(Color.white);

		

		// Dibujamos el actual
		if ((m_FirstPoint != null) && (m_LastPoint != null) && !isZooming) {
			GeneralPathX line = new GeneralPathX();			
			line.moveTo(m_FirstPoint.getX(), m_FirstPoint.getY());
			line.lineTo(m_LastPoint.getX(), m_LastPoint.getY());
			FPolyline2D shp = new FPolyline2D(line);
			drawLineWithArrow((Graphics2D)g, getMapControl().getViewPort().getAffineTransform(), shp);
		}
		g.setPaintMode();
	}

	/**
	 * Reimplementación del método mousePressed de Behavior.
	 *
	 * @param e MouseEvent
	 */
	public void mousePressed(MouseEvent e) {

			int modifiers = e.getModifiersEx();
			int ctrlDownMask = modifiers & MouseEvent.CTRL_DOWN_MASK;
			if(ctrlDownMask == MouseEvent.CTRL_DOWN_MASK ){
				isZooming = true;
			}
			m_FirstPoint = e.getPoint();
			getMapControl().repaint();

			if (listener.cancelDrawing()) {
				getMapControl().cancelDrawing();
				getMapControl().repaint();
			}
	}
	
	
	
		
	

	/**
	 * Reimplementación del método mouseReleased de Behavior.
	 *
	 * @param e MouseEvent
	 *
	 * @throws BehaviorException Excepción lanzada cuando el Behavior.
	 */
	public void mouseReleased(MouseEvent e) throws BehaviorException {
	    if (m_FirstPoint == null) {
	    	isZooming = false;
	    	return;
	    }
		Point2D p1;
		Point2D p2;
		Point pScreen = e.getPoint();

		ViewPort vp = getMapControl().getMapContext().getViewPort();
		p1 = vp.toMapPoint(m_FirstPoint);
		
		
		//we snap to the nearest point of the adjusting layer
		int pixelTolerance = 8;
		double mapTolerance = vp.toMapDistance(pixelTolerance);
		
		double minDist = mapTolerance;
		Rectangle2D r = new Rectangle2D.Double(p1.getX() - mapTolerance / 2,
				p1.getY() - mapTolerance / 2, mapTolerance, mapTolerance);

		if (snappingLyr.isVisible()){
			try {
				IFeatureIterator iterator = snappingLyr.getSource().getFeatureIterator(r, null, null, false);
				SnappingVisitor snapVisitor = 
					new SnappingVisitor(snapper, p1, mapTolerance, null);
				SnappingVisitor snapVisitor2 = 
					new SnappingVisitor(pointSnapper, p1, mapTolerance, null);
				
				while(iterator.hasNext()){
					IFeature feature = iterator.next();
					IGeometry geo = feature.getGeometry();
					snapVisitor.visitItem(geo);
					snapVisitor2.visitItem(geo);
				}
				Point2D theSnappedPoint = snapVisitor.getSnapPoint();
				if(theSnappedPoint == null)
					theSnappedPoint = snapVisitor2.getSnapPoint();
				
				if (theSnappedPoint != null) {
					p1.setLocation(theSnappedPoint);
				}
			} catch (ReadDriverException e1) {
				e1.printStackTrace();
				throw new BehaviorException("Error de driver intentando aplicar snap", e1);
			}
		}//isVisible
				
				
		p2 = vp.toMapPoint(pScreen);
//		Rectangle2D.Double rectangle = new Rectangle2D.Double();
//		rectangle.setFrameFromDiagonal(p1, p2);

//		MoveEvent event = new MoveEvent(m_FirstPoint, e.getPoint(), e);
		MoveEvent event = new MoveEvent(p1, p2, e);
		listener.vector(event);


		m_FirstPoint = null;
		m_LastPoint = null;
		isZooming = false;
	}

	/**
	 * Reimplementación del método mouseDragged de Behavior.
	 *
	 * @param e MouseEvent
	 */
	public void mouseDragged(MouseEvent e) {
		m_LastPoint = e.getPoint();
		getMapControl().repaint();
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#setListener(com.iver.cit.gvsig.fmap.tools.ToolListener)
	 */
	public void setListener(ToolListener listener) {
		this.listener = (VectorListener) listener;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.tools.Behavior.Behavior#getListener()
	 */
	public ToolListener getListener() {
		return listener;
	}
	
	
	private void drawLineWithArrow(Graphics2D g, AffineTransform affineTransform, FShape shp){
		FGeometryUtil.drawLineWithArrow(g, affineTransform, shp, arrowColor, stroke, lenghtArrow, widthArrow);

	}

	public FLyrVect getSnappingLyr() {
		return snappingLyr;
	}

	public void setSnappingLyr(FLyrVect snappingLyr) {
		this.snappingLyr = snappingLyr;
	}

}

