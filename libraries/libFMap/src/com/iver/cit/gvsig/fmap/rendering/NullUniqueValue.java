package com.iver.cit.gvsig.fmap.rendering;

import java.sql.Types;

import com.hardcode.gdbms.engine.values.NullValue;

/**
 * Clase que extiende a NullValue para especificar los vlaores no representados
 * por ningún otro valor.
 * 
 * @author Vicente Caballero Navarro
 */
public class NullUniqueValue extends NullValue {
	public int getSQLType() {
		return Types.OTHER;
	}

	public String toString() {
		return "Default";
	}
}
