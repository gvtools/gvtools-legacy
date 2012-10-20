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
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.controls.comboscale.ComboScale;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

/**
 * <p>
 * Configuration panel to set the range of scales in which the labels will be
 * visible.
 * </p>
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Apr 4, 2008
 * 
 */
public class LabelScaleRange extends JPanel implements IWindow, ActionListener {

	private static final long serialVersionUID = -450284029750650479L;
	private static final long[] defaultScales = new long[] { 1000, 2000, 5000,
			10000, 50000, 100000, 200000, 500000, };
	private long maxScale, minScale;
	private ComboScale cmbMaxScale;
	private ComboScale cmbMinScale;
	private JRadioButton rdBtnUseSameRangeThanLayer;
	private JRadioButton rdBtnDontShowLabelWhenZoomed;

	public LabelScaleRange(long minScale, long maxScale) {
		super();
		setLayout(new BorderLayout());
		this.maxScale = maxScale;
		this.minScale = minScale;

		getCmbMinScale().setScale(minScale);
		getCmbMaxScale().setScale(maxScale);
		GridBagLayoutPanel p = new GridBagLayoutPanel();
		p.addComponent(new JLabel(PluginServices.getText(this,
				"specify_the_range_of_scales_at_which_labels_will_be_shown")
				+ ":"));
		p.addComponent(new JBlank(10, 10));
		p.addComponent(getRdUseSameRangeThanLayer());
		p.addComponent(new JBlank(10, 10));
		p.addComponent(getRdDontShowLabelsWhenZoomed());
		p.addComponent(new JBlank(10, 10));
		ButtonGroup group = new ButtonGroup();
		group.add(getRdUseSameRangeThanLayer());
		group.add(getRdDontShowLabelsWhenZoomed());
		getRdUseSameRangeThanLayer().addActionListener(this);
		getRdDontShowLabelsWhenZoomed().addActionListener(this);
		getRdUseSameRangeThanLayer().setSelected(
				maxScale == -1 && minScale == -1);
		getRdDontShowLabelsWhenZoomed().setSelected(
				!getRdUseSameRangeThanLayer().isSelected());
		getCmbMinScale().setEnabled(
				getRdDontShowLabelsWhenZoomed().isSelected());
		getCmbMaxScale().setEnabled(
				getRdDontShowLabelsWhenZoomed().isSelected());

		JPanel aux;
		JLabel l;
		l = new JLabel("    " + PluginServices.getText(this, "out_beyond")
				+ ":", JLabel.RIGHT);
		aux = new JPanel();
		aux.add(getCmbMaxScale());
		// aux.add(new JLabel("("+PluginServices.getText(this,
		// "min_scale")+")"));
		aux.add(new JLabel("(" + PluginServices.getText(this, "max_scale")
				+ ")"));
		p.addComponent(l, aux);

		l = new JLabel(
				"    " + PluginServices.getText(this, "in_beyond") + ":",
				JLabel.RIGHT);
		aux = new JPanel();
		aux.add(getCmbMinScale());
		// aux.add(new JLabel("("+PluginServices.getText(this,
		// "max_scale")+")"));
		aux.add(new JLabel("(" + PluginServices.getText(this, "min_scale")
				+ ")"));
		p.addComponent(l, aux);

		add(new JBlank(10, 10), BorderLayout.WEST);
		add(new JBlank(10, 10), BorderLayout.EAST);
		add(p, BorderLayout.CENTER);
		add(new AcceptCancelPanel(this, this), BorderLayout.SOUTH);
	}

	private JRadioButton getRdDontShowLabelsWhenZoomed() {
		if (rdBtnDontShowLabelWhenZoomed == null) {
			rdBtnDontShowLabelWhenZoomed = new JRadioButton(
					PluginServices
							.getText(this, "dont_show_labels_when_zoomed"));
		}
		return rdBtnDontShowLabelWhenZoomed;
	}

	private JRadioButton getRdUseSameRangeThanLayer() {
		if (rdBtnUseSameRangeThanLayer == null) {
			rdBtnUseSameRangeThanLayer = new JRadioButton(
					PluginServices.getText(this,
							"use_the_same_scale_range_as_the_feature_layer"));
		}
		return rdBtnUseSameRangeThanLayer;
	}

	public long getMaxScale() {
		return maxScale;
	}

	private ComboScale getCmbMaxScale() {
		if (cmbMaxScale == null) {
			cmbMaxScale = new ComboScale();
			TreeSet<Long> ts = new TreeSet<Long>();
			if (maxScale != -1)
				ts.add(maxScale);
			for (int i = 0; i < defaultScales.length; i++) {
				ts.add(defaultScales[i]);
			}
			long[] items = new long[ts.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = ts.first();
				ts.remove(ts.first());
			}
			cmbMaxScale.setItems(items);

		}

		return cmbMaxScale;
	}

	private ComboScale getCmbMinScale() {
		if (cmbMinScale == null) {
			cmbMinScale = new ComboScale();
			TreeSet<Long> ts = new TreeSet<Long>();
			if (minScale != -1)
				ts.add(minScale);
			for (int i = 0; i < defaultScales.length; i++) {
				ts.add(defaultScales[i]);
			}
			long[] items = new long[ts.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = ts.first();
				ts.remove(ts.first());
			}
			cmbMinScale.setItems(items);
		}

		return cmbMinScale;
	}

	public long getMinScale() {
		return minScale;
	}

	public WindowInfo getWindowInfo() {
		WindowInfo wi = new WindowInfo(WindowInfo.MODALDIALOG
				| WindowInfo.RESIZABLE);
		wi.setTitle(PluginServices.getText(this, "scale_range_window_title"));
		wi.setWidth(480);
		wi.setHeight(200);
		return wi;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public void actionPerformed(ActionEvent e) {
		Component c = (Component) e.getSource();
		if (getRdDontShowLabelsWhenZoomed().equals(c)
				|| getRdUseSameRangeThanLayer().equals(c)) {
			getCmbMinScale().setEnabled(
					getRdDontShowLabelsWhenZoomed().isSelected());
			getCmbMaxScale().setEnabled(
					getRdDontShowLabelsWhenZoomed().isSelected());

		}

		if ("OK".equals(e.getActionCommand())) {
			maxScale = (getRdUseSameRangeThanLayer().isSelected()) ? -1
					: getCmbMaxScale().getScale();
			minScale = (getRdUseSameRangeThanLayer().isSelected()) ? -1
					: getCmbMinScale().getScale();
			PluginServices.getMDIManager().closeWindow(this);
		}

		if ("CANCEL".equals(e.getActionCommand())) {
			PluginServices.getMDIManager().closeWindow(this);
		}
	}
}
