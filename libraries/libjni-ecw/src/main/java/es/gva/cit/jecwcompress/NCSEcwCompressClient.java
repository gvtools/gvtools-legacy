/**********************************************************************
 * $Id: NCSEcwCompressClient.java 4274 2006-03-06 07:32:00Z nacho $
 *
 * Name:     NCSEcwCompressClient.java
 * Project:  
 * Purpose:  
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
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

package es.gva.cit.jecwcompress;

/**
 * Esta clase contiene las funciones para b�sicas para la compresi�n de Ecw.
 * Para su uso debe seguirse la secuencia de operaciones l�gica:
 * <UL>
 * <LI>Creaci�n de objeto de esta clase NCSEcwCompressClient</LI>
 * <LI>Asignaci�n de par�metros de compresi�n con las operaciones set</LI>
 * <LI>Crear una clase servidora de datos que implemente el interfaz
 * ReadCallBack y herede de JniObject</LI>
 * <LI>Hacer un open con NCSEcwCompressOpen</LI>
 * <LI>ejecutar el compresor con NCSEcwCompress y cerrar con NCSEcwCompressClose
 * </LI>
 * </UL>
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR>
 *         Equipo de desarrollo gvSIG.<BR>
 *         http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class NCSEcwCompressClient extends JNIBase {

	private native long NCSEcwCompressClientNat();

	private native int NCSEcwCompressOpenNat(long cPtr,
			boolean bCalculateSizesOnly);

	private native int NCSEcwCompressNat(long cPtr, ReadCallBack read);

	private native int NCSEcwCompressCloseNat(long cPtr);

	private native void NCSEcwCompressCancelNat(long cPtr);

	private native void finalizeNat(long cPtr);

	private String inputFilename = null;
	private String outputFilename = null;
	private double targetCompression = 0.0;
	private int eCompressFormat = 0;
	private int eCompressHint = 0;
	private int nBlockSizeX = 0;
	private int nBlockSizeY = 0;
	private int nInOutSizeX = 0;
	private int nInOutSizeY = 0;
	private int nInputBands = 0;
	private int nOutputBands = 0;
	private long nInputSize = 0;
	private double fCellIncrementX = 0.0;
	private double fCellIncrementY = 0.0;
	private double fOriginX = 0.0;
	private double fOriginY = 0.0;
	private int eCellSizeUnits = 0;
	private String szDatum = null;
	private String szProjection = null;
	private double fActualCompression = 0.0;
	private double fCompressionSeconds = 0.0;
	private double fCompressionMBSec = 0.0;
	private long nOutputSize = 0;

	public byte[] buffer;
	private long readInfo;
	private int porcentaje;

	/**
	 * Esta funci�n es llamada desde C para inicializar el buffer que
	 * contendr� 1 linea de datos
	 */
	private void initialize() {
		buffer = new byte[nInOutSizeX * nInputBands];
	}

	/**
	 * Esta funci�n es llamada desde C para asignar la direcci�n de memoria
	 * de la estructura readInfo
	 */
	private void setReadInfo(long ptr) {
		readInfo = ptr;
	}

	/**
	 * Esta funci�n es llamada desde C para recuperar la direcci�n de
	 * memoria de readInfo
	 */
	private long getReadInfo() {
		return readInfo;
	}

	/**
	 * Devuelve la cantidad de imagen comprimida en tanto por cien.
	 */
	public int getPercent() {
		return porcentaje;
	}

	/**
	 * Contructor
	 * 
	 * @throws EcwException
	 *             Se produce cuando la llamada nativa devuelve un c�digo de
	 *             error
	 */
	public NCSEcwCompressClient() throws EcwException {

		cPtr = NCSEcwCompressClientNat();

		if (cPtr == 0)
			throw new EcwException(
					"Error en la creaci�n del objeto NCSEcwCompressClient");
	}

	/**
	 * Inicializa el compresor
	 * 
	 * @throws EcwException
	 *             Se produce cuando la llamada nativa devuelve un c�digo de
	 *             error
	 */
	public void NCSEcwCompressOpen(boolean bCalculateSizesOnly)
			throws EcwException {

		if (cPtr == 0)
			throw new EcwException(
					"Error en NCSEcwCompressOpen(). No hay una referencia v�lida al objeto NCSEcwCompressClient.");

		int error = NCSEcwCompressOpenNat(cPtr, bCalculateSizesOnly);

		if (error == -1)
			throw new EcwException(
					"Error en NCSEcwCompressOpen(). No se ha podido obtener un objeto NCSEcwCompress valido");
		else if (error != 0)
			throw new EcwException(
					"Error en NCSEcwCompressOpen(). La llamada nativa ha devuelto un error "
							+ NCSError.ErrorToString(error));

	}

	/**
	 * Realiza la funci�n de compresi�n
	 * 
	 * @throws EcwException
	 *             Se produce cuando la llamada nativa devuelve un c�digo de
	 *             error
	 */
	public void NCSEcwCompress(ReadCallBack read) throws EcwException {

		if (cPtr == 0)
			throw new EcwException(
					"Error en NCSEcwCompress(). No hay una referencia v�lida al objeto NCSEcwCompressClient.");

		if (read == null)
			throw new EcwException(
					"Error en NCSEcwCompress(). El par�metro ReadCallBack no puede ser nulo.");

		int error = NCSEcwCompressNat(cPtr, read);

		if (error == -1)
			throw new EcwException(
					"Error en NCSEcwCompress(). No se ha podido obtener un objeto NCSEcwCompress valido");
		else if (error != 0)
			throw new EcwException(
					"Error en NCSEcwCompress(). La llamada nativa ha devuelto un error "
							+ NCSError.ErrorToString(error));
	}

	/**
	 * Cierra el compresor
	 * 
	 * @throws EcwException
	 *             Se produce cuando la llamada nativa devuelve un c�digo de
	 *             error
	 */
	public void NCSEcwCompressClose() throws EcwException {

		if (cPtr == 0)
			throw new EcwException(
					"Error en NCSEcwCompress(). No hay una referencia v�lida al objeto NCSEcwCompressClient.");

		int error = NCSEcwCompressCloseNat(cPtr);

		if (error == -1)
			throw new EcwException(
					"Error en NCSEcwCompress(). No se ha podido obtener un objeto NCSEcwCompress valido");
		else if (error != 0)
			throw new EcwException(
					"Error en NCSEcwCompress(). La llamada nativa ha devuelto un error "
							+ NCSError.ErrorToString(error));
	}

	/**
	 * Cancela la compresi�n
	 * 
	 * @throws EcwException
	 *             Se produce cuando la llamada nativa devuelve un c�digo de
	 *             error
	 */
	public void NCSEcwCompressCancel() throws EcwException {

		if (cPtr == 0)
			throw new EcwException(
					"Error en NCSEcwCompress(). No hay una referencia v�lida al objeto NCSEcwCompressClient.");

		NCSEcwCompressCancelNat(cPtr);
	}

	/**
	 * @throws EcwException
	 *             Se produce cuando la llamada nativa devuelve un c�digo de
	 *             error
	 */
	public void finalize() throws EcwException {

		if (cPtr == 0)
			throw new EcwException(
					"Error en finalize(). No hay una referencia v�lida al objeto NCSEcwCompressClient y no se ha podido liberar la memoria.");

		// finalizeNat(cPtr);
	}

	/**
	 * Asigna el nombre del fichero de entrada
	 * 
	 * @param filename
	 *            Nombre del fichero
	 */
	public void setInputFilename(String filename) {
		inputFilename = filename;
	};

	/**
	 * Asigna el nombre del fichero de salida
	 * 
	 * @param filename
	 *            Nombre del fichero
	 */
	public void setOutputFilename(String filename) {
		outputFilename = filename;
	};

	/**
	 * Asigna el nivel de compresi�n
	 * 
	 * @param compress
	 *            nivel de compresi�n
	 */
	public void setTargetCompress(double compress) {
		targetCompression = compress;
	};

	/**
	 * Asigna el formato de compresi�n.
	 * 
	 * @param format
	 *            formato de compresi�n. Los valores que puede tomar son:
	 *            <UL>
	 *            <LI>COMPRESS_NONE = NCSCS_NONE</LI>
	 *            <LI>COMPRESS_UINT8 = NCSCS_GREYSCALE</LI>
	 *            <LI>COMPRESS_YUV = NCSCS_YUV</LI>
	 *            <LI>COMPRESS_MULTI = NCSCS_MULTIBAND</LI>
	 *            <LI>COMPRESS_RGB = NCSCS_sRGB</LI>
	 *            </UL>
	 */
	public void setCompressFormat(int format) {
		eCompressFormat = format;
	};

	/**
	 * Asigna el Compress Hint.
	 * 
	 * @param Compress
	 *            hint. Los valores que puede tomar son:
	 *            <UL>
	 *            <LI>COMPRESS_HINT_NONE = 0</LI>
	 *            <LI>COMPRESS_HINT_FAST = 1</LI>
	 *            </LI>COMPRESS_HINT_BEST = 2</LI>
	 *            <LI>COMPRESS_HINT_INTERNET = 3</LI>
	 *            </UL>
	 */
	public void setCompressHint(int hint) {
		eCompressHint = hint;
	};

	/**
	 * Asigna el tama�o de bloque en x
	 * 
	 * @param n
	 *            tama�o de bloque en x
	 */
	public void setBlockSizeX(int n) {
		nBlockSizeX = n;
	};

	/**
	 * Asigna el tama�o de bloque en y
	 * 
	 * @param n
	 *            tama�o de bloque en y
	 */
	public void setBlockSizeY(int n) {
		nBlockSizeY = n;
	};

	/**
	 * Asigna el tama�o de la imagen de salida en x
	 * 
	 * @param n
	 *            tama�o de imagen de salida en x
	 */
	public void setInOutSizeX(int n) {
		nInOutSizeX = n;
	};

	/**
	 * Asigna el tama�o de la imagen de salida en y
	 * 
	 * @param n
	 *            tama�o de imagen de salida en y
	 */
	public void setInOutSizeY(int n) {
		nInOutSizeY = n;
	};

	/**
	 * Asigna el n�mero de bandas de entrada
	 * 
	 * @param n
	 *            N�mero de bandas de entrada
	 */
	public void setInputBands(int n) {
		nInputBands = n;
	};

	/**
	 * Asigna el n�mero de bandas de salida
	 * 
	 * @param n
	 *            N�mero de bandas de salida
	 */
	public void setOutputBands(int n) {
		nOutputBands = n;
	};

	public void setInputSize(long nis) {
		nInputSize = nis;
	};

	public void setCellIncrementX(double x) {
		fCellIncrementX = x;
	};

	public void setCellIncrementY(double y) {
		fCellIncrementY = y;
	};

	public void setOriginX(double x) {
		fOriginX = x;
	};

	public void setOriginY(double y) {
		fOriginY = y;
	};

	/**
	 * Asigna el tama�o de celda
	 * 
	 * @param cellu
	 *            tama�o de celda. Puede tomar un valor entre los siguientes:
	 *            <UL>
	 *            <LI>ECW_CELL_UNITS_INVALID = 0</LI>
	 *            <LI>ECW_CELL_UNITS_METERS = 1</LI>
	 *            <LI>ECW_CELL_UNITS_DEGREES = 2</LI>
	 *            <LI>ECW_CELL_UNITS_FEET = 3</LI>
	 *            <LI>ECW_CELL_UNITS_UNKNOWN = 4</LI>
	 *            </UL>
	 */
	public void setCellSizeUnits(int cellu) {
		eCellSizeUnits = cellu;
	};

	/**
	 * Asigna el datum
	 * 
	 * @param dat
	 *            datum
	 */
	public void setDatum(String dat) {
		szDatum = dat;
	};

	/**
	 * Asigna la proyecci�n
	 * 
	 * @param proj
	 *            Proyecci�n
	 */
	public void setProjection(String proj) {
		szProjection = proj;
	};

	public void setActualCompression(double comp) {
		fActualCompression = comp;
	};

	public void setCompressionSeconds(double comp) {
		fCompressionSeconds = comp;
	};

	public void setCompressionMBSec(double comp) {
		fCompressionMBSec = comp;
	};

	public void setOutputSize(long n) {
		nOutputSize = n;
	};
}
