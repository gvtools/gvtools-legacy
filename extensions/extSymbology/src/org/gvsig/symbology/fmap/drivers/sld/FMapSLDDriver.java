/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 *   Av. Blasco Ibáñez, 50
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
package org.gvsig.symbology.fmap.drivers.sld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.gvsig.remoteClient.sld.SLDExternalGraphic;
import org.gvsig.remoteClient.sld.SLDFill;
import org.gvsig.remoteClient.sld.SLDGraphic;
import org.gvsig.remoteClient.sld.SLDMark;
import org.gvsig.remoteClient.sld.SLDProtocolHandler;
import org.gvsig.remoteClient.sld.SLDProtocolHandlerFactory;
import org.gvsig.remoteClient.sld.SLDStroke;
import org.gvsig.remoteClient.sld.SLDTags;
import org.gvsig.remoteClient.sld.SLDUtils;
import org.gvsig.remoteClient.sld.UnsupportedSLDVersionException;
import org.gvsig.remoteClient.sld.filterEncoding.FExpression;
import org.gvsig.remoteClient.sld.filterEncoding.Filter;
import org.gvsig.remoteClient.sld.filterEncoding.FilterFactory;
import org.gvsig.remoteClient.sld.filterEncoding.FilterTags;
import org.gvsig.remoteClient.sld.layers.ISLDLayer;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDLineSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDMultiLineSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDMultiPointSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDMultiPolygonSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDPointSymbolizer;
import org.gvsig.remoteClient.sld.symbolizers.SLDPolygonSymbolizer;
import org.gvsig.symbology.fmap.rendering.VectorFilterExpressionLegend;
import org.gvsig.symbology.fmap.rendering.filter.operations.Expression;
import org.gvsig.symbology.fmap.symbols.CharacterMarkerSymbol;
import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;
import org.gvsig.symbology.fmap.symbols.MarkerLineSymbol;
import org.gvsig.symbology.fmap.symbols.PictureFillSymbol;
import org.gvsig.symbology.fmap.symbols.PictureLineSymbol;
import org.gvsig.symbology.fmap.symbols.PictureMarkerSymbol;
import org.xmlpull.v1.XmlPullParserException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiShapeSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.drivers.legend.IFMapLegendDriver;
import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.FInterval;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.NullIntervalValue;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.rendering.XmlBuilder;
/**
 * Implements the driver for Styled Layer Descriptor documents in order
 * to allow gvSIG to read or write XML documents of this OGC Standard.
 *
 * @author pepe vidal salvador - jose.vidal.salvador@iver.es
 */
public class FMapSLDDriver implements IFMapLegendDriver{

	private static final String DESCRIPTION = "Styled Layer Descriptor";
	private static final String FILE_EXTENSION = "sld";
	private static final String DEFAULT_ENCODING="ISO-8859-1";
	private FLayer layer;
	private String[] fieldNames;
	private int[] fieldTypes;

	private static String sldReadVersion;

	public boolean accept(File f) {
		if (f.isDirectory()) return true;
		String fName = f.getAbsolutePath();
		if (fName!=null) {
			fName = fName.toLowerCase();
			return fName.endsWith(".sld");
		}
		return false;
	}

	public String getDescription() {return DESCRIPTION;}
	public String getFileExtension() {return FILE_EXTENSION;}
	/**
	 * Obtains the classifying field names that are used in the .sld
	 * document
	 *
	 * @return Array of Strings with the classifying field names
	 */
	public String[] getClassifyingFieldNames() { return fieldNames; }
	/**
	 * Returns the classifying field types of the classifying field names
	 * that are used in the .sld document
	 *
	 * @return Array of integers with the types
	 */
	public int[] getClassifyingFieldTypes() { return fieldTypes; }

	/**
	 * Returns true if any layer contained in the TOC menu of gvSIG has the
	 * same name that is passed as an argument. Otherwise,false.
	 *
	 * @param layer
	 * @param name
	 *
	 * @return boolean
	 */
	private boolean checkIsUsableSldLyr(FLayer layer, String name) {
		if (layer instanceof FLayers) {
			FLayers layers = (FLayers) layer;
			for (int i = 0; i < layers.getLayersCount(); i++) {
				if (checkIsUsableSldLyr(layers.getLayer(i), name)) {
					return true;
				}
			}
			return false;
		}
		return layer.getName().equals(name);
	}

	public Hashtable<FLayer, ILegend> read(FLayers layers,FLayer layer, File file) throws LegendDriverException {
		this.layer = layer;
		SLDProtocolHandler my_sld = null;

		try {

			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(file);
			sldReadVersion = my_sld.getVersion();
			((SLDProtocolHandler) my_sld).parse(file);

		} catch (LegendDriverException e) {
			throw new LegendDriverException(e.getType());
		} catch (IOException e) {
			throw new LegendDriverException(LegendDriverException.READ_DRIVER_EXCEPTION);
		} catch (XmlPullParserException e) {
			throw new LegendDriverException(LegendDriverException.PARSE_LEGEND_FILE_ERROR);
		} catch (UnsupportedSLDVersionException e) {
			throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_FILE_VERSION);
		}

		ArrayList<ISLDLayer> sldLayers = my_sld.getLayers();
		Hashtable<FLayer, ILegend> table = new Hashtable<FLayer, ILegend>();
		ArrayList<ISLDLayer> usableLayers = new ArrayList<ISLDLayer>();


		for (Iterator<ISLDLayer> iterator = sldLayers.iterator(); iterator.hasNext();) {
			ISLDLayer aLayer = (ISLDLayer) iterator.next();
			if (checkIsUsableSldLyr(layers, aLayer.getName())){
				usableLayers.add(aLayer);
			}
		}


		for (int i = 0; i < usableLayers.size(); i++) {
			if(usableLayers.get(i).getName().compareTo(layer.getName()) == 0) {
				try {
					ILegend legend = createLegend(usableLayers.get(i));;
					if (legend != null) {
						table.put(layer, legend);

					}
				}catch (ReadDriverException e) {
					e.printStackTrace();
					throw new LegendDriverException(LegendDriverException.READ_DRIVER_EXCEPTION);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					throw new LegendDriverException(LegendDriverException.READ_DRIVER_EXCEPTION);
				}
			}
		}

		if (table.isEmpty()) {
			throw new LegendDriverException(LegendDriverException.LAYER_NAME_NOT_FOUND);
		}
		return table;

	}
	/**
	 * Takes a SLDLayer and with its information, creates a gvSIG legend. Depending
	 * on if the layer has filters or not, it will be created a single symbol legend
	 * or a filter expression legend
	 *
	 * @param sldLayer layer contained in a .sld document
	 * @return a gvSIG legend
	 * @throws ReadDriverException
	 * @throws NumberFormatException
	 * @throws LegendDriverException
	 * @throws UnknownHostException
	 */
	private ILegend createLegend(ISLDLayer sldLayer) throws ReadDriverException, NumberFormatException, LegendDriverException {
		FLyrVect lyrVect = (FLyrVect)this.layer;
		ArrayList<Filter> filters = sldLayer.getLayerFilters();
		fieldNames = sldLayer.getFieldNames();

		if (!checkFieldNames(fieldNames))
			throw new LegendDriverException(LegendDriverException.CLASSIFICATION_FIELDS_NOT_FOUND);


		//Multishape not yet supported
		if((lyrVect.getShapeType()%FShape.Z) == FShape.MULTI )
			throw new LegendDriverException(LegendDriverException.READ_DRIVER_EXCEPTION);

		//SingleSymbolLegend
		else if((lyrVect.getShapeType()%FShape.Z) != FShape.MULTI && !sldLayer.layerHasFilterForSymbolizers(lyrVect.getShapeType())) {
			SingleSymbolLegend createdLegend = new SingleSymbolLegend();
			ArrayList<ISLDSymbolizer> symbolizers;
			symbolizers = sldLayer.getSymbolizersByShapeType(lyrVect.getShapeType());
			IMultiLayerSymbol multiLayerSymbol = SymbologyFactory.createEmptyMultiLayerSymbol(lyrVect.getShapeType());

			if (multiLayerSymbol != null) {
				for (int i = 0; i < symbolizers.size(); i++) {
					ISymbol sym = SLDSymbolizer2ISymbol(symbolizers.get(i));
					multiLayerSymbol.addLayer(sym);
				}
				ISymbol defaultSym = (multiLayerSymbol.getLayerCount() > 1) ?
						multiLayerSymbol :
							multiLayerSymbol.getLayer(0);

				defaultSym.setDescription("Default");
				createdLegend.setDefaultSymbol(defaultSym);
				return createdLegend;
			}
			return null;
		}
		//VectorialFilterExpressionLegend
		else  {
			VectorFilterExpressionLegend legend = new VectorFilterExpressionLegend(lyrVect.getShapeType(),fieldNames);
			for (int i = 0; i < filters.size(); i++) {
				for (int j = 0; j < filters.get(i).getSymbolizers().size(); j++) {

					ISLDSymbolizer sldSym = filters.get(i).getSymbolizers().get(j);
					ISymbol sym = SLDSymbolizer2ISymbol(sldSym);
					sym.setDescription(filters.get(i).getExpression().toString());
					legend.addSymbol(filters.get(i).getExpression().toString(), sym);
				}
			}
			return legend;
		}
	}
	/**
	 * Checks if the field names contained in an sld document appear in the information
	 * of the layer that we are using. If all the field names are correct the method
	 * returns true.Otherwise, false.
	 *
	 * @param fieldNames
	 * @return
	 * @throws ReadDriverException
	 */
	private boolean checkFieldNames(String[] fieldNames) throws ReadDriverException {

		for (int i = 0; i < fieldNames.length; i++) {
			if (((FLyrVect) this.layer).getSource().getRecordset().getFieldIndexByName(fieldNames[i]) == -1)
				return false;
		}
		return true;
	}
	/**
	 * Transforms a SLD Symbolizer into an ISymbol.
	 *
	 * @param symbolizer to be transfomed into an ISymbol
	 * @return ISymbol
	 * @throws NumberFormatException
	 * @throws LegendDriverException
	 * @throws UnknownHostException
	 */
	public static ISymbol SLDSymbolizer2ISymbol(ISLDSymbolizer symbolizer) throws NumberFormatException, LegendDriverException {
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		//LINE//
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (symbolizer instanceof SLDLineSymbolizer) {
			SLDLineSymbolizer myLine = (SLDLineSymbolizer) symbolizer;

			SimpleLineStyle simplLine= new SimpleLineStyle();
			int lineCap;
			if (myLine.getStroke().getExpressionLineCap().getLiteral() != null)
				lineCap = myLine.getStroke().getLineCap();
			else lineCap = BasicStroke.CAP_SQUARE;

			int lineJoin;
			if (myLine.getStroke().getExpressionLineJoin().getLiteral() != null)
				lineJoin = myLine.getStroke().getLineJoin();
			else lineJoin = BasicStroke.JOIN_MITER;

			Float dashOffset;
			if (myLine.getStroke().getExpressionDashOffset().getLiteral() != null)
				dashOffset = Float.valueOf(myLine.getStroke().getExpressionDashOffset().getLiteral());
			else dashOffset = 0f;

			Float width;
			if (myLine.getStroke().getExpressionWidth().getLiteral()!= null)
				width = myLine.getStroke().getStrokeWidth();
			else width = 0f;

			float[] dash;
			if(myLine.getStroke().getFloatDashArray().length == 0) {
				dash = null;
			}
			else dash = myLine.getStroke().getFloatDashArray();

			BasicStroke str = new BasicStroke(width,
					lineCap,
					lineJoin,
					10,
					dash,
					dashOffset);


			simplLine.setStroke(str);

			//SimpleLineSymbol
			if ( !myLine.getStroke().isHasGraphicFill() && !myLine.getStroke().isHasGraphicStroke() ) {
				SimpleLineSymbol line = new SimpleLineSymbol();
				line.setLineStyle(simplLine);
				line.setUnit(-1);
				line.setLineColor(myLine.getStroke().getStrokeColor());
				if(myLine.getStroke().getExpressionOpacity().getLiteral() != null)
					line.setAlpha((int)(255 * Float.valueOf(myLine.getStroke().getExpressionOpacity().getLiteral())));
				if (myLine.getStroke().getExpressionWidth().getLiteral() != null)
					line.setLineWidth(myLine.getStroke().getStrokeWidth());
				return line;
			}
			else {
				//MarkerLineSymbol
				MarkerLineSymbol line = new MarkerLineSymbol();
				line.setLineStyle(simplLine);
				line.setUnit(-1);
				//If the marker is an instance of SimpleMarkerSymbol
				if(myLine.getStroke().getGraphic().getMarks().size() > 0) {
					SLDMark myMark = myLine.getStroke().getGraphic().getMarks().get(0);
					SimpleMarkerSymbol marker = new SimpleMarkerSymbol();
					FExpression size = myLine.getStroke().getGraphic().getExpressionSize();
					FExpression rotation = myLine.getStroke().getGraphic().getExpressionRotation();
					marker = createMarker(myMark,size,rotation);
					line.setLineWidth(marker.getSize());
					marker.setUnit(-1);
					marker.setStyle(SLDUtils.setMarkerStyle(myMark.getWellKnownName().getLiteral()));
					line.setMarker(marker);
					line.setAlpha(marker.getColor().getAlpha());
					if(str.getDashArray() != null)
						line.setSeparation(str.getDashArray()[1]);
					return line;
				}
				//If the marker is an instance of PictureMarkerSymbol
				else if (myLine.getStroke().getGraphic().getExternalGraphics().size() > 0) {
					SLDExternalGraphic myExtGraphic = myLine.getStroke().getGraphic().getExternalGraphics().get(0);
					PictureMarkerSymbol marker = new PictureMarkerSymbol();
					FExpression size = myLine.getStroke().getGraphic().getExpressionSize();
					FExpression rotation = myLine.getStroke().getGraphic().getExpressionRotation();
					marker = createMarker(myExtGraphic, size, rotation);
					if (marker != null) {
						line.setLineWidth(marker.getSize());
						marker.setUnit(-1);
						line.setMarker(marker);
						line.setAlpha(marker.getColor().getAlpha());
					}
					return line;
				}
			}
		}


		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		//POLYGON//
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		else if (symbolizer instanceof SLDPolygonSymbolizer) {
			SLDPolygonSymbolizer myPolygon = (SLDPolygonSymbolizer)symbolizer;
			//SimpleFillSymbol
			if ( myPolygon.getFill() != null && myPolygon.getFill().getFillGraphic() == null) {
				SimpleFillSymbol simpleFill = new SimpleFillSymbol();

				if( myPolygon.getFill().getExpressionColor().getLiteral() != null) {
					Color color = myPolygon.getFill().getFillColor();
					if(myPolygon.getFill().getExpressionOpacity().getLiteral() != null) {
						simpleFill.setHasFill(true);
						int alpha = (int) ((myPolygon.getFill().getFillOpacity()) * 255);
						Color myColor = new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha);
						simpleFill.setFillColor(myColor);
					}
					else
						simpleFill.setFillColor(color);
				}

				//Outline of the symbol
				if(myPolygon.getStroke() != null) {
					simpleFill.setHasOutline(true);
					simpleFill.setOutline(createOutline(myPolygon.getStroke()));
				}
				return simpleFill;
			}
			else if (myPolygon.getFill() != null && myPolygon.getFill().getFillGraphic() != null) {
				SLDGraphic myFill = myPolygon.getFill().getFillGraphic();



				//MarkerFillSymbol
				if(myFill.getMarks().size() > 0 || myFill.getExternalGraphics().size() > 0) {
					MarkerFillSymbol markerFill = new MarkerFillSymbol();
					IMarkerFillPropertiesStyle markerProperties = markerFill.getMarkerFillProperties();
					markerProperties.setFillStyle(markerFill.GRID_FILL);
					markerFill.setMarkerFillProperties(markerProperties);

					FExpression size = myFill.getExpressionSize();
					FExpression rotation = myFill.getExpressionRotation();


					//Outline of the symbol
					if(myPolygon.getStroke() != null) {
						markerFill.setHasOutline(true);
						markerFill.setOutline(createOutline(myPolygon.getStroke()));
					}
					//Marker->SimpleMarkerSymbol
					if(myFill.getMarks().size() > 0) {
						SimpleMarkerSymbol marker = new SimpleMarkerSymbol();
						SLDMark myMark = myFill.getMarks().get(0);
						marker = createMarker(myMark, size, rotation);
						if (marker != null) {
							markerFill.setMarker(marker);
							if(marker.getColor() != null )
								markerFill.setFillColor(marker.getColor());
						}
						return markerFill;
					}
					//Marker->PictureMarkerSymbol
					else if (myFill.getExternalGraphics().size() > 0) {
						PictureMarkerSymbol marker = new PictureMarkerSymbol();
						SLDExternalGraphic myExtGraphic = myFill.getExternalGraphics().get(0);
						marker = createMarker(myExtGraphic, size, rotation);
						if (marker != null) {
							markerFill.setMarker(marker);
							if(marker.getColor() != null)
								markerFill.setFillColor(marker.getColor());
						}
						return markerFill;
					}
				}
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		//POINT//
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		else if (symbolizer instanceof SLDPointSymbolizer) {
			SLDPointSymbolizer myPoint = (SLDPointSymbolizer)symbolizer;
			//Marker->SimpleMarkerSymbol
			if(myPoint.getGraphic().getMarks().size() > 0) {
				SLDMark myMark;
				myMark = myPoint.getGraphic().getMarks().get(0);
				FExpression size = myPoint.getGraphic().getExpressionSize();
				FExpression rotation = myPoint.getGraphic().getExpressionRotation();
				SimpleMarkerSymbol marker = createMarker(myMark,size,rotation);
				marker.setUnit(-1);
				return marker;
			}
			//Marker->PictureMarkerSymbol
			else if (myPoint.getGraphic().getExternalGraphics().size() > 0) {
				SLDExternalGraphic myExtGraphic = myPoint.getGraphic().getExternalGraphics().get(0);
				FExpression size = myPoint.getGraphic().getExpressionSize();
				FExpression rotation = myPoint.getGraphic().getExpressionRotation();
				PictureMarkerSymbol marker = createMarker(myExtGraphic, size, rotation);
				marker.setUnit(-1);
				return marker;
			}
		}
		return null;
	}

	public static SimpleMarkerSymbol createMarker(SLDMark myMark, FExpression size, FExpression rotation) throws NumberFormatException, LegendDriverException {

		SimpleMarkerSymbol marker = new SimpleMarkerSymbol();
		String name = myMark.getWellKnownName().getLiteral();
		marker.setStyle(SLDUtils.setMarkerStyle(name));


		if(myMark.getFill() != null) {
			if( myMark.getFill().getExpressionColor().getLiteral() != null) {
				Color color = myMark.getFill().getFillColor();
				if(myMark.getFill().getExpressionOpacity().getLiteral() != null) {
					int alpha = (int) ((myMark.getFill().getFillOpacity()) * 255);
					Color myColor = new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha);
					marker.setColor(myColor);
				}
				else
					marker.setColor(color);
			}
		}
		if (myMark.getStroke() != null) {
			marker.setOutlined(false);
			if( myMark.getStroke().getExpressionColor().getLiteral() != null &&
					myMark.getStroke().getExpressionWidth().getLiteral() != null ) {
				marker.setOutlineColor(myMark.getStroke().getStrokeColor());
				marker.setOutlineSize(myMark.getStroke().getStrokeWidth());
				marker.setOutlined(true);
			}
		}
		if( size.getLiteral() != null) {
			marker.setSize(Float.valueOf(size.getLiteral()));
		}
		if( rotation.getLiteral() != null)
			marker.setRotation(Float.valueOf(rotation.getLiteral()));

		return marker;
	}



	public static PictureMarkerSymbol createMarker(SLDExternalGraphic myExtGraphic,FExpression size, FExpression rotation) throws LegendDriverException {
		PictureMarkerSymbol marker = new PictureMarkerSymbol() ;

		//We take the first URL by default
		URL url = myExtGraphic.getOnlineResource().get(0);
		try {
			if (url != null) {
				marker = new PictureMarkerSymbol(url,url);
				if( size.getLiteral() != null) {
					marker.setUnit(-1);
					marker.setSize(Float.valueOf(size.getLiteral()));
				}
				if( rotation.getLiteral() != null)
					marker.setRotation(Float.valueOf(rotation.getLiteral()));
			}
		} catch (IOException e1) {
			throw new LegendDriverException(LegendDriverException.READ_DRIVER_EXCEPTION);
		}


		return marker;
	}

	public static ILineSymbol createOutline(SLDStroke stroke) throws NumberFormatException, LegendDriverException {
		SLDLineSymbolizer line ;

		try {
			line = (SLDLineSymbolizer) Class.forName(
					classNameForVersion(SLDLineSymbolizer.class,sldReadVersion )).newInstance();

			line.setStroke(stroke);
			return (ILineSymbol) SLDSymbolizer2ISymbol(line);
		} catch (Exception e) {
			throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_FILE_VERSION);
		}
	}



	public void write(FLayers layers,FLayer layer, ILegend legend, File file, String version)throws LegendDriverException  {

		DecimalFormatSymbols dformater_rules = new DecimalFormatSymbols ();
		dformater_rules.setDecimalSeparator ('.');
		DecimalFormat df = new DecimalFormat("##########.0##########",dformater_rules);

		String sldVersion = version;

		HashMap<String, String> attributes = new HashMap<String, String>();
		attributes.put("xsi:schemaLocation","http://www.opengis.net/sld StyledLayerDescriptor.xsd");
		attributes.put("xmlns","http://www.opengis.net/sld");
		attributes.put("xmlns:ogc","http://www.opengis.net/ogc");
		attributes.put("xmlns:xlink","http://www.w3.org/1999/xlink");
		attributes.put("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
		attributes.put(SLDTags.VERSION_ATTR, sldVersion);


		XmlBuilder xmlBuilder = new XmlBuilder();
		xmlBuilder.setEncoding(DEFAULT_ENCODING);
		xmlBuilder.writeHeader();
		xmlBuilder.openTag(SLDTags.SLD_ROOT, attributes);
		xmlBuilder.openTag(SLDTags.NAMEDLAYER);
		xmlBuilder.writeTag(SLDTags.NAME,layer.getName());
		xmlBuilder.openTag(SLDTags.USERSTYLE);
		xmlBuilder.openTag(SLDTags.FEATURETYPESTYLE);
		try {

			//VectorialIntervalLegend
			if (legend.getClass().equals(VectorialIntervalLegend.class) ) {
				VectorialIntervalLegend myLegend = (VectorialIntervalLegend)legend;
				xmlBuilder.writeTag(SLDTags.FEATURETYPENAME,"Feature");

				ISymbol[] symbols = myLegend.getSymbols();
				if (symbols.length==0){
					NotificationManager.showMessageInfo(PluginServices.getText(this, "not_save_empty_legend"), null);
					return;
				}
				Object[] values = myLegend.getValues();

				NumberFormat nf=NumberFormat.getInstance();

				FInterval interval;
				if(symbols[0] != null && symbols[0] instanceof MultiShapeSymbol)
					throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_CREATION);

				for(int i = 0; i < symbols.length; i++ )
				{
					if (values[i] instanceof NullIntervalValue)
						continue;

					interval = (FInterval)values[i];
					xmlBuilder.openTag(SLDTags.RULE);
					xmlBuilder.writeTag(FilterTags.NAME, ""+interval.getMin() +" - " +interval.getMax());
					xmlBuilder.openTag(FilterTags.FILTER);
					xmlBuilder.openTag(FilterTags.AND);
					xmlBuilder.openTag(FilterTags.PROPERTYISGREATEROREQUALTHAN);
					xmlBuilder.writeTag(FilterTags.PROPERTYNAME,myLegend.getClassifyingFieldNames()[0]);
					double f = (double) interval.getMin();

					xmlBuilder.writeTag(FilterTags.LITERAL, ""+df.format(f));
					xmlBuilder.closeTag();
					xmlBuilder.openTag(FilterTags.PROPERTYISLESSOREQUALTHAN);
					xmlBuilder.writeTag(FilterTags.PROPERTYNAME,myLegend.getClassifyingFieldNames()[0]);
					f= (double)interval.getMax();
					xmlBuilder.writeTag(FilterTags.LITERAL, ""+df.format(f));
					xmlBuilder.closeTag();
					xmlBuilder.closeTag();
					xmlBuilder.closeTag();

					ISLDSymbolizer symSLD = (ISLDSymbolizer)ISymbol2SLDSymbolizer(symbols[i], sldVersion);
					xmlBuilder.writeRaw(symSLD.toXML());

					xmlBuilder.closeTag();
				}
			}
			else if (legend.getClass().equals(VectorialUniqueValueLegend.class)) {

				VectorialUniqueValueLegend myLegend = (VectorialUniqueValueLegend)legend;
				xmlBuilder.writeTag(SLDTags.FEATURETYPENAME,"Feature");
				ISymbol defSym = null;
				ISymbol[] symbols = myLegend.getSymbols();
				if (symbols.length==0){
					NotificationManager.showMessageInfo(PluginServices.getText(this, "not_save_empty_legend"), null);
					return;
				}
				Object[] values = myLegend.getValues();
				Value myVal;

				if(symbols[0] != null && symbols[0] instanceof MultiShapeSymbol)
					throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_CREATION);

				for(int i = 0; i < symbols.length; i++ )
				{


					if (values[i] instanceof NullIntervalValue)
						continue;

					myVal = (Value) values[i];

					if (myVal.toString().compareTo("Default") == 0) {
						defSym = symbols[i];
					}
					else {
						xmlBuilder.openTag(SLDTags.RULE);
						if(myVal instanceof StringValue)
							xmlBuilder.writeTag(FilterTags.NAME,"\""+myVal.toString()+"\"");
						else
							xmlBuilder.writeTag(FilterTags.NAME, ""+df.format(Double.valueOf(myVal.toString())));

						xmlBuilder.openTag(FilterTags.FILTER);

						xmlBuilder.openTag(FilterTags.PROPERTYISEQUALTO);
						xmlBuilder.writeTag(FilterTags.PROPERTYNAME,myLegend.getClassifyingFieldNames()[0]);
						if(myVal instanceof StringValue)
							xmlBuilder.writeTag(FilterTags.LITERAL, ""+"\""+myVal.toString()+"\"");
						else
							xmlBuilder.writeTag(FilterTags.LITERAL, ""+df.format(Double.valueOf(myVal.toString())));

						xmlBuilder.closeTag();
						xmlBuilder.closeTag();

						ISLDSymbolizer symSLD = (ISLDSymbolizer)ISymbol2SLDSymbolizer(symbols[i], sldVersion);
						xmlBuilder.writeRaw(symSLD.toXML());

						xmlBuilder.closeTag();
					}
				}
			}
			else if (legend instanceof VectorFilterExpressionLegend) {
				VectorFilterExpressionLegend myLegend = (VectorFilterExpressionLegend)legend;
				xmlBuilder.writeTag(SLDTags.FEATURETYPENAME,"Feature");

				ISymbol[] symbols = myLegend.getSymbols();
				if (symbols.length==0){
					NotificationManager.showMessageInfo(PluginServices.getText(this, "not_save_empty_legend"), null);
					return;
				}
				Object[] values = myLegend.getValues();
				String[] descriptions = myLegend.getDescriptions();
				for(int i = 0; i < symbols.length; i++ ){
					if (!(values[i] instanceof Expression)){
						throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_CREATION);
					}
					Expression filter4Symbol = (Expression) values[i];
					FilterFactory filterFactory = new FilterFactory();

					xmlBuilder.openTag(SLDTags.RULE);
					xmlBuilder.writeTag(FilterTags.NAME, descriptions[i]);
					xmlBuilder.openTag(FilterTags.FILTER);
					xmlBuilder.writeRaw(filterFactory.createXMLFromExpression(filter4Symbol));
					xmlBuilder.closeTag();

					ISLDSymbolizer symSLD = (ISLDSymbolizer)ISymbol2SLDSymbolizer(symbols[i], sldVersion);
					xmlBuilder.writeRaw(symSLD.toXML());
					xmlBuilder.closeTag();

				}
			}
			//SingleSymbolLegend
			else if (legend instanceof SingleSymbolLegend ) {
				SingleSymbolLegend myLegend = (SingleSymbolLegend)legend;
				xmlBuilder.writeTag(SLDTags.FEATURETYPENAME,"Feature");
				xmlBuilder.openTag(SLDTags.RULE);
				ISLDSymbolizer symSLD = (ISLDSymbolizer)ISymbol2SLDSymbolizer(myLegend.getDefaultSymbol(), sldVersion);
				xmlBuilder.writeRaw(symSLD.toXML());
				xmlBuilder.closeTag();
			}
			else {
				throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_CREATION);
			}
			xmlBuilder.closeTag();
			xmlBuilder.closeTag();
			xmlBuilder.closeTag();
			xmlBuilder.closeTag();

			OutputStream out = new FileOutputStream(file.getAbsolutePath());
			OutputStreamWriter writer = new OutputStreamWriter(out, DEFAULT_ENCODING);
//			FileWriter writer = new FileWriter(file.getAbsolutePath());
			writer.write( xmlBuilder.getXML());
			writer.close();

		} catch (LegendDriverException e) {
			throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_CREATION);
		} catch (IOException e) {
			throw new LegendDriverException(LegendDriverException.SAVE_LEGEND_ERROR);
		} catch (InstantiationException e) {
			throw new LegendDriverException(LegendDriverException.SAVE_LEGEND_ERROR);
		} catch (IllegalAccessException e) {
			throw new LegendDriverException(LegendDriverException.SAVE_LEGEND_ERROR);
		} catch (ClassNotFoundException e) {
			throw new LegendDriverException(LegendDriverException.SAVE_LEGEND_ERROR);
		}
	}

	private static String classNameForVersion(Class sldClass, String sldVersion) {
		String versionString = sldVersion.replaceAll("\\.", "_");
//		String packageName = sldClass.getPackage().getName();
//		String packageName = "org.gvsig.remoteClient.sld.";
//		String subPackage = packageName.substring(packageName.lastIndexOf(".")+1, packageName.length());
//		packageName = packageName.substring(0, packageName.lastIndexOf('.')+1);
//		String cName = packageName + "sld" + versionString + "." +
//		subPackage + sldClass.getName().substring(
//				sldClass.getName().lastIndexOf(".", sldClass.getName().length())) + versionString;

		String path = sldClass.toString();
		path = sldClass.toString().substring("class ".length(),path.length());
		path = path.replace(".sld.", ".sld.sld"+versionString+".");
		String cName = path+versionString;

		return cName;
	}
	/**
	 * Transforms an ISymbol into a SLD Symbolizer
	 *
	 * @param symbol ISymbol to be transformed
	 * @return SLD Symbolizer
	 * @throws LegendDriverException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private ISLDSymbolizer ISymbol2SLDSymbolizer(ISymbol symbol, String sldVersion)
	throws LegendDriverException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//MultiLayer Symbol
		if(symbol instanceof IMultiLayerSymbol) {
			//Made up of  lines
			if(symbol instanceof MultiLayerLineSymbol) {
				MultiLayerLineSymbol multiLine = (MultiLayerLineSymbol) symbol;
				SLDMultiLineSymbolizer multiSldLine =
					(SLDMultiLineSymbolizer) Class.forName(
							classNameForVersion(SLDMultiLineSymbolizer.class, sldVersion)).newInstance();
				for (int i = 0; i < multiLine.getLayerCount(); i++) {
					multiSldLine.addSldLine((SLDLineSymbolizer)ISymbol2SLDSymbolizer(multiLine.getLayer(i), sldVersion));
				}
				return (ISLDSymbolizer) multiSldLine;
			}
			//Made up of polygons
			else if (symbol instanceof MultiLayerFillSymbol) {
				MultiLayerFillSymbol multiFill = (MultiLayerFillSymbol) symbol;
				SLDMultiPolygonSymbolizer multiSldPolygon =
					(SLDMultiPolygonSymbolizer) Class.forName(
							classNameForVersion(SLDMultiPolygonSymbolizer.class, sldVersion)).newInstance();
				for (int i = 0; i < multiFill.getLayerCount(); i++) {
					multiSldPolygon.addSldPolygon((SLDPolygonSymbolizer)ISymbol2SLDSymbolizer(multiFill.getLayer(i), sldVersion));
				}
				return (ISLDSymbolizer) multiSldPolygon;
			}
			//Made up of markers
			else if (symbol instanceof MultiLayerMarkerSymbol) {
				MultiLayerMarkerSymbol multiMarker = (MultiLayerMarkerSymbol) symbol;
				SLDMultiPointSymbolizer multiSldPoint = (SLDMultiPointSymbolizer) Class.forName(
						classNameForVersion(SLDMultiPointSymbolizer.class, sldVersion)).newInstance();
				for (int i = 0; i < multiMarker.getLayerCount(); i++) {
					multiSldPoint.addSldPoint((SLDPointSymbolizer)ISymbol2SLDSymbolizer(multiMarker.getLayer(i), sldVersion));
				}
				return (ISLDSymbolizer) multiSldPoint;
			}
		}
//		MultishapeSymbol not yet supported
//		else if(symbol instanceof MultiShapeSymbol) {
//		MultiShapeSymbol multiShape = (MultiShapeSymbol) symbol;
//		SLDMultiShapeSymbolizer sldMultiShape = new SLDMultiShapeSymbolizer();

//		sldMultiShape.addSldLine((SLDLineSymbolizer)ISymbol2SLDSymbolizer(multiShape.getLineSymbol()));
//		sldMultiShape.addSldPolygon((SLDPolygonSymbolizer)ISymbol2SLDSymbolizer(multiShape.getFillSymbol()));
//		sldMultiShape.addSldPoint((SLDPointSymbolizer)ISymbol2SLDSymbolizer(multiShape.getMarkerSymbol()));

//		return sldMultiShape;
//		}
		//Single symbol
		else {
			//Made up of a line
			if (symbol instanceof ILineSymbol) {
				ILineSymbol myLine = (ILineSymbol)symbol;
				SLDLineSymbolizer mySldLine = (SLDLineSymbolizer) Class.forName(
						classNameForVersion(SLDLineSymbolizer.class, sldVersion)).newInstance();
				if (myLine.getLineStyle() != null)
					mySldLine.setStroke(createStroke(myLine, sldVersion));
				return mySldLine;
			}
			//Made up of a polygon
			else if (symbol instanceof IFillSymbol) {
				IFillSymbol myFill = (IFillSymbol)symbol;
				SLDPolygonSymbolizer mySldPolygon =(SLDPolygonSymbolizer) Class.forName(
						classNameForVersion(SLDPolygonSymbolizer.class, sldVersion)).newInstance();
				SLDFill fill = (SLDFill) Class.forName(
						classNameForVersion(SLDFill.class, sldVersion)).newInstance();
				SLDGraphic graphic = (SLDGraphic) Class.forName(
						classNameForVersion(SLDGraphic.class, sldVersion)).newInstance();

				if (myFill.getOutline() != null)
					mySldPolygon.setStroke(createStroke(myFill.getOutline(), sldVersion));

				if (myFill instanceof MarkerFillSymbol) {
					MarkerFillSymbol markerFill = (MarkerFillSymbol) symbol;
					fill.setFillGraphic(createMarkerFill(markerFill.getMarker(), graphic, sldVersion));
				}
				else if (myFill instanceof PictureFillSymbol) {
					PictureFillSymbol picFill = (PictureFillSymbol) symbol;
					graphic.addExternalGraphic(createPictureFill(picFill.getImagePath(), sldVersion));
					fill.setFillGraphic(graphic);
				}
				else if (!(myFill instanceof MarkerFillSymbol) && !(myFill instanceof PictureFillSymbol)){
					if (myFill.getFillColor() != null && myFill.hasFill()){

						FExpression expressionColor = new FExpression();
						expressionColor.setLiteral(SLDUtils.convertColorToHexString(myFill.getFillColor()));
						fill.setExpressionColor(expressionColor);

						FExpression expressionOpacity = new FExpression();
						float opacity =(float ) myFill.getFillColor().getAlpha() / (float) 255;
						expressionOpacity.setLiteral(String.valueOf(opacity));
						fill.setExpressionOpacity(expressionOpacity);

					}
					else if (myFill.getFillColor() != null && !myFill.hasFill()){

						FExpression expressionColor = new FExpression();
						expressionColor.setLiteral(SLDUtils.convertColorToHexString(Color.white));
						fill.setExpressionColor(expressionColor);

						FExpression expressionOpacity = new FExpression();
						expressionOpacity.setLiteral(String.valueOf(255));
						fill.setExpressionOpacity(expressionOpacity);
					}
				}

				mySldPolygon.setFill(fill);

				return mySldPolygon;
			}
			//Made up of a marker
			else if (symbol instanceof IMarkerSymbol) {
				IMarkerSymbol myMarker = (IMarkerSymbol)symbol;
				SLDPointSymbolizer mySldPoint = (SLDPointSymbolizer) Class.forName(
						classNameForVersion(SLDPointSymbolizer.class, sldVersion)).newInstance();
				mySldPoint.setGraphic(createGraphic(myMarker, sldVersion));
				return mySldPoint;
			}
		}
		throw new LegendDriverException(LegendDriverException.UNSUPPORTED_LEGEND_CREATION);
	}

	private SLDGraphic createMarkerFill(IMarkerSymbol markerFill, SLDGraphic graphic, String sldVersion)
	throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SLDGraphic myGraphic = (SLDGraphic) Class.forName(
				classNameForVersion(SLDGraphic.class, sldVersion)).newInstance();
		SLDFill fill = (SLDFill) Class.forName(
				classNameForVersion(SLDFill.class, sldVersion)).newInstance();
		boolean hasOpacity = false;
		float opacity = 0 ;

		myGraphic = graphic;

		if (markerFill.getColor() != null) {

			FExpression expressionColor = new FExpression();
			expressionColor.setLiteral(SLDUtils.convertColorToHexString(markerFill.getColor()));
			fill.setExpressionColor(expressionColor);

			FExpression expressionOpacity = new FExpression();
			opacity =(float ) markerFill.getColor().getAlpha() / (float) 255;
			expressionOpacity.setLiteral(String.valueOf(opacity));

			hasOpacity = true;
		}
		if(markerFill instanceof MultiLayerMarkerSymbol) {
			MultiLayerMarkerSymbol multi = (MultiLayerMarkerSymbol)markerFill;
			IMarkerSymbol marker = null;
			double max = 0;
			for (int i = 0; i < multi.getLayerCount(); i++) {
				if (max < ((IMarkerSymbol)multi.getLayer(i)).getSize()) {
					marker =  (IMarkerSymbol) multi.getLayer(i);
					max = ((IMarkerSymbol)multi.getLayer(i)).getSize();
				}
			}
			myGraphic = createMarkerFill(marker, myGraphic, sldVersion);
		}
		else if (markerFill instanceof SimpleMarkerSymbol) {
			SimpleMarkerSymbol myMarker = (SimpleMarkerSymbol) markerFill;
			SLDMark mark = (SLDMark) Class.forName(
					classNameForVersion(SLDMark.class, sldVersion)).newInstance();
			FExpression wellKnownName = new FExpression();
			wellKnownName.setLiteral(SLDUtils.getMarkWellKnownName(myMarker.getStyle()));
			mark.setWellKnownName(wellKnownName);
			mark.setFill(fill);
			myGraphic.addMark(mark);
		}
		else if (markerFill instanceof PictureMarkerSymbol) {
			PictureMarkerSymbol myMarkerSymbol = (PictureMarkerSymbol) markerFill;
			myGraphic.addExternalGraphic(createPictureFill(myMarkerSymbol.getImagePath(), sldVersion));
		}
		else if (markerFill instanceof CharacterMarkerSymbol) {
			SLDMark mark = (SLDMark) Class.forName(
					classNameForVersion(SLDMark.class, sldVersion)).newInstance();
			FExpression wellKnownName = new FExpression();
			wellKnownName.setLiteral(SLDUtils.getMarkWellKnownName(SimpleMarkerSymbol.SQUARE_STYLE));
			mark.setWellKnownName(wellKnownName);
			mark.setFill(fill);
			myGraphic.addMark(mark);
		}

		FExpression expressionSize = new FExpression();
		expressionSize.setLiteral(String.valueOf(markerFill.getSize()));
		myGraphic.setExpressionSize(expressionSize);

		if(hasOpacity) {

			FExpression expressionOpacity = new FExpression();
			expressionOpacity.setLiteral(String.valueOf(opacity));
			myGraphic.setExpressionOpacity(expressionOpacity);

		}
		return myGraphic;
	}



	private SLDExternalGraphic createPictureFill(String imagePath, String sldVersion)
	throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SLDExternalGraphic extGra = (SLDExternalGraphic) Class.forName(
				classNameForVersion(SLDExternalGraphic.class, sldVersion)).newInstance();
		try {
			if (imagePath != null) {
				extGra.addOnlineResource(new URL(imagePath));
				extGra.setFormat("image/"+imagePath.substring(imagePath.lastIndexOf(".")+1,imagePath.length()));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return extGra;
	}

	private SLDStroke createStroke(ILineSymbol myLine, String sldVersion)
	throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SLDStroke stroke = (SLDStroke) Class.forName(
				classNameForVersion(SLDStroke.class, sldVersion)).newInstance();

		SLDLineSymbolizer mySldLine = (SLDLineSymbolizer) Class.forName(
				classNameForVersion(SLDLineSymbolizer.class, sldVersion)).newInstance();
		//If the outline symbol is a multilayer symbol the line is made up of the first
		//layer of the multilayer symbol
		if (myLine instanceof MultiLayerLineSymbol) {
			MultiLayerLineSymbol multiLine = (MultiLayerLineSymbol) myLine;
			for (int i = 0; i < multiLine.getLayerCount(); i++) {
				ILineSymbol myNewLine =  (ILineSymbol) multiLine.getLayer(i);
				if (myLine.getLineWidth() < myNewLine.getLineWidth())
					myLine = myNewLine;
			}
			myLine = (ILineSymbol) multiLine.getLayer(0);
		}

		BasicStroke str= (BasicStroke) myLine.getLineStyle().getStroke();

		if (myLine.getColor() != null) {
			stroke.setExpressionColor(SLDUtils.convertColorToHexString(myLine.getColor()));
			float opacity =(float ) myLine.getColor().getAlpha() / (float) 255;
			stroke.setExpressionOpacity(Double.toString(opacity));
		}
		if(str != null) {
			stroke.setFloatDashArray(str.getDashArray());
			stroke.setExpressionLineCap(SLDUtils.convertLineCapToString(str.getEndCap()));
			stroke.setExpressionLineJoin(SLDUtils.convertLineJoinToString(str.getLineJoin()));
			stroke.setExpressionWidth(Double.toString(myLine.getLineWidth()));
			stroke.setExpressionDashOffset(Double.toString(str.getDashPhase()));
		}
		if(myLine instanceof MarkerLineSymbol) {
			MarkerLineSymbol myMarkerLine = (MarkerLineSymbol) myLine;
			float[] dash =  {Float.valueOf(String.valueOf(myLine.getLineWidth())),
					Float.valueOf(String.valueOf(myMarkerLine.getSeparation()))};
			stroke.setFloatDashArray(dash);
			stroke.setGraphic(createGraphic(myMarkerLine.getMarker(), sldVersion));
		}
		else if (myLine instanceof PictureLineSymbol) {
			PictureLineSymbol picLine = (PictureLineSymbol)myLine;
			SLDGraphic graphic = (SLDGraphic) Class.forName(
					classNameForVersion(SLDGraphic.class, sldVersion)).newInstance();
			graphic.addExternalGraphic(createPictureFill(picLine.getImagePath(), sldVersion));
			stroke.setGraphic(graphic);
		}

		return stroke;
	}



	private SLDGraphic createGraphic(IMarkerSymbol myMarker, String sldVersion)
	throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		SLDGraphic graphic = (SLDGraphic) Class.forName(
				classNameForVersion(SLDGraphic.class, sldVersion)).newInstance();

		graphic.getExpressionSize().setLiteral(String.valueOf(myMarker.getSize()));
		graphic.getExpressionRotation().setLiteral(String.valueOf(myMarker.getRotation()));
		graphic.getExpressionOpacity().setLiteral(String.valueOf(myMarker.getColor().getAlpha()/255));

		if (myMarker instanceof SimpleMarkerSymbol) {
			SimpleMarkerSymbol marker = (SimpleMarkerSymbol) myMarker;
			SLDStroke str = (SLDStroke) Class.forName(
					classNameForVersion(SLDStroke.class, sldVersion)).newInstance();
			SLDMark mark = (SLDMark) Class.forName(
					classNameForVersion(SLDMark.class, sldVersion)).newInstance();

			if (marker.getOutlineColor() != null)
				str.setExpressionColor(SLDUtils.convertColorToHexString(marker.getOutlineColor()));

			str.setExpressionWidth(String.valueOf(marker.getOutlineSize()));
			mark.setStroke(str);
			if (marker.getColor() != null) {

				SLDFill fill = (SLDFill) Class.forName(
						classNameForVersion(SLDFill.class, sldVersion)).newInstance();


				FExpression expressionColor = new FExpression();
				expressionColor.setLiteral(SLDUtils.convertColorToHexString(marker.getColor()));
				fill.setExpressionColor(expressionColor);

				FExpression expressionOpacity = new FExpression();
				float opacity =(float ) marker.getColor().getAlpha() / (float) 255;
				expressionOpacity.setLiteral(String.valueOf(opacity));
				fill.setExpressionOpacity(expressionOpacity);

				mark.setFill(fill);

			}
			FExpression wellKnownName = new FExpression();
			wellKnownName.setLiteral(SLDUtils.getMarkWellKnownName(marker.getStyle()));
			mark.setWellKnownName(wellKnownName);
			graphic.addMark(mark);
		}
		else if (myMarker instanceof PictureMarkerSymbol) {
			PictureMarkerSymbol marker =  (PictureMarkerSymbol)myMarker;
			SLDExternalGraphic ext = (SLDExternalGraphic) Class.forName(
					classNameForVersion(SLDExternalGraphic.class, sldVersion)).newInstance();
			try {
				if (marker.getImagePath() != null)
					ext.addOnlineResource(new URL(marker.getImagePath()));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			graphic.addExternalGraphic(ext);
		}
		else if (myMarker instanceof CharacterMarkerSymbol) {
			SLDMark mark = (SLDMark) Class.forName(
					classNameForVersion(SLDMark.class, sldVersion)).newInstance();
			FExpression wellKnownName = new FExpression();
			wellKnownName.setLiteral(SLDUtils.getMarkWellKnownName(SimpleMarkerSymbol.SQUARE_STYLE));
			mark.setWellKnownName(wellKnownName);

			if (myMarker.getColor() != null) {
				SLDFill fill = (SLDFill) Class.forName(
						classNameForVersion(SLDFill.class, sldVersion)).newInstance();

				FExpression expressionColor = new FExpression();
				expressionColor.setLiteral(SLDUtils.convertColorToHexString(myMarker.getColor()));
				fill.setExpressionColor(expressionColor);

				FExpression expressionOpacity = new FExpression();
				float opacity =(float ) myMarker.getColor().getAlpha() / (float) 255;
				expressionOpacity.setLiteral(String.valueOf(opacity));
				fill.setExpressionOpacity(expressionOpacity);

				mark.setFill(fill);
			}
			graphic.addMark(mark);
		}

		return graphic;
	}

	public ArrayList<String> getSupportedVersions() {
		return SLDProtocolHandlerFactory.getSupportedVersions();
	}

}

