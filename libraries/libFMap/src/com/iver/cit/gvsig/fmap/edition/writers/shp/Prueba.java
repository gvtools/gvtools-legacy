package com.iver.cit.gvsig.fmap.edition.writers.shp;

import java.io.File;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;


public class Prueba {

	public FLayer createLayer(String layerName, VectorialFileDriver d,
			File f, CoordinateReferenceSystem crs) throws ReadDriverException {
		VectorialFileAdapter adapter = new VectorialFileAdapter(f);
		adapter.setDriver((VectorialDriver) d);
		VectorialEditableAdapter vea=new VectorialEditableAdapter();
		vea.setOriginalVectorialAdapter(adapter);
		//FileEditableFeatureSource fefs=new FileEditableFeatureSource((VectorialFileAdapter)adapter);
		//MemoryExpansionFile mef=new MemoryExpansionFile();
		//DefaultEditableFeatureSource defs=new DefaultEditableFeatureSource(mef,fefs, new FBitSet());

		//vea.start();





		FLyrVect capa = new FLyrVect();
		capa.setName(layerName);

		//TODO Meter esto dentro de la comprobación de si hay memoria
		if (false) {
		} else {
			capa.setSource(adapter);
			capa.setCrs(crs);
		}

		try {
			// Le asignamos también una legenda por defecto acorde con
			// el tipo de shape que tenga. Tampoco sé si es aquí el
			// sitio adecuado, pero en fin....
			if (d instanceof WithDefaultLegend) {
				WithDefaultLegend aux = (WithDefaultLegend) d;
				adapter.start();
				capa.setLegend((IVectorLegend) aux.getDefaultLegend());
				adapter.stop();
			} else {
				capa.setLegend(LegendFactory.createSingleSymbolLegend(
						capa.getShapeType()));
			}
			vea.startEdition(EditionEvent.GRAPHIC);
			Value[] values=new Value[5];
			values[0]=ValueFactory.createValue("hola0");
			values[1]=ValueFactory.createValue("hola1");
			values[2]=ValueFactory.createValue("hola2");
			values[3]=ValueFactory.createValue(300d);
			values[4]=ValueFactory.createValue(1d);
			DefaultFeature df0=new DefaultFeature(ShapeFactory.createPoint2D(1,3),values);
			vea.addRow(df0,"",EditionEvent.GRAPHIC);
			DefaultFeature df1=new DefaultFeature(ShapeFactory.createPoint2D(2,4),values);
			vea.addRow(df1,"",EditionEvent.GRAPHIC);
			DefaultFeature df2=new DefaultFeature(ShapeFactory.createPoint2D(3,5),values);
			vea.addRow(df2,"", EditionEvent.GRAPHIC);
			DefaultFeature df3=new DefaultFeature(ShapeFactory.createPoint2D(4,6),values);
			vea.addRow(df3,"", EditionEvent.GRAPHIC);
			ShpWriter writer=null;

			writer = (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
			writer.initialize(capa);
			vea.stopEdition(writer,EditionEvent.GRAPHIC);
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} catch (LegendLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidateRowException e) {
			e.printStackTrace();
		} catch (StartWriterVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverLoadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (InitializeWriterException e) {
			e.printStackTrace();
		} catch (StopWriterVisitorException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		DriverManager driverManager = LayerFactory.getDM();
		driverManager.loadDrivers(new File("d:/eclipse/workspace/fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers"));
		Prueba prueba=new Prueba();
		FLayer layer=null;
		try {
			layer = prueba.createLayer(
					"prueba",
					(VectorialFileDriver) LayerFactory.getDM().getDriver(
							"gvSIG shp driver"), new File(
							"c:/Layers/puntosPrueba.shp"), ProjectionUtils
							.getCRS("EPSG:23030"));
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		}

	}
}
