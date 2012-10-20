/*
 * Created on 26-ene-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
 * @author FJP
 * 
 *         Se usa para habilitar una comunicación entre los drivers y las capas
 *         Aquí meteremos cosas como si el driver mete las cosas en memoria, si
 *         va a entregar las entidades ya proyectadas por sí mismo, si es
 *         steaming, o ese tipo de cosas. De todas formas, no es una decisión
 *         fija. Es probable que cambiemos el mecanismo en el futuro, así que
 *         cuidado con usar demasiado esta clase. Otra posibilidad es ponerle
 *         una propiedad a la capa que sea getAttributes, y en lugar de hacer
 *         que pase por el adapter, asignarla en la clase
 *         <code>LayerFactory</code>, que es donde se asigna el driver a la
 *         capa, y en ese momento se puede traspasar la información.
 */
public class DriverAttributes {

	private boolean isLoadedInMemory = false;

	/**
	 * @return Returns the isLoadedInMemory.
	 */
	public boolean isLoadedInMemory() {
		return isLoadedInMemory;
	}

	/**
	 * @param isLoadedInMemory
	 *            The isLoadedInMemory to set.
	 */
	public void setLoadedInMemory(boolean isLoadedInMemory) {
		this.isLoadedInMemory = isLoadedInMemory;
	}
}
