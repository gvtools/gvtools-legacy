/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
package com.iver.cit.gvsig;

import javax.swing.ImageIcon;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.preferences.IPreference;
import com.iver.andami.preferences.IPreferenceExtension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.simpleWizard.SimpleWizard;
import com.iver.cit.gvsig.project.documents.gui.Annotation_ConfigureLabel;
import com.iver.cit.gvsig.project.documents.gui.Annotation_Create;
import com.iver.cit.gvsig.project.documents.gui.Annotation_FieldSelect;
import com.iver.cit.gvsig.project.documents.gui.Annotation_Open;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.legend.gui.General;
import com.iver.cit.gvsig.project.documents.view.legend.gui.PanelLegendAnnotation;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ThemeManagerWindow;
import com.iver.cit.gvsig.project.documents.view.legend.preferences.Annotation_Preferences;

/**
 * Extension to create an annotation layer.
 *
 * @author Vicente Caballero Navarro
 */
public class Annotation_Extension extends Extension implements
		IPreferenceExtension {
	private MapContext map = null;

	private Annotation_Preferences ap = new Annotation_Preferences();

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {

		AddLayer.addWizard(Annotation_Open.class);

		ThemeManagerWindow.addPage(PanelLegendAnnotation.class);

		ThemeManagerWindow.setTabEnabledForLayer(General.class, Annotation_Layer.class, true);
		ThemeManagerWindow.setTabEnabledForLayer(PanelLegendAnnotation.class, Annotation_Layer.class, true);
		registerIcons();
	}

	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"ext-annotation-pack-graphics",
				this.getClass().getClassLoader().getResource("images/package_graphics.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"annotation-properties",
				Annotation_Preferences.class.getClassLoader().getResource("images/AnnotationProperties.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"annotation-modify",
				this.getClass().getClassLoader().getResource("images/ModifyAnnotationCursor.png")
			);
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		if ("ANNOTATIONLAYER".equals(actionCommand)) {
			ImageIcon Logo=PluginServices.getIconTheme().get("ext-annotation-pack-graphics");

			SimpleWizard wizard = new SimpleWizard(Logo);

			FLyrVect lv = (FLyrVect) map.getLayers().getActives()[0];
			Annotation_Layer la=null;
			try {
				la = Annotation_Layer.createLayerFromVect(lv);
			} catch (ReadDriverException e) {
				NotificationManager.addError(e);
			}
			la.setName(lv.getName());

			Annotation_FieldSelect panel1 = new Annotation_FieldSelect(wizard
					.getWizardComponents(), la);
			Annotation_ConfigureLabel panel2 = new Annotation_ConfigureLabel(
					wizard.getWizardComponents(), la);

			wizard.getWizardComponents().addWizardPanel(panel1);
			wizard.getWizardComponents().addWizardPanel(panel2);

			wizard.getWizardComponents()
					.setFinishAction(
							new Annotation_Create(wizard.getWizardComponents(),
									map, la));

			wizard.getWindowInfo().setWidth(540);
			wizard.getWindowInfo().setHeight(380);
			wizard.getWindowInfo().setTitle(
					PluginServices.getText(this, "to_annotation"));

			PluginServices.getMDIManager().addWindow(wizard);

		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if ((v != null)
				&& v instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
			com.iver.cit.gvsig.project.documents.view.gui.View vista = (com.iver.cit.gvsig.project.documents.view.gui.View) v;
			IProjectView model = vista.getModel();
			map = model.getMapContext();

			FLayer[] layers = map.getLayers().getActives();

			if (layers.length == 1) {
				if (layers[0].isAvailable() && layers[0] instanceof FLyrVect) {
					FLyrVect lv = (FLyrVect) layers[0];
					try {
						SelectableDataSource sds = lv.getSource()
								.getRecordset();

						if (sds.getFieldCount() > 0) {
							return true;
						}
					} catch (ReadDriverException e) {
						return false;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		} else if (v instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
			return true;
		} else {
			return false;
		}
	}

	public IPreference[] getPreferencesPages() {
		return new IPreference[] {ap};
	}
}
