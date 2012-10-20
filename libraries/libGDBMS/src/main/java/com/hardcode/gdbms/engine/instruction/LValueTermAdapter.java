/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;

/**
 * Adaptador
 * 
 * @author Fernando González Cortés
 */
public class LValueTermAdapter extends AbstractExpression {
	// private DataSource[] tables;
	// private DataSource source;
	private Field field = null;

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#getFieldName()
	 */
	public String getFieldName() {
		return Utilities.getText(getEntity());
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#simplify()
	 */
	public void simplify() {
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.CachedExpression#evaluate(long)
	 */
	public Value evaluate(long row) throws EvaluationException {
		try {
			return getField().evaluateExpression(row);
		} catch (ReadDriverException e) {
			throw new EvaluationException();
		} catch (SemanticException e) {
			throw new EvaluationException();
		}
	}

	/**
	 * Obtiene el campo al que referencia este adaptador
	 * 
	 * @return Field
	 * @throws AmbiguousFieldNameException
	 *             Si hay varios campos con el mismo nombre y no se especificó
	 *             la tabla
	 * @throws FieldNotFoundException
	 *             Si no hay ningún campo con ese nombre
	 * @throws SemanticException
	 *             Si existe algún error semántico en la instrucción
	 * @throws ReadDriverException
	 *             TODO
	 */
	private Field getField() throws AmbiguousFieldNameException,
			FieldNotFoundException, SemanticException, ReadDriverException {
		if (field == null) {
			field = FieldFactory.createField(getInstructionContext()
					.getFromTables(), getFieldName(), getInstructionContext()
					.getDs());
		}

		return field;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.CachedExpression#isLiteral()
	 */
	public boolean isLiteral() {
		return false;
	}

	/**
	 * @see com.hardcode.gdbms.engine.instruction.Expression#calculateLiteralCondition()
	 */
	public void calculateLiteralCondition() {
	}
}
