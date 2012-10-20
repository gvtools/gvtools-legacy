/*
 * Created on 31-mar-2005
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

import com.hardcode.gdbms.engine.values.Value;

/**
 * @author FJP TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultFeature extends DefaultRow implements IFeature {
	private IGeometry theGeom;

	/**
	 * @deprecated Dont use it, please. Instead, use DefaultFeature(IGeometry,
	 *             Value[], String ID). You shoud know the correct ID of a
	 *             feature.
	 * @param geom
	 * @param att
	 */
	public DefaultFeature(IGeometry geom, Value[] att) {
		super(att);
		this.theGeom = geom;

	}

	public DefaultFeature(IGeometry geom, Value[] att, String id) {
		super(att, id);
		this.theGeom = geom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.core.IFeature#getGeometry()
	 */
	public IGeometry getGeometry() {
		return theGeom;
	}

	public IRow cloneRow() {
		IGeometry geom = null;
		if (theGeom != null)
			geom = theGeom.cloneGeometry();
		Value[] attri = null;
		if (getAttributes() != null)
			attri = (Value[]) getAttributes().clone();
		DefaultFeature df = new DefaultFeature(geom, attri, getID());
		return df;
	}

	public void setGeometry(IGeometry g) {
		theGeom = g;

	}

}
