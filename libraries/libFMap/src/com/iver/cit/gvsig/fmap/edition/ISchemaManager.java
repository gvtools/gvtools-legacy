/*
 * Created on 06-feb-2006
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
* $Id: ISchemaManager.java 10627 2007-03-06 17:10:21Z caballero $
* $Log$
* Revision 1.5  2007-03-06 17:08:55  caballero
* Exceptions
*
* Revision 1.4  2006/06/29 07:33:56  fjp
* Cambios ISchemaManager y IFieldManager por terminar
*
* Revision 1.3  2006/03/14 19:27:25  azabala
* *** empty log message ***
*
* Revision 1.2  2006/03/14 18:17:12  fjp
* Preparando la creación de cero de un tema
*
* Revision 1.1  2006/02/06 18:15:14  azabala
* ISchemaManager is needed to create new datastores' schemas from a LayerDefiniton. First version in CVS
*
*
*/
package com.iver.cit.gvsig.fmap.edition;

import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;

/**
 * Before writing in a persistent datastore with a writer,
 * usually we'll need to create a schema that describe
 * data entities we are going to save into.
 * Event thought ascii based datastores, like DXF, will need to
 * create phisically file (this would be its schema).
 * 
 * This is the responsability of ISchemaManager: it will
 * create datastores phisical schemas from logical definitions
 * of LayerDefinition.
 * 
 *  TODO If we are going to move LayerDefinition responsability
 *  to DataSource (which will have a TableDescription reference)
 *  this interface have to change (to receive a TableDescription)
 * @author azabala
 * 
 */
public interface ISchemaManager {
	public void createSchema(ITableDefinition layerDefinition)throws SchemaEditionException;
	public void removeSchema(String name) throws SchemaEditionException;
	public void renameSchema(String antName, String newName) throws SchemaEditionException;

}

