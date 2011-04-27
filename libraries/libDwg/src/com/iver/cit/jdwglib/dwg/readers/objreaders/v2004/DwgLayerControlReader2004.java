/*
 * Created on 25-ene-2007 by azabala
 *
 */
package com.iver.cit.jdwglib.dwg.readers.objreaders.v2004;

import java.util.ArrayList;
import java.util.Vector;

import com.iver.cit.jdwglib.dwg.CorruptedDwgEntityException;
import com.iver.cit.jdwglib.dwg.DwgHandleReference;
import com.iver.cit.jdwglib.dwg.DwgObject;
import com.iver.cit.jdwglib.dwg.DwgUtil;
import com.iver.cit.jdwglib.dwg.objects.DwgBlockControl;
import com.iver.cit.jdwglib.dwg.objects.DwgLayerControl;

/**
 * @author alzabord
 */
public class DwgLayerControlReader2004 extends AbstractDwg2004Reader{

	/* (non-Javadoc)
	 * @see com.iver.cit.jdwglib.dwg.readers.IDwgObjectReader#readSpecificObj(int[], int, com.iver.cit.jdwglib.dwg.DwgObject)
	 */
	public void readSpecificObj(int[] data, int offset, DwgObject dwgObj)throws RuntimeException, CorruptedDwgEntityException
		 {
		//TODO Si no lo leemos, mejor ni considerarlo
		//Ver la especificación de este objeto

		 if(! (dwgObj instanceof DwgLayerControl))
		    	throw new RuntimeException(this.getClass().getName()+" solo puede leer DwgLAyerControl");

		 DwgLayerControl blk = (DwgLayerControl) dwgObj;
			int bitPos = offset;
			blk.inserta();
			ArrayList v;

			v = DwgUtil.getBitLong(data, bitPos);
			bitPos = ((Integer)v.get(0)).intValue();
			int numReactors = ((Integer)v.get(1)).intValue();
			blk.setNumReactors(numReactors);

			v = DwgUtil.testBit(data, bitPos);
			bitPos = ((Integer)v.get(0)).intValue();
			boolean XdicFlag = ((Boolean) v.get(1)).booleanValue();

			v = DwgUtil.getBitShort(data, bitPos);
			bitPos = ((Integer)v.get(0)).intValue();
			int enumeration = ((Integer)v.get(1)).intValue();

			DwgHandleReference hr = new DwgHandleReference();
			bitPos = hr.read(data, bitPos);
			blk.setNullHandle(hr);

			if(!XdicFlag){
				hr = new DwgHandleReference();
				bitPos = hr.read(data, bitPos);
				blk.setXDicObjHandle(hr);
			}
			Vector handles = new Vector();
			hr = new DwgHandleReference();
			bitPos = hr.read(data, bitPos);
			handles.add(hr);

			blk.setCode2Handles(handles);

	}

}
