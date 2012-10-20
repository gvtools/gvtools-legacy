package org.gvsig.exceptions;

import java.util.Iterator;

/**
 * 
 * 
 * @author Equipo de desarrollo de gvSIG
 * 
 */
public interface IBaseException {

	/**
	 * Returns the message that describes the exception.
	 * 
	 * @return The message.
	 */
	public String getMessage();

	/**
	 * Returns the message that describes the exception, with indentation.
	 * 
	 * @param indent
	 *            Quantity of blanks to insert at the start of the message.
	 * @return The message with indentation.
	 */
	public String getMessage(int indent);

	/**
	 * Returns the translated message that describes the exception.
	 * 
	 * @return The translated message with indentation.
	 */
	public String getLocalizedMessage();

	/**
	 * Returns the translated message that describes the exception with
	 * indentation.
	 * 
	 * @param translator
	 *            Instance of a class that fulfills the IExceptionTranslator
	 *            interface. His method "getText" takes charge returning the
	 *            expression, correspondent to the key that delivers him,
	 *            translated into the configured language.
	 * @param indent
	 *            Quantity of blanks to insert at the start of the message.
	 * @return The translated message with indentation.
	 */
	public String getLocalizedMessage(IExceptionTranslator translator,
			int indent);

	/**
	 * Crosses the exceptions chained through cause to conform the message.
	 * 
	 * @return The compound message with all the messages of the stack of
	 *         exceptions.
	 */
	public String getMessageStack();

	/**
	 * Crosses the exceptions chained through cause to conform the compound
	 * message with indentation.
	 * 
	 * @param indent
	 *            Quantity of blanks to insert at the start of the messages.
	 * @return The compound message with all the messages of the stack of
	 *         exceptions.
	 */
	public String getMessageStack(int indent);

	/**
	 * Crosses the exceptions chained through cause to conform the compound
	 * message in the corresponding language.
	 * 
	 * @return The translated compound message.
	 * 
	 */
	public String getLocalizedMessageStack();

	/**
	 * Crosses the exceptions chained through cause to conform the compound
	 * message in the corresponding language.
	 * 
	 * @param translator
	 *            Instance of a class that fulfills the IExceptionTranslator
	 *            interface. His method "getText" takes charge returning the
	 *            expression, correspondent to the key that delivers him,
	 *            translated into the configured language.
	 * @param indent
	 *            Quantity of blanks to insert at the start of the messages.
	 * @return The translated message with indentation.
	 * 
	 */
	public String getLocalizedMessageStack(IExceptionTranslator translator,
			int indent);

	/**
	 * @return The exception's code.
	 */
	public long getCode();

	/**
	 * @return The format string.
	 */
	public String getFormatString();

	/**
	 * @return The message key associated to the exception.
	 */
	public String getMessageKey();

	/**
	 * @return A iterator for the chained exceptions.
	 */
	public Iterator iterator();

}