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
package org.gvsig.rastertools.saveraster.map;


import java.awt.geom.Point2D;

import org.gvsig.fmap.raster.tools.SaveRasterListenerImpl;
import org.gvsig.rastertools.saveraster.ui.SaveRasterDialog;
import org.gvsig.rastertools.saveraster.ui.SaveRasterPanel;
import org.opengis.referencing.crs.ProjectedCRS;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.tools.Events.RectangleEvent;


/**
 * Extensión de la clase SaveRasterListenerImpl de FMap para poder llamar a métodos
 * de andami o de gvSIG.
 *
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class SaveRasterListener extends SaveRasterListenerImpl {

//	FLyrRaster 			layer = null;

	public SaveRasterListener(MapControl mapCtrl) {
		super(mapCtrl);
	}

	/**
	 * 
	 * @param flyr
	 */
/*
	public void  setRasterLayer(FLyrRaster flyr){
		layer = flyr;
	}
*/

	public void rectangle(RectangleEvent event) {
		super.rectangle(event);

		if (PluginServices.getMainFrame() != null)
				PluginServices.getMainFrame().enableControls();

		FLayers layers = mapCtrl.getMapContext().getLayers();

		SaveRasterDialog saveRaster = null;
		boolean open = false;
		IWindow[] win = PluginServices.getMDIManager().getAllWindows();
		for(int i = 0; i < win.length; i++) {
			if(win[i] instanceof SaveRasterDialog) {
				saveRaster = (SaveRasterDialog)win[i];
				open = true;
			}
		}
		if(saveRaster == null)
			saveRaster = new SaveRasterDialog(layers, mapCtrl);

		ViewPort vp = mapCtrl.getViewPort();
		Point2D ini = vp.fromMapPoint(rect.getMinX(), rect.getMinY());
		Point2D fin = vp.fromMapPoint(rect.getMaxX(), rect.getMaxY());
		if(Math.abs(fin.getY()-ini.getY())>10 && Math.abs(fin.getX()-ini.getX())>10){
			SaveRasterPanel dialog = saveRaster.getControlsPanel();
			dialog.getBProperties().setEnabled(false);
			dialog.setCrs(vp.getCrs());
			//dialog.setWidthInPixelsGeodesicas((int)Math.abs(fin.getX()-ini.getX()));
			//dialog.setHeightInPixelsGeodesicas((int)Math.abs(fin.getY()-ini.getY()));

			int dec = 2;
			if (!(vp.getCrs() instanceof ProjectedCRS))
				dec = 6;

			int indexPoint = String.valueOf(rect.getMaxX()).indexOf('.');
			try{
				dialog.getTInfDerX().setValue(String.valueOf(rect.getMaxX()).substring(0,indexPoint + dec));
			}catch(IndexOutOfBoundsException ex){
				dialog.getTInfDerX().setValue(String.valueOf(rect.getMaxX()));
			}

			indexPoint = String.valueOf(rect.getMaxY()).indexOf('.');
			try{
				dialog.getTInfDerY().setValue(String.valueOf(rect.getMinY()).substring(0,indexPoint + dec));
			}catch(IndexOutOfBoundsException ex){
				dialog.getTInfDerY().setValue(String.valueOf(rect.getMinY()));
			}

			indexPoint = String.valueOf(rect.getMinX()).indexOf('.');
			try{
				dialog.getTSupIzqX().setValue(String.valueOf(rect.getMinX()).substring(0,indexPoint + dec));
			}catch(IndexOutOfBoundsException ex){
				dialog.getTSupIzqX().setValue(String.valueOf(rect.getMinX()));
			}

			indexPoint = String.valueOf(rect.getMinY()).indexOf('.');
			try{
				dialog.getTSupIzqY().setValue(String.valueOf(rect.getMaxY()).substring(0,indexPoint + dec));
			}catch(IndexOutOfBoundsException ex){
				dialog.getTSupIzqY().setValue(String.valueOf(rect.getMaxY()));
			}
			if(!open)
				PluginServices.getMDIManager().addWindow(saveRaster);
		}
	}
}
