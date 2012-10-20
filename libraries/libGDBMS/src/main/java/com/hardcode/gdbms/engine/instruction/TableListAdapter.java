/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import java.util.ArrayList;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;

/**
 * Adaptador
 * 
 * @author Fernando González Cortés
 */
public class TableListAdapter extends Adapter {
	private DataSource[] tables;

	/**
	 * Obtiene los DataSources de la cláusula from
	 * 
	 * @return array de datasources
	 * 
	 * @throws TableNotFoundException
	 *             Si no se encontró alguna tabla
	 * @throws RuntimeException
	 */
	public DataSource[] getTables() throws TableNotFoundException {
		if (tables == null) {
			Adapter[] hijos = getChilds();
			ArrayList ret = new ArrayList();

			try {
				for (int i = 0; i < hijos.length; i++) {
					TableRefAdapter tRef = (TableRefAdapter) hijos[i];

					if (tRef.getAlias() == null) {
						ret.add(getInstructionContext().getDSFactory()
								.createRandomDataSource(tRef.getName(),
										DataSourceFactory.MANUAL_OPENING));
					} else {
						ret.add(getInstructionContext().getDSFactory()
								.createRandomDataSource(tRef.getName(),
										tRef.getAlias(),
										DataSourceFactory.MANUAL_OPENING));
					}
				}

				tables = (DataSource[]) ret.toArray(new DataSource[0]);
			} catch (NoSuchTableException e) {
				throw new TableNotFoundException();
			} catch (DriverLoadException e) {
				throw new RuntimeException(e);
			} catch (ReadDriverException e) {
				throw new RuntimeException(e);
			}
		}

		return tables;
	}
}
