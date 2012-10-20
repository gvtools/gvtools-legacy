/* gvSIG. Sistema de Informacion Geografica de la Generalitat Valenciana
 *
 * Copyright (C) 2009 IVER T.I.
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
 *  IVER T.I.
 *   C/ Lerida, 20
 *   46009 Valencia
 *   SPAIN
 *   http://www.iver.es
 *   dac@iver.es
 *   +34 963163400
 *   
 *  or
 *  
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibañez, 50
 *   46010 VALENCIA
 *   SPAIN
 */
package org.gvsig.app.documents.table.summarize.exceptions;

public class GroupingErrorException extends Exception {

	public GroupingErrorException() {
		super();
	}

	public GroupingErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public GroupingErrorException(String message) {
		super(message);
	}

	public GroupingErrorException(Throwable cause) {
		super(cause);
	}
}