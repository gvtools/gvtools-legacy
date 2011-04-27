/*
 * Created on 01-mar-2006
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
/*
 * Created on 01-mar-2006
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
/* CVS MESSAGES:
*
* $Id: NumericFieldListModel.java 7372 2006-09-19 17:57:47Z azabala $
* $Log$
* Revision 1.3  2006-09-19 17:57:47  azabala
* we made public internal class (it is needed in other classes)
*
* Revision 1.2  2006/08/11 16:12:27  azabala
* *** empty log message ***
*
* Revision 1.1  2006/05/24 21:13:09  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.1  2006/03/05 19:56:06  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.core.gui;

import javax.swing.AbstractListModel;

/**
 * @author azabala
 *
 */
public class NumericFieldListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;
	private String[] numericFields;

	public NumericFieldListModel(String[] numericFields) {
		this.numericFields = numericFields;
	}

	public int getSize() {
		if (numericFields == null)
			return 0;
		else
			return numericFields.length;
	}

	public Object getElementAt(int arg0) {
		if (numericFields == null || numericFields.length == 0)
			return new NumericFieldListEntry(null, arg0);
		else
			return new NumericFieldListEntry(numericFields[arg0], arg0);
	}
	
	public class NumericFieldListEntry{
		int index;
		String key;
		
		NumericFieldListEntry(String key, int index){
			this.key = key;
			this.index = index;
		}
		
		public int hashCode(){
			return 1;
		}
		
		public String getKey(){
			return key;
		}
		
		public boolean equals(Object o){
			if(!(o instanceof NumericFieldListEntry))
				return false;
			return ((NumericFieldListEntry)o).key.equals(key);
		}
		
		public String toString(){
			String prefix = index + " - ";
			if(key == null)
				return prefix;
			else
				return prefix + key;
		}
	}
}

