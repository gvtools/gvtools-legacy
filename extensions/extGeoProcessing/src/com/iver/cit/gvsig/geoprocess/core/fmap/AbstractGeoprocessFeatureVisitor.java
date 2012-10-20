/*
 * Created on 06-jul-2006
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
 * $Id: AbstractGeoprocessFeatureVisitor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/07/21 09:08:15  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;

/**
 * Abstract base class to all feature visitor created for geoprocessing.
 * 
 * @author azabala
 * 
 */
public abstract class AbstractGeoprocessFeatureVisitor implements
		FeatureVisitor {
	/**
	 * processes visited features.
	 */
	FeatureProcessor featureProcessor;

	/**
	 * Constructor. It receives a FeatureProcessor to process visited features
	 * (usually to save them)
	 * 
	 * @param processor
	 */
	public AbstractGeoprocessFeatureVisitor(FeatureProcessor processor) {
		this.featureProcessor = processor;
	}

	public abstract void visit(IGeometry g, int index) throws VisitorException,
			ProcessVisitorException;

	public abstract String getProcessDescription();

	/**
	 * finishes visitation by closing result layer writing.
	 */
	public void stop(FLayer layer) throws VisitorException {
		featureProcessor.finish();
	}

	/**
	 * to start the visit process, prepares result layer.
	 */
	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			this.featureProcessor.start();
			return true;
		}
		return false;
	}

}
