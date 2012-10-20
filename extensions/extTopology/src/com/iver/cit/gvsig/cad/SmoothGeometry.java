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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.gvsig.fmap.core.FGeometryUtil;
import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.topology.ui.util.BoxLayoutPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.FCurve;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * Smooths selected geometries (with dimension 1 or 2) applying the specified
 * curve.
 * 
 * Supported curves are:
 * <ol>
 * <li>BEZIER</li>
 * <li>B_SPLINE</li>
 * <li>CARDINAL SPLINE</li>
 * <li>CATMULLROM SPLINE</li>
 * <li>CUBIC B_SPLINE</li>
 * <li>LAGRANGE</li>
 * <li>NATURAL CUBIC SPLINE</li>
 * <li>NURBS</li
 * </ol>
 * 
 * @author Alvaro Zabala
 * 
 */
public class SmoothGeometry extends SimplifyGeometry {

	int selectedCurveOption = FCurve.B_SPLINE;

	public void execute(String actionCommand) {

		CurveOptionPanel optionPanel = new CurveOptionPanel();
		PluginServices.getMDIManager().addWindow(optionPanel);
		selectedCurveOption = optionPanel.selectedOption();
		if (optionPanel.isAccepted())
			super.execute(actionCommand);
	}

	protected IGeometry process(IGeometry originalGeometry, int lyrShapeType) {
		return FGeometryUtil.smoothGeometry(originalGeometry,
				selectedCurveOption);
	}

	protected String getName() {
		return "SMOOTH_CURVE";
	}

	protected void registerIcons() {
		PluginServices.getIconTheme().registerDefault(
				"curve-geometry",
				this.getClass().getClassLoader()
						.getResource("images/smooth-curve.png"));
	}

	class CurveOptionPanel extends BoxLayoutPanel implements IWindow {
		private static final long serialVersionUID = -5996796998659896140L;

		private JComboBox curveTypes;

		String[] curveTypesNames = { PluginServices.getText(this, "BEZIER"),
				PluginServices.getText(this, "B_SPLINE"),
				PluginServices.getText(this, "CARDINAL_SPLINE"),
				PluginServices.getText(this, "CATMULLROM_SPLINE"),
				PluginServices.getText(this, "CUBIC_BSPLINE"),
				PluginServices.getText(this, "LAGRANGE_CURVE"),
				PluginServices.getText(this, "NATURAL_CUBIC_SPLINE"),
				PluginServices.getText(this, "NURB_SPLINE") };

		String title = PluginServices.getText(this, "SELECCIONAR_CURVA");

		private boolean accepted = false;

		private WindowInfo viewInfo;

		CurveOptionPanel() {

			this.addRow(new JComponent[] { new JLabel(title) });

			this.addComponent(PluginServices.getText(this, "CURVE_TYPE"),
					getCurveTypesCb());

			ActionListener cancelAction = new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					accepted = false;
					PluginServices.getMDIManager().closeWindow(
							CurveOptionPanel.this);
				}
			};

			ActionListener okAction = new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					accepted = true;
					PluginServices.getMDIManager().closeWindow(
							CurveOptionPanel.this);
				}
			};

			AcceptCancelPanel acceptCancelPnl = new AcceptCancelPanel(okAction,
					cancelAction);
			this.addRow(new JComponent[] { acceptCancelPnl }, 600, 50);
		}

		public boolean isAccepted() {
			return accepted;
		}

		public int selectedOption() {
			return getCurveTypesCb().getSelectedIndex();
		}

		JComboBox getCurveTypesCb() {
			if (curveTypes == null) {
				curveTypes = new JComboBox();
				DefaultComboBoxModel defaultModel = new DefaultComboBoxModel(
						curveTypesNames);
				curveTypes.setModel(defaultModel);
			}
			return curveTypes;
		}

		public WindowInfo getWindowInfo() {
			if (viewInfo == null) {
				viewInfo = new WindowInfo(WindowInfo.MODALDIALOG
						| WindowInfo.PALETTE);
				viewInfo.setTitle(title);
				viewInfo.setWidth(550);
				viewInfo.setHeight(145);
			}
			return viewInfo;
		}

		public Object getWindowProfile() {
			return WindowInfo.DIALOG_PROFILE;
		}

	}

}
