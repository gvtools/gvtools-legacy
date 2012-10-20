/*
 * Created on 15-mar-2006
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
 * $Id: DeferredFeaturePersisterProcessor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/05/24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/03/15 18:33:53  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.fmap.edition.IWriter;

/**
 * Subclass of FeaturePersisterProcessor2 that deferred postProcess call to
 * subjacent IWriter.
 * 
 * It is thinked for batch processes, because delegates preprocess and
 * postprocess calls to external processes that uses this class' instances.
 * 
 * PRECONDITIONS: IWriter has preprocessed yet. POSTCONDITIONS: Must call
 * IWriter postprocess externally
 * 
 * 
 * @author azabala
 * 
 */

public class DeferredFeaturePersisterProcessor extends
		FeaturePersisterProcessor2 {

	public DeferredFeaturePersisterProcessor(IWriter writer) {
		super(writer);
	}

	public void start() throws StartVisitorException {
	}

	public void finish() throws StopVisitorException {

	}

}
