package com.iver.cit.gvsig.fmap.edition.writers.shp;

import java.io.File;

import javax.swing.JFileChooser;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverManager;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;

public class testWriter {

	public FLayer createLayer(String layerName, VectorialFileDriver d,
			File f, CoordinateReferenceSystem crs) throws DriverException {
			//TODO Comprobar si hay un adaptador ya
			VectorialFileAdapter adapter = new VectorialFileAdapter(f);
			adapter.setDriver(d);
			FLyrVect capa = new FLyrVect();
			capa.setName(layerName);

			capa.setSource(adapter);
			capa.setCrs(crs);


			return capa;
		}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// LayerFactory.setDriversPath("D:/java/eclipse30/eclipse/workspace/FMap 03/driversDebug");
		LayerFactory.setDriversPath("d:/eclipse/workspace/fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers");
		DriverManager driverManager = LayerFactory.getDM();
		// DriverManager driverManager = new DriverManager();
		// driverManager.loadDrivers(new File("../fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers"));
		testWriter test=new testWriter();

		try {
			JFileChooser jfc = new JFileChooser();
			if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				File original = jfc.getSelectedFile();
				FLyrVect layer= (FLyrVect) test.createLayer("prueba",
						(VectorialFileDriver)driverManager.getDriver("gvSIG shp driver"),
						original,
						ProjectionUtils.getCRS("EPSG:23030"));


				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					File newFile = jfc.getSelectedFile();

					ShpWriter writer = (ShpWriter)LayerFactory.getWM().getWriter("Shape Writer");
					writer.initialize(layer);
					writer.preProcess();
					ReadableVectorial adapter = layer.getSource();
					for (int i=0; i < adapter.getShapeCount(); i++)
					{
						IFeature feat = adapter.getFeature(i);
						IRowEdited editFeat = new DefaultRowEdited(feat, IRowEdited.STATUS_MODIFIED, i);
						writer.process(editFeat);
					}
					writer.postProcess();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.exit(0);
	}

}
