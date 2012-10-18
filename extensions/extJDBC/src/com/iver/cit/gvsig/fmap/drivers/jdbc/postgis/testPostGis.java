/*
 * Created on 03-mar-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.drivers.jdbc.postgis;

import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Enumeration;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.postgresql.fastpath.Fastpath;

import com.iver.cit.gvsig.fmap.core.ICanReproject;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ConnectionFactory;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.WKBParser2;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ISpatialDB;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;


/**
 * @author FJP
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class testPostGis {

      public static void main(String[] args)
      {
      /*    System.err.println("dburl has the following format:");
          System.err.println("jdbc:postgresql://HOST:PORT/DATABASENAME");
          System.err.println("tablename is 'jdbc_test' by default.");
          System.exit(1); */


          String dburl = "jdbc:postgresql://localhost/latin1";
          String dbuser = "postgres";
          String dbpass = "aquilina";

//        String dburl = "jdbc:postgresql://192.168.0.217/postgis";
//        String dbuser = "gvsig";
//        String dbpass = "";


//          String dbtable = "carreteras_lin_5k_t10";
          String dbtable = "VIAS";

          Connection conn = null;
          System.out.println("Creating JDBC connection...");
          try {
            Class.forName("org.postgresql.Driver");
              Enumeration enumDrivers = DriverManager.getDrivers();
              while (enumDrivers.hasMoreElements())
              {
                  System.out.println("Driver " + enumDrivers.nextElement().toString());
              }
              conn = DriverManager.getConnection(dburl, dbuser, dbpass);

              conn.setAutoCommit(false);

              long t1 = System.currentTimeMillis();
              test1(conn, dburl, dbuser, dbpass, dbtable);
              long t2 = System.currentTimeMillis();
              System.out.println("Tiempo de consulta1:" + (t2 - t1) + " milisegundos");

              /* FLyrVect lyr = initLayerPostGIS();
              t1 = System.currentTimeMillis();
              test4(lyr);
              t2 = System.currentTimeMillis();

              System.out.println("Tiempo de consulta2:" + (t2 - t1) + " milisegundos"); */

              conn.close();

          } catch (ClassNotFoundException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }


      }
      private static void test1(Connection conn, String dburl, String dbuser, String dbpass, String dbtable)
      {
          try
          {
              // magic trickery to be pgjdbc 7.2 compatible
              // This works due to the late binding of data types in most java VMs. As
              // this is more a demo source than a real-world app, we can risk this
              // problem.
              /* if (conn.getClass().getName().equals("org.postgresql.jdbc2.Connection")) {
                  ((org.postgresql.Connection) conn).addDataType("geometry", "org.postgis.PGgeometry");
                  ((org.postgresql.Connection) conn).addDataType("box3d", "org.postgis.PGbox3d");
              } else {
                  ((org.postgresql.PGConnection) conn).addDataType("geometry", "org.postgis.PGgeometry");
                  ((org.postgresql.PGConnection) conn).addDataType("box3d", "org.postgis.PGbox3d");
              } */



            /*
            * Create a statement and execute a select query.
            */
              // String strSQL = "select AsBinary(the_geom) as geom, nom_provin from " + dbtable;
              // String strSQL = "select ASBINARY(the_geom) as geom, gid from " + dbtable;
              // String strSQL = "select ASBINARY(geometria) as geom, fecha_inicio_evento, fecha_fin_evento, date1, time1 from " + dbtable;
        	  String strSQL = "select ASBINARY(the_geom) as geom from " + dbtable;
              // strSQL = "select ASTEXT(the_geom), nom_provin as geom from " + dbtable;
              /* String strSQL = "SELECT gid, rd_3, rd_5, rd_6, rd_10, rd_11, rd_12, rd_13, rd_14,";
              strSQL = strSQL + " rd_15, rd_16, kilometers, cost, metros, AsText(force_2d(the_geom)) FROM vias";
              strSQL = strSQL + " WHERE TRUE";
              */
              // PreparedStatement s = conn.prepareStatement(strSQL);

              Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              // s.execute("begin");

              // Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              // s.setFetchSize(5);
              int fetchSize = 150000;
            s.execute("declare wkb_cursor binary cursor for " + strSQL);
            ResultSet r =   s.executeQuery("fetch forward " + fetchSize + " in wkb_cursor");
//              /// ResultSet r =   s.executeQuery("fetch forward all in wkb_cursor");

            // String strSQL2 = "select AsBinary(the_geom) as geom, nom_provin from " + dbtable;
            // PreparedStatement ps = conn.prepareStatement(strSQL2);
            // ResultSet r = ps.executeQuery();

//              ResultSet r = s.executeQuery(strSQL);
            WKBParser2 parser2 = new WKBParser2();
            // WKBParser parser = new WKBParser();
            long id=0;
            /* for (int i=1; i< 100; i++)
            {
                r.absolute(i);
                System.out.println("Row " + i + ":" + r.getString(2));
            }
            r.beforeFirst();
              for (int i=1; i< 100; i++)
              {
                  r.absolute(i);
                  System.out.println("Row " + i + ":" + r.getString(2));
              } */
            Timestamp date1 = new Timestamp(2006-1900, 8, 5, 16, 0, 0, 0);
            long time1 = date1.getTime();
            System.out.println("time1 = " + time1 + " data1 + " + date1);
            Timestamp date2 = new Timestamp(2006-1900, 8, 4, 9, 0, 0, 0);
            long time2 = date2.getTime();
            System.out.println("time2 = " + time2 + " data2 + " + date2);
            double num_msSecs2000 = 9.466776E11;


            while( r.next() )
            {
              /*
              * Retrieve the geometry as an object then cast it to the geometry type.
              * Print things out.
              */
                // Object obj = r.getObject(2);
                byte[] arrayByte = r.getBytes(1);

                // IGeometry gp = parser.parse(arrayByte);
                IGeometry gp2 = parser2.parse(arrayByte);

//                String strAux = r.getString(5);
//                System.out.println("Straux = " + strAux);
//                long asLong = r.getLong(2);
//                double asDouble = r.getDouble(2);
//                Date asDate = r.getDate(2);
                //                byte[] data1 = r.getBytes(2);
//                byte[] data2 = r.getBytes(3);
//                byte[] bdate1 = r.getBytes(4);
//                byte[] btime1 = r.getBytes(5);
//                ByteBuffer buf = ByteBuffer.wrap(data1);
//                ByteBuffer bufDate1 = ByteBuffer.wrap(bdate1);
//                ByteBuffer bufTime1 = ByteBuffer.wrap(btime1);
//
//                long daysAfter2000 = bufDate1.getInt() + 1;
//                long msecs = daysAfter2000*24*60*60*1000;
//                long real_msecs_date1 = (long) (num_msSecs2000 + msecs);
//                Date realDate1 = new Date(real_msecs_date1);
//                System.err.println("Date1 = " + realDate1 + " diff = " + (real_msecs_date1 - num_msSecs2000));
//
//                Calendar cal = new GregorianCalendar();
//                cal.setTimeInMillis(0);
//                // bufTime1.order(ByteOrder.LITTLE_ENDIAN);
//                long microsecs = bufTime1.getLong();
//                long real_msecs = microsecs - 3600000; // le quitamos una hora.
//                cal.setTimeInMillis(real_msecs);
//                long milis = cal.getTimeInMillis();
//                Time mytime1 = new Time(real_msecs);
//                Date mytime1asdate = new Date(real_msecs);
//                System.err.println("microsecs = " + microsecs + " TIME1 = " + mytime1);
//                System.err.println("microsecs = " + (long)num_msSecs2000 + " TIME1ASDATE = " + mytime1asdate);
//
//
//				double n1 = buf.getDouble(0); // num segs after 2000
////				Timestamp ts2000 = new Timestamp(2000-1900, 0, 1, 0, 0 , 0, 0);
////				int offset = ts2000.getTimezoneOffset() * 60 * 1000;
//
////				double num_msSecs2000 = ts2000.getTime() + offset;
//				long real_msecs2 = (long) (num_msSecs2000 + n1*1000);
//				Timestamp real = new Timestamp(real_msecs2);
//
//                // int id = r.getInt(2);
//                System.out.println("Fila " + id + ": fecha:" + real);
//                id++;
//                // Geometry regeom = PGgeometry.geomFromString(obj.toString());
//
//              // PGgeometry geom = (PGgeometry)obj;
//               // int id = r.getInt(2);
//              // System.out.println("Row " + id + ":" + strAux);
//              // System.out.println(geom.toString());
//                // System.out.println("provin=" + r.getString(2));
//                /* if ((id % fetchSize) == 0)
//                {
//                    r =   s.executeQuery("fetch forward " + fetchSize + " in wkb_cursor");
//                } */

            }
            // s.execute("end");
            s.close();

          }
          catch( Exception e )
          {
            e.printStackTrace();
          }
        }
      private static void test2(Connection conn, String dburl, String dbuser, String dbpass, String dbtable)
      {
          try
          {
            /*
            * Create a statement and execute a select query.
            */
              String strSQL = "select gid from " + dbtable;
              PreparedStatement s = conn.prepareStatement(strSQL);

              // Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              int fetchSize = 5000;
              ResultSet r = s.executeQuery(strSQL);
              int id=0;
              while( r.next() )
              {
                String strAux = r.getString(1);
                id++;
                // System.out.println("Row " + id + ":" + strAux);
              }
              s.close();
          }
          catch( Exception e )
          {
            e.printStackTrace();
          }
        }


      private static void test3(Connection conn, String dburl, String dbuser, String dbpass, String dbtable)
      {
          try
          {
        	  Fastpath  fp;
        	  if (conn.getClass().getName().equals("org.postgresql.jdbc2.Connection")) {
        		  // ((org.postgresql.Connection) conn).addDataType("geometry", "org.postgis.PGgeometry");
        		  // ((org.postgresql.Connection) conn).addDataType("box3d", "org.postgis.PGbox3d");
        	  } else {
        		  ((org.postgresql.PGConnection) conn).addDataType("geometry", "org.postgis.PGgeometry");
        		  ((org.postgresql.PGConnection) conn).addDataType("box3d", "org.postgis.PGbox3d");
        		  fp =  ((org.postgresql.PGConnection) conn).getFastpathAPI();
        	  }



            /*
            * Create a statement and execute a select query.
            */
              String strSQL = "select * from " + dbtable;
              // String strSQL = "select ASBINARY(the_geom) as geom from " + dbtable;
              // strSQL = "select ASTEXT(the_geom), nom_provin as geom from " + dbtable;
              /* String strSQL = "SELECT gid, rd_3, rd_5, rd_6, rd_10, rd_11, rd_12, rd_13, rd_14,";
              strSQL = strSQL + " rd_15, rd_16, kilometers, cost, metros, AsText(force_2d(the_geom)) FROM vias";
              strSQL = strSQL + " WHERE TRUE";
              */
              // PreparedStatement s = conn.prepareStatement(strSQL);

              Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              // s.execute("begin");

              // Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
              // s.setFetchSize(5);
            /*  int fetchSize = 150000;
            s.execute("declare wkb_cursor2 binary cursor for " + strSQL);
            ResultSet r =   s.executeQuery("fetch forward " + fetchSize + " in wkb_cursor2"); */

            // String strSQL2 = "select AsBinary(the_geom) as geom, nom_provin from " + dbtable;
            // PreparedStatement ps = conn.prepareStatement(strSQL2);
            // ResultSet r = ps.executeQuery();

            ResultSet r = s.executeQuery(strSQL);
            // WKBParser parser = new WKBParser();
            long id=0;
            /* FastpathArg args[] = new FastpathArg[2];
            args[0] = new FastpathArg(fd);
            args[1] = new FastpathArg(len);
            return fp.getData("loread", args); */
            while( r.next() )
            {
              /*
              * Retrieve the geometry as an object then cast it to the geometry type.
              * Print things out.
              */
                Object obj = r.getObject(9);
                // fp.
            	// final Shape current = (Shape) r.getObject(1);
                // byte[] arrayByte = r.getBytes(1);
                // String strAux = r.getString(2);

                id++;
                // Geometry regeom = PGgeometry.geomFromString(obj.toString());

              // PGgeometry geom = (PGgeometry)obj;
               // int id = r.getInt(2);
              // System.out.println("Row " + id + ":" + strAux);
              // System.out.println(geom.toString());
                // System.out.println("provin=" + r.getString(2));
                /* if ((id % fetchSize) == 0)
                {
                    r =   s.executeQuery("fetch forward " + fetchSize + " in wkb_cursor");
                } */


            }
            // s.execute("end");
            s.close();

          }
          catch( Exception e )
          {
            e.printStackTrace();
          }
      }

      private static void test4(FLyrVect lyr)
      {
    	try
    	{
    		ISpatialDB dbAdapter = (ISpatialDB) lyr.getSource();
            IVectorialDatabaseDriver dbDriver = (IVectorialDatabaseDriver) dbAdapter.getDriver();
    	    IFeatureIterator geomIt = dbDriver.getFeatureIterator(lyr.getFullExtent(), "23030");
	        while (geomIt.hasNext())
	        {
	        	IFeature feat = geomIt.next();
	        	IGeometry geom = feat.getGeometry();
	        }
  		}
  		catch(Exception e)
  		{
  			e.printStackTrace();
  		}

      }
}
