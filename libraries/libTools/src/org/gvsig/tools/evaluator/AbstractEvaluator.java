package org.gvsig.tools.evaluator;

public abstract class AbstractEvaluator implements Evaluator {

	public String getDescription() {
		return "";
	}

	public String getCQL() {
		return null;
	}

	public EvaluatorFieldValue[] getFieldValues(String name) {
		return null;
	}
}
