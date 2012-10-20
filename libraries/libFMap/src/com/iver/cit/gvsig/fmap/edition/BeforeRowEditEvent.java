package com.iver.cit.gvsig.fmap.edition;

import com.iver.utiles.swing.threads.Cancellable;

public class BeforeRowEditEvent extends EditionEvent {

	String newFID;
	int changeType;
	Cancellable cancel;

	public BeforeRowEditEvent(IEditableSource source, String newFID,
			int changeType, Cancellable cancel, int sourceType) {
		super(source, EditionEvent.ROW_EDITION, sourceType);
		this.newFID = newFID;
		this.changeType = changeType;
		this.cancel = cancel;
	}

	/**
	 * @return Returns the cancel.
	 */
	public Cancellable getCancel() {
		return cancel;
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
	public String getFID() {
		return newFID;
	}

}
