/*
 * Created on 04-jul-2005
 *
 * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.geoprocess.wizard;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.iver.andami.PluginServices;

/**
 * 
 * This component is the first step of GeoProcessing Wizard. It allows user to
 * select which geoprocessing operation want to do.
 * 
 * @author jmorell, azabala
 */
public class GeoProcessingOperationSelectorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JLabel jLabel = null;

	private JRadioButton bufferRadioButton = null;

	private JRadioButton dissolveRadioButton = null;

	private JRadioButton mergeRadioButton = null;

	private JRadioButton intersectRadioButton = null;

	private JRadioButton unionRadioButton = null;

	private JRadioButton spatialJoinRadioButton = null;

	private JRadioButton clipRadioButton = null;

	private ButtonGroup buttonGroup = null;

	private JRadioButton convexHullRadioButton = null;

	private JRadioButton differenceRadioButton = null;

	private JPanel mainPanel = null;

	private JLabel imageLabel = null;

	private ImageIcon bufferDescIcon;

	private ImageIcon dissolveDescIcon;

	private ImageIcon mergeDescIcon;

	private ImageIcon intersectDescIcon;

	private ImageIcon unionDescIcon;

	private ImageIcon spatialJoinDescIcon;

	private ImageIcon clipDescIcon;

	private ImageIcon convexHullDescIcon;

	private ImageIcon differenceDescIcon;

	/**
	 * This is the default constructor
	 */
	public GeoProcessingOperationSelectorPanel() {
		super();
		initialize();
	}

	public void changeSelection() {
		if (isBufferSelected()) {
			imageLabel.setIcon(bufferDescIcon);
		} else if (isDissolveSelected()) {
			imageLabel.setIcon(dissolveDescIcon);
		} else if (isMergeSelected()) {
			imageLabel.setIcon(mergeDescIcon);
		} else if (isIntersectSelected()) {
			imageLabel.setIcon(intersectDescIcon);
		} else if (isUnionSelected()) {
			imageLabel.setIcon(unionDescIcon);
		} else if (isClipSelected()) {
			imageLabel.setIcon(clipDescIcon);
		} else if (isSpatialJoinSelected()) {
			imageLabel.setIcon(spatialJoinDescIcon);
		} else if (isDifferenceSelected()) {
			imageLabel.setIcon(differenceDescIcon);
		} else if (isConvexHullSelected()) {
			imageLabel.setIcon(convexHullDescIcon);
		}
	}

	public boolean isBufferSelected() {
		return bufferRadioButton.isSelected();
	}

	public boolean isDissolveSelected() {
		return dissolveRadioButton.isSelected();
	}

	public boolean isMergeSelected() {
		return mergeRadioButton.isSelected();
	}

	public boolean isIntersectSelected() {
		return intersectRadioButton.isSelected();
	}

	public boolean isUnionSelected() {
		return unionRadioButton.isSelected();
	}

	public boolean isClipSelected() {
		return clipRadioButton.isSelected();
	}

	public boolean isSpatialJoinSelected() {
		return spatialJoinRadioButton.isSelected();
	}

	public boolean isDifferenceSelected() {
		return differenceRadioButton.isSelected();
	}

	public boolean isConvexHullSelected() {
		return convexHullRadioButton.isSelected();
	}

	private void initializeImages() {

		bufferDescIcon = PluginServices.getIconTheme().get("buffered-desc");
		dissolveDescIcon = PluginServices.getIconTheme().get("dissolved-desc");
		mergeDescIcon = PluginServices.getIconTheme().get("merge-desc");
		intersectDescIcon = PluginServices.getIconTheme().get("intersect-desc");
		unionDescIcon = PluginServices.getIconTheme().get("union-desc");
		spatialJoinDescIcon = PluginServices.getIconTheme().get(
				"spatialjoin-desc");
		clipDescIcon = PluginServices.getIconTheme().get("clip-desc");
		convexHullDescIcon = PluginServices.getIconTheme().get(
				"convexhull-desc");
		differenceDescIcon = PluginServices.getIconTheme().get(
				"difference-desc");

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {

		initializeImages();
		imageLabel = new JLabel();
		imageLabel.setText("");
		imageLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
		imageLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
		imageLabel.setIcon(bufferDescIcon);
		imageLabel.setBounds(new java.awt.Rectangle(5, 44, 271, 328));
		jLabel = new JLabel();
		this.setLayout(null);
		this.setBounds(new java.awt.Rectangle(0, 0, 486, 377));
		this.add(jLabel, null);
		this.add(imageLabel, null);
		jLabel.setText(PluginServices.getText(this,
				"Elija_una_herramienta_de_analisis") + ":");
		this.add(getMainPanel(), null);

		jLabel.setBounds(new java.awt.Rectangle(4, 5, 471, 32));
		confButtonGroup();
		getBufferRadioButton().setSelected(true);
	}

	private void confButtonGroup() {
		if (buttonGroup == null) {
			buttonGroup = new ButtonGroup();
			buttonGroup.add(getBufferRadioButton());
			buttonGroup.add(getDissolveRadioButton());
			buttonGroup.add(getMergeRadioButton());
			buttonGroup.add(getIntersectRadioButton());
			buttonGroup.add(getUnionRadioButton());
			buttonGroup.add(getSpatialJoinRadioButton());
			buttonGroup.add(getClipRadioButton());
			buttonGroup.add(getConvexHullRadioButton());
			buttonGroup.add(getDifferenceRadioButton());

		}
	}

	/**
	 * This method initializes bufferRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getBufferRadioButton() {
		if (bufferRadioButton == null) {
			bufferRadioButton = new JRadioButton();
			bufferRadioButton.setText(PluginServices.getText(this,
					"Area_de_influencia"));
			bufferRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return bufferRadioButton;
	}

	/**
	 * This method initializes clipRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getClipRadioButton() {
		if (clipRadioButton == null) {
			clipRadioButton = new JRadioButton();
			clipRadioButton.setText(PluginServices.getText(this, "Recortar"));
			clipRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}
			});
		}
		return clipRadioButton;
	}

	/**
	 * This method initializes dissolveRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDissolveRadioButton() {
		if (dissolveRadioButton == null) {
			dissolveRadioButton = new JRadioButton();
			dissolveRadioButton.setText(PluginServices
					.getText(this, "Disolver"));
			dissolveRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return dissolveRadioButton;
	}

	/**
	 * This method initializes mergeRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getMergeRadioButton() {
		if (mergeRadioButton == null) {
			mergeRadioButton = new JRadioButton();
			mergeRadioButton.setText(PluginServices.getText(this, "Juntar"));
			mergeRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return mergeRadioButton;
	}

	/**
	 * This method initializes intersectRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getIntersectRadioButton() {
		if (intersectRadioButton == null) {
			intersectRadioButton = new JRadioButton();
			intersectRadioButton.setText(PluginServices.getText(this,
					"Interseccion"));
			intersectRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return intersectRadioButton;
	}

	/**
	 * This method initializes unionRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getUnionRadioButton() {
		if (unionRadioButton == null) {
			unionRadioButton = new JRadioButton();
			unionRadioButton.setText(PluginServices.getText(this, "Union"));
			unionRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return unionRadioButton;
	}

	/**
	 * This method initializes spatialJoinRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getSpatialJoinRadioButton() {
		if (spatialJoinRadioButton == null) {
			spatialJoinRadioButton = new JRadioButton();
			spatialJoinRadioButton.setText(PluginServices.getText(this,
					"Enlace_espacial"));
			spatialJoinRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return spatialJoinRadioButton;
	}

	/**
	 * This method initializes convexHullRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getConvexHullRadioButton() {
		if (convexHullRadioButton == null) {
			convexHullRadioButton = new JRadioButton();
			convexHullRadioButton.setText(PluginServices.getText(this,
					"Convex_Hull"));
			convexHullRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return convexHullRadioButton;
	}

	/**
	 * This method initializes differenceRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDifferenceRadioButton() {
		if (differenceRadioButton == null) {
			differenceRadioButton = new JRadioButton();
			differenceRadioButton.setText(PluginServices.getText(this,
					"Diferencia"));
			differenceRadioButton.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					changeSelection();
				}

			});
		}
		return differenceRadioButton;
	}

	/**
	 * This method initializes mainPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel
					.setLayout(new BoxLayout(getMainPanel(), BoxLayout.Y_AXIS));
			mainPanel.setBounds(new java.awt.Rectangle(283, 44, 192, 328));
			mainPanel.add(getBufferRadioButton(), null);
			mainPanel.add(getDissolveRadioButton(), null);
			mainPanel.add(getClipRadioButton(), null);
			mainPanel.add(getIntersectRadioButton(), null);
			mainPanel.add(getMergeRadioButton(), null);
			mainPanel.add(getUnionRadioButton(), null);
			mainPanel.add(getSpatialJoinRadioButton(), null);
			mainPanel.add(getConvexHullRadioButton(), null);
			mainPanel.add(getDifferenceRadioButton(), null);
		}
		return mainPanel;
	}
} // @jve:decl-index=0:visual-constraint="24,7"
