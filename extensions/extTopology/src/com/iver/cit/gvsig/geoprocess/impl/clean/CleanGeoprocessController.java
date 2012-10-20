package com.iver.cit.gvsig.geoprocess.impl.clean;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.swing.JOptionPane;

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
import com.iver.cit.gvsig.geoprocess.impl.clean.fmap.CleanGeoprocess;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;

public class CleanGeoprocessController extends AbstractGeoprocessController {

	private ICleanGeoprocessUserEntries userEntries;

	private CleanGeoprocess geoProcess;

	@Override
	public IGeoprocess getGeoprocess() {
		return geoProcess;
	}

	@Override
	public boolean launchGeoprocess() {

		boolean createErrorLayer = userEntries.createLyrsWithErrorGeometries();

		String error = PluginServices.getText(this, "Error_entrada_datos");

		FLyrVect inputLayer = userEntries.getInputLayer();
		FLayers layers = userEntries.getFLayers();
		File outputFile = null;
		try {
			outputFile = userEntries.getOutputFile();
		} catch (FileNotFoundException e3) {

			String errorDescription = PluginServices.getText(this,
					"Error_seleccionar_resultado");
			userEntries.error(errorDescription, error);
			return false;
		}
		if (outputFile == null || (outputFile.getAbsolutePath().length() == 0)) {
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

		geoProcess = new CleanGeoprocess(inputLayer);

		SHPLayerDefinition definition = (SHPLayerDefinition) geoProcess
				.createLayerDefinition();
		definition.setFile(outputFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(
				outputFile.getAbsolutePath());
		IWriter writer = null;
		try {
			writer = getShpWriter(definition);
		} catch (Exception e1) {
			String errorDescription = PluginServices.getText(this,
					"Error_preparar_escritura_resultados");
			userEntries.error(errorDescription, error);
			return false;
		}
		geoProcess.setResultLayerProperties(writer, schemaManager);
		HashMap<String, Object> params = new HashMap<String, Object>();

		String resultLayerName = outputFile.getName();
		params.put("resultlayername", resultLayerName);

		boolean onlySelected = userEntries.cleanOnlySelection();
		params.put("layer_selection", new Boolean(onlySelected));

		boolean createLayerWithError = userEntries
				.createLyrsWithErrorGeometries();
		params.put("createlayerswitherrors", new Boolean(createLayerWithError));

		try {
			double fuzzyTol = userEntries.getFuzzyTolerance();
			params.put("fuzzyTolerance", new Double(fuzzyTol));

			double dangleTol = userEntries.getDangleTolerance();
			params.put("dangleTolerance", new Double(dangleTol));
		} catch (GeoprocessException e1) {
			JOptionPane.showMessageDialog(null, e1.toString(), "Error!",
					JOptionPane.WARNING_MESSAGE);
			userEntries.error(e1.toString(), error);
			return false;
		}

		params.put("cleanbefore", new Boolean(false));

		// String outputLayerType = userEntries.getOutputLayerType();
		// params.put("outputlayertype",outputLayerType);

		try {
			geoProcess.setParameters(params);
			geoProcess.checkPreconditions();
			IMonitorableTask task1 = geoProcess.createTask();
			if (task1 == null) {
				return false;
			}
			AddResultLayerTask task2 = new AddResultLayerTask(geoProcess);
			task2.setLayers(layers);
			MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(
					task1, task2);
			if (globalTask.preprocess())
				PluginServices.cancelableBackgroundExecution(globalTask);

		} catch (GeoprocessException e) {
			String errorDescription = PluginServices.getText(this,
					"Error_fallo_geoproceso");
			userEntries.error(errorDescription, error);
			return false;
		}
		return true;
	}

	@Override
	public void setView(IGeoprocessUserEntries userEntries) {
		this.userEntries = (ICleanGeoprocessUserEntries) userEntries;
	}

	public int getHeight() {
		return 250;
	}

	public int getWidth() {
		return 700;
	}

}
