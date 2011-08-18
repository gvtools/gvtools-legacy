package com.iver.cit.gvsig.vectorialdb;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;


/**
 * @author Fernando González Cortés
 */
public class ConnectionSettings {
    private String host;
    private String port;
    private String db;
    private String schema;
    private String driver;
    private String user;
    private String name;
    private String passw;

    public String getDb() {
        return db;
    }
    public void setDb(String db) {
        this.db = db;
    }
    public String getDriver() {
        return driver;
    }
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public String toString(){
        return host+","+port+","+db+","+driver+","+user+","+schema+","+name;
    }

    public void setFromString(String str) {
        String[] values = str.split(",");
        host = values[0];
        port = values[1];
        db = values[2];
        driver = values[3];
        user = values[4];
        schema = values[5];
        name = values[6];
        if (values.length == 8)
            passw = values[7];
    }
    public String getPassw() {
        return passw;
    }
    public void setPassw(String passw) {
        this.passw = passw;
    }
    public String getConnectionString() throws DriverLoadException
    {
        IVectorialDatabaseDriver vecDriver = (IVectorialDatabaseDriver) LayerFactory.getDM().getDriver(getDriver());
        String connectionString = vecDriver.getConnectionStringBeginning() + "//" + getHost();

        connectionString += (":" + getPort());

        connectionString += ("/" + getDb());

        return connectionString;
    }
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
}

// [eiel-gestion-conexiones]