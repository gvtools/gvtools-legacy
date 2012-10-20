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
package com.iver.cit.gvsig.fmap.edition.commands;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.InternalField;

public class RenameFieldCommand extends AbstractCommand {

	EditableAdapter edAdapter;
	InternalField field;
	String antName;
	String newName;

	public RenameFieldCommand(EditableAdapter edAdapter, InternalField field,
			String newName) {
		this.edAdapter = edAdapter;
		this.field = field;
		antName = field.getFieldDesc().getFieldAlias();
		this.newName = newName;
	}

	public void undo() throws EditionCommandException {
		edAdapter.undoRenameField(field, antName);
	}

	public void redo() throws EditionCommandException {
		try {
			edAdapter.doRenameField(field, newName);
		} catch (ReadDriverException e) {
			throw new EditionCommandException(edAdapter.getWriter().getName(),
					e);
		}
	}

	public String getType() {
		return "Rename Field " + antName + " to " + newName;
	}

}
