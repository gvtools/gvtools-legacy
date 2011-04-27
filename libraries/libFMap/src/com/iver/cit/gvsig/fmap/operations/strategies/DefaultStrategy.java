/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.fmap.operations.strategies;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.BitSet;

import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.log4j.Logger;
import org.cresques.cts.ICoordTrans;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.ClassifiableVectorial;
import com.iver.cit.gvsig.fmap.layers.layerOperations.Selectable;
import com.iver.cit.gvsig.fmap.layers.layerOperations.SingleLayer;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.utiles.swing.threads.Cancellable;
import com.iver.utiles.swing.threads.CancellableMonitorable;


/**
 * Implementa la Strategy por defecto. Los métodos que tendrán en común la
 * mayor parte de las estrategias
 */
public class DefaultStrategy implements Strategy {
    public static final int EQUALS = 0;
    public static final int DISJOINT = 1;
    public static final int INTERSECTS = 2;
    public static final int TOUCHES = 3;
    public static final int CROSSES = 4;
    public static final int WITHIN = 5;
    public static final int CONTAINS = 6;
    public static final int OVERLAPS = 7;

	private static Logger logger = Logger.getLogger(DefaultStrategy.class.getName());
	FLayer capa = null;

	/**
	 * Crea un nuevo DefaultStrategy.
	 *
	 * @param capa DOCUMENT ME!
	 */
	public DefaultStrategy(FLayer capa) {
		this.capa = capa;

		SingleLayer foo = (SingleLayer) capa;
		ClassifiableVectorial vectorial = (ClassifiableVectorial) capa;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByRect(java.awt.geom.Rectangle2D)
	 */
	public FBitSet queryByRect(Rectangle2D rect, CancellableMonitorable cancel) throws ReadDriverException, VisitorException {
		QueryByRectVisitor visitor = new QueryByRectVisitor();

		visitor.setRect(rect);
		process(visitor, cancel);
		return visitor.getBitSet();
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByShape(com.iver.cit.gvsig.fmap.fshape.IGeometry,
	 * 		int)
	 */
	public FBitSet queryByShape(IGeometry g, int relationship, CancellableMonitorable cancel)
		throws ReadDriverException, VisitorException {
		QueryByGeometryVisitor visitor = new QueryByGeometryVisitor(g, relationship);
		process(visitor);

		return visitor.getBitSet();
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#getSelectionBounds()
	 */
	public Rectangle2D getSelectionBounds() {
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#createIndex()
	 */
	public void createIndex() {
	}

	/**
	 * @throws ReadDriverException
	 * @see com.iver.cit.gvsig.fmap.operations.LayerOperations#draw(java.awt.image.BufferedImage,
	 * 		ISymbol, FShape)
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
		Cancellable cancel) throws ReadDriverException {
		try {
			ReadableVectorial adapter = ((SingleLayer) capa).getSource();
			ICoordTrans ct = getCapa().getCoordTrans();
//			logger.info("adapter.start()");
			adapter.start();

//			logger.info("getCapa().getRecordset().start()");
			SelectableDataSource rsSel = ((AlphanumericData) getCapa()).getRecordset();
			if (rsSel != null)
			    rsSel.start();

			int sc;
			Rectangle2D extent = viewPort.getAdjustedExtent();
			AffineTransform at = viewPort.getAffineTransform();

			sc = adapter.getShapeCount();
			// TODO: A revisar si es o no conveniente este sistema
			// de comunicación con los drivers.
			DriverAttributes attr = adapter.getDriverAttributes();
			boolean bMustClone = false;
			if (attr != null)
			{
			    if (attr.isLoadedInMemory())
			    {
			        bMustClone = attr.isLoadedInMemory();
			    }
			}
			IVectorLegend l = (IVectorLegend) ((ClassifiableVectorial) capa).getLegend();
            FBitSet bitSet = null;
            if (getCapa() instanceof Selectable){
                Selectable selection = (Selectable) getCapa();
                bitSet = selection.getSelection();
            }
			for (int i = 0; i < sc; i++) {
				if (cancel.isCanceled()) {
					break;
				}


				IGeometry geom = adapter.getShape(i);

				if (geom == null) {
					continue;
				}

				if (ct != null) {
				    if (bMustClone)
				        geom = geom.cloneGeometry();
					geom.reProject(ct);
				}

				// if (geom.intersects(extent)) {
				if (geom.fastIntersects(extent.getMinX(), extent.getMinY(),
				         extent.getWidth(), extent.getHeight())) {
					ISymbol symbol = l.getSymbol(i);

                    if (symbol ==null)
                        continue;
                    if (bitSet != null)
                        if (bitSet.get(i)) {
    						symbol = symbol.getSymbolForSelection();
    					}
                    if (symbol != null)
                    	geom.draw(g, viewPort, symbol, cancel);

				}
				/* else
				{
				    System.out.println("no pinto id=" + i);
				} */
			}

//			logger.info("getCapa().getRecordset().stop()");
			if (rsSel != null)
			    rsSel.stop();


//			logger.debug("adapter.stop()");
			adapter.stop();
			// TODO: A revisar si es o no conveniente este sistema
			// de comunicación con los drivers.
			// DriverAttributes attr = adapter.getDriverAttributes();
			/* if (attr != null)
			{
			    if (attr.isLoadedInMemory())
			    {
			        // Quitamos lo de la reproyección al vuelo para que
			        // solo se haga una vez.
			        getCapa().setCoordTrans(null);
			    }
			} */

		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getCapa().getName(),e);
		} catch (InitializeDriverException e) {
			throw new ReadDriverException(getCapa().getName(),e);
		}
	}


	/**
	 * Devuelve la capa.
	 *
	 * @return Returns the capa.
	 */
	public FLayer getCapa() {
		return capa;
	}

	/**
	 * @throws ExpansionFileReadException
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#process(com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor,
	 * 		java.util.BitSet)
	 */
	public void process(FeatureVisitor visitor, BitSet subset)
		throws ReadDriverException, ExpansionFileReadException, VisitorException {
		process(visitor, subset, null);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param visitor DOCUMENT ME!
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#process(com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor)
	 */
	public void process(FeatureVisitor visitor)
		throws ReadDriverException, VisitorException {
		process(visitor, (CancellableMonitorable)null);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#queryByPoint(Point2D,
	 * 		double)
	 */
	public FBitSet queryByPoint(Point2D p, double tolerance, CancellableMonitorable cancel)
		throws ReadDriverException, VisitorException {
        // TODO: OJO!!!!. Está implementado como un rectangulo.
        // Lo correcto debería ser calculando las distancias reales
        // es decir, con un círculo.
        Rectangle2D recPoint = new Rectangle2D.Double(p.getX() - (tolerance / 2),
                p.getY() - (tolerance / 2), tolerance, tolerance);
		return queryByRect(recPoint);
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.operations.strategies.Strategy#print(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort)
	 */
	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel, PrintRequestAttributeSet properties)
		throws ReadDriverException {
		// (jaume) symbols handle the PrintProperties automatically, the following is unnecessary
//		if (capa instanceof FLyrVect) {
//			((FLyrVect)capa).beforePrinting(properties);
//			draw(null, g, viewPort, cancel); // Quiero ejecutar el draw del padre, que es el que va sin acelaración!!
//			((FLyrVect)capa).afterPrinting();
//		}else{
//			draw(null, g, viewPort, cancel); // Quiero ejecutar el draw del padre, que es el que va sin acelaración!!
//		}
		draw(null, g, viewPort, cancel);
	}

	public void process(FeatureVisitor visitor, Rectangle2D rectangle) throws ReadDriverException, ExpansionFileReadException, VisitorException {
		process(visitor, rectangle, null);
	}

	/**
	 * Verifies cancelation events, and return a boolean flag
	 * if processes must be stopped for this cancelations events.
	 *
	 * @param cancel
	 * @param va
	 * @param visitor
	 * @return
	 */
	protected boolean verifyCancelation(Cancellable cancel, ReadableVectorial va, FeatureVisitor visitor){
		if(cancel != null){
			if(cancel.isCanceled()){
				try {
					va.stop();
					visitor.stop(capa);
				} catch (VisitorException e) {
					return false;
				} catch (ReadDriverException e) {
					return false;
				}
//				logger.info("visitor canceled");
				return true;
			}
		}
		return false;
	}

	public void process(FeatureVisitor visitor, BitSet subset, CancellableMonitorable cancel) throws ReadDriverException, ExpansionFileReadException, VisitorException {
//		try {
//			logger.info("visitor.start()");

			if (visitor.start(capa)) {
				ReadableVectorial va = ((SingleLayer) capa).getSource();
				try {
					va.start();
				} catch (InitializeDriverException e) {
					throw new ReadDriverException(getCapa().getName(),e);
				}
				for (int i = 0; i < va.getShapeCount(); i++) {

					if(verifyCancelation(cancel, va, visitor))
						return;
					if (subset.get(i)) {
						if(cancel != null){
							cancel.reportStep();
						}
						visitor.visit(va.getShape(i), i);
					}
				}
				va.stop();

//				logger.info("visitor.stop()");
				visitor.stop(capa);
			}
//		} catch (ExpansionFileReadException e) {
//			throw new ReadDriverException();
//		}
	}

	public void process(FeatureVisitor visitor, CancellableMonitorable cancel) throws ReadDriverException, VisitorException {
		try {
//			logger.info("visitor.start()");

			if (visitor.start(capa)) {
				ReadableVectorial va = ((SingleLayer) capa).getSource();
				ICoordTrans ct = getCapa().getCoordTrans();
				va.start();
				for (int i = 0; i < va.getShapeCount(); i++) {
					if(cancel != null){
						cancel.reportStep();
					}
					if(verifyCancelation(cancel, va, visitor))
						return;
				    IGeometry geom = va.getShape(i);
				    if (geom == null) {
						continue;
					}
				    if (ct != null) {
				    	if (!capa.getProjection().getAbrev().equals(capa.getMapContext().getViewPort().getProjection().getAbrev())){
				    		geom.reProject(ct);
				    	}
				    }

					visitor.visit(geom, i);
				}
				va.stop();
//				logger.info("visitor.stop()");
				visitor.stop(capa);
			}
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getCapa().getName(),e);
		} catch (InitializeDriverException e) {
			throw new ReadDriverException(getCapa().getName(),e);
		}
	}

	public void process(FeatureVisitor visitor, Rectangle2D rectangle, CancellableMonitorable cancel) throws ReadDriverException, ExpansionFileReadException, VisitorException {
		FilterRectVisitor filterVisitor = new FilterRectVisitor();
		filterVisitor.setRectangle(rectangle);
		filterVisitor.setWrappedVisitor(visitor);
		process(filterVisitor, cancel);
	}

	public FBitSet queryByPoint(Point2D p, double tolerance) throws ReadDriverException, VisitorException {
		return queryByPoint(p, tolerance, null);
	}

	public FBitSet queryByRect(Rectangle2D rect) throws ReadDriverException, VisitorException {
		return queryByRect(rect, null);
	}

	public FBitSet queryByShape(IGeometry g, int relationship) throws ReadDriverException, VisitorException {
		return queryByShape(g, relationship, null);
	}

	/**
	 * Tells when in a spatial query the use of an spatial index is an advanced,
	 * or it would be better to use a secuential search.
	 * <br>
	 * The criteria to decide is the area of the query region. If it is less than
	 * 1/4 of full extent layer region, the spatial index will be an improve.
	 * Else, a secuential scan would be better
	 * @param rectangle
	 * @return
	 * @throws ExpansionFileReadException
	 * @throws DriverException
	 */
	protected boolean isSpatialIndexNecessary(Rectangle2D extent) throws ReadDriverException, ExpansionFileReadException {
		FLyrVect lyr = (FLyrVect) getCapa();
		double areaExtent = extent.getWidth() * extent.getHeight();
		double areaFullExtent = lyr.getFullExtent().getWidth() *
			                                lyr.getFullExtent().getHeight();
		return areaExtent < (areaFullExtent / 4.0);

	}

}
