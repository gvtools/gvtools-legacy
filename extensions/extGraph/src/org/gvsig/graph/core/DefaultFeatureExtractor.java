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

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class DefaultFeatureExtractor implements IFeatureExtractor {

	private FLyrVect lyr;

	public DefaultFeatureExtractor(FLyrVect lyr) {
		this.lyr = lyr;
	}
	
	public IFeature getFeature(long i) {
		ReadableVectorial va = lyr.getSource();
		IFeature f = null;
		try {
			va.start();
			f = va.getFeature((int) i);
		    if (lyr.getCoordTrans() != null) {
		    	if (!lyr.getProjection().getAbrev().equals(lyr.getMapContext().getViewPort().getProjection().getAbrev())){
		    		IGeometry geom = f.getGeometry();
		    		geom.reProject(lyr.getCoordTrans());
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
			SelectableDataSource rs = lyr.getSource().getRecordset();
			rs.start();
			f = rs.getFieldValue(i, idField);
			rs.stop();
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
		    if (lyr.getCoordTrans() != null) {
		    	if (!lyr.getProjection().getAbrev().equals(lyr.getMapContext().getViewPort().getProjection().getAbrev())){
		    		geom.reProject(lyr.getCoordTrans());
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

