/*
 * Created on 10-nov-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * @author fjp
 * 
 */
public class VectorialDefaultAdapter extends VectorialAdapter {
	private SelectableDataSource ds;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.layers.VectorialAdapter#getRecordset()
	 */
	public SelectableDataSource getRecordset() throws ReadDriverException {
		if (getDriver() instanceof ObjectDriver) {
			if (ds == null) {
				String name = LayerFactory.getDataSourceFactory()
						.addDataSource((ObjectDriver) getDriver());
				try {
					ds = new SelectableDataSource(LayerFactory
							.getDataSourceFactory().createRandomDataSource(
									name, DataSourceFactory.AUTOMATIC_OPENING));
				} catch (NoSuchTableException e) {
					throw new RuntimeException(e);
				} catch (DriverLoadException e) {
					throw new ReadDriverException(name, e);
				}
			}
			return ds;
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#start()
	 */
	public void start() throws ReadDriverException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#stop()
	 */
	public void stop() throws ReadDriverException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public IGeometry getShape(int index) throws ReadDriverException {
		return getDriver().getShape(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShapeType()
	 */
	public int getShapeType() throws ReadDriverException {
		return getDriver().getShapeType();
	}

}
