package com.iver.cit.gvsig.fmap.drivers.featureiterators;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ISpatialDB;

public class JoinFeatureIterator extends DefaultFeatureIterator implements
		IFeatureIterator {

	protected IFeatureIterator it;
	protected ISpatialDB dbAdapter = null;

	public JoinFeatureIterator(FLyrVect layer, ViewPort viewPort, String[] fields) throws ReadDriverException {
		String[] none = new String[0];
		if (layer.getSource() instanceof ISpatialDB) {
			
			it = layer.getSource().getFeatureIterator(
					viewPort.getAdjustedExtent(),
					none,
					viewPort.getCrs(),
					true);
			dbAdapter = (ISpatialDB) layer.getSource();
		}
		else {
			it = layer.getSource().getFeatureIterator(
					viewPort.getAdjustedExtent(),
					fields,
					viewPort.getCrs(),
					true);
			if (it instanceof DefaultFeatureIterator) {
				((DefaultFeatureIterator) it).setRecordset(layer.getRecordset());
			}
			
		}
		this.fieldNames = fields;
		this.recordset = layer.getRecordset();
	}

	public void closeIterator() throws ReadDriverException {
		it.closeIterator();
	}

	public boolean hasNext() throws ReadDriverException {
		return it.hasNext();
	}

	public IFeature next() throws ReadDriverException {
		IFeature feat = it.next();
		if (dbAdapter != null) {
			int i = dbAdapter.getRowIndexByFID(feat);
			Value[] att = getValues(i);
			feat = new DefaultFeature(feat.getGeometry(), att, feat.getID());
		}
		return feat;
	}

}
