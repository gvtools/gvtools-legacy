package org.gvsig.tools.evaluator;

import java.util.Iterator;

public interface EvaluatorData {

	public Object getDataValue(String name);

	public Object getContextValue(String name);

	public Iterator getDataValues();

	public Iterator getDataNames();
}
