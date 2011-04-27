/*
 * Created on 01-jun-2004
 *
 */
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.project.documents.view.legend.gui;

import javax.swing.JCheckBox;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerEvent;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.Annotation_Legend;
import com.iver.cit.gvsig.gui.JComboBoxUnits;
import com.iver.cit.gvsig.project.Project;

/**
 * Panel legend annotation properties.
 *
 * @author Vicente Caballero Navarro
 */
public class PanelLegendAnnotation extends AbstractThemeManagerPage {

	private Annotation_Legend m_Renderer;

	private Annotation_Layer m_lyr;

	private ITextSymbol symbol;

//	private javax.swing.JRadioButton jRadioButton = null;
//
//	private javax.swing.JRadioButton jRadioButton1 = null;

	private JCheckBox jCheckBoxTextOnly = null;

	private JCheckBox chkDelOverLapping = null;

	private JCheckBox chkAvoidOverLapping = null;

	private JComboBoxUnits cmbUnits;

	/**
	 * This is the default constructor
	 */
	public PanelLegendAnnotation() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setSize(454, 308);
		this.add(getJCheckBoxTextOnly(), null);
//		ButtonGroup jButtonGroup = new ButtonGroup();
//		jButtonGroup.add(getJRadioButton1());
//		jButtonGroup.add(getJRadioButton());

		this.add(getCmbUnits(),null);
//		this.add(getJRadioButton1(), null);
//		this.add(getJRadioButton(), null);
		this.add(getChkDelOverLapping(), null);
		this.add(getChkAvoidOverLapping(), null);
	}

//	public void setLayer(FLayer lyr, ILegend r) {
//		m_lyr = (Annotation_Layer) lyr;
//		m_Renderer = (Annotation_Legend) r;
//		getChkAvoidOverLapping().setSelected(((Annotation_Legend)r).isAvoidOverLapping());
//		getChkDelOverLapping().setSelected(((Annotation_Legend)r).isDelOverLapping());
//		if (r.getDefaultSymbol() instanceof FSymbol)
//			setFSymbol((FSymbol) r.getDefaultSymbol());
//		m_lyr.addLayerListener(this);
//		if (m_lyr.isEditing()){
//			chkAvoidOverLapping.setVisible(false);
//			chkDelOverLapping.setVisible(false);
//		}else{
//			chkAvoidOverLapping.setVisible(true);
//			chkDelOverLapping.setVisible(true);
//		}
//	}

	public void setFSymbol(ISymbol sym) {
		if (sym == null) {
			symbol = SymbologyFactory.createDefaultTextSymbol();
		} else
			symbol = (ITextSymbol)sym;


	}
	public JComboBoxUnits getCmbUnits() {
		if (cmbUnits == null) {
			cmbUnits = new JComboBoxUnits();
			cmbUnits.setSelectedIndex(Project.getDefaultDistanceUnits());
			cmbUnits.setName("CMBUNITS");
			cmbUnits.setBounds(new java.awt.Rectangle(27,50,303,23));
		}

		return cmbUnits;
	}
	/**
	 * This method initializes jRadioButton
	 *
	 * @return javax.swing.JRadioButton
	 */
//	private javax.swing.JRadioButton getJRadioButton() {
//		if (jRadioButton == null) {
//			jRadioButton = new javax.swing.JRadioButton();
//			jRadioButton.setText(PluginServices.getText(this, "En_pixels"));
//			jRadioButton.setBounds(new java.awt.Rectangle(27,50,303,23));
//			jRadioButton.setSelected(true);
//		}
//		return jRadioButton;
//	}

	/**
	 * This method initializes jRadioButton1
	 *
	 * @return javax.swing.JRadioButton
	 */
//	private javax.swing.JRadioButton getJRadioButton1() {
//		if (jRadioButton1 == null) {
//			jRadioButton1 = new javax.swing.JRadioButton();
//			jRadioButton1.setText(PluginServices.getText(this, "En_metros"));
//			jRadioButton1.setBounds(new java.awt.Rectangle(27,21,303,23));
//		}
//		return jRadioButton1;
//	}
//
//	public ILegend getLegend() {
//		return m_Renderer;
//	}

	/**
	 * @param renderer
	 *            The renderer to set.
	 */
//	public void setRenderer(ILegend renderer) {
//		this.m_Renderer = (IVectorialLegend) renderer;
//		getChkDelOverLapping().setSelected(((Annotation_Legend)renderer).isDelOverLapping());
//		getChkAvoidOverLapping().setSelected(((Annotation_Legend)renderer).isAvoidOverLapping());
//		if (m_Renderer.getDefaultSymbol() instanceof FSymbol)
//			setFSymbol((FSymbol) m_Renderer.getDefaultSymbol());
//	}

	/**
	 * This method initializes jCheckBox1
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getJCheckBoxTextOnly() {
		if (jCheckBoxTextOnly == null) {
			jCheckBoxTextOnly = new JCheckBox();
			jCheckBoxTextOnly.setBounds(27, 88, 303, 23);
			jCheckBoxTextOnly.setText(PluginServices.getText(this,
					"draw_text_only"));
		}
		return jCheckBoxTextOnly;
	}

//	public void updateValuesFromControls(IVectorialLegend l) {
//		m_FSymbol.setFontSizeInPixels(jRadioButton.isSelected());
//		m_FSymbol.setShapeVisible(!jCheckBoxTextOnly.isSelected());
//		((Annotation_Legend)l).setAvoidOverLapping(chkAvoidOverLapping.isSelected());
//		((Annotation_Legend)l).setDelOverLapping(chkDelOverLapping.isSelected());
//		l.setDefaultSymbol(m_FSymbol);
//	}

	public void repaint() {
		super.repaint();
	}

//	public void actualizar() {
//		getJRadioButton().setSelected(m_FSymbol.isFontSizeInPixels());
//		getJCheckBoxTextOnly().setSelected(!m_FSymbol.isShapeVisible());
//	}

	/**
	 * This method initializes chkOverWrite
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getChkDelOverLapping() {
		if (chkDelOverLapping == null) {
			chkDelOverLapping = new JCheckBox();
			chkDelOverLapping.setBounds(new java.awt.Rectangle(27,147,303,23));
			chkDelOverLapping.setText(PluginServices.getText(this,"remove_annotation_overlapping"));
			chkDelOverLapping.setSelected(false);
		}
		return chkDelOverLapping;
	}

	/**
	 * This method initializes chkTryNotOverWrite
	 *
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getChkAvoidOverLapping() {
		if (chkAvoidOverLapping == null) {
			chkAvoidOverLapping = new JCheckBox();
			chkAvoidOverLapping.setBounds(new java.awt.Rectangle(27,116,303,23));
			chkAvoidOverLapping.setText(PluginServices.getText(this,"avoid_overlapping"));

		}
		return chkAvoidOverLapping;
	}

	public void visibilityChanged(LayerEvent e) {
		// TODO Auto-generated method stub

	}

	public void activationChanged(LayerEvent e) {
		// TODO Auto-generated method stub

	}

	public void nameChanged(LayerEvent e) {
		// TODO Auto-generated method stub

	}

	public void editionChanged(LayerEvent e) {
		if (m_lyr ==null)
			return;
		if (m_lyr.isEditing()){
			chkAvoidOverLapping.setVisible(false);
			chkDelOverLapping.setVisible(false);
		}else{
			chkAvoidOverLapping.setVisible(true);
			chkDelOverLapping.setVisible(true);
		}

	}

	public String getName() {
		return PluginServices.getText(this,"annotation");
	}

	public void acceptAction() {
		// TODO Auto-generated method stub

	}

	public void cancelAction() {
		// TODO Auto-generated method stub

	}

	public void applyAction() {
//		AttrInTableLabelingStrategy labeling = new AttrInTableLabelingStrategy();
//    	labeling.setUnit(getCmbUnits().getSelectedUnitIndex());
//    	m_lyr.setLabelingStrategy(labeling);
//		m_Renderer.setFontInPixels(jRadioButton.isSelected());
		m_Renderer.setPointVisible(!jCheckBoxTextOnly.isSelected());
		m_Renderer.setAvoidOverLapping(chkAvoidOverLapping.isSelected());
		m_Renderer.setDelOverLapping(chkDelOverLapping.isSelected());
		m_Renderer.setDefaultSymbol(symbol);
		m_Renderer.setUnits(getCmbUnits().getSelectedUnitIndex());
		try {
			m_lyr.setLegend(m_Renderer);
		} catch (LegendLayerException e) {
			e.printStackTrace();
		}
	}

	public void setModel(FLayer layer) {
		m_lyr = (Annotation_Layer) layer;
		try {
			m_Renderer = (Annotation_Legend) m_lyr.getLegend().cloneLegend();
		} catch (XMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getChkAvoidOverLapping().setSelected(m_Renderer.isAvoidOverLapping());
		getChkDelOverLapping().setSelected(m_Renderer.isDelOverLapping());
		setFSymbol(m_Renderer.getDefaultSymbol());
		getJCheckBoxTextOnly().setSelected(!m_Renderer.isPointVisible());
		getCmbUnits().setSelectedUnitIndex(m_Renderer.getUnits());
//		if (m_lyr.getLabelingStrategy()!=null){
//			getCmbUnits().setSelectedUnitIndex(((AttrInTableLabelingStrategy)m_lyr.getLabelingStrategy()).getUnit());
//		}else{
//			getCmbUnits().setSelectedUnitIndex(-1);
//		}

//		getJRadioButton1().setSelected(!m_Renderer.isFontSizeInPixels());
//		m_lyr.addLayerListener(this);
		if (m_lyr.isEditing()){
			chkAvoidOverLapping.setVisible(false);
			chkDelOverLapping.setVisible(false);
		}else{
			chkAvoidOverLapping.setVisible(true);
			chkDelOverLapping.setVisible(true);
		}
	}
}