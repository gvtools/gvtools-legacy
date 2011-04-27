package com.hardcode.gdbms.engine.strategies;

import com.hardcode.gdbms.engine.instruction.CustomAdapter;
import com.hardcode.gdbms.engine.instruction.SelectAdapter;
import com.hardcode.gdbms.engine.instruction.UnionAdapter;


/**
 * Manejador de las distintas estrategias disponibles para ejecutar las
 * instrucciones
 *
 * @author Fernando González Cortés
 */
public class StrategyManager {
	/**
	 * Obtiene la estrategia más adecuada en función de la instrucción a
	 * ejecutar y de las condiciones actuales del sistema
	 *
	 * @param instr Instrucción que se desea ejecutar
	 *
	 * @return estrategia capaz de ejecutar la instrucción
	 */
	public static Strategy getStrategy(SelectAdapter instr) {
		return new FirstStrategy();
	}

	/**
	 * Obtiene la estrategia óptima para ejecutar la instrucción de union que
	 * se pasa como parámetro
	 *
	 * @param instr instrucción que se quiere ejecutar
	 *
	 * @return
	 */
	public static Strategy getStrategy(UnionAdapter instr) {
		return new FirstStrategy();
	}

	/**
	 * Gets the only strategy to execute custom queries
	 *
	 * @param instr root node of the custom query to execute
	 *
	 * @return Strategy
	 */
	public static Strategy getStrategy(CustomAdapter instr) {
		return new FirstStrategy();
	}
}
