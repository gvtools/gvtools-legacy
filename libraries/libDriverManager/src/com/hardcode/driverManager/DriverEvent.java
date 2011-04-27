package com.hardcode.driverManager;

public class DriverEvent {
	public final static int DRIVER_EVENT_LOADING_START = 0;

	public final static int DRIVER_EVENT_LOADING_END = 1;
	
	private int type;

	public DriverEvent(int _type) {
		type = _type;
	}

	public int getType() {
		return type;
	}
}
