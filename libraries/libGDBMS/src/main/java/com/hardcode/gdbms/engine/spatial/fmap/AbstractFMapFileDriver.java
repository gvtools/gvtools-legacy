/*
 * Created on 30-ago-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
USA.
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
package com.hardcode.gdbms.engine.spatial.fmap;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.SpatialFileDriver;
import com.hardcode.gdbms.engine.data.file.FileDataWare;
import com.hardcode.gdbms.engine.spatial.Geometry;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;
import com.iver.cit.gvsig.fmap.layers.VectorialFileAdapter;

public abstract class AbstractFMapFileDriver implements SpatialFileDriver {

	private VectorialAdapter adapter;
	private DataSource dataSource;

	/**
	 * @throws OpenDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#open(java.io.File)
	 */
	public void open(VectorialFileAdapter adapter) throws OpenDriverException {
		try {
			this.adapter = adapter;
			adapter.start();
			dataSource = adapter.getRecordset();
			dataSource.start();
		} catch (ReadDriverException e) {
			throw new OpenDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#close()
	 */
	public void close() throws CloseDriverException {
		try {
			adapter.stop();
			dataSource.stop();
		} catch (ReadDriverException e) {
			throw new CloseDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#writeFile(com.hardcode.gdbms.engine.data.file.FileDataWare,
	 *      java.io.File)
	 */
	public void writeFile(FileDataWare dataWare) throws WriteDriverException {
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.FileDriver#createSource(java.lang.String,
	 *      java.lang.String[], int[])
	 */
	public void createSource(String path, String[] fieldNames, int[] fieldTypes)
			throws ReadDriverException {
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return dataSource.getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return dataSource.getFieldCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return dataSource.getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return dataSource.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		return dataSource.getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException {
		return adapter.getFullExtent();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getGeometry(long)
	 */
	public Geometry getGeometry(long rowIndex) throws ReadDriverException {
		try {
			return new FMapGeometry(adapter.getShape((int) rowIndex));
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.SpatialDriver#getJTSGeometry(long)
	 */
	public com.vividsolutions.jts.geom.Geometry getJTSGeometry(long rowIndex)
			throws ReadDriverException {
		try {
			IGeometry ig = adapter.getShape((int) rowIndex);
			GeneralPathX gpx = new GeneralPathX();
			gpx.append(ig.getPathIterator(null), true);
			return FConverter.java2d_to_jts(new FShapeGeneralPathX(gpx, ig
					.getGeometryType()));
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	protected VectorialAdapter getAdapter() {
		return adapter;
	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldWidth(int)
	 */
	public int getFieldWidth(int i) throws ReadDriverException {
		return dataSource.getFieldWidth(i);
	}

}
