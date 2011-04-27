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
package com.iver.cit.gvsig.fmap.drivers;

/**
 * <p>Exception produced accessing a driver for input or output operations.</p>
 */
public class DriverIOException extends Exception {
	/**
	 * <p>Constructs a new driver I/O exception with <code>null</code> as its detail message.
	 *  The cause is not initialized, and may subsequently be initialized by a call to <code>initCause</code>.</p>
	 */
	public DriverIOException() {
		super();
	}

	/**
	 * <p>Constructs a new driver I/0 exception with the specified detail message. The cause is not
	 *  initialized, and may subsequently be initialized by a call to <code>initCause</code>.
	 * 
	 * @param message the detail message. The detail message is saved for later retrieval by the <code>getMessage()</code> method.
	 */
	public DriverIOException(String message) {
		super(message);
	}

	/**
	 * <p>Constructs a new driver I/O exception with the specified detail message and cause.</p>
	 * <p>Note that the detail message associated with <code>cause</code> is not automatically incorporated in this
	 *  exception's detail message.</p>
	 *  
	 * @param message the detail message (which is saved for later retrieval by the <code>getMessage()</code> method).
	 * @param cause the cause (which is saved for later retrieval by the <code>getCause()</code> method). (A <code>null</code>
	 *  value is permitted, and indicates that the cause is nonexistent or unknown).
	 */
	public DriverIOException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * <p>Constructs a new driver I/O exception with the specified cause and a detail message of <code>(cause==null ?
	 *  null : cause.toString())</code> (which typically contains the class and detail message of cause).
	 *  
	 * @param cause the cause (which is saved for later retrieval by the <code>getCause()</code> method). (A <code>null</code>
	 *  value is permitted, and indicates that the cause is nonexistent or unknown).
	 */
	public DriverIOException(Throwable cause) {
		super(cause);
	}
}
