package com.iver.cit.gvsig.project.documents.layout.fframes;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.MapContext;

public interface IFFrameUseFMap {
	public static final int AUTOMATICO = 0;
	public static final int CONSTANTE = 1;
	public static final int MANUAL = 2;

	public AffineTransform getATMap();

	public void setATMap(AffineTransform at);

	public MapContext getMapContext();

	public void refresh();

	public void setNewExtent(Rectangle2D r);

	public BufferedImage getBufferedImage();

	public void fullExtent() throws ReadDriverException;

	public void setPointsToZoom(Point2D px1, Point2D px2);

	public void movePoints(Point2D px1, Point2D px2);

	public int getTypeScale();

	public void refreshOriginalExtent();
}
