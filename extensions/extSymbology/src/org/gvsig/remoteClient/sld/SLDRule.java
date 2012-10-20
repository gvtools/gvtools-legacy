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
import java.util.HashSet;
import java.util.Set;

import org.gvsig.remoteClient.sld.filterEncoding.Filter;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDLineSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDPointSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDPolygonSymbolizer;

/**
 * Implements the Rule element of an SLD specification.<br>
 * 
 * Rules are used to group rendering instructions by feature-property conditions
 * and map scales.Rule definitions are placed inmediatelly inside of
 * feature-style definitions.
 * 
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * @author Pepe Vidal Salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDRule implements ISLDFeatures {

	protected String name;
	protected String title;
	protected String ruleAbstract;
	protected SLDLegendGraphic legendGraphic;
	protected double minScaleDenominator;
	protected double maxScaleDenominator;
	protected Filter filter;
	protected Filter elseFilter;
	protected ArrayList<ISLDSymbolizer> lineSymbolizers = new ArrayList<ISLDSymbolizer>();
	protected ArrayList<ISLDSymbolizer> pointSymbolizers = new ArrayList<ISLDSymbolizer>();
	protected ArrayList<ISLDSymbolizer> polygonSymbolizers = new ArrayList<ISLDSymbolizer>();
	protected ArrayList<ISLDSymbolizer> textSymbolizers = new ArrayList<ISLDSymbolizer>();
	protected ArrayList<ISLDSymbolizer> rasterSymbolizers = new ArrayList<ISLDSymbolizer>();
	Set<String> fieldNames = new HashSet<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRuleAbstract() {
		return ruleAbstract;
	}

	public void setRuleAbstract(String ruleAbstract) {
		this.ruleAbstract = ruleAbstract;
	}

	public SLDLegendGraphic getLegendGraphic() {
		return legendGraphic;
	}

	public void setLegendGraphic(SLDLegendGraphic legendGraphic) {
		this.legendGraphic = legendGraphic;
	}

	public double getMinScaleDenominator() {
		return minScaleDenominator;
	}

	public void setMinScaleDenominator(double minScaleDenominator) {
		this.minScaleDenominator = minScaleDenominator;
	}

	public double getMaxScaleDenominator() {
		return maxScaleDenominator;
	}

	public void setMaxScaleDenominator(double maxScaleDenominator) {
		this.maxScaleDenominator = maxScaleDenominator;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getElseFilter() {
		return elseFilter;
	}

	public void setElseFilter(Filter elseFilter) {
		this.elseFilter = elseFilter;
	}

	public ArrayList<ISLDSymbolizer> getLineSymbolizers() {
		return lineSymbolizers;
	}

	public void setLineSymbolizers(ArrayList<ISLDSymbolizer> lineSymbolizers) {
		this.lineSymbolizers = lineSymbolizers;
	}

	public ArrayList<ISLDSymbolizer> getPointSymbolizers() {
		return pointSymbolizers;
	}

	public void setPointSymbolizers(ArrayList<ISLDSymbolizer> pointSymbolizers) {
		this.pointSymbolizers = pointSymbolizers;
	}

	public ArrayList<ISLDSymbolizer> getPolygonSymbolizers() {
		return polygonSymbolizers;
	}

	public void setPolygonSymbolizers(
			ArrayList<ISLDSymbolizer> polygonSymbolizers) {
		this.polygonSymbolizers = polygonSymbolizers;
	}

	public ArrayList<ISLDSymbolizer> getTextSymbolizers() {
		return textSymbolizers;
	}

	public void setTextSymbolizers(ArrayList<ISLDSymbolizer> textSymbolizers) {
		this.textSymbolizers = textSymbolizers;
	}

	public ArrayList<ISLDSymbolizer> getRasterSymbolizers() {
		return rasterSymbolizers;
	}

	public void setRasterSymbolizers(ArrayList<ISLDSymbolizer> rasterSymbolizers) {
		this.rasterSymbolizers = rasterSymbolizers;
	}

	public Set<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(Set<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public void addLineSymbolizer(SLDLineSymbolizer sldLine) {
		this.lineSymbolizers.add(sldLine);

	}

	public void addPolygonSymbolizer(SLDPolygonSymbolizer sldPolygon) {
		this.polygonSymbolizers.add(sldPolygon);

	}

	public void addPointSymbolizer(SLDPointSymbolizer sldPoint) {
		this.pointSymbolizers.add(sldPoint);

	}

	public abstract boolean hasFilter();

}
