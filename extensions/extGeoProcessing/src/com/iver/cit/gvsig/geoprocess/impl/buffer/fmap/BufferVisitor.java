/*
 * Created on 05-feb-2006
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
* $Id: BufferVisitor.java 32916 2010-07-01 09:12:40Z vcaballero $
* $Log$
* Revision 1.5  2007-08-07 15:09:22  azabala
* changes to remove UnitUtils' andami dependencies
*
* Revision 1.4  2007/03/06 16:47:58  caballero
* Exceptions
*
* Revision 1.3  2006/07/26 18:06:27  azabala
* fixed bug when internal buffers collapses geometries
*
* Revision 1.2  2006/07/21 11:04:50  azabala
* Unified createTask and process() methods
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
* Revision 1.7  2006/05/01 19:18:09  azabala
* revisión general del buffer (añadidos anillos concentricos, buffers interiores y exteriores, etc)
*
* Revision 1.6  2006/04/07 19:00:33  azabala
* *** empty log message ***
*
* Revision 1.5  2006/03/28 16:27:25  azabala
* *** empty log message ***
*
* Revision 1.4  2006/03/07 21:01:33  azabala
* *** empty log message ***
*
* Revision 1.3  2006/03/05 19:56:27  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/17 16:33:46  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/09 16:00:36  azabala
* First version in CVS
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.buffer.fmap;

import java.util.ArrayList;
import java.util.Stack;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessingResultsProcessor;
import com.iver.cit.gvsig.geoprocess.core.util.JTSFacade;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * Abstract base class to those all FeatureVisitor that computes
 * geometry buffers.
 *
 *
 * @author azabala
 *
 */
public abstract class BufferVisitor implements FeatureVisitor {
//INTERNAL, EXTERNAL OR BOTH FLAGS
	/**
	 * For polygonal buffers, only compute interior buffers
	 */
	public static final byte BUFFER_INSIDE_POLY = 0;

	/**
	 * For polygonal buffers, only compute exterior buffers
	 * (is the default operation, it applies to any geometry type)
	 */
	public static final byte BUFFER_OUTSIDE_POLY = 1;

	/**
	 * For polygonal buffers, compute interior and exterior buffers
	 */
	public static final byte BUFFER_INSIDE_OUTSIDE_POLY = 2;

//SQUARE OR ROUND CAP FLAGS

	/**
	 * Buffer with square cap
	 */
	public static final byte CAP_SQUARE = 0;
	/**
	 * Buffer with round cap
	 */
	public static final byte CAP_ROUND = 1;


	/**
	 * type of buffer to compute (polygons could have the three types,
	 * point and lines only outside buffer.
	 */
	private byte typeOfBuffer = BUFFER_OUTSIDE_POLY;
	/**
	 * To use or not round caps.
	 */
	private byte capBuffer = CAP_ROUND;

	/**
	 * Number of radial rings buffers
	 */
	private int numberOfRadialBuffers = 1;

	/**
	 * It process individual JTS buffer results
	 */
	protected GeoprocessingResultsProcessor resultsProcessor;

	/**
	 * Counter of buffers generated
	 */
	protected int numProcessed = 0;

	/**
	 * Flag to decide if simplify or not the input
	 * geometries. Actually always is true, but
	 * it could change
	 */
	private boolean simplifyGeometry = true;

	private boolean dissolveBuffers;

	protected SelectableDataSource recordset;


	public void setSimplifyGeometry(boolean simplify){
		this.simplifyGeometry = simplify;
	}
	
	public boolean isSimplifyGeometry(){
		return this.simplifyGeometry;
	}
	
	/**
	 * Sets type of buffer flag
	 * @param typeOfBuffer
	 */
	public void setTypeOfBuffer(byte typeOfBuffer){
		this.typeOfBuffer = typeOfBuffer;
	}

	/**
	 * Sets type of cap flag
	 * @param typeOfCap
	 */
	public void setTypeOfCap(byte typeOfCap){
		capBuffer = typeOfCap;
	}

	/**
	 * Sets number of radial rings buffers
	 * @param number
	 */
	public void setNumberOfRadialBuffers(int number){
		numberOfRadialBuffers = number;
	}

	/**
	 * Return number of radial buffers.
	 * @return
	 */
	public int getNumberOfRadialBuffers(){
		return numberOfRadialBuffers;
	}


	/**
	 * Computes a buffer distance for a feature of the buffer layer
	 * @param g
	 * @param index
	 * @return
	 */
	public abstract double getBufferDistance(IGeometry g, int index);

	/**
	 * Generates a buffer geometry for the visited IGeometry
	 */
	public void visit(IGeometry g, int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
		Geometry jtsGeo = g.toJTSGeometry();
		double bufferDistance = getBufferDistance(g, index);
		computeBuffer(jtsGeo, bufferDistance, index);
	}

	/**
	 * Verifys if a geometry buffer is null.
	 * It is useful when you're working with internal buffers. If the internal
	 * buffer distance is greater than the geometry radius, the buffer result
	 * will be null.
	 *
	 * @param newGeometry
	 * @return
	 */
	public boolean verifyNilGeometry(Geometry newGeometry){
		if(newGeometry instanceof GeometryCollection){
			if(((GeometryCollection)newGeometry).getNumGeometries() == 0){
				//we have collapsed initial geometry
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * Compute a buffer (in function of the params) of
	 * the original geometry
	 *
	 * @param originalGeometry
	 * @param bufferDistance
	 * @return
	 * @throws VisitorException
	 * @throws ProcessWriterException
	 *
	 */
	public void computeBuffer(Geometry originalGeometry, double bufferDistance, int index) throws VisitorException{
		Geometry solution = null;
		Geometry inputParam = originalGeometry;
		/*
		 * When we try to apply large buffer distances, we could get OutOfMemoryError
		 * exceptions. Explanation in
		 * http://lists.jump-project.org/pipermail/jts-devel/2005-February/000991.html
		 * http://lists.jump-project.org/pipermail/jts-devel/2005-September/001292.html
		 * This problems hasnt been resolved in JTS 1.7.
		 */
		if(originalGeometry.getDimension() != 0 && simplifyGeometry){
			//ver si 1/10 de la distancia de buffer
			//es un % de simplificación adecuado
			inputParam =
				TopologyPreservingSimplifier.
					simplify(originalGeometry,
							bufferDistance / 10d);
		}
		int cap = BufferOp.CAP_ROUND;
		if(capBuffer == CAP_SQUARE){
			cap = BufferOp.CAP_SQUARE;
		}

		//this two references are necessary to compute radial rings
		Geometry previousExteriorRing = null;
		Geometry previousInteriorRing = null;
		
		if(typeOfBuffer == BUFFER_INSIDE_POLY){
				//if we have radial internal buffers, we start by
				//most interior buffer
				for(int i = numberOfRadialBuffers; i >= 1; i--){
					double distRing = i * bufferDistance;
					BufferOp bufOp = new BufferOp(inputParam);
					bufOp.setEndCapStyle(cap);
					Geometry newGeometry = bufOp.getResultGeometry(-1 * distRing);
					if(verifyNilGeometry(newGeometry)){
						//we have collapsed original geometry
						return;
					}
					if(previousInteriorRing != null){
//						solution = newGeometry.difference(previousInteriorRing);
						solution = JTSFacade.difference(newGeometry, previousInteriorRing);
					}else{
						solution = newGeometry;
					}
					numProcessed++;
					IFeature feature=null;
					try {
						feature = createFeature(solution, distRing, index);
					} catch (ReadDriverException e) {
						e.printStackTrace();
					}
					resultsProcessor.processFeature(feature);
					previousInteriorRing = newGeometry;
				}
		}else if(typeOfBuffer == BUFFER_OUTSIDE_POLY){
			for(int i = 1; i <= numberOfRadialBuffers; i++){
				double distRing = i * bufferDistance;
				BufferOp bufOp = new BufferOp(inputParam);
				bufOp.setEndCapStyle(cap);
				Geometry newGeometry = bufOp.getResultGeometry(distRing);
				if(previousExteriorRing != null){
//					solution = newGeometry.difference(previousExteriorRing);
					solution = JTSFacade.difference(newGeometry, previousExteriorRing);
				}else{
					solution = newGeometry;
				}
				numProcessed++;
				IFeature feature=null;
				try {
					feature = createFeature(solution, distRing, index);
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
				resultsProcessor.processFeature(feature);
				previousExteriorRing = newGeometry;
			}
		}else if(typeOfBuffer == BUFFER_INSIDE_OUTSIDE_POLY){
			GeometryFactory geomFact = new GeometryFactory();
			for(int i = 1; i <= numberOfRadialBuffers; i++){
				double distRing = i * bufferDistance;
				BufferOp bufOp = new BufferOp(inputParam);
				bufOp.setEndCapStyle(cap);
				Geometry out = bufOp.getResultGeometry(distRing);
				Geometry in = bufOp.getResultGeometry(-1 * distRing);
				boolean collapsedInterior = verifyNilGeometry(in);
				if(previousExteriorRing == null || previousInteriorRing == null){
					if(collapsedInterior)
						solution = out;
					else{
//						solution = out.difference(in);
						solution = JTSFacade.difference(out, in);
					}
				}else{
					if(collapsedInterior){
//						solution = out.difference(previousExteriorRing);
						solution = JTSFacade.difference(out, previousExteriorRing);
					}else{
//						Geometry outRing = out.difference(previousExteriorRing);
//						Geometry inRing = previousInteriorRing.difference(in);
						Geometry outRing = JTSFacade.difference(out, previousExteriorRing);
						Geometry inRing = JTSFacade.difference(previousInteriorRing, in);
						
						Geometry[] geomArray = new Geometry[]{outRing, inRing};
						solution = geomFact.createGeometryCollection(geomArray);
						//FMap doesnt work with GeometryCollection, so we try
						//to pass to a MultiPolygon.
						ArrayList polygons = new ArrayList();
						Stack stack = new Stack();
						stack.push(solution);
						while(stack.size() != 0){
							GeometryCollection geCol =
								(GeometryCollection) stack.pop();
							for(int j = 0; j < geCol.getNumGeometries(); j++){
								Geometry geometry = geCol.getGeometryN(j);
								if(geometry instanceof GeometryCollection)
									stack.push(geometry);
								if(geometry instanceof Polygon)
									polygons.add(geometry);
							}//for
						}//while
						Polygon[] pols = new Polygon[polygons.size()];
						polygons.toArray(pols);
						MultiPolygon newSolution = geomFact.createMultiPolygon(pols);
						solution = newSolution;
					}
				}//else
				numProcessed++;
				IFeature feature=null;
				try {
					feature = createFeature(solution, -1 * distRing, distRing, index);
				} catch (ReadDriverException e) {
					e.printStackTrace();
				}
				resultsProcessor.processFeature(feature);
				previousExteriorRing = out;
				if(!collapsedInterior)
					previousInteriorRing = in;
			}//for
		}//else

	}



	/**
	 * Creates a feature for a simple buffer process for polygons
	 * (the result feature will have a field with the buffer distance)
	 * @param jtsGeo
	 * @param distance
	 * @return
	 * @throws ReadDriverException 
	 */
	protected IFeature createFeature(Geometry jtsGeo, double distance, int index) throws ReadDriverException{
		IGeometry iGeo = FConverter.jts_to_igeometry(jtsGeo);
		Value[] values = null;
		if (!dissolveBuffers){
			int fieldCount = recordset.getFieldCount();
			values=new Value[fieldCount];
			for (int i = 0; i < fieldCount; i++) {
				values[i]=recordset.getFieldValue(index, i);
			}
		}else{
			values = new Value[2];
			values[0] = ValueFactory.createValue(numProcessed);
			values[1] = ValueFactory.createValue(distance);
		}
		return FeatureFactory.createFeature(values, iGeo);
	}

	/**
	 * Creates a feature for a external and internal buffer process for polygons
	 * (the result feature will have a field with the external buffer distance,
	 * and a field with the internal buffer distance)
	 * @param jtsGeo
	 * @param distance
	 * @return
	 * @throws ReadDriverException 
	 */
	protected IFeature createFeature(Geometry jtsGeo, double distanceFrom, double distanceTo, int index) throws ReadDriverException{
		IGeometry iGeo = FConverter.jts_to_igeometry(jtsGeo);
		Value[] values = null;
		if (!dissolveBuffers){
			int fieldCount = recordset.getFieldCount();
			values=new Value[fieldCount];
			for (int i = 0; i < fieldCount; i++) {
				values[i]=recordset.getFieldValue(index, i);
			}
		}else{
			values = new Value[3];
			values[0] = ValueFactory.createValue(numProcessed);
			values[1] = ValueFactory.createValue(distanceFrom);
			values[2] = ValueFactory.createValue(distanceTo);
		}
		
		return FeatureFactory.createFeature(values, iGeo);
	}


	/**
	 * Sets BufferResultProcessor, class that has the responsability
	 * of process individual JTS buffered geometries (save it,
	 * to cache it, etc)
	 * @param processor
	 */
	public void setBufferProcessor(GeoprocessingResultsProcessor processor) {
		this.resultsProcessor = processor;

	}

	/**
	 * Returns buffer processor
	 * @return
	 */
	public GeoprocessingResultsProcessor getBufferProcessor() {
		return resultsProcessor;
	}

	/**
	 * Returns type of buffer flag (internal, external, both)
	 * @return
	 */
	public byte getTypeOfBuffer() {
		return typeOfBuffer;
	}

	public void setIsDissolve(boolean dissolveBuffers) {
		this.dissolveBuffers=dissolveBuffers;
	}
	public boolean start(FLayer layer) throws StartVisitorException {
		if(layer instanceof VectorialData && layer instanceof AlphanumericData){
			try {
				this.recordset = ((AlphanumericData)layer).getRecordset();
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}
		return false;
	}

}

