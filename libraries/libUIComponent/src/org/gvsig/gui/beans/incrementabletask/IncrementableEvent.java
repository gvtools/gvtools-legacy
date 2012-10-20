package org.gvsig.gui.beans.incrementabletask;

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

import java.util.EventObject;

/**
 * Se utiliza para poder definir que acción esta ocurriendo en un momento dado
 * en la ventana IncrementableTask
 * 
 * @version 23/04/2007
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class IncrementableEvent extends EventObject {
	private static final long serialVersionUID = 75795297813891036L;

	/**
	 * Definir si la acción que esta ocurriendo es la de resumir.
	 */
	public static final int RESUMED = 1;

	/**
	 * Definir si la acción que esta ocurriendo es la de suspender
	 */
	public static final int SUSPENDED = 2;

	/**
	 * Definir si la acción que esta ocurriendo es la de cancelar.
	 */
	public static final int CANCELED = 3;

	/**
	 * Constructor de IncrementableEvent
	 * 
	 * @param source
	 */
	public IncrementableEvent(Object source) {
		super(source);
	}
}