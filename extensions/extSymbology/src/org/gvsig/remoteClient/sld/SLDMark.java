/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package org.gvsig.remoteClient.sld;

import org.gvsig.remoteClient.sld.filterEncoding.FExpression;

/**
 * Implements the Mark element of an SLD implementation specification.<p>
 * The Mark element of a Graphic defines a �shape� which has coloring applied to it.<p>
 * The WellKnownName element gives the well-known name of the shape of the mark.
 * Allowed values include at least �square�, �circle�, �triangle�, �star�, �cross�,
 * and �x�, though map servers may draw a different symbol instead if they don't
 * have a shape for all of these. The default WellKnownName is �square�. Renderings 
 * of these marks may be made solid or hollow depending on Fill and Stroke elements.
 * <p> 
 * The Mark element serves two purposes. It allows the selection of simple shapes,
 * and, in combination with the capability to select and mix multiple external-URL 
 * graphics and marks, it allows a style to be specified that can produce a usable 
 * result in a best-effort rendering environment, provided that a simple Mark is 
 * included at the bottom of the list of sources for every Graphic.<p>
 * 
 * @see SLDFill
 * @see SLDStroke
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDMark implements ISLDFeatures {

	protected SLDFill fill;
	protected SLDStroke stroke;
	protected FExpression wellKnownName = new FExpression();
	
	public SLDFill getFill() {
		return fill;
	}
	public void setFill(SLDFill fill) {
		this.fill = fill;
	}
	public SLDStroke getStroke() {
		return stroke;
	}
	public void setStroke(SLDStroke stroke) {
		this.stroke = stroke;
	}
	public FExpression getWellKnownName() {
		return wellKnownName;
	}
	public void setWellKnownName(FExpression wellKnownName) {
		this.wellKnownName = wellKnownName;
	}

	
}
