package org.gvsig.fmap.drivers.gpe.writer.v2;

import java.io.File;

import org.gvsig.fmap.drivers.gpe.handlers.DefaultFmapContentHandler;
import org.gvsig.fmap.drivers.gpe.handlers.FmapErrorHandler;
import org.gvsig.fmap.drivers.gpe.handlers.FmapHandlerFactory;
import org.gvsig.fmap.drivers.gpe.reader.GMLVectorialDriver;
import org.gvsig.gpe.GPERegister;
import org.gvsig.gpe.parser.GPEParser;

import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;

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
public class GPEGMLToGMLWriterTest extends GPEWriterTest {
	private String sFile = "../extGPE-gvSIG" + File.separatorChar + "testdata"
			+ File.separatorChar + "GML-lines.gml";

	protected FLayer getLayerToWrite() throws Exception {
		MapControl mapControl = new MapControl();
		mapControl.setMapContext(getMapContext());
		FmapErrorHandler errorHandler = FmapHandlerFactory.createErrorHandler();
		DefaultFmapContentHandler contentHandler = FmapHandlerFactory
				.createContentHandler(errorHandler, new GMLVectorialDriver());
		GPEParser parser = GPERegister.createParser("GML");
		parser.parse(contentHandler, errorHandler, new File(sFile).toURI());
		return mapControl.getMapContext().getLayers().getLayer(0);
	}

	protected Class getGPEWriterHandlerClass() {
		return org.gvsig.gpe.gml.writer.GPEGmlWriterHandlerImplementor.class;
	}

}
