package org.gvsig.fmap.drivers.gpe.writer.v2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.fmap.drivers.gpe.writer.ExportTask;
import org.gvsig.gpe.GPERegister;
import org.gvsig.gpe.exceptions.WriterHandlerCreationException;
import org.gvsig.gpe.exceptions.WriterHandlerNotRegisteredException;
import org.gvsig.gpe.parser.GPEErrorHandler;
import org.gvsig.gpe.parser.GPEErrorHandlerTest;
import org.gvsig.gpe.writer.GPEWriterHandler;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayer;

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
public abstract class GPEWriterTest extends TestCase {
	private String gpeWritersFile = "config" + File.separatorChar +
	"writer.properties";
	private String gpeParsersFile = "config" + File.separatorChar +
	"parser.properties";
	private MapContext mapContext = null;
	private GPEWriterHandler writerHandler = null;
	private GPEErrorHandler errorHandler = null;
	private String fileName = "testdata" + File.separatorChar + "output";
			
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp() throws FileNotFoundException, IOException, WriterHandlerNotRegisteredException{
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
		System.out.println("*************** GPE register *************");
		GPERegister.addParsersFile(new File(gpeParsersFile));
		GPERegister.addWritersFile(new File(gpeWritersFile));
		//Register the writer that is going to be used
		GPERegister.addGpeWriterHandler(getGPEWriterHandlerName(),
				getGPEWriterHandlerDescription(),
				getGPEWriterHandlerClass());
	}
	
	public void testWrite() throws Exception{
		FLayer layer = getLayerToWrite();				
		ExportTask task = new ExportTask(getLayerToWrite(),
				getWriterHandler(),
				getMapContext(),
				new File(fileName));
		task.run();
	}
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown(){
		GPEErrorHandler error = getErrorHandler();
		for (int i=0 ; i<error.getErrorsSize() ; i++){
			System.out.println(error.getErrorAt(i));
		}
		assertEquals(error.getErrorsSize(),0);
		for (int i=0 ; i<error.getWarningsSize() ; i++){
			System.out.println(error.getWarningAt(i));
		}
	}
	
	/**
	 * @return the writer handler to use
	 */
	protected abstract Class getGPEWriterHandlerClass();

	protected GPEWriterHandler getWriterHandler() throws WriterHandlerCreationException, FileNotFoundException{
		if (writerHandler == null){
			writerHandler = GPERegister.createWriter(getGPEWriterHandlerName());
			writerHandler.setErrorHandler(getErrorHandler());
			writerHandler.setOutputStream(new FileOutputStream(fileName));
		}
		return writerHandler;
	}
	
	/**
	 * Each test must to return its parser name
	 * to register it before to start the parsing
	 * process
	 */
	public String getGPEWriterHandlerName(){
		return "FORMAT VERSION";
	}
	
	/**
	 * Each test must to return its parser description
	 * to register it before to start the parsing
	 * process
	 */
	public String getGPEWriterHandlerDescription(){
		return "default writer handler description";
	}
	
	/**
	 * Create the layer that will be written
	 * @return
	 * The Layer
	 * @throws Exception
	 */
	protected abstract FLayer getLayerToWrite() throws Exception;
	
	/**
	 * @return the mapContext
	 */
	protected MapContext getMapContext() {
		if (mapContext == null){
			CoordinateReferenceSystem crs = ProjectionUtils.getCRS(getSRS());
			ViewPort vp = new ViewPort(crs);
			mapContext = new MapContext(vp);
		}
		return mapContext;
	}
		
	/**
	 * @return the mapcontext SRS. If you want to cahnge it
	 * you have to override this method
	 */
	protected String getSRS(){
		return "EPSG:23030";
	}	
	
	/**
	 * Gets the file format. The deafult writer
	 * format will be used by default
	 * @return
	 */
	public String getFormat(){
		return null;
	}
	
	/**
	 * Gets the file format. The deafult writer
	 * version will be used by default
	 * @return
	 */
	public String getVersion(){
		return null;
	}
	
	/**
	 * @return the errorHandler
	 */
	public GPEErrorHandler getErrorHandler() {
		if (errorHandler == null){
			errorHandler = new GPEErrorHandlerTest();
		}
		return errorHandler;
	}
	
}
