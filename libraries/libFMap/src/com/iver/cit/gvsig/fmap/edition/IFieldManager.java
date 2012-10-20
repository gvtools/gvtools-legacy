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
package com.iver.cit.gvsig.fmap.edition;

import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

public interface IFieldManager {

	public FieldDescription[] getOriginalFields();

	public void addField(FieldDescription fieldDesc);

	/**
	 * @param fieldName
	 * @return FieldDescription of removed field, if any. If not found, returns
	 *         null
	 */
	public FieldDescription removeField(String fieldName);

	public void renameField(String antName, String newName);

	/**
	 * @return true if the table needs to be rewritten. (As a dbf, for example)
	 * @throws WriteDriverException
	 * @throws EditionException
	 */
	public boolean alterTable() throws WriteDriverException;

	public FieldDescription[] getFields();

}
