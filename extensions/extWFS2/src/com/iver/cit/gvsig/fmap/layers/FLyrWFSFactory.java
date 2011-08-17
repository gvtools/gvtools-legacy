package com.iver.cit.gvsig.fmap.layers;

import java.awt.Component;
import java.net.URL;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.cresques.cts.IProjection;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.wfs.FMapWFSDriver;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
/* CVS MESSAGES:
 *
 * $Id: FLyrWFSFactory.java 13632 2007-09-11 16:00:50Z jorpiell $
 * $Log$
 * Revision 1.2.2.6  2007-09-11 16:00:50  jorpiell
 * If the SRS is not supported by gvSIG, an information message is showed
 *
 * Revision 1.2.2.5  2007/04/03 10:24:25  jorpiell
 * La transformación de coordenadas que se usa para reproyactar no debe ponerse en la capa a piñon fijo.
 *
 * Revision 1.2.2.4  2007/01/04 10:06:25  jcampos
 * Upgrade new version
 *
 * Revision 1.2.2.3  2006/11/17 11:28:45  ppiqueras
 * Corregidos bugs y aÃ±adida nueva funcionalidad.
 *
 * Revision 1.3  2006/11/16 16:57:05  jorpiell
 * Se inserta en adapter
 *
 * Revision 1.2  2006/11/06 13:58:03  jorpiell
 * Al crear una nueva capa se declara como activa. De esa forma se pueden aplicar tantos filtros como queramos
 *
 * Revision 1.1  2006/10/31 09:38:15  jorpiell
 * Se ha creado una factoria para crear la capa. De ese modo no se repite código desde le panel de propiedades y desde el panel de la capa
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class FLyrWFSFactory {

	/**
	 * 
	 * @param layer
	 * @param host
	 * @param onlineResource
	 * @param driver
	 * @param loadLayer
	 * If the layer has to be loaded
	 * @param firstLoad
	 * If is the first time that the layer is loaded
	 * @return
	 */
	public FLyrWFS getFLyrWFS(FLayer layer, 
			URL host,
			String onlineResource,
			FMapWFSDriver driver,
			boolean loadLayer,
			boolean firstLoad){
		FLyrWFS wfsLayer = (FLyrWFS)layer;
		wfsLayer.setHost(host);
		wfsLayer.setWfsDriver(driver);
//		Sets the WFS adapter
		WFSAdapter adapter = new WFSAdapter();
    	adapter.setDriver((VectorialDriver) driver);
    	wfsLayer.setSource(adapter);
		wfsLayer.setOnlineResource(onlineResource);
		BaseView activeView = 
			(BaseView) PluginServices.getMDIManager().getActiveWindow();
		//The SRS original
		IProjection projection = CRSFactory.getCRS(getSRS(wfsLayer.getSrs()));
		wfsLayer.setProjection(projection);
		if (loadLayer){
			try {
				if ((projection == null) && (firstLoad)){
					JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),
							PluginServices.getText(this,"wfs_srs_unknown"));	
				}
				wfsLayer.load();
				if (wfsLayer.getNumfeatures() == wfsLayer.getWfsDriver().getRowCount()) {
					JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),
							PluginServices.getText(this,"maxFeatures_aviso"));	
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.getLogger(getClass().getName()).error(e.getMessage());
				JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),
						PluginServices.getText(this,"cantLoad"));
				return null;
			}
		}
		wfsLayer.setActive(true);
		return wfsLayer;
	}
	
	/**
	 * Removing the URN prefix
	 * @param srs
	 * @return
	 */
	private String getSRS(String srs){
		if (srs == null){
			return null;
		}
		if ((srs.startsWith("urn:x-ogc:def:crs:")) || srs.startsWith("urn:ogc:def:crs:")){
			String newString = srs.substring(srs.lastIndexOf(":") + 1, srs.length());
			if (srs.indexOf("EPSG") > 0){
				if (newString.indexOf("EPSG") < 0){
					newString = "EPSG:" + newString;
				}
			}
			return newString;			
		}
		if (srs.toLowerCase().startsWith("crs:")){
			return srs.substring(4, srs.length());
		}
		return srs;
	}

}
