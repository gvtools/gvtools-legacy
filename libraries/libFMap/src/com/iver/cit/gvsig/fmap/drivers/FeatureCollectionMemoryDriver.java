/*
 * Created on 18-sep-2007
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
 * $Id: FeatureCollectionMemoryDriver.java 14143 2007-09-27 16:58:37Z azabala $
 * $Log: FeatureCollectionMemoryDriver.java,v $
 * Revision 1.1  2007/09/19 15:27:41  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap.drivers;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * Driver to work with a collection of IFeature.
 * 
 * If offers a layer to work with a collection of a features as a vectorial
 * driver. It doesnt allow to write or edit existing features.
 * 
 * @author azabala
 * 
 */
public class FeatureCollectionMemoryDriver implements VectorialDriver,
		ObjectDriver, BoundedShapes {

	public static final Rectangle2D EMPTY_FULL_EXTENT = new Rectangle2D.Double();
	/**
	 * Name of the data source of this driver
	 */
	String name;
	/**
	 * contains all features this driver allows to access.
	 */
	List<IFeature> features;

	/**
	 * Definition of the features.
	 */
	LayerDefinition layerDefinition;

	/**
	 * Full extent of all features
	 */
	Rectangle2D fullExtent;

	// TODO Remove this class
	DriverAttributes attributes = null;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            descriptive name of the data source
	 * @param features
	 *            collection of features in memory
	 * @param definition
	 *            definition of the layer of these features
	 */
	public FeatureCollectionMemoryDriver(String name, List<IFeature> features,
			LayerDefinition definition) {
		this.name = name;
		this.features = features;
		this.layerDefinition = definition;
		this.attributes = new DriverAttributes();
		attributes.setLoadedInMemory(true);
		computeFullExtent();
	}

	public int getShapeType() {
		return layerDefinition.getShapeType();
	}

	public String getName() {
		return name;
	}

	public int getShapeCount() throws ReadDriverException {
		return features.size();
	}

	public DriverAttributes getDriverAttributes() {
		return attributes;
	}

	public Rectangle2D getFullExtent() throws ReadDriverException,
			ExpansionFileReadException {
		if (fullExtent == null) {
			// collection is empty
			return EMPTY_FULL_EXTENT;
		}
		return fullExtent;
	}

	public IGeometry getShape(int index) throws ReadDriverException {
		if (index < features.size())
			return ((IFeature) features.get(index)).getGeometry();
		else
			return null;
	}

	public void reload() throws ReloadDriverException {
		this.name = "";
		this.features = new ArrayList<IFeature>();
		this.fullExtent = null;
		this.layerDefinition = null;
	}

	public boolean isWritable() {
		return false;
	}

	public int[] getPrimaryKeys() throws ReadDriverException {
		return null;
	}

	public void write(DataWare dataWare) throws WriteDriverException,
			ReadDriverException {
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		IFeature feature = (IFeature) features.get((int) rowIndex);
		return feature.getAttributes()[fieldId];
	}

	public int getFieldCount() throws ReadDriverException {
		return layerDefinition.getFieldsDesc().length;
	}

	public String getFieldName(int fieldId) throws ReadDriverException {
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		return fields[fieldId].getFieldName();
	}

	public long getRowCount() throws ReadDriverException {
		return features.size();
	}

	public int getFieldType(int i) throws ReadDriverException {
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		return fields[i].getFieldType();
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		return fields[i].getFieldLength();
	}

	public Rectangle2D getShapeBounds(int index) throws ReadDriverException,
			ExpansionFileReadException {
		IGeometry geometry = getShape(index);
		return geometry.getBounds2D();
	}

	public int getShapeType(int index) throws ReadDriverException {
		IGeometry geometry = getShape(index);
		return geometry.getGeometryType();
	}

	private void computeFullExtent() {
		Iterator featuresIt = features.iterator();
		while (featuresIt.hasNext()) {
			IFeature feature = (IFeature) featuresIt.next();
			Rectangle2D rAux = feature.getGeometry().getBounds2D();
			if (fullExtent == null)
				fullExtent = rAux;
			else
				fullExtent.add(rAux);
		}
	}

}
