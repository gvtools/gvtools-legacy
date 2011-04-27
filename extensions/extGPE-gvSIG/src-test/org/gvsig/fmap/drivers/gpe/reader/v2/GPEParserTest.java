package org.gvsig.fmap.drivers.gpe.reader.v2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.drivers.gpe.reader.GMLVectorialDriver;
import org.gvsig.gpe.GPERegister;
import org.gvsig.gpe.exceptions.ParserCreationException;
import org.gvsig.gpe.parser.GPEParser;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

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
/* CVS MESSAGES:
 *
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public abstract class GPEParserTest extends TestCase {
	private String gpeDriversFile = "config" + File.separatorChar +
			"parser.properties";
	private MapControl mapControl = null;
	private GMLVectorialDriver driver = null;
	private FLyrVect layer = null;
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws FileNotFoundException, IOException{
//		LayerFactory.setDriversPath("../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
//		LayerFactory.setWritersPath("../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
//		System.out.println("****************READERS****************");
//		for (int i=0 ; i<LayerFactory.getDM().getDriverNames().length ; i++){
//			System.out.println(LayerFactory.getDM().getDriverNames()[i]);
//		}
//		System.out.println("****************WRITERS****************");
//		for (int i=0 ; i<LayerFactory.getWM().getWriterNames().length ; i++){
//			System.out.println(LayerFactory.getWM().getWriterNames()[i]);
//		}
//		System.out.println("*************** GPE register *************");
		GPERegister.addParsersFile(new File(gpeDriversFile));
		driver = new GMLVectorialDriver();
		driver.open(new File(getFile()));
	}
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown(){
		for (int i=0 ; i<getLayer().getNumErrors() ; i++){
			System.out.println(getLayer().getError(i));
		}
		assertEquals(getLayer().getNumErrors(),0);
		
	}
	
	/**
	 * The test
	 * @throws Exception
	 */
	public void testParse() throws Exception{
		IProjection proj = CRSFactory.getCRS("EPSG:23030");
		ViewPort vp = new ViewPort(proj);
		MapContext mapContext = new MapContext(vp);
		mapControl = new MapControl();
		mapControl.setMapContext(mapContext);
		makeAsserts();
	}
	
	/**
	 * @return The GPE parser
	 * @throws GPEParserCreationException
	 */	
	protected GPEParser getParser() throws ParserCreationException{
		return GPERegister.createParser("GML");
	}
	
	/**
	 * @return the file to load
	 */
	public abstract String getFile();

	/**
	 * Make the asserts
	 * @throws Exception 
	 */
	public abstract void makeAsserts() throws Exception;

	/**
	 * @return the MapControl
	 */
	public MapControl getMapControl() {
		return mapControl;
	}
	
	/**
	 * @return the parent layer
	 */
	public FLyrVect getLayer(){
		if (layer == null){
			layer = (FLyrVect)LayerFactory.createLayer("Test", driver, CRSFactory.getCRS("EPSG:23030"));
		}
		return layer;
	}

}
