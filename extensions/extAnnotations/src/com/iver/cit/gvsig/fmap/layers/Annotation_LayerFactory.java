package com.iver.cit.gvsig.fmap.layers;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.rendering.Annotation_Legend;

public class Annotation_LayerFactory {
	public static FieldDescription[] getDefaultFieldDescriptions() {
		String[] fieldNames = {
				Annotation_Mapping.TEXT,
				Annotation_Mapping.TYPEFONT,
				Annotation_Mapping.STYLEFONT,
				Annotation_Mapping.COLOR,
				Annotation_Mapping.HEIGHT,
				Annotation_Mapping.ROTATE};
		int[] fieldLength = {
			150, // text
			25,  // typefont
			-1,  // font style
			10,  // color
			-1,  // height
			-1   // rotate
		};

		FieldDescription[] fieldsDescriptions = new FieldDescription[fieldNames.length];
		FieldDescription desc;

		for (int i=0; i<fieldNames.length; i++) {
			desc = new FieldDescription();
			desc.setFieldName(fieldNames[i]);
			if (fieldLength[i]!=-1) {
				desc.setFieldLength(fieldLength[i]);
			}
			desc.setFieldType(Annotation_Mapping.getType(fieldNames[i]));
			fieldsDescriptions[i] = desc;
		}
		return fieldsDescriptions;
	}

	public static void createEmptyLayer(File file, CoordinateReferenceSystem crs)
			throws DriverLoadException, InitializeWriterException,
			StartWriterVisitorException, StopWriterVisitorException,
			LoadLayerException {
		if (!file.exists())
		{
			SHPLayerDefinition lyrDef = new SHPLayerDefinition();
			lyrDef.setFieldsDesc(getDefaultFieldDescriptions());
			lyrDef.setFile(file);
			lyrDef.setName(file.getName());
			lyrDef.setShapeType(FShape.POINT);
			ShpWriter writer= (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
			writer.setFile(file);
			writer.initialize(lyrDef);
			writer.preProcess();
			writer.postProcess();
			int pos = file.getPath().toLowerCase().lastIndexOf(".shp");
			File gvaFile = null;
			if (pos!=-1) {
				gvaFile = new File(file.getPath().substring(0, pos)+".gva");
			}
			else {
				gvaFile = new File(file.getPath()+".gva");
			}
			try {
				gvaFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static Annotation_Layer createLayer(String layerName, File file,
			CoordinateReferenceSystem crs, int units) {
		return createLayer(layerName, file, crs, units, null);
	}

	public static Annotation_Layer createLayer(String layerName, File file,
			CoordinateReferenceSystem crs, int units, Color background) {
		if (file == null) {
			return null;
		}

		FLayer lyr = null;
		String driverName = "gvSIG shp driver";
		Driver driver = null;

		try {
			driver = LayerFactory.getDM().getDriver(driverName);
		} catch (DriverLoadException e) {
			PluginServices.getLogger().error(e.getMessage(),e);
			return null;
		}

		lyr = LayerFactory.createLayer(layerName, (VectorialFileDriver) driver,
				file, crs, background);

		Annotation_Layer al = new Annotation_Layer();
		LayerListener[] layerListeners = lyr.getLayerListeners();

		for (int i = 0; i < layerListeners.length; i++) {
			al.addLayerListener(layerListeners[i]);
		}
		al.setSource(((FLyrVect) lyr).getSource());
		al.setCrs(lyr.getCrs());
		al.setName(layerName);

		try {
			Annotation_Mapping.addAnnotationMapping(al);
		} catch (Exception e) {
			PluginServices.getLogger().error(e.getMessage(),e);
			return null;
		}
		((Annotation_Legend)al.getLegend()).setUnits(units);
		return al;
	}
}