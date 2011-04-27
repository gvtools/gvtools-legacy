package org.gvsig.fmap.drivers.gpe.reader;

import org.gvsig.fmap.drivers.gpe.model.GPEFeature;
import org.gvsig.xmlschema.som.IXSSchemaDocument;
import org.gvsig.xmlschema.som.IXSTypeDefinition;

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
/* CVS MESSAGES:
 *
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class AddFeatureToDriver {
	private IXSSchemaDocument schema = null;
		
	public AddFeatureToDriver(){
		
	}

	/**
	 * Add a feature to a layer
	 * @param driver
	 * The driver
	 * @param feature
	 * The feature to add
	 */
	public void addFeatureToLayer(GPEVectorialDriver driver, GPEFeature feature){
		addFeatureToLayer_(driver, feature);		
	}

	/**
	 * Add a feature to a layer
	 * @param layer
	 * The layer
	 * @param feature
	 * The feature to add
	 */
	private void addFeatureToLayer_(GPEVectorialDriver driver, GPEFeature feature){
		IXSTypeDefinition elementType = null;
		//If the feature has a type it will try to retrieve it
//		if (feature.getTypeName() != null){
//			elementType = schema.getTypeByName(feature.getTypeName());
//		}
		//If the type exists in the schema
		if (elementType != null){
			//layer.addFeature(feature, elementType);
			driver.addFeature(feature);
			//If the type doesn't exist in the XML schema
		}else{
			driver.addFeature(feature);
		}		
	}

	/**
	 * @return the schema
	 */
	public IXSSchemaDocument getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(IXSSchemaDocument schema) {
		this.schema = schema;
	}



}
