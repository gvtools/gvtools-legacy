/*
 * Created on 30-ago-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, 
USA.
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
package com.hardcode.gdbms.engine.spatial.fmap;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.geotools.renderer.style.Style2D;

import com.hardcode.gdbms.engine.spatial.Geometry;
import com.hardcode.gdbms.engine.spatial.Renderer;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;


public class FMapGeometry implements Geometry {
	
	private GeneralPathX gpx = new GeneralPathX();

	public FMapGeometry(IGeometry g) {
		gpx.append(g.getPathIterator(null), true);
	}
	
	/**
	 * @see com.hardcode.gdbms.engine.spatial.Geometry#draw(java.awt.Graphics2D, org.geotools.renderer.style.Style2D)
	 */
	public void draw(Graphics2D g, Style2D style) {
        Renderer.drawShape(g, gpx, style);
	}

	/**
	 * @see com.hardcode.gdbms.engine.spatial.Geometry#transform(java.awt.geom.AffineTransform)
	 */
	public void transform(AffineTransform mt) {
		gpx.transform(mt);
	}

	/**
	 * @see com.hardcode.gdbms.engine.spatial.Geometry#intersects(java.awt.geom.Rectangle2D)
	 */
	public boolean intersects(Rectangle2D r) {
		return gpx.intersects(r);
	}

}
