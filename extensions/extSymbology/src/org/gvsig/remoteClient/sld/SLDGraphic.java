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

import java.util.ArrayList;

import org.gvsig.remoteClient.sld.filterEncoding.FExpression;
/**
 * Implements the Graphic element of an SLD implementation specification.<p>
 * A Graphic is a �graphic symbol� with an inherent shape, color(s), and possibly size. A
 * �graphic� can be very informally defined as �a little picture� and can be of either a raster
 * or vector-graphic source type. The term �graphic� is used since the term �symbol� is
 * similar to �symbolizer� which is used in a different context in SLD.<p>
 * If the Graphic element is omitted from the parent element, then nothing will be plotted.<p>
 * Graphics can either be referenced from an external URL in a common format (such as
 * GIF or SVG) or may be derived from a Mark. Multiple external URLs and marks may be
 * referenced with the semantic that they all provide the equivalent graphic in different
 * formats. The �hot spot� to use for positioning the rendering at a point must either be
 * inherent in the external format or is defined to be the �central point� of the graphic,
 * where the exact definition �central point� is system-dependent.<p>
 * The default if neither an ExternalGraphic nor a Mark is specified is to use the default
 * mark of a �square� with a 50%-gray fill and a black outline, with a size of 6 pixels,
 * 
 * @see SLDExternalGraphic
 * @see SLDMark
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDGraphic implements ISLDFeatures {
	
	protected ArrayList<SLDExternalGraphic> externalGraphics = new ArrayList<SLDExternalGraphic>();
	protected ArrayList<SLDMark> marks = new ArrayList<SLDMark>();
	protected FExpression expressionOpacity = new FExpression();
	protected FExpression expressionSize = new FExpression();
	protected FExpression expressionRotation = new FExpression();
	
	
	public ArrayList<SLDExternalGraphic> getExternalGraphics() {
		return externalGraphics;
	}
	public void setExternalGraphics(ArrayList<SLDExternalGraphic> externalGraphics) {
		this.externalGraphics = externalGraphics;
	}
	public void addExternalGraphic(SLDExternalGraphic external) {
		this.externalGraphics.add(external);
	}
	
	public ArrayList<SLDMark> getMarks() {
		return marks;
	}
	public void setMarks(ArrayList<SLDMark> marks) {
		this.marks = marks;
	}
	
	public void addMark(SLDMark mark) {
		this.marks.add(mark);
	}
	public FExpression getExpressionOpacity() {
		return expressionOpacity;
	}
	public void setExpressionOpacity(FExpression expressionOpacity) {
		this.expressionOpacity = expressionOpacity;
	}
	public FExpression getExpressionSize() {
		return expressionSize;
	}
	public void setExpressionSize(FExpression expressionSize) {
		this.expressionSize = expressionSize;
	}
	public FExpression getExpressionRotation() {
		return expressionRotation;
	}
	public void setExpressionRotation(FExpression expressionRotation) {
		this.expressionRotation = expressionRotation;
	}

	
	public float getGraphicSize(){
		return Float.valueOf(this.expressionSize.getLiteral());
	}
	public float getGraphicOpacity(){
		return Float.valueOf(expressionOpacity.getLiteral());
	}
	public float getGraphicRotation(){
		return Float.valueOf(expressionRotation.getLiteral());
	}

}
