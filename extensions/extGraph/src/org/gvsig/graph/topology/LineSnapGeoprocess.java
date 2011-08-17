/*
 * Created on 10-oct-2006
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
*/
package org.gvsig.graph.topology;

import java.io.File;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.impl.topology.lineclean.fmap.LineCleanVisitor;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.util.SnappingCoordinateMap;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.IPipedTask;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class LineSnapGeoprocess extends AbstractGeoprocess {

	/**
	 * Schema of the result layer
	 */
	private ILayerDefinition resultLayerDefinition;


	/**
	 * flag to only clip selection of input layer
	 */
	private boolean onlyFirstLayerSelection = false;
	
	/**
	 * flag to mark if must create a layer with the detected
	 * pseudonodes
	 */
	private boolean createLyrsWithErrorGeometries = false;

	/**
	 * Processes features (writing them)
	 */
	FeaturePersisterProcessor2 processor;


	private boolean onlySnapNodes = false;
	
	private double tolerance = 0.1;


	public LineSnapGeoprocess(FLyrVect inputLayer){
		this.firstLayer = inputLayer;
	}


	public void setParameters(Map params) throws GeoprocessException {
		Boolean firstLayerSelection = (Boolean) params.get("firstlayerselection");
		if (firstLayerSelection != null)
			this.onlyFirstLayerSelection =
				firstLayerSelection.booleanValue();
		
		Boolean createLyrsWithError = (Boolean) params.get("createlayerswitherrors");
		if (createLyrsWithError != null)
			this.createLyrsWithErrorGeometries =
				createLyrsWithError.booleanValue();

		Boolean onlySnapNodes = (Boolean) params.get("onlysnapnodes");
		if (onlySnapNodes != null)
			this.onlySnapNodes  =
				onlySnapNodes.booleanValue();

	}

	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException("CLEAN: capa de entrada a null");
		if (this.writer == null || this.schemaManager == null) {
			throw new GeoprocessException(
					"Operacion de CLEAN sin especificar capa de resultados");
		}
		try {
			if(firstLayer.getSource().getShapeCount() == 0){
				throw new GeoprocessException(
				"Capa de entrada vacia");
			}
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
			"Error al verificar si la capa está vacía");
		}
	}

	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException("Error al ejecutar el geoproceso", e);
		}
	}

	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			try {
				resultLayerDefinition = DefinitionUtils.
							createLayerDefinition(firstLayer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultLayerDefinition;
	}


	class LineCleanTask extends AbstractMonitorableTask implements IPipedTask{

		private SnappingCoordinateMap coordMap;

		private LineCleanTask() {
			setInitialStep(0);
			try {
				if (onlyFirstLayerSelection) {
					int numSelected = firstLayer.getRecordset().getSelection()
							.cardinality();
					setFinalStep(numSelected);
				} else {
					int numShapes = firstLayer.getSource().getShapeCount();
					setFinalStep(numShapes);
				}// else
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setDeterminatedProcess(true);
			setStatusMessage(PluginServices.getText(this,
					"LineClean._Progress_Message"));

		}

		/**
		 * Verifies cancelation events, and return a boolean flag if processes must
		 * be stopped for this cancelations events.
		 *
		 * @param cancel
		 * @param va
		 * @param visitor
		 * @return
		 * @throws DriverIOException
		 */
		protected boolean verifyCancelation(ReadableVectorial va) {
			if (isCanceled()) {
				try {
					va.stop();
				} finally {
					return true;
				}
			}
			return false;
		}


		public void run() throws Exception {
			processor =
				new FeaturePersisterProcessor2(writer);

			FBitSet selection = null;
			coordMap = new SnappingCoordinateMap(tolerance);
			try {
				processor.start();

				ReadableVectorial va = firstLayer.getSource();
				va.start();
				for (int i = 0; i < va.getShapeCount(); i++) {// for each geometry
					if (verifyCancelation(va)) {
						return;
					}
					IFeature feat = null;
					if(selection != null){
						if (selection.get(i)) {
								reportStep();
								feat = va.getFeature(i);
								feat = processFeature(feat);
								processor.processFeature(feat);
						}

					}else{
						reportStep();
						feat = va.getFeature(i);
						feat = processFeature(feat);
					}
					processor.processFeature(feat);
				}// for
				va.stop();
				processor.finish();


			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}

		private IFeature processFeature(IFeature feat) {
			IGeometry geom = feat.getGeometry();
			Geometry jtsGeom = geom.toJTSGeometry();
			Coordinate[] coords = jtsGeom.getCoordinates();
			Coordinate c1 = coords[0];
			Coordinate c2 = coords[coords.length-1];
			if (coordMap.containsKey(c1)) {
				c1 = (Coordinate) coordMap.get(c1);
			}
			else
			{
				coordMap.put(c1, c1);
			}
			if (coordMap.containsKey(c2)) {
				c2 = (Coordinate) coordMap.get(c2);
			}
			else
			{
				coordMap.put(c2, c2);
			}
			coords[0] = c1;
			coords[1] = c2;
			jtsGeom.getFactory().createLineString(coords);
			feat.setGeometry(FConverter.jts_to_igeometry(jtsGeom));
			return feat;
			
		}

		// TODO INTERNACIONALIZAR LOS MENSAJES
		public String getNote() {
			String cleaningText = PluginServices.getText(this, "Limpiando_lineas");
			String of = PluginServices.getText(this, "de");
			return cleaningText + " " + getCurrentStep() + " "
					+ of + " " + getFinishStep();
		}

		public void cancel() {
			setCanceled(true);
			LineSnapGeoprocess.this.cancel();
		}

		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IPipedTask#getResult()
		 */
		public Object getResult() {
			try {
				return LineSnapGeoprocess.this.getResult();
			} catch (GeoprocessException e) {
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IPipedTask#setEntry(java.lang.Object)
		 */
		public void setEntry(Object object) {
			// TODO Auto-generated method stub

		}

		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IMonitorableTask#finished()
		 */
		public void finished() {
			// TODO Auto-generated method stub

		}
	}


	public IMonitorableTask createTask() {
		return new LineCleanTask();
	}

	public FLayer getResult() throws GeoprocessException {
		FLyrVect cleanedLayer = (FLyrVect) createLayerFrom(this.writer);
		return cleanedLayer;
	}


	public double getTolerance() {
		return tolerance;
	}


	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

}

