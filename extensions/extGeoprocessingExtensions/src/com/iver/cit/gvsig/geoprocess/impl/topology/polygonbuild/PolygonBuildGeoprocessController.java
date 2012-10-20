/*
 * Created on 18-dic-2006
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
 * $Id: PolygonBuildGeoprocessController.java 21235 2008-06-05 14:08:38Z azabala $
 * $Log$
 * Revision 1.1  2006-12-21 17:23:27  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/12/19 19:29:30  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.topology.polygonbuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.gui.AddResultLayerTask;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.topology.polygonbuild.fmap.PolygonBuildGeoprocess;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class PolygonBuildGeoprocessController extends
		AbstractGeoprocessController {

	private IPolygonBuildGeoprocessUserEntries userEntries;
	private PolygonBuildGeoprocess geoprocess;

	public void setView(IGeoprocessUserEntries userEntries) {
		this.userEntries = (IPolygonBuildGeoprocessUserEntries) userEntries;
	}

	public IGeoprocess getGeoprocess() {
		return geoprocess;
	}

	public boolean launchGeoprocess() {
		FLyrVect inputLayer = userEntries.getInputLayer();
		FLayers layers = userEntries.getFLayers();
		File outputFile = null;
		try {
			outputFile = userEntries.getOutputFile();
		} catch (FileNotFoundException e3) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this,
					"Error_seleccionar_resultado");
			userEntries.error(errorDescription, error);
			return false;
		}
		if (outputFile == null || (outputFile.getAbsolutePath().length() == 0)) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this,
					"Error_seleccionar_resultado");
			userEntries.error(errorDescription, error);
			return false;
		}
		if (outputFile.exists()) {
			if (!userEntries.askForOverwriteOutputFile(outputFile)) {
				return false;
			}
		}

		geoprocess = new PolygonBuildGeoprocess(inputLayer);

		SHPLayerDefinition definition = (SHPLayerDefinition) geoprocess
				.createLayerDefinition();
		definition.setFile(outputFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(
				outputFile.getAbsolutePath());
		IWriter writer = null;
		try {
			writer = getShpWriter(definition);
		} catch (Exception e1) {
			String error = PluginServices.getText(this,
					"Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this,
					"Error_preparar_escritura_resultados");
			userEntries.error(errorDescription, error);
			return false;
		}
		geoprocess.setResultLayerProperties(writer, schemaManager);

		HashMap params = new HashMap();
		boolean onlySelected = userEntries.isFirstOnlySelected();
		params.put("firstlayerselection", new Boolean(onlySelected));

		boolean addErrors2TOC = userEntries.createLyrsWithErrorGeometries();
		params.put("addgroupoflyrs", new Boolean(addErrors2TOC));
		if (addErrors2TOC) {
			boolean applyDangleTol = userEntries.applyDangleTolerance();
			params.put("applydangletol", new Boolean(applyDangleTol));
			if (applyDangleTol) {
				double dangleTolerance = 0d;
				try {
					dangleTolerance = userEntries.getDangleTolerance();
				} catch (GeoprocessException e) {
					String error = PluginServices.getText(this,
							"Error_entrada_datos");
					String errorDescription = PluginServices.getText(this,
							"Distancia_dangle_incorrecta");
					userEntries.error(errorDescription, error);
					return false;
				}// catch
				params.put("dangletolerance", new Double(dangleTolerance));
			}// if

			boolean applySnapTol = userEntries.applySnapTolerance();
			params.put("applysnaptol", new Boolean(applySnapTol));
			if (applySnapTol) {
				double snapTolerance = 0d;
				try {
					snapTolerance = userEntries.getDangleTolerance();
				} catch (GeoprocessException e) {
					String error = PluginServices.getText(this,
							"Error_entrada_datos");
					String errorDescription = PluginServices.getText(this,
							"Distancia_snap_incorrecta");
					userEntries.error(errorDescription, error);
					return false;
				}// catch
				params.put("snaptolerance", new Double(snapTolerance));
			}// if
		}// if addErrors2TOC
		boolean computeCleanBefore = userEntries.computeCleanBefore();
		params.put("computeclean", new Boolean(computeCleanBefore));

		try {
			geoprocess.setParameters(params);
			geoprocess.checkPreconditions();
			IMonitorableTask task1 = geoprocess.createTask();
			if (task1 == null) {
				return false;

			}
			AddResultLayerTask task2 = new AddResultLayerTask(geoprocess);
			task2.setLayers(layers);
			MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(
					task1, task2);
			if (globalTask.preprocess())
				PluginServices.cancelableBackgroundExecution(globalTask);

		} catch (GeoprocessException e) {
			String error = PluginServices.getText(this, "Error_ejecucion");
			String errorDescription = PluginServices.getText(this,
					"Error_fallo_geoproceso");
			userEntries.error(errorDescription, error);
			return false;
		}
		return true;
	}

	// Esto mejor llevarlo al plugin no??
	// o en su defecto al panel
	public int getWidth() {
		return 700;
	}

	public int getHeight() {
		return 600;
	}

}
