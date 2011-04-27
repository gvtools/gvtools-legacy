/*
 * Created on 02-may-2006
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
 * $Id: SpatiallyIndexedSpatialJoinVisitor.java 18783 2008-02-12 12:57:15Z vcaballero $
 * $Log$
 * Revision 1.3  2007-09-19 16:08:13  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.2  2006/06/02 18:21:28  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/05/24 21:09:47  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.1  2006/05/02 18:58:47  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap;

import java.awt.geom.Rectangle2D;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.spatialindex.INearestNeighbourFinder;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This visitor does spatial join with nearest neighbour criteria by using an
 * spatial index with nearest neighbour searching capabilities.<br>
 * By now, RTreeSptLib is the only spatial index that is also a
 * INearestNeigbourFinder implementation. <br>
 *
 * @author azabala
 *
 */
public class SpatiallyIndexedSpatialJoinVisitor extends
		NearestSpatialJoinVisitor {
	/**
	 * Geometry.distance() is a costly operation. Thats the reason for we are
	 * only using a nearest neighbour. TODO SpatialIndex works with Rectangle2D,
	 * and this may be a simplification that drives to errors. Make aditional
	 * probes to use a number of default neighbours
	 */
	static final int DEFAULT_NUM_NEIGBOURS = 1;

	/**
	 * Number of neighbours that nearestFinder must found.
	 */
	int numOfNeighbours = DEFAULT_NUM_NEIGBOURS;

	/**
	 * Spetialized instance in nearest neighbour searchs.
	 */
	private INearestNeighbourFinder nearestFinder;

	/**
	 * Constructor
	 *
	 * @param sourceLayer
	 * @param targetLayer
	 * @param processor
	 * @throws DriverException
	 */
	public SpatiallyIndexedSpatialJoinVisitor(FLyrVect sourceLayer,
			FLyrVect targetLayer, FeatureProcessor processor)
			throws ReadDriverException {
		super(sourceLayer, targetLayer, processor);
		// lo comentamos porque debe ser llamado externamente
		// initialize();
	}

	public void initialize() {
		ISpatialIndex spatialIndex = targetLayer.getISpatialIndex();
		if (spatialIndex instanceof INearestNeighbourFinder)
			nearestFinder = (INearestNeighbourFinder) spatialIndex;
		else
			throw new IllegalArgumentException(
					"La segunda capa de spatial join ha de tener un índice espacial de tipo INearestNeighbourFinder para usar este visitor");
	}

	/**
	 * Processes a Feature of source layer, looking for its nearest feature of
	 * target layer and taking attributes from it
	 */
	public void visit(IGeometry g, int sourceIndex) throws VisitorException, ProcessVisitorException {
		if (g == null)
			return;
		try {
			// no se si el rtree hará la busqueda bien si el rectangulo
			// de busqueda no está insertado. Hacer las pruebas (si no,
			// añadimos ahora y borramos luego)
			Geometry gJts = g.toJTSGeometry();
			Rectangle2D rect = g.getBounds2D();
			List nearestLst = nearestFinder.findNNearest(numOfNeighbours, rect);
			int targetIndex = -1;// index of nearest neighbour
			double nearestDistance = Double.MAX_VALUE;
			ReadableVectorial rv = targetLayer.getSource();
			rv.start();
			for (int i = 0; i < nearestLst.size(); i++) {
				int index2 = ((Integer) nearestLst.get(i)).intValue();
				IGeometry g2 = rv.getShape(index2);
				Geometry g2Jts = g2.toJTSGeometry();
				double dist = gJts.distance(g2Jts);
				if (dist <= nearestDistance) {
					// by adding <=, we follow the convention that
					// if two features are at the same distance, take
					// the last as nearest neighbour
					nearestDistance = dist;
					targetIndex = index2;
				}// if
			}// for
			rv.stop();
			if (targetIndex == -1)
				throw new ProcessVisitorException(targetRecordset.getName(),null,
						"Problemas durante el spatial join, no se encontró un vecino mas proximo");
			IFeature joinedFeature = createFeature(g, sourceIndex, targetIndex,
					nearestDistance);
			this.featureProcessor.processFeature(joinedFeature);
		} catch (ReadDriverException e) {
			throw new ProcessVisitorException(targetRecordset.getName(),e,
					"Error de driver al escribir un feature resultante de un spatial join");
		}

	}

}
