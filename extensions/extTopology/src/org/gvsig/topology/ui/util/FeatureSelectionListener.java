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
package org.gvsig.topology.ui.util;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.gvsig.util.GFeatureParameter;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.drivers.featureiterators.FeatureBitsetIterator;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * PointListener which allows to select features with the mouse. The selected
 * feature is passed to a GFeatureParameter instance.
 * 
 * 
 * @author Alvaro Zabala
 * 
 */
public class FeatureSelectionListener implements PointListener {

	private static final int TOLERANCE_IN_PIXELS = 3;

	private final Image img = new ImageIcon(
			MapControl.class.getResource("images/PointSelectCursor.gif"))
			.getImage();

	private Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(img,
			new Point(16, 16), "");

	/**
	 * View with the map where we are gonna to select the feature
	 */
	private View view;
	/**
	 * Paremeter which will have the reference to the selected feature
	 */
	private GFeatureParameter featureParameter;

	private FeatureSelectionCallBack callBack;

	/**
	 * Constructor
	 * 
	 * @param view
	 * @param featureParameter
	 * @param callingParent
	 */
	public FeatureSelectionListener(View view,
			GFeatureParameter featureParameter,
			FeatureSelectionCallBack callBack) {
		super();
		this.view = view;
		this.featureParameter = featureParameter;
		this.callBack = callBack;
	}

	public void point(PointEvent event) throws BehaviorException {

		MapControl mapControl = view.getMapControl();
		ViewPort viewPort = mapControl.getMapContext().getViewPort();
		double tolerance = mapControl.getViewPort().toMapDistance(
				TOLERANCE_IN_PIXELS);
		Point2D wcPoint = viewPort.toMapPoint(event.getPoint());
		List<IFeature> selectedFeatures = new ArrayList<IFeature>();

		List<FLyrVect> containers = featureParameter.getFeatureContainer();
		for (int i = 0; i < containers.size(); i++) {
			FLyrVect lyr = containers.get(i);
			try {
				FBitSet newBitSet = lyr.queryByPoint(wcPoint, tolerance);
				FeatureBitsetIterator featureIterator = new FeatureBitsetIterator(
						newBitSet, lyr.getSource());
				if (featureIterator.hasNext()) {
					IFeature selectedFeature = featureIterator.next();
					selectedFeatures.add(selectedFeature);
				} // if
			} catch (Exception e) {
				e.printStackTrace();
				throw new BehaviorException("Fallo al consultar "
						+ lyr.getName());
			}
		}// for

		int selectedFeaturesSize = selectedFeatures.size();

		if (selectedFeaturesSize == 0) {
			GUIUtil.getInstance()
					.messageBox(
							PluginServices
									.getText(this,
											"No_ha_seleccionado_ningun_elemento.Se_cogera_el_elemento_por_defecto"),
							PluginServices.getText(this, "Warning"));
			this.featureParameter.setValue(featureParameter.getDefaultValue());
		} else if (selectedFeaturesSize > 1) {
			GUIUtil.getInstance().messageBox(
					PluginServices.getText(this,
							"Debe_seleccionar_un_único_elemento"),
					PluginServices.getText(this, "Error"));
			return;
		} else {
			this.featureParameter.setValue(selectedFeatures.get(0));
		}
		mapControl.setPrevTool();
		this.callBack.featureSelected();
	}

	public void pointDoubleClick(PointEvent event) throws BehaviorException {
		point(event);
	}

	public boolean cancelDrawing() {
		return false;
	}

	public Cursor getCursor() {
		return cursor;
	}

}
