package com.hardcode.gdbms.engine.internalExceptions;

/**
 * timer executable task
 *
 * @author Fernando González Cortés
 */
public interface Task {
	/**
	 * Method called when the Timer timeouts
	 */
	public void execute();
}
