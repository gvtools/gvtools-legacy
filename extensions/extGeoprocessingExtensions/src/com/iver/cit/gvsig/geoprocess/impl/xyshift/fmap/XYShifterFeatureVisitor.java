/*
 * Created on 27-jun-2006
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
 * $Id: XYShifterFeatureVisitor.java 10626 2007-03-06 16:55:54Z caballero $
 * $Log$
 * Revision 1.3  2007-03-06 16:48:14  caballero
 * Exceptions
 *
 * Revision 1.2  2006/06/29 17:58:31  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/06/28 18:17:21  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.impl.xyshift.fmap;

import java.awt.geom.AffineTransform;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;

/**
 * This visitor apply an xy offset to visited features and save the result.
 *
 * @author azabala
 *
 */
public class XYShifterFeatureVisitor implements FeatureVisitor {
	/**
	 * offset transform
	 */
	AffineTransform offsetTransform;

	/**
	 * recordset of the source layer
	 */
	SelectableDataSource recordset;

	/**
	 * Schema of the result layer (the same that the input layer)
	 */
	ILayerDefinition layerDefinition;

	/**
	 * If its different of null manages user selection (discarting any feature
	 * else)
	 */
	private FBitSet selection;

	/**
	 * processes result features
	 */
	FeatureProcessor featureProcessor;

	/**
	 * Default constructor
	 *
	 * @param featureProcessor
	 * @param selection
	 * @param dx
	 *            offset to apply in x direction
	 * @param dy
	 *            offset to apply in y direction
	 */
	public XYShifterFeatureVisitor(FeatureProcessor featureProcessor,
										ILayerDefinition layerDef,
										double dx, double dy) {
		this.featureProcessor = featureProcessor;
		this.layerDefinition = layerDef;
		this.offsetTransform = createTransform(dx, dy);
	}

	public void setSelection(FBitSet selection){
		this.selection = selection;
	}

	private AffineTransform createTransform(double dx, double dy) {
		AffineTransform solution = new AffineTransform();
		solution.translate(dx, dy);
		return solution;
	}

	public void visit(IGeometry g, int index) throws VisitorException, StopWriterVisitorException, ProcessVisitorException {
		if(g == null)
			return;
		if(selection != null){
			if(! selection.get(index))
				return;
		}
		IGeometry newGeometry = g.cloneGeometry();
		newGeometry.transform(offsetTransform);
		IFeature newFeature;
		try {
			newFeature = createFeature(newGeometry, index);
		} catch (ReadDriverException e) {
			throw new ProcessVisitorException(recordset.getName(),e,"Error al construir el feature de resultado");
		}
		featureProcessor.processFeature(newFeature);

	}

	private IFeature createFeature(IGeometry geometry, int layerIndex) throws ReadDriverException {
		IFeature solution = null;
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		Value[] featureAttr = new Value[fields.length];
		int numFields = recordset.getFieldCount();
		for (int indexField = 0; indexField < numFields; indexField++) {
			// for each field of firstRs
			String fieldName = recordset.getFieldName(indexField);
			for (int j = 0; j < fields.length; j++) {
				if (fieldName.equalsIgnoreCase(fields[j].getFieldName())) {
					featureAttr[j] = recordset.getFieldValue(layerIndex,indexField);
					break;
				}// if
			}// for
		}// for
		// now we put null values
		for (int i = 0; i < featureAttr.length; i++) {
			if (featureAttr[i] == null)
				featureAttr[i] = ValueFactory.createNullValue();
		}
		solution = FeatureFactory.createFeature(featureAttr, geometry);
		return solution;
	}

	public String getProcessDescription() {
		return "Apply a xy shift to IGeometry coordinates";
	}

	public void stop(FLayer layer) throws StopWriterVisitorException, VisitorException {
		featureProcessor.finish();
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			try {
				this.recordset = ((AlphanumericData) layer).getRecordset();
				this.featureProcessor.start();
			} catch (ReadDriverException e) {
				return false;// must we throw an Exception??
			}
			return true;
		}
		return false;
	}
}