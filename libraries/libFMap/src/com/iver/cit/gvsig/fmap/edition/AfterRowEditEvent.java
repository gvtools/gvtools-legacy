package com.iver.cit.gvsig.fmap.edition;


public class AfterRowEditEvent extends EditionEvent {

	long numRow;
	int changeType;
	public AfterRowEditEvent(IEditableSource source, long numRow, int changeType,int sourceType)
	{
		super(source, EditionEvent.ROW_EDITION, sourceType);
		this.numRow = numRow;
		this.changeType = changeType;
	}
	/**
	 * @return Returns the changeType.
	 */
	public int getChangeType() {
		return changeType;
	}
	/**
	 * @return Returns the numRow.
	 */
	public long getNumRow() {
		return numRow;
	}

}
