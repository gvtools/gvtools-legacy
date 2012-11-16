package com.iver.cit.gvsig.project;

import java.util.ArrayList;

public class Units {

	public static ArrayList<String> AREANAMES = new ArrayList<String>();
	public static ArrayList<String> AREAABBR = new ArrayList<String>();
	public static ArrayList<Double> AREATRANS2METER = new ArrayList<Double>();

	public static ArrayList<String> DISTANCENAMES = new ArrayList<String>();
	public static ArrayList<String> DISTANCEABBR = new ArrayList<String>();
	public static ArrayList<Double> DISTANCETRANS2METER = new ArrayList<Double>();

	static {
		addDistanceUnit("Kilometros", "Km", 1000);
		addDistanceUnit("Metros", "m", 1);
		addDistanceUnit("Centimetros", "cm", 0.01);
		addDistanceUnit("Milimetros", "mm", 0.001);
		addDistanceUnit("Millas", "mi", 1609.344);
		addDistanceUnit("Yardas", "Ya", 0.9144);
		addDistanceUnit("Pies", "ft", 0.3048);
		addDistanceUnit("Pulgadas", "inche", 0.0254);
		addDistanceUnit("Grados", "�", 1 / 8.983152841195214E-6);

		addAreaUnit("Kilometros", "Km", true, 1000);
		addAreaUnit("Metros", "m", true, 1);
		addAreaUnit("Centimetros", "cm", true, 0.01);
		addAreaUnit("Milimetros", "mm", true, 0.001);
		addAreaUnit("Millas", "mi", true, 1609.344);
		addAreaUnit("Yardas", "Ya", true, 0.9144);
		addAreaUnit("Pies", "ft", true, 0.3048);
		addAreaUnit("Pulgadas", "inche", true, 0.0254);
		addAreaUnit("Grados", "�", true, 1 / 8.983152841195214E-6);

	}

	public static void addAreaUnit(String name, String abbr, boolean isLinear,
			double trans2meter) {
		if (!AREANAMES.contains(name)) {
			AREANAMES.add(name);
			String pow = "";
			if (isLinear)
				pow = String.valueOf((char) 178);
			AREAABBR.add(abbr + pow);
			AREATRANS2METER.add(new Double(trans2meter));
		}
	}

	public static String[] getAreaNames() {
		return (String[]) AREANAMES.toArray(new String[0]);
	}

	public static String[] getAreaAbbr() {
		return (String[]) AREAABBR.toArray(new String[0]);
	}

	public static double[] getAreaTrans2Meter() {
		int size = AREATRANS2METER.size();
		double[] trans2meters = new double[size];
		for (int i = 0; i < size; i++) {
			trans2meters[i] = ((Double) AREATRANS2METER.get(i)).doubleValue();
		}
		return trans2meters;
	}

	public static void addDistanceUnit(String name, String abbr,
			double trans2meter) {
		if (!DISTANCENAMES.contains(name)) {
			DISTANCENAMES.add(name);
			DISTANCEABBR.add(abbr);
			DISTANCETRANS2METER.add(new Double(trans2meter));
		}
	}

	public static String[] getDistanceNames() {
		return (String[]) DISTANCENAMES.toArray(new String[0]);
	}

	public static String[] getDistanceAbbr() {
		return (String[]) DISTANCEABBR.toArray(new String[0]);
	}

	public static double[] getDistanceTrans2Meter() {
		int size = DISTANCETRANS2METER.size();
		double[] trans2meters = new double[size];
		for (int i = 0; i < size; i++) {
			trans2meters[i] = ((Double) DISTANCETRANS2METER.get(i))
					.doubleValue();
		}
		return trans2meters;
	}

	public static int getDistancePosition(String s) {
		for (int i = 0; i < DISTANCENAMES.size(); i++) {
			if (DISTANCENAMES.get(i).equals(s)) {
				return i;
			}
		}
		return 0;
	}

}
