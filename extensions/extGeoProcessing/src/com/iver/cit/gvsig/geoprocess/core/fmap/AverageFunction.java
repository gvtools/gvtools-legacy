/*
 * Created on 23-feb-2006
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
 * $Id: AverageFunction.java 5919 2006-06-20 18:21:48Z azabala $
 * $Log$
 * Revision 1.2  2006-06-20 18:19:43  azabala
 * refactorización para que todos los nuevos geoprocesos cuelguen del paquete impl
 *
 * Revision 1.1  2006/05/24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/02/26 20:55:28  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.ValueFactory;

public class AverageFunction implements SummarizationFunction {
	double sum = 0d;
	int numElements = 0;

	public void process(NumericValue value) {
		sum += value.doubleValue();
		numElements++;
	}

	public NumericValue getSumarizeValue() {
		if(numElements == 0)
			return ValueFactory.createValue(0d);
		double res = sum / (double)numElements;
		return ValueFactory.createValue(res);
	}
	
	public String toString(){
		return "avg";
	}

	public void reset() {
		sum = 0d;
		numElements = 0;
		
	}
}
