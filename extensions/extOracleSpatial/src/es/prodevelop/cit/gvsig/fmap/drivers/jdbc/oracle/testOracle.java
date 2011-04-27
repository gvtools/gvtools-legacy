/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import java.awt.geom.PathIterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.STRUCT;

import org.cresques.cts.IProjection;

import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WKBParser2;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.oracle.OraReader;


/**
 * @author FJP
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class testOracle {
    private static boolean addOrdsToGPX(GeneralPathX gpx, int zero_based_start,
        int zero_based_include_end, Datum[] ords, int d, int ltype,
        boolean do_the_move, boolean must_do_first) {
        int length = ords.length;
        boolean return_following_must_do_first = true;

        double x = ((NUMBER) ords[zero_based_start]).doubleValue();
        double y = ((NUMBER) ords[zero_based_start + 1]).doubleValue();

        if (must_do_first) {
            if (do_the_move) {
                gpx.moveTo(x, y);
            }
            else {
                gpx.lineTo(x, y);
            }
        }

        int ind = 1;

        int size = ((zero_based_include_end - zero_based_start) / d) + 1;
        int indx;
        int indx2;

        if (ltype == PathIterator.SEG_QUADTO) { // (interpretation = 2)

            double x2;
            double y2;

            while (ind < size) {
                indx = zero_based_start + (ind * d);
                x = ((NUMBER) ords[indx]).doubleValue();
                y = ((NUMBER) ords[indx + 1]).doubleValue();

                indx2 = zero_based_start + ((ind + 1) * d);

                if (indx >= length) {
                    indx2 = zero_based_start;
                }

                x2 = ((NUMBER) ords[indx2]).doubleValue();
                y2 = ((NUMBER) ords[indx2 + 1]).doubleValue();
                gpx.quadTo(x, y, x2, y2);
                ind++;
                ind++;
            }

            return_following_must_do_first = false;
        }
        else { // PathIterator.SEG_LINETO (interpretation = 1)

            while (ind < size) {
                indx = zero_based_start + (ind * d);
                x = ((NUMBER) ords[indx]).doubleValue();
                y = ((NUMBER) ords[indx + 1]).doubleValue();
                gpx.lineTo(x, y);
                ind++;
            }
        }

        return return_following_must_do_first;
    }

    private static int getLineToType(Datum[] infos, int i) {
        int resp = PathIterator.SEG_LINETO;

        try {
            if (((NUMBER) infos[(3 * i) + 2]).intValue() == 2) {
                resp = PathIterator.SEG_QUADTO;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return resp;
    }

    // CREATE TYPE sdo_geometry AS OBJECT (
    // SDO_GTYPE NUMBER,
    // SDO_SRID NUMBER,
    // SDO_POINT SDO_POINT_TYPE,
    // SDO_ELEM_INFO SDO_ELEM_INFO_ARRAY,
    // SDO_ORDINATES SDO_ORDINATE_ARRAY);
    private static GeneralPathX parseOracleStruct(STRUCT s)
        throws SQLException {
        GeneralPathX resp = new GeneralPathX();
        ARRAY infoARRAY = null;
        ARRAY ordsARRAY = null;
        Datum[] info_array = null;
        Datum[] ords_array = null;
        int info_array_size = 0;
        int[] start_ind;
        int[] end_ind;
        int dims = 0;
        boolean next_must_do_first = true;
        Datum[] aux = s.getOracleAttributes();

        infoARRAY = (ARRAY) aux[3];
        ordsARRAY = (ARRAY) aux[4];

        dims = ((NUMBER) aux[0]).intValue() / 1000;

        if (dims == 0) {
            dims = 2;
        }

        info_array = (Datum[]) infoARRAY.getOracleArray();
        ords_array = (Datum[]) ordsARRAY.getOracleArray();
        info_array_size = info_array.length / 3;

        int last_index = ords_array.length - dims + 1;

        // set indices:
        start_ind = new int[info_array_size];
        end_ind = new int[info_array_size];

        for (int i = 0; i < info_array_size; i++)
            start_ind[i] = ((NUMBER) info_array[3 * i]).intValue();

        for (int i = 0; i < (info_array_size - 1); i++)
            end_ind[i] = start_ind[i + 1] - 1;

        end_ind[info_array_size - 1] = last_index;

        int lineType = PathIterator.SEG_LINETO;

        if (end_ind[0] == 0) { // collection of paths

            for (int i = 1; i < info_array_size; i++) {
                lineType = getLineToType(info_array, i);
                next_must_do_first = addOrdsToGPX(resp, start_ind[i] - 1,
                        end_ind[i] - 1, ords_array, dims, lineType, (i == 1),
                        next_must_do_first);
            }
        }
        else {
            // standard case, do the moveto always
            for (int i = 0; i < info_array_size; i++) {
                lineType = getLineToType(info_array, i);
                addOrdsToGPX(resp, start_ind[i] - 1, end_ind[i] - 1,
                    ords_array, dims, lineType, true, true);
            }
        }

        return resp;
    }

    public static void main(String[] args) {
        /*
         * System.err.println("dburl has the following format:");
         * System.err.println("jdbc:postgresql://HOST:PORT/DATABASENAME");
         * System.err.println("tablename is 'jdbc_test' by default.");
         * System.exit(1);
         */
        String dburl = "jdbc:oracle:thin:@//localhost:1521/xe";
        String dbuser = "system";
        String dbpass = "aquilina";

        String dbtable = "VIAS";

        Connection conn = null;
        System.out.println("Creating JDBC connection...");

        try {
            DriverManager.registerDriver(new OracleDriver());

            conn = DriverManager.getConnection(dburl, dbuser, dbpass);

            conn.setAutoCommit(false);

            long t1 = System.currentTimeMillis();
            test1(conn, dburl, dbuser, dbpass, dbtable);

            // testHashID(conn, dburl, dbuser, dbpass, dbtable);
            long t2 = System.currentTimeMillis();
            System.out.println("Tiempo de consulta1:" + (t2 - t1) +
                " milisegundos");

            //			t1 = System.currentTimeMillis();
            //			OracleSpatialDriver driver = initDriverOracle(conn, dburl, dbuser, dbpass, dbtable);
            //			while (driver.getRowCount() == 0)
            //			{
            //				// do Nothing
            //			}
            //			t2 = System.currentTimeMillis();
            //			System.out.println("Tiempo de inicializar capa:" + (t2 - t1)
            //					+ " milisegundos");
            //
            //			t1 = System.currentTimeMillis();
            //			test4(driver);
            //			t2 = System.currentTimeMillis();
            //			System.out.println("Tiempo de recorrer capa:" + (t2 - t1)
            //					+ " milisegundos");
            conn.close();
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param conn
     * @param dburl
     * @param dbuser
     * @param dbpass
     * @param dbtable
     */
    private static void test1(Connection conn, String dburl, String dbuser,
        String dbpass, String dbtable) {
        try {
            String strSQL = "select c.geometry, ROWID from " + dbtable + " c";

            //			String strSQL = "select c.GEOMETRY, ROWID  from VIAS c where (sdo_filter(GEOMETRY, mdsys.sdo_geometry(2003, 82337, null, mdsys.sdo_elem_info_array(1, 1003, 1), mdsys.sdo_ordinate_array(-167305.445478584,3826564.14408529, 1026816.46891846,3826564.14408529, 1026816.46891846,4919672.72433395, -167305.445478584,4919672.72433395, -167305.445478584,3826564.14408529)), 'mask=anyinteract querytype=window') = 'TRUE')";
            Statement s = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            int fetchSize = 20000;
            s.setFetchSize(fetchSize);
            s.setFetchDirection(ResultSet.FETCH_FORWARD);

            //            s.execute("declare wkb_cursor binary cursor for " + strSQL);
            long t1 = System.currentTimeMillis();

            //            ResultSet r =   s.executeQuery("fetch forward " + fetchSize + " in wkb_cursor");
            OracleResultSet r = (OracleResultSet) s.executeQuery(strSQL);
            long t2 = System.currentTimeMillis();
            System.out.println("Tiempo de consulta:" + (t2 - t1) + " msecs.");

            ResultSetMetaData metadata = r.getMetaData();
            System.out.println(metadata.getColumnClassName(1));

            // Estaría bien tener una clase OraParser separada,
            // igual que tenemos WKBParser y WKTParser.
            WKBParser2 parser = new WKBParser2();
            int numReg = 0;

            while (r.next()) {
                /*
                 * ¿Hay otros métodos de leer el campo SDO_GEOMETRY?
                 * STRUCT parece muy lento, sobre todo al tener que procesarlo
                 * después.
                 */

                //				JGeometry geom = (JGeometry) r.getObject(1, mymap);
                //				byte[] aux2 = r.getBytes(1);
                //				InputStream aux = r.getBinaryStream(1);
                STRUCT _st = (oracle.sql.STRUCT) r.getObject(1);
                GeneralPathX gpx = parseOracleStruct(_st);

                //
                //				// Prueba con la parte de Prodevelop
                ////				GeneralPathX gpx = OracleSpatialUtils.structToGPX(_st
                ////						.getOracleAttributes());
                //				IGeometry geom = ShapeFactory.createGeometry(new FPolyline2D(gpx));

                /* PRUEBA CON LA CARGA STANDARD DE ORACLE */
                //				 JGeometry jg = JGeometry.load(_st);

                // GeneralPathX gpx = OracleSpatialUtils.structToGPX(_st);
                numReg++;
            }

            System.out.println("numReg = " + numReg);
            s.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testHashID(Connection conn, String dburl,
        String dbuser, String dbpass, String dbtable) {
        try {
            /*
             * Create a statement and execute a select query.
             */

            // String strSQL = "select rowid, c.geometry.SDO_ELEM_INFO from " +
            // dbtable + "
            // c";
            String strSQL = "select rowid from " + dbtable;

            PreparedStatement s = conn.prepareStatement(strSQL);

            // Statement s =
            // conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
            // ResultSet.CONCUR_READ_ONLY);
            int fetchSize = 5000;
            s.setFetchSize(fetchSize);

            ResultSet r = s.executeQuery(strSQL);
            int id = 0;

            while (r.next()) {
                String strAux = r.getString(1);
                id++;

                // System.out.println("Row " + id + ":" + strAux);
            }

            s.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prueba con JTS_IO. (OraReader)
     * @param conn
     * @param dburl
     * @param dbuser
     * @param dbpass
     * @param dbtable
     * @throws SQLException
     */
    private static void test3(Connection conn, String dburl, String dbuser,
        String dbpass, String dbtable) throws SQLException {
        // Usando el OraReader de JTSIO.
        OraReader or = new OraReader();

        String strSQL = "select c.GEOMETRY, ROWID  from VIAS c where (sdo_filter(GEOMETRY, mdsys.sdo_geometry(2003, 82337, null, mdsys.sdo_elem_info_array(1, 1003, 1), mdsys.sdo_ordinate_array(-167305.445478584,3826564.14408529, 1026816.46891846,3826564.14408529, 1026816.46891846,4919672.72433395, -167305.445478584,4919672.72433395, -167305.445478584,3826564.14408529)), 'mask=anyinteract querytype=window') = 'TRUE')";

        Statement s = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
        int fetchSize = 20000;
        s.setFetchSize(fetchSize);
        s.setFetchDirection(ResultSet.FETCH_FORWARD);

        long t1 = System.currentTimeMillis();
        ResultSet r = s.executeQuery(strSQL);
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo de consulta:" + (t2 - t1) + " msecs.");

        ResultSetMetaData metadata = r.getMetaData();
        System.out.println(metadata.getColumnClassName(1));

        WKBParser2 parser = new WKBParser2();
        int numReg = 0;

        while (r.next()) {
            STRUCT st = (oracle.sql.STRUCT) r.getObject(1);
            MultiLineString pt2 = (MultiLineString) or.read(st);
            numReg++;
        }

        System.out.println("numReg = " + numReg);
        s.close();
    }

    private static void test4(OracleSpatialDriver driver) {
        try {
            IFeatureIterator geomIt = driver.getFeatureIterator(
                    "SELECT ROWID, GEOMETRY FROM VIAS");

            while (geomIt.hasNext()) {
                IFeature feat = geomIt.next();
                IGeometry geom = feat.getGeometry();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static OracleSpatialDriver initDriverOracle(IConnection conn,
        String dburl, String dbuser, String dbpass, String dbtable) {
        try {
            String fidField = "rowid"; // BE CAREFUL => MAY BE NOT!!!
            String geomField = "geometry"; // BE CAREFUL => MAY BE NOT!!! =>

            String[] fields = new String[1];
            fields[0] = "rowid";

            String whereClause = "";

            OracleSpatialDriver driver = new OracleSpatialDriver();

            // Here you can set the workingArea
            // driver.setWorkingArea(dbLayerDefinition.getWorkingArea());
            String strEPSG = "23030";
            DBLayerDefinition lyrDef = new DBLayerDefinition();
            lyrDef.setName(dbtable);
            lyrDef.setTableName(dbtable);
            lyrDef.setWhereClause(whereClause);
            lyrDef.setFieldNames(fields);
            lyrDef.setFieldGeometry(geomField);
            lyrDef.setFieldID(fidField);

            // if (dbLayerDefinition.getWorkingArea() != null)
            // lyrDef.setWorkingArea(dbLayerDefinition.getWorkingArea());
            lyrDef.setSRID_EPSG(strEPSG);

            if (driver instanceof ICanReproject) {
                ((ICanReproject) driver).setDestProjection(strEPSG);
            }

            driver.setData(conn, lyrDef);

            IProjection proj = null;

            if (driver instanceof ICanReproject) {
                proj = CRSFactory.getCRS("EPSG:" +
                        ((ICanReproject) driver).getSourceProjection(conn, lyrDef));
            }

            return driver;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
