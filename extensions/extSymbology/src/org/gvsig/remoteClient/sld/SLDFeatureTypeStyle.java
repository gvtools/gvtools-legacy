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

/**
 * Implements the FeatureTypeStyle element of an SLD implementation
 * specification .
 * <p>
 * The FeatureTypeStyle defines the styling that is to be applied to a single
 * feature type of a layer. This element may also be externally re-used outside
 * of the WMSes and layers.
 * <p>
 * 
 * The FeatureTypeStyle element identifies that explicit separation between the
 * handling of features of specific feature types. The 'layer' concept is unique
 * to WMS and SLD, but features are used more generally, such as in WFS and GML,
 * so this explicit separation is important.
 * <p>
 * 
 * Like a UserStyle, a FeatureTypeStyle can have a Name, Title, and Abstract.
 * The Name element does not have an explicit use at present, though it
 * conceivably might be used to reference a feature style in some feature-style
 * library. The Title and Abstract are for human-readable information.
 * <p>
 * 
 * The FeatureTypeName identifies the specific feature type that the
 * feature-type style is for. It is allowed to be optional, but only if one
 * feature type is in-context (in-layer) and that feature type must match the
 * syntax and semantics of all feature-property references inside of the
 * FeatureTypeStyle. Note that there is no restriction against a single
 * UserStyle from including multiple FeatureTypeStyles that reference the same
 * FeatureTypeName. This case does not create an exception in the rendering
 * semantics,however, since a map styler is expected to process all
 * FeatureTypeStyles in the order that they appear, regardless, plotting one
 * instance over top of another.
 * <p>
 * 
 * The SemanticTypeIdentifier is experimental and is intended to be used to
 * identify what the feature style is suitable to be used for using
 * community-controlled name(s). For example, a single style may be suitable to
 * use with many different feature types. The syntax of the
 * SemanticTypeIdentifier string is undefined, but the strings
 * �generic:line�, �generic:polygon�, �generic:point�,
 * �generic:text�, �generic:raster�, and �generic:any� are reserved
 * to indicate that a FeatureTypeStyle may be used with any feature type with
 * the corresponding default geometry type (i.e., no feature properties are
 * referenced in the feature-type style).
 * <p>
 * 
 * The FeatureTypeStyle contains one or more Rule elements that allow
 * conditional rendering. Rules are discussed in Section 10.
 * 
 * @see SLDRule
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 * 
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public abstract class SLDFeatureTypeStyle implements ISLDFeatures {

	protected String name;
	protected String title;
	protected String ftsAbstract;
	protected String featureTypeName;
	protected ArrayList<String> semanticTypeIdentifier = new ArrayList<String>();
	protected ArrayList<SLDRule> rules = new ArrayList<SLDRule>();

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

	public String getFtsAbstract() {
		return ftsAbstract;
	}

	public void setFtsAbstract(String ftsAbstract) {
		this.ftsAbstract = ftsAbstract;
	}

	public String getFeatureTypeName() {
		return featureTypeName;
	}

	public void setFeatureTypeName(String featureTypeName) {
		this.featureTypeName = featureTypeName;
	}

	public ArrayList<String> getSemanticTypeIdentifier() {
		return semanticTypeIdentifier;
	}

	public void setSemanticTypeIdentifier(
			ArrayList<String> semanticTypeIdentifier) {
		this.semanticTypeIdentifier = semanticTypeIdentifier;
	}

	public void addSemanticTypeIdentifier(String cad) {
		this.semanticTypeIdentifier.add(cad);
	}

	public ArrayList<SLDRule> getRules() {
		return rules;
	}

	public void setRules(ArrayList<SLDRule> rules) {
		this.rules = rules;
	}

	public void addRule(SLDRule rule) {
		this.rules.add(rule);
	}

}
