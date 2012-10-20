/*
 * @(#) DocumentFilterExt.java        12/11/2007
 *
 * Proyecto: Seguridad Vial - Carreteras
 *
 * Copyright (c) 2007 IVER T.I.
 * Lérida 20, 46009 Valencia, España
 * Todos los derechos reservados.
 *
 */
package org.gvsig.app.documents.table.summarize.utils;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * DocumentFilterExt extiende a DocumentFilter para limitar el nº máximo de
 * caracteres que se pueden introducir en un documento o para bloquear la
 * escritura en el componente. Es posible usar esta clase con todas aquellas que
 * usen un objeto Document como modelo (JTextField, JTextArea, etc.)
 */
class DocumentFilterExt extends DocumentFilter {
	private int maxLength = 0; // Indica la longitud máxima de caracteres
								// admitida. Si cero, no hay longitud máxima!
	private boolean locked = false; // Bloquea la edición en el documento
	private boolean upperCase = false; // Devuelve el texto en mayusculas

	public void setUpperCase(boolean upperCase) {
		this.upperCase = upperCase;
	}

	public boolean isUpperCase() {
		return this.upperCase;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public void insertString(FilterBypass fb, int offs, String str,
			AttributeSet a) throws BadLocationException {

		if (locked)
			return;
		String sTextoAInsertar = getTextoAInsertar(fb, offs, 0, str);
		if (sTextoAInsertar != null)
			super.insertString(fb, offs, sTextoAInsertar, a);

	}// public void insertString(FilterBypass fb, int offs, String str,
		// AttributeSet a) throws BadLocationException {

	public void replace(FilterBypass fb, int offs, int length, String str,
			AttributeSet a) throws BadLocationException {

		if (locked)
			return;
		String sTextoAInsertar = getTextoAInsertar(fb, offs, length, str);
		if (sTextoAInsertar != null)
			super.replace(fb, offs, length, sTextoAInsertar, a);

	}// public void replace(FilterBypass fb, int offs, int length, String str,
		// AttributeSet a)throws BadLocationException {

	public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {
		if (locked)
			return;
		super.remove(fb, offset, length);
	}

	/**
	 * Esta función se encarga de analizar el texto que se va a insertar en el
	 * documento. En caso de que sea más grande que el tamaño máximo permitido
	 * devuelve la parte "que cabe" hasta completar maxLength.
	 * <p>
	 * Nota: Si maxLength = 0, entonces no hay un límite establecido.
	 * 
	 * @param fb
	 *            : FilterBypass que puede ser usado para modificar el documento
	 * @param offs
	 *            : Lugar de inserción en el documento
	 * @param length
	 *            : Longitud del texto a eliminar del documento (se usa en
	 *            reemplazos)
	 * @param str
	 *            : Texto a insertar.
	 * @return String
	 */
	private String getTextoAInsertar(FilterBypass fb, int offs, int length,
			String str) {

		// Si upperCase == true, se convierte todo a mayusculas
		if (upperCase)
			str = str.toUpperCase();

		// Si no hay límite en el nº máximo de caracteres admitidos...
		if (maxLength <= 0)
			return str;

		// Si la cadena a insertar cabe entera en el documento...
		if ((fb.getDocument().getLength() + str.length() - length) <= maxLength) {
			return str;

			// Si la cadena a insertar no cabe entera en el documento...
		} else {

			int liNumChars = 0;
			liNumChars = maxLength - (fb.getDocument().getLength() - length);
			if (liNumChars > 0) {
				if (liNumChars > str.length())
					liNumChars = str.length();
				return str.substring(0, liNumChars);
			}
		} // if ((fb.getDocument().getLength() + str.length() - length) <=
			// maxLength) {

		return null;
	}// private String getTextoAInsertar(...

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		if (maxLength < 0)
			maxLength = 0;
		this.maxLength = maxLength;
	}

}// class DocumentFilterExt extends DocumentFilter {

