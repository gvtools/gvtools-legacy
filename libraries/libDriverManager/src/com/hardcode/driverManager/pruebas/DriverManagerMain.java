package com.hardcode.driverManager.pruebas;

import java.io.File;
import java.io.IOException;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;

/**
 * @author Fernando González Cortés
 */
public class DriverManagerMain {

	public static void main(String[] args) throws IOException, DriverLoadException {
		DriverManager dm = new DriverManager();
		dm.loadDrivers(new File ("c:\\tirar\\"));
		Adder a = (Adder) dm.getDriver("num");
		System.out.println(a.add("3", "4"));
		a = (Adder) dm.getDriver("txt");
		System.out.println(a.add("3", "4"));
	}
}
