/*
 * Created on 26-ene-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
USA.
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
package com.iver.cit.gvsig.fmap.demo;

import java.io.File;

import javax.swing.JFrame;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.tools.ZoomInListenerImpl;
import com.iver.cit.gvsig.fmap.tools.Behavior.RectangleBehavior;
public class MapImage extends JFrame {

	private javax.swing.JPanel jContentPane = null;

	private MapControl newMapControl = null;
	/**
	 * This is the default constructor
	 */
	public MapImage() {
		super();
		initialize();
	}
	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(300,200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}
	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if(jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getNewMapControl(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}
	/**
	 * This method initializes newMapControl
	 *
	 * @return com.iver.cit.gvsig.fmap.NewMapControl
	 */
	private MapControl getNewMapControl() {
		if (newMapControl == null) {
			newMapControl = new MapControl();
			LayerFactory.setDriversPath(
			"C:\\eclipse3\\workspace\\Andami\\gvSIG\\extensiones\\com.iver.cit.gvsig\\drivers");

			try {
				FLayer l = LayerFactory.createLayer("Vias",
					(VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver"),
					new File("C:/Documents and Settings/fernando/Mis documentos/vias.shp"),
					CRSFactory.getCRS("EPSG:23030"));
				newMapControl.getMapContext().getLayers().addLayer(l);
				newMapControl.addMapTool("zoom", new RectangleBehavior(new ZoomInListenerImpl(newMapControl)));
				newMapControl.setTool("zoom");
			}/* azabala -modificaciones en layerfactory-
			catch (DriverException e) {
				e.printStackTrace();
			} */
			catch (DriverLoadException e) {
				e.printStackTrace();
			} catch (CancelationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		return newMapControl;
	}
 }
