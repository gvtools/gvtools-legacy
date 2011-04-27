package com.iver.cit.gvsig.project.documents.view.tool.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.cit.gvsig.project.documents.view.gui.FontOptions;

/**
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es> 01/11/2008
 *
 */
public class TextPropertiesPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private JLabel jLabel1 = null;
	private JComboBox cmbTypeFont = null;
	private JLabel jLabel2 = null;
	private JComboBox cmbStyleFont = null;
	private JLabel jLabel3 = null;
	private JTextField txtHeight = null;
	private JLabel jLabel4 = null;
	private ColorChooserPanel colorPanel = null;
	private JLabel jLabel5 = null;
	private JTextField txtRotate = null;

	/**
	 * This is the default constructor
	 */
	public TextPropertiesPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new GridBagLayout());
		Insets rightInsets = new Insets(4,4,4,0);
		Insets leftInsets = new Insets(4,0,4,4);
		GridBagConstraints txtRotationConstraints = new GridBagConstraints();
		txtRotationConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		txtRotationConstraints.gridy = 5;
		txtRotationConstraints.weightx = 1.0;
		txtRotationConstraints.gridx = 1;
		txtRotationConstraints.insets = rightInsets;
		
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.gridy = 5;
		gridBagConstraints10.insets = leftInsets;
		gridBagConstraints10.anchor = GridBagConstraints.WEST;
		
		jLabel5 = new JLabel();
		jLabel5.setText(PluginServices.getText(this,"fontrotate"));
		
		GridBagConstraints colorChooserConstraints = new GridBagConstraints();
		colorChooserConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		colorChooserConstraints.anchor = GridBagConstraints.WEST;
		colorChooserConstraints.gridy = 4;
		colorChooserConstraints.weightx = 1.0;
		colorChooserConstraints.gridx = 1;
		colorChooserConstraints.insets = rightInsets;
		
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.gridy = 4;
		gridBagConstraints8.anchor = GridBagConstraints.WEST;
		gridBagConstraints8.insets = leftInsets;
		
		jLabel4 = new JLabel();
		jLabel4.setText(PluginServices.getText(this,"fontcolor"));
		
		GridBagConstraints txtHeightConstraints = new GridBagConstraints();
		txtHeightConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		txtHeightConstraints.gridy = 3;
		txtHeightConstraints.weightx = 1.0;
		txtHeightConstraints.gridx = 1;
		txtHeightConstraints.insets = rightInsets;
		
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.gridy = 3;
		gridBagConstraints6.anchor = GridBagConstraints.WEST;
		gridBagConstraints6.insets = leftInsets;
		
		jLabel3 = new JLabel();
		jLabel3.setText(PluginServices.getText(this,"fontheight"));
		
		GridBagConstraints fontStyleConstraints = new GridBagConstraints();
		fontStyleConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		fontStyleConstraints.gridy = 2;
		fontStyleConstraints.weightx = 1.0;
		fontStyleConstraints.gridx = 1;
		fontStyleConstraints.insets = rightInsets;
		
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 2;
		gridBagConstraints4.anchor = GridBagConstraints.WEST;
		gridBagConstraints4.insets = leftInsets;
		
		
		jLabel2 = new JLabel();
		jLabel2.setText(PluginServices.getText(this,"fontstyle"));
		
		GridBagConstraints fontTypeConstraints = new GridBagConstraints();
		fontTypeConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		fontTypeConstraints.gridy = 1;
		fontTypeConstraints.weightx = 1.0;
		fontTypeConstraints.gridx = 1;
		fontTypeConstraints.insets = rightInsets;
		
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.insets = leftInsets;
		
		jLabel1 = new JLabel();
		jLabel1.setText(PluginServices.getText(this,"fonttype"));
		jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.TRAILING);
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
		
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.insets = rightInsets;
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = leftInsets;
		
		this.add(jLabel1, gridBagConstraints2);
		this.add(getCmbFontType(), fontTypeConstraints);
		this.add(jLabel2, gridBagConstraints4);
		this.add(getCmbFontStyle(), fontStyleConstraints);
		this.add(jLabel3, gridBagConstraints6);
		this.add(getTxtHeightField(), txtHeightConstraints);
		this.add(jLabel4, gridBagConstraints8);
		this.add(getColorChooser(), colorChooserConstraints);
		this.add(jLabel5, gridBagConstraints10);
		this.add(getTxtRotationField(), txtRotationConstraints);		
	}

	/**
	 * This method initializes txtTypeFont
	 *
	 * @return javax.swing.JTextField
	 */
	private JComboBox getCmbFontType() {
		if (cmbTypeFont == null) {
			cmbTypeFont = new JComboBox();
			String[] types= FontOptions.getFontTypes();
			for (int i=0;i<types.length;i++){
				cmbTypeFont.addItem(types[i]);
			}
			cmbTypeFont.setSelectedItem(FontOptions.ARIAL);

		}
		return cmbTypeFont;
	}

	/**
	 * This method initializes txtStyleFont
	 *
	 * @return javax.swing.JTextField
	 */
	private JComboBox getCmbFontStyle() {
		if (cmbStyleFont == null) {
			cmbStyleFont = new JComboBox();
			String [] styles=FontOptions.getFontStyles();
			for (int i =0;i<styles.length;i++){
				cmbStyleFont.addItem(styles[i]);
			}
			cmbStyleFont.setSelectedItem(FontOptions.PLAIN);
		}
		return cmbStyleFont;
	}

	/**
	 * This method initializes txtHeight
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtHeightField() {
		if (txtHeight == null) {
			txtHeight = new JTextField();
			txtHeight.setText("10");
		}
		return txtHeight;
	}

	/**
	 * This method initializes txtColor
	 *
	 * @return javax.swing.JTextField
	 */
	private ColorChooserPanel getColorChooser() {
		if (colorPanel == null) {
			colorPanel = new ColorChooserPanel();
			colorPanel.setColor(Color.BLACK);
		}
		return colorPanel;
	}

	/**
	 * This method initializes txtRotate
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtRotationField() {
		if (txtRotate == null) {
			txtRotate = new JTextField();
			txtRotate.setText("0.0");
		}
		return txtRotate;
	}

	public String getFontType() {
		return getCmbFontType().getSelectedItem().toString();
	}

	public void setFontType(String typeFont) {
		getCmbFontType().setSelectedItem(typeFont);
	}

	/**
	 * @return {@link java.awt.Font#PLAIN}, {@link java.awt.Font#BOLD}
	 * or {@link java.awt.Font#ITALIC}
	 */
	public int getFontStyle() {
		return getCmbFontStyle().getSelectedIndex();
	}

	/**
	 * One of {@link java.awt.Font#PLAIN}, {@link java.awt.Font#BOLD}
	 * or {@link java.awt.Font#ITALIC}
	 * @param style
	 */
	public void setFontStyle(int style) {
		getCmbFontStyle().setSelectedIndex(style);
	}

	public double getTextHeight() {
		return Double.parseDouble(getTxtHeightField().getText());
	}

	public void setTextHeight(double height) {
		getTxtHeightField().setText(new Double(height).toString());
	}

	public Color getColor() {
		return getColorChooser().getColor();
	}

	public void setColor(Color color) {
		getColorChooser().setColor(color);
		getColorChooser().setAlpha(color.getAlpha());
	}

	public double getRotation() {
		return Double.parseDouble(getTxtRotationField().getText());
	}

	public void setRotation(double rotation) {
		getTxtRotationField().setText(new Double(rotation).toString());
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
