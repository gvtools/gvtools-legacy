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
 * $Id: SumFuncListModel.java 6745 2006-08-11 16:12:27Z azabala $
 * $Log$
 * Revision 1.3  2006-08-11 16:12:27  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/06/20 18:19:43  azabala
 * refactorización para que todos los nuevos geoprocesos cuelguen del paquete impl
 *
 * Revision 1.1  2006/05/24 21:13:09  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.3  2006/05/08 15:37:08  azabala
 * bug fixed
 *
 * Revision 1.2  2006/04/07 19:00:58  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:56:06  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.gui;

import java.util.Map;

import javax.swing.AbstractListModel;

import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;

/**
 * 
 * @author azabala
 * 
 */
public class SumFuncListModel extends AbstractListModel {

	private static final long serialVersionUID = 1L;
	String[] fieldNames;
	Map nfield_sumFunctions;

	public SumFuncListModel(Map field_functions, String[] fieldNames) {
		this.nfield_sumFunctions = field_functions;
		this.fieldNames = fieldNames;
	}

	public int getSize() {
		int solution = fieldNames.length;
		return solution;
	}

	public Object getElementAt(int arg0) {
		String key = "";
		String fieldName = fieldNames[arg0];
		SummarizationFunction[] functions = (SummarizationFunction[]) nfield_sumFunctions
				.get(fieldName);
		if ((functions == null) || functions.length == 0)
			return new SumarizeFunctionListEntry(null, arg0);// We use a blank
																// space for an
																// error of

		for (int i = 0; i < functions.length - 1; i++) {
			key += functions[i].toString();
			key += ",";
		}
		key += functions[functions.length - 1].toString();
		return new SumarizeFunctionListEntry(key, arg0);
	}

	class SumarizeFunctionListEntry {
		int index;
		String entry;

		SumarizeFunctionListEntry(String entry, int index) {
			this.entry = entry;
			this.index = index;
		}

		public int hashCode() {
			return 1;
		}

		public String toString() {
			String prefix = index + " - ";
			if (entry == null)
				return prefix;
			else
				return prefix + entry;
		}

		public boolean equals(Object o) {
			if (!(o instanceof SumarizeFunctionListEntry))
				return false;
			return ((SumarizeFunctionListEntry) o).entry.equals(entry);
		}
	}

}
