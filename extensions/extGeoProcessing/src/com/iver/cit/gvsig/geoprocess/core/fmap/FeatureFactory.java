/*
 * Created on 08-feb-2006
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
 * $Id: FeatureFactory.java 5412 2006-05-24 21:15:07Z azabala $
 * $Log$
 * Revision 1.1  2006-05-24 21:12:16  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.4  2006/03/21 19:30:46  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/14 18:32:46  fjp
 * Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
 *
 * Revision 1.2  2006/02/17 16:34:00  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/09 15:59:48  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.fmap;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;

public class FeatureFactory {
	/**
	 * Precondition: objs order and layerdefinition' s fielddescriptions order
	 * must be the same.
	 * 
	 * @param objs
	 * @param g
	 * @param layerDefinition
	 * @return
	 */
	public static IFeature createFeature(Object[] objs, IGeometry g,
			ITableDefinition layerDefinition) {
		IFeature solution = null;
		Value[] attributes = new Value[layerDefinition.getFieldsDesc().length];
		// Aunque no nos hayan pedido todos los campos, devolveremos
		// tantos atributos como la capa tiene. Eso sí, puestos a null
		for (int i = 0; i < objs.length; i++) {
			Object object = objs[i];
			// Esto hay que corregirlo
			int fieldType = layerDefinition.getFieldsDesc()[i].getFieldType();
			Value newValue = XTypes.getValue(object, fieldType);
			attributes[i] = newValue;
		}
		solution = new DefaultFeature(g, attributes);
		return solution;
	}

	public static IFeature createFeature(Value[] values, IGeometry g) {
		return new DefaultFeature(g, values);
	}
}
