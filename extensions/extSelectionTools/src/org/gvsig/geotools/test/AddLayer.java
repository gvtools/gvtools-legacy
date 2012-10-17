package org.gvsig.geotools.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cresques.cts.ProjectionUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceInfo;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.legend.gui.General;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LegendManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ThemeManagerWindow;

public class AddLayer extends Extension {

	@Override
	public void initialize() {
		ThemeManagerWindow.setTabEnabledForLayer(General.class,
				GTFLyrVect.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LegendManager.class,
				GTFLyrVect.class, true);
	}

	@Override
	public void execute(String actionCommand) {
		try {
			View view = (View) PluginServices.getMDIManager().getActiveWindow();
			MapContext context = view.getMapControl().getMapContext();
			// String file = "/home/victorzinho/Descargas/vias.shp";
			String path = "/home/victorzinho/workspace/"
					+ "cursos/gvsig_2012/datos/areas_agua.shp";
			File file = new File(path);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("url", file.toURI().toURL());
			DataStore store = DataStoreFinder.getDataStore(map);
			String typeName = store.getTypeNames()[0];

			FLyrVect layer = new GTFLyrVect(store.getFeatureSource(typeName));
			layer.setName("municipios");
			layer.setCrs(ProjectionUtils.getCRS("EPSG:23030"));
			layer.setLegend(LegendFactory.createSingleSymbolLegend(layer
					.getShapeType()));
			context.getLayers().addLayer(layer);

			DataSourceInfo info = new DataSourceInfo(layer.getRecordset());
			String name = file.getName().substring(0,
					file.getName().lastIndexOf('.'));
			LayerFactory.getDataSourceFactory().addSourceInfo(name, info);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "ERROR!!!!");
			e.printStackTrace();
		} catch (LegendLayerException e) {
			JOptionPane.showMessageDialog(null, "ERROR!!!!");
			e.printStackTrace();
		} catch (ReadDriverException e) {
			JOptionPane.showMessageDialog(null, "ERROR!!!!");
			e.printStackTrace();
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}
