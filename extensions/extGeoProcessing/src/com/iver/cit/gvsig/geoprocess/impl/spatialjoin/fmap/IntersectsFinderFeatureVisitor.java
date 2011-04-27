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
 * $Id: IntersectsFinderFeatureVisitor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.2  2006/06/02 18:21:28  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/05/24 21:09:47  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.2  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:59:47  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;
import com.vividsolutions.jts.geom.Geometry;
/**
 * Given a feature of a JOIN source layer,
 *  it looks for intersecting geometries of JOIN target layer and applies
 *  a sumarization function to its numeric fields.
 *
 * @author azabala
 *
 */
public class IntersectsFinderFeatureVisitor implements FeatureVisitor {
	/**
	 * Reads atributes of target layer
	 */
	SelectableDataSource targetRecordset;
	/**
	 * Geometry of layer A whose intersections of layer B
	 * we are looking for
	 */
	private Geometry queryGeometry;
	/**
	 * It has indexes of intersections
	 */
	private List intersectIndexes = null;
	/**
	 * Relates a set of sumarization functions with a numeric field
	 * name
	 */
	Map fields_sumarizationFun = null;
	/**
	 * When its not null, has the selection of the source layer
	 */
	private FBitSet selection;

	/**
	 * Constructor from a given geometry
	 * @param geometry geometry whose intersections we are looking for
	 * @param functions sumarization functions for each numeric value of target layer
	 */
	public IntersectsFinderFeatureVisitor(Geometry geometry, Map functions) {
		this.queryGeometry = geometry;
		this.fields_sumarizationFun = functions;
		intersectIndexes = new ArrayList();
	}

	public IntersectsFinderFeatureVisitor(Map functions) {
		this.fields_sumarizationFun = functions;
		intersectIndexes = new ArrayList();
	}

	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
		if(selection != null){
			if(! selection.get(index)){
				//dont process feature because is not selected
				return;
			}
		}
		Geometry jtsGeo = g.toJTSGeometry();
		if (queryGeometry.intersects(jtsGeo)) {
			intersectIndexes.add(new Integer(index));
			try {
				applySumarizeFunction(index);
			} catch (ReadDriverException e) {
				throw new ProcessVisitorException(targetRecordset.getName(),e,"Error al acceder a los atributos de la capa destino en un spatial join");
			}
		}
	}

	public String getProcessDescription() {
		return "Looking intersects and sumarizing for a spatial join";
	}

	private void applySumarizeFunction(int recordIndex) throws ReadDriverException {
		Iterator fieldsIt = fields_sumarizationFun.keySet().iterator();
		while (fieldsIt.hasNext()) {
			String field = (String) fieldsIt.next();
			int fieldIndex = targetRecordset.getFieldIndexByName(field);
			Value valToSumarize = targetRecordset.getFieldValue(recordIndex,
					fieldIndex);
			SummarizationFunction[] functions =
				(SummarizationFunction[]) fields_sumarizationFun.get(field);
			for (int i = 0; i < functions.length; i++) {
				functions[i].process((NumericValue) valToSumarize);
			}// for
		}// while
	}

	public List getIntersectIndexes() {
		return intersectIndexes;
	}

	public int getNumIntersections() {
		return intersectIndexes.size();
	}

	public boolean hasFoundIntersections() {
		return intersectIndexes.size() > 0;
	}

	public void clearIntersections(){
		intersectIndexes.clear();
	}

	public void stop(FLayer layer) throws VisitorException {
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			try {
				targetRecordset = ((AlphanumericData) layer).getRecordset();
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}
		return false;
	}

	public void setQueryGeometry(Geometry queryGeometry) {
		this.queryGeometry = queryGeometry;
	}

	public void setSelection(FBitSet selection) {
		this.selection = selection;
	}

}
