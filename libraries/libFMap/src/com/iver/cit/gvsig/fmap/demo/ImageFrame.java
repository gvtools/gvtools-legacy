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
package com.iver.cit.gvsig.fmap.demo;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cresques.cts.ProjectionUtils;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ImageFrame extends JFrame {
	private javax.swing.JPanel jContentPane = null;
	/**
	 * This is the default constructor
	 */
	public ImageFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		FLayer l;

		try {
			LayerFactory.setDriversPath(
				"d:/java/eclipse30/eclipse/workspace/Andami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");

			l = LayerFactory.createLayer("Provin",
					(VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver"),
					new File("C:/Documents and Settings/fjp/Mis documentos/provin.shp"),
					ProjectionUtils.getCRS("EPSG:23030"));

			ViewPort vp = new ViewPort(ProjectionUtils.getCRS("EPSG:23030"));
			vp.setImageSize(new Dimension(getWidth(), getHeight()));

			MapContext mapa = new MapContext(vp);
			mapa.getLayers().addLayer(l);

			BufferedImage img = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			mapa.draw(img, img.createGraphics(),mapa.getScaleView());
			JLabel lbl = new JLabel(new ImageIcon(img));
			getJContentPane().add(lbl, java.awt.BorderLayout.CENTER);
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
		}

		return jContentPane;
	}
}
