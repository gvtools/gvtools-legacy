/*
 * Created on 16-feb-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hardcode.gdbms.driver.dbf_Fernando;

/**
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;

/**
 * Class to read and write data to a dbase III format file. Creation date:
 * (5/15/2001 5:15:13 PM)
 */
public class DbaseFile {
	// Header information for the DBase File
	private DbaseFileHeader myHeader;
	private FileInputStream fin;
	private FileChannel channel;
	private ByteBuffer buffer;

	// Retrieve number of records in the DbaseFile
	public int getRecordCount() {
		return myHeader.getNumRecords();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getFieldCount() {
		return myHeader.getNumFields();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean getBooleanFieldValue(int rowIndex, int fieldId) {
		int recordOffset = (myHeader.getRecordLength() * rowIndex)
				+ myHeader.getHeaderLength() + 1;

		// Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < (fieldId - 1); i++) {
			fieldOffset += myHeader.getFieldLength(i);
		}

		buffer.position(recordOffset + fieldOffset);

		char bool = (char) buffer.get();

		return ((bool == 't') || (bool == 'T') || (bool == 'Y') || (bool == 'y'));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getStringFieldValue(int rowIndex, int fieldId) {
		int recordOffset = (myHeader.getRecordLength() * rowIndex)
				+ myHeader.getHeaderLength() + 1;

		// Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < fieldId; i++) {
			fieldOffset += myHeader.getFieldLength(i);
		}

		buffer.position(recordOffset + fieldOffset);

		byte[] data = new byte[myHeader.getFieldLength(fieldId)];
		buffer.get(data);

		return new String(data);
	}

	// Retrieve the record at the given index

	/*
	 * public Object[] getRecord(long inIndex) throws IOException { long
	 * nRecordOffset = (myHeader.getRecordLength() * inIndex) +
	 * myHeader.getHeaderLength(); // retrieve the record length int
	 * tempNumFields = myHeader.getNumFields(); // storage for the actual values
	 * Object[] tempRow = new Object[tempNumFields]; buffer.position((int)
	 * nRecordOffset); // read the deleted flag char tempDeleted = (char)
	 * buffer.get(); // read the record length int tempRecordLength = 1; // for
	 * the deleted character just read. // read the Fields for (int j = 0; j <
	 * tempNumFields; j++) { // find the length of the field. int
	 * tempFieldLength = myHeader.getFieldLength(j); tempRecordLength =
	 * tempRecordLength + tempFieldLength; // find the field type char
	 * tempFieldType = myHeader.getFieldType(j);
	 * //System.out.print("Reading Name="
	 * +myHeader.getFieldName(j)+" Type="+tempFieldType
	 * +" Length="+tempFieldLength); // read the data. Object tempObject = null;
	 * switch (tempFieldType) { case 'L': // logical data type, one character
	 * (T,t,F,f,Y,y,N,n) char tempChar = (char) buffer.get(); if ((tempChar ==
	 * 'T') || (tempChar == 't') || (tempChar == 'Y') || (tempChar == 'y')) {
	 * tempObject = new Boolean(true); } else { tempObject = new Boolean(false);
	 * } break; case 'C': // character record. byte[] sbuffer = new
	 * byte[tempFieldLength]; buffer.get(sbuffer); tempObject = new
	 * String(sbuffer, "ISO-8859-1").trim(); break; case 'D': // date data type.
	 * byte[] dbuffer = new byte[8]; buffer.get(dbuffer); String tempString =
	 * new String(dbuffer, 0, 4); try { int tempYear =
	 * Integer.parseInt(tempString); tempString = new String(dbuffer, 4, 2); int
	 * tempMonth = Integer.parseInt(tempString) - 1; tempString = new
	 * String(dbuffer, 6, 2); int tempDay = Integer.parseInt(tempString);
	 * Calendar c = Calendar.getInstance(); c.set(Calendar.YEAR, tempYear);
	 * c.set(Calendar.MONTH, tempMonth); c.set(Calendar.DAY_OF_MONTH, tempDay);
	 * tempObject = c.getTime(); } catch (NumberFormatException e) { } break;
	 * case 'M': // memo field. byte[] mbuffer = new byte[10];
	 * buffer.get(mbuffer); break; case 'N': // number case 'F': // floating
	 * point number byte[] fbuffer = new byte[tempFieldLength];
	 * buffer.get(fbuffer); try { tempString = new String(fbuffer); tempObject =
	 * Double.valueOf(tempString.trim()); } catch (NumberFormatException e) { }
	 * break; default: byte[] defbuffer = new byte[tempFieldLength];
	 * buffer.get(defbuffer);
	 * System.out.println("Do not know how to parse Field type " +
	 * tempFieldType); } tempRow[j] = tempObject; //
	 * System.out.println(" Data="+tempObject); } // ensure that the full record
	 * has been read. if (tempRecordLength < myHeader.getRecordLength()) {
	 * byte[] tempbuff = new byte[myHeader.getRecordLength() -
	 * tempRecordLength]; buffer.get(tempbuff); /* if (tempTelling){
	 * System.out.println
	 * ("DBF File has "+(myHeader.getRecordLength()-tempRecordLength
	 * )+" extra bytes per record"); tempTelling = false; }
	 */
	/*
	 * } return tempRow; }
	 */

	/**
	 * Retrieve the name of the given column.
	 * 
	 * @param inIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getFieldName(int inIndex) {
		return myHeader.getFieldName(inIndex).trim();
	}

	/**
	 * Retrieve the type of the given column.
	 * 
	 * @param inIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public char getFieldType(int inIndex) {
		return myHeader.getFieldType(inIndex);
	}

	/**
	 * Retrieve the length of the given column.
	 * 
	 * @param inIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getFieldLength(int inIndex) {
		return myHeader.getFieldLength(inIndex);
	}

	/*
	 * Retrieve the value of the given column as string.
	 * 
	 * @param idField DOCUMENT ME!
	 * 
	 * @param idRecord DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * public Object getFieldValue(int idField, long idRecord) throws
	 * IOException { Object[] tmpReg = getRecord(idRecord); return
	 * tmpReg[idField]; }
	 */
	/*
	 * DOCUMENT ME!
	 * 
	 * @param idField DOCUMENT ME!
	 * 
	 * @param idRecord DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * public double getFieldValueAsDouble(int idField, int idRecord) throws
	 * IOException { Object[] tmpReg = getRecord(idRecord); return (double)
	 * Double.parseDouble(tmpReg[idField].toString()); }
	 */

	/**
	 * Retrieve the location of the decimal point.
	 * 
	 * @param inIndex
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getFieldDecimalLength(int inIndex) {
		return myHeader.getFieldDecimalCount(inIndex);
	}

	/**
	 * read the DBF file into memory.
	 * 
	 * @param file
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void open(File file) throws FileNotFoundDriverException {

		try {
			fin = new FileInputStream(file);
			channel = fin.getChannel();
			buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			// create the header to contain the header information.
			myHeader = new DbaseFileHeader();
			myHeader.readHeader(buffer);
		} catch (IOException e) {
			throw new FileNotFoundDriverException(file.getName(), e,
					file.getAbsolutePath());
		}
	}

	/**
	 * Removes all data from the dataset
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void close() throws IOException {
		fin.close();
		channel.close();
		buffer = null;
	}
}
