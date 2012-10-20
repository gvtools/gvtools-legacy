package org.gvsig.gui.beans.numberTextField;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

/**
 * <p>
 * Provides a TextField component suitable for numbers. No error is produced
 * when other characters are introduced, but just numbers are kept in the field
 * after pressing enter or after focus is lost.
 * </p>
 * 
 * <p>
 * The component parses numbers according to the default Locale. For example
 * when es_ES locale is active, a comma (",") is expected to separate the
 * fractional part from the integer part, and when en_US is the default locale,
 * then a dot (".") is expected to separate them.
 * </p>
 * 
 * <p>
 * The format of the accepted numbers can be modified by using the
 * {@link #getFormat()} method. For example, to get a TextField that accepts
 * just integer numbes we would use:
 * </p>
 * 
 * <pre>
 * NumberTextField field = new NumberTextField();
 * field.getFormat().setParseIntegerOnly(true);
 * </pre>
 * 
 * <p>
 * In order to get a TextField that accepts double values with a minimum of two
 * fractional digits and a maximum of five, we would use:
 * </p>
 * 
 * <pre>
 * NumberTextField field = new NumberTextField();
 * field.getFormat().setMinimumFractionDigits(0);
 * field.getFormat().setMaximumFractionDigits(5);
 * </pre>
 * 
 * <p>
 * NumberTextField commits the value to the Field when the focus is lost, while
 * standard JFormattedTextField just commits the value to the Field when the
 * user presses ENTER.
 * </p>
 */
public class PositiveNumberField extends NumberTextField implements
		FocusListener {

	private static final long serialVersionUID = 1289959692215502516L;

	public PositiveNumberField() {
		super();
		initialize();
	}

	public PositiveNumberField(int columns) {
		super(columns);
		initialize();
	}

	public PositiveNumberField(double value, int columns) {
		super(value, columns);
		initialize();
	}

	public PositiveNumberField(int value, int columns) {
		super(value, columns);
		initialize();
	}

	private void initialize() {
		addFocusListener(this);
		getFormat().setParseIntegerOnly(true);
		getFormat().setGroupingUsed(false);
	}

	public void focusLost(FocusEvent e) {
		try {
			commitEdit();
		} catch (ParseException e1) {
		}
		if (getIntValue() < 0) {
			setValue(-getIntValue());
		}
	}
}
