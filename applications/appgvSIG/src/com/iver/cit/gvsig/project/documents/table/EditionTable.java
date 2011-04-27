package com.iver.cit.gvsig.project.documents.table;

import java.io.IOException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.table.CancelEditingTableException;
import com.iver.cit.gvsig.exceptions.table.StartEditingTableException;
import com.iver.cit.gvsig.exceptions.table.StopEditingTableException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public interface EditionTable {
    /**
     * DOCUMENT ME!
     * @throws StartEditingTableException
     * @throws EditionException
     */
    public void startEditing() throws StartEditingTableException;

    /**
     * DOCUMENT ME!
     */
    public void stopEditing() throws StopEditingTableException;

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     */
    public void hideColumns(int[] index);

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     */
    public void setUneditableColumns(int[] index);

    /**
     * DOCUMENT ME!
     *
     * @param numColumns DOCUMENT ME!
     * @param values DOCUMENT ME!
     */
    public void setDefaultValues(int[] numColumns, Value[] values);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Value getDefaultValue();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    // public int[] getSelectedColumns();

    /**
     * DOCUMENT ME!
     * @throws IOException
     * @throws CancelEditingTableException
     */
    public void cancelEditing() throws CancelEditingTableException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isEditing();

    /**
     * DOCUMENT ME!
     */
    public void refresh();
    public void addRow(IRow[] rows) throws ValidateRowException, ReadDriverException, ExpansionFileWriteException;
    public void copyRow() throws ReadDriverException, ExpansionFileReadException;
    public void cutRow() throws ReadDriverException, ExpansionFileReadException;
    public void removeRow() throws ReadDriverException, ExpansionFileReadException;
    public void addColumn(FieldDescription fld);
    public void removeColumn();
    public boolean isCopied();
    public void pasteRow() throws ValidateRowException, ReadDriverException, ExpansionFileWriteException;

}
