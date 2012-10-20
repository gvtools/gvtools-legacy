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

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.addlayer.fileopen.AbstractFileOpen;
import com.iver.cit.gvsig.fmap.MapControl;

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.raster.gui.wizards.IFileOpen#execute(java.io.File[])
	 */
	public Rectangle2D createLayer(File file, MapControl mapControl,
			CoordinateReferenceSystem crs) {
		return null;
	}

	@Override
	public boolean accepts(File file) {
		return false;
	}
}