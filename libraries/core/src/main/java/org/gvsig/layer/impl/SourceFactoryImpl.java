package org.gvsig.layer.impl;

import java.util.Map;

import org.gvsig.layer.Source;
import org.gvsig.layer.SourceFactory;

public class SourceFactoryImpl implements SourceFactory {

	@Override
	public Source createFileSource(String path) {
		assert false;
		return null;
	}

	@Override
	public Source createDBSource(String host, int port, String user,
			String password, String dbName, String tableName, String driverInfo) {
		assert false;
		return null;
	}

	@Override
	public Source createSource(Map<String, String> properties) {
		return createSource(null, properties);
	}

	@Override
	public Source createSource(String typeName, Map<String, String> properties) {
		return new SourceImpl(typeName, properties);
	}

}
