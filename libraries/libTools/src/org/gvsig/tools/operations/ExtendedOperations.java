package org.gvsig.tools.operations;


public interface ExtendedOperations {

	public Object invokeOperation(int code,
			OperationContext context)
	throws OperationException,
	OperationNotSupportedException;

	public Object invokeOperation(String name,
			OperationContext context)
	throws OperationException,
	OperationNotSupportedException;


	public Object getOperation(int code)
	throws OperationException,
	OperationNotSupportedException;

	public Object getOperation(String name)
	throws OperationException,
	OperationNotSupportedException;


	public boolean implementsOperation(int code);

	public boolean implementsOperation(String name);

}