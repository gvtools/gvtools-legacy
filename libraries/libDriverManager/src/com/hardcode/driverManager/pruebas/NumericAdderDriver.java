package com.hardcode.driverManager.pruebas;

import com.hardcode.driverManager.Driver;

/**
 * @author Fernando González Cortés
 */
public class NumericAdderDriver implements Driver, Adder {

	/**
	 * @see com.hardcode.driverManager.pruebas.Adder#add(java.lang.String,
	 *      java.lang.String)
	 */
	public String add(String a, String b) {
		return new Integer(new Integer(a).intValue()
				+ new Integer(b).intValue()).toString();
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "drivers para números del Fernan";
	}

}
