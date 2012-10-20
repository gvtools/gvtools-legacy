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

/**
 * 
 * @author <a href="mailto:jsanz@prodevelop.es"> Jorge Gaspar Sanz Salinas</a>
 * @author <a href="mailto:vsanjaime@prodevelop.es"> Vicent Sanjaime Calvet</a>
 * 
 */

import org.apache.log4j.Logger;
import org.gvsig.normalization.persistence.GeocodingPersistence;
import org.gvsig.normalization.persistence.GeocodingTags;

import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;

/**
 * Class Fieldtype.
 * 
 * This class defines the type of the one new field
 */
public class Fieldtype implements GeocodingPersistence {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Fieldtype.class);

	/**
	 * new field of type String
	 */
	private Stringvalue _stringvalue;

	/**
	 * new field of type Date
	 */
	private Datevalue _datevalue;

	/**
	 * new field of type Decimal
	 */
	private Decimalvalue _decimalvalue;

	/**
	 * new field of type Integer
	 */
	private Integervalue _integervalue;

	/**
	 * Constructor
	 */
	public Fieldtype() {
	}

	/**
	 * @return the value of field 'datevalue'.
	 */
	public Datevalue getDatevalue() {
		return this._datevalue;
	}

	/**
	 * @return the value of field 'decimalvalue'.
	 */
	public Decimalvalue getDecimalvalue() {
		return this._decimalvalue;
	}

	/**
	 * @return the value of field 'integervalue'.
	 */
	public Integervalue getIntegervalue() {
		return this._integervalue;
	}

	/**
	 * @return the value of field 'stringvalue'.
	 */
	public Stringvalue getStringvalue() {
		return this._stringvalue;
	}

	/**
	 * Sets the value of field 'datevalue'.
	 * 
	 * @param datevalue
	 *            the value of field 'datevalue'.
	 */
	public void setDatevalue(Datevalue datevalue) {
		this._datevalue = datevalue;
	}

	/**
	 * Sets the value of field 'decimalvalue'.
	 * 
	 * @param decimalvalue
	 *            the value of field 'decimalvalue'.
	 */
	public void setDecimalvalue(Decimalvalue decimalvalue) {
		this._decimalvalue = decimalvalue;
	}

	/**
	 * Sets the value of field 'integervalue'.
	 * 
	 * @param integervalue
	 *            the value of field 'integervalue'.
	 */
	public void setIntegervalue(Integervalue integervalue) {
		this._integervalue = integervalue;
	}

	/**
	 * Sets the value of field 'stringvalue'.
	 * 
	 * @param stringvalue
	 *            the value of field 'stringvalue'.
	 */
	public void setStringvalue(Stringvalue stringvalue) {
		this._stringvalue = stringvalue;
	}

	// /**
	// * Saves the internal state of the object on the provided PersistentState
	// * object.
	// *
	// * @param state
	// */
	// public void saveToState(PersistentState state) throws
	// PersistenceException {
	// if (this._stringvalue != null) {
	// state.set("stringvalue", this._stringvalue);
	// } else if (this._datevalue != null) {
	// state.set("datevalue", this._datevalue);
	// } else if (this._decimalvalue != null) {
	// state.set("decimalvalue", this._decimalvalue);
	// } else {
	// state.set("integervalue", this._integervalue);
	// }
	// }
	//
	// /**
	// * Set the state of the object from the state passed as parameter.
	// *
	// * @param state
	// */
	// public void loadFromState(PersistentState state)
	// throws PersistenceException {
	// try {
	// if (state.get("stringvalue") != null) {
	// this._stringvalue = (Stringvalue) state.get("stringvalue");
	// } else if (state.get("datevalue") != null) {
	// this._datevalue = (Datevalue) state.get("datevalue");
	// } else if (state.get("decimalvalue") != null) {
	// this._decimalvalue = (Decimalvalue) state.get("decimalvalue");
	// } else {
	// this._integervalue = (Integervalue) state.get("integervalue");
	// }
	// } catch (Exception e) {
	// log.error("Error parsing the object", e);
	// }
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
		xml.setName(GeocodingTags.FIELDTYPE);
		if (this._stringvalue != null) {
			xml.addChild(this._stringvalue.getXMLEntity());
		} else if (this._datevalue != null) {
			xml.addChild(this._datevalue.getXMLEntity());
		} else if (this._decimalvalue != null) {
			xml.addChild(this._decimalvalue.getXMLEntity());
		} else {
			xml.addChild(this._integervalue.getXMLEntity());
		}

		return xml;
	}

	/**
	 * Load object
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		XMLEntity xml2 = xml.getChild(0);
		if (xml2.getName().trim()
				.compareToIgnoreCase(GeocodingTags.STRINGVALUE) == 0) {
			this._stringvalue = new Stringvalue();
			this._stringvalue.setXMLEntity(xml2);
		} else if (xml2.getName().trim()
				.compareToIgnoreCase(GeocodingTags.DATEVALUE) == 0) {
			this._datevalue = new Datevalue();
			this._datevalue.setXMLEntity(xml2);
		} else if (xml2.getName().trim()
				.compareToIgnoreCase(GeocodingTags.DECIMALVALUE) == 0) {
			this._decimalvalue = new Decimalvalue();
			this._decimalvalue.setXMLEntity(xml2);
		} else {
			this._integervalue = new Integervalue();
			this._integervalue.setXMLEntity(xml2);
		}
	}

}
