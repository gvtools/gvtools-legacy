package com.iver.cit.gvsig.fmap.layers;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.fmap.Source;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;

/**
 * This class should take files, urls, etc. as input and produce {@link FLayer}
 * instances
 * 
 * @author fergonco
 */
public class GTLayerFactory {

	public static FLyrVect createVectorLayer(String layerName, File file,
			CoordinateReferenceSystem crs) throws IOException {
		return createVectorLayer(layerName, getSource(file.toURI().toURL()),
				crs, null);
	}

	public static FLyrVect createVectorLayer(String layerName, File file,
			CoordinateReferenceSystem crs, Color background) throws IOException {
		return createVectorLayer(layerName, getSource(file.toURI().toURL()),
				crs, background);
	}

	private static FLyrVect createVectorLayer(String layerName, Source source,
			CoordinateReferenceSystem crs, Color background) throws IOException {
		FLyrVect layer = new FLyrVect(source);

		layer.setName(layerName);
		layer.setCrs(crs);

		try {
			WithDefaultLegend aux = source.getDefaultLegend();
			if (aux != null) {
				layer.setLegend((IVectorLegend) aux.getDefaultLegend());

				ILabelingStrategy labeler = aux.getDefaultLabelingStrategy();
				if (labeler instanceof AttrInTableLabelingStrategy) {
					((AttrInTableLabelingStrategy) labeler).setLayer(layer);
				}
				layer.setLabelingStrategy(labeler);
				layer.setIsLabeled(true); // TODO: ací no s'hauria de detectar
											// si té etiquetes?????
			} else {
				IVectorLegend leg = LegendFactory.createSingleSymbolLegend(
						layer.getShapeType(), background);
				layer.setLegend(leg);

			}
		} catch (LegendLayerException e) {
			throw new IOException("Cannot set legend", e);
		} catch (ReadDriverException e) {
			throw new IOException(e);
		}
		return layer;
	}

	private static Source getSource(URL url) {
		return new DefaultSource(url);
	}

}
