package com.hardcode.gdbms.gui.editingTable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SetUp;

/**
 * @author Fernando González Cortés
 */
public class EditingFrame extends JFrame {

	private javax.swing.JPanel jContentPane = null;
	private EditingTable editingTable = null;
	private JButton jButton = null;
	private JPanel jPanel = null;
	private JButton jButton1 = null;

	/**
	 * This is the default constructor
	 */
	public EditingFrame() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(1000, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getEditingTable(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getJPanel(), java.awt.BorderLayout.WEST);
		}
		return jContentPane;
	}

	/**
	 * This method initializes editingTable
	 * 
	 * @return com.hardcode.gdbms.gui.editingTable.EditingTable
	 */
	private EditingTable getEditingTable() {
		if (editingTable == null) {
			editingTable = new EditingTable();
		}
		return editingTable;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
		}
		return jButton;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.add(getJButton1(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
		}
		return jButton1;
	}

	public static void main(String[] args) throws Exception {
		DataSourceFactory dsf = SetUp.setUp();
		final DataSource ds = dsf.createRandomDataSource("persona",
				DataSourceFactory.MANUAL_OPENING);
		ds.start();
		final EditingFrame ef = new EditingFrame();
		ef.getEditingTable().setDataSource(ds);
		ef.getEditingTable().startEditing();
		ef.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					ef.getEditingTable().commit();
					ds.stop();
					System.exit(0);
				} catch (ReadDriverException e1) {
					throw new RuntimeException(e1);
				} catch (WriteDriverException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		ef.show();
	}
}
