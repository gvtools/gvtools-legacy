/*
 * ChangeListener.java        02/01/2008
 *
 * Proyecto: Seguridad Vial - Carreteras
 *
 * Copyright (c) 2007 IVER T.I.
 * Lérida 20, 46009 Valencia, España
 * Todos los derechos reservados.
 *
 */
package org.gvsig.app.documents.table.summarize.utils;

import javax.swing.event.ChangeEvent;

/**
 * Interfaz que deben implementar todos aquellos objetos interseados en
 * detectar cuando ha cambiado el contenido de un JTextFieldExt, JNumericField o
 * JTextArea.
 * 
 * @author dance
 * @version 02/01/2008 dance
 */
public interface ChangeListener {
	public void textChanged(ChangeEvent e);
}
