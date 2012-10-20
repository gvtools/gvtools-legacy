/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

package org.gvsig.remoteClient.arcims.utils;

import java.text.ParseException;
import java.util.Date;

import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * Class that will generate convenient Value objects using ArcIMS types.
 * 
 * @author jsanz
 */
public class ArcImsValueFactory extends ValueFactory {
	public static Value createValueByType(String text, int type, char delDec)
			throws ParseException {
		Value value;

		switch (type) {
		case FieldInformation.BOOLEAN:
			value = ValueFactory.createValue(Boolean.valueOf(text)
					.booleanValue());

			break;

		case FieldInformation.SHAPE:
		case FieldInformation.STRING:
			value = ValueFactory.createValue(text);

			break;

		case FieldInformation.DATE:

			// This tipe is changed to use miliseconds as source of the value
			if (text != null) {
				value = ValueFactory
						.createValue(new Date(Long.parseLong(text)));
			} else {
				value = ValueFactory.createNullValue();
			}

			break;

		case FieldInformation.FLOAT:

			if (text != null) {
				value = ValueFactory.createValue(Float.parseFloat(text.replace(
						delDec, '.')));
			} else {
				value = ValueFactory.createNullValue();
			}

			break;

		case FieldInformation.DOUBLE:

			if (text != null) {
				value = ValueFactory.createValue(Double.parseDouble(text
						.replace(delDec, '.')));
			} else {
				value = ValueFactory.createNullValue();
			}

			break;

		case FieldInformation.SMALLINT:
			value = ValueFactory.createValue(Short.parseShort(text));

			break;

		case FieldInformation.BIGINT:
			value = ValueFactory.createValue(Long.parseLong(text));

			break;

		case FieldInformation.ID:
		case FieldInformation.INTEGER:
			value = ValueFactory.createValue(Integer.parseInt(text));

			break;

		default:
			value = ValueFactory.createValue(text);
		}

		return value;
	}
}
