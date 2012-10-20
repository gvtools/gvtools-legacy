/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.fmap.operations.strategies;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * Query by Theme.
 * 
 * @author Fernando González Cortés
 */
public class QueryByThemeVisitor implements FeatureVisitor {
	private FLayer layer;
	private FLayer toQuery;
	private int relation;
	private FBitSet bitset = null;

	/**
	 * Crea un nuevo QueryByThemeVisitor.
	 * 
	 * @param toQuery
	 *            Capa.
	 * @param layer
	 *            Capa.
	 * @param relation
	 *            Relación.
	 */
	public QueryByThemeVisitor(FLayer toQuery, FLayer layer, int relation) {
		this.layer = layer;
		this.toQuery = toQuery;
		this.relation = relation;
	}

	/**
	 * @throws ReadDriverException
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#visit(com.iver.cit.gvsig.fmap.core.IGeometry,
	 *      int)
	 */
	public void visit(IGeometry g, int index) throws ReadDriverException,
			VisitorException, ProcessVisitorException {
		Strategy s = StrategyManager.getStrategy((FLyrVect) toQuery);
		QueryByGeometryVisitor visitor = new QueryByGeometryVisitor(g, relation);
		s.process(visitor);

		bitset.or(visitor.getBitSet());
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#stop(com.iver.cit.gvsig.fmap.layers.FLayer)
	 */
	public void stop(FLayer layer) throws VisitorException {
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor#start(com.iver.cit.gvsig.fmap.layers.FLayer)
	 */
	public boolean start(FLayer layer) throws StartVisitorException {
		bitset = new FBitSet();

		return layer instanceof FLyrVect;
	}

	/**
	 * Devuelve un FBitSet con los índices de los registros como respuesta a la
	 * consulta.
	 * 
	 * @return FBitSet con los índices de los registros como respuesta a la
	 *         consulta.
	 */
	public FBitSet getBitset() {
		return bitset;
	}

	public String getProcessDescription() {
		return "Selecting geometries by layer criteria";
	}
}
