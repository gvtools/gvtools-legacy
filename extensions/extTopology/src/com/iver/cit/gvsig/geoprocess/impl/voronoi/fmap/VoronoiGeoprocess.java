/*
 * Created on 10-abr-2006
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
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.geoprocess.impl.voronoi.fmap;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.gvsig.jts.voronoi.VoronoiAndTinInputLyr;
import org.gvsig.jts.voronoi.Voronoier;
import org.gvsig.jts.voronoi.Voronoier.VoronoiStrategy;
import org.gvsig.topology.Messages;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.geoprocess.core.AbstractMonitorableGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.utiles.swing.threads.CancellableProgressTask;

public class VoronoiGeoprocess extends AbstractMonitorableGeoprocess {

	private static Logger logger = Logger.getLogger(VoronoiGeoprocess.class
			.getName());
	private boolean useTin;
	private boolean useThiessen;
	private VoronoiStrategy algorithm;

	public VoronoiGeoprocess(VoronoiAndTinInputLyr inputLayer) {
		super();
		this.firstLayer = inputLayer;
	}

	public void checkPreconditions() throws GeoprocessException {
		if (!(firstLayer instanceof VoronoiAndTinInputLyr))
			throw new GeoprocessException(
					"Geoproceso Voronoi cuya capa de entrada es incorrecta: VoronoiAndTinInputLyr");
		if (algorithm == null)
			throw new GeoprocessException("Algoritmo a emplear no especificado");

	}

	public ILayerDefinition createLayerDefinition() {
		ILayerDefinition resultLayerDefinition = new SHPLayerDefinition();
		resultLayerDefinition.setShapeType(XTypes.POLYGON);
		FieldDescription[] fields = new FieldDescription[2];
		fields[0] = new FieldDescription();
		fields[0].setFieldLength(10);
		fields[0].setFieldName("fid");
		fields[0].setFieldType(XTypes.BIGINT);

		fields[1] = new FieldDescription();
		fields[1].setFieldLength(10);
		fields[1].setFieldDecimalCount(4);
		fields[1].setFieldName(PluginServices.getText(this, "punto_causante"));
		fields[1].setFieldType(XTypes.BIGINT);

		resultLayerDefinition.setFieldsDesc(fields);
		return resultLayerDefinition;
	}

	public void setParameters(Map params) throws GeoprocessException {
		Boolean onlySelection = (Boolean) params.get("layer_selection");
		if (onlySelection != null)
			this.operateOnlyWithSelection = onlySelection.booleanValue();

		Boolean useTinB = (Boolean) params.get("tin");
		if (useTinB != null)
			useTin = useTinB.booleanValue();

		Boolean useThiessenB = (Boolean) params.get("thiessen");
		if (useThiessenB != null)
			useThiessen = useThiessenB.booleanValue();

		this.algorithm = (VoronoiStrategy) params.get("algorithm");
	}

	public void process(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		if (progressMonitor != null) {
			initialize(progressMonitor);
		}

		// Prepare the result
		try {
			writer.preProcess();
		} catch (StartWriterVisitorException e) {
			throw new GeoprocessException(e);
		}
		FeaturePersisterProcessor2 featureProcessor = new FeaturePersisterProcessor2(
				writer);
		try {
			featureProcessor.start();
		} catch (StartVisitorException e1) {
			throw new GeoprocessException(e1);
		}

		try {// todo quitar esto y hacer que devuelva un FLyrVect
			List<? extends IFeature> voronoiFeatures = null;
			// FIXME El algoritmo a usar debe poderse especificar a traves del
			// IGeoprocessUserEntries
			if (useTin) {
				voronoiFeatures = Voronoier.createTIN(
						(VoronoiAndTinInputLyr) firstLayer,
						operateOnlyWithSelection, algorithm.getName(),
						progressMonitor);
			} else if (useThiessen) {
				voronoiFeatures = Voronoier.createThiessen(
						(VoronoiAndTinInputLyr) firstLayer,
						operateOnlyWithSelection, algorithm.getName(),
						progressMonitor);
			}

			if (progressMonitor != null) {
				progressMonitor.setInitialStep(0);
				int numOfSteps = voronoiFeatures.size();
				progressMonitor.setFinalStep(numOfSteps);
				progressMonitor.setDeterminatedProcess(true);
				progressMonitor.setNote(Messages.getText("saving_results"));
				progressMonitor.setStatusMessage(Messages
						.getText("voronoi_diagram_layer_message"));
			}

			for (int i = 0; i < voronoiFeatures.size(); i++) {
				featureProcessor.processFeature(voronoiFeatures.get(i));
				if (progressMonitor != null) {
					progressMonitor.reportStep();
				}

			}
			featureProcessor.finish();
			if (progressMonitor != null) {
				progressMonitor.finished();
			}
		} catch (StopVisitorException e) {
			throw new GeoprocessException(
					"Error al finalizar el guardado de los resultados del geoproceso",
					e);
		} catch (BaseException e) {
			throw new GeoprocessException(
					"Error al acceder a la informacion del driver dentro del geoproceso",
					e);
		}
	}

	public void initialize(CancellableProgressTask progressMonitor)
			throws GeoprocessException {
		try {
			progressMonitor.setInitialStep(0);
			int numOfSteps = firstLayer.getSource().getShapeCount();
			progressMonitor.setFinalStep(numOfSteps);
			progressMonitor.setDeterminatedProcess(true);
			progressMonitor.setNote(Messages.getText("voronoi_diagram_note"));
			progressMonitor.setStatusMessage(Messages
					.getText("voronoi_diagram_layer_message"));
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"error accediendo al numero de features de una layer", e);
		}
	}

}
