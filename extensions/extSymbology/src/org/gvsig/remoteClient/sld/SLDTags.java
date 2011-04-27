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
/**
 * Tags for SLD
 *
 * @see http://portal.opengeospatial.org/files/?artifact_id=1188
 *
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class SLDTags
{
	public final static String SLD_ROOT="StyledLayerDescriptor";
	public final static String VERSION_ATTR="version";

	public final static String NAMEDLAYER="NamedLayer";
	public final static String USERDEFINEDLAYER="UserLayer";
	public final static String NAME="Name";
	public final static String TITLE ="Title";
	public final static String ABSTRACT ="Abstract";
	public final static String USERSTYLE ="UserStyle";
	public final static String NAMEDSTYLE ="NamedStyle";
	public final static String FEATURETYPESTYLE ="FeatureTypeStyle";
	public final static String FEATURETYPENAME ="FeatureTypeName";
	public final static String FEATURETYPECONSTRAINT = "FeatureTypeConstraint";
	public final static String SEMANTICTYPEIDENTIFIER="SemanticTypeIdentifier";
	public final static String RULE = "Rule";
	public final static String FILTER = "Filter";

	public final static String LITERAL = "Literal";
	public static final String LITERAL_ATTR = "literal";

	public static final String LINESYMBOLIZER = "LineSymbolizer";
	public static final String POINTSYMBOLIZER = "PointSymbolizer";
	public static final String TEXTSYMBOLIZER = "TextSymbolizer";
	public static final String POLYGONSYMBOLIZER = "PolygonSymbolizer";
	public static final String RASTERSYMBOLIZER = "RasterSymbolizer";

	public static final String GEOMETRY = "Geometry";
	public static final String CSSPARAMETER = "CssParameter";
	public static final String NAME_ATTR = "name";

	public static final String NAME_ATTR_VERSAL = "Name";//Ppara compatibilizar leyendas generadas con la 1.1.2

	public static final String FILL = "Fill";
	public static final String FILL_ATTR = "fill";
	public static final String FILLOPACITY_ATTR = "fill-opacity";

	public static final String STROKE = "Stroke";
	public static final String STROKE_ATTR = "stroke";
	public static final String STROKE_WIDTH_ATTR = "stroke-width";
	public static final String STROKE_OPACITY_ATTR = "stroke-opacity";


	public static final String SIZE = "Size";
	public static final String ROTATION = "Rotation";
	public static final String WIDTH = "Width";
	public static final String MARK = "Mark";

	public static final String GRAPHIC = "Graphic";
	public static final String EXTERNALGRAPHIC = "ExternalGraphic";
	public static final String LEGENDGRAPHIC = "LegendGraphic";
	public static final String GRAPHICFILL = "GraphicFill";
	public static final String GRAPHICSTROKE = "GraphicStroke";

	public static final String LABEL = "Label";
	public static final String LABELPLACEMENT = "LabelPlacement";
	public static final String FONT = "Font";
	public static final String FONTFAMILY_ATTR = "font-family";
	public static final String FONTSTYLE_ATTR = "font-style";
	public static final String FONTSIZE_ATTR = "font-size";
	public static final String FONTWEIGHT_ATTR = "font-weight";
	public static final String DISPLACEMENT_ATTR = "displacement";

	public static final String WELLKNOWNNAME = "WellKnownName";
	public static final String FORMAT = "Format";

	public static final String OVERLAPBEHAVIOR = "OverlapBehavior";
	public static final String OPACITY = "Opacity";
	public static final String COLORMAP = "ColorMap";
	public static final String MINSCALEDENOMINATOR = "MinScaleDenominator";
	public static final String MAXSCALEDENOMINATOR = "MaxScaleDenominator";
	public static final String ELSEFILTER = "ElseFilter";
	public static final String HALO = "Halo";
	public static final String PRIORITY_ATTR = "priority";
	public static final String VENDOROPTION_ATTR = "vendoroption";
	public static final String FID_ATTR = "fid";
	public static final String LOWERBOUNDARY = "LowerBoundary";
	public static final String UPPERBOUNDARY = "UpperBoundary";
	public static final String WILDCARD_ATTR = "wildCard";
	public static final String SINGLECHAR_ATTR = "singleChar";
	public static final String ESCAPECHAR_ATTR = "escapeChar";
	public static final String ESCAPE_ATTR = "escape";

	public static final String ROTATION_ATTR = "rotation";
	public static final String WIDTH_ATTR = "width";
	public static final String LINECAP_ATTR = "linecap";
	public static final String STROKE_LINECAP_ATTR = "stroke-linecap";
	public static final String LINEJOIN_ATTR = "linejoin";
	public static final String STROKE_LINEJOIN_ATTR = "stroke-linejoin";
	public static final String DASHARRAY_ATTR = "dasharray";
	public static final String STROKE_DASHARRAY_ATTR = "stroke-dasharray";
	public static final String DASHOFFSET_ATTR = "dashoffset";
	public static final String STROKE_DASHOFFSET_ATTR = "stroke-dashoffset";

	public static final String LAYER_FEATURE_CONST = "LayerFeatureConstraints";
	public static final String IS_DEFAULT = "IsDefault";

	public static final String REMOTE_OWS = "RemoteOWS";
	public static final String SERVICE = "Service";
	public static final String ONLINE_RESOURCE = "OnlineResource";
	public final static String XLINK_HREF ="xlink:href";

	public static final String EXTENT = "Extent";
	public static final String VALUE = "Value";
	public static final String CSSPARAMETER_WIDTH = "CssParameter name=\"Stroke-width\"";
	public static final String PROPERTY_NAME =  "PropertyName";
	public static final String FUNCTION = "Function";


	public static final String SQUARE = "square";
	public static final String CIRCLE = "circle";
	public static final String TRIANGLE = "triangle";
	public static final String STAR = "star";
	public static final String CROSS = "cross";



}
