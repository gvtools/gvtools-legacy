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
 * $Id: IntersectionGeoprocess.java 30063 2009-07-21 11:52:25Z vcaballero $
 * $Log$
 * Revision 1.6  2007-09-19 16:07:28  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.5  2007/08/07 16:07:06  azabala
 * centrilizing JTS in JTSFacade and allowing all geometry types (not only Polygon)
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
 * Revision 1.2  2006/06/08 18:25:20  azabala
 * modificaciones para admitir capas de shapeType MULTI
 *
 * Revision 1.1  2006/05/24 21:10:40  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.9  2006/05/01 19:14:06  azabala
 * la cancelacion no solo para el ITask que ejecuta el geoproceso, además llama al metodo cancel() del mismo (que se supone que debería hacer un drop() con los resultados del geoproceso inconcluso)
 *
 * Revision 1.8  2006/03/23 21:04:36  azabala
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/21 19:29:18  azabala
 * *** empty log message ***
 *
 * Revision 1.6  2006/03/17 19:53:22  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/15 18:33:36  azabala
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
 * Revision 1.1  2006/02/26 20:54:25  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.intersection.fmap;

import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IOverlayGeoprocess;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * Computes intersection between two layers.
 * 
 * @author azabala
 * 
 */
public class IntersectionGeoprocess extends AbstractGeoprocess implements
		IOverlayGeoprocess {

	/**
	 * overlay layer
	 */
	private FLyrVect overlayLayer;

	/**
	 * Schema of the result layer
	 */
	private ILayerDefinition resultLayerDefinition;

	/**
	 * flag to only clip selection of input layer
	 */
	private boolean onlyFirstLayerSelection = false;

	/**
	 * flag to only clip with selection of clipping layer
	 */
	private boolean onlyClipLayerSelection = false;

	public IntersectionGeoprocess(FLyrVect inputLayer) {
		setFirstOperand(inputLayer);
	}

	public void setSecondOperand(FLyrVect overlayLayer) {
		this.overlayLayer = overlayLayer;

	}

	public void setFirstOperand(FLyrVect firstLayer) {
		this.firstLayer = firstLayer;
	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean firstLayerSelection = (Boolean) params
				.get("firstlayerselection");
		if (firstLayerSelection != null)
			this.onlyFirstLayerSelection = firstLayerSelection.booleanValue();

		Boolean secondLayerSelection = (Boolean) params
				.get("secondlayerselection");
		if (secondLayerSelection != null)
			this.onlyClipLayerSelection = secondLayerSelection.booleanValue();

	}

	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException(
					"Interseccion: capa de entrada a null");
		if (overlayLayer == null)
			throw new GeoprocessException("Interseccion: capa de union a null");
		if (this.writer == null || this.schemaManager == null) {
			throw new GeoprocessException(
					"Operacion de interseccion sin especificar capa de resultados");
		}
		/*
		 * azabala: interseccion con cualquier tipo de capa try { if
		 * ((firstLayer.getShapeType() != XTypes.POLYGON) &&
		 * (firstLayer.getShapeType() != XTypes.MULTI)) { throw new
		 * GeoprocessException(
		 * "Primera capa de interseccion no es de polígonos"); } if
		 * ((overlayLayer.getShapeType() != XTypes.POLYGON) &&
		 * (overlayLayer.getShapeType() != XTypes.MULTI)) { throw new
		 * GeoprocessException(
		 * "Segunda capa de interseccion no es de polígonos"); } } catch
		 * (ReadDriverException e) { throw new GeoprocessException(
		 * "Error al tratar de chequear si las capas a intersectar son de polígonos"
		 * ); }
		 */

	}

	public void process() throws GeoprocessException {
		try {
			new IntersectionMonitorableTask().run();
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error de acceso a driver durante geoproceso interseccion");
		}
	}

	public void cancel() {
		try {
			schemaManager.removeSchema("");
		} catch (SchemaEditionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			try {
				if (onlyClipLayerSelection)
					if (onlyFirstLayerSelection)
						resultLayerDefinition = DefinitionUtils
								.mergeLayerDefinitions(firstLayer, overlayLayer);
					else
						resultLayerDefinition = DefinitionUtils
								.mergeLayerDefinitions(overlayLayer, firstLayer);
				else
					resultLayerDefinition = DefinitionUtils
							.mergeLayerDefinitions(firstLayer, overlayLayer);
				resultLayerDefinition.setShapeType(FShape.MULTI);
			} catch (Exception e) {
				// TODO Quizas createLayerDefinition deberia lanzar
				// una excepcion
				e.printStackTrace();
			}
		}
		return resultLayerDefinition;
	}

	public IMonitorableTask createTask() {
		try {
			return new IntersectionMonitorableTask();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * IMonitorableTask that allows to run intersection geoprocess in
	 * background, with cancelation requests.
	 * 
	 * @author azabala
	 * 
	 */
	class IntersectionMonitorableTask implements IMonitorableTask {
		private CancellableMonitorable cancelMonitor = null;
		String INTERSECTION_MESSAGE = PluginServices.getText(this,
				"Mensaje_interseccion");
		String INTERSECTION_NOTE = PluginServices.getText(this,
				"Mensaje_procesando_interseccion");
		String OF = PluginServices.getText(this, "De");
		private boolean finished = false;

		IntersectionMonitorableTask() throws ReadDriverException {
			initialize();
		}

		void initialize() throws ReadDriverException {
			cancelMonitor = createCancelMonitor();
		}

		private CancellableMonitorable createCancelMonitor()
				throws ReadDriverException {
			DefaultCancellableMonitorable monitor = new DefaultCancellableMonitorable();
			monitor.setInitialStep(0);
			// Really its undeterminated, but we must to process all
			// elements of first layer (or selection) we are going to
			// consideer determinated
			monitor.setDeterminatedProcess(true);
			int numSteps = 0;
			if (onlyFirstLayerSelection) {
				FBitSet selection = firstLayer.getRecordset().getSelection();
				numSteps = selection.cardinality();
			} else {
				numSteps = firstLayer.getSource().getShapeCount();
			}
			if (onlyClipLayerSelection) {
				FBitSet selection = overlayLayer.getRecordset().getSelection();
				numSteps = selection.cardinality();
			} else {
				numSteps = firstLayer.getSource().getShapeCount();
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
			return INTERSECTION_MESSAGE;
		}

		public String getNote() {
			return INTERSECTION_NOTE + " " + getCurrentStep() + " " + OF + " "
					+ getFinishStep();
		}

		public void cancel() {
			((DefaultCancellableMonitorable) cancelMonitor).setCanceled(true);
			// This does this geoprocess athomic. In this
			// call we remove result files
			IntersectionGeoprocess.this.cancel();
		}

		public void run() throws GeoprocessException {

			try {
				// Prepare the result
				if (!(writer instanceof MultiShpWriter)) {
					schemaManager.createSchema(createLayerDefinition());
				}
				writer.preProcess();
				Strategy strategy = StrategyManager.getStrategy(firstLayer);
				Strategy overlayStrategy = StrategyManager
						.getStrategy(overlayLayer);
				FeaturePersisterProcessor2 featureProcessor = new FeaturePersisterProcessor2(
						writer);
				IntersectVisitor visitor = new IntersectVisitor(overlayLayer,
						featureProcessor, overlayStrategy,
						onlyClipLayerSelection);
				if (onlyFirstLayerSelection) {
					strategy.process(visitor, firstLayer.getRecordset()
							.getSelection(), cancelMonitor);
				} else if (onlyClipLayerSelection) {
					IntersectVisitor visitor2 = new IntersectVisitor(
							firstLayer, featureProcessor, strategy,
							onlyFirstLayerSelection);
					overlayStrategy.process(visitor2, overlayLayer
							.getRecordset().getSelection(), cancelMonitor);
				} else {
					strategy.process(visitor, cancelMonitor);
				}

			} catch (ReadDriverException e) {
				throw new GeoprocessException(
						"Error de driver al calcular el geoproceso interseccion");
			} catch (ProcessVisitorException e) {
				throw new GeoprocessException(
						"Error al procesar el feature de una capa durante el geoproceso interseccion");
			} catch (VisitorException e) {
				throw new GeoprocessException(
						"Error de driver al calcular el geoproceso interseccion");
			} catch (SchemaEditionException e) {
				throw new GeoprocessException(
						"Error al crear el esquema/fichero de la nueva capa");

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
