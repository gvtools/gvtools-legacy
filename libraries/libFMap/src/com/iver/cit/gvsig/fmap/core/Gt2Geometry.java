/*
 * Created on 20-sep-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.fmap.core;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.cresques.cts.ICoordTrans;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.gt2.FLiteShape;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Geometry;


    /**
     * Geometría.
     *
     * @author FJP
     */
    public class Gt2Geometry extends AbstractGeometry {
        /**
		 *
		 */
		private static final long serialVersionUID = 1587400440363673610L;
		private FLiteShape shp;



        /**
         * Crea un nuevo FGeometry.
         *
         * @param shp DOCUMENT ME!
         */
        Gt2Geometry(FLiteShape shp) {
            this.shp = shp;
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#draw(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, org.geotools.renderer.style.Style2D)
         */
//        public void draw(Graphics2D g, ViewPort vp, ISymbol symbol) {
//            // OJO CON LA PRECISION. DEBERIAMOS USAR: ((GeneralPathX) shp.m_Polyline).transform(mT);
//            // O HACER UNA FUNCION DE TRANSFORMACIÓN QUE USE LOS DOUBLES DEL
//            // SHAPE
//            // Shape shpTrans = vp.getAffineTransform().createTransformedShape(shp);
//            transform(vp.getAffineTransform());
//
//            switch (shp.getShapeType()) {
//                case FShape.POINT:
//                    shpPainter.paint(g, shp, symbol.getPointStyle2D(), 0);
//
//                    break;
//
//                case FShape.LINE:
//                    shpPainter.paint(g, shp, symbol.getLineStyle2D(), 0);
//
//                    break;
//
//                case FShape.POLYGON:
//                    shpPainter.paint(g, shp, symbol.getPolygonStyle2D(), 0);
//
//                    break;
//
//                case FShape.TEXT:
//                    shpPainter.paint(g, shp, symbol.getTextStyle2D(), 0);
//            }
//        }

        /**
         * Dibuja la geometria actual en el graphics que se le pasa como parámetro,
         * aplicandole las características del símbolo.
         *
         * @param g Graphics2D.
         * @param vp ViewPort.
         * @param symbol Símbolo.
         */
        public void draw(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel) {
            // OJO CON LA PRECISION. DEBERIAMOS USAR: ((GeneralPathX) shp.m_Polyline).transform(mT);
            // O HACER UNA FUNCION DE TRANSFORMACIÓN QUE USE LOS DOUBLES DEL
            // SHAPE
            // Shape shpTrans = vp.getAffineTransform().createTransformedShape(shp);
            transform(vp.getAffineTransform());
            symbol.draw(g, vp.getAffineTransform(), shp, cancel);
            // FGraphicUtilities.DrawShape(g, vp.getAffineTransform(), shp, symbol);
        }

        /**
         * Aplica la transformación a la geometría de la matriz de transformación
         * que se pasa como parámetro.
         *
         * @param at Matriz de transformación.
         */
        public void transform(AffineTransform at) {
            shp.transform(at);
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#intersects(java.awt.geom.Rectangle2D)
         */
        public boolean intersects(Rectangle2D r) {
            return shp.intersects(r);
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#getBounds2D()
         */
        public Rectangle2D getBounds2D() {
            return shp.getBounds2D();
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#toJTSGeometry()
         */
        public Geometry toJTSGeometry() {
            return shp.getGeometry();
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#createLabels(int, boolean)
         */
        public FLabel[] createLabels(int position, boolean duplicates) {
            FLabel[] aux = new FLabel[1];
            aux[0] = FLabel.createFLabel(shp);

            return aux;
        }

        /**
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
         */
        public int getGeometryType() {
            return shp.getShapeType();
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
         */
        public IGeometry cloneGeometry() {
            return new Gt2Geometry(new FLiteShape(shp.getGeometry()));
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#reProject(org.cresques.cts.ICoordTrans)
         */
        public void reProject(ICoordTrans ct) {
            shp.reProject(ct);
        }

        /**
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#getPathIterator(AffineTransform)
         */
        public PathIterator getPathIterator(AffineTransform at) {
            return (GeneralPathXIterator)shp.getPathIterator(null);
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#fastIntersects(double, double, double, double)
         */
        public boolean fastIntersects(double x, double y, double w, double h) {
            return shp.intersects(x,y,w,h);
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#drawInts(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.core.v02.FSymbol)
         */
        public void drawInts(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel) {
            draw(g, vp, symbol, cancel);
        }

        /* (non-Javadoc)
         * @see com.iver.cit.gvsig.fmap.core.IGeometry#toWKB()
         */
        public byte[] toWKB() throws IOException {
            // TODO Auto-generated method stub
            return null;
        }

		public Handler[] getHandlers(int type) {
			if (type == IGeometry.SELECTHANDLER)
				return shp.getSelectHandlers();
			return shp.getStretchingHandlers();

		}

		public PathIterator getPathIterator(AffineTransform at, double flatness) {
			return (GeneralPathXIterator)shp.getPathIterator(at, flatness);
		}

		public boolean contains(double x, double y) {
			return shp.contains(x,y);
		}

		public boolean contains(double x, double y, double w, double h) {
			return shp.contains(x,y,w,h);
		}

		public boolean intersects(double x, double y, double w, double h) {
			return shp.intersects(x,y,w,h);
		}

		public Rectangle getBounds() {
			return shp.getBounds();
		}

		public boolean contains(Point2D p) {
			return shp.contains(p);
		}

		public boolean contains(Rectangle2D r) {
			return shp.contains(r);
		}

		public Shape getInternalShape() {
			return shp;
		}

		public void drawInts(Graphics2D graphics2D, ViewPort viewPort,
				double dpi, CartographicSupport cartographicSymbol,
				Cancellable cancel) {
			// TODO Auto-generated method stub
			throw new Error("Not yet implemented!");
			
		}

}


