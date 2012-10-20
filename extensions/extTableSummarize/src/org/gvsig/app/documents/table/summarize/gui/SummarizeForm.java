/* gvSIG. Sistema de Informacion Geografica de la Generalitat Valenciana
 *
 * Copyright (C) 2009 IVER T.I.
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
 *  IVER T.I.
 *   C/ Lerida, 20
 *   46009 Valencia
 *   SPAIN
 *   http://www.iver.es
 *   dac@iver.es
 *   +34 963163400
 *   
 *  or
 *  
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibañez, 50
 *   46010 VALENCIA
 *   SPAIN
 */
package org.gvsig.app.documents.table.summarize.gui;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.gvsig.app.documents.table.summarize.Summarize;
import org.gvsig.app.documents.table.summarize.exceptions.DBFExportException;
import org.gvsig.app.documents.table.summarize.exceptions.GroupByFieldNotExistsException;
import org.gvsig.app.documents.table.summarize.exceptions.GroupingErrorException;
import org.gvsig.app.documents.table.summarize.exceptions.InitializationException;
import org.gvsig.app.documents.table.summarize.exceptions.SummarizeException;
import org.gvsig.app.documents.table.summarize.utils.SelectedStatistics;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.ProjectDocumentFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.swing.fileFilter.ExampleFileFilter;
import com.iver.utiles.swing.threads.SwingWorker;

/**
 * Contains the GUI of the Summarize tool for tables.
 * 
 * @author IVER T.I. <http://www.iver.es> 01/02/2009
 */
public class SummarizeForm extends javax.swing.JPanel implements IWindow {

	private static final long serialVersionUID = -1184150355136852507L;
	private static Logger logger = Logger.getLogger(SummarizeForm.class
			.getName());
	private WindowInfo moWindowInfo = null;
	public Table table;
	public String selectedColumn;
	public FBitSet allrows;
	public long tempNumFilteredRows;

	public ArrayList<SelectedStatistics> operations = new ArrayList<SelectedStatistics>();

	SwingWorker loader = null;

	/** Creates new form SummarizeForm */
	public SummarizeForm() {
		initComponents();

		setTranslation();
		loadDBFColumnsInComboBox();
		setFormatTableHeaders();
		loadTable();
	}

	public void show(Table table, String selectedColumn) {
		this.jLabelLoader.setVisible(false);
		this.table = table;
		this.selectedColumn = selectedColumn;

		// SET AS SELECTED THE COLUMN THAT THE USER HAS SELECTED IN THE ORIGINAL
		// DBF TABLE
		String item = null;
		for (int i = 0; i < jComboBoxExtGroupByField.getItemCount(); i++) {
			item = (String) jComboBoxExtGroupByField.getItemAt(i);
			if (item.equals(selectedColumn)) {
				jComboBoxExtGroupByField.setSelectedItem(item);
			}
		}

		PluginServices.getMDIManager().addCentredWindow(this);
	}

	public void show(Table table) throws SummarizeException {
		if (jComboBoxExtGroupByField.getItemCount() > 0) {
			show(table, (String) jComboBoxExtGroupByField.getItemAt(0));
		} else {
			throw new SummarizeException(PluginServices.getText(this,
					"Summarize_Table_has_no_fields"));
		}
	}

	public boolean isDBFFile() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();
		if (v instanceof Table)
			return true;
		else
			return false;
	}

	public void loadDBFColumnsInComboBox() {

		if (isDBFFile()) {
			Table table = (Table) PluginServices.getMDIManager()
					.getActiveWindow();

			try {
				DataSource sds = table.getModel().getModelo().getRecordset();
				// BitSet indices = table.getSelectedFieldIndices();
				// System.out.println("columna seleccionada: "
				// +sds.getFieldName(indices.nextSetBit(0)));
				for (int i = 0; i < sds.getFieldCount(); i++) {
					jComboBoxExtGroupByField.addItem(sds.getFieldName(i));
				}
			} catch (ReadDriverException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Error_accessing_the_table"), e);
			}
		}
	}

	/**
	 * Formatea los headers del grid.
	 */
	public void setFormatTableHeaders() {
		DefaultTableModel model = (DefaultTableModel) jXTableSeleccion
				.getModel();
		Object[] newIdentifiers = new Object[] {
				PluginServices.getText(this, "SummarizeForm_columna_id"),
				PluginServices.getText(this, "SummarizeForm_columna_minimum"),
				PluginServices.getText(this, "SummarizeForm_columna_maximum"),
				PluginServices.getText(this, "SummarizeForm_columna_average"),
				PluginServices.getText(this, "SummarizeForm_columna_sum"),
				PluginServices.getText(this, "SummarizeForm_columna_sd"),
				PluginServices.getText(this, "SummarizeForm_columna_variance") };

		model.setColumnIdentifiers(newIdentifiers);

		// TableColumnModel columnModel = jXTableSeleccion.getColumnModel();
		//
		// columnModel.getColumn(0).setPreferredWidth(50);
		// columnModel.getColumn(1).setPreferredWidth(50);
		// columnModel.getColumn(2).setPreferredWidth(50);
		// columnModel.getColumn(3).setPreferredWidth(50);
		// columnModel.getColumn(4).setPreferredWidth(50);
		// columnModel.getColumn(5).setPreferredWidth(60);
		// columnModel.getColumn(6).setPreferredWidth(50);

		jXTableSeleccion.setHorizontalScrollEnabled(true);
		jXTableSeleccion.setHighlighters(new AlternateRowHighlighter());
	}

	/**
	 * Loads the table rows, selecting only the ones which has numeric format.
	 * Thats because statistics can not be done over alphanumeric values.
	 */
	public void loadTable() {

		DefaultTableModel model = (DefaultTableModel) jXTableSeleccion
				.getModel();
		while (model.getRowCount() > 0) {
			model.removeRow(0);
		}

		if (isDBFFile()) {
			Table table = (Table) PluginServices.getMDIManager()
					.getActiveWindow();

			try {
				DataSource sds = table.getModel().getModelo().getRecordset();

				for (int i = 0; i < sds.getFieldCount(); i++) {
					// only numeric columns will be set on the table
					if (sds.getFieldType(i) == Types.BIGINT
							|| sds.getFieldType(i) == Types.DECIMAL
							|| sds.getFieldType(i) == Types.DOUBLE
							|| sds.getFieldType(i) == Types.FLOAT
							|| sds.getFieldType(i) == Types.INTEGER
							|| sds.getFieldType(i) == Types.NUMERIC
							|| sds.getFieldType(i) == Types.REAL
							|| sds.getFieldType(i) == Types.SMALLINT
							|| sds.getFieldType(i) == Types.TINYINT) {

						Object[] fila = new Object[] { sds.getFieldName(i),
								false, false, false, false, false, false };
						model.addRow(fila);
					}
				}
				// set the edit mode on on every cell
				for (int i = 0; i < jXTableSeleccion.getRowCount(); i++) {
					jXTableSeleccion.setEditingRow(i);
					for (int j = 0; j < jXTableSeleccion.getColumnCount(); j++) {
						jXTableSeleccion.setEditingColumn(j);
					}
				}
			} catch (ReadDriverException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Error_accessing_the_table"), e);
			}
		}
	}

	/**
	 * Traduccion de todos los componentes del formulario.
	 */
	private void setTranslation() {
		jLabelDescription.setText(PluginServices.getText(this,
				"lbl_SummarizeForm_descripcion1"));
		jLabelPunto1.setText(PluginServices.getText(this,
				"lbl_SummarizeForm_punto1"));
		jLabelPunto2.setText(PluginServices.getText(this,
				"lbl_SummarizeForm_punto2"));
		jLabelPunto3.setText(PluginServices.getText(this,
				"lbl_SummarizeForm_punto3"));
		jButtonAceptar.setText(PluginServices.getText(this,
				"btn_SummarizeForm_aceptar"));
		jButtonCancel.setText(PluginServices.getText(this,
				"btn_SummarizeForm_cancelar"));
	}

	public void createTableDocument(String documentName, String driverName,
			String filePath) {
		// basically copied from ProjectTableFactory.createFromGUI
		LayerFactory.getDataSourceFactory().addFileDataSource(driverName,
				documentName, filePath);

		DataSource dataSource;
		try {
			dataSource = LayerFactory.getDataSourceFactory()
					.createRandomDataSource(documentName,
							DataSourceFactory.AUTOMATIC_OPENING);
			SelectableDataSource sds = new SelectableDataSource(dataSource);
			EditableAdapter auxea = new EditableAdapter();
			auxea.setOriginalDataSource(sds);

			ProjectTable projectTable = ProjectFactory.createTable(
					documentName, auxea);

			ProjectDocumentFactory pde = null;
			ExtensionPoints extensionPoints = ExtensionPointsSingleton
					.getInstance();

			ExtensionPoint extPoint = ((ExtensionPoint) extensionPoints
					.get("Documents"));
			try {
				pde = (ProjectDocumentFactory) extPoint
						.create(ProjectTableFactory.registerName);
				if (pde == null) {
					Exception e = new Exception(PluginServices.getText(this,
							"Error_creating_new_table"));
					NotificationManager.showMessageError(PluginServices
							.getText(this, "Error_creating_new_table"), e);
					return;
				}

				projectTable.setProjectDocumentFactory(pde);
				ProjectExtension pe = (ProjectExtension) PluginServices
						.getExtension(ProjectExtension.class);
				Project project = pe.getProject();
				project.addDocument(projectTable);
				project.setModified(true);
				IWindow table = projectTable.createWindow();
				PluginServices.getMDIManager().addWindow(table);

			} catch (InstantiationException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Error_creating_new_table"), e);
			} catch (IllegalAccessException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Error_creating_new_table"), e);
			} catch (Exception e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Error_creating_new_table"), e);
			}

		} catch (DriverLoadException e1) {
			NotificationManager.showMessageError(
					PluginServices.getText(this, "Error_creating_new_table"),
					e1);
		} catch (NoSuchTableException e1) {
			NotificationManager.showMessageError(
					PluginServices.getText(this, "Error_creating_new_table"),
					e1);
		} catch (ReadDriverException e1) {
			NotificationManager.showMessageError(
					PluginServices.getText(this, "Error_creating_new_table"),
					e1);
		}

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jLabelDescription = new javax.swing.JLabel();
		jPanelMain = new javax.swing.JPanel();
		jLabelPunto1 = new javax.swing.JLabel();
		jComboBoxExtGroupByField = new org.gvsig.app.documents.table.summarize.utils.JComboBoxExt();
		jLabelPunto2 = new javax.swing.JLabel();
		jScrollPaneTree = new javax.swing.JScrollPane();
		jXTableSeleccion = new org.jdesktop.swingx.JXTable();
		jLabelPunto3 = new javax.swing.JLabel();
		jTextFieldExtFile = new org.gvsig.app.documents.table.summarize.utils.JTextFieldExt();
		jButtonFileChooser = new javax.swing.JButton();
		jLabelLoader = new javax.swing.JLabel();
		jButtonAceptar = new javax.swing.JButton();
		jButtonCancel = new javax.swing.JButton();

		jLabelDescription
				.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelDescription.setText("description1");

		jPanelMain.setBorder(javax.swing.BorderFactory.createEtchedBorder());

		jLabelPunto1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelPunto1.setText("point1");

		jComboBoxExtGroupByField
				.addItemListener(new java.awt.event.ItemListener() {
					public void itemStateChanged(java.awt.event.ItemEvent evt) {
						jComboBoxExtGroupByFieldItemStateChanged(evt);
					}
				});

		jLabelPunto2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelPunto2.setText("punto2");
		jLabelPunto2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

		jXTableSeleccion.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] {

				}, new String[] {
						PluginServices
								.getText(this, "SummarizeForm_columna_id"),
						PluginServices.getText(this,
								"SummarizeForm_columna_minimum"),
						PluginServices.getText(this,
								"SummarizeForm_columna_maximum"),
						PluginServices.getText(this,
								"SummarizeForm_columna_average"),
						PluginServices.getText(this,
								"SummarizeForm_columna_sum"),
						PluginServices
								.getText(this, "SummarizeForm_columna_sd"),
						PluginServices.getText(this,
								"SummarizeForm_columna_variance") }) {
			Class[] tableModelTypes = new Class[] { java.lang.String.class,
					java.lang.Boolean.class, java.lang.Boolean.class,
					java.lang.Boolean.class, java.lang.Boolean.class,
					java.lang.Boolean.class, java.lang.Boolean.class };
			boolean[] canEdit = new boolean[] { false, true, true, true, true,
					true, true };

			public Class getColumnClass(int columnIndex) {
				return tableModelTypes[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		jXTableSeleccion.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jXTableSeleccionMouseClicked(evt);
			}
		});
		jScrollPaneTree.setViewportView(jXTableSeleccion);

		jLabelPunto3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabelPunto3.setText("punto3");

		jButtonFileChooser.setText("...");
		jButtonFileChooser
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						jButtonFileChooserActionPerformed(evt);
					}
				});

		org.jdesktop.layout.GroupLayout jPanelMainLayout = new org.jdesktop.layout.GroupLayout(
				jPanelMain);
		jPanelMain.setLayout(jPanelMainLayout);
		jPanelMainLayout
				.setHorizontalGroup(jPanelMainLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelMainLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanelMainLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jScrollPaneTree,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												525, Short.MAX_VALUE)
										.add(jLabelPunto2,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												525, Short.MAX_VALUE)
										.add(jLabelPunto3,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												525, Short.MAX_VALUE)
										.add(jComboBoxExtGroupByField,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												525, Short.MAX_VALUE)
										.add(jLabelPunto1,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												525, Short.MAX_VALUE)
										.add(org.jdesktop.layout.GroupLayout.TRAILING,
												jPanelMainLayout
														.createSequentialGroup()
														.add(jTextFieldExtFile,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																474,
																Short.MAX_VALUE)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(jButtonFileChooser)))
								.addContainerGap()));
		jPanelMainLayout
				.setVerticalGroup(jPanelMainLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanelMainLayout
								.createSequentialGroup()
								.addContainerGap()
								.add(jLabelPunto1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED)
								.add(jComboBoxExtGroupByField,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jLabelPunto2)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jScrollPaneTree,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										251, Short.MAX_VALUE)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jLabelPunto3)
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanelMainLayout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jButtonFileChooser)
										.add(jTextFieldExtFile,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));

		jLabelLoader.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"/images/loader.gif"))); // NOI18N

		jButtonAceptar.setText(PluginServices.getText(this, "Aceptar"));
		jButtonAceptar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonAceptarActionPerformed(evt);
			}
		});

		jButtonCancel.setText(PluginServices.getText(this, "Cancelar"));
		jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButtonCancelActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.LEADING)
								.add(org.jdesktop.layout.GroupLayout.TRAILING,
										layout.createSequentialGroup()
												.add(jLabelLoader,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
														29,
														org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.UNRELATED)
												.add(jButtonAceptar)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED)
												.add(jButtonCancel))
								.add(jLabelDescription,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										549, Short.MAX_VALUE)
								.add(org.jdesktop.layout.GroupLayout.TRAILING,
										jPanelMain,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)).addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(jLabelDescription,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								39,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED)
						.add(jPanelMain,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED)
						.add(layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.BASELINE)
								.add(jButtonCancel)
								.add(jButtonAceptar)
								.add(jLabelLoader,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										18,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
						.addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	private void jXTableSeleccionMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jXTableSeleccionMouseClicked

		int indiceFila = jXTableSeleccion.getSelectedRow();
		int indiceColumna = jXTableSeleccion.getSelectedColumn();

		if (indiceFila != -1 && indiceColumna != -1) {
			boolean check = Boolean.valueOf(String.valueOf(jXTableSeleccion
					.getValueAt(indiceFila, indiceColumna)));
			jXTableSeleccion.setValueAt(!check, indiceFila, indiceColumna);
		}
	}// GEN-LAST:event_jXTableSeleccionMouseClicked

	private void jComboBoxExtGroupByFieldItemStateChanged(
			java.awt.event.ItemEvent evt) {// GEN-FIRST:event_jComboBoxExtGroupByFieldItemStateChanged
		if (evt.getStateChange() == ItemEvent.SELECTED) {
			this.selectedColumn = (String) jComboBoxExtGroupByField
					.getSelectedItem();
		}
	}// GEN-LAST:event_jComboBoxExtGroupByFieldItemStateChanged

	private void jButtonFileChooserActionPerformed(
			java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonFileChooserActionPerformed

		String dbfDir = "";
		JFileChooser filechooser = new JFileChooser();
		filechooser.setDialogTitle(PluginServices.getText(this,
				"Choose_target_file_DBF"));

		// Aplicamos el filtro para mostarar solo ficheros dbf en el arbol de
		// directorios
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("dbf");
		filter.setDescription(PluginServices.getText(this, "DBF_files"));
		filechooser.setFileFilter(filter);

		int returnVal = filechooser.showOpenDialog((Component) PluginServices
				.getMDIManager().getActiveWindow());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File file = filechooser.getSelectedFile();
				dbfDir = file.getCanonicalPath();
				if (!dbfDir.endsWith(".dbf"))
					dbfDir += ".dbf";

				File aux = new File(dbfDir);
				if (aux.exists()) {
					// si no se elige sobreescribir el fichero, entonces se
					// busca un numero para numerarlo (1), (2)...
					dbfDir = dbfDir.substring(0, dbfDir.indexOf(".dbf"));
					boolean flag = true;
					int i = 0;
					while (flag) {
						i++;
						aux = new File(dbfDir + "(" + i + ").dbf");
						if (aux.exists())
							flag = true;
						else
							flag = false;
					}
					file = new File(dbfDir + "(" + i + ")");
				}

				dbfDir = file.getCanonicalPath();
				if (!dbfDir.endsWith(".dbf"))
					dbfDir += ".dbf";
				jTextFieldExtFile.setText(dbfDir);

			} catch (IOException e) {
				PluginServices.getLogger().error("Error selecting output file",
						e);
			}
		}

	}// GEN-LAST:event_jButtonFileChooserActionPerformed

	private void jButtonAceptarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonAceptarActionPerformed
		// if a file has been allready selected...
		if (!jTextFieldExtFile.getText().equals("")
				&& jTextFieldExtFile.getText().endsWith(".dbf")) {

			loader = new SwingWorker() {
				public Object construct() {
					jLabelLoader.setVisible(true);
					return true;
				}

				public void finished() {
					jLabelLoader.setVisible(false);
				}
			};
			loader.start();

			// carga las operaciones escogidas por el usuario en las estructuras
			// ArrayList<String> headers, ArrayList<Integer> types y
			// ArrayList<SelectedStatistics> operations
			try {
				boolean flag = loadSelectedOperations(false);

				if (!flag) {
					JOptionPane.showMessageDialog(this, PluginServices.getText(
							this, "SummarizeForm_seleccionar_operaciones"));
				} else {
					File endFile = new File(jTextFieldExtFile.getText());
					if (endFile.exists()) {
						int returnValue = JOptionPane
								.showConfirmDialog(
										this,
										PluginServices
												.getText(this,
														"File_exists_Do_you_want_to_overwrite_it?"),
										PluginServices.getText(this,
												"Warning_Output_File"),
										JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
						if (returnValue == JOptionPane.NO_OPTION) {
							return;
						}
						endFile.delete();
					}
					Summarize sumCalculator = new Summarize();
					sumCalculator.setDataSource(this.table.getModel()
							.getModelo().getRecordset());
					sumCalculator.setGroupByField(selectedColumn);
					sumCalculator.setOperations(operations);
					sumCalculator.summarizeToDbf(endFile);
					createTableDocument(endFile.getName(), "gdbms dbf driver",
							endFile.getAbsolutePath());
					PluginServices.getMDIManager().closeWindow(this);
				}
			} catch (GroupByFieldNotExistsException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Group_by_field_does_not_exist"), e);
			} catch (ReadDriverException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Error_accessing_the_table"), e);
			} catch (DBFExportException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Error_saving_the_output_DBF_file"), e);
			} catch (GroupingErrorException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Error_calculating_the_groups"), e);
			} catch (FieldNotFoundException e) {
				NotificationManager.showMessageError(PluginServices.getText(
						this, "Summarize_Field_does_not_exist"), e);
			} catch (InitializationException e) {
				NotificationManager.showMessageError(e.getMessage(), e);
			} catch (SummarizeException e) {
				NotificationManager
						.showMessageError(PluginServices.getText(this,
								"Error_summarizing_table"), e);
			}

			loader.interrupt();
		} else {
			JOptionPane.showMessageDialog(this, PluginServices.getText(this,
					"SummarizeForm_fichero_destino"));
		}
	}// GEN-LAST:event_jButtonAceptarActionPerformed

	private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButtonCancelActionPerformed
		PluginServices.getMDIManager().closeWindow(SummarizeForm.this);
	}// GEN-LAST:event_jButtonCancelActionPerformed

	/**
	 * Loads the user chosen operations in these structures: ArrayList<String>
	 * headers, ArrayList<Integer> types y ArrayList<SelectedStatistics>
	 * operations Headers has the header names, types has the type of each field
	 * and operations has the selected statistics operations on every field.
	 * 
	 * @param flag
	 * @return
	 * @throws ReadDriverException
	 * @throws GroupByFieldNotExistsException
	 * @throws FieldNotFoundException
	 */
	public boolean loadSelectedOperations(boolean flag)
			throws ReadDriverException, FieldNotFoundException {
		DataSource sds = table.getModel().getModelo().getRecordset();

		operations.clear();

		// recorremos la tabla para tomar los datos de las operaciones marcadas
		// por el usuario
		// y lo introducimos en la estructura SelectedStatistics.
		// Solo se introduciran filas si alguna de las operaciones de esa
		// columna ha sido seleccionada (para evitar proceso porsterior en los
		// bucles).
		for (int j = 0; j < jXTableSeleccion.getRowCount(); j++) {

			if ((Boolean) jXTableSeleccion.getValueAt(j, 1)
					|| (Boolean) jXTableSeleccion.getValueAt(j, 2)
					|| (Boolean) jXTableSeleccion.getValueAt(j, 3)
					|| (Boolean) jXTableSeleccion.getValueAt(j, 4)
					|| (Boolean) jXTableSeleccion.getValueAt(j, 5)
					|| (Boolean) jXTableSeleccion.getValueAt(j, 6)) {

				flag = true;

				// SELECTED OPERATIONS STORAGE ARRAY
				SelectedStatistics fila = new SelectedStatistics();

				fila.setColumnName((String) jXTableSeleccion.getValueAt(j, 0));
				fila.setColumnNumber(j);
				fila.setMin((Boolean) jXTableSeleccion.getValueAt(j, 1));
				fila.setMax((Boolean) jXTableSeleccion.getValueAt(j, 2));
				fila.setMean((Boolean) jXTableSeleccion.getValueAt(j, 3));
				fila.setSum((Boolean) jXTableSeleccion.getValueAt(j, 4));
				fila.setSd((Boolean) jXTableSeleccion.getValueAt(j, 5));
				fila.setVar((Boolean) jXTableSeleccion.getValueAt(j, 6));

				String currFieldName = (String) jXTableSeleccion.getValueAt(j,
						0);
				int currFieldIdx = sds.getFieldIndexByName(currFieldName);
				if (currFieldIdx == -1) {
					throw new FieldNotFoundException();
				}
				int currFieldType = sds.getFieldType(currFieldIdx);
				fila.setColumnType(currFieldType);
				operations.add(fila);
			}
		}

		return flag;
	}

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButtonAceptar;
	private javax.swing.JButton jButtonCancel;
	private javax.swing.JButton jButtonFileChooser;
	private org.gvsig.app.documents.table.summarize.utils.JComboBoxExt jComboBoxExtGroupByField;
	private javax.swing.JLabel jLabelDescription;
	private javax.swing.JLabel jLabelLoader;
	private javax.swing.JLabel jLabelPunto1;
	private javax.swing.JLabel jLabelPunto2;
	private javax.swing.JLabel jLabelPunto3;
	private javax.swing.JPanel jPanelMain;
	private javax.swing.JScrollPane jScrollPaneTree;
	private org.gvsig.app.documents.table.summarize.utils.JTextFieldExt jTextFieldExtFile;
	private org.jdesktop.swingx.JXTable jXTableSeleccion;

	// End of variables declaration//GEN-END:variables
	public WindowInfo getWindowInfo() {
		// Este metodo lo invoca el framework (Andami) para obtener informacion
		// acerca de la ventana en la que debe mostrar el panel.
		if (moWindowInfo == null) {
			moWindowInfo = new WindowInfo(WindowInfo.MODALDIALOG
					| WindowInfo.RESIZABLE);
			// moWindowInfo = new WindowInfo(WindowInfo.ICONIFIABLE |
			// WindowInfo.RESIZABLE);
			moWindowInfo.setHeight(515);
			moWindowInfo.setWidth(600);
			moWindowInfo.setTitle(PluginServices.getText(this,
					"title_summarize"));
		}
		return moWindowInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}