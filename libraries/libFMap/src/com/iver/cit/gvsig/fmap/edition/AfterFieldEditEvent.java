package com.iver.cit.gvsig.fmap.edition;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

public class AfterFieldEditEvent extends EditionEvent {

	FieldDescription fieldChanged;
	int changeType;
	public AfterFieldEditEvent(IEditableSource source, FieldDescription field, int changeType)
	{
		super(source, EditionEvent.FIELD_EDITION, EditionEvent.ALPHANUMERIC);
		this.fieldChanged = field;
		this.changeType = changeType;
	}
	/**
	 * @return Returns the changeType.
	 */
	public int getChangeType() {
		return changeType;
	}
	/**
	 * @return Returns the changed field.
	 */
	public FieldDescription getFieldChanged() {
		return fieldChanged;
	}

}
