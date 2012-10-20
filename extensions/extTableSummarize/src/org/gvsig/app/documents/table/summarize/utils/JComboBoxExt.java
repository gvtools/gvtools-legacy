package org.gvsig.app.documents.table.summarize.utils;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

/**
 * JComboBoxExt es una clase que extiende JComboBox para incorporar nuevas
 * características como lo posibilidad de bloquear la edición en el control por
 * parte del usuario.
 */
public class JComboBoxExt extends JComboBox {

	private static final long serialVersionUID = 1L;
	private DocumentFilterExt loDocumentFilterExt = null; // El control del
															// máximo número de
															// caracteres
															// admitidos, etc.
															// se realiza desde
															// este objeto

	// private boolean locked = false; //Atributo de loDocumentFilterExt
	// private int maxLength = 0; //Atributo de loDocumentFilterExt

	public JComboBoxExt() {
		super();

		Object c = getEditor().getEditorComponent();
		if (c instanceof JTextField) {
			loDocumentFilterExt = new DocumentFilterExt();
			((AbstractDocument) ((JTextField) c).getDocument())
					.setDocumentFilter(loDocumentFilterExt);
		}// if (c instanceof JTextField){

	}

	public void setLocked(boolean locked) {
		// this.locked = locked;
		if (loDocumentFilterExt == null)
			return;
		loDocumentFilterExt.setLocked(locked);
	}

	public boolean isLocked() {
		// return this.locked;
		if (loDocumentFilterExt == null)
			return false;
		return loDocumentFilterExt.isLocked();
	}

	public int getMaxLength() {
		if (loDocumentFilterExt == null)
			return 0;
		return loDocumentFilterExt.getMaxLength();
	}

	public void setMaxLength(int maxLength) {
		if (loDocumentFilterExt == null)
			return;
		loDocumentFilterExt.setMaxLength(maxLength);
	}

	public void setSelectedIndex(int anIndex) {
		setSelectedIndex(anIndex, false);
	}

	/*
	 * public void setSelectedItem(Object anObject) { setSelectedItem(anObject,
	 * false); }
	 */
	/**
	 * @param anIndex
	 * @param forceSelection
	 *            - Si True, se intenta seleccionar el elemento
	 *            independientemente de si el control está bloqueado o no
	 *            (locked)
	 */
	public void setSelectedIndex(int anIndex, boolean forceSelection) {
		if (isLocked() && !forceSelection)
			return;
		// OJO: setSelectedIndex llama internamente a super.setSelectedItem()
		// que a su vez llamará a setSelectedItem(anObject, false)!!!
		// Con este flujo, el valor de forceSelection en este método no se
		// propaga! Para solucionar esto no debemos sobrescribir
		// setSelectedItem. El boqueo sigue funcionando igual porque cuando
		// actúas desde el interfaz de usuario, para seleccionar un
		// elemento siempre se "entra" por setSelectedIndex.
		super.setSelectedIndex(anIndex);
	}
	/*
	 * public void setSelectedItem(Object anObject, boolean forceSelection) { if
	 * (locked && !forceSelection) return; super.setSelectedItem(anObject); }
	 */
}
