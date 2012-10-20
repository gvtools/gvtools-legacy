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
package com.iver.cit.gvsig;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerListener;
import com.iver.cit.gvsig.fmap.layers.MappingAnnotation;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.gui.panels.annotation.ConfigureLabel;
import com.iver.cit.gvsig.gui.panels.annotation.SelectAnnotationLayerNameAndField;
import com.iver.cit.gvsig.gui.simpleWizard.SimpleWizard;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class ThemeToAnnotationExtension extends Extension {
	private MapContext map = null;
	private IWindow view = null;

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons() {

	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */

	public class MyFinishAction extends FinishAction {
		private JWizardComponents myWizardComponents;
		private MapContext map;
		private FLyrVect layerVectorial;
		private FLyrAnnotation layerAnnotation;

		public MyFinishAction(JWizardComponents wizardComponents,
				MapContext map, FLyrVect layerVectorial,
				FLyrAnnotation layerAnnotation) {
			super(wizardComponents);
			this.map = map;
			this.layerVectorial = layerVectorial;
			this.layerAnnotation = layerAnnotation;
			myWizardComponents = wizardComponents;

		}

		public void performAction() {

			myWizardComponents.getFinishButton().setEnabled(false);
			SelectAnnotationLayerNameAndField panel1 = (SelectAnnotationLayerNameAndField) myWizardComponents
					.getWizardPanel(0);
			ConfigureLabel panel2 = (ConfigureLabel) myWizardComponents
					.getWizardPanel(1);

			SelectableDataSource source;
			MappingAnnotation mapping = new MappingAnnotation();

			try {
				source = this.layerAnnotation.getRecordset();

				mapping.setColumnText(source.getFieldIndexByName(panel1
						.getField()));

				if (!panel2.getAngleFieldName().equals(
						ConfigureLabel.TEXT_FOR_DEFAULT_VALUE)) {
					mapping.setColumnRotate(source.getFieldIndexByName(panel2
							.getAngleFieldName()));
				}

				if (!panel2.getColorFieldName().equals(
						ConfigureLabel.TEXT_FOR_DEFAULT_VALUE)) {
					mapping.setColumnColor(source.getFieldIndexByName(panel2
							.getColorFieldName()));
				}

				if (!panel2.getSizeFieldName().equals(
						ConfigureLabel.TEXT_FOR_DEFAULT_VALUE)) {
					mapping.setColumnHeight(source.getFieldIndexByName(panel2
							.getSizeFieldName()));
				}
				this.layerAnnotation.setInPixels(panel2.sizeUnitsInPixels());

				if (!panel2.getFontFieldName().equals(
						ConfigureLabel.TEXT_FOR_DEFAULT_VALUE)) {
					mapping.setColumnTypeFont(source.getFieldIndexByName(panel2
							.getFontFieldName()));
				}
			} catch (ReadDriverException e) {
				e.printStackTrace();
				return;
			}

			this.layerAnnotation.setName(panel1.getNewLayerName());
			this.layerAnnotation.setMapping(mapping);

			this.map.getLayers().addLayer(this.layerAnnotation);
			this.map.getLayers().removeLayer(this.layerVectorial);

			Project project = ((ProjectExtension) PluginServices
					.getExtension(ProjectExtension.class)).getProject();
			ArrayList projectTables = project
					.getDocumentsByType(ProjectTableFactory.registerName);
			for (int i = 0; i < projectTables.size(); i++) {
				ProjectTable pt = (ProjectTable) projectTables.get(i);
				if (pt.getAssociatedTable() != null
						&& pt.getAssociatedTable().equals(this.layerVectorial)) {
					pt.setAssociatedTable(this.layerAnnotation);
				}
			}

			this.layerAnnotation.setActive(true);
			this.myWizardComponents.getCancelAction().performAction();
			PluginServices.getMainFrame().enableControls();
		}

	}

	public void execute(String actionCommand) {
		if ("LAYERTOANNOTATION".equals(actionCommand)) {
			ImageIcon Logo = new javax.swing.ImageIcon(this.getClass()
					.getClassLoader()
					.getResource("images/package_graphics.png"));

			SimpleWizard wizard = new SimpleWizard(Logo);

			FLyrVect lv = (FLyrVect) map.getLayers().getActives()[0];
			FLyrAnnotation la = new FLyrAnnotation(null);
			LayerListener[] layerListeners = lv.getLayerListeners();
			for (int i = 0; i < layerListeners.length; i++) {
				la.addLayerListener(layerListeners[i]);
			}

			la.setSource(lv.getSource());
			la.setCrs(lv.getCrs());

			SelectAnnotationLayerNameAndField panel1 = new SelectAnnotationLayerNameAndField(
					wizard.getWizardComponents(), la);
			ConfigureLabel panel2 = new ConfigureLabel(
					wizard.getWizardComponents(), la);

			wizard.getWizardComponents().addWizardPanel(panel1);
			wizard.getWizardComponents().addWizardPanel(panel2);

			wizard.getWizardComponents().setFinishAction(
					new MyFinishAction(wizard.getWizardComponents(), map, lv,
							la));

			wizard.getWindowInfo().setWidth(540);
			wizard.getWindowInfo().setHeight(380);
			wizard.getWindowInfo().setTitle(
					PluginServices.getText(this, "to_annotation"));

			PluginServices.getMDIManager().addWindow(wizard);
			((ProjectDocument) ((View) view).getModel()).setModified(true);

			/*
			 * FLyrVect lv=(FLyrVect)map.getLayers().getActives()[0];
			 * FLyrAnnotation la=new FLyrAnnotation();
			 * la.setSource(lv.getSource());
			 * 
			 * MappingFieldsToAnotation mfta=new MappingFieldsToAnotation(la);
			 * PluginServices.getMDIManager().addView(mfta);
			 * 
			 * if (mfta.isOk()){ map.getLayers().addLayer(la);
			 * map.getLayers().removeLayer(lv); }
			 */

		}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v != null
				&& v instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
			com.iver.cit.gvsig.project.documents.view.gui.View vista = (com.iver.cit.gvsig.project.documents.view.gui.View) v;
			IProjectView model = vista.getModel();
			map = model.getMapContext();
			FLayer[] layers = map.getLayers().getActives();
			if (layers.length == 1) {
				if (layers[0].isAvailable() && layers[0] instanceof FLyrVect) {
					FLyrVect lv = (FLyrVect) layers[0];
					ReadableVectorial src = lv.getSource();
					try {
						if (src == null || src.getShapeType() == FShape.POLYGON
								|| src.getShapeType() == FShape.LINE)
							return false;
						SelectableDataSource sds = lv.getSource()
								.getRecordset();
						if (sds.getFieldCount() > 0)
							return true;
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
		view = PluginServices.getMDIManager().getActiveWindow();

		if (view == null) {
			return false;
		} else if (view instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
			return true;
		} else {
			return false;
		}
	}

}
