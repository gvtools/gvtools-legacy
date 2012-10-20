/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Gobernment (CIT)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 */
package org.gvsig.tools.extensionPoint;

import java.util.TreeMap;

/**
 * Clase para registro de puntos de extension. <br>
 * <br>
 * 
 * @author jjdelcerro
 */
public class ExtensionPoints extends TreeMap {

	private static final long serialVersionUID = -798417910971607414L;

	/**
	 * Evita que se a�adan elementos que no son puntos de extension. <br>
	 * <br>
	 * Aunque la clase se comporta como un <i>Map</i>, no esta permitido a�adir
	 * a esta objetos que no sean de la clase <i>ExtensionPoint</i>. Si
	 * intentamos a�adir un elemento que no sea de esta clase, se disparara una
	 * excepcion ClassCastException. <br>
	 * <br>
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) throws ClassCastException {
		throw new ClassCastException();
	}

	/**
	 * A�ade un punto de extension al registro de puntos de extension. <br>
	 * <br>
	 * Mediante este metodo puede a�adir un punto de extension al registro de
	 * puntos de extension, llevandose detras todas las extensiones que esten
	 * registradas en el. <br>
	 * <br>
	 * En caso de que ya existiese un punto de extension con el nombre dado,
	 * a�adira a este las extensiones del punto de extension suministrado. <br>
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
	 * A�ade un punto de extension al registro de puntos de extension. <br>
	 * <br>
	 * Mediante este metodo puede a�adir un punto de extension al registro de
	 * puntos de extension, llevandose detras todas las extensiones que esten
	 * registradas en el. <br>
	 * <br>
	 * En caso de que ya existiese un punto de extension con el nombre dado,
	 * a�adira a este las extensiones del punto de extension suministrado. <br>
	 * <br>
	 * Cuando se a�ade un punto de extension, es imprescindible que <i>key</i> y
	 * el nombre del punto de extension que se este a�adiendo coincidan. <br>
	 * <br>
	 * 
	 * @param key
	 *            Nombre del punto de extension
	 * @param value
	 *            Punto de extension a registrar
	 * @return
	 * 
	 */
	public Object put(String key, ExtensionPoint value) {
		if (!value.getName().equals(key)) {
			throw new IllegalArgumentException();
		}
		ExtensionPoint n = (ExtensionPoint) super.get(key);
		if (n == null) {
			return super.put(key, value);
		}
		// Como estamos actualizando un punto de extension, a�adimos a este las
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
		ExtensionPoint extensionPoint = (ExtensionPoint) super
				.get(extensionPointName);
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
		ExtensionPoint extensionPoint = (ExtensionPoint) super
				.get(extensionPointName);
		if (extensionPoint == null) {
			extensionPoint = new ExtensionPoint(extensionPointName);
			super.put(extensionPoint.getName(), extensionPoint);
		}

		extensionPoint.put(name, description, data);
	}

}
