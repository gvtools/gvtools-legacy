package org.gvsig.fmap.raster.grid.roi;

import java.awt.Color;
import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import org.gvsig.raster.dataset.FileNotExistsException;
import org.gvsig.raster.grid.Grid;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class VectorialROIsReader {

	private String filename = null;
	private CoordinateReferenceSystem crs = null;
	private FLyrVect fLyrVect = null;
	private HashMap rois = null;
	private Grid grid = null;

	public VectorialROIsReader(String filename, Grid grid,
			CoordinateReferenceSystem crs) throws LoadLayerException,
			FileNotExistsException {
		this.filename = filename;
		this.crs = crs;
		this.grid = grid;
		File file = new File(filename);
		if (file.exists()) {
			fLyrVect = (FLyrVect) LayerFactory.createLayer("layer1",
					"gvSIG shp driver", file, crs);
		} else
			throw new FileNotExistsException("file not found");
	}

	public ArrayList read(ArrayList existingROIs) throws ReadDriverException,
			InvalidROIsShpException {
		SelectableDataSource dataSource = fLyrVect.getSource().getRecordset();

		// Validación del .shp:
		int nameFieldIndex = dataSource.getFieldIndexByName("name");
		int rFiledIndex = dataSource.getFieldIndexByName("R");
		int gFiledIndex = dataSource.getFieldIndexByName("G");
		int bFiledIndex = dataSource.getFieldIndexByName("B");
		if (nameFieldIndex < 0 || rFiledIndex < 0 || gFiledIndex < 0
				|| bFiledIndex < 0) {
			throw new InvalidROIsShpException("");
		}
		if (dataSource.getFieldType(nameFieldIndex) != Types.VARCHAR
				|| dataSource.getFieldType(rFiledIndex) < Types.NUMERIC
				|| dataSource.getFieldType(rFiledIndex) > Types.DOUBLE
				|| dataSource.getFieldType(gFiledIndex) < Types.NUMERIC
				|| dataSource.getFieldType(gFiledIndex) > Types.DOUBLE
				|| dataSource.getFieldType(bFiledIndex) < Types.NUMERIC
				|| dataSource.getFieldType(bFiledIndex) > Types.DOUBLE)
			throw new InvalidROIsShpException("");

		if (existingROIs != null)
			rois = new HashMap();
		if (existingROIs != null) {
			for (int i = 0; i < existingROIs.size(); i++) {
				VectorialROI roi = (VectorialROI) existingROIs.get(i);
				rois.put(roi.getName(), roi);
			}
		}
		String roiName;
		int r, g, b;
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			IFeature feature = fLyrVect.getSource().getFeature(i);
			roiName = feature.getAttribute(nameFieldIndex).toString();
			VectorialROI roi = null;
			if (!rois.containsKey(roiName)) {
				roi = new VectorialROI(grid);
				roi.setName(roiName);
				r = ((NumericValue) feature.getAttribute(rFiledIndex))
						.intValue();
				g = ((NumericValue) feature.getAttribute(gFiledIndex))
						.intValue();
				b = ((NumericValue) feature.getAttribute(bFiledIndex))
						.intValue();
				roi.setColor(new Color(r, g, b));
				rois.put(roi.getName(), roi);
			} else
				roi = (VectorialROI) rois.get(roiName);
			roi.addGeometry(feature.getGeometry());
		}
		return new ArrayList(rois.values());
	}
}
