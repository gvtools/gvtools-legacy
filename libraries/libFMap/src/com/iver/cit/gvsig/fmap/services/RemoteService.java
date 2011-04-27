/*
 * Created on 30-may-2005
 */
package com.iver.cit.gvsig.fmap.services;

import java.io.IOException;

import com.hardcode.gdbms.engine.data.driver.DriverException;

/**
 * @author luisw
 */
public interface RemoteService {
//	private String serviceName;
//	private String host;
//	private int port;
//	
//	public RemoteService(String name, String host, int port){
//		serviceName = name;
//		this.host = host;
//		this.port = port;
//	}
	public void setServiceName(String serviceName);
	public String getServiceName(); //{
//		return serviceName;
//	}
	public void setHost(String hostName) throws IOException ;
	public String getHost(); //{
//		return host;
//	}
	public void setPort(int portNr); //{
	public int getPort(); //{
//		return port;
//	}
	public void connect() throws IOException, DriverException;
	public boolean isConnected();	
	public void close();
}
