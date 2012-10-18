package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.AttrQueryFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.AttrQuerySelectionFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.DefaultFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.IndexedSptQueryFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.SpatialQueryFeatureIterator;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;

public class GTVectorialAdapter implements ReadableVectorial {
	private FLyrVect layer;
	private ISpatialIndex spatialIndex;

	public GTVectorialAdapter(FLyrVect layer) {
		this.layer = layer;
	}

	@Override
	public void start() throws ReadDriverException, InitializeDriverException {
		layer.start();
	}

	@Override
	public void stop() throws ReadDriverException {
		layer.stop();
	}

	@Override
	public IGeometry getShape(int index) throws ReadDriverException,
			ExpansionFileReadException {
		try {
			return layer.getShape(index);
		} catch (IOException e) {
			throw new ReadDriverException("none", e);
		}
	}

	@Override
	public int getShapeCount() throws ReadDriverException {
		try {
			return layer.getShapeCount();
		} catch (IOException e) {
			throw new ReadDriverException("none", e);
		}
	}

	@Override
	public Rectangle2D getFullExtent() throws ReadDriverException,
			ExpansionFileReadException {
		return layer.getFullExtent();
	}

	@Override
	public int getShapeType() throws ReadDriverException {
		return layer.getShapeType();
	}

	@Override
	public VectorialDriver getDriver() {
		// TODO gt: drivers are going to be removed
		return null;
	}

	@Override
	public void setDriver(VectorialDriver driver) {
		// TODO gt: drivers are going to be removed
	}

	@Override
	public SelectableDataSource getRecordset() throws ReadDriverException {
		return layer.getRecordset();
	}

	@Override
	public DriverAttributes getDriverAttributes() {
		return layer.getDriverAttributes();
	}

	@Override
	public ISpatialIndex getSpatialIndex() {
		return spatialIndex;
	}

	@Override
	public void setSpatialIndex(ISpatialIndex spatialIndex) {
		this.spatialIndex = spatialIndex;
	}

	@Override
	public void setCrs(CoordinateReferenceSystem crs) {
		layer.setCrs(crs);
	}

	@Override
	public CoordinateReferenceSystem getCrs() {
		return layer.getCrs();
	}

	@Override
	public IFeature getFeature(int numReg) throws ReadDriverException {
		// TODO gt: copied from VectorialAdapter
		IGeometry geom;
		IFeature feat = null;
		try {
			geom = getShape(numReg);
			DataSource rs = getRecordset();
			Value[] regAtt = new Value[rs.getFieldCount()];
			for (int fieldId = 0; fieldId < rs.getFieldCount(); fieldId++) {
				regAtt[fieldId] = rs.getFieldValue(numReg, fieldId);
			}

			feat = new DefaultFeature(geom, regAtt, numReg + "");
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getDriver().getName(), e);
		}
		return feat;
	}

	@Override
	public IFeatureIterator getFeatureIterator() throws ReadDriverException {
		// TODO gt: copied from VectorialAdapter
		return new DefaultFeatureIterator(this, getCrs(), null, null);
	}

	@Override
	public IFeatureIterator getFeatureIterator(String[] fields,
			CoordinateReferenceSystem newCrs) throws ReadDriverException {
		// TODO gt: copied from VectorialAdapter
		return new DefaultFeatureIterator(this, getCrs(), newCrs, fields);
	}

	@Override
	public IFeatureIterator getFeatureIterator(String sql,
			CoordinateReferenceSystem newCrs, boolean withSelection)
			throws ReadDriverException {
		// TODO gt: copied from VectorialAdapter
		if (withSelection)
			return new AttrQuerySelectionFeatureIterator(this, getCrs(),
					newCrs, sql);
		else
			return getFeatureIterator(sql, newCrs);
	}

	@Override
	public IFeatureIterator getFeatureIterator(String sql,
			CoordinateReferenceSystem newCrs) throws ReadDriverException {
		// TODO gt: copied from VectorialAdapter
		return new AttrQueryFeatureIterator(this, getCrs(), newCrs, sql);
	}

	@Override
	public IFeatureIterator getFeatureIterator(Rectangle2D rect,
			String[] fields, CoordinateReferenceSystem newCrs,
			boolean fastIteration) throws ReadDriverException {
		// TODO gt: copied from VectorialAdapter
		if (spatialIndex != null) {
			try {
				if (isSpatialIndexNecessary(rect))
					return new IndexedSptQueryFeatureIterator(this, getCrs(),
							newCrs, fields, rect, spatialIndex, fastIteration);
			} catch (ExpansionFileReadException e) {
				e.printStackTrace();
				throw new ReadDriverException("Error al iterar la capa", e);
			}
		}
		return new SpatialQueryFeatureIterator(this, getCrs(), newCrs, fields,
				rect, fastIteration);
	}

	protected boolean isSpatialIndexNecessary(Rectangle2D extent)
			throws ReadDriverException, ExpansionFileReadException {
		// TODO gt: copied from VectorialAdapter
		Rectangle2D driverExtent = getFullExtent();
		double areaExtent = extent.getWidth() * extent.getHeight();
		double areaFullExtent = driverExtent.getWidth()
				* driverExtent.getHeight();
		return areaExtent < (areaFullExtent / 4.0);
	}
}
