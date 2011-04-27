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
package org.gvsig.remoteClient.sld.symbolizers;

import org.gvsig.remoteClient.sld.AbstractSLDSymbolizer;
import org.gvsig.remoteClient.sld.SLDFill;
import org.gvsig.remoteClient.sld.SLDStroke;

import com.iver.cit.gvsig.fmap.core.FShape;
/**
 * Implements the PolygonSymbolizer element of an SLD implementation specification.<p>
 * 
 * PolygonSymbolizer is used draw a polygon (or other area-type geometries),
 * including filling its interior and stroking its border (outline).<p>
 * 
 * @see SLDStroke
 * @see SLDFill
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDPolygonSymbolizer extends AbstractSLDSymbolizer implements ISLDSymbolizer{

	protected SLDFill fill;
	protected SLDStroke stroke;
	
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
	
	public int getShapeType() {return FShape.POLYGON;}

	
}
