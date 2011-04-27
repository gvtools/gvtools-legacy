/*
 * Created on 21-feb-2006
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
 * $Id: IntersectVisitor.java 30063 2009-07-21 11:52:25Z vcaballero $
 * $Log$
 * Revision 1.4  2007-09-19 16:07:28  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.3  2007/08/07 16:07:06  azabala
 * centrilizing JTS in JTSFacade and allowing all geometry types (not only Polygon)
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.3  2006/06/08 18:25:20  azabala
 * modificaciones para admitir capas de shapeType MULTI
 *
 * Revision 1.2  2006/06/02 18:21:28  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/05/24 21:10:40  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.6  2006/05/01 19:12:17  azabala
 * optimizaciones haciendo uso de strategy.process(rectangle) (el bitset se estaba recorriendo secuencial, no aleatorio)
 *
 * Revision 1.5  2006/03/26 20:03:06  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/23 21:04:36  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/21 19:29:18  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/05 19:58:47  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/26 20:54:38  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.intersection.fmap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.cit.gvsig.geoprocess.core.util.JTSFacade;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Computes intersections of features of a layer with features of an overlay
 * layer.
 *
 * @author azabala
 *
 * FIXME Crear una clase abstracta: OverlayVisitor, pues todos los visitors de
 * un overlay son muy parecidos.
 *
 */
public class IntersectVisitor implements FeatureVisitor {

	/**
	 * Allows to get attributes of first layer features which is being
	 * intersected
	 */
	SelectableDataSource firstRs;

	/**
	 * Number of fields of first layer recordset
	 */
	int numFieldsA;

	/**
	 * looks for overlay features of the processed feauture by spatial criteria
	 * (queryByEnvelope)
	 */
	FLyrVect overlayLayer;

	/**
	 * Strategy to process overlay layer
	 */
	Strategy strategy;

	/**
	 * flag to decide if process onlye features selected in overlay layer.
	 */
	boolean onlyOverlaySelection;

	/**
	 * Gets attributes of second layer features which are being intersected.
	 */
	SelectableDataSource secondRs;

	/**
	 * Number of fields of second layer recordset
	 */
	int numFieldsB;

	/**
	 * Schema of the result layer
	 */
	ILayerDefinition layerDefinition;

	/**
	 * It processes features resulting of intersetions. It could saves them in
	 * persistent datastore, caching them, reprocess them, reprojects them, etc.
	 */
	FeatureProcessor featureProcessor;

	/**
	 * Constructor
	 * @param overlayLayer
	 * @param processor
	 * @param strategy
	 * @param onlyOverlaySelection
	 * @throws ReadDriverException
	 * @throws DriverException
	 * @throws com.iver.cit.gvsig.fmap.DriverException
	 */
	public IntersectVisitor(FLyrVect overlayLayer, FeatureProcessor processor,
			Strategy strategy, boolean onlyOverlaySelection) throws ReadDriverException{
		this.overlayLayer = overlayLayer;
		this.featureProcessor = processor;
		this.strategy = strategy;
		this.onlyOverlaySelection = onlyOverlaySelection;
		secondRs = overlayLayer.getRecordset();
		numFieldsB = secondRs.getFieldCount();
	}

	public void visit(IGeometry g, final int index) throws VisitorException, ProcessVisitorException {
		if(g == null)
			return;
		
//		if(g.getGeometryType() != XTypes.POLYGON &&
//				g.getGeometryType() != XTypes.MULTI)
//			return;
		
		final Geometry firstJts = g.toJTSGeometry();
		final boolean onlyOverlay = onlyOverlaySelection;
		try {
			strategy.process(new FeatureVisitor(){
				public void visit(IGeometry g, int indexOverlay) throws VisitorException, ProcessVisitorException {
					if(onlyOverlay){
						try {
							if(!overlayLayer.getRecordset().getSelection().get(indexOverlay))
								return;
						} catch (ReadDriverException e) {
							throw new ProcessVisitorException(overlayLayer.getName(),e,
							"Error en interseccion: verificando si un posible overlay esta seleccionado");
						}//geometry g is not selected
					}
					
					
//					if(g.getGeometryType() != XTypes.POLYGON &&
//							g.getGeometryType() != XTypes.MULTI)
//						return;
					
					
					Geometry overlayJts = g.toJTSGeometry();
					if (firstJts.intersects(overlayJts)) {
						
//						Geometry newGeoJts = EnhancedPrecisionOp.intersection(firstJts, overlayJts);
//						if (!(newGeoJts instanceof Polygon)
//								&& !(newGeoJts instanceof MultiPolygon)) {
//							// intersection of adjacent polygons is a linestring
//							// but we are not interested in it
//							return;
//						}
						Geometry newGeoJts = JTSFacade.intersection(firstJts, overlayJts);
						if(JTSFacade.checkNull(newGeoJts))
							return;
						
						IFeature intersectionFeature;
						try {
							intersectionFeature = createFeature(newGeoJts,
									index, indexOverlay);
						} catch (ReadDriverException e) {
							throw new ProcessVisitorException(overlayLayer.getName(),e,
									"Error al crear el feature resultante de la interseccion");
						}
						featureProcessor.processFeature(intersectionFeature);
					}// if intersects
				}

				public String getProcessDescription() {
					return "Computing intersections of a polygon with its adjacents";
				}
				public void stop(FLayer layer) throws VisitorException {
				}
				public boolean start(FLayer layer) throws StartVisitorException {
					return true;
				}},g.getBounds2D());

		} catch (ReadDriverException e) {
			throw new ProcessVisitorException(overlayLayer.getName(),e,
					"Error buscando los overlays que intersectan con un feature");
		} 
	}

	public void stop(FLayer layer) throws VisitorException {
		featureProcessor.finish();
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			try {
				this.firstRs = ((AlphanumericData) layer).getRecordset();
				numFieldsA = firstRs.getFieldCount();
				this.featureProcessor.start();
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}
		return false;
	}


	/**
	 * TODO Meter este metodo en FeatureFactory, porque lo estoy copypasteando
	 * en todos los Visitor
	 *
	 * FIXME El esquema es resultado de la mezcla de los esquemas de dos capas
	 * ¿Como saber qué campo de ILayerDefinition se toma de firstRs y cual de secondRs?
	 * Mirando DefinitionUtils, vemos que primero se recorre firstLayer,
	 * y luego secondLayer. REVISAR
	 */
	private IFeature createFeature(Geometry jtsGeometry, int firstLayerIndex,
			int overlayLayerIndex) throws ReadDriverException {
		IFeature solution = null;
		IGeometry intersectGeometry = FConverter.jts_to_igeometry(jtsGeometry);


		Value[] featureAttr = new Value[numFieldsA + numFieldsB];
		for (int indexField = 0; indexField < numFieldsA; indexField++) {
			featureAttr[indexField] = firstRs.getFieldValue(firstLayerIndex,
					indexField);
		}
		for (int indexFieldB = 0; indexFieldB < numFieldsB; indexFieldB++) {
			featureAttr[numFieldsA + indexFieldB] = secondRs.getFieldValue(
					overlayLayerIndex, indexFieldB);
		}
		solution = FeatureFactory.createFeature(featureAttr, intersectGeometry);
		return solution;

	}

	public void setFeatureProcessor(FeatureProcessor featureProcessor) {
		this.featureProcessor = featureProcessor;
	}

	public String getProcessDescription() {
		return "Computing intersections between two layers";
	}

	public ILayerDefinition getLayerDefinition() {
		return layerDefinition;
	}

	public void setLayerDefinition(ILayerDefinition layerDefinition) {
		this.layerDefinition = layerDefinition;
	}

}
