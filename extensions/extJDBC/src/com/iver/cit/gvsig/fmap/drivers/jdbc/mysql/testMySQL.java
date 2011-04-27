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

/* CVS MESSAGES:
 * 
 * $Id: testMySQL.java 6566 2006-07-31 06:47:19Z jaume $
 * $Log$
 * Revision 1.6  2006-07-31 06:47:19  jaume
 * arregla algunos bugs
 *
 * Revision 1.5  2005/12/15 16:44:35  fjp
 * Falsa modificación. Solo para pruebas
 *
 * Revision 1.4  2005/12/15 16:43:47  fjp
 * Segunda prueba con las variables del CVS
 *
 */
package com.iver.cit.gvsig.fmap.drivers.jdbc.mysql;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;

import com.iver.cit.gvsig.fmap.drivers.WKTParser;



/**
 * @author FJP
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class testMySQL {

      public static void main(String[] args) 
      { 
      /*    System.err.println("dburl has the following format:");
          System.err.println("jdbc:postgresql://HOST:PORT/DATABASENAME");
          System.err.println("tablename is 'jdbc_test' by default.");
          System.exit(1); */

          
      // Tarda casi 28 segundos en recuperar el tema de provincias!!
      String dburl = "jdbc:mysql://localhost/test";
      String dbuser = "root";
      String dbpass = "aquilina";

      String dbtable = "vias";

     java.sql.Connection conn; 
        try 
        { 
            System.out.println("Creating JDBC connection...");
            Class.forName("com.mysql.jdbc.Driver");
            Enumeration enumDrivers = DriverManager.getDrivers();
            while (enumDrivers.hasMoreElements())
            {
                System.out.println("Driver " + enumDrivers.nextElement().toString());
            }
            conn = DriverManager.getConnection(dburl, dbuser, dbpass);
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
            System.out.println(conn.getMetaData().getURL());
            System.out.println("Drivername = " + conn.getMetaData().getDriverName());
            conn.setAutoCommit(false);
            System.out.println(conn.getCatalog() + " " + conn.getClass().getName());
            java.sql.Driver drv = DriverManager.getDriver(conn.getMetaData().getURL());
            System.out.println(drv.getClass().getName());
          /* 
          * Create a statement and execute a select query. 
          */
            // String strSQL = "select ogc_geom as geom from vias";
            String strSQL = "select ASTEXT(ogc_geom) as geom from " + dbtable;
            /* String strSQL = "SELECT gid, rd_3, rd_5, rd_6, rd_10, rd_11, rd_12, rd_13, rd_14,"; 
            strSQL = strSQL + " rd_15, rd_16, kilometers, cost, metros, AsText(force_2d(the_geom)) FROM vias"; 
            strSQL = strSQL + " WHERE TRUE";
            */
            // PreparedStatement s = conn.prepareStatement(strSQL);
            long t1 = System.currentTimeMillis();
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            s.setFetchSize(2000);
            ResultSet r = s.executeQuery(strSQL);
			long t2 = System.currentTimeMillis();

			System.out.println("Tiempo de consulta:" + (t2 - t1) + " milisegundos");
			t1 = System.currentTimeMillis();
			int cont = 0;
			WKTParser parser = new WKTParser();
          while( r.next() ) 
          { 
            /* 
            * Retrieve the geometry as an object then cast it to the geometry type. 
            * Print things out. 
            */ 
              // Object obj = r.getObject(1);
              // InputStream inS = r.getAsciiStream(1);
              String strAux = r.getString(1);
              // IGeometry geom = parser.read(strAux);
              cont++;
              // int id = r.getInt(2);
              // System.out.println("Row " + id + ":");
              // Geometry regeom = PGgeometry.geomFromString(obj.toString());
              // System.out.println(obj.toString());
              
            // PGgeometry geom = (PGgeometry)obj; 
            /* int id = r.getInt(2);
            System.out.println("Row " + id + ":"); 
            System.out.println(geom.toString()); */ 
          }
          s.close(); 
          conn.close();
			t2 = System.currentTimeMillis();

			System.out.println("Tiempo de recorrido:"  + (t2 - t1) + " milisegundos. " + cont + " registros.");
          
        } 
        catch( Exception e ) 
        { 
          e.printStackTrace(); 
        }  
      }
}
