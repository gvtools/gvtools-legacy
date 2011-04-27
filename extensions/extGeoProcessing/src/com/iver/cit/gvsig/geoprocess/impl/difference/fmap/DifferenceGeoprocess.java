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
 * $Id: DifferenceGeoprocess.java 29576 2009-06-26 10:38:37Z jmvivo $
 * $Log$
 * Revision 1.6  2007-09-19 16:05:53  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.5  2007/08/07 15:42:19  azabala
 * centrilizing JTS in JTSFacade
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
 * Revision 1.2  2006/06/08 18:24:23  azabala
 * modificaciones para admitir capas de shapeType MULTI
 *
 * Revision 1.1  2006/05/24 21:11:38  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.10  2006/05/01 19:15:37  azabala
 * la cancelacion no solo para el ITask que ejecuta el geoproceso, además llama al metodo cancel() del mismo (que se supone que debería hacer un drop() con los resultados del geoproceso inconcluso)
 *
 * Revision 1.9  2006/03/26 20:02:25  azabala
 * *** empty log message ***
 *
 * Revision 1.8  2006/03/21 19:26:53  azabala
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/17 19:53:05  azabala
 * *** empty log message ***
 *
 * Revision 1.6  2006/03/15 18:31:50  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/14 18:32:46  fjp
 * Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
 *
 * Revision 1.4  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/03/05 19:57:58  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/26 20:53:13  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.difference.fmap;

import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
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
 * This geoprocess computes diference geometries of two overlay polygon layers.
 * Difference of two geometries is the set of point of one geometry that the
 * other geometry doesnt have.
 * By analogy, this geoprocess computes difference geometries between vectorial
 * layers.
 *
 * @author azabala
 *
 */
public class DifferenceGeoprocess extends AbstractGeoprocess
					implements IOverlayGeoprocess {

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

	public DifferenceGeoprocess(FLyrVect inputLayer) {
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
		/*AZABALA: PERMITIMOS CAPAS DE PUNTOS, LINEAS Y DE POLIGONOS
		try {
			if (firstLayer.getShapeType() != XTypes.POLYGON
					&& firstLayer.getShapeType() != XTypes.MULTI) {
				throw new GeoprocessException(
						"Primera capa de interseccion no es de polígonos");
			}
			if (overlayLayer.getShapeType() != XTypes.POLYGON
					&& overlayLayer.getShapeType() != XTypes.MULTI) {
				throw new GeoprocessException(
						"Segunda capa de interseccion no es de polígonos");
			}
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
			"Error al tratar de chequear si las capas a intersectar son de polígonos");
		}
		*/

	}


	//FIXME La unica diferencia entre este geoproceso y el intersection
	//es que usa visitors distintos
	//REDISEÑAR TODOS LOS OVERLAYGEOPROCESS
	public void process() throws GeoprocessException {
		try {
			new DifferenceMonitorableTask().run();
		}catch (DriverIOException e) {
			throw new GeoprocessException("Error de lectura de driver durante geoproceso diferencia");
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
				resultLayerDefinition = DefinitionUtils.createLayerDefinition(firstLayer);
				//All overlay geoprocesses could generate various kind of geometry types
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
			return new DifferenceMonitorableTask();
		} catch (DriverIOException e) {
			//FIXME Debe lanzar excepcion createTask ?
			return null;
		}
	}

	/**
	 * IMonitorableTask that allows to run diff geoprocess in background,
	 * with cancelation requests.
	 *
	 * @author azabala
	 *
	 */
	class DifferenceMonitorableTask implements IMonitorableTask {
		private CancellableMonitorable cancelMonitor = null;
		String DIFFERENCE_MESSAGE = PluginServices.getText(this, "Mensaje_difference");
		String DIFFERENCE_NOTE = PluginServices.getText(this, "Mensaje_procesando_diferencia");
		String OF = PluginServices.getText(this, "De");
		private boolean finished = false;

		DifferenceMonitorableTask() throws DriverIOException {
			initialize();
		}
		void initialize() throws DriverIOException {
			cancelMonitor = createCancelMonitor();
		}

		private CancellableMonitorable createCancelMonitor() {
			DefaultCancellableMonitorable monitor = new
							DefaultCancellableMonitorable();
			monitor.setInitialStep(0);
			//Really its undeterminated, but so we must to process all
			//elements of first layer (or selection) we are going to
			//consideer determinated
			monitor.setDeterminatedProcess(true);
			int numSteps = 0;
			if (onlyFirstLayerSelection) {
				FBitSet selection = null;
				try {
					selection = firstLayer.getRecordset().getSelection();
				} catch (ReadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				numSteps = selection.cardinality();
			} else {
				try {
					numSteps = firstLayer.getSource().getShapeCount();
				} catch (ReadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
			return DIFFERENCE_MESSAGE;
		}

		public String getNote() {
			return DIFFERENCE_NOTE + " " +
			getCurrentStep() + " "+
			OF + " "+
			getFinishStep();
		}

		public void cancel() {
			((DefaultCancellableMonitorable) cancelMonitor).setCanceled(true);
			DifferenceGeoprocess.this.cancel();
		}

		public void run() throws GeoprocessException {
			try {
				if (!(writer instanceof MultiShpWriter)){
					schemaManager.createSchema(createLayerDefinition());
				}
				writer.preProcess();
				Strategy strategy =
					StrategyManager.getStrategy(firstLayer);
				FeaturePersisterProcessor2 featureProcessor =
					new FeaturePersisterProcessor2(writer);
				Strategy overlayStrategy =
					StrategyManager.getStrategy(overlayLayer);
				DifferenceVisitor visitor = new DifferenceVisitor(overlayLayer,
						featureProcessor, overlayStrategy, onlyClipLayerSelection);
				visitor.setLayerDefinition(resultLayerDefinition);
				if (onlyFirstLayerSelection) {
					strategy.process(visitor, firstLayer.getRecordset()
							.getSelection(), cancelMonitor);
				} else {
					strategy.process(visitor, cancelMonitor);
				}

			} catch (ProcessVisitorException e) {
				throw new GeoprocessException(
						"Error al procesar el feature de una capa durante el geoproceso interseccion");
			} catch (SchemaEditionException e) {
				throw new GeoprocessException(
					"Error al crear el esquema/fichero de la nueva capa");
			} catch (ReadDriverException e) {
				throw new GeoprocessException(
					"Error de driver al calcular el geoproceso interseccion");
			} catch (VisitorException e) {
				throw new GeoprocessException(
					"Error de driver al calcular el geoproceso interseccion");
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
		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IMonitorableTask#finished()
		 */
		public void finished() {
			// TODO Auto-generated method stub

		}
	}

}
