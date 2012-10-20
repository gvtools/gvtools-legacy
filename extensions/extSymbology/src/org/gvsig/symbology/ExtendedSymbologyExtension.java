/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
package org.gvsig.symbology;

import org.gvsig.symbology.fmap.labeling.GeneralLabelingStrategy;
import org.gvsig.symbology.fmap.labeling.PlacementManager;
import org.gvsig.symbology.fmap.labeling.lang.functions.IndexOfFunction;
import org.gvsig.symbology.fmap.labeling.lang.functions.SubstringFunction;
import org.gvsig.symbology.fmap.labeling.placements.LinePlacementAtBest;
import org.gvsig.symbology.fmap.labeling.placements.LinePlacementAtExtremities;
import org.gvsig.symbology.fmap.labeling.placements.LinePlacementInTheMiddle;
import org.gvsig.symbology.fmap.labeling.placements.MarkerPlacementAroundPoint;
import org.gvsig.symbology.fmap.labeling.placements.MarkerPlacementOnPoint;
import org.gvsig.symbology.fmap.labeling.placements.PolygonPlacementInside;
import org.gvsig.symbology.fmap.labeling.placements.PolygonPlacementOnCentroid;
import org.gvsig.symbology.fmap.labeling.placements.PolygonPlacementParallel;
import org.gvsig.symbology.fmap.rendering.filter.operations.OperatorsFactory;
import org.gvsig.symbology.gui.layerproperties.DefaultLabeling;
import org.gvsig.symbology.gui.layerproperties.DotDensity;
import org.gvsig.symbology.gui.layerproperties.FeatureDependent;
import org.gvsig.symbology.gui.layerproperties.GeneralLabeling;
import org.gvsig.symbology.gui.layerproperties.GraduatedSymbols;
import org.gvsig.symbology.gui.layerproperties.OnSelection;
import org.gvsig.symbology.gui.layerproperties.ProportionalSymbols;
import org.gvsig.symbology.gui.layerproperties.QuantityByCategory;
import org.gvsig.symbology.gui.styling.CharacterMarker;
import org.gvsig.symbology.gui.styling.GradientFill;
import org.gvsig.symbology.gui.styling.LineFill;
import org.gvsig.symbology.gui.styling.MarkerFill;
import org.gvsig.symbology.gui.styling.MarkerLine;
import org.gvsig.symbology.gui.styling.PictureFill;
import org.gvsig.symbology.gui.styling.PictureLine;
import org.gvsig.symbology.gui.styling.PictureMarker;
import org.gvsig.symbology.gui.styling.editortools.LabelStyleNewTextFieldTool;
import org.gvsig.symbology.gui.styling.editortools.LabelStyleOpenBackgroundFile;
import org.gvsig.symbology.gui.styling.editortools.LabelStylePanTool;
import org.gvsig.symbology.gui.styling.editortools.LabelStyleRemoveLastTextField;
import org.gvsig.symbology.gui.styling.editortools.PointLabelForbiddenPrecedenceTool;
import org.gvsig.symbology.gui.styling.editortools.PointLabelHighPrecedenceTool;
import org.gvsig.symbology.gui.styling.editortools.PointLabelLowPrecedenceTool;
import org.gvsig.symbology.gui.styling.editortools.PointLabelNormalPrecedenceTool;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;
import com.iver.cit.gvsig.gui.styling.StyleEditor;
import com.iver.cit.gvsig.gui.styling.SymbolEditor;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LabelingManager;
import com.iver.cit.gvsig.project.documents.view.legend.gui.LegendManager;

public class ExtendedSymbologyExtension extends Extension {

	public void execute(String actionCommand) {

	}

	public void initialize() {

		PluginServices.getIconTheme().registerDefault(
				"high-density",
				DotDensity.class.getClassLoader().getResource(
						"images/high-density-sample.png"));
		PluginServices.getIconTheme().registerDefault(
				"medium-density",
				DotDensity.class.getClassLoader().getResource(
						"images/medium-density-sample.png"));
		PluginServices.getIconTheme().registerDefault(
				"low-density",
				DotDensity.class.getClassLoader().getResource(
						"images/low-density-sample.png"));
		PluginServices.getIconTheme().registerDefault(
				"quantities-by-category",
				QuantityByCategory.class.getClassLoader().getResource(
						"images/QuantitiesByCategory.png"));
		PluginServices.getIconTheme().registerDefault(
				"dot-density",
				QuantityByCategory.class.getClassLoader().getResource(
						"images/DotDensity.PNG"));

		PluginServices.getIconTheme().registerDefault(
				"graduated-symbols",
				QuantityByCategory.class.getClassLoader().getResource(
						"images/GraduatedSymbols.PNG"));

		PluginServices.getIconTheme().registerDefault(
				"proportional-symbols",
				QuantityByCategory.class.getClassLoader().getResource(
						"images/ProportionalSymbols.PNG"));

		PluginServices.getIconTheme().registerDefault(
				"filter-expressions",
				QuantityByCategory.class.getClassLoader().getResource(
						"images/FilterExpressions.PNG"));
		PluginServices.getIconTheme().registerDefault(
				"add-text-icon",
				this.getClass().getClassLoader()
						.getResource("images/add-text.png"));
		PluginServices.getIconTheme().registerDefault(
				"remove-text-icon",
				this.getClass().getClassLoader()
						.getResource("images/remove-text.png"));
		PluginServices.getIconTheme()
				.registerDefault(
						"hand-icon",
						this.getClass().getClassLoader()
								.getResource("images/hand.gif"));

		PluginServices.getIconTheme().registerDefault(
				"set-high-precedence-point-label-icon",
				this.getClass().getClassLoader()
						.getResource("images/high-precedence.png"));
		PluginServices.getIconTheme().registerDefault(
				"set-normal-precedence-point-label-icon",
				this.getClass().getClassLoader()
						.getResource("images/normal-precedence.png"));
		PluginServices.getIconTheme().registerDefault(
				"set-low-precedence-point-label-icon",
				this.getClass().getClassLoader()
						.getResource("images/low-precedence.png"));
		PluginServices.getIconTheme().registerDefault(
				"set-forbidden-precedence-point-label-icon",
				this.getClass().getClassLoader()
						.getResource("images/forbidden-precedence.png"));

		// modules for symbol editor
		SymbolEditor.addSymbolEditorPanel(MarkerFill.class, FShape.POLYGON);
		SymbolEditor.addSymbolEditorPanel(PictureFill.class, FShape.POLYGON);
		SymbolEditor.addSymbolEditorPanel(LineFill.class, FShape.POLYGON);
		SymbolEditor.addSymbolEditorPanel(GradientFill.class, FShape.POLYGON);
		SymbolEditor.addSymbolEditorPanel(CharacterMarker.class, FShape.POINT);
		SymbolEditor.addSymbolEditorPanel(PictureMarker.class, FShape.POINT);
		SymbolEditor.addSymbolEditorPanel(PictureLine.class, FShape.LINE);
		SymbolEditor.addSymbolEditorPanel(MarkerLine.class, FShape.LINE);

		// legends available in the legend page
		LegendManager.addLegendPage(DotDensity.class);
		LegendManager.addLegendPage(GraduatedSymbols.class);
		LegendManager.addLegendPage(ProportionalSymbols.class);
		LegendManager.addLegendPage(QuantityByCategory.class);

		// LegendManager.addLegendPage(Statistics.class);
		// LegendManager.addLegendPage(BarChart3D.class);
		// LegendManager.addLegendPage(PieChart3D.class);

		// Editor tools
		StyleEditor.addEditorTool(LabelStylePanTool.class);
		StyleEditor.addEditorTool(LabelStyleNewTextFieldTool.class);
		StyleEditor.addEditorTool(LabelStyleRemoveLastTextField.class);
		StyleEditor.addEditorTool(LabelStyleOpenBackgroundFile.class);
		StyleEditor.addEditorTool(PointLabelHighPrecedenceTool.class);
		StyleEditor.addEditorTool(PointLabelNormalPrecedenceTool.class);
		StyleEditor.addEditorTool(PointLabelLowPrecedenceTool.class);
		StyleEditor.addEditorTool(PointLabelForbiddenPrecedenceTool.class);

		LabelingManager.addLabelingStrategy(GeneralLabeling.class);

		// labeling methods in the labeling page
		// (inverse order to the wanted to be shown)
		GeneralLabeling.addLabelingMethod(OnSelection.class);
		GeneralLabeling.addLabelingMethod(FeatureDependent.class);
		GeneralLabeling.addLabelingMethod(DefaultLabeling.class);

		PlacementManager.addLabelPlacement(LinePlacementAtExtremities.class);
		PlacementManager.addLabelPlacement(LinePlacementAtBest.class);
		PlacementManager.addLabelPlacement(LinePlacementInTheMiddle.class);
		PlacementManager.addLabelPlacement(MarkerPlacementOnPoint.class);
		// PlacementManager.addLabelPlacement(MarkerCenteredAtPoint.class);
		PlacementManager.addLabelPlacement(MarkerPlacementAroundPoint.class);
		PlacementManager.addLabelPlacement(PolygonPlacementOnCentroid.class);
		PlacementManager.addLabelPlacement(PolygonPlacementInside.class);
		PlacementManager.addLabelPlacement(PolygonPlacementParallel.class);

		LabelingFactory
				.setDefaultLabelingStrategy(GeneralLabelingStrategy.class);

		OperatorsFactory.getInstance().addOperator(SubstringFunction.class);
		OperatorsFactory.getInstance().addOperator(IndexOfFunction.class);
	}

	public boolean isEnabled() {
		return true; // or whatever
	}

	public boolean isVisible() {
		return true; // or whatever
	}

}
