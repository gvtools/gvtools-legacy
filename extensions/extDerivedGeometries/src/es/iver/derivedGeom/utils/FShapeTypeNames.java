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

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;

/**
 * <p>Utility to convert a shape type in its representation in characters.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class FShapeTypeNames {
	/**
	 * <p>Returns an <code>String</code> that represents a <code>FShape</code> type.</p>
	 * 
	 * @param type a type defined in {@link FShape FShape}
	 * @return an <code>String</code> that represents the type
	 */
	public static String getFShapeTypeName(int type) {
		switch(type) {
			case FShape.NULL:
				return "NULL";
			case FShape.POINT:
				return PluginServices.getText(null, "POINT");
			case FShape.LINE:
				return PluginServices.getText(null, "LINE");
			case FShape.POLYGON:
				return PluginServices.getText(null, "POLYGON");
			case FShape.TEXT:
				return PluginServices.getText(null, "TEXT");
			case FShape.MULTI:
				return PluginServices.getText(null, "MULTI");
			case FShape.MULTIPOINT:
				return PluginServices.getText(null, "MULTIPOINT");
			case FShape.CIRCLE:
				return PluginServices.getText(null, "CIRCLE");
			case FShape.ARC:
				return PluginServices.getText(null, "ARC");
			case FShape.ELLIPSE:
				return PluginServices.getText(null, "ELLIPSE");
			case FShape.Z:
				return "Z";
			default:
				return PluginServices.getText(null, "UNKNOWN");
		}
	}
}
