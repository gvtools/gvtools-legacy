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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;

import org.gvsig.remoteClient.sld.filterEncoding.FExpression;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;

/**
 * Implements the Stroke element of an SLD specification which encapsulates the
 * graphical-symbolization parameters for linear geometries
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDStroke implements ISLDFeatures {

	protected FExpression expressionWidth = new FExpression();
	protected FExpression expressionColor = new FExpression();
	protected FExpression expressionOpacity = new FExpression();
	protected FExpression expressionLineJoin = new FExpression();
	protected FExpression expressionLineCap = new FExpression();
	protected FExpression expressionDashOffset = new FExpression();
	protected SLDGraphic graphic;
	protected boolean hasGraphicFill = false;
	protected boolean hasGraphicStroke = false;
	protected ArrayList<Float> dashArray = new ArrayList<Float>();

	public FExpression getExpressionWidth() {
		return expressionWidth;
	}

	public void setExpressionWidth(FExpression expressionWidth) {
		this.expressionWidth = expressionWidth;
	}

	public FExpression getExpressionColor() {
		return expressionColor;
	}

	public void setExpressionColor(FExpression expressionColor) {
		this.expressionColor = expressionColor;
	}

	public FExpression getExpressionOpacity() {
		return expressionOpacity;
	}

	public void setExpressionOpacity(FExpression expressionOpacity) {
		this.expressionOpacity = expressionOpacity;
	}

	public FExpression getExpressionLineJoin() {
		return expressionLineJoin;
	}

	public void setExpressionLineJoin(FExpression expressionLineJoin) {
		this.expressionLineJoin = expressionLineJoin;
	}

	public FExpression getExpressionLineCap() {
		return expressionLineCap;
	}

	public void setExpressionLineCap(FExpression expressionLineCap) {
		this.expressionLineCap = expressionLineCap;
	}

	public FExpression getExpressionDashOffset() {
		return expressionDashOffset;
	}

	public void setExpressionDashOffset(FExpression expressionDashOffset) {
		this.expressionDashOffset = expressionDashOffset;
	}

	public SLDGraphic getGraphic() {
		return graphic;
	}

	public void setGraphic(SLDGraphic graphic) {
		this.graphic = graphic;
	}

	public boolean isHasGraphicFill() {
		return hasGraphicFill;
	}

	public void setHasGraphicFill(boolean hasGraphicFill) {
		this.hasGraphicFill = hasGraphicFill;
	}

	public boolean isHasGraphicStroke() {
		return hasGraphicStroke;
	}

	public void setHasGraphicStroke(boolean hasGraphicStroke) {
		this.hasGraphicStroke = hasGraphicStroke;
	}

	public ArrayList<Float> getDashArray() {
		return dashArray;
	}

	public void setDashArray(ArrayList<Float> dashArray) {
		this.dashArray = dashArray;
	}

	public float getStrokeWidth() {
		return Float.valueOf(expressionWidth.getLiteral());
	}

	public void setExpressionWidth(String width) {
		this.expressionWidth.setLiteral(width);
	}

	public void setExpressionColor(String color) {
		this.expressionColor.setLiteral(color);
	}

	public void setExpressionOpacity(String opacity) {
		this.expressionOpacity.setLiteral(opacity);
	}

	public void setExpressionLineJoin(String lineJoin) {
		this.expressionLineJoin.setLiteral(lineJoin);
	}

	public void setExpressionLineCap(String lineCap) {
		this.expressionLineCap.setLiteral(lineCap);
	}

	public void setExpressionDashOffset(String dashOffset) {
		this.expressionDashOffset.setLiteral(dashOffset);
	}

	public Color getStrokeColor() throws NumberFormatException,
			LegendDriverException {
		return SLDUtils.convertHexStringToColor(this.expressionColor
				.getLiteral());
	}

	public float[] getFloatDashArray() {
		float[] myDash = new float[this.dashArray.size()];
		for (int i = 0; i < dashArray.size(); i++) {
			myDash[i] = dashArray.get(i);
		}
		return myDash;
	}

	public void setFloatDashArray(float[] dash) {
		if (dash != null) {
			dashArray.clear();
			for (int i = 0; i < dash.length; i++) {
				dashArray.add(dash[i]);
			}
		}
	}

	public int getLineJoin() throws LegendDriverException {
		if (expressionLineJoin.getLiteral().compareTo("bevel") == 0)
			return BasicStroke.JOIN_BEVEL;
		else if (expressionLineJoin.getLiteral().compareTo("miter") == 0)
			return BasicStroke.JOIN_MITER;
		else if (expressionLineJoin.getLiteral().compareTo("round") == 0)
			return BasicStroke.JOIN_ROUND;
		else
			throw new LegendDriverException(
					LegendDriverException.PARSE_LEGEND_FILE_ERROR);

	}

	public int getLineCap() throws LegendDriverException {
		if (expressionLineCap.getLiteral().compareTo("butt") == 0)
			return BasicStroke.CAP_BUTT;
		else if (expressionLineCap.getLiteral().compareTo("round") == 0)
			return BasicStroke.CAP_ROUND;
		else if (expressionLineCap.getLiteral().compareTo("square") == 0)
			return BasicStroke.CAP_SQUARE;
		else
			throw new LegendDriverException(
					LegendDriverException.PARSE_LEGEND_FILE_ERROR);
	}
}
