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
package com.iver.cit.gvsig.fmap.layers.layerOperations;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;

/**
 * <p>
 * Interface that layers with vector data must implement.
 * </p>
 * 
 * <p>
 * Has methods for supporting the <i>visitor pattern</i> on the vector data
 * features.
 * </p>
 */
public interface VectorialData {
	/**
	 * <p>
	 * Processes the layer's geometries indicated in the bit set, using the
	 * criterion of a <code>FeatureVisitor</code> object.
	 * </p>
	 * 
	 * @param visitor
	 *            object that allows visit each feature
	 * @param subset
	 *            indicates the indexes of the geometries that will be visited
	 * 
	 * @throws ReadDriverException
	 *             any exception produced reading with the driver.
	 * @throws ExpansionFileReadException
	 *             any exception produced accessing an
	 *             <code>ExpansionFile</code>.
	 * @throws VisitorException
	 *             any exception produced visiting the features.
	 */
	public void process(FeatureVisitor visitor, FBitSet subset)
			throws ReadDriverException, ExpansionFileReadException,
			VisitorException;

	/**
	 * <p>
	 * Processes the layer's geometries which intersect the rectangle passed as
	 * parameter, using the criterion of a <code>FeatureVisitor</code> object.
	 * </p>
	 * 
	 * @param visitor
	 *            object that allows visit each feature
	 * @param rect
	 *            indicates the boundaries that will be analyzed
	 * 
	 * @throws ReadDriverException
	 *             any exception produced reading with the driver.
	 * @throws ExpansionFileReadException
	 *             any exception produced accessing an
	 *             <code>ExpansionFile</code>.
	 * @throws VisitorException
	 *             any exception produced visiting the features.
	 */
	public void process(FeatureVisitor visitor, Rectangle2D rect)
			throws ReadDriverException, ExpansionFileReadException,
			VisitorException;

	/**
	 * <p>
	 * Processes the layer's geometries using the criterion of a
	 * <code>FeatureVisitor</code> object.
	 * </p>
	 * 
	 * @param visitor
	 *            object that allows visit each feature
	 * 
	 * @throws ReadDriverException
	 *             any exception produced reading with the driver.
	 * @throws VisitorException
	 *             any exception produced visiting the features.
	 */
	public void process(FeatureVisitor visitor) throws ReadDriverException,
			VisitorException;
}
