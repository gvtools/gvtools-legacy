package com.iver.cit.gvsig.fmap.drivers;

public class DBException extends Exception {
	private Exception e;

	public DBException(Exception e) {
		this.e = e;
	}

	public Throwable getCause() {
		return e.getCause();
	}

	public String getMessage() {
		return e.getMessage();
	}

	public void printStackTrace() {
		e.printStackTrace();
	}
}
