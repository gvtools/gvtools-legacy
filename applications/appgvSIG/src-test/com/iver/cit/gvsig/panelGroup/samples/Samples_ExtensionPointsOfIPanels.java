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

package com.iver.cit.gvsig.panelGroup.samples;

import com.iver.cit.gvsig.panelGroup.PanelGroupDialog;
import com.iver.cit.gvsig.panelGroup.loaders.PanelGroupLoaderFromExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * <p>This class has information to create samples to test {@link PanelGroupManager PanelGroupManager},
 *  {@link TabbedPanel TabbedPanel}, {@link TreePanel TreePanel},
 *  {@link AbstractPanel AbstractPanel}, {@link PanelGroupLoaderUtilities PanelGroupLoaderUtilities},
 *  {@link PanelGroupLoaderFromList PanelGroupLoaderFromList}, {@link PanelGroupLoaderFromExtensionPoint PanelGroupLoaderFromExtensionPoint},
 *  and {@link PanelGroupDialog PanelGroupDialog}.</p>
 * 
 * @version 16/10/2007
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es) 
 */
public class Samples_ExtensionPointsOfIPanels {
	public final static String EXTENSIONPOINT1_NAME = "RasterLayerProperties";
	public final static String EXTENSIONPOINT2_NAME = "ImaginaryLayerProperties";
	public final static String EXTENSIONPOINT3_NAME = "Other tests";
	public final static String EXTENSIONPOINT4_NAME = "Test 1 exceptions";
	public final static String EXTENSIONPOINT5_NAME = "Test 2 exceptions";
	public final static String EXTENSIONPOINT6_NAME = "Test 3 exceptions";
	public final static String EXTENSIONPOINT7_NAME = "Test 4 exceptions";
	public final static String EXTENSIONPOINT8_NAME = "Test 5 exceptions";
	public final static String EXTENSIONPOINT9_NAME = "Test 6 exceptions";
	public final static String EXTENSIONPOINT10_NAME = "Test 7 exceptions";
	public final static String[] EXTENSIONPOINTS1_NAMES = {"Information", "Bands", "Transparency", "Enhanced", "PanSharpening", "Scale"};
	public final static String[] EXTENSIONPOINTS2_NAMES = {"Information", "Transparency", "Scale"};
	public final static String[] EXTENSIONPOINTS3_OTHER_NAMES = {"SamplePanelWithoutGroupLabel", "SamplePanelGroupLabelRepeated"};
	public final static String[] EXTENSIONPOINTS4_NAMES = {"SampleInitializingExcetionPanel", "SampleInfoPanel"}; // Test 'ListCouldntLoadPanelFromListException' with any 'Exception' 
	public final static String[] EXTENSIONPOINTS5_NAMES = {"SampleInfoPanel", "SampleUndefinedPreferredSizeExceptionPanel"}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException'
	public final static String[] EXTENSIONPOINTS6_NAMES = {}; // Test 'ListCouldntAddPanelException' with a 'EmptyPanelGroupException'
	public final static String[] EXTENSIONPOINTS7_NAMES = {"SampleInvisiblePanel.class"}; // Test 'ListCouldntAddPanelException' with a 'EmptyPanelGroupGUIException'
	public final static String[] EXTENSIONPOINTS8_NAMES = {"SampleInitializingExcetionPanel", "SampleUndefinedPreferredSizeExceptionPanel"}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException', a 'EmptyPanelGroupException' and a 'ListCouldntLoadPanelFromListException' with any 'Exception'
	public final static String[] EXTENSIONPOINTS9_NAMES = {"SampleInvisiblePanel", "SampleInitializingExcetionPanel", "SampleUndefinedPreferredSizeExceptionPanel"}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException', a 'EmptyPanelGroupGUIException' and a 'ListCouldntLoadPanelFromListException' with any 'Exception'
	public final static String[] EXTENSIONPOINTS10_NAMES = {"SampleBandSetupPanel", "SampleInvisiblePanel", "SampleInitializingExcetionPanel", "SampleUndefinedPreferredSizeExceptionPanel"}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException', a EmptyPanelGroupGUIException and a 'ListCouldntLoadPanelFromListException' with any 'Exception'
	public final static Class[] EXTENSIONPOINTS1_CLASSES = {SampleInfoPanel.class, SampleBandSetupPanel.class, SampleTransparencyPanel.class, SampleEnhancedPanel.class, SamplePanSharpeningPanel.class, SampleScalePanel.class};
	public final static Class[] EXTENSIONPOINTS2_CLASSES = {SampleInfoPanel.class, SampleTransparencyPanel.class, SampleScalePanel.class};
	public final static Class[] OTHER_PANELS_EXTENSIONPOINTS3_CLASSES = {SamplePanelWithoutGroupLabel.class, SamplePanelGroupLabelRepeated.class};
	public final static Class[] EXTENSIONPOINTS4_CLASSES = {SampleInitializingExcetionPanel.class, SampleInfoPanel.class}; // Test 'ListCouldntLoadPanelFromListException' with any 'Exception' 
	public final static Class[] EXTENSIONPOINTS5_CLASSES = {SampleInfoPanel.class, SampleUndefinedPreferredSizeExceptionPanel.class}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException'
	public final static Class[] EXTENSIONPOINTS6_CLASSES = {}; // Test 'ListCouldntAddPanelException' with a 'EmptyPanelGroupException'
	public final static Class[] EXTENSIONPOINTS7_CLASSES = {SampleInvisiblePanel.class}; // Test 'ListCouldntAddPanelException' with a 'EmptyPanelGroupGUIException'
	public final static Class[] EXTENSIONPOINTS8_CLASSES = {SampleInitializingExcetionPanel.class, SampleUndefinedPreferredSizeExceptionPanel.class}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException', a 'EmptyPanelGroupException' and a 'ListCouldntLoadPanelFromListException' with any 'Exception'
	public final static Class[] EXTENSIONPOINTS9_CLASSES = {SampleInvisiblePanel.class, SampleInitializingExcetionPanel.class, SampleUndefinedPreferredSizeExceptionPanel.class}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException', a 'EmptyPanelGroupGUIException' and a 'ListCouldntLoadPanelFromListException' with any 'Exception'
	public final static Class[] EXTENSIONPOINTS10_CLASSES = {SampleBandSetupPanel.class, SampleInvisiblePanel.class, SampleInitializingExcetionPanel.class, SampleUndefinedPreferredSizeExceptionPanel.class}; // Test 'ListCouldntAddPanelException' with a 'PanelWithNoPreferredSizeDefinedException', a EmptyPanelGroupGUIException and a 'ListCouldntLoadPanelFromListException' with any 'Exception'
	public final static Object REFERENCE1 = new String("Raster Layer");
	public final static Object REFERENCE2 = new String("Imaginary Layer");
	public final static Object REFERENCE3 = new String("Other tests");	
	public final static String REFERENCE1_NAME = "Raster Layer reference";
	public final static String REFERENCE2_NAME = "Imaginary Layer reference";
	public final static String REFERENCE3_NAME = "Other tests reference";
	public final static String[] PANELS1_IDS = {"Information_ID", "Bands_ID", "Transparency_ID", "Enhanced_ID", "PanSharpening_ID", "Scale_ID"};
	public final static String[] PANELS2_IDS = {"Information_ID", "Transparency_ID", "Scale_ID"};
	public final static String[] PANELS3_IDS = {"WithoutGroup_ID", "GroupLabelRepeated_ID"};
	public final static String[] PANELS1_LABELS = {"Information_LABEL", "Bands_LABEL", "Transparency_LABEL", "Enhanced_LABEL", "PanSharpening_LABEL", "Scale_LABEL"};
	public final static String[] PANELS2_LABELS = {"Information_LABEL", "Transparency_LABEL", "Scale_LABEL"};
	public final static String[] PANELS3_LABELS = {"WithoutGroup_LABEL", "GroupLabelRepeated_LABEL"};
	public final static String[] PANELS1_LABELGROUPS = {"Information_LABELGROUP", "Bands_LABELGROUP", "Transparency_LABELGROUP", "Enhanced_LABELGROUP", "PanSharpening_LABELGROUP", "Scale_LABELGROUP"};
	public final static String[] PANELS2_LABELGROUPS = {"Information_LABELGROUP", "Transparency_LABELGROUP", "Scale_LABELGROUP"};
	public final static String[] PANELS3_LABELGROUPS = {null, "GroupLabelRepeated_LABELGROUP"};
	public final static short PANELS_DEFAULT_WIDTH = 500;
	public final static short PANELS_DEFAULT_HEIGHT = 400;
	
	private static ExtensionPoints extensionPoints = null;
	
	/**
	 * <p>Loads the information of the sample.</p>
	 */
	public static void loadSample() {
		if (extensionPoints == null) {
			extensionPoints = ExtensionPointsSingleton.getInstance();
	
			extensionPoints.add(EXTENSIONPOINT1_NAME, EXTENSIONPOINTS1_NAMES[0], EXTENSIONPOINTS1_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT1_NAME, EXTENSIONPOINTS1_NAMES[1], EXTENSIONPOINTS1_CLASSES[1]);
			extensionPoints.add(EXTENSIONPOINT1_NAME, EXTENSIONPOINTS1_NAMES[2], EXTENSIONPOINTS1_CLASSES[2]);
			extensionPoints.add(EXTENSIONPOINT1_NAME, EXTENSIONPOINTS1_NAMES[3], EXTENSIONPOINTS1_CLASSES[3]);
			extensionPoints.add(EXTENSIONPOINT1_NAME, EXTENSIONPOINTS1_NAMES[4], EXTENSIONPOINTS1_CLASSES[4]);
			extensionPoints.add(EXTENSIONPOINT1_NAME, EXTENSIONPOINTS1_NAMES[5], EXTENSIONPOINTS1_CLASSES[5]);

			extensionPoints.add(EXTENSIONPOINT2_NAME, EXTENSIONPOINTS2_NAMES[0], EXTENSIONPOINTS2_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT2_NAME, EXTENSIONPOINTS2_NAMES[1], EXTENSIONPOINTS2_CLASSES[1]);
			extensionPoints.add(EXTENSIONPOINT2_NAME, EXTENSIONPOINTS2_NAMES[2], EXTENSIONPOINTS2_CLASSES[2]);

			extensionPoints.add(EXTENSIONPOINT3_NAME, EXTENSIONPOINTS3_OTHER_NAMES[0], OTHER_PANELS_EXTENSIONPOINTS3_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT3_NAME, EXTENSIONPOINTS3_OTHER_NAMES[1], OTHER_PANELS_EXTENSIONPOINTS3_CLASSES[1]);

			extensionPoints.add(EXTENSIONPOINT4_NAME, EXTENSIONPOINTS4_NAMES[0], EXTENSIONPOINTS4_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT4_NAME, EXTENSIONPOINTS4_NAMES[1], EXTENSIONPOINTS4_CLASSES[1]); 

			extensionPoints.add(EXTENSIONPOINT5_NAME, EXTENSIONPOINTS5_NAMES[0], EXTENSIONPOINTS5_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT5_NAME, EXTENSIONPOINTS5_NAMES[1], EXTENSIONPOINTS5_CLASSES[1]); 

			extensionPoints.add(EXTENSIONPOINT6_NAME, null, null);

			extensionPoints.add(EXTENSIONPOINT7_NAME, EXTENSIONPOINTS7_NAMES[0], EXTENSIONPOINTS7_CLASSES[0]);

			extensionPoints.add(EXTENSIONPOINT8_NAME, EXTENSIONPOINTS8_NAMES[0], EXTENSIONPOINTS8_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT8_NAME, EXTENSIONPOINTS8_NAMES[1], EXTENSIONPOINTS8_CLASSES[1]); 

			extensionPoints.add(EXTENSIONPOINT9_NAME, EXTENSIONPOINTS9_NAMES[0], EXTENSIONPOINTS9_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT9_NAME, EXTENSIONPOINTS9_NAMES[1], EXTENSIONPOINTS9_CLASSES[1]);
			extensionPoints.add(EXTENSIONPOINT9_NAME, EXTENSIONPOINTS9_NAMES[2], EXTENSIONPOINTS9_CLASSES[2]);

			extensionPoints.add(EXTENSIONPOINT10_NAME, EXTENSIONPOINTS10_NAMES[0], EXTENSIONPOINTS10_CLASSES[0]);
			extensionPoints.add(EXTENSIONPOINT10_NAME, EXTENSIONPOINTS10_NAMES[1], EXTENSIONPOINTS10_CLASSES[1]);
			extensionPoints.add(EXTENSIONPOINT10_NAME, EXTENSIONPOINTS10_NAMES[2], EXTENSIONPOINTS10_CLASSES[2]);
			extensionPoints.add(EXTENSIONPOINT10_NAME, EXTENSIONPOINTS10_NAMES[3], EXTENSIONPOINTS10_CLASSES[3]);
		}
	}
}
