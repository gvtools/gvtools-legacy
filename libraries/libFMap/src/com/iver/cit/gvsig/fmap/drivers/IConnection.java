package com.iver.cit.gvsig.fmap.drivers;

public interface IConnection {

	void close() throws DBException;

	boolean isClosed() throws DBException;

	String getCatalogName() throws DBException;

	String getNameServer() throws DBException;

	String getURL() throws DBException;

	void setDataConnection(String connectionStr, String user, String _pw)
			throws DBException;

	String getTypeConnection();

	String getIdentifierQuoteString();

}
