/*
 * Created on 28-jun-2006
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
 * $Id: XYShiftGeoprocessController.java 21235 2008-06-05 14:08:38Z azabala $
 * $Log$
 * Revision 1.6  2007-08-16 14:41:36  azabala
 * changes to remove UnitUtils' andami dependencies
 *
 * Revision 1.5  2006/11/29 13:11:23  jmvivo
 * Se ha añadido mas información al mensaje de error para los GeoprocessException: e.getMessage()
 *
 * Revision 1.4  2006/10/23 15:16:12  jmvivo
 * implementados el getWidth y el getHeight
 *
 * Revision 1.3  2006/08/11 17:17:55  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/29 17:58:31  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/28 18:17:21  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.xyshift;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;

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
import com.iver.cit.gvsig.geoprocess.impl.xyshift.fmap.XYShiftGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.xyshift.gui.GeoprocessingXYShiftPanel2;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class XYShiftGeoprocessController extends AbstractGeoprocessController {

	private GeoprocessingXYShiftPanel2 panel;
	private XYShiftGeoprocess xyShift;

	public void setView(IGeoprocessUserEntries viewPanel) {
		this.panel = (GeoprocessingXYShiftPanel2) viewPanel;
	}

	public IGeoprocess getGeoprocess() {
		return xyShift;
	}

	public boolean launchGeoprocess() {
		FLyrVect inputLayer = panel.getInputLayer();
		FLayers layers = panel.getFLayers();
		File outputFile = null;
		try {
			outputFile = panel.getOutputFile();
		} catch (FileNotFoundException e3) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this,
					"Error_seleccionar_resultado");
			panel.error(errorDescription, error);
			return false;
		}
		if (outputFile == null || (outputFile.getAbsolutePath().length() == 0)) {
			String error = PluginServices.getText(this, "Error_entrada_datos");
			String errorDescription = PluginServices.getText(this,
					"Error_seleccionar_resultado");
			panel.error(errorDescription, error);
			return false;
		}
		if (outputFile.exists()) {
			if (!panel.askForOverwriteOutputFile(outputFile)) {
				return false;
			}
		}
		xyShift = new XYShiftGeoprocess(inputLayer);
		SHPLayerDefinition definition = (SHPLayerDefinition) xyShift
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
			panel.error(errorDescription, error);
			return false;
		}
		xyShift.setResultLayerProperties(writer, schemaManager);
		HashMap params = new HashMap();
		boolean onlySelected = panel.isFirstOnlySelected();
		params.put("firstlayerselection", new Boolean(onlySelected));
		try {
			double xShift = panel.getXOffset();
			params.put("xshift", new Double(xShift));
			double yShift = panel.getYOffset();
			params.put("yshift", new Double(yShift));
		} catch (GeoprocessException e1) {
			String error = PluginServices.getText(this,
					"Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this,
					"Error_preparar_escritura_resultados");
			panel.error(errorDescription, error);
			return false;
		}

		CoordinateReferenceSystem crs = ((View) PluginServices.getMDIManager()
				.getActiveWindow()).getMapControl().getViewPort().getCrs();
		params.put("projection", crs);

		int distanceUnits = ((View) PluginServices.getMDIManager()
				.getActiveWindow()).getMapControl().getViewPort()
				.getDistanceUnits();
		params.put("distanceunits", new Integer(distanceUnits));

		int mapUnits = -1;
		if (crs instanceof ProjectedCRS) {
			mapUnits = ((View) PluginServices.getMDIManager().getActiveWindow())
					.getMapControl().getViewPort().getMapUnits();
		} else {
			mapUnits = 1;
		}
		params.put("mapunits", new Integer(mapUnits));

		try {
			xyShift.setParameters(params);
			xyShift.checkPreconditions();
			IMonitorableTask task1 = xyShift.createTask();
			if (task1 == null) {
				return false;

			}
			AddResultLayerTask task2 = new AddResultLayerTask(xyShift);
			task2.setLayers(layers);
			MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(
					task1, task2);
			if (globalTask.preprocess())
				PluginServices.cancelableBackgroundExecution(globalTask);

		} catch (GeoprocessException e) {
			String error = PluginServices.getText(this, "Error_ejecucion");
			String errorDescription = PluginServices.getText(this,
					"Error_fallo_geoproceso");
			errorDescription = "<html>" + errorDescription + ":<br>"
					+ e.getMessage() + "</html>";
			panel.error(errorDescription, error);
			return false;
		}
		return true;
	}

	public int getWidth() {
		return 750;
	}

	public int getHeight() {
		return 240;
	}
}
