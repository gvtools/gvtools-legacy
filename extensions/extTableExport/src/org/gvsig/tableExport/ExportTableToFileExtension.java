package org.gvsig.tableExport;


import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Types;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.GenericFileFilter;


/**
 * Extensión que permite exportar una tabla a cualquier
 * driver de fichero que soporte escritura.
 *
 * @author jmvivo
 */
public class ExportTableToFileExtension extends Extension {
    /**
     * @see com.iver.andami.plugins.IExtension#initialize()
     */
    public void initialize() {
    }

    /**
     * @param actionCommand Debe de estar compuesto por tres parámentros
     *                      separados por '|' dónde el primero es el nombre
     *                      del driver, el segundo la extensión de los ficheros
     *                      y el tercero el nombre del tipo. Ejemplo:<br>
     *  "gdbms dbf driver|dbf|dbf file"
     *
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    public void execute(String actionCommand) {
    	IWindow v = PluginServices.getMDIManager().getActiveWindow();
    	Table table = (Table) v;

    	String[] params=actionCommand.split("[|]");

    	if (params.length != 3){
    		NotificationManager.showMessageError("Parametros no validos: "+actionCommand, null);
    		return;

    	}


    	try {
    		exportToFile(table,params[0],params[1],params[2]);
    	} catch (Exception e) {
    		NotificationManager.showMessageError("error_en_el_proceso", e);
    	}

    }

	public void exportToFile(Table table, String driverName,String fileExtension,String fileDescription) throws Exception{
		FileDriver driver = (FileDriver)LayerFactory.getDM().getDriver(driverName);
		if (driver == null){
			throw new Exception("Driver '"+driverName+"' Not Found");
		}
		File file = this.askForFileName(fileExtension,fileDescription);
		if (file == null){
			return;
		}
		exportToFile(table,driver,file);
	}

	public void exportToFile(Table table, FileDriver driver,String fileExtension,String fileDescription) throws Exception{
		File file = this.askForFileName(fileExtension,fileDescription);
		exportToFile(table,driver,file);
	}

	public void exportToFile(Table table, FileDriver driver,File file) throws Exception{
		SelectableDataSource source;
		ITableDefinition orgDef;
		try {
			source = table.getModel().getModelo().getRecordset();
			source.start();
			orgDef = table.getModel().getModelo().getTableDefinition();
		} catch (Exception e) {
			throw new Exception("Error preparando la fuente", e); // TODO intenacionalizacion??
		}


		try {
			if (!file.exists()){

				//Builds a valid empty dbf file. We need it for call to the method Driver.open
				driver.createSource(file.getAbsolutePath(),new String[] {"0"},new int[] {Types.INTEGER} );
				file.createNewFile();

			}

			driver.open(file);
		} catch (IOException e) {
			throw new Exception("Error preparando el fichero destino", e); // TODO intenacionalizacion??
		}

		IWriter writer = ((IWriteable)driver).getWriter();
		try {
			writer.initialize(orgDef);
			writer.preProcess();
			SourceIterator iter = new SourceIterator(source);
			IFeature feature;
			int i=0;
			while (iter.hasNext()){
				 feature = iter.nextFeature();

				 DefaultRowEdited edRow = new DefaultRowEdited(feature,
						 DefaultRowEdited.STATUS_ADDED, i);
				 writer.process(edRow);
				 i++;

			}
			writer.postProcess();


		} catch (Exception e) {
			throw new Exception("Error generando fichero", e); // TODO intenacionalizacion??

		}

		try {
			source.stop();
			driver.close();
		} catch (Exception e) {
			throw new Exception("Error cerrando ficheros", e); // TODO intenacionalizacion??
		}


	}

	private File askForFileName(String ext,String extDescription){
		JFileChooser jfc = new JFileChooser();
		if ( ext.equals ("dbf") ) {
					extDescription = PluginServices.getText(this,"Ficheros_dbf");
		}
		jfc.addChoosableFileFilter(new GenericFileFilter(ext,
				extDescription));
		if (jfc.showSaveDialog((Component) PluginServices.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
			File file=jfc.getSelectedFile();
			if (file==null){
				return null;
			}
			if (file.exists()){
				int resp = JOptionPane.showConfirmDialog(
						(Component) PluginServices.getMainFrame(),PluginServices.getText(this,"fichero_ya_existe_seguro_desea_guardarlo"),
						PluginServices.getText(this,"guardar"), JOptionPane.YES_NO_OPTION);
				if (resp != JOptionPane.YES_OPTION) {
					return null;
				}
			}
			String name = file.getAbsolutePath();
			if (!name.toLowerCase().endsWith("." +ext.toLowerCase())){
				file = new File(name + "."+ext);
			}
			return file;
		} else{
			return null;
		}

	}


    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    public boolean isEnabled() {
       return true;
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isVisible()
     */
    public boolean isVisible() {
        IWindow v = PluginServices.getMDIManager().getActiveWindow();

        if (v == null) {
            return false;
        } else if (v instanceof Table) {
            return true;
        } else {
            return false;
        }
    }

    private class SourceIterator implements Iterator{
    	private int index;
    	private long count;
    	private FBitSet selection = null;
    	private SelectableDataSource source;

    	public SourceIterator(SelectableDataSource source) throws DriverException, ReadDriverException{
    		this.source = source;
    		if (source.getSelection().cardinality()== 0){
    			this.selection = null;
    			this.index=0;
    			this.count = source.getRowCount();
    		} else{
    			this.selection = source.getSelection();
    			this.index=selection.nextSetBit(0);
    			this.count = selection.cardinality();
    		}

    	}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			if (this.selection == null)
				return this.index < this.count;
			return this.index >= 0;
		}

		public Object next() {
			try {
				return this.nextFeature();
			} catch (DriverException e) {
				throw new RuntimeException(e);
			} catch (ReadDriverException e) {
				throw new RuntimeException(e);
			}
		}

		public IFeature nextFeature() throws DriverException, ReadDriverException {

			Value[] values = this.source.getRow(this.index);
			IFeature feat = new DefaultFeature(null, values, "" + this.index);
			if (this.selection == null){
				this.index++;
			} else {
				this.index = this.selection.nextSetBit(this.index + 1);
			}
			return feat;

		}

		public long count(){
			return this.count;
		}

    }
}
