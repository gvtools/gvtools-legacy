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
*/


package com.iver.cit.gvsig.project.documents.table;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Types;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.project.documents.table.gui.CSVSeparatorOptionsPanel;
import com.iver.cit.gvsig.project.documents.table.gui.Statistics.MyObjectStatistics;


/**
 * Class to create dbf and csv files at disk with the statistics group generated from a table. 
 * 
 * dbf -> Data Base File
 * csv -> Comma Separated Value
 * 
 * @author Ángel Fraile Griñán  e-mail: angel.fraile@iver.es
 * 
 */


public class ExportStatisticsFile {

	
	private String lastPath = null;
	private Hashtable<String, MyFileFilter> dbfExtensionsSupported; // Supported extensions.
	private Hashtable<String, MyFileFilter> csvExtensionsSupported;
	
	private static Logger logger = Logger.getLogger(ExportStatisticsFile.class.getName());

	
	public ExportStatisticsFile(List<MyObjectStatistics> valores) {
		
        	JFileChooser jfc = new JFileChooser(lastPath);
			jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
			
			// Adding required extensions (dbf, csv)
			dbfExtensionsSupported = new Hashtable<String, MyFileFilter>();
			csvExtensionsSupported = new Hashtable<String, MyFileFilter>();
			dbfExtensionsSupported.put("dbf", new MyFileFilter("dbf",PluginServices.getText(this, "Ficheros_dbf"), "dbf"));
			csvExtensionsSupported.put("csv", new MyFileFilter("csv",PluginServices.getText(this, "Ficheros_csv"), "csv"));

			Iterator<MyFileFilter> iter = csvExtensionsSupported.values().iterator();
			while (iter.hasNext()) {
				jfc.addChoosableFileFilter(iter.next());
			}

			iter = dbfExtensionsSupported.values().iterator();
			while (iter.hasNext()) {
				jfc.addChoosableFileFilter(iter.next());
			}
			
			// Opening a JFileCooser
			if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
					File endFile = jfc.getSelectedFile();
						if (endFile.exists()){// File exists in the directory.
							int resp = JOptionPane.showConfirmDialog(
									(Component) PluginServices.getMainFrame(),
									PluginServices.getText(this,
											"fichero_ya_existe_seguro_desea_guardarlo")+"\n"+endFile.getAbsolutePath(),
									PluginServices.getText(this,"guardar"), JOptionPane.YES_NO_OPTION);// Informing the user
							if (resp != JOptionPane.YES_OPTION) {//cancel pressed.
								return;
							}
						}//end if exits.
						MyFileFilter filter = (MyFileFilter)jfc.getFileFilter();// dbf, csv
						endFile = filter.normalizeExtension(endFile);//"name" + "." + "dbf", "name" + "." + "csv"
						
						if(filter.getExtensionOfAFile(endFile).toLowerCase().compareTo("csv") == 0) { // csv file
							exportToCSVFile(valores,endFile); // export to csv format
						}
						else if(filter.getExtensionOfAFile(endFile).toLowerCase().compareTo("dbf") == 0) {// dbf file
							exportToDBFFile(valores,endFile); // export to dbf format
						}
			}//end if aprove option.
	}
	
	/**
	 * Creating  cvs format file with the statistics.
	 * Option to select the two columns separator.
	 * 
	 * Example with semicolon: Name;data\n
	 * 					   Name2;data2\n
	 * 
	 * @param valores
	 * 			- Pairs: String name (key) + Double value (
	 * @param endFile
	 * 			- File to write the information
	 */
	
	private void exportToCSVFile(List<MyObjectStatistics> valores, File endFile) {
	
		try {
			CSVSeparatorOptionsPanel csvSeparatorOptions = new CSVSeparatorOptionsPanel();
			PluginServices.getMDIManager().addWindow(csvSeparatorOptions);
			
			String separator = csvSeparatorOptions.getSeparator();
			
			if(separator != null) {
				
				FileWriter fileCSV = new FileWriter(endFile);
				
				fileCSV.write(PluginServices.getText(this, "Nombre") + separator + PluginServices.getText(this, "Valor")+ "\n");
				
				Iterator<MyObjectStatistics> iterador = valores.listIterator();
				
				while (iterador.hasNext()) {// Writing value,value\n
					 MyObjectStatistics data= (MyObjectStatistics) iterador.next();
					 fileCSV.write(data.getKey() + separator + (data.getValue())+ "\n");
				}
				fileCSV.close();
				JOptionPane.showMessageDialog(null, PluginServices.getText(this, "fichero_creado_en") + " " + endFile.getAbsolutePath(),
						PluginServices.getText(this, "fichero_creado_en_formato")+ " csv "+
						PluginServices.getText(this, "mediante_el_separador")+ 
						" \""+ separator + "\"", JOptionPane.INFORMATION_MESSAGE);// Informing the user
			}
			else
				return;
				
		} catch (IOException e) {// Informing the user
			logger.error("Error exportando a formato csv");
			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "Error_exportando_las_estadisticas") + " " + endFile.getAbsolutePath(),
					PluginServices.getText(this, "Error"), JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * Creating dbf format file with the statistics
	 * @param valores
	 * 			- Pairs String name (key) + Double value
	 * @param endFile
	 * 			- File to write the information
	 */
	private void exportToDBFFile(List<MyObjectStatistics> valores, File endFile) {
		
		try {
			FileDriver driver = null;
			try {
				driver = (FileDriver) LayerFactory.getDM().getDriver("gdbms dbf driver");
			} catch (DriverLoadException e1) {
				logger.error("Error Creando el driver dbf");
			} 
	
			try {
				if (!endFile.exists()){
					try {
						driver.createSource(endFile.getAbsolutePath(),new String[] {"0"},new int[] {Types.DOUBLE} );
					} catch (ReadDriverException e) {
						logger.error("Error en createSource");
					}
					endFile.createNewFile();
				}
	
				try {
					driver.open(endFile);
				} catch (OpenDriverException e) {
					logger.error("Error abriendo el fichero de destino");
				}
			} catch (IOException e) {
				try {
					throw new Exception("Error creando el fichero de destino", e);
				} catch (Exception e1) {
					logger.error("Error creando el fichero de destino");
				} 
			}
			
			IWriter writer = ((IWriteable)driver).getWriter();
			ITableDefinition orgDef = new TableDefinition();
			try {
				// Preparing the total rows in the new dbf file, in this case two rows : Name  Value
				FieldDescription[] fields = new FieldDescription[2];
				fields[0] = new FieldDescription();
				fields[0].setFieldType(Types.VARCHAR);
				fields[0].setFieldName(PluginServices.getText(this, "Nombre"));
				fields[0].setFieldLength(50);
				fields[1] = new FieldDescription();
				fields[1].setFieldType(Types.DOUBLE);
				fields[1].setFieldName(PluginServices.getText(this, "Valor"));
				fields[1].setFieldLength(50);
				fields[1].setFieldDecimalCount(25);
				fields[1].setFieldLength(100);
				orgDef.setFieldsDesc(fields);
				writer.initialize(orgDef);
			} catch (InitializeWriterException e) {
				logger.error("Error en la inicialización del writer");
			}
			try {
				writer.preProcess();
			} catch (StartWriterVisitorException e) {
				logger.error("Error en el preProcess del writer");
			}
			try {
				int index = 0;
				Value[] value = new Value[2];
				IFeature feat = null;

				Iterator<MyObjectStatistics> iterador = valores.listIterator();
				
				while (iterador.hasNext()) {
					MyObjectStatistics data= (MyObjectStatistics) iterador.next();
					value[0] = ValueFactory.createValue(data.getKey());
					value[1] = ValueFactory.createValue(data.getValue());
					feat = new DefaultFeature(null, value,"" + index++);
					write(writer, feat, index);
				}
			} catch (Exception e) {
				logger.error("Error en el write");
			}
			try {
				writer.postProcess();// Operation finished
				JOptionPane.showMessageDialog(null, PluginServices.getText(this, "fichero_creado_en") + " " + endFile.getAbsolutePath(),
						PluginServices.getText(this, "fichero_creado_en_formato")+ " dbf", JOptionPane.INFORMATION_MESSAGE);// Informing the user
			} catch (StopWriterVisitorException e) {
				logger.error("Error en el postProcess del writer");
			}
		} catch (Exception e) {// Informing the user
			logger.error("Error exportando a formato dbf");
			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "Error_exportando_las_estadisticas") + " " + endFile.getAbsolutePath(),
					PluginServices.getText(this, "Error"), JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * Writer to create dbf file
	 * 
	 * @param writer
	 * @param feature
	 * 				- fill with the corresponding rows
	 * @param index 
	 * 				- fill file position 
	 */
	private void write(IWriter writer, IFeature feature, int index) {
		DefaultRowEdited edRow = new DefaultRowEdited(feature,
				 DefaultRowEdited.STATUS_ADDED, index);
		 try {
			writer.process(edRow);
		} catch (VisitorException e) {
			logger.error("Error en la generación del fichero dbf");
		}
	}
}

/**
 * @author Ángel Fraile Griñán  e-mail: angel.fraile@iver.es
 * 
 * Class to work with the file extensions.
 */
class MyFileFilter extends FileFilter {

	private String[] extensiones = new String[1];
	private String description;
	private boolean dirs = true;
	private String info = null;

	public MyFileFilter(String[] ext, String desc) {
		extensiones = ext;
		description = desc;
	}

	public MyFileFilter(String[] ext, String desc, String info) {
		extensiones = ext;
		description = desc;
		this.info = info;
	}

	public MyFileFilter(String ext, String desc) {
		extensiones[0] = ext;
		description = desc;
	}

	public MyFileFilter(String ext, String desc, String info) {
		extensiones[0] = ext;
		description = desc;
		this.info = info;
	}

	public MyFileFilter(String ext, String desc, boolean dirs) {
		extensiones[0] = ext;
		description = desc;
		this.dirs = dirs;
	}

	public MyFileFilter(String ext, String desc, boolean dirs, String info) {
		extensiones[0] = ext;
		description = desc;
		this.dirs = dirs;
		this.info = info;
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			if (dirs) {
				return true;
			} else {
				return false;
			}
		}
		for (int i = 0; i < extensiones.length; i++) {
			if (extensiones[i].equals("")) {
				continue;
			}
			if (getExtensionOfAFile(f).equalsIgnoreCase(extensiones[i])) {
				return true;
			}
		}
		return false;
	}

	public String getDescription() {
		return description;
	}

	public String[] getExtensions() {
		return extensiones;
	}

	public boolean isDirectory() {
		return dirs;
	}

	public String getExtensionOfAFile(File file) {
		String name;
		int dotPos;
		name = file.getName();
		dotPos = name.lastIndexOf(".");
		if (dotPos < 1) {
			return "";
		}
		return name.substring(dotPos + 1);
	}

	public File normalizeExtension(File file) {
		String ext = getExtensionOfAFile(file);
		if (ext.equals("") || !(this.accept(file))) {
			return new File(file.getAbsolutePath() + "." + extensiones[0]);
		}
		return file;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}