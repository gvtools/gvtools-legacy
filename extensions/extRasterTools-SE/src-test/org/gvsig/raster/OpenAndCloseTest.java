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
package org.gvsig.raster;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.buffer.WriterBufferServer;
import org.gvsig.raster.dataset.FileNotOpenException;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.grid.filter.FilterTypeException;
import org.gvsig.raster.grid.filter.RasterFilterListManager;
import org.gvsig.raster.grid.filter.enhancement.BrightnessContrastListManager;
import org.gvsig.raster.grid.filter.enhancement.EnhancementStretchListManager;
import org.gvsig.raster.grid.filter.enhancement.LinearStretchParams;
import org.gvsig.raster.hierarchy.IStatistics;
import org.gvsig.raster.util.process.ClippingProcess;
import org.gvsig.rastertools.clipping.ClippingBaseTest;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * Test de pruebas del proceso de recorte
 * https://gvsig.org/web/docdev/docs/desarrollo/plugins/raster-tools/funcionalidades/recorte-de-raster/caracteristicas?portal_status_message=Changes%20saved.
 * 
 * 07/05/2008
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class OpenAndCloseTest extends ClippingBaseTest {
		//ulx, lrx, lry, uly
		protected int[]                coords             = new int[]{0, 4000, 4000, 0};
		
		/*
		 * (non-Javadoc)
		 * @see junit.framework.TestCase#setUp()
		 */
		//public void setUp() {
		public static void main(String[] args) {
			System.err.println("******************************************");
			System.err.println("*** UniqueProcessTest running... ***");
			System.err.println("******************************************");
			OpenAndCloseTest t = new OpenAndCloseTest();
			t.test();
		}
		
		//public void testStack() {
		public void test() {
			for (int i = 0; i < 1000; i++) {
				FLyrRasterSE layer = openLayer("./test-images/prueba.jpg");
				
				/*try {
					clippingProcess(new int[]{0, 1, 2}, false, new int[]{0, 869, 869, 0});
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				
				/*try {
					clippingProcessReal(new int[]{0, 1, 2}, false, new double[]{0, 869, 869, 0});
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
				draw(layer);
				
				layer.setRemoveRasterFlag(true);
				layer.getDataSource().close();
				layer.getRender().free();
				
				Runtime r = Runtime.getRuntime();

				System.err.println("Memoria Total: " + (r.totalMemory() / 1024) +"KB");
				System.err.println("Memoria Usada: " + ((r.totalMemory() - r.freeMemory()) / 1024) +"KB");
				System.err.println("Memoria Libre: " + (r.freeMemory() / 1024) +"KB");
				System.err.println("Memoria MaxMemory: " + (r.maxMemory() / 1024) +"KB");
				System.out.println("**************************************************");
			}
		}
		
		private void draw(FLyrRasterSE layer) {
			RasterFilterListManager filterManager = new RasterFilterListManager(layer.getRenderFilterList());
			IStatistics stats = layer.getDataSource().getStatistics();
			
			EnhancementStretchListManager elm = new EnhancementStretchListManager(filterManager);
			BrightnessContrastListManager br = new BrightnessContrastListManager(filterManager);
			
			try {
				stats.calcFullStatistics();
				elm.addEnhancedStretchFilter(LinearStretchParams.createStandardParam(layer.getRenderBands(), 0.0, stats, false), 
											stats, 
											layer.getRender().getRenderBands(), 
											false);
				br.addBrightnessFilter(80);
				br.addContrastFilter(56);
				layer.setTransparency(25);
			} catch (FileNotOpenException e) {
				//No podemos aplicar el filtro
			} catch (RasterDriverException e) {
				//No podemos aplicar el filtro
			} catch (FilterTypeException e) {
			} catch (InterruptedException e) {
			}
			
			ViewPort vp = new ViewPort(null);
			vp.setImageSize(new Dimension(1118, 662));
			vp.setExtent(new Rectangle2D.Double(-346.88, 0, 1563.762, 870));
			vp.setProjection(null);
			BufferedImage bi = new BufferedImage(1118, 662, BufferedImage.TYPE_INT_ARGB);
			Cancellable cancel = new Cancellable() {
				public boolean isCanceled() {
					return false;
				}

				public void setCanceled(boolean canceled) {}
			};
			try {
				layer.draw(bi, (Graphics2D)bi.getGraphics(), vp, cancel, 5342.0);
			} catch (ReadDriverException e) {
			}
		}
		 
		/**
		 * Proceso de recorte de la imagen en secuencial.
		 * @param drawBands
		 * @param onePerBand
		 * @param interp
		 */
		protected ClippingProcess clippingProcessPixel(int[] drawBands, boolean onePerBand, int[] coords) {
			ClippingProcess clippingProcess = new ClippingProcess();
			clippingProcess.addParam("viewname", null);
			clippingProcess.addParam("pixelcoordinates", coords);
			clippingProcess.addParam("filename", getFileTemp());
			clippingProcess.addParam("datawriter", new WriterBufferServer());
			clippingProcess.addParam("layer", lyr);
			clippingProcess.addParam("drawablebands", drawBands);
			clippingProcess.addParam("onelayerperband", new Boolean(onePerBand));
			clippingProcess.addParam("affinetransform", new AffineTransform());
			clippingProcess.addParam("resolution", new int[]{870, 870});
			try {
				clippingProcess.execute();
			} catch (InterruptedException e) {
			}
			return clippingProcess;
		}
		
		/**
		 * Proceso de recorte de la imagen en secuencial.
		 * @param drawBands
		 * @param onePerBand
		 * @param interp
		 */
		protected ClippingProcess clippingProcessReal(int[] drawBands, boolean onePerBand, double[] coords) {
			ClippingProcess clippingProcess = new ClippingProcess();
			clippingProcess.addParam("viewname", null);
			clippingProcess.addParam("realcoordinates", coords);
			clippingProcess.addParam("filename", getFileTemp());
			clippingProcess.addParam("datawriter", new WriterBufferServer());
			clippingProcess.addParam("layer", lyr);
			clippingProcess.addParam("drawablebands", drawBands);
			clippingProcess.addParam("onelayerperband", new Boolean(onePerBand));
			clippingProcess.addParam("affinetransform", new AffineTransform());
			clippingProcess.addParam("resolution", new int[]{870, 870});
			try {
				clippingProcess.execute();
			} catch (InterruptedException e) {
			}
			return clippingProcess;
		}
		
}
