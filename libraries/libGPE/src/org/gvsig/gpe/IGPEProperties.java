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
* 2008 Iver T.I.  {{Task}}
*/
 
package org.gvsig.gpe;

import java.util.Properties;

/**
 * This class is used to add properties to the {@link GPEDefaults}.
 * Each parser or writer that need configurable properties to work
 * have to use this class to provide them. 
 * <p>
 * The library uses the SPI (Service Provider Interface) mechanism to
 * add a set of configurable properties. The information about how
 * it is possible to add new properties can be read in 
 * {@link GPEDefaults}
 * </p>
 * @see {@link GPEDeafults} 
 */
public interface IGPEProperties {
	
	/**
	 * Returns a set of properties that are registered in 
	 * {@link GPEDefaults} and can be read and written by the
	 * consumer application.
	 */
	public Properties getProperties();	
		
}

