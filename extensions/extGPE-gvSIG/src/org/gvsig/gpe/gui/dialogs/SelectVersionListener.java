package org.gvsig.gpe.gui.dialogs;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.gvsig.fmap.drivers.gpe.addlayer.GPEWriterFileFilter;
import org.gvsig.fmap.drivers.gpe.addlayer.XMLSchemaFileFilter;
import org.gvsig.fmap.drivers.gpe.handlers.FmapErrorHandler;
import org.gvsig.fmap.drivers.gpe.writer.ExportTask;
import org.gvsig.fmap.drivers.gpe.writer.schema.GMLSchemaCreator;
import org.gvsig.gpe.GPERegister;
import org.gvsig.gpe.exceptions.WriterHandlerCreationException;
import org.gvsig.gpe.writer.GPEWriterHandler;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.utiles.FileUtils;
import com.iver.utiles.Utils;

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
/* CVS MESSAGES:
 *
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class SelectVersionListener implements ActionListener, ItemListener {
	public static final String CANCEL_BUTTON = "cancel";
	public static final String EXPORT_BUTTON = "export";
	public static final String WRITER_COMBO = "combo";
	public static final String FILE_BUTTON = "file";
	public static final String SCHEMA_BUTTON = "schema";
	private SelectVersionWindow window = null;
	private static String lastPath = null;

	public SelectVersionListener(SelectVersionWindow window) {
		super();
		this.window = window;
		writerComboSelectionChange();
		if (lastPath != null) {
			window.setFile(lastPath);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo(CANCEL_BUTTON) == 0) {
			cancelButtonActionPerformed();
		} else if (e.getActionCommand().compareTo(EXPORT_BUTTON) == 0) {
			try {
				exportButtonActionPerformed();
			} catch (WriterHandlerCreationException e1) {
				NotificationManager.addError(e1);
			} catch (FileNotFoundException e1) {
				NotificationManager.addError(e1);
			}
		} else if (e.getActionCommand().compareTo(WRITER_COMBO) == 0) {
			writerComboSelectionChange();
		} else if (e.getActionCommand().compareTo(FILE_BUTTON) == 0) {
			fileButtonActionPerformed();
		} else if (e.getActionCommand().compareTo(SCHEMA_BUTTON) == 0) {
			schemaButtonActionPerformed();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
		if (window.isXMLSchemaCreated()) {
			addDefaultSchema();
		}
	}

	/**
	 * Create and add a default XML schema
	 */
	private void addDefaultSchema() {
		String sFile = window.getSelectedFile();
		if (sFile.length() > 0) {
			File file = new File(sFile);
			String extension = FileUtils.getFileExtension(file);
			sFile = sFile.substring(0, sFile.length() - extension.length());
			sFile = sFile + "xsd";
			window.setXMLSchema(sFile);
		} else {
			window.setXMLSchema("default.xsd");
		}
	}

	/**
	 * @return a file filter for the selected driver
	 */
	private FileFilter getFileFilter() {
		GPEWriterHandler writer = window.getSelectedWriter();
		FileFilter filter = new GPEWriterFileFilter(writer);
		return filter;
	}

	/**
	 * When the cancel button is clicked
	 */
	private boolean fileButtonActionPerformed() {
		File file = getFile(lastPath, getFileFilter());
		if (file == null) {
			return false;
		}
		String sFile = null;
		String extension = Utils.getExtension(file);
		if (extension == null) {
			sFile = file.getPath()
					+ "."
					+ window.getSelectedWriter().getFileExtension()
							.toLowerCase();
		} else {
			sFile = file.getPath();
		}
		window.setFile(sFile);
		return true;
	}

	/**
	 * When the schema button is clicked
	 */
	private boolean schemaButtonActionPerformed() {
		File file = getFile(lastPath, new XMLSchemaFileFilter());
		if (file == null) {
			return false;
		}
		String sFile = null;
		String extension = Utils.getExtension(file);
		if (extension == null) {
			sFile = file.getPath() + "." + "xsd";
		} else {
			sFile = file.getPath();
		}
		window.setXMLSchema(sFile);
		return true;
	}

	/**
	 * 
	 * @param lastPath_
	 * @return
	 */
	private File getFile(String lastPath, FileFilter fileFilter) {
		JFileChooser jfc = new JFileChooser(lastPath);
		jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
		jfc.addChoosableFileFilter(fileFilter);
		jfc.showSaveDialog((Component) PluginServices.getMainFrame());
		File f = jfc.getSelectedFile();
		if (f == null) {
			return null;
		}
		return f;
	}

	/**
	 * When the cancel button is clicked
	 */
	private void cancelButtonActionPerformed() {
		PluginServices.getMDIManager().closeWindow(window);
	}

	/**
	 * When the export button is clicked
	 * 
	 * @throws FileNotFoundException
	 * @throws GPEWriterHandlerCreationException
	 */
	private void exportButtonActionPerformed()
			throws WriterHandlerCreationException, FileNotFoundException {
		if (!isExportable(window.getSelectedFile(),
				window.getSelectedXMLSchema())) {
			return;
		}
		// Save the files that has been exported
		ArrayList exportedFiles = new ArrayList();
		// Closes the window
		cancelButtonActionPerformed();
		// Prepare the writerHandler to write
		GPEWriterHandler writer = cloneWriterHandler();
		FLayer[] actives = getActives();
		if (actives.length > 0) {
			lastPath = window.getSelectedFile();
			exportedFiles.add(lastPath);
			// It generates the XML schema
			if (window.isXMLSchemaCreated()) {
				createXMLSchema(actives[0]);
			}
			// Export the first selected layer
			exportLayer(writer, actives[0]);
			// Export the other layers
			for (int i = 1; i < actives.length; i++) {
				// Gets a new file...
				boolean isSelected = isNextFileSelected();
				if (!isSelected) {
					continue;
				}

				while (!isExportable(window.getSelectedFile(),
						window.getSelectedXMLSchema())
						&& (isSelected)) {
					isSelected = isNextFileSelected();
				}
				if (!isSelected) {
					continue;
				}
				lastPath = window.getSelectedFile();
				exportedFiles.add(lastPath);
				writer = cloneWriterHandler();
				// It generates the XML schema
				if (window.isXMLSchemaCreated()) {
					createXMLSchema(actives[i]);
				}
				exportLayer(writer, actives[i]);
			}
		}
	}

	/**
	 * Return true if the outputFile and the output schema are exportable
	 * 
	 * @param outputFile
	 *            The output file
	 * @param outputSchema
	 *            the output schema
	 * @return If it is exportable
	 */
	private boolean isExportable(String outputFile, String outputSchema) {
		// If the output file doesn't exist or the user
		// wants to override it continue
		if (!fileIsOk(window.getSelectedFile())) {
			return false;
		}
		// If the output schema doesn't exist or the user
		// wants to override it continue
		if (window.isXMLSchemaCreated()) {
			if (!fileIsOk(window.getSelectedXMLSchema())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Select a new output file for the output file and for the XML schema
	 * 
	 * @return true if the layer can be exported
	 */
	private boolean isNextFileSelected() {
		boolean selectedFile = fileButtonActionPerformed();
		if (!selectedFile) {
			return false;
		}
		// Gets a new Schema
		if (window.isXMLSchemaCreated()) {
			boolean selectedSchema = schemaButtonActionPerformed();
			if (!selectedSchema) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates an XML schema
	 * 
	 * @param the
	 *            layer to create the schema
	 */
	private void createXMLSchema(FLayer layer) {
		if (layer instanceof FLyrVect) {
			File schema = new File(window.getSelectedXMLSchema());
			try {
				GMLSchemaCreator schemaCreator = new GMLSchemaCreator(schema);
				SHPLayerDefinition lyrDef = new SHPLayerDefinition();
				SelectableDataSource sds = ((FLyrVect) layer).getRecordset();
				FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
				lyrDef.setFieldsDesc(fieldsDescrip);
				lyrDef.setShapeType(((FLyrVect) layer).getShapeType());
				schemaCreator.createFile(lyrDef);
				schemaCreator.writeFile();
			} catch (IOException e) {
				NotificationManager.addError(e);
			} catch (ReadDriverException e) {
				NotificationManager.addError(e);
			}
		}
	}

	/**
	 * Clones the selected writerHandler
	 * 
	 * @param writer
	 * @return
	 * @throws FileNotFoundException
	 * @throws GPEWriterHandlerCreationException
	 */
	private GPEWriterHandler cloneWriterHandler()
			throws WriterHandlerCreationException, FileNotFoundException {
		GPEWriterHandler writer = GPERegister.createWriter(window
				.getSelectedWriter().getName());
		writer.setOutputStream(new FileOutputStream(window.getSelectedFile()));
		writer.setErrorHandler(new FmapErrorHandler());
		return writer;
	}

	/**
	 * @return if the file can be written
	 */
	private boolean fileIsOk(String sFile) {
		File file = new File(sFile);
		// If the file exists...
		if (file.exists()) {
			int resp = JOptionPane.showConfirmDialog(
					(Component) PluginServices.getMainFrame(),
					PluginServices.getText(this,
							"fichero_ya_existe_seguro_desea_guardarlo")
							+ "\n"
							+ file.getAbsolutePath(), PluginServices.getText(
							this, "guardar"), JOptionPane.YES_NO_OPTION);
			if (resp != JOptionPane.YES_OPTION) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Export one layer
	 * 
	 * @param writer
	 *            The writerHandler
	 * @param layer
	 *            The layer to export
	 */
	private void exportLayer(GPEWriterHandler writer, FLayer layer) {
		ExportTask task = new ExportTask(layer, writer, getMapControl()
				.getMapContext(), new File(window.getSelectedFile()));
		PluginServices.cancelableBackgroundExecution(task);
	}

	/**
	 * @return the layer to export
	 */
	private FLayer[] getActives() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof IView) {
			return ((IView) window).getMapControl().getMapContext().getLayers()
					.getActives();
		}
		return null;
	}

	/**
	 * @return the current mapContext
	 */
	private MapControl getMapControl() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof IView) {
			return ((IView) window).getMapControl();
		}
		return null;
	}

	/**
	 * When the writer combo selection changes
	 */
	private void writerComboSelectionChange() {
		window.initializeSelection();
		GPEWriterHandler writer = window.getSelectedWriter();
		// String[] formats = writer.getFormat();
		// for (int i=0 ; i<formats.length ; i++){
		window.addFormat(writer.getFormat());
		// }
		window.setSelectedFormat(writer.getFormat());
		// String[] versions = writer.getVersions();
		// for (int i=0 ; i<versions.length ; i++){
		// window.addVersion(versions[i]);
		// }
		// window.setSelectedVersion(writer.getDefaultVersion());
	}

}
