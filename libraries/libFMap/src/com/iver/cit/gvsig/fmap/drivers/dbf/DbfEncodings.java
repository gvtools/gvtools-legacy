package com.iver.cit.gvsig.fmap.drivers.dbf;

import java.nio.charset.Charset;
import java.util.Hashtable;

public class DbfEncodings {
	private static DbfEncodings theInstance = null;
	private final int[] dbfIds = { 0x00, 0x02, 0x03, 0x04, 0x64, 0x65, 0x66,
			0x67, 0x6A, 0x6B, 0x78, 0x79, 0x7A, 0x7B, 0x7C, 0x7D, 0x7E, 0x96,
			0x97, 0x98, 0xC8, 0xC9, 0xCA, 0xCB, 0xF7, 0xF8, 0x99 };

	private Hashtable<Byte, String> dbfId2charset;

	DbfEncodings() {

	}

	public static DbfEncodings getInstance() {
		if (theInstance == null)
			theInstance = new DbfEncodings();
		return theInstance;
	}

	public String getCharsetForDbfId(int i) {
		String cCP = null;
		switch (i) {
		case (0x00):
			cCP = "UNKNOWN";
			Charset.defaultCharset().name(); // "No codepage defined";
			break;
		case (0x01):
			cCP = "US-ASCII"; // Codepage 437 US MSDOS";
			break;
		case (0x02):
			cCP = "Cp850"; // Codepage 850 International MS-DOS";
			break;
		case (0x03):
			cCP = "Cp1252"; // "Codepage 1252 Windows ANSI";
			break;
		case (0x04):
			cCP = "MacRoman"; // Codepage 10000 Standard MacIntosh";
			break;
		case (0x64):
			cCP = "Cp852"; // "Codepage 852 Eastern European MS-DOS";
			break;
		case (0x65):
			cCP = "Cp866"; // "Codepage 866 Russian MS-DOS";
			break;
		case (0x66):
			cCP = "Cp865"; // "Codepage 865 Nordic MS-DOS";
			break;
		case (0x67):
			cCP = "Cp861"; // "Codepage 861 Icelandic MS-DOS";
			break;
		// case (0x68):
		// cCP = "Cp895"; //"Codepage 895 Kamenicky (Czech): MS-DOS";
		// break;
		// case (0x69):
		// cCP = "Cp620"; //"Codepage 620 Mazovia (Polish): MS-DOS";
		// break;
		case (0x6A):
			cCP = "Cp737"; // "Codepage 737 Greek MS-DOS (437G):";
			break;
		case (0x6B):
			cCP = "Cp857"; // "Codepage 857 Turkish MS-DOS";
			break;
		case (0x78):
			cCP = "Big5"; // "Codepage 950 Chinese (Hong Kong SAR, Taiwan):
							// Windows";
			break;
		case (0x79):
			cCP = "Cp949"; // "Codepage 949 Korean Windows";
			break;
		case (0x7A):
			cCP = "GB2312"; // "Codepage 936 Chinese (PRC, Singapore): Windows";
			break;
		case (0x7B):
			cCP = "EUC-JP"; // "Codepage 932 Japanese Windows";
			break;
		case (0x7C):
			cCP = "Cp838"; // "Codepage 874 Thai Windows";
			break;
		case (0x7D):
			cCP = "windows-1255"; // "Codepage 1255 Hebrew Windows";
			break;
		case (0x7E):
			cCP = "Cp1256"; // "Codepage 1256 Arabic Windows";
			break;
		case (0x96):
			cCP = "cyrillic"; // "Codepage 10007 Russian MacIntosh";
			break;
		case (0x97):
			cCP = "macintosh";
			break;
		case (0x98):
			cCP = "MacGreek"; // "Codepage 10006 Greek MacIntosh";
			break;
		case (0xC8):
			cCP = "Cp1250"; // "Codepage 1250 Eastern European Windows";
			break;
		case (0xC9):
			cCP = "Cp1251"; // "Codepage 1251 Russian Windows";
			break;
		case (0xCA):
			cCP = "Cp1254"; // "Codepage 1254 Turkish Windows";
			break;
		case (0xCB):
			cCP = "ISO-8859-7"; // "Codepage 1253 Greek Windows";
			break;
		case (0xF7):
			cCP = "ISO-8859-1"; // "(inventado)";
			break;
		case (0xF8):
			cCP = "ISO-8859-15"; // "(inventado)";
			break;
		case (0x99):
			cCP = "UTF-8"; // "utf8 (inventado)";
			break;
		}
		return cCP;
	}

	public short getDbfIdForCharset(Charset charset) {
		short dbfId = 0x0;
		String s = charset.name();
		if (s.equalsIgnoreCase("US-ASCII"))
			dbfId = 0x01; // Codepage 437 US MSDOS";
		if (s.equalsIgnoreCase("Cp850"))
			dbfId = 0x02;
		if (s.equalsIgnoreCase("Cp1252"))
			dbfId = 0x03;
		if (s.equalsIgnoreCase("MacRoman"))
			dbfId = 0x04;
		if (s.equalsIgnoreCase("Cp852"))
			dbfId = 0x64;
		if (s.equalsIgnoreCase("Cp866"))
			dbfId = 0x65;
		if (s.equalsIgnoreCase("Cp865"))
			dbfId = 0x66;
		if (s.equalsIgnoreCase("Cp861"))
			dbfId = 0x67;
		if (s.equalsIgnoreCase("Cp737"))
			dbfId = 0x6A;
		if (s.equalsIgnoreCase("Cp857"))
			dbfId = 0x6B;
		if (s.equalsIgnoreCase("Big5"))
			dbfId = 0x78;
		if (s.equalsIgnoreCase("Cp949"))
			dbfId = 0x79;
		if (s.equalsIgnoreCase("GB2312"))
			dbfId = 0x7A;
		if (s.equalsIgnoreCase("EUC-JP"))
			dbfId = 0x7B;
		if (s.equalsIgnoreCase("Cp838"))
			dbfId = 0x7C;
		if (s.equalsIgnoreCase("windows-1255"))
			dbfId = 0x7D;

		if (s.equalsIgnoreCase("Cp1256"))
			dbfId = 0x7E;
		if (s.equalsIgnoreCase("windows-1256"))
			dbfId = 0x7E;

		if (s.equalsIgnoreCase("cyrillic"))
			dbfId = 0x96;
		if (s.equalsIgnoreCase("macintosh"))
			dbfId = 0x97;
		if (s.equalsIgnoreCase("MacGreek"))
			dbfId = 0x98;
		if (s.equalsIgnoreCase("Cp1250"))
			dbfId = 0xC8;
		if (s.equalsIgnoreCase("windows-1250"))
			dbfId = 0xC8;
		if (s.equalsIgnoreCase("Cp1251"))
			dbfId = 0xC9;
		if (s.equalsIgnoreCase("windows-1251"))
			dbfId = 0xC9;
		if (s.equalsIgnoreCase("Cp1254"))
			dbfId = 0xCA;
		if (s.equalsIgnoreCase("windows-1254"))
			dbfId = 0xCA;
		if (s.equalsIgnoreCase("ISO-8859-7"))
			dbfId = 0xCB;
		if (s.equalsIgnoreCase("ISO-8859-1"))
			dbfId = 0xF7; // invented
		if (s.equalsIgnoreCase("ISO-8859-15"))
			dbfId = 0xF8; // invented
		if (s.equalsIgnoreCase("UTF-8"))
			dbfId = 0x99;
		System.out.println("getDbfIdForCharset " + s + " dbfId = " + dbfId);

		return dbfId;
	}

	public int[] getSupportedDbfLanguageIDs() {
		return dbfIds;
	}
}
