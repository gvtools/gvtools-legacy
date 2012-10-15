/*
 * Created on 22-dic-2004
 */
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
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
package com.iver.cit.gvsig.DEMO;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.geotools.data.DataSourceException;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.postgis.PostgisDataStore;
import org.geotools.data.postgis.PostgisDataStoreFactory;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;

import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLyrGT2;
import com.iver.cit.gvsig.fmap.layers.FLyrGT2_old;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * Pruebas de capas GT2, hechas por Fran, sacadas del
 * CommandListener
 * @author Luis W. Sevilla (sevilla_lui@gva.es)
 */
/**
 * @author victorzinho
 *
 */
public class PruebasGT2 implements ActionListener {
    static PostgisDataStoreFactory postGisFactory = new PostgisDataStoreFactory();

    Map remote;
    private MapContext m_Mapa;


    /**
     * Returns the parameters
     */
    public HashMap getParams() {
        HashMap params = new HashMap();
        // Param[] dbParams = postGisFactory.getParametersInfo();
        params.put("dbtype", "postgis"); //$NON-NLS-1$
        params.put("host", "localhost");
        params.put("port", new Integer(5432));

        params.put("database", "latin1");

        params.put("user", "postgres");
        params.put("passwd", "aquilina");

        params.put("wkb enabled", Boolean.TRUE);
        params.put("loose bbox", Boolean.TRUE);

        params.put("namespace", ""); //$NON-NLS-1$

        return params;
    }

    /**
    *
    */
   protected void addLayerGT2_Shp() {
       /* JFileChooser fileChooser = new JFileChooser(); // lastFolder);
       fileChooser.addChoosableFileFilter(new SimpleFileFilter("shp", "Shapefile (*.shp)"));

       int result = fileChooser.showOpenDialog(theView);

       if (result == JFileChooser.APPROVE_OPTION) {
           File file = fileChooser.getSelectedFile();
           // lastFolder = file.getParentFile();

           try {
            // Load the file
           	URL url = file.toURL();

            DataStore store;

            // Para shapes
            store = new ShapefileDataStore(url);

            loadLayer(store, store.getTypeNames()[0]);
           } catch (Throwable t) {
               JOptionPane.showMessageDialog(null, "An error occurred while loading the file",
                   "Demo GT2", JOptionPane.ERROR_MESSAGE);
               t.printStackTrace();
           }
       } */
   }
   protected void addLayer_PostGIS()
   {
       /* String nomTabla = JOptionPane.showInputDialog(null, "¿Nombre de la tabla a cargar?", "provin");
       PostGisDriver driver = new PostGisDriver();
       String dburl = "jdbc:postgresql://localhost/latin1";
       String dbuser = "postgres";
       String dbpass = "aquilina";
       String fields = "ASBINARY(the_geom, 'XDR') as the_geom, ND_4, NOM_PROVIN";
       String whereClause = "";


       Connection conn;
    try {
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(dburl, dbuser, dbpass);
        conn.setAutoCommit(false);
        driver.setData(conn, nomTabla, fields, whereClause, -1);
        FLayer lyr = LayerFactory.createDBLayer(driver, nomTabla, null);
        m_Mapa.getLayers().addLayer(lyr);
    } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        */
   }

   protected void addLayer_mySQL()
   {
       /* String nomTabla = JOptionPane.showInputDialog(null, "¿Nombre de la tabla a cargar?", "provin");
       MySQLDriver driver = new MySQLDriver();
       String dburl = "jdbc:mysql://localhost/test";
       String dbuser = "root";
       String dbpass = "aquilina";
       String fields = "ASBINARY(ogc_geom) as the_geom, ID, NOM_PROVIN, ND_4, ND_5, ND_14, AREA";
       // String fields = "ASBINARY(ogc_geom) as the_geom, ID, RD_5, RD_11";
       String whereClause = "";


       Connection conn;
    try {
        Class.forName("com.mysql.jdbc.Driver");
        // Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?user=monty&password=greatsqldb");

        conn = DriverManager.getConnection(dburl, dbuser, dbpass);
        conn.setAutoCommit(false);
        driver.setData(conn, nomTabla, fields, whereClause, 2);
        FLayer lyr = LayerFactory.createDBLayer(driver, nomTabla, null);
        m_Mapa.getLayers().addLayer(lyr);
    } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        */
   }

   protected void addLayerGT2_PostGIS()
   {
       String nomTabla = JOptionPane.showInputDialog(null, "¿Nombre de la tabla a cargar?", "provin");

	    remote = getParams();
	    /* remote.put("dbtype","postgis");
	    remote.put("host","localhost"); //José Miguel
	    remote.put("port", new Integer(5432));
	    remote.put("database", "latin1");
	    remote.put("user", "postgres");
	    remote.put("passwd", "aquilina");
	    remote.put("charset", "");
	    remote.put("namespace", ""); */

	    PostgisDataStore store;
		try {
			store = (PostgisDataStore) postGisFactory.createDataStore(remote);
            FLyrGT2 lyrGT2 = new FLyrGT2();
            lyrGT2.setDataStore(store);
            lyrGT2.setTableName(nomTabla);
            m_Mapa.getLayers().addLayer(lyrGT2);
            // loadLayer(store, nomTabla);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }


   /**
    * Load the data from the specified dataStore and construct a {@linkPlain Context context} with
    * a default style.
    *
    * @param url The url of the shapefile to load.
    * @param name DOCUMENT ME!
    *
    * @throws IOException is a I/O error occured.
    * @throws DataSourceException if an error occured while reading the data source.
    * @throws FileNotFoundException DOCUMENT ME!
    */
   protected void loadLayer(DataStore store, String layerName)
       throws IOException, DataSourceException {
       long t1 = System.currentTimeMillis();
       final FeatureSource features = store.getFeatureSource(layerName);
       long t2 = System.currentTimeMillis();
       System.out.println("t2-t1= " + (t2-t1));
       // Create the style
       final StyleBuilder builder = new StyleBuilder();
       final Style style;
       Class geometryClass = features.getSchema().getDefaultGeometry().getType();

       if (LineString.class.isAssignableFrom(geometryClass)
               || MultiLineString.class.isAssignableFrom(geometryClass)) {
           style = builder.createStyle(builder.createLineSymbolizer());
       } else if (Point.class.isAssignableFrom(geometryClass)
               || MultiPoint.class.isAssignableFrom(geometryClass)) {
           style = builder.createStyle(builder.createPointSymbolizer());
       } else {
           style = builder.createStyle(builder.createPolygonSymbolizer(Color.ORANGE, Color.BLACK, 1));
       }

       final MapLayer layer = new DefaultMapLayer(features, style);
       layer.setTitle(layerName);

       FLyrGT2_old lyrGT2 = new FLyrGT2_old(layer);
       try {
			m_Mapa.getLayers().addLayer(lyrGT2);
		} catch (CancelationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

   }

   public void setMapContext(MapContext map)
   {
   		m_Mapa = map;
   }
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand() == "ADD_GT2_POSTGIS_PROPIO") {
			addLayer_PostGIS();
		}
		if (e.getActionCommand() == "ADD_GT2_MYSQL_PROPIO") {
			addLayer_mySQL();
		}

		if (e.getActionCommand() == "ADD_GT2_POSTGIS") {
			addLayerGT2_PostGIS();
		}
		if (e.getActionCommand() == "ADD_GT2_SHP") {
		    addLayerGT2_Shp();
		}
	}
}
