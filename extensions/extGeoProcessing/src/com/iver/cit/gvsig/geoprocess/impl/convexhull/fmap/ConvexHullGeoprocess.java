/*
 * Created on 16-feb-2006
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
 * $Id: ConvexHullGeoprocess.java 13881 2007-09-19 16:22:04Z jaume $
 * $Log$
 * Revision 1.4  2007-09-19 16:05:22  jaume
 * removed unnecessary imports
 *
 * Revision 1.3  2007/05/15 07:24:19  cesar
 * Add the finished method for execution from Event Dispatch Thread
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
 *
 * Revision 1.1  2006/06/20 18:20:45  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/05/24 21:13:31  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.10  2006/05/01 19:16:22  azabala
 * la cancelacion no solo para el ITask que ejecuta el geoproceso, además llama al metodo cancel() del mismo (que se supone que debería hacer un drop() con los resultados del geoproceso inconcluso)
 *
 * Revision 1.9  2006/03/21 19:26:41  azabala
 * *** empty log message ***
 *
 * Revision 1.8  2006/03/17 19:52:54  azabala
 * *** empty log message ***
 *
 * Revision 1.7  2006/03/15 18:31:06  azabala
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
 * Revision 1.3  2006/03/05 19:57:25  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/26 20:52:43  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/17 16:32:20  azabala
 * *** empty log message ***
 *
 *
 */

package com.iver.cit.gvsig.geoprocess.impl.convexhull.fmap;

import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IOneLayerGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * Geoprocess that computes the convex hull of all geometries of the input
 * layer.
 * 
 * @author azabala
 * 
 */
public class ConvexHullGeoprocess extends AbstractGeoprocess implements
		IOneLayerGeoprocess {

	/**
	 * Schema of the result layer
	 */
	private LayerDefinition resultLayerDefinition;

	/**
	 * Iterates over geometries computing convex hull. TODO Comparative Memory
	 * vs Scalable
	 */
	private ScalableConvexHullVisitor visitor;

	/**
	 * If only consideer selection to compute convex hull
	 */
	private boolean convexSelection;

	public ConvexHullGeoprocess() {
		visitor = new ScalableConvexHullVisitor();
	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean onlySelection = (Boolean) params.get("layer_selection");
		if (onlySelection != null)
			convexSelection = onlySelection.booleanValue();

	}

	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException(
					"Convex Hull con capa de entrada a null");
		if (this.writer == null || this.schemaManager == null) {
			throw new GeoprocessException(
					"Operacion de convex hull sin especificar capa de resultados");
		}

	}

	public void process() throws GeoprocessException {
		Strategy strategy = StrategyManager.getStrategy(firstLayer);
		try {
			if (convexSelection) {
				strategy.process(visitor, firstLayer.getRecordset()
						.getSelection());
			} else {
				strategy.process(visitor);
			}

			IGeometry convexHull = visitor.getConvexHull();
			Object[] attrs = new Object[1];
			attrs[0] = new Long(0);
			IFeature feature = FeatureFactory.createFeature(attrs, convexHull,
					resultLayerDefinition);
			writer.preProcess();
			DefaultRowEdited editedFeature = new DefaultRowEdited(feature,
					IRowEdited.STATUS_ADDED, 0);
			writer.process(editedFeature);
			writer.postProcess();

		} catch (ProcessVisitorException e) {
			throw new GeoprocessException(
					"Problemas durante el proceso de calculo del convex hull");
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Problemas con el driver calculando el convex hull");
		} catch (VisitorException e) {
			throw new GeoprocessException(
					"Problemas con el driver calculando el convex hull");
		}
	}

	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			resultLayerDefinition = new SHPLayerDefinition();
			resultLayerDefinition.setShapeType(XTypes.POLYGON);
			FieldDescription[] fields = new FieldDescription[1];
			fields[0] = new FieldDescription();
			fields[0].setFieldLength(10);
			fields[0].setFieldName("FID");
			fields[0].setFieldType(XTypes.BIGINT);
			resultLayerDefinition.setFieldsDesc(fields);
		}
		return resultLayerDefinition;
	}

	public void setFirstOperand(FLyrVect firstLayer) {
		this.firstLayer = firstLayer;
	}

	public IMonitorableTask createTask() {
		try {
			return new ConvexHullMonitorableTask();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * IMonitorableTask that allows to run convex hull geoprocess in background,
	 * with cancelation requests.
	 * 
	 * @author azabala
	 * 
	 */
	class ConvexHullMonitorableTask implements IMonitorableTask {
		private CancellableMonitorable cancelMonitor = null;
		String MAIN_MESSAGE = PluginServices
				.getText(this, "Mensaje_convexhull");
		String HULL_MESSAGE = PluginServices.getText(this,
				"Mensaje_procesando_convexhull");
		String of = PluginServices.getText(this, "De");
		private boolean finished = false;

		ConvexHullMonitorableTask() throws ReadDriverException {
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
			if (convexSelection) {
				FBitSet selection = firstLayer.getRecordset().getSelection();
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
			return MAIN_MESSAGE;
		}

		public String getNote() {
			return HULL_MESSAGE + " " + getCurrentStep() + " " + of + " "
					+ getFinishStep();
		}

		public void cancel() {
			((DefaultCancellableMonitorable) cancelMonitor).setCanceled(true);
			ConvexHullGeoprocess.this.cancel();
		}

		public void run() throws GeoprocessException {

			Strategy strategy = StrategyManager.getStrategy(firstLayer);
			try {
				if (convexSelection) {
					strategy.process(visitor, firstLayer.getRecordset()
							.getSelection(), cancelMonitor);
				} else {
					strategy.process(visitor, cancelMonitor);
				}

				IGeometry convexHull = visitor.getConvexHull();
				Object[] attrs = new Object[1];
				attrs[0] = new Long(0);
				IFeature feature = FeatureFactory.createFeature(attrs,
						convexHull, resultLayerDefinition);
				writer.preProcess();
				DefaultRowEdited editedFeature = new DefaultRowEdited(feature,
						IRowEdited.STATUS_ADDED, 0);
				writer.process(editedFeature);
				writer.postProcess();

			} catch (ProcessVisitorException e) {
				throw new GeoprocessException(
						"Problemas durante el proceso de calculo del convex hull");
			} catch (ReadDriverException e) {
				throw new GeoprocessException(
						"Problemas con el driver calculando el convex hull");
			} catch (VisitorException e) {
				throw new GeoprocessException(
						"Problemas con el driver calculando el convex hull");
			}
			finished = true;
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
