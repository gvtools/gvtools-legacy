package com.iver.cit.gvsig.fmap.spatialindex;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import spatialindex.rtree.RTree;
import spatialindex.spatialindex.IData;
import spatialindex.spatialindex.INode;
import spatialindex.spatialindex.IVisitor;
import spatialindex.spatialindex.Region;
import spatialindex.storagemanager.DiskStorageManager;
import spatialindex.storagemanager.IBuffer;
import spatialindex.storagemanager.IStorageManager;
import spatialindex.storagemanager.PropertySet;
import spatialindex.storagemanager.RandomEvictionsBuffer;

/**
 * <p>
 * RTree spatial index based in spatial index library: <br>
 * http://u-foria.org/marioh/spatialindex/index.html <br>
 * marioh@cs.ucr.edu
 * </p>
 * It has the problem that spatial index file creation is a bit slowly
 * (in comparation with other indexes).
 */
public class RTreeSptLib implements IPersistentSpatialIndex,
									INearestNeighbourFinder{
	/**
	 * Page size of associated file
	 */
	private static final int defaultPageSize = 32 * 1024;
	private static final double defaultFillFactor = 0.85d;

	/**
	 * Size of memory buffer of the index
	 */
	private static final int BUFFER_SIZE = 25000;
	RTree rtree;
	String rtreeFile;
	IStorageManager diskfile;
	/**
	 * Constructor
	 * @param overwriteFile tells if we must override existing files.
	 * If we are goint to create a new spatial index (or if we want to overwrite
	 * an existing one) we must to use always 'true'. If we want to load
	 * an existing spatial index file, overwrite must be 'false'
	 * @param fileName name of the rtree spatial index file
	 * @throws SpatialIndexException
	 */

	public RTreeSptLib(boolean overwriteFile, String fileName)
			throws SpatialIndexException {
		try {
			this.rtreeFile = fileName;
			PropertySet ps = new PropertySet();

			// overwrite the file if it exists.
			Boolean b = new Boolean(overwriteFile);
			ps.setProperty("Overwrite", b);

			// .idx and .dat extensions will be added.
			ps.setProperty("FileName", fileName);

			Integer i = new Integer(defaultPageSize);
			ps.setProperty("PageSize", i);
			diskfile = new DiskStorageManager(ps);
			load();
		} catch (SecurityException e) {
			//throw new SpatialIndexException("No tenemos permisos de escritura?", e);
			throw new SpatialIndexException();
		} catch (FileNotFoundException e) {
			//throw new SpatialIndexException("El fichero no existe", e);
			throw new SpatialIndexException();
		} catch (IOException e) {
			//throw new SpatialIndexException("Error de I/O", e);
			throw new SpatialIndexException();
		}
	}

	/**
	 * If the spatial index file exists and has content
	 */
	public boolean exists(){
		return (new File(rtreeFile+".dat").length() != 0);
	}


	class RTreeVisitor implements IVisitor{
		ArrayList solution = new ArrayList();
		public void visitNode(INode n) {
		}
		public void visitData(IData d) {
			solution.add(new Integer(d.getIdentifier()));
		}
		public List getSolution(){
			return solution;
		}
	}


	public List query(Rectangle2D rect) {
		List solution = null;
		Region region = createRegion(rect);
		RTreeVisitor visitor = new RTreeVisitor();
		rtree.intersectionQuery(region,visitor);
		solution = visitor.getSolution();
		return solution;
	}

	public List containtmentQuery(Rectangle2D rect) {
		List solution = null;
		Region region = createRegion(rect);
		RTreeVisitor visitor = new RTreeVisitor();
		rtree.containmentQuery(region,visitor);
		solution = visitor.getSolution();
		return solution;
	}

	/**
	 * Warn! This RTree implemention doesnt care if 'index'
	 * entry has been indexed yet
	 */
	public void insert(Rectangle2D rect, int index) {
		rtree.insertData(null, createRegion(rect), index);
	}

	private Region createRegion(Rectangle2D rect){
		Region region = null;
		double xmin = rect.getMinX();
		double ymin = rect.getMinY();
		double xmax = rect.getMaxX();
		double ymax = rect.getMaxY();
		double[] p1 = new double[]{xmin, ymin};
		double[] p2 = new double[]{xmax, ymax};
		region = new Region(p1, p2);
		return region;
	}

	public void delete(Rectangle2D rect, int index) {
		rtree.deleteData(createRegion(rect), index);
	}

	/**
	 * Looks for N indexes nearest to the specified rect.
	 * @param numberOfNearest
	 * @param rect
	 * @return
	 */
	public List findNNearest(int numberOfNearest, Rectangle2D rect) {
		List solution = null;
		Region region = createRegion(rect);
		RTreeVisitor visitor = new RTreeVisitor();
		rtree.nearestNeighborQuery(numberOfNearest, region,visitor);
		solution = visitor.getSolution();
		return solution;
	}

	/**
	 * Looks for the N indexes nearest to the specified point
	 * @param numberOfNearest
	 * @param point
	 * @return
	 */
	public List findNNearest(int numberOfNearest, Point2D point) {
		List solution = null;
		spatialindex.spatialindex.Point sptPoint = new
			spatialindex.spatialindex.Point(new double[]{point.getX(), point.getY()});
		RTreeVisitor visitor = new RTreeVisitor();
		rtree.nearestNeighborQuery(numberOfNearest, sptPoint,visitor);
		solution = visitor.getSolution();
		return solution;
	}



	public void flush(){
		rtree.flush();
	}

	public static void main(String[] args){
		try {
			File file = new File("c:/kk/pruebartree.idx");
			System.out.println(file.getName());
			RTreeSptLib rtree = new RTreeSptLib(false, "c:/pruebartree");
			if(rtree.exists()){
				System.out.println("Fichero ya creado");
				List items = rtree.query(new Rectangle2D.Double(399,0,400,4000));
				for(int i = 0; i < items.size(); i++){
					System.out.println((Integer)items.get(i));
				}
			}
			rtree.insert(new Rectangle2D.Double(0,0, 400, 4000), 1);
			rtree.insert(new Rectangle2D.Double(0,110, 2000, 5000), 2);
			rtree.insert(new Rectangle2D.Double(110,0, 4000, 4000), 3);
			rtree.insert(new Rectangle2D.Double(10,110, 8000, 111000), 4);
			rtree.insert(new Rectangle2D.Double(1110,1110, 40, 22200), 5);
			rtree.insert(new Rectangle2D.Double(0,0, 40, 48540), 6);
			rtree.insert(new Rectangle2D.Double(0,0, 6330, 56400), 7);
			rtree.flush();

		} catch (SpatialIndexException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public void load() {
//		 applies a main memory random buffer on top of the persistent
		// storage manager
		IBuffer buffer = new RandomEvictionsBuffer(diskfile, BUFFER_SIZE, false);

		// Create a new, empty, RTree with dimensionality 2, minimum load
		// 70%, using "file" as
		// the StorageManager and the RSTAR splitting policy.
		PropertySet ps2 = new PropertySet();

		Double f = new Double(defaultFillFactor);
		ps2.setProperty("FillFactor", f);

		Integer i = new Integer(2);
		ps2.setProperty("Dimension", i);

		File file = new File(rtreeFile + ".dat");
		if(file.length() != 0){
			ps2.setProperty("IndexIdentifier", new Integer(1));
		}
		rtree = new RTree(ps2, buffer);
	}

	public void close() {
	}

}
