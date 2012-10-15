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
* $Id: SpatialJoinGeoprocessController.java 21232 2008-06-05 14:03:49Z azabala $
* $Log$
* Revision 1.7  2007-08-07 16:07:34  azabala
* bug solved when a layer vect hasnt numeric fields
*
* Revision 1.6  2006/11/29 13:11:23  jmvivo
* Se ha añadido mas información al mensaje de error para los GeoprocessException: e.getMessage()
*
* Revision 1.5  2006/10/23 10:29:18  caballero
* ancho y alto del panel
*
* Revision 1.4  2006/08/11 16:30:38  azabala
* *** empty log message ***
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
* Revision 1.4  2006/06/12 19:15:38  azabala
* cambios para poder trabajar en geoprocessing con capas MULTI (jdbc, etc)
*
* Revision 1.3  2006/06/02 18:21:28  azabala
* *** empty log message ***
*
* Revision 1.2  2006/05/25 08:21:48  jmvivo
* Añadida peticion de confirmacion para sobreescribir el fichero de salida, si este ya existiera
*
* Revision 1.1  2006/05/24 21:09:47  azabala
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
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.AddResultLayerTask;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap.SpatialJoinGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.spatialjoin.gui.SpatialJoinPanelIF;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class SpatialJoinGeoprocessController extends
		AbstractGeoprocessController {

	private SpatialJoinPanelIF geoProcessingSpatialjoinPanel;
	private SpatialJoinGeoprocess spatialJoin;

	public void setView(IGeoprocessUserEntries viewPanel) {
		this.geoProcessingSpatialjoinPanel =
			(SpatialJoinPanelIF) viewPanel;
	}
	public IGeoprocess getGeoprocess() {
		return spatialJoin;
	}

	public boolean launchGeoprocess() {
//		 this open a modal dialog and sets sumarize functions
		Map sumarizeFunctions = null;
		FLyrVect secondLayer = geoProcessingSpatialjoinPanel.getSecondLayer();
		if(XTypes.getNumericFieldsNames(secondLayer).length > 0){
		if (!geoProcessingSpatialjoinPanel.isNearestSelected()){
			boolean isOk = geoProcessingSpatialjoinPanel.openSumarizeFunction();
			if(! isOk){
				//Ok button wasnt pushed. Dialog was closed directly
				return false;
			}
			sumarizeFunctions = geoProcessingSpatialjoinPanel
				.getSumarizeFunctions();
			if(sumarizeFunctions == null || sumarizeFunctions.size() == 0){
				geoProcessingSpatialjoinPanel.error(PluginServices.getText(this, "Error_spjoinmn_sin_funcion"),
				PluginServices.getText(this, "Error_entrada_datos"));
				return false;
			}
		}
		}
		FLyrVect inputLayer = geoProcessingSpatialjoinPanel.getFirstLayer();
		FLayers layers = geoProcessingSpatialjoinPanel.getFLayers();
		File outputFile = null;
		try {
			outputFile = geoProcessingSpatialjoinPanel.getOutputFile();
		} catch (FileNotFoundException e3) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_resultado");
			geoProcessingSpatialjoinPanel.error(errorDescription, error);
			return false;
		}
		if (outputFile == null || (outputFile.getAbsolutePath().length() == 0)) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_resultado");
			geoProcessingSpatialjoinPanel.error(errorDescription, error);
			return false;
		}
		if (outputFile.exists()) {
			if (!geoProcessingSpatialjoinPanel.askForOverwriteOutputFile(outputFile)) {
				return false;
			}
		}

		boolean onlyFirstSelection = geoProcessingSpatialjoinPanel
				.onlyFirstLayerSelected();
		boolean onlySecondSelection = geoProcessingSpatialjoinPanel
				.onlySecondLayerSelected();
		boolean nearest = geoProcessingSpatialjoinPanel.isNearestSelected();


		SpatialJoinGeoprocess join = new SpatialJoinGeoprocess(inputLayer);
		join.setSecondOperand(secondLayer);
		join.setFields_sumFunctions(sumarizeFunctions);
		HashMap params = new HashMap();
		Boolean first = new Boolean(onlyFirstSelection);
		params.put("firstlayerselection", first);

		Boolean second = new Boolean(onlySecondSelection);
		params.put("secondlayerselection", second);
		if(! nearest){
			if(!secondLayer.isSpatiallyIndexed()){
				final IMonitorableTask sptIdxTask =
					geoProcessingSpatialjoinPanel.
					askForSpatialIndexCreation(secondLayer);
				if(sptIdxTask != null){
					PluginServices.backgroundExecution(
							new Runnable(){
						public void run() {
							PluginServices.
							cancelableBackgroundExecution(sptIdxTask);
						}}
					);
				}	//if
			}//if

		}//if nearest
		Boolean nearSel = new Boolean(nearest);
		params.put("nearest", nearSel);
		try {
			ShpSchemaManager schemaManager = new ShpSchemaManager(outputFile.getAbsolutePath());
			IWriter writer = new ShpWriter();
			join.setResultLayerProperties(writer, schemaManager);
			join.setParameters(params);
			SHPLayerDefinition definition =
				(SHPLayerDefinition) join.createLayerDefinition();
			definition.setFile(outputFile);
			((ShpWriter)writer).setFile(definition.getFile());
			writer.initialize(definition);
			writer = getShpWriter(definition);
			join.checkPreconditions();
			IMonitorableTask task1 = join.createTask();
			if(task1 == null){
				//mensaje de error
				return false;
			}
			AddResultLayerTask task2 = new AddResultLayerTask(join);
			task2.setLayers(layers);
			MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(task1,
					task2);
			if (globalTask.preprocess())
				PluginServices.cancelableBackgroundExecution(globalTask);
		} catch (GeoprocessException e) {
			String error = PluginServices.getText(this, "Error_ejecucion");
			String errorDescription = PluginServices.getText(this, "Error_fallo_geoproceso");
			errorDescription = "<html>" + errorDescription + ":<br>" + e.getMessage()+ "</html>";
			geoProcessingSpatialjoinPanel.error(errorDescription, error);
			return false;

		} catch (Exception e) {
			String error = PluginServices.getText(this, "Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this, "Error_preparar_escritura_resultados");
			geoProcessingSpatialjoinPanel.error(errorDescription, error);
			return false;
		}
		return true;
	}
	public int getWidth() {
		return 700;
	}

	public int getHeight() {
		return 350;
	}
}

