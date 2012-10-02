package com.iver.cit.gvsig.fmap.layers;

import java.io.File;
import java.io.FileNotFoundException;

import org.cresques.cts.ProjectionUtils;
import org.geotools.referencing.CRS;
import org.gvsig.tools.file.PathGenerator;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.layers.DriverLayerException;
import com.iver.cit.gvsig.exceptions.layers.FileLayerException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.layers.NameLayerException;
import com.iver.cit.gvsig.exceptions.layers.ProjectionLayerException;
import com.iver.cit.gvsig.exceptions.layers.XMLLayerException;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.utiles.XMLEntity;

public class FLayerFileVectorial extends FLyrVect{
	private boolean loaded = false;
	private File dataFile = null;
	private VectorialFileDriver fileDriver = null;
	private static PathGenerator pathGenerator=PathGenerator.getInstance();
	
	public FLayerFileVectorial() {
		super();
	}

	public FLayerFileVectorial(String name, String fileName,String driverName,String crsName) throws Exception {
		super();
		this.setName(name);
		this.setFileName(fileName);
		this.setDriverByName(driverName);
		this.setCrs(crsName);
	}

	/* Esto deberia ir en el FLyrDefault */
	public void setCrs(String crsName) throws Exception{
		CoordinateReferenceSystem crs = CRS.decode(crsName);
		if (crs == null) {
			throw new Exception("No se ha encontrado la proyeccion: " + crsName);
		}
		setCrs(crs);
	}

	public void setFileName(String filePath) throws FileNotFoundException{
		if (dataFile != null) {
			//TODO: que excepcion lanzar???
			return;
		}
		String path=pathGenerator.getPath(filePath);
		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException(path);
		}
		this.dataFile = file;
	}

	public void setFile(File file) throws FileNotFoundException {
		String path = pathGenerator.getPath(file.getAbsolutePath());
		if (dataFile != null) {
			//TODO: que excepcion lanzar???
			return;
		}
		if (!file.exists()) {
			throw new FileNotFoundException(path);
		}
		this.dataFile = new File(path);
	}

	public String getFileName() {
		if (this.dataFile == null) {
			return null;
		}
		return this.dataFile.getAbsolutePath();
	}


	public void setDriver(VectorialFileDriver driver) {
		this.fileDriver = driver;
	}

	public void setDriverByName(String driverName) throws DriverLoadException {
		this.setDriver(
		  (VectorialFileDriver)LayerFactory.getDM().getDriver(driverName)
		);
	}

	public VectorialFileDriver getDriver() {
		return this.fileDriver;
	}

	/* FIXME: esto tendria que tener declarado un throws de algo*/
	public void wakeUp() throws LoadLayerException {
		if (!loaded) {
			this.load();
		}

	}


	public void load() throws LoadLayerException {
		if (this.dataFile == null) {
			this.setAvailable(false);
			throw new FileLayerException(getName(),null);
		}
		if (this.getName() == null || this.getName().length() == 0) {
			this.setAvailable(false);
			throw new NameLayerException(getName(),null);
		}
		if (this.fileDriver == null) {
			this.setAvailable(false);
			throw new DriverLayerException(getName(),null);
		}
		if (this.getCrs() == null) {
			this.setAvailable(false);
			throw new ProjectionLayerException(getName(),null);
		}

			VectorialFileAdapter adapter = new VectorialFileAdapter(this.dataFile);
			adapter.setDriver(this.fileDriver);

			this.setSource(adapter);

			try {
				this.putLoadSelection();
				this.putLoadLegend();
				this.initializeLegendDefault();
			} catch (LegendLayerException e) {
				this.setAvailable(false);
				throw new LegendLayerException(getName(),e);
			} catch (XMLException e) {
				this.setAvailable(false);
				throw new XMLLayerException(getName(),e);
			} catch (ReadDriverException e) {
				this.setAvailable(false);
				throw new LoadLayerException(getName(),e);
			}
		this.cleanLoadOptions();
		this.loaded = true;
	}

	/* Esto deberia ir en FLyrVect */
	private void initializeLegendDefault() throws LegendLayerException, ReadDriverException {
		if (this.getLegend() == null) {
            if (this.getRecordset().getDriver() instanceof WithDefaultLegend) {
                WithDefaultLegend aux = (WithDefaultLegend) this.getRecordset().getDriver();
                this.setLegend((IVectorLegend) aux.getDefaultLegend());

                ILabelingStrategy labeler = aux.getDefaultLabelingStrategy();
                if (labeler instanceof AttrInTableLabelingStrategy) {
                	((AttrInTableLabelingStrategy) labeler).setLayer(this);
                }

                this.setLabelingStrategy(labeler);
            } else {
                this.setLegend(LegendFactory.createSingleSymbolLegend(
                        this.getShapeType()));
            }
		}
	}

	public void setXMLEntity(XMLEntity xml) throws XMLException {
        CoordinateReferenceSystem crs = null;
        if (xml.contains("proj")) {
            crs = ProjectionUtils.getCRS(xml.getStringProperty("proj"));
        }
        else
        {
            crs = this.getMapContext().getViewPort().getCrs();
        }
		this.setName(xml.getName());
		this.setCrs(crs);
		try {
			this.setDriver(
				(VectorialFileDriver)LayerFactory.getDM().getDriver(
					xml.getStringProperty("driverName")
				)
			);
		} catch (DriverLoadException e) {
			throw new XMLException(e);
		} catch (ClassCastException e) {
			throw new XMLException(e);
		}
		try {
			this.setFileName(xml.getStringProperty("file"));
		} catch (FileNotFoundException e) {
			throw new XMLException(e);
		}

		super.setXMLEntityNew(xml);
	}
}
