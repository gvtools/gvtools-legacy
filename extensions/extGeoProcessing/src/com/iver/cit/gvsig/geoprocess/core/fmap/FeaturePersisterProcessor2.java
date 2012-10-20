/*
 * Created on 22-feb-2006
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
 * $Id: FeaturePersisterProcessor2.java 13881 2007-09-19 16:22:04Z jaume $
 * $Log$
 * Revision 1.3  2007-09-19 16:02:53  jaume
 * removed unnecessary imports
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/05/24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.3  2006/03/15 18:34:03  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/05 19:58:58  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/26 20:55:08  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;

/**
 * It saves in a persistent data store Features, by using a IWriter. It does
 * preprocess and postprocess in start() and stop() methods.
 * 
 * @author azabala
 * 
 */
public class FeaturePersisterProcessor2 implements FeatureProcessor {
	/*
	 * precondition: writer must have its schema created with
	 * ISchemaManager#createOrAlterSchema() FIXME Redesing writer to create
	 * schema by implementing ISchemaManager interface
	 */
	IWriter writer;
	int numFeatures;

	public FeaturePersisterProcessor2(IWriter writer) {
		this.writer = writer;
	}

	public void processFeature(IRow feature) throws VisitorException {
		DefaultRowEdited editedFeature = new DefaultRowEdited(feature,
				IRowEdited.STATUS_ADDED, numFeatures++);
		writer.process(editedFeature);
	}

	public void start() throws StartVisitorException {
		this.writer.preProcess();
	}

	public void finish() throws StopVisitorException {
		writer.postProcess();
	}

}
