/*
 * Created on 03-feb-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
 *   Av. Blasco Ibáñez, 50
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
/* CVS MESSAGES:
 *
 * $Id: SpatialCacheImpl.java 5412 2006-05-24 21:15:07Z azabala $
 * $Log$
 * Revision 1.1  2006-05-24 21:12:36  azabala
 * primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
 *
 * Revision 1.5  2006/03/21 19:30:56  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/02/20 19:43:35  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/02/17 16:34:11  azabala
 * *** empty log message ***
 *
 * Revision 1.2  2006/02/13 21:14:03  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/09 15:59:24  azabala
 * First version in CVS
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.agil.core.spatialindex.RTree;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO Solo trabaja con JTS Geometry porque hace uso de serialización para
 * gestionar el volcado de memoria a disco. Si hacemos IFeature y IGeometry
 * Serializable, e implementamos una serialización óptima (por ejemplo basada en
 * ShpWriter o algo así) podriamos gestionar con caché multinivel casi cualquier
 * cosa.
 * 
 * Ademas, la clave de la cache tiene que ser un String
 * (debe haber un bug). Por eso hago lo de pasar a String
 * al cachear a partir de idx (put(idx+"")
 * 
 * @author azabala
 * 
 */
public class SpatialCacheImpl implements SpatialCache {
	public static final int DEFAULT_CACHE_SIZE = 300;

	public static final MemoryStoreEvictionPolicy DEFAULT_EVICTION_POLICY = MemoryStoreEvictionPolicy.LFU;

	private boolean overflowToDisk = true;

	private boolean eternal = true;

	private Cache cache;

	private RTree rtree;
	
	private static SpatialCacheImpl instance;
	
	
	public static SpatialCacheImpl getInstance() throws IOException{
		if(instance == null){
			instance = new SpatialCacheImpl();
		}
		return instance;
	}
	
	/**
	 * It's a singleton cache because is an only recurse for all the system.
	 * Any thread, window or graphical component could use it to saves spatial
	 * objects.
	 * @throws IOException
	 * 
	 * TODO Key cache must be based in feature id and a layer id
	 * (by now is only based in feature id)
	 */
	private SpatialCacheImpl()throws IOException{
		CacheManager manager = CacheManager.create();
		String tempPath = 
			System.getProperty("java.io.tmpdir");
		cache = new Cache("gvsig.geospatial",
				DEFAULT_CACHE_SIZE,
				DEFAULT_EVICTION_POLICY,
				overflowToDisk,
				tempPath,// disk store path
				eternal,
				60,// time to live
				60,// time to idle
				false, // disk persistent
				0,
				null);
		manager.addCache(cache);
		rtree = new RTree(tempPath,"gvsig.geospatial_idx");
		rtree.create();
		rtree.open();
	}

	public Serializable getElement(long idx) {
		//esto es una guarreria pq no se q le pasa
		//a EHCache (no funciona bien)
		Element element = cache.get((idx+""));
		if(element != null)
			return element.getValue();
		else 
			return null;
	}

	/**
	 * Lo que devuelve son los indices de los candidatos
	 */
	public List getCandidatesIndexes(Envelope rect) {
		ArrayList solution = new ArrayList();
		Iterator idxIterator = rtree.iterator(rect);
		while(idxIterator.hasNext()){
			solution.add(idxIterator.next());
		}
		return solution;
	}

	public void remove(Envelope rect, Serializable object) {
		rtree.delete(rect);
		cache.remove(object);
	}

	public void put(long idx, Envelope env, Serializable object) {
		cache.put(new Element((idx+""), object));
		rtree.write(env, idx);
	}

	public List getKeys() {
		return cache.getKeys();
	}

}
