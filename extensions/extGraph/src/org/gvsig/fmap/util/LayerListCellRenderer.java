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
* 2008 Software Colaborativo (www.scolab.es)   development
*/
 
package org.gvsig.fmap.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.iver.cit.gvsig.fmap.layers.FLayer;


public class LayerListCellRenderer extends DefaultListCellRenderer {
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean hasFocus) {
		FLayer lyr = (FLayer) value;
		if (lyr == null) return this;
		 super.getListCellRendererComponent(list, 
                 value, 
                 index, 
                 isSelected, 
                 hasFocus);
		
		setText(lyr.getName());
		// TODO: PUT SOME ICONS HERE
		return this;
	}


}

