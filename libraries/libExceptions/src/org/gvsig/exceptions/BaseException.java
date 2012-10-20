package org.gvsig.exceptions;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * Esta clase esta pensada para actuar como clase base para las excepciones que
 * se lanzan dentro del proyecto de gvSIG.
 * 
 * Añade la implementacion necesaria para disponer de mensajes de error
 * internacionalizables, a traves del metodo getLocalizedMessage, asi como una
 * serie de metodos que nos permiten obtener los mesanes de error de la cadena
 * de excepciones enlazadas a traves de su "causa", asi como utilidades que
 * permitan recorrer de forma comoda esta cadena de excepciones por medio de un
 * Iterador.
 * 
 * @author Equipo de desarrollo de gvSIG.
 * 
 */
public abstract class BaseException extends Exception implements IBaseException {
	private final static String BLANKS = "                                                                                                     ";
	private static IExceptionTranslator translator = null;
	private static Logger logger = null;

	protected String messageKey;

	protected String formatString;

	/**
	 * Unique code of error.
	 */
	protected long code;

	/**
	 * Returns the format string received in the parameter with its keys
	 * replaced with the corresponding values of the map.
	 * 
	 * @param formatString
	 * @param values
	 *            map
	 * @return string formatted
	 */
	private String format(String formatString, Map values) {
		String key;
		String ret = formatString;
		if (formatString == null) {
			Logger lLogger = getLogger();
			lLogger.error(this.getClass().getName() + ": formatString is null.");
			if (values != null) {
				Iterator keys = values.keySet().iterator();
				ret = "values = { ";
				while (keys.hasNext()) {
					key = (String) keys.next();
					ret = ret.concat(key + ": " + (String) values.get(key)
							+ "; ");
				}
				ret = ret.concat(" }");
			}

			return ret;
		}
		if (values != null) {
			Iterator keys = values.keySet().iterator();
			while (keys.hasNext()) {
				key = (String) keys.next();
				ret = ret.replaceAll("%\\(" + key + "\\)",
						(String) values.get(key));
			}
		}
		return ret;
	}

	protected Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(BaseException.class.getName());
		}
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return format(this.formatString, values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getMessage(int)
	 */
	public String getMessage(int indent) {
		return insertBlanksAtStart(format(formatString, values()), indent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getLocalizedMessage()
	 */
	public String getLocalizedMessage() {
		return getLocalizedMessage(translator, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.exceptions.IBaseException#getLocalizedMessage(org.gvsig.exceptions
	 * .IExceptionTranslator, int)
	 */
	public String getLocalizedMessage(IExceptionTranslator translator,
			int indent) {

		String fmt;
		if (translator == null) {
			translator = BaseException.translator;
		}
		if (translator == null) {
			fmt = getFormatString();
		} else {
			fmt = getMessageKey();
			if (fmt == null) {
				fmt = getFormatString();
			} else {
				fmt = translator.getText(fmt);
			}
		}
		return insertBlanksAtStart(format(fmt, values()), indent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getMessageStack()
	 */
	public String getMessageStack() {
		return getMessageStack(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getMessageStack(int)
	 */
	public String getMessageStack(int indent) {
		Iterator iter = this.iterator();
		String msg = "";
		String msg1;
		Exception ex;
		int i = 1;
		while (iter.hasNext()) {
			ex = ((Exception) iter.next());
			if (ex instanceof BaseException) {
				BaseException bex = (BaseException) ex;
				msg1 = bex.getMessage(indent * i);
			} else {
				msg1 = insertBlanksAtStart(ex.getMessage(), indent * i);
			}
			if (msg1 != null && !msg1.equals("")) {
				if (msg.equals("")) {
					msg = msg1;
				} else {
					msg = msg + "\n" + msg1;
				}
			}
			i++;
		}
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getLocalizedMessageStack()
	 */
	public String getLocalizedMessageStack() {
		return getLocalizedMessageStack(BaseException.translator, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.exceptions.IBaseException#getLocalizedMessageStack(org.gvsig
	 * .exceptions.IExceptionTranslator, int)
	 */
	public String getLocalizedMessageStack(IExceptionTranslator translator,
			int indent) {
		Iterator iter = this.iterator();
		String msg = "";
		Exception ex;
		while (iter.hasNext()) {
			ex = ((Exception) iter.next());
			if (ex instanceof BaseException) {
				BaseException bex = (BaseException) ex;
				if (msg.equals("")) {
					msg = bex.getLocalizedMessage(translator, indent);
				} else {
					msg = msg
							+ "\n"
							+ bex.getLocalizedMessage(translator, indent)
									.trim();
				}
			} else {
				if (msg.equals("")) {
					msg = ex.getLocalizedMessage();
				} else {
					msg = msg + "\n" + ex.getLocalizedMessage();
				}
			}
		}
		return msg;
	}

	/**
	 * Inserts blanks at the start of a string.
	 * 
	 * @param str
	 *            A string.
	 * @param len
	 *            Quantity of blanks to insert at the start of str.
	 * @return A string compund by the quantity of blanks that len indicates and
	 *         str.
	 */
	static String insertBlanksAtStart(String str, int len) {
		try {
			return BLANKS.substring(0, len) + str;
		} catch (IndexOutOfBoundsException e) {
			return BLANKS + str;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getCode()
	 */
	public long getCode() {
		return this.code;
	}

	/**
	 * Sets the exception's code.
	 */
	public void setCode(long code) {
		this.code = code;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getFormatString()
	 */
	public String getFormatString() {
		return this.formatString;
	}

	/**
	 * Sets the format string.
	 * 
	 * @param formatString
	 */
	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#getMessageKey()
	 */
	public String getMessageKey() {
		return this.messageKey;
	}

	/**
	 * Sets the property messageKey.
	 * 
	 * @param messageKey
	 */
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.exceptions.IBaseException#iterator()
	 */
	public Iterator iterator() {
		return new BaseExceptionIterator(this);
	}

	/**
	 * @return A map that serves to replace in the format string the keys with
	 *         the corresponding values.
	 */
	abstract protected Map values();

	/**
	 * Sets the property translator.
	 * 
	 * @param translator
	 *            It(He,She) is used to translate the messages associated with
	 *            the exceptions.
	 */
	public static void setTranslator(IExceptionTranslator translator) {
		BaseException.translator = translator;
	}

	public static void setTranslator(Object translator) {
		BaseException.translator = new TranslatorWraper(translator);
	}

	public String toString() {
		return format(this.formatString, values());
	}

}

class TranslatorWraper implements IExceptionTranslator {

	private Object translator = null;
	private Method method = null;

	public TranslatorWraper(Object translator) {
		Class theClass = translator.getClass();
		String s = "";

		this.translator = translator;
		try {
			method = theClass
					.getMethod("getText", new Class[] { s.getClass() });
		} catch (Exception e) {
			throw new RuntimeException(
					"El objeto translator suministrado no tiene el metodo getText apropiado.",
					e);
		}

	}

	public String getText(String key) {
		try {
			return (String) (method.invoke(translator, new String[] { key }));
		} catch (Exception e) {
			return key;
		}
	}

}
