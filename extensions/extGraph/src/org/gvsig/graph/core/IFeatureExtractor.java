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

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/**
 * @author Francisco José Peñarrubia (fjp@scolab.es)
 *
 * Useful to deal with layers without fast random access (for example, PostGIS).
 * DefaultFeatureExtractor may use a Layer and va.getFeature().
 * CacheFeatureExtractor may use a cache.
 * This way, the algorithms will be more isolated from refactoring, and porting will be easier.
 */
public interface IFeatureExtractor {
	IFeature getFeature(long i);
	IGeometry getGeometry(long i);
	Value getFieldValue(long idRec, int idField);
	int getNumFeatures();
}

