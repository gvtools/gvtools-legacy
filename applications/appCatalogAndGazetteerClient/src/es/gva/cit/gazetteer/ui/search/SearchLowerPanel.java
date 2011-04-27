
/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
package es.gva.cit.gazetteer.ui.search;
import java.awt.GridBagConstraints;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.gvsig.i18n.Messages;

import es.gva.cit.catalog.querys.Coordinates;
import es.gva.cit.catalog.ui.search.SearchAditionalPropertiesPanel;
import es.gva.cit.catalog.utils.CatalogConstants;
import es.gva.cit.catalog.utils.Doubles;
import es.gva.cit.gazetteer.querys.FeatureType;
import es.gva.cit.gazetteer.utils.thesaurusjtree.ThesaurusJTree;

/**
 * @author Jorge Piera Llodra (piera_jor@gva.es)
 */
public class SearchLowerPanel extends JPanel {
	private FeatureType[] features = null; 
	private SearchAditionalPropertiesPanel aditionalPanel = null;
	
	  private javax.swing.JRadioButton allRButton;
	    private javax.swing.JRadioButton anyRButton;
	    private javax.swing.JPanel aspectPanel;
	    private javax.swing.JLabel brxLabel;
	    private javax.swing.JTextField brxText;
	    private javax.swing.JLabel bryLabel;
	    private javax.swing.JTextField bryText;
	    private javax.swing.JPanel coordinatesPanel;
	    private javax.swing.JComboBox coordinatesRelationshipCombo;
	    private javax.swing.JPanel coordinatesRelationshipPanel;
	    private javax.swing.JCheckBox deleteCheck;
	    private javax.swing.JRadioButton exactRButton;
	    private javax.swing.JCheckBox insSearchCheck;
	    private javax.swing.JPanel intSearchPanel;
	    private javax.swing.JPanel leftPanel;
	    private javax.swing.JPanel lowerCoordinatesPanel;
	    private javax.swing.JComboBox nResultsCombo;
	    private javax.swing.JLabel nResultsLabel;
	    private javax.swing.JPanel nResultsPanel;
	    private javax.swing.JPanel nameOptionsPanel;
	    private javax.swing.JCheckBox paintCheck;
	    private javax.swing.JPanel rigthPanel;
	    private javax.swing.JPanel rigthUpperPanel;
	    private javax.swing.JScrollPane scroll;
	    private javax.swing.JLabel typeLabel;
	    private javax.swing.JPanel typePanel;
	    private ThesaurusJTree typeTree;
	    private javax.swing.JLabel ulxLabel;
	    private javax.swing.JTextField ulxText;
	    private javax.swing.JLabel ulyLabel;
	    private javax.swing.JTextField ulyText;
	    private javax.swing.JPanel upperCoordinatesPanel;
	    private javax.swing.JCheckBox zoomCheck;

	/** Creates new form searchLowerPanel */
	public SearchLowerPanel(FeatureType[] features, SearchAditionalPropertiesPanel aditionalPanel) {
		this.features = features;
		initComponents();
		initLabels();
		initDefaultValues();
		addOptionalPanel(aditionalPanel);
	}
	
	/**
	 * It adds the aditional panel
	 * @param aditionalPanel
	 */
	private void addOptionalPanel(SearchAditionalPropertiesPanel aditionalPanel){
		if (aditionalPanel != null){
			this.aditionalPanel = aditionalPanel;
			GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 3;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			rigthUpperPanel.add(aditionalPanel, gridBagConstraints);
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" C�digo Generado  ">                          
	private void initComponents() { 	       
        java.awt.GridBagConstraints gridBagConstraints;

        leftPanel = new javax.swing.JPanel();
        nameOptionsPanel = new javax.swing.JPanel();
        exactRButton = new javax.swing.JRadioButton();
        anyRButton = new javax.swing.JRadioButton();
        allRButton = new javax.swing.JRadioButton();
        typePanel = new javax.swing.JPanel();
        typeLabel = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        typeTree = new ThesaurusJTree(features,Messages.getText("rootNode"));
        nResultsPanel = new javax.swing.JPanel();
        nResultsLabel = new javax.swing.JLabel();
        nResultsCombo = new javax.swing.JComboBox();
        rigthPanel = new javax.swing.JPanel();
        rigthUpperPanel = new javax.swing.JPanel();
        intSearchPanel = new javax.swing.JPanel();
        insSearchCheck = new javax.swing.JCheckBox();
        coordinatesPanel = new javax.swing.JPanel();
        upperCoordinatesPanel = new javax.swing.JPanel();
        ulxLabel = new javax.swing.JLabel();
        ulxText = new javax.swing.JTextField();
        ulyLabel = new javax.swing.JLabel();
        ulyText = new javax.swing.JTextField();
        lowerCoordinatesPanel = new javax.swing.JPanel();
        brxLabel = new javax.swing.JLabel();
        brxText = new javax.swing.JTextField();
        bryLabel = new javax.swing.JLabel();
        bryText = new javax.swing.JTextField();
        coordinatesRelationshipPanel = new javax.swing.JPanel();
        coordinatesRelationshipCombo = new javax.swing.JComboBox();
        aspectPanel = new javax.swing.JPanel();
        zoomCheck = new javax.swing.JCheckBox();
        deleteCheck = new javax.swing.JCheckBox();
        paintCheck = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridLayout());

        setPreferredSize(new java.awt.Dimension(200, 518));
        leftPanel.setLayout(new java.awt.BorderLayout());

        leftPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
        leftPanel.setPreferredSize(new java.awt.Dimension(100, 518));
        nameOptionsPanel.setLayout(new java.awt.GridBagLayout());

        nameOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("concordancia"));
        nameOptionsPanel.setPreferredSize(new java.awt.Dimension(100, 81));
        exactRButton.setText("jRadioButton1");
        exactRButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        exactRButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        nameOptionsPanel.add(exactRButton, gridBagConstraints);

        anyRButton.setText("jRadioButton1");
        anyRButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        anyRButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        nameOptionsPanel.add(anyRButton, gridBagConstraints);

        allRButton.setText("jRadioButton2");
        allRButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        allRButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        nameOptionsPanel.add(allRButton, gridBagConstraints);

        leftPanel.add(nameOptionsPanel, java.awt.BorderLayout.NORTH);

        typePanel.setLayout(new java.awt.GridBagLayout());

        typePanel.setPreferredSize(new java.awt.Dimension(100, 386));
        typeLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        typePanel.add(typeLabel, gridBagConstraints);

        scroll.setViewportView(typeTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        typePanel.add(scroll, gridBagConstraints);

        leftPanel.add(typePanel, java.awt.BorderLayout.CENTER);

        nResultsPanel.setLayout(new java.awt.GridBagLayout());

        nResultsPanel.setPreferredSize(new java.awt.Dimension(100, 47));
        nResultsLabel.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        nResultsPanel.add(nResultsLabel, gridBagConstraints);

        nResultsCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Art\u00edculo 1", "Art\u00edculo 2", "Art\u00edculo 3", "Art\u00edculo 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        nResultsPanel.add(nResultsCombo, gridBagConstraints);

        leftPanel.add(nResultsPanel, java.awt.BorderLayout.SOUTH);

        add(leftPanel);

        rigthPanel.setLayout(new java.awt.BorderLayout());

        rigthPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 3));
        rigthPanel.setPreferredSize(new java.awt.Dimension(100, 274));
        rigthUpperPanel.setLayout(new java.awt.GridBagLayout());

        intSearchPanel.setLayout(new java.awt.GridBagLayout());

        intSearchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("inteligent"));
        intSearchPanel.setPreferredSize(new java.awt.Dimension(100, 43));
        insSearchCheck.setText("jCheckBox1");
        insSearchCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        insSearchCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        intSearchPanel.add(insSearchCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        rigthUpperPanel.add(intSearchPanel, gridBagConstraints);

        coordinatesPanel.setLayout(new java.awt.GridBagLayout());

        coordinatesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("coordinates"));
        coordinatesPanel.setPreferredSize(new java.awt.Dimension(100, 138));
        upperCoordinatesPanel.setLayout(new java.awt.GridBagLayout());

        upperCoordinatesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("upper"));
        upperCoordinatesPanel.setPreferredSize(new java.awt.Dimension(100, 43));
        ulxLabel.setText("ULX:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 0.15;
        upperCoordinatesPanel.add(ulxLabel, gridBagConstraints);

        ulxText.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        upperCoordinatesPanel.add(ulxText, gridBagConstraints);

        ulyLabel.setText("ULY:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 0.15;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        upperCoordinatesPanel.add(ulyLabel, gridBagConstraints);

        ulyText.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.35;
        upperCoordinatesPanel.add(ulyText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        coordinatesPanel.add(upperCoordinatesPanel, gridBagConstraints);

        lowerCoordinatesPanel.setLayout(new java.awt.GridBagLayout());

        lowerCoordinatesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("lower"));
        lowerCoordinatesPanel.setPreferredSize(new java.awt.Dimension(100, 43));
        brxLabel.setText("ULX:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 0.15;
        lowerCoordinatesPanel.add(brxLabel, gridBagConstraints);

        brxText.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.35;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        lowerCoordinatesPanel.add(brxText, gridBagConstraints);

        bryLabel.setText("ULY:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 0.15;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        lowerCoordinatesPanel.add(bryLabel, gridBagConstraints);

        bryText.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.35;
        lowerCoordinatesPanel.add(bryText, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        coordinatesPanel.add(lowerCoordinatesPanel, gridBagConstraints);

        coordinatesRelationshipPanel.setLayout(new java.awt.GridBagLayout());

        coordinatesRelationshipPanel.setPreferredSize(new java.awt.Dimension(100, 24));
        coordinatesRelationshipCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Art\u00edculo 1", "Art\u00edculo 2", "Art\u00edculo 3", "Art\u00edculo 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        coordinatesRelationshipPanel.add(coordinatesRelationshipCombo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 5, 2);
        coordinatesPanel.add(coordinatesRelationshipPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        rigthUpperPanel.add(coordinatesPanel, gridBagConstraints);

        aspectPanel.setLayout(new java.awt.GridBagLayout());

        aspectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("aspect"));
        aspectPanel.setPreferredSize(new java.awt.Dimension(100, 81));
        zoomCheck.setText("jCheckBox1");
        zoomCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        zoomCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        aspectPanel.add(zoomCheck, gridBagConstraints);

        deleteCheck.setText("jCheckBox1");
        deleteCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        deleteCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        aspectPanel.add(deleteCheck, gridBagConstraints);

        paintCheck.setText("jCheckBox1");
        paintCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        paintCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 0);
        aspectPanel.add(paintCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        rigthUpperPanel.add(aspectPanel, gridBagConstraints);

        rigthPanel.add(rigthUpperPanel, java.awt.BorderLayout.NORTH);

        add(rigthPanel);
	}// </editor-fold>                        

	/**
	 * Rewrite the labels
	 */
	private void initLabels(){
		nameOptionsPanel.setBorder(BorderFactory.createTitledBorder(Messages.getText("concordancia")));
		exactRButton.setText(Messages.getText("exactSentence"));
		anyRButton.setText(Messages.getText("anyWord"));
		allRButton.setText(Messages.getText("allWords"));
		typeLabel.setText(Messages.getText("resourceTypeColumn"));
		nResultsLabel.setText(Messages.getText("resutsByPage"));
		coordinatesPanel.setBorder(BorderFactory.createTitledBorder(Messages.getText("coordinates")));
		upperCoordinatesPanel.setBorder(BorderFactory.createTitledBorder(Messages.getText("upperCoordinates")));
		lowerCoordinatesPanel.setBorder(BorderFactory.createTitledBorder(Messages.getText("lowerCoordinates")));
		ulxLabel.setText(Messages.getText("ULX") + ":");
		ulyLabel.setText(Messages.getText("ULY") + ":");
		brxLabel.setText(Messages.getText("BRX") + ":");
		bryLabel.setText(Messages.getText("BRY") + ":");
		aspectPanel.setBorder(BorderFactory.createTitledBorder(Messages.getText("aspect")));
		zoomCheck.setText(Messages.getText("goto"));
		deleteCheck.setText(Messages.getText("mantainold"));
		paintCheck.setText(Messages.getText("paint"));
		intSearchPanel.setBorder(BorderFactory.createTitledBorder(Messages.getText("inteligentSearch")));
		insSearchCheck.setText(Messages.getText("inteligentSearchAscii"));
	}
	
	/**
	 * Initializae some components with their default values
	 */
	private void initDefaultValues(){
		//remove the combos
		nResultsCombo.removeAllItems();
		coordinatesRelationshipCombo.removeAllItems();
		//remove the text fields
		ulxText.setText("");
		ulyText.setText("");
		brxText.setText("");
		bryText.setText("");
		//Select the check boxes
		deleteCheck.setSelected(true);		
		insSearchCheck.setSelected(true);
		zoomCheck.setSelected(true);
		paintCheck.setSelected(true);
		//Select the radio button
		ButtonGroup group = new ButtonGroup();
		group.add(exactRButton);
		group.add(anyRButton);
		group.add(allRButton);
		exactRButton.setSelected(true);
	}

	/**
	 * @return the selected thesaurus 
	 */
	public FeatureType getType() {        
		DefaultMutableTreeNode dmt = (DefaultMutableTreeNode)typeTree.getLastSelectedPathComponent();
		if (dmt == null)
			return null;
		return (FeatureType) dmt.getUserObject();
	}
	
	/**
	 * @return all the feature types
	 */
	 public FeatureType[] getAllTypes() {        
	        DefaultMutableTreeNode root = (DefaultMutableTreeNode)typeTree.getModel().getRoot();
	        FeatureType[] featureType = new FeatureType[root.getChildCount()];        
	        for (int i=0 ; i<root.getChildCount() ; i++){
	            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
	            featureType[i] = (FeatureType) child.getUserObject();
	        }	        
	        return featureType;
	    } 
	
	/**
	 * @return the concordancia
	 */
	public String getConcordancia() {        
		if (exactRButton.isSelected()) {
			return CatalogConstants.EXACT_WORDS;
		}else if (anyRButton.isSelected()) {
			return CatalogConstants.ANY_WORD;
		}else if (allRButton.isSelected()) {
			return CatalogConstants.ALL_WORDS;
		}
		return CatalogConstants.ANY_WORD;
	}

	/**
	 * @return the number of results by page
	 */
	public int getResultsByPage() {        
		return ((Integer)nResultsCombo.getSelectedItem()).intValue();		
	} 

	/**
	 * @return the coordinates
	 */
	public Coordinates getCoordinates() {        
		return new Coordinates(ulxText.getText(), ulyText.getText(),
				brxText.getText(), bryText.getText());
	} 
	
	/**
	 * Set the coordinates
	 * @param coordinates
	 * The coordinates to set
	 */
    public void setCoordinates(Coordinates coordinates) {        
        ulxText.setText(Doubles.get5Decimals(coordinates.ulx));
        ulyText.setText(Doubles.get5Decimals(coordinates.uly));
        brxText.setText(Doubles.get5Decimals(coordinates.brx));
        bryText.setText(Doubles.get5Decimals(coordinates.bry));
    } 

	/**
	 * @return the coordinates relationship
	 */
	public String getCoordinatesOption() {        
		return (String) coordinatesRelationshipCombo.getSelectedItem();
	} 

	/**
	 * @return if the GOTO component is clicked
	 */
	public boolean isGoToClicked() {        
		return zoomCheck.isSelected();
	} 

	/**
	 * @return if the Mantain Old button is enabled
	 */
	public boolean isKeepOldClicked() {        
		return deleteCheck.isSelected();
	}

	/**
	 * @return if the Mantain Old button is enabled
	 */
	public boolean isMarkedPlaceClicked() {        
		return paintCheck.isSelected();
	}


	/**
	 * @return if the With accents button is enabled
	 */
	public boolean isAccentsSearchEnabled() {        
		return insSearchCheck.isSelected();
	}

	/**
	 * Add a new number of resultas by page to the
	 * combo
	 * @param number
	 * Results by page
	 */
	public void addResultsByPageNumber(int number){
		nResultsCombo.addItem(new Integer(number));
	}

	/**
	 * Add a new coordinates option for the coordinates
	 * combo
	 * @param option
	 * Coordinates option
	 */
	public void addCoordinatesRelationship(String option){
		coordinatesRelationshipCombo.addItem(option);
	}

	/**
	 * Returns the properties for teh aditional panel
	 * @return
	 */
	public Properties getProperties() {
		if (aditionalPanel != null){
			return aditionalPanel.getProperties();
		}
		return null;
	}
}

