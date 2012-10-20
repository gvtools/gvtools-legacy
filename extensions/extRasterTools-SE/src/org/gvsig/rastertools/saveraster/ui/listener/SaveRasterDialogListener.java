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
package org.gvsig.rastertools.saveraster.ui.listener;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.IRasterLayerActions;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelEvent;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelListener;
import org.gvsig.gui.beans.propertiespanel.PropertiesPanel;
import org.gvsig.raster.Configuration;
import org.gvsig.raster.RasterLibrary;
import org.gvsig.raster.buffer.IQueryableRaster;
import org.gvsig.raster.dataset.GeoRasterWriter;
import org.gvsig.raster.dataset.NotSupportedExtensionException;
import org.gvsig.raster.dataset.Params;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.raster.util.RasterUtilities;
import org.gvsig.rastertools.saveraster.operations.RasterizerLayer;
import org.gvsig.rastertools.saveraster.operations.SaveRasterActions;
import org.gvsig.rastertools.saveraster.operations.SaveRasterProcess;
import org.gvsig.rastertools.saveraster.ui.SaveRasterDialog;
import org.gvsig.rastertools.saveraster.ui.SaveRasterPanel;
import org.gvsig.rastertools.saveraster.ui.properties.WriterPropertiesDialog;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayers;

/**
 * Clase que gestiona los eventos de los botones del dialogo de Salvar a raster.
 * 
 * @version 19/04/2007
 * @author Nacho Brodin (nachobrodin@gmail.com)
 * 
 */
public class SaveRasterDialogListener implements ButtonsPanelListener {
	private SaveRasterDialog dialog = null;
	private FLayers layers = null;
	private MapControl mapCtrl = null;
	private GeoRasterWriter writer = null;

	/**
	 * Constructor
	 * 
	 * @param dialog
	 */
	public SaveRasterDialogListener(SaveRasterDialog dialog, FLayers layers,
			MapControl mapCtrl) {
		this.dialog = dialog;
		this.layers = layers;
		this.mapCtrl = mapCtrl;

		dialog.addButtonPressedListener(this);

		// Captura de eventos para el botón de propiedades
		((SaveRasterPanel) dialog.getControlsPanel()).getBProperties()
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						propsButtonActionPerformed(evt);
					}
				});
	}

	/**
	 * Eventos para los botones de aplicar, aceptar y cancelar
	 * 
	 * @param e
	 */
	public void actionButtonPressed(ButtonsPanelEvent e) {
		switch (e.getButton()) {
		case ButtonsPanel.BUTTON_APPLY:
			acceptButtonActionPerformed(e);
			break;
		case ButtonsPanel.BUTTON_CLOSE:
			dialog.closeJDialog();
			break;
		}
	}

	/**
	 * Acciones realizadas al aceptar.
	 * 
	 * @param e
	 * @return true si se ha completado la operación de escritura y false si no
	 *         se ha hecho
	 */
	private boolean acceptButtonActionPerformed(ButtonsPanelEvent e) {
		SaveRasterPanel controlPanel = dialog.getControlsPanel();

		String fName = dialog.getDataInputListener().getFileName();
		Dimension dimension = new Dimension((int) dialog.getDataInputListener()
				.getWidthInPixels(), (int) dialog.getDataInputListener()
				.getHeightInPixels());

		// Limitamos el tamaño
		if (dialog.getDataInputListener().getWidthInPixels() > 20000
				|| dialog.getDataInputListener().getHeightInPixels() > 20000) {
			RasterToolsUtil.messageBoxInfo("output_file_too_big", dialog);
		}

		// Comprobamos que el en la ruta de destino tengamos permisos de
		// escritura
		File f = new File(fName);
		if (f.exists())
			if (!RasterToolsUtil.messageBoxYesOrNot("raster_error_file_exists",
					dialog))
				return false;

		f = new File(fName.substring(0, fName.lastIndexOf(File.separator)));

		/*
		 * if(f.exists() && f.isDirectory() && !f.canWrite()) {
		 * RasterToolsUtil.messageBoxError("error_file_not_writable", dialog);
		 * return false; }
		 */

		dialog.getDataInputListener().resetFileName();
		dialog.getDataInputListener().enableButtons();

		double lrX = Double.parseDouble(controlPanel.getTInfDerX().getValue());
		double lrY = Double.parseDouble(controlPanel.getTInfDerY().getValue());
		double ulX = Double.parseDouble(controlPanel.getTSupIzqX().getValue());
		double ulY = Double.parseDouble(controlPanel.getTSupIzqY().getValue());

		double width = 0;
		if (ulX > lrX)
			width = (double) (ulX - lrX);
		else
			width = (double) (lrX - ulX);

		double height = 0;
		if (ulY > lrY)
			height = (double) (ulY - lrY);
		else
			height = (double) (lrY - ulY);

		Rectangle2D ext = null;

		if (ulY < lrY)
			ext = new Rectangle2D.Double(ulX, ulY, width, height);
		else
			ext = new Rectangle2D.Double(ulX, lrY, width, height);

		// Controlamos el tamaño en caso de un jpeg2000
		long bytes = RasterUtilities.getBytesFromRaster(
				(int) dimension.getWidth(), (int) dimension.getHeight(), 0, 3);
		long maxJp2 = 13000 * 12500 * 3;
		if (fName.endsWith(".jp2")) {
			if (bytes > maxJp2) {
				RasterToolsUtil.messageBoxInfo("output_file_too_big_jpeg2000",
						null);
				return false;
			}
		}

		Params params = getWriterParams(fName);
		int blockSize = getBlockSize(params, layers);

		ViewPort viewPort = new ViewPort(layers.getCrs());
		viewPort.setBackColor(mapCtrl.getViewPort().getBackColor());
		viewPort.setImageSize(dimension);
		viewPort.setExtent(ext);

		// Creamos el servidor de datos de la vista
		RasterizerLayer rasterizerLayer = new RasterizerLayer(layers, viewPort,
				blockSize);
		ArrayList<FLyrRasterSE> remoteLayerList = new ArrayList<FLyrRasterSE>();

		// Comprueba si hay capas remotas en la lista.
		// Esto es util porque el proceso lo tiene en cuenta para salvar a
		// jpg2000.
		boolean remoteLayers = false;
		for (int i = 0; i < layers.getLayersCount(); i++) {
			if (layers.getLayer(i) instanceof FLyrRasterSE
					&& ((FLyrRasterSE) layers.getLayer(i))
							.isActionEnabled(IRasterLayerActions.REMOTE_ACTIONS)) {
				remoteLayerList.add(((FLyrRasterSE) layers.getLayer(i)));
				remoteLayers = true;
			}
		}

		// Creamos la clase con el proceso y lo lanzamos
		SaveRasterProcess saveRasterProcess = new SaveRasterProcess();
		saveRasterProcess.setActions(new SaveRasterActions());
		saveRasterProcess.addParam("viewport", viewPort);
		saveRasterProcess.addParam("remotelayers", new Boolean(remoteLayers));
		saveRasterProcess.addParam("dimension", dimension);
		saveRasterProcess.addParam("rasterizerlayer", rasterizerLayer);
		saveRasterProcess.addParam("filename", fName);
		saveRasterProcess.addParam("writerparams", params);
		if (remoteLayers)
			saveRasterProcess.addParam("layers", remoteLayerList);
		saveRasterProcess.start();

		return true;
	}

	/**
	 * Función a ejecutar cuando se pulsa el botón de propiedades.
	 * 
	 * @param e
	 *            ActionEvent
	 */
	private void propsButtonActionPerformed(ActionEvent e) {
		String name = dialog.getDataInputListener().getFileName();

		// Si no se ha seleccionado ningún fichero salimos
		if (name == null || name.equals(""))
			return;

		Params params = getWriterParams(name);
		PropertiesPanel panel = new PropertiesPanel();
		WriterPropertiesDialog dialog = new WriterPropertiesDialog(panel,
				params);
		PluginServices.getMDIManager().addWindow(dialog);
	}

	/**
	 * Obtiene los parámetros del driver de escritura. Si el driver no se ha
	 * creado aún se obtienen unos parámetros con la inicialización por defecto.
	 * Si se ha creado ya y se han modificado se devuelven los parámetros con
	 * las modificaciones. Si se cambia de driver se devolverá un WriterParams
	 * como si fuera la primera vez que se abre.
	 * 
	 * @param name
	 *            Nombre del fichero sobre el que se salva.
	 * @return WriterParams
	 */
	private Params getWriterParams(String name) {
		String ext = RasterUtilities.getExtensionFromFileName(name);
		try {
			if (writer == null) // La primera vez que se obtiene el driver
				writer = GeoRasterWriter.getWriter(name);
			else {
				String newType = GeoRasterWriter.getDriverType(ext);
				String oldType = writer.getDriverName();
				if (!newType.equals(oldType)) // Cambio de driver después de
												// haber seleccionado y
												// modificado las propiedades de
												// uno
					writer = GeoRasterWriter.getWriter(name);
			}

			if (writer == null)
				JOptionPane.showMessageDialog(
						(Component) PluginServices.getMainFrame(),
						PluginServices.getText(this, "no_driver_escritura"));

			return writer.getParams();

		} catch (NotSupportedExtensionException e1) {
			JOptionPane.showMessageDialog(
					(Component) PluginServices.getMainFrame(),
					PluginServices.getText(this, "no_driver_escritura"));
			return null;
		} catch (RasterDriverException e1) {
			JOptionPane.showMessageDialog(
					(Component) PluginServices.getMainFrame(),
					PluginServices.getText(this, "no_driver_escritura"));
			return null;
		}
	}

	/**
	 * Calculo del tamaño de bloque. Para ello comprueba si el raster está
	 * siendo tileado. Si está siendo tileado se aplica el alto del tile menor
	 * para no pedir un bloque mayor que lo que un servidor puede devolver. Si
	 * no está siendo tileado se devuelve el tamaño de bloque de los parámetros.
	 * 
	 * @param flyrs
	 *            Capas
	 * @param params
	 *            Parámetros del driver de escritura
	 * @return tamaño de bloque
	 */
	private int getBlockSize(Params params, FLayers flyrs) {

		int blockSize = Configuration.getValue("cache_blockheight",
				Integer.valueOf(RasterLibrary.blockHeight)).intValue();
		blockSize = RasterLibrary.blockHeight;

		// Recorremos todas las capas comprobando si alguna de ellas implementa
		// RasterOperations y tilea.
		// En ese caso se obtiene el ancho de bloque. El ancho de bloque total
		// será el menor obtenido.
		// Esto lo hacemos para que las capas que tilean WMS, WCS, ... no hagan
		// demasiadas peticiones al servidor
		// por tener un ancho de bloque muy pequeño de modo que el ancho del
		// bloque se ajuste al Tile menor
		// soportado por los servidores que intervienen en el salvado.
		int[] wBlock = null;
		boolean isTiling = false;
		int block = Integer.MAX_VALUE;
		for (int i = 0; i < flyrs.getLayersCount(); i++) {
			if (flyrs.getLayer(i) instanceof IQueryableRaster) {
				if (((IQueryableRaster) flyrs.getLayer(i)).isTiled()) {
					wBlock = ((IQueryableRaster) flyrs.getLayer(i))
							.getTileSize();
					if ((wBlock[0] - 1) < block) {
						block = wBlock[0] - 1;
						isTiling = true;
					}
				}
			}
		}
		if (isTiling) {
			params.changeParamValue("blocksize", String.valueOf(block));
			return block;
		}

		return blockSize;
	}
}