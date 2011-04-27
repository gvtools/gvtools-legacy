package org.gvsig.layerLoadingOrder;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gvsig.gui.beans.numberTextField.NumberTextField;
import org.gvsig.gui.beans.numberTextField.PositiveNumberField;
import org.gvsig.gui.beans.simplecombobox.SimpleComboBox;

import com.iver.andami.PluginServices;
import com.iver.andami.preferences.AbstractPreferencePage;
import com.iver.andami.preferences.StoreException;
import com.iver.cit.gvsig.gui.preferencespage.LayerOrderPage;

public class SmartOrderPage extends AbstractPreferencePage implements ItemListener {
	protected JLabel jlb_vector = null;
	protected SimpleComboBox jcb_vector = null;
	protected PositiveNumberField jtf_vector = null;
	
	protected JLabel jlb_raster = null;
	protected SimpleComboBox jcb_raster = null;
	protected PositiveNumberField jtf_raster = null;
	
	protected JLabel jlb_other = null;
	protected SimpleComboBox jcb_other = null;
	protected PositiveNumberField jtf_other = null;

	protected boolean initialized = false;
	protected boolean changed = false;

	private MyValueChangeListener changeListener;

	protected String[] jcb_vectorValues;

	private ImageIcon icon = null;

	public SmartOrderPage() {
		super();
		setParentID(LayerOrderPage.class.getName());
	}

	public String getID() {
		return this.getClass().getName();
	}

	public ImageIcon getIcon() {
		if (icon==null) {
			icon = PluginServices.getIconTheme().get("smart-order-manager");
		}
		return icon;
	}

	public JPanel getPanel() {
		initializeValues();
		return this;
	}

	public String getTitle() {
		return PluginServices.getText(this, "SmartOrderManager");
	}

	public void setChangesApplied() {
		changed = false;
	}

	public void storeValues() throws StoreException {
		OrderConfig config = getConfig();
		config.setVectorBehaviour(getVectorCombo().getSelectedCode());
		config.setVectorPosition(getVectorPosField().getIntValue());
		config.setRasterBehaviour(getRasterCombo().getSelectedCode());
		config.setRasterPosition(getRasterPosField().getIntValue());
		config.setOtherLayersBehaviour(getOtherCombo().getSelectedCode());
		config.setOtherLayersPosition(getOtherPosField().getIntValue());
	}

	public void initializeDefaults() {
		getVectorCombo().setSelectedCode(OrderConfig.ON_TOP);
		getRasterCombo().setSelectedCode(OrderConfig.ON_TOP);
		getOtherCombo().setSelectedCode(OrderConfig.ON_TOP);
		changed = true;
	}

	public void initializeValues() {
		if (!initialized) {
			initialized=true;
			createUI();
			initializeUI();
			changed = false;
		}
	}

	public boolean isValueChanged() {
		return changed;
	}

	private void createUI() {
		changeListener = new MyValueChangeListener();
		Insets insets = new Insets(8, 8, 8, 8);
		addComponent(getVectorLabel(), getVectorCombo(), getVectorPosField(), GridBagConstraints.NONE, insets);
		getVectorPosField().addKeyListener(changeListener);
		addComponent(getRasterLabel(), getRasterCombo(), getRasterPosField(), GridBagConstraints.NONE, insets);
		getRasterPosField().addKeyListener(changeListener);
		addComponent(getOtherLabel(), getOtherCombo(), getOtherPosField(), GridBagConstraints.NONE, insets);
		getOtherPosField().addKeyListener(changeListener);
	}

	private void initializeUI() {
		OrderConfig config = getConfig();
		
		getVectorCombo().setSelectedCode(config.getVectorBehaviour());
		if (config.getVectorBehaviour()==OrderConfig.FROM_TOP || 
				config.getVectorBehaviour()==OrderConfig.FROM_BOTTOM) {
			getVectorPosField().setValue(config.getVectorPosition());
		}			
		getRasterCombo().setSelectedCode(config.getRasterBehaviour());
		if (config.getRasterBehaviour()==OrderConfig.FROM_TOP || 
				config.getVectorBehaviour()==OrderConfig.FROM_BOTTOM) {
			getRasterPosField().setValue(config.getRasterPosition());
		}
		getOtherCombo().setSelectedCode(config.getOtherLayersBehaviour());
		if (config.getVectorBehaviour()==OrderConfig.FROM_TOP || 
				config.getVectorBehaviour()==OrderConfig.FROM_BOTTOM) {
			getOtherPosField().setValue(config.getOtherLayersPosition());
		}
	}

	protected JLabel getVectorLabel() {
		if (jlb_vector==null) {
			jlb_vector = new JLabel(PluginServices.getText(this, "Vector_Layers_"));
		}
		return jlb_vector;
	}

	protected SimpleComboBox getVectorCombo() {
		if (jcb_vector==null) {
			jcb_vector = new SimpleComboBox();
			jcb_vector.addItem(
					PluginServices.getText(this, "On_the_top"), OrderConfig.ON_TOP);
			jcb_vector.addItem(
					PluginServices.getText(this, "At_the_bottom"), OrderConfig.AT_THE_BOTTOM);
			jcb_vector.addItem(
					PluginServices.getText(this, "Over_Raster_Layers"), OrderConfig.OVER_RASTER);
			jcb_vector.addItem(
					PluginServices.getText(this, "Under_Raster_Layers"), OrderConfig.UNDER_RASTER);
			jcb_vector.addItem(
					PluginServices.getText(this, "Fixed_position_counting_from_top"), OrderConfig.FROM_TOP);
			jcb_vector.addItem(
					PluginServices.getText(this, "Fixed_position_counting_from_bottom"), OrderConfig.FROM_BOTTOM);
			jcb_vector.addItemListener(this);
		}
		return jcb_vector;
	}

	protected NumberTextField getVectorPosField() {
		if (jtf_vector==null) {
			jtf_vector = new PositiveNumberField(7);
			jtf_vector.setEnabled(false);
		}
		return jtf_vector;
	}

	protected JLabel getRasterLabel() {
		if (jlb_raster==null) {
			jlb_raster = new JLabel(PluginServices.getText(this, "Raster_Layers_"));
		}
		return jlb_raster;
	}

	protected SimpleComboBox getRasterCombo() {
		if (jcb_raster==null) {
			jcb_raster = new SimpleComboBox();
			jcb_raster.addItem(
					PluginServices.getText(this, "On_the_top"), OrderConfig.ON_TOP);
			jcb_raster.addItem(
					PluginServices.getText(this, "At_the_bottom"), OrderConfig.AT_THE_BOTTOM);
			jcb_raster.addItem(
					PluginServices.getText(this, "Over_Vector_Layers"), OrderConfig.OVER_VECTOR);
			jcb_raster.addItem(
					PluginServices.getText(this, "Under_Vector_Layers"), OrderConfig.UNDER_VECTOR);
			jcb_raster.addItem(
					PluginServices.getText(this, "Fixed_position_counting_from_top"), OrderConfig.FROM_TOP);
			jcb_raster.addItem(
					PluginServices.getText(this, "Fixed_position_counting_from_bottom"), OrderConfig.FROM_BOTTOM);
			jcb_raster.addItemListener(this);
		}
		return jcb_raster;
	}

	protected NumberTextField getRasterPosField() {
		if (jtf_raster==null) {
			jtf_raster = new PositiveNumberField(7);
			jtf_raster.setEnabled(false);
		}
		return jtf_raster;
	}

	protected JLabel getOtherLabel() {
		if (jlb_other==null) {
			jlb_other = new JLabel(PluginServices.getText(this, "Other_Layers_"));
		}
		return jlb_other;
	}

	protected SimpleComboBox getOtherCombo() {
		if (jcb_other==null) {
			jcb_other = new SimpleComboBox();
			jcb_other.addItem(
					PluginServices.getText(this, "On_the_top"), OrderConfig.ON_TOP);
			jcb_other.addItem(
					PluginServices.getText(this, "At_the_bottom"), OrderConfig.AT_THE_BOTTOM);
			jcb_other.addItem(
					PluginServices.getText(this, "Fixed_position_counting_from_top"), OrderConfig.FROM_TOP);
			jcb_other.addItem(
					PluginServices.getText(this, "Fixed_position_counting_from_bottom"), OrderConfig.FROM_BOTTOM);
			jcb_other.addItemListener(this);
		}
		return jcb_other;
	}

	protected NumberTextField getOtherPosField() {
		if (jtf_other==null) {
			jtf_other = new PositiveNumberField(7);
			jtf_other.setEnabled(false);
		}
		return jtf_other;
	}

	/**
	 * <p>Gets the global config for the SmartOrderManager.</p>
	 * 
	 * <p>Config is saved in the extension, not in the preferences page.
	 * Therefore, the preferences page just shows the information contained
	 * in the extension and saves it again in the extension.</p>
	 * 
	 * @return
	 */
	private OrderConfig getConfig() {
		SmartOrderExtension ext = (SmartOrderExtension) PluginServices.getExtension(SmartOrderExtension.class);
		if (ext!=null) {
			return ext.getConfig();
		}
		return null;
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource()==getVectorCombo()) {
			getVectorPosField().setEnabled(getVectorCombo().getSelectedIndex()>3);
		}
		else if (e.getSource()==getRasterCombo()) {
			getRasterPosField().setEnabled(getRasterCombo().getSelectedIndex()>3);
		}
		else if (e.getSource()==getOtherCombo()) {
			getOtherPosField().setEnabled(getOtherCombo().getSelectedIndex()>1);
		}
		changed = true;
	}

	private class MyValueChangeListener implements KeyListener, MouseListener {
		public void keyPressed(KeyEvent e)      { changed = true; }
		public void keyReleased(KeyEvent e)     { changed = true; }
		public void keyTyped(KeyEvent e)        { changed = true; }
		public void mouseClicked(MouseEvent e)  { changed = true; }
		public void mouseEntered(MouseEvent e)  { changed = true; }
		public void mouseExited(MouseEvent e)   { changed = true; }
		public void mousePressed(MouseEvent e)  { changed = true; }
		public void mouseReleased(MouseEvent e) { changed = true; }
	}

}
