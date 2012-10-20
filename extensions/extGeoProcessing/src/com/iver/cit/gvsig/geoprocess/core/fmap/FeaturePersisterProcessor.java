/*
 * Created on 16-feb-2006
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
 * $Id: FeaturePersisterProcessor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.2  2007-03-06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/05/24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.4  2006/03/15 18:34:03  azabala
 * *** empty log message ***
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
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISchemaManager;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.vividsolutions.jts.geom.Geometry;

/**
 * It saves in a persistent data store the result of an individual geoprocess.
 * 
 * @author azabala
 * 
 * 
 * 
 */
public class FeaturePersisterProcessor extends GeometryPersisterProcessor {

	private SelectableDataSource recordset;

	public FeaturePersisterProcessor(ITableDefinition layerDefinition,
			ISchemaManager schemaManager, IWriter writer)
			throws SchemaEditionException, VisitorException {
		super(layerDefinition, schemaManager, writer);
	}

	public FeaturePersisterProcessor(ITableDefinition layerDefinition,
			ISchemaManager schemaManager, IWriter writer,
			SelectableDataSource recordset) throws SchemaEditionException,
			VisitorException {
		this(layerDefinition, schemaManager, writer);
		this.recordset = recordset;
	}

	public void setSelectableDataSource(SelectableDataSource recordset) {
		this.recordset = recordset;
	}

	public void processJtsGeometry(Geometry g, int index)
			throws VisitorException, ReadDriverException {
		IGeometry clipGeometry = FConverter.jts_to_igeometry(g);
		FieldDescription[] fields = this.layerDefinition.getFieldsDesc();
		Value[] attrs = new Value[fields.length];
		for (int i = 0; i < fields.length; i++) {
			FieldDescription field = fields[i];
			String attrName = field.getFieldName();
			Value value = null;
			int fieldIndex = recordset.getFieldIndexByName(attrName);
			value = recordset.getFieldValue(index, fieldIndex);
			attrs[i] = value;
		}// for

		IFeature feature = FeatureFactory.createFeature(attrs, clipGeometry);
		DefaultRowEdited editedFeature = new DefaultRowEdited(feature,
				IRowEdited.STATUS_ADDED, index);
		writer.process(editedFeature);
	}

}
