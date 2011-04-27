	/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
* $Id: OnSelectionLabeled.java 10815 2007-03-20 16:16:20Z jaume $
* $Log$
* Revision 1.3  2007-03-20 16:16:20  jaume
* refactored to use ISymbol instead of FSymbol
*
* Revision 1.2  2007/03/09 08:33:43  jaume
* *** empty log message ***
*
* Revision 1.1.2.2  2007/02/01 11:42:47  jaume
* *** empty log message ***
*
* Revision 1.1.2.1  2007/01/30 18:10:45  jaume
* start commiting labeling stuff
*
*
*/
package org.gvsig.symbology.fmap.labeling;

import java.util.BitSet;

import org.gvsig.symbology.fmap.drivers.featureiterators.FeatureSelectionIterator;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.DefaultLabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
/**
 *
 * OnSelectionLabeled.java
 *
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es Apr 2, 2008
 *
 */
public class OnSelectionLabeled extends DefaultLabelingMethod {

	@Override
	public String getClassName() {
		return getClass().getName();
	}

	public IFeatureIterator getFeatureIteratorByLabelClass(FLyrVect layer, LabelClass lc, ViewPort viewPort, String[] usedFields)
	throws ReadDriverException {

		String sqlFields = "";
		for (int i = 0; i < usedFields.length; i++) {
			sqlFields += usedFields[i];
			if (i < usedFields.length -1) sqlFields += ", ";
		}
		String fieldNames[] = layer.getSource().getRecordset().getFieldNames();
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		for (int i=0; i<fieldNames.length-1; i++) {
			sql.append(fieldNames[i]);
			sql.append(",");
		}
		sql.append(fieldNames[fieldNames.length-1]);
		sql.append(" from ");
		sql.append(layer.getRecordset().getName());
		if(lc.isUseSqlQuery()){
			sql.append(" where ");
			sql.append(lc.getSQLQuery());
		}
		sql.append(";");

		return layer.getSource().getFeatureIterator(sql.toString(), layer.getProjection(),true);


	}


}
