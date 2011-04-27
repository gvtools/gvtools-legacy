package org.gvsig.fmap.geometries.iso;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.gvsig.fmap.geometries.iso.primitive.Box;
import org.gvsig.fmap.geometries.operation.GeometryOperation;
import org.gvsig.fmap.geometries.operation.GeometryOperationException;

import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;

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
/* CVS MESSAGES:
 *
 * $Id: AbstractGeometry.java,v 1.2 2008/03/25 08:47:41 cvs Exp $
 * $Log: AbstractGeometry.java,v $
 * Revision 1.2  2008/03/25 08:47:41  cvs
 * Visitors removed
 *
 * Revision 1.1  2008/03/12 08:46:21  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * This interface must to be implemented by all the geometries.
 * It contains methods that has been created because of they are
 * on the ISO 19107 and others that has been inherited because of 
 * the previous gvSIG versions. 
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 * @see http://www.iso.org/iso/iso_catalogue/catalogue_tc/catalogue_detail.htm?csnumber=32554
 * @see http://www.iso.org/iso/iso_catalogue/catalogue_tc/catalogue_detail.htm?csnumber=26012
 */
public interface AbstractGeometry {	
	
	/**
	 * This method returns the geometry identifier just as is
	 * described on the ISO 10136. 
	 * @return the geometry identifier
	 */
	public String getId();
	
	/**
	 * This method returns the geometry coordinates reference
	 * system just as is described on the ISO 10136. 
	 * @return the projection
	 */
	public IProjection getSRS();
	
	/**
	 * This method has been created because of the ISO 19107
	 * @return if the geometry is simple.
	 */
	public boolean isSimple();	
	
	/**
	 * This method has been created because of the ISO 19107
	 * @return the number of dimensions.
	 * 
	 */
	public int getCoordinateDimension();
	
	/**
	 * This method has been created because of the ISO 19107
     * @return the boundary that encloses the geometry
	 */
	public Box getBoundary();
	
	/**
	 * This method has been created because of the ISO 19107. It
	 * returns a cloned geometry in the new SRS
	 * @param newProjection
	 * The new projection
     * @return the boundary that encloses the geometry
	 */
	public AbstractGeometry transform(IProjection newProjection);
			
	/**
	 * Gets a concrete operation
	 * @param opeartionCode
	 * Operation code. See the GeometryOperation class to see the codes
	 * @return
	 * An operation or null
	 */
	public GeometryOperation getOperation(int opeartionCode);
			
	/**
	 * Executes the operation for a concrete geometry
	 * @param operationCode
	 * @return
	 * The operation result (if exists)
	 * @throws GeometryOperationException
	 */
	public Object doOperation(int opeartionCode) throws GeometryOperationException;
			
	/**
	 * It returns the geometry type, that can be one of the constants 
	 * defined in FShape: POINT, LINE, POLIGON
	 * @return The geometry type
	 */
	int getGeometryType();

	/**
	 * It clones the geometry
	 * @return The cloned geometry
	 */
	IGeometry cloneGeometry();

	/**
	 * Devuelve true si la geometría intersecta con el rectángulo que se pasa
	 * como parámetro.
	 *
	 * @param r Rectángulo.
	 *
	 * @return True, si intersecta.
	 * @deprecated
	 */	
	boolean intersects(Rectangle2D r);

	/**
	 * Se usa en las strategies de dibujo para comprobar de manera rápida
	 * si intersecta con el rectángulo visible
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 * @deprecated
	 */
	public boolean fastIntersects(double x, double y, double w, double h);

	/**
	 * Devuelve el Rectángulo que ocupa la geometría.
	 * @return Rectángulo.
	 * @deprecated Use getBoundary instead of this
	 */
	Rectangle2D getBounds2D();

	/**
	 * Reproyecta la geometría a partir del transformador de coordenadas.
	 *
	 * @param ct Coordinate Transformer.
	 * @deprecated
	 */
	void reProject(ICoordTrans ct);

	/**
	 * Devuelve el GeneralPathXIterator con la información relativa a la geometría.
	 * @param at TODO
	 *
	 * @return PathIterator.
	 * @deprecated
	 */
	PathIterator getPathIterator(AffineTransform at);

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @deprecated
	 */
    public byte[] toWKB() throws IOException;
   
    /**
	 * It returns the handlers of the geomety,
	 * these they can be of two types is straightening and of seleccion.
	 *
	 * @param type Type of handlers
	 *
	 * @return Handlers.
	 */
	public Handler[] getHandlers(int type);

	/**
	 * 
	 * @param at
	 * @deprecated
	 */
	public void transform(AffineTransform at);

	/**
	 * 
	 * @param at
	 * @param flatness
	 * @return
	 * @deprecated
	 */
	PathIterator getPathIterator(AffineTransform at, double flatness);

	/**
	 * Useful to have the real shape behind the scenes.
	 * May be uses to edit it knowing it it is a Circle, Ellipse, etc
	 * @return
	 * @deprecated
	 */
	Shape getInternalShape();	
}
