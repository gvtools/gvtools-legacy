package com.iver.cit.gvsig.fmap.edition;

import java.io.IOException;
import java.rmi.server.UID;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.AlphanumericDBDriver;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.exceptions.expansionfile.CloseExpansionFileException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.CancelEditingLayerException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultRow;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.TableDefinition;
import com.iver.cit.gvsig.fmap.edition.commands.AddFieldCommand;
import com.iver.cit.gvsig.fmap.edition.commands.AddRowCommand;
import com.iver.cit.gvsig.fmap.edition.commands.Command;
import com.iver.cit.gvsig.fmap.edition.commands.CommandCollection;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;
import com.iver.cit.gvsig.fmap.edition.commands.MemoryCommandRecord;
import com.iver.cit.gvsig.fmap.edition.commands.ModifyRowCommand;
import com.iver.cit.gvsig.fmap.edition.commands.RemoveFieldCommand;
import com.iver.cit.gvsig.fmap.edition.commands.RemoveRowCommand;
import com.iver.cit.gvsig.fmap.edition.commands.RenameFieldCommand;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.AbstractFieldManager;
import com.iver.cit.gvsig.fmap.edition.rules.IRule;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.Cancel;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class EditableAdapter implements IEditableSource, IWriteable {
	private static Logger logger = Logger.getLogger(EditableAdapter.class.getName());

	protected boolean isEditing = false;

	private SelectableDataSource ds = null;

	protected FBitSet delRows = new FBitSet();

	private CommandRecord cr;

	protected IWriter writer;

	/**
	 * Flag que indica que hay que tomar las siguientes operaciones como una
	 * operaci�n at�mica
	 */
	private boolean complex = false;

	private CommandCollection commands = null;

	protected ArrayList listFields = new ArrayList();

	protected ArrayList listInternalFields = new ArrayList();

	protected boolean bFieldsHasBeenChanged = false;

	/**
	 * La clave ser� el fieldId. Para buscar si un value de una row ha de ser
	 * rellenado con defaultValue o con lo que venga del expansion file,
	 * miraremos si existe en este hash. Si existe, usamos el value del
	 * expansion file. Si no existe, usamos el defaultValue del campo busc�ndolo
	 * en la lista internalFields. Por cierto, en listInternalFields NO se
	 * borran campos. Solo se van a�adiendo nuevos actualFields.
	 */
	protected TreeMap actualFields; // la clave ser� el fieldId.

	protected ArrayList fastAccessFields = new ArrayList();

	protected class MyFieldManager extends AbstractFieldManager {

		public boolean alterTable() throws WriteDriverException {
			return getFieldManager().alterTable();
		}

		public void addField(FieldDescription fieldDesc) {
			super.addField(fieldDesc);
		}

		public FieldDescription removeField(String fieldName) {
			// TODO Auto-generated method stub
			return super.removeField(fieldName);
		}

		public void renameField(String antName, String newName) {
			// TODO Auto-generated method stub
			super.renameField(antName, newName);
		}

	}

	/*
	 * Establece una relaci�n entre los �ndices de las geometr�as en el
	 * EditableFeatureSource y los �ndices en el fichero de expansi�n FJP:
	 * CAMBIO: NECESITAMOS TRABAJAR CON FEATURE Y FEATUREITERATOR PARA IR
	 * PREPARANDO EL CAMINO, GUARDAMOS EL FEATUREID (STRING) COMO CLAVE, Y COMO
	 * VALOR, EL INDICE DENTRO DEL FICHERO DE EXPANSION (Integer). Lo de que
	 * FeatureId sea un String es por compatibilidad con OGC. Seg�n OGC, una
	 * Feature tiene que tener un Id string En el caso de los randomaccess,
	 * ser�n el id de registro En los casos de base de datos espaciales, supongo
	 * que siempre ser� num�rico tambi�n, pero lo tendremos que convertir a
	 * string. Lo que est� claro es que NO se puede confiar nunca en que sea
	 * algo correlativo (1, 2, 3, 4, 5, ... => FALSO!!)
	 */
	protected HashMap relations = new HashMap();

	/*
	 * Fichero en el que se guardan las nuevas geometr�as, producto de adiciones
	 * o de modificaciones
	 */
	protected ExpansionFile expansionFile;

	protected int numAdd = 0;

	private ObjectDriver editingDriver = new myObjectDriver();

	private SelectableDataSource ods;

	private ArrayList editionListeners = new ArrayList();

	private ArrayList rules = new ArrayList();

	protected int actualIndexFields;

	protected boolean isFullExtentDirty = false;

	private ArrayList fieldEvents=new ArrayList();
	private ArrayList rowEvents=new ArrayList();

	/**
	 * Crea un nuevo EditableAdapter.
	 */
	public EditableAdapter() {
		expansionFile = new MemoryExpansionFile(this);
		cr = new MemoryCommandRecord();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ds
	 *            DOCUMENT ME!
	 * @throws DriverException
	 */
	public void setOriginalDataSource(SelectableDataSource ds) throws ReadDriverException {
		this.ods = ds;
		initalizeFields(ds);
		Driver drv = ods.getDriver();
		if (drv instanceof IWriteable) {
			setWriter(((IWriteable) drv).getWriter());
		}


	}

	/**
	 * @param ds
	 * @throws ReadDriverException
	 * @throws DriverException
	 */
	private void initalizeFields(SelectableDataSource ds) throws ReadDriverException {
		FieldDescription[] fields = ds.getFieldsDescription();
		listInternalFields.clear();
		actualIndexFields = 0;
		actualFields = new TreeMap();
//		fastAccessFields = new ArrayList();
		for (int i=0; i < fields.length; i++)
		{
			fields[i].setFieldAlias(fields[i].getFieldName());
			InternalField field = new InternalField(fields[i], InternalField.ORIGINAL, new Integer(i));
			listFields.add(field);
			// field.setFieldIndex(i);
			actualFields.put(field.getFieldId(), field);
//			fastAccessFields.add(fields[i]);
			System.out.println("INITIALIZEFIELDS: FIELD " + field.getFieldDesc().getFieldAlias());
		}
			fieldsChanged();
			bFieldsHasBeenChanged = false;
	}

	private TreeMap deepCloneInternalFields(TreeMap col)
	{
		TreeMap clonedFields = new TreeMap();
		for (Iterator iter = col.values().iterator(); iter.hasNext();) {
			InternalField fld = (InternalField) iter.next();
			InternalField clonedField = fld.cloneInternalField();
			clonedFields.put(clonedField.getFieldId(), clonedField);
		}

		return clonedFields;
	}
	private void fieldsChanged() throws ReadDriverException {
		fastAccessFields= new ArrayList();
		int index = 0;
		for (Iterator iter = actualFields.values().iterator(); iter.hasNext();) {
			InternalField fld = (InternalField) iter.next();
			fastAccessFields.add(fld.getFieldDesc());
			fld.setFieldIndex(index++);
		}

		listInternalFields.add(deepCloneInternalFields(actualFields));
		actualIndexFields = listInternalFields.size()-1;
		ds = null;
		getRecordset().mapExternalFields();
		bFieldsHasBeenChanged = true;
	}

	/**
	 * DOCUMENT ME!
	 * @throws StartEditionLayerException
	 * @throws StartWriterVisitorException
	 *
	 * @throws EditionException
	 *             DOCUMENT ME!
	 */
	public void startEdition(int sourceType) throws StartWriterVisitorException {
		isEditing = true;

		fireStartEditionEvent(sourceType);
	}

	/**
	 * Se ejecuta preProcess() del IWriter, luego se itera por los registros
	 * borrados por si el IWriter los quiere borrar (solo ser� necesario cuando
	 * escribimos sobre la misma tabla) y luego se itera por los nuevos
	 * registros llamando a process con el registro correcto. (A�adidos,
	 * modificados). Para finalizar, se ejecuta PostProcess
	 *
	 * @param writer
	 *            IWriter que recibir� las llamadas.
	 *
	 * @throws EditionException
	 *             DOCUMENT ME!
	 *
	 */
	public void stopEdition(IWriter writer, int sourceType)
			throws StopWriterVisitorException {
		saveEdits(writer, sourceType);
		isEditing = false;
		cr.clearAll();
		fireStopEditionEvent(sourceType);
	}

	public void saveEdits(IWriter writer, int sourceType)
			throws StopWriterVisitorException {

		Throwable true_cause = null;
		// TODO: ARREGLAR ESTO PARA QUE CUANDO HA HABIDO CAMBIOS
		// EN LOS CAMPOS, PODAMOS CAMBIAR LO QUE TOQUE (A SER POSIBLE
		// SIN TENER QUE REESCRIBIR TODA LA TABLA CON POSTGIS)
		if (bFieldsHasBeenChanged)
		{
			// Para cada campo de los originales, miramos si no est� en
			// los actuales. Si no est�, le decimos al fieldManager
			// que lo borre. Si est�, pero le hemos cambiado el nombre
			// le pedimos al fieldManager que le cambie el nombre.
			// Luego recorremos los campos actuales para ver cuales
			// son nuevos, y los a�adimos.

			TreeMap ancientFields = (TreeMap) listInternalFields
					.get(0);
			Collection aux = ancientFields.values();
			Iterator it = aux.iterator();
			while (it.hasNext()) {
				InternalField fld = (InternalField) it.next();
				if (actualFields.containsKey(fld.getFieldId())) {
					// Es un original
					String f1 = fld.getFieldDesc().getFieldName();
					String f2 = ((InternalField)actualFields.get(fld.getFieldId())).getFieldDesc().getFieldAlias();
					if (f1.compareTo(f2) != 0){
						getFieldManager().renameField(f1, f2);
					}
				}
				else {	// No est�, hay que borrarlo
					getFieldManager().removeField(fld.getFieldDesc().getFieldAlias());
				}
			}
			Collection aux2= actualFields.values();
			Iterator it2 = aux2.iterator();
			while (it2.hasNext()) {
				InternalField fld = (InternalField) it2.next();
				if (!ancientFields.containsKey(fld.getFieldId())) {
					// Es uno a�adido
					getFieldManager().addField(fld.getFieldDesc());
				}
			}
		}
		
		ITableDefinition tab_def = null;
		
		try {
			tab_def = writer.getTableDefinition();
			writer.preProcess();
			// Procesamos primero los borrados.
			// Cuando se genere un tema nuevo, no se les debe hacer caso
			// a estos registros

			int rowCount = getRowCount();
			if (writer.isWriteAll()){
				for (int i = 0; i < rowCount; i++) {
					IRowEdited row=getRow(i);
//					IRowEdited rowEdited = new DefaultRowEdited(row.getLinkedRow()
//							.cloneRow(), row.getStatus(), i);
					if (row != null) {
						writer.process(row);
					}
				}
			} else {
				for (int i = delRows.nextSetBit(0); i >= 0; i = delRows
					.nextSetBit(i + 1)) {
					int calculatedIndex = i;
					Integer integer = new Integer(calculatedIndex);
					// Si no est� en el fichero de expansi�n, es de los originales
					// y hay que borrarlo
					DefaultRowEdited edRow = null;
					if (!relations.containsKey(integer)) {
						edRow = new DefaultRowEdited(new DefaultRow(ods
								.getRow(calculatedIndex)),
								IRowEdited.STATUS_DELETED, calculatedIndex);
						writer.process(edRow);
					}
				}
				// Escribimos solo aquellos registros que han cambiado
				for (int i = 0; i < rowCount; i++) {
					int calculatedIndex = getCalculatedIndex(i);
					Integer integer = new Integer(calculatedIndex);
					DefaultRowEdited edRow = null;
					// Si est� en el fichero de expansi�n hay que modificar
					if (relations.containsKey(integer)) {
						int num = ((Integer) relations.get(integer)).intValue();
						// ExpansionFile ya entrega el registro formateado como debe
						IRowEdited rowFromExpansion = expansionFile.getRow(num);
						// �Habr�a que hacer aqu� setID(index + "")?
						edRow = new DefaultRowEdited(rowFromExpansion.getLinkedRow()
								.cloneRow(), rowFromExpansion.getStatus(), i);
						writer.process(edRow);
					}
				}
			}
			writer.postProcess();
			writer.getTableDefinition().setFieldsDesc(getRecordset().getFieldsDescription());
			ods.reload();
			ds = null;
			clean();
		} catch (ReadDriverException e) {
			repairConnectionIfNeeded(tab_def);
			true_cause = e.getCause();
			throw new StopWriterVisitorException(writer.getName(), true_cause == null ? e : true_cause);
		} catch (StartWriterVisitorException e) {
			repairConnectionIfNeeded(tab_def);
			true_cause = e.getCause();
			throw new StopWriterVisitorException(writer.getName(), true_cause == null ? e : true_cause);
		} catch (VisitorException e) {
			repairConnectionIfNeeded(tab_def);
			true_cause = e.getCause();
			throw new StopWriterVisitorException(writer.getName(), true_cause == null ? e : true_cause);
		}

	}

	private void repairConnectionIfNeeded(ITableDefinition tdef) {
		
		if (tdef instanceof DBLayerDefinition) {
			DBLayerDefinition dbl = (DBLayerDefinition) tdef;
			if (dbl.getConnection() instanceof ConnectionJDBC) {
				ConnectionJDBC conn = (ConnectionJDBC) dbl.getConnection();
				try {
					conn.getConnection().rollback();
				} catch (SQLException e) {
					logger.error("Unable to rollback connection after write error: " + e.getMessage());
				}
			}
		}
		
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void cancelEdition(int sourceType) throws CancelEditingLayerException {
		isEditing = false;
		try {
			ds= null;
			clean();
			cr.clearAll();
		} catch (ReadDriverException e) {
			throw new CancelEditingLayerException(writer.getName(),e);
		}
		fireCancelEditionEvent(sourceType);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.IEditableSource#getRow(int)
	 */
	public IRowEdited getRow(int index) throws ReadDriverException, ExpansionFileReadException {
		int calculatedIndex = getCalculatedIndex(index);
		Integer integer = new Integer(calculatedIndex);
		DefaultRowEdited edRow = null;
		// Si no est� en el fichero de expansi�n
		if (!relations.containsKey(integer)) {
				/*
				 * edRow = new DefaultRowEdited(new
				 * DefaultRow(ods.getRow(calculatedIndex), "" + index),
				 * DefaultRowEdited.STATUS_ORIGINAL, index);
				 */
				DefaultRow auxR = new DefaultRow(ods.getRow(calculatedIndex));
				edRow = new DefaultRowEdited(auxR,
						IRowEdited.STATUS_ORIGINAL, index);

				return createExternalRow(edRow, 0);
//				edRow = new DefaultRowEdited(new DefaultRow(ods
//						.getRow(calculatedIndex)),
//						DefaultRowEdited.STATUS_ORIGINAL, index);
		}
		int num = ((Integer) relations.get(integer)).intValue();

		// return expansionFile.getRow(num);
		// ExpansionFile ya entrega el registro formateado como debe
		IRowEdited rowFromExpansion;
		rowFromExpansion = expansionFile.getRow(num);
		// �Habr�a que hacer aqu� setID(index + "")?
		edRow = new DefaultRowEdited(rowFromExpansion.getLinkedRow()
				.cloneRow(), rowFromExpansion.getStatus(), index);
		return edRow;



	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @throws ReadDriverException
	 *
	 * @throws DriverIOException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public int getRowCount() throws ReadDriverException {
			return (int) (ods.getRowCount() + numAdd) - delRows.cardinality();// -
			// expansionFile.getInvalidRows().cardinality();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.IEditableSource#addRow(com.iver.cit.gvsig.fmap.core.IRow,
	 *      java.lang.String)
	 */
	public int addRow(IRow row, String descrip, int sourceType) throws ValidateRowException, ReadDriverException, ExpansionFileWriteException{
		validateRow(row,sourceType);

		int calculatedIndex = doAddRow(row, sourceType);
		Command command = new AddRowCommand(this, row, calculatedIndex,
				sourceType);
		command.setDescription(descrip);
		if (complex) {
			commands.add(command);
		} else {
			cr.pushCommand(command);
		}

		return calculatedIndex;
	}

	/**
	 * DOCUMENT ME!
	 * @throws EditionCommandException
	 *
	 * @throws DriverIOException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void undo() throws EditionCommandException{
		// seleccion.clear();
		if (cr.moreUndoCommands()) {
			cr.undoCommand();
		}
	}

	/**
	 * DOCUMENT ME!
	 * @throws EditionCommandException
	 *
	 * @throws DriverIOException
	 *             DOCUMENT ME!
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void redo() throws EditionCommandException {
		// seleccion.clear();
		if (cr.moreRedoCommands()) {
			cr.redoCommand();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.IEditableSource#removeRow(int)
	 */
	public void removeRow(int index, String descrip, int sourceType) throws ReadDriverException, ExpansionFileReadException {

		int calculatedIndex = getCalculatedIndex(index);
		Command command = new RemoveRowCommand(this, calculatedIndex,
				sourceType);
		command.setDescription(descrip);
		if (complex) {
			commands.add(command);
		} else {
			cr.pushCommand(command);
		}
		doRemoveRow(calculatedIndex, sourceType);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.IEditableSource#modifyRow(int,
	 *      com.iver.cit.gvsig.fmap.core.IRow)
	 */
	public int modifyRow(int index, IRow row, String descrip, int sourceType) throws ValidateRowException, ExpansionFileWriteException, ReadDriverException, ExpansionFileReadException {
		validateRow(row,sourceType);
		int calculatedIndex = getCalculatedIndex(index);
		int pos = doModifyRow(calculatedIndex, row, sourceType);
		Command command = new ModifyRowCommand(this, calculatedIndex, pos, row,
				sourceType);
		command.setDescription(descrip);
		if (complex) {
			commands.add(command);
		} else {
			cr.pushCommand(command);
		}

		return pos;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void compact() {
		expansionFile.compact(relations);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void startComplexRow() {
		complex = true;
		commands = new CommandCollection();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @throws IOException
	 *             DOCUMENT ME!
	 * @throws DriverIOException
	 *             DOCUMENT ME!
	 */
	public void endComplexRow(String description) {
		commands.setDescription(description);
		cr.pushCommand(commands);
		complex = false;
		for (int j = 0; j < editionListeners.size(); j++) {
			for (int i = 0; i < fieldEvents.size(); i++) {
				IEditionListener listener = (IEditionListener) editionListeners
					.get(j);
				listener.afterFieldEditEvent((AfterFieldEditEvent)fieldEvents.get(i));
			}
			for (int i = 0; i < rowEvents.size(); i++) {
				IEditionListener listener = (IEditionListener) editionListeners
						.get(j);
				listener.afterRowEditEvent(null,(AfterRowEditEvent)rowEvents.get(i));
			}
		}
		fieldEvents.clear();
		rowEvents.clear();
	}

	/**
	 * Actualiza en el mapa de �ndices, la posici�n en la que estaba la
	 * geometr�a antes de ser modificada. Se marca como v�lida, en caso de que
	 * fuera una modificaci�n de una geometr�a que estuviese en el fichero de
	 * expansi�n antes de ser modificada y se pone el puntero de escritura del
	 * expansion file a justo despues de la penultima geometr�a
	 *
	 * @param geometryIndex
	 *            �ndice de la geometr�a que se quiere deshacer su modificaci�n
	 * @param previousExpansionFileIndex
	 *            �ndice que ten�a antes la geometr�a en el expansionFile. Si
	 *            vale -1 quiere decir que es una modificaci�n de una geometr�a
	 *            original y por tanto no hay que actualizar el mapa de indices
	 *            sino eliminar su entrada.
	 * @throws IOException
	 * @throws DriverIOException
	 */
	public int undoModifyRow(int geometryIndex,
			int previousExpansionFileIndex, int sourceType) throws EditionCommandException  {

		if (previousExpansionFileIndex == -1) {
			DefaultRowEdited edRow = null;
			try {
				edRow = new DefaultRowEdited(new DefaultRow(ods
							.getRow(geometryIndex)),
							IRowEdited.STATUS_ORIGINAL, geometryIndex);
			} catch (ReadDriverException e) {
				throw new EditionCommandException(writer.getName(),e);
			}
			boolean cancel=true;
			try {
				cancel = fireBeforeModifyRow(edRow, geometryIndex,
						sourceType);
			} catch (ReadDriverException e) {
				throw new EditionCommandException(writer.getName(),e);
			}
			if (cancel)
				return -1;
			// Se elimina de las relaciones y del fichero de expansi�n
			relations.remove(new Integer(geometryIndex));
			expansionFile.deleteLastRow();
		} else {
			boolean cancel=true;
			try {
				cancel = fireBeforeModifyRow(expansionFile
						.getRow(previousExpansionFileIndex), geometryIndex,
						sourceType);
			} catch (ExpansionFileReadException e) {
				throw new EditionCommandException(writer.getName(),e);
			} catch (ReadDriverException e) {
				throw new EditionCommandException(writer.getName(),e);
			}
			if (cancel)
				return -1;
			int numAnt=((Integer)relations.get(new Integer(geometryIndex))).intValue();
			// Se actualiza la relaci�n de �ndices
			relations.put(new Integer(geometryIndex), new Integer(
					previousExpansionFileIndex));
			return numAnt;
		}
		fireAfterModifyRow(geometryIndex, sourceType);
		return -1;
	}

	/**
	 * Elimina una geometria. Si es una geometr�a original de la capa en edici�n
	 * se marca como eliminada (haya sido modificada o no). Si es una geometr�a
	 * a�adida posteriormente se invalida en el fichero de expansi�n, para que
	 * una futura compactaci�n termine con ella.
	 *
	 * @param index
	 *            �ndice de la geometr�a.
	 * @throws ReadDriverException
	 * @throws ExpansionFileReadException
	 *
	 * @throws DriverIOException
	 * @throws IOException
	 */
	public IRow doRemoveRow(int index, int sourceType) throws ReadDriverException, ExpansionFileReadException{
		boolean cancel = fireBeforeRemoveRow(index, sourceType);
		if (cancel)
			return null;
		// Llega un calculatedIndex
		delRows.set(index, true);
		System.err.println("Elimina una Row en la posici�n: " + index);
		// TODO: Con tablas no es necesario devolver la anterior feature. Por
		// ahora.
		isFullExtentDirty = true;
		fireAfterRemoveRow(index, sourceType);
		return null;
	}

	/**
	 ** Si se intenta modificar una geometr�a original de la capa en edici�n se
	 * a�ade al fichero de expansi�n y se registra la posici�n en la que se
	 * a�adi�. Si se intenta modificar una geometria que se encuentra en el
	 * fichero de expansi�n, �sta puede estar ah� (en el ExpansionFile
	 * por haber sido a�adida o modificada. Si ha sido a�adida, entonces hay
	 * que respetar su estatus para que los writers puedan saber que es
	 * un registro NUEVO).
	 *
	 * @param index
	 *            DOCUMENT ME!
	 * @param feat
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @throws ExpansionFileWriteException
	 * @throws ReadDriverException
	 * @throws ExpansionFileReadException
	 *
	 */
	public int doModifyRow(int index, IRow feat, int sourceType) throws ExpansionFileWriteException, ReadDriverException, ExpansionFileReadException {
		boolean cancel = fireBeforeModifyRow(feat, index, sourceType);
		if (cancel)
			return -1;

		int pos = -1;
		Integer integer = new Integer(index);
//		System.err.println("Modifica una Row en la posici�n: " + index);
		// Si la geometr�a no ha sido modificada
		if (!relations.containsKey(integer)) {
			int expansionIndex = expansionFile.addRow(feat,
					IRowEdited.STATUS_MODIFIED, actualIndexFields);
			relations.put(integer, new Integer(expansionIndex));
		} else {
			// Obtenemos el �ndice en el fichero de expansi�n
			int num = ((Integer) relations.get(integer)).intValue();
			pos = num;

			/*
			 * Se modifica la geometr�a y nos guardamos el �ndice dentro del
			 * fichero de expansi�n en el que se encuentra la geometr�a
			 * modificada
			 */
			num = expansionFile.modifyRow(num, feat, actualIndexFields);

			/*
			 * Actualiza la relaci�n del �ndice de la geometr�a al �ndice en el
			 * fichero de expansi�n.
			 */
			relations.put(integer, new Integer(num));
		}
		isFullExtentDirty = true;
		fireAfterModifyRow(index, sourceType);
		return pos;
	}

	/**
	 * A�ade una geometria al fichero de expansi�n y guarda la correspondencia
	 * en la tabla relations.
	 *
	 * @param feat
	 *            geometr�a a guardar.
	 *
	 * @return calculatedIndex
	 * @throws ExpansionFileWriteException
	 * @throws DriverIOException
	 * @throws IOException
	 */
	public int doAddRow(IRow feat, int sourceType) throws ReadDriverException, ExpansionFileWriteException  {
		boolean cancel = fireBeforeRowAdded(sourceType,feat.getID());
		if (cancel)
			return -1;
		// A�ade la geometr�a
		// int virtualIndex = 0;
		int calculatedIndex = -1;

		calculatedIndex = (int) ods.getRowCount() + numAdd;

		int pos = expansionFile.addRow(feat, IRowEdited.STATUS_ADDED, actualIndexFields);
		relations.put(new Integer(calculatedIndex), new Integer(pos));
		numAdd++;
		System.err.println("A�ade una Row en la posici�n: " + calculatedIndex);
		isFullExtentDirty = true;
		fireAfterRowAdded(feat,calculatedIndex, sourceType);
		return calculatedIndex;
	}

	/**
	 * Se desmarca como invalidada en el fichero de expansion o como eliminada
	 * en el fichero original
	 *
	 * @param index
	 *            DOCUMENT ME!
	 */
	public void undoRemoveRow(int index, int sourceType) throws EditionCommandException {
		delRows.set(index, false);
		String fid;
		try {
			fid = getRow(index).getID();

		boolean cancel = fireBeforeRowAdded(sourceType,fid);
		if (cancel){
			delRows.set(index,true);
			return;
		}
		} catch (ExpansionFileReadException e) {
			throw new EditionCommandException(getOriginalDriver().getName(),e);
		} catch (ReadDriverException e) {
			throw new EditionCommandException(getOriginalDriver().getName(),e);
		}
		fireAfterRowAdded(null,index, sourceType);
	}

	/**
	 * Se elimina del final del fichero de expansi�n poniendo el puntero de
	 * escritura apuntando al final de la pen�ltima geometr�a. Deber� quitar la
	 * relaci�n del mapa de relaciones
	 *
	 * @param fmapSpatialIndex
	 *            �ndice de la geometr�a que se a�adi�
	 * @throws DriverIOException
	 * @throws IOException
	 */
	public void undoAddRow(int calculatedIndex, int sourceType)
			throws EditionCommandException {
		boolean cancel;
		try {
			cancel = fireBeforeRemoveRow(calculatedIndex, sourceType);
		} catch (ReadDriverException e) {
			throw new EditionCommandException(getOriginalDriver().getName(),e);
		}
		if (cancel)
			return;
		expansionFile.deleteLastRow();
		relations.remove(new Integer(calculatedIndex));
		numAdd--;
		fireAfterRemoveRow(calculatedIndex, sourceType);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.layers.VectorialAdapter#getRecordset()
	 */
	public SelectableDataSource getRecordset() throws ReadDriverException  {
		if (isEditing) {
			if (ds == null) {
				String name = LayerFactory.getDataSourceFactory()
						.addDataSource(editingDriver);

				try {

					ds = new SelectableDataSource(LayerFactory
							.getDataSourceFactory().createRandomDataSource(
									name, DataSourceFactory.AUTOMATIC_OPENING));
					ds.start();
					ds.setSelectionSupport(ods.getSelectionSupport());

				} catch (NoSuchTableException e) {
					throw new RuntimeException(e);
				} catch (DriverLoadException e) {
					throw new ReadDriverException(name,e);
				}
			}

			return ds;
		}
		return ods;
	}

	/**
	 * Return always the original recordset (even when is editing,
	 * nor the getRecorset() method)
	 *
	 * */
	public SelectableDataSource getOriginalRecordset(){
		return ods;
	}


	/**
	 * DOCUMENT ME!
	 *
	 * @return
	 * @throws ReadDriverException
	 */
	public FBitSet getSelection() throws ReadDriverException {
		/*
		 * try { return getRecordset().getSelection(); } catch
		 * (DriverLoadException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } return null;
		 */
		return getRecordset().getSelection();
	}

	public void setSelection(FBitSet selection) throws ReadDriverException {
		/*
		 * try { getRecordset().setSelection(selection); } catch
		 * (DriverLoadException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		getRecordset().setSelection(selection);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isEditing() {
		return isEditing;
	}

	public int getInversedIndex(long rowIndex) {
		int intervalNotDeleted = 0;
		int antDeleted = -1;
		int idPedido = (int) rowIndex;
		int numNotDeleted = 0;
		int numBorradosAnt = 0;

		for (int i = delRows.nextSetBit(0); i >= 0; i = delRows
				.nextSetBit(i + 1)) {
			intervalNotDeleted = i - antDeleted - 1;
			numNotDeleted += intervalNotDeleted;
			if (i > idPedido) {
				numNotDeleted = numNotDeleted + (i - idPedido);
				break;
			}
			numBorradosAnt++;
			antDeleted = i;
		}
		numNotDeleted = idPedido - numBorradosAnt;
		// System.out.println("Piden Viejo : "+ rowIndex + " y devuelvo como
		// nuevo " + (numNotDeleted));
		return numNotDeleted;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rowIndex
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getCalculatedIndex(long rowIndex) {
		int numNotDeleted = 0;
		int intervalNotDeleted = 0;
		int antDeleted = -1;
		int calculatedIndex;
		int idPedido = (int) rowIndex;
		int numBorradosAnt = 0;

		for (int i = delRows.nextSetBit(0); i >= 0; i = delRows
				.nextSetBit(i + 1)) {
			intervalNotDeleted = i - antDeleted - 1;
			numNotDeleted += intervalNotDeleted;
			if (numNotDeleted > idPedido) {
				break;
			}
			numBorradosAnt++;
			antDeleted = i;
		}
		calculatedIndex = numBorradosAnt + idPedido;
		// System.out.println("Piden Registro : "+ rowIndex + " y devuelvo el "
		// + (calculatedIndex));
		return calculatedIndex;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @author Vicente Caballero Navarro
	 */
	private class myObjectDriver implements ObjectDriver {
		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
		 */
		public int[] getPrimaryKeys() throws ReadDriverException {
			return ods.getPrimaryKeys();
			// int[] pk=new int[1];
			/*
			 * for (int i=0;i<getRowCount();i++){ pk[i]=i; }
			 */
			// pk[0]=1;
			// return pk;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
		 */
		public void write(DataWare dataWare) throws ReadDriverException, WriteDriverException {
			DataWare dataWareOrig = ods
					.getDataWare(DataSourceFactory.DATA_WARE_DIRECT_MODE);
			dataWareOrig.commitTrans();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
		 */
		public void setDataSourceFactory(DataSourceFactory dsf) {
			ods.setDataSourceFactory(dsf);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.driverManager.Driver#getName()
		 */
		public String getName() {
			return ods.getName();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
		 *      int)
		 */
		public Value getFieldValue(long rowIndex, int fieldId) throws ReadDriverException {
			// Si no est� en el fichero de expansi�n
			// Integer integer = new Integer(getCalculatedIndex(rowIndex));


			try {
				IRow row = getRow((int)rowIndex);
				return row.getAttribute(fieldId);
//				if (!relations.containsKey(integer)) {
//					return ods.getFieldValue(rowIndex, fieldId);
//				} else {
//					int num = ((Integer) relations.get(integer)).intValue();
//					DefaultRowEdited feat = (DefaultRowEdited) expansionFile
//							.getRow(num);
//
//					if (feat == null) {
//						return null;
//					}
//
//					return feat.getAttribute(fieldId);
//				}
//			} catch (DriverException e) {
//				e.printStackTrace();
//				throw new DriverException(e);
			} catch (ExpansionFileReadException e) {
				throw new ReadDriverException(getRecordset().getDriver().getName(),e);
			}

			/**
			 * try { if (!relations.containsKey(integer)) { // Si ha sido
			 * eliminada if (delRows.get(integer.intValue())) { return null; }
			 * else { return ods.getFieldValue(rowIndex, fieldId); }} else { int
			 * num = ((Integer) relations.get(integer)).intValue();
			 * DefaultRowEdited feat = (DefaultRowEdited)
			 * expansionFile.getRow(num); if (feat==null)return null; return
			 * feat.getAttribute(fieldId); }} catch (DriverException e) {
			 * e.printStackTrace(); throw new DriverException(e); } catch
			 * (IOException e) { e.printStackTrace(); throw new
			 * DriverException(e); }
			 */
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
		 */
		public int getFieldCount() throws ReadDriverException {
			return fastAccessFields.size();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
		 */
		public String getFieldName(int fieldId) throws ReadDriverException {
//			int i=0;
//			for (Iterator iter = actualFields.values().iterator(); iter.hasNext();) {
//				InternalField fld = (InternalField) iter.next();
//				if (i == fieldId)
//					return fld.getFieldDesc().getFieldAlias();
//				i++;
//
//			}
//			throw new DriverException("FieldId " + fieldId + " not found ");
			FieldDescription aux = (FieldDescription) fastAccessFields.get(fieldId);
			return aux.getFieldAlias();
			// return null;
			// return ods.getFieldName(fieldId);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
		 */
		public long getRowCount() throws ReadDriverException {
			return (int) (ods.getRowCount() + numAdd)
					- delRows.cardinality();// -
			// expansionFile.getInvalidRows().cardinality();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
		 */
		public int getFieldType(int fieldId) throws ReadDriverException {
//			int i=0;
//			for (Iterator iter = actualFields.values().iterator(); iter.hasNext();) {
//				InternalField fld = (InternalField) iter.next();
//				if (i == fieldId)
//					return fld.getFieldDesc().getFieldType();
//				i++;
//
//			}
			FieldDescription aux = (FieldDescription) fastAccessFields.get(fieldId);
			return aux.getFieldType();

//			return ods.getFieldType(i);
		}

		public int getFieldWidth(int fieldId) throws ReadDriverException {
//			int i=0;
//			for (Iterator iter = actualFields.values().iterator(); iter.hasNext();) {
//				InternalField fld = (InternalField) iter.next();
////				if (fld.getFieldIndex() == i)
////					return fld.getFieldDesc().getFieldLength();
//				if (i == fieldId)
//					return fld.getFieldDesc().getFieldLength();
//				i++;
//
//			}
//
//			return ods.getFieldWidth(i);
			FieldDescription aux = (FieldDescription) fastAccessFields.get(fieldId);
			return aux.getFieldLength();

		}

		public void reload() throws ReloadDriverException {
			ods.reload();

		}
	}

	public CommandRecord getCommandRecord() {
		return cr;
	}

	protected void fireAfterRemoveRow(int index, int sourceType) {
		AfterRowEditEvent event = new AfterRowEditEvent(this, index,
				EditionEvent.CHANGE_TYPE_DELETE, sourceType);
		if (complex){
			rowEvents.add(event);
			return;
		}
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.afterRowEditEvent(null, event);
		}

	}

	protected boolean fireBeforeRemoveRow(int index, int sourceType) throws ReadDriverException {
		Cancel cancel = new Cancel();
		String fid=null;
		IRow row=null;
		try {
			row=getRow(getInversedIndex(index));
			fid = row.getID();
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getOriginalDriver().getName(),e);
		}
		BeforeRowEditEvent event = new BeforeRowEditEvent(this, fid,
				EditionEvent.CHANGE_TYPE_DELETE, cancel, sourceType);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.beforeRowEditEvent(row, event);
			if (cancel.isCanceled())
				return true;
		}
		return false;
	}

	protected void fireAfterRowAdded(IRow feat,int calculatedIndex, int sourceType) {
		AfterRowEditEvent event = new AfterRowEditEvent(this, calculatedIndex,
				EditionEvent.CHANGE_TYPE_ADD, sourceType);
		if (complex){
			rowEvents.add(event);
			return;
		}
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.afterRowEditEvent(feat, event);
		}
	}

	protected void fireAfterFieldAdded(FieldDescription field) {
		AfterFieldEditEvent event = new AfterFieldEditEvent(this,field,
				EditionEvent.CHANGE_TYPE_ADD);
		if (complex) {
			fieldEvents.add(event);
			return;
		}
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.afterFieldEditEvent(event);

		}
	}

	protected void fireAfterFieldRemoved(FieldDescription field) {
		AfterFieldEditEvent event = new AfterFieldEditEvent(this,field,
				EditionEvent.CHANGE_TYPE_DELETE);
		if (complex) {
			fieldEvents.add(event);
			return;
		}
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.afterFieldEditEvent(event);
		}
	}

	protected void fireAfterFieldModified(FieldDescription field) {
		AfterFieldEditEvent event = new AfterFieldEditEvent(this,field,
				EditionEvent.CHANGE_TYPE_MODIFY);
		if (complex) {
			fieldEvents.add(event);
			return;
		}
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.afterFieldEditEvent(event);
		}
	}


	protected boolean fireBeforeRowAdded(int sourceType,String newFID) throws ReadDriverException{
		Cancel cancel = new Cancel();
		BeforeRowEditEvent event = new BeforeRowEditEvent(this, newFID,
				EditionEvent.CHANGE_TYPE_ADD, cancel, sourceType);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.beforeRowEditEvent(null, event);
			if (cancel.isCanceled())
				return true;
		}
		return false;
	}

	protected boolean fireBeforeFieldAdded(FieldDescription field) {
		Cancel cancel = new Cancel();
		BeforeFieldEditEvent event = new BeforeFieldEditEvent(this, field,
		EditionEvent.CHANGE_TYPE_ADD, cancel);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
			.get(i);
			listener.beforeFieldEditEvent(event);
			if (cancel.isCanceled())
				return true;
		}
		return false;
	}

	protected boolean fireBeforeRemoveField(FieldDescription field){
		Cancel cancel = new Cancel();
		BeforeFieldEditEvent event = new BeforeFieldEditEvent(this, field,
		EditionEvent.CHANGE_TYPE_DELETE, cancel);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
			.get(i);
			listener.beforeFieldEditEvent(event);
			if (cancel.isCanceled())
				return true;
		}
		return false;
	}


	protected boolean fireBeforeModifyRow(IRow feat, int index, int sourceType) throws ReadDriverException {
		Cancel cancel = new Cancel();
		String fid=null;
		try {
			fid = getRow(getInversedIndex(index)).getID();
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getOriginalDriver().getName(),e);
		}
		BeforeRowEditEvent event = new BeforeRowEditEvent(this, fid,
				EditionEvent.CHANGE_TYPE_MODIFY, cancel, sourceType);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.beforeRowEditEvent(feat, event);
			if (cancel.isCanceled())
				return true;
		}
		return false;
	}

	protected void fireAfterModifyRow(int index, int sourceType) {
		AfterRowEditEvent event = new AfterRowEditEvent(this, index,
				EditionEvent.CHANGE_TYPE_MODIFY, sourceType);
		if (complex){
			rowEvents.add(event);
			return;
		}
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.afterRowEditEvent(null, event);
		}

	}

	protected void fireStartEditionEvent(int sourceType) {
		EditionEvent ev = new EditionEvent(this, EditionEvent.START_EDITION,
				sourceType);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.processEvent(ev);
		}

	}

	protected void fireStopEditionEvent(int sourceType) {
		EditionEvent ev = new EditionEvent(this, EditionEvent.STOP_EDITION,
				sourceType);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.processEvent(ev);
		}

	}

	protected void fireCancelEditionEvent(int sourceType) {
		EditionEvent ev = new EditionEvent(this, EditionEvent.CANCEL_EDITION,
				sourceType);
		for (int i = 0; i < editionListeners.size(); i++) {
			IEditionListener listener = (IEditionListener) editionListeners
					.get(i);
			listener.processEvent(ev);
		}

	}

	public void addEditionListener(IEditionListener listener) {
		if (!editionListeners.contains(listener))
			editionListeners.add(listener);
	}

	public void removeEditionListener(IEditionListener listener) {
		editionListeners.remove(listener);
	}

	public IWriter getWriter() {
		return writer;
	}

	protected void setWriter(IWriter writer) {
		this.writer = writer;

	}
	/*
	 * azabala: esto funciona para todos los drivers gdbms
	 * salvo para MySQL, que necesita que el ITableDefinition
	 * contenga el nombre de la tabla (y por tanto requiere
	 * DBLayerDefinition-en realidad hace falta DBTableDefinition)
	 * TODO REVISAR LA ARQUITECTURA DE ESTO
	 *
	 * */
	public ITableDefinition getTableDefinition() throws ReadDriverException {
		Driver originalDriver = getOriginalDriver();
		if(! (originalDriver instanceof AlphanumericDBDriver)){
			TableDefinition tableDef = new TableDefinition();
			tableDef.setFieldsDesc(getRecordset().getFieldsDescription());
			tableDef.setName(getRecordset().getSourceInfo().name);
			return tableDef;
		}
		AlphanumericDBDriver dbDriver = (AlphanumericDBDriver)originalDriver;
		return dbDriver.getTableDefinition();


	}

	public void validateRow(IRow row,int sourceType) throws ValidateRowException  {
		for (int i = 0; i < rules.size(); i++) {
			IRule rule = (IRule) rules.get(i);
			boolean bAux = rule.validate(row,sourceType);
			if (bAux == false) {
				ValidateRowException ex = new ValidateRowException(writer.getName(),null);
				// TODO: Lanzar una RuleException con datos como el registro
				// que no cumple, la regla que no lo ha cumplido, etc.
				throw ex;
			}
		}
	}

	public ArrayList getRules() {
		return rules;
	}

	public void setRules(ArrayList rules) {
		this.rules = rules;
	}

	private void clean() throws ReadDriverException {
		try {
			expansionFile.close();
		} catch (CloseExpansionFileException e) {
			throw new ReadDriverException(getRecordset().getDriver().getName(),e);
		}
		relations.clear();
		numAdd = 0;
		delRows.clear();
		// TODO: Es muy probable que necesitemos un reload de los datasources, al
		// igual que lo tenemos en las capas. Por ahora, basta con retocar
		// listInternalFields, pero casi seguro que lo correcto ser�a hacer un
		// reload completo.
		initalizeFields(ods);

//		listInternalFields.clear();
//		listInternalFields.add(actualFields);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.IEditableSource#getFieldManager()
	 */
	public IFieldManager getFieldManager() {
		if (ods.getDriver() instanceof IWriteable)
		{
			IWriter writer = ((IWriteable)ods.getDriver()).getWriter();
			if ((writer != null) && (writer instanceof IFieldManager))
			{
				IFieldManager fldManager = (IFieldManager) writer;
				return fldManager;
			}
		}
		return null;
	}

	/**
	 * Tiene en cuenta los campos actuales para formatear una row con ellos. Le
	 * pasamos los campos que hab�a en el momento en que se cre� esa row.
	 *
	 * @param edRow
	 * @param indexInternalFields
	 * @return
	 */
	public IRowEdited createExternalRow(IRowEdited edRow,
			int indexInternalFields) {

		// para acelerar
		if (bFieldsHasBeenChanged == false)
			return edRow;

		Value[] att = edRow.getAttributes();
		TreeMap ancientFields = (TreeMap) listInternalFields
				.get(indexInternalFields);
		Value[] newAtt = new Value[actualFields.size()];
		Collection aux = actualFields.values();
		Iterator it = aux.iterator();
		int i = 0;
		Value val = null;
		while (it.hasNext()) {
			// Para cada campo de los actuales, miramos si ya estaba cuando
			// el registro estaba guardado.
			// Si estaba, cogemos el valor de ese campo en el registro
			// guardado. Si no estaba, ha sido a�adido despu�s y ponemos
			// su valor por defecto.
			// Nota importante: fieldIndex es el �ndice del campo cuando
			// se guard�. NO es el �ndice actual dentro de actualFields.
			// Se usa SOLO para recuperar el valor de los atributos
			// antiguos. Por eso no nos preocupamos de mantener actuallizados
			// el resto de campos cuando se borra o a�ade un nuevo campo.
			InternalField fld = (InternalField) it.next();
			// System.err.println("fld = " + fld.getFieldDesc().getFieldAlias() +  " id=" + fld.getFieldId());
			if (ancientFields.containsKey(fld.getFieldId())) {
				InternalField ancientField = (InternalField) ancientFields
						.get(fld.getFieldId());
				val = att[ancientField.getFieldIndex()];
				// val = att[ancientField.getFieldId().intValue()];
				// System.out.println("fld: " + fld.getFieldDesc().getFieldAlias() + " ancient:" + " val" + val);
			} else
				val = fld.getFieldDesc().getDefaultValue();
			newAtt[i++] = val;
		}
		IRowEdited newRow = (IRowEdited) edRow.cloneRow();
		newRow.setAttributes(newAtt);
		return newRow;
	}

	public void removeField(String fieldName) throws WriteDriverException, ReadDriverException {

		InternalField fld = findFieldByName(fieldName);
		if (fld == null)
			throw new WriteDriverException(getRecordset().getDriver().getName(),null);
		//throw new WriteDriverException("Field " + fieldName + " not found when removing field");
		Command command = new RemoveFieldCommand(this, fld);
		if (complex) {
			commands.add(command);
		} else {
			cr.pushCommand(command);
		}
		doRemoveField(fld);

	}

	private InternalField findFieldByName(String fieldName) {
		Collection aux = actualFields.values();
		Iterator it = aux.iterator();
		while (it.hasNext()) {
			InternalField fld = (InternalField) it.next();
			if (fld.getFieldDesc().getFieldAlias().compareToIgnoreCase(fieldName) == 0)
				return fld;
		}

		return null;
	}

	public void undoRemoveField(InternalField field) throws EditionCommandException {
		// field.setDeleted(false);
//		field.setFieldIndex(actualFields.size());
		actualFields.put(field.getFieldId(), field);
		try {
			fieldsChanged();
		} catch (ReadDriverException e) {
			throw new EditionCommandException(writer.getName(),e);
		}
		fireAfterFieldAdded(field.getFieldDesc());
	}

	public void doRemoveField(InternalField field) throws ReadDriverException {
		boolean cancel = fireBeforeRemoveField(field.getFieldDesc());
		if (cancel) return;
		actualFields.remove(field.getFieldId());
		fieldsChanged();
		fireAfterFieldRemoved(field.getFieldDesc());
	}

	public void renameField(String antName, String newName) throws ReadDriverException{

		InternalField fld = findFieldByName(antName);
		Command command = new RenameFieldCommand(this, fld, newName);
		if (complex) {
			commands.add(command);
		} else {
			cr.pushCommand(command);
		}
		doRenameField(fld, newName);

	}

	public void undoRenameField(InternalField field, String antName) throws EditionCommandException{
		field.getFieldDesc().setFieldAlias(antName);
		try {
			fieldsChanged();
		} catch (ReadDriverException e) {
			throw new EditionCommandException(writer.getName(),e);
		}
		fireAfterFieldModified(field.getFieldDesc());

	}

	public void doRenameField(InternalField field, String newName) throws ReadDriverException{
		field.getFieldDesc().setFieldAlias(newName);
		fieldsChanged();
		fireAfterFieldModified(field.getFieldDesc());

	}


	public void addField(FieldDescription field) throws ReadDriverException {

		InternalField fld = new InternalField(field, InternalField.ADDED, new Integer(listFields.size()));
		Command command = new AddFieldCommand(this, fld);
		if (complex) {
			commands.add(command);
		} else {
			cr.pushCommand(command);
		}
		listFields.add(fld);
		doAddField(fld);

	}

	public void undoAddField(InternalField field) throws EditionCommandException  {
		boolean cancel = fireBeforeRemoveField(field.getFieldDesc());
		if (cancel)
			return;

		// field.setDeleted(true);
		actualFields.remove(field.getFieldId());
		try {
			fieldsChanged();
		} catch (ReadDriverException e) {
			throw new EditionCommandException(writer.getName(),e);
		}
		fireAfterFieldRemoved(field.getFieldDesc());

	}

	public int doAddField(InternalField field) throws ReadDriverException {
		boolean cancel;
		cancel = fireBeforeFieldAdded(field.getFieldDesc());
		if (cancel)
			return -1;

		// field.setDeleted(false);
//		field.setFieldIndex(actualFields.size());
		actualFields.put(field.getFieldId(), field);
		fieldsChanged();
		fireAfterFieldAdded(field.getFieldDesc());
//		return field.getFieldIndex();
		return field.getFieldId().intValue();
	}

	public Driver getOriginalDriver()
	{
		return ods.getDriver();
	}

	/**
	 * Use it to be sure the recordset will have the right fields. It forces a new SelectableDataSource
	 * to be created next time it is needed
	 */
	public void cleanSelectableDatasource() {
		ds = null;
	}

	public FieldDescription[] getFieldsDescription() {
		return (FieldDescription[]) fastAccessFields.toArray(new FieldDescription[0]);
	}
	public String getNewFID() {
		return "fid-" + (new UID()).toString();
	}

//	private InternalField getInternalFieldByIndex(int fieldId)
//	{
//		for (Iterator iter = actualFields.values().iterator(); iter.hasNext();) {
//			InternalField fld = (InternalField) iter.next();
//			if (fld.getFieldIndex() == fieldId)
//				return fld;
//		}
//		return null;
//	}

}

// [eiel-gestion-excepciones]