/*
 * Created on 23-feb-2006
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
* $Id: DissolveGeoprocess.java 18947 2008-02-13 21:57:01Z azabala $
* $Log$
* Revision 1.6  2007-09-19 16:06:59  jaume
* removed unnecessary imports
*
* Revision 1.5  2007/08/07 15:46:26  azabala
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
* Revision 1.2  2006/06/08 18:24:43  azabala
* modificaciones para admitir capas de shapeType MULTI
*
* Revision 1.1  2006/05/24 21:11:14  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
* Revision 1.10  2006/05/08 15:37:26  azabala
* added alphanumeric dissolve
*
* Revision 1.9  2006/05/01 19:14:50  azabala
* la cancelacion no solo para el ITask que ejecuta el geoproceso, además llama al metodo cancel() del mismo (que se supone que debería hacer un drop() con los resultados del geoproceso inconcluso)
*
* Revision 1.8  2006/03/23 21:03:45  azabala
* *** empty log message ***
*
* Revision 1.7  2006/03/17 19:53:14  azabala
* *** empty log message ***
*
* Revision 1.6  2006/03/15 18:33:24  azabala
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
* Revision 1.2  2006/03/05 19:58:20  azabala
* *** empty log message ***
*
* Revision 1.1  2006/02/26 20:53:44  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.IOneLayerGeoprocess;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;
import com.iver.utiles.swing.threads.IMonitorableTask;
/**
 * Processes each geometry of a polygonal vectorial layer, looking for
 * its adjacent polygons. If this adjacent polygons has the same specified
 * field value as processed geometry, this polygons will be dissolved.
 *
 *
 * @author azabala
 *
 */
public class DissolveGeoprocess extends AbstractGeoprocess
							implements IOneLayerGeoprocess {

	private String dissolveField;
	private boolean dissolveOnlySelection;
	private ILayerDefinition resultLayerDefinition;
	private Map fields_functions;
	
	private IDissolveCriteria criteria;
	private int dissolveType = FeatureDissolver.ALPHANUMERIC_DISSOLVE;

	private boolean dissolveOnlyAdjacents;


	/**
	 * Constructor.
	 *
	 * @param inputLayer Layer whose geometries we are
	 * going to dissolve
	 */
	public DissolveGeoprocess(FLyrVect inputLayer, String dissolveField) {
		setFirstOperand(inputLayer);
		this.dissolveField = dissolveField;
	}

	public void setFieldsFunctions(Map fieldsFunctions){
		this.fields_functions = fieldsFunctions;
	}

	public void setFirstOperand(FLyrVect inputLayer) {
		this.firstLayer = inputLayer;

	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean onlySelection =
			(Boolean) params.get("layer_selection");
		if(onlySelection != null)
			dissolveOnlySelection = onlySelection.booleanValue();

		Boolean onlyAdjacents =
			(Boolean) params.get("only_adjacents");
		if(onlyAdjacents != null)
			dissolveOnlyAdjacents = onlyAdjacents.booleanValue();


		try {
			if(dissolveOnlyAdjacents){
				dissolveType = FeatureDissolver.SPATIAL_DISSOLVE;
				criteria =
					new SingleFieldAdjacencyDissolveCriteria(dissolveField,
							firstLayer);
			}else{
				criteria = new SingleFieldDissolveCriteria(dissolveField,
						firstLayer);
			}
		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
			throw new GeoprocessException("Error preparando la lectura del dissolve field", e);
		}

	}

	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException("Buffer con capa de entrada a null");
		if(this.writer == null ||
		   this.schemaManager == null){
			throw new GeoprocessException("Operacion de dissolve sin especificar capa de resultados");
		}
		/*
		try {
			if(firstLayer.getShapeType() != XTypes.POLYGON
					&& firstLayer.getShapeType() != XTypes.MULTI)
				throw new GeoprocessException("La capa a disolver debe ser de polígonos");
		} catch (ReadDriverException e) {
			throw new GeoprocessException("Error intentando verificar el tipo de geometria de la capa a disolver");
		}
		*/
		if(this.dissolveField == null)
			throw new GeoprocessException("No se ha proporcionado el campo para dissolver");

	}

	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException(e);
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
		try {
			if(resultLayerDefinition == null){
				resultLayerDefinition = criteria.createLayerDefinition(fields_functions);
				resultLayerDefinition.setShapeType(firstLayer.getShapeType());
			}
				
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultLayerDefinition;
	}



	public IMonitorableTask createTask() {
		try {
			return new DissolveMonitorableTask();
		} catch (ReadDriverException e) {
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
	class DissolveMonitorableTask implements IMonitorableTask {
		private CancellableMonitorable cancelMonitor = null;

		String DISSOLVE_MESSAGE = PluginServices.getText(this, "Mensaje_dissolve");
		String DISSOLVE_NOTE = PluginServices.getText(this, "Mensaje_procesando_dissolves");
		String OF = PluginServices.getText(this, "De");
		private boolean finished = false;

		FeatureDissolver dissolver = null;

		DissolveMonitorableTask() throws ReadDriverException{
			initialize();
		}
		void initialize() throws ReadDriverException {
			cancelMonitor = createCancelMonitor();
		}

		private CancellableMonitorable createCancelMonitor() throws ReadDriverException{
			DefaultCancellableMonitorable monitor = new
							DefaultCancellableMonitorable();
			monitor.setInitialStep(0);


			if(dissolveOnlySelection)
				monitor.setFinalStep(firstLayer.getRecordset().getSelection().cardinality());
			else
				monitor.setFinalStep(firstLayer.getSource().getShapeCount());
			monitor.setDeterminatedProcess(true);
			return monitor;
		}

		public int getInitialStep() {
			return cancelMonitor.getInitialStep();
		}

		public int getFinishStep() {
			return cancelMonitor.getFinalStep();
		}

		public int getCurrentStep() {
			//return cancelMonitor.getCurrentStep();
//			if(visitor == null)
//				return getInitialStep();
//			else
//				return visitor.getNumProcessedGeometries();
			if(dissolver == null)
				return getInitialStep();
			else
				return dissolver.getNumProcessedGeometries();
		}

		public String getStatusMessage() {
			return DISSOLVE_MESSAGE;
		}

		public String getNote() {
			return DISSOLVE_NOTE + " "+
			getCurrentStep() + " " +
			OF + " " +
			getFinishStep();
		}

		public void cancel() {
			((DefaultCancellableMonitorable) cancelMonitor).setCanceled(true);
			DissolveGeoprocess.this.cancel();
		}

		public void run() throws GeoprocessException {
			try {
				FeaturePersisterProcessor2 processor =
					new FeaturePersisterProcessor2(writer);

				dissolver = new
					FeatureDissolver(processor,
							firstLayer,
							fields_functions,
							criteria,
							dissolveType);
				
				if (dissolveOnlySelection) {
					FBitSet selection = firstLayer.
										getRecordset().
										getSelection();
					dissolver.setSelection(selection);
				}
				dissolver.dissolve(cancelMonitor);
			}catch(Exception e){
				e.printStackTrace();

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

