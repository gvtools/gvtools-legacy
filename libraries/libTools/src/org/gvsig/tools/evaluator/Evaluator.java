package org.gvsig.tools.evaluator;

public interface Evaluator {

	/**
	 * Evalue with the data suministred as parameter.
	 *
	 * @param data
	 * @return the result of the evaluation.
	 */
	public Object evaluate(EvaluatorData data);

	/**
	 * Get the simbolic name of the evaluator.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Get a description of the action performed with the evaluator.
	 *
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Get an CQL representation of the evaluator.
	 * 
	 * @return the CQL string or null if not supported.
	 */
	public String getCQL();

	/**
	 * Get an array of match values for the name in the evaluation.
	 *
	 * @param name
	 * @return An array of match values or null if not use name in the
	 *         evaluation or if not is supported this method.
	 */
	public EvaluatorFieldValue[] getFieldValues(String name);
}
