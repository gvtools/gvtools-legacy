package com.iver.cit.gvsig.fmap.edition.commands;

import java.io.IOException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;

public class RemoveRowCommand extends AbstractCommand {

	private EditableAdapter efs;
	private int index;
	private int sourceType = EditionEvent.GRAPHIC;

	public RemoveRowCommand(EditableAdapter ef, int i, int sourceType) {
		super();
		efs = ef;
		index = i;
		this.sourceType = sourceType;
	}

	/**
	 * @throws DriverIOException
	 * @throws IOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#undo()
	 */
	public void undo() throws EditionCommandException {
		efs.undoRemoveRow(index, sourceType);
	}

	/**
	 * @throws IOException
	 * @throws DriverIOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#redo()
	 */
	public void redo() throws EditionCommandException {
		try {
			efs.doRemoveRow(index, sourceType);
		} catch (ReadDriverException e) {
			throw new EditionCommandException(efs.getWriter().getName(), e);
		}
	}

	public String getType() {
		return "Remove";
	}
}
