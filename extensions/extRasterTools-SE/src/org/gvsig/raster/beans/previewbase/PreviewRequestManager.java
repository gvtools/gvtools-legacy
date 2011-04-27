/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2007 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.raster.beans.previewbase;

import java.awt.Dimension;
import java.awt.Graphics2D;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.IRasterLayerActions;
import org.gvsig.gui.beans.imagenavigator.IClientImageNavigator;
import org.gvsig.gui.beans.imagenavigator.ImageUnavailableException;
import org.gvsig.raster.datastruct.Extent;
import org.gvsig.raster.grid.filter.FilterTypeException;
import org.gvsig.raster.hierarchy.IRasterRendering;
import org.gvsig.raster.util.RasterToolsUtil;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayer;

/**
 * Gestor de visualización de preview. Se encarga del repintado de la imagen 
 * de la previsualización
 * 
 * 19/02/2008
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class PreviewRequestManager implements IClientImageNavigator {
	private PreviewBasePanel        previewBasePanel  = null;
	private FLyrRasterSE            previewLayer      = null;
	private IPreviewRenderProcess   renderProcess     = null; 

	/**
	 * Construye un ColorTableListener
	 * @param 
	 */
	public PreviewRequestManager(	PreviewBasePanel panel, 
									IPreviewRenderProcess renderProcess, 
									FLyrRasterSE layer) {
		this.previewBasePanel = panel;
		this.renderProcess = renderProcess;
		setLayer(layer);
	}

	/**
	 * Asigna la capa raster de la vista
	 * @param fLayer
	 */
	private void setLayer(FLayer fLayer) {
		if (fLayer instanceof FLyrRasterSE) {
			FLyrRasterSE ly = ((FLyrRasterSE) fLayer);
			try {
				if(ly.isActionEnabled(IRasterLayerActions.REMOTE_ACTIONS))
					previewLayer = (FLyrRasterSE)ly.getFileLayer();//ly;
				else
					previewLayer = (FLyrRasterSE) fLayer.cloneLayer();					
			} catch (Exception e) {
				RasterToolsUtil.messageBoxError("preview_not_available", previewBasePanel, e);
			}
		}
	}

	/**
	 * Cierra la capa abierta para previsualización
	 */
	public void closePreviewLayer() {
		if (previewLayer != null) {
			previewLayer.setRemoveRasterFlag(true);
			previewLayer.removeLayerListener(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.imagenavigator.IClientImageNavigator#drawImage(java.awt.Graphics2D,
	 *      double, double, double, double, double, int, int)
	 */
	public void drawImage(Graphics2D g, double x1, double y1, double x2, double y2, double zoom, int width, int height) 
		throws ImageUnavailableException {
		if (previewLayer == null || !(previewLayer instanceof IRasterRendering))
			throw new ImageUnavailableException(PluginServices.getText(this, "error_dont_exists_layer"));

		IRasterRendering rendering = ((IRasterRendering) previewLayer);

		// Inicializo el ViewPort
		ViewPort vp = new ViewPort(null);
		Extent extent = new Extent(x1, y1, x2, y2);
		vp.setExtent(extent.toRectangle2D());
		vp.setImageSize(new Dimension(width, height));

		rendering.getRenderFilterList().pushStatus();
		try {
			renderProcess.process(rendering);
		} catch (FilterTypeException e1) {
			RasterToolsUtil.debug("error_adding_filters", this, e1);
			throw new ImageUnavailableException(PluginServices.getText(this, "error_adding_filters"));
		} catch (ImageUnavailableException e3) {
			// No guardamos el mensaje en el log porque solo sirve para visualizarlo en el preview
			throw new ImageUnavailableException(e3.getMessage());
		} catch (Exception e2) {
			RasterToolsUtil.debug("error_adding_filters", this, e2);
			throw new ImageUnavailableException(PluginServices.getText(this, "error_adding_filters"));
		}

		try {
			previewLayer.draw(null, g, vp, null, 1.0);
		} catch (ReadDriverException e) {
			RasterToolsUtil.debug("error_preview_render", this, e);
			throw new ImageUnavailableException(PluginServices.getText(this, "error_preview_render"));
		} catch (Exception e2) {
			RasterToolsUtil.debug("error_preview_render", this, e2);
			throw new ImageUnavailableException(PluginServices.getText(this, "error_preview_render"));
		}
		rendering.getRenderFilterList().popStatus();
	}
}