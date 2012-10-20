package com.iver.cit.gvsig.fmap.drivers.wfs;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.Hashtable;

import com.iver.cit.gvsig.fmap.drivers.WFSDriverException;

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
 * $Id: FMapWFSDriverFactory.java 18381 2008-01-30 12:00:25Z jpiera $
 * $Log$
 * Revision 1.1.2.3  2006-12-18 08:43:25  jorpiell
 * Add some comments
 *
 * Revision 1.1.2.2  2006/11/17 11:28:45  ppiqueras
 * Corregidos bugs y aÃ±adida nueva funcionalidad.
 *
 * Revision 1.2  2006/11/16 13:30:36  jorpiell
 * Se crea siempre un driver nuevo. Sino hay problemas al recuperar un gvp con muchos wfs's
 *
 * Revision 1.1  2006/09/05 15:41:52  jorpiell
 * Añadida la funcionalidad de cargar WFS desde el catálogo
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class FMapWFSDriverFactory {
	private static Hashtable drivers = new Hashtable();

	private FMapWFSDriverFactory() {
	}

	static public final FMapWFSDriver getFMapDriverForURL(URL url)
			throws ConnectException, IOException, WFSDriverException {
		// The driver cant be linked with one URL because it has the features to
		// load
		FMapWFSDriver drv = null;
		if (drv == null) {
			drv = new FMapWFSDriver();
			drv.getCapabilities(url);
			drivers.put(url, drv);
		}
		return drv;
	}
}
