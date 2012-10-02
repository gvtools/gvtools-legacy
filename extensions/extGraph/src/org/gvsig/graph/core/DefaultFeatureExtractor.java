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
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class DefaultFeatureExtractor implements IFeatureExtractor {

	private FLyrVect lyr;
	
	// FIXME: ONLY SUITABLE IF USED IN POPULATE ROUTE BECAUSE ALWAYS ACCESS THE SAME FIELD
	private ArrayList<Value> cacheStreetNames = null;
	private Value nullValue;

	public DefaultFeatureExtractor(FLyrVect lyr) {
		this.lyr = lyr;
		nullValue = ValueFactory.createNullValue();
		int n = getNumFeatures();
		cacheStreetNames = new ArrayList<Value>(getNumFeatures());
		for (int i = 0; i <= n; i++) {
			cacheStreetNames.add(nullValue);
		}
	}
	
	public IFeature getFeature(long i) {
		ReadableVectorial va = lyr.getSource();
		IFeature f = null;
		try {
			va.start();
			f = va.getFeature((int) i);
		    if (lyr.getCrsTransform() != null) {
				if (!lyr.getCrs()
						.getName()
						.equals(lyr.getMapContext().getViewPort().getCrs()
								.getName())) {
		    		IGeometry geom = f.getGeometry();
		    		geom.reProject(lyr.getCrsTransform());
		    		f.setGeometry(geom);
		    	}
		    }
			
			va.stop();
		} catch (ExpansionFileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;
	}

	public Value getFieldValue(long i, int idField) {
		
		Value f = null;
		try {
			f= cacheStreetNames.get((int)i);
			if (f == nullValue) {				
				SelectableDataSource rs = lyr.getSource().getRecordset();
				rs.start();
				f = rs.getFieldValue(i, idField);
				cacheStreetNames.set((int) i, f);
				rs.stop();
			}
//			f = cacheStreetNames.get((int) i);
		} catch (ExpansionFileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return f;

	}

	public IGeometry getGeometry(long i) {
		ReadableVectorial va = lyr.getSource();
		IGeometry geom = null;
		try {
			va.start();
			geom = va.getShape((int) i);
		    if (lyr.getCrsTransform() != null) {
				if (!lyr.getCrs()
						.getName()
						.equals(lyr.getMapContext().getViewPort().getCrs()
								.getName())) {
		    		geom.reProject(lyr.getCrsTransform());
		    	}
		    }			
			va.stop();
		} catch (ExpansionFileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return geom;

	}

	public FLyrVect getLyrVect() {
		return lyr;
	}

	public int getNumFeatures() {
		try {
			return lyr.getSource().getShapeCount();
		} catch (ReadDriverException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}

