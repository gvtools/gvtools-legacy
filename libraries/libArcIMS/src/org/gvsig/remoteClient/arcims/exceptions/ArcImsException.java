package org.gvsig.remoteClient.arcims.exceptions;

/**
 * ArcIms specific exception, @see Exception
 */
public class ArcImsException extends Exception 
{	
	static final long serialVersionUID = 0;
	
	private String arcims_message = null;
	
	/**
	 *
	 */
	public ArcImsException() {
		super();
	}

	/**
	 * Creates an ArcimsException
	 *
	 * @param message
	 */
	public ArcImsException(String message) {
		super(message);
	}

	/**
	 * Creates an ArcimsException
	 *
	 * @param message
	 * @param cause
	 */
	public ArcImsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	  * Creates an ArcimsException
	 *
	 * @param cause
	 */
	public ArcImsException(Throwable cause) {
		super(cause);
	}
	
	public String getArcImsMessage()
	{
		if (arcims_message == null)
			return "";
		else
			return arcims_message;
	}
	
	public void setArcImsMessage(String mes)
	{
		arcims_message = mes;
	}
}
