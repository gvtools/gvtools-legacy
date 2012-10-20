package org.gvsig.fmap.drivers.gpe.reader;

import org.gvsig.gpe.gml.parser.GPEDefaultGmlParser;
import org.gvsig.gpe.kml.parser.GPEDeafultKmlParser;
import org.gvsig.gpe.parser.GPEParser;

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
public class GPEDriverFactory {

	/**
	 * Creates a new FMap contentHandler. This method will be deleted on gvSIG
	 * 1.2.
	 * 
	 * @param errorHandler
	 *            The error handler
	 * @param parser
	 *            The GP parser
	 * @return One FMAP ContentHandler
	 */
	public static GPEVectorialDriver createDriver(GPEParser parser) {
		GPEVectorialDriver driver = null;
		if (parser instanceof GPEDefaultGmlParser) {
			driver = new GMLVectorialDriver();
		} else if (parser instanceof GPEDeafultKmlParser) {
			driver = new KMLVectorialDriver();
		}
		return driver;
	}
}
