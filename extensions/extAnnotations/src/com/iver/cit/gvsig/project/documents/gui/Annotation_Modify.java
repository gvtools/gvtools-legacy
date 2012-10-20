/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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

package com.iver.cit.gvsig.project.documents.gui;

import jwizardcomponent.FinishAction;
import jwizardcomponent.JWizardComponents;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.Annotation_Mapping;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.Annotation_Legend;
import com.iver.cit.gvsig.gui.panels.annotation.ConfigureLabel;

/**
 * Dialog to create a new annotation layer.
 * 
 * @author Vicente Caballero Navarro
 */
public class Annotation_Modify extends FinishAction {
	private JWizardComponents myWizardComponents;
	private Annotation_Layer layerAnnotation;

	public Annotation_Modify(JWizardComponents wizardComponents,
			MapContext map, Annotation_Layer layerAnnotation) {
		super(wizardComponents);
		this.layerAnnotation = layerAnnotation;
		myWizardComponents = wizardComponents;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void performAction() {
		myWizardComponents.getFinishButton().setEnabled(false);

		Annotation_FieldSelect panel1 = (Annotation_FieldSelect) myWizardComponents
				.getWizardPanel(0);
		Annotation_ConfigureLabel panel2 = (Annotation_ConfigureLabel) myWizardComponents
				.getWizardPanel(1);

		SelectableDataSource source;
		Annotation_Mapping mapping = new Annotation_Mapping();

		try {
			source = this.layerAnnotation.getRecordset();

			mapping.setColumnText(source.getFieldIndexByName(panel1.getField()));

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
			// AttrInTableLabelingStrategy labeling = new
			// AttrInTableLabelingStrategy();
			((Annotation_Legend) layerAnnotation.getLegend()).setUnits(panel2
					.getCmbUnits().getSelectedUnitIndex());
			// labeling.setUnit(panel2.getCmbUnits().getSelectedUnitIndex());
			// this.layerAnnotation.setLabelingStrategy(labeling);
			// ((FSymbol)
			// this.layerAnnotation.getLegend().getDefaultSymbol()).setFontSizeInPixels(panel2.sizeUnitsInPixels());

			this.layerAnnotation.deleteSpatialIndex();

			if (!panel2.getFontFieldName().equals(
					ConfigureLabel.TEXT_FOR_DEFAULT_VALUE)) {
				mapping.setColumnTypeFont(source.getFieldIndexByName(panel2
						.getFontFieldName()));
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}

		try {
			this.layerAnnotation.setMapping(mapping);
		} catch (LegendLayerException e) {
			NotificationManager.addError(e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}

		this.myWizardComponents.getCancelAction().performAction();
	}
}
