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
package es.prodevelop.cit.gvsig.arcims.extension;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.About;
import com.iver.cit.gvsig.AddLayer;
import com.iver.cit.gvsig.project.documents.view.legend.gui.General;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LabelingManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LegendManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ThemeManagerWindow;
import com.iver.cit.gvsig.project.documents.view.toc.gui.FPopupMenu;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMSCollection;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FRasterLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.gui.toc.ArcImsLayerScaleTocMenuEntry;
import es.prodevelop.cit.gvsig.arcims.gui.toc.ArcImsPropsTocMenuEntry;
import es.prodevelop.cit.gvsig.arcims.gui.wizards.ArcImsWizard;

/**
 * This class implements the extension to access ArcIMS servers.
 * 
 * @author jldominguez
 * 
 */
public class ArcimsClientModule extends Extension {
	/**
	 * This method initializes the extension. Adds the ArcIMS wizard and the
	 * right-click popup menus to the gvSIG resources.
	 */
	public void initialize() {
		// must add menus and a new tab to the wizard..
		// to create an arcims layer
		FPopupMenu.addEntry(new ArcImsPropsTocMenuEntry());
		// FPopupMenu.addEntry(new ArcImsRasterPropsTocMenuEntry());
		FPopupMenu.addEntry(new ArcImsLayerScaleTocMenuEntry());
		AddLayer.addWizard(ArcImsWizard.class);

		// properties tabs:
		ThemeManagerWindow.setTabEnabledForLayer(General.class,
				FFeatureLyrArcIMS.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LegendManager.class,
				FFeatureLyrArcIMS.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(LabelingManager.class,
				FFeatureLyrArcIMS.class, true);

		// about
		java.net.URL newurl = createResourceUrl("about/extarcims-about.html");
		About claseAbout = (About) PluginServices
				.getExtension(com.iver.cit.gvsig.About.class);
		claseAbout.getAboutPanel().addAboutUrl("ArcIMS", newurl);

		// catalog
		ExtensionPoints extensionPoints = ExtensionPointsSingleton
				.getInstance();
		extensionPoints.add("CatalogLayers", "arcims_raster",
				FRasterLyrArcIMS.class);
		extensionPoints.add("CatalogLayers", "arcims_vectorial",
				FFeatureLyrArcIMSCollection.class);

		// FLyrVect.forTestOnlyVariableUseIterators_REMOVE_THIS_FIELD = false;
		// UIManager.put("Label.font", new Font("SimSun", Font.PLAIN, 12));
	}

	/**
	 * This method is called when the extension's controls are used. So far, the
	 * ArcIMS plugin does <i>not</i> add any controls.
	 * 
	 * @param actionCommand
	 *            the control's string action commmad.
	 * 
	 */
	public void execute(String actionCommand) {
	}

	/**
	 * This method is called to find out if the plugin's controls are enabled or
	 * not. So far, the ArcIMS plugin does <i>not</i> add any controls.
	 * 
	 * @return <b>true</b> if controls must be enabled, <b>false</b> if not.
	 */
	public boolean isEnabled() {
		return false;
	}

	/**
	 * This method is called to find out if the plugin's controls are visible or
	 * not. So far, the ArcIMS plugin does <i>not</i> add any controls.
	 * 
	 * @return <b>true</b> if controls must be visible, <b>false</b> if not.
	 */
	public boolean isVisible() {
		return false;
	}

	private java.net.URL createResourceUrl(String path) {
		return getClass().getClassLoader().getResource(path);
	}
}
