/*
 * Created on 12-abr-2007
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
 * $Id: IndexedSptQueryFeatureIterator.java 30109 2009-07-23 10:34:02Z jpiera $
 * $Log$
 * Revision 1.3  2007-09-19 16:02:02  azabala
 * removed unused import
 *
 * Revision 1.2  2007/05/30 20:12:20  azabala
 * fastIteration = true optimized.
 *
 * Revision 1.1  2007/05/29 19:08:11  azabala
 * first version in cvs
 *
 * Revision 1.1  2007/04/19 17:27:58  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap.drivers.featureiterators;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;

/**
 * Feature iterator for a spatial query resolved with an spatial index.
 *
 * @author azabala
 *
 */
public class IndexedSptQueryFeatureIterator extends SpatialQueryFeatureIterator {

	/**
	 * Spatial index to resolve the spatial query
	 */
	private ISpatialIndex spatialIndex;

	/**
	 * List of indexes returned by te spatial index as result
	 * of the query, over iterator is going to iterate
	 */
	List<Integer> resultIdx;

	/**
	 * flag that marks if the iterator is doing a fast iteration
	 */
	boolean fastIteration;

	/**
	 * Constructor.
	 *
	 * @param source
	 * @param sourceCrs
	 * @param targetCrs
	 * @param fieldNames
	 * @param spatialQuery
	 * @param spatialIndex
	 * @param fastIteration
	 * @throws ReadDriverException
	 */
	public IndexedSptQueryFeatureIterator(ReadableVectorial source,
			CoordinateReferenceSystem sourceCrs,
			CoordinateReferenceSystem targetCrs, String[] fieldNames,
			Rectangle2D spatialQuery, ISpatialIndex spatialIndex,
			boolean fastIteration)throws ReadDriverException {
		super(source, sourceCrs, targetCrs, fieldNames, spatialQuery,
				fastIteration);
		this.spatialIndex = spatialIndex;
		this.fastIteration = fastIteration;
		//the query is in the source projection, not in the targetProj
		//(in super() spatialQuery is reprojected)
		this.resultIdx = this.spatialIndex.query(this.rect);
	}

//	public boolean hasNext() {
//		if (fastIteration) {
//			if (resultIdx != null && currentFeature < resultIdx.size())
//				return true;
//			else
//				return false;
//		} else {
//			try {
//				while (true) {
//					if (currentFeature >= resultIdx.size())
//						return false;
//					if (spatialChecker.intersects(rect,
//							((Integer) resultIdx.get(currentFeature)).intValue()))
//						return true;
//					currentFeature++;
//				}// while
//			} catch (ExpansionFileReadException e) {
//				e.printStackTrace();
//				return false;
//			} catch (ReadDriverException e) {
//				e.printStackTrace();
//				return false;
//			}
//
//		}
//	}

	public boolean hasNext(){
		try {
			while (true) {
				if (currentFeature >= resultIdx.size())
					return false;
				Integer indexObj = resultIdx.get(currentFeature);
				if(indexObj != null){
					if (spatialChecker.intersects(rect,indexObj.intValue()))//((Integer) resultIdx.get(currentFeature)).intValue()))
						return true;
				}else{
					return false;
				}
				currentFeature++;
			}// while
		} catch (ExpansionFileReadException e) {
			e.printStackTrace();
			return false;
		} catch (ReadDriverException e) {
			e.printStackTrace();
			return false;
		}
	}

	public IFeature next() throws ReadDriverException {
		IGeometry geom;
		if (fastIteration) {
			try {
				geom = chekIfCloned(source.getShape(spatialChecker.getIndexOfLast()));
				reprojectIfNecessary(geom);
			} catch (ExpansionFileReadException e) {
				throw new ReadDriverException("Error accediendo al driver", e);
			}
		} else {
			//we dont check if spatialChecker.returnShapes because when fastIteration = false,
			//spatialChecker is PrecisseSpatialChecker (always return shapes)
			geom = spatialChecker.getLastGeometry();
		}
		Value[] regAtt = getValues(spatialChecker.getIndexOfLast());
		DefaultFeature feat = new DefaultFeature(geom, regAtt, spatialChecker.getIndexOfLast() + "");
		currentFeature++;
		return feat;
	}

}
