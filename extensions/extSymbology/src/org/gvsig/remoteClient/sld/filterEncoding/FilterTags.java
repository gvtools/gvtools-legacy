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
package org.gvsig.remoteClient.sld.filterEncoding;

/**
 * Tags for Filter Encoding
 * 
 * @see http://www.opengeospatial.org/standards/filter
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class FilterTags {

	public final static String FILTER = "ogc:Filter";
	public final static String NAME = "Name";

	public final static String PROPERTYISEQUALTO = "ogc:PropertyIsEqualTo";
	public final static String PROPERTYISNOTEQUALTO = "ogc:PropertyIsNotEqualTo";
	public final static String PROPERTYISLESSTHAN = "ogc:PropertyIsLessThan";
	public final static String PROPERTYISLESSOREQUALTHAN = "ogc:PropertyIsLessThanOrEqualTo";
	public final static String PROPERTYISGREATERTHAN = "ogc:PropertyIsGreaterThan";
	public final static String PROPERTYISGREATEROREQUALTHAN = "ogc:PropertyIsGreaterThanOrEqualTo";
	public final static String PROPERTYISLIKE = "ogc:PropertyIsLike";
	public final static String PROPERTYISNULL = "ogc:PropertyIsNull";
	public final static String PROPERTYISBETWEEN = "ogc:PropertyIsBetween";
	public final static String PROPERTYFEATUREID = "ogc:FeatureId";
	public static final String PROPERTYNAME = "ogc:PropertyName";

	public final static String EQUALS = "Equals";
	public final static String DISJOINT = "Disjoint";
	public final static String INTERSECTS = "Intersects";
	public final static String TOUCHES = "Touches";
	public final static String CROSSES = "Corsses";
	public final static String WITHIN = "Within";
	public static final String DWITHIN = "DWithin";
	public final static String CONTAINS = "Contains";
	public final static String OVERLAPS = "Overlaps";
	public final static String BEYOND = "Beyond";
	public final static String BBOX = "BBOX";

	public final static String AND = "ogc:And";
	public final static String NOT = "ogc:Not";
	public final static String OR = "ogc:Or";

	public static final String ADD = "ogc:Add";
	public static final String SUB = "ogc:Sub";
	public static final String MULT = "ogc:Mult";
	public static final String DIV = "ogc:Div";

	public final static String LITERAL = "ogc:Literal";
	public static final String LITERAL_ATTR = "literal";
	public static final String EXPRESSION = "Expression";
	public static final String FUNCTION = "Function";
	public static final String LOWER_BOUNDARY = "LowerBoundary";
	public static final String UPPER_BOUNDARY = "UpperBoundary";
	public static final String WILDCHAR = "wildChar";
	public static final String SINGLECHAR = "singleChar";
	public static final String ESCAPECHAR = "escapeChar";

	public static final String ENVELOPE = "Envelope";

	// Geometries
	public static final String GML_POINT = "Point";
	public static final String GML_LINESTRING = "LineString";
	public static final String GML_LINEARRING = "LinearRing";
	public static final String GML_POLYGON = "Polygon";
	public static final String GML_MULTIPOINT = "MultiPoint";
	public static final String GML_MULTILINESTRING = "MultiLineString";
	public static final String GML_MULTIPOLYGON = "MultiPolygon";
	public static final String GML_MULTIGEOMETRY = "MultiGeometry";
	public static final String GML_POINTPROPERTY = "pointProperty";
	public static final String GML_LINESTRINGPROPERTY = "lineStringProperty";
	public static final String GML_POLYGONPROPERTY = "polygonProperty";
	public static final String GML_GEOMETRYPROPERTY = "geometryProperty";
	public static final String GML_MULTIPOINTPROPERTY = "multiPointProperty";
	public static final String GML_MULTILINESTRINGPROPERTY = "multiLineStringProperty";
	public static final String GML_MULTIPOLYGONPROPERTY = "multiPolygonProperty";
	public static final String GML_MULTIGEOMETRYPROPERTY = "multiGeometryProperty";

}
