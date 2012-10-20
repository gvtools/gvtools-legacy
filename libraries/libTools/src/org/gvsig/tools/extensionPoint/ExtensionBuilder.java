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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Clase de utilidad usada para crear las extensiones.
 * 
 * Esta clase presenta un par de metodos estaticos para permitir crear un objeto
 * a partir de una clase.
 * 
 * @author jjdelcerro
 * 
 */
public abstract class ExtensionBuilder implements IExtensionBuilder {

	/**
	 * Crea un objeto de la clase indicada.
	 * 
	 * @param cls
	 *            Clase de la que crear la instancia
	 * @return
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object create(Class cls) throws InstantiationException,
			IllegalAccessException {
		Object obj = null;

		if (cls == null) {
			return null;
		}
		obj = cls.newInstance();
		return obj;
	}

	/**
	 * Crea un objeto de la clase indicada.
	 * 
	 * Crea un objeto de la clase indicada pasandole al constructor los
	 * argumentos indicados en <i>args</i>. <br>
	 * 
	 * @param cls
	 *            Clase de la que crear la instancia
	 * @param args
	 *            Argumentos que pasar al constructor.
	 * @return
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object create(Class cls, Object[] args)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Object obj = null;
		Constructor create = null;
		Class[] types = new Class[args.length];

		if (cls == null) {
			return null;
		}
		for (int n = 0; n < args.length; n++) {
			Object arg = args[n];
			types[n] = arg.getClass();
		}
		create = cls.getConstructor(types);
		obj = create.newInstance(args);
		return obj;
	}

	/**
	 * Crea un objeto de la clase indicada.
	 * 
	 * Crea un objeto de la clase indicada pasandole al constructor un como
	 * argumento un Map.. <br>
	 * 
	 * @param cls
	 *            Clase de la que crear la instancia
	 * @param args
	 *            Map a pasar como argumento al constructor.
	 * @return
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Object create(Class cls, Map args) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Object obj = null;
		Constructor create = null;
		Class[] types = new Class[1];
		Object[] argsx = new Object[1];

		if (cls == null) {
			return null;
		}
		types[0] = Map.class;
		argsx[0] = args;
		create = cls.getConstructor(types);
		obj = create.newInstance(argsx);
		return obj;
	}
}
