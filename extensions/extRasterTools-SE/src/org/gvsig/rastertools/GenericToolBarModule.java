/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.rastertools;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.raster.util.extensionPoints.ExtensionPoint;
import org.gvsig.rastertools.analysisview.ViewRasterAnalysisTocMenuEntry;
import org.gvsig.rastertools.clipping.ClippingTocMenuEntry;
import org.gvsig.rastertools.colortable.ColorTableTocMenuEntry;
import org.gvsig.rastertools.enhanced.EnhancedTocMenuEntry;
import org.gvsig.rastertools.filter.FilterTocMenuEntry;
import org.gvsig.rastertools.generictoolbar.GenericToolBarMenuItem;
import org.gvsig.rastertools.generictoolbar.GenericToolBarPanel;
import org.gvsig.rastertools.geolocation.GeoLocationTocMenuEntry;
import org.gvsig.rastertools.histogram.HistogramTocMenuEntry;
import org.gvsig.rastertools.overviews.OverviewsTocMenuEntry;
import org.gvsig.rastertools.properties.RasterPropertiesTocMenuEntry;
import org.gvsig.rastertools.reproject.ReprojectTocMenuEntry;
import org.gvsig.rastertools.roi.ROIManagerTocMenuEntry;
import org.gvsig.rastertools.saveas.SaveAsTocMenuEntry;
import org.gvsig.rastertools.saveraster.SaveRasterTocMenuEntry;
import org.gvsig.rastertools.selectrasterlayer.SelectLayerTocMenuEntry;
import org.gvsig.rastertools.vectorizacion.VectorizationTocMenuEntry;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiFrame.MDIFrame;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
/**
 * Extension para la barra de herramientas generica
 * 
 * @version 13/02/2008
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class GenericToolBarModule extends Extension {
	private GenericToolBarPanel toolBar = null;
	
	/**
	 * Crea y devuelve la barra de herramientas
	 * @return
	 */
	private GenericToolBarPanel getGenericToolBarPanel() {
		if (toolBar == null) {
			MDIFrame f = (MDIFrame) PluginServices.getMainFrame();
			if (f == null)
				return null;
			for (int i = 0; i < f.getContentPane().getComponentCount(); i++) {
				if (f.getContentPane().getComponent(i) instanceof JPanel) {
					JPanel panel = (JPanel) f.getContentPane().getComponent(i);
					for (int j = 0; j < panel.getComponentCount(); j++) {
						if (panel.getComponent(j) instanceof JToolBar) {
							toolBar = new GenericToolBarPanel();
							panel.add(toolBar, BorderLayout.PAGE_START);
							return toolBar;
						}
					}
				}
			}
		} else {
			toolBar.setPreferredSize(new Dimension(300, getToolbarHeight()));
		}
		
		return toolBar;
	}

	/**
	 * Obtenemos el alto de cualquier toolbar que este visible en gvSIG y no sea
	 * nuestro para poder asignarselo al GenericToolBar como PreferredSize. En
	 * caso de no encontrar ninguno que cumpla las condiciones, se devolverá 24
	 * @return
	 */
	private int getToolbarHeight() {
		if ((PluginServices.getMainFrame() == null) ||
				(PluginServices.getMainFrame().getToolbars() == null) ||
				(PluginServices.getMainFrame().getToolbars().length <= 0))
			return 24;
		
		for (int i = 0; i < PluginServices.getMainFrame().getToolbars().length; i++) {
			if ((PluginServices.getMainFrame().getToolbars()[i].getHeight() > 16) &&
					((Object) PluginServices.getMainFrame().getToolbars()[i] != (Object) toolBar))
				return PluginServices.getMainFrame().getToolbars()[i].getHeight();
		}
		return 24;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
		
		// Creación del punto de extensión para registrar paneles en el cuadro de propiedades.
		ExtensionPoint point = ExtensionPoint.getExtensionPoint("GenericToolBarGroup");
		point.setDescription("Punto de extension para los grupos de menus del GenericToolBarPanel");

		point.register("RasterLayer", new GenericToolBarMenuItem(RasterToolsUtil.getText(this, "capa_raster"), PluginServices.getIconTheme().get("layer-icon")));
		point.register("RasterProcess", new GenericToolBarMenuItem(RasterToolsUtil.getText(this, "process_raster"), PluginServices.getIconTheme().get("process-icon")));
		point.register("GeoRaster", new GenericToolBarMenuItem(RasterToolsUtil.getText(this, "transformaciones_geograficas"), PluginServices.getIconTheme().get("transgeo-icon")));
		point.register("RasterExport", new GenericToolBarMenuItem(RasterToolsUtil.getText(this, "raster_export"), PluginServices.getIconTheme().get("raster-export")));
		
		point = ExtensionPoint.getExtensionPoint("GenericToolBarMenu");
		point.setDescription("Punto de extension para los submenus del GenericToolBarPanel");
		point.register("RasterProperties", RasterPropertiesTocMenuEntry.getSingleton());
		point.register("SelectLayer", SelectLayerTocMenuEntry.getSingleton());
		point.register("HistogramPanel", HistogramTocMenuEntry.getSingleton());
		point.register("ViewColorTable", ColorTableTocMenuEntry.getSingleton());
		point.register("Overviews", OverviewsTocMenuEntry.getSingleton());
		point.register("RoisManager", ROIManagerTocMenuEntry.getSingleton());
		point.register("ViewRasterAnalysis", ViewRasterAnalysisTocMenuEntry.getSingleton());
		
		point.register("SaveAs", SaveAsTocMenuEntry.getSingleton());
		point.register("ClippingPanel", ClippingTocMenuEntry.getSingleton());
		point.register("SaveRaster", SaveRasterTocMenuEntry.getSingleton());
		
		point.register("FilterPanel", FilterTocMenuEntry.getSingleton());
		point.register("EnhancedPanel", EnhancedTocMenuEntry.getSingleton());
		point.register("GeoLocation", GeoLocationTocMenuEntry.getSingleton());
		point.register("Vectorization", VectorizationTocMenuEntry.getSingleton());
		ReprojectTocMenuEntry menuEntry = ReprojectTocMenuEntry.getSingleton();
		point.register(menuEntry.getText(), menuEntry);

		if (getGenericToolBarPanel() != null)
			getGenericToolBarPanel().reloadMenuGroup();
	}
	
	/**
	 * Registra los iconos a utilizar en la botonera.
	 */
	private void registerIcons() {
		PluginServices.getIconTheme().register(
				"layer-icon",
				this.getClass().getClassLoader().getResource("images/rasterlayer.png")
			);
		PluginServices.getIconTheme().register(
				"process-icon",
				this.getClass().getClassLoader().getResource("images/icon_process.gif")
			);
		PluginServices.getIconTheme().register(
				"transgeo-icon",
				this.getClass().getClassLoader().getResource("images/rastertransgeo.gif")
			);
		PluginServices.getIconTheme().registerDefault(
				"raster-export",
				this.getClass().getClassLoader().getResource("images/raster-export.png")
			);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return false;
	}

	/**
	 * Establece si la barra de herramientas esta visible
	 * @param enabled
	 */
	private void setToolBarVisible(boolean enabled) {
		if (getGenericToolBarPanel() == null)
			return;

		toolBar.setVisible(enabled);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager().getActiveWindow();
		if (f == null) {
			setToolBarVisible(false);
			return false;
		}

		if (f instanceof BaseView) {
			BaseView vista = (BaseView) f;
			IProjectView model = vista.getModel();
			MapContext mapa = model.getMapContext();
			if (mapa.getLayers().getLayersCount() > 0) {
				setToolBarVisible(true);
				if (getGenericToolBarPanel() != null) {
					getGenericToolBarPanel().setLayers(mapa.getLayers());
				}
				return true;
			}
		}

		setToolBarVisible(false);
		return false;			
	}
	
	public void execute(String actionCommand) {}
}