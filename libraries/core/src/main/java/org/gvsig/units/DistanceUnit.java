package org.gvsig.units;

import java.util.ArrayList;

public enum DistanceUnit {
	KM("Kilometros", "Km", 1000), M("Metros", "m", 1), CM("Centimetros", "cm",
			0.01), MM("Milimetros", "mm", 0.001), MI("Millas", "mi", 1609.344), YA(
			"Yardas", "Ya", 0.9144), FT("Pies", "ft", 0.3048), INCHE(
			"Pulgadas", "inche", 0.0254);

	public String name;
	public String symbol;
	public double toMeter;

	DistanceUnit(String name, String symbol, double toMeter) {
		this.name = name;
		this.symbol = symbol;
		this.toMeter = toMeter;
	}

	public static String[] getDistanceNames() {
		ArrayList<String> ret = new ArrayList<String>();
		for (DistanceUnit unit : DistanceUnit.values()) {
			ret.add(unit.name);
		}

		return ret.toArray(new String[0]);
	}

	public static String[] getDistanceSymbols() {
		ArrayList<String> ret = new ArrayList<String>();
		for (DistanceUnit unit : DistanceUnit.values()) {
			ret.add(unit.symbol);
		}

		return ret.toArray(new String[0]);
	}

	/**
	 * @param name
	 * @return
	 * @throws IllegalArgumentException
	 *             If there is no unit with the specified name
	 */
	public static DistanceUnit fromName(String name)
			throws IllegalArgumentException {
		for (DistanceUnit unit : values()) {
			if (unit.name.equals(name)) {
				return unit;
			}
		}

		throw new IllegalArgumentException("No such unit: " + name);
	}
}
