/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
* $Id: NetPage1.java 31189 2009-10-07 07:19:44Z fpenarrubia $
* $Log$
* Revision 1.5  2006-12-04 17:13:39  fjp
* *** empty log message ***
*
* Revision 1.4  2006/11/20 08:44:55  fjp
* borrar tramos amarillos, seleccionar solo campos numéricos y situar las barreras encima del tramo invalidado
*
* Revision 1.3  2006/11/09 10:12:24  fjp
* Traducciones
*
* Revision 1.2  2006/10/24 08:04:41  jaume
* *** empty log message ***
*
* Revision 1.1  2006/10/19 15:12:10  jaume
* *** empty log message ***
*
*
*/
package org.gvsig.graph.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import jwizardcomponent.JWizardPanel;

import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JIncrementalNumberField;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
/**
 * Configures the length
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
class NetPage1 extends JWizardPanel implements ActionListener {
	private NetWizard owner;
	private JComboBox cmbLengthField;
	// TODO comprobar si  es necesario
	private Hashtable exceptions;
	private JComboBox cmbTypeField;
	private JComboBox cmbSenseField;
	private JIncrementalNumberField txtUnitFactor;
	private JComboBox cmbCostField;

	private Object useLineLengthItem = "< "+PluginServices.getText(this, "use_line_length")+" >";

	private Object nullValue = "- "+PluginServices.getText(this, "none")+" -";
	private JCheckBox chkTypeField;
	private JCheckBox chkLengthField;
	private JCheckBox chkCostUnits;
	private JCheckBox chkSenseField;
	private JCheckBox chkCostField;
	private JTextField txtDigitizedDir;
	private JTextField txtReverseDigitizedDir;
	private JTextField txtFile;

	NetPage1(NetWizard wizard) {
		super(wizard.getWizardComponents());
		this.owner = wizard;
		initialize();
	}

	private void initialize() {
		GridBagLayoutPanel contentPane = new GridBagLayoutPanel();
		contentPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "field_configuration"),
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

		contentPane.setPreferredSize(new Dimension(520, 300));
		// Way type
		cmbTypeField = new JComboBox();
		cmbTypeField.addItem(nullValue);
		
		cmbTypeField.setToolTipText(PluginServices.getText(this, "type_field_text"));
		String[] ss = owner.getNumericLayerFieldNames();
		for (int i = 0; i < ss.length; i++) {
			cmbTypeField.addItem(ss[i]);
		}
		cmbTypeField.addActionListener(this);
		
		chkTypeField = new JCheckBox(PluginServices.getText(this, "select_type_field")+":");
		chkTypeField.addActionListener(this);
		contentPane.addComponent(chkTypeField, cmbTypeField);

		// Length
		cmbLengthField = new JComboBox();
		cmbLengthField.addItem(nullValue);
		cmbLengthField.setToolTipText(PluginServices.getText(this, "length_field_text"));
		ss = owner.getNumericLayerFieldNames();
		for (int i = 0; i < ss.length; i++) {
			cmbLengthField.addItem(ss[i]);
		}
		cmbLengthField.addActionListener(this);
		chkLengthField = new JCheckBox(PluginServices.getText(this, "select_length_field")+":");
		chkLengthField.addActionListener(this);
		contentPane.addComponent(chkLengthField, cmbLengthField);

		// Cost
		chkCostField = new JCheckBox(PluginServices.getText(this, "cost_field")+":");
		chkCostField.addActionListener(this);
		contentPane.addComponent(chkCostField, cmbCostField = new JComboBox());

		cmbCostField.addItem(useLineLengthItem );
		cmbCostField.setToolTipText(PluginServices.getText(this, "cost_field_text"));
		cmbCostField.addActionListener(this);
		String[] numericFields = owner.getNumericLayerFieldNames();

		for (int i = 0; i < numericFields.length; i++)
			cmbCostField.addItem(numericFields[i]);

		// Costs units
		txtUnitFactor = new  JIncrementalNumberField("1");
		chkCostUnits = new JCheckBox(PluginServices.getText(this, "unit_factor")+":");
		chkCostUnits.addActionListener(this);
		contentPane.addComponent(chkCostUnits, txtUnitFactor);
		txtUnitFactor.setToolTipText(PluginServices.getText(this, "unit_factor_text"));

		// Sense
		cmbSenseField = new JComboBox();
		cmbSenseField.addItem(nullValue);
		String[] fieldNames = owner.getLayerFieldNames();
		for (int i = 0; i < fieldNames.length; i++) {
			cmbSenseField.addItem(fieldNames[i]);
		}
		cmbSenseField.addActionListener(this);
		cmbSenseField.setToolTipText(PluginServices.getText(this, "sense_field_text"));
		chkSenseField = new JCheckBox(PluginServices.getText(this, "select_sense_field")+":");
		chkSenseField.addActionListener(this);
		txtDigitizedDir = new JTextField(12);
		txtReverseDigitizedDir = new JTextField(12);
		contentPane.addComponent(chkSenseField, cmbSenseField);
		contentPane.addComponent(new JLabel(PluginServices.getText(this,
				"digitizedDirection") + ":"), txtDigitizedDir);
		contentPane.addComponent(new JLabel(PluginServices.getText(this,
		"reverseDigitizedDirection")), txtReverseDigitizedDir);
		
		// separator
		contentPane.addComponent(new JLabel(" "));

		// Choose a .net file to write network. By default, saved in a temp directory, but
		// the user may decide to change the place to save it.
		JPanel panelFile = new JPanel();
		txtFile = new JTextField();
		txtFile.setText(NetworkUtils.getNetworkFile(owner.getLayer()).getPath());
		JButton btnJFC = new JButton("...");
		btnJFC.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						if (f.isDirectory())
							return true;
						String path = f.getPath().toLowerCase();
						if (path.endsWith(".net"))
							return true;
						return false;
					}

					@Override
					public String getDescription() {
						return (PluginServices.getText(this, "Ficheros_NET"));
					}
					
				});
				
				int res = fileChooser.showSaveDialog(getParent());
				if (res==JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getPath();
					if (!path.toLowerCase().endsWith(".net"))
						path = path + ".net";
					txtFile.setText(path);
				}
			}
			
		});
		panelFile.setLayout(new BorderLayout());
		panelFile.add(txtFile, BorderLayout.CENTER);
		panelFile.add(btnJFC, BorderLayout.EAST);
		contentPane.addComponent(new JLabel(PluginServices.getText(this,
		"save_net_file_in")), panelFile);
		
		actionPerformed(null);
		this.add(contentPane);
	}

	public void actionPerformed(ActionEvent e) {

		txtUnitFactor.setEnabled(
				!cmbCostField.getSelectedItem().equals(useLineLengthItem));
		if (chkTypeField.isSelected()) {
			owner.setTypeField(cmbTypeField.getSelectedItem().equals(nullValue)?
					null : (String) cmbTypeField.getSelectedItem());
			cmbTypeField.setEnabled(true);
		} else {
			owner.setTypeField(null);
			cmbTypeField.setEnabled(false);
		}

		if (chkLengthField.isSelected()) {
			owner.setLengthField(cmbLengthField.getSelectedItem().equals(nullValue)?
				null : (String) cmbLengthField.getSelectedItem());
			cmbLengthField.setEnabled(true);
		} else {
			owner.setLengthField(null);
			cmbLengthField.setEnabled(false);
		}

		if (chkSenseField.isSelected()) {
			owner.setSenseField(cmbSenseField.getSelectedItem().equals(nullValue)?
					null : (String) cmbSenseField.getSelectedItem());
			cmbSenseField.setEnabled(true);
			txtDigitizedDir.setEnabled(true);
			txtReverseDigitizedDir.setEnabled(true);
			if (e.getSource() == cmbSenseField) {
				updateDirectionValues();
			}
		} else {
			owner.setSenseField(null);
			cmbSenseField.setEnabled(false);
			txtDigitizedDir.setEnabled(false);
			txtReverseDigitizedDir.setEnabled(false);			
		}

		if (chkCostField.isSelected()) {
			owner.setCostField(cmbCostField.getSelectedItem().equals(useLineLengthItem)?
				null : (String) cmbCostField.getSelectedItem());
			cmbCostField.setEnabled(true);
		} else {
			owner.setCostField(null);
			cmbCostField.setEnabled(false);
		}

		if (chkCostUnits.isSelected()) {
			owner.setUnitFactor( txtUnitFactor.getDouble());
			txtUnitFactor.setEnabled(true);
		} else {
			owner.setUnitFactor(1); // TODO use unknown?
			txtUnitFactor.setEnabled(false);
		}
	}

	/**
	 * We collect values from sense field for let say 1000 recs.
	 * The idea is to show the user some of the possible values he may use.
	 * For example, teleatlas: FT, TF
	 * @throws BaseException 
	 */
	private void updateDirectionValues() {
		String senseField = (String) cmbSenseField.getSelectedItem();
		SelectableDataSource sds = null;
		try {
			sds = owner.getLayer().getRecordset();
			int fieldIndex = sds.getFieldIndexByName(senseField);
			Value first = null;
			Value second = null;
			sds.start();
			long hasta = Math.min(sds.getRowCount(), 1000);
			for (int i=0; i < hasta; i++) {
				Value aux = sds.getFieldValue(i, fieldIndex);
				if (aux.toString().equalsIgnoreCase(""))
					continue;
				if ((first == null) && (!(aux instanceof NullValue)))
					first = aux;
				if (first != null) {
					if (!((BooleanValue)first.equals(aux)).getValue()) {
						second = aux;
						break;
					}
				}
			}
			sds.stop();
			if (first == null) first = ValueFactory.createValue("");
			if (second == null) second= ValueFactory.createValue("");
			String aux1 = first.toString();
			String aux2 = second.toString();
			if (aux1.equalsIgnoreCase("TF"))
				if (aux2.equalsIgnoreCase("FT")) {
					String b = aux2;
					aux2 = aux1;
					aux1 = b;
				}
			txtDigitizedDir.setText(aux1);
			txtReverseDigitizedDir.setText(aux2);
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} catch (IncompatibleTypesException e) {
			e.printStackTrace();
		}
		finally {
			try {
				sds.stop();
			} catch (ReadDriverException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
	}

	public String getTxtFile() {
		return txtFile.getText();
	}

	public String getSenseDigitalization() {
		return txtDigitizedDir.getText();
	}

	public String getSenseReverseDigitalization() {
		return txtReverseDigitizedDir.getText();
	}
}
