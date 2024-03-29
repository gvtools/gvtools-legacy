/*
 * Created on 25-abr-2007
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: DifferenceTest.java 12564 2007-07-12 11:33:24Z azabala $
 * $Log$
 * Revision 1.1  2007-07-12 11:33:24  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.difference;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class DifferenceTest extends TestCase {

	static final String fwAndamiDriverPath = "../_fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers";
	private File baseDataPath;
	private File baseDriversPath;

	private String SHP_DRIVER_NAME = "gvSIG shp driver";

	private CoordinateReferenceSystem DEFAULT_CRS = ProjectionUtils
			.getCRS("EPSG:23030");
	private CoordinateReferenceSystem newCrs = ProjectionUtils
			.getCRS("EPSG:23029");

	protected void setUp() throws Exception {
		super.setUp();
		URL url = this.getClass().getResource("testdata");
		if (url == null)
			throw new Exception(
					"No se encuentra el directorio con datos de prueba");

		baseDataPath = new File(url.getFile());
		if (!baseDataPath.exists())
			throw new Exception(
					"No se encuentra el directorio con datos de prueba");

		baseDriversPath = new File(fwAndamiDriverPath);
		if (!baseDriversPath.exists())
			throw new Exception("Can't find drivers path: "
					+ fwAndamiDriverPath);

		LayerFactory.setDriversPath(baseDriversPath.getAbsolutePath());
	}

	public void test1() {
		// try {
		// //pruebas de reproyeccion y seleccion de numero de campos
		// FLyrVect lyr = (FLyrVect) newLayer("Cantabria.shp", SHP_DRIVER_NAME);
		// lyr.setAvailable(true);
	}
}
