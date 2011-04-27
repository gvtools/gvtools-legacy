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

import org.gvsig.normalization.persistence.GeocodingPersistence;
import org.gvsig.normalization.persistence.GeocodingTags;

import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;

/**
 * Class Infieldseparators.
 * 
 * This class has the characters that they define in one number the thousand
 * separator, the decimal separator and the text separator
 * 
 */
public class Infieldseparators implements GeocodingPersistence {

	/**
	 * Thousand separator
	 */
	private String _thousandseparator;

	/**
	 * decimal separator
	 */
	private String _decimalseparator;

	/**
	 * text separator
	 */
	private String _textseparator;

	/**
	 * Constructor
	 */
	public Infieldseparators() {
	}

	/**
	 * Returns the value of field 'decimalseparator'.
	 * 
	 * @return the value of field 'decimalseparator'.
	 */
	public String getDecimalseparator() {
		return this._decimalseparator;
	}

	/**
	 * Returns the value of field 'textseparator'.
	 * 
	 * @return the value of field 'textseparator'.
	 */
	public String getTextseparator() {
		return this._textseparator;
	}

	/**
	 * Returns the value of field 'thousandseparator'.
	 * 
	 * @return the value of field 'thousandseparator'.
	 */
	public String getThousandseparator() {
		return this._thousandseparator;
	}

	/**
	 * Sets the value of field 'decimalseparator'.
	 * 
	 * @param decimalseparator
	 *            the value of field 'decimalseparator'
	 */
	public void setDecimalseparator(String decimalseparator) {
		this._decimalseparator = decimalseparator;
	}

	/**
	 * Sets the value of field 'textseparator'.
	 * 
	 * @param textseparator
	 *            the value of field 'textseparator'.
	 */
	public void setTextseparator(String textseparator) {
		this._textseparator = textseparator;
	}

	/**
	 * Sets the value of field 'thousandseparator'.
	 * 
	 * @param thousandseparator
	 *            the value of field 'thousandseparator'.
	 */
	public void setThousandseparator(String thousandseparator) {
		this._thousandseparator = thousandseparator;
	}

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
		xml.setName(GeocodingTags.INFIELDSEPARATORS);
		xml.putProperty(GeocodingTags.THOUSANDSEPARATOR,
				this._thousandseparator);
		xml.putProperty(GeocodingTags.DECIMALSEPARATOR, this._decimalseparator);
		xml.putProperty(GeocodingTags.TEXTSEPARATOR, this._textseparator);

		return xml;
	}

	/**
	 * Load object
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		this._thousandseparator = xml
				.getStringProperty(GeocodingTags.THOUSANDSEPARATOR);
		this._decimalseparator = xml
				.getStringProperty(GeocodingTags.DECIMALSEPARATOR);
		this._textseparator = xml
				.getStringProperty(GeocodingTags.TEXTSEPARATOR);

	}

}
