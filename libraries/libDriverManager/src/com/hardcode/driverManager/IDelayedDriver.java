package com.hardcode.driverManager;

/**
 * @author fjp
 * 
 * 
 *         Use it to know when a driver has finished to load its data. Useful if
 *         you need a fast adding and you don't care about rendering. When the
 *         driver finish to load, it will send a DriverEvent. Use it to refresh
 *         Mapcontrol, for example.
 */
public interface IDelayedDriver {
	public void addDriverEventListener(DriverEventListener listener);

	public void removeDriverEventListener(DriverEventListener listener);
}
