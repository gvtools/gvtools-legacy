/*
 * Created on 17-feb-2006
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
* $Id: UnionGeoprocess.java 29575 2009-06-26 10:31:21Z jmvivo $
* $Log$
* Revision 1.6  2007-09-19 16:08:38  jaume
* ReadExpansionFileException removed from this context
*
* Revision 1.5  2007/08/07 16:10:20  azabala
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
* Revision 1.2  2006/06/08 18:25:49  azabala
* modificaciones para admitir capas de shapeType MULTI
*
* Revision 1.1  2006/05/24 21:09:11  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.12  2006/05/01 19:04:47  azabala
* la cancelacion no solo para el ITask que ejecuta el geoproceso, además llama al metodo cancel() del mismo (que se supone que debería hacer un drop() con los resultados del geoproceso inconcluso)
*
* Revision 1.11  2006/03/26 20:03:18  azabala
* *** empty log message ***
*
* Revision 1.10  2006/03/23 21:05:29  azabala
* *** empty log message ***
*
* Revision 1.9  2006/03/21 19:29:45  azabala
* *** empty log message ***
*
* Revision 1.8  2006/03/17 19:53:53  azabala
* *** empty log message ***
*
* Revision 1.7  2006/03/15 18:34:41  azabala
* *** empty log message ***
*
* Revision 1.6  2006/03/14 18:32:46  fjp
* Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
*
* Revision 1.5  2006/03/07 21:01:33  azabala
* *** empty log message ***
*
* Revision 1.4  2006/03/06 19:48:39  azabala
* *** empty log message ***
*
* Revision 1.3  2006/02/26 20:55:37  azabala
* *** empty log message ***
*
* Revision 1.2  2006/02/20 19:43:51  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/19 20:55:34  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.union.fmap;

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
import com.iver.cit.gvsig.geoprocess.core.fmap.DeferredFeaturePersisterProcessor;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IOverlayGeoprocess;
import com.iver.cit.gvsig.geoprocess.impl.difference.fmap.DifferenceVisitor;
import com.iver.cit.gvsig.geoprocess.impl.intersection.fmap.IntersectVisitor;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;
import com.iver.utiles.swing.threads.IMonitorableTask;
/**
 * Union geoprocess is known like "spatial or" because is an overlay
 * geoprocess (it computes intersections between features of
 * two layers) formed for geometries of one layer or another.
 * <br>
 *  Algorithm makes these three passes:
 *  a) computing intersections and saves them in a temp file.
 *  b) computing differences with first layer.
 *  c) computing differences with second layer.
 * @author azabala
 *
 *TODO Is very similar to ClipGeoprocess. Build an overlay abstract class.
 *
 *
 */
public class UnionGeoprocess extends AbstractGeoprocess
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

	/**
	 * Constructor
	 *
	 */
	public UnionGeoprocess(FLyrVect inputLayer){
		setFirstOperand(inputLayer);
	}

	public void setSecondOperand(FLyrVect secondLayer) {
		this.overlayLayer = secondLayer;
	}

	public void setFirstOperand(FLyrVect firstLayer) {
		this.firstLayer = firstLayer;

	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean firstLayerSelection = (Boolean) params.get("firstlayerselection");
		if(firstLayerSelection != null)
			this.onlyFirstLayerSelection = firstLayerSelection.booleanValue();

		Boolean secondLayerSelection = (Boolean) params.get("secondlayerselection");
		if(secondLayerSelection != null)
			this.onlyClipLayerSelection = secondLayerSelection.booleanValue();

	}

	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException("Union: capa de entrada a null");
		if (overlayLayer == null)
			throw new GeoprocessException("Union: capa de union a null");
		if(this.writer == null ||
		   this.schemaManager == null){
			throw new GeoprocessException("Operacion de union sin especificar capa de resultados");
		}

		/*
		TODO: REVISAR EL CASO DE PUNTOS + PUNTOS: NO HAY QUE CALCULAR NADA
		try {
			if(firstLayer.getShapeType() != XTypes.POLYGON
					&& firstLayer.getShapeType() != XTypes.MULTI){
				throw new GeoprocessException("Primera capa de union no es de polígonos");
			}
			if(overlayLayer.getShapeType() != XTypes.POLYGON
					&& overlayLayer.getShapeType() != XTypes.MULTI){
				throw new GeoprocessException("Segunda capa de union no es de polígonos");
			}
		} catch (ReadDriverException e) {
			throw new GeoprocessException("Error al tratar de chequear si las capas a unir son de polígonos");
		}
		*/

	}

	/**
	 * Runs execution of this geoprocess.
	 */
	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException();
		}
	}

	public void cancel() {
		// TODO Auto-generated method stub

	}

	public ILayerDefinition createLayerDefinition() {
		if(resultLayerDefinition == null){
			try {
				resultLayerDefinition = DefinitionUtils.
						mergeLayerDefinitions(firstLayer, overlayLayer);
				resultLayerDefinition.setShapeType(FShape.MULTI);
			} catch (Exception e) {
				//TODO Quizas createLayerDefinition deberia lanzar
				//una excepcion
				e.printStackTrace();
			}
		}
		return resultLayerDefinition;
	}

	public IMonitorableTask createTask() {
		try {
			return new UnionMonitorableTask();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * IMonitorableTask that allows to run diff geoprocess in background,
	 * with cancelation requests.
	 *
	 * @author azabala
	 *
	 * FIXME INTERNACIONALIZAR TODOS LOS TEXTOS
	 *
	 */
	class UnionMonitorableTask implements IMonitorableTask {
		private CancellableMonitorable cancelMonitor = null;
		String UNION_MESSAGE = PluginServices.getText(this, "Mensaje_union");
		String FIRST_PASS_NOTE = PluginServices.getText(this, "Mensaje_primera_pasada_union");
		String SECOND_PASS_NOTE = PluginServices.getText(this, "Mensaje_segunda_pasada_union");
		String THIRD_PASS_NOTE = PluginServices.getText(this, "Mensaje_tercera_pasada_union");
		String OF = PluginServices.getText(this, "De");


		String currentMessage = "";
		private boolean finished = false;

		UnionMonitorableTask() throws ReadDriverException {
			initialize();
		}
		void initialize() throws ReadDriverException {
			cancelMonitor = createCancelMonitor();
			currentMessage = "Initializing...";
		}

		private CancellableMonitorable createCancelMonitor() throws ReadDriverException {
			DefaultCancellableMonitorable monitor = new
							DefaultCancellableMonitorable();
			monitor.setInitialStep(0);
			monitor.setDeterminatedProcess(true);
			int numSteps = 0;
			//num steps = 2 * firstlayer (intersect + diff A-B)
			//+ second layer (diff B-A)
			if (onlyFirstLayerSelection) {
				FBitSet selection = firstLayer.getRecordset().getSelection();
				numSteps += (2 * selection.cardinality());
			} else {
				numSteps += 2 * firstLayer.getSource().getShapeCount();
			}
			if(onlyClipLayerSelection) {
				FBitSet selection = overlayLayer.getRecordset().getSelection();
				numSteps += (2 * selection.cardinality());
			}else{
				numSteps += 2 * overlayLayer.getSource().getShapeCount();
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
			return UNION_MESSAGE;
		}

		public String getNote() {
			return currentMessage + " "+
			getCurrentStep() + " "+
			OF + " " + getFinishStep();
		}

		public void cancel() {
			((DefaultCancellableMonitorable) cancelMonitor).setCanceled(true);
			UnionGeoprocess.this.cancel();
		}

		public void run() throws GeoprocessException {
			/*
			 * Lo vamos a hacer en tres pasadas:
			 * a) intersecciones con el geoproceso Intersection,
			 * guardando en el fichero del usuario.
			 * b) diferencias de first con overlay, guardando en el
			 * mismo fichero
			 * c) diferencias de overlay con first, guardando en el
			 * mismo fichero
			 */
			try {
				if (!(writer instanceof MultiShpWriter)){
					schemaManager.createSchema(createLayerDefinition());
				}
				writer.preProcess();

				Strategy strategy = StrategyManager.getStrategy(firstLayer);
				Strategy strategy2 = StrategyManager.getStrategy(overlayLayer);
				DeferredFeaturePersisterProcessor featureProcessor =
					new DeferredFeaturePersisterProcessor(writer);

				IntersectVisitor visitor = new IntersectVisitor(overlayLayer,
															featureProcessor,
															strategy2,
															onlyClipLayerSelection);
				//FIXME Meter las selecciones
				currentMessage = FIRST_PASS_NOTE;
				if(onlyFirstLayerSelection){
					FBitSet selection = firstLayer.getRecordset().getSelection();
					strategy.process(visitor, selection, cancelMonitor);
				}else{
					strategy.process(visitor, cancelMonitor);
				}

				currentMessage = SECOND_PASS_NOTE;
				DifferenceVisitor visitor2 = new DifferenceVisitor(overlayLayer,
						featureProcessor, strategy2, onlyClipLayerSelection);
				visitor2.setLayerDefinition(resultLayerDefinition);
				if(onlyFirstLayerSelection){
					FBitSet selection = firstLayer.getRecordset().getSelection();
					strategy.process(visitor2, selection, cancelMonitor);
				}else{
					strategy.process(visitor2, cancelMonitor);
				}
				currentMessage = THIRD_PASS_NOTE;
				DifferenceVisitor visitor3 = new DifferenceVisitor(firstLayer,
						featureProcessor, strategy, onlyFirstLayerSelection);
				visitor3.setLayerDefinition(resultLayerDefinition);
				if(onlyClipLayerSelection){
					FBitSet selection = overlayLayer.getRecordset().getSelection();
					strategy2.process(visitor3, selection, cancelMonitor);
				}else{
					strategy2.process(visitor3, cancelMonitor);
				}
				writer.postProcess();

			} catch (SchemaEditionException e) {
				throw new GeoprocessException("Error al crear el esquema/fichero de la nueva capa");
			} catch (ReadDriverException e) {
				throw new GeoprocessException("Error de driver al calcular el geoproceso interseccion");
			} catch (ProcessVisitorException e) {
				throw new GeoprocessException("Error al procesar el feature de una capa durante el geoproceso interseccion");
			} catch (VisitorException e) {
				throw new GeoprocessException("Error al procesar el feature de una capa durante el geoproceso interseccion");
			} finally{
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

