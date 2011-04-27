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
package org.gvsig.topology.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.topology.IOneLyrRule;
import org.gvsig.topology.ITopologyRule;
import org.gvsig.topology.ITwoLyrRule;
import org.gvsig.topology.TopologyRuleDefinitionException;
import org.gvsig.topology.TopologyRuleFactory;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * GUI component to create new topology rules instances.
 * 
 * @author Alvaro Zabala
 * 
 */
public class NewTopologyRulePanel extends JPanel implements IWindow{

	private static final long serialVersionUID = 2072228070829536392L;
	
	private WindowInfo viewInfo;
	private GridBagLayoutPanel leftPanel;
	private JScrollPane rightPanel;
	private AcceptCancelPanel acceptCancelPanel;
	private LayerJComboBox lyrCombo;
	private LayerJComboBox lyrCombo2;
	private JComboBox topologyRulesCombo;
	private JEditorPane htmlViewer;
	
	/**
	 * Relates the user friendly rule name (showed in the rules' combobox) with the
	 * class that implements this rule
	 */
	private Map<String, Class<?>> ruleName_ruleClass;

	/**
	 * Layers that have been selected by the user to form part of a topology
	 */
	private List<FLyrVect> layers;
	/**
	 * MapContext of the active view that opens this dialog
	 */
	private MapContext mapContext;
	
	/**
	 * New topology rule created by this panel
	 */
	private ITopologyRule newTopologyRule;

	
	/**
	 * Constructor.
	 * 
	 * @param layers
	 */
	public NewTopologyRulePanel(List<FLyrVect> lyrsOfTopology, MapContext mapContext) {
		super();
		this.mapContext = mapContext;
		setLayout(new BorderLayout());
		this.layers = lyrsOfTopology;
		ruleName_ruleClass = new HashMap<String, Class<?>>();
		initialize();
	}
	
	/**
	 * Returns an instance of the topology rule specified by the user.
	 * @return
	 */
	public ITopologyRule getNewTopologyRule(){
		return this.newTopologyRule;
	}
	

	/**
	 * Checks if the rule selected in rules combo applies to two layers.
	 * @return true if the selected rule applies to two layers.
	 */
	private boolean checkTwoLyrRule(){
		 Class clazz = ruleName_ruleClass.get(topologyRulesCombo.getSelectedItem());
		 if(! ITwoLyrRule.class.isAssignableFrom(clazz)){
	    	return false;
	     }else
	    	return true;
	}
	
	/**
	 * Enables or disables second layer combo box, in function of the selected rule
	 * applies to one or to two layers.
	 */
	private void updateSecondLayerComboStatus(){
		if(!checkTwoLyrRule()){
			lyrCombo2.setEnabled(false);
			lyrCombo2.setToolTipText(PluginServices.getText(null, "disable_for_onelyr_rules"));
		}else{
			try {
				Class<?> clazz = ruleName_ruleClass.get(topologyRulesCombo.getSelectedItem());
				ITwoLyrRule twoLyrRule = (ITwoLyrRule) clazz.newInstance();
				
				FLayers combo2Lyrs = new FLayers();
				combo2Lyrs.setMapContext(mapContext);
				combo2Lyrs.setParentLayer(mapContext.getLayers());
				int numLyrs = layers.size();
				for(int i = 0; i < numLyrs; i++){
					FLyrVect tempLyr = layers.get(i);
					if(tempLyr == lyrCombo.getSelectedLayer())//we jump the selected origin layer
						continue;
					if(twoLyrRule.acceptsDestinationLyr(tempLyr)){
						combo2Lyrs.addLayer(tempLyr);
					}
				}//for
				
				if(combo2Lyrs.getLayersCount() == 0){
					lyrCombo2.setEnabled(false);
					lyrCombo2.setToolTipText(PluginServices.getText(null, "no_available_destination_lyr"));
				}else{
					lyrCombo2.setLayers(combo2Lyrs);
					lyrCombo2.setEnabled(true);
				}
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				lyrCombo2.setEnabled(false);
			} 
		}//else
	}
	
	private void updateRuleDescription(){
		 Class clazz = ruleName_ruleClass.get(topologyRulesCombo.getSelectedItem());
		 ITopologyRule rule;
		try {
			rule = (ITopologyRule) clazz.newInstance();
			URL description = rule.getDescription();
			htmlViewer.setPage(description);	 
		} catch (Exception e) {
			htmlViewer.setText("<p>"+PluginServices.getText(this, "UNAVAILABLE_DESCRIPTION")+"</p>");
		} 
	}
	/**
	 * Initializes the component
	 */
	private void initialize() {
		leftPanel = new GridBagLayoutPanel();
		Insets insets = new Insets(2, 0, 2, 0);
		JLabel firstLyrText = new JLabel(PluginServices.getText(this,
				"features_of_first_layer"));
		
		FLayers flayers = new FLayers();
		flayers.setMapContext(mapContext);
		flayers.setParentLayer(mapContext.getLayers());
		for(int i = 0; i < layers.size(); i++){
			FLyrVect lyr = layers.get(i);
			flayers.addLayer(lyr);
		}
		lyrCombo = new LayerJComboBox(flayers);
		lyrCombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				FLyrVect originLyr = (FLyrVect) lyrCombo.getSelectedLayer();
//				updateTopologyRulesCombo(originLyr);
				ComboBoxModel rulesComboModel = createRulesCbModelForInputLyr(originLyr);
				topologyRulesCombo.setModel(rulesComboModel);
			}
		});
		
		JLabel ruleText = new JLabel(PluginServices.getText(this,
				"Topology_rule"));
		
		topologyRulesCombo = createRulesCombo();
		topologyRulesCombo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateRuleDescription();
				updateSecondLayerComboStatus();
			}});
		
		
		JLabel destinationLyrText = new JLabel(PluginServices.getText(this, 
				"features_of_destination_lyr"));
		lyrCombo2 = new LayerJComboBox(flayers);
		
		leftPanel.addComponent(firstLyrText, insets);
		leftPanel.addComponent(lyrCombo, insets);
		leftPanel.addComponent(ruleText, insets);
		leftPanel.addComponent(topologyRulesCombo, insets);
		leftPanel.addComponent(destinationLyrText, insets);
		leftPanel.addComponent(lyrCombo2, insets);
		
		//FIXME Mover todo esto a un TopologyRuleDescriptionPanel
		rightPanel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		htmlViewer = new JEditorPane(){
			public URL getPage() {
				return null;
			}
		};
		htmlViewer.setEditable(false);
		htmlViewer.setEditorKit(new HTMLEditorKit());
		rightPanel.setViewportView(htmlViewer);
		rightPanel.setPreferredSize(new Dimension(250, 140));
		updateRuleDescription();
		
		
		acceptCancelPanel = new AcceptCancelPanel(
			new ActionListener(){//ok
			public void actionPerformed(ActionEvent e) {
				String ruleDesc = (String) topologyRulesCombo.getSelectedItem();
				Class ruleClass = ruleName_ruleClass.get(ruleDesc);
				if(ruleClass != null){
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("originLyr", lyrCombo.getSelectedLayer());
					if(ITwoLyrRule.class.isAssignableFrom(ruleClass)){
						if(lyrCombo2.isEnabled()){
							params.put("destinationLyr", lyrCombo2.getSelectedLayer());
						}else{
							String errorMessage = PluginServices.getText(this, "no_hay_capas_suficientes_para_esta_regla");
							String errorTitle = PluginServices.getText(this, "error_creando_regla");
							GUIUtil.getInstance().messageBox(errorMessage, errorTitle);
							return;
						}
					}
					
					try {
						newTopologyRule = TopologyRuleFactory.createRule(ruleClass, params, null);
					} catch (TopologyRuleDefinitionException e1) {
						e1.printStackTrace();
						String errorMessage = PluginServices.getText(this, e1.toString());
						String errorTitle = PluginServices.getText(this, "error_creando_regla");
						GUIUtil.getInstance().messageBox(errorMessage, errorTitle);
						return;
					}
					PluginServices.getMDIManager().closeWindow(
							NewTopologyRulePanel.this);
				}
			}}, 
			new ActionListener(){//cancel
			public void actionPerformed(ActionEvent arg0) {
				PluginServices.getMDIManager().closeWindow(
						NewTopologyRulePanel.this);
			}});
		
		add(leftPanel, BorderLayout.WEST);
		add(rightPanel, BorderLayout.EAST);
		add(acceptCancelPanel, BorderLayout.SOUTH);
		
		updateSecondLayerComboStatus();
	}

	private JComboBox createRulesCombo() {
		JComboBox solution = new JComboBox();
		FLyrVect originLyr = (FLyrVect) lyrCombo.getSelectedLayer();
		solution.setModel(createRulesCbModelForInputLyr(originLyr));
		return solution;
	}
	
	private ComboBoxModel createRulesCbModelForInputLyr(FLyrVect inputLyr){
		DefaultComboBoxModel cbModel = null;
		
		ExtensionPoints extensionPoints = 
			ExtensionPointsSingleton.getInstance();
		ExtensionPoint topologyRules = 
			(ExtensionPoint) extensionPoints.get("TopologyRules");
		
		ruleName_ruleClass.clear();
		
		if (topologyRules == null) {
			cbModel = new DefaultComboBoxModel(new Object[]{PluginServices.getText(null,
																"Rules_not_registered")});
		}else{
			Vector<String> comboItems = new Vector<String>();
			Iterator<?> i = topologyRules.keySet().iterator();
			while (i.hasNext()) {
				String nombre = (String) i.next();
				String localizedText = PluginServices.getText(this, nombre);
				Class<?> clazz = (Class<?>) topologyRules.get(nombre);
				ruleName_ruleClass.put(localizedText, clazz);
				IOneLyrRule rule;
				boolean enabled = false;
				try {
					rule = (IOneLyrRule) clazz.newInstance();
					enabled = rule.acceptsOriginLyr(inputLyr);
				}catch(Exception e){
					e.printStackTrace();
					enabled = false;
				}//try
				if(enabled)
					comboItems.add(localizedText);
			}//while
			cbModel = new DefaultComboBoxModel(comboItems);
		}//else
		return cbModel;
	}
	
	
//	private void updateTopologyRulesCombo(FLyrVect originLyr) {
//		int itemCount = topologyRulesCombo.getItemCount();
//		for(int i = 0; i < itemCount; i++){
//			String ruleClazzName = (String) topologyRulesCombo.getItemAt(i);
//			Class clazz = ruleName_ruleClass.get(topologyRulesCombo.getSelectedItem());
//			IOneLyrRule rule;
//			try {
//				rule = (IOneLyrRule) clazz.newInstance();
//				boolean enabled = rule.acceptsOriginLyr(originLyr);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//	}
	

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODALDIALOG
					 | WindowInfo.PALETTE 
					 | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,
					"Topology_rules"));
			viewInfo.setWidth(650);
			viewInfo.setHeight(145);
		}
		return viewInfo;
	}
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

}
