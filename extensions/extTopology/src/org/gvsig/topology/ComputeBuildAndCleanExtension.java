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
import java.io.FileNotFoundException;
import java.util.List;

import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.FLyrUtil;
import org.gvsig.topology.ui.util.GUIUtil;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.ILineCleanGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.LineCleanGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.topology.polygonbuild.IPolygonBuildGeoprocessUserEntries;
import com.iver.cit.gvsig.geoprocess.impl.topology.polygonbuild.PolygonBuildGeoprocessController;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extension to launch BUILD and CLEAN geoprocesses for lineal layers.
 * 
 * @author Alvaro Zabala
 * 
 */
public class ComputeBuildAndCleanExtension extends Extension {

	String messageSelection = PluginServices.getText(this,
			"Desea_calcular_el_build_solo_con_las_polilineas_seleccionadas");

	String msgErrorLyrBuild = PluginServices
			.getText(this,
					"Desea_añadir_capa_con_geometrias_que_no_forman_parte_de_poligonos");

	String msgErrorLyrClean = PluginServices.getText(this,
			"Desea_añadir_capa_con_pseudonodos");

	String title = PluginServices.getText(this, "Introduccion_de_datos");

	public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		View vista = (View) f;
		IProjectView model = vista.getModel();
		final MapContext mapContext = model.getMapContext();
		List<FLyrVect> activeVectorialLyrs = FLyrUtil
				.getActiveVectorialLyrs(mapContext);
		FLyrVect lyrOfLines = null;
		for (FLyrVect lyr : activeVectorialLyrs) {
			int shapeType;
			try {
				shapeType = lyr.getShapeType();
			} catch (ReadDriverException e) {
				e.printStackTrace();
				continue;
			}
			int numDim = FGeometryUtil.getDimensions(shapeType);
			if (numDim != 1)
				continue;
			else {
				lyrOfLines = lyr;
				break;
			}
		}
		if (lyrOfLines != null) {
			final FLyrVect inputLyr = lyrOfLines;
			if (actionCommand.equalsIgnoreCase("COMPUTE_BUILD")) {
				PolygonBuildGeoprocessController polygonBuild = new PolygonBuildGeoprocessController();
				polygonBuild.setView(createBuildPolygonUserEntries(mapContext,
						inputLyr));
				polygonBuild.launchGeoprocess();
			} else if (actionCommand.equalsIgnoreCase("COMPUTE_CLEAN")) {
				LineCleanGeoprocessController lineCleanGp = new LineCleanGeoprocessController();
				lineCleanGp.setView(createLineCleanUserEntries(mapContext,
						inputLyr));
				lineCleanGp.launchGeoprocess();
			}
		} else {
			GUIUtil.getInstance().messageBox(
					PluginServices.getText(this,
							"Debe_seleccionar_una_capa_de_lineas_en_el_TOC"),
					PluginServices.getText(this, "Error"));
		}
	}

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"compute-clean",
				this.getClass().getClassLoader()
						.getResource("images/compute-clean.png"));

		PluginServices.getIconTheme().registerDefault(
				"compute-build",
				this.getClass().getClassLoader()
						.getResource("images/compute-build.png"));
	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			MapContext mapContext = vista.getModel().getMapContext();
			FLayers layers = mapContext.getLayers();
			int numLayers = layers.getLayersCount();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect && layer.isAvailable()
						&& layer.isActive()) {
					int shapeType;
					try {
						shapeType = ((FLyrVect) layer).getShapeType();
					} catch (ReadDriverException e) {
						e.printStackTrace();
						continue;
					}
					int numDim = FGeometryUtil.getDimensions(shapeType);
					if (numDim == 1)
						return true;

				}
			}
		}
		return false;
	}

	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			FLayers layers = model.getMapContext().getLayers();
			int numLayers = layers.getLayersCount();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect || layer instanceof Topology)
					return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Generates a shp output file (in the topology's temp files directory) for
	 * the layer resulting of a clean or build operation.
	 * 
	 * @param inputLyr
	 *            original layer to compute the clean or build geoprocess
	 * @return
	 */
	private File getOutputFileForLyr(final FLyrVect inputLyr, String sufix) {
		String filesDirectory = GUIUtil.getInstance().getFilesDirectory();
		if (!filesDirectory.endsWith("/"))
			filesDirectory += "/";
		String fileName = inputLyr.getName();
		if (fileName.endsWith(".shp"))
			fileName = fileName.substring(0, fileName.length() - 4);
		fileName += "_" + sufix + ".shp";
		return new File(filesDirectory + fileName);
	}

	/**
	 * Creates a IPolygonBuildGeoprocessUserEntries implementation to provides
	 * PolygonBuildGeoprocessController user entries.
	 * 
	 * @param mapContext
	 *            mapContext of the active view where the input lyr is added.
	 * @param inputLyr
	 */
	private IPolygonBuildGeoprocessUserEntries createBuildPolygonUserEntries(
			final MapContext mapContext, final FLyrVect inputLyr) {
		return new IPolygonBuildGeoprocessUserEntries() {

			public boolean createLyrsWithErrorGeometries() {
				return GUIUtil.getInstance().optionMessage(msgErrorLyrBuild,
						title);
			}

			public boolean applyDangleTolerance() {
				return false;
			}

			public boolean applySnapTolerance() {
				return false;
			}

			public boolean computeCleanBefore() {
				return false;
			}

			public double getDangleTolerance() throws GeoprocessException {
				return 0;
			}

			public boolean isFirstOnlySelected() {
				boolean firstOnlySelected = false;
				try {
					if (inputLyr.getRecordset().getSelection().cardinality() != 0) {
						firstOnlySelected = GUIUtil.getInstance()
								.optionMessage(messageSelection, title);
					}
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
				return firstOnlySelected;
			}

			public boolean askForOverwriteOutputFile(File outputFile) {
				return GUIUtil.getInstance().askForOverwriteOutputFile(
						outputFile);
			}

			public void error(String message, String title) {
				GUIUtil.getInstance().messageBox(message, title);
			}

			public FLayers getFLayers() {
				return mapContext.getLayers();
			}

			public FLyrVect getInputLayer() {
				return inputLyr;
			}

			public File getOutputFile() throws FileNotFoundException {
				return getOutputFileForLyr(inputLyr, "build");
			}

			public void setFLayers(FLayers layers) {
			}
		};
	}

	/**
	 * 
	 * Creates a ILineCleanGeoprocessUserEntries implementation to provides
	 * LineCleanGeoprocessController user entries.
	 * 
	 * @param mapContext
	 * @param inputLyr
	 * @return
	 */
	private ILineCleanGeoprocessUserEntries createLineCleanUserEntries(
			final MapContext mapContext, final FLyrVect inputLyr) {
		return new ILineCleanGeoprocessUserEntries() {

			String messageSelection = PluginServices
					.getText(this,
							"Desea_calcular_el_clean_solo_con_las_polilineas_seleccionadas");

			String title = PluginServices
					.getText(this, "Introduccion_de_datos");

			public boolean cleanOnlySelection() {
				boolean firstOnlySelected = false;
				try {
					if (inputLyr.getRecordset().getSelection().cardinality() != 0) {
						firstOnlySelected = GUIUtil.getInstance()
								.optionMessage(messageSelection, title);
					}
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
				return firstOnlySelected;
			}

			public boolean askForOverwriteOutputFile(File outputFile) {
				return GUIUtil.getInstance().askForOverwriteOutputFile(
						outputFile);
			}

			public void error(String message, String title) {
				GUIUtil.getInstance().messageBox(message, title);
			}

			public FLayers getFLayers() {
				return mapContext.getLayers();
			}

			public FLyrVect getInputLayer() {
				return inputLyr;
			}

			public File getOutputFile() throws FileNotFoundException {
				return getOutputFileForLyr(inputLyr, "clean");
			}

			public void setFLayers(FLayers layers) {
			}

			public boolean createLyrsWithErrorGeometries() {
				return GUIUtil.getInstance().optionMessage(msgErrorLyrClean,
						title);
			}
		};

	}

}
