package com.iver.cit.gvsig.fmap.operations.strategies;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.resources.geometry.XRectangle2D;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FGraphicUtilities;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.utiles.swing.threads.Cancellable;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;


/**
 * Esta clase se encargará de dibujar de la forma más eficiente los temas de
 * anotaciones.
 *
 * @author Vicente Caballero Navarro
 */
public class AnnotationStrategy extends DefaultStrategy {
	private static Logger logger = Logger.getLogger(AnnotationStrategy.class.getName());
	private IMarkerSymbol markerSymbol;
	private Graphics2D graphics=null;
	private ViewPort viewPort=null;
	private double heightDefault=-1;

	{
		markerSymbol = SymbologyFactory.createDefaultMarkerSymbol();
		markerSymbol.setColor(Color.BLACK);
	}

	/**
     * Crea un nuevo AnotationStrategy.
     *
     * @param layer DOCUMENT ME!
     */
    public AnnotationStrategy(FLayer layer) {
        super(layer);
        capa = (FLyrAnnotation) layer;
        markerSymbol.setSize(5);
    }
    /**
	 * @see com.iver.cit.gvsig.fmap.operations.LayerOperations#draw(java.awt.image.BufferedImage,
	 * 		java.awt.Graphics2D, ISymbol, Cancellable)
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
		Cancellable cancel) throws ReadDriverException {
		Rectangle2D elExtent = viewPort.getAdjustedExtent();
		graphics=g;
		FLyrAnnotation lyrAnnotation=(FLyrAnnotation)capa;
		List lstIndexes=null;

		IVectorLegend l=(IVectorLegend)lyrAnnotation.getLegend();
		FBitSet bitSet=lyrAnnotation.getRecordset().getSelection();

		boolean inPixels=lyrAnnotation.isInPixels();
		FSymbol theSymbol = (FSymbol) l.getDefaultSymbol();
		theSymbol.setFontSizeInPixels(inPixels);
		System.out.println("Dibujando Anotaciones...");
		this.viewPort=viewPort;//capa.getFMap().getViewPort();
		AffineTransform at=viewPort.getAffineTransform();
		try {
			int sc;
			sc=lyrAnnotation.getSource().getShapeCount();
            // If area of needed extent is less than fullExtent / 4,
            // it will be worthy to use SpatialIndex.
            // Otherwhise, we will not use it.
			boolean bUseSpatialIndex = false;
            if(lyrAnnotation.getISpatialIndex() != null)
            {
            	if(isSpatialIndexNecessary(elExtent)){
            		lstIndexes = lyrAnnotation.getISpatialIndex().query(elExtent);
                    sc = lstIndexes.size();
                    System.out.println("LISTA DEL SPATIALINDEX.SIZE = " + sc);
                    bUseSpatialIndex = true;
            	}//if
            }//if

			FontMetrics metrics = g.getFontMetrics();
			SpatialCache cache = lyrAnnotation.getSpatialCache();
			cache.clearAll();
			int numOriginal;
			for (int numReg = 0; numReg < sc; numReg++) {
				if (cancel.isCanceled()){
					break;
				}
                if (bUseSpatialIndex){
                    Integer idRec = (Integer) lstIndexes.get(numReg);
                    numOriginal = idRec.intValue();
                }else{
                    numOriginal = numReg;
                }
				/* if (lyrAnnotation.getSource() instanceof EditableAdapter)
					numOriginal=((EditableAdapter)lyrAnnotation.getSource()).getCalculatedIndex(numOriginal);*/

				FLabel theLabel = lyrAnnotation.getLabel(numOriginal);
				if ((theLabel == null) || (theLabel.getOrig() == null))
					continue;


				Rectangle2D r=null;
				if (inPixels && lyrAnnotation.getMapping().getColumnHeight()==-1) {
					r=getDefaultBoundBoxinPixels(metrics,theLabel.getOrig(),theLabel.getString());
				}else {
					r=getBoundBox(theLabel.getOrig(),(float)theLabel.getHeight(), theLabel.getJustification(),theLabel.getString());
				}
				theLabel.setBoundBox(r);

				if (XRectangle2D.intersectInclusive(elExtent, r))
				{
					FPoint2D p=new FPoint2D(viewPort.fromMapPoint(new Point2D.Double(r.getX(),r.getY())));
					markerSymbol.draw(g, at, p, null);
					// FGraphicUtilities.DrawShape(g,at,p,symbolPoint);
					if (bitSet.get(numOriginal)) {
						FGraphicUtilities.DrawAnnotation(g, at, theSymbol, theLabel,metrics,true);
					}else{
						FGraphicUtilities.DrawAnnotation(g, at, theSymbol, theLabel,metrics,false);
					}


					if (lyrAnnotation.isSpatialCacheEnabled())
					{
						if (cache.getMaxFeatures() >= cache.size())
						{
							// 	Ya reproyectado todo
							IGeometry geo=ShapeFactory.createPoint2D(r.getX(),r.getY());
							Rectangle2D re= new Rectangle2D.Double(r.getX(),r.getY(),1,1);
							cache.insert(re, geo);
						}
					}
				} // XIntersects


			}

		//	 System.out.println("..................Fin del dibujado ..............");
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getCapa().getName(),e);
		}
		heightDefault=-1;
	}
	  /**
	 * @see com.iver.cit.gvsig.fmap.operations.LayerOperations#draw(java.awt.image.BufferedImage,
	 * 		java.awt.Graphics2D, ISymbol, Cancellable)
	 */
	public void print(BufferedImage image, Graphics2D g, ViewPort viewPort,
		Cancellable cancel) throws ReadDriverException {
		Rectangle2D elExtent = viewPort.getAdjustedExtent();
		graphics=g;
		FLyrAnnotation lyrAnnotation=(FLyrAnnotation)capa;
		List lstIndexes=null;

		IVectorLegend l=(IVectorLegend)lyrAnnotation.getLegend();
		FBitSet bitSet=lyrAnnotation.getRecordset().getSelection();

		boolean inPixels=lyrAnnotation.isInPixels();
		FSymbol theSymbol = (FSymbol) l.getDefaultSymbol();
		theSymbol.setFontSizeInPixels(inPixels);
		this.viewPort=viewPort;//capa.getFMap().getViewPort();
		AffineTransform at=viewPort.getAffineTransform();
		try {
			int sc;
			sc=lyrAnnotation.getSource().getShapeCount();
            // If area of needed extent is less than fullExtent / 4,
            // it will be worthy to use SpatialIndex.
            // Otherwhise, we will not use it.
			boolean bUseSpatialIndex = false;
            if(lyrAnnotation.getISpatialIndex() != null)
            {
            	if(isSpatialIndexNecessary(elExtent)){
            		lstIndexes = lyrAnnotation.getISpatialIndex().query(elExtent);
                    sc = lstIndexes.size();
                    bUseSpatialIndex = true;
            	}//if
            }//if

			FontMetrics metrics = g.getFontMetrics();
			//SpatialCache cache = lyrAnnotation.createSpatialCache();
			int numOriginal;
			for (int numReg = 0; numReg < sc; numReg++) {
				if (cancel.isCanceled()){
					break;
				}
                if (bUseSpatialIndex){
                    Integer idRec = (Integer) lstIndexes.get(numReg);
                    numOriginal = idRec.intValue();
                }else{
                    numOriginal = numReg;
                }
				/* if (lyrAnnotation.getSource() instanceof EditableAdapter)
					numOriginal=((EditableAdapter)lyrAnnotation.getSource()).getCalculatedIndex(numOriginal);*/

				FLabel theLabel = lyrAnnotation.getLabel(numOriginal);
				if ((theLabel == null) || (theLabel.getOrig() == null))
					continue;


				Rectangle2D r=null;
				if (inPixels && lyrAnnotation.getMapping().getColumnHeight()==-1) {
					r=getDefaultBoundBoxinPixels(metrics,theLabel.getOrig(),theLabel.getString());
				}else {
					r=getBoundBox(theLabel.getOrig(),(float)theLabel.getHeight(), theLabel.getJustification(),theLabel.getString());
				}
				theLabel.setBoundBox(r);

				if (XRectangle2D.intersectInclusive(elExtent, r))
				{
					FPoint2D p=new FPoint2D(viewPort.fromMapPoint(new Point2D.Double(r.getX(),r.getY())));
					markerSymbol.draw(g, at, p, null);
					//FGraphicUtilities.DrawShape(g,at,p,markerSymbol);
					if (bitSet.get(numOriginal)) {
						FGraphicUtilities.DrawAnnotation(g, at, theSymbol, theLabel,metrics,true);
					}else{
						FGraphicUtilities.DrawAnnotation(g, at, theSymbol, theLabel,metrics,false);
					}


				} // XIntersects


			}

		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getCapa().getName(),e);
		}
		heightDefault=-1;
	}

	 public Rectangle2D getDefaultBoundBoxinPixels(FontMetrics metrics,
			Point2D p, String s) {
		int w = metrics.stringWidth(s);
		double width = viewPort.toMapDistance(w);
		if (heightDefault == -1) {
			int h = metrics.getMaxAscent();
			heightDefault = viewPort.toMapDistance(h);
		}
		return new Rectangle2D.Double(p.getX(), p.getY(), width, heightDefault);

	}

	 /**
		 * Construcción del rectángulo
		 *
		 * @param p
		 * @param g
		 *            DOCUMENT ME!
		 * @param justification
		 *            DOCUMENT ME!
		 * @param vp
		 *            DOCUMENT ME!
		 *
		 * @return
		 */
    public Rectangle2D getBoundBox(Point2D p, float hp,
        int justification,String s) {
        //Rectangle2D bounding=null;
        if (((FLyrAnnotation)capa).isInPixels()){
        	graphics.setFont(graphics.getFont().deriveFont(hp));
        }else{
        	float alturaPixels = (float) ((hp * viewPort.getAffineTransform().getScaleX())*FLabel.SQUARE);
        	graphics.setFont(graphics.getFont().deriveFont(alturaPixels));
        }
        FontMetrics metrics = graphics.getFontMetrics();
        int w = metrics.stringWidth(s);
        double width = viewPort.toMapDistance(w);
        int h = metrics.getMaxAscent();
        double height = viewPort.toMapDistance(h);
        //double dist = viewPort.toMapDistance(3);
        return new Rectangle2D.Double(p.getX(), p.getY(), width, height);
       /* switch (justification) {
            case FLabel.LEFT_BOTTOM:
                bounding=justification(p, width,height, 0, 0);

                break;

            case FLabel.LEFT_CENTER:
            	 bounding=justification(p, width,height, 0, -(height / 2));

                break;

            case FLabel.LEFT_TOP:
            	 bounding=justification(p,width,height, 0, -height);

                break;

            case FLabel.CENTER_BOTTOM:
            	 bounding=justification(p, width,height, -(width / 2), -dist);

                break;

            case FLabel.CENTER_CENTER:
            	 bounding=justification(p, width,height, -(width / 2), -(height / 2));

                break;

            case FLabel.CENTER_TOP:
            	 bounding=justification(p, width,height, -(width / 2), -height);

                break;

            case FLabel.RIGHT_BOTTOM:
            	 bounding=justification(p, width,height, -width, -dist);

                break;

            case FLabel.RIGHT_CENTER:
            	 bounding=justification(p, width,height, -width, -(height / 2));

                break;

            case FLabel.RIGHT_TOP:
            	 bounding=justification(p, width,height, -width, -height);

                break;
        }

        return bounding;
        */
    }
  /*  private Rectangle2D justification(Point2D p, double w,double h, double x, double y) {
        Rectangle2D r=new Rectangle2D.Double(p.getX() + x, p.getY() - y, w, h);
        return r;
    }
    */

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByShape(com.iver.cit.gvsig.fmap.core.IGeometry, int)
     */
    public FBitSet queryByShape(IGeometry g, int relationship)
    throws ReadDriverException, VisitorException {
        // Si hay un índice espacial, lo usamos para hacer el query.
        FLyrVect lyr = (FLyrVect) capa;
//        if (lyr.getSpatialIndex() == null)
        if(lyr.getISpatialIndex() == null)
            return super.queryByShape(g, relationship);

        long t1 = System.currentTimeMillis();
        ReadableVectorial va = lyr.getSource();
        MathTransform trans = lyr.getCrsTransform();
        Rectangle2D bounds = g.getBounds2D();
//        Coordinate c1 = new Coordinate(bounds.getMinX(), bounds.getMinY());
//        Coordinate c2 = new Coordinate(bounds.getMaxX(), bounds.getMaxY());
//        Envelope env = new Envelope(c1, c2);
//        List lstRecs = lyr.getSpatialIndex().query(env);
        List lstRecs = lyr.getISpatialIndex().query(bounds);
        Integer idRec;
        FBitSet bitset = new FBitSet();
        Geometry jtsShape = g.toJTSGeometry();
        IntersectionMatrix m;
        int index;
            try {
				va.start();
			} catch (InitializeDriverException e) {
				throw new ReadDriverException(getCapa().getName(),e);
			}

            for (int i=0; i < lstRecs.size(); i++)
            {
                idRec = (Integer) lstRecs.get(i);
                index = idRec.intValue();
                IGeometry geom=getGeometry(((FLyrAnnotation)capa).getLabel(index).getBoundBox());
                if (trans != null) {
                    geom.reProject(trans);
                }
                Geometry jtsGeom = geom.toJTSGeometry();
                switch (relationship) {
                case CONTAINS:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isContains()) {
                        bitset.set(index, true);
                    }
                    break;

                case CROSSES:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isCrosses(jtsGeom.getDimension(), jtsShape.getDimension())) {
                        bitset.set(index, true);
                    }
                    break;

                case DISJOINT:
                    // TODO: CREO QUE EL DISJOINT NO SE PUEDE METER AQUI
                    m = jtsShape.relate(jtsGeom);
                    if (m.isDisjoint()) {
                        bitset.set(index, true);
                    }
                    break;

                case EQUALS:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isEquals(jtsGeom.getDimension(), jtsShape.getDimension())) {
                        bitset.set(index, true);
                    }
                    break;

                case INTERSECTS:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isIntersects()) {
                        bitset.set(index, true);
                    }
                    break;

                case OVERLAPS:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isOverlaps(jtsGeom.getDimension(), jtsShape.getDimension()))
                    {
                        bitset.set(index, true);
                    }

                    break;

                case TOUCHES:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isTouches(jtsGeom.getDimension(), jtsShape.getDimension()))
                    {
                        bitset.set(index, true);
                    }

                    break;

                case WITHIN:
                    m = jtsShape.relate(jtsGeom);
                    if (m.isWithin()) {
                        bitset.set(index, true);
                    }

                    break;
                }
            }
            va.stop();

        long t2 = System.currentTimeMillis();
        logger.debug("queryByShape optimizado sobre la capa " + lyr.getName() + ". " + (t2-t1) + " mseg.");
        return bitset;
    }
    public FBitSet queryByRect(Rectangle2D rect) throws ReadDriverException, VisitorException {
        // Si hay un índice espacial, lo usamos para hacer el query.
        FLyrAnnotation lyr = (FLyrAnnotation) capa;
//        if (lyr.getSpatialIndex() == null)
          if(lyr.getISpatialIndex() == null)
            return super.queryByRect(rect);

        ReadableVectorial va = lyr.getSource();
        MathTransform trans = lyr.getCrsTransform();
        Rectangle2D bounds = rect;
//        Coordinate c1 = new Coordinate(bounds.getMinX(), bounds.getMinY());
//        Coordinate c2 = new Coordinate(bounds.getMaxX(), bounds.getMaxY());
//        Envelope env = new Envelope(c1, c2);
//
//        List lstRecs = lyr.getSpatialIndex().query(env);
        //azabala
        List lstRecs = lyr.getISpatialIndex().query(bounds);
        Integer idRec;
        FBitSet bitset = new FBitSet();
        int index;
            try {
				va.start();
            } catch (InitializeDriverException e) {
				throw new ReadDriverException(getCapa().getName(),e);
			}
            DriverAttributes attr = va.getDriverAttributes();
            boolean bMustClone = false;
            if (attr != null)
            {
                if (attr.isLoadedInMemory())
                {
                    bMustClone = attr.isLoadedInMemory();
                }
            }

            for (int i=0; i < lstRecs.size(); i++)
            {
                idRec = (Integer) lstRecs.get(i);
                index = idRec.intValue();
                IGeometry geom=getGeometry(((FLyrAnnotation)capa).getLabel(index).getBoundBox());
                if (trans != null) {
                    if (bMustClone)
                        geom = geom.cloneGeometry();
                    geom.reProject(trans);
                }
                //System.out.println("Rectángulo de selección = "+ rect);
                //System.out.println("Rectángulo de la geometría = "+ geom.getBounds2D());
                if (geom.intersects(rect)){
                    bitset.set(index, true);
                }

            }
            va.stop();
        return bitset;

    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByPoint(java.awt.geom.Point2D, double)
     */
    public FBitSet queryByPoint(Point2D p, double tolerance)
    throws ReadDriverException, VisitorException {
        // TODO: OJO!!!!. Está implementado como un rectangulo.
        // Lo correcto debería ser calculando las distancias reales
        // es decir, con un círculo.
        Rectangle2D recPoint = new Rectangle2D.Double(p.getX() - (tolerance / 2),
                p.getY() - (tolerance / 2), tolerance, tolerance);
        return queryByRect(recPoint);
    }
    private IGeometry getGeometry(Rectangle2D r){
    	GeneralPathX resul = new GeneralPathX();
		Point2D[] vs=new Point2D[4];
		vs[0]=new Point2D.Double(r.getX(),r.getY());
    	vs[1]=new Point2D.Double(r.getMaxX(),r.getY());
    	vs[2]=new Point2D.Double(r.getMaxX(),r.getMaxY());
    	vs[3]=new Point2D.Double(r.getX(),r.getMaxY());
    	//vs[4]=new Point2D.Double(r.getX(),r.getY());
		for (int i = 0; i < vs.length; i++) {
			if (i == 0) {
				resul.moveTo(vs[i].getX(),vs[i].getY());
			} else {
				resul.lineTo(vs[i].getX(),vs[i].getY());
			}
		}
		resul.closePath();
		return ShapeFactory.createPolygon2D(resul);
    }
}
