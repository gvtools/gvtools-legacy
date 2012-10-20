package com.hardcode.gdbms.engine.instruction;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.parser.ParseException;

/**
 * Adaptador de la instrucción UNION
 * 
 * @author Fernando González Cortés
 */
public class UnionAdapter extends Adapter {
	/**
	 * DOCUMENT ME!
	 * 
	 * @param table
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * @throws DriverLoadException
	 * @throws ParseException
	 * @throws SemanticException
	 * @throws EvaluationException
	 * @throws ReadDriverException
	 *             TODO
	 * @throws RuntimeException
	 *             DOCUMENT ME!
	 * @throws IllegalStateException
	 *             DOCUMENT ME!
	 */
	private DataSource getTable(int table) throws DriverLoadException,
			ParseException, SemanticException, EvaluationException,
			ReadDriverException {
		Adapter hijo = getChilds()[table];

		if (hijo instanceof TableRefAdapter) {
			String name = Utilities.getText(hijo.getEntity());

			return getTableByName(name);
		} else if (hijo instanceof SelectAdapter) {
			return getTableBySelect((SelectAdapter) hijo);
		} else {
			throw new IllegalStateException("Cannot create the DataSource");
		}
	}

	/**
	 * @throws DriverLoadException
	 * @throws ParseException
	 * @throws SemanticException
	 * @throws EvaluationException
	 * @throws ReadDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.instruction.UnionInstruction#getFirstTable()
	 */
	public DataSource getFirstTable() throws DriverLoadException,
			ParseException, SemanticException, EvaluationException,
			ReadDriverException {
		return getTable(0);
	}

	/**
	 * Obtiene el data source a partir de una select
	 * 
	 * @param select
	 * 
	 * @return
	 * @throws ParseException
	 * @throws DriverLoadException
	 * @throws SemanticException
	 * @throws EvaluationException
	 * @throws ReadDriverException
	 *             TODO
	 */
	private DataSource getTableBySelect(SelectAdapter select)
			throws ParseException, DriverLoadException, SemanticException,
			EvaluationException, ReadDriverException {
		return getInstructionContext().getDSFactory().createRandomDataSource(
				select, DataSourceFactory.MANUAL_OPENING);
	}

	/**
	 * Obtiene un data source por el nombre
	 * 
	 * @param name
	 * 
	 * @return
	 * 
	 * @throws TableNotFoundException
	 *             Si nop hay ninguna tabla con el nombre 'name'
	 * @throws RuntimeException
	 */
	private DataSource getTableByName(String name)
			throws TableNotFoundException {
		String[] tabla = name.split(" ");

		try {
			if (tabla.length == 1) {
				return getInstructionContext().getDSFactory()
						.createRandomDataSource(name,
								DataSourceFactory.MANUAL_OPENING);
			} else {
				return getInstructionContext().getDSFactory()
						.createRandomDataSource(tabla[0], tabla[1],
								DataSourceFactory.MANUAL_OPENING);
			}
		} catch (DriverLoadException e) {
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			throw new TableNotFoundException();
		} catch (ReadDriverException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @throws DriverLoadException
	 * @throws ParseException
	 * @throws SemanticException
	 * @throws EvaluationException
	 * @throws ReadDriverException
	 *             TODO
	 * @see com.hardcode.gdbms.engine.instruction.UnionInstruction#getSecondTable()
	 */
	public DataSource getSecondTable() throws DriverLoadException,
			ParseException, SemanticException, EvaluationException,
			ReadDriverException {
		return getTable(1);
	}
}
