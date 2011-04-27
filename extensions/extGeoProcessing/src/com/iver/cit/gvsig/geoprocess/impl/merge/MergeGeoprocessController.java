/*
 * Created on 11-abr-2006
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
* $Id: MergeGeoprocessController.java 21232 2008-06-05 14:03:49Z azabala $
* $Log$
* Revision 1.8  2006-11-29 13:11:23  jmvivo
* Se ha añadido mas información al mensaje de error para los GeoprocessException: e.getMessage()
*
* Revision 1.7  2006/10/23 10:29:18  caballero
* ancho y alto del panel
*
* Revision 1.6  2006/08/11 16:30:38  azabala
* *** empty log message ***
*
* Revision 1.5  2006/07/26 17:21:55  azabala
* added capability of saving result layer in multiple SHP when input layers are DXF
*
* Revision 1.4  2006/07/21 09:56:25  azabala
* fixed bug 667: exception when user dont select any layer to merge
*
* Revision 1.3  2006/07/21 09:10:34  azabala
* fixed bug 608: user doesnt enter any result file to the geoprocess panel
*
* Revision 1.2  2006/06/29 07:33:57  fjp
* Cambios ISchemaManager y IFieldManager por terminar
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.2  2006/05/25 08:21:48  jmvivo
* Añadida peticion de confirmacion para sobreescribir el fichero de salida, si este ya existiera
*
* Revision 1.1  2006/05/24 21:10:15  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.2  2006/05/08 15:35:32  azabala
* *** empty log message ***
*
* Revision 1.1  2006/04/11 17:55:51  azabala
* primera version en cvs
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.merge;

import java.io.File;
import java.io.FileNotFoundException;

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
import com.iver.cit.gvsig.geoprocess.impl.merge.fmap.MergeGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.merge.gui.MergePanelIF;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class MergeGeoprocessController extends AbstractGeoprocessController {
	private MergePanelIF geoProcessingMergePanel;
	private MergeGeoprocess merge;

	public void setView(IGeoprocessUserEntries viewPanel) {
		this.geoProcessingMergePanel =
			(MergePanelIF) viewPanel;
	}

	public IGeoprocess getGeoprocess() {
		return merge;
	}

	public boolean launchGeoprocess() {
		FLyrVect[] inputLayers = geoProcessingMergePanel.getSelectedLayers();
		if(inputLayers == null || inputLayers.length == 0){
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_capas_merge");
			geoProcessingMergePanel.error(errorDescription, error);
			return false;
		}
		//a reference to layers' FLayers allow us to add result layer to
		//active view's TOC.
		FLayers layers = geoProcessingMergePanel.getFLayers();
		FLyrVect schemaToPreserve = geoProcessingMergePanel.getSelectedSchema();
		if(schemaToPreserve == null){
			String error = PluginServices.
				getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.
				getText(this, "Error_seleccionar_esquema_merge");
			geoProcessingMergePanel.error(errorDescription, error);
			return false;
		}
		File outputFile = null;
		try {
			outputFile = geoProcessingMergePanel.getOutputFile();
		} catch (FileNotFoundException e3) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_resultado");
			geoProcessingMergePanel.error(errorDescription, error);
			return false;
		}
		if (outputFile == null || (outputFile.getAbsolutePath().length() == 0)) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_resultado");
			geoProcessingMergePanel.error(errorDescription, error);
			return false;
		}
		if (outputFile.exists()) {
			if (!geoProcessingMergePanel.askForOverwriteOutputFile(outputFile)) {
				return false;
			}
		}
		MergeGeoprocess merge = new MergeGeoprocess();
		merge.setInputLayers(inputLayers);
		merge.setOutputSchemaLayer(schemaToPreserve);

		SHPLayerDefinition definition = (SHPLayerDefinition) merge
										.createLayerDefinition();
		definition.setFile(outputFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(outputFile.getAbsolutePath());
		IWriter writer = null;
		try {
			writer = getShpWriter(definition);
		} catch (Exception e1) {
			String error = PluginServices.getText(this, "Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this, "Error_preparar_escritura_resultados");
			geoProcessingMergePanel.error(errorDescription, error);
			return false;
		}
		merge.setResultLayerProperties(writer, schemaManager);
		try {
			merge.checkPreconditions();
		} catch (GeoprocessException e) {
			String error = PluginServices.getText(this, "Error_chequeando_precondiciones");
			String errorDescription = PluginServices.getText(this, "Error_chequeo_tipo_geometria");
			errorDescription = "<html>" + errorDescription + ":<br>" + e.getMessage()+ "</html>";
			geoProcessingMergePanel.error(errorDescription, error);
			return false;
		}
		IMonitorableTask task1 = merge.createTask();
		AddResultLayerTask task2 = new AddResultLayerTask(merge);
		task2.setLayers(layers);
		MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(task1,
				task2);
		if (globalTask.preprocess())
			PluginServices.cancelableBackgroundExecution(globalTask);
		return true;
	}
	public int getWidth() {
		return 700;
	}

	public int getHeight() {
		return 500;
	}
}

