package com.hardcode.gdbms.engine.function;

import java.util.HashMap;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class FunctionManager {
	private static HashMap nameFunction = new HashMap();
	static {
		addFunction(new ConcatenateFunction());
		addFunction(new DateFunction());
		addFunction(new BooleanFunction());
		addFunction(new Count());
		addFunction(new Sum());
	}

	/**
	 * Añade una nueva función al sistema
	 *
	 * @param function función
	 *
	 * @throws RuntimeException DOCUMENT ME!
	 */
	public static void addFunction(Function function) {
		if (nameFunction.get(function.getName()) != null) {
			throw new RuntimeException("Ya hay una función llamada " +
				function.getName());
		}

		nameFunction.put(function.getName(), function);
	}

	/**
	 * Obtiene la funcion de nombre name
	 *
	 * @param name nombre de la funcion que se quiere obtener
	 *
	 * @return función o null si no hay ninguna función que devuelva dicho
	 * 		   nombre
	 */
	public static Function getFunction(String name) {
		return ((Function) nameFunction.get(name)).cloneFunction();
	}
}
