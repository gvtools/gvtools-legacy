package org.gvsig.fmap.drivers.gpe.reader;

import java.awt.Component;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.fmap.drivers.gpe.handlers.DefaultFmapContentHandler;
import org.gvsig.fmap.drivers.gpe.handlers.FmapErrorHandler;
import org.gvsig.fmap.drivers.gpe.handlers.FmapHandlerFactory;
import org.gvsig.fmap.drivers.gpe.model.GPEElement;
import org.gvsig.fmap.drivers.gpe.model.GPEFeature;
import org.gvsig.fmap.drivers.gpe.model.GPEGeometry;
import org.gvsig.gpe.GPERegister;
import org.gvsig.gpe.parser.GPEParser;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public abstract class GPEVectorialDriver implements IGPEDriver,
		VectorialDriver, ObjectDriver, BoundedShapes {
	private Rectangle2D extent = null;
	// The data
	private HashMap features = null;
	private int numFeatures = 0;
	private ArrayList parsers = null;
	private CoordinateReferenceSystem crs = null;
	private File m_Fich;
	private boolean isWarningShowed = false;
	private DriverAttributes attributes = null;

	GPEVectorialDriver() {
		super();
		features = new HashMap();
		GPEParser[] registeredParsers = GPERegister.getAllParsers();
		parsers = new ArrayList();
		for (int i = 0; i < registeredParsers.length; i++) {
			for (int j = 0; j < getGPEParsers().size(); j++) {
				if (registeredParsers[i].getClass() == getGPEParsers().get(j)) {
					parsers.add(registeredParsers[i]);
				}
			}
		}
	}

	/**
	 * @return the parser
	 */
	public ArrayList getParsers() {
		return parsers;
	}

	/**
	 * @return the projection
	 */
	public CoordinateReferenceSystem getCrs() {
		return crs;
	}

	/**
	 * @param crs
	 *            the projection to set
	 */
	public void setCrs(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}

	/**
	 * Add a new feature in the layer
	 * 
	 * @param feature
	 *            The feature to add
	 */
	public void addFeature(GPEFeature feature) {
		IGeometry geometry = getGeometry(feature);
		// if the geometry exists
		if (geometry != null) {
			// Update the extent
			Rectangle2D boundsShp = geometry.getBounds2D();
			if (extent == null) {
				extent = boundsShp;
			} else {
				extent.add(boundsShp);
			}
			// Set the geometry
			feature.getGeometry().setReprojectedGeometry(geometry);
			// Set the attributes
			features.put(new Integer(numFeatures), feature);
			numFeatures++;
		}
	}

	/**
	 * Gets the geometry
	 * 
	 * @param feature
	 *            The feature to add
	 */
	private IGeometry getGeometry(GPEFeature feature) {
		GPEGeometry gpeGeometry = ((GPEFeature) feature).getGeometry();
		if (gpeGeometry != null) {
			CoordinateReferenceSystem geomCrs = null;
			if (gpeGeometry.getSrs() != null) {
				try {
					geomCrs = ProjectionUtils.getCRS(gpeGeometry.getSrs());
				} catch (Exception e) {
					// If the CRS factory has an error.
				}
			}
			if (geomCrs == null) {
				return gpeGeometry.getIGeometry();
			} else {
				if (crs == null) {
					return gpeGeometry.getIGeometry();
				} else {
					if (geomCrs.getName().equals(crs.getName())) {
						return gpeGeometry.getIGeometry();
					} else {
						MathTransform trans = ProjectionUtils.getCrsTransform(
								geomCrs, crs);
						FShape shape = (FShape) gpeGeometry.getIGeometry()
								.getInternalShape();
						shape.reProject(trans);
						return ShapeFactory.createGeometry(shape);
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.drivers.gpe.reader.IGPEDriver#setExtent(java.
	 * awt.geom.Rectangle2D)
	 */
	public void setExtent(Rectangle2D extent) {
		this.extent = extent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getDriverAttributes()
	 */
	public DriverAttributes getDriverAttributes() {
		if (attributes == null) {
			attributes = new DriverAttributes();
			attributes.setLoadedInMemory(true);
		}
		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getFullExtent()
	 */
	public Rectangle2D getFullExtent() {
		return extent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShape(int)
	 */
	public IGeometry getShape(int index) {
		return ((GPEFeature) features.get(new Integer(index))).getGeometry()
				.getReprojectedGeometry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeCount()
	 */
	public int getShapeCount() {
		return features.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeType()
	 */
	public int getShapeType() {
		return FShape.MULTI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#isWritable()
	 */
	public boolean isWritable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#reload()
	 */
	public void reload() {
		numFeatures = 0;
		features.clear();
		extent = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode
	 * .gdbms.engine.data.edition.DataWare)
	 */
	public void write(DataWare dataWare) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory
	 * (com.hardcode.gdbms.engine.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() {
		if (features.size() > 0) {
			GPEFeature feature = (GPEFeature) features.get(new Integer(0));
			return feature.getelements().size() + 1;
		}
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) {
		if (fieldId == getFieldCount() - 1) {
			return "fid";
		}
		if (features.size() > 0) {
			GPEFeature feature = (GPEFeature) features.get(new Integer(0));
			Iterator it = feature.getelements().keySet().iterator();
			String fieldName = null;
			for (int i = 0; i <= fieldId; i++) {
				fieldName = (String) it.next();
			}
			return fieldName;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) {
		if (i == getFieldCount() - 1) {
			return Types.VARCHAR;
		}
		if (getRowCount() > 1) {
			Value value = getFieldValue(0, i);
			return value.getSQLType();
		}
		return Types.VARCHAR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 * int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId) {
		GPEFeature feature = (GPEFeature) features.get(new Integer(
				(int) rowIndex));
		if (fieldId == getFieldCount() - 1) {
			return feature.getId();
		}
		String attName = getFieldName(fieldId);
		GPEElement element = (GPEElement) feature.getelements().get(attName);
		if (element != null) {
			return element.getValue();
		}
		return ValueFactory.createValue("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldWidth(int)
	 */
	public int getFieldWidth(int i) {
		return 50;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() {
		return features.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.BoundedShapes#getShapeBounds(int)
	 */
	public Rectangle2D getShapeBounds(int index) {
		return ((GPEFeature) features.get(new Integer(index))).getGeometry()
				.getShapeBounds();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.BoundedShapes#getShapeType(int)
	 */
	public int getShapeType(int index) {
		return FShape.MULTI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#close()
	 */
	public void close() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#getFile()
	 */
	public File getFile() {
		return m_Fich;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#initialize()
	 */
	public void initialize() {
		FmapErrorHandler errorHandler = FmapHandlerFactory.createErrorHandler();
		DefaultFmapContentHandler contentHandler = FmapHandlerFactory
				.createContentHandler(errorHandler, this);

		GPEParser parser = null;
		for (int i = 0; i < parsers.size(); i++) {
			if (((GPEParser) parsers.get(i)).accept(getFile().toURI())) {
				parser = (GPEParser) parsers.get(i);
			}
		}
		if (parser == null) {
			parser = (GPEParser) parsers.get(0);
		}
		parser.parse(contentHandler, errorHandler, getFile().toURI());
		// TODO patch to support multilayer on KML
		if (getName().equals(KMLVectorialDriver.DRIVERNAME)) {
			if (isWarningShowed == false) {
				JOptionPane.showMessageDialog((Component) PluginServices
						.getMainFrame(), PluginServices.getText(this,
						"gpe_gvsig_dont_support_multilayer"));
				isWarningShowed = true;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#open(java.io.File)
	 */
	public void open(File f) {
		m_Fich = f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		for (int i = 0; i < parsers.size(); i++) {
			if (((GPEParser) parsers.get(i)).accept(f.toURI())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.drivers.gpe.reader.IGPEDriver#getTypeName()
	 */
	public String getTypeName() {
		if (features.size() > 0) {
			GPEFeature feature = (GPEFeature) features.get(new Integer(0));
			return feature.getName();
		}
		return null;
	}

}
