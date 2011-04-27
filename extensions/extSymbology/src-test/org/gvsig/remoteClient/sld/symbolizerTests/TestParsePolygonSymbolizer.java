package org.gvsig.remoteClient.sld.symbolizerTests;
import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.gvsig.remoteClient.sld.SLDProtocolHandler;
import org.gvsig.remoteClient.sld.SLDProtocolHandlerFactory;
import org.gvsig.remoteClient.sld.SLDUtils;
import org.gvsig.remoteClient.sld.UnsupportedSLDVersionException;
import org.gvsig.remoteClient.sld.layers.ISLDLayer;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;
import org.gvsig.symbology.fmap.drivers.sld.FMapSLDDriver;
import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;
import org.gvsig.symbology.fmap.symbols.PictureMarkerSymbol;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;


public class TestParsePolygonSymbolizer extends TestCase {
	SLDProtocolHandler my_sld = null;	
	File f,f2,f3,f4;

	public void setUp() {
		f = new File("src-test/test-data/sld-sample-files/polygons/polygonSymbolizer.sld" );
		f2 = new File("src-test/test-data/sld-sample-files/polygons/polygonSymbolizerWithoutOgcPrefix.sld" );
		f3 = new File("src-test/test-data/sld-sample-files/polygons/SimpleFill.sld" );
		f4 = new File("src-test/test-data/sld-sample-files/polygons/PictureFill.sld" );
	}
	/**
	 * Tests the parsing of an sld document where is included the prefix ogc: inside 
	 * the filter expressions 
	 */
	public void testParsing() {
		long t1 = System.nanoTime();
		try {
			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(f);		
			((SLDProtocolHandler) my_sld).parse(f);

		} catch (LegendDriverException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedSLDVersionException e) {
			e.printStackTrace();
			fail();
		}
		long t2 = System.nanoTime();
		System.out.println("Test done with apparently no errors in "+ (t2-(float)t1)/1000+" nano-seconds");
	}

	/**
	 * Tests the parsing of an sld document where is not included the prefix ogc: inside 
	 * the filter expressions 
	 */
	public void testParsingWithoutOgcPrefix() {
		long t1 = System.nanoTime();
		try {
			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(f2);		
			((SLDProtocolHandler) my_sld).parse(f2);

		} catch (LegendDriverException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedSLDVersionException e) {
			e.printStackTrace();
			fail();
		}
		long t2 = System.nanoTime();
		System.out.println("Test done with apparently no errors in "+ (t2-(float)t1)/1000+" nano-seconds");
	}

	/**
	 * Tests the parsing of an sld document and checks that the symbol that
	 * is specified inside the sld is the same symbol that is created after that.
	 */
	public void testParsingAndCreation() {

		long t1 = System.nanoTime();
		try {
			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(f3);		
			((SLDProtocolHandler) my_sld).parse(f3);

		} catch (LegendDriverException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedSLDVersionException e) {
			e.printStackTrace();
			fail();
		}


		ArrayList<ISLDLayer> sldLayers = new ArrayList<ISLDLayer>();
		sldLayers = my_sld.getLayers();
		ArrayList<ISLDSymbolizer> symbolizers;
		symbolizers = sldLayers.get(0).getSymbolizersByShapeType(FShape.POLYGON);
		IMultiLayerSymbol multiLayerSymbol = SymbologyFactory.createEmptyMultiLayerSymbol(FShape.POLYGON);

		if (multiLayerSymbol != null) {
			for (int i = 0; i < symbolizers.size(); i++) {
				ISymbol sym = null;
				try {
					sym = FMapSLDDriver.SLDSymbolizer2ISymbol(symbolizers.get(i));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					fail();
				} catch (LegendDriverException e) {
					e.printStackTrace();
					fail();
				}
				multiLayerSymbol.addLayer(sym);
			}
			ISymbol defaultSym = (multiLayerSymbol.getLayerCount() > 1) ?
					multiLayerSymbol :
						multiLayerSymbol.getLayer(0);

			SimpleFillSymbol simpleFill =  (SimpleFillSymbol)defaultSym;
			SimpleLineSymbol simpleLine = (SimpleLineSymbol) simpleFill.getOutline();

			try {
				if (!(simpleFill.getFillColor().equals(SLDUtils.convertHexStringToColor("#FFFFFF"))))
					fail();
				else if (! (simpleFill.getFillColor().getAlpha() == 255))
					fail();
				else if (! (simpleLine.getLineWidth() == 2))
					fail();
				else if (! (simpleLine.getColor().equals(SLDUtils.convertHexStringToColor("#000000"))))
					fail();
				else if (SLDUtils.convertLineCapToString(((BasicStroke)simpleLine.getLineStyle().getStroke()).getEndCap()).compareTo("butt") !=0 )
					fail();
				else if (SLDUtils.convertLineJoinToString(((BasicStroke)simpleLine.getLineStyle().getStroke()).getLineJoin()).compareTo("miter") !=0 )
					fail();
			} catch (NumberFormatException e) {
				e.printStackTrace();
				fail();
			} catch (LegendDriverException e) {
				e.printStackTrace();
				fail();
			}
		}

		long t2 = System.nanoTime();
		System.out.println("Test done with apparently no errors in "+ (t2-(float)t1)/1000+" nano-seconds");

	}

	public void testParsingAndCreation2() {

		long t1 = System.nanoTime();
		try {
			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(f4);		
			((SLDProtocolHandler) my_sld).parse(f4);

		} catch (LegendDriverException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedSLDVersionException e) {
			e.printStackTrace();
			fail();
		}


		ArrayList<ISLDLayer> sldLayers = new ArrayList<ISLDLayer>();
		sldLayers = my_sld.getLayers();
		ArrayList<ISLDSymbolizer> symbolizers;
		symbolizers = sldLayers.get(0).getSymbolizersByShapeType(FShape.POLYGON);
		IMultiLayerSymbol multiLayerSymbol = SymbologyFactory.createEmptyMultiLayerSymbol(FShape.POLYGON);

		if (multiLayerSymbol != null) {
			for (int i = 0; i < symbolizers.size(); i++) {
				ISymbol sym = null;
				try {
					sym = FMapSLDDriver.SLDSymbolizer2ISymbol(symbolizers.get(i));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					fail();
				} catch (LegendDriverException e) {
					e.printStackTrace();
					fail();
				}
				multiLayerSymbol.addLayer(sym);
			}
			ISymbol defaultSym = (multiLayerSymbol.getLayerCount() > 1) ?
					multiLayerSymbol :
						multiLayerSymbol.getLayer(0);

			MarkerFillSymbol markerFill =  (MarkerFillSymbol)defaultSym;
			SimpleLineSymbol simpleLine = (SimpleLineSymbol) markerFill.getOutline();
			URL myURL = null;
			try {
				myURL = new URL("http://maps.massgis.state.ma.us/images/question_mark.gif");
			} catch (MalformedURLException e) {
				e.printStackTrace();
				fail();
			}


			Color outlineColor = null;
			try {
				outlineColor = SLDUtils.convertHexStringToColor("#000000");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (LegendDriverException e) {
				e.printStackTrace();
				fail();
			}
			outlineColor = new Color(outlineColor.getRed(),outlineColor.getGreen(),outlineColor.getBlue(),(int)(0.2*255));

			if (!(markerFill.getMarker().getRotation() == 0.5))
				fail();
			else if (!(markerFill.getMarker().getSize() == 30))
				fail();
			else if ((((PictureMarkerSymbol) markerFill.getMarker()).getImagePath().compareTo(myURL.toString()))!= 0)
				fail();
			else if (! (simpleLine.getLineWidth() == 1))
				fail();
			else if (!(simpleLine.getColor().equals(outlineColor)))
				fail();
			else if (!(simpleLine.getColor().getAlpha() == (0.2*255)))
				fail();
			else if (SLDUtils.convertLineCapToString(((BasicStroke)simpleLine.getLineStyle().getStroke()).getEndCap()).compareTo("butt") !=0 )
				fail();
			else if (SLDUtils.convertLineJoinToString(((BasicStroke)simpleLine.getLineStyle().getStroke()).getLineJoin()).compareTo("miter") !=0 )
				fail();
		}

		long t2 = System.nanoTime();
		System.out.println("Test done with apparently no errors in "+ (t2-(float)t1)/1000+" nano-seconds");

	}
}

