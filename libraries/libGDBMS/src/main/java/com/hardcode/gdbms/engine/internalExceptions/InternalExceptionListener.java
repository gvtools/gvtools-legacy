package com.hardcode.gdbms.engine.internalExceptions;

/**
 * @author Fernando González Cortés
 */
public interface InternalExceptionListener {
	public void exceptionRaised(InternalExceptionEvent event);
}
