package com.iver.cit.gvsig.fmap.edition.commands;

import java.io.IOException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.layers.FBitSet;

/**
 * Añade una geometría nueva al EditableFeatureSource
 */
public class AddRowCommand extends AbstractCommand {
	private IRow row;
	private int calculatedIndex;
	private EditableAdapter efs;
	private int sourceType = EditionEvent.GRAPHIC;

	public AddRowCommand(EditableAdapter ef, IRow row, int calculatedIndex,
			int sourceType) {
		super();
		efs = ef;
		this.calculatedIndex = calculatedIndex;
		this.row = row;
		this.sourceType = sourceType;
	}

	/**
	 * @throws IOException
	 * @throws DriverIOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#undo()
	 */
	public void undo() throws EditionCommandException {
		efs.undoAddRow(calculatedIndex, sourceType);
	}

	/**
	 * @throws IOException
	 * @throws DriverIOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#redo()
	 */
	public void redo() throws EditionCommandException {
		try {
			calculatedIndex = efs.doAddRow(row, sourceType);
			efs.setSelection(new FBitSet());
		} catch (ReadDriverException e) {
			throw new EditionCommandException(efs.getWriter().getName(), e);
		}
	}

	public String getType() {
		return "Add";
	}
}
