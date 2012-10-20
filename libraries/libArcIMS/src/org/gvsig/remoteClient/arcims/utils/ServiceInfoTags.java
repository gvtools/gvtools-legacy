/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

package org.gvsig.remoteClient.arcims.utils;

/**
 * Class containing a description for all the TAGS defined in the SERVICEINFO
 * Object returned from a ArcIMS Server
 */
public class ServiceInfoTags {
	/*
	 * Elements of a ServiceInfo XML
	 */
	public final static String tARCXML = "ARCXML";
	public final static String tBACKGROUND = "BACKGROUND";
	public final static String tENVELOPE = "ENVELOPE";
	public final static String tENVIRONMENT = "ENVIRONMENT";
	public final static String tEXTENSION = "EXTENSION";
	public static final String tERROR = "ERROR";
	public final static String tEXTRACTPARAMS = "EXTRACTPARAMS";
	public final static String tFCLASS = "FCLASS";
	public final static String tFEATURECOORDSYS = "FEATURECOORDSYS";
	public final static String tFILTERCOORDSYS = "FILTERCOORDSYS ";
	public final static String tFIELD = "FIELD";
	public final static String tGCSTYLE = "GCSTYLE";
	public final static String tIMAGELIMIT = "IMAGELIMIT";
	public final static String tIMAGESIZE = "IMAGESIZE";
	public final static String tLAYERINFO = "LAYERINFO";
	public final static String tLEGEND = "LEGEND";
	public final static String tLOCALE = "LOCALE";
	public final static String tMAPUNITS = "MAPUNITS";
	public final static String tOUTPUT = "OUTPUT";
	public final static String tOUTPUTFIELD = "OUTPUTFIELD";
	public final static String tOUTPUTFILE = "OUTPUTFILE";
	public final static String tPROPERTIES = "PROPERTIES";
	public final static String tQUERY = "QUERY";
	public final static String tRESPONSE = "RESPONSE";
	public final static String tSCREEN = "SCREEN";
	public final static String tSEPARATORS = "SEPARATORS";
	public final static String tSERVICEINFO = "SERVICEINFO";
	public final static String tSQVAR = "SQVAR";
	public final static String tSTOREDQUERIES = "STOREDQUERIES";
	public final static String tSTOREDQUERY = "STOREDQUERY";
	public final static String tUIFONT = "UIFONT";

	/*
	 * Attributes of a ServiceInfo XML
	 */
	public final static String aPOLYGON = "polygon";
	public final static String aPOLYLINE = "line";
	public final static String aMULTIPOINT = "point";
	public final static String aAUTOEXTEND = "autoextend";
	public final static String aBACKGROUNDCOLOR = "backgroundcolor";
	public final static String aBASEURL = "baseurl";
	public final static String aCOLOR = "color";
	public final static String aCOUNTRY = "country";
	public final static String aCS = "cs";
	public final static String aDPI = "dpi";
	public final static String aENVELOPEIE = "Initial_Extent";
	public final static String aENVELOPEEL = "Extent_Limit";
	public static final String aFONT = "font";
	public final static String aHEIGHT = "height";
	public final static String aID = "id";
	public final static String aLANGUAGE = "language";
	public final static String aMAXSCALE = "maxscale";
	public final static String aMINSCALE = "minscale";
	public final static String aMAXX = "maxx";
	public final static String aMAXY = "maxy";
	public final static String aMINX = "minx";
	public final static String aMINY = "miny";
	public final static String aNAME = "name";
	public final static String aPATH = "path";
	public final static String aPRECISION = "precision";
	public final static String aSTYLE = "style";
	public final static String aSIZE = "size";
	public final static String aPIXELCOUNT = "pixelcount";
	public final static String aTRANSPARENCY = "transparency";
	public final static String aTS = "ts";
	public final static String aTYPE = "type";
	public final static String aUNITS = "units";
	public final static String aURL = "url";
	public final static String aVISIBLE = "visible";
	public final static String aWIDTH = "width";

	/*
	 * Values of a ServiceInfo XML
	 */
	public final static String vLAYERTYPE_F = "featureclass";
	public final static String vLAYERTYPE_I = "image";

	/*
	 * Types of ArcIMS Services
	 */
	public final static String vIMAGESERVICE = "ImageServer";
	public final static String vFEATURESERVICE = "FeatureServer";
	public final static String vINI_SRS = "EPSG:";

	/*
	 * Map units string IDs
	 */
	public final static String vMAP_UNITS_DECIMAL_DEGREES = "decimal_degrees";
	public final static String vMAP_UNITS_FEET = "feet";
	public final static String vMAP_UNITS_METERS = "meters";

	/*
	 * Image formats
	 */
	public final static String vPNG24 = "png";
	public final static String vPNG8 = "png8";
	public final static String vJPEG = "jpg";
	public final static String vGIF = "gif";

	/*
	 * Renderers
	 */
	public final static String tSIMPLERENDERER = "SIMPLERENDERER";
	public static final String tGROUPRENDERER = "GROUPRENDERER";
	public static final String tSCALEDEPENDENTRENDERER = "SCALEDEPENDENTRENDERER";
	public static final String tSIMPLELABELRENDERER = "SIMPLELABELRENDERER";
	public static final String tVALUEMAPLABELRENDERER = "VALUEMAPLABELRENDERER";
	public static final String tVALUEMAPRENDERER = "VALUEMAPRENDERER";
	public static final String[] RENDERERS = new String[6]; // See static at the
															// end
	public final static String tEXACT = "EXACT";
	public final static String tRANGE = "RANGE";
	public final static String tOTHER = "OTHER";

	/*
	 * Symbology attributes
	 */
	public static final String aBOUNDARY = "boundary";
	public static final String aCAPTYPE = "captype";
	public final static String aEQUALITY = "equality";
	public static final String aJOINTYPE = "jointype";
	public final static String aLABEL = "label";
	public final static String aLABELFIELD = "labelfield";
	public static final String aLINETHICKNESS = "linethickness";
	public static final String aLOOKUPFIELD = "lookupfield";
	public static final String aLOWER = "lower";
	public final static String aMETHOD = "method";
	public final static String aROTATIONALANGLES = "rotationalangles";
	public final static String aSFIELD = "field";
	public static final String UPPER = "upper";
	public final static String VALUE = "value";

	/*
	 * Types of symbols supported
	 */
	public static final String tSIMPLELINESYMBOL = "SIMPLELINESYMBOL";
	public static final String tSIMPLEMARKERSYMBOL = "SIMPLEMARKERSYMBOL";
	public static final String tSIMPLEPOLYGONSYMBOL = "SIMPLEPOLYGONSYMBOL";
	public static final String tTEXTSYMBOL = "TEXTSYMBOL";

	/*
	 * Types not supported
	 */
	public static final String tRASTERMARKERSYMBOL = "RASTERMARKERSYMBOL";
	public static final String tGRADIENTFILLSYMBOL = "GRADIENTFILLSYMBOL";
	public static final String tHASHLINESYMBOL = "HASHLINESYMBOL";
	public static final String tRASTERFILLSYMBOL = "RASTERFILLSYMBOL";
	public static final String tCALLOUTMARKERSYMBOL = "CALLOUTMARKERSYMBOL";
	public static final String tCHARTSYMBOL = "CHARTSYMBOL";
	public static final String tRASTERSHIELDSYMBOL = "RASTERSHIELDSYMBOL";
	public static final String tSHIELDSYMBOL = "SHIELDSYMBOL";
	public static final String tTEXTMARKERSYMBOL = "TEXTMARKERSYMBOL";
	public static final String tTRUETYPEMARKERSYMBOL = "TRUETYPEMAKERSYMBOL";
	public static final String[] SYMBOLS = new String[14];
	public static final String[] NOT_SUPP_SYMBOLS = new String[9];

	static {
		SYMBOLS[0] = tSIMPLELINESYMBOL;
		SYMBOLS[1] = tSIMPLEMARKERSYMBOL;
		SYMBOLS[2] = tSIMPLEPOLYGONSYMBOL;
		SYMBOLS[3] = tTEXTSYMBOL;
		SYMBOLS[4] = tRASTERMARKERSYMBOL;

		// Not supported
		SYMBOLS[5] = tGRADIENTFILLSYMBOL;
		SYMBOLS[6] = tHASHLINESYMBOL;
		SYMBOLS[7] = tRASTERFILLSYMBOL;
		SYMBOLS[8] = tTRUETYPEMARKERSYMBOL;
		SYMBOLS[9] = tCALLOUTMARKERSYMBOL;
		SYMBOLS[10] = tCHARTSYMBOL;
		SYMBOLS[11] = tRASTERSHIELDSYMBOL;
		SYMBOLS[12] = tSHIELDSYMBOL;
		SYMBOLS[13] = tTEXTMARKERSYMBOL;

		RENDERERS[0] = tSIMPLERENDERER;
		RENDERERS[1] = tGROUPRENDERER;
		RENDERERS[2] = tVALUEMAPRENDERER;
		RENDERERS[3] = tSCALEDEPENDENTRENDERER;
		RENDERERS[4] = tSIMPLELABELRENDERER;
		RENDERERS[5] = tVALUEMAPLABELRENDERER;

		NOT_SUPP_SYMBOLS[0] = tGRADIENTFILLSYMBOL;
		NOT_SUPP_SYMBOLS[1] = tHASHLINESYMBOL;
		NOT_SUPP_SYMBOLS[2] = tRASTERFILLSYMBOL;
		NOT_SUPP_SYMBOLS[3] = tTRUETYPEMARKERSYMBOL;
		NOT_SUPP_SYMBOLS[4] = tCALLOUTMARKERSYMBOL;
		NOT_SUPP_SYMBOLS[5] = tCHARTSYMBOL;
		NOT_SUPP_SYMBOLS[6] = tRASTERSHIELDSYMBOL;
		NOT_SUPP_SYMBOLS[7] = tSHIELDSYMBOL;
		NOT_SUPP_SYMBOLS[8] = tTEXTMARKERSYMBOL;
	}
}
