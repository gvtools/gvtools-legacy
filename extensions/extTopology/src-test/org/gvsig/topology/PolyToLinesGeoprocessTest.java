/*
 * Created on 10-abr-2006
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
* $Id: 
* $Log: 
*/
package org.gvsig.topology;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import junit.framework.TestCase;

import org.cresques.cts.IProjection;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
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
import com.iver.cit.gvsig.geoprocess.impl.polytolines.fmap.PolyToLinesGeoprocess;
import com.iver.utiles.swing.threads.IMonitorableTask;

public class PolyToLinesGeoprocessTest extends TestCase {
	
	private File baseDataPath;
	private File baseDriversPath;
	private IProjection PROJECTION_DEFAULT;
	private FLyrVect poly1;

	public void setUp() throws Exception {
		super.setUp();
		URL url = PolyToLinesGeoprocessTest.class.getResource("testdata");
		if (url == null)
			throw new Exception(
					"No se encuentra el directorio con datos de prueba");

		baseDataPath = new File(url.getFile());
		if (!baseDataPath.exists())
			throw new Exception(
					"No se encuentra el directorio con datos de prueba");

		baseDriversPath = new File(
				"../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
		if (!baseDriversPath.exists())
			throw new Exception("Can't find drivers path ");
		com.iver.cit.gvsig.fmap.layers.LayerFactory
				.setDriversPath(baseDriversPath.getAbsolutePath());
		LayerFactory.setWritersPath(baseDriversPath.getAbsolutePath());
		if (LayerFactory.getDM().getDriverNames().length < 1)
			throw new Exception("Can't find drivers in path: "
					+ baseDriversPath);
		PROJECTION_DEFAULT = CRSFactory.getCRS("EPSG:23030");
		
		poly1 = (FLyrVect) newLayer("poligonos.shp",
				"gvSIG shp driver");
		poly1.createSpatialIndex();

	}

	public FLayer newLayer(String fileName, String driverName)
			throws LoadLayerException {
		FLayer solution = null;
		File file = new File(baseDataPath, fileName);
		solution = com.iver.cit.gvsig.fmap.layers.LayerFactory.createLayer(
				fileName, driverName, file, PROJECTION_DEFAULT);
		solution.setAvailable(true);
		return solution;

	}
	
	
	
	public void test1(){
		
		PolyToLinesGeoprocess geoprocess = new PolyToLinesGeoprocess(poly1);
		String temp = System.getProperty("java.io.tmpdir") + poly1.getName();
		File newFile = new File(temp);
		SHPLayerDefinition definition = (SHPLayerDefinition) geoprocess
				.createLayerDefinition();
		definition.setFile(newFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(newFile
				.getAbsolutePath());
		IWriter writer = null;
		try {
			schemaManager.createSchema(definition);
			int shapeType = definition.getShapeType();
			if (shapeType != XTypes.MULTI) {
				writer = new ShpWriter();
				((ShpWriter) writer).setFile(definition.getFile());
			} else {
				writer = new MultiShpWriter();
				((MultiShpWriter) writer).setFile(definition.getFile());
			}
			writer.initialize(definition);
		} catch (Exception e1) {
			String error = PluginServices.getText(this,
					"Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this,
					"Error_preparar_escritura_resultados");
			GUIUtil.getInstance().messageBox(error, errorDescription);
		}

		geoprocess.setResultLayerProperties(writer, schemaManager);
		
		HashMap params = new HashMap();
		Boolean selected = new Boolean(false);
		params.put("layer_selection", selected);

		try {
			geoprocess.setParameters(params);
			geoprocess.checkPreconditions();
			IMonitorableTask task = geoprocess.createTask();

			task.run();

			FLyrVect result = (FLyrVect) geoprocess.getResult();

			System.out.println("numero de geometrias de la capa resultado :"
					+ result.getSource().getShapeCount());
			
		} catch (GeoprocessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
