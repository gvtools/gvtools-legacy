package com.iver.cit.gvsig.fmap.edition;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.utiles.swing.threads.Cancellable;

public class BeforeFieldEditEvent extends EditionEvent {

	FieldDescription newField;
	int changeType;
	Cancellable cancel;
	public BeforeFieldEditEvent(IEditableSource source, FieldDescription field, int changeType, Cancellable cancel)
	{
		super(source, EditionEvent.FIELD_EDITION, EditionEvent.ALPHANUMERIC);
		this.newField = field;
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
	 * @return Returns the new field.
	 */
	public FieldDescription getNewField() {
		return newField;
	}

}
