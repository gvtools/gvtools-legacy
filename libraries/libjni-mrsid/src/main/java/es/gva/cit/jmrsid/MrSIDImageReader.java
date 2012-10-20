/**********************************************************************
 * $Id: MrSIDImageReader.java 8460 2006-10-31 16:26:34Z nacho $
 *
 * Name:     MrSIDImageReader.java
 * Project:  JMRSID. Interfaz java to MrSID (Lizardtech).
 * Purpose:  
 * Author:   Nacho Brodin, brodin_ign@gva.es
 *
 **********************************************************************/
/*Copyright (C) 2004  Nacho Brodin <brodin_ign@gva.es>

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.


 [01] 01-Oct-2005 nbt New call to JNI function MrSIDImageReaderArrayNat to convert name string to char array.

 */

package es.gva.cit.jmrsid;

import java.io.IOException;

/**
 * 
 * @author Nacho Brodin <brodin_ign@gva.es>.<BR>
 *         Equipo de desarrollo gvSIG.<BR>
 *         http://www.gvsig.gva.es
 * @version 0.0
 * @link http://www.gvsig.gva.es
 */
public class MrSIDImageReader extends MrSIDImageReaderBase {

	private native long MrSIDImageReaderNat(String pszFilename);

	private native long MrSIDImageReaderArrayNat(byte[] b);

	private native void FreeMrSIDImageReaderNat(long cPtr);

	public MrSIDImageReader() {
	}

	/**
	 * Constructor
	 * 
	 * @param cPtr
	 *            dirección de memoria al objeto MrSIDImageReader de C.
	 */

	public MrSIDImageReader(long cPtr) {
		this.cPtr = cPtr;
	}

	/**
	 * Destructor
	 */

	protected void finalize() {
		if (cPtr != 0)
			this.close();
	}

	/**
	 * Cierra el MrSIDImageReader
	 * 
	 */
	public void close() {
		if (cPtr != 0) {
			FreeMrSIDImageReaderNat(cPtr);
			cPtr = 0;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param pszFilename
	 *            Nombre del fichero
	 * @throws MrSIDException
	 *             , IOException
	 */

	public MrSIDImageReader(String pszFilename) throws MrSIDException,
			IOException {

		/*
		 * if ((pszFilename == null) || (pszFilename.equals(""))) throw new
		 * MrSIDException("Nombre de fichero incorrecto");
		 * 
		 * File f = new File( pszFilename ); if(!f.exists()) throw new
		 * IOException("The file "+pszFilename+" don't exists");
		 * 
		 * if(!f.canRead()) throw new IOException("I can't read the file");
		 */

		cPtr = MrSIDImageReaderArrayNat(pszFilename.getBytes());

		if (cPtr == 0)
			throw new MrSIDException("Error in MrDID Open");

	}

	/**
	 * Obtiene el número de niveles
	 */
	public int getNumLevels() throws MrSIDException {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("mac os x")) {
			return getNumLevelsFromMinMagnification();
		} else {
			String msg1 = "Error en getNumLevels. No se ha obtenido un puntero valido a LTIImage";
			String msg2 = "La llamada nativa a getNumLevels ha devuelto un código de error";
			return baseSimpleFunction(2, msg1, msg2);
		}

	}

	/**
	 * Obtiene el número de niveles s partir de getMinMagnification. (Funcion
	 * creada para Mac sobre Power PC por los problemas con getNumLevels)
	 */
	public int getNumLevelsFromMinMagnification() throws MrSIDException {

		double mag = getMinMagnification();

		double aux = getWidth() * mag;
		int cont = 0;
		while (aux < getWidth()) {
			aux *= 2;
			cont++;
		}
		return cont;
	}

}