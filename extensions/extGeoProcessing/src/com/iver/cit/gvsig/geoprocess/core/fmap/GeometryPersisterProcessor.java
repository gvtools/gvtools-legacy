/*
 * Created on 09-feb-2006
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
 * $Id: GeometryPersisterProcessor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.4  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.3  2006/06/29 07:33:57  fjp
 * Cambios ISchemaManager y IFieldManager por terminar
 *
 * Revision 1.2  2006/05/31 09:10:12  fjp
 * Ubicación de IWriter
 *
 * Revision 1.1  2006/05/24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.4  2006/05/01 19:10:09  azabala
 * comentario
 *
 * Revision 1.3  2006/03/14 18:32:46  fjp
 * Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
 *
 * Revision 1.2  2006/02/26 20:54:52  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/17 16:33:25  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/13 17:52:45  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/12 21:02:58  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/09 16:00:36  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISchemaManager;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Persists individual buffer results in a persistent data store
 * 
 * @author azabala
 * 
 */
public class GeometryPersisterProcessor implements
		GeoprocessingResultsProcessor {
	protected ITableDefinition layerDefinition;
	protected ISchemaManager schemaManager;
	protected IWriter writer;

	private long numProcessed = 0;

	public GeometryPersisterProcessor(ITableDefinition layerDefinition,
			ISchemaManager schemaManager, IWriter writer)
			throws SchemaEditionException, VisitorException {
		this.layerDefinition = layerDefinition;
		this.schemaManager = schemaManager;
		this.writer = writer;
		this.schemaManager.createSchema(layerDefinition);
		this.writer.preProcess();
	}

	// FIXME usar el atributo INDEX para leer del recordset atributos
	public void processJtsGeometry(Geometry g, int index)
			throws VisitorException, ReadDriverException {
		IGeometry fmapBuffer = FConverter.jts_to_igeometry(g);
		Object[] attrs = new Object[1];
		attrs[0] = new Long(numProcessed++);
		IFeature feature = FeatureFactory.createFeature(attrs, fmapBuffer,
				layerDefinition);
		DefaultRowEdited editedFeature = new DefaultRowEdited(feature,
				IRowEdited.STATUS_ADDED, (int) numProcessed);
		writer.process(editedFeature);
	}

	public void finish() throws VisitorException {
		writer.postProcess();
	}

	public void processFeature(IFeature feature) throws VisitorException {
		DefaultRowEdited editedFeature = new DefaultRowEdited(feature,
				IRowEdited.STATUS_ADDED, (int) numProcessed);
		writer.process(editedFeature);
	}

}
