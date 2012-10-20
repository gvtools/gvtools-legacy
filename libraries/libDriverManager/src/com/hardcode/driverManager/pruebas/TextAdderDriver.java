package com.hardcode.driverManager.pruebas;

import com.hardcode.driverManager.Driver;

/**
 * @author Fernando González Cortés
 */
public class TextAdderDriver implements Driver, Adder {

	/**
	 * @see com.hardcode.driverManager.pruebas.Adder#add(java.lang.String,
	 *      java.lang.String)
	 */
	public String add(String a, String b) {
		return a + b;
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "Driver para texto del Fernan";
	}

}
