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

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

public class InternalField {
	public final static int ORIGINAL = 0;

	public final static int DELETED = 2;

	public final static int ADDED = 3;

	private int fieldType = -1;

	private Integer fieldId;

	// private boolean isDeleted = false;

	private FieldDescription fieldDesc = null;

	private int fieldIndex;

	// public boolean isDeleted() {
	// return isDeleted;
	// }
	//
	// public void setDeleted(boolean isDeleted) {
	// this.isDeleted = isDeleted;
	// }

	public FieldDescription getFieldDesc() {
		return fieldDesc;
	}

	/**
	 * Possible values: InternalField.ORIGINAL, InternalField.DELETED, ADDED
	 * 
	 * @return
	 */
	public int getFieldType() {
		return fieldType;
	}

	public InternalField(FieldDescription fieldDesc, int fieldType,
			Integer fieldId) {
		this.fieldDesc = fieldDesc;
		this.fieldType = fieldType;
		this.fieldId = fieldId;
	}

	/**
	 * All fields are created and stored in fieldList. Every field has a unique
	 * fieldId. This fieldId is useful to find the field that was used to
	 * generate a row. De esta forma, si nos interesa saber si un campo actual
	 * es el mismo que aquél con el que fue generado la row correspondiente,
	 * miramos si coincide su fieldIndex.
	 * 
	 * @return
	 */
	public Integer getFieldId() {
		return fieldId;
	}

	public int getFieldIndex() {
		return fieldIndex;
	}

	public void setFieldIndex(int i) {
		fieldIndex = i;

	}

	public InternalField cloneInternalField() {
		FieldDescription copyFldDesc = fieldDesc.cloneField();
		InternalField newFld = new InternalField(copyFldDesc, fieldType,
				new Integer(fieldId.intValue()));
		newFld.setFieldIndex(fieldIndex);
		return newFld;
	}

}
