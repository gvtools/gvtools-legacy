package org.gvsig.remotesensing;

import javax.swing.Icon;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.ILayerState;
import org.gvsig.raster.gui.IGenericToolBarMenuItem;
import org.gvsig.raster.util.extensionPoints.ExtensionPoint;
import org.gvsig.remotesensing.tasseledcap.gui.TasseledCapPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;

/**
 * Extensión para el cálculo de Trasseled Cap
 * 
 * @author Diego Guerrero Sevilla (diego.guerrero@uclm.es)
 */
public class TasseledCapExtension extends Extension implements
		IGenericToolBarMenuItem {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		// TasseledCapRasterFilterListManager.register();
		ExtensionPoint extensionPoint = ExtensionPoint
				.getExtensionPoint("GenericToolBarMenu");
		extensionPoint.register("TasseledCap", this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if (actionCommand.equals("tasseled_cap")) {
			com.iver.andami.ui.mdiManager.IWindow activeWindow = PluginServices
					.getMDIManager().getActiveWindow();

			// si la ventana activa es de tipo Vista
			if (activeWindow instanceof View) {
				TasseledCapPanel tasseledcapPanel = new TasseledCapPanel(
						(View) activeWindow);

				PluginServices.getMDIManager().addWindow(tasseledcapPanel);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f.getClass() == View.class) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			FLayers layers = mapa.getLayers();
			for (int i = 0; i < layers.getLayersCount(); i++)
				if (layers.getLayer(i) instanceof FLyrRasterSE)
					return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			return mapa.getLayers().getLayersCount() > 0;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.raster.gui.IGenericToolBarMenuItem#execute(com.iver.cit.gvsig
	 * .project.documents.view.toc.ITocItem,
	 * com.iver.cit.gvsig.fmap.layers.FLayer[])
	 */
	public void execute(ITocItem item, FLayer[] selectedItems) {
		this.execute("tasseled_cap");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.raster.gui.IGenericToolBarMenuItem#getGroup()
	 */
	public String getGroup() {
		return "RasterProcess";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.raster.gui.IGenericToolBarMenuItem#getIcon()
	 */
	public Icon getIcon() {
		return PluginServices.getIconTheme().get("blank-icon");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.raster.gui.IGenericToolBarMenuItem#getOrder()
	 */
	public int getOrder() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.raster.gui.IGenericToolBarMenuItem#getText()
	 */
	public String getText() {
		return PluginServices.getText(this, "tasseled_cap");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.raster.gui.IGenericToolBarMenuItem#isEnabled(com.iver.cit.gvsig
	 * .project.documents.view.toc.ITocItem,
	 * com.iver.cit.gvsig.fmap.layers.FLayer[])
	 */
	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		if ((selectedItems == null) || (selectedItems.length != 1))
			return false;

		if (!(selectedItems[0] instanceof ILayerState))
			return false;

		if (!((ILayerState) selectedItems[0]).isOpen())
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.raster.gui.IGenericToolBarMenuItem#isVisible(com.iver.cit.gvsig
	 * .project.documents.view.toc.ITocItem,
	 * com.iver.cit.gvsig.fmap.layers.FLayer[])
	 */
	public boolean isVisible(ITocItem item, FLayer[] selectedItems) {
		if ((selectedItems == null) || (selectedItems.length != 1))
			return false;

		if (!(selectedItems[0] instanceof FLyrRasterSE))
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.raster.gui.IGenericToolBarMenuItem#getGroupOrder()
	 */
	public int getGroupOrder() {
		return 0;
	}
}