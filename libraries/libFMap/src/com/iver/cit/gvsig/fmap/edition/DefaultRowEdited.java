package com.iver.cit.gvsig.fmap.edition;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IRow;

public class DefaultRowEdited implements IRowEdited {

	private IRow row;
	private int status;
	private int index;

	/**
	 * Quizás conviene que RowEdited tenga un getRow y un getStatus.
	 * Así valdría también para los feature.
	 *
	 * @param baseRow
	 * @param status
	 * @param externalIndex
	 */
	public DefaultRowEdited(IRow baseRow, int status, int index)
	{
		this.status = status;
		row = baseRow;
		this.index = index;
	}
	public IRow getLinkedRow() {
		return row;
	}

	public int getStatus() {
		return status;
	}

	public String getID() {
		return row.getID();
	}

	public Value getAttribute(int fieldIndex) {
		return row.getAttribute(fieldIndex);
	}
	public Value[] getAttributes() {
		return row.getAttributes();
	}
	public IRow cloneRow() {
		DefaultRowEdited nr = new DefaultRowEdited(row.cloneRow(), status, index);
		return nr;
	}
	public int getIndex() {
		return index;
	}
	public void setID(String ID) {
		row.setID(ID);
	}
	public void setAttributes(Value[] att) {
		row.setAttributes(att);
	}

}
