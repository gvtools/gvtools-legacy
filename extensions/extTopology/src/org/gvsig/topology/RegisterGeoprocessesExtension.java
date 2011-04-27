/*
 * Created on 10-abr-2006
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
/* CVS MESSAGES:
*
* $Id: 
* $Log: 
*/
package org.gvsig.topology;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.geoprocess.impl.build.BuildGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.clean.CleanGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.fliplines.FlipLinesGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.generalization.GeneralizationGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.lineorpolygontopoints.LineToPointsGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.polytolines.PolyToLinesGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.referencing.ReferencingGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.smooth.SmoothGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.snapandcrack.SnapAndCrackGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.voronoi.VoronoiGeoprocessPlugin;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

public class RegisterGeoprocessesExtension extends Extension{

	public void initialize() {
		ExtensionPoints extensionPoints = 
			ExtensionPointsSingleton.getInstance();
//		extensionPoints.add("GeoprocessManager",
//				"SNAPANDCRACK", 
//				SnapAndCrackGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager",
				"VORONOI", 
				VoronoiGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"GENERALIZATION", 
				GeneralizationGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"FLIPLINES", 
				FlipLinesGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"POLYGONTOLINES", 
				PolyToLinesGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"POLYGONORLINESTOPOINTS", 
				LineToPointsGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"SMOOTH_GEOMETRIES", 
				SmoothGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"SPATIAL_ADJUST", 
				ReferencingGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"LineClean",
				CleanGeoprocessPlugin.class);
		
		extensionPoints.add("GeoprocessManager",
				"PolygonBuild",
				BuildGeoprocessPlugin.class);
		
		registerIcons();
	}
	
	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"voroidesc-icon",
				VoronoiGeoprocessPlugin.class.getResource("resources/voronoi_desc.png")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"generalizationdesc-icon",
				GeneralizationGeoprocessPlugin.class.getResource("resources/generalization_desc.png")
			);
		PluginServices.getIconTheme().registerDefault(
				"crackdesc-icon",
				SnapAndCrackGeoprocessPlugin.class.getResource("resources/crackdesc.png")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"fliplines-icon",
				FlipLinesGeoprocessPlugin.class.getResource("resources/flipline_desc.png")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"polytolines-icon",
				PolyToLinesGeoprocessPlugin.class.getResource("resources/polygontoline_desc.gif")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"linetopoints-icon",
				LineToPointsGeoprocessPlugin.class.getResource("resources/linetopoint_desc.gif")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"smoothdesc-icon",
				SmoothGeoprocessPlugin.class.getResource("resources/smooth_desc.gif")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"spatialadjustdesc-icon",
				ReferencingGeoprocessPlugin.class.getResource("resources/spatial_ adjust_desc.gif")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"clean-icon",
				CleanGeoprocessPlugin.class.getResource("resources/spatial_ adjust_desc.gif")
			);
		
		PluginServices.getIconTheme().registerDefault(
				"build-icon",
				BuildGeoprocessPlugin.class.getResource("resources/spatial_ adjust_desc.gif")
			);
		
	}
	
	public void execute(String actionCommand) {
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return true;
	}
}

