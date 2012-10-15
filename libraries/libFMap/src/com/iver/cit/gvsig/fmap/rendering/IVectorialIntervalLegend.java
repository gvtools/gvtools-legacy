/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig.fmap.rendering;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;



/**
 * Interface that allows the methods to classify the legend through intervals.
 * 
 */
public interface IVectorialIntervalLegend extends IClassifiedVectorLegend {



    public IInterval getInterval(Value v) ;
    public int getIntervalType();
   
   
	/**
	 * 
	 * Returns the symbol starting from an interval
	 *
	 * @param key interval.
	 *
	 * @return symbol.
	 */
    public ISymbol getSymbolByInterval(IInterval key);

    /**
     * Inserts the type of the classification of the intervals.
	 *
	 * @param tipoClasificacion type of the classification.
	 */
    public void setIntervalType(int tipoClasificacion);
    
    /**
     * Returns if the rest of values are used or not to be represented.
	 * @deprecated 18-09-07 when moving definitely to FeatureIterators
	 * 
	 * @return True if the rest of values are used.
	 */

    public void setDataSource(DataSource ds) throws FieldNotFoundException, ReadDriverException;
    /**
     * @deprecated 18/09/07 will be removed when Strategies will be discarded
     */
    public ISymbol getSymbol(int recordIndex) throws ReadDriverException;

}
