/*
 * Created on 22-jun-2005
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
package com.iver.cit.gvsig.geoprocess.wizard;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.geoprocess.impl.buffer.BufferGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.clip.ClipGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.difference.DifferenceGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.dissolve.DissolveGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.intersection.IntersectionGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.merge.MergeGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.spatialjoin.SpatialJoinGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.union.UnionGeoprocessPlugin;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * GeoProcessingExtension class
 * 
 * @author jmorell
 */
public class GeoProcessingExtension extends Extension {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#inicializar()
	 */
	public void initialize() {
		// TODO Auto-generated method stub

		PluginServices.getIconTheme().registerDefault(
				"geo-process",
				this.getClass().getClassLoader()
						.getResource("images/geoprocessicon.png"));

		PluginServices.getIconTheme().registerDefault(
				"buffered-desc",
				this.getClass().getClassLoader()
						.getResource("images/bufferdesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"dissolved-desc",
				this.getClass().getClassLoader()
						.getResource("images/dissolvedesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"merge-desc",
				this.getClass().getClassLoader()
						.getResource("images/mergedesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"intersect-desc",
				this.getClass().getClassLoader()
						.getResource("images/intersectdesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"union-desc",
				this.getClass().getClassLoader()
						.getResource("images/uniondesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"spatialjoin-desc",
				this.getClass().getClassLoader()
						.getResource("images/spatialjoindesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"clip-desc",
				this.getClass().getClassLoader()
						.getResource("images/clipdesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"convexhull-desc",
				this.getClass().getClassLoader()
						.getResource("images/convexhulldesc.png"));

		PluginServices.getIconTheme().registerDefault(
				"difference-desc",
				this.getClass().getClassLoader()
						.getResource("images/differencedesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"bufferdesc-resource",
				BufferGeoprocessPlugin.class
						.getResource("resources/bufferdesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"clipdesc-resource",
				ClipGeoprocessPlugin.class
						.getResource("resources/clipdesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"differencedesc-resource",
				DifferenceGeoprocessPlugin.class
						.getResource("resources/differencedesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"dissolvedesc-resource",
				DissolveGeoprocessPlugin.class
						.getResource("resources/dissolvedesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"intersectdesc-resource",
				IntersectionGeoprocessPlugin.class
						.getResource("resources/intersectdesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"mergedesc-resource",
				MergeGeoprocessPlugin.class
						.getResource("resources/mergedesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"spatialjoindesc-resource",
				SpatialJoinGeoprocessPlugin.class
						.getResource("resources/spatialjoindesc.png"));
		PluginServices.getIconTheme().registerDefault(
				"uniondesc-resource",
				UnionGeoprocessPlugin.class
						.getResource("resources/uniondesc.png"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		AndamiCmd cmd = null;
		if (actionCommand.equalsIgnoreCase("GEOPROCESSING")) {
			cmd = new GeoProcessingWizardCmd();
		} else if (actionCommand.equalsIgnoreCase("BUFFER")) {
			cmd = new BufferCmd();
		} else if (actionCommand.equalsIgnoreCase("CLIP")) {
			cmd = new ClipCmd();
		} else if (actionCommand.equalsIgnoreCase("DISSOLVE")) {
			cmd = new DissolveCmd();
		} else if (actionCommand.equalsIgnoreCase("INTERSECT")) {
			cmd = new IntersectionCmd();
		} else if (actionCommand.equalsIgnoreCase("DIFFERENCE")) {
			cmd = new DifferenceCmd();
		} else if (actionCommand.equalsIgnoreCase("UNION")) {
			cmd = new UnionCmd();
		} else if (actionCommand.equalsIgnoreCase("CONVEXHULL")) {
			cmd = new ConvexHullCmd();
		} else if (actionCommand.equalsIgnoreCase("MERGE")) {
			cmd = new MergeCmd();
		} else if (actionCommand.equalsIgnoreCase("SPATIAL_JOIN")) {
			cmd = new SpatialJoinCmd();
		}
		cmd.execute();
	}

	public boolean isEnabled() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			FLayers layers = model.getMapContext().getLayers();
			int numLayers = layers.getLayersCount();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect && layer.isAvailable())
					return true;
			}
		}
		return false;
	}

	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices
				.getMDIManager().getActiveWindow();
		if (f == null) {
			return false;
		}
		if (f instanceof View) {
			View vista = (View) f;
			IProjectView model = vista.getModel();
			FLayers layers = model.getMapContext().getLayers();
			int numLayers = layers.getLayersCount();
			for (int i = 0; i < numLayers; i++) {
				FLayer layer = layers.getLayer(i);
				if (layer instanceof FLyrVect)
					return true;
			}
			return false;
		}
		return false;
	}

}
