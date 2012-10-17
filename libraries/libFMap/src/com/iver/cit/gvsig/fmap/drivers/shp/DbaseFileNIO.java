/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.drivers.shp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import com.iver.cit.gvsig.fmap.drivers.dbf.DbfEncodings;
import com.iver.utiles.bigfile.BigByteBuffer2;


/**
 * Class to read and write data to a dbase III format file. Creation date:
 * (5/15/2001 5:15:13 PM)
 */
public class DbaseFileNIO {
	private static Charset defaultCharset = Charset.defaultCharset();

	// Header information for the DBase File
	private DbaseFileHeaderNIO myHeader;
	private FileInputStream fin;
	private FileChannel channel;
	private BigByteBuffer2 buffer;

	CharBuffer charBuffer;

	private Charset charSet;

	/**
	 * Retrieve number of records in the DbaseFile
	 *
	 * @return N�mero de registros.
	 */
	public int getRecordCount() {
		return myHeader.getNumRecords();
	}

	/**
	 * Devuelve el n�mero de fields.
	 *
	 * @return N�mero de fields.
	 */
	public int getFieldCount() {
		return myHeader.getNumFields();
	}

	/**
	 * Devuelve el valor de un boolean a partir de su n�mero de fila y de
	 * field.
	 *
	 * @param rowIndex N�mero de fila.
	 * @param fieldId N�mero columna.
	 *
	 * @return boolean.
	 */
	public boolean getBooleanFieldValue(int rowIndex, int fieldId) {
		int recordOffset = (myHeader.getRecordLength() * rowIndex) +
			myHeader.getHeaderLength() + 1;

		//Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < (fieldId - 1); i++) {
			fieldOffset += myHeader.getFieldLength(i);
		}

		buffer.position(recordOffset + fieldOffset);

		char bool = (char) buffer.get();

		return ((bool == 't') || (bool == 'T') || (bool == 'Y') ||
		(bool == 'y'));
	}

	/**
	 * Devuelve el String a partir del n�mero de fila y columna.
	 *
	 * @param rowIndex N�mero de fila.
	 * @param fieldId N�mero de columna.
	 *
	 * @return String.
	 * @throws UnsupportedEncodingException
	 */
	public String getStringFieldValue(int rowIndex, int fieldId) throws UnsupportedEncodingException {
		int recordOffset = (myHeader.getRecordLength() * rowIndex) +
			myHeader.getHeaderLength() + 1;

		//Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < fieldId; i++) {
			fieldOffset += myHeader.getFieldLength(i);
		}

		buffer.position(recordOffset + fieldOffset);

		byte[] data = new byte[myHeader.getFieldLength(fieldId)];

//		ByteBuffer byteBuffer = ByteBuffer.wrap(data);


		buffer.get(data);
		return new String(data, charSet.name());
	}

	/**
	 * Devuelve el Number a partir de una fila y columna.
	 *
	 * @param rowIndex N�mero fila.
	 * @param fieldId N�mero columna.
	 *
	 * @return Number.
	 */
	public Number getNumberFieldValue(int rowIndex, int fieldId) {
		//System.out.println("rowIndex = "+rowIndex+ " , "+"fieldId = "+fieldId);
		int recordOffset = (myHeader.getRecordLength() * rowIndex) +
			myHeader.getHeaderLength() + 1;

		//Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < fieldId; i++) {
			fieldOffset += myHeader.getFieldLength(i);
		}

		buffer.position(recordOffset + fieldOffset);

		byte[] data = new byte[myHeader.getFieldLength(fieldId)];
		buffer.get(data);

		String s = new String(data);
		s = s.trim();

		if (getFieldType(fieldId) == 'N') {
			Object tempObject = Double.valueOf(s);

			return new Double(tempObject.toString());
		} else {
			Object tempObject = Integer.valueOf(s);

			return new Integer(tempObject.toString());
		}

		//return 0;
	}

	// Retrieve the record at the given index

	/*    public Object[] getRecord(long inIndex) throws IOException {
	   long nRecordOffset = (myHeader.getRecordLength() * inIndex) +
	       myHeader.getHeaderLength();
	   // retrieve the record length
	   int tempNumFields = myHeader.getNumFields();
	   // storage for the actual values
	   Object[] tempRow = new Object[tempNumFields];
	       buffer.position((int) nRecordOffset);
	       // read the deleted flag
	       char tempDeleted = (char) buffer.get();
	       // read the record length
	       int tempRecordLength = 1; // for the deleted character just read.
	       // read the Fields
	       for (int j = 0; j < tempNumFields; j++) {
	           // find the length of the field.
	           int tempFieldLength = myHeader.getFieldLength(j);
	           tempRecordLength = tempRecordLength + tempFieldLength;
	           // find the field type
	           char tempFieldType = myHeader.getFieldType(j);
	           //System.out.print("Reading Name="+myHeader.getFieldName(j)+" Type="+tempFieldType +" Length="+tempFieldLength);
	           // read the data.
	           Object tempObject = null;
	           switch (tempFieldType) {
	           case 'L': // logical data type, one character (T,t,F,f,Y,y,N,n)
	               char tempChar = (char) buffer.get();
	               if ((tempChar == 'T') || (tempChar == 't') ||
	                       (tempChar == 'Y') || (tempChar == 'y')) {
	                   tempObject = new Boolean(true);
	               } else {
	                   tempObject = new Boolean(false);
	               }
	               break;
	           case 'C': // character record.
	               byte[] sbuffer = new byte[tempFieldLength];
	                                   buffer.get(sbuffer);
	               tempObject = new String(sbuffer, "ISO-8859-1").trim();
	               break;
	           case 'D': // date data type.
	               byte[] dbuffer = new byte[8];
	                                   buffer.get(dbuffer);
	               String tempString = new String(dbuffer, 0, 4);
	               try {
	                   int tempYear = Integer.parseInt(tempString);
	                   tempString = new String(dbuffer, 4, 2);
	                   int tempMonth = Integer.parseInt(tempString) - 1;
	                   tempString = new String(dbuffer, 6, 2);
	                   int tempDay = Integer.parseInt(tempString);
	                   Calendar c = Calendar.getInstance();
	                   c.set(Calendar.YEAR, tempYear);
	                   c.set(Calendar.MONTH, tempMonth);
	                   c.set(Calendar.DAY_OF_MONTH, tempDay);
	                   tempObject = c.getTime();
	               } catch (NumberFormatException e) {
	               }
	               break;
	           case 'M': // memo field.
	               byte[] mbuffer = new byte[10];
	                                   buffer.get(mbuffer);
	               break;
	           case 'N': // number
	           case 'F': // floating point number
	               byte[] fbuffer = new byte[tempFieldLength];
	                                   buffer.get(fbuffer);
	               try {
	                   tempString = new String(fbuffer);
	                   tempObject = Double.valueOf(tempString.trim());
	               } catch (NumberFormatException e) {
	               }
	               break;
	           default:
	               byte[] defbuffer = new byte[tempFieldLength];
	                                   buffer.get(defbuffer);
	               System.out.println("Do not know how to parse Field type " +
	                   tempFieldType);
	           }
	           tempRow[j] = tempObject;
	           //                                System.out.println(" Data="+tempObject);
	       }
	       // ensure that the full record has been read.
	       if (tempRecordLength < myHeader.getRecordLength()) {
	           byte[] tempbuff = new byte[myHeader.getRecordLength() -
	               tempRecordLength];
	           buffer.get(tempbuff);
	           /* if (tempTelling){
	                   System.out.println("DBF File has "+(myHeader.getRecordLength()-tempRecordLength)+" extra bytes per record");
	                   tempTelling = false;
	           } */
	/*           }
	   return tempRow;
	   }
	 */

	/**
	 * Retrieve the name of the given column.
	 *
	 * @param inIndex �ndice.
	 *
	 * @return nombre del campo.
	 */
	public String getFieldName(int inIndex) {
		byte[] bytes = myHeader.getFieldName(inIndex).trim().getBytes();
		String result;
		try {
			result = new String(bytes, charSet.name());
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return myHeader.getFieldName(inIndex).trim();
	}

	/**
	 * Retrieve the type of the given column.
	 *
	 * @param inIndex �ndice.
	 *
	 * @return tipo de campo.
	 */
	public char getFieldType(int inIndex) {
		return myHeader.getFieldType(inIndex);
	}

	/**
	 * Retrieve the length of the given column.
	 *
	 * @param inIndex indice.
	 *
	 * @return longitud del field.
	 */
	public int getFieldLength(int inIndex) {
		return myHeader.getFieldLength(inIndex);
	}

	/*
	 * Retrieve the value of the given column as string.
	 *
	 * @param idField DOCUMENT ME!
	 * @param idRecord DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	         public Object getFieldValue(int idField, long idRecord) throws IOException {
	             Object[] tmpReg = getRecord(idRecord);
	             return tmpReg[idField];
	         }
	 */
	/*
	 * DOCUMENT ME!
	 *
	 * @param idField DOCUMENT ME!
	 * @param idRecord DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	         public double getFieldValueAsDouble(int idField, int idRecord) throws IOException {
	             Object[] tmpReg = getRecord(idRecord);
	             return (double) Double.parseDouble(tmpReg[idField].toString());
	         }
	 */

	/**
	 * Retrieve the location of the decimal point.
	 *
	 * @param inIndex �ndice.
	 *
	 * @return localizaci�n.
	 */
	public int getFieldDecimalLength(int inIndex) {
		return myHeader.getFieldDecimalCount(inIndex);
	}

	/**
	 * read the DBF file into memory.
	 *
	 * @param file Fichero.
	 *
	 * @throws IOException
	 */
	public void open(File file) throws IOException {
		fin = new FileInputStream(file);
		channel = fin.getChannel();

        // buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        buffer = new BigByteBuffer2(channel, FileChannel.MapMode.READ_ONLY);

		// create the header to contain the header information.
		myHeader = new DbaseFileHeaderNIO();
		myHeader.readHeader(buffer);

		charBuffer = CharBuffer.allocate(myHeader.getRecordLength() - 1);
		String charSetName = DbfEncodings.getInstance().getCharsetForDbfId(myHeader.getLanguageID());
		if (charSetName == null)
		{
			charSet = defaultCharset; //Charset.defaultCharset();
//			System.out.println("Opening file " + file.getName() + " with languageId = " + myHeader.getLanguageID());
		}
		else
		{
//			System.out.println("Opening file " + file.getName() + " with encoding " + charSetName);
			if (charSetName.equalsIgnoreCase("UNKNOWN"))
				charSet = defaultCharset; //Charset.defaultCharset();
			else{
				try{
					charSet = Charset.forName(charSetName);
				}catch (UnsupportedCharsetException e) {
					charSet = defaultCharset; //Charset.defaultCharset();
				}
			}
		}

	}

	/**
	 * Removes all data from the dataset
	 *
	 * @throws IOException .
	 */
	public void close() throws IOException {
		fin.close();
		channel.close();
	}
	public static Charset getDefaultCharset() {
		return defaultCharset;
	}

	public static void setDefaultCharset(Charset defaultCharset) {
		DbaseFileNIO.defaultCharset = defaultCharset;
	}

	public void setCharSet(Charset charSet) {
		this.charSet = charSet;

	}

	public Charset getCharSet() {
		return charSet;
	}

	/**
	 * @return Returns the DbaseFileHeaderNIO.
	 */
	public DbaseFileHeaderNIO getDBaseHeader() {
		return myHeader;
	}
}
