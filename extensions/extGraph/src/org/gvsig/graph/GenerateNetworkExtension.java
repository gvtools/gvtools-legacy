/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package org.gvsig.graph;

import java.awt.Component;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.core.writers.NetworkFileRedWriter;
import org.gvsig.graph.core.writers.NetworkGvTableWriter;
import org.gvsig.graph.gui.wizard.NetWizard;
import org.gvsig.graph.preferences.RoutePage;
import org.gvsig.graph.topology.LineSnapGeoprocess;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.dbf.DbfWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.SimpleFileFilter;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;
import com.iver.utiles.swing.threads.IPipedTask;
import com.iver.utiles.swing.threads.PipeTask;

public class GenerateNetworkExtension extends Extension implements
		IPreferenceExtension {
	private static final IPreference[] thePreferencePages = new IPreference[] { new RoutePage() };

	public boolean onlySnapNodes = true;

	private double snapTolerance;

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"build_graph",
				this.getClass().getClassLoader()
						.getResource("images/build_graph.png"));

		// ExtensionPoints extensionPoints =
		// ExtensionPointsSingleton.getInstance();
		// ((ExtensionPoint)
		// extensionPoints.get("View_TocActions")).remove("FLyrVectEditProperties");
		// ((ExtensionPoint)
		// extensionPoints.get("View_TocActions")).remove("FLyrVectEditProperties2");
		// ((ExtensionPoint)
		// extensionPoints.get("View_TocActions")).put("FLyrVectEditProperties",new
		// FLyrVectEditPropertiesTocMenuEntry2());
		// extensionPoints.add("View_TocActions","FLyrVectEditProperties2",new
		// FLyrVectEditPropertiesTocMenuEntry2());

	}

	public void execute(String actionCommand) {
		IView view = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapControl = view.getMapControl();
		MapContext map = mapControl.getMapContext();
		FLayers tocLyrs = map.getLayers();
		SingleLayerIterator lyrIterator = new SingleLayerIterator(tocLyrs);
		while (lyrIterator.hasNext()) {
			FLayer lyr = lyrIterator.next();
			if ((lyr.isActive()) && (lyr instanceof FLyrVect)) {
				FLyrVect lyrVect = (FLyrVect) lyr;
				int shapeType;
				try {
					shapeType = lyrVect.getShapeType();
					if ((shapeType & FShape.LINE) == FShape.LINE) {
						if (actionCommand.equalsIgnoreCase("GENERATE_RED")) {
							generateRedNetwork(lyrVect, tocLyrs);
							return;
						}
					}
				} catch (BaseException e) {
					e.printStackTrace();
					NotificationManager.addError(e);
				}

			}
		}

	}

	private void generateNetwork(FLyrVect lyr) {
		NetworkGvTableWriter netBuilder = new NetworkGvTableWriter();
		// Por ahora, a pelo, pero hay que sacar un cuadro
		// de diálogo para hecer el mapping.
		// También un cuadro de diálogo para seleccionar
		// en qué tablas quiere escribir, y su formato
		// (dbf, postgres, etc)
		String fieldType = "tipored";
		String fieldDist = "length";
		String fieldSense = "sen";
		String fieldCost = "cost";
		try {
			netBuilder.setLayer(lyr);
			netBuilder.setFieldType(fieldType);
			netBuilder.setFieldDist(fieldDist);
			netBuilder.setFieldSense(fieldSense);
			netBuilder.setFieldCost(fieldCost);
			DbfWriter nodeWriter = new DbfWriter();
			nodeWriter.setFile(new File("c:/nodes.dbf"));

			DbfWriter edgeWriter = new DbfWriter();
			edgeWriter.setFile(new File("c:/edges.dbf"));

			netBuilder.setEdgeWriter(edgeWriter);
			netBuilder.setNodeWriter(nodeWriter);

			netBuilder.writeNetwork();
		} catch (BaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JOptionPane.showMessageDialog(null,
				PluginServices.getText(this, "done"));
	}

	class GenerateRedNetworkAfterCleanTask extends AbstractMonitorableTask
			implements IPipedTask {

		File redFile;
		NetworkFileRedWriter netBuilder;

		FLyrVect inputLayer;
		FLyrVect pseudonodes;
		FLayers tocLyrs;

		/**
		 * Constructor
		 * 
		 * @param tocLyrs
		 */
		GenerateRedNetworkAfterCleanTask(NetworkFileRedWriter netBuilder,
				FLayers tocLyrs) {
			this.netBuilder = netBuilder;
			this.tocLyrs = tocLyrs;
			setInitialStep(0);
			setDeterminatedProcess(true);
			setStatusMessage(PluginServices.getText(this,
					"Generando_red_a_partir_de_capa_lineal"));
		}

		public void run() throws Exception {
			int numShapes;
			try {
				numShapes = inputLayer.getSource().getShapeCount();
				// lo del 10 es para que termine después de
				// escribir los puntos
				setFinalStep(numShapes + 10);

			} catch (BaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			netBuilder.setLayer(inputLayer);
			netBuilder.setCancellableMonitorable(this);
			netBuilder.setRedFile(redFile);
			netBuilder.writeNetwork();
			tocLyrs.addLayer(inputLayer);
			if (pseudonodes != null)
				tocLyrs.addLayer(pseudonodes);
			enableControls(inputLayer, redFile);
		}

		public String getNote() {
			String processText = PluginServices.getText(this,
					"Procesando_linea");
			String of = PluginServices.getText(this, "de");
			return processText + " " + getCurrentStep() + " " + of + " "
					+ getFinishStep();
		}

		public void cancel() {
			setCanceled(true);
		}

		public boolean isFinished() {
			return (getCurrentStep() >= getFinalStep());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.iver.utiles.swing.threads.IPipedTask#getResult()
		 */
		public Object getResult() {
			// TODO Auto-generated method stub
			return null;
		}

		/**
		 * Implementation of PipeTask interface
		 */
		public void setEntry(Object object) {
			// The previous task of this piped task is clean geoprocess
			// whose result es FLayers with two layers
			// first layer has cleaned layer
			// and second layer has pseudonodes layer
			if (object instanceof FLayers) {
				FLayers layers = (FLayers) object;
				this.inputLayer = (FLyrVect) layers.getLayer(0);
				inputLayer.createSpatialIndex();
				this.redFile = NetworkUtils.getNetworkFile(inputLayer);
				this.pseudonodes = (FLyrVect) layers.getLayer(1);
			} else if (object instanceof FLyrVect) // no había errores
			{
				this.inputLayer = (FLyrVect) object;
				inputLayer.createSpatialIndex();
				this.redFile = NetworkUtils.getNetworkFile(inputLayer);
				this.pseudonodes = null;
			}
		}
	}

	public void enableControls(final FLyrVect layer, final File netFile)
			throws BaseException {
		int resp = JOptionPane.showConfirmDialog((Component) PluginServices
				.getMDIManager().getActiveWindow(), PluginServices.getText(
				null, "load_generated_network"), PluginServices.getText(null,
				"Network"), JOptionPane.YES_NO_OPTION);

		if (resp == JOptionPane.YES_OPTION) {
			LoadDefaultNetworkExtension ext = (LoadDefaultNetworkExtension) PluginServices
					.getExtension(LoadDefaultNetworkExtension.class);
			ext.loadNetwork(layer, netFile);
		}

		PluginServices.backgroundExecution(new Runnable() {
			public void run() {
				PluginServices.getMainFrame().enableControls();
			}
		});
	}

	public class GenerateRedNetworkTask extends AbstractMonitorableTask {
		FLyrVect layer;

		File redFile;

		NetworkFileRedWriter netBuilder;

		/**
		 * Constructor
		 */
		public GenerateRedNetworkTask(FLyrVect layer, File redFile,
				NetworkFileRedWriter netBuilder) {
			this.layer = layer;
			try {
				// if (layer.isSpatiallyIndexed()) {
				layer.setISpatialIndex(NetworkUtils.createJtsQuadtree(layer));
				// }

				this.redFile = redFile;
				this.netBuilder = netBuilder;
				setInitialStep(0);
				int numShapes;

				numShapes = layer.getSource().getShapeCount();
				// lo del 10 es porque escribimos los nodos después de
				// los tramos.
				setFinalStep(numShapes + 10);
				setDeterminatedProcess(true);
				setStatusMessage(PluginServices.getText(this,
						"Generando_red_a_partir_de_capa_lineal"));
			} catch (BaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void run() throws Exception {
			netBuilder.setLayer(layer);
			netBuilder.setCancellableMonitorable(this);
			netBuilder.setRedFile(redFile);
			netBuilder.writeNetwork();
			enableControls(layer, redFile);
		}

		public String getNote() {
			String processText = PluginServices.getText(this,
					"Procesando_linea");
			String of = PluginServices.getText(this, "de");
			return processText + " " + getCurrentStep() + " " + of + " "
					+ getFinishStep();
		}

		public void cancel() {
			setCanceled(true);
		}

		public Object getResult() {
			return null;

		}

		public void setEntry(Object object) {
			this.layer = (FLyrVect) object;
		}
	}

	/**
	 * It returns a geoprocess to make a CLEAN of the input layer
	 */
	private LineSnapGeoprocess createCleanGeoprocess(FLyrVect lineLyr) {
		File outputFile = null;
		JOptionPane.showMessageDialog(null, PluginServices.getText(null,
				"Especifique_fichero_shp_resultante"), PluginServices.getText(
				null, "Fichero_para_capa_corregida"),
				JOptionPane.INFORMATION_MESSAGE);
		JFileChooser jfc = new JFileChooser();
		SimpleFileFilter filterShp = new SimpleFileFilter("shp",
				PluginServices.getText(this, "shp_files"));
		jfc.setFileFilter(filterShp);
		if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File newFile = jfc.getSelectedFile();
			String path = newFile.getAbsolutePath();
			if (newFile.exists()) {
				int resp = JOptionPane.showConfirmDialog(
						(Component) PluginServices.getMainFrame(),
						PluginServices.getText(this,
								"fichero_ya_existe_seguro_desea_guardarlo"),
						PluginServices.getText(this, "guardar"),
						JOptionPane.YES_NO_OPTION);
				if (resp != JOptionPane.YES_OPTION) {
					return null;
				}
			}// if
			if (!(path.toLowerCase().endsWith(".shp"))) {
				path = path + ".shp";
			}
			outputFile = new File(path);
		} else {
			return null;
		}
		// LineCleanGeoprocess geoprocess = new LineCleanGeoprocess(lineLyr);
		LineSnapGeoprocess geoprocess = new LineSnapGeoprocess(lineLyr);
		geoprocess.setTolerance(snapTolerance);
		SHPLayerDefinition definition = (SHPLayerDefinition) geoprocess
				.createLayerDefinition();
		definition.setFile(outputFile);
		ShpSchemaManager schemaManager = new ShpSchemaManager(
				outputFile.getAbsolutePath());
		IWriter writer = null;
		try {
			int shapeType = definition.getShapeType();
			if (shapeType != XTypes.MULTI) {
				writer = new ShpWriter();
				((ShpWriter) writer).setFile(definition.getFile());
				writer.initialize(definition);
			} else {
				writer = new MultiShpWriter();
				((MultiShpWriter) writer).setFile(definition.getFile());
				writer.initialize(definition);
			}
		} catch (Exception e1) {
			String error = PluginServices.getText(this,
					"Error_escritura_resultados");
			String errorDescription = PluginServices.getText(this,
					"Error_preparar_escritura_resultados");
			return null;
		}
		geoprocess.setResultLayerProperties(writer, schemaManager);
		HashMap params = new HashMap();
		params.put("layer_selection", new Boolean(false));
		params.put("onlysnapnodes", new Boolean(onlySnapNodes));
		// boolean createLayerWithError = true;
		// params.put("createlayerswitherrors", new
		// Boolean(createLayerWithError));

		try {
			geoprocess.setParameters(params);
			geoprocess.checkPreconditions();
			return geoprocess;

		} catch (GeoprocessException e) {
			String error = PluginServices.getText(this, "Error_ejecucion");
			String errorDescription = PluginServices.getText(this,
					"Error_fallo_geoproceso");
			return null;
		}

	}

	private void generateRedNetwork(FLyrVect lyr, FLayers tocLyrs) {

		NetworkFileRedWriter netBuilder = new NetworkFileRedWriter();
		// Por ahora, a pelo, pero hay que sacar un cuadro
		// de diálogo para hecer el mapping.
		// También un cuadro de diálogo para seleccionar
		// en qué tablas quiere escribir, y su formato
		// (dbf, postgres, etc)

		ImageIcon icon = new ImageIcon(this.getClass().getClassLoader()
				.getResource("images/net-wizard-logo.jpg"));

		NetWizard wiz = new NetWizard(icon, lyr);
		PluginServices.getMDIManager().addWindow(wiz);
		if (!wiz.wasFinishPressed())
			return;
		// try {
		String fieldType = wiz.getFieldType();
		String fieldLength = wiz.getFieldLength();
		String fieldCost = wiz.getFieldCost();
		String fieldSense = wiz.getFieldSense();

		netBuilder.setLayer(lyr);
		netBuilder.setFieldType(fieldType);
		netBuilder.setFieldDist(fieldLength);
		netBuilder.setFieldSense(fieldSense);
		netBuilder.setFieldCost(fieldCost);
		netBuilder.setDigitalizationDirection(wiz.getSenseDigitalization());
		netBuilder.setReverseDigitalizationDirection(wiz
				.getSenseReverseDigitalization());
		File redFile = wiz.getNetworkFile();

		boolean applySnap = wiz.getApplySnapTolerance();
		if (applySnap) {
			snapTolerance = wiz.getSnapTolerance();
			netBuilder.setSnapTolerance(snapTolerance);
		}

		boolean cleanOrigLyr = wiz.getCleanOriginalLayer();
		LineSnapGeoprocess clean = null;
		if (cleanOrigLyr) {
			clean = createCleanGeoprocess(lyr);
			if (clean == null)
				return;

		}
		if (clean != null) {
			// we wont start the process of network creation
			// until clean geoprocess will be finished
			IPipedTask cleanTask = (IPipedTask) clean.createTask();
			GenerateRedNetworkAfterCleanTask task = new GenerateRedNetworkAfterCleanTask(
					netBuilder, tocLyrs);

			PipeTask pipe = new PipeTask(cleanTask, (IPipedTask) task);

			PluginServices.cancelableBackgroundExecution(pipe);
			// PluginServices.cancelableBackgroundExecution(task);

		} else {
			GenerateRedNetworkTask task = new GenerateRedNetworkTask(lyr,
					redFile, netBuilder);
			PluginServices.cancelableBackgroundExecution(task);
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		IWindow f = PluginServices.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			FLayer[] activeLayers = mapa.getLayers().getActives();
			if (activeLayers.length > 0)
				if (activeLayers[0] instanceof FLyrVect) {
					FLyrVect lyrVect = (FLyrVect) activeLayers[0];
					int shapeType;
					try {
						if (!lyrVect.isAvailable())
							return false;

						shapeType = lyrVect.getShapeType();
						if ((shapeType & FShape.LINE) == FShape.LINE)
							// if (shapeType == FShape.LINE)
							return true;
					} catch (BaseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
		return false;

	}

	public IPreference[] getPreferencesPages() {
		return thePreferencePages;
	}

}
