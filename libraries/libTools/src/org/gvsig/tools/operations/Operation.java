package org.gvsig.tools.operations;

public abstract class Operation {

	/**
	 * Invokes this operation in context ctx
	 * 
	 * @param self
	 *            the object to which apply this operation
	 * @param ctx
	 *            Parameter container
	 * @return Place-holder object that may contain any specific return value.
	 * @throws OperationException
	 *             The implementation is responsible to throw this exception
	 *             when needed.
	 */
	public abstract Object invoke(Object self, OperationContext ctx)
			throws OperationException;

	/**
	 * Returns the constant value that identifies this operation and that was
	 * obtained upon registering it.
	 * 
	 * @return operation unique index
	 */
	public abstract int getOperationIndex();

}
