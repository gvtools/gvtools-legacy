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
package com.iver.cit.gvsig.project.documents.table;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.addlayer.fileopen.AbstractFileOpen;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
/**
 * Clase que indicará que ficheros puede tratar al panel de apertura de ficheros
 *
 * @version 04/09/2007
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class TableFileOpen extends AbstractFileOpen {

	/**
	 * Constructor de FileOpenRaster
	 */
	public TableFileOpen() {
		TreeSet filters = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				TableFileFilter dff1 = (TableFileFilter) o1;
				TableFileFilter dff2 = (TableFileFilter) o2;

				return dff1.driver.getName().compareTo(dff2.driver.getName());
			}
		});

		Class[] driverClasses = new Class[] { FileDriver.class };
		String[] driverNames = LayerFactory.getDM().getDriverNames();
		TableFileFilter auxF;
		try {
			for (int i = 0; i < driverNames.length; i++) {
				System.err.println("DRIVER " + i + " : " + driverNames[i]);
				boolean is = false;
				for (int j = 0; j < driverClasses.length; j++) {
					if (LayerFactory.getDM().isA(driverNames[i], driverClasses[j])) {
						is = true;
						break;
					}
				}
				if (is) {
					auxF = new TableFileFilter(driverNames[i]);
					System.out.println("DRIVER " + i + " : " + driverNames[i]);
					filters.add(auxF);
				}
			}
			Iterator iterator = filters.iterator();
			while (iterator.hasNext()) {
				TableFileFilter filter = (TableFileFilter) iterator.next();
				getFileFilter().add(filter);
			}
		} catch (DriverLoadException exception) {
			NotificationManager.addError("No se pudo acceder a los drivers", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.gui.wizards.IFileOpen#execute(java.io.File[])
	 */
	public Rectangle2D createLayer(File file, MapControl mapControl,
			String driverName, CoordinateReferenceSystem crs) {
		return null;
	}
}