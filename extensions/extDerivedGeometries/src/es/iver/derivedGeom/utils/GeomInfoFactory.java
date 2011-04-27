package es.iver.derivedGeom.utils;

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

import java.sql.Types;

import javax.swing.ImageIcon;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;

/**
 * <p>Creates the particular {@link GeomInfo GeomInfo} by its name and type.</p>
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class GeomInfoFactory {
	/**
	 * <p>Creates a <code>JLabel</code> that represents a geometric information,
	 *  and stores the configuration parameters.</p> 
	 * 
	 * @param text the text of the label
	 * @param name the name of the label
	 * @param type the type of the geometry
	 * 
	 * @return the new object
	 * 
	 * @see Types
	 */
	public static GeomInfo createGeomInfo(String text, String name, int type) {
		ImageIcon icon = null;

		switch (type) {
			case FShape.POLYGON:
				icon = PluginServices.getIconTheme().get("Polygon");
				return new GeomInfo(icon, text, name, type);
			case FShape.LINE:
				icon = PluginServices.getIconTheme().get("Rect");
				return new GeomInfo(icon, text, name, type);
			case FShape.MULTIPOINT:
				icon = PluginServices.getIconTheme().get("MultiPoint");
				return new GeomInfo(icon, text, name, type);
			case FShape.POINT:
				icon = PluginServices.getIconTheme().get("Point");
				return new GeomInfo(icon, text, name, type);
			case FShape.MULTI: // Don't used
				icon = PluginServices.getIconTheme().get("multi-icon");
				return new GeomInfo(icon, text, name, type);
			default:
				throw new IllegalArgumentException();
		}
	}
}
