package com.iver.cit.gvsig.geoprocess.impl.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.gui.AddResultLayerTask;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.build.fmap.BuildGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.clean.fmap.CleanGeoprocess;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class BuildGeoprocessController extends AbstractGeoprocessController {

	private IBuildGeoprocessUserEntries userEntries;

	private AbstractGeoprocess geoprocess;

	@Override
	public IGeoprocess getGeoprocess() {
		return geoprocess;
	}

	@Override
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

		HashMap<String, Object> params = new HashMap<String, Object>();

		boolean cleanBefore = userEntries.cleanBefore();
		params.put("cleanbefore", new Boolean(cleanBefore));

		if (cleanBefore)
			geoprocess = new CleanGeoprocess(inputLayer);
		else
			geoprocess = new BuildGeoprocess(inputLayer);

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

		String resultLayerName = outputFile.getName();
		params.put("resultlayername", resultLayerName);

		boolean onlySelected = userEntries.buildOnlySelection();
		params.put("firstlayerselection", new Boolean(onlySelected));

		boolean addErrors2TOC = userEntries.createLyrsWithErrorGeometries();
		params.put("createlayerswitherrors", new Boolean(addErrors2TOC));

		if (cleanBefore) {
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

			double fuzzyTolerance = 0d;
			try {
				fuzzyTolerance = userEntries.getFuzzyTolerance();
			} catch (GeoprocessException e) {
				String error = PluginServices.getText(this,
						"Error_entrada_datos");
				String errorDescription = PluginServices.getText(this,
						"Distancia_snap_incorrecta");
				userEntries.error(errorDescription, error);
				return false;
			}// catch
			params.put("fuzzyTolerance", new Double(fuzzyTolerance));
		}// if addErrors2TOC

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

	@Override
	public void setView(IGeoprocessUserEntries userEntries) {
		this.userEntries = (IBuildGeoprocessUserEntries) userEntries;
	}

	public int getHeight() {
		return 300;
	}

	public int getWidth() {
		return 700;
	}

}
