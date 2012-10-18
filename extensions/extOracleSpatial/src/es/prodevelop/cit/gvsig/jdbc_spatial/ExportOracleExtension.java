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
package es.prodevelop.cit.gvsig.jdbc_spatial;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.About;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;


/**
 * This extension adds the export-to-oracle button.
 *
 * @author jldominguez
 *
 */
public class ExportOracleExtension extends Extension {
	
    private static Logger logger = Logger.getLogger(ExportOracleExtension.class.getName());
    
    public static boolean ORACLE_JAR_PRESENT = false;

    public void initialize() {
    	
    	ORACLE_JAR_PRESENT = isOracleJarPresent();

        // about
        java.net.URL newurl = createResourceUrl("about/jdbc-os-about.html");
        About claseAbout = (About) PluginServices.getExtension(com.iver.cit.gvsig.About.class);
        claseAbout.getAboutPanel().addAboutUrl("JDBC Oracle Spatial", newurl);
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();

        OraEpsgTableLoader loader = new OraEpsgTableLoader();
        if (!loader.createOracleEpsgTable()) {
        	logger.error("Unable to create ORA_EPSG datasource!");
        }
    }

    private boolean isOracleJarPresent() {
    	
    	try {
    		Class rowid_class = Class.forName("oracle.sql.ROWID");
    	} catch (Exception ex) {
    		logger.error("Unable to instantiate ROWID (oracle jar missing?) : " + ex.getMessage());
    		return false;
    	}
		return true;
	}

	public void execute(String actionCommand) {
		
        if (actionCommand.compareToIgnoreCase("EXPORT_TO_ORACLE_SPATIAL") == 0) {
            FLyrVect lyrv = null;
            MapContext mx = null;

            try {
                IWindow w = PluginServices.getMDIManager().getActiveWindow();

                if (w instanceof View) {
                    View v = (View) w;
                    MapControl mc = v.getMapControl();
                    mx = mc.getMapContext();

                    FLayer[] lyrs = mx.getLayers().getActives();

                    if (lyrs.length == 1) {
                        FLayer lyr = lyrs[0];

                        if (lyr instanceof FLyrVect) {
                            lyrv = (FLyrVect) lyr;

                            ExportToOracle export = new ExportToOracle();
                            export.toOracle(mx, lyrv);
                        }
                    }
                }
            }
            catch (Exception ex) {
            	logger.error(
                    "Unexpected error while getting active vect layer: " +
                    ex.getMessage());
            	logger.error("Nothing done.");
            }
        }
    }

    public boolean isEnabled() {
        return isVisible();
    }

    /**
     * Is visible when there is one vector layer selected
     */
    public boolean isVisible() {
       
    	if (!ORACLE_JAR_PRESENT) {
    		return false;
    	}
    	// if (true) return true;
    	
        try {
            IWindow w = PluginServices.getMDIManager().getActiveWindow();

            if (w instanceof View) {
                View v = (View) w;
                MapControl mc = v.getMapControl();
                MapContext mx = mc.getMapContext();
                FLayer[] lyrs = mx.getLayers().getActives();

                if (lyrs.length == 1) {
                    FLayer lyr = lyrs[0];

                    if ((lyr instanceof FLyrVect) && (true)) {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    private java.net.URL createResourceUrl(String path) {
        return getClass().getClassLoader().getResource(path);
    }
}
