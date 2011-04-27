package org.gvsig.tableImport.importfields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.ImageIcon;

import org.gvsig.tableImport.importfields.ImportFieldParams.FielToImport;
import org.gvsig.tableImport.importfields.ui.ImportFieldPanel;
import org.gvsig.tableImport.importfields.ui.LinkDefinitionPanel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.EditionUtilities;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IEditableSource;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.gui.Table;


/**
 * Extensión que permite importar datos en una tabla.
 *
 * @author jmvivo
 */
public class ImportFieldsExtension extends Extension {

	public void initialize() {
		// TODO Auto-generated method stub

	}

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    public boolean isEnabled() {
        IWindow v = PluginServices.getMDIManager().getActiveWindow();
        if (!(v instanceof Table)) {
            return false;
        }
        IEditableSource ies=((Table)v).getModel().getModelo();
        if (!(ies instanceof IWriteable)){
        	return false;

        }
        IWriter writer =((IWriteable) ies).getWriter();
   	 	if (writer == null){
   	 		return false;
   	 	}
   	 	if (!writer.canAlterTable()){
   	 		return false;
   	 	}

        return true;

    }

    /**
     * @see com.iver.andami.plugins.IExtension#isVisible()
     */
    public boolean isVisible() {
        IWindow v = PluginServices.getMDIManager().getActiveWindow();

        if (v == null) {
            return false;
//        } else if (!(v instanceof Table) && !(v instanceof BaseView)) {
        } else if (!(v instanceof Table) ) {
            return false;
        }
        return true;

    }

	public void execute(String actionCommand) {
    	ImportFieldParams param = new ImportFieldParams();

    	IWindow v = PluginServices.getMDIManager().getActiveWindow();
    	if (v instanceof Table){
    		Table table = (Table) v;
    		param.setLockTable(true);
    		try {
				param.setTable(table.getModel());
			} catch (ReadDriverException e) {
				NotificationManager.showMessageError(PluginServices.getText(null, "Failed_filling_table"), e);
			}
    	}


		ImageIcon logo = new javax.swing.ImageIcon(this.getClass().getClassLoader().getResource("images/package_graphics.png"));

		WizardAndami wizard = new WizardAndami(logo);
		// Adds the wizard panels:

		LinkDefinitionPanel linkPanel = new LinkDefinitionPanel(wizard.getWizardComponents(),param);
		wizard.getWizardComponents().addWizardPanel(linkPanel);

		ImportFieldPanel fieldsPanel = new ImportFieldPanel(wizard.getWizardComponents(),param);
		wizard.getWizardComponents().addWizardPanel(fieldsPanel);

		wizard.getWizardComponents().getFinishButton().setEnabled(false);
		wizard.getWindowInfo().setWidth(640);
		wizard.getWindowInfo().setHeight(350);
		wizard.getWindowInfo().setTitle(PluginServices.getText(this, "import_fields"));

        wizard.getWizardComponents().setFinishAction(new ImportFieldsAction(wizard ,param));

		PluginServices.getMDIManager().addWindow(wizard);


	}

	public void doImportField(ImportFieldParams params) throws Exception{

		if (!params.isValid()){
			//TODO: ver que excepcion a lanzar
			throw new Exception("invalid Paramenters: "+ params.getValidationMsg());
		}


		IEditableSource edSource=null;
		IEditableSource edSourceToImport=params.getTableToImport().getModelo();

		SelectableDataSource rsSourceToImport=edSourceToImport.getRecordset();

		ArrayList fieldsToImport = new ArrayList();
		ArrayList fieldsToImport_des= new ArrayList();
		ArrayList fieldsToImport_pos= new ArrayList();
		Iterator iter;
		Map values;
		int i;

		try{
			rsSourceToImport.start();

			// Cargamos la lista con los campos que vamos a importar
			iter =params.getFieldsToImport().iterator();
			FielToImport fieldToImport;
			while (iter.hasNext()){
				fieldToImport = (FielToImport) iter.next();
				if (fieldToImport.toImport){
					fieldsToImport.add(fieldToImport);
				}
			}

			// Cargamos la lista de la definicio de capos desde la
			// tabla a importar
			iter = fieldsToImport.iterator();
			FieldDescription[] toImportAllFieldsDescription = edSourceToImport.getFieldsDescription();
			FieldDescription tmpFieldDesc, newFieldDesc;

			while (iter.hasNext()){
				fieldToImport = (FielToImport) iter.next();
				for (i=0;i<toImportAllFieldsDescription.length;i++){
					tmpFieldDesc =toImportAllFieldsDescription[i];
					if (tmpFieldDesc.getFieldName().equals(fieldToImport.originalFieldName)){
						newFieldDesc = tmpFieldDesc.cloneField();
						newFieldDesc.setFieldLength(tmpFieldDesc.getFieldLength());
						newFieldDesc.setFieldName(fieldToImport.fieldNameToUse);
						newFieldDesc.setDefaultValue(tmpFieldDesc.getDefaultValue());
						newFieldDesc.setFieldAlias(fieldToImport.fieldNameToUse);
						newFieldDesc.setFieldType(tmpFieldDesc.getFieldType());
						fieldsToImport_des.add(newFieldDesc);
						fieldsToImport_pos.add(new Integer(i));
					}
				}
			}

			//Cagamos los valores en un hash
			values= this.loadValuesFromSource(
					rsSourceToImport,
					params.getTableToImportField(),
					fieldsToImport_pos);
		} catch (Exception e){
			throw e;
		}finally{

			rsSourceToImport.stop();
			rsSourceToImport = null;
			edSourceToImport = null;
		}

		FLyrVect layer=null;


		boolean changeEditing = false;
		// Ponemos en edicion si no lo esta
		if (params.getTable().getAssociatedTable() instanceof FLyrVect){
			// Viene de una capa
			layer = (FLyrVect) params.getTable().getAssociatedTable();
			if (!layer.isEditing()){
				layer.setEditing(true);
				changeEditing=true;
			}
			edSource = (VectorialEditableAdapter)layer.getSource();
		} else {
			// es una tabla normal
			edSource = params.getTable().getModelo();
			if (!edSource.isEditing()){
				edSource.startEdition(EditionEvent.ALPHANUMERIC);
				changeEditing=true;
			}
		}

		edSource.startComplexRow();

		int originalFieldsCount = edSource.getRecordset().getFieldCount();
		int finalFieldsCount = originalFieldsCount + fieldsToImport.size();
		// Añadimos los campos
		iter = fieldsToImport_des.iterator();
		while (iter.hasNext()){
			((EditableAdapter)edSource).addField((FieldDescription) iter.next());
		}

		// Recorremos la fuente y vamos actualizando
		int rowCount = edSource.getRowCount();
		IRowEdited originalRow;
		IRow newRow;
		IRowEdited newRowEdited;
		Value[] finalValues;
		Value[] originalValues;
		Value[] valuesToUse;
		Value key;
		int column;
		int srcKeyPos = edSource.getRecordset().getFieldIndexByName(params.getTableField());
		for (i=0;i<rowCount;i++){
			originalRow = edSource.getRow(i);

			key = originalRow.getAttribute(srcKeyPos);
			valuesToUse = (Value[]) values.get(key);
			if (valuesToUse == null){
				continue;
			}
			newRow = originalRow.getLinkedRow().cloneRow();
			originalValues = newRow.getAttributes();
			finalValues = new Value[finalFieldsCount];
			System.arraycopy(originalValues, 0, finalValues, 0, originalFieldsCount);
			for (column = 0;column < valuesToUse.length;column++){
				finalValues[column+originalFieldsCount]= valuesToUse[column];
			}
			newRow.setAttributes(finalValues);
			newRowEdited = new DefaultRowEdited(newRow,
	    			 IRowEdited.STATUS_MODIFIED, i);
			edSource.modifyRow(newRowEdited.getIndex(), newRowEdited.getLinkedRow(), "",
					 EditionEvent.ALPHANUMERIC);

		}

		edSource.endComplexRow("Import fields");
		if (changeEditing){
			if (layer == null){
				IWriter writer= ((IWriteable)edSource).getWriter();
				writer.initialize(edSource.getTableDefinition());
				edSource.stopEdition(writer, EditionEvent.ALPHANUMERIC);
				edSource.getSelection().clear();


			} else{
				layer.setRecordset(edSource.getRecordset());
				ISpatialWriter spatialWriter = (ISpatialWriter) ((VectorialEditableAdapter)edSource).getWriter();
				ILayerDefinition lyrDef =EditionUtilities.createLayerDefinition(layer);
				spatialWriter.initialize(lyrDef);
				edSource.stopEdition(spatialWriter,EditionEvent.ALPHANUMERIC);
				layer.setEditing(false);
				edSource.getSelection().clear();
			}
		}





	}

	private Map loadValuesFromSource(SelectableDataSource source, String keyFieldName, ArrayList fieldsPositions) throws ReadDriverException{
		HashMap values= new HashMap();
		int row,i;
		Value[] rowValues;
		Value key;
		int keyPos = source.getFieldIndexByName(keyFieldName);
		long rowCount =source.getRowCount();
		for (row=0;row < rowCount;row++){
			key = source.getFieldValue(row, keyPos);
			if (values.containsKey(key)){
				continue;
			}
			rowValues = new Value[fieldsPositions.size()];

			for (i=0;i<fieldsPositions.size();i++){
				rowValues[i]=source.getFieldValue(row, ((Integer)fieldsPositions.get(i)).intValue());
			}

			values.put(key, rowValues);

		}

		return values;
	}

}
