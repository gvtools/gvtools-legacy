package org.gvsig.xmlschema.som.impl;

import org.gvsig.xmlschema.som.IXSSchema;
import org.gvsig.xmlschema.som.IXSSimpleTypeDefinition;
import org.gvsig.xmlschema.utils.TypeUtils;

/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: XSSimpleTypeDefinitionImpl.java 151 2007-06-14 16:15:05Z jorpiell $
 * $Log$
 * Revision 1.1  2007/06/14 16:15:03  jorpiell
 * builds to create the jars generated and add the schema code to the libGPEProject
 *
 * Revision 1.1  2007/06/14 13:50:07  jorpiell
 * The schema jar name has been changed
 *
 * Revision 1.3  2007/06/07 14:54:13  jorpiell
 * Add the schema support
 *
 * Revision 1.2  2007/05/30 12:25:48  jorpiell
 * Add the element collection
 *
 * Revision 1.1  2007/05/25 11:55:00  jorpiell
 * First update
 *
 *
 */
/**
 * @author Jorge Piera LLodr� (jorge.piera@iver.es)
 */
public class XSSimpleTypeDefinitionImpl extends XSTypeDefinitionImpl implements
		IXSSimpleTypeDefinition {
	private Object value = null;

	public XSSimpleTypeDefinitionImpl(IXSSchema schema, Object value) {
		super(schema);
	}

	public XSSimpleTypeDefinitionImpl(IXSSchema schema) {
		super(schema);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.schema.som.IXSSimpleTypeDefinition#getValue()
	 */
	public Object getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.schema.som.IXSSimpleTypeDefinition#getValue()
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.schema.som.IXSTypeDefinition#getTypeName()
	 */
	public String getTypeName() {
		return TypeUtils.STRING;
	}

}
