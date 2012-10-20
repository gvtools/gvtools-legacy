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
package com.iver.cit.gvsig.addlayer.fileopen.vectorial;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * Clase para definir que ficheros aceptara la extension vectorial, es necesario
 * para el JFileChooser
 * 
 * @version 05/09/2007
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class VectorialFileFilter extends FileFilter {
	public Driver driver = null;

	public VectorialFileFilter(String driverName) throws DriverLoadException {
		driver = LayerFactory.getDM().getDriver(driverName);
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;

		if (driver instanceof VectorialFileDriver)
			return ((VectorialFileDriver) driver).accept(f);

		if (driver instanceof FileDriver)
			return ((FileDriver) driver).fileAccepted(f);

		throw new RuntimeException(PluginServices.getText(this,
				"Tipo_no_reconocido"));
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 * 
	 *      Returns a description more useful for file selection dialogs if the
	 *      driver type is known.
	 */
	public String getDescription() {
		/* translate known driver names */
		if (((Driver) driver).getName() == "gvSIG shp driver") {
			return (PluginServices.getText(this, "Ficheros_SHP"));
		}
		if (((Driver) driver).getName() == "gvSIG GML Memory Driver") {
			return (PluginServices.getText(this, "gml_files"));
		}
		if (((Driver) driver).getName() == "gvSIG KML Memory Driver") {
			return (PluginServices.getText(this, "Ficheros_KML"));
		}

		/*
		 * Unknown: return default string.
		 */
		return ((Driver) driver).getName();

	}

}