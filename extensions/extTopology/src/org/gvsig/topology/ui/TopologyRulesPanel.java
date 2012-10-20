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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.gvsig.gui.beans.swing.JButton;
import org.gvsig.topology.IOneLyrRule;
import org.gvsig.topology.ITopologyRule;
import org.gvsig.topology.ITwoLyrRule;
import org.gvsig.topology.topologyrules.MustBeLargerThanClusterTolerance;
import org.gvsig.topology.ui.util.GUIUtil;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

/**
 * Panel to show topology rules of a topology
 * 
 * @author Alvaro Zabala
 * 
 *         TODO Ver si quitamos ITopologyContentProvider y usamos Topology en su
 *         lugar. La idea de ITopologyContentProvider es que un mismo control
 *         valga para topologias ya existentes o bien para factorias, cuando
 *         todavia no se ha creado la topologia
 * 
 */
public class TopologyRulesPanel extends JWizardPanel {

	private static final long serialVersionUID = 4121232928062159340L;

	/**
	 * text of the title showed in the component
	 */
	private static String title = PluginServices
			.getText(null, "Topology_rules");

	/**
	 * Rules showed by this component as selected
	 */
	private List<ITopologyRule> rules;

	/**
	 * Provides rules from or for a topology
	 */
	private ITopologyContentProvider contentProvider;

	/**
	 * Contains listeners interested in the addition or removing of rules to
	 * this component.
	 */
	private List<RuleSelectionListener> selectionListeners;

	/**
	 * table which shows the selected rules
	 */
	private JTable rulesTable;

	private boolean readOnly;

	public TopologyRulesPanel(JWizardComponents wizardComponents,
			ITopologyContentProvider layerProvider) {
		this(wizardComponents, layerProvider, false);
	}

	public TopologyRulesPanel(JWizardComponents wizardComponents,
			ITopologyContentProvider layerProvider, boolean readOnly) {
		super(wizardComponents, title);
		this.contentProvider = layerProvider;
		this.selectionListeners = new ArrayList<RuleSelectionListener>();
		this.readOnly = readOnly;
		setLayout(new BorderLayout());
		rules = new ArrayList<ITopologyRule>();
		List<ITopologyRule> existingRules = contentProvider.getRules();
		if (existingRules != null && existingRules.size() > 0)
			rules.addAll(existingRules);
		initialize();
	}

	public void addRuleSelectionListener(RuleSelectionListener selectionListener) {
		this.selectionListeners.add(selectionListener);
	}

	public void updateContent(ITopologyContentProvider contentProvider) {
		rules.clear();
		this.contentProvider = contentProvider;
		List<ITopologyRule> existingRules = contentProvider.getRules();
		if (existingRules != null && existingRules.size() > 0)
			rules.addAll(existingRules);
		rulesTable.revalidate();

	}

	/**
	 * Interface that must be implemented by all of these classes interested in
	 * listening adding or removing rules events.
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	public interface RuleSelectionListener {
		public void selectionEvent(SelectedRuleEvent event);
	}

	/**
	 * Events generated when a rule is added or removed from this component
	 * 
	 * @author Alvaro Zabala
	 * 
	 */
	public class SelectedRuleEvent {
		public static final int RULE_ADDED = 0;
		public static final int RULE_REMOVED = 1;

		private int eventType;

		public int getEventType() {
			return eventType;
		}

		private ITopologyRule rule;

		public ITopologyRule getRule() {
			return rule;
		}
	}

	/**
	 * Returns the rules of this component's content
	 * 
	 * @return
	 */
	public List<ITopologyRule> getRules() {
		return rules;
	}

	private void fireLayerSelectionEvent(SelectedRuleEvent event) {
		for (int i = 0; i < selectionListeners.size(); i++) {
			RuleSelectionListener listener = selectionListeners.get(i);
			listener.selectionEvent(event);
		}
	}

	private void initialize() {
		JLabel rulesText = new JLabel(PluginServices.getText(null,
				"Topology_rules"));
		add(rulesText, BorderLayout.NORTH);

		TableModel dataModel = new AbstractTableModel() {
			public int getColumnCount() {
				return 2;
			}

			public int getRowCount() {
				return rules.size();
			}

			public Object getValueAt(int row, int col) {
				ITopologyRule rule = rules.get(row);
				if (col == 0)
					return rule.getName();
				else {
					String desc = "";
					desc += ((IOneLyrRule) rule).getOriginLyr().getName();

					if (rule instanceof ITwoLyrRule) {
						ITwoLyrRule twoLyrRule = (ITwoLyrRule) rule;
						desc += ", " + twoLyrRule.getDestinationLyr().getName();
					}
					return desc;
				}
			}
		};
		rulesTable = new JTable();
		rulesTable.setModel(dataModel);
		rulesTable.getColumnModel().getColumn(0)
				.setHeaderValue(PluginServices.getText(null, "Rule"));
		rulesTable.getColumnModel().getColumn(1)
				.setHeaderValue(PluginServices.getText(null, "Layers"));
		JScrollPane scrollTable = new JScrollPane(rulesTable);

		add(scrollTable, BorderLayout.CENTER);

		if (!readOnly) {
			add(getButtonsPanel(), BorderLayout.SOUTH);
		}
	}

	private JPanel getButtonsPanel() {
		JButton addRuleButton = new JButton(PluginServices.getText(null,
				"Add_Rule"));
		addRuleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<FLyrVect> selectedLyrs = contentProvider.getLayers();
				if (selectedLyrs.size() == 0) {
					GUIUtil.getInstance().messageBox(
							"Message_no_layers_selected",
							"Message_error_creating_topology");
					return;
				}
				NewTopologyRulePanel newRulePanel = new NewTopologyRulePanel(
						contentProvider.getLayers(), contentProvider
								.getMapContext());
				PluginServices.getMDIManager().addWindow(newRulePanel);
				ITopologyRule newRule = newRulePanel.getNewTopologyRule();
				if (newRule != null) {
					if (!rules.contains(newRule)) {
						rules.add(newRule);
						SelectedRuleEvent event = new SelectedRuleEvent();
						event.eventType = SelectedRuleEvent.RULE_ADDED;
						fireLayerSelectionEvent(event);
					} else {
						GUIUtil.getInstance().messageBox(
								"Message_rule_already_in_topology",
								"Message_error_creating_topology");
					}
				}
				rulesTable.revalidate();
			}
		});

		JButton removeRuleButton = new JButton(PluginServices.getText(this,
				"Remove_Rule"));
		removeRuleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int rowIdx = rulesTable.getSelectedRow();
				if (rowIdx == -1) {
					GUIUtil.getInstance().messageBox(
							PluginServices.getText(this,
									"Message_error_select_rule"),
							PluginServices.getText(this,
									"Message_error_creating_topology"));
					return;
				}
				ITopologyRule rule = rules.get(rowIdx);

				if (rule instanceof MustBeLargerThanClusterTolerance) {
					GUIUtil.getInstance().messageBox(
							PluginServices.getText(this,
									"Message_error_delete_cluster_rule"),
							PluginServices.getText(this,
									"Message_error_creating_topology"));
					return;
				}
				SelectedRuleEvent event = new SelectedRuleEvent();
				event.eventType = SelectedRuleEvent.RULE_REMOVED;
				event.rule = rule;
				fireLayerSelectionEvent(event);
				rules.remove(rule);
				rulesTable.revalidate();
			}
		});

		JButton removeAllRulesBtn = new JButton(PluginServices.getText(this,
				"Remove_All"));
		removeAllRulesBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < rules.size(); i++) {
					SelectedRuleEvent event = new SelectedRuleEvent();
					event.eventType = SelectedRuleEvent.RULE_REMOVED;
					fireLayerSelectionEvent(event);
				}
				rules.clear();
				rulesTable.revalidate();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.add(addRuleButton);
		buttonPanel.add(removeRuleButton);
		buttonPanel.add(removeAllRulesBtn);
		return buttonPanel;
	}
}
