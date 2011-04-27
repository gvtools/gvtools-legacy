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
package org.gvsig.rastertools.clipping;

import java.awt.geom.AffineTransform;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.buffer.BufferInterpolation;
import org.gvsig.raster.buffer.WriterBufferServer;
import org.gvsig.raster.dataset.GeoRasterWriter;
import org.gvsig.raster.dataset.NotSupportedExtensionException;
import org.gvsig.raster.dataset.Params;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.dataset.properties.DatasetColorInterpretation;
import org.gvsig.raster.util.process.ClippingProcess;

/**
 * Test de pruebas del proceso de recorte de imagenes Jpg. 
 * El test genera multiples tiles en el directorio temporal
 * https://gvsig.org/web/docdev/docs/desarrollo/plugins/raster-tools/funcionalidades/recorte-de-raster/caracteristicas?portal_status_message=Changes%20saved.
 * 
 * 03/03/2009
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class JpegClippingProcessTest extends ClippingBaseTest {
		protected String               testImg  = "/home/nacho/images1/EcwJp2/valencia2002.ecw";//baseDir + "03AUG23153350-M2AS-000000122423_01_P001-BROWSE.jpg";
		//protected String               testImg  = "/home/nacho/images1/MultiplesFicheros/ComunidadValenciana/p198r033_7t20010601_z31_nn10.tif";
		private int                    nTiles   = 800;
		private int                    tileSize = 1000;
		
		/*
		 * (non-Javadoc)
		 * @see junit.framework.TestCase#setUp()
		 */
		public void setUp() {
			resetTime();
			System.err.println("**********************************************");
			System.err.println("*** Jpeg    ClippingProcessTest running... ***");
			System.err.println("**********************************************");
		}
		
		public void testStack() {
			FLyrRasterSE lyr = openLayer(testImg);
			int maxInitX = (int)lyr.getPxWidth() - tileSize;
			int maxInitY = (int)lyr.getPxHeight() - tileSize;
			
			for (int i = 0; i < nTiles; i++) {
				int xRandom = (int)(Math.random() * maxInitX);
				int yRandom = (int)(Math.random() * maxInitY);
				ClippingProcess process = clippingProcess(new int[]{0, 1, 2}, 
						false, 
						BufferInterpolation.INTERPOLATION_Undefined, 
						getColorInterpretation(3), 
						100, 
						100,
						new int[]{xRandom, yRandom + tileSize, xRandom + tileSize, yRandom});
				try {
					System.out.println("Tile:" + i + " : " + xRandom + "," + (yRandom + tileSize) + "," + (xRandom + tileSize)  + "," +  yRandom);
					process.execute();
				} catch (InterruptedException e) {
				}
			}
						
			if(lyr != null)
				lyr.removeLayerListener(null);
			
			System.err.println("**********************************************");
			System.err.println("*** Time:" + getTime());
			System.err.println("*** Jpeg    ClippingProcessTest ending...  ***");
			System.err.println("**********************************************");	
		}
		
		/**
		 * Proceso de recorte de la imagen en secuencial.
		 * @param drawBands
		 * @param onePerBand
		 * @param interp
		 */
		protected ClippingProcess clippingProcess(int[] drawBands, boolean onePerBand, int interp, DatasetColorInterpretation ci, int resX, int resY, int[] coords) {
			String file = getFileTemp();
			ClippingProcess clippingProcess = new ClippingProcess();
			clippingProcess.addParam("viewname", null);
			clippingProcess.addParam("pixelcoordinates", coords);
			clippingProcess.addParam("filename", file + ".jpg");
			clippingProcess.addParam("datawriter", new WriterBufferServer());
			clippingProcess.addParam("layer", lyr);
			clippingProcess.addParam("drawablebands", drawBands);
			clippingProcess.addParam("onelayerperband", new Boolean(onePerBand));
			clippingProcess.addParam("interpolationmethod", new Integer(interp));
			clippingProcess.addParam("affinetransform", new AffineTransform());
			clippingProcess.addParam("colorInterpretation", ci);
			clippingProcess.addParam("resolution", new int[]{resX, resY});
			Params params = null;
			try {
				params = GeoRasterWriter.getWriter(file + ".jpg").getParams();
			} catch (NotSupportedExtensionException e) {
			} catch (RasterDriverException e) {
			}
			clippingProcess.addParam("driverparams", params);
			return clippingProcess;
		}
}
