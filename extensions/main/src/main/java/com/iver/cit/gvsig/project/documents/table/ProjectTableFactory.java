package com.iver.cit.gvsig.project.documents.table;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.AddLayer;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.addlayer.fileopen.FileOpenWizard;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.ProjectDocumentFactory;
import com.iver.cit.gvsig.project.documents.contextMenu.actions.CopyDocumentContextMenuAction;
import com.iver.cit.gvsig.project.documents.contextMenu.actions.CutDocumentContextMenuAction;
import com.iver.cit.gvsig.project.documents.contextMenu.actions.PasteDocumentContextMenuAction;
import com.iver.cit.gvsig.project.documents.table.gui.DataBaseOpenDialog;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * Factory of Table.
 * 
 * @author Vicente Caballero Navarro
 */
public class ProjectTableFactory extends ProjectDocumentFactory {
	public static String registerName = "ProjectTable";

	static {
		// A�adimos nuestra extension para el tratamiento de la apertura de
		// ficheros
		// dentro de gvSIG
		ExtensionPoints extensionPoints = ExtensionPointsSingleton
				.getInstance();
		extensionPoints.add("FileTableOpenDialog", "FileOpenTable",
				new TableFileOpen());
	}

	/**
	 * Returns image of button.
	 * 
	 * @return Image button.
	 */
	public ImageIcon getButtonIcon() {
		return PluginServices.getIconTheme().get("document-table-icon");
	}

	/**
	 * Returns image of selected button.
	 * 
	 * @return Image button.
	 */
	public ImageIcon getSelectedButtonIcon() {
		return PluginServices.getIconTheme().get("document-table-icon-sel");
	}

	/**
	 * Introduce a gui to be able from the characteristics that we want a
	 * ProjectDocument
	 * 
	 * @param project
	 *            present Project.
	 * 
	 * @return new ProjectDocument.
	 */
	public ProjectDocument createFromGUI(Project project) {
		try {
			AddLayerDialog fopen = new AddLayerDialog(PluginServices.getText(
					this, "Nueva_tabla"));
			FileOpenWizard fod = new FileOpenWizard("FileTableOpenDialog",
					false);
			fod.setTitle(PluginServices.getText(this, "Tablas"));

			DataBaseOpenDialog dbod = new DataBaseOpenDialog(fopen);
			// dbod.setClasses(new Class[] { DBDriver.class });
			fopen.addWizardTab(PluginServices.getText(this, "Fichero"), fod);
			fopen.addTab(PluginServices.getText(this, "base_datos"), dbod);
			PluginServices.getMDIManager().addWindow(fopen);

			if (fopen.isAccepted()) {
				JPanel panel = fopen.getSelectedTab();

				if (panel instanceof FileOpenWizard) {
					File[] files = fod.getFiles();
					String[] driverNames = fod.getDriverNames();
					ProjectTable tableToReturn = null;
					if (files.length > 0) {
						ProjectTable[] auxTables = new ProjectTable[files.length - 1];

						for (int i = 0; i < files.length; i++) {
							String name = files[i].getName();

							LayerFactory.getDataSourceFactory()
									.addFileDataSource(driverNames[i], name,
											files[i].getAbsolutePath());

							DataSource dataSource = LayerFactory
									.getDataSourceFactory()
									.createRandomDataSource(name,
											DataSourceFactory.AUTOMATIC_OPENING);
							SelectableDataSource sds = new SelectableDataSource(
									dataSource);
							EditableAdapter auxea = new EditableAdapter();
							auxea.setOriginalDataSource(sds);

							// TODO: fjp: ESTO HAY QUE REVISARLO.
							// Por ahora, para obtener un driver que sirva para
							// esta
							// fuente de datos, compruebo que implementa
							// IWriteable.
							// IWriter writer = (IWriter)
							// LayerFactory.getWM().getWriter(driverNames[i]);
							// Driver drv =
							// LayerFactory.getDM().getDriver(driverNames[i]);
							// if (drv instanceof IWriter)
							// {
							// auxea.setWriter((IWriter) drv);
							// }
							ProjectTable projectTable = ProjectFactory
									.createTable(name, auxea);

							projectTable.setProjectDocumentFactory(this);

							Table t = new Table();
							t.setModel(projectTable);
							PluginServices.getMDIManager().addWindow(t);

							if (i == 0)
								tableToReturn = projectTable;
							else
								auxTables[i - 1] = projectTable;
						}
						if (files.length > 1) {
							for (int i = auxTables.length - 1; i >= 0; i--) {
								project.addDocument(auxTables[i]);
							}
						}
					}
					return tableToReturn;

				} else if (panel instanceof DataBaseOpenDialog) {
					String driverName = dbod.getDriverName();
					int port = -1;

					try {
						port = Integer.parseInt(dbod.getPort());
					} catch (NumberFormatException e) {
					}

					String name = dbod.getHost() + "/" + dbod.getDataBase();

					if (port != -1) {
						name = dbod.getHost() + ":" + port + "/"
								+ dbod.getDataBase();
					}

					String user = dbod.getUser().trim();
					String password = dbod.getPassword();

					if (user.equals("")) {
						user = null;
						password = null;
					}

					name = name + " Table:" + dbod.getTable();

					LayerFactory.getDataSourceFactory().addDBDataSourceByTable(
							name, dbod.getHost(), port, user, password,
							dbod.getDataBase(), dbod.getTable(), driverName);

					DataSource dataSource = LayerFactory.getDataSourceFactory()
							.createRandomDataSource(name,
									DataSourceFactory.AUTOMATIC_OPENING);
					SelectableDataSource sds = new SelectableDataSource(
							dataSource);
					EditableAdapter auxea = new EditableAdapter();

					// TODO: fjp: ESTO HAY QUE REVISARLO.
					// Por ahora, para obtener un driver que sirva para esta
					// fuente de datos, compruebo que implementa IWriteable.
					// IWriter writer = (IWriter)
					// LayerFactory.getWM().getWriter(driverNames[i]);
					// Driver drv = LayerFactory.getDM().getDriver(driverName);
					// if (drv instanceof IWriter)
					// {
					// auxea.setWriter((IWriter) drv);
					// }
					auxea.setOriginalDataSource(sds);

					ProjectTable projectTable = ProjectFactory.createTable(
							name, auxea);
					projectTable.setProjectDocumentFactory(this);

					Table t = new Table();
					t.setModel(projectTable);
					PluginServices.getMDIManager().addWindow(t);

					return projectTable;
				}
			}
		} catch (DriverLoadException e) {
			NotificationManager.addError("Error al cargar los drivers", e);
		} catch (NoSuchTableException e) {
			e.printStackTrace();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Create a new ProjectTable
	 * 
	 * @param baseName
	 *            name
	 * 
	 * @return ProjectTable.
	 */
	public static ProjectTable createTable(String name, IEditableSource es) {
		ProjectTable t = new ProjectTable();

		if (es != null) {
			t.setModel(es);

			try {
				t.createAlias();
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}

		t.setName(name);
		t.setCreationDate(DateFormat.getInstance().format(new Date()));
		int numTables = ((Integer) ProjectDocument.NUMS.get(registerName))
				.intValue();
		ProjectDocument.NUMS.put(registerName, new Integer(numTables++));

		return t;
	}

	/**
	 * Returns the name of registration in the point of extension.
	 * 
	 * @return Name of registration
	 */
	public String getRegisterName() {
		return registerName;
	}

	/**
	 * Registers in the points of extension the Factory with alias.
	 * 
	 */
	public static void register() {
		register(registerName, new ProjectTableFactory(),
				"com.iver.cit.gvsig.project.ProjectTable");

		registerAction(registerName, "copy",
				new CopyDocumentContextMenuAction());
		registerAction(registerName, "cut", new CutDocumentContextMenuAction());
		registerAction(registerName, "paste",
				new PasteDocumentContextMenuAction());

		PluginServices.getIconTheme().registerDefault(
				"document-table-icon",
				AddLayer.class.getClassLoader()
						.getResource("images/tablas.png"));//
		PluginServices.getIconTheme().registerDefault(
				"document-table-icon-sel",
				AddLayer.class.getClassLoader().getResource(
						"images/tablas_sel.png"));

		PluginServices.getIconTheme().registerDefault(
				"edit-copy",
				AddLayer.class.getClassLoader().getResource(
						"images/editcopy.png"));//
		PluginServices.getIconTheme().registerDefault(
				"edit-cut",
				AddLayer.class.getClassLoader().getResource(
						"images/editcut.png"));
		PluginServices.getIconTheme().registerDefault(
				"edit-paste",
				AddLayer.class.getClassLoader().getResource(
						"images/editpaste.png"));
		PluginServices.getIconTheme().registerDefault(
				"edit-delete",
				AddLayer.class.getClassLoader().getResource(
						"images/editdelete.png"));
	}

	/**
	 * Returns the name of ProjectDocument.
	 * 
	 * @return Name of ProjectDocument.
	 */
	public String getNameType() {
		return PluginServices.getText(this, "Tabla");
	}

	/**
	 * Create a new ProjectDocument.
	 * 
	 * @param project
	 *            Opened project.
	 * 
	 * @return ProjectDocument.
	 */
	public ProjectDocument create(Project project) {
		ProjectTable pt = null;

		pt = ProjectTableFactory.createTable("", null);
		pt.setProject(project, 0);

		pt.setProjectDocumentFactory(this);

		return pt;
	}

	/**
	 * Returns the priority of de ProjectDocument.
	 * 
	 * @return Priority.
	 */
	public int getPriority() {
		return 1;
	}

	public boolean resolveImportXMLConflicts(XMLEntity root, Project project,
			Hashtable conflicts) {
		return true;
	}
}

// [eiel-gestion-conexiones]