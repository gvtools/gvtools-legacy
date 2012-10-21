/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;

import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.AvailableLayersTreeCellRenderer;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.JMultilineToolTip;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.LayersListElement;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.SelectedLayersListCellRenderer;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.ServiceNamesObject;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.ServicesTableModel;
import es.prodevelop.cit.gvsig.arcims.gui.wizards.ArcImsWizard;

/**
 * This class implements the ArcIMS wizard's panel which enables the user to
 * select which layers must be retrieved from a Image Service (selected on the
 * ServiceNamesPanel).
 * 
 * @see es.prodevelop.cit.gvsig.arcims.gui.panels.ServiceNamesPanel
 * 
 * 
 * @author jldominguez
 */
public class FeatureServicePanel extends JPanel implements ActionListener,
		ItemListener, ListDataListener, KeyListener, ChangeListener,
		TreeSelectionListener, MouseListener {
	protected static Logger logger = Logger.getLogger(FeatureServicePanel.class
			.getName());
	private static final long serialVersionUID = 0;
	protected ArcImsWizard parentWizard = null;
	protected JPanel detailsPanel = null;
	protected JPanel layersPanel = null;
	protected JPanel selectedLayersPanel = null;
	protected JPanel finalPanel = null;
	protected JLabel newLayerNameLabel = null;
	protected JTextField newLayerNameTextField = null;
	protected JButton selectedLayersUpButton = null;
	protected JButton selectedLayersDownButton = null;
	protected JList orderedLayersList = null;
	protected JTree availableLayersTree = null;
	protected DefaultTreeModel availableLayersTreeModel = null;
	protected DefaultMutableTreeNode layersTreeRootNode = null;
	protected ServiceNamesObject layersTreeRootObject = null;
	protected DefaultListModel orderedLayersListModel = null;
	protected JButton addLayerButton = null;
	protected JButton removeLayerButton = null;
	protected JScrollPane availableLayersScrollPane = null;
	protected JScrollPane selectedLayersScrollPane = null;
	protected JButton addAllButton = null;
	protected JButton removeAllButton = null;
	protected JScrollPane serviceDetailsScrollPane = null;
	protected JEditorPane serviceDetailsTable = null;
	protected Vector serviceDetailsTableDataVector = null;
	protected Vector serviceDetailsTableColumnNamesVector = null;
	protected JTabbedPane imgServiceTabbedPane = null;
	protected JPanel tabPanel = null;
	protected JPanel imgServiceTab_1 = null;
	protected JPanel imgServiceTab_2 = null;
	protected JPanel changeServerPanel = null;
	protected JButton serviceInfoNextButton = null;
	protected JButton serviceInfoPreviousButton = null;
	protected JButton changeServerButton = null;
	protected JCheckBox showIdCheckBox = null;
	protected final String bgColor0 = "\"#FEEDD6\""; // light -
	protected final String bgColor3 = "\"#FBFFE1\""; // light yellow
	protected final String font = "Arial";
	private JPanel groupCheckPanel;
	private JCheckBox groupCheckBox;
	private JLabel versionTitleLabel = null;
	private JLabel versionLabel = null;
	protected boolean isProperties = false;

	/**
	 * The owning wizard is useds as a parameter to allow easy <i>upwards</i>
	 * references.
	 * 
	 * @param parent
	 *            the wizard that owns this panel
	 */
	public FeatureServicePanel(ArcImsWizard parent, boolean editionallowed,
			boolean properties) {
		super();
		isProperties = properties;
		parentWizard = parent;
		initialize(editionallowed);
	}

	protected void initialize(boolean editionallowed) {
		setLayout(null);
		this.setSize(new java.awt.Dimension(511, 470));
		this.initInfoVectors();

		imgServiceTab_1 = new JPanel();
		imgServiceTab_1.setLayout(null);
		imgServiceTab_2 = new JPanel();
		imgServiceTab_2.setLayout(null);

		imgServiceTab_1.add(getDetailsPanel(), null);
		imgServiceTab_2.add(getFinalPanel(), null);
		imgServiceTab_2.add(getLayersPanel(), null);
		imgServiceTab_2.add(getShowIdCheckBox(), null);
		imgServiceTab_2.add(getSelectedLayersPanel(), null);

		addImageFormatPanel();

		tabPanel = new JPanel();
		tabPanel.setBounds(18, 8, 490 - 15, 415);
		tabPanel.setLayout(new BorderLayout());

		imgServiceTabbedPane = new JTabbedPane();

		// imgServiceTabbedPane.setBounds(0, 0, 510, 432);
		imgServiceTabbedPane.addTab(PluginServices.getText(this, "info"),
				imgServiceTab_1);

		if (editionallowed) {
			imgServiceTabbedPane.addTab(PluginServices.getText(this, "layers"),
					imgServiceTab_2);
		}

		imgServiceTabbedPane.addChangeListener(this);

		changeServerButton = new JButton(PluginServices.getText(this,
				"change_service"));
		changeServerButton.addActionListener(this);
		changeServerButton.setBounds(95, 8, 120, 25);
		serviceInfoPreviousButton = new JButton(PluginServices.getText(this,
				"previous"));
		serviceInfoPreviousButton.addActionListener(this);
		serviceInfoPreviousButton.setBounds(395 - 103, 4, 100, 20);
		serviceInfoNextButton = new JButton(
				PluginServices.getText(this, "next"));
		serviceInfoNextButton.addActionListener(this);
		serviceInfoNextButton.setBounds(395, 4, 100, 20);

		changeServerPanel = new JPanel();
		changeServerPanel.setLayout(null);
		changeServerPanel.setBounds(0, 440, 525, 42);

		// changeServerPanel.add(changeServerButton);
		changeServerPanel.add(serviceInfoPreviousButton);
		changeServerPanel.add(serviceInfoNextButton);

		versionTitleLabel = new JLabel(PluginServices.getText(this,
				"server_version") + ":");
		versionTitleLabel.setBounds(new java.awt.Rectangle(39, 443, 125, 21));
		add(versionTitleLabel, null);
		versionLabel = new JLabel("-");
		versionLabel.setBounds(new java.awt.Rectangle(170 - 15, 443, 160, 21));
		add(versionLabel, null);

		tabPanel.add(imgServiceTabbedPane, BorderLayout.CENTER);
		add(tabPanel);
		add(changeServerPanel);
	}

	// previous (395 - 103, 444, 100, 20));
	protected void addImageFormatPanel() {
		groupCheckPanel = new JPanel();
		groupCheckPanel.setBounds(180 - 6, 210 - 8, 501 - 180 - 34, 54); // hasta
																			// y
																			// =
																			// 264
		groupCheckPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
				null, PluginServices.getText(this, "group_layers_option"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

		groupCheckBox = new JCheckBox(PluginServices.getText(this,
				"group_layers"), false);
		groupCheckBox.setBounds(14, 20, 155, 21);

		groupCheckPanel.setLayout(null);
		groupCheckPanel.add(groupCheckBox);
		imgServiceTab_2.add(groupCheckPanel);
	}

	// private J
	protected void initInfoVectors() {
		serviceDetailsTableDataVector = new Vector();

		orderedLayersListModel = new DefaultListModel();
		orderedLayersListModel.addListDataListener(this);
	}

	/**
	 * Gets a list model that contains the layers that have been selected by the
	 * user (among those offered by the server)
	 * 
	 * @return the selected layers' list model
	 */
	public DefaultListModel getOrderedLayersListModel() {
		return this.orderedLayersListModel;
	}

	/**
	 * This method initializes detailsPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			detailsPanel = new JPanel();
			detailsPanel.setLayout(null);
			detailsPanel.setBounds(new java.awt.Rectangle(5, 5, 470 - 15,
					345 + 30));
			serviceDetailsScrollPane = new JScrollPane();
			detailsPanel.add(serviceDetailsScrollPane, null);
		}

		return detailsPanel;
	}

	public void setDetailsPanelServiceNameInBorder(String name) {
		this.detailsPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(null,
						PluginServices.getText(this, "service_info") + ": "
								+ name,
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
						null));
	}

	/**
	 * This method initializes layersPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getLayersPanel() {
		if (layersPanel == null) {
			layersPanel = new JPanel();
			layersPanel.setLayout(null);
			layersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "available_layers"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			layersPanel.setBounds(new java.awt.Rectangle(4, 58, 491 - 34,
					151 - 8)); // hasta y = 209
			layersPanel.add(getAvailableLayersScrollPane(), null);
		}

		return layersPanel;
	}

	protected JCheckBox getShowIdCheckBox() {
		if (showIdCheckBox == null) {
			showIdCheckBox = new JCheckBox(PluginServices.getText(this,
					"show_layer_ids"), false);

			// showIdCheckBox.setFont(new Font("SimSun", Font.PLAIN, 12));
			showIdCheckBox.setBounds(4, 230 - 8, 168, 15);
			showIdCheckBox.addItemListener(this);
		}

		return showIdCheckBox;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getSelectedLayersPanel() {
		if (selectedLayersPanel == null) {
			selectedLayersPanel = new JPanel();
			selectedLayersPanel.setLayout(null);
			selectedLayersPanel
					.setBorder(javax.swing.BorderFactory.createTitledBorder(
							null,
							PluginServices.getText(this, "selected_layers"),
							javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
							javax.swing.border.TitledBorder.DEFAULT_POSITION,
							null, null));
			selectedLayersPanel.setBounds(new java.awt.Rectangle(4, 265 - 8,
					491 - 34, 130));
			selectedLayersPanel.add(getSelectedLayersUpButton(), null);
			selectedLayersPanel.add(getSelectedLayersDownButton(), null);
			selectedLayersPanel.add(getAddLayerButton(), null);
			selectedLayersPanel.add(getRemoveLayerButton(), null);
			selectedLayersPanel.add(getSelectedLayersScrollPane(), null);
			selectedLayersPanel.add(getAddAllButton(), null);
			selectedLayersPanel.add(getRemoveAllButton(), null);
		}

		return selectedLayersPanel;
	}

	/**
	 * This method initializes finalPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getFinalPanel() {
		if (finalPanel == null) {
			newLayerNameLabel = new JLabel();
			newLayerNameLabel
					.setBounds(new java.awt.Rectangle(60, 18, 195, 21));
			newLayerNameLabel.setText(PluginServices.getText(this,
					"enter_new_layer_name"));
			finalPanel = new JPanel();
			finalPanel.setLayout(null);
			finalPanel.setBounds(new java.awt.Rectangle(4, 8, 491 - 34, 49)); // hasta
																				// y
																				// =
																				// 58
			finalPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "new_layer_name"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));

			// finalPanel.add(newLayerNameLabel, null);
			finalPanel.add(getNewLayerNameTextField(), null);
		}

		return finalPanel;
	}

	/**
	 * This method initializes newLayerNameTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	public JTextField getNewLayerNameTextField() {
		if (newLayerNameTextField == null) {
			newLayerNameTextField = new JTextField();
			newLayerNameTextField.addKeyListener(this);
			newLayerNameTextField.addActionListener(this);
			newLayerNameTextField.setBounds(new java.awt.Rectangle(10, 19, 437,
					20));
		}

		return newLayerNameTextField;
	}

	/**
	 * This method initializes availableLayersTree
	 * 
	 * @return javax.swing.JTree
	 */
	public void setAvailableLayersTree(JTree t) {
		availableLayersTree = t;
	}

	public JTree getAvailableLayersTree() {
		if (availableLayersTree == null) {
			availableLayersTreeModel = new DefaultTreeModel(layersTreeRootNode);
			availableLayersTree = new JTree(availableLayersTreeModel) {
				public JToolTip createToolTip() {
					JMultilineToolTip jmtt = new JMultilineToolTip();

					return jmtt;
				}

				private static final long serialVersionUID = 0;
			};

			ToolTipManager.sharedInstance().registerComponent(
					availableLayersTree);
			availableLayersTree
					.setCellRenderer(new AvailableLayersTreeCellRenderer());

			availableLayersTree.addTreeSelectionListener(this);
			availableLayersTree.addMouseListener(this);
		}

		return availableLayersTree;
	}

	/**
	 * This method initializes selectedLayersUpButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getSelectedLayersUpButton() {
		if (selectedLayersUpButton == null) {
			selectedLayersUpButton = new JButton();

			ImageIcon ii = createImageIcon("images/up-arrow.png");
			selectedLayersUpButton.setIcon(ii);
			selectedLayersUpButton.setBounds(new java.awt.Rectangle(10, 35, 36,
					36));
			selectedLayersUpButton.addActionListener(this);
		}

		return selectedLayersUpButton;
	}

	/**
	 * This method initializes selectedLayersDownButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getSelectedLayersDownButton() {
		if (selectedLayersDownButton == null) {
			selectedLayersDownButton = new JButton();

			ImageIcon ii = createImageIcon("images/down-arrow.png");
			selectedLayersDownButton.setIcon(ii);
			selectedLayersDownButton.setBounds(new java.awt.Rectangle(10, 75,
					36, 36));
			selectedLayersDownButton.addActionListener(this);
		}

		return selectedLayersDownButton;
	}

	/**
	 * This method initializes orderedLayersList
	 * 
	 * @return javax.swing.JList
	 */
	public void setOrderedLayersList(JList l) {
		orderedLayersList = l;
	}

	public JList getOrderedLayersList() {
		if (orderedLayersList == null) {
			if (orderedLayersListModel == null) {
				orderedLayersListModel = new DefaultListModel();
				orderedLayersListModel.addListDataListener(this);
			}

			orderedLayersList = new JList(orderedLayersListModel) {
				public JToolTip createToolTip() {
					JMultilineToolTip jmltt = new JMultilineToolTip();

					return jmltt;
				}

				private static final long serialVersionUID = 0;
			};
			ToolTipManager.sharedInstance()
					.registerComponent(orderedLayersList);
			orderedLayersList
					.setCellRenderer(new SelectedLayersListCellRenderer());
		}

		return orderedLayersList;
	}

	/**
	 * This method initializes addLayerButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getAddLayerButton() {
		if (addLayerButton == null) {
			addLayerButton = new JButton();
			addLayerButton.setText(PluginServices.getText(this, "add"));
			addLayerButton.addActionListener(this);
			addLayerButton.setBounds(new java.awt.Rectangle(365 - 34, 25, 111,
					21));
		}

		return addLayerButton;
	}

	/**
	 * This method initializes removeLayerButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getRemoveLayerButton() {
		if (removeLayerButton == null) {
			removeLayerButton = new JButton();
			removeLayerButton.setText(PluginServices.getText(this, "remove"));
			removeLayerButton.addActionListener(this);
			removeLayerButton.setBounds(new java.awt.Rectangle(365 - 34, 75,
					111, 21));
		}

		return removeLayerButton;
	}

	public void setSelectedLayersScrollPaneViewPort() {
		this.selectedLayersScrollPane.setViewportView(orderedLayersList);
	}

	/**
	 * Adds a layer to the list of available lists. This is done while parsing
	 * the server's reponse after a <tt>getCapabilities()</tt> request.
	 * 
	 * @param e
	 *            the element to be added
	 */
	public void addLayerToAvailableList(LayersListElement e) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(e);
		this.layersTreeRootNode.add(node);
	}

	/**
	 * Adds a layer to the list of selected layers after the user has clicked on
	 * the <i>Add</i> button or double-clicked on an item on the available
	 * layers list.
	 * 
	 * @param e
	 *            the element to be added
	 */
	public void addLayerToSelectedList(LayersListElement e) {
		if (orderedLayersListModel.contains(e)) {
			int r;
			String the_message = PluginServices.getText(this,
					"this_layer_was_already_added")
					+ ". "
					+ PluginServices.getText(this, "add_again_question")
					+ "\n"
					+ PluginServices.getText(this, "layer")
					+ ": "
					+ e.getName();
			String the_title = PluginServices.getText(this,
					"this_layer_was_already_added");

			r = JOptionPane.showOptionDialog(this, the_message, the_title,
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, null, null);

			if (r == JOptionPane.NO_OPTION) {
				return;
			}
		}

		e.setAdded(true);
		this.orderedLayersListModel.add(0, e);
	}

	/**
	 * Adds a layer to the list of selected layers without confirmation.
	 * 
	 * @param e
	 *            the element to be added
	 */
	public void addLayerToSelectedListNoConfirm(LayersListElement e) {
		e.setAdded(true);
		this.orderedLayersListModel.add(0, e);
	}

	/**
	 * This method initializes availableLayersScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	public JScrollPane getAvailableLayersScrollPane() {
		if (availableLayersScrollPane == null) {
			availableLayersScrollPane = new JScrollPane();
			availableLayersScrollPane.setBounds(new java.awt.Rectangle(10, 20,
					471 - 34, 123 - 8));
		}

		return availableLayersScrollPane;
	}

	/**
	 * This method initializes selectedLayersScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	public JScrollPane getSelectedLayersScrollPane() {
		if (selectedLayersScrollPane == null) {
			selectedLayersScrollPane = new JScrollPane();
			selectedLayersScrollPane.setBounds(new java.awt.Rectangle(55, 20,
					301 - 34, 101));
		}

		return selectedLayersScrollPane;
	}

	/**
	 * This method initializes addAllButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getAddAllButton() {
		if (addAllButton == null) {
			addAllButton = new JButton();
			addAllButton.setText(PluginServices.getText(this, "add_all"));
			addAllButton.addActionListener(this);
			addAllButton
					.setBounds(new java.awt.Rectangle(365 - 34, 50, 111, 21));
		}

		return addAllButton;
	}

	/**
	 * This method initializes removeAllButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getRemoveAllButton() {
		if (removeAllButton == null) {
			removeAllButton = new JButton();
			removeAllButton.setText(PluginServices.getText(this, "remove_all"));
			removeAllButton.addActionListener(this);
			removeAllButton.setBounds(new java.awt.Rectangle(365 - 34, 100,
					111, 21));
		}

		return removeAllButton;
	}

	public void contentsChanged(ListDataEvent e) {
	}

	/**
	 * Automatically invoqued when new layers are added to the selected layers
	 * list. It checks whether the wizard is ready to finish and send gvSIG the
	 * user's selection.
	 */
	public void intervalAdded(ListDataEvent e) {
		logger.info("Added. Now selected: " + orderedLayersListModel.size());
		updateWizardLayerQuery();
		parentWizard.fireWizardComplete(this.isCorrectlyConfigured());
	}

	/**
	 * Automatically invoqued when new layers are removed from the selected
	 * layers list. It checks whether the wizard is ready to finish and send
	 * gvSIG the user's selection.
	 */
	public void intervalRemoved(ListDataEvent e) {
		logger.info("Removed. Now selected: " + orderedLayersListModel.size());
		updateWizardLayerQuery();
		this.parentWizard.fireWizardComplete(this.isCorrectlyConfigured());
	}

	/**
	 * Checks whether the wizard is ready to finish and send gvSIG the user's
	 * selection.
	 * 
	 * @return <b>true</b> if the wizard is ready to finish, <b>false</b>
	 *         otherwise
	 */
	public boolean isCorrectlyConfigured() {
		if (this.orderedLayersListModel.size() == 0) {
			return false;
		}

		if (this.newLayerNameTextField.getText().length() == 0) {
			return false;
		}

		return true;
	}

	/**
	 * Updates panel's local variables: <tt>setLayerQuery(String)</tt> sets a
	 * comma-separated list of layers (the selected ones) and
	 * <tt>setNewLayerName(String)</tt> sets the name of the new layer to be
	 * created and sent to gvSIG.
	 */
	protected void updateWizardLayerQuery() {
		if (orderedLayersListModel.size() == 0) {
			return;
		}

		String idAcum = "";

		for (int i = orderedLayersListModel.size() - 1; i >= 0; i--) {
			idAcum = idAcum
					+ ","
					+ ((LayersListElement) orderedLayersListModel.get(i))
							.getID();
		}

		idAcum = idAcum.substring(1); // elimina la coma inicial

		parentWizard.setLayerQuery(idAcum);
		parentWizard.setNewLayerName(this.newLayerNameTextField.getText());
	}

	/**
	 * Empties both lists: available layers list and selected layers list.
	 */
	public void emptyTables() {
		// re-start available layers' tree node:
		layersTreeRootObject = new ServiceNamesObject(
				parentWizard.getServiceName(), "ImageServer", "Enabled");
		layersTreeRootNode = new DefaultMutableTreeNode(layersTreeRootObject);

		// empty the service dateails table:
		loadServiceDetailsTable(null);

		if (availableLayersTree != null) {
			availableLayersTree.removeAll();
		}

		refreshAvailableLayersTree();

		if (orderedLayersListModel != null) {
			orderedLayersListModel.removeAllElements();
		}

		refreshSelectedLayersList();

		newLayerNameTextField.setText(parentWizard.getServiceName());
	}

	/**
	 * Listens to button events: Add (all) layer, Remove (all) layer, Move up,
	 * Move down.
	 */
	public void actionPerformed(ActionEvent e) {
		LayersListElement sel;
		DefaultMutableTreeNode node;
		int ind;
		int noofremoved;
		int[] selInds;

		if (e.getSource() == addLayerButton) {
			// JTable.getSelectionRows() is 0-based but root is displayed so
			// item 0 is the root itself
			selInds = availableLayersTree.getSelectionRows();

			if (selInds == null) {
				return;
			}

			if (selInds.length == 0) {
				return;
			}

			Object root = availableLayersTree.getModel().getRoot();
			int noofchildren = availableLayersTree.getModel().getChildCount(
					root);

			for (int i = 0; i < noofchildren; i++) {
				// JTable.getSelectionRows() is 0-based but root is displayed so
				// item 0 is the root itself, and we ignore it here of course:
				ind = LayersListElement.getFirstIndexInIntArray(selInds, i + 1);

				if (ind != -1) {
					node = (DefaultMutableTreeNode) availableLayersTree
							.getModel().getChild(root, i);
					sel = (LayersListElement) node.getUserObject();
					addLayerToSelectedList(sel);
				}
			}

			refreshAvailableLayersTree();
			refreshSelectedLayersList();
		}

		if (e.getSource() == removeLayerButton) {
			selInds = orderedLayersList.getSelectedIndices();

			if (selInds.length == 0) {
				return;
			}

			noofremoved = 0;

			for (int i = 0; i < selInds.length; i++) {
				sel = (LayersListElement) orderedLayersListModel
						.remove(selInds[i] - noofremoved);
				noofremoved++;
				sel.setAdded(orderedLayersListModel.contains(sel));
			}

			refreshAvailableLayersTree();
			refreshSelectedLayersList();
		}

		if (e.getSource() == addAllButton) {
			Object root = availableLayersTree.getModel().getRoot();
			int nofchildren = this.availableLayersTree.getModel()
					.getChildCount(root);

			for (int i = 0; i < nofchildren; i++) {
				node = (DefaultMutableTreeNode) availableLayersTree.getModel()
						.getChild(root, i);
				sel = (LayersListElement) node.getUserObject();
				logger.info("Adding element: " + sel.toString());
				addLayerToSelectedList(sel);
			}

			refreshAvailableLayersTree();
			refreshSelectedLayersList();
		}

		if (e.getSource() == removeAllButton) {
			while (orderedLayersListModel.size() > 0) {
				sel = (LayersListElement) this.orderedLayersListModel.remove(0);
				sel.setAdded(false);
			}

			refreshAvailableLayersTree();
			refreshSelectedLayersList();
		}

		if (e.getSource() == selectedLayersDownButton) {
			int l = orderedLayersListModel.size();

			if (l == 0) {
				return;
			}

			selInds = orderedLayersList.getSelectedIndices();

			if (selInds.length != 1) {
				return;
			}

			if (l == (selInds[0] + 1)) {
				return; // last item cannot be moved down
			}

			sel = (LayersListElement) orderedLayersListModel.remove(selInds[0]);
			orderedLayersListModel.add(selInds[0] + 1, sel);
			refreshSelectedLayersList();
			orderedLayersList.setSelectedIndex(selInds[0] + 1);
			refreshAvailableLayersTree();
		}

		if (e.getSource() == selectedLayersUpButton) {
			int l = orderedLayersListModel.size();

			if (l == 0) {
				return;
			}

			selInds = orderedLayersList.getSelectedIndices();

			if (selInds.length != 1) {
				return;
			}

			if (selInds[0] == 0) {
				return; // first item cannot be moved up
			}

			sel = (LayersListElement) orderedLayersListModel.remove(selInds[0]);
			orderedLayersListModel.add(selInds[0] - 1, sel);
			refreshSelectedLayersList();
			orderedLayersList.setSelectedIndex(selInds[0] - 1);
			refreshAvailableLayersTree();
		}

		if (e.getSource() == newLayerNameTextField) {
			try {
				JTabbedPane jtp = ((JTabbedPane) parentWizard.getParent());
				AddLayerDialog fod = (AddLayerDialog) jtp.getParent();
				JPanel jp = (JPanel) fod.getComponent(1);
				JButton acceptButton = (JButton) jp.getComponent(0);
				ActionListener[] clickListeners = acceptButton
						.getActionListeners();
				ActionEvent artificialEvent = new ActionEvent(acceptButton,
						ActionEvent.ACTION_PERFORMED, "");

				if (!acceptButton.isEnabled()) {
					return;
				}

				for (int i = 0; i < clickListeners.length; i++)
					clickListeners[i].actionPerformed(artificialEvent);
			} catch (RuntimeException e1) {
				logger.error(
						"Unable to send the click event to the FOpenDialog 'Accept' button. ",
						e1);
			}
		}

		if (e.getSource() == changeServerButton) {
			this.parentWizard.setEnabledPanels("main");
		}

		if (e.getSource() == serviceInfoNextButton) {
			int currentTab = this.imgServiceTabbedPane.getSelectedIndex();
			int maxTab = this.imgServiceTabbedPane.getTabCount() - 1;

			if (currentTab < maxTab) {
				imgServiceTabbedPane.setSelectedIndex(currentTab + 1);
			}
		}

		if (e.getSource() == serviceInfoPreviousButton) {
			int currentTab = this.imgServiceTabbedPane.getSelectedIndex();

			if (currentTab > 0) {
				imgServiceTabbedPane.setSelectedIndex(currentTab - 1);
			} else {
				if (currentTab == 0) {
					parentWizard.setEnabledPanels("main");
				}
			}
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	/**
	 * Listens to changes in the text field where the new layer's name is typed.
	 * The decision whether the wizard is ready to finish or not depends on this
	 * event too.
	 */
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == newLayerNameTextField) {
			this.parentWizard.setNewLayerName(this.newLayerNameTextField
					.getText());
			this.parentWizard.fireWizardComplete(this.isCorrectlyConfigured());
		}
	}

	/**
	 * This method initializes serviceDetailsTable
	 * 
	 * @return JEditorPane
	 */
	protected JEditorPane getServiceDetailsTable() {
		if (serviceDetailsTable == null) {
			serviceDetailsTable = new JEditorPane();
			serviceDetailsTable.setContentType("text/html");
			serviceDetailsTable.setEditable(false);

			String htmlInfo = this.getFormattedHtmlInfo(
					serviceDetailsTableDataVector,
					serviceDetailsTableColumnNamesVector);
			serviceDetailsTable.setText(htmlInfo);
		}

		return serviceDetailsTable;
	}

	public void loadServiceDetailsTable(ServiceInformation si) {
		Vector data = new Vector();
		Vector item = new Vector();
		Vector cols = new Vector();

		if (si != null) {
			// A valid ArcImsProtocolHandler.ServiceInformation parameter
			cols.add(PluginServices.getText(this, "property"));
			cols.add(PluginServices.getText(this, "value"));

			if (si.getEnvelope() != null) {
				item.add(PluginServices.getText(this, "xrange"));
				item.add("[ "
						+ ServicesTableModel.leaveNDigits(si.getEnvelope()
								.getXmin(), 10)
						+ ", "
						+ ServicesTableModel.leaveNDigits(si.getEnvelope()
								.getXmax(), 10) + " ]");
				data.add(item.clone());
				item.removeAllElements();
				item.add(PluginServices.getText(this, "yrange"));
				item.add("[ "
						+ ServicesTableModel.leaveNDigits(si.getEnvelope()
								.getYmin(), 10)
						+ ", "
						+ ServicesTableModel.leaveNDigits(si.getEnvelope()
								.getYmax(), 10) + " ]");
				data.add(item.clone());
				item.removeAllElements();
			} else {
				item.add(PluginServices.getText(this, "xrange"));
				item.add(PluginServices.getText(this, "not_available"));
				data.add(item.clone());
				item.removeAllElements();
				item.add(PluginServices.getText(this, "yrange"));
				item.add(PluginServices.getText(this, "not_available"));
				data.add(item.clone());
				item.removeAllElements();
			}

			if (si.getLocale().getLanguage().length() > 0) {
				item.add(PluginServices.getText(this, "language"));
				item.add(lowerPS(si.getLocale().getLanguage() + "_language"));
				data.add(item.clone());
				item.removeAllElements();
			} else {
				item.add(PluginServices.getText(this, "language"));
				item.add(PluginServices.getText(this, "not_available"));
				data.add(item.clone());
				item.removeAllElements();
			}

			if (si.getMapunits().length() > 0) {
				item.add(PluginServices.getText(this, "units"));
				item.add(lowerPS(si.getMapunits()));
				data.add(item.clone());
				item.removeAllElements();
			} else {
				item.add(PluginServices.getText(this, "units"));
				item.add(PluginServices.getText(this, "not_available"));
				data.add(item.clone());
				item.removeAllElements();
			}

			if (si.getLocale().getCountry().length() > 0) {
				item.add(PluginServices.getText(this, "country"));
				item.add(lowerPS(si.getLocale().getCountry() + "_country"));
				data.add(item.clone());
				item.removeAllElements();
			} else {
				item.add(PluginServices.getText(this, "country"));
				item.add(PluginServices.getText(this, "not_available"));
				data.add(item.clone());
				item.removeAllElements();
			}

			if (si.getFeaturecoordsys().length() > 0) {
				item.add(PluginServices.getText(this, "coord_system"));
				item.add(si.getFeaturecoordsys());
				data.add(item.clone());
				item.removeAllElements();
			} else {
				item.add(PluginServices.getText(this, "coord_system"));
				item.add(PluginServices.getText(this, "not_available"));
				data.add(item.clone());
				item.removeAllElements();
			}

			if (si.getScreen_dpi() != -1) {
				item.add("DPI");
				item.add(Integer.toString(si.getScreen_dpi()));
				data.add(item.clone());
				item.removeAllElements();
			} else {
				item.add("DPI");
				item.add(PluginServices.getText(this, "default") + ", 96");
				data.add(item.clone());
				item.removeAllElements();
			}

			if (this instanceof ImageServicePanel) {
				if (si.getImagelimit_pixelcount().length() > 0) {
					item.add(PluginServices.getText(this, "max_no_of_pixels"));
					item.add(si.getImagelimit_pixelcount());
					data.add(item.clone());
					item.removeAllElements();
				} else {
					item.add(PluginServices.getText(this, "max_no_of_pixels"));
					item.add(PluginServices.getText(this, "not_available"));
					data.add(item.clone());
					item.removeAllElements();
				}
			}
		} else {
			// A null ServiceInformation parameter (we must empty the
			// table). do nothing, because 'data' and 'cols' vectors are empty
		}

		serviceDetailsTableDataVector = (Vector) data.clone();
		serviceDetailsTableColumnNamesVector = (Vector) cols.clone();
		serviceDetailsTable = null;
		serviceDetailsTable = getServiceDetailsTable();
		serviceDetailsScrollPane.setBounds(new java.awt.Rectangle(0, 0,
				470 - 14, 345 + 31));
		serviceDetailsScrollPane.setViewportView(serviceDetailsTable);
	}

	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = createResourceUrl(path);

		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			logger.error("File not found: " + path);

			return null;
		}
	}

	protected void setNextPreviousEnabled(int selectedTab) {
		int lastTabIndex = imgServiceTabbedPane.getTabCount() - 1;
		serviceInfoNextButton.setEnabled(selectedTab < lastTabIndex);
		serviceInfoPreviousButton.setEnabled((selectedTab > 0)
				|| (!isProperties));
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == imgServiceTabbedPane) {
			setNextPreviousEnabled(imgServiceTabbedPane.getSelectedIndex());
		}
	}

	public void setServiceInfoTabNumber(int i) {
		imgServiceTabbedPane.setSelectedIndex(i);
	}

	protected String getFormattedHtmlInfo(Vector data, Vector colnames) {
		String location_title_text = PluginServices
				.getText(this, "data_origin");
		String properties_title_text = PluginServices.getText(this,
				"properties");

		String server_title = PluginServices.getText(this, "server");
		String service_name_title = PluginServices.getText(this, "service");
		String service_type_title = PluginServices
				.getText(this, "service_type");

		String serverURL = parentWizard.getHostName();
		String serviceName = parentWizard.getServiceName();
		String serviceType = lowerPS(parentWizard.getServiceType());

		String html = "<html><body>\n"
				+ "<table align=\"center\" width=\"425\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\">\n"
				+ "<tr valign=\"top\" bgcolor=\"#FFFFFF\">\n"
				+ "<td height=\"18\" bgcolor=" + bgColor3
				+ " colspan=\"2\"><font face=\"" + font + "\" size=\"4\"><b>"
				+ location_title_text + "</font></b></td>\n</tr>\n";
		html = html
				+ "<tr valign=\"top\" bgcolor="
				+ bgColor0
				+ ">\n"
				+ "<td width=\"110\" height=\"18\" bgcolor=\"#D6D6D6\" align=\"right\"><font face=\""
				+ font + "\" size=\"3\"><b>" + server_title
				+ "</b></font></td>" + "<td><font face=\"" + font
				+ "\" size=\"3\">" + serverURL + "</font></td>\n</tr>\n";
		html = html
				+ "<tr valign=\"top\" bgcolor="
				+ bgColor0
				+ ">\n"
				+ "<td height=\"18\" bgcolor=\"#D6D6D6\" align=\"right\"><font face=\""
				+ font + "\" size=\"3\"><b>" + service_name_title
				+ "</b></font></td>" + "<td bgcolor=\"#eaeaea\"><font face=\""
				+ font + "\" size=\"3\">" + serviceName
				+ "</font></td>\n</tr>\n";
		html = html
				+ "<tr valign=\"top\" bgcolor="
				+ bgColor0
				+ ">\n"
				+ "<td height=\"18\" bgcolor=\"#D6D6D6\" align=\"right\"><font face=\""
				+ font + "\" size=\"3\"><b>" + service_type_title
				+ "</b></font></td>" + "<td><font face=\"" + font
				+ "\" size=\"3\">" + serviceType
				+ "</font></td>\n</tr>\n</table>\n\n";
		html = html
				+ "<table align=\"center\" width=\"425\" border=\"0\" cellpadding=\"4\" cellspacing=\"4\">\n"
				+ "<tr valign=\"top\" bgcolor=\"#FFFFFF\">\n"
				+ "<td height=\"18\" bgcolor=" + bgColor3
				+ " colspan=\"2\"><font face=\"" + font + "\" size=\"4\"><b>"
				+ properties_title_text + "</font></b></td>\n</tr>\n";

		for (int i = 0; i < data.size(); i++) {
			html = html + getTwoStringsOfNthItemIntoHtmlCode(data, i);
		}

		html = html + "</table>\n</body></html>";

		return html;
	}

	protected String getTwoStringsOfNthItemIntoHtmlCode(Vector d, int i) {
		String string_1 = (String) ((Vector) d.get(i)).get(0);
		String string_2 = (String) ((Vector) d.get(i)).get(1);
		String[] bgrColor = new String[2];
		bgrColor[0] = "\"#feedd6\"";
		bgrColor[1] = "\"#eaeaea\"";

		String response = "<tr valign=\"top\">\n"
				+ "    <td width=\"110\" height=\"18\" bgcolor=\"#d6d6d6\" align=\"right\"><font face=\""
				+ font + "\" size=\"3\"><b>" + string_1 + "</b></font></td>"
				+ "    <td bgcolor=" + bgrColor[i % 2] + "><font face=\""
				+ font + "\" size=\"3\">" + string_2 + "</font></td>"
				+ "\n</tr>\n";

		return response;
	}

	public void valueChanged(TreeSelectionEvent e) {
		if (e.getSource() == availableLayersTree) {
			availableLayersTree.removeSelectionRow(0);
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == showIdCheckBox) {
			setShowIdsToTwoLists(showIdCheckBox.isSelected());
			refreshAvailableLayersTree();
			refreshSelectedLayersList();
		}
	}

	protected void setShowIdsToTwoLists(boolean show) {
		// available layers tree:
		DefaultMutableTreeNode dmtn;
		LayersListElement lle;
		Object root = availableLayersTreeModel.getRoot();
		int nofchildren = availableLayersTreeModel.getChildCount(root);

		for (int i = 0; i < nofchildren; i++) {
			dmtn = (DefaultMutableTreeNode) availableLayersTreeModel.getChild(
					root, i);
			lle = (LayersListElement) dmtn.getUserObject();
			lle.setShowIds(show);
		}

		availableLayersTree.repaint();

		// selected layers list:
		for (int i = 0; i < orderedLayersListModel.size(); i++) {
			lle = (LayersListElement) orderedLayersListModel.get(i);
			lle.setShowIds(show);
		}

		orderedLayersList.repaint();
	}

	public void refreshAvailableLayersTree() {
		setAvailableLayersTree(null);
		availableLayersTree = getAvailableLayersTree();
		availableLayersScrollPane.setViewportView(availableLayersTree);
	}

	public void refreshSelectedLayersList() {
		setOrderedLayersList(null);
		orderedLayersList = getOrderedLayersList();
		selectedLayersScrollPane.setViewportView(orderedLayersList);
	}

	public JButton getChangeServerButton() {
		return changeServerButton;
	}

	public void mouseClicked(MouseEvent e) {
		if ((e.getSource() == this.availableLayersTree)
				&& (e.getClickCount() == 2)) {
			ActionEvent artificialEvent = new ActionEvent(this.addLayerButton,
					ActionEvent.ACTION_PERFORMED, "");
			this.actionPerformed(artificialEvent);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public JPanel getImgServiceTab_2() {
		return imgServiceTab_2;
	}

	public JButton getServiceInfoNextButton() {
		return serviceInfoNextButton;
	}

	public JPanel getChangeServerPanel() {
		return changeServerPanel;
	}

	private String lowerPS(String str) {
		return PluginServices.getText(this, str.toLowerCase());
	}

	protected java.net.URL createResourceUrl(String path) {
		return getClass().getClassLoader().getResource(path);
	}

	public boolean isGroupOptionSelected() {
		return groupCheckBox.isSelected();
	}

	public void setTextInVersionLabel(String vers) {
		versionLabel.setText(vers);
	}
} // @jve:decl-index=0:visual-constraint="10,10"
