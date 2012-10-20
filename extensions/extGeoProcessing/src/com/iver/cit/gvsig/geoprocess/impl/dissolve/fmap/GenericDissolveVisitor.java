/*
 * Created on 21-mar-2006
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
 * $Id: GenericDissolveVisitor.java 13874 2007-09-19 16:12:18Z jaume $
 * $Log$
 * Revision 1.3  2007-09-19 16:06:36  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.3  2006/06/08 18:24:43  azabala
 * modificaciones para admitir capas de shapeType MULTI
 *
 * Revision 1.2  2006/06/02 18:21:28  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/05/24 21:11:14  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.2  2006/03/23 21:03:45  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/21 19:27:38  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class GenericDissolveVisitor implements FeatureVisitor {

	/**
	 * Allows to get attributes of disolved layer features
	 */
	SelectableDataSource recordset;

	/**
	 * Is used to do spatial querys (looking for adjacent polygons to visited
	 * feature geometry
	 */
	FLyrVect dissolvedLayer;

	/**
	 * It marks all features that have already been dissolved (to avoid process
	 * them in subsecuent steps)
	 * 
	 */
	FBitSet dissolvedGeometries;

	/**
	 * Processes results of dissolve operations (save them in a file, or cache
	 * them in memory, etc)
	 */
	FeatureProcessor featureProcessor;
	/**
	 * Strategy to make queries to first layer
	 */
	Strategy strategy;

	/**
	 * Visitor to looks for adjacent features to a given feature
	 */
	IndividualGeometryDissolveVisitor visitor = null;

	/**
	 * Constructor
	 * 
	 * @param layerToDissolve
	 */
	public GenericDissolveVisitor(FeatureProcessor processor) {
		this.featureProcessor = processor;
		dissolvedGeometries = new FBitSet();
	}

	public int getNumProcessedGeometries() {
		return dissolvedGeometries.cardinality();
	}

	/*
	 * Algorithm to compute dissolve is strongly based in depth first algorithm
	 * to traverse graphs.
	 * 
	 * It puts features to dissolve in a stack. While stack is not empty, get
	 * Features and looks for adjacent to it. For each adjacent feature, verify
	 * its dissolve field value, and if it is similar to feature to dissolve
	 * with, obtain a new feature by unioning their geometries. For each
	 * adjacent feature, put it in the Stack
	 */
	public void visit(IGeometry g, int index) throws VisitorException,
			ProcessVisitorException {
		if (g == null)
			return;
		if (g.getGeometryType() != XTypes.POLYGON
				&& g.getGeometryType() != XTypes.MULTI)
			return;
		if (!dissolvedGeometries.get(index)) {
			// if we havent dissolved this feature
			Stack toDissol = new Stack();// stack for adjacent features
			DissolvedFeature feature;
			try {
				feature = createFeature(g, index);
				Geometry jtsGeometry = g.toJTSGeometry();
				feature.setJtsGeometry(jtsGeometry);
				toDissol.push(feature);
				List geometries = dissolve(toDissol);
				Geometry dissolvedGeom = union(geometries);
				DissolvedFeature newFeature = createFeature(null, -1);
				newFeature.setJtsGeometry(dissolvedGeom);
				this.featureProcessor.processFeature(newFeature);
			} catch (ReadDriverException e) {
				throw new ProcessVisitorException(recordset.getName(), e,
						"Error al procesar las geometrias a fusionar durante dissolve");
			} catch (VisitorException e) {
				throw new ProcessVisitorException(recordset.getName(), e,
						"Error al procesar las geometrias a fusionar durante dissolve");
			}
		}// if
	}

	protected Geometry union(List geometries) {
		Geometry[] geom = new Geometry[geometries.size()];
		geometries.toArray(geom);
		GeometryFactory fact = geom[0].getFactory();
		Geometry geomColl = fact.createGeometryCollection(geom);
		Geometry union = geomColl.buffer(0.0);
		return union;
	}

	/**
	 * Inner class to manage dissolve geoprocess. It mantains feature info
	 * interesting for dissolve (int index, JTS Geometry, etc)
	 * 
	 * @author azabala
	 * 
	 */
	class DissolvedFeature extends DefaultFeature {
		int index;

		Geometry jtsGeometry;

		public DissolvedFeature(IGeometry geom, Value[] att, int index) {
			super(geom, att);
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public Geometry getJtsGeometry() {
			return jtsGeometry;
		}

		public void setJtsGeometry(Geometry jtsGeometry) {
			this.jtsGeometry = jtsGeometry;
		}

		public IGeometry getGeometry() {
			IGeometry solution = super.getGeometry();
			if (solution == null && jtsGeometry != null) {
				solution = FConverter.jts_to_igeometry(jtsGeometry);
			}
			return solution;
		}

	}

	/**
	 * Creates a new IFeature with util info for dissolve geoprocess (it ignores
	 * non numerical values, etc)
	 * 
	 * @param g
	 * @param index
	 * @return
	 * @throws DriverException
	 */
	private DissolvedFeature createFeature(IGeometry g, int index) {
		DissolvedFeature solution = new DissolvedFeature(g, null, index);
		return solution;
	}

	/**
	 * For each individual geometry processed in DissolveVisitor's visit method,
	 * this Visitor visits its adjacent polygons geometries to check dissolve
	 * conditions.
	 * 
	 * @author azabala
	 * 
	 */
	class IndividualGeometryDissolveVisitor implements FeatureVisitor {
		/**
		 * Marks index of features that have been dissolved yet
		 */
		FBitSet dissolvedFeatures;

		/**
		 * It saves all features for we are looking for adjacent geometries.
		 * Dissolving is similar to network tracking algorithms: one feature is
		 * adjacent to two, two is adjacent to four, etc We will save features
		 * to process in this stack (Depth first aproximation)
		 */
		Stack featuresToDissolve;

		/**
		 * Feature for which we are looking for features to dissolve
		 */
		DissolvedFeature feature;

		/**
		 * Recordset to recover attribute values
		 */
		SelectableDataSource recordset;

		IndividualGeometryDissolveVisitor(DissolvedFeature feature,
				FBitSet dissolvedFeatures, Stack featuresToDissolve) {
			this.dissolvedFeatures = dissolvedFeatures;
			this.feature = feature;
			this.featuresToDissolve = featuresToDissolve;
		}

		public String getProcessDescription() {
			return "Dissolving a polygon with its adjacents";
		}

		public void visit(IGeometry g, int index) throws VisitorException,
				ProcessVisitorException {
			// Its the feature whose adjacents we are looking for?
			if (index == feature.getIndex())
				return;
			// have we dissolved this feature yet?
			if (dissolvedFeatures.get(index))
				return;
			Geometry jtsGeo = g.toJTSGeometry();
			DissolvedFeature adjacentFeature = createFeature(g, index);
			adjacentFeature.setJtsGeometry(jtsGeo);
			if (jtsGeo.intersects(feature.getJtsGeometry())) {// They are
																// adjacent
				// TODO PARA HACER GENERICO EL ALGORITMO, Y QUE VALGA
				// TANTO PARA DISSOLVER BUFFERS COMO PARA UN DISSOLVE
				// ALFANUMERICO, AQUI USARIAMOS UN STRATEGY DISTINTO
				dissolvedFeatures.set(index);
				featuresToDissolve.push(adjacentFeature);
				// geometriesToDissolve.add(jtsGeo);
				// List toDissolve = dissolve(featuresToDissolve);
				// geometriesToDissolve.addAll(toDissolve);
			}// if touches
		}// visit

		public void stop(FLayer layer) throws VisitorException {
		}

		// FIXME Create an abstract FeatureVisitor
		public boolean start(FLayer layer) throws StartVisitorException {
			try {
				recordset = ((AlphanumericData) layer).getRecordset();
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}

		public void setProcessedFeature(DissolvedFeature feature2) {
			this.feature = feature2;

		}
	}// IndividualDissolve

	private List dissolve(Stack toDissol) throws ReadDriverException,
			ExpansionFileReadException, VisitorException {
		List solution = new ArrayList();
		while (toDissol.size() != 0) {
			DissolvedFeature feature = (DissolvedFeature) toDissol.pop();
			// flags this idx (to not to process in future)
			dissolvedGeometries.set(feature.getIndex());
			if (visitor == null) {
				visitor = new IndividualGeometryDissolveVisitor(feature,
						dissolvedGeometries, toDissol);
			} else {
				visitor.setProcessedFeature(feature);
			}
			solution.add(feature.getJtsGeometry());
			Rectangle2D bounds = feature.getGeometry().getBounds2D();
			double xmin = bounds.getMinX();
			double ymin = bounds.getMinY();
			double xmax = bounds.getMaxX();
			double ymax = bounds.getMaxY();
			double magnify = 15d;
			Rectangle2D query = new Rectangle2D.Double(xmin - magnify, ymin
					- magnify, (xmax - xmin) + magnify, (ymax - ymin) + magnify);

			strategy.process(visitor, query);
		}// while
		return solution;
	}

	public void stop(FLayer layer) throws VisitorException {
		this.featureProcessor.finish();
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			try {
				dissolvedLayer = (FLyrVect) layer;
				recordset = ((AlphanumericData) layer).getRecordset();
				featureProcessor.start();
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}
		return false;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public String getProcessDescription() {
		return "Dissolving polygons of a layer";
	}

}
