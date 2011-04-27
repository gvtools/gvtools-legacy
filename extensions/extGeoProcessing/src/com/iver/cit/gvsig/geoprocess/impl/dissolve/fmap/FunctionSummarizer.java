/*
 * Created on 22-may-2006
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
* $Id: FunctionSummarizer.java 10626 2007-03-06 16:55:54Z caballero $
* $Log$
* Revision 1.2  2007-03-06 16:47:58  caballero
* Exceptions
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.1  2006/05/24 21:11:14  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;

/**
 * For a given key set of numeric fields to summarize, and the summarization
 * functions selected by user, it computes this summarization values.
 *
 *
 * @author azabala
 *
 */
public class FunctionSummarizer {
	/**
	 * Relates each numeric field with its sumarization functions
	 */
	Map numericField_functions;
	/**
	 * Reads field values, to compute sumarization functions
	 */
	SelectableDataSource recordset;
	/**
	 * Contains results of sumarization functions computations
	 */
	List sumarizedValues;

	/**
	 * Constructor
	 * @param numericField_functions
	 * @param recordset
	 */
	public FunctionSummarizer(Map numericField_functions,
			SelectableDataSource recordset){
		this.numericField_functions = numericField_functions;
		this.recordset = recordset;
		sumarizedValues = new ArrayList();
	}

	/**
	 * Resets sumarization functions state, and clear list of results
	 *
	 */
	public void resetFunctions() {
		if (numericField_functions == null)
			return;
		Iterator fieldsIt = numericField_functions.keySet().iterator();
		while (fieldsIt.hasNext()) {
			String field = (String) fieldsIt.next();
			SummarizationFunction[] functions =
				(SummarizationFunction[]) numericField_functions
					.get(field);
			for (int i = 0; i < functions.length; i++) {
				functions[i].reset();
			}// for
		}// while

		sumarizedValues.clear();
	}

	/**
	 * Reads field values of the feature of index "recordIndex", and applies
	 * sumarization functions for them
	 * @param recordIndex
	 * @throws DriverException
	 */
	public void applySumarizeFunction(int recordIndex)
			throws ReadDriverException {
		if (numericField_functions == null)
			return;
		Iterator fieldsIt = numericField_functions.keySet().iterator();
		while (fieldsIt.hasNext()) {
			String field = (String) fieldsIt.next();
			int fieldIndex = recordset.getFieldIndexByName(field);
			Value valToSumarize = recordset.getFieldValue(recordIndex,
					fieldIndex);
			SummarizationFunction[] functions =
				(SummarizationFunction[]) numericField_functions
													.get(field);
			for (int i = 0; i < functions.length; i++) {
				functions[i].process((NumericValue) valToSumarize);
			}// for
		}// while
	}

	public List getValues(){
		Iterator fieldsIt = numericField_functions.keySet().iterator();
		while(fieldsIt.hasNext()){
			String field = (String) fieldsIt.next();
			SummarizationFunction[] functions =
				(SummarizationFunction[]) numericField_functions
													.get(field);
			for (int i = 0; i < functions.length; i++) {
				sumarizedValues.add(functions[i].getSumarizeValue());
			}// for
		}//while
		return sumarizedValues;
	}


}

