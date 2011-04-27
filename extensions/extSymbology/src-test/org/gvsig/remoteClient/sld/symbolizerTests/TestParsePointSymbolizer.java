package org.gvsig.remoteClient.sld.symbolizerTests;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.gvsig.remoteClient.sld.SLDProtocolHandler;
import org.gvsig.remoteClient.sld.SLDProtocolHandlerFactory;
import org.gvsig.remoteClient.sld.UnsupportedSLDVersionException;
import org.xmlpull.v1.XmlPullParserException;

import com.iver.cit.gvsig.fmap.drivers.legend.LegendDriverException;


public class TestParsePointSymbolizer extends TestCase {
	SLDProtocolHandler my_sld = null;
	File f;
	
	public void setUp() {
		f = new File("src-test/test-data/sld-sample-files/points/pointSymbolizer.sld" );
	}

	public void testParsing() {
		long t1 = System.currentTimeMillis();
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
		System.out.println("Test parsing done with apparently no errors in "+ (t2-(float)t1)+" nano-seconds");
	}

}
