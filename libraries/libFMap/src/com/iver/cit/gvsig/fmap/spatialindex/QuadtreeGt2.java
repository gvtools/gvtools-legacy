/*
 * Created on 16-may-2006
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
* $Id: QuadtreeGt2.java 10627 2007-03-06 17:10:21Z caballero $
* $Log$
* Revision 1.3  2007-03-06 17:08:59  caballero
* Exceptions
*
* Revision 1.2  2006/11/29 19:27:59  azabala
* bug fixed (caused when we query for a bbox which is greater or equal to a layer bbox)
*
* Revision 1.1  2006/05/24 21:58:04  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.geotools.data.DataSourceException;
import org.geotools.index.TreeException;
import org.geotools.index.quadtree.Node;
import org.geotools.index.quadtree.QuadTree;
import org.geotools.index.quadtree.StoreException;
import org.geotools.index.quadtree.fs.FileSystemIndexStore;
import org.geotools.index.quadtree.fs.IndexHeader;

import com.vividsolutions.jts.geom.Envelope;
/**
 * This Quadtree spatial index implementation is based
 * in a fork of org.geotools.index.quadtree.Quadtree implementation.
 * <br>
 * This implementation offers us:
 * <ol>
 * <li>Persistence of spatial index</li>
 * </ol>
 * We had to fork geotools quadtree for many reasons:
 * <ol>
 * <li>It was strongly dependent of SHP format, so it returned not only
 * a num of rectangle, it also returned byte offset of this rectangle in shp file</li>
 * <li>
 * Query artifact wasnt run well at all
 * </li>
 * </ol>
 * @author azabala
 *
 */
public class QuadtreeGt2 implements IPersistentSpatialIndex {
	/**
	 * Geotools quadtree implementation
	 */
	QuadTree quadtree;
	/**
	 * Persistent storage
	 */
	String quadtreeFile;
	/**
	 * Spatial index file extension
	 */
	final String qExt = ".qix";
	/**
	 * qix format has many versions, and allows
	 * different byte orders.
	 */
	String byteOrder;
	/**
	 * Bounds of the layer to index
	 */
	Envelope bounds;
	/**
	 * Number of records of the layer to index
	 */
	int numRecs = 0;

	boolean inMemory = false;
	/**
	 * Constructor.
	 * You must say a qix file path, and if you want to overwrite this file
	 * if exists. Also, you must specify how many geometries are going to index,
	 * and the bounding box of all the geometries.
	 *
	 *
	 * @param quadtreeFile qix file path
	 * @param byteOrder byte order (bigendian, etc)
	 * @param bounds Rectangle2D of all the geometries to index
	 * @param numRecords num of geometries to index
	 * @param overwrite if we want to overwrite a possible existing qix file
	 * @throws SpatialIndexException
	 */
	public QuadtreeGt2(String quadtreeFile,
			String byteOrder,
			Rectangle2D bounds,
			int numRecords,
			boolean overwrite) throws SpatialIndexException{

		this.quadtreeFile = quadtreeFile +  qExt;
		this.byteOrder = byteOrder;
		this.bounds = toJtsEnvelope(bounds);
		this.numRecs = numRecords;

		if(exists()){
			if(!overwrite){
				load();
				return;
			}
		}
		quadtree = new QuadTree(numRecs, this.bounds);
	}

	/**
	 * If the spatial index file exists and has content
	 */
	public boolean exists(){
		return (new File(quadtreeFile).length() != 0);
	}

	public void load() throws SpatialIndexException{
			try {
//				openQuadTreeInMemory();
				openQuadTree();
			} catch (StoreException e) {
				//throw new SpatialIndexException("Error al cargar el fichero qix", e);
				throw new SpatialIndexException();
			}

	}


	public List query(Rectangle2D rect) {
		try {
			return (List) queryQuadTree(toJtsEnvelope(rect));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList();
	}


	public void insert(Rectangle2D rect, int index) {
		try {
			quadtree.insert(index, toJtsEnvelope(rect));
		} catch (StoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Envelope toJtsEnvelope(Rectangle2D rect){
		double xmin = rect.getMinX();
		double xmax = rect.getMaxX();
		double ymin = rect.getMinY();
		double ymax = rect.getMaxY();
		return new Envelope(xmin, xmax, ymin, ymax);
	}


	public void delete(Rectangle2D rect, int index) {
		if(inMemory)
			quadtree.delete(toJtsEnvelope(rect), index);
	}

	void openQuadTree() throws StoreException{
		if (quadtree == null) {
			File file = new File(quadtreeFile);
			//Intento de cargar todo el quadtree en memoria
			FileSystemIndexStore store = new FileSystemIndexStore(file);
			quadtree = store.load();
		}
	}

	 void openQuadTreeInMemory() throws StoreException {
		if (quadtree == null) {
			File file = new File(quadtreeFile);
			//Intento de cargar todo el quadtree en memoria
			FileSystemIndexStore store = new FileSystemIndexStore(file);
			QuadTree filequadtree = store.load();
			quadtree = new QuadTree(filequadtree.getNumShapes(),
					filequadtree.getMaxDepth(),
					filequadtree.getRoot().getBounds());
			Stack nodes = new Stack();
			nodes.push(filequadtree.getRoot());
			while(nodes.size() != 0){
				Node node = (Node) nodes.pop();
				Envelope nodeEnv = node.getBounds();
				int[] shapeIds = node.getShapesId();
				for(int i = 0; i < shapeIds.length; i++){
					quadtree.insert(shapeIds[i], nodeEnv);
				}
				int numSubnodes = node.getNumSubNodes();
				for(int i = 0; i < numSubnodes; i++){
					nodes.push(node.getSubNode(i));
				}
			}//while
			filequadtree.close();
		}
	}

	/**
	 * QuadTree Query
	 *
	 * @param bbox
	 *
	 * @return
	 *
	 * @throws DataSourceException
	 * @throws IOException
	 * @throws TreeException
	 *             DOCUMENT ME!
	 * @throws StoreException
	 */
	private Collection queryQuadTree(Envelope bbox) throws
			IOException, TreeException, StoreException {

        List solution = null;
		if ((quadtree != null)){
				//&& !bbox.contains(quadtree.getRoot().getBounds())) {
			solution = quadtree.query(bbox);
//            tmp = quadtree.search(bbox);
//            if( tmp==null || !tmp.isEmpty())
//            	return tmp;

		}else
			solution = new ArrayList();
//		if( quadtree!=null )
//			quadtree.close();
//    	return null;
		return solution;
	}

	public void flush() {
		byte order = 0;
		if ((byteOrder == null) || byteOrder.equalsIgnoreCase("NM")) {
			order = IndexHeader.NEW_MSB_ORDER;
		} else if (byteOrder.equalsIgnoreCase("NL")) {
			order = IndexHeader.NEW_LSB_ORDER;
		}
	    File file = new File(quadtreeFile);
	    FileSystemIndexStore store = new FileSystemIndexStore(file, order);
	    try {
			store.store(quadtree);
		} catch (StoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			quadtree.close();
		} catch (StoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





//	private void buildQuadTree() throws TreeException {
//			ShapeFileIndexer indexer = new ShapeFileIndexer();
//			indexer.setIdxType(ShapeFileIndexer.QUADTREE);
//			indexer.setShapeFileName(shpURL.getPath());
//
//			try {
//				indexer.index(false, readWriteLock);
//			} catch (MalformedURLException e) {
//				throw new TreeException(e);
//			} catch (LockTimeoutException e) {
//				throw new TreeException(e);
//			} catch (Exception e) {
//				File f = new File(treeURL.getPath());
//
//				if (f.exists()) {
//					f.delete();
//				}
//
//				if (e instanceof TreeException) {
//					throw (TreeException) e;
//				} else {
//					throw new TreeException(e);
//				}
//			}
//		}
}




	/**
    private int buildRTree(ShapefileReader reader, File rtreeFile,
        boolean verbose)
        throws TreeException, LockTimeoutException, IOException {
        DataDefinition keyDef = new DataDefinition("US-ASCII");
        keyDef.addField(Integer.class);
        keyDef.addField(Long.class);

        FileSystemPageStore fps = new FileSystemPageStore(rtreeFile, keyDef,
                this.max, this.min, this.split);
        org.geotools.index.rtree.RTree rtree = new org.geotools.index.rtree.RTree(fps);
        Record record = null;
        Data data = null;

        int cnt = 0;

        while (reader.hasNext()) {
            record = reader.nextRecord();
            data = new Data(keyDef);

            //Aquí estamos indexando a partir del número de rectangulo
            //luego creo que el segundo valor lo podemos obviar.
            data.addValue(new Integer(++cnt));
            data.addValue(new Long(record.offset()));

            rtree.insert(new Envelope(record.minX, record.maxX, record.minY,
                    record.maxY), data);

            if (verbose && ((cnt % 500) == 0)) {
                System.out.print('.');
            }
        }

        rtree.close();

        return cnt;
    }
    */

