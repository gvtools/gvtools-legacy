///*
// * Created on 16-dic-2004
// *
// * TODO To change the template for this generated file go to
// * Window - Preferences - Java - Code Generation - Code and Comments
// */
///* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
// *
// * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
// *
// * For more information, contact:
// *
// *  Generalitat Valenciana
// *   Conselleria d'Infraestructures i Transport
// *   Av. Blasco Ib��ez, 50
// *   46010 VALENCIA
// *   SPAIN
// *
// *      +34 963862235
// *   gvsig@gva.es
// *      www.gvsig.gva.es
// *
// *    or
// *
// *   IVER T.I. S.A
// *   Salamanca 50
// *   46005 Valencia
// *   Spain
// *
// *   +34 963163400
// *   dac@iver.es
// */
//package com.iver.cit.gvsig.fmap.layers;
//
//import java.awt.Graphics2D;
//import java.awt.Rectangle;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//
//import javax.print.attribute.PrintRequestAttributeSet;
//
//import org.geotools.data.FeatureSource;
//import org.geotools.map.DefaultMapContext;
//import org.geotools.map.MapContext;
//import org.geotools.map.MapLayer;
//import org.geotools.renderer.lite.LiteRenderer2;
//
//import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
//import com.iver.cit.gvsig.fmap.ViewPort;
//import com.iver.utiles.swing.threads.Cancellable;
//import com.vividsolutions.jts.geom.Envelope;
//
///**
// * @author FJP
// *
// * TODO To change the template for this generated type comment go to
// * Window - Preferences - Java - Code Generation - Code and Comments
// */
//// TODO: Cuando no sea para pruebas, habr� que ponerla privada
//public class FLyrGT2_old extends FLyrDefault {
//
//	private MapLayer m_lyrGT2;
//	private LiteRenderer2 liteR2;
//	private MapContext mapContextGT2;
//
//	public FLyrGT2_old(MapLayer lyrGT2)
//	{
//		m_lyrGT2 = lyrGT2;
//		setName(lyrGT2.getTitle());
//		mapContextGT2 = new DefaultMapContext();
//		mapContextGT2.addLayer(lyrGT2);
//		liteR2 = new LiteRenderer2(mapContextGT2);
//	}
//
//	/* (non-Javadoc)
//	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#getFullExtent()
//	 */
//	public Rectangle2D getFullExtent() {
//		FeatureSource fs = m_lyrGT2.getFeatureSource();
//		Envelope bounds = null;
//		try {
//			bounds = fs.getBounds();
//			if (bounds == null)
//			{
//				bounds = fs.getFeatures().getBounds();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new Rectangle2D.Double(bounds.getMinX(), bounds.getMinY(),
//					bounds.getWidth(), bounds.getHeight());
//	}
//
//	/* (non-Javadoc)
//	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#draw(java.awt.image.BufferedImage, java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.operations.Cancellable)
//	 */
//	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort, Cancellable cancel,double scale) throws ReadDriverException {
//        try {
//        	 if (isWithinScale(scale)){
//        	// mapExtent = this.context.getAreaOfInterest();
//        	m_lyrGT2.setVisible(isVisible());
//        	Envelope envelope = new Envelope(viewPort.getExtent().getMinX(),
//        			viewPort.getExtent().getMinY(), viewPort.getExtent().getMaxX(),
//        			viewPort.getExtent().getMaxY());
//        	mapContextGT2.setAreaOfInterest(envelope);
//            /* FeatureResults results = queryLayer(m_lyrGT2, envelope,
//                    destinationCrs);
//
//            // extract the feature type stylers from the style object
//            // and process them
//            processStylers(g, results,
//            		m_lyrGT2.getStyle().getFeatureTypeStyles(), viewPort.getAffineTransform(),
//                mapContextGT2.getCoordinateReferenceSystem()); */
//        	Rectangle r = new Rectangle(viewPort.getImageSize());
//        	long t1 = System.currentTimeMillis();
//        	liteR2.paint(g,r, viewPort.getAffineTransform());
//        	long t2 = System.currentTimeMillis();
//        	System.out.println("Tiempo en pintar capa " + getName() + " de GT2:" + (t2- t1) + " milisegundos");
//        	 }
//        	 } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//
//
//	}
//
//	/**
//	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#print(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.utiles.swing.threads.Cancellable)
//	 */
//	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel,double scale, PrintRequestAttributeSet properties) throws ReadDriverException {
//	}
//
//}
