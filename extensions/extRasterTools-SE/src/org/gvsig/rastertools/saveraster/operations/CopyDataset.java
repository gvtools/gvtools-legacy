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
package org.gvsig.rastertools.saveraster.operations;

import java.io.File;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.gui.beans.incrementabletask.IncrementableTask;
import org.gvsig.raster.RasterProcess;
import org.gvsig.raster.buffer.BufferInterpolation;
import org.gvsig.raster.buffer.WriterBufferServer;
import org.gvsig.raster.dataset.GeoRasterWriter;
import org.gvsig.raster.dataset.NotSupportedExtensionException;
import org.gvsig.raster.dataset.Params;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.util.ExternalCancellable;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.raster.util.process.ClippingProcess;
import org.gvsig.rastertools.saveas.SaveAsActions;

import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;

/**
 * Builds a copy of a raster file
 * 
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class CopyDataset {
	private String 						fDstName          = null;
	private String 						fSrcName          = null;
	private FLyrRasterSE                src               = null;
	private RasterProcess               clippingProcess   = null;
	private IncrementableTask           incrementableTask = null; 

	/**
	 * Constructor
	 * @param src	Nombre del fichero fuente
	 * @param dst	Nombre del fichero destino
	 * @throws LoadLayerException 
	 * @throws RasterDriverException 
	 * @throws NotSupportedExtensionException 
	 */
	public CopyDataset(String src, String dst) throws LoadLayerException {
		fSrcName = src;
		fDstName = dst;
		File f = new File(fDstName);
		if(f.exists())
			f.delete();
		this.src = FLyrRasterSE.createLayer("lyr", new File(fSrcName), null);
	}
	
	/**
	 * Constructor
	 * @param src	Nombre del fichero fuente
	 * @param dst	Nombre del fichero destino
	 * @throws LoadLayerException 
	 * @throws RasterDriverException 
	 * @throws NotSupportedExtensionException 
	 */
	public CopyDataset(String src, String dst, IncrementableTask incrementableTask) throws LoadLayerException {
		fSrcName = src;
		fDstName = dst;
		this.incrementableTask = incrementableTask;
		File f = new File(fDstName);
		if(f.exists())
			f.delete();
		this.src = FLyrRasterSE.createLayer("lyr", new File(fSrcName), null);
	}
	
	/**
	 * Función que realiza la copia del dataset
	 * @throws InterruptedException 
	 */
	public void copy() throws InterruptedException {
		// Creación de parámetros
		WriterBufferServer dataWriter = new WriterBufferServer();
		int[] dValues = new int[] { 0, (int) src.getPxHeight(), (int) src.getPxWidth(), 0 };
		int[] drawableBands = new int[src.getBandCount()];
		for (int i = 0; i < src.getBandCount(); i++)
			drawableBands[i] = i;
		Params params = null;
		try {
			params = GeoRasterWriter.getWriter(fDstName).getParams();
		} catch (NotSupportedExtensionException e1) {
			RasterToolsUtil.messageBoxError("no_driver_escritura", this, e1);
		} catch (RasterDriverException e1) {
			RasterToolsUtil.messageBoxError("no_driver_escritura", this, e1);
		}

		// Lanzamiento del proceso de guardado
		clippingProcess = new ClippingProcess();
		clippingProcess.setActions(new SaveAsActions());
		if(incrementableTask != null)
			clippingProcess.addParam("cancellable", new ExternalCancellable(incrementableTask));
		clippingProcess.addParam("showenddialog", new Boolean(false));
		clippingProcess.addParam("pixelcoordinates", dValues);
		clippingProcess.addParam("filename", fDstName);
		clippingProcess.addParam("datawriter", dataWriter);
		clippingProcess.addParam("layer", src);
		clippingProcess.addParam("drawablebands", drawableBands);
		clippingProcess.addParam("colorInterpretation", src.getDataSource().getColorInterpretation());
		clippingProcess.addParam("onelayerperband", new Boolean(false));
		clippingProcess.addParam("interpolationmethod", new Integer(BufferInterpolation.INTERPOLATION_NearestNeighbour));
		clippingProcess.addParam("affinetransform", src.getAffineTransform(0));
		clippingProcess.addParam("resolution", new int[]{(int) src.getPxWidth(),
														 (int) src.getPxHeight()});
		clippingProcess.addParam("driverparams", params);
		clippingProcess.execute();
	}
	
	/**
	 * Obtiene el incremento de escritura
	 * @return
	 */
	public int getPercent() {
		if(clippingProcess != null)
			return clippingProcess.getPercent();
		return 0;
	}
}