package com.iver.cit.gvsig.fmap.edition;

import java.io.IOException;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.CancelEditingLayerException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.layers.StopEditionLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public interface IEditableSource {
	/**
     * Método invocado cuando se comienza la edición, para poner en marcha las
     * estructuras de datos necesarias para la misma, notificar al servidor en
     * protocolos en los que sea necesario, ...
	 * @throws StartEditionLayerException
	 * @throws StartWriterVisitorException
     *
     * @throws EditionException Si no se logra poner la fuente de datos en
     *         edición
     */
    void startEdition(int sourceType) throws StartWriterVisitorException ;

    /**
     * Invocado cuando termina la edición. En función de la clase concreta que
     * implemente este método se generará el fichero con los resultados de la
     * edición, se realizará una transacción a la base de datos, etc.
     * @throws StopEditionLayerException
     * @throws StopWriterVisitorException
     *
     * @throws EditionException Si no se consiguen llevar a cabo las
     *         modificaciones
     */
   void stopEdition(IWriter writer, int sourceType) throws StopWriterVisitorException;

    /**
     * Cancela la edición sin escribir los cambios
     * @throws CancelEditingLayerException
     *
     * @throws IOException Si se produce un error
     */
    void cancelEdition(int sourceType) throws CancelEditingLayerException;

    /**
     * Si el índice se corresponde a una geometria de las originales de la capa
     * en edición y no ha sido modificada ni eliminada devuelve la geometria
     * original. Si ha sido modificada debera de buscar en el fichero de
     * expansión y si ha sido eliminada debera devolver null
     *
     * @param index Índice de la geometría.
     *
     * @return Geometría.
     * @throws ReadDriverException
     * @throws ExpansionFileReadException
     *
     * @throws IOException Si se produce un error con el fichero de expansión
     * @throws DriverIOException Si se produce un error accediendo a las
     *         geometrías originales
     */
    IRowEdited getRow(int index) throws ReadDriverException, ExpansionFileReadException;

    /**
     * Devuelve el número de geometrias que hay actualmente en edición.
     *
     * @return Número de geometrías.
     * @throws ReadDriverException
     *
     * @throws DriverIOException Si se produce un error accediendo a la capa
     * @throws DriverException
     */
    int getRowCount() throws ReadDriverException;

    /**
     * Añade una geometria al fichero de expansión y guarda la correspondencia
     * en una tabla asociada.
     *
     * @param g geometría a guardar.
     * @throws ValidateRowException
     * @throws ExpansionFileWriteException
     * @throws ReadDriverException
     *
     * @throws DriverIOException Si se produce un error accediendo a las
     *         geometrías originales
     * @throws IOException Si se produce un error con el fichero de expansión
     */
     int addRow(IRow row,String descrip, int sourceType) throws ValidateRowException, ReadDriverException, ExpansionFileWriteException;

    /**
     * Deshace la última acción realizada. Si no hay más acciones no realiza
     * ninguna acción
     * @throws EditionCommandException
     *
     * @throws DriverIOException Si se produce un error accediendo a las
     *         geometrías originales
     * @throws IOException Si se produce un error con el fichero de expansión
     */
    void undo() throws EditionCommandException;

    /**
     * Rehace la última acción deshecha. Si no hay más acciones no hace nada
     * @throws EditionCommandException
     *
     * @throws DriverIOException Si se produce un error accediendo a las
     *         filas originales
     */
    void redo() throws EditionCommandException;

    /**
     * Elimina una geometria. Si es una geometría original de la capa en
     * edición se marca como eliminada (haya sido modificada o no). Si es una
     * geometría añadida posteriormente se invalida en el fichero de
     * expansión, para que una futura compactación termine con ella.
     *
     * @param index Índice de la geometría que se quiere eliminar
     * @throws ExpansionFileReadException
     * @throws ReadDriverException
     *
     * @throws DriverIOException Si se produce un error accediendo a las
     *         geometrías originales
     * @throws IOException Si se produce un error con el fichero de expansión
     */
    void removeRow(int index,String descrip, int sourceType) throws ReadDriverException, ExpansionFileReadException;

    /**
     * Si se intenta modificar una geometría original de la capa en edición se
     * añade al fichero de expansión y se registra la posición en la que se
     * añadió. Si se intenta modificar una geometria que se encuentra en el
     * fichero de expansión (por ser nueva o original pero modificada) se
     * invoca el método modifyGeometry y se actualiza el índice de la
     * geometria en el fichero.
     *
     * @param index Índice de la geometría que se quiere eliminar
     * @param type TODO
     * @param g Geometría nueva
     * @throws ExpansionFileWriteException
     * @throws ValidateRowException
     * @throws ReadDriverException
     * @throws ExpansionFileReadException
     * @throws IOException Si se produce un error con el fichero de expansión
     * @throws DriverIOException Si se produce un error accediendo a las
     *         geometrías originales
     */
    int modifyRow(int index, IRow row,String descrip, int sourceType) throws ValidateRowException, ExpansionFileWriteException, ReadDriverException, ExpansionFileReadException;



    /**
     * Compacta el almacenamiento de las geometrías que están en edición. Tras
     * esta operación, el orden de las geometrías seguramente cambiará y toda
     * llamada a getGeometry devolverá una geometría distinta de null, ya que
     * las eliminadas son borradas definitivamente. Hay que tener especial
     * cuidado al invocar este método ya que cualquier tipo de asociación
     * entre geometrías y otro tipo de objetos (comandos de edición, snapping,
     * ...) que use el índice de la geometría se verá afectado por éste método
     */
    void compact();

    /**
     * Establece la imagen de las geometrías seleccionadas con el fin de que en
     * una edición interactiva se pueda obtener dicha imagen para simular el
     * copiado, rotado, etc
     *
     * @param i imagen
     */
    // void setSelectionImage(Image i);

    /**
     * Obtiene una imagen con las geometrías seleccionadas
     *
     * @return imagen
     */
    // Image getSelectionImage();

    /**
     * DOCUMENT ME!
     */
    void startComplexRow();

    void endComplexRow(String description);
    public int undoModifyRow(int geometryIndex,int previousExpansionFileIndex, int sourceType) throws EditionCommandException;
    public IRow doRemoveRow(int index, int sourceType) throws ReadDriverException, ExpansionFileReadException;
    public int doModifyRow(int index, IRow feat, int sourceType) throws ExpansionFileWriteException, ReadDriverException, ExpansionFileReadException;
    public int doAddRow(IRow feat, int sourceType) throws ReadDriverException, ExpansionFileWriteException;
    public void undoRemoveRow(int index, int sourceType) throws EditionCommandException;
    public void undoAddRow(int index, int sourceType) throws EditionCommandException;
    public SelectableDataSource getRecordset() throws ReadDriverException;
    public boolean isEditing();
    public FBitSet getSelection() throws ReadDriverException;
    public CommandRecord getCommandRecord();

	public void addEditionListener(IEditionListener listener);

	public void removeEditionListener(IEditionListener listener);

	public ITableDefinition getTableDefinition() throws ReadDriverException;

	public void validateRow(IRow row, int sourceType) throws ValidateRowException;

	/**
	 * Use it to add, remove or rename fields. If null, you cannot modifiy the
	 * table structure The changes will be applied when stopEditing() is called.
	 */
	public IFieldManager getFieldManager();

	public void saveEdits(IWriter writer, int sourceType) throws StopWriterVisitorException;

	/**
	 * @return
	 */
	public Driver getOriginalDriver();

	/**
	 * Please, use this if you need support for defaultValues.
	 * Don't user getRecordset().getFieldsDescription()!!.
	 * The reason is ResultSetMetadata has no information about
	 * defaultValues in fields.
	 * @return
	 */
	public FieldDescription[] getFieldsDescription();


}
