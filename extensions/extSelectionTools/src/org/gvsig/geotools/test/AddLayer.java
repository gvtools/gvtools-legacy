package org.gvsig.geotools.test;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class AddLayer extends Extension {

	@Override
	public void initialize() {
	}

	@Override
	public void execute(String actionCommand) {
		try {
			View view = (View) PluginServices.getMDIManager().getActiveWindow();
			MapContext context = view.getMapControl().getMapContext();
			String file = "/home/victorzinho/Descargas/vias.shp";
			// String file = "/home/victorzinho/workspace/"
			// + "cursos/gvsig_2012/datos/municipios.shp";
			FLyrVect layer = new GTFLyrVect(new File(file));
			layer.setName("municipios");
			layer.setProjection(null);
			layer.setLegend(LegendFactory.createSingleSymbolLegend(layer
					.getShapeType()));
			context.getLayers().addLayer(layer);
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
