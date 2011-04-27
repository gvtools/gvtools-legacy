/*
 * Created on 12-abr-2007
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 * $Id: SpatialQueryFeatureIterator.java 31422 2009-10-29 12:19:04Z nbrodin $
 * $Log$
 * Revision 1.4  2007-09-19 16:02:29  azabala
 * bug solved (feature returned by iterator has the attributes of the next record)
 *
 * Revision 1.3  2007/06/07 09:30:09  azabala
 * refactor of BOUND FACTOR
 *
 * Revision 1.2  2007/06/06 18:03:03  azabala
 * bug fixed
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

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.geotools.resources.geometry.XRectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FNullGeometry;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;

public class SpatialQueryFeatureIterator extends DefaultFeatureIterator {

	/**
	 * region for which features we are iterating
	 */
	protected Rectangle2D rect;

	/**
	 * Checks feature geometries to see if intersects the given extent filter
	 */
	protected ISpatialCheck spatialChecker;

	/**
	 * Feature which will be returned the next time
	 */
	IFeature nextFeature = null;

	private boolean isUsedNext=true;
	private boolean hasNext=false;

	/**
	 * Proportion of query extent area and layer extent area
	 * to use boundedshapes capability (reading bounds without
	 * reading its geometry)
	 *
	 */
	public static final double BOUNDED_SHAPES_FACTOR = 4.0d;

	/**
	 * Constructor.
	 *
	 * @param source
	 *            vectorial data source
	 * @param sourceProj
	 *            original projection of the data (it could be null)
	 * @param targetProj
	 *            projection of the returned features
	 * @param fieldNames
	 * @param spatialQuery
	 * @throws ReadDriverException
	 */
	public SpatialQueryFeatureIterator(ReadableVectorial source,
			IProjection sourceProj, IProjection targetProj,
			String[] fieldNames, Rectangle2D spatialQuery, boolean fastIteration)
			throws ReadDriverException {
		super(source, sourceProj, targetProj, fieldNames);
		setRect(spatialQuery);
		if(fastIteration)
			spatialChecker = new FastSpatialCheck();
		else
			spatialChecker = new PrecisseSpatialCheck();
	}

	public Rectangle2D getRect() {
		return rect;
	}

	public void setRect(Rectangle2D rect) {
		//by design, rect Rectangle2D must be in the target reprojection
		//if targetReprojection != sourceReprojection, we are going to reproject
		//rect to the source reprojection (is faster).
		//once spatial check is made, result features will be reprojected in the inverse direction
		if(this.targetProjection != null &&
		   this.sourceProjection != null &&
		   this.targetProjection.getAbrev() != this.sourceProjection.getAbrev()){
			ICoordTrans trans = targetProjection.getCT(sourceProjection);
			try{
				rect = trans.convert(rect);
			} catch (Exception e) {
				// if we get an exception during re-projection, that usually
				// means that the provided rectangle coordinates exceed the
				// logical limits of the target CRS. In this case we can fallback
				// to the full layer extent
				try {
					this.rect = source.getFullExtent();
				} catch (ExpansionFileReadException e1) {
					throw new RuntimeException(e1);
				} catch (ReadDriverException e1) {
					throw new RuntimeException(e1);
				}
			}
		}
		this.rect = rect;
	}

	public boolean hasNext() throws ReadDriverException {
		if(!isUsedNext){
			return hasNext;
		}
		try {
			while(true){
				if(currentFeature >= source.getShapeCount())
					return (hasNext=false);
				if(spatialChecker.intersects(rect, currentFeature)){
					//we only update the counter if spatialChecker could read the geometry
					//if it is boundedshape it doesnt read the geometry, so we need to read
					//currentFeature again
//					if(spatialChecker.returnShapes())
//						currentFeature++;
					break;
				}
				else
					currentFeature++;
			}//while


			//here, currentFeature intersects with the rectangle2d filter
			IGeometry geom = null;
			IFeature feat = null;
			if(spatialChecker.returnShapes()){
				geom = spatialChecker.getLastGeometry();
			}else{
				geom = chekIfCloned(source.getShape(currentFeature));
//				currentFeature++;
				reprojectIfNecessary(geom);
			}
			Value[] regAtt = getValues(currentFeature);
			if (geom==null) {
				geom = new FNullGeometry();
			}
			feat = new DefaultFeature(geom, regAtt, currentFeature + "");
			nextFeature = feat;
			currentFeature++;
			return (hasNext = true);
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(
					"Error accediendo a datos del driver durante la iteracion",
					e);
		}
	}

	public IFeature next() throws ReadDriverException {
		isUsedNext=true;
		return nextFeature;
	}

	/**
	 * Classes that chekcs if a specified feature intersects with a given Rectangle2D must
	 * implement this interface.
	 *
	 * This interface is necessary because there are many approach to do that
	 * (an exact intersection, an intersection based in bounds2d, etc)
	 *
	 *
	 * @author azabala
	 *
	 */
	interface ISpatialCheck {
		public boolean intersects(Rectangle2D extent, int featureIndex)
				throws ExpansionFileReadException, ReadDriverException;

		/**
		 * Tells if this spatialcheck could return the geometry of the features
		 * (or if it is boundedshapes based, needs another driver access operation)
		 * @return
		 */
		public boolean returnShapes();
		/**
		 * Returns the last readed geometry (if the spatialcheck do that)
		 * @return
		 */
		public IGeometry getLastGeometry();

		/**
		 * Return the index of the last readed geometry
		 * @return
		 */
		public int getIndexOfLast();


	}




	/**
	 * All classes that return the bounds Rectangle2D of a feature must
	 * implement this interface.
	 *
	 * @author azabala
	 *
	 */
	interface BoundsProvider {
		/**
		 * Returns the bound of the specified feature index
		 *
		 * @param featureIndex
		 * @return
		 * @throws ExpansionFileReadException
		 * @throws ReadDriverException
		 */
		public Rectangle2D getBounds(int featureIndex)
				throws ExpansionFileReadException, ReadDriverException;

		/**
		 * Tells if this boundsProvider could returns shapes
		 *
		 * @return
		 */
		public boolean returnShapes();

		/**
		 * Returns the last geometry readed, if the boundsProvider could do that
		 *
		 * @return
		 */
		public IGeometry getLastGeometry();
	}




	/**
	 * BoundsProvider that uses a BoundedShapes (faster than others)
	 *
	 * @author azabala
	 *
	 */
	class BoundedShapesProvider implements BoundsProvider {
		BoundedShapes boundedShapes;

		BoundedShapesProvider(BoundedShapes boundedShapes) {
			this.boundedShapes = boundedShapes;
		}

		public Rectangle2D getBounds(int featureIndex)
				throws ExpansionFileReadException, ReadDriverException {
			return boundedShapes.getShapeBounds(featureIndex);
		}

		public boolean returnShapes() {
			return false;
		}

		public IGeometry getLastGeometry() {
			return null;
		}

	}




	/**
	 * BoundsProvider that returns feature bounds from the feature geometry
	 *
	 * @author azabala
	 *
	 */
	class IGeometryBoundProvider implements BoundsProvider {
		/**
		 * Adapter of a given driver from which we read the features bounds.
		 */
		ReadableVectorial source;

		IGeometry lastGeometry;

		IGeometryBoundProvider(ReadableVectorial sourceOfFeatures) {
			this.source = sourceOfFeatures;
		}

		public Rectangle2D getBounds(int featureIndex)
				throws ExpansionFileReadException, ReadDriverException {
			lastGeometry = chekIfCloned(source.getShape(featureIndex));
			if (lastGeometry ==null)
				return new Rectangle2D.Double(0,0,0,0);
//			reprojectIfNecessary(this.lastGeometry);
			//bounds2D is in the original projection
			Rectangle2D solution = lastGeometry.getBounds2D();
			//the readed geometry in the specified projection
//			reprojectIfNecessary(this.lastGeometry);
			return solution;
		}

		public IGeometry getLastGeometry() {
			reprojectIfNecessary(this.lastGeometry);
			return lastGeometry;
		}

		public boolean returnShapes() {
			return true;
		}

	}




	/**
	 * Checks if the specified features intersects with rectangle2D instances in
	 * a rough but fast manner.
	 *
	 */
	class FastSpatialCheck implements ISpatialCheck {
		BoundsProvider boundProvider;
		int lastIndex;


		public FastSpatialCheck() {
			try {
				if(isBoundedShapesNecessary()){
					if (source instanceof BoundedShapes){
						boundProvider = new BoundedShapesProvider(
								(BoundedShapes) source);
					}else if (source.getDriver() instanceof BoundedShapes){
						boundProvider = new BoundedShapesProvider(
								(BoundedShapes) source.getDriver());
				    }else{
				    	boundProvider = new IGeometryBoundProvider(source);
				    }
				}else{
						boundProvider = new IGeometryBoundProvider(source);
				}
			} catch (ExpansionFileReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				boundProvider = new IGeometryBoundProvider(source);
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				boundProvider = new IGeometryBoundProvider(source);
			}
		}


		protected boolean isBoundedShapesNecessary() throws ReadDriverException, ExpansionFileReadException {
			Rectangle2D driverExtent = source.getFullExtent();
			double areaExtent = rect.getWidth() * rect.getHeight();
			double areaFullExtent = driverExtent.getWidth() *
				                         driverExtent.getHeight();
			return areaExtent < (areaFullExtent / BOUNDED_SHAPES_FACTOR);

		}

		public boolean intersects(Rectangle2D extent, int featureIndex)
				throws ExpansionFileReadException, ReadDriverException {
			this.lastIndex = featureIndex;
			Rectangle2D featureBounds = boundProvider.getBounds(featureIndex);
			if(featureBounds == null)
				return false;
			return XRectangle2D.intersectInclusive(extent, featureBounds);
		}

		public BoundsProvider getBoundsProvider() {
			return boundProvider;
		}

		public boolean returnShapes() {
			return boundProvider.returnShapes();
		}

		public IGeometry getLastGeometry() {
			return boundProvider.getLastGeometry();
		}

		public int getIndexOfLast() {
			return lastIndex;
		}

	}// FastSpatialCheck


	/**
	 * Checks if the specified features intersect with rectangle2D instances in
	 * a precisse manner
	 *
	 * @author azabala
	 *
	 */

	class PrecisseSpatialCheck implements ISpatialCheck {
		IGeometry lastGeometry;
		int lastIndex;

		public PrecisseSpatialCheck() {
		}

		public boolean intersects(Rectangle2D extent, int featureIndex)
				throws ExpansionFileReadException, ReadDriverException {
			this.lastIndex = featureIndex;
			this.lastGeometry = chekIfCloned(source.getShape(lastIndex));
			//the spatial check is made in the original projection
			boolean solution = lastGeometry.fastIntersects(rect.getMinX(),
					rect.getMinY(), rect.getWidth(), rect.getHeight());
			//but the solution is returned in the new projection (if applies)
			reprojectIfNecessary(lastGeometry);
			return solution;
		}

		public boolean returnShapes() {
			return true;
		}

		public IGeometry getLastGeometry() {
			return lastGeometry;
		}

		public int getIndexOfLast() {
			return lastIndex;
		}
	}
}
