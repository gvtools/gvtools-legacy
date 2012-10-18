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
package org.gvsig.symbology.gui.styling;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Stack;

import javax.swing.JFrame;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.gui.beans.imagenavigator.IClientImageNavigator;
import org.gvsig.gui.beans.imagenavigator.ImageNavigator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.ILabelable;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.operations.Cancel;

public class LayerPreview extends ImageNavigator implements IClientImageNavigator {
	private static final long serialVersionUID = -7554322442540714114L;
	private FLayer layer = null;
	private ViewPort vp;
	private Stack<Cancel> cancels = new Stack<Cancel>();

	public LayerPreview() {
		super(null);
		setClientImageNavigator(this);
	}

	public void setLayer(FLayer layer) throws ExpansionFileReadException, ReadDriverException {
		this.layer = layer;
		if (layer != null) {
			vp = new ViewPort(layer.getCrs());
			Rectangle2D b = layer.getFullExtent();
			setViewDimensions(b.getMinX(), b.getMaxY(), b.getMaxX(), b.getMinY());
		}
		updateBuffer();
		setEnabled(true);
	}

	public void drawImage(Graphics2D g, double x1, double y1, double x2, double y2, double zoom, int width, int height) {
		Cancel c = new Cancel();
		while (!cancels.isEmpty()) cancels.pop().setCanceled(true);

		cancels.push(c);
		if (layer == null || width <= 0 || height <= 0 || vp == null)
			return;
		vp.setExtent(new Rectangle2D.Double(x1, y2, x2-x1, y1-y2));
		vp.setImageSize(new Dimension(width, height));
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

//		double scale = vp.getScale();
		double scale = layer.getMapContext().getScaleView();
		try {
			layer.draw(bi, g, vp, c, scale);
			if (layer instanceof ILabelable && ((ILabelable) layer).isLabeled()) {
				((ILabelable) layer).drawLabels(bi, g, vp, c, scale, MapContext.getScreenDPI());
			}
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}