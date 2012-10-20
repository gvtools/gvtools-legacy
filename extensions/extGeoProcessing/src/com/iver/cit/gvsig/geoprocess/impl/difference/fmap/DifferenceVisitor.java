/*
 * Created on 22-feb-2006
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
 * $Id: DifferenceVisitor.java 13874 2007-09-19 16:12:18Z jaume $
 * $Log$
 * Revision 1.5  2007-09-19 16:05:53  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.4  2007/08/07 15:42:19  azabala
 * centrilizing JTS in JTSFacade
 *
 * Revision 1.3  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.2  2006/12/04 19:44:25  azabala
 * comments removed
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.3  2006/06/08 18:24:23  azabala
 * modificaciones para admitir capas de shapeType MULTI
 *
 * Revision 1.2  2006/06/02 18:21:28  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/05/24 21:11:38  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.4  2006/05/01 19:15:18  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/26 20:02:25  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/05 19:58:10  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/26 20:53:28  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.difference.fmap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
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
import com.vividsolutions.jts.precision.EnhancedPrecisionOp;

public class DifferenceVisitor implements FeatureVisitor {

	/**
	 * Allows to get attributes of first layer features which is being
	 * differenced
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
	 * Strategy to process overlaylayer, allowing cancelations
	 */
	Strategy strategy;

	/**
	 * this flag marks if visitor only must process overlay layer selection
	 */
	boolean onlyOverlayLayerSelected;

	/**
	 * It processes features resulting of intersetions. It could saves them in
	 * persistent datastore, caching them, reprocess them, reprojects them, etc.
	 */
	FeatureProcessor featureProcessor;

	/**
	 * Schema of result layer
	 */
	ILayerDefinition layerDefinition;

	/**
	 * Constructor
	 * 
	 * @param overlayLayer
	 * @param processor
	 * @throws DriverException
	 */
	public DifferenceVisitor(FLyrVect overlayLayer, FeatureProcessor processor,
			Strategy strategy, boolean onlySelection) {
		this.overlayLayer = overlayLayer;
		this.featureProcessor = processor;
		this.strategy = strategy;
		this.onlyOverlayLayerSelected = onlySelection;
	}

	/**
	 * Inner class to process with a given strategy all geometries of overlay
	 * layer that overlays with a given geometry of input layer.
	 * 
	 * @author azabala
	 * 
	 */

	/*
	 * TODO Dado un feature, es factible pensar que los de la otra capa que lo
	 * cubran pueden estar en memoria (aunque esto no sea siempre así). Podemos
	 * hacer un EnhancedMemoryOverlay, que primero lo haga todo en memoria,
	 * capture un OutOfMemoryException y lo haga de modo incremental
	 */
	class UnionOverlaysVisitor implements FeatureVisitor {
		/**
		 * Result of the strategy process (union of overlays of a IGeometry)
		 */
		Geometry overlayGeometry;

		/**
		 * Flag to process or not selections of overlay layer
		 */
		boolean overlayLayerSelected = true;

		Geometry getUnionOfOverlays() {
			return overlayGeometry;
		}

		public void visit(IGeometry g, int index) throws VisitorException,
				ProcessVisitorException {
			if (g == null)
				return;

			/*
			 * TODO Cuando hagamos uso de los iteradores en geoprocessing, meter
			 * un readableVectorial.getFeatureIterator(IFeatureFilter), que
			 * pueda englobar varias opciones de filtrado. Así, por ejemplo,
			 * puedo tener en cuenta las SELECCIONES.
			 */
			if (overlayLayerSelected) {
				try {
					if (!overlayLayer.getRecordset().getSelection().get(index))
						return;
				} catch (ReadDriverException e) {
					throw new ProcessVisitorException(overlayLayer.getName(),
							e,
							"Error en diferencia: verificando si un posible overlay esta seleccionado");
				}// geometry g is not selected
			}

			/*
			 * Prueba para hacer diferencia entre cualquier tipo de geometria
			 * if(g.getGeometryType() != XTypes.POLYGON && g.getGeometryType()
			 * != XTypes.MULTI) return;
			 */

			Geometry actualGeometry = g.toJTSGeometry();
			if (overlayGeometry == null) {
				overlayGeometry = actualGeometry;
			} else {
				overlayGeometry = JTSFacade.union(actualGeometry,
						overlayGeometry);
				// overlayGeometry = actualGeometry.union(overlayGeometry);
			}// if

		}// visit

		public String getProcessDescription() {
			return "";
		}

		public void stop(FLayer layer) throws VisitorException {
		}

		public boolean start(FLayer layer) throws StartVisitorException {
			return true;
		}
	}// UnionOverlaysVisitor

	public void visit(IGeometry g, final int index) throws VisitorException,
			ProcessVisitorException {
		if (g == null)
			return;

		/*
		 * if(g.getGeometryType() != XTypes.POLYGON && g.getGeometryType() !=
		 * XTypes.MULTI) return;
		 */
		Geometry firstJts = g.toJTSGeometry();
		Geometry solution = null;
		try {
			UnionOverlaysVisitor unionVisitor = new UnionOverlaysVisitor();
			unionVisitor.overlayLayerSelected = onlyOverlayLayerSelected;
			strategy.process(unionVisitor, g.getBounds2D());

			// now we compute difference of firstJts and overlaysUnion
			Geometry overlays = unionVisitor.getUnionOfOverlays();
			if (overlays != null) {
				solution = EnhancedPrecisionOp.difference(firstJts, overlays);
			} else {
				solution = firstJts;
			}

			/*
			 * TODO Que pasa si la diferencia entre dos lineas es un punto, o
			 * una linea??? Saldrían geometrias mezcladas
			 * 
			 * if (!(solution instanceof Polygon)) { if (!(solution instanceof
			 * MultiPolygon)) { // intersection of adjacent polygons is a
			 * linestring // but we are not interested in it return; } }
			 */
			if (!JTSFacade.checkNull(solution)) {
				featureProcessor.processFeature(createFeature(solution, index));
			}
		} catch (ReadDriverException e) {
			throw new ProcessVisitorException(overlayLayer.getName(), e,
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
	 * 
	 * @param jtsGeometry
	 * @param firstLayerIndex
	 * @return
	 * @throws DriverException
	 * 
	 *             FIXME Revisar. Para el caso de la Union, hay que hacer dos
	 *             pasadas: diferencia A-B y diferencia B-A. Pero los features
	 *             tienen que tener el esquema de la intersección. ¿Como saber
	 *             el lugar que ocupa cada atributo en el esquema
	 *             ILayerDefinition, en el caso de que dos capas tuviesen
	 *             atributos con el mismo nombre?) De momento, se buscará en
	 *             ILayerDefinition la posicion que ocupa un campo a partir de
	 *             su nombre. Esto obliga a que 2 campos no tomen el mismo
	 *             nombre
	 * 
	 *             POSIBLE SOLUCION: ANTEPONER EL NOMBRE DE LA LAYER AL NOMBRE
	 *             DEL CAMPO
	 */
	private IFeature createFeature(Geometry jtsGeometry, int firstLayerIndex)
			throws ReadDriverException {
		IFeature solution = null;
		IGeometry diffGeometry = FConverter.jts_to_igeometry(jtsGeometry);
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		Value[] featureAttr = new Value[fields.length];
		for (int indexField = 0; indexField < numFieldsA; indexField++) {
			// for each field of firstRs
			String fieldName = firstRs.getFieldName(indexField);
			for (int j = 0; j < fields.length; j++) {
				if (fieldName.equalsIgnoreCase(fields[j].getFieldName())) {
					featureAttr[j] = firstRs.getFieldValue(firstLayerIndex,
							indexField);
					break;
				}// if
			}// for
		}// for
			// now we put null values
		for (int i = 0; i < featureAttr.length; i++) {
			if (featureAttr[i] == null)
				featureAttr[i] = ValueFactory.createNullValue();
		}
		solution = FeatureFactory.createFeature(featureAttr, diffGeometry);
		return solution;
	}

	public void setFeatureProcessor(FeatureProcessor featureProcessor) {
		this.featureProcessor = featureProcessor;
	}

	public String getProcessDescription() {
		return "Computing differences between two layers";
	}

	public ILayerDefinition getLayerDefinition() {
		return layerDefinition;
	}

	public void setLayerDefinition(ILayerDefinition layerDefinition) {
		this.layerDefinition = layerDefinition;
	}

}
