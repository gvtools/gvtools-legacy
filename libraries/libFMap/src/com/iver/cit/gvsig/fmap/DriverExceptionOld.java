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
package com.iver.cit.gvsig.fmap;

import com.iver.utiles.ExceptionDescription;
import com.iver.utiles.IDescriptableException;

/**
 * <p>A <code>DriverException</code> is thrown if a driver fails. This exception doesn't reports about
 *  the cause of the failure, that task is made by its inner {@link ExceptionDescription ExceptionDescription}.</p>
 */
public class DriverExceptionOld extends Exception implements IDescriptableException{
	
	/*
	 * azabala.
	 * El problema que tenemos es que una DriverException puede ser debida
	 * a multiples causas, y para poder mostrar al usuario información de cara
	 * a que subsane estas causas (sobretodo considerando el estado UnAvalaible
	 * de un FLayer) seria necesario hacer un analisis exhaustivo de estas causas.
	 * 
	 * Por este motivo añado un ExceptionDescription a DriverException
	 * (si finalmente DriverIOException tb lo requiere, habría que meter herencias)
	 * que SÍ que tiene esta inforamación.
	 * 
	 */
	/**
	 * <p>Detailed information about the cause of this exception. It's useful for example for {@link DriverException DriverException}.</p>
	 */
	private ExceptionDescription exceptionType;
	
	/**
	 * <p>Constructs a new <code>DriverException</code> with the specified detail message. The cause is not
	 *  initialized, and may subsequently be initialized by a call to initCause.</p>
	 */
	public DriverExceptionOld(String message) {
		super(message);
	}
	
	/**
	 * <p>Constructs a new <code>DriverException</code> with the specified detail message and the exception which
	 *  produced this one.</p>
	 * 
	 * @param message the detail message (which is saved for later retrieval by the getMessage() method).
	 * @param type the exception which caused this one and has detailed information about the causes
	 */
	public DriverExceptionOld(String message, ExceptionDescription type){
		super(message);
		this.exceptionType = type;
	}
	
	/**
	 * <p>Constructs a new <code>DriverException</code> with the specified detail message, cause and the original
	 *  exception that produced this one. This second exception will have the complete information about the causes.</p>
	 * <p>Note that the detail message associated with the cause is not automatically incorporated
	 *  in this exception's detail message.</p>
	 *
	 * @param message the detail message (which is saved for later retrieval calling <code>getMessage()</code>).
	 * @param cause the cause (which is saved for later retrieval calling <code>getCause()</code>)). (A <code>null</code>
	 *  value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param exceptionType detailed information about the cause of this exception
	 */
	public DriverExceptionOld(String message, 
							Throwable cause, 
						ExceptionDescription exceptionType){
		super(message, cause);
		this.exceptionType = exceptionType;
	}
	
	/**
	 * <p>Gets the exception which caused this one and has detailed information about the causes.</p>
	 *
	 * @return the exception which caused this one and has detailed information about the causes
	 */
	public ExceptionDescription getExceptionType(){
		if(exceptionType != null)
			return exceptionType;
		else return null;
//		else 
//			return ExceptionDescription.getType("GENERIC");
	}

	/**
	 * <p>Constructs a new <code>DriverException</code> with the specified detail message and cause.</p>
	 * 
	 * @param message the detail message (which is saved for later retrieval calling <code>getMessage()</code>).
	 * @param cause the cause (which is saved for later retrieval calling <code>getCause()</code>). (A <code>null</code>
	 *  value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public DriverExceptionOld(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public DriverExceptionOld(Throwable cause) {
		super(cause);
	}

	/**
	 * <p>Constructs a new <code>DriverException</code> with the cause and a description.</p>
	 * 
	 * @param cause the cause (which is saved for later retrieval calling <code>getCause()</code>). (A <code>null</code>
	 *  value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param type the exception which caused this one and has detailed information about the causes
	 */
	public DriverExceptionOld(Throwable cause, ExceptionDescription type){
		super(cause);
		this.exceptionType = type;
	}
}
