/*
 * Created on 10-oct-2006
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
 * $Id: LineCleanGeoprocessController.java 21235 2008-06-05 14:08:38Z azabala $
 * $Log$
 * Revision 1.1  2006-12-21 17:23:27  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/12/04 19:42:23  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/10/23 16:52:39  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/17 18:25:53  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/10 18:50:17  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.topology.lineclean;

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
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.fmap.LineCleanGeoprocess;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class LineCleanGeoprocessController extends AbstractGeoprocessController {

	private ILineCleanGeoprocessUserEntries userEntries;
	private LineCleanGeoprocess geoprocess;

	public void setView(IGeoprocessUserEntries viewPanel) {
		this.userEntries = (ILineCleanGeoprocessUserEntries) viewPanel;
	}

	public IGeoprocess getGeoprocess() {
		return geoprocess;
	}

	public boolean launchGeoprocess() {
		/*
		 * TODO METER TODO ESTO EN LA CLASE ABSTRACTA
		 */
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

		geoprocess = new LineCleanGeoprocess(inputLayer);

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
		boolean onlySelected = userEntries.cleanOnlySelection();
		params.put("layer_selection", new Boolean(onlySelected));

		boolean createLayerWithError = userEntries
				.createLyrsWithErrorGeometries();
		params.put("createlayerswitherrors", new Boolean(createLayerWithError));

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

	public int getWidth() {
		return 700;
	}

	public int getHeight() {
		return 600;
	}

}
