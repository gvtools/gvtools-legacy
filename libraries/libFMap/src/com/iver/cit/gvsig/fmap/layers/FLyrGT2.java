///*
// * Created on 20-sep-2005
// *
// * gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
// *
// * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
// *
// * For more information, contact:
// *
// *  Generalitat Valenciana
// *   Conselleria d'Infraestructures i Transport
// *   Av. Blasco Ib��ez, 50
// *   46010 VALENCIA
// *   SPAIN
// *
// *      +34 963862235
// *   gvsig@gva.es
// *      www.gvsig.gva.es
// *
// *    or
// *
// *   IVER T.I. S.A
// *   Salamanca 50
// *   46005 Valencia
// *   Spain
// *
// *   +34 963163400
// *   dac@iver.es
// */
//package com.iver.cit.gvsig.fmap.layers;
//
//import java.awt.Graphics2D;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.util.NoSuchElementException;
//
//import javax.print.attribute.PrintRequestAttributeSet;
//
//import org.geotools.data.DataStore;
//import org.geotools.data.DefaultQuery;
//import org.geotools.data.DefaultTransaction;
//import org.geotools.data.FeatureReader;
//import org.geotools.data.FeatureSource;
//import org.geotools.data.Query;
//import org.geotools.data.Transaction;
//import org.geotools.feature.Feature;
//import org.geotools.feature.IllegalAttributeException;
//import org.geotools.filter.AbstractFilter;
//import org.geotools.filter.BBoxExpression;
//import org.geotools.filter.FilterFactory;
//import org.geotools.filter.FilterType;
//import org.geotools.filter.GeometryFilter;
//import org.geotools.filter.IllegalFilterException;
//
//import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
//import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
//import com.iver.cit.gvsig.fmap.ViewPort;
//import com.iver.cit.gvsig.fmap.core.IGeometry;
//import com.iver.cit.gvsig.fmap.core.ShapeFactory;
//import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
//import com.iver.cit.gvsig.fmap.core.gt2.FLiteShape;
//import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
//import com.iver.cit.gvsig.fmap.core.v02.FConverter;
//import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
//import com.iver.utiles.swing.threads.Cancellable;
//import com.vividsolutions.jts.geom.Envelope;
//import com.vividsolutions.jts.geom.Geometry;
//
//public class FLyrGT2 extends FLyrSecuential {
//
//    DataStore dataStore;
//    String typeName;
//    String tableName;
//    static FilterFactory ff = FilterFactory.createFilterFactory();
//
//    public Rectangle2D getFullExtent() throws ReadDriverException {
//        Rectangle2D r;
//        Envelope jtsR = null;
//        try {
//            jtsR = dataStore.getFeatureSource(tableName).getBounds();
//        } catch (IOException e) {
//            throw new ReadDriverException(getName(),e);
//        }
//        r = FConverter.convertEnvelopeToRectangle2D(jtsR);
//        return r;
//    }
//
//    private void prepareDrawing()
//    {
//
//    }
//    private void drawSelectedFeatures()
//    {
//
//    }
//    public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
//            Cancellable cancel, double scale) throws ReadDriverException {
//        if (!isWithinScale(scale)) return;
//        try {
//            prepareDrawing();
//            FeatureSource featSource = dataStore.getFeatureSource(tableName);
//
//            Envelope env = new Envelope(viewPort.getExtent().getMinX(),
//                    viewPort.getExtent().getMinY(), viewPort.getExtent().getMaxX(),
//                    viewPort.getExtent().getMaxY());
//
////          create filter to select only features that satify 300000 >= "field" >= 100000
//
//            BBoxExpression bb = ff.createBBoxExpression(env);
//            GeometryFilter bboxFilter =
//                    ff.createGeometryFilter(AbstractFilter.GEOMETRY_BBOX);
//            bboxFilter.addRightGeometry(bb);
//            String strGeom = dataStore.getSchema(tableName).getDefaultGeometry().getName();
//            bboxFilter.addLeftGeometry(
//                    ff.createAttributeExpression(dataStore.getSchema(tableName),strGeom));
//
//            Query theQuery = new DefaultQuery(tableName, bboxFilter);
//            // Query theQuery = new DefaultQuery(tableName, Filter.NONE);
//            Transaction t = new DefaultTransaction();
//            FeatureReader reader = dataStore.getFeatureReader(theQuery, t);
//            Feature f = null;
//            IMarkerSymbol sym = SymbologyFactory.createDefaultMarkerSymbol();
//            while ( reader.hasNext() ) {
//                if (cancel.isCanceled()) {
//                    reader.close();
//                    break;
//                }
//
//               f = reader.next();
//               Geometry geom = f.getDefaultGeometry();
//               FLiteShape shpLite = new FLiteShape(geom);
//               IGeometry gAux = ShapeFactory.createGeometry(shpLite);
//               gAux.draw(g,viewPort,sym, cancel);
//            }
//
//            reader.close();
//
//        } catch (IOException e) {
//            throw new ReadDriverException(getName(),e);
//        } catch (IllegalFilterException e) {
//            throw new ReadDriverException(getName(),e);
//        } catch (NoSuchElementException e) {
//            throw new ReadDriverException(getName(),e);
//        } catch (IllegalAttributeException e) {
//            throw new ReadDriverException(getName(),e);
//        }
//
//    }
//
//    public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel,
//            double scale, PrintRequestAttributeSet properties) throws ReadDriverException {
//        // TODO Auto-generated method stub
//
//    }
//
//    /**
//     * @return Returns the dataStore.
//     */
//    public DataStore getDataStore() {
//        return dataStore;
//    }
//
//    /**
//     * @param dataStore The dataStore to set.
//     * @throws IOException
//     */
//    public void setDataStore(DataStore dataStore) throws IOException {
//        this.dataStore = dataStore;
////      feature type name is defaulted to the name of shapefile (without extension)
//        typeName = dataStore.getTypeNames()[0];
//    }
//
//    /**
//     * @param tableName The tableName to set.
//     */
//    public void setTableName(String tableName) {
//        this.tableName = tableName;
//    }
//
//	public void process(FeatureVisitor visitor, FBitSet subset) throws ReadDriverException, VisitorException {
//		// TODO Auto-generated method stub
//
//	}
//
//	public void process(FeatureVisitor visitor, Rectangle2D rect) throws ReadDriverException, VisitorException {
//		try
//		{
//
//	        Envelope env = FConverter.convertRectangle2DtoEnvelope(rect);
//
//
//	        BBoxExpression bb = ff.createBBoxExpression(env);
//	        GeometryFilter bboxFilter =
//	                ff.createGeometryFilter(FilterType.GEOMETRY_BBOX);
//	        bboxFilter.addRightGeometry(bb);
//	        String strGeom = dataStore.getSchema(tableName).getDefaultGeometry().getName();
//	        bboxFilter.addLeftGeometry(
//	                ff.createAttributeExpression(dataStore.getSchema(tableName),strGeom));
//
//	        Query theQuery = new DefaultQuery(tableName, bboxFilter);
//	        // Query theQuery = new DefaultQuery(tableName, Filter.NONE);
//	        Transaction t = new DefaultTransaction();
//	        FeatureReader reader = dataStore.getFeatureReader(theQuery, t);
//	        Feature f = null;
//	        while ( reader.hasNext() ) {
//	           f = reader.next();
//	           Geometry geom = f.getDefaultGeometry();
//	           FLiteShape shpLite = new FLiteShape(geom);
//	           IGeometry gAux = ShapeFactory.createGeometry(shpLite);
//	           visitor.visit(gAux, -1);
//	        }
//
//	        reader.close();
//
//    } catch (IOException e) {
//        throw new ReadDriverException(getName(),e);
//    } catch (IllegalFilterException e) {
//        throw new ReadDriverException(getName(),e);
//    } catch (NoSuchElementException e) {
//        throw new ReadDriverException(getName(),e);
//    } catch (IllegalAttributeException e) {
//        throw new ReadDriverException(getName(),e);
//    }
//
//
//	}
//
//	public void process(FeatureVisitor visitor) throws ReadDriverException, VisitorException {
//		// TODO Auto-generated method stub
//
//	}
//
//	public SelectableDataSource getRecordset() {
//		// TODO Auto-generated method stub
//		// fjp: simplificar SelectableDataSource y quitar los m�todos que no
//		// necesitamos. (getPrimeryKeys, getDataWare, getDataFactory... etc)
//		return null;
//	}
//
//}
