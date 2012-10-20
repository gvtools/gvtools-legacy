package org.gvsig.exceptions;

/**
 * 
 * @author Equipo de desarrollo de gvSIG
 * 
 */
public interface IExceptionTranslator {

	/**
	 * @param key
	 *            The key of the message error.
	 * @return The translated error message corresponding to the key that it
	 *         obtains as parameter.
	 */
	public String getText(String key);
}
