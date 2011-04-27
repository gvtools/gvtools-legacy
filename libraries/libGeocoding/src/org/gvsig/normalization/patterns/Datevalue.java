/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2008 Prodevelop S.L. main development
 */

package org.gvsig.normalization.patterns;

import org.gvsig.normalization.persistence.GeocodingPersistence;
import org.gvsig.normalization.persistence.GeocodingTags;

import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 * 
 */

/**
 * Class Datevalue.
 * 
 * This class defines the new field type like Date
 */
public class Datevalue implements GeocodingPersistence {

	/**
	 * format of the date
	 */
	private String _datevalueformat;

	/**
	 * Constructor
	 */
	public Datevalue() {
	}

	/**
	 * Returns the value of field 'datevalueformat'.
	 * 
	 * @return the value of field 'datevalueformat'.
	 */
	public String getDatevalueformat() {
		return this._datevalueformat;
	}

	/**
	 * Sets the value of field 'datevalueformat'.
	 * 
	 * @param datevalueformat
	 *            the value of field 'datevalueformat'.
	 */
	public void setDatevalueformat(String datevalueformat) {
		this._datevalueformat = datevalueformat;
	}

	// /**
	// * Saves the internal state of the object on the provided PersistentState
	// * object.
	// *
	// * @param state
	// */
	// public void saveToState(PersistentState state) throws
	// PersistenceException {
	// state.set("datevalueformat", this._datevalueformat);
	// }
	//
	// /**
	// * Set the state of the object from the state passed as parameter.
	// *
	// * @param state
	// */
	// public void loadFromState(PersistentState state)
	// throws PersistenceException {
	// this._datevalueformat = state.getString("datevalueformat");
	// }

	/**
	 * Get class name
	 */
	public String getClassName() {
		return this.getClass().getName();
	}

	/**
	 * Persist object
	 */
	public XMLEntity getXMLEntity() throws XMLException {
		XMLEntity xml = new XMLEntity();
		xml.setName(GeocodingTags.DATEVALUE);
		xml.putProperty(GeocodingTags.DATEVALUEFORMAT, this._datevalueformat);
		return xml;
	}

	/**
	 * Load object
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		this._datevalueformat = xml
				.getStringProperty(GeocodingTags.DATEVALUEFORMAT);
	}

}
