/*
 * Created on 01-jul-2005
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
package com.iver.cit.gvsig.geoprocess.wizard;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.JPanel;

import org.cresques.cts.IProjection;
import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.geoprocess.core.gui.GeoProcessingOverlayPanel;
import com.iver.cit.gvsig.geoprocess.impl.buffer.BufferGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.buffer.gui.GeoProcessingBufferPanel;
import com.iver.cit.gvsig.geoprocess.impl.clip.ClipGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.convexhull.ConvexHullGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.convexhull.gui.GeoProcessingConvexHullPanel;
import com.iver.cit.gvsig.geoprocess.impl.difference.DifferenceGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.dissolve.DissolveGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.dissolve.gui.GeoProcessingDissolvePanel;
import com.iver.cit.gvsig.geoprocess.impl.intersection.IntersectionGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.merge.MergeGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.merge.gui.GeoProcessingMergePanel;
import com.iver.cit.gvsig.geoprocess.impl.spatialjoin.SpatialJoinGeoprocessController;
import com.iver.cit.gvsig.geoprocess.impl.spatialjoin.gui.GeoProcessingSpatialjoinPanel;
import com.iver.cit.gvsig.geoprocess.impl.union.UnionGeoprocessController;

/**
 * Container component panel of the Geoprocessing Wizard. It contains all
 * spetialized panels to do geoprocessing. It is an Andami's View (it is added
 * to ANDAMI like a JInternalFrame)
 *
 * @author jmorell, azabala
 */
public class GeoProcessingPanel extends JPanel implements IWindow,
		GeoProcessingWizardIF, GeoProcessingIF {

	/*
	 * AZABALA Inicialmente queria usar el API Wizard de la libreria IVER
	 * UTILES. No obstante, ese API es para construir Wizards estáticos, donde
	 * todas las fases/pantallas son las mismas: 1->2->3->...->FIN.
	 *
	 * El componente de GeoprocessingWizard está pensado para que la pantalla 2
	 * sea distinta, en función de la selección que haya hecho el usuario en la
	 * pantalla 1.
	 *
	 * A falta de construir un API genérica de Wizards (que contemple
	 * bifurcaciones) conservamos la concepción original.
	 */

	private static final long serialVersionUID = 1L;

	private WindowInfo viewInfo = null;

	private GeoProcessingOperationSelectorPanel geoProcessingOperationSelectorPanel = null;

	private GeoProcessingBufferPanel geoProcessingBufferPanel = null;

	// FIXME podriamos reutilizar el mismo panel para todos los
	// geoprocesos
	private GeoProcessingOverlayPanel geoProcessingClipPanel = null;

	private GeoProcessingDissolvePanel geoProcessingDissolvePanel = null;

	private GeoProcessingMergePanel geoProcessingMergePanel = null;

	private GeoProcessingOverlayPanel geoProcessingIntersectPanel = null;

	private GeoProcessingOverlayPanel geoProcessingUnionPanel = null;

	private GeoProcessingOverlayPanel geoProcessingDifferencePanel = null;

	private GeoProcessingSpatialjoinPanel geoProcessingSpatialjoinPanel = null;

	private GeoProcessingConvexHullPanel geoProcessingConvexHullPanel = null;

	private JPanel buttonsPanel = null;

	private JButton closeButton = null;

	private JButton previousButton = null;

	private JButton nextButton = null;

	private FLayers layers = null;
	/**
	 * FIXME No se esta utilizando. Revisar si es necesario
	 */
	private IProjection proj;

	private JPanel mainPanel = null;

	/**
	 * These String constants are used to tells to CardLayout which
	 * panel it must show, in response to user selection of radio button
	 * panel.
	 */
	private final static String CONVEX_HULL = "convex";
	private final static String SPATIAL_JOIN = "spt_join";
	private final static String UNION = "union";
	private final static String INTERSECT = "intersect";
	private final static String DIFFERENCE = "diff";
	private final static String MERGE = "merge";
	private final static String DISSOLVE = "dissolve";
	private final static String CLIP = "clip";
	private final static String BUFFER = "buffer";
	private final static String OP_SELECT = "opselect";

	/**
	 * Constructor
	 * @param layers
	 * @param proj
	 */
	public GeoProcessingPanel(FLayers layers, IProjection proj) {
		super();
		this.layers = layers;
		this.proj = proj;
		initialize();
	}

	// Methods that models public behabiour of wizard component
	/**
	 * It closes geoprocessing wizard dialog
	 */
	public void closeDialog() {
		if (PluginServices.getMainFrame() == null) {
			Container container = getParent();
			Container parentOfContainer = null;
			//TODO This code is used in many classes
			//Reuse it
			while(! (container instanceof Window)){
				parentOfContainer = container.getParent();
				container = parentOfContainer;
			}
			((Window)container).dispose();
		} else {
			PluginServices.getMDIManager().closeWindow(GeoProcessingPanel.this);
		}
	}

	/**
	 * Shows previous wizard step
	 */
	public void previousStep() {
		showOptionSelectionPanel();
		previousButton.setEnabled(false);
		nextButton.setText(PluginServices.getText(this, "Siguiente"));

	}

	private void showBufferPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, BUFFER);
	}

	private void showClipPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, CLIP);
	}

	private void showDissolvePanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, DISSOLVE);
	}

	private void showMergePanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, MERGE);
	}

	private void showIntersectPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, INTERSECT);
	}

	private void showUnionPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, UNION);
	}

	private void showSpatialJoinPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, SPATIAL_JOIN);
	}

	private void showDifferencePanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, DIFFERENCE);
	}

	private void showOptionSelectionPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, OP_SELECT);
	}

	public void nextStep() {
		// 2ª FASE DEL ASISTENTE
		/*
		 * TODO No me gusta como esta planteado, a base de muchos if-else
		 * anidados. REVISAR
		 */
		if (nextButton.getText().equals(
				PluginServices.getText(this, "Siguiente"))) {
			if (geoProcessingOperationSelectorPanel.isBufferSelected()) {// BUFFER
				showBufferPanel();
			} else if (geoProcessingOperationSelectorPanel.isClipSelected()) {// CLIP
				showClipPanel();
			} else if (geoProcessingOperationSelectorPanel.isDissolveSelected()) {
				showDissolvePanel();
			} else if (geoProcessingOperationSelectorPanel.isMergeSelected()) {
				showMergePanel();
			} else if (geoProcessingOperationSelectorPanel
					.isIntersectSelected()) {
				showIntersectPanel();
			} else if (geoProcessingOperationSelectorPanel.isUnionSelected()) {
				showUnionPanel();
			} else if (geoProcessingOperationSelectorPanel
					.isSpatialJoinSelected()) {
				showSpatialJoinPanel();
			} else if (geoProcessingOperationSelectorPanel
					.isDifferenceSelected()) {
				showDifferencePanel();
			} else if (geoProcessingOperationSelectorPanel
					.isConvexHullSelected()) {
				showConvexHullPanel();
			} else {
				previousStep();
				return;
			}
			previousButton.setEnabled(true);
			nextButton.setText(PluginServices.getText(this, "Terminar"));

		} else if (nextButton.getText().equals(
				PluginServices.getText(this, "Terminar"))) {
			boolean closeDialog = false;
			if (geoProcessingOperationSelectorPanel.isBufferSelected()) {
				closeDialog = doBuffer();
			} else if (geoProcessingOperationSelectorPanel.isClipSelected()) {
				closeDialog = doClip();
			} else if (geoProcessingOperationSelectorPanel.isDissolveSelected()) {
				closeDialog = doDissolve();
			} else if (geoProcessingOperationSelectorPanel.isMergeSelected()) {
				closeDialog = doMerge();
			} else if (geoProcessingOperationSelectorPanel
					.isIntersectSelected()) {
				closeDialog = doIntersect();
			} else if (geoProcessingOperationSelectorPanel.isUnionSelected()) {
				closeDialog = doUnion();
			} else if (geoProcessingOperationSelectorPanel
					.isSpatialJoinSelected()) {
				closeDialog = doSpatialJoin();
			} else if (geoProcessingOperationSelectorPanel
					.isConvexHullSelected()) {
				closeDialog = doConvexHull();
			} else if (geoProcessingOperationSelectorPanel
					.isDifferenceSelected()) {
				closeDialog = doDifference();
			}
			if(closeDialog)
				closeDialog();
		}
	}

	private void showConvexHullPanel() {
		((CardLayout)mainPanel.getLayout()).show(mainPanel, CONVEX_HULL);
	}

	private Component getGeoProcessingConvexHullPanel() {
		if (geoProcessingConvexHullPanel == null) {
			geoProcessingConvexHullPanel = new GeoProcessingConvexHullPanel(
					layers);
			geoProcessingConvexHullPanel
					.setName("geoProcessingConvexHullPanel");
			geoProcessingConvexHullPanel
					.setPreferredSize(new java.awt.Dimension(300, 300));
		}
		return geoProcessingConvexHullPanel;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setSize(500, 400);
		this.add(getButtonsPanel(), null);
		this.add(getMainPanel(), null);
		showOptionSelectionPanel();
		previousButton.setEnabled(false);
	}

	private Component getGeoProcessingDifferencePanel() {
		if (geoProcessingDifferencePanel == null) {
			String titleText = PluginServices.getText(this,
					"Diferencia_Introduccion_de_datos");
			geoProcessingDifferencePanel = new GeoProcessingOverlayPanel(
					layers, titleText);
			geoProcessingDifferencePanel
					.setName("geoProcessingDifferencePanel");
		}
		return geoProcessingDifferencePanel;
	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setTitle(PluginServices.getText(this,
					"Herramientas_de_analisis"));
		}
		return viewInfo;
	}

	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}
	/**
	 * This method initializes geoProcessingOperationSelectorPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingOperationSelectorPanel() {
		if (geoProcessingOperationSelectorPanel == null) {
			geoProcessingOperationSelectorPanel = new GeoProcessingOperationSelectorPanel();
			geoProcessingOperationSelectorPanel
					.setName("geoProcessingOperationSelectorPanel");
		}
		return geoProcessingOperationSelectorPanel;
	}

	/**
	 * This method initializes geoProcessingBufferPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingBufferPanel() {
		if (geoProcessingBufferPanel == null) {
			geoProcessingBufferPanel = new GeoProcessingBufferPanel(layers);
			geoProcessingBufferPanel.setName("geoProcessingBufferPanel");
		}
		return geoProcessingBufferPanel;
	}

	/**
	 * This method initializes geoProcessingClipPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingClipPanel() {
		if (geoProcessingClipPanel == null) {
			String titleText = PluginServices.getText(this,
					"Recortar._Introduccion_de_datos")
					+ ":";
			geoProcessingClipPanel = new GeoProcessingOverlayPanel(layers,
					titleText);
			geoProcessingClipPanel.setName("geoProcessingClipPanel");
			// Si no le meto esta línea, no se visualiza el menú. Ver que puede
			// estar pasando ...
			geoProcessingClipPanel.setPreferredSize(new java.awt.Dimension(300,
					300));
		}
		return geoProcessingClipPanel;
	}

	/**
	 * This method initializes geoProcessingDissolvePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingDissolvePanel() {
		if (geoProcessingDissolvePanel == null) {
			geoProcessingDissolvePanel = new GeoProcessingDissolvePanel(layers);
			geoProcessingDissolvePanel.setName("geoProcessingDissolvePanel");
		}
		return geoProcessingDissolvePanel;
	}

	/**
	 * This method initializes geoProcessingMergePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingMergePanel() {
		if (geoProcessingMergePanel == null) {
			geoProcessingMergePanel = new GeoProcessingMergePanel(layers);
			geoProcessingMergePanel.setName("geoProcessingMergePanel");
		}
		return geoProcessingMergePanel;
	}

	/**
	 * This method initializes geoProcessingIntersectPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingIntersectPanel() {
		if (geoProcessingIntersectPanel == null) {
			String titleText = PluginServices.getText(this,
					"Interseccion._Introduccion_de_datos");
			geoProcessingIntersectPanel = new GeoProcessingOverlayPanel(layers,
					titleText);
			geoProcessingIntersectPanel.setName("geoProcessingIntersectPanel");
		}
		return geoProcessingIntersectPanel;
	}

	/**
	 * This method initializes geoProcessingUnionPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingUnionPanel() {
		if (geoProcessingUnionPanel == null) {
			String titleText = PluginServices.getText(this,
					"Union._Introduccion_de_datos");
			geoProcessingUnionPanel = new GeoProcessingOverlayPanel(layers,
					titleText);
			geoProcessingUnionPanel.setName("geoProcessingUnionPanel");
		}
		return geoProcessingUnionPanel;
	}

	/**
	 * This method initializes geoProcessingSpatialjoinPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getGeoProcessingSpatialjoinPanel() {
		if (geoProcessingSpatialjoinPanel == null) {
			geoProcessingSpatialjoinPanel = new GeoProcessingSpatialjoinPanel(
					layers);
			geoProcessingSpatialjoinPanel
					.setName("geoProcessingSpatialjoinPanel");
			// Si no le meto esta línea, no se visualiza el menú. Ver que puede
			// estar pasando ...
			geoProcessingSpatialjoinPanel
					.setPreferredSize(new java.awt.Dimension(300, 300));
		}
		return geoProcessingSpatialjoinPanel;
	}

	/**
	 * This method initializes buttonsPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
			buttonsPanel.setName("buttonsPanel");


			// esto también lo cambiaria...JButton, JButton1, JButton2 no
			buttonsPanel.setBounds(new java.awt.Rectangle(14,353,466,40));
			// dan claridad al codigo
			buttonsPanel.add(getCloseButton(), null);
			buttonsPanel.add(new JPanel(),null); //just a separator;
			buttonsPanel.add(getPreviousButton(), null);
			buttonsPanel.add(getNextButton(), null);
		}
		return buttonsPanel;
	}

	/**
	 * This method initializes closeButton
	 *
	 * @return JButton
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText(PluginServices.getText(this, "Cerrar"));
			closeButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					closeDialog();
				}
			});
		}
		return closeButton;
	}

	/**
	 * This method initializes previousButton
	 *
	 * @return JButton
	 */
	private JButton getPreviousButton() {
		if (previousButton == null) {
			previousButton = new JButton();
			previousButton.setText(PluginServices.getText(this, "Anterior"));
			previousButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							previousStep();
						}
					});
		}
		return previousButton;
	}

	/**
	 * This method initializes nextButton
	 *
	 * @return JButton
	 */
	private JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setText(PluginServices.getText(this, "Siguiente"));
			nextButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					nextStep();
				}
			});
		}
		return nextButton;
	}

	public boolean doBuffer() {
		BufferGeoprocessController controller =
			new BufferGeoprocessController();
		controller.setView(geoProcessingBufferPanel);
		return controller.launchGeoprocess();
	}

	private ShpWriter getShpWriter(SHPLayerDefinition definition) throws Exception {
		ShpWriter writer = new ShpWriter();
		writer.setFile(definition.getFile());
		writer.initialize(definition);
		return writer;
	}

	public boolean doMerge() {
		MergeGeoprocessController controller =
			new MergeGeoprocessController();
		controller.setView(geoProcessingMergePanel);
		return controller.launchGeoprocess();
	}

	public boolean doDissolve() {
		DissolveGeoprocessController controller =
			new DissolveGeoprocessController();
		controller.setView(geoProcessingDissolvePanel);
		return controller.launchGeoprocess();
	}

	public boolean doSpatialJoin() {
		SpatialJoinGeoprocessController controller =
			new SpatialJoinGeoprocessController();
		controller.setView(geoProcessingSpatialjoinPanel);
		return controller.launchGeoprocess();

	}

	public boolean doClip() {
		ClipGeoprocessController controller =
			new ClipGeoprocessController();
		controller.setView(geoProcessingClipPanel);
		return controller.launchGeoprocess();
	}

	// Spatial join con Intersect
	public boolean doIntersect() {
		IntersectionGeoprocessController controller =
			new IntersectionGeoprocessController();
		controller.setView(geoProcessingIntersectPanel);
		return controller.launchGeoprocess();
	}

	public boolean doUnion() {
		UnionGeoprocessController controller =
			new UnionGeoprocessController();
		controller.setView(geoProcessingUnionPanel);
		return controller.launchGeoprocess();
	}


	public boolean doConvexHull() {
		ConvexHullGeoprocessController controller =
			new ConvexHullGeoprocessController();
		controller.setView(geoProcessingConvexHullPanel);
		return controller.launchGeoprocess();
	}

	public boolean doDifference() {
		DifferenceGeoprocessController controller =
			new DifferenceGeoprocessController();
		controller.setView(geoProcessingDifferencePanel);
		return controller.launchGeoprocess();
	}



	/**
	 * This method initializes mainPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new CardLayout());
			mainPanel.setBounds(new java.awt.Rectangle(12,8,469,339));
			mainPanel.setBorder(javax.swing.BorderFactory
					.createEtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
			mainPanel.add(getGeoProcessingOperationSelectorPanel(), OP_SELECT);
			mainPanel.add(getGeoProcessingConvexHullPanel(), CONVEX_HULL);
			mainPanel.add(getGeoProcessingSpatialjoinPanel(), SPATIAL_JOIN);
			mainPanel.add(getGeoProcessingUnionPanel(), UNION);
			mainPanel.add(getGeoProcessingIntersectPanel(), INTERSECT);
			mainPanel.add(getGeoProcessingDifferencePanel(), DIFFERENCE);
			mainPanel.add(getGeoProcessingMergePanel(), MERGE);
			mainPanel.add(getGeoProcessingDissolvePanel(), DISSOLVE);
			mainPanel.add(getGeoProcessingClipPanel(), CLIP);
			mainPanel.add(getGeoProcessingBufferPanel(), BUFFER);
		}
		return mainPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="70,7"
