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

/* CVS MESSAGES:
 *
 * $Id: PlacementProperties.java 15674 2007-10-30 16:39:02Z jdominguez $
 * $Log$
 * Revision 1.7  2007-04-19 14:22:29  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2007/04/13 12:43:08  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2007/04/13 12:10:56  jaume
 * *** empty log message ***
 *
 * Revision 1.4  2007/04/12 16:01:32  jaume
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/28 15:38:56  jaume
 * GUI for lines, points and polygons
 *
 * Revision 1.2  2007/03/09 11:25:00  jaume
 * Advanced symbology (start committing)
 *
 * Revision 1.1.2.2  2007/02/09 11:00:03  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2007/02/01 12:12:41  jaume
 * theme manager window and all its components are now dynamic
 *
 *
 */
package org.gvsig.symbology.gui.layerproperties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.cresques.cts.ProjectionUtils;
import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JBlank;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.symbology.fmap.labeling.GeneralLabelingStrategy;
import org.gvsig.symbology.fmap.labeling.PlacementManager;
import org.gvsig.symbology.fmap.labeling.placements.MultiShapePlacementConstraints;
import org.gvsig.symbology.fmap.labeling.placements.PointPlacementConstraints;
import org.gvsig.symbology.fmap.styles.PointLabelPositioneer;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.styles.ArrowDecoratorStyle;
import com.iver.cit.gvsig.fmap.core.styles.IStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.DefaultLabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.gui.styling.AbstractStyleSelectorFilter;
import com.iver.cit.gvsig.gui.styling.StylePreviewer;
import com.iver.cit.gvsig.gui.styling.StyleSelector;
import com.iver.utiles.swing.JComboBox;

/**
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 * 
 */
public class PlacementProperties extends JPanel implements
		IPlacementProperties, ActionListener {
	private static final long serialVersionUID = 1022470370547576765L;
	private PointLabelPositioneer defaultPointStyle = new PointLabelPositioneer(
			new byte[] { 2, 2, 1, 3, 2, 3, 3, 2 }, PluginServices.getText(this,
					"prefer_top_right_all_allowed"));
	private PointLabelPositioneer pointStyle = null;
	private int shapeType;
	private JPanel pnlContent = null;
	private JPanel pnlCenter = null;
	IPlacementConstraints constraints;
	private GridBagLayoutPanel orientationPanel;
	private DuplicateLayersMode duplicateLabelsMode;
	private JRadioButton rdBtnHorizontal;
	private JRadioButton rdBtnParallel;
	private JRadioButton rdBtnFollowingLine;
	private JRadioButton rdBtnPerpendicular;
	private JPanel positionPanel;
	private MiniMapContext preview;
	private ArrayList<String> linePositionItem = new ArrayList<String>();

	private JPanel polygonSettingsPanel;
	private JRadioButton rdBtnAlwaysHorizontal;
	private JRadioButton rdBtnAlwaysStraight;
	private GridBagLayoutPanel pointSettingsPanel;
	private JRadioButton rdBtnOffsetLabelHorizontally;
	private JRadioButton rdBtnOffsetLabelOnTopPoint;
	private StylePreviewer stylePreview;
	private JButton btnChangeLocation;
	private JPanel locationPanel;
	private JComboBox cmbLocationAlongLines;
	private JRadioButton chkBellow;
	private JRadioButton chkOnTheLine;
	private JRadioButton chkAbove;
	private JComboBox cmbOrientationSystem;
	private JLabelHTML lblPointPosDesc = new JLabelHTML(""); // pointStyle.getDescription());
	IPlacementConstraints oldConstraints;
	private JCheckBox chkFitInsidePolygon;
	private boolean fireEvent = false;

	/**
	 * Constructs a new panel for PlacementProperties.
	 * 
	 * @param constraints
	 *            , if not null this parameters is used to fill the panel. If it
	 *            is null this PlacementProperties constructor creates a new
	 *            default one that is ready to be used for the shapetype passed
	 *            as second parameter
	 * 
	 * @param shapeType
	 *            , defines the target shapetype of the IPlacementConstraints
	 *            obtained;
	 * @throws ReadDriverException
	 */
	public PlacementProperties(IPlacementConstraints constraints, int shapeType)
			throws ReadDriverException {
		initialize(constraints, shapeType, getPnlDuplicateLabels());
	}

	PlacementProperties(IPlacementConstraints costraints, int shapeType,
			DuplicateLayersMode duplicatesMode) {

		initialize(costraints, shapeType, duplicatesMode);
		refreshComponents();
	}

	private void refreshComponents() {
		fireEvent = false;
		getChkOnTheLine().setSelected(constraints.isOnTheLine());
		getChkAbove().setSelected(constraints.isAboveTheLine());
		getChkBelow().setSelected(constraints.isBelowTheLine());

		getRdBtnHorizontal().setSelected(constraints.isHorizontal());
		getRdBtnParallel().setSelected(constraints.isParallel());
		getRdBtnFollowingLine().setSelected(constraints.isFollowingLine());
		getRdBtnPerpendicular().setSelected(constraints.isPerpendicular());

		getCmbOrientationSystem().setSelectedIndex(
				constraints.isPageOriented() ? 1 : 0);

		// points mode
		if (constraints.isOnTopOfThePoint()) {
			getRdOffsetLabelOnTopPoint().setSelected(true);
		} else if (constraints.isAroundThePoint()) {
			getRdOffsetLabelHorizontally().setSelected(true);
		}

		// lines mode
		if (constraints.isAtTheBeginingOfLine()) {
			getCmbLocationAlongLines().setSelectedIndex(1);
		} else if (constraints.isInTheMiddleOfLine()) {
			getCmbLocationAlongLines().setSelectedIndex(0);
		} else if (constraints.isAtTheEndOfLine()) {
			getCmbLocationAlongLines().setSelectedIndex(2);
		} else if (constraints.isAtBestOfLine()) {
			getCmbLocationAlongLines().setSelectedIndex(3);
		}

		// polygon mode
		getChkFitInsidePolygon().setSelected(constraints.isFitInsidePolygon());
		if (constraints.isHorizontal()) {
			getRdBtnAlwaysHorizontal().setSelected(true);
		} else if (constraints.isParallel()) {
			getRdBtnAlwaysStraight().setSelected(true);
		}

		// if(constraints.isFollowingLine()){
		// setComponentEnabled(getPositionPanel(), false);
		// }

		// duplicates mode
		int dupMode = constraints.getDuplicateLabelsMode();
		duplicateLabelsMode.setMode(dupMode);
		fireEvent = true;
		changeIcon();
	}

	private void initialize(IPlacementConstraints constraints, int shapeType,
			DuplicateLayersMode duplicatesMode) {

		this.duplicateLabelsMode = duplicatesMode;
		this.shapeType = shapeType;
		this.oldConstraints = constraints;
		// this.constraints = constraints != null ?
		// PlacementManager.createPlacementConstraints(constraints.getXMLEntity())
		// :
		// PlacementManager.createPlacementConstraints(shapeType);
		if (constraints != null) {
			this.constraints = PlacementManager
					.createPlacementConstraints(constraints.getXMLEntity());
			if (constraints instanceof PointPlacementConstraints) {
				PointLabelPositioneer positioneer = ((PointPlacementConstraints) constraints)
						.getPositioneer();
				if (positioneer != null)
					this.setPointStyle(positioneer);
				this.lblPointPosDesc.setText(this.getPointStyle()
						.getDescription());
			}
		} else {
			this.constraints = PlacementManager
					.createPlacementConstraints(shapeType);
		}

		linePositionItem.add(PluginServices.getText(this, "in_the_middle"));
		linePositionItem.add(PluginServices.getText(this, "at_begin"));
		linePositionItem.add(PluginServices.getText(this, "at_end"));
		linePositionItem.add(PluginServices.getText(this, "at_best"));

		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(410, 380));
		this.add(getPnlContent(), BorderLayout.CENTER);
	}

	public WindowInfo getWindowInfo() {
		WindowInfo viewInfo = new WindowInfo(WindowInfo.MODALDIALOG
				| WindowInfo.RESIZABLE);
		viewInfo.setWidth(getWidth());
		viewInfo.setHeight(getHeight());
		viewInfo.setTitle(PluginServices.getText(this, "placement_properties"));
		return viewInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private JPanel getPnlContent() {
		if (pnlContent == null) {
			pnlContent = new JPanel();
			pnlContent.setLayout(new BorderLayout());
			pnlContent.add(getPnlCenter(), java.awt.BorderLayout.CENTER);
			pnlContent
					.add(getPnlDuplicateLabels(), java.awt.BorderLayout.SOUTH);
		}
		return pnlContent;
	}

	private DuplicateLayersMode getPnlDuplicateLabels() {
		if (duplicateLabelsMode == null) {
			duplicateLabelsMode = new DuplicateLayersMode();

		}

		return duplicateLabelsMode;
	}

	private JPanel getPnlCenter() {
		if (pnlCenter == null) {
			pnlCenter = new JPanel();
			switch (shapeType % FShape.Z) {
			case FShape.POINT:
			case FShape.MULTIPOINT: // TODO (09/01/08) is this correct?? if not
									// fix it also in PlacementManager,
									// MarkerPlacementAroundPoint
				pnlCenter.setBorder(BorderFactory.createTitledBorder(null,
						PluginServices.getText(this, "point_settings")));
				pnlCenter.add(getPointSettingsPanel());
				break;
			case FShape.LINE:
				pnlCenter.setLayout(new BorderLayout());
				pnlCenter.setBorder(BorderFactory.createTitledBorder(null,
						PluginServices.getText(this, "line_settings")));
				JPanel aux = new JPanel(new GridLayout(1, 2));
				aux.add(getOrientationPanel());
				aux.add(getPositionPanel());
				pnlCenter.add(aux, BorderLayout.CENTER);
				pnlCenter.add(getLocationPanel(), BorderLayout.SOUTH);

				break;

			case FShape.POLYGON:
				pnlCenter.setLayout(new BorderLayout());
				pnlCenter.setBorder(BorderFactory.createTitledBorder(null,
						PluginServices.getText(this, "polygon_settings")));
				pnlCenter.add(getPolygonSettingsPanel(), BorderLayout.CENTER);
				break;

			default:
				break;
			}
		}
		return pnlCenter;
	}

	private JPanel getLocationPanel() {
		if (locationPanel == null) {
			locationPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
			locationPanel.setBorder(BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "location")));
			locationPanel.add(new JLabel(PluginServices.getText(this,
					"location_along_the_lines") + ":"));
			locationPanel.add(getCmbLocationAlongLines());
		}

		return locationPanel;
	}

	private JComboBox getCmbLocationAlongLines() {
		if (cmbLocationAlongLines == null) {
			cmbLocationAlongLines = new JComboBox(
					(String[]) linePositionItem
							.toArray(new String[linePositionItem.size()]));
			cmbLocationAlongLines.addActionListener(this);
		}

		return cmbLocationAlongLines;
	}

	private PointLabelPositioneer getPointStyle() {
		if (pointStyle == null) {
			pointStyle = defaultPointStyle;
			lblPointPosDesc.setText(pointStyle.getDescription());
		}
		return pointStyle;
	}

	private void setPointStyle(PointLabelPositioneer pointStyle) {
		this.pointStyle = pointStyle;
	}

	private GridBagLayoutPanel getPointSettingsPanel() {
		if (pointSettingsPanel == null) {
			pointSettingsPanel = new GridBagLayoutPanel();
			pointSettingsPanel.addComponent(getRdOffsetLabelHorizontally());
			JPanel aux = new JPanel();
			aux.add(getStylePreviewer());

			JPanel aux2 = new JPanel();
			aux2.setLayout(new BoxLayout(aux2, BoxLayout.Y_AXIS));
			aux2.add(lblPointPosDesc);
			aux2.add(new JBlank(20, 5));
			aux2.add(getBtnChangeLocation());

			aux.add(aux2);
			pointSettingsPanel.addComponent("", aux);
			pointSettingsPanel.addComponent(
					"",
					new JLabel(PluginServices.getText(this,
							"label-point-priority-help")));
			pointSettingsPanel.addComponent(getRdOffsetLabelOnTopPoint());
			ButtonGroup group = new ButtonGroup();
			group.add(getRdOffsetLabelHorizontally());
			group.add(getRdOffsetLabelOnTopPoint());
		}

		return pointSettingsPanel;
	}

	private JButton getBtnChangeLocation() {
		if (btnChangeLocation == null) {
			btnChangeLocation = new JButton(PluginServices.getText(this,
					"change_location"));
			btnChangeLocation.addActionListener(this);
		}

		return btnChangeLocation;
	}

	private StylePreviewer getStylePreviewer() {
		if (stylePreview == null) {
			stylePreview = new StylePreviewer();
			stylePreview.setStyle(getPointStyle());
			stylePreview.setPreferredSize(new Dimension(80, 80));
		}
		return stylePreview;
	}

	private JRadioButton getRdOffsetLabelOnTopPoint() {
		if (rdBtnOffsetLabelOnTopPoint == null) {
			rdBtnOffsetLabelOnTopPoint = new JRadioButton(
					PluginServices.getText(this,
							"offset_labels_on_top_of_the_points"));

		}

		return rdBtnOffsetLabelOnTopPoint;
	}

	private JRadioButton getRdOffsetLabelHorizontally() {
		if (rdBtnOffsetLabelHorizontally == null) {
			rdBtnOffsetLabelHorizontally = new JRadioButton(
					PluginServices.getText(this, "offset_labels_horizontally"));

		}

		return rdBtnOffsetLabelHorizontally;
	}

	private JPanel getPolygonSettingsPanel() {
		if (polygonSettingsPanel == null) {
			polygonSettingsPanel = new JPanel(new BorderLayout(10, 10));
			JPanel aux = new JPanel();
			aux.setLayout(new BoxLayout(aux, BoxLayout.Y_AXIS));
			aux.add(new JBlank(10, 10));
			aux.add(getRdBtnAlwaysHorizontal());
			aux.add(new JBlank(10, 10));
			aux.add(getRdBtnAlwaysStraight());
			aux.add(new JBlank(10, 50));

			polygonSettingsPanel.add(getPreview(), BorderLayout.CENTER);
			polygonSettingsPanel.add(aux, BorderLayout.EAST);
			ButtonGroup group = new ButtonGroup();
			group.add(getRdBtnAlwaysHorizontal());
			group.add(getRdBtnAlwaysStraight());
			polygonSettingsPanel.add(getChkFitInsidePolygon(),
					BorderLayout.SOUTH);
		}

		return polygonSettingsPanel;
	}

	private JCheckBox getChkFitInsidePolygon() {
		if (chkFitInsidePolygon == null) {
			chkFitInsidePolygon = new JCheckBox(PluginServices.getText(this,
					"fit_inside_polygon"));

		}

		return chkFitInsidePolygon;
	}

	private JRadioButton getRdBtnAlwaysStraight() {
		if (rdBtnAlwaysStraight == null) {
			rdBtnAlwaysStraight = new JRadioButton(PluginServices.getText(this,
					"always_straight"));
			rdBtnAlwaysStraight.addActionListener(this);
		}

		return rdBtnAlwaysStraight;
	}

	private JRadioButton getRdBtnAlwaysHorizontal() {
		if (rdBtnAlwaysHorizontal == null) {
			rdBtnAlwaysHorizontal = new JRadioButton(PluginServices.getText(
					this, "always_horizontal"));
			rdBtnAlwaysHorizontal.addActionListener(this);
		}

		return rdBtnAlwaysHorizontal;
	}

	private JPanel getPositionPanel() {
		if (positionPanel == null) {
			positionPanel = new JPanel(new BorderLayout());
			positionPanel.setBorder(BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "position")));
			GridBagLayoutPanel aux2 = new GridBagLayoutPanel();
			JPanel aux = new JPanel(new GridLayout(4, 1));

			aux.add(getChkAbove());
			getChkAbove().setSelected(true);
			aux.add(getChkOnTheLine());
			aux.add(getChkBelow());

			ButtonGroup group = new ButtonGroup();
			group.add(getChkAbove());
			group.add(getChkOnTheLine());
			group.add(getChkBelow());

			aux2.addComponent(aux);
			aux2.addComponent(
					PluginServices.getText(this, "orientation_system"),
					getCmbOrientationSystem());
			positionPanel.add(getPreview(), BorderLayout.CENTER);
			positionPanel.add(aux2, BorderLayout.SOUTH);
		}
		return positionPanel;
	}

	private Component getPreview() {
		if (preview == null) {
			preview = new MiniMapContext(shapeType);
		}
		return preview;
	}

	private JComboBox getCmbOrientationSystem() {
		if (cmbOrientationSystem == null) {
			cmbOrientationSystem = new JComboBox(new String[] {
					PluginServices.getText(this, "line"),
					PluginServices.getText(this, "page") });
			cmbOrientationSystem.setSelectedIndex(0);
			cmbOrientationSystem.addActionListener(this);
		}

		return cmbOrientationSystem;
	}

	private JRadioButton getChkBelow() {
		if (chkBellow == null) {
			chkBellow = new JRadioButton(PluginServices.getText(this, "below"));
			chkBellow.addActionListener(this);
		}
		return chkBellow;
	}

	private JRadioButton getChkOnTheLine() {
		if (chkOnTheLine == null) {
			chkOnTheLine = new JRadioButton(PluginServices.getText(this,
					"on_the_line"));
			chkOnTheLine.addActionListener(this);
		}
		return chkOnTheLine;
	}

	private JRadioButton getChkAbove() {
		if (chkAbove == null) {
			chkAbove = new JRadioButton(PluginServices.getText(this, "above"));
			chkAbove.addActionListener(this);
		}
		return chkAbove;
	}

	private GridBagLayoutPanel getOrientationPanel() {
		if (orientationPanel == null) {
			orientationPanel = new GridBagLayoutPanel();
			orientationPanel.setBorder(BorderFactory.createTitledBorder(null,
					PluginServices.getText(this, "orientation")));
			orientationPanel.addComponent(getRdBtnHorizontal());
			orientationPanel.addComponent(getRdBtnParallel());
			orientationPanel.addComponent(getRdBtnFollowingLine());
			orientationPanel.addComponent(getRdBtnPerpendicular());
			ButtonGroup group = new ButtonGroup();
			group.add(getRdBtnHorizontal());
			group.add(getRdBtnParallel());
			group.add(getRdBtnFollowingLine());
			group.add(getRdBtnPerpendicular());

		}
		return orientationPanel;
	}

	private JRadioButton getRdBtnParallel() {
		if (rdBtnParallel == null) {
			rdBtnParallel = new JRadioButton(PluginServices.getText(this,
					"parallel"));

			rdBtnParallel.addActionListener(this);
		}
		return rdBtnParallel;
	}

	private JRadioButton getRdBtnFollowingLine() {
		if (rdBtnFollowingLine == null) {
			rdBtnFollowingLine = new JRadioButton(PluginServices.getText(this,
					"following_line"));
			rdBtnFollowingLine.addActionListener(this);
		}
		return rdBtnFollowingLine;
	}

	private JRadioButton getRdBtnPerpendicular() {
		if (rdBtnPerpendicular == null) {
			rdBtnPerpendicular = new JRadioButton(PluginServices.getText(this,
					"perpedicular"));
			rdBtnPerpendicular.addActionListener(this);
		}
		return rdBtnPerpendicular;
	}

	private JRadioButton getRdBtnHorizontal() {
		if (rdBtnHorizontal == null) {
			rdBtnHorizontal = new JRadioButton(PluginServices.getText(this,
					"horizontal"));
			rdBtnHorizontal.addActionListener(this);
		}
		return rdBtnHorizontal;
	}

	void applyConstraints() {
		int mode = 0;

		switch (shapeType % FShape.Z) {
		case FShape.POINT:
		case FShape.MULTIPOINT: // TODO (09/01/08) is this correct? if not fix
								// it also in PlacementManager,
								// MarkerPlacementAroundPoint
			if (getRdOffsetLabelOnTopPoint().isSelected()) {
				mode = IPlacementConstraints.ON_TOP_OF_THE_POINT;
			} else if (getRdOffsetLabelHorizontally().isSelected()) {
				mode = IPlacementConstraints.OFFSET_HORIZONTALY_AROUND_THE_POINT;
			}
			((PointPlacementConstraints) constraints)
					.setPositioneer((PointLabelPositioneer) stylePreview
							.getStyle());
			break;
		case FShape.LINE:
			if (getRdBtnFollowingLine().isSelected()) {
				mode = IPlacementConstraints.FOLLOWING_LINE;
			} else if (getRdBtnParallel().isSelected()) {
				mode = IPlacementConstraints.PARALLEL;
			} else if (getRdBtnPerpendicular().isSelected()) {
				mode = IPlacementConstraints.PERPENDICULAR;
			} else {
				mode = IPlacementConstraints.HORIZONTAL;
			}

			constraints.setAboveTheLine(getChkAbove().isSelected());
			constraints.setBelowTheLine(getChkBelow().isSelected());
			constraints.setOnTheLine(getChkOnTheLine().isSelected());

			constraints.setPageOriented(getCmbOrientationSystem()
					.getSelectedIndex() == 1);
			int i = getCmbLocationAlongLines().getSelectedIndex();
			if (i == 0) {
				i = IPlacementConstraints.AT_THE_MIDDLE_OF_THE_LINE;
			} else if (i == 1) {
				i = IPlacementConstraints.AT_THE_BEGINING_OF_THE_LINE;
			} else if (i == 2) {
				i = IPlacementConstraints.AT_THE_END_OF_THE_LINE;
			} else if (i == 3) {
				i = IPlacementConstraints.AT_BEST_OF_LINE;
			}
			constraints.setLocationAlongTheLine(i);
			break;
		case FShape.POLYGON:
			mode = IPlacementConstraints.HORIZONTAL;
			if (getRdBtnAlwaysHorizontal().isSelected()) {
				mode = IPlacementConstraints.HORIZONTAL;
			} else if (getRdBtnAlwaysStraight().isSelected()) {
				mode = IPlacementConstraints.PARALLEL;
			}

			constraints.setFitInsidePolygon(getChkFitInsidePolygon()
					.isSelected());
			break;
		}
		constraints.setPlacementMode(mode);

		constraints.setDuplicateLabelsMode(duplicateLabelsMode.getMode());
	}

	private void setComponentEnabled(Component c, boolean b) {
		if (c instanceof JComponent) {
			JComponent c1 = (JComponent) c;
			for (int i = 0; i < c1.getComponentCount(); i++) {
				setComponentEnabled(c1.getComponent(i), b);
			}
		}
		c.setEnabled(b);
	}

	public IPlacementConstraints getPlacementConstraints() {
		return constraints;
	}

	public void actionPerformed(ActionEvent e) {
		JComponent c = (JComponent) e.getSource();
		boolean okPressed = "OK".equals(e.getActionCommand());
		boolean cancelPressed = "CANCEL".equals(e.getActionCommand());
		if (okPressed || cancelPressed) {
			if (okPressed)
				applyConstraints();

			if ("CANCEL".equals(e.getActionCommand()))
				constraints = oldConstraints;
			PluginServices.getMDIManager()
					.closeWindow(PlacementProperties.this);

			return;
		} else if (c.equals(rdBtnAlwaysHorizontal)
				|| c.equals(rdBtnAlwaysStraight)) {

		} else if (c.equals(rdBtnHorizontal)) {
			// lock position panel and location panel
			setComponentEnabled(getPositionPanel(), false);
			setComponentEnabled(getLocationPanel(), false);
		} else if (c.equals(rdBtnParallel) || c.equals(rdBtnPerpendicular)) {
			// unlock position panel and location panel but keep orientation
			// system locked
			setComponentEnabled(getLocationPanel(), true);
			setComponentEnabled(getPositionPanel(), true);
			getCmbOrientationSystem().setEnabled(true);
		} else if (c.equals(rdBtnFollowingLine)) {
			setComponentEnabled(getLocationPanel(), true);
			setComponentEnabled(getPositionPanel(), true);
			getCmbOrientationSystem().setSelectedItem(
					PluginServices.getText(this, "line"));
			getCmbOrientationSystem().setEnabled(false);
		} else if (c.equals(btnChangeLocation)) {
			StyleSelector stySel = new StyleSelector(getPointStyle(),
					FShape.POINT, new AbstractStyleSelectorFilter(
							new PointLabelPositioneer()), false);
			stySel.setTitle(PluginServices.getText(this,
					"placement_priorities_selector"));
			PluginServices.getMDIManager().addWindow(stySel);
			IStyle sty = (IStyle) stySel.getSelectedObject();
			if (sty != null) {
				stylePreview.setStyle(sty);
				lblPointPosDesc.setText(sty.getDescription());
			}
		}

		changeIcon();

	}

	private void changeIcon() {
		if (preview != null && fireEvent) {
			applyConstraints();
			preview.setConstraints(constraints);
		}
	}

	private class JLabelHTML extends JLabel {
		private static final long serialVersionUID = -5031405572546951108L;

		public JLabelHTML(String text) {
			super(text);
			setPreferredSize(new Dimension(250, 60));
		}

		@Override
		public void setText(String text) {
			// silly fix to avoid too large text lines
			super.setText("<html>" + text + "</html>");
		}
	}

	public static IPlacementProperties createPlacementProperties(
			IPlacementConstraints placementConstraints, int shapeType)
			throws ReadDriverException {
		return createPlacementProperties(placementConstraints, shapeType, null);
	}

	protected static IPlacementProperties createPlacementProperties(
			IPlacementConstraints placementConstraints, int shapeType,
			DuplicateLayersMode duplicatesMode) throws ReadDriverException {
		IPlacementProperties pp;
		if (shapeType == FShape.MULTI) {
			pp = new MultiShapePlacementProperties(
					(MultiShapePlacementConstraints) placementConstraints);
		} else {
			pp = new PlacementProperties(placementConstraints, shapeType, null);
		}

		((JPanel) pp).add(new AcceptCancelPanel(pp, pp), BorderLayout.SOUTH);
		return pp;
	}
} // @jve:decl-index=0:visual-constraint="10,10"

class MiniMapContext extends JComponent {
	private static final long serialVersionUID = 229128782038834443L;
	private static final CoordinateReferenceSystem CRS = ProjectionUtils
			.getCRS("EPSG:23030");
	private static final String DRIVER_NAME = "gvSIG shp driver";
	private MapContext theMapContext;
	private FLyrVect line;
	private FLyrVect backgroundPolygon;
	private int hMargin = 5, vMargin = 5;
	private FLyrVect polygon;
	private int type;
	private IPlacementConstraints placement;

	public MiniMapContext(int shapeType) {
		this.type = shapeType;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setConstraints(IPlacementConstraints constraints) {
		placement = constraints;
		repaint();
	}

	private MapContext getMapContext() throws LoadLayerException,
			ReadDriverException {
		if (theMapContext == null) {
			String lineFile = getClass().getClassLoader()
					.getResource("docs/line.shp").getFile();
			line = (FLyrVect) LayerFactory.createLayer("line", DRIVER_NAME,
					new File(lineFile), CRS);

			SimpleLineSymbol sym = new SimpleLineSymbol();
			sym.setLineColor(Color.red);
			SimpleLineStyle sty = new SimpleLineStyle();
			ArrowDecoratorStyle arrow = new ArrowDecoratorStyle();
			arrow.getMarker().setSize(15);
			arrow.setArrowMarkerCount(1);
			arrow.getMarker().setColor(Color.red);
			sty.setArrowDecorator(arrow);
			sym.setLineStyle(sty);
			sym.setLineWidth(2);
			line.setLegend(new SingleSymbolLegend(sym));

			String backgroundPolygonFile = getClass().getClassLoader()
					.getResource("docs/bg-polygon.shp").getFile();
			backgroundPolygon = (FLyrVect) LayerFactory.createLayer(
					"bg-polygon", DRIVER_NAME, new File(backgroundPolygonFile),
					CRS);
			String polygonFile = getClass().getClassLoader()
					.getResource("docs/polygon.shp").getFile();
			polygon = (FLyrVect) LayerFactory.createLayer("polygon",
					DRIVER_NAME, new File(polygonFile), CRS);

			SimpleFillSymbol sym2 = new SimpleFillSymbol();
			sym2.setFillColor(new Color(50, 245, 125));
			SimpleLineSymbol outline = new SimpleLineSymbol();
			outline.setLineColor(Color.DARK_GRAY);
			outline.setLineWidth(0.5);
			sym2.setOutline(outline);

			SingleSymbolLegend polyLegend = new SingleSymbolLegend(sym2);
			polygon.setLegend(polyLegend);
			backgroundPolygon.setLegend(polyLegend);

			GeneralLabelingStrategy labeling1 = new GeneralLabelingStrategy();
			GeneralLabelingStrategy labeling2 = new GeneralLabelingStrategy();
			DefaultLabelingMethod method = new DefaultLabelingMethod();

			LabelClass lc = null;
			if (method.getLabelClasses() != null
					&& method.getLabelClasses().length > 0) {
				lc = method.getLabelClasses()[0];
			} else {
				lc = new LabelClass();
				method.addLabelClass(lc);
			}

			String[] sampleExpression = { PluginServices.getText(this, "text") };
			lc.setLabelExpressions(sampleExpression);

			lc.getTextSymbol().setFontSize(16);

			labeling1.setLabelingMethod(method);
			labeling2.setLabelingMethod(method);
			labeling1.setLayer(line);
			labeling2.setLayer(polygon);

			line.setLabelingStrategy(labeling1);
			line.setIsLabeled(true);

			polygon.setLabelingStrategy(labeling2);
			polygon.setIsLabeled(true);
			ViewPort theViewPort = new ViewPort(CRS);

			theViewPort.setExtent(new Rectangle2D.Double(289600, 3973700, 2000,
					2000));
			theMapContext = new MapContext(theViewPort);
			theMapContext.getLayers().addLayer(backgroundPolygon);
			theMapContext.getLayers().addLayer(polygon);
			theMapContext.getLayers().addLayer(line);

		}
		line.getLabelingStrategy().setPlacementConstraints(placement);
		polygon.getLabelingStrategy().setPlacementConstraints(placement);
		Dimension sz = getBounds().getSize();
		sz.setSize(sz.width - 2 * hMargin, sz.height - 2 * vMargin);
		theMapContext.getViewPort().setImageSize(sz);
		theMapContext.getViewPort().setBackColor(new Color(255, 0, 0));

		return theMapContext;
	}

	@Override
	protected void paintComponent(Graphics g) {
		try {
			getMapContext();
			if (type % FShape.Z == FShape.LINE) {
				line.setVisible(true);
				polygon.setVisible(false);
			} else if (type % FShape.Z == FShape.POLYGON) {
				line.setVisible(false);
				polygon.setVisible(true);
			} else {

			}
			backgroundPolygon.setVisible(line.isVisible());

			Rectangle bounds = getBounds();
			Dimension sz = bounds.getSize();
			sz.setSize(sz.width - 2 * vMargin, sz.height - 2 * hMargin);
			Dimension imageSize = sz;
			BufferedImage bi = new BufferedImage(imageSize.width,
					imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
			getMapContext().draw(bi, bi.createGraphics(),
					getMapContext().getScaleView());
			g.setColor(new Color(150, 180, 255));
			g.fillRect(vMargin, hMargin, bounds.width - 2 * hMargin,
					bounds.height - 2 * vMargin);
			g.drawImage(bi, vMargin, vMargin, null);
			bi = null;
		} catch (Exception e) {
			e.printStackTrace();
			String noneSelected = "["
					+ PluginServices.getText(this, "preview_not_available")
					+ "]";
			int vGap = 5, hGap = 5;
			Rectangle r = getBounds();
			FontMetrics fm = g.getFontMetrics();
			int lineWidth = fm.stringWidth(noneSelected);
			float scale = (float) r.getWidth() / lineWidth;
			Font f = g.getFont();
			float fontSize = f.getSize() * scale;
			g.setFont(f.deriveFont(fontSize));
			((Graphics2D) g).drawString(noneSelected, (r.x * scale)
					- (hGap / 2), r.height / 2 + vGap * scale);
		}

	}
}
