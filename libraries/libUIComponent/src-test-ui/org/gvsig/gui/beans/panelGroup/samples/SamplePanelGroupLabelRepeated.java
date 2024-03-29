/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2007 IVER T.I. and Generalitat Valenciana.
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
 */

package org.gvsig.gui.beans.panelGroup.samples;

import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.gvsig.gui.beans.panelGroup.panels.AbstractPanel;

/**
 * <p>
 * Sample of {@link AbstractPanel AbstractPanel}.
 * </p>
 * 
 * @version 23/10/2007
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class SamplePanelGroupLabelRepeated extends AbstractPanel implements
		Serializable {
	private static final long serialVersionUID = 1857806149242138609L;

	/**
	 * <p>
	 * Element for the interface.
	 * </p>
	 */
	private JTextArea jTextArea = null;

	/**
	 * @see AbstractPanel#AbstractPanel()
	 */
	public SamplePanelGroupLabelRepeated() {
		super();
		initialize();
	}

	/**
	 * @see AbstractPanel#AbstractPanel(String, String, String)
	 */
	public SamplePanelGroupLabelRepeated(String id, String label,
			String labelGroup) {
		super(id, label, labelGroup);
		initialize();
	}

	@Override
	protected void initialize() {
		add(new JScrollPane(getJTextArea()));
		setToolTipText(getID());

		setID(Samples_Data.PANELS3_IDS[1]);
		setLabel(Samples_Data.PANELS3_LABELS[1]);
		setLabelGroup(Samples_Data.PANELS3_LABELGROUPS[1]);
		setPreferredSize(new Dimension(Samples_Data.PANELS_DEFAULT_WIDTH,
				Samples_Data.PANELS_DEFAULT_HEIGHT));
		resetChangedStatus();
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea(5, 40);
			jTextArea
					.setText("I\'m a JTextArea object in the \"Panel\" with:\n\nID: "
							+ getID()
							+ "\nLabel: "
							+ getLabel()
							+ "\nLabelGroup: " + getLabelGroup());
			jTextArea.setEditable(false);
			jTextArea.setBackground(Color.GRAY);
		}

		return jTextArea;
	}

	@Override
	public void setID(String id) {
		super.setID(id);

		setToolTipText(getID());
		getJTextArea().setText(
				"I\'m a JTextArea object in the \"Panel\" with:\n\nID: "
						+ getID() + "\nLabel: " + getLabel() + "\nLabelGroup: "
						+ getLabelGroup());
		hasChanged = true;
	}

	@Override
	public void setLabel(String label) {
		super.setLabel(label);

		getJTextArea().setText(
				"I\'m a JTextArea object in the \"Panel\" with:\n\nID: "
						+ getID() + "\nLabel: " + getLabel() + "\nLabelGroup: "
						+ getLabelGroup());
		hasChanged = true;
	}

	@Override
	public void setLabelGroup(String labelGroup) {
		super.setLabelGroup(labelGroup);

		getJTextArea().setText(
				"I\'m a JTextArea object in the \"Panel\" with:\n\nID: "
						+ getID() + "\nLabel: " + getLabel() + "\nLabelGroup: "
						+ getLabelGroup());
		hasChanged = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#accept()
	 */
	public void accept() {
		System.out.println("I'm the IPanel: " + toString()
				+ "\n and I'm executing an 'accept' method.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#apply()
	 */
	public void apply() {
		System.out.println("I'm the IPanel: " + toString()
				+ "\n and I'm executing an 'apply' method.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#cancel()
	 */
	public void cancel() {
		System.out.println("I'm the IPanel: " + toString()
				+ "\n and I'm executing a 'cancel' method.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.panelGroup.panels.IPanel#selected()
	 */
	public void selected() {
		System.out.println("I'm the IPanel: " + toString()
				+ "\n and I've been selected. My information is: " + "\n\tID: "
				+ getID() + "\n\tLABEL_GROUP: " + getLabelGroup()
				+ "\n\tLABEL: " + getLabel() + "\n\tCLASS: " + getClass()
				+ "\n\tMy Preferred Size: " + getPreferredSize()
				+ "\n\tAnd My size: " + getSize());
	}
}
