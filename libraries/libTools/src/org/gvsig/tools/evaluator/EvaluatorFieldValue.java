package org.gvsig.tools.evaluator;

public class EvaluatorFieldValue {

	final public static int MATCH = 0;
	final public static int RANGE = 1;
	final public static int NEAREST = 2;

	private int type;
	private Object value1;
	private Object value2;
	private int count;
	private int tolerance;
	private String fieldName;

	public EvaluatorFieldValue(String fieldName, Object value) {
		this.fieldName = fieldName;
		this.type = MATCH;
		this.value1 = value;
		this.value2 = null;
		this.count = -1;
		this.tolerance = -1;
	}

	public EvaluatorFieldValue(String fieldName, Object value1, Object value2) {
		this.fieldName = fieldName;
		this.type = RANGE;
		this.value1 = value1;
		this.value2 = value2;
		this.count = -1;
		this.tolerance = -1;
	}

	public EvaluatorFieldValue(String fieldName, int count, int tolerance,
			Object value) {
		this.fieldName = fieldName;
		this.type = NEAREST;
		this.count = count;
		this.tolerance = tolerance;
		this.value1 = value;
		this.value2 = null;
	}

	public EvaluatorFieldValue(String fieldName, int count, Object value) {
		this.fieldName = fieldName;
		this.type = NEAREST;
		this.count = count;
		this.tolerance = -1;
		this.value1 = value;
		this.value2 = null;
	}

	/**
	 * Get the type of operation realiced over the field.
	 * 
	 * The posibles values are: MATCH RANGE or NEAREST
	 * 
	 * @return an int with the type of operation
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Get the fieldName over the operation is realiced.
	 * 
	 * @return String name of field.
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * Get the count used in the nearest operation.
	 * 
	 * @return the count or -1 if not aplicable.
	 */
	public int getCount() {
		return this.count;
	}

	/**
	 * Get the tolerance used in the nearest operation.
	 * 
	 * @return the tolerance or -1 if not aplicable.
	 */
	public int getTolerance() {
		return this.tolerance;
	}

	/**
	 * Get the value used in the match or nearest operation.
	 * 
	 * @return the match value or null if not aplicable.
	 */
	public Object getValue() {
		return this.value1;
	}

	/**
	 * Get the initial value used in the range operation.
	 * 
	 * @return the first value or null if not aplicable.
	 */
	public Object getValue1() {
		return this.value1;
	}

	/**
	 * Get the final value used in the range operation.
	 * 
	 * @return the final value or null if not aplicable.
	 */
	public Object getValue2() {
		return this.value2;
	}
}
