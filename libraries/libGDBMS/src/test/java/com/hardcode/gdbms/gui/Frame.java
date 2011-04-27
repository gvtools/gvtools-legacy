/*
 * Created on 12-oct-2004
 */
package com.hardcode.gdbms.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class Frame extends JFrame {
	private javax.swing.JPanel jContentPane = null;
	private JTable table = null;
	private JScrollPane jScrollPane = null;

	/**
	 * This is the default constructor
	 */
	public Frame() {
		super();
		initialize();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 */
	public void setTableModel(TableModel t) {
		getTable().setModel(t);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("JFrame");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					try {
						((GDBMSTableModel) getTable().getModel()).getDataSource()
						 .stop();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
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
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}

		return jContentPane;
	}

	/**
	 * This method initializes table
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getTable() {
		if (table == null) {
			table = new JTable();
		}

		return table;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getTable());
		}

		return jScrollPane;
	}
}
