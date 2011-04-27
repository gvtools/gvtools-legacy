package com.iver.cit.gvsig.geoprocess;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.fmap.LineCleanGeoprocess;
/*
 * Created on 13-jun-2007
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id: LineCleanTest.java 12563 2007-07-12 11:11:01Z azabala $
 * $Log$
 * Revision 1.1  2007-07-12 11:11:01  azabala
 * *** empty log message ***
 *
 *
 */
/**
 * Tests por LineClean topologic geoprocess.
 * 
 */
public class LineCleanTest extends TestCase {
	static final String fwAndamiDriverPath = "../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers";
	private static File baseDataPath;
	private static File baseDriversPath;
	public static String SHP_DRIVER_NAME = "gvSIG shp driver";

	static IProjection PROJECTION_DEFAULT = CRSFactory.getCRS("EPSG:23030");
	
	
	protected void setUp() throws Exception {
		URL url = LineCleanTest.class.getResource("testdata");
		if (url == null)
			throw new Exception("No se encuentra el directorio con datos de prueba");

		baseDataPath = new File(url.getFile());
		if (!baseDataPath.exists())
			throw new Exception("No se encuentra el directorio con datos de prueba");

		baseDriversPath = new File(fwAndamiDriverPath);
		if (!baseDriversPath.exists())
			throw new Exception("Can't find drivers path: " + fwAndamiDriverPath);

		LayerFactory.setDriversPath(baseDriversPath.getAbsolutePath());
		LayerFactory.setWritersPath(baseDriversPath.getAbsolutePath());
		if (LayerFactory.getDM().getDriverNames().length < 1)
			throw new Exception("Can't find drivers in path: " + fwAndamiDriverPath);
		
	}


	public static  FLayer newLayer(String fileName,
									   String driverName)
								throws LoadLayerException {
		File file = new File(baseDataPath, fileName);
		return LayerFactory.createLayer(fileName,
										driverName,
										file, PROJECTION_DEFAULT);
	}
	
	
	public void test1() throws LoadLayerException, InitializeWriterException, GeoprocessException{
		FLyrVect inputLayer = (FLyrVect) newLayer("carrilbici.shp", SHP_DRIVER_NAME);
		File outputFile = new File(baseDataPath+"/test1result.shp");
		LineCleanGeoprocess geoprocess = new LineCleanGeoprocess(inputLayer);
		SHPLayerDefinition definition = (SHPLayerDefinition) geoprocess.createLayerDefinition();
		definition.setFile(outputFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(outputFile.getAbsolutePath());
		IWriter writer = null;
		int shapeType = definition.getShapeType();
		if(shapeType != XTypes.MULTI){
			writer = new ShpWriter();
			((ShpWriter) writer).setFile(definition.getFile());
			writer.initialize(definition);
		}else{
			writer = new MultiShpWriter();
			((MultiShpWriter) writer).setFile(definition.getFile());
			writer.initialize(definition);
		}
		geoprocess.setResultLayerProperties(writer, schemaManager);
		HashMap params = new HashMap();
		params.put("layer_selection", new Boolean(false));
		geoprocess.setParameters(params);
		geoprocess.checkPreconditions();
		geoprocess.process();
	}
	

}

