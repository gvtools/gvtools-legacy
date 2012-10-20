/*
 * Created on 08-mar-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ISpatialDB;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.fmap.layers.layerOperations.Selectable;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedVectorLegend;
import com.iver.utiles.swing.threads.Cancellable;
import com.iver.utiles.swing.threads.CancellableMonitorable;

/**
 * @author FJP
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DBStrategy extends DefaultStrategy {

	public DBStrategy(FLayer capa) {
		super(capa);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.operations.strategies.Strategy#draw(java.awt.
	 * image.BufferedImage, java.awt.Graphics2D,
	 * com.iver.cit.gvsig.fmap.ViewPort,
	 * com.iver.cit.gvsig.fmap.operations.Cancellable)
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
			Cancellable cancel) throws ReadDriverException {
		// Nos aprovechamos del SQL para lanzar la consulta
		// teniendo en cuenta el boundingbox que toca.
		FLyrVect lyr = (FLyrVect) getCapa();
		ISpatialDB dbAdapter = (ISpatialDB) ((FLyrVect) capa).getSource();
		// IVectorialDatabaseDriver dbDriver = (IVectorialDatabaseDriver)
		// dbAdapter.getDriver();
		try {
			dbAdapter.start();
			Selectable selectable = lyr.getRecordset();
			MathTransform trans = lyr.getCrsTransform();
			FBitSet bitSet = selectable.getSelection();

			String strEPSG = ProjectionUtils.getAbrev(viewPort.getCrs());
			// TODO: EXPLORAR LAS LEYENDAS PARA SABER LOS CAMPOS QUE VAMOS
			// A NECESITAR RECUPERAR, PARA INCLUIR EN LA CONSULTA SOLO
			// AQUELLOS QUE VAMOS A NECESITAR. CON ESO GANAREMOS VELOCIDAD.
			// => MODIFICAR EL getFeatureIterator para que admita los nombres
			// de los campos además del rectángulo que pides.
			String[] usedFields = null;
			IClassifiedVectorLegend l = null;
			if (lyr.getLegend() instanceof IClassifiedVectorLegend) {
				l = (IClassifiedVectorLegend) lyr.getLegend();

				usedFields = l.getClassifyingFieldNames();

			}

			Rectangle2D rectAux = viewPort.getAdjustedExtent();
			if (trans != null) {
				rectAux = ProjectionUtils.transform(rectAux, trans.inverse());
			}

			IFeatureIterator geomIt = dbAdapter.getFeatureIterator(rectAux,
					strEPSG, usedFields);
			if (geomIt == null) {
				// dbAdapter.stop();
				return;
			}

			DriverAttributes attr = dbAdapter.getDriverAttributes();
			boolean bMustClone = false;
			if (attr != null) {
				if (attr.isLoadedInMemory()) {
					bMustClone = attr.isLoadedInMemory();
				}
			}

			int i;
			ISymbol symbol;
			SpatialCache cache = lyr.getSpatialCache();
			cache.clearAll();
			while (geomIt.hasNext()) {
				if (cancel.isCanceled()) {
					geomIt.closeIterator();
					break;
				}
				IFeature feat = geomIt.next();
				if (feat == null) {
					continue;
				}
				IGeometry geom = feat.getGeometry();

				// System.out.println("PINTANDO FEATURE " + feat.getID());

				if (trans != null) {
					if (bMustClone)
						geom = geom.cloneGeometry();
					geom.reProject(trans);
				}

				i = dbAdapter.getRowIndexByFID(feat);
				// System.out.println("Antes de pintar " + i);
				// Value val = feat.getAttribute(0);
				symbol = l.getSymbolByFeature(feat);
				// symbol = l.getSymbol(i);
				if (symbol == null)
					continue;
				if (bitSet.get(i)) {
					symbol = symbol.getSymbolForSelection();
				}

				if (lyr.isSpatialCacheEnabled()) {
					if (cache.getMaxFeatures() >= cache.size()) {
						// Ya reproyectado todo
						cache.insert(geom.getBounds2D(), geom);
					}
				}

				// symbol = l.getDefaultSymbol();
				geom.drawInts(g, viewPort, symbol, null);

			}
			dbAdapter.stop();
		} catch (InitializeDriverException e) {
			throw new ReadDriverException(getCapa().getName(), e);
		} catch (NoninvertibleTransformException e) {
			throw new ReadDriverException(getCapa().getName(), e);
		}

	}

	/**
	 * Processes features of layer whose geometries would intersect rectangle
	 * passed as param by calling visitor's visit method.
	 * 
	 * FIXME FeatureVisitor is designed to work with memory and file drivers
	 * (visit(IGeometry, index), and to work with geometries (thats the reason
	 * for not to create Values, instead of pass an index)
	 * 
	 * But DBDrivers recovers IGeometry and Values in
	 * dbAdapter.getFeatureIterator method. We must add a visit(Feature) method
	 * to FeatureVisitor.
	 * 
	 * @throws ExpansionFileReadException
	 * 
	 * 
	 */
	public void process(FeatureVisitor visitor, Rectangle2D rect,
			CancellableMonitorable cancel) throws ReadDriverException,
			ExpansionFileReadException, VisitorException {
		FLyrVect lyr = (FLyrVect) getCapa();
		if (visitor.start(lyr)) {
			ISpatialDB dbAdapter = (ISpatialDB) ((FLyrVect) capa).getSource();
			IVectorialDatabaseDriver dbDriver = (IVectorialDatabaseDriver) dbAdapter
					.getDriver();
			try {
				dbAdapter.start();
			} catch (InitializeDriverException e) {
				throw new ReadDriverException(getCapa().getName(), e);
			}
			MathTransform trans = lyr.getCrsTransform();
			String strEPSG = ProjectionUtils.getAbrev(lyr.getCrs());
			Rectangle2D rectAux = rect;
			if (trans != null) {
				try {
					rectAux = ProjectionUtils.transform(rectAux,
							trans.inverse());
				} catch (NoninvertibleTransformException e) {
					throw new ReadDriverException(getCapa().getName(), e);
				}
			}
			IFeatureIterator geomIt = dbAdapter.getFeatureIterator(rectAux,
					strEPSG, null);
			if (geomIt == null) {
				return;
			}
			DriverAttributes attr = dbAdapter.getDriverAttributes();
			boolean bMustClone = false;
			if (attr != null) {
				if (attr.isLoadedInMemory()) {
					bMustClone = attr.isLoadedInMemory();
				}// if
			}// if
			int i;
			while (geomIt.hasNext()) {
				if (cancel != null) {
					cancel.reportStep();
				}
				IFeature feat = geomIt.next();
				IGeometry geom = feat.getGeometry();
				if (trans != null) {
					if (bMustClone) {
						geom = geom.cloneGeometry();
					}// if
					geom.reProject(trans);
				}// if
				i = dbDriver.getRowIndexByFID(feat);
				if (geom.intersects(rect)) {
					visitor.visit(geom, i);
				}
			}
			dbAdapter.stop();
			visitor.stop(lyr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy#queryByRect
	 * (java.awt.geom.Rectangle2D)
	 */
	public FBitSet queryByRect(Rectangle2D rect, CancellableMonitorable cancel)
			throws ReadDriverException, VisitorException {
		// Nos aprovechamos del SQL para lanzar la consulta
		// teniendo en cuenta el boundingbox que toca.
		FLyrVect lyr = (FLyrVect) getCapa();
		ISpatialDB dbAdapter = (ISpatialDB) ((FLyrVect) capa).getSource();
		// IVectorialDatabaseDriver dbDriver = (IVectorialDatabaseDriver)
		// dbAdapter.getDriver();
		try {
			dbAdapter.start();
			MathTransform trans = lyr.getCrsTransform();
			String strEPSG = ProjectionUtils.getAbrev(lyr.getCrs());
			Rectangle2D rectAux = rect;
			if (trans != null) {
				rectAux = ProjectionUtils.transform(rectAux, trans.inverse());
			}

			FBitSet selection = new FBitSet();

			IFeatureIterator geomIt = dbAdapter.getFeatureIterator(rectAux,
					strEPSG, null);
			if (geomIt == null) {
				return selection;
			}

			DriverAttributes attr = dbAdapter.getDriverAttributes();
			boolean bMustClone = false;
			if (attr != null) {
				if (attr.isLoadedInMemory()) {
					bMustClone = attr.isLoadedInMemory();
				}
			}

			int i;

			while (geomIt.hasNext()) {
				if (cancel != null) {
					cancel.reportStep();
					if (cancel.isCanceled()) {
						dbAdapter.stop();
						return selection;
					}
				}
				IFeature feat = geomIt.next();
				IGeometry geom = feat.getGeometry();

				if (trans != null) {
					if (bMustClone)
						geom = geom.cloneGeometry();
					geom.reProject(trans);
				}

				i = dbAdapter.getRowIndexByFID(feat);
				if (geom.intersects(rect))
					selection.set(i, true);
			}
			dbAdapter.stop();

			return selection;
		} catch (InitializeDriverException e) {
			throw new ReadDriverException(getCapa().getName(), e);
		} catch (NoninvertibleTransformException e) {
			throw new ReadDriverException(getCapa().getName(), e);
		}
	}
}
