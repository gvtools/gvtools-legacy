package com.iver.cit.gvsig.fmap.edition.commands;

/**
 * <p>
 * Interface for adding support to an editable object to be repainted or
 * refreshed, as consequence of a command of edition operation executed.
 * </p>
 */
public interface CommandListener {
	/**
	 * <p>
	 * Updates visible graphical components.
	 * </p>
	 */
	public void commandRepaint();

	/**
	 * <p>
	 * Updates information.
	 * </p>
	 */
	public void commandRefresh();
}
