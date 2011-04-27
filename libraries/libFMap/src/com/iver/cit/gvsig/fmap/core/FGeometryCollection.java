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
package com.iver.cit.gvsig.fmap.core;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import org.cresques.cts.ICoordTrans;
import org.geotools.data.postgis.attributeio.WKBEncoder;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * Colección de Geometrías.
 *
 * @author Vicente Caballero Navarro
 */
public class FGeometryCollection extends AbstractGeometry {
	private ArrayList geometries = new ArrayList();

	/**
	 * Crea un nuevo FGeometryCollection.
	 *
	 * @param geoms vector de geometrías.
	 */
	public FGeometryCollection(IGeometry[] geoms) {
		for (int i = 0; i < geoms.length; i++) {
			geometries.add(geoms[i]);
		}
	}

	public void addGeometry(IGeometry g){
		geometries.add(g);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#draw(java.awt.Graphics2D,
	 * 		com.iver.cit.gvsig.fmap.ViewPort, ISymbol)
	 */
	public void draw(Graphics2D g, ViewPort vp, ISymbol symbol) {
		for (int i = 0; i < geometries.size(); i++)
			((IGeometry)geometries.get(i)).draw(g, vp, symbol);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#createLabels(int, boolean)
	 */
	public FLabel[] createLabels(int position, boolean duplicates) {
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#intersects(java.awt.geom.Rectangle2D)
	 */
/*	public boolean intersects(Rectangle2D r, double flatness) {
	    boolean resul = false;
		for (int i = 0; i < geometries.size(); i++)
		{
			resul = ((IGeometry)geometries.get(i)).intersects(r, flatness);
			if (resul) break;
		}

		return resul;
	}
*/
	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
	    boolean resul = false;
		for (int i = 0; i < geometries.size(); i++)
		{
			resul = ((IGeometry)geometries.get(i)).intersects(r);
			if (resul) break;
		}

		return resul;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getBounds2D()
	 */
	public Rectangle2D getBounds2D() {
		Rectangle2D rAux = null;

		for (int i = 0; i < geometries.size(); i++)
			if (rAux==null){
				rAux=((IGeometry)geometries.get(i)).getBounds2D();
			}else{
				rAux.add(((IGeometry)geometries.get(i)).getBounds2D());
			}
		return rAux;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getGeometryType()
	 */
	public int getGeometryType() {
		/*int ret = 0;

		for (int i = 0; i < geometries.length; i++) {
			ret = ret | geometries[i].getGeometryType();
		}

		return ret;
		*/
		return FShape.MULTI;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#draw(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.core.v02.FSymbol)
	 */
	public void draw(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel) {
		for (int i = 0; i < geometries.size(); i++)
			((IGeometry)geometries.get(i)).draw(g, vp, symbol, cancel);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#cloneGeometry()
	 */
	public IGeometry cloneGeometry() {
		IGeometry[] newGeometries = new IGeometry[geometries.size()];

		for (int i = 0; i < geometries.size(); i++)
			newGeometries[i] = ((IGeometry)geometries.get(i)).cloneGeometry();

		return new FGeometryCollection(newGeometries);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#toJTSGeometry()
	 */
	public Geometry toJTSGeometry() {
        Geometry[] theGeoms = new Geometry[geometries.size()];
        for (int i = 0; i < geometries.size(); i++)
        {
            theGeoms[i] = ((IGeometry)geometries.get(i)).toJTSGeometry();
        }
        GeometryCollection geomCol = new GeometryFactory().createGeometryCollection(theGeoms);


		return geomCol;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#reProject(org.cresques.cts.ICoordTrans)
	 */
	public void reProject(ICoordTrans ct) {
		for (int i = 0; i < geometries.size(); i++)
			((IGeometry)geometries.get(i)).reProject(ct);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getPathIterator(AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		// Necesitamos convertir todo esto a una GeneralPathX, tarde o temprano.
		// Así que lo intento aquí por primera vez.
		// Lo necesitamos para la edición, porque se están
		// añadiendo las geometrías como FGeometryCollection
		// para que el explode sea sencillo. No lo veo muy
		// claro eso, pero bueno.
		/* GeneralPathX gp = new GeneralPathX();
		double[] coords = new double[6];
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			GeneralPathXIterator pi = gAux.getGeneralPathXIterator();
			// Si el primer punto y el ultimo son iguales, conectamos
			// la geometría.
			boolean bFirst = true;
			double[] firstCoord = new double[6];
			while (!pi.isDone())
			{
				int type = pi.currentSegment(coords);
				switch (type)
				{
					case GeneralPathXIterator.SEG_MOVETO:
						if ((!bFirst) || (firstCoord != coords))
							gp.moveTo(coords[0], coords[1]);
						break;
			    	case SEG_LINETO:
			    		lineTo(coords[0], coords[1]);
			    		break;
				    case SEG_QUADTO:
				    	quadTo(coords[0], coords[1],
					       coords[2], coords[3]);
				    	break;
				    case SEG_CUBICTO:
				    	// Not implemented
				    	System.err.println("ERROR. TRAMO CUBICO. SIN IMPLEMENTAR TODAVÍA");
						curveTo(coords[0], coords[1],
							coords[2], coords[3],
							coords[4], coords[5]);
						break;
					    case SEG_CLOSE:
					    	closePath();
					    	break;
				}
				pi.next();
			}
		}		*/
		GeneralPathX gp = new GeneralPathX();
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			gp.append(gAux.getPathIterator(null), true);
		}
		return (GeneralPathXIterator) gp.getPathIterator(null);
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.IGeometry#fastIntersects(double, double, double, double)
     */
    public boolean fastIntersects(double x, double y, double w, double h) {
	    boolean resul = false;
		for (int i = 0; i < geometries.size(); i++)
		{
			resul = ((IGeometry)geometries.get(i)).fastIntersects(x,y,w,h);
			if (resul) break;
		}
		return resul;
    }

    /**
     * @see com.iver.cit.gvsig.fmap.core.IGeometry#toWKB()
     */
    public byte[] toWKB() throws IOException {
        return WKBEncoder.encodeGeometry(toJTSGeometry());
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.core.IGeometry#drawInts(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.core.v02.FSymbol)
     */
    public void drawInts(Graphics2D g, ViewPort vp, ISymbol symbol, Cancellable cancel) {
        for (int i = 0; i < geometries.size(); i++)
            ((IGeometry)geometries.get(i)).drawInts(g, vp, symbol, null);

    }

	/**
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getShapes()
	 */
	/*public FShape[] getShapes() {
		ArrayList shapes=new ArrayList();
		for (int i= 0;i<geometries.size();i++){
			FShape[] s=((IGeometry)geometries.get(i)).getShapes();
			for (int j=0;j<s.length;j++){
				shapes.add(s[j]);
			}
		}
		return (FShape[])shapes.toArray(new FShape[0]);
	}*/
	public IGeometry[] getGeometries(){
		return (IGeometry[])geometries.toArray(new IGeometry[0]).clone();
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.core.IGeometry#getHandlers(int)
	 */
	public Handler[] getHandlers(int type) {
		ArrayList handlers=new ArrayList();
		for (int i = 0; i < geometries.size(); i++){
			Handler[] handAux=((IGeometry)geometries.get(i)).getHandlers(type);
			for (int j=0;j<handAux.length;j++){
				handlers.add(handAux[j]);
			}
		}
		return (Handler[])handlers.toArray(new Handler[0]);
	}


	public void transform(AffineTransform at) {
		for (int i = 0; i < geometries.size(); i++){
			((IGeometry)geometries.get(i)).transform(at);
		}
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		GeneralPathX gp = new GeneralPathX();
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			gp.append(gAux.getPathIterator(null), true);
		}
		return gp.getPathIterator(at, flatness);
	}

	public boolean contains(double x, double y) {
		boolean bRes;
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			bRes = gAux.contains(x,y);
			if (bRes) return bRes;
		}

		return false;
	}

	public boolean contains(double x, double y, double w, double h) {
		boolean bRes;
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			bRes = gAux.contains(x,y, w, h);
			if (bRes) return bRes;
		}

		return false;
	}

	public boolean intersects(double x, double y, double w, double h) {
		boolean bRes;
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			bRes = gAux.intersects(x,y, w, h);
			if (bRes) return bRes;
		}

		return false;
	}

	public Rectangle getBounds() {
		Rectangle rAux = null;

		for (int i = 0; i < geometries.size(); i++)
			if (rAux==null){
				rAux=((IGeometry)geometries.get(i)).getBounds();
			}else{
				rAux.add(((IGeometry)geometries.get(i)).getBounds());
			}
		return rAux;
	}

	public boolean contains(Point2D p) {
		boolean bRes;
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			bRes = gAux.contains(p);
			if (bRes) return bRes;
		}

		return false;
	}

	public boolean contains(Rectangle2D r) {
		boolean bRes;
		for (int i=0; i < geometries.size(); i++)
		{
			IGeometry gAux = (IGeometry) geometries.get(i);
			bRes = gAux.contains(r);
			if (bRes) return bRes;
		}

		return false;
	}

	public Shape getInternalShape() {
		return this;
	}

	public void drawInts(Graphics2D graphics2D, ViewPort viewPort, double dpi,
			CartographicSupport cartographicSymbol, Cancellable cancel) {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented!");

	}
}
