package com.iver.cit.gvsig.fmap.edition;

public class EditionEvent {
	public static int START_EDITION = 0;
	public static int STOP_EDITION = 1;
	public static int CANCEL_EDITION = 2;
	public static int ROW_EDITION = 3;
	public static int FIELD_EDITION = 4;

	public static int CHANGE_TYPE_ADD = 10;
	public static int CHANGE_TYPE_MODIFY = 11;
	public static int CHANGE_TYPE_DELETE = 12;

	public static int ALPHANUMERIC = 13;
	public static int GRAPHIC = 14;

	IEditableSource source; // Será un EditableAdapter (o sus subclases:
							// VectorialEditableAdapter
	// o VectorialEditableDBAdapter
	int sourceType;
	int changeType;

	/*
	 * public EditionEvent(IEditableSource source, int type) { this.source =
	 * source; this.changeType = type; this.sourceType=GRAPHIC; }
	 */
	public EditionEvent(IEditableSource source, int type, int sourceType) {
		this.source = source;
		this.changeType = type;
		this.sourceType = sourceType;
	}

	/**
	 * @return Returns the source.
	 */
	public IEditableSource getSource() {
		return source;
	}

	/**
	 * @return Returns the type.
	 */
	public int getChangeType() {
		return changeType;
	}

	public int getSourceType() {
		return sourceType;
	}

}
