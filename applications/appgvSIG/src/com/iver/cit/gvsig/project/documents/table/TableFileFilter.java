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

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
/**
 * Clase para definir que ficheros aceptara el filtro de tablas, es necesario
 * para el JFileChooser
 * 
 * @version 05/09/2007
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class TableFileFilter extends FileFilter {
	public Driver driver = null;

	public TableFileFilter(String driverName) throws DriverLoadException {
		driver = LayerFactory.getDM().getDriver(driverName);
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;

		if (driver instanceof FileDriver)
			return ((FileDriver) driver).fileAccepted(f);

		throw new RuntimeException(PluginServices.getText(this, "Tipo_no_reconocido"));
	}

	/**
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
				
		if (((Driver) driver).getName().equals ( "gdbms dbf driver" ) ) {			
			return (PluginServices.getText(this, "Ficheros_dbf"));
		}
		if (((Driver) driver).getName().equals ( "csv string" ) ) {
			return (PluginServices.getText(this, "Ficheros_csv"));
		}		
		return ((Driver) driver).getName();
	}
}