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
package es.prodevelop.cit.gvsig.jdbc_spatial;

import java.io.File;
import java.sql.Types;

import com.hardcode.gdbms.driver.dbf.DBFDriver;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.data.driver.GDBMSDriver;
import com.hardcode.gdbms.engine.data.driver.ReadAccess;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.file.AbstractFileDataSource;
import com.hardcode.gdbms.engine.data.file.FileDataSource;
import com.hardcode.gdbms.engine.data.file.FileDataSourceFactory;
import com.hardcode.gdbms.engine.data.file.FileDataWare;
import com.hardcode.gdbms.engine.data.file.FileSourceInfo;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 * This class is a simple FileDataSource adapter used to create a data source
 * based on a DBF file which contains the oracle <--> epsg codes.
 * 
 * @author jldominguez
 * 
 */
public class UtilityFileDataSourceAdapter extends AbstractFileDataSource
		implements FileDataSource {
	private File file;
	private DBFDriver driver;
	private int sem = 0;
	private int fieldCount = -1;

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#start()
	 */
	public void start() throws ReadDriverException {
		try {
			if (sem == 0) {
				driver.open(file);
			}

			sem++;
		} catch (OpenDriverException e) {
			throw new ReadDriverException(driver.getName(), e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#stop()
	 */
	public void stop() throws ReadDriverException {
		try {
			sem--;

			if (sem == 0) {
				driver.close();
			} else if (sem < 0) {
				throw new RuntimeException("DataSource closed too many times");
			}
		} catch (CloseDriverException e) {
			throw new ReadDriverException(driver.getName(), e);
		}
	}

	/**
	 * Asigna el driver al adaptador
	 * 
	 * @param driver
	 *            The driver to set.
	 */
	public void setDriver(FileDriver driver) {
		this.driver = (DBFDriver) driver;
	}

	/**
	 * Sets the source information of the DataSource
	 * 
	 * @param sourceInfo
	 *            The file to set.
	 */
	public void setSourceInfo(SourceInfo sourceInfo) {
		super.setSourceInfo(sourceInfo);
		file = new File(((FileSourceInfo) sourceInfo).file);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDriver()
	 */
	public ReadAccess getReadDriver() {
		return driver;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getName()
	 */
	public String getName() {
		return sourceInfo.name;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws ReadDriverException {
		// The last field is the pk/row in FileDataSources
		return new int[] { getFieldCount() - 1 };
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		if (fieldCount == -1) {
			fieldCount = getReadDriver().getFieldCount() + 1;
		}

		return fieldCount;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		// last field is the virtual primary key
		if (fieldId == (getFieldCount() - 1)) {
			return "PK";
		}

		return getReadDriver().getFieldName(fieldId);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		// last field is the virtual primary key
		if (i == (getFieldCount() - 1)) {
			return Types.BIGINT;
		}

		return getReadDriver().getFieldType(i);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		// last field is the virtual primary key
		if (fieldId == (getFieldCount() - 1)) {
			return ValueFactory.createValue(rowIndex);
		}

		Value v = getReadDriver().getFieldValue(rowIndex, fieldId);

		return (v == null) ? ValueFactory.createNullValue() : v;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.file.FileDataSource#getDriver()
	 */
	public DBFDriver getDriver() {
		return driver;
	}

	/**
	 * @throws DriverException
	 * @see com.hardcode.gdbms.engine.data.DataSource#getDataWare(int)
	 */
	public DataWare getDataWare(int mode) throws ReadDriverException {
		FileDataWare dw = FileDataSourceFactory.newDataWareInstance();
		FileDriver driver;
		driver = new DBFDriver();
		((GDBMSDriver) driver).setDataSourceFactory(getDataSourceFactory());
		dw.setDriver(driver);
		dw.setDataSourceFactory(dsf);
		dw.setSourceInfo(getSourceInfo());

		return dw;
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return getReadDriver().getFieldWidth(i);
	}

	public boolean isVirtualField(int fieldId) throws ReadDriverException {
		// last field is the virtual primary key
		if (fieldId == (this.getFieldCount() - 1)) {
			return true;
		}

		return false;
	}

	public void reload() throws ReloadDriverException {
		try {
			sem = 0;
			driver.close();
			fieldCount = -1;
			start();
		} catch (Exception e) {
			throw new ReloadDriverException(driver.getName(), e);
		}

		raiseEventReloaded();
	}
}
