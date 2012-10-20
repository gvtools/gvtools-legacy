package com.iver.cit.gvsig.fmap.edition.commands;

import java.io.IOException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;

public class ModifyRowCommand extends AbstractCommand {

	private EditableAdapter efs;
	private IRow rowNext;
	private int calculatedIndexAnt;
	private int previousIndexInExpansionFile;
	private int type = EditionEvent.GRAPHIC;

	/**
	 * @param ef
	 *            IEditableSource
	 * @param calculatedIndex
	 *            index of modified row
	 * @param p
	 *            previous index in expansion file. -1 if previous is from
	 *            original data source.
	 * @param newRow
	 * @throws IOException
	 * @throws DriverIOException
	 */
	public ModifyRowCommand(EditableAdapter ef, int calculatedIndex,
			int previousInExpansionFile, IRow newRow, int type) {
		super();
		efs = ef;
		calculatedIndexAnt = calculatedIndex;
		rowNext = newRow;
		previousIndexInExpansionFile = previousInExpansionFile;
		this.type = type;
	}

	/**
	 * @throws DriverIOException
	 * @throws IOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#undo()
	 */
	public void undo() throws EditionCommandException {
		previousIndexInExpansionFile = efs.undoModifyRow(calculatedIndexAnt,
				previousIndexInExpansionFile, type);
	}

	/**
	 * @throws DriverIOException
	 * @throws IOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#redo()
	 */
	public void redo() throws EditionCommandException {
		try {
			previousIndexInExpansionFile = efs.doModifyRow(calculatedIndexAnt,
					rowNext, type);
		} catch (ExpansionFileWriteException e) {
			throw new EditionCommandException(efs.getWriter().getName(), e);
		} catch (ReadDriverException e) {
			throw new EditionCommandException(efs.getWriter().getName(), e);
		}
	}

	public String getType() {
		return "Modify";
	}
}
