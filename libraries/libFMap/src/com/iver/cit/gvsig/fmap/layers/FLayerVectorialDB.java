/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package com.iver.cit.gvsig.fmap.layers;

import org.cresques.cts.IProjection;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.layers.DriverLayerException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.layers.NameLayerException;
import com.iver.cit.gvsig.exceptions.layers.ProjectionLayerException;
import com.iver.cit.gvsig.exceptions.layers.XMLLayerException;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.DefaultJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.utiles.XMLEntity;


public class FLayerVectorialDB extends FLyrVect {
    private boolean loaded = false;
    private IVectorialDatabaseDriver dbDriver = null;

    /* Esto deberia ir en el FLyrDefault */
    public void setProjectionByName(String projectionName)
        throws Exception {
        IProjection proj = CRSFactory.getCRS(projectionName);

        if (proj == null) {
            throw new Exception("No se ha encontrado la proyeccion: " +
                projectionName);
        }

        this.setProjection(proj);
    }

    public void setDriver(IVectorialDatabaseDriver driver) {
        this.dbDriver = driver;
    }

    public void setDriverByName(String driverName) throws ReadDriverException {
		try {
			this.setDriver(
			  (IVectorialDatabaseDriver)LayerFactory.getDM().getDriver(driverName)
			);
		} catch (DriverLoadException e) {
			throw new ReadDriverException(getName(),e);
		}
	}

    public IVectorialDatabaseDriver getDriver() {
        return this.dbDriver;
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
			throw new NameLayerException(this.getName(),null);
		}
		if (this.dbDriver == null) {
			this.setAvailable(false);
			//TODO: traducir???
			throw new DriverLayerException(this.getName(),null);
		}
		if (this.getProjection() == null) {
			this.setAvailable(false);
			//TODO: traducir???
			throw new ProjectionLayerException(this.getName(),null);
		}

//		try {
			try {
				((DefaultJDBCDriver)this.dbDriver).load();
			} catch (ReadDriverException e1) {
				throw new LoadLayerException(this.getName(),e1);
			}
//		} catch (Exception e) {
//			this.setAvailable(false);
//			throw new DriverIOException(e);
//		}

//		try {
			VectorialDBAdapter dbAdapter = new VectorialDBAdapter();
			dbAdapter.setDriver(this.dbDriver);
			this.setSource(dbAdapter);

//		} catch (Exception e) {
//			this.setAvailable(false);
//			throw new DriverIOException(e);
//		}

//		try {
			try {
				this.putLoadSelection();
				this.putLoadLegend();
				this.initializeLegendDefault();
			} catch (XMLException e) {
				this.setAvailable(false);
				throw new XMLLayerException(this.getName(),e);
			} catch (LegendLayerException e) {
				this.setAvailable(false);
				throw new LegendLayerException(this.getName(),e);
			} catch (ReadDriverException e) {
				this.setAvailable(false);
				throw new LoadLayerException(this.getName(),e);
			}
//		} catch (Exception e) {
//			this.setAvailable(false);
//			//TODO: traducir???
//			throw new DriverIOException(e);
//		}
		this.cleanLoadOptions();
	}

    /* Esto deberia ir en FLyrVect */
	private void initializeLegendDefault() throws ReadDriverException, LegendLayerException {
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
        IProjection proj = null;

        if (xml.contains("proj")) {
            proj = CRSFactory.getCRS(xml.getStringProperty("proj"));
        }
        else {
            proj = this.getMapContext().getViewPort().getProjection();
        }

        this.setName(xml.getName());
        this.setProjection(proj);

        String driverName = xml.getStringProperty("db");
        IVectorialDatabaseDriver driver;

        try {
            driver = (IVectorialDatabaseDriver) LayerFactory.getDM()
                                                           .getDriver(driverName);

            //Hay que separar la carga de los datos del XMLEntity del load.
            driver.setXMLEntity(xml.getChild(2));
            this.setDriver(driver);
        }
        catch (Exception e) {
            throw new XMLException(e);
        }

        super.setXMLEntityNew(xml);
    }
}
