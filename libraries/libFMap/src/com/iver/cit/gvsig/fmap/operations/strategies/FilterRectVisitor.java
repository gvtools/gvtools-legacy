/*
 * Created on 26-feb-2006
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
* $Id: FilterRectVisitor.java 30933 2009-09-23 06:13:48Z vcaballero $
* $Log$
* Revision 1.4  2007-03-06 17:08:55  caballero
* Exceptions
*
* Revision 1.3  2006/06/20 18:15:19  azabala
* añadida comprobación de geometria nula en la construcción del indice espacial
*
* Revision 1.2  2006/03/07 20:59:28  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/26 20:50:00  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.fmap.operations.strategies;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLayer;
/**
 * This FeatureVisitor is a spatial filter.
 * It wraps a FeatureVisitor, and only calls its visit
 * method if visited geometries are within specified rectangle.
 * @author azabala
 *
 */
public class FilterRectVisitor implements FeatureVisitor {
	private Rectangle2D rect;
	private FeatureVisitor wrappedVisitor;

	public void setRectangle(Rectangle2D rect){
		this.rect = rect;
	}
	public void visit(IGeometry g, int index) throws ReadDriverException, VisitorException, ProcessVisitorException {
		if (g != null) {
			if(g.intersects(rect)) {
				wrappedVisitor.visit(g, index);
			}
			 //When the rectangle is an absolutely vertical line 
			else if(rect.getWidth() == 0.0) {
				rect.setFrame(rect.getX()-0.000000005, rect.getY(), 0.00000001, rect.getHeight());
				if(g.intersects(rect)) {
					wrappedVisitor.visit(g, index);
				}
			}
			 //When the rectangle is an absolutely horizontal line
			else if(rect.getHeight() == 0.0) {
				rect.setFrame(rect.getX(), rect.getY()-0.000000005, rect.getWidth(), 0.00000001);
				if(g.intersects(rect)) {
					wrappedVisitor.visit(g, index);
				}
			}
		}
	}
	public void stop(FLayer layer) throws VisitorException {
		wrappedVisitor.stop(layer);
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		return wrappedVisitor.start(layer);
	}
	public FeatureVisitor getWrappedVisitor() {
		return wrappedVisitor;
	}
	public void setWrappedVisitor(FeatureVisitor wrappedVisitor) {
		this.wrappedVisitor = wrappedVisitor;
	}
	public String getProcessDescription() {
		return "Filters visit calls to a visitor by spatial criteria";
	}


}

