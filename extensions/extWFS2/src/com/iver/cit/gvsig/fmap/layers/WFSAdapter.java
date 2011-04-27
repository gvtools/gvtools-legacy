/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.layers;

import java.io.IOException;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.WFSDriver;


/**
 * Adapta un driver de WFS a la interfaz vectorial, manteniendo además el
 * estado necesario por una capa vectorial WFS (URL del host, estado del
 * protocolo)
 */
public class WFSAdapter extends VectorialAdapter {
	private SelectableDataSource ds = null;

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getRecordset()
	 */
	public SelectableDataSource getRecordset() {
		if (ds==null && driver instanceof WFSDriver)
		{
			String name = LayerFactory.getDataSourceFactory().addDataSource((ObjectDriver)driver);
			try {
				// ds = LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING);
				ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING));
			} catch (NoSuchTableException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}  catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (DriverLoadException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return ds;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#start()
	 */
	public void start() {
		try {
			((WFSDriver)driver).open();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#stop()
	 */
	public void stop() {
		((WFSDriver)driver).close();

	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public IGeometry getShape(int index) throws ReadDriverException {
		IGeometry geom = ((WFSDriver)driver).getShape(index);
		
		return geom;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShapeType()
	 */
	public int getShapeType() {
		return ((WFSDriver)driver).getShapeType();
	}

}
