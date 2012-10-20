package com.iver.cit.gvsig.fmap.edition.commands;

import com.iver.cit.gvsig.fmap.FMapEvent;

/**
 * <p>
 * Event produced when a new command is invoked.
 * </p>
 */
public class CommandEvent extends FMapEvent {
	/**
	 * <p>
	 * Reference to the new command.
	 * </p>
	 */
	private Command command;

	/**
	 * <p>
	 * Creates a new command event.
	 * </p>
	 * 
	 * @param command
	 *            the new comman
	 */
	public CommandEvent(Command command) {
		this.command = command;
	}

	/**
	 * <p>
	 * Gets the new command.
	 * </p>
	 * 
	 * @return the new command
	 */
	public Command getCommand() {
		return command;
	}
}
