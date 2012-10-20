/*
 * Created on 02-feb-2006
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
 * $Id: AttributeBufferVisitor.java 35728 2011-07-08 12:01:37Z fpuga $
 * $Log$
 * Revision 1.4  2007-08-07 15:09:22  azabala
 * changes to remove UnitUtils' andami dependencies
 *
 * Revision 1.3  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.2  2006/07/21 11:22:52  azabala
 * fixed bug 654: AttributeBufferVisitor overwrited result processor of BufferVisitor (and always was null)
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.2  2006/06/02 18:20:33  azabala
 * limpieza de imports
 *
 * Revision 1.1  2006/05/24 21:14:41  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.8  2006/05/01 19:18:09  azabala
 * revisión general del buffer (añadidos anillos concentricos, buffers interiores y exteriores, etc)
 *
 * Revision 1.7  2006/03/28 16:27:16  azabala
 * *** empty log message ***
 *
 * Revision 1.6  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/05 19:57:05  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/17 16:31:38  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/12 21:02:44  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/09 16:01:06  azabala
 * First version in CVS
 *
 * Revision 1.1  2006/02/02 19:46:01  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.buffer.fmap;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.engine.values.NumericValue;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.util.UnitUtils;

/**
 * FeatureVisitor that computes buffer geometries from original geometrie and an
 * attribute value of IFeature
 * 
 * @author azabala
 * 
 */
public class AttributeBufferVisitor extends BufferVisitor {
	/**
	 * Attribute name from which visitor will get buffer distances
	 */
	private String attributeName;

	/**
	 * projection of the active view. Useful to convert linear distances in
	 * angular arcs (approach for buffering geometries in geographic
	 * coordinates)
	 */
	private CoordinateReferenceSystem crs;
	private int distanceUnits;
	private int mapUnits;

	/**
	 * Constructor
	 * 
	 * @param attributeName
	 * @param crs
	 * @param mapUnits
	 * @param distanceUnits
	 * @param recordset
	 */
	public AttributeBufferVisitor(String attributeName,
			CoordinateReferenceSystem crs, int distanceUnits, int mapUnits)
			throws GeoprocessException {
		this.attributeName = attributeName;
		this.crs = crs;
		this.distanceUnits = distanceUnits;
		this.mapUnits = mapUnits;
	}

	public void stop(FLayer layer) throws VisitorException {
		resultsProcessor.finish();
	}

	public double getBufferDistance(IGeometry g, int index) {
		NumericValue value = null;
		try {
			int fieldIndex = recordset.getFieldIndexByName(attributeName);
			// we could throw an AttributeBufferException, or
			// we could check preconditions in BufferGeoprocess
			// TODO to check attribute is a NUMBER
			value = (NumericValue) recordset.getFieldValue(index, fieldIndex);
		} catch (Exception e) {
			e.printStackTrace();
			return 0d;
		}
		if (value == null)
			return 0d;

		return UnitUtils.getInInternalUnits(value.doubleValue(), crs,
				distanceUnits, mapUnits);
	}

	public String getProcessDescription() {
		return "Computing buffers applying distances from attribute";
	}
}
