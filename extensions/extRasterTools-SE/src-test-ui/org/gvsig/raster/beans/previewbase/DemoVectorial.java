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
package org.gvsig.raster.beans.previewbase;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import org.cresques.cts.IProjection;
import org.gvsig.gui.beans.imagenavigator.IClientImageNavigator;
import org.gvsig.gui.beans.imagenavigator.ImageNavigator;
import org.gvsig.rastertools.TestUI;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.operations.Cancel;

public class DemoVectorial implements IClientImageNavigator {
	private FLayer         layer              = null;
	static final String    fwAndamiDriverPath = "../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers";

	public DemoVectorial() {
		try {
			IProjection PROJ = CRSFactory.getCRS("EPSG:23030");
			LayerFactory.setDriversPath(new File(fwAndamiDriverPath).getAbsolutePath());
			layer = LayerFactory.createLayer("line", "gvSIG shp driver", new File("C:\\Documents and Settings\\borja\\Escritorio\\images_gvsig\\vectorial\\t_areas.shp"), PROJ);
			TestUI jFrame = new TestUI("DemoVectorial");
			jFrame.setSize(new Dimension(598, 167));
			ImageNavigator imNav = new ImageNavigator(this);
			jFrame.setContentPane(imNav);
			Rectangle2D b = layer.getFullExtent();
			imNav.setViewDimensions(b.getMinX(), b.getMaxY(), b.getMaxX(), b.getMinY());
			imNav.updateBuffer();
			imNav.setEnabled(true);
			jFrame.setVisible(true);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		new DemoVectorial();
	}

	public void drawImage(Graphics2D g, double x1, double y1, double x2, double y2, double zoom, int width, int height) {
		ViewPort vp = new ViewPort(null);
		vp.setExtent(new Rectangle2D.Double(x1, y2, x2 - x1, y1 - y2));
		vp.setImageSize(new Dimension(width, height));
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		try {
			layer.draw(bi, g, vp, new Cancel(), 0);
		} catch (ReadDriverException e) {
		}
	}
}