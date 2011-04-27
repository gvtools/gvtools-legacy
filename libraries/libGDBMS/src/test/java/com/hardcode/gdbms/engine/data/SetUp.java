package com.hardcode.gdbms.engine.data;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.PropertyConfigurator;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverManager;
import com.hardcode.driverManager.DriverValidation;
import com.hardcode.gdbms.engine.data.driver.DBDriver;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionCatcher;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionEvent;
import com.hardcode.gdbms.engine.internalExceptions.InternalExceptionListener;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

/**
 * @author Fernando González Cortés
 */
public class SetUp {

	private static void createDataBase() throws ClassNotFoundException, SQLException{
        Class.forName("org.hsqldb.jdbcDriver" );

        Connection c = java.sql.DriverManager.getConnection("jdbc:hsqldb:file:src/test/resources/testdb");
	        Statement st = c.createStatement();

            st.execute("DROP TABLE persona IF EXISTS CASCADE");
			
            st.execute("CREATE CACHED TABLE persona (id INTEGER, nombre VARCHAR(10), apellido VARCHAR(10), fecha DATE, tiempo TIME, marcatiempo TIMESTAMP, PK INTEGER, PRIMARY KEY(PK))");
            st.execute("INSERT INTO persona VALUES(0, 'fernando', 'gonzalez', '1980-9-5', '10:30:00' , '1980-09-05 10:30:00.666666666', 0)");
			st.execute("INSERT INTO persona VALUES(1, 'huracán', 'gonsales', '1980-9-5', '10:30:00' , '1980-09-05 10:30:00.666666666', 1)");
			st.execute("INSERT INTO persona VALUES(2, 'fernan', null, '1980-9-5', '10:30:00' , '1980-09-05 10:30:00.666666666', 2)");
			st.execute("SHUTDOWN");
			st.close();
			c.close();
	    
	}

    /**
     * @throws Exception
     * 
     */
    private static void createCSV() throws Exception {
        TestUtilities.copyFile(new File("src/test/resources/persona.backup.csv"), new File("src/test/resources/persona.csv"));
    }
    
	/**
	 * DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static DataSourceFactory setUp() throws Exception {
	    createDataBase();
	    
	    createCSV();
	    
		//Setup del log4j
		PropertyConfigurator.configure(SetUp.class.getClassLoader()
										   .getResource("log4j.properties"));

		//Setup de los drivers
		DriverManager dm = new DriverManager();
		dm.setValidation(new DriverValidation() {
				public boolean validate(Driver d) {
					return ((d instanceof ObjectDriver) ||
					(d instanceof FileDriver) ||
					(d instanceof DBDriver));
				}
			});
		dm.loadDrivers(new File("../fwAndami/gvSIG/extensiones/com.iver.cit.gvsig/drivers"));

		Throwable[] t = dm.getLoadFailures();
		if (t.length != 0) throw new RuntimeException(
		        t[0]);

		//Setup del factory de DataSources
        DataSourceFactory dsf = LayerFactory.getDataSourceFactory();
		dsf.setDriverManager(dm);

		//Setup de las tablas
		dsf.addFileDataSource("csv", "persona", "src/test/resources/persona.csv");
		dsf.addFileDataSource("csv", "sort", "src/test/resources/sort.csv");
		dsf.addDBDataSourceByTable("hsqldbpersona", null, 0, "sa", "", "src/test/resources/testdb",
				"persona", "GDBMS HSQLDB driver");
		dsf.addDBDataSourceByTable("hsqldbpersonatransactional", null, 0, "sa", "", "src/test/resources/testdb",
				"persona", "GDBMS HSQLDB Transactional driver");
		dsf.addDBDataSourceBySQL("hsqldbapellido", null, 0, "sa", "",
		        "src/test/resources/testdb", "select apellido from persona",
		        "GDBMS HSQLDB driver");
		dsf.addFileDataSource("csv", "nulos", "src/test/resources/nulos.csv");
		
		dsf.addSpatialDBDataSource("poligonos", "127.0.0.1", 5432, "root", "", "sigusal", "polygon0", "the_geom", "GDBMS PostGIS driver");
		
		dsf.addDBDataSourceByTable("foo", null, 0, null, null, null, null, "FooDriver");
		dsf.addSpatialFileDataSource("GDBMS shapefile driver", "shppuntos", "src/test/resources/puntos.shp");
		dsf.addSpatialFileDataSource("FMap ShapeFile Driver", "shplineas", "src/test/resources/lineas.shp");
		dsf.addSpatialFileDataSource("FMap ShapeFile Driver", "shppoligonos", "src/test/resources/poligonos.shp");
		dsf.addSpatialFileDataSource("FMap DXF Driver", "dxfprueba", "src/test/resources/dxfprueba.dxf");
		dsf.addDataSource(new FakeObjectDriver(), "objectpersona");
		
		dsf.initialize("/tmp");
		
		InternalExceptionCatcher.addInternalExceptionListener(new InternalExceptionListener() {
            /**
             * @see com.hardcode.gdbms.engine.internalExceptions.InternalExceptionListener#exceptionRaised(com.hardcode.gdbms.engine.internalExceptions.InternalExceptionEvent)
             */
            public void exceptionRaised(InternalExceptionEvent event) {
                throw new RuntimeException(event.getInternalException());
            }
        });
		
		return dsf;
	}


}
