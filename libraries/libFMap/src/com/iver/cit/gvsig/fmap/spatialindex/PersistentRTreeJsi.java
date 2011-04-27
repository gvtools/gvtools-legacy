/*
 * Created on 13-jun-2007
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
* $Id: PersistentRTreeJsi.java 12380 2007-06-27 20:17:30Z azabala $
* $Log$
* Revision 1.1  2007-06-27 20:17:30  azabala
* new spatial index (rix)
*
*
*/
package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.imageio.stream.FileImageOutputStream;

import com.infomatiq.jsi.IntProcedure;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.RTree;

/**
 * Persistent spatial index which can resolve nearest neighbour queries.
 * <br>
 * 
 * To use:
 * 
 * PersistentRTreeJsi sptidx = new PersistentRtreeJsi("/home/kk");
 * if(sptidx.exists())
 *  sptidx.load();
 *  
 *  
 *  sptidx.add(rect, int);
 *  ...
 *  sptidx.add(rect2,int2);
 *  sptidx.flush();
 * 
 * @author azabala
 *
 */
public class PersistentRTreeJsi implements IPersistentSpatialIndex,
		INearestNeighbourFinder, ISpatialIndex {

	/**
	 * Spatial index in memory
	 */
	private RTree rtree;
	/**
	 * Spatial index file
	 */
	private File rtreeFile;
	
	private boolean hasChange = false;
	/**
	 * Spatial index file extension
	 */
	final String rExt = ".rix";
	
	private LinkedHashMap  rectangles;
	
	/**
	 * Constructor
	 * @param file path of the spatial index file
	 * @throws SpatialIndexException 
	 */
	public PersistentRTreeJsi(String file, boolean overwrite) throws SpatialIndexException {
		rtree = new RTree();
		Properties props = new Properties();
		rtree.init(props);
		rtreeFile = new File(file + rExt);
		rectangles = new LinkedHashMap();
		if(! overwrite)
			load();
	}
	
	
	public void flush() {
		try {
			if(! hasChange)
				return;
			RandomAccessFile file = new RandomAccessFile(rtreeFile,
															"rw");
			FileImageOutputStream output = new FileImageOutputStream(file);
			output.setByteOrder(ByteOrder.LITTLE_ENDIAN);
			int numShapes = rtree.size();
			output.writeInt(numShapes);
			
			Iterator iterator = rtree.iterator();
			int count = 0;
			while(iterator.hasNext()){
				Integer  idx = (Integer) iterator.next();
				Rectangle nr = (Rectangle) rectangles.get(idx);
				float xmin = nr.min[0];
				float ymin = nr.min[1];
				
				float xmax = nr.max[0];
				float ymax = nr.max[1];
				
				
				output.writeFloat(xmin);
				output.writeFloat(ymin);
				output.writeFloat(xmax);
				output.writeFloat(ymax);
				
				output.writeInt(idx.intValue());
				count++;
			}
			output.flush();
			output.close();
			file.close();
			hasChange = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean exists() {
		return rtreeFile.exists();
	}
	
	

	public void load() throws SpatialIndexException {
		try {
			if(! rtreeFile.exists()){
				return;
			}
			RandomAccessFile file = new RandomAccessFile(rtreeFile, "r");
			FileChannel channel = file.getChannel();
			MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			buf.order(ByteOrder.LITTLE_ENDIAN);
			int numShapes = buf.getInt();
			for(int i = 0; i < numShapes; i++){
				float xmin, ymin, xmax, ymax;
				int shapeIndex;
				xmin = buf.getFloat();
				ymin = buf.getFloat();
				xmax = buf.getFloat();
				ymax = buf.getFloat();
				shapeIndex = buf.getInt();
				
				Rectangle jsiRect = new Rectangle(xmin, ymin, xmax, ymax);
				rtree.add(jsiRect, shapeIndex);
				
				
			}
		}catch(Exception e){
			throw new SpatialIndexException(e);
		}
	}

	public void close() {
		rectangles.clear();
		rectangles = null;
	}

	
	//TODO Make this class public and remove duplicates with
	//RTreeJsi
	class ListIntProcedure implements IntProcedure{
		ArrayList solution = new ArrayList();
		
		public boolean execute(int arg0) {
			solution.add(new Integer(arg0));
			return true;
		}
		
		public List getSolution(){
			return solution;
		}
	}
	
	
	public List query(Rectangle2D rect) {
		ListIntProcedure solution = new ListIntProcedure();
		rtree.intersects(toJsiRect(rect), solution);
		return solution.getSolution();
	}

	public void insert(Rectangle2D rect, int index) {
		Rectangle jsiRect = toJsiRect(rect);
		rtree.add(jsiRect, index);
		rectangles.put(new Integer(index), jsiRect);
		hasChange = true;
	}

	
	public void delete(Rectangle2D rect, int index) {
		rtree.delete(toJsiRect(rect), index);
		rectangles.remove(new Integer(index));
		hasChange = true;
	}

	
	public List findNNearest(int numberOfNearest, Rectangle2D rect){
		return (List) rtree.nearest(toJsiRect(rect), numberOfNearest);
	}
	
	public List findNNearest(int numberOfNearest, Point2D point){
		com.infomatiq.jsi.Point jsiPoint =
			new com.infomatiq.jsi.Point((float)point.getX(),(float)point.getY());
		return (List) rtree.nearest(jsiPoint, numberOfNearest);
	}
	
	
//	TODO Make this method abstract  and remove duplicates with
	//RTreeJsi
	private Rectangle toJsiRect(Rectangle2D rect){
		Rectangle jsiRect = new Rectangle((float)rect.getMinX(),
				(float)rect.getMinY(),
				(float)rect.getMaxX(),
				(float)rect.getMaxY());
		return jsiRect;
	}
	
	public int size(){
		return rtree.size();
	}

}

