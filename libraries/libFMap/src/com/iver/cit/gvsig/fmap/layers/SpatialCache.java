package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Rectangle2D;
import java.util.List;

import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.ItemVisitor;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class SpatialCache {
	public int maxFeatures = 1000; // Por defecto, pero se puede cambiar
	int fastNumTotalRegs = 0;
	Quadtree quadTree = new Quadtree();

	public int getMaxFeatures() {
		return maxFeatures;
	}

	public void setMaxFeatures(int maxFeatures) {
		this.maxFeatures = maxFeatures;
	}

	/**
	 * Método de conveniencia
	 * 
	 * @param r
	 * @param igeometry
	 */
	public synchronized void insert(Rectangle2D bounds, IGeometry geom) {
		Envelope env = FConverter.convertRectangle2DtoEnvelope(bounds);
		this.insert(env, geom);
		// fastNumTotalRegs++;
	}

	public synchronized void query(Envelope searchEnv, ItemVisitor visitor) {
		quadTree.query(searchEnv, visitor);
	}

	public synchronized List query(Envelope searchEnv) {
		return quadTree.query(searchEnv);
	}

	public void insert(Envelope itemEnv, Object item) {
		quadTree.insert(itemEnv, item);
		fastNumTotalRegs++;
	}

	public boolean remove(Envelope itemEnv, Object item) {
		boolean resul = quadTree.remove(itemEnv, item);
		if (resul)
			fastNumTotalRegs--;
		return resul;
	}

	public int size() {
		return fastNumTotalRegs;
	}

	public void clearAll() {
		quadTree = new Quadtree();
		fastNumTotalRegs = 0;
	}

	public void remove(Rectangle2D bounds, IGeometry geom) {
		Envelope env = FConverter.convertRectangle2DtoEnvelope(bounds);
		this.remove(env, geom);
	}

}
