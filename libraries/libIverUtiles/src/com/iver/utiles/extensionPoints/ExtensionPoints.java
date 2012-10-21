package com.iver.utiles.extensionPoints;

import java.util.TreeMap;

/**
 * Clase para registro de puntos de extension. <br>
 * <br>
 * 
 * @author jjdelcerro
 */
public class ExtensionPoints extends TreeMap<String, ExtensionPoint> {

	private static final long serialVersionUID = -798417910971607414L;

	/**
	 * Añade un punto de extension al registro de puntos de extension. <br>
	 * <br>
	 * Mediante este metodo puede añadir un punto de extension al registro de
	 * puntos de extension, llevandose detras todas las extensiones que esten
	 * registradas en el. <br>
	 * <br>
	 * En caso de que ya existiese un punto de extension con el nombre dado,
	 * añadira a este las extensiones del punto de extension suministrado. <br>
	 * <br>
	 * 
	 * @param value
	 *            Punto de extension a registrar
	 * @return
	 * 
	 */
	public Object put(ExtensionPoint value) {
		return put(value.getName(), value);
	}

	/**
	 * Añade un punto de extension al registro de puntos de extension. <br>
	 * <br>
	 * Mediante este metodo puede añadir un punto de extension al registro de
	 * puntos de extension, llevandose detras todas las extensiones que esten
	 * registradas en el. <br>
	 * <br>
	 * En caso de que ya existiese un punto de extension con el nombre dado,
	 * añadira a este las extensiones del punto de extension suministrado. <br>
	 * <br>
	 * Cuando se añade un punto de extension, es imprescindible que <i>key</i> y
	 * el nombre del punto de extension que se este añadiendo coincidan. <br>
	 * <br>
	 * 
	 * @param key
	 *            Nombre del punto de extension
	 * @param value
	 *            Punto de extension a registrar
	 * @return
	 * 
	 */
	public ExtensionPoint put(String key, ExtensionPoint value) {
		if (!value.getName().equals(key)) {
			throw new IllegalArgumentException();
		}
		ExtensionPoint n = super.get(key);
		if (n == null) {
			return super.put(key, value);
		}
		// Como estamos actualizando un punto de extension, añadimos a este las
		// extensiones del que nos acaban de suministrar.
		n.putAll(value);
		return value;
	}

	/**
	 * Registra una extension en un punto de extension. <br>
	 * <br>
	 * Mediante este metodo puede registrar sobre un punto de extension una
	 * extension. La extension esta identificada mediante un nombre unico, y una
	 * clase que se usara para manejar la extension o una clase que contruira el
	 * objeto que maneje la extension. <br>
	 * <br>
	 * Si ya existe en el punto de extension indicado por
	 * <i>extensionPointName</i> una extension con el nombre <i>name</i>, esta
	 * sera sustituida por la nueva extension. <br>
	 * 
	 * @param extensionPointName
	 *            Nombre del punto de extension
	 * @param name
	 *            Nombre o identificador de la extension
	 * @param data
	 *            Clase que implementa la extension o que la construye.
	 * 
	 * 
	 */
	public void add(String extensionPointName, String name, Object data) {
		ExtensionPoint extensionPoint = super.get(extensionPointName);
		if (extensionPoint == null) {
			extensionPoint = new ExtensionPoint(extensionPointName);
			super.put(extensionPoint.getName(), extensionPoint);
		}

		extensionPoint.put(name, data);
	}

	/**
	 * Registra una extension en un punto de extension. <br>
	 * <br>
	 * Mediante este metodo puede registrar sobre un punto de extension una
	 * extension. La extension esta identificada mediante un nombre unico, y una
	 * clase que se usara para manejar la extension o una clase que contruira el
	 * objeto que maneje la extension. <br>
	 * <br>
	 * Si ya existe en el punto de extension indicado por
	 * <i>extensionPointName</i> una extension con el nombre <i>name</i>, esta
	 * sera sustituida por la nueva extension. <br>
	 * 
	 * @param extensionPointName
	 *            Nombre del punto de extension
	 * @param name
	 *            Nombre o identificador de la extension
	 * @param description
	 *            descripcion de la extension.
	 * @param data
	 *            Clase que implementa la extension o que la construye.
	 * 
	 * 
	 */
	public void add(String extensionPointName, String name, String description,
			Object data) {
		ExtensionPoint extensionPoint = super.get(extensionPointName);
		if (extensionPoint == null) {
			extensionPoint = new ExtensionPoint(extensionPointName);
			super.put(extensionPoint.getName(), extensionPoint);
		}

		extensionPoint.put(name, description, data);
	}

}
