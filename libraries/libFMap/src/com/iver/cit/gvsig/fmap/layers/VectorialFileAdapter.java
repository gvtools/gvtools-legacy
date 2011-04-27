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

import java.io.File;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.CloseDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ExternalData;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;


/**
 * Adapta un driver de fichero vectorial a la interfaz vectorial, manteniendo
 * además el estado necesario por una capa vectorial de fichero (el nombre del
 * fichero)
 */
public class VectorialFileAdapter extends VectorialAdapter {
	private boolean driverInitialized = false;
	private File file;
	private SelectableDataSource ds;
	private String dataSourceName;

	/**
	 * <code>reference_count</code> lleva un contador de referencias a este
	 * adaptador, de forma que si la cuenta de referencias no es cero, no
	 * abrimos otra vez el adaptador porque se supone que está abierto.
	 */
	private int reference_count = 0;

	/**
	 * Crea un nuevo VectorialFileAdapter.
	 *
	 * @param file Fichero.
	 */
	public VectorialFileAdapter(File file) {
		this.file = file;
	}

	/**
	 * Devuelve driver.
	 *
	 * @return VectorialFileDriver.
	 */
	VectorialFileDriver getFileDriver() {
		return (VectorialFileDriver) getDriver();
	}

	/**
	 * incrementa el contador de las veces que se ha abierto el fichero.
	 * Solamente cuando el contador está a cero pide al driver que abra el
	 * fichero
	 * @throws InitializeDriverException
	 */
	public synchronized void start() throws ReadDriverException, InitializeDriverException {
		    if (reference_count == 0)
		    {
				getFileDriver().open(file);

				if (!driverInitialized) {
					getFileDriver().initialize();
					driverInitialized = true;
				}
				getRecordset().start();
		    }
			reference_count++;
	}

	/**
	 * decrementa el contador de número de aperturas y cuando llega a cero pide
	 * al driver que cierre el fichero
	 */
	public synchronized void stop() throws ReadDriverException {
		try {
		    if (reference_count == 0)
		    {
		        getFileDriver().close();
		    }
		    else
		        if (reference_count < 0)
		            throw new RuntimeException("Contador de referencias de driver ="
		                    + reference_count + ". Demasiados stop().");
	    	reference_count--;
		} catch (CloseDriverException e) {
			throw new ReadDriverException(getDriver().getName(),e);
		}
	}

	/**
	 * Is synchronized to allow thread safe access to features stored
	 * in files.
	 *
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShape(int)
	 */
	public synchronized IGeometry getShape(int index) throws ReadDriverException {
		return getFileDriver().getShape(index);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.ReadableVectorial#getShapeType()
	 */
	public int getShapeType() throws ReadDriverException {
		return getFileDriver().getShapeType();
	}

	/**
	 * @throws ReadDriverException
	 * @see com.iver.cit.gvsig.fmap.layers.VectorialAdapter#getRecordset()
	 */
	public SelectableDataSource getRecordset() throws ReadDriverException {
		String name =null;
		try {
			if (ds == null) {
				VectorialFileDriver driver = (VectorialFileDriver) getDriver();

				if (driver instanceof ExternalData) {
					ExternalData ed = (ExternalData) driver;
					File dataFile = ed.getDataFile(file);
					String driverName = ed.getDataDriverName();

					name = LayerFactory.getDataSourceFactory().addFileDataSource(driverName,
						dataFile.getAbsolutePath());
					ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING));
				} else if (driver instanceof ObjectDriver) {
					name = LayerFactory.getDataSourceFactory().addDataSource((ObjectDriver)driver);
					ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING));
				} else {
					return null;
				}
			}
		} catch (NoSuchTableException e) {
			throw new RuntimeException(
				"Error de implementación, se ha añadido una tabla y luego esa tabla no ha podido ser cargada");
		} catch (DriverLoadException e) {
			throw new ReadDriverException(name,e);
		}

		return ds;
	}

	/**
	 * Devuelve el fichero.
	 *
	 * @return Fichero.
	 */
	public File getFile() {
		return file;
	}
	/**
	 * Returns the feature whose index is numReg
	 * <br>
	 * Is synchronized to do thread safe accessing to features
	 * stored in files.
	 * @param numReg index of feature
	 * @return feature
	 *
	 */
    public synchronized IFeature getFeature(int numReg) throws ReadDriverException
    {
        IGeometry geom;
        IFeature feat = null;
        geom = getShape(numReg);
        DataSource rs = getRecordset();
        int fieldCount=rs.getFieldCount();
        Value[] regAtt = new Value[fieldCount];

        for (int fieldId=0; fieldId < fieldCount; fieldId++ )
        {
            regAtt[fieldId] =  rs.getFieldValue(numReg, fieldId);
        }
        feat = new DefaultFeature(geom, regAtt, "" + numReg);
        return feat;
    }

}
