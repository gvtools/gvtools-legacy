package org.gvsig.gui.beans.simplecombobox;

import javax.swing.JComboBox;

/**
 * <p>
 * Simple ComboBox component to add items with an associated code. Typical usage
 * would be adding Strings items and associated action codes. Example
 * </p>
 * 
 * <pre>
 * SimpleComboBox combo = new SimpleComboBox();
 * combo.addItem("Left", TextComponent.ALIGN_LEFT);
 * combo.addItem("Right", TextComponent.ALIGN_RIGHT;
 * combo.addItem("Center", TextComponent.ALIGN_CENTER);
 * combo.addItem("Justify", TextComponent.ALIGN_JUSTIFY);
 * . . .
 * int textAlign = combo.getSelectedCode();
 * textComponent.align(textAlign);
 * </pre>
 * <p>
 * This component provides a basic selection autocompletion when the user
 * presses a key (while the combo has the focus).
 * </p>
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es>
 * 
 */
public class SimpleComboBox extends JComboBox {
	private static final long serialVersionUID = 5805274299238455334L;

	public SimpleComboBox() {
		super();
	}

	/**
	 * <p>
	 * Invalid method, do not use it. Use {@link #addItem(Object, int)} instead.
	 * </p>
	 */
	public void addItem(Object anObject) {
		throw new RuntimeException(
				"Invalid method: use addItem(Object anObject, int code) instead or regular Swing JComboBox");
	}

	public void addItem(Object anObject, int code) {
		ComboItem item = new ComboItem(anObject, code);
		super.addItem(item);
	}

	public Object getItemAt(int index) {
		ComboItem item = (ComboItem) super.getItemAt(index);
		if (item != null) {
			return item.getObject();
		}
		return null;
	}

	public Object getSelectedItem() {
		ComboItem item = (ComboItem) super.getSelectedItem();
		if (item != null) {
			return item.getObject();
		}
		return null;
	}

	public void setSelectedIndex(int anIndex) {
		int size = dataModel.getSize();
		if (anIndex == -1) {
			setSelectedItem(null);
		} else if (anIndex < -1 || anIndex >= size) {
			throw new IllegalArgumentException("setSelectedIndex: " + anIndex
					+ " out of bounds");
		} else {
			Object item = dataModel.getElementAt(anIndex);
			super.setSelectedItem(item);
		}
	}

	public void setSelectedItem(Object anObject) {
		ComboItem item;
		for (int i = 0; i < getItemCount(); i++) {
			item = (ComboItem) super.getItemAt(i);
			if (item.getObject().equals(anObject)) {
				super.setSelectedItem(item);
			}
		}
	}

	public void setSelectedCode(int code) {
		ComboItem item;
		for (int i = 0; i < getItemCount(); i++) {
			item = (ComboItem) super.getItemAt(i);
			if (item.getCode() == code) {
				super.setSelectedItem(item);
			}
		}
	}

	public Object[] getSelectedObjects() {
		Object obj = getSelectedItem();
		if (obj != null) {
			return new Object[] { obj };
		} else {
			return new Object[0];
		}
	}

	/**
	 * Gets the code associated to the selected item
	 * 
	 * @return
	 */
	public int getSelectedCode() {
		ComboItem item = (ComboItem) super.getSelectedItem();
		if (item != null) {
			return item.getCode();
		}
		return -1;
	}

	private class ComboItem {
		private Object theObject;
		private int code;

		public ComboItem(Object object, int code) {
			this.theObject = object;
			this.code = code;
		}

		public Object getObject() {
			return theObject;
		}

		public int getCode() {
			return code;
		}

		public String toString() {
			return theObject.toString();
		}
	}
}