/*
 * Created on 28-feb-2006
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
 * $Id: SpatialJoinGeoprocess.java 18783 2008-02-12 12:57:15Z vcaballero $
 * $Log$
 * Revision 1.6  2007-09-19 16:08:13  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.5  2007/08/07 16:09:26  azabala
 * code cleaning (removing duplicate code)
 *
 * Revision 1.4  2007/05/15 07:24:19  cesar
 * Add the finished method for execution from Event Dispatch Thread
 *
 * Revision 1.3  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.2  2006/06/29 07:33:57  fjp
 * Cambios ISchemaManager y IFieldManager por terminar
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
 * Revision 1.10  2006/05/08 15:38:05  azabala
 * added nn spatial join with rtree
 *
 * Revision 1.9  2006/05/02 18:57:33  azabala
 * added a new implementation of nearest neighbour finder, based in RTree spatial index
 *
 * Revision 1.8  2006/05/01 19:09:23  azabala
 * Intento de optimizar el spatial join por vecino mas proximo (no funciona)
 *
 * Revision 1.7  2006/03/21 19:29:36  azabala
 * *** empty log message ***
 *
 * Revision 1.6  2006/03/17 19:53:43  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/15 18:34:31  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/14 18:32:46  fjp
 * Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
 *
 * Revision 1.3  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:59:32  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap;

import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.fmap.spatialindex.INearestNeighbourFinder;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;
import com.iver.cit.gvsig.fmap.spatialindex.RTreeJsi;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.ITwoLayersGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * This geoprocess implements Spatial Join operation. A spatial join is a join
 * where the criteria to link a feature of layer A to one (or many) features of
 * layer B is a spatial critera.<br>
 * We can do two types of spatial join:
 * <ul>
 * <li><b>Intersect spatial join (1->N).</b> Given a feature, looks for all
 * intersecting features, and apply a sumarization function to all of them. The
 * result is a feature where all numeric features of layer B have been grouped
 * by one or many sumarization functions.</li>
 * <li><b>Nearest spatial join (1->N)</b>Given a feature, looks for the nearest
 * feature of layer B, and tooks its fields.</li>
 * </ul>
 * 
 * @author azabala
 * 
 */
public class SpatialJoinGeoprocess extends AbstractGeoprocess implements
		ITwoLayersGeoprocess {
	/**
	 * overlay layer
	 */
	private FLyrVect secondLayer;
	/**
	 * Relates each numeric field of target layer with many sumarization
	 * functions
	 */
	private Map fields_sumFunctions;

	/**
	 * flag to only clip selection of input layer
	 */
	private boolean onlyFirstLayerSelection = false;

	/**
	 * flag to only clip with selection of clipping layer
	 */
	private boolean onlySecondLayerSelection = false;

	/**
	 * flag to apply a nearest spatial join (1 to 1 join) or a intersect spatial
	 * join (1 to m join)
	 */
	private boolean nearestSpatialJoin = true;

	/**
	 * Visitor that will do the process
	 */
	private SpatialJoinVisitor visitor = null;
	/**
	 * It will process results of joined features
	 */
	private FeaturePersisterProcessor2 processor;

	public SpatialJoinGeoprocess(FLyrVect inputLayer) {
		setFirstOperand(inputLayer);
	}

	public void setSecondOperand(FLyrVect overlayLayer) {
		this.secondLayer = overlayLayer;

	}

	public void setFirstOperand(FLyrVect firstLayer) {
		this.firstLayer = firstLayer;

	}

	/**
	 * PRECONDITION: We must setResultLayerProperties before to call
	 * setParameters. FIXME
	 */
	public void setParameters(Map params) throws GeoprocessException {
		Boolean firstLayerSelection = (Boolean) params
				.get("firstlayerselection");
		if (firstLayerSelection != null)
			this.onlyFirstLayerSelection = firstLayerSelection.booleanValue();

		Boolean secondLayerSelection = (Boolean) params
				.get("secondlayerselection");
		if (secondLayerSelection != null)
			this.onlySecondLayerSelection = secondLayerSelection.booleanValue();

		if (writer == null)
			throw new GeoprocessException(
					"Hay que hacer setResultLayerProperties antes de hacer setParameters en el spatial join");

		/*
		 * ¿POR QUE ESTA REPETIDO? processor = new
		 * FeaturePersisterProcessor2(writer);
		 */
		processor = new FeaturePersisterProcessor2(writer);

		Boolean nearest = (Boolean) params.get("nearest");
		if (nearest != null)
			this.nearestSpatialJoin = nearest.booleanValue();
		if (nearestSpatialJoin) {
			try {
				// ISpatialIndex spatialIndex = secondLayer.getISpatialIndex();
				// if(spatialIndex != null &&
				// (spatialIndex instanceof INearestNeighbourFinder))
				// {
				visitor = new SpatiallyIndexedSpatialJoinVisitor(
						this.firstLayer, this.secondLayer, processor);
				// }else{
				//
				// visitor = new NearestSpatialJoinVisitor(this.firstLayer,
				// this.secondLayer,
				// processor);
				// }
			} catch (ReadDriverException e) {
				throw new GeoprocessException(
						"Error preparando el procesado de las capas a enlazar");
			}

		} else {
			try {
				visitor = new IntersectSpatialJoinVisitor(this.firstLayer,
						this.secondLayer, this.fields_sumFunctions, processor);
			} catch (ReadDriverException e) {
				throw new GeoprocessException(
						"Error preparando el procesado de las capas a enlazar");
			}
		}
		visitor.setFeatureProcessor(processor);

	}

	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException(
					"Spatial Join: capa de entrada a null");
		if (secondLayer == null)
			throw new GeoprocessException("Spatial Join: 2ª capa a null");
		if (this.writer == null || this.schemaManager == null) {
			throw new GeoprocessException(
					"Operacion spatial join sin especificar capa de resultados");
		}
		try {
			int firstLayerType = firstLayer.getShapeType();
			int secondLayerType = secondLayer.getShapeType();
			if (firstLayerType == XTypes.POINT
					&& secondLayerType == XTypes.POINT && (!nearestSpatialJoin)) {
				throw new GeoprocessException(
						"No está permitido el spatial join M:N entre puntos");
			}
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error al tratar de chequear la geometria de las capas a enlazar espacialmente");
		}
	}

	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException(e);
		}
	}

	// FIXME to throw an edition exception in
	// schema manager
	public void cancel() {
		try {
			this.schemaManager.removeSchema("");
		} catch (SchemaEditionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * FIXME Lanzar una excepcion si esto se llama antes que el setParameters
	 * (esto me lo aseguro obligando a meter los datos en el constructor)
	 * 
	 * FIXME Añadir, en las relaciones 1-N, el número de features que participan
	 * en la relación de la parte de N
	 */
	public ILayerDefinition createLayerDefinition() {
		ILayerDefinition solution = null;
		try {
			solution = visitor.getResultLayerDefinition();
		} catch (GeoprocessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return solution;
	}

	public Map getFields_sumFunctions() {
		return fields_sumFunctions;
	}

	public void setFields_sumFunctions(Map fields_sumFunctions) {
		this.fields_sumFunctions = fields_sumFunctions;
	}

	public IMonitorableTask createTask() {
		try {
			return new SpatialJoinMonitorableTask();
		} catch (Exception e) {
			return null;
		}
	}

	class SpatialJoinMonitorableTask implements IMonitorableTask {
		private CancellableMonitorable cancelMonitor = null;
		String SPJOIN_MESSAGE = PluginServices.getText(this,
				"Mensaje_enlace_espacial");
		String SPJOIN_NOTE = PluginServices.getText(this,
				"Mensaje_procesando_enlace_espacial");
		String OF = PluginServices.getText(this, "De");

		private boolean finished = false;

		SpatialJoinMonitorableTask() throws ReadDriverException {
			initialize();
		}

		void initialize() throws ReadDriverException {
			cancelMonitor = createCancelMonitor();
		}

		private CancellableMonitorable createCancelMonitor()
				throws ReadDriverException {
			DefaultCancellableMonitorable monitor = new DefaultCancellableMonitorable();
			monitor.setInitialStep(0);
			monitor.setDeterminatedProcess(true);
			int numSteps = 0;
			if (onlyFirstLayerSelection) {
				FBitSet selection = firstLayer.getRecordset().getSelection();
				numSteps += (2 * selection.cardinality());
			} else {
				numSteps += 2 * firstLayer.getSource().getShapeCount();
			}
			monitor.setFinalStep(numSteps);
			return monitor;
		}

		public int getInitialStep() {
			return cancelMonitor.getInitialStep();
		}

		public int getFinishStep() {
			return cancelMonitor.getFinalStep();
		}

		public int getCurrentStep() {
			return cancelMonitor.getCurrentStep();
		}

		public String getStatusMessage() {
			return SPJOIN_MESSAGE;
		}

		public String getNote() {
			return SPJOIN_NOTE + " " + getCurrentStep() + " " + OF + " "
					+ getFinishStep();
		}

		public void cancel() {
			((DefaultCancellableMonitorable) cancelMonitor).setCanceled(true);
			SpatialJoinGeoprocess.this.cancel();
		}

		public void run() throws GeoprocessException {
			ISpatialIndex oldSptIdx = null;
			if (nearestSpatialJoin) {
				// TODO Para spatial join m-n lo estoy haciendo
				// en el GeoprocessController, mientras que para el 1-n
				// aquí: unificar criterios
				oldSptIdx = secondLayer.getISpatialIndex();
				if (oldSptIdx == null
						|| !(oldSptIdx instanceof INearestNeighbourFinder)) {
					RTreeJsi newSptIdx = new RTreeJsi();
					newSptIdx.create();
					ReadableVectorial source = secondLayer.getSource();
					try {
						int numFeatures = source.getShapeCount();
						source.start();
						for (int i = 0; i < numFeatures; i++) {
							IGeometry geometry = source.getShape(i);
							if (geometry != null)
								newSptIdx.insert(geometry.getBounds2D(), i);
						}
						source.stop();
					} catch (ReadDriverException e) {
						throw new GeoprocessException(
								"Error intentando indexar para busqueda por mas proximo");
					}
					secondLayer.setISpatialIndex(newSptIdx);
				}// if oldSptIdx
			}// if nearest

			if (visitor instanceof SpatiallyIndexedSpatialJoinVisitor) {
				// here checks for Nearest Neighbour capabilitie
				((SpatiallyIndexedSpatialJoinVisitor) visitor).initialize();
			}

			Strategy strategy = StrategyManager.getStrategy(firstLayer);
			Strategy secondLyrStrategy = StrategyManager
					.getStrategy(secondLayer);
			visitor.setCancelableStrategy(secondLyrStrategy);
			visitor.setOnlySecondLyrSelection(onlySecondLayerSelection);
			try {
				if (onlyFirstLayerSelection) {
					strategy.process(visitor, firstLayer.getRecordset()
							.getSelection(), cancelMonitor);

				} else {
					strategy.process(visitor, cancelMonitor);
				}

				// If we changed spatial index to allow Nearest Neighbour
				// queries,
				// recover the old spatial index
				if (oldSptIdx != null)
					secondLayer.setISpatialIndex(oldSptIdx);

			} catch (ReadDriverException e) {
				throw new GeoprocessException(
						"Error al acceder a los datos durante un spatial join");
			} catch (ProcessVisitorException e) {
				throw new GeoprocessException(
						"Error al procesar los datos durante un spatial join");
			} catch (VisitorException e) {
				throw new GeoprocessException(
						"Error al procesar los datos durante un spatial join");
			} finally {
				finished = true;
			}
		}

		public boolean isDefined() {
			return cancelMonitor.isDeterminatedProcess();
		}

		public boolean isCanceled() {
			return cancelMonitor.isCanceled();
		}

		public boolean isFinished() {
			return finished;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.iver.utiles.swing.threads.IMonitorableTask#finished()
		 */
		public void finished() {
			// TODO Auto-generated method stub

		}
	}

}
