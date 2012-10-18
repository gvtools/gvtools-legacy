package com.iver.cit.gvsig.fmap.layers;

import java.io.IOException;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.AbstractDataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.layers.ReloadLayerException;

public class GTDataSource extends AbstractDataSource {
	private FLyrVect layer;

	public GTDataSource(FLyrVect layer) {
		this.layer = layer;
	}

	@Override
	public DataSourceFactory getDataSourceFactory() {
		return LayerFactory.getDataSourceFactory();
	}

	@Override
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		try {
			return layer.getFieldValue(rowIndex, fieldId);
		} catch (IOException e) {
			throw new ReadDriverException("geotools", e);
		}
	}

	@Override
	public int getFieldCount() throws ReadDriverException {
		return layer.getFieldCount();
	}

	@Override
	public String getFieldName(int fieldId) throws ReadDriverException {
		return layer.getFieldName(fieldId);
	}

	@Override
	public long getRowCount() throws ReadDriverException {
		try {
			return layer.getRowCount();
		} catch (IOException e) {
			throw new ReadDriverException("geotools", e);
		}
	}

	@Override
	public String getName() {
		try {
			return layer.getDataSourceName();
		} catch (IOException e) {
			throw new RuntimeException("Cannot get datasource name", e);
		}
	}

	@Override
	public void start() throws ReadDriverException {
		layer.start();
	}

	@Override
	public void stop() throws ReadDriverException {
		layer.stop();
	}

	@Override
	public int[] getPrimaryKeys() throws ReadDriverException {
		// TODO Auto-generated method stub
		return new int[0];
	}

	@Override
	public DataWare getDataWare(int mode) throws ReadDriverException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Driver getDriver() {
		// TODO gt: Drivers are going to be removed
		return null;
	}

	@Override
	public void reload() throws ReloadDriverException {
		try {
			layer.reload();
		} catch (ReloadLayerException e) {
			throw new ReloadDriverException("Cannot reload layer", e);
		}
	}

	@Override
	public int getFieldType(int i) throws ReadDriverException {
		return layer.getFieldType(i);
	}

	@Override
	public int getFieldWidth(int i) throws ReadDriverException {
		return layer.getFieldWidth(i);
	}
}
