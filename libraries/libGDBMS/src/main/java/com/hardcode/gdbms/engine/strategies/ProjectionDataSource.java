package com.hardcode.gdbms.engine.strategies;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.persistence.Memento;
import com.hardcode.gdbms.engine.data.persistence.MementoException;
import com.hardcode.gdbms.engine.data.persistence.OperationLayerMemento;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.values.Value;


/**
 * DataSource que añade características de proyección sobre campos al
 * DataSource subyacente.
 *
 * @author Fernando González Cortés
 */
public class ProjectionDataSource extends OperationDataSource {
	private DataSource source;
	private Expression[] fields;
	private String[] aliases;

	/**
	 * Creates a new ProjectionDataSource object.
	 *
	 * @param source DataSource origen de la información
	 * @param fields Con los índices de los campos proyectados
	 * @param aliases Nombres asignados en la instrucción a los campos
	 */
	public ProjectionDataSource(DataSource source, Expression[] fields,
		String[] aliases) {
		this.source = source;
		this.fields = fields;
		this.aliases = aliases;
	}

	/**
	 * Dado el índice de un campo en la tabla proyección, se devuelve el índice
	 * real en el DataSource subyacente
	 *
	 * @param index índice del campo cuyo índice en el DataSource subyacente se
	 * 		  quiere obtener
	 *
	 * @return índice del campo en el DataSource subyacente
	 */
	private Expression getFieldByIndex(int index) {
		return fields[index];
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public void stop() throws ReadDriverException {
		source.stop();
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public int getFieldCount() throws ReadDriverException {
		return fields.length;
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public int getFieldIndexByName(String fieldName) throws ReadDriverException {
		/*
		 * Se comprueba si dicho índice está mapeado o la ProjectionDataSource
		 * no lo tiene
		 */
		for (int i = 0; i < fields.length; i++) {
			if (fieldName.compareTo(fields[i].getFieldName()) == 0) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		if (aliases[fieldId] != null) {
			return aliases[fieldId];
		} else {
			String name = fields[fieldId].getFieldName();

			if (name == null) {
				return "unknown" + fieldId;
			} else {
				return name;
			}
		}
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
		throws ReadDriverException {
		try {
			return getFieldByIndex(fieldId).evaluate(rowIndex);
		} catch (EvaluationException e) {
			throw new ReadDriverException(getName(),e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public long getRowCount() throws ReadDriverException {
		return source.getRowCount();
	}

	/**
	 * @see com.hardcode.gdbms.data.DataSource#
	 */
	public void start() throws ReadDriverException {
		source.start();
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		throw new UnsupportedOperationException(
			"cannot get the field type of an expression");
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.DataSource#getMemento()
	 */
	public Memento getMemento() throws MementoException {
		return new OperationLayerMemento(getName(),
			new Memento[] { source.getMemento() }, getSQL());
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		throw new UnsupportedOperationException(
		"cannot get the field width of an expression");
	}
}
