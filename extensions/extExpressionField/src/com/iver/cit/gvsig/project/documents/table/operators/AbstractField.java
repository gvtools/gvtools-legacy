package com.iver.cit.gvsig.project.documents.table.operators;

import java.util.Date;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.AbstractOperator;
import com.iver.cit.gvsig.project.documents.table.Index;

public abstract class AbstractField extends AbstractOperator {
	private boolean isEval = false;

	public Object getValue(String nameField, Index indexRow,
			SelectableDataSource sds) {
		try {
			int index = sds.getFieldIndexByName(nameField);
			Value value = sds.getFieldValue(indexRow.get(), index);
			if (value instanceof IntValue) {
				int iv = ((IntValue) value).intValue();
				return new Integer(iv);
			} else if (value instanceof NumericValue) {
				double dv = ((NumericValue) value).doubleValue();
				return new Double(dv);
			} else if (value instanceof DateValue) {
				Date date = ((DateValue) value).getValue();
				return date;
			} else if (value instanceof BooleanValue) {
				boolean b = ((BooleanValue) value).getValue();
				return new Boolean(b);
			} else {
				return value.toString();
			}
		} catch (ReadDriverException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void eval(BSFManager interpreter) throws BSFException {
		if (!isEval) {
			interpreter.declareBean("jfield", this, Field.class);
			interpreter
					.exec(ExpressionFieldExtension.JYTHON,
							null,
							-1,
							-1,
							"def field(nameField):\n"
									+ "  return jfield.getValue(nameField,indexRow,sds)");
			isEval = true;
		}
	}
}
