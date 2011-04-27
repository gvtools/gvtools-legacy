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
* $Id: BufferGeoprocessController.java 21232 2008-06-05 14:03:49Z azabala $
* $Log$
* Revision 1.7  2007-08-07 15:09:22  azabala
* changes to remove UnitUtils' andami dependencies
*
* Revision 1.6  2006/11/29 13:11:23  jmvivo
* Se ha añadido mas información al mensaje de error para los GeoprocessException: e.getMessage()
*
* Revision 1.5  2006/10/23 10:27:38  caballero
* ancho y alto del panel
*
* Revision 1.4  2006/08/11 16:13:08  azabala
* refactoring to make logic independent from UI
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
* cambios para poder trabajar en geoprocessing con capas MULTI (dxf, jdbc, etc)
*
* Revision 1.3  2006/06/02 18:20:04  azabala
* cuando el buffer es con dissolve se crea indice espacial para optimizar
*
* Revision 1.2  2006/05/25 08:21:48  jmvivo
* Añadida peticion de confirmacion para sobreescribir el fichero de salida, si este ya existiera
*
* Revision 1.1  2006/05/24 21:15:07  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.3  2006/05/08 15:34:59  azabala
* refactoring of ITask api
*
* Revision 1.2  2006/05/01 19:20:14  azabala
* revisión general del buffer (añadidos anillos concentricos, buffers interiores y exteriores, etc)
*
* Revision 1.1  2006/04/11 17:55:51  azabala
* primera version en cvs
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.buffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.cresques.cts.IProjection;
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
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessPanel;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.buffer.fmap.BufferGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.buffer.fmap.BufferVisitor;
import com.iver.cit.gvsig.geoprocess.impl.buffer.gui.BufferPanelIF;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;
/**
 * Controller class for a Buffer Geoprocess
 * @author azabala
 *
 */
public class BufferGeoprocessController extends
					AbstractGeoprocessController {
	/**
	 * UI class to read user entries.
	 * By using UI interfaces, we are allowing geoprocessing
	 * work with GUI and Command Line UI.
	 */
	private BufferPanelIF bufferPanel;
	/**
	 * Geoprocess we are going to launch with
	 * launchGeoprocess() method
	 */
	private BufferGeoprocess buffer;

	/**
	 * Default constructor
	 *
	 */
	public BufferGeoprocessController(){}

	/**
	 * Sets GUI panel to read user entries
	 */
	public void setView(IGeoprocessUserEntries viewPanel) {
		this.bufferPanel = (BufferPanelIF) viewPanel;
	}

	public IGeoprocess getGeoprocess() {
		return buffer;
	}

	public boolean launchGeoprocess() {
		FLyrVect inputLayer = bufferPanel.getInputLayer();
		FLayers layers = bufferPanel.getFLayers();
		File outputFile = null;
		try {
			outputFile = bufferPanel.getOutputFile();
		} catch (FileNotFoundException e3) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_resultado");
			bufferPanel.error(errorDescription, error);
			return false;
		}
		if (outputFile == null || (outputFile.getAbsolutePath().length() == 0)) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this, "Error_seleccionar_resultado");
			bufferPanel.error(errorDescription, error);
			return false;
		}
		if (outputFile.exists()) {
			if (!bufferPanel.askForOverwriteOutputFile(outputFile)) {
				return false;
			}
		}
		buffer = new BufferGeoprocess(inputLayer);
		HashMap params = new HashMap();
		boolean onlySelected = bufferPanel.isBufferOnlySelected();
		params.put("layer_selection", new Boolean(onlySelected));
		boolean dissolveBuffer = bufferPanel
				.isDissolveBuffersSelected();
		params.put("dissolve_buffers", new Boolean(dissolveBuffer));
		byte strategy = 0;
		if (bufferPanel.isConstantDistanceSelected()) {
			strategy = BufferGeoprocess.CONSTANT_DISTANCE_STRATEGY;
			double bufferDistance = -1;
			try {
				bufferDistance = bufferPanel.getConstantDistance();
			} catch (GeoprocessException e) {
				String error = PluginServices.getText(this, "Error_entrada_datos");
				String errorDescription = PluginServices.getText(this, "Error_distancia_buffer");
				errorDescription = "<html>" + errorDescription + ":<br>" + e.getMessage()+ "</html>";
				bufferPanel.error(errorDescription, error);
				return false;
			}
			params.put("buffer_distance", new Double(bufferDistance));
		} else if (bufferPanel.isAttributeDistanceSelected()) {
			strategy = BufferGeoprocess.ATTRIBUTE_DISTANCE_STRATEGY;
			String attributeName = null;
			try {
				attributeName = bufferPanel
						.getAttributeDistanceField();
			} catch (GeoprocessException e) {
				String error = PluginServices.getText(this, "Error_entrada_datos");
				String errorDescription = PluginServices.getText(this, "Error_atributo_no_numerico");
				errorDescription = "<html>" + errorDescription + ":<br>" + e.getMessage()+ "</html>";
				bufferPanel.error(errorDescription, error);
				return false;
			}
			params.put("attr_name", attributeName);
		}
		params.put("strategy_flag", new Byte(strategy));

		//number of radial buffers
		int numberOfRadials = bufferPanel.getNumberOfRadialBuffers();
		params.put("numRings", new Integer(numberOfRadials));

		//type of polygon buffer
		String typePolygonBuffer = bufferPanel.getTypePolygonBuffer();
		byte typePolBuffer = BufferVisitor.BUFFER_OUTSIDE_POLY;
		if(typePolygonBuffer.equals(BufferPanelIF.BUFFER_INSIDE)){
			typePolBuffer = BufferVisitor.BUFFER_INSIDE_POLY;
		}else if(typePolygonBuffer.equals(BufferPanelIF.BUFFER_INSIDE_OUTSIDE)){
			typePolBuffer = BufferVisitor.BUFFER_INSIDE_OUTSIDE_POLY;
		}
		params.put("typePolBuffer", new Byte(typePolBuffer));

		//round cap or square cap
		byte cap = BufferVisitor.CAP_ROUND;
		boolean squareCap = bufferPanel.isSquareCap();
		if(squareCap)
			cap = BufferVisitor.CAP_SQUARE;
		params.put("cap", new Byte(cap));
		
		
		IProjection proj =  ((View)PluginServices.
				getMDIManager().
				getActiveWindow()).
				getMapControl().
				getViewPort().
				getProjection();
		params.put("projection", proj);
		
		
		int distanceUnits = ((View)PluginServices.
				getMDIManager().
				getActiveWindow()).getMapControl().getViewPort().getDistanceUnits();
		params.put("distanceunits", new Integer(distanceUnits));
		
		
		boolean isProjected = proj.isProjected();
		int mapUnits = -1;
		if(isProjected){
			mapUnits = ((View)PluginServices.
				getMDIManager().
				getActiveWindow()).getMapControl().getViewPort().getMapUnits();
		}else{
			mapUnits = 1;
		}
		params.put("mapunits", new Integer(mapUnits));
		
		
		try {
			buffer.setParameters(params);
		} catch (GeoprocessException e2) {
			String error = PluginServices.getText(this, "Error_ejecucion");
			String errorDescription = PluginServices.getText(this, "Error_fallo_geoproceso");
			bufferPanel.error(errorDescription, error);
			return false;
		}

		SHPLayerDefinition definition = (SHPLayerDefinition) buffer
				.createLayerDefinition();
		definition.setFile(outputFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(outputFile.getAbsolutePath());
		IWriter writer = null;
		try {
			writer = getShpWriter(definition);
		} catch (Exception e1) {
			String error = PluginServices.getText(this, "Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this, "Error_preparar_escritura_resultados");
			bufferPanel.error(errorDescription, error);
			return false;
		}
		buffer.setResultLayerProperties(writer, schemaManager);

		try {
			buffer.checkPreconditions();
			IMonitorableTask task1 = buffer.createTask();
			AddResultLayerTask task2 = new AddResultLayerTask(buffer);
			task2.setLayers(layers);
			MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(task1,
					task2);
			if (globalTask.preprocess())
				PluginServices.cancelableBackgroundExecution(globalTask);
			return true;
		} catch (GeoprocessException e) {
			String error = PluginServices.getText(this, "Error_ejecucion");
			String errorDescription = PluginServices.getText(this, "Error_fallo_geoproceso");
			errorDescription = "<html>" + errorDescription + ":<br>" + e.getMessage()+ "</html>";
			bufferPanel.error(errorDescription, error);
			return false;
		}

	}
	public int getWidth() {
		return 700;
	}

	public int getHeight() {
		return 375;
	}
}

