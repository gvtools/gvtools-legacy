package org.gvsig.exceptions;

import java.util.HashMap;
import java.util.Map;

public class DriverException extends BaseException {
	
	private static final long serialVersionUID = -8985920349210629999L;
	private String driverName;
	
	public DriverException() {
		init();
	}
	
	public DriverException(String driverName) {
		init();
		this.driverName = driverName;
	}
	
	public DriverException(String driverName, Throwable cause) {
		init();
		this.driverName = driverName;
		initCause(cause);
	}

	public void init() {
		messageKey="Error_in_the_driver_%(driverName)s";
		formatString="Error in the driver %(driverName)s";
		code = serialVersionUID;
	}

	
	public String getDriverName() {
		return driverName;
	}
	
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	protected Map values() {
		HashMap values = new HashMap();
		values.put("driverName",this.driverName);
		return values;
	}
}
