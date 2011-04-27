/*
 * Created on 09-may-2006
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
 * $Id: AdjacencyDissolveVisitor.java 13881 2007-09-19 16:22:04Z jaume $
 * $Log$
 * Revision 1.5  2007-09-19 16:06:59  jaume
 * removed unnecessary imports
 *
 * Revision 1.4  2007/08/07 16:06:04  azabala
 * centrilizing JTS in JTSFacade and allowing all geometry types (not only Polygon)
 *
 * Revision 1.3  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.2  2006/07/21 11:06:06  azabala
 * fixed bug 604: empty dist field in buffered dissolved features
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:11:14  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Stack;

import org.cresques.cts.ICoordTrans;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;
import com.vividsolutions.jts.precision.SimpleGeometryPrecisionReducer;

/**
 * <p>
 * This Visitor generates a dissolve of input layer based in adjacency criteria:
 * it dissolves two polygons, if and only if they are adjacent.
 * </p>
 * TODO: To dissolve buffers from radial rings create a DissolveVisitor
 * to compare FROM-TO Fields.
 *
 * @author azabala
 *
 */
public class AdjacencyDissolveVisitor extends DissolveVisitor {

	/**
	 * Reference to the buffer distance of the visited buffered feature.
	 */
	private DoubleValue currentBufferDistance = null;

	/**
	 * FIXME REFACTOR THIS!!!!
	 * This is a workaround to avoid the use of strategies
	 * and allow the cancelation
	 * 
	 * To optimize disolution of buffers, we need to avoid the reading
	 * of geometries that also has been processed. Strategies cant do this
	 * 
	 */
	CancellableMonitorable cancelMonitor = null;
	
//	FIXME Probe to optimize the union of the features 
	//(buffer + dissolve when applies to almost all features of the layer
	//is very inefficient
	private Geometry geometry;
	public AdjacencyDissolveVisitor(String dissolveField,
			FeatureProcessor processor) {
		super(dissolveField, processor);
	}

	//FIXME REFACTOR THIS!!!
	public void setCancelMonitor(CancellableMonitorable cancelMonitor){
		this.cancelMonitor = cancelMonitor;
	}
	protected boolean verifyIfDissolve(DissolvedFeature f1, DissolvedFeature f2) {
		Geometry geo1 = f1.getJtsGeometry();
		Geometry geo2 = f2.getJtsGeometry();
		return geo1.intersects(geo2);
	}

	/**
	 * Creates a new IFeature with util info for dissolve geoprocess
	 * (it ignore non numerical values, etc)
	 *
	 * @param g
	 * @param index
	 * @return
	 * @throws DriverException
	 */
	protected DissolvedFeature createFeature(IGeometry g, int index) {
		DissolvedFeature solution = new DissolvedFeature(g, null, index);
		return solution;
	}

	/**
	 * We overwrite this method because we are not interested in save sumarization
	 * function values with dissolved features.
	 * Instead, we want to add buffer distance like an attribute of them.
	 */
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
//		if(g.getGeometryType() != XTypes.POLYGON &&
//				g.getGeometryType() != XTypes.MULTI)
//			return;
		if (!dissolvedGeometries.get(index)) {
			try {
				int fieldIndex = recordset.getFieldIndexByName("DIST");
				currentBufferDistance = (DoubleValue) recordset.getFieldValue(index, fieldIndex);
				// if we havent dissolved this feature
				Stack toDissol = new Stack();// stack for adjacent features
				DissolvedFeature feature = createFeature(g, index);
				toDissol.push(feature);
//				ArrayList geometries = new ArrayList();
				Value[] values = dissolveGeometries(toDissol);
//				Geometry geometry = union(geometries);
				Value[] valuesWithFID = new Value[values.length + 1];
				System.arraycopy(values, 0, valuesWithFID, 1, values.length);
				valuesWithFID[0] = ValueFactory.createValue(fid);
				DissolvedFeature dissolved = new DissolvedFeature(null,valuesWithFID, fid/*index*/);
				dissolved.setJtsGeometry(geometry);
				this.featureProcessor.processFeature(dissolved);
				fid++;
				resetFunctions();
				geometry = null;
			} catch (ReadDriverException e) {
				throw new ProcessVisitorException(recordset.getName(),e,
					"Error al procesar las geometrias a fusionar durante dissolve");
			} catch (VisitorException e) {
				throw new ProcessVisitorException(recordset.getName(),e,
				"Error al procesar las geometrias a fusionar durante dissolve");
			}
		}// ifponer aqui geometry a null?
	}

	/**
	 * We overwrite this method to ignore sumarization values and to
	 * add buffer distance to the attributes of the result features.
	 * @throws VisitorException
	 * @throws ExpansionFileReadException
	 * @throws ReadDriverException
	 */
	protected Value[] dissolveGeometries(Stack toDissol) throws
			ReadDriverException, ExpansionFileReadException, VisitorException {

		IndividualGeometryDissolveVisitor visitor = null;
		DissolvedFeature feature = null;
		while (toDissol.size() != 0) {
			feature = (DissolvedFeature) toDissol.pop();
			// flags this idx (to not to process in future)
			dissolvedGeometries.set(feature.getIndex());
			if (visitor == null) {
				visitor = new IndividualGeometryDissolveVisitor(feature,
						dissolvedGeometries, toDissol,
						numericField_sumarizeFunction);
				visitor.setDissolveField(this.dissolveField);
			} else {
				visitor.setProcessedFeature(feature);
			}
			Rectangle2D bounds = feature.getGeometry().getBounds2D();
			double xmin = bounds.getMinX();
			double ymin = bounds.getMinY();
			double xmax = bounds.getMaxX();
			double ymax = bounds.getMaxY();
			double magnify = 15d;
			Rectangle2D query = new Rectangle2D.Double(xmin - magnify, ymin
					- magnify, (xmax - xmin) + magnify, (ymax - ymin) + magnify);

			if (dissolvedLayer.getISpatialIndex() == null) {
				strategy.process(visitor, query);
			}else{
				process(visitor, query);
			}	
			//al final de toda la pila de llamadas recursivas,
			//geometries tendrá todas las geometrias que debemos dissolver
//			geometries.add(feature.getJtsGeometry());
			if(geometry == null){
				geometry = feature.getJtsGeometry();
			}else{
				GeometryFactory factory = geometry.getFactory();
				Geometry[] geoms = new Geometry[]{geometry, feature.getJtsGeometry()};
				GeometryCollection collection = factory.createGeometryCollection(geoms);
//				geometry = EnhancedPrecisionOp.buffer(collection, 0d);
				
				try{
					geometry = EnhancedPrecisionOp.buffer(collection, 0d);
				}catch(Throwable t){
					PrecisionModel precision = new PrecisionModel(1000);
					//FIXME do a test with TopologyPreservingSimplier and compare
					SimpleGeometryPrecisionReducer reducer = new SimpleGeometryPrecisionReducer(precision);
					geometry = reducer.reduce(collection);
					geometry = EnhancedPrecisionOp.buffer(geometry, 0d);
				}
			}
		}// while
		Value[] values = new Value[1];
		values[0] = currentBufferDistance;
		return values;
	}

	void process(IndividualGeometryDissolveVisitor visitor, Rectangle2D query){
		try {
			if (visitor.start(dissolvedLayer)) {
				ReadableVectorial va = dissolvedLayer.getSource();
				ICoordTrans ct = dissolvedLayer.getCoordTrans();
				List lstRecs = dissolvedLayer.getISpatialIndex().query(query);
				Integer idRec;
				int index;
					va.start();
					DriverAttributes attr = va.getDriverAttributes();
					boolean bMustClone = false;
					if (attr != null) {
						if (attr.isLoadedInMemory()) {
							bMustClone = attr.isLoadedInMemory();
						}
					}

					for (int i = 0; i < lstRecs.size(); i++) {
						if(cancelMonitor != null){
							if(cancelMonitor.isCanceled())
								return;
						}
						idRec = (Integer) lstRecs.get(i);
						index = idRec.intValue();
						if(getDissolvedGeometries().get(index))
							continue;
						
						IGeometry geom = va.getShape(index);
						if (geom == null)// azabala
							continue;
						if (ct != null) {
							if (bMustClone)
								geom = geom.cloneGeometry();
							geom.reProject(ct);
						}
						if (geom.intersects(query))
							visitor.visit(geom, index);
					}// for
					va.stop();
			}// if visitor.start
			
			visitor.stop(dissolvedLayer);
		} catch (StartVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InitializeDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpansionFileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProcessVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	

}
