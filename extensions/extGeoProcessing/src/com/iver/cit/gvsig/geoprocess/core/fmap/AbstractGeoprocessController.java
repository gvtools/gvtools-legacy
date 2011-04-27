/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
* $Id: AbstractGeoprocessController.java 21230 2008-06-05 14:01:14Z azabala $
* $Log$
* Revision 1.5  2006-06-20 18:19:43  azabala
* refactorización para que todos los nuevos geoprocesos cuelguen del paquete impl
*
* Revision 1.4  2006/06/12 19:15:38  azabala
* cambios para poder trabajar en geoprocessing con capas MULTI (dxf, jdbc, etc)
*
* Revision 1.3  2006/06/08 18:21:24  azabala
* Se añade chequeo de capas vacías antes de añadir result al TOC
*
* Revision 1.2  2006/06/02 18:21:28  azabala
* *** empty log message ***
*
* Revision 1.1  2006/05/24 21:12:16  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.1  2006/04/11 17:55:51  azabala
* primera version en cvs
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.fmap;


import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessController;
import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;

/**
 * Abstract base class for all implementations of GeoprocessController.
 * <br>
 * A <b>GeoprocessController</b> instance reads user inputs, and builds a Geoprocess
 * instance with these data.
 * 
 * This abstraction is very usefull to allow the building of any GUI component
 * (toolbar button, dialog, extensible geoprocessing toolbox, etc.) reusing the
 * reading of user entries and the construction of the geoprocess instance.
 * 
 * @author azabala
 *
 */
public abstract class AbstractGeoprocessController implements
		IGeoprocessController {

	public abstract void setView(IGeoprocessUserEntries userEntries);

	public abstract IGeoprocess getGeoprocess();

	public abstract boolean launchGeoprocess();
	
	/**
	 * Returns a ShpWriter from a SHPLayerDefinition.
	 * TODO Independize Writer and LayerDefinition of implementation
	 * (by now we are only saving in SHP format)
	 * 
	 * @param definition
	 * @return
	 * @throws Exception
	 */
	public IWriter getShpWriter(SHPLayerDefinition definition) throws Exception {
		int shapeType = definition.getShapeType();
		if(shapeType != XTypes.MULTI){
			ShpWriter writer = new ShpWriter();
			writer.setFile(definition.getFile());
			writer.initialize(definition);
			return writer;
		}else{
			MultiShpWriter writer = new MultiShpWriter();
			writer.setFile(definition.getFile());
			writer.initialize(definition);
			return writer;
		}
			
	}
	
}

