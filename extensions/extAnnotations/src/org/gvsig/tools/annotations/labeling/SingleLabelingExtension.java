/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2008 IVER T.I. and Generalitat Valenciana.
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
 *   Lérida 20
 *   46009 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gvsig.tools.annotations.labeling;

import java.util.WeakHashMap;

import org.gvsig.tools.annotations.labeling.fmap.tools.SingleLabelingTool;
import org.gvsig.tools.annotations.labeling.gui.SingleLabelingToolUI;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.core.ILabelable;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.tools.Behavior.Behavior;
import com.iver.cit.gvsig.fmap.tools.Behavior.PointBehavior;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * 
 * SingleLabelingExtension.java
 * 
 * Extension that allows the user to see a custom label over the feature that
 * she/he selects
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Feb 28, 2008
 * @author Cesar Martinez Izquierdo - cesar.martinez@iver.es Oct 2008
 * 
 */
public class SingleLabelingExtension extends Extension {
	private WeakHashMap<BaseView, SingleLabelingToolUI> toolUIs = new WeakHashMap<BaseView, SingleLabelingToolUI>();

	public void execute(String actionCommand) {
		if ("SINGLE_LABELING_TOOL".equals(actionCommand)) {
			BaseView v = (BaseView) PluginServices.getMDIManager()
					.getActiveWindow();
			SingleLabelingToolUI toolUI;
			if (toolUIs.containsKey(v)) {
				toolUI = toolUIs.get(v);
			} else {
				toolUI = new SingleLabelingToolUI(v);
				toolUIs.put(v, toolUI);
			}
			Behavior behavior = v.getMapControl().getMapTool(
					SingleLabelingTool.TOOLNAME);
			if (behavior == null) {
				v.getMapControl().addMapTool(
						SingleLabelingTool.TOOLNAME,
						new PointBehavior(new SingleLabelingTool(v
								.getMapControl(), toolUI)));
			} else {
				if (behavior.getListener() instanceof SingleLabelingToolUI) {
					toolUI.setTargetLayer(toolUI.getTargetLayer());
				}
			}
			v.getMapControl().setTool(SingleLabelingTool.TOOLNAME);
			PluginServices.getMDIManager().addWindow(toolUI);
		}
	}

	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"single-labeling-tool",
				getClass().getClassLoader().getResource(
						"images/single-labeling-tool.gif"));
		PluginServices.getIconTheme().registerDefault(
				"single-labeling-tool-config",
				getClass().getClassLoader().getResource(
						"images/single-labeling-tool-config.gif"));

	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		if (PluginServices.getMDIManager().getActiveWindow() instanceof View) {
			View v = (View) PluginServices.getMDIManager().getActiveWindow();
			FLayers layers = v.getMapControl().getMapContext().getLayers();
			if (layers.getLayersCount() == 0)
				return false;
			for (int i = 0; i < layers.getLayersCount(); i++) {
				FLayer layer = layers.getLayer(i);
				if (layer.isAvailable() && layer.isActive()
						&& !(layer instanceof ILabelable))
					return false;
			}
			return true;
		}
		return false;
	}

}
