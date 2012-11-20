package org.gvsig.units;

import java.util.ArrayList;

public enum AreaUnit {
	KM2("Kilometros", "Km", 1000), M2("Metros", "m", 1), CM2("Centimetros",
			"cm", 0.01), MM2("Milimetros", "mm", 0.001), MI2("Millas", "mi",
			1609.344), YA2("Yardas", "Ya", 0.9144), FT2("Pies", "ft", 0.3048), INCHE2(
			"Pulgadas", "inche", 0.0254);

	public String name;
	public String symbol;
	public double toMeter;

	AreaUnit(String name, String symbol, double toMeter) {
		this.name = name;
		this.symbol = symbol + String.valueOf((char) 178);
		this.toMeter = toMeter;
	}

	public static String[] getDistanceNames() {
		ArrayList<String> ret = new ArrayList<String>();
		for (AreaUnit p : AreaUnit.values()) {
			ret.add(p.name);
		}

		return ret.toArray(new String[0]);
	}

	public static String[] getDistanceSymbols() {
		ArrayList<String> ret = new ArrayList<String>();
		for (AreaUnit p : AreaUnit.values()) {
			ret.add(p.symbol);
		}

		return ret.toArray(new String[0]);
	}

	/**
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 *             If there is no unit with the specified name
	 */
	public static AreaUnit fromName(String name)
			throws IllegalArgumentException {
		for (AreaUnit unit : values()) {
			if (unit.name.equals(name)) {
				return unit;
			}
		}

		throw new IllegalArgumentException("No such unit: " + name);
	}

}
