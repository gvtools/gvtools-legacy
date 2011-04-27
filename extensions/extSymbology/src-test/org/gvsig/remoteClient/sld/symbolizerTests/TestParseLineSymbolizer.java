package org.gvsig.remoteClient.sld.symbolizerTests;
import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.gvsig.remoteClient.sld.SLDProtocolHandler;
import org.gvsig.remoteClient.sld.SLDProtocolHandlerFactory;
import org.gvsig.remoteClient.sld.SLDUtils;
import org.gvsig.remoteClient.sld.UnsupportedSLDVersionException;
import org.gvsig.remoteClient.sld.layers.ISLDLayer;
import org.gvsig.remoteClient.sld.symbolizers.ISLDSymbolizer;
import org.gvsig.symbology.fmap.drivers.sld.FMapSLDDriver;
import org.gvsig.symbology.fmap.symbols.MarkerLineSymbol;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerLineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;


public class TestParseLineSymbolizer extends TestCase {
	SLDProtocolHandler my_sld = null;	
	File f,f2,f3,f4,f5;

	public void setUp() {
		f  = new File("src-test/test-data/sld-sample-files/lines/simpleLine.sld" );
		f2 = new File("src-test/test-data/sld-sample-files/lines/line_without_ogc.sld" );
		f3 = new File("src-test/test-data/sld-sample-files/lines/markerLine.sld" );
		f5 = new File("src-test/test-data/sld-sample-files/lines/multiLine.sld" );
	}
	/**
	 * Tests the parsing of an sld document where is included the prefix ogc: inside 
	 * the filter expressions 
	 */
	public void testParsingSimpleLine() {
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
			fail();
		} catch (IOException e2) {
			fail();
		} catch (XmlPullParserException e3) {
			fail();
		} catch (UnsupportedSLDVersionException e4) {
			fail();
		}
		long t2 = System.nanoTime();
		System.out.println("Test done with apparently no errors in "+ (t2-(float)t1)/1000+" nano-seconds");
	}

	public void testParsingAndCreation() {
		long t1 = System.nanoTime();
		try {
			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(f3);		
			((SLDProtocolHandler) my_sld).parse(f3);

			ArrayList<ISLDLayer> sldLayers = new ArrayList<ISLDLayer>();
			sldLayers = my_sld.getLayers();
			ArrayList<ISLDSymbolizer> symbolizers;
			symbolizers = sldLayers.get(0).getSymbolizersByShapeType(FShape.LINE);
			IMultiLayerSymbol multiLayerSymbol = SymbologyFactory.createEmptyMultiLayerSymbol(FShape.LINE);

			if (multiLayerSymbol != null) {
				for (int i = 0; i < symbolizers.size(); i++) {
					ISymbol sym = null;
					sym = FMapSLDDriver.SLDSymbolizer2ISymbol(symbolizers.get(i));

					multiLayerSymbol.addLayer(sym);
				}
				ISymbol defaultSym = (multiLayerSymbol.getLayerCount() > 1) ?
						multiLayerSymbol :
							multiLayerSymbol.getLayer(0);
				
				MarkerLineSymbol markerLine = (MarkerLineSymbol)defaultSym;
				
				if (markerLine.getLineWidth() != 6)
					fail();
				if (SLDUtils.convertLineCapToString(((BasicStroke)markerLine.getLineStyle().getStroke()).getEndCap()).compareTo("butt") != 0)
					fail();
				if (SLDUtils.convertLineJoinToString(((BasicStroke)markerLine.getLineStyle().getStroke()).getLineJoin()).compareTo("bevel") != 0)
					fail();
				if(!((SimpleMarkerSymbol)markerLine.getMarker()).getColor().equals(SLDUtils.convertHexStringToColor("#808080")))
					fail();	
				if(!((SimpleMarkerSymbol)markerLine.getMarker()).getOutlineColor().equals(SLDUtils.convertHexStringToColor("#FF0000")))
					fail();
				
			}
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
	
	public void testParsingMultiLine() {
		long t1 = System.nanoTime();
		try {
			my_sld = SLDProtocolHandlerFactory.createVersionedProtocolHandler(f5);		
			((SLDProtocolHandler) my_sld).parse(f5);

			ArrayList<ISLDLayer> sldLayers = new ArrayList<ISLDLayer>();
			sldLayers = my_sld.getLayers();
			ArrayList<ISLDSymbolizer> symbolizers;
			symbolizers = sldLayers.get(0).getSymbolizersByShapeType(FShape.LINE);
			IMultiLayerSymbol multiLayerSymbol = SymbologyFactory.createEmptyMultiLayerSymbol(FShape.LINE);

			if (multiLayerSymbol != null) {
				for (int i = 0; i < symbolizers.size(); i++) {
					ISymbol sym = null;
					sym = FMapSLDDriver.SLDSymbolizer2ISymbol(symbolizers.get(i));

					multiLayerSymbol.addLayer(sym);
				}
				ISymbol defaultSym = (multiLayerSymbol.getLayerCount() > 1) ?
						multiLayerSymbol :
							multiLayerSymbol.getLayer(0);
				
				MultiLayerLineSymbol multiLine = (MultiLayerLineSymbol) defaultSym;
				
				if(multiLine.getLayerCount() != 2) 
					fail();
			}
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
	

}