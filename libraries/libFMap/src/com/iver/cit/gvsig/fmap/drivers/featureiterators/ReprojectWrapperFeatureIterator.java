/*
 * Created on 07-jun-2007
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
* $Id: ReprojectWrapperFeatureIterator.java 23921 2008-10-14 13:43:30Z vcaballero $
* $Log$
* Revision 1.2  2007-09-19 16:25:04  jaume
* ReadExpansionFileException removed from this context
*
* Revision 1.1  2007/06/07 11:49:28  azabala
* first version in cvs
*
*
*/
package com.iver.cit.gvsig.fmap.drivers.featureiterators;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;

/**
 * FeatureIterator for drivers that can not reproject
 * features
 * @author azabala
 *
 */
public class ReprojectWrapperFeatureIterator implements IFeatureIterator{


	IFeatureIterator featureIterator;
	protected CoordinateReferenceSystem sourceCrs;
	protected CoordinateReferenceSystem targetCrs;

	public ReprojectWrapperFeatureIterator(IFeatureIterator featureIterator,
			CoordinateReferenceSystem sourceCrs,
			CoordinateReferenceSystem targetCrs) {
		this.featureIterator = featureIterator;
		this.sourceCrs = sourceCrs;
		this.targetCrs = targetCrs;
	}

	public boolean hasNext() throws ReadDriverException {
		return featureIterator.hasNext();
	}

	public IFeature next() throws ReadDriverException {
		IFeature solution = featureIterator.next();
		reprojectIfNecessary(solution.getGeometry());
		return solution;
	}

	public void closeIterator() throws ReadDriverException {
		featureIterator.closeIterator();
	}

	/**
	 *
	 * Checks if must reproject the given geom
	 * and reprojects it if true
	 * @param geom
	 *
	 * TODO Esto es igual que DefaultFeatureIterator. Esta clase
	 * no extiende de la anterior porque esta clase no quiere saber nada
	 * del origen de datos (ReadableVectorial). Refactorizar y crear una
	 * clase abstracta intermedia entre ambas
	 */
	protected void reprojectIfNecessary(IGeometry geom){
		if (this.targetCrs != null && this.sourceCrs != null
				&& !this.targetCrs.getName().equals(this.sourceCrs.getName())){
			MathTransform trans = ProjectionUtils.getCrsTransform(sourceCrs,  targetCrs);
			geom.reProject(trans);
		}
	}

}

