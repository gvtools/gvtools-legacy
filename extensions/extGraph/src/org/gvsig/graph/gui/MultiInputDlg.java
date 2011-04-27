package org.gvsig.graph.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.JFileChooser;

import com.iver.andami.PluginServices;

public class MultiInputDlg extends JDialog {
	private boolean canceled = false;
	private class MyListener implements ActionListener {

		private MultiInputDlg dlg;
		public MyListener(MultiInputDlg dlg)
		{
			this.dlg = dlg;
		}
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equalsIgnoreCase("OK"))
			{
				closeCellEditor();
				dlg.dispose();
			}
			if (e.getActionCommand().equalsIgnoreCase("CANCEL"))
			{
				dlg.canceled = true;
				dlg.dispose();
				
			}			
		}
		
	}

	private class MyModel extends DefaultTableModel {

		private ArrayList l;
		private ArrayList r;

		MyModel(ArrayList leftList, ArrayList rightList) {
			l = leftList;
			r = rightList;
		}
		
		public ArrayList getRigthValues()
		{
			return r;
		}
		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			if (l == null) return 0;
			return l.size();
		}

		public Object getValueAt(int row, int column) {
			if (column == 0)
				return l.get(row);
			return r.get(row);
		}

		public boolean isCellEditable(int row, int column) {
			if (column == 1)
				return true;
			else
				return false;
		}

		public void setValueAt(Object aValue, int row, int column) {
			r.set(row, aValue);
		}
		
	}
	private ArrayList firstList;
	private ArrayList secondList;
	
	private String msg;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1914037292995440998L;
	private JTextArea jLblMsg = null;  //  @jve:decl-index=0:visual-constraint="220,10"
	private JScrollPane jScrollPane = null;  //  @jve:decl-index=0:visual-constraint="183,139"
	private JTable jTable = null;
	private String colName1;
	private String colName2;
	private JButton btnLoadVelocities;
	private JButton btnSaveVelocities;

	public MultiInputDlg(String msg, ArrayList leftValues, ArrayList veloKm) {
		firstList = leftValues;
		secondList = veloKm;
		this.msg = msg;
		initialize();
	}
	private void closeCellEditor() {
		if (jTable.getCellEditor() != null) {
			DefaultCellEditor cellEditor = (DefaultCellEditor) jTable.getCellEditor();
			cellEditor.stopCellEditing();
			// getTblStages().setValueAt(cellEditor.getCellEditorValue(), getTblStages().getEditingRow(), getTblStages().getEditingColumn());
		}
		
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		jLblMsg = new JTextArea();
		jLblMsg.setText(msg);
		jLblMsg.setBackground(SystemColor.control);
		jLblMsg.setLineWrap(true);
		jLblMsg.setPreferredSize(new Dimension(200, 100));
		jLblMsg.setFocusable(false);
		
		BorderLayout layout = new BorderLayout();
		layout.setHgap(30);
		layout.setVgap(10);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(jLblMsg, BorderLayout.NORTH);
		this.setSize(new Dimension(480, 350));
		this.getContentPane().add(getJScrollPane(), BorderLayout.CENTER);
		MyListener myListener = new MyListener(this);
		AcceptCancelPanel okCancelPanel = new AcceptCancelPanel(myListener, myListener);
		btnLoadVelocities = new JButton(PluginServices.getText(this,"load_velocities"));
		btnLoadVelocities.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					loadVelocities();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, e1.getMessage());
				}
			}
			
		});
		btnSaveVelocities = new JButton(PluginServices.getText(this,"save_velocities"));
		btnSaveVelocities.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					saveVelocities();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		JPanel aux = new JPanel();
		aux.add(btnLoadVelocities);
		aux.add(btnSaveVelocities);
		okCancelPanel.add(aux, BorderLayout.WEST);
		this.getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
	
	}

	protected void saveVelocities() throws IOException {
		closeCellEditor();
		
		String curDir = System.getProperty("user.dir");

		JFileChooser fileChooser = new JFileChooser("velocities", new File(curDir));
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String path = f.getPath().toLowerCase();
				if (path.endsWith(".properties"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return ".properties files";
			}
			
		});
		int res = fileChooser.showSaveDialog((Component) this);
		if (res==JFileChooser.APPROVE_OPTION) {
			File f =fileChooser.getSelectedFile();
			if (!f.getPath().toLowerCase().endsWith(".properties"))
				f = new File(f.getPath() + ".properties");
			
			Properties prop = new Properties();
			for (int i=0; i < firstList.size(); i++) {
				prop.setProperty(firstList.get(i) + "", (String) secondList.get(i));
			}
			FileOutputStream fout = new FileOutputStream(f);
			prop.store(fout, "");
		}
		
	}
	protected void loadVelocities() throws IOException {
		String curDir = System.getProperty("user.dir");

		JFileChooser fileChooser = new JFileChooser("velocities", new File(curDir));
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String path = f.getPath().toLowerCase();
				if (path.endsWith(".properties"))
					return true;
				return false;
			}

			@Override
			public String getDescription() {
				return (PluginServices.getText(this,"Ficheros_PROP"));
			}
			
		});
		int res = fileChooser.showOpenDialog((Component) this);
		if (res==JFileChooser.APPROVE_OPTION) {
			File f =fileChooser.getSelectedFile();
			Properties prop = new Properties();
			FileInputStream fin = new FileInputStream(f);
			prop.load(fin);
			firstList = new ArrayList();
			secondList = new ArrayList();
			Enumeration e = prop.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				firstList.add(key);
				secondList.add(prop.getProperty(key));
			}
			MyModel myModel = new MyModel(firstList, secondList);
			getJTable().setModel(myModel);
			String col1 = PluginServices.getText(this, "col_arc_type");
			String col2 = PluginServices.getText(this, "col_km_per_hour");
			setColumnNames(col1, col2);
			
		}
		
	}
	public ArrayList getRightValues() {
		return secondList;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable();
			MyModel myModel = new MyModel(firstList, secondList);
			jTable.setModel(myModel);
		}
		return jTable;
	}
	public boolean isCanceled() {
		return canceled;
	}
	public void setColumnNames(String col1, String col2) {
		this.colName1 = col1;
		this.colName2 = col2;
		jTable.getColumnModel().getColumn(0).setHeaderValue(col1);
		jTable.getColumnModel().getColumn(1).setHeaderValue(col2);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
