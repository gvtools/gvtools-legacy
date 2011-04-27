/*
 * Created on 14-feb-2006
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

*
* $Id: ClipGeoprocess.java 12948 2007-08-07 15:11:18Z azabala $
* $Log$
* Revision 1.4  2007-08-07 15:11:18  azabala
* code cleaning (removing duplicate code)
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
* Revision 1.2  2006/06/08 18:20:23  azabala
* optimizaciones: se usa consulta espacial para solo chequear los elementos de la primera capa que intersecten con la capa de recorte
*
* Revision 1.1  2006/05/24 21:14:07  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.11  2006/05/01 19:16:38  azabala
* la cancelacion no solo para el ITask que ejecuta el geoproceso, además llama al metodo cancel() del mismo (que se supone que debería hacer un drop() con los resultados del geoproceso inconcluso)
*
* Revision 1.10  2006/03/21 19:26:30  azabala
* *** empty log message ***
*
* Revision 1.9  2006/03/17 19:52:43  azabala
* *** empty log message ***
*
* Revision 1.8  2006/03/15 18:30:39  azabala
* *** empty log message ***
*
* Revision 1.7  2006/03/14 18:32:46  fjp
* Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
*
* Revision 1.6  2006/03/07 21:01:33  azabala
* *** empty log message ***
*
* Revision 1.5  2006/03/06 19:48:39  azabala
* *** empty log message ***
*
* Revision 1.4  2006/03/05 19:57:14  azabala
* *** empty log message ***
*
* Revision 1.3  2006/02/19 20:56:32  azabala
* *** empty log message ***
*
* Revision 1.2  2006/02/17 19:25:28  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/17 16:31:58  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.clip.fmap;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.edition.ISchemaManager;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IOverlayGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.impl.convexhull.fmap.ScalableUnionVisitor;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A clip between two layers is the intersection of the first layer with the
 * convex hull of the second layer.
 *
 * It is an individual case of OverlayGeoprocess. When we would have piped
 * geoprocesses, we could model it like a pipe of a ConvexHull geoprocess and an
 * intersection geoprocess.
 *
 * @author azabala
 *
 */
public class ClipGeoprocess extends AbstractGeoprocess implements
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

	/**
	 * processes input layer's features to clip its geometries with clipping
	 * layer bounding box
	 */
	private ClipVisitor visitor;

	/**
	 * Constructor
	 *
	 */
	public ClipGeoprocess(FLyrVect inputLayer) {
		setFirstOperand(inputLayer);
	}

	public void setSecondOperand(FLyrVect secondLayer) {
		this.overlayLayer = secondLayer;

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
			throw new GeoprocessException("Clip: capa de entrada a null");
		if (overlayLayer == null)
			throw new GeoprocessException("Clip: capa de clip a null");
		if (this.writer == null || this.schemaManager == null) {
			throw new GeoprocessException(
					"Operacion de clip sin especificar capa de resultados");
		}

		try {
			if (overlayLayer.getShapeType() != XTypes.POLYGON &&
					(overlayLayer.getShapeType() != XTypes.MULTI)) {
				throw new GeoprocessException(
						"La capa de recorte no es de polígonos");
			}
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
			"Error al tratar de chequear si la capa de recorte es de polígonos");
		}
	}

	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException(e);
		}
	}


	public void setResultLayerProperties(IWriter writer,
			ISchemaManager schemaManager) {
		this.writer = writer;
		this.schemaManager = schemaManager;

	}


	public ILayerDefinition createLayerDefinition() {
		//result layer definition will be the same that
		//input layer
		if(resultLayerDefinition == null){
			try {
				resultLayerDefinition = DefinitionUtils
						.createLayerDefinition(firstLayer);
			} catch (Exception e) {
				// TODO Quizas createLayerDefinition deberia lanzar
				// una excepcion
				e.printStackTrace();
			}
		}
		return resultLayerDefinition;
	}

	/**
	 * Computes union of all geometries of the clipping layer
	 *
	 * @return
	 * @throws com.iver.cit.gvsig.fmap.DriverException
	 * @throws ReadDriverException
	 * @throws VisitorException
	 * @throws ExpansionFileReadException
	 */
	
	//TODO Esto lo vamos a quitar, y lo vamos a hacer para cada
	//feature individual
	private Geometry computeJtsClippingPoly()
			throws ReadDriverException, ExpansionFileReadException, VisitorException {
		ScalableUnionVisitor visitor = new ScalableUnionVisitor(overlayLayer.getShapeType());

		Strategy strategy = StrategyManager.getStrategy(overlayLayer);
		if (onlyClipLayerSelection) {
			strategy.process(visitor, overlayLayer.getRecordset()
					.getSelection());
		} else {
			strategy.process(visitor);
		}
		return visitor.getJtsConvexHull();
	}

	public IMonitorableTask createTask() {
		final CancellableMonitorable cancelMonitor =
			createCancelMonitor();

		return new IMonitorableTask() {
			String CLIP_GEOP_MSG = PluginServices.getText(this,
												"Mensaje_clip");
			String CLIP_MESSAGE = PluginServices.getText(this,
					"Mensaje_procesando_clip_primero");
			String INTERS_MESSAGE = PluginServices.getText(this,
				"Mensaje_procesando_clip_segundo");
			String of = PluginServices.getText(this,
			"De");
			String currentMsg = CLIP_MESSAGE;
			private boolean finished = false;

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
				return CLIP_GEOP_MSG;
			}

			public String getNote() {
				return currentMsg  +
				" " +
				getCurrentStep()+
				" "+
				of+
				" "+
				getFinishStep();
			}

			public void cancel() {
				((DefaultCancellableMonitorable) cancelMonitor)
						.setCanceled(true);
				ClipGeoprocess.this.cancel();
			}

			public void run() {

				//Esto lo vamos a quitar
				Geometry clippingGeometry = null;
				try {
					clippingGeometry = computeJtsClippingPoly();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				currentMsg = INTERS_MESSAGE;
				visitor = new ClipVisitor(clippingGeometry, resultLayerDefinition,
						schemaManager, writer);
				Strategy strategy = StrategyManager.getStrategy(firstLayer);
				Rectangle2D clippingRect = FConverter.
					convertEnvelopeToRectangle2D(clippingGeometry.
											getEnvelopeInternal());
				try {
					if (onlyFirstLayerSelection) {
						visitor.setSelection(firstLayer.getRecordset().getSelection());
					}
					strategy.process(visitor,
							clippingRect,
							cancelMonitor);


				} catch (Exception e) {
					e.printStackTrace();
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

			public void finished() {
				// TODO Auto-generated method stub
				
			}
		};
	}

	private DefaultCancellableMonitorable createCancelMonitor() {
		DefaultCancellableMonitorable monitor = new DefaultCancellableMonitorable();
		monitor.setInitialStep(0);
		monitor.setDeterminatedProcess(true);
		int clipSteps = 0;
		try {
			if (onlyClipLayerSelection){
				FBitSet selection = overlayLayer.getRecordset().getSelection();
				clipSteps = selection.cardinality();
			}else{
					clipSteps = overlayLayer.getSource().getShapeCount();

			}
			int  firstSteps = 0;
			if (onlyFirstLayerSelection) {
				FBitSet selection = firstLayer.getRecordset().getSelection();
				firstSteps = selection.cardinality();
			} else {
					firstSteps = firstLayer.getSource()
									.getShapeCount();

			}
			int totalSteps = clipSteps + firstSteps;
			monitor.setFinalStep(totalSteps);
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return monitor;
	}

}
