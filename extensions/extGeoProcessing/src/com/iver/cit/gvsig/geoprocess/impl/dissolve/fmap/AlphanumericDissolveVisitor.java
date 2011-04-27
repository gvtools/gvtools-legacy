/*
 * Created on 26-abr-2006
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
 * $Id: AlphanumericDissolveVisitor.java 10626 2007-03-06 16:55:54Z caballero $
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
 * Revision 1.1  2006/05/24 21:11:14  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.2  2006/05/08 15:37:26  azabala
 * added alphanumeric dissolve
 *
 * Revision 1.1  2006/05/01 19:21:50  azabala
 * primera version en cvs (todavía no funciona)
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.vividsolutions.jts.geom.Geometry;

/**
 * <p>
 * This Visitor generates a dissolve of input layer based
 * in alphanumeric criteria: it dissolves two polygons with the same
 * dissolve attribute value, and doesnt care if they are adjacent or not.
 * </p>
 * @author azabala
 *
 */
public class AlphanumericDissolveVisitor extends DissolveVisitor {

	public AlphanumericDissolveVisitor(String dissolveField,
			FeatureProcessor processor) {
		super(dissolveField, processor);
	}

	protected boolean verifyIfDissolve(DissolvedFeature f1, DissolvedFeature f2) {
		// dissolveField is the last
		int fieldIndex = 0;
		if(numericField_sumarizeFunction != null)
			fieldIndex = numericField_sumarizeFunction.keySet().size();
		Value adjacentVal = f1.getAttribute(fieldIndex);
		Value val = f2.getAttribute(fieldIndex);
		if (adjacentVal.doEquals(val)) {
			return true;
		}// if val equals

		return false;
	}

	class IndividualAlphaVisitor extends IndividualGeometryDissolveVisitor{
		protected IndividualAlphaVisitor(DissolvedFeature feature, FBitSet dissolvedFeatures, Stack featuresToDissolve, Map fields_sumarize) {
			super(feature, dissolvedFeatures, featuresToDissolve, fields_sumarize);
		}

		public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
			if(g == null)
				return;
			if (index == feature.getIndex())
				return;
			// have we dissolved this feature yet?
			if (dissolvedFeatures.get(index))
				return;
			try {
				DissolvedFeature adjacentFeature = createFeature(g, index);
				Geometry jtsGeo = g.toJTSGeometry();
				adjacentFeature.setJtsGeometry(jtsGeo);
				if (verifyIfDissolve(feature, adjacentFeature)) {
					dissolvedFeatures.set(index);
					// we actualize geometry by unioning
					featuresToDissolve.push(adjacentFeature);
					applySumarizeFunction(index);
				}
			} catch (ReadDriverException e) {
				throw new ProcessVisitorException(recordset.getName(),e,
						"Error al cargar los polígonos adyacentes durante un dissolve");
			}
		}
		public String getProcessDescription() {
			return "";
		}
		public void stop(FLayer layer) throws VisitorException {
		}
		public boolean start(FLayer layer) throws StartVisitorException {
			return true;
		}
	}

	protected Value[] dissolveGeometries(Stack toDissol,
			List geometries) throws	ReadDriverException, VisitorException {
		IndividualAlphaVisitor visitor = null;
		DissolvedFeature feature = null;
		while (toDissol.size() != 0) {
			feature = (DissolvedFeature) toDissol.pop();
			// flags this idx (to not to process in future)
			dissolvedGeometries.set(feature.getIndex());
			if (visitor == null) {
				visitor = new IndividualAlphaVisitor(feature,
						dissolvedGeometries, toDissol,
						numericField_sumarizeFunction);
				visitor.setDissolveField(this.dissolveField);
			} else {
				visitor.setProcessedFeature(feature);
			}
			strategy.process(visitor);
			//al final de toda la pila de llamadas recursivas,
			//geometries tendrá todas las geometrias que debemos dissolver
			geometries.add(feature.getJtsGeometry());
		}// while
		Value[] values = visitor.getSumarizedValues2();
		return values;
	}

}
