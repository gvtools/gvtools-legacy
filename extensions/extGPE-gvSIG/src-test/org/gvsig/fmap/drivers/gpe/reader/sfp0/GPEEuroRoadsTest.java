package org.gvsig.fmap.drivers.gpe.reader.sfp0;

import java.io.File;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;

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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GPEEuroRoadsTest extends GPEParserTest {

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.gpe.GPEDriverTest#getFile()
	 */
	public String getFile() {
		return "../extGPE-gvSIG" + File.separatorChar +
			"testdata" + File.separatorChar + "euroroads.gml";
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.gpe.GPEDriverTest#makeAsserts()
	 */
	public void makeAsserts() throws DriverIOException, ReadDriverException  {
		assertEquals(getLayer().getSource().getShapeCount(), 7);
//		FlyrGPEVectorial roadNodeLayer = (FlyrGPEVectorial)((IGPELayer)layer).getLayer(0);
//		FlyrGPEVectorial roadLinkLayer = (FlyrGPEVectorial)((IGPELayer)layer).getLayer(1);
//		FlyrGPEVectorial excludeLayer = (FlyrGPEVectorial)((IGPELayer)layer).getLayer(2);
//		FlyrGPEVectorial routesLayer = (FlyrGPEVectorial)((IGPELayer)layer).getLayer(3);
//		FlyrGPEVectorial propertiesLayer = (FlyrGPEVectorial)((IGPELayer)layer).getLayer(4);
//		assertEquals(roadNodeLayer.getDriver().getRowCount(), 4);
//		assertEquals(roadLinkLayer.getDriver().getRowCount(), 3);
//		assertEquals(excludeLayer.getDriver().getRowCount(), 0);
//		assertEquals(routesLayer.getDriver().getRowCount(), 0);
//		assertEquals(propertiesLayer.getDriver().getRowCount(), 0);
	}
}
