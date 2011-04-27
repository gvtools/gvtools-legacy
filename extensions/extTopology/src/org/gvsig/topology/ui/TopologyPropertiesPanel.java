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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.topology.ITopologyRule;
import org.gvsig.topology.Topology;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.WizardPanel;

/**
 * Panel which shows properties of a given topology
 * @author Alvaro Zabala
 *
 */
public class TopologyPropertiesPanel extends JPanel implements com.iver.andami.ui.mdiManager.IWindow{
	
	private static final long serialVersionUID = 4607286708592192479L;

//	private Topology workingTopology;
	private Topology originalTopology;
	
	private JTabbedPane        jTabbedPane    = null;
	
	private GeneralTabPanel    generalTab     = null;
	private LayerSelectionPanel lyrsPanel     = null;
	private TopologyRulesPanel rulesTabPanel = null;
	private TopologyErrorPanel errorPanel   =  null;
	
	private JPanel  acceptPanel = null;
	private boolean            accepted       = false;
	
	private boolean readOnly = true;
	
	/**
	 * Constructor. It receives the topology whose properties we are going to
	 * show in a tabbed panel.
	 * 
	 * @param topology
	 */
	public TopologyPropertiesPanel(Topology topology){
		this.originalTopology = topology;		
		initialize();
	}
	
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(523, 385);
		this.setPreferredSize(new Dimension(523, 385));
		this.add(getJTabbedPane(), BorderLayout.CENTER);
		if(readOnly){
			this.add(getAcceptPanel(),BorderLayout.SOUTH);
		}else{
			this.add(getAcceptCancelPanel(), BorderLayout.SOUTH);
		}
		this.add(getAcceptCancelPanel(), BorderLayout.SOUTH);
		
		final TopologyContentProvider contentProvider = 
				new TopologyContentProvider(originalTopology);
		//General tab
		generalTab = new GeneralTabPanel(readOnly);
		getJTabbedPane().addTab(PluginServices.getText(this, "General"), generalTab);
		
		//Layers tab		
		lyrsPanel = new LayerSelectionPanel(originalTopology.getLayers(), 
											originalTopology.getMapContext(), readOnly);
		
		
		//if panel is read only, we should delete this
		/*
		lyrsPanel.addLayerSelectionListener(new LayerSelectionListener(){
			public void selectionEvent(SelectedLayerEvent event) {
				FLyrVect eventLyr = (FLyrVect) event.lyrOfEvent;
				String warningMessage = "";
				String title = "";
				if(event.eventType == SelectedLayerEvent.REMOVED_EVENT_TYPE){
					List<ITopologyRule> rulesToRemove = originalTopology.getRulesByLyr(eventLyr);
					
					if(rulesToRemove.size() > 0){
						warningMessage += PluginServices.getText(this, "Remove_Lyr_WARNING1");
						for(int i = 0; i < rulesToRemove.size() - 1; i++){
							warningMessage += rulesToRemove.get(i).getDescription(); 
							warningMessage += ";";
						}
						warningMessage += rulesToRemove.get(rulesToRemove.size() - 1).getName()+"\n";
					}
					
					warningMessage += PluginServices.getText(this, "Remove_Lyr_WARNING2");
					title = PluginServices.getText(this, "warning");
					boolean deleteLyr = GUIUtil.getInstance().
												optionMessage(warningMessage, 
													title);
					
					if(deleteLyr)
						originalTopology.removeLayer(eventLyr);
				}else if(event.eventType == SelectedLayerEvent.ADDED_EVENT_TYPE){
					boolean addLyr = true;
					if(originalTopology.getStatus() == ITopologyStatus.VALIDATED_WITH_ERRORS){
						warningMessage = PluginServices.getText(this, "AddLyrDeleteErrors_WARNING");
						title = PluginServices.getText(this, "warning");
						addLyr = GUIUtil.getInstance().optionMessage(warningMessage, title);
					}
					
					if(addLyr){
						originalTopology.addLayer(eventLyr);
						errorPanel.updateErrorTable();
					}
				}
				
				generalTab.updateContent();
				rulesTabPanel.updateContent(contentProvider);
			}});
		*/
		getJTabbedPane().addTab(PluginServices.getText(this, "Layers"), lyrsPanel);
		
		//Rules tab
		rulesTabPanel = new TopologyRulesPanel(null, contentProvider, readOnly);
		/*
		rulesTabPanel.addRuleSelectionListener(new RuleSelectionListener(){
			public void selectionEvent(SelectedRuleEvent event) {
				ITopologyRule eventRule = event.getRule();
				if(event.getEventType() == SelectedRuleEvent.RULE_REMOVED){
					originalTopology.removeRule(eventRule);
					
					generalTab.updateContent();
					rulesTabPanel.updateContent(contentProvider);
				}
				
			}});
		rulesTabPanel.addRuleSelectionListener(new RuleSelectionListener(){
			public void selectionEvent(SelectedRuleEvent event) {
				generalTab.updateContent();
			}});
		*/
		getJTabbedPane().addTab(PluginServices.getText(this, "Rules"), rulesTabPanel);
		
		
		//Topology errors tab
		errorPanel = new TopologyErrorPanel(originalTopology.getErrorContainer());
		getJTabbedPane().addTab(PluginServices.getText(this, "Errors"), errorPanel);
	}
	
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setBounds(0, 0, 
					getWindowInfo().getWidth() - 10, getWindowInfo().getHeight() - 10);
			jTabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JTabbedPane tabs = (JTabbedPane) e.getSource();
					if(acceptPanel instanceof AcceptCancelPanel){
						((AcceptCancelPanel)acceptPanel).
							setOkButtonEnabled(!(tabs.getSelectedComponent() instanceof WizardPanel));
					}
					
				}
			});
		}
		return jTabbedPane;
	}
	
//	JPanel southPanel = new JPanel();
//	JButton okButton = new JButton(PluginServices.getText(this, "OK"));
//
//	okButton.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent arg0) {
//			Window parentWindow = GUIUtil.getInstance().getParentWindow(
//					panel);
//			parentWindow.setVisible(false);
//			parentWindow.dispose();
//			panel.setOkPressed(true);
//		}
//	});
//
//	southPanel.setLayout(new BorderLayout());
//	JPanel aux = new JPanel();
//	aux.add(okButton, BorderLayout.EAST);
//	southPanel.add(aux, BorderLayout.EAST);
//	panel.add(southPanel, BorderLayout.SOUTH);
	
	private JPanel getAcceptPanel(){
		if(acceptPanel == null){
			acceptPanel = new JPanel();
			JButton okButton = new JButton(PluginServices.getText(this, "OK"));
	
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (PluginServices.getMainFrame() != null) {
						PluginServices.getMDIManager().closeWindow((IWindow) TopologyPropertiesPanel.this);
					} else {
						((JDialog) (getParent().getParent().getParent().getParent())).dispose();
					}
				}
			});
			acceptPanel.setLayout(new BorderLayout());
			JPanel aux = new JPanel();
			aux.add(okButton, BorderLayout.EAST);
			acceptPanel.add(aux, BorderLayout.EAST);
		}
		return acceptPanel;
	}
	
	private JPanel getAcceptCancelPanel(){
		if (acceptPanel == null) {
			ActionListener okAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					accepted = true;
					try {
						verifyChanges();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (PluginServices.getMainFrame() == null) {
						((JDialog) (getParent().getParent().getParent().getParent())).dispose();
					} else {
						PluginServices.getMDIManager().closeWindow((IWindow) TopologyPropertiesPanel.this);
					}
				}
			};
			
			ActionListener cancelAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (PluginServices.getMainFrame() != null) {
						PluginServices.getMDIManager().closeWindow((IWindow) TopologyPropertiesPanel.this);
					} else {
						((JDialog) (getParent().getParent().getParent().getParent())).dispose();
					}
				}
			};
			acceptPanel = new AcceptCancelPanel(okAction, cancelAction);
		}
		return acceptPanel;
	}
	
	public boolean isAccepted() {
		return accepted;
	}
	
	public WindowInfo getWindowInfo() {
		WindowInfo m_viewinfo = new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.RESIZABLE | WindowInfo.PALETTE);
//		WindowInfo m_viewinfo = new WindowInfo(WindowInfo.MODELESSDIALOG| WindowInfo.RESIZABLE | WindowInfo.PALETTE);
		m_viewinfo.setTitle(PluginServices.getText(this, "Topology_properties"));
		m_viewinfo.setHeight(500);
		m_viewinfo.setWidth(620);
		return m_viewinfo;
	}
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	
	private void verifyChanges() throws CloneNotSupportedException{
		
		if(generalTab.isChanged()){
			String topologyName = generalTab.getTopologyName();
			
			double clusterTolerance = generalTab.getClusterTolerance();
			if(clusterTolerance < 0){
				GUIUtil.getInstance().messageBox(PluginServices.getText(this, "Message_cluster_tol_not_numeric"), 
						PluginServices.getText(this, "Message_error_in_data_input"));
				return;
			}
			
			int maxNumberOfErrors = generalTab.getMaxNumberOfErrors();
			if(maxNumberOfErrors < 0){
				GUIUtil.getInstance().messageBox(PluginServices.getText(this, "Max_number_Errors_not_numeric"), 
						PluginServices.getText(this, "Message_error_in_data_input"));
				return;
			}
			
			originalTopology.setName(topologyName);
			//Meter aquí un mensaje que avise de que si se cambia la tolerancia de cluster se va a resetear
			//el estado y los errores topologicos 
			originalTopology.setClusterTolerance(clusterTolerance);
			originalTopology.setMaxNumberOfErrors(maxNumberOfErrors);
		}//general tab
		
//		try {
//			Topology.copyProperties(workingTopology, originalTopology);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
		
        GUIUtil.getInstance().updateTopologyInToc(originalTopology.getMapContext(), originalTopology);		

	}
	
	
	/**
	 * Panel with general properties of a topology.
	 * 
	 * @author Alvaro Zabala
	 * 
	 * TODO Extend BoxLayoutPanel instead of GridBagLayoutPanel.
	 *
	 */
	class GeneralTabPanel extends GridBagLayoutPanel {
		private static final long serialVersionUID = 2564792443049810354L;
		JTextField nameTextField = null;
		JTextField clusterTextField;
		JLabel statusLabel;
		JTextField maxNumberOfErrorsTextField;
		JLabel nLyrsLabel;
		JLabel nRulesLabel;
		
		boolean readOnly = false;
		
		boolean changed = false;
		
		public GeneralTabPanel(boolean readOnly){
			super();
			this.readOnly = readOnly;
			initialize();
		}
		
		public String getTopologyName(){
			return nameTextField.getText();
		}
		
		public boolean isChanged(){
			return changed;
		}
		
		public double getClusterTolerance(){
			double newClusterTol;
			try{
				newClusterTol = Double.parseDouble(clusterTextField.getText());
			}catch(NumberFormatException e){
				newClusterTol = -1;
			}
			return newClusterTol;
		}
		
		public int getMaxNumberOfErrors(){
			int maxNumberErrors;
			try{
				maxNumberErrors = Integer.parseInt(maxNumberOfErrorsTextField.getText());
			}catch(NumberFormatException e){
				maxNumberErrors = -1;
			}	
			return maxNumberErrors;
		}
		
		class ChangeDocumentListener implements DocumentListener {
		    public void insertUpdate(DocumentEvent e) {
		        testChange(e);
		    }
		    public void removeUpdate(DocumentEvent e) {
		        testChange(e);
		    }
		    public void changedUpdate(DocumentEvent e) {
		        //Plain text components don't fire these events
		    }

		    public void testChange(DocumentEvent e) {
		        int changeLength = e.getLength();
		        if(changeLength > 0)
		        	changed = true;
		    }
		}
		
		private void initialize(){
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			add(Box.createVerticalStrut(10));
			JPanel firstPanel = new JPanel();
			firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.X_AXIS));
			firstPanel.add(Box.createHorizontalStrut(15));
			String topologyName = originalTopology.getName();
			firstPanel.add(new JLabel(PluginServices.getText(this, "topology_name")));
			firstPanel.add(Box.createHorizontalStrut(5));
			nameTextField = new JTextField(topologyName, 20);
			firstPanel.add(nameTextField);
			firstPanel.setPreferredSize(new Dimension(500, 20));
			firstPanel.setMinimumSize(new Dimension(500, 20));
			firstPanel.setMaximumSize(new Dimension(500, 20));
			firstPanel.setAlignmentX(LEFT_ALIGNMENT);
			add(firstPanel);

			
			add(Box.createVerticalStrut(10));
			JPanel secondPanel = new JPanel();
			secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.X_AXIS));
			secondPanel.add(Box.createHorizontalStrut(15));
			double clusterTolerance = originalTopology.getClusterTolerance();
			secondPanel.add(new JLabel(PluginServices.getText(this, "cluster_tolerance")));
			secondPanel.add(Box.createHorizontalStrut(5));
			clusterTextField = new JTextField(Double.toString(clusterTolerance), 20);
			secondPanel.add(clusterTextField);
			secondPanel.setPreferredSize(new Dimension(500, 20));
			secondPanel.setMinimumSize(new Dimension(500, 20));
			secondPanel.setMaximumSize(new Dimension(500, 20));
			secondPanel.setAlignmentX(LEFT_ALIGNMENT);
			add(secondPanel);
			
			add(Box.createVerticalStrut(10));
			JPanel thirdPanel = new JPanel();
			thirdPanel.setLayout(new BoxLayout(thirdPanel, BoxLayout.X_AXIS));
			thirdPanel.add(Box.createHorizontalStrut(15));
			thirdPanel.add(new JLabel(PluginServices.getText(this, "topology_status")));
			String topologyText = getStatusAsText();
			thirdPanel.add(Box.createHorizontalStrut(5));
			statusLabel = new JLabel("<html><b>"+topologyText+"</b></html>");
			thirdPanel.add(statusLabel);
			thirdPanel.setPreferredSize(new Dimension(500, 20));
			thirdPanel.setMinimumSize(new Dimension(500, 20));
			thirdPanel.setMaximumSize(new Dimension(500, 20));
			thirdPanel.setAlignmentX(LEFT_ALIGNMENT);
			add(thirdPanel);
			
			add(Box.createVerticalStrut(10));
			JPanel fourthPanel = new JPanel();
			int maxNumberOfErrors = originalTopology.getMaxNumberOfErrors();
			fourthPanel.setLayout(new BoxLayout(fourthPanel, BoxLayout.X_AXIS));
			fourthPanel.add(Box.createHorizontalStrut(15));
			fourthPanel.add(new JLabel(PluginServices.getText(this, "max_number_of_errors")));
			fourthPanel.add(Box.createHorizontalStrut(5));
			maxNumberOfErrorsTextField = new JTextField(Integer.toString(maxNumberOfErrors), 20);
			fourthPanel.add(maxNumberOfErrorsTextField);
			fourthPanel.setPreferredSize(new Dimension(500, 20));
			fourthPanel.setMinimumSize(new Dimension(500, 20));
			fourthPanel.setMaximumSize(new Dimension(500, 20));
			fourthPanel.setAlignmentX(LEFT_ALIGNMENT);
			add(fourthPanel);
			
			add(Box.createVerticalStrut(10));
			JPanel fithPanel = new JPanel();
			int numberOfLyrs = originalTopology.getLayerCount();
			fithPanel.setLayout(new BoxLayout(fithPanel, BoxLayout.X_AXIS));
			fithPanel.add(Box.createHorizontalStrut(15));
			fithPanel.add(new JLabel(PluginServices.getText(this, "number_of_layers")));
			fithPanel.add(Box.createHorizontalStrut(5));
			nLyrsLabel = new JLabel("<html><b>"+numberOfLyrs+"</b></html>");
			fithPanel.add(nLyrsLabel);
			fithPanel.setPreferredSize(new Dimension(500, 20));
			fithPanel.setMinimumSize(new Dimension(500, 20));
			fithPanel.setMaximumSize(new Dimension(500, 20));
			fithPanel.setAlignmentX(LEFT_ALIGNMENT);
			add(fithPanel);
			
			
			add(Box.createVerticalStrut(10));
			JPanel sixthPanel = new JPanel();
			int numberOfRules = originalTopology.getAllRules().size();
			sixthPanel.setLayout(new BoxLayout(sixthPanel, BoxLayout.X_AXIS));
			sixthPanel.add(Box.createHorizontalStrut(15));
			sixthPanel.add(new JLabel(PluginServices.getText(this, "number_of_rules")));
			sixthPanel.add(Box.createHorizontalStrut(5));
			nRulesLabel = new JLabel(String.valueOf(numberOfRules));
			sixthPanel.add(nRulesLabel);
			sixthPanel.setPreferredSize(new Dimension(500, 20));
			sixthPanel.setMinimumSize(new Dimension(500, 20));
			sixthPanel.setMaximumSize(new Dimension(500, 20));
			sixthPanel.setAlignmentX(LEFT_ALIGNMENT);
			add(sixthPanel);
			add(Box.createVerticalStrut(10));	
			
			//if read only, disable text fields
			if(readOnly){
				nameTextField.setEditable(false);
				clusterTextField.setEditable(false);
				maxNumberOfErrorsTextField.setEditable(false);
			}else{
				ChangeDocumentListener changeListener = new ChangeDocumentListener();
				nameTextField.getDocument().addDocumentListener(changeListener);
				clusterTextField.getDocument().addDocumentListener(changeListener);
				maxNumberOfErrorsTextField.getDocument().addDocumentListener(changeListener);
			}
		}

		/**
		 * Returns a descriptive text with the status of the topology
		 * 
		 * @return topology status as text
		 */
		private String getStatusAsText() {
			byte topologyStatus = originalTopology.getStatus();
			String topologyText = null;
			switch(topologyStatus){
			case Topology.VALIDATED:
				topologyText = PluginServices.getText(this, "VALIDATED_STATUS");
				break;
			case Topology.NOT_VALIDATED:
				topologyText = PluginServices.getText(this, "NOT_VALIDATED_STATUS");
				break;
			case Topology.VALIDATED_WITH_DIRTY_ZONES:
				topologyText = PluginServices.getText(this, "VALIDATED_WITH_DIRTY_ZONES");
				break;
			case Topology.VALIDATING:
				topologyText = PluginServices.getText(this, "VALIDATING_STATUS");
				break;
			case Topology.VALIDATED_WITH_ERRORS:
				topologyText = PluginServices.getText(this, "VALIDATED_WITH_ERRORS");
				break;
			case Topology.EMPTY:
				topologyText = PluginServices.getText(this, "EMPTY_STATUS");
				break;
			default:
			}
			return topologyText;
		}
		
		
		public void updateContent(){
			nameTextField.setText(originalTopology.getName());
			clusterTextField.setText(Double.toString(originalTopology.getClusterTolerance()));
			String statusText = getStatusAsText();
			statusLabel.setText("<html><b>"+statusText+"</b></html>");
			maxNumberOfErrorsTextField.setText(Integer.toString(originalTopology.getMaxNumberOfErrors()));
			nLyrsLabel.setText("<html><b>"+originalTopology.getLayerCount()+"</b></html>");
			nRulesLabel.setText(String.valueOf(originalTopology.getRuleCount()));
		}
	}
	
	
	/**
	 * Provides layers to LayerSelectionPanel from a Topology
	 * @author Alvaro Zabala
	 *
	 */
	class TopologyContentProvider implements ITopologyContentProvider
	{
		Topology topology;
		
		public TopologyContentProvider(Topology topology) {
			this.topology = topology;
		}

		public List<FLyrVect> getLayers() {
			return topology.getLayers();
		}

		public MapContext getMapContext() {
			return topology.getMapContext();
		}

		public List<ITopologyRule> getRules() {
			return topology.getAllRules();
		}
	}
	
}
