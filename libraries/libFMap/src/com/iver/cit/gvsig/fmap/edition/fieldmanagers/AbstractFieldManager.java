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
package com.iver.cit.gvsig.fmap.edition.fieldmanagers;

import java.util.ArrayList;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.IFieldManager;

public abstract class AbstractFieldManager implements IFieldManager {

	protected ArrayList fieldCommands = new ArrayList();

	protected FieldDescription[] originalFields;

	public FieldDescription[] getOriginalFields() {
		return originalFields;
	}

	public void setOriginalFields(FieldDescription[] fields) {
		originalFields = fields;

	}

	public void addField(FieldDescription fieldDesc) {
		AddFieldCommand c = new AddFieldCommand(fieldDesc);
		fieldCommands.add(c);
	}

	public FieldDescription removeField(String fieldName) {
		RemoveFieldCommand c = new RemoveFieldCommand(fieldName);
		FieldDescription[] act = getFields();
		FieldDescription found = null;
		for (int i = 0; i < act.length; i++) {
			if (act[i].getFieldAlias().compareToIgnoreCase(fieldName) == 0) {
				found = act[i];
				break;
			}
		}
		fieldCommands.add(c);
		return found;
	}

	public void renameField(String antName, String newName) {
		RenameFieldCommand c = new RenameFieldCommand(antName, newName);
		fieldCommands.add(c);
	}

	public FieldDescription[] getFields() {
		ArrayList aux = new ArrayList();
		for (int i = 0; i < aux.size(); i++) {
			aux.add(getOriginalFields()[i]);
		}
		// procesamos comandos para devolver los campos reales.
		for (int j = 0; j < fieldCommands.size(); j++) {
			FieldCommand fc = (FieldCommand) fieldCommands.get(j);
			if (fc instanceof AddFieldCommand) {
				AddFieldCommand ac = (AddFieldCommand) fc;
				aux.add(ac.getFieldDesc());
			}
			if (fc instanceof RemoveFieldCommand) {
				RemoveFieldCommand rc = (RemoveFieldCommand) fc;
				for (int k = 0; k < aux.size(); k++) {
					FieldDescription fAux = (FieldDescription) aux.get(k);
					if (fAux.getFieldAlias().compareTo(rc.getFieldName()) == 0) {
						aux.remove(k);
						break;
					}
				}
			}
			if (fc instanceof RenameFieldCommand) {
				RenameFieldCommand renc = (RenameFieldCommand) fc;
				for (int k = 0; k < aux.size(); k++) {
					FieldDescription fAux = (FieldDescription) aux.get(k);
					if (fAux.getFieldAlias().compareTo(renc.getAntName()) == 0) {
						fAux.setFieldName(renc.getNewName());
						fAux.setFieldAlias(renc.getNewName());
						break;
					}
				}

			}

		}
		return (FieldDescription[]) aux.toArray(new FieldDescription[0]);
	}

}
