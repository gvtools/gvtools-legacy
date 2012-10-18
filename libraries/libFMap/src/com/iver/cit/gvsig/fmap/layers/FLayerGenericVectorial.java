package com.iver.cit.gvsig.fmap.layers;

import org.cresques.cts.ProjectionUtils;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.layers.DriverLayerException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.layers.NameLayerException;
import com.iver.cit.gvsig.exceptions.layers.ProjectionLayerException;
import com.iver.cit.gvsig.exceptions.layers.XMLLayerException;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.utiles.XMLEntity;

public class FLayerGenericVectorial extends FLyrVect {
	private boolean loaded = false;

	private VectorialDriver vDriver = null;


	/* Esto deberia ir en el FLyrDefault */
	public void setCrs(String crsCode) throws Exception{
		setCrs(ProjectionUtils.getCRS(crsCode));
	}


	public void setDriver(VectorialDriver vDriver) {
		this.vDriver = vDriver;
	}

	public VectorialDriver getDriver() {
		return this.vDriver ;
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
	/* FIXME: esto tendria que tener declarado un throws de algo*/
	public void wakeUp() throws LoadLayerException {
		if (!loaded) {
			this.load();
		}

	}


	public void load() throws LoadLayerException {
		if (this.getName() == null || this.getName().length() == 0) {
			this.setAvailable(false);
			throw new NameLayerException(getName(),null);
		}
		if (this.vDriver == null) {
			this.setAvailable(false);
			throw new DriverLayerException(getName(),null);
		}
		if (getCrs() == null) {
			this.setAvailable(false);
			throw new ProjectionLayerException(getName(),null);
		}

		VectorialAdapter adapter = null;
		adapter = new VectorialDefaultAdapter();
		adapter.setDriver(this.vDriver);
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
	}

	public void setXMLEntity(XMLEntity xml) throws XMLException {
		throw new RuntimeException("To implement");
//        CoordinateReferenceSystem crs = null;
//        if (xml.contains("proj")) {
//            crs = ProjectionUtils.getCRS(xml.getStringProperty("proj"));
//        }
//        else
//        {
//            crs = this.getMapContext().getViewPort().getCrs();
//        }
//		this.setName(xml.getName());
//		setCrs(crs);
//
//        String driverName = xml.getStringProperty("other");
//        VectorialDriver driver = null;
//        try {
//            driver = (VectorialDriver) LayerFactory.getDM().getDriver(driverName);
//        } catch (DriverLoadException e) {
//            // Si no existe ese driver, no pasa nada.
//            // Puede que el desarrollador no quiera que
//            // aparezca en el cuadro de diálogo y ha metido
//            // el jar con sus clases en nuestro directorio lib.
//            // Intentamos cargar esa clase "a pelo".
//            if (xml.getChild(2).contains("className"))
//            {
//                String className2 = xml.getChild(2).getStringProperty("className");
//                try {
//                    driver = (VectorialDriver) Class.forName(className2).newInstance();
//                } catch (Exception e1) {
//                    throw new XMLException(e1);
//                }
//            }
//        } catch (NullPointerException npe) {
//            // Si no existe ese driver, no pasa nada.
//            // Puede que el desarrollador no quiera que
//            // aparezca en el cuadro de diálogo y ha metido
//            // el jar con sus clases en nuestro directorio lib.
//            // Intentamos cargar esa clase "a pelo".
//            if (xml.getChild(2).contains("className"))
//            {
//                String className2 = xml.getChild(2).getStringProperty("className");
//                try {
//                    driver = (VectorialDriver) Class.forName(className2).newInstance();
//                } catch (Exception e1) {
//                    throw new XMLException(e1);
//                }
//            }
//        }
//        if (driver == null) {
//        	throw new XMLException(new Exception("Error al cargar el driver"));
//        }
//        if (driver instanceof IPersistence)
//        {
//        	IPersistence persist = (IPersistence) driver;
//            persist.setXMLEntity(xml.getChild(2));
//        }
//        this.setDriver(driver);
//        super.setXMLEntityNew(xml);
	}
}
