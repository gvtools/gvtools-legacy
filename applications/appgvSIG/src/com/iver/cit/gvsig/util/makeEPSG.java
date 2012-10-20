package com.iver.cit.gvsig.util;

/* * 
 * Copyright (C) 2010 Benjamin Ducke
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 * benjamin.ducke@oadigital.net
 *
 *
 ***************************************************************************
 *
 * makeEPSG.java - program to convert EPSG PostgreSQL scripts to HSQL statements
 * For use with gvSIG.
 *
 * This program was designed to be run on preprocessed input that was
 * prepared as described in the document "PrepareForHSQL.xml", which
 * is part of the Geotools project source files.
 * 
 * Once the EPSG PostgreSQL scripts have been modified as described in above
 * document, merge them into one file, in the correct order:
 * 
 *   cat EPSG_Tables.sql EPSG_Data.sql EPSG_FKeys.sql > input.sql
 *   
 * Then run input.sql through this program to obtain the properly formatted EPSG.sql
 * file. EPSG.sql can then be injected into gvSIG's geotools-epsg-hsql-2.1.1_gvsig.jar
 * to update the EPSG codes database.
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class makeEPSG {

	/* output line number range for debugging */
	public static final int MIN = 6850;
	public static final int MAX = 6870;

	public static void main(String args[]) {

		if (args.length != 1) {
			System.out
					.println("Convert an SQL script to HSQLDB execute statements (one per line)");
			System.out.println("Usage: java makeEPSG <input>");
			System.out
					.println("The output will be a file called 'ESPG.sql' in the current directory.");
			System.out
					.println("All text processing will be done in ISO-8859-15.\n");
			System.out
					.println("This program was designed to be run on preprocessed input that was");
			System.out
					.println("prepared as described in the document 'PrepareForHSQL.xml', which");
			System.out
					.println("is part of the Geotools project source files.\n");
			System.out
					.println("Once the EPSG PostgreSQL scripts have been modified as described in above");
			System.out
					.println("document, merge them into one file, in the correct order:");
			System.out
					.println("  cat EPSG_Tables.sql EPSG_Data.sql EPSG_FKeys.sql > input.sql\n");
			System.out
					.println("Then run input.sql through this program to obtain the properly formatted EPSG.sql");
			System.out
					.println("file. EPSG.sql can then be injected into gvSIG's geotools-epsg-hsql-2.1.1_gvsig.jar");
			System.out.println("to update the EPSG codes database.");
			System.exit(0);
		}

		try {
			// Open files for input and output
			File inFile = new File(args[0]);
			File outFile = new File("EPSG.sql");
			FileInputStream fis = new FileInputStream(inFile);
			InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-15");
			BufferedReader in = new BufferedReader(isr);
			FileOutputStream fout = new FileOutputStream(outFile);
			OutputStreamWriter out = new OutputStreamWriter(fout, "ISO-8859-15");
			if (inFile.exists()) {
				System.out.println("Processing (" + isr.getEncoding() + "): "
						+ args[0]);
				String line = in.readLine();
				String output = new String("");
				String terminator = new String(";".trim());
				String lastInsert = new String("");
				Boolean needsComma = false;
				Boolean insideStatement = false;
				int i = 0;
				needsComma = false;
				while (line != null) {
					if (line.trim().endsWith(terminator)) {
						insideStatement = false;
					}
					final String upperCase = line.toUpperCase();
					// Merge sequential INSERTs that target the same table
					if (upperCase.startsWith("INSERT INTO")) {
						insideStatement = true;
						final String tableName = line.substring(12,
								upperCase.indexOf("VALUES")).trim();
						if (lastInsert.equals(tableName)) {// write into open
															// statement
							line = line.substring(upperCase.indexOf("("));
							needsComma = true;
						} else {// open new statement
							final String token2 = new String("\n"
									+ line.substring(upperCase.indexOf("(")));
							line = new String("insert into " + tableName
									+ " values" + token2);
							lastInsert = new String(tableName);
							needsComma = false;
						}
					}
					// Compact string
					if (!line.contains("'") && !line.contains("\"")) {
						line = line.toUpperCase();
						line = line.replaceAll("([ ]+)", " ");
						line = line.replace(" (", "(");
						line = line.replace("( ", "(");
						line = line.replace(" )", ")");
						line = line.replace(") )", "))");
						/* modify field definitions */
						line = line.replace("DOUBLE PRECISION", "DOUBLE");
					}
					line = line.replace("( '", "('");
					line = line.replace("( \"", "(\"");
					line = line.replace("' )", "')");
					line = line.replace("\" )", "\")");
					if (!line.trim().endsWith(terminator)) {
						if (line.trim().length() > 0) {
							output = output + line.trim();
						}
					} else {
						i++;
						output = output
								+ line.substring(0, line.lastIndexOf(";"));
						// Careful with "\n" and "," at end of line
						if (needsComma == true) {
							out.write(",");
							needsComma = false;
						}
						if (i > 1) {
							out.write("\n");
						}
						out.write(output);

						/* DEBUG */
						if (i > MIN && i < MAX) {
							System.out.println("[" + i + "] " + output);
						}

						output = "";
					}
					line = in.readLine();
				}
				out.write("\n");
				// Create some indices for faster queries
				out.write("CREATE INDEX ALIAS_OBJECT_CODE ON EPSG_ALIAS(OBJECT_CODE)\n");
				out.write("CREATE INDEX CRS_DATUM_CODE ON EPSG_COORDINATEREFERENCESYSTEM(DATUM_CODE)\n");
				out.write("CREATE INDEX CRS_PROJECTION_CODE ON EPSG_COORDINATEREFERENCESYSTEM(PROJECTION_CONV_CODE)\n");
				out.write("CREATE INDEX COORDINATE_AXIS_CODE ON EPSG_COORDINATEAXIS(COORD_AXIS_CODE)\n");
				out.write("CREATE INDEX COORDINATE_AXIS_SYS_CODE ON EPSG_COORDINATEAXIS(COORD_SYS_CODE)\n");
				out.write("CREATE INDEX COORDINATE_OPERATION_CRS ON EPSG_COORDOPERATION(SOURCE_CRS_CODE, TARGET_CRS_CODE)\n");
				out.write("CREATE INDEX COORDINATE_OPERATION_METHOD_CODE ON EPSG_COORDOPERATION(COORD_OP_METHOD_CODE)\n");
				out.write("CREATE INDEX PARAMETER_USAGE_METHOD_CODE ON EPSG_COORDOPERATIONPARAMUSAGE(COORD_OP_METHOD_CODE)\n");
				out.write("CREATE INDEX PARAMETER_VALUES ON EPSG_COORDOPERATIONPARAMVALUE(COORD_OP_CODE, COORD_OP_METHOD_CODE)\n");
				out.write("CREATE INDEX PARAMETER_VALUE_CODE ON EPSG_COORDOPERATIONPARAMVALUE(PARAMETER_CODE)\n");
				out.write("CREATE INDEX PATH_CONCAT_OPERATION_CODE ON EPSG_COORDOPERATIONPATH(CONCAT_OPERATION_CODE)\n");
				out.write("CREATE INDEX SUPERSESSION_OBJECT_CODE ON EPSG_SUPERSESSION(OBJECT_CODE)\n");
				out.write("CREATE INDEX NAME_CRS ON EPSG_COORDINATEREFERENCESYSTEM(COORD_REF_SYS_NAME)\n");
				out.write("CREATE INDEX NAME_CS ON EPSG_COORDINATESYSTEM(COORD_SYS_NAME)\n");
				out.write("CREATE INDEX NAME_AXIS ON EPSG_COORDINATEAXISNAME(COORD_AXIS_NAME)\n");
				out.write("CREATE INDEX NAME_DATUM ON EPSG_DATUM(DATUM_NAME)\n");
				out.write("CREATE INDEX NAME_ELLIPSOID ON EPSG_ELLIPSOID(ELLIPSOID_NAME)\n");
				out.write("CREATE INDEX NAME_PRIME_MERIDIAN ON EPSG_PRIMEMERIDIAN(PRIME_MERIDIAN_NAME)\n");
				out.write("CREATE INDEX NAME_COORD_OP ON EPSG_COORDOPERATION(COORD_OP_NAME)\n");
				out.write("CREATE INDEX NAME_METHOD ON EPSG_COORDOPERATIONMETHOD(COORD_OP_METHOD_NAME)\n");
				out.write("CREATE INDEX NAME_PARAMETER ON EPSG_COORDOPERATIONPARAM(PARAMETER_NAME)\n");
				out.write("CREATE INDEX NAME_UNIT ON EPSG_UNITOFMEASURE(UNIT_OF_MEAS_NAME)\n");
				// Write final statement
				out.write("SHUTDOWN COMPACT\n");
				out.close();
			} else {
				System.err.println("ERROR: Input file '" + args[0]
						+ "'. Does not exist.\n");
				System.exit(-1);
			}
		} catch (Exception e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}
}
