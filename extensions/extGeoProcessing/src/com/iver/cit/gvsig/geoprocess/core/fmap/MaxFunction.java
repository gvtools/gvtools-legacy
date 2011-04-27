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
 * $Id: MaxFunction.java 29470 2009-06-19 08:49:29Z jmvivo $
 * $Log$
 * Revision 1.3  2007-05-24 10:35:12  caballero
 * Min_Value
 *
 * Revision 1.2  2006/06/20 18:19:43  azabala
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

/**
 * * Sumarization function that returns min value of all field values.
 *
 * @author azabala
 */
public class MaxFunction implements SummarizationFunction {

	boolean set = false;
	double maxValue = 0;

	public void process(NumericValue value) {
		double val = value.doubleValue();
		if (!set || maxValue < val) {
			maxValue = val;
			set= true;
		}
	}

	public NumericValue getSumarizeValue() {
		if(!set){
			//process method wasnt be called
			return ValueFactory.createValue(0d);
		}
		return ValueFactory.createValue(maxValue);
	}

	public String toString(){
		return "max";
	}

	public void reset() {
		set = false;
	}

}
