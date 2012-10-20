/*
 * Created on 01-mar-2006
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
 * $Id: NearestSpatialJoinVisitor.java 13874 2007-09-19 16:12:18Z jaume $
 * $Log$
 * Revision 1.3  2007-09-19 16:08:13  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.2  2007/03/06 16:47:58  caballero
 * Exceptions
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
 * Revision 1.6  2006/05/01 19:08:41  azabala
 * documentacion, y algunos metodos pasados de private a "friendly" -sin modificador-
 *
 * Revision 1.5  2006/03/21 19:29:36  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/03/15 18:34:31  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/03/14 18:32:46  fjp
 * Cambio con LayerDefinition para que sea compatible con la definición de tablas también.
 *
 * Revision 1.2  2006/03/07 21:01:33  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/06 19:48:39  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/03/05 19:59:47  azabala
 * *** empty log message ***
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.spatialjoin.fmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;

/**
 * This visitor implement Nearest Geometry Spatial Join.
 * 
 * It is a particular case of 1-1 relationship (a feature of layer A always be
 * related to a feature of layer B)
 * 
 * If two features of layer B are at the same distance of a feature of layer A,
 * we will took the first one analized.
 * 
 * In this visitor we apply a secuential scaning strategy: given a geometry of
 * layer A, we check distances with all geometries of layer B return the
 * geometry at the min distance.
 * 
 * 
 * @author azabala
 * 
 */
public class NearestSpatialJoinVisitor implements SpatialJoinVisitor {
	/**
	 * Needed to create layer definition
	 */
	FLyrVect sourceLayer;
	/**
	 * Reads data of features for the source layer
	 */
	SelectableDataSource sourceRecordset;

	/**
	 * Reads data of features for the target layer
	 */
	FLyrVect targetLayer;

	/**
	 * Recordset of this layer
	 */
	SelectableDataSource targetRecordset;

	/**
	 * Strategy to do querys against target Layer
	 */
	Strategy strategy;

	/**
	 * Processes results of dissolve operations (save them in a file, or cache
	 * them in memory, etc)
	 */
	FeatureProcessor featureProcessor;

	/**
	 * It looks for nearest feature to a given feature
	 */
	private NearestFeatureVisitor lookForNearestVisitor;

	/**
	 * Schema of the result layer
	 */
	ILayerDefinition layerDefinition;
	/**
	 * flag to process only selections of second layer
	 */
	boolean onlySecondLayerSelection;

	/**
	 * Constructor. It receives layer with which we want to do a spatial join
	 * 
	 * @param targetRecordset
	 * @throws ReadDriverException
	 *             TODO
	 */
	public NearestSpatialJoinVisitor(FLyrVect sourceLayer,
			FLyrVect targetLayer, FeatureProcessor processor)
			throws ReadDriverException {
		this.sourceLayer = sourceLayer;
		this.sourceRecordset = sourceLayer.getRecordset();
		this.targetLayer = targetLayer;
		this.targetRecordset = targetLayer.getRecordset();
		this.featureProcessor = processor;
		this.lookForNearestVisitor = new NearestFeatureVisitor();
	}

	/**
	 * Processes a Feature of source layer, looking for its nearest feature of
	 * target layer and taking attributes from it
	 */
	public void visit(IGeometry g, int sourceIndex) throws VisitorException,
			ProcessVisitorException {
		if (g == null)
			return;
		lookForNearestVisitor.setQueryGeometry(g.toJTSGeometry());
		try {
			if (onlySecondLayerSelection)
				strategy.process(lookForNearestVisitor,
						targetRecordset.getSelection());
			else
				strategy.process(lookForNearestVisitor);
			int targetIndex = lookForNearestVisitor.getNearestFeatureIndex();
			double shortestDistance = lookForNearestVisitor.getShortestDist();
			IFeature joinedFeature = createFeature(g, sourceIndex, targetIndex,
					shortestDistance);
			this.featureProcessor.processFeature(joinedFeature);
		} catch (ReadDriverException e) {
			throw new ProcessVisitorException(targetRecordset.getName(), e,
					"Problemas accediendo a los datos durante un nearest spatial join");
		}
	}

	public void stop(FLayer layer) throws VisitorException {
		this.featureProcessor.finish();

	}

	public boolean start(FLayer layer) throws StartVisitorException {
		this.featureProcessor.start();
		return true;
	}

	public String getProcessDescription() {
		return "Spatial joining by nearest criteria";
	}

	IFeature createFeature(IGeometry g, int sourceLayerIndex,
			int targetLayerIndex, double shortestDist)
			throws ReadDriverException {
		IFeature solution = null;
		int numFieldsA = sourceRecordset.getFieldCount();
		int numFieldsB = targetRecordset.getFieldCount();
		Value[] featureAttr = new Value[numFieldsA + numFieldsB + 1];
		for (int indexField = 0; indexField < numFieldsA; indexField++) {
			featureAttr[indexField] = sourceRecordset.getFieldValue(
					sourceLayerIndex, indexField);
		}
		for (int indexFieldB = 0; indexFieldB < numFieldsB; indexFieldB++) {
			featureAttr[numFieldsA + indexFieldB] = targetRecordset
					.getFieldValue(targetLayerIndex, indexFieldB);
		}
		featureAttr[numFieldsA + numFieldsB] = ValueFactory
				.createValue(shortestDist);
		solution = FeatureFactory.createFeature(featureAttr, g);
		return solution;
	}

	public ILayerDefinition getResultLayerDefinition()
			throws GeoprocessException {
		if (layerDefinition == null) {
			try {
				layerDefinition = DefinitionUtils.mergeLayerDefinitions(
						sourceLayer, targetLayer);
				// spatial join, in difference of union, intersection or
				// difference
				// adds an additional field to mergeLayerDefinitions result:
				// the shortest distance
				List tempfieldDescs = Arrays.asList(layerDefinition
						.getFieldsDesc());
				ArrayList fieldDescs = new ArrayList(tempfieldDescs);
				FieldDescription newField = new FieldDescription();
				newField.setFieldName("DIST");
				newField.setFieldType(XTypes.DOUBLE);
				newField.setFieldLength(DefinitionUtils
						.getDataTypeLength(XTypes.DOUBLE));
				newField.setFieldDecimalCount(DefinitionUtils.NUM_DECIMALS);
				fieldDescs.add(newField);
				FieldDescription[] newDescs = new FieldDescription[fieldDescs
						.size()];
				fieldDescs.toArray(newDescs);
				layerDefinition.setFieldsDesc(newDescs);

			} catch (Exception e) {
				throw new GeoprocessException(
						"Problemas al crear el esquema de la capa solucion de un spatial join");
			}
		}
		return layerDefinition;
	}

	public void setFeatureProcessor(FeatureProcessor processor) {
		this.featureProcessor = processor;
	}

	public void setCancelableStrategy(Strategy secondLyrStrategy) {
		this.strategy = secondLyrStrategy;

	}

	public void setOnlySecondLyrSelection(boolean onlySecondLayerSelection) {
		this.onlySecondLayerSelection = onlySecondLayerSelection;
	}

}
