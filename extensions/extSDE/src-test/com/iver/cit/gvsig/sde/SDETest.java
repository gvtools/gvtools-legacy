package com.iver.cit.gvsig.sde;

import java.sql.ResultSet;

import junit.framework.TestCase;

import com.esri.sde.sdk.client.SeException;
import com.iver.cit.gvsig.fmap.drivers.ConnectionFactory;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.sde.ArcSdeDriver;
import com.iver.cit.gvsig.fmap.drivers.sde.ConnectionSDE;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * Unit test of ArcSDE.
 *
 * @author Vicente Caballero Navarro
 *
 */
public class SDETest extends TestCase {
	ArcSdeDriver driver = new ArcSdeDriver();
	ResultSet rsGood = null;
	DBLayerDefinition lyrDef = new DBLayerDefinition();

	public SDETest() {
		super("ArcSDE unit tests");
	}
	protected void setUp() throws Exception {
		super.setUp();
			lyrDef.setUser("sde");
			lyrDef.setPassword("iver");
			lyrDef.setHost("192.168.0.114");
			lyrDef.setPort(5151);
			lyrDef.setName("MUNICIPIOSDEF");
			lyrDef.setTableName("MUNICIPIOSDEF");
			lyrDef.setDataBase("sde");
			lyrDef.setFieldGeometry("SHAPE");
			lyrDef.setFieldID("OBJECTID");
			lyrDef.setWhereClause("");
			lyrDef.setFieldNames(new String[] {"OBJECTID","COD_INE","COD_MUN"});
			lyrDef.setSRID_EPSG("23030");
	        lyrDef.setConnectionName("NOMBRE CONEXIÓN");
	        assertNotNull("driver null ", driver);
	        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
	    	extensionPoints.add("databaseconnections",ConnectionSDE.class.toString(),ConnectionSDE.class);
	    	String connectionStr=driver.getConnectionString(lyrDef.getHost(),String.valueOf(lyrDef.getPort()), lyrDef.getDataBase(),lyrDef.getUser(), lyrDef.getPassword());
			IConnection conn = ConnectionFactory.createConnection(connectionStr, lyrDef.getUser(),lyrDef.getPassword());
		    driver.setData(conn, lyrDef);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		driver.close();
	}
	public void testConnection(){

		assertTrue(driver.getConnection() instanceof ConnectionSDE);
	}
	public void testExtent() {
		assertNotNull("FullExtent",driver.getFullExtent());
	}
	public void testGetFieldValue() {
		for (int i=0; i < 3; i++){
			//String aux = driver.getFieldValue(i, 0).toString();
			Object id =  driver.getFieldValue(i, 0);
			System.out.println(id);
			assertNotNull(id);
		}
	}
	public void testLayers() throws SeException {
		assertTrue(((ConnectionSDE)driver.getConnection()).getConnection().getLayers().capacity()>0);
	}

}
