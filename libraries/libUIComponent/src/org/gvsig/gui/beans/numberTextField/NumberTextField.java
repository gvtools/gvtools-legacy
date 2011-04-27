package org.gvsig.gui.beans.numberTextField;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * <p>Provides a TextField component suitable for numbers. No error is
 * produced when other characters are introduced, but just numbers are kept
 * in the field after pressing enter or after focus is lost.</p>
 * 
 * <p>The component parses numbers according to the default Locale. For example
 * when es_ES locale is active, a comma (",") is expected to separate the
 * fractional part from the integer part, and when en_US is the default locale,
 * then a dot (".") is expected to separate them.</p>
 * 
 * <p>The format of the accepted numbers can be modified by using the
 *  {@link #getFormat()} method. For example, to get a TextField that accepts
 *  just integer numbes we would use:</p>
 *  <pre>
 *    NumberTextField field = new NumberTextField();
 *    field.getFormat().setParseIntegerOnly(true);</pre>
 *  
 *  <p>In order to get a TextField that accepts double values with a minimum
 *  of two fractional digits and a maximum of five, we would use:</p>
 *  <pre>
 *    NumberTextField field = new NumberTextField();
 *    field.getFormat().setMinimumFractionDigits(0);
 *    field.getFormat().setMaximumFractionDigits(5);
 *  </pre>
 *  
 *  <p>NumberTextField commits the value to the Field when the focus is lost,
 *  while standard JFormattedTextField just commits the value to the Field
 *  when the user presses ENTER.</p>
 */
public class NumberTextField extends JFormattedTextField
	implements FocusListener{
	private static final long serialVersionUID = 1045806433744300963L;
	private NumberFormat format;

	public NumberTextField() {
        super();
        format = NumberFormat.getInstance();
        setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(
                (NumberFormat)format)));
		initialize();
	}
	
	public NumberTextField(int columns) {
		this();
		setColumns(columns);
	}

	public NumberTextField(double value, int columns) {
		this();
		setColumns(columns);
		super.setValue(new Double(value));
	}

	public NumberTextField(int value, int columns) {
		this();
		setColumns(columns);
		super.setValue(new Integer(value));
	}

	/**
	 * <p>Gets the NumberFormat accepted by this text field. It can be
	 * used to change the format accepted by this text field.</p>
	 * 
	 * <p>Examples:</p>
	 * 
	 * <ul>
	 * <li>To accept numbers with 0 to 5 fraction digits (as 2, 2.232, 6.23211):
	 * <pre>
	 *   getFormat().setMinimumFractionDigits(0);
	 *   getFormat().setMaximumFractionDigits(5);</pre></li>
	 * <li>To accept numbers with exactly two fraction digits (as 76.23, 12.00):
	 * <pre>
	 *   getFormat().setMinimumFractionDigits(2);
	 *   getFormat().setMaximumFractionDigits(2);</pre></li>
	 * <li>To accept just integer numbers (as 23, 5542, 0):
	 * <pre>
	 *   getFormat().setParseIntegerOnly(true);</pre></li>
	 * <li>To enable digit grouping (enabled by default):
	 * <pre>
	 *   getFormat().setGroupingUsed(true);</pre></li></ul>
	 * <p>Note: digit grouping enabled means to display 2534313.23 as
	 * 2,534,313.23</p> 
	 * 
	 * @return
	 */
	public NumberFormat getFormat() {
		return format;
	}
	
	private void initialize() {
		addFocusListener(this);
		setHorizontalAlignment(JTextField.RIGHT);
	}
	
	/**
	 * <p>Sets the value of this TextField as a
	 * <code>double</code> primitive type.</p>
	 * 
	 */
	public void setValue(double value) {
		setValue(new Double(value));
	}

	/**
	 * <p>Sets the value of this TextField as an
	 * <code>int</code> primitive type.</p>
	 */
	public void setValue(int value) {
		setValue(new Integer(value));
	}

	/**
	 * <p>Sets the value of this TextField as a
	 * <code>long</code> primitive type.</p>
	 * 
	 */
	public void setValue(long value) {
		setValue(new Long(value));
	}

	/**
	 * <p>Gets the value of this TextField as a
	 * <code>double</code> primitive type.</p>
	 * 
	 * @return The value as a <code>double</code>
	 */
	public double getDoubleValue() {
		Object value = getValue();
		if (value instanceof Double) {
			return ((Double)value).doubleValue();
		}
		else if (value instanceof Long) {
			return ((Long)value).doubleValue();
		}
		else if (value instanceof Integer) {
			return ((Integer)value).doubleValue();
		}
		else
			return 0;
	}

	/**
	 * <p>Gets the value of this TextField as an
	 * <code>int</code> primitive type.</p>
	 * 
	 * @return The value as an <code>int</code>
	 */
	public int getIntValue() {
		Object value = getValue();
		if (value instanceof Long) {
			return ((Long)value).intValue();
		}
		else if (value instanceof Double) {
			return ((Double)value).intValue();
		}
		else if (value instanceof Integer) {
			return ((Integer)value).intValue();
		}
		else
			return 0;
	}

	/**
	 * <p>Gets the value of this TextField as a
	 * <code>long</code> primitive type.</p>
	 * 
	 * @return The value as a <code>long</code>
	 */
	public long getLongValue() {
		Object value = getValue();
		if (value instanceof Long) {
			return ((Long)value).longValue();
		}
		else if (value instanceof Double) {
			return ((Double)value).longValue();
		}
		else if (value instanceof Integer) {
			return ((Integer)value).longValue();
		}
		else
			return 0;
	}

	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		try {
			commitEdit();
		} catch (ParseException e1) {}
	}
}
