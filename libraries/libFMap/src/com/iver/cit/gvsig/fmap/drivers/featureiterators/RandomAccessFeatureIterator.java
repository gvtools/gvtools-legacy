package com.iver.cit.gvsig.fmap.drivers.featureiterators;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class RandomAccessFeatureIterator implements IFeatureIterator {
	VectorialDriver drv;
	SelectableDataSource ds;
	Rectangle2D r = null;
	String epsg;
	int numRec = 0;

	public RandomAccessFeatureIterator(VectorialDriver driver,
			SelectableDataSource sds, Rectangle2D r, String strEPSG) {
		this.drv = driver;
		this.ds = sds;
		this.r = r;
		this.epsg = strEPSG;
	}

	public RandomAccessFeatureIterator(VectorialDriver driver,
			SelectableDataSource sds, String strEPSG) {
		this.drv = driver;
		this.ds = sds;
		this.epsg = strEPSG;
	}

	public boolean hasNext() throws ReadDriverException {
		boolean bMore = (numRec < drv.getShapeCount());
		return bMore;
	}

	public IFeature next() throws ReadDriverException {
		IGeometry geom;
		IFeature feat = null;
		geom = drv.getShape(numRec);
		// Si pedimos solo las que entran en un rectángulo....
		// ¿QUE PASA SI TENEMOS UN ÍNDICE ESPACIAL?
		if (r != null) {
			while (!(geom.fastIntersects(r.getMinX(), r.getMinY(),
					r.getWidth(), r.getHeight()))) {
				numRec++;
				if (numRec < drv.getShapeCount())
					geom = drv.getShape(numRec);
				else
					return null;
			}
		}
		Value[] regAtt = new Value[ds.getFieldCount()];
		for (int fieldId = 0; fieldId < ds.getFieldCount(); fieldId++) {
			regAtt[fieldId] = ds.getFieldValue(numRec, fieldId);
		}
		feat = new DefaultFeature(geom, regAtt, numRec + "");
		numRec++;
		return feat;
	}

	public void closeIterator() throws ReadDriverException {

	}

}
