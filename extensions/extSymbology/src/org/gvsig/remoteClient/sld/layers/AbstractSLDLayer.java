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

package org.gvsig.remoteClient.sld.layers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.gvsig.remoteClient.sld.SLDFeatureTypeStyle;
import org.gvsig.remoteClient.sld.SLDLayerFeatureConstraints;
import org.gvsig.remoteClient.sld.SLDRule;
import org.gvsig.remoteClient.sld.filterEncoding.Filter;
import org.gvsig.remoteClient.sld.styles.AbstractSLDStyle;
import org.gvsig.remoteClient.sld.styles.SLDUserStyle;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;

import com.iver.cit.gvsig.fmap.core.FShape;

/**
 * Implements an abstract class for the different kinds of layer that supports
 * SLD implementation specification .
 * <p>
 * The Name element identifies the well-known name of the layer being
 * referenced, and is required. All possible well-known names are usually
 * identified in the capabilities document for a server.
 * <p>
 * The LayerFeatureConstraints element is optional in a NamedLayer and allows
 * the user to specify constraints on what features of what feature types are to
 * be selected by the named-layer reference. It is essentially a filter that
 * allows the selection of fewer features than are present in the named layer.
 * <p>
 * A named styled layer can include any number of named styles and user-defined
 * styles,including zero, mixed in any order. If zero styles are specified, then
 * the default styling for the specified named layer is to be used.
 * <p>
 * A named style, similar to a named layer, is referenced by a well-known name.
 * A particular named style only has meaning when used in conjunction with a
 * particular named layer. All available styles for each available layer are
 * normally named in a capabilities document.
 * 
 * @see SLDNamedLayer
 * @see SLDUserLayer
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class AbstractSLDLayer implements ISLDLayer {

	protected String name;
	protected ArrayList<SLDLayerFeatureConstraints> layerFeatureConstraints = new ArrayList<SLDLayerFeatureConstraints>();
	protected ArrayList<AbstractSLDStyle> layerStyles = new ArrayList<AbstractSLDStyle>();

	public ArrayList<ISLDSymbolizer> getSymbolizersByShapeType(int type) {

		ArrayList<ISLDSymbolizer> symbolizers = new ArrayList<ISLDSymbolizer>();
		for (int i = 0; i < this.getLayerStyles().size(); i++) {
			if (getLayerStyles().get(i) instanceof SLDUserStyle) {
				SLDUserStyle myStyle = (SLDUserStyle) this.getLayerStyles()
						.get(i);
				for (int k = 0; k < myStyle.getFeatureTypeStyle().size(); k++) {
					SLDFeatureTypeStyle myFeatureTypeStyle = myStyle
							.getFeatureTypeStyle().get(k);
					for (int s = 0; s < myFeatureTypeStyle.getRules().size(); s++) {
						SLDRule myRule = (SLDRule) myFeatureTypeStyle
								.getRules().get(s);

						if ((type % FShape.Z) == FShape.LINE) {
							for (int j = 0; j < myRule.getLineSymbolizers()
									.size(); j++) {
								symbolizers.add(myRule.getLineSymbolizers()
										.get(j));
							}
						} else if ((type % FShape.Z) == FShape.POLYGON) {
							for (int j = 0; j < myRule.getPolygonSymbolizers()
									.size(); j++) {
								symbolizers.add(myRule.getPolygonSymbolizers()
										.get(j));
							}
						} else if ((type % FShape.Z) == FShape.POINT) {
							for (int j = 0; j < myRule.getPointSymbolizers()
									.size(); j++) {
								symbolizers.add(myRule.getPointSymbolizers()
										.get(j));
							}
						}
					}
				}
			}
		}
		return symbolizers;
	}

	public ArrayList<Filter> getLayerFilters() {
		ArrayList<Filter> layerFilter = new ArrayList<Filter>();
		for (int i = 0; i < this.getLayerStyles().size(); i++) {
			if (this.getLayerStyles().get(i) instanceof SLDUserStyle) {
				SLDUserStyle userStyle = (SLDUserStyle) this.getLayerStyles()
						.get(i);
				for (int j = 0; j < userStyle.getFeatureTypeStyle().size(); j++) {
					SLDFeatureTypeStyle featTypeStyle = userStyle
							.getFeatureTypeStyle().get(j);
					for (int k = 0; k < featTypeStyle.getRules().size(); k++) {
						SLDRule rule = (SLDRule) featTypeStyle.getRules()
								.get(k);
						if (rule.getFilter() != null)
							layerFilter.add(rule.getFilter());
					}
				}
			}
		}
		return layerFilter;
	}

	public String[] getFieldNames() {
		Set<String> l = new HashSet<String>();
		for (int i = 0; i < this.getLayerFilters().size(); i++) {
			l.addAll(getLayerFilters().get(i).getFieldNames());
		}
		return l.toArray(new String[l.size()]);
	}

	public boolean layerHasFilterForSymbolizers(int shapeType) {
		for (int i = 0; i < getLayerStyles().size(); i++) {
			if (getLayerStyles().get(i) instanceof SLDUserStyle) {
				SLDUserStyle myStyle = (SLDUserStyle) getLayerStyles().get(i);
				for (int j = 0; j < myStyle.getFeatureTypeStyle().size(); j++) {
					SLDFeatureTypeStyle myFeatureTypeStyle = myStyle
							.getFeatureTypeStyle().get(j);
					for (int k = 0; k < myFeatureTypeStyle.getRules().size(); k++) {
						SLDRule myRule = (SLDRule) myFeatureTypeStyle
								.getRules().get(k);
						if (myRule.hasFilter()) {
							if ((shapeType % FShape.Z) == FShape.LINE
									&& myRule.getLineSymbolizers().size() > 0)
								return true;
							else if ((shapeType % FShape.Z) == FShape.POLYGON
									&& myRule.getPolygonSymbolizers().size() > 0)
								return true;
							else if ((shapeType % FShape.Z) == FShape.POINT
									&& myRule.getPointSymbolizers().size() > 0)
								return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean layerHasFilters() {
		for (int i = 0; i < getLayerStyles().size(); i++) {
			if (getLayerStyles().get(i) instanceof SLDUserStyle) {
				SLDUserStyle myStyle = (SLDUserStyle) getLayerStyles().get(i);
				for (int j = 0; j < myStyle.getFeatureTypeStyle().size(); j++) {
					SLDFeatureTypeStyle myFeatureTypeStyle = myStyle
							.getFeatureTypeStyle().get(j);
					for (int k = 0; k < myFeatureTypeStyle.getRules().size(); k++) {
						SLDRule myRule = (SLDRule) myFeatureTypeStyle
								.getRules().get(k);
						if (myRule.hasFilter())
							return true;
					}
				}
			}
		}
		return false;
	}

	public boolean layerHasSymbolizers(int shapeType) {
		for (int i = 0; i < getLayerStyles().size(); i++) {
			if (getLayerStyles().get(i) instanceof SLDUserStyle) {
				SLDUserStyle myStyle = (SLDUserStyle) getLayerStyles().get(i);
				for (int j = 0; j < myStyle.getFeatureTypeStyle().size(); j++) {
					SLDFeatureTypeStyle myFeatureTypeStyle = myStyle
							.getFeatureTypeStyle().get(j);
					for (int k = 0; k < myFeatureTypeStyle.getRules().size(); k++) {
						SLDRule myRule = (SLDRule) myFeatureTypeStyle
								.getRules().get(k);
						if ((shapeType % FShape.Z) == FShape.LINE
								&& myRule.getLineSymbolizers().size() > 0)
							return true;
						else if ((shapeType % FShape.Z) == FShape.POLYGON
								&& myRule.getPolygonSymbolizers().size() > 0)
							return true;
						else if ((shapeType % FShape.Z) == FShape.POINT
								&& myRule.getPointSymbolizers().size() > 0)
							return true;
					}
				}
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<AbstractSLDStyle> getLayerStyles() {
		return layerStyles;
	}

	public void setLayerStyles(ArrayList<AbstractSLDStyle> layerStyles) {
		this.layerStyles = layerStyles;
	}

	public ArrayList<SLDLayerFeatureConstraints> getLayerFeatureConstraints() {
		return layerFeatureConstraints;
	}

	public void setLayerFeatureConstraints(
			ArrayList<SLDLayerFeatureConstraints> layerFeatureConstraints) {
		this.layerFeatureConstraints = layerFeatureConstraints;
	}

	public void addLayerFeatureConstraint(
			SLDLayerFeatureConstraints layerFeatureCons) {
		this.layerFeatureConstraints.add(layerFeatureCons);
	}

	public void addLayerStyle(AbstractSLDStyle style) {
		this.layerStyles.add(style);
	}
}
