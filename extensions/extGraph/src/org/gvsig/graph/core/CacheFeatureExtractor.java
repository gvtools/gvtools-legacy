/* gvSIG. Geographic Information System of the Valencian Government
*
* Copyright (C) 2007-2008 Infrastructures and Transports Department
* of the Valencian Government (CIT)
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
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
* MA  02110-1301, USA.
* 
*/

/*
* AUTHORS (In addition to CIT):
* 2009 Software Colaborativo (www.scolab.es)   development
*/
 
package org.gvsig.graph.core;

import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class CacheFeatureExtractor implements IFeatureExtractor {

	ArrayList<IFeature> feats;
	
	/**
	 * @param lyr The layer from features will be extracted.
	 * @param fields null if you want to use all the fields. If you need only some
	 * fields, put here their names.
	 */
	public CacheFeatureExtractor(FLyrVect lyr, String[] fields) {
		try {
			IFeatureIterator iterator = null;
			if (fields != null)
				iterator = lyr.getSource().getFeatureIterator(fields, null);
			else
				iterator = lyr.getSource().getFeatureIterator();
			
			while (iterator.hasNext()) {
				IFeature feat = iterator.next();
				feats.add(feat);
			}
			iterator.closeIterator();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public IFeature getFeature(long i) {
		return feats.get((int) i);
	}

	public Value getFieldValue(long idRec, int idField) {
		return getFeature(idRec).getAttribute(idField);
	}

	public IGeometry getGeometry(long i) {
		return getFeature(i).getGeometry();
	}

	public int getNumFeatures() {
		return feats.size();
	}

}

