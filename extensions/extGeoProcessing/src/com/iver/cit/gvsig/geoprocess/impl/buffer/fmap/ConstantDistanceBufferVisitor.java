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
 * $Id: ConstantDistanceBufferVisitor.java 32916 2010-07-01 09:12:40Z vcaballero $
 * $Log$
 * Revision 1.6  2007-08-07 15:09:22  azabala
 * changes to remove UnitUtils' andami dependencies
 *
 * Revision 1.5  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.4  2006/09/15 10:42:54  caballero
 * extensibilidad de documentos
 *
 * Revision 1.3  2006/08/29 07:21:09  cesar
 * Rename com.iver.cit.gvsig.fmap.Fmap class to com.iver.cit.gvsig.fmap.MapContext
 *
 * Revision 1.2  2006/06/29 17:31:47  azabala
 * Uses UnitUtils
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
 * Revision 1.3  2006/03/05 19:57:05  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/20 19:44:21  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/17 16:31:38  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/12 21:02:58  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/09 16:01:06  azabala
 * First version in CVS
 *
 * Revision 1.1  2006/02/02 19:46:51  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.buffer.fmap;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.util.UnitUtils;

/**
 * This FeatureVisitor computes buffers and saves it to a new FLayer. All
 * IFeature of this Layer will have the same buffer distance.
 * 
 * @author azabala
 * 
 */
public class ConstantDistanceBufferVisitor extends BufferVisitor {
	/**
	 * buffer distance to use
	 */
	private double distance;

	/**
	 * View projection (allow to convert, if needed, coordinates when working
	 * with not projected (geographics) CRS's)
	 */
	private CoordinateReferenceSystem crs;
	private int distanceUnits;
	private int mapUnits;

	/**
	 * Constructor
	 * 
	 * @param distance
	 *            buffer distance to use
	 * @param viewCrs
	 *            TODO
	 * @param mapUnits
	 * @param distanceUnits
	 * @throws GeoprocessException
	 */
	public ConstantDistanceBufferVisitor(double distance,
			CoordinateReferenceSystem viewCrs, int distanceUnits, int mapUnits)
			throws GeoprocessException {
		this.distance = distance;
		this.crs = viewCrs;
		this.distanceUnits = distanceUnits;
		this.mapUnits = mapUnits;
	}

	public void stop(FLayer layer) throws VisitorException {
		// we must flush JTS processors
		resultsProcessor.finish();
	}

	public double getBufferDistance(IGeometry g, int index) {
		// first: pass the user entry to internal units
		return UnitUtils.getInInternalUnits(distance, crs, distanceUnits,
				mapUnits);
	}

	public String getProcessDescription() {
		return "Computing buffers with constant distances";
	}

}
