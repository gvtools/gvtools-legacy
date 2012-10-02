package org.gvsig.hyperlink.actions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.hyperlink.AbstractActionManager;
import org.gvsig.hyperlink.AbstractHyperLinkPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;


public class LoadRasterLayer extends AbstractActionManager implements Serializable{
	protected static final String actionCode = "Load_Raster_Layer";

	public AbstractHyperLinkPanel createPanel(URI doc)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	public String getActionCode() {
		return actionCode;
	}

	public boolean hasPanel() {
		return false;
	}

	public void showDocument(URI doc) {
		IWindow activeWindow = PluginServices.getMDIManager().getActiveWindow();
		if (activeWindow instanceof BaseView) {
			BaseView view = (BaseView) activeWindow;
			String fileName = null;
			if (doc.isAbsolute()) {
				try {
					fileName = new File(doc).getCanonicalPath();
				} catch (MalformedURLException e) {
					PluginServices.getLogger().warn("Hyperlink - Malformed URL", e);
				} catch (IOException e) {
					PluginServices.getLogger().warn("Hyperlink - Malformed URL", e);
				}
			}
			if (fileName==null || fileName.equals("")) {
				return;
			}
			String viewName = PluginServices.getMDIManager().getWindowInfo(activeWindow).getTitle();
			try {
				// assume that Raster projection has same projection as view, because the user has no opportunity to choose the projection
//				FLayer newLayer = LayerFactory.createLayer(fileName, "gvSIG Image Driver",
//						new File(fileName), view.getProjection());
				FLayer newLayer = FLyrRasterSE.createLayer(fileName, fileName,
						view.getCrs());
				view.getMapControl().getMapContext().getLayers().addLayer(newLayer);
			} catch (LoadLayerException e) {
				PluginServices.getLogger().warn("Hyperlink - Error loading raster layer", e);
			}
		}
	}

	public String getDescription() {
		return PluginServices.getText(this, "Loads_raster_layers_in_gvSIG");
	}

	public String getName() {
		return PluginServices.getText(this, "Load_Raster_Layer");
	}
}
