/*
 * Created on 25-ene-2007 by azabala
 *
 */
package com.iver.cit.jdwglib.dwg.readers.objreaders.v15;

import com.iver.cit.jdwglib.dwg.DwgObject;

/**
 * @author alzabord
 */
public class DwgLayerControlReader15 extends AbstractDwg15Reader{

	/* (non-Javadoc)
	 * @see com.iver.cit.jdwglib.dwg.readers.IDwgObjectReader#readSpecificObj(int[], int, com.iver.cit.jdwglib.dwg.DwgObject)
	 */
	public void readSpecificObj(int[] data, int offset, DwgObject dwgObj) {
		//TODO Si no lo leemos, mejor ni considerarlo
		//Ver la especificación de este objeto
	}

}
