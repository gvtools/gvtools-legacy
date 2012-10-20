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
package com.iver.cit.gvsig.cad;

import java.util.ArrayList;

import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.fmap.core.NewFConverter;
import org.gvsig.jts.JtsUtil;
import org.gvsig.topology.ui.util.GUIUtil;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This extension only will active when active layer is a linear (dimension = 1)
 * vectorial layer with selected lines.
 * 
 * It smooths the selected lines.
 * 
 * @author Alvaro Zabala
 * 
 */
public class SimplifyGeometry extends Extension {

	public void execute(String actionCommand) {

		try {
			FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
					.getActiveLayerEdited().getLayer();

			VectorialLayerEdited vle = (VectorialLayerEdited) CADExtension
					.getEditionManager().getActiveLayerEdited();
			VectorialEditableAdapter vea = vle.getVEA();

			// FIXME Si el editable adapter coge las selecciones echas antes de
			// entrar en edicion
			ArrayList selectedRow = (ArrayList) CADUtil
					.getSelectedFeatures(vle);
			ArrayList selectedRowAux = new ArrayList();
			vea.startComplexRow();

			for (int i = 0; i < selectedRow.size(); i++) {
				IRowEdited rowEd = (IRowEdited) selectedRow.get(i);
				IFeature feature = (IFeature) rowEd.getLinkedRow();
				IGeometry geometry = feature.getGeometry();

				IGeometry newGeometry = process(geometry, lv.getShapeType());

				DefaultFeature modifiedFeature = (DefaultFeature) feature
						.cloneRow();
				modifiedFeature.setGeometry(newGeometry);

				CADUtil.modifyFeature(vle, rowEd.getIndex(),
						"SIMPLIFY_GEOMETRY", modifiedFeature);

				selectedRowAux.add(new DefaultRowEdited(modifiedFeature,
						IRowEdited.STATUS_MODIFIED, rowEd.getIndex()));

			}// for
			vea.endComplexRow(getName());
			vle.setSelectionCache(VectorialLayerEdited.NOTSAVEPREVIOUS,
					selectedRowAux);

		} catch (ReadDriverException e) {
			String title = PluginServices
					.getText(this, "Error_De_Acceso_Datos");
			String msg = PluginServices.getText(this,
					"Error_accediendo_a_los_datos");
			GUIUtil.getInstance().messageBox(msg, title);
			e.printStackTrace();
		}
	}

	protected IGeometry process(IGeometry originalGeometry, int lyrShapeType) {
		int geometryDimensions = FGeometryUtil.getDimensions(lyrShapeType);
		Geometry jtsGeometry = NewFConverter.toJtsGeometry(originalGeometry);
		Geometry generalizedGeometry = null;
		if (geometryDimensions == 1)
			generalizedGeometry = JtsUtil.douglasPeuckerSimplify(jtsGeometry,
					JtsUtil.GENERALIZATION_FACTOR.doubleValue());
		else
			generalizedGeometry = JtsUtil.topologyPreservingSimplify(
					jtsGeometry, JtsUtil.GENERALIZATION_FACTOR.doubleValue());

		return NewFConverter.toFMap(generalizedGeometry);
	}

	public void initialize() {
		registerIcons();
	}

	protected void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"smooth-geometry",
				this.getClass().getClassLoader()
						.getResource("images/smooth-geometry.gif"));
	}

	protected String getName() {
		return "SIMPLIFY_GEOMETRY";
	}

	/**
	 * Returns if this Edit CAD tool is visible. For this, there must be an
	 * active vectorial editing lyr in the TOC, which geometries' dimension
	 * would must be linear or polygonal, and with at least one selected
	 * geometry.
	 * 
	 */
	public boolean isEnabled() {
		try {
			if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
				if (CADExtension.getEditionManager().getActiveLayerEdited() == null)
					return false;
				FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
						.getActiveLayerEdited().getLayer();
				int geometryDimensions = FGeometryUtil.getDimensions(lv
						.getShapeType());
				if (geometryDimensions <= 0)
					return false;

				return lv.getRecordset().getSelection().cardinality() != 0;
			}
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(), e);
			return false;
		}
		return true;
	}

	public boolean isVisible() {
		if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE)
			return true;
		return false;
	}
}
