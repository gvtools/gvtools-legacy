
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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

package com.iver.cit.gvsig.fmap.operation.strategies;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.cresques.cts.ICoordTrans;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.Annotation_Mapping;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy;
import com.iver.cit.gvsig.fmap.rendering.Annotation_Legend;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;


/**
 * Esta clase se encargará de dibujar de la forma más eficiente los temas de
 * anotaciones.
 *
 * @author Vicente Caballero Navarro
 */
public class Annotation_Strategy extends DefaultStrategy {
    private IMarkerSymbol symbolPoint = SymbologyFactory.createDefaultMarkerSymbol();
    private Annotation_Layer capa;
    private static AffineTransform ati=new AffineTransform();
    /**
     * Crea un nuevo AnotationStrategy.
     *
     * @param layer
     */
    public Annotation_Strategy(FLayer layer) {
        super(layer);
        capa = (Annotation_Layer) getCapa();
        symbolPoint.setSize(5);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByShape(com.iver.cit.gvsig.fmap.core.IGeometry,
     *      int)
     */
    public FBitSet queryByShape(IGeometry g, int relationship) throws ReadDriverException, VisitorException{
        // Si hay un índice espacial, lo usamos para hacer el query.
        FLyrVect lyr = capa;

        // if (lyr.getSpatialIndex() == null)
        if (lyr.getISpatialIndex() == null) {
            return super.queryByShape(g, relationship);
        }

        long t1 = System.currentTimeMillis();
        ReadableVectorial va = lyr.getSource();
        ICoordTrans ct = lyr.getCoordTrans();
        Rectangle2D bounds = g.getBounds2D();

        // Coordinate c1 = new Coordinate(bounds.getMinX(), bounds.getMinY());
        // Coordinate c2 = new Coordinate(bounds.getMaxX(), bounds.getMaxY());
        // Envelope env = new Envelope(c1, c2);
        // List lstRecs = lyr.getSpatialIndex().query(env);
        List lstRecs = lyr.getISpatialIndex().query(bounds);
        Integer idRec;
        FBitSet bitset = new FBitSet();
        Geometry jtsShape = g.toJTSGeometry();
        IntersectionMatrix m;
        int index;

        try {
            va.start();

            // Annotation_Legend aLegend=(Annotation_Legend)capa.getLegend();
            for (int i = 0; i < lstRecs.size(); i++) {
                idRec = (Integer) lstRecs.get(i);
                index = idRec.intValue();

                IGeometry geom = va.getShape(index);

                // FSymbol symbol=(FSymbol)aLegend.getSymbol(index);
                // IGeometry geom=aLegend.getTextWrappingGeometry(symbol,index);
                // IGeometry
                // geom=getGeometry(((Annotation_Layer)capa).getLabel(index).getBoundBox());
                if (ct != null) {
                    geom.reProject(ct);
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

                    if (m.isCrosses(jtsGeom.getDimension(),
                                jtsShape.getDimension())) {
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

                    if (m.isEquals(jtsGeom.getDimension(),
                                jtsShape.getDimension())) {
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

                    if (m.isOverlaps(jtsGeom.getDimension(),
                                jtsShape.getDimension())) {
                        bitset.set(index, true);
                    }

                    break;

                case TOUCHES:
                    m = jtsShape.relate(jtsGeom);

                    if (m.isTouches(jtsGeom.getDimension(),
                                jtsShape.getDimension())) {
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
        } catch (ExpansionFileReadException e) {
			throw new ReadDriverException(capa.getName(),e);
		}

        long t2 = System.currentTimeMillis();

        // logger.debug("queryByShape optimizado sobre la capa " + lyr.getName()
        // + ". " + (t2-t1) + " mseg.");
        return bitset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param rect DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws VisitorException
     * @throws ReadDriverException
     *
     * @throws DriverException DOCUMENT ME!
     */
    public FBitSet queryByRect(Rectangle2D rect) throws ReadDriverException, VisitorException {
        // Si hay un índice espacial, lo usamos para hacer el query.
        Annotation_Layer lyr = capa;
        ReadableVectorial va = lyr.getSource();
        ICoordTrans ct = lyr.getCoordTrans();
        Rectangle2D bounds = rect;
        // if (lyr.getSpatialIndex() == null)
        if (lyr.getISpatialIndex() == null) {
            return super.queryByRect(rect);
        }
        // Coordinate c1 = new Coordinate(bounds.getMinX(), bounds.getMinY());
        // Coordinate c2 = new Coordinate(bounds.getMaxX(), bounds.getMaxY());
        // Envelope env = new Envelope(c1, c2);
        //
        // List lstRecs = lyr.getSpatialIndex().query(env);
        // azabala
        List lstRecs = lyr.getISpatialIndex().query(bounds);
        Integer idRec;
        FBitSet bitset = new FBitSet();
        int index;

        try {
            va.start();

            DriverAttributes attr = va.getDriverAttributes();
            boolean bMustClone = false;

            if (attr != null) {
                if (attr.isLoadedInMemory()) {
                    bMustClone = attr.isLoadedInMemory();
                }
            }
            ViewPort vp=getCapa().getMapContext().getViewPort();
            SelectableDataSource sds=va.getRecordset();
            // Annotation_Legend aLegend=(Annotation_Legend)capa.getLegend();
            for (int i = 0; i < lstRecs.size(); i++) {
                idRec = (Integer) lstRecs.get(i);
                index = idRec.intValue();

                Annotation_Mapping mapping = capa.getAnnotatonMapping();
                NumericValue vRotation = (NumericValue) sds.getFieldValue(index,
                        mapping.getColumnRotate());
                NumericValue vHeight = (NumericValue) sds.getFieldValue(index,
                        mapping.getColumnHeight());
                NumericValue vStyle = (NumericValue) sds.getFieldValue(index,mapping.getColumnStyleFont());
                StringValue vType = (StringValue) sds.getFieldValue(index,mapping.getColumnTypeFont());
                Value vText = sds.getFieldValue(index,
                        mapping.getColumnText());
                va.start();
                IGeometry geom = va.getShape(i);
                va.stop();

                if (ct != null) {
                    if (bMustClone) {
                        geom = geom.cloneGeometry();
                    }

                    geom.reProject(ct);
                }
                geom.transform(vp.getAffineTransform());
                 Annotation_Legend aLegend=(Annotation_Legend)capa.getLegend();
                int unit=aLegend.getUnits();
                IGeometry geom1 = capa.getTextWrappingGeometryInPixels(unit,vHeight.floatValue(),
                        vText.toString(), vRotation.doubleValue(),vType.getValue(),vStyle.intValue(), index, vp,geom);
                geom1.transform(vp.getAffineTransform().createInverse());
                if (geom1.intersects(rect)) {
                    bitset.set(index, true);
                }
            }

            va.stop();
        } catch (ExpansionFileReadException e) {
			throw new ReadDriverException(capa.getName(),e);
		} catch (NoninvertibleTransformException e) {
			throw new ReadDriverException(capa.getName(),e);
		}

        return bitset;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByPoint(java.awt.geom.Point2D,
     *      double)
     */
    public FBitSet queryByPoint(Point2D p, double tolerance) throws ReadDriverException, VisitorException {
        // TODO: OJO!!!!. Está implementado como un rectangulo.
        // Lo correcto debería ser calculando las distancias reales
        // es decir, con un círculo.
        Rectangle2D recPoint = new Rectangle2D.Double(p.getX() -
                (tolerance / 2), p.getY() - (tolerance / 2), tolerance,
                tolerance);

        return queryByRect(recPoint);
    }
}
