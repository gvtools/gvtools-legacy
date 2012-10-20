/*
 * @(#) JTextFieldExt.java        12/11/2007
 *
 * Proyecto: Seguridad Vial - Carreteras
 *
 * Copyright (c) 2007 IVER T.I.
 * Lérida 20, 46009 Valencia, España
 * Todos los derechos reservados.
 *
 */
package org.gvsig.app.documents.table.summarize.utils;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

/**
 * JTextFieldExt es una clase que extiende JTextField para incorporar nuevas
 * características como son:
 * <p>
 * - La posibilidad de limitar el número máximo de caracteres a escribir
 * <p>
 * - La posibilidad de seleccionar el contenido al obtener el foco
 */
public class JTextFieldExt extends JTextField implements FocusListener,
		DocumentListener {

	private static final long serialVersionUID = 1L;

	private boolean selectTextOnFocusGained = true; // Indica si se debe
													// seleccionar el contenido
													// del jTextField cuando
													// este obtiene el foco.
	private DocumentFilterExt loDocumentFilterExt = null; // El control del
															// máximo número de
															// caracteres
															// admitidos, etc.
															// se realiza desde
															// este objeto
	// private boolean locked = false; //Atributo de loDocumentFilterExt
	// private int maxLength = 0; //Atributo de loDocumentFilterExt

	private Vector changeListeners; // Vector para guardar los objetos que
									// implementen el interfaz ChangeListener

	// Consturctor
	public JTextFieldExt() {
		super();

		// Asignar el DocumentFilter extendido para controlar el contenido del
		// JTextField
		loDocumentFilterExt = new DocumentFilterExt();
		((AbstractDocument) this.getDocument())
				.setDocumentFilter(loDocumentFilterExt);

		changeListeners = new Vector();
		getDocument().addDocumentListener(this);

		// Registrar la propia clase como FocusListener
		addFocusListener(this);
	}

	// Constructor con el número de caracteres máximo
	public JTextFieldExt(int maxLength) {
		this();
		setMaxLength(maxLength);
	}

	public void setUpperCase(boolean upperCase) {
		if (loDocumentFilterExt == null)
			return;
		loDocumentFilterExt.setUpperCase(upperCase);
	}

	public boolean isUpperCase() {
		if (loDocumentFilterExt == null)
			return false;
		return loDocumentFilterExt.isUpperCase();
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

	public void setText(String t) {
		boolean locked = isLocked();
		setLocked(false);

		super.setText(t);

		setLocked(locked);
	}

	// Atributos
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

	public boolean isSelectTextOnFocusGained() {
		return selectTextOnFocusGained;
	}

	public void setSelectTextOnFocusGained(boolean selectTextOnFocusGained) {
		this.selectTextOnFocusGained = selectTextOnFocusGained;
	}

	// Método de la interfaz FocusListener
	public void focusGained(FocusEvent e) {
		if (selectTextOnFocusGained)
			this.select(0, this.getText().length());
	}

	// Método de la interfaz FocusListener
	public void focusLost(FocusEvent e) {
	}

	public synchronized void addChangeListener(ChangeListener listener) {
		changeListeners.addElement(listener);
	}

	public synchronized void removeChangeListener(ChangeListener listener) {
		changeListeners.removeElement(listener);
	}

	private void notificarChangeEvent(ChangeEvent e) {
		if (changeListeners == null)
			return;

		// Crear una copia del vector de listener
		Vector v;
		synchronized (this) {
			v = (Vector) changeListeners.clone();
		}
		for (int i = 0; i < v.size(); i++) {
			ChangeListener listener = (ChangeListener) v.elementAt(i);
			listener.textChanged(e);
		}
		v = null;
	}// private void notificarChangeEvent()

	// Método de la interfaz DocumentListener
	public void changedUpdate(DocumentEvent e) {
		notificarChangeEvent(new ChangeEvent(this));
	}

	// Método de la interfaz DocumentListener
	public void insertUpdate(DocumentEvent e) {
		notificarChangeEvent(new ChangeEvent(this));
	}

	// Método de la interfaz DocumentListener
	public void removeUpdate(DocumentEvent e) {
		notificarChangeEvent(new ChangeEvent(this));
	}
} // Class JTextFieldExt