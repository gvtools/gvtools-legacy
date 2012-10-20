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

/**
 * 
 * 
 * @version 20/08/2008
 * 
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public interface IIncrementable {
	/**
	 * Devuelve el titulo de la ventana IncrementableTask
	 * 
	 * @return String
	 */
	public String getTitle();

	/**
	 * Devuelve el contenido del log de la ventana IncrementableTask
	 * 
	 * @return String
	 */
	public String getLog();

	/**
	 * Devuelve la etiqueta de la ventana IncrementableTask
	 * 
	 * @return String
	 */
	public String getLabel();

	/**
	 * Devuelve el porcentaje de 0 a 100 de la ventana IncrementableTask
	 * 
	 * @return int
	 */
	public int getPercent();

	/**
	 * <p>
	 * Determines if this process can be canceled.
	 * </p>
	 * 
	 * @return <code>true</code> if this process can be canceled, otherwise
	 *         <code>false</code>
	 */
	public boolean isCancelable();

	/**
	 * <p>
	 * Determines if this process can be paused.
	 * </p>
	 * 
	 * @return <code>true</code> if this process can be paused, otherwise
	 *         <code>false</code>
	 */
	public boolean isPausable();
}