package com.iver.gvsig;

import java.io.File;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.gvsig.measure.Operations;

/**
 * Extension responsible for calculating the perimeter of the geometries
 * selected, only when these they be of type line. When there is not an active
 * layer, or some of the geometries selected be not of type line this tool will
 * remain in not visible way.
 * 
 * @author Vicente Caballero Navarro
 */
public class PerimeterExtension extends Extension {
	private MapContext map;
	private FLyrVect lv;

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		ViewPort vp = map.getViewPort();
		Operations operations = new Operations();

		ShpWriter writer = ((IndexedShpDriver) lv.getSource().getDriver())
				.getShpWriter();
		SHPLayerDefinition lyrDef;
		try {
			ReadableVectorial adapter = lv.getSource();
			int numRows = adapter.getShapeCount();
			IGeometry[] geometries = new IGeometry[numRows];
			Object[] values = new Object[numRows];
			for (int i = 0; i < numRows; i++) {
				IFeature feat = adapter.getFeature(i);
				geometries[i] = feat.getGeometry().cloneGeometry();
				values[i] = feat.getAttributes();
			}

			lyrDef = Operations.createLayerDefinition(lv);
			FieldDescription[] fD = lyrDef.getFieldsDesc();
			FieldDescription[] newFD = operations.getPerimeterFields(fD);
			lyrDef.setFieldsDesc(newFD);

			File newFile = ((VectorialFileDriver) adapter.getDriver())
					.getFile();
			writer.setFile(newFile);
			writer.initialize(lyrDef);
			writer.preProcess();

			for (int i = 0; i < numRows; i++) {
				IGeometry geom = geometries[i];
				Value[] vals = (Value[]) values[i];
				Value[] newValues = new Value[vals.length + 1];
				for (int j = 0; j < vals.length; j++) {
					newValues[j] = vals[j];
				}

				double perimeter = operations.getPerimeter(geom, vp);
				newValues[newValues.length - 1] = ValueFactory
						.createValue(perimeter);
				DefaultFeature df = new DefaultFeature(geom, newValues);
				IRowEdited edRow = new DefaultRowEdited(df,
						IRowEdited.STATUS_ADDED, i);
				writer.process(edRow);
			}
			writer.postProcess();
			adapter.getDriver().reload();

			VectorialFileAdapter newAdapter = new VectorialFileAdapter(newFile);
			newAdapter.setDriver(adapter.getDriver());

			lv.setSource(newAdapter);
			lv.setRecordset(newAdapter.getRecordset());

		} catch (com.hardcode.gdbms.engine.data.driver.DriverException e1) {
			e1.printStackTrace();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} catch (InitializeWriterException e) {
			e.printStackTrace();
		} catch (StartWriterVisitorException e) {
			e.printStackTrace();
		} catch (StopWriterVisitorException e) {
			e.printStackTrace();
		} catch (ProcessWriterVisitorException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();

		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			map = model.getMapContext();
			FLayers layers = map.getLayers();
			FLayer[] layersActives = layers.getActives();
			if (layersActives.length == 1) {
				if (layersActives[0] instanceof FLyrVect) {
					lv = (FLyrVect) layersActives[0];
					try {
						int type = lv.getShapeType();
						if (FShape.ARC == type || FShape.LINE == type) {
							return true;
						}
					} catch (ReadDriverException e) {
						return false;
					}
				}
			}

		}
		return false;
	}
}
