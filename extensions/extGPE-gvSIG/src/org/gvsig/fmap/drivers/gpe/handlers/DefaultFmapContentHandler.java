package org.gvsig.fmap.drivers.gpe.handlers;

import java.io.IOException;

import org.gvsig.fmap.drivers.gpe.exceptions.CurveConversionWarning;
import org.gvsig.fmap.drivers.gpe.exceptions.NotMultipleLayerWarning;
import org.gvsig.fmap.drivers.gpe.model.GPEBBox;
import org.gvsig.fmap.drivers.gpe.model.GPECurve;
import org.gvsig.fmap.drivers.gpe.model.GPEElement;
import org.gvsig.fmap.drivers.gpe.model.GPEFeature;
import org.gvsig.fmap.drivers.gpe.model.GPEGeometry;
import org.gvsig.fmap.drivers.gpe.model.GPEMultiGeometry;
import org.gvsig.fmap.drivers.gpe.model.GPEMultiLineGeometry;
import org.gvsig.fmap.drivers.gpe.model.GPEMultiPointGeometry;
import org.gvsig.fmap.drivers.gpe.model.GPEMultiPolygonGeometry;
import org.gvsig.fmap.drivers.gpe.model.GPEPolygon;
import org.gvsig.fmap.drivers.gpe.reader.AddFeatureToDriver;
import org.gvsig.fmap.drivers.gpe.reader.GPEVectorialDriver;
import org.gvsig.fmap.drivers.gpe.reader.IGPEDriver;
import org.gvsig.fmap.drivers.gpe.reader.KMLVectorialDriver;
import org.gvsig.fmap.drivers.gpe.utils.GPETypesConversion;
import org.gvsig.gpe.parser.GPEContentHandler;
import org.gvsig.gpe.parser.GPEErrorHandler;
import org.gvsig.gpe.parser.IAttributesIterator;
import org.gvsig.gpe.parser.ICoordinateIterator;

import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;

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
/* CVS MESSAGES:
 *
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public abstract class DefaultFmapContentHandler extends GPEContentHandler {
	protected AddFeatureToDriver addFeature = null;
	private int features = 0;
	private boolean hasLayer = false;
	private IGPEDriver driver = null;

	public DefaultFmapContentHandler(GPEErrorHandler errorHandler,
			IGPEDriver driver) {
		super();
		setErrorHandler(errorHandler);
		this.driver = driver;
		GPEFeature.initIdFeature();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.parser.GPEContentHandler#startLayer(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * org.gvsig.gpe.parser.IAttributesIterator, java.lang.Object,
	 * java.lang.Object)
	 */
	public Object startLayer(String id, String namespace, String name,
			String description, String srs,
			IAttributesIterator attributesIterator, Object parentLayer,
			Object box) {
		// Only one layer is supported
		if (hasLayer == false) {
			hasLayer = true;
			addFeature = new AddFeatureToDriver();
			// addFeature.setSchema(getSchemaDocument());
		} else {
			getErrorHandler().addWarning(new NotMultipleLayerWarning());
			// TODO patch to support multilayer on KML
			if (driver.getName().equals(KMLVectorialDriver.DRIVERNAME)) {
				return driver;
			}
			return null;
		}
		return driver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#endLayer(java.lang.Object)
	 */
	public void endLayer(Object layer) {
		IGPEDriver gpeDriver = (IGPEDriver) layer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.parser.GPEContentHandler#startPoint(java.lang.String,
	 * org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */
	public Object startPoint(String id, ICoordinateIterator coords, String srs) {
		double[] buffer = new double[coords.getDimension()];
		double y = 0.0;
		try {
			coords.hasNext();
			coords.next(buffer);
			return new GPEGeometry(id, ShapeFactory.createPoint2D(buffer[0],
					buffer[1]), srs);
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.parser.GPEContentHandler#startLineString(java.lang.String,
	 * org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */
	public Object startLineString(String id, ICoordinateIterator coords,
			String srs) {
		GeneralPathX gp = new GeneralPathX();
		double[] buffer = new double[coords.getDimension()];
		try {
			while (coords.hasNext()) {
				coords.next(buffer);
				gp.append(ShapeFactory.createPoint2D(buffer[0], buffer[1]),
						true);
			}
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
		return new GPEGeometry(id, ShapeFactory.createPolyline2D(gp), srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.parser.GPEContentHandler#startPolygon(java.lang.String,
	 * org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */

	public Object startPolygon(String id, ICoordinateIterator coords, String srs) {
		GeneralPathX gp = new GeneralPathX();
		double[] buffer = new double[coords.getDimension()];
		try {
			while (coords.hasNext()) {
				coords.next(buffer);
				gp.append(ShapeFactory.createPoint2D(buffer[0], buffer[1]),
						true);
			}
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
		return new GPEPolygon(id, gp, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.parser.GPEContentHandler#startInnerPolygon(java.lang.String
	 * , org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */
	public Object startInnerPolygon(String id, ICoordinateIterator coords,
			String srs) {
		GeneralPathX gp = new GeneralPathX();
		double[] buffer = new double[coords.getDimension()];
		try {
			while (coords.hasNext()) {
				coords.next(buffer);
				gp.append(ShapeFactory.createPoint2D(buffer[0], buffer[1]),
						true);
			}
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
		return new GPEPolygon(id, gp, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addGeometryToFeature(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addGeometryToFeature(Object geometry, Object feature) {
		((GPEFeature) feature).setGeometry((GPEGeometry) geometry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#addBboxToLayer(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addBboxToLayer(Object bbox, Object layer) {
		// if (layer != null){
		// GPEBBox gpeBBox = (GPEBBox)bbox;
		// if (gpeBBox.getSrs() != null){
		// IProjection projection = null;
		// try{
		// CRSFactory.getCRS(gpeBBox.getSrs());
		// }catch(Exception e){
		// //If the CRS factory has an error.
		// }
		// if ((projection != null) &&
		// (!(projection.equals(((FLayer)layer).getProjection())))){
		// //TODO reproyectar la bbox y asignarsela a la capa
		// }
		// }
		// ((IGPEDriver)layer).setExtent(gpeBBox.getBbox2D());
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addElementToFeature(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addElementToFeature(Object element, Object feature) {
		((GPEFeature) feature).addElement((GPEElement) element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#addFeatureToLayer(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addFeatureToLayer(Object feature, Object layer) {
		// If it is null is a multilayer: not supported yet
		if (layer != null) {
			addFeature.addFeatureToLayer((GPEVectorialDriver) layer,
					(GPEFeature) feature);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addInnerPolygonToPolygon(java.lang.Object
	 * , java.lang.Object)
	 */
	public void addInnerPolygonToPolygon(Object innerPolygon, Object Polygon) {
		((GPEPolygon) Polygon).addInnerPolygon(((GPEPolygon) innerPolygon));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#addNameToFeature(java.lang.String,
	 * java.lang.Object)
	 */
	public void addNameToFeature(String name, Object feature) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addParentElementToElement(java.lang.
	 * Object, java.lang.Object)
	 */
	public void addParentElementToElement(Object parent, Object element) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#addSrsToLayer(java.lang.String,
	 * java.lang.Object)
	 */
	public void addSrsToLayer(String srs, Object Layer) {
		// this.srs = srs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.parser.GPEContentHandler#startBbox(java.lang.String,
	 * org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */
	public Object startBbox(String id, ICoordinateIterator coords, String srs) {
		return new GPEBBox(id, coords, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.parser.GPEContentHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.Object,
	 * org.gvsig.gpe.parser.IAttributesIterator, java.lang.Object)
	 */
	public Object startElement(String namespace, String name, Object value,
			IAttributesIterator attributesIterator, Object parentElement) {
		return new GPEElement(name, GPETypesConversion.fromJavaTogvSIG(value),
				(GPEElement) parentElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.parser.GPEContentHandler#startFeature(java.lang.String,
	 * java.lang.String, java.lang.String,
	 * org.gvsig.gpe.parser.IAttributesIterator, java.lang.Object)
	 */
	public Object startFeature(String id, String name, String xsElementName,
			IAttributesIterator attributesIterator, Object layer) {

		return new GPEFeature(ValueFactory.createValue(id), name, xsElementName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.parser.GPEContentHandler#startLinearRing(java.lang.String,
	 * org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */
	public Object startLinearRing(String id, ICoordinateIterator coords,
			String srs) {
		GeneralPathX gp = new GeneralPathX();
		double[] buffer = new double[coords.getDimension()];
		try {
			while (coords.hasNext()) {
				coords.next(buffer);
				gp.append(ShapeFactory.createPoint2D(buffer[0], buffer[1]),
						true);
			}
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
		return new GPEGeometry(id, ShapeFactory.createPolygon2D(gp), srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#startMultiPoint(java.lang.String,
	 * java.lang.String)
	 */
	public Object startMultiPoint(String id, String srs) {
		return new GPEMultiPointGeometry(id, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addPointToMultiPoint(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addPointToMultiPoint(Object point, Object multiPoint) {
		((GPEMultiGeometry) multiPoint).addGeometry((GPEGeometry) point);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#startMultiLineString(java.lang.String,
	 * java.lang.String)
	 */
	public Object startMultiLineString(String id, String srs) {
		super.startMultiLineString(id, srs);
		return new GPEMultiLineGeometry(id, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addLineStringToMultiLineString(java.
	 * lang.Object, java.lang.Object)
	 */
	public void addLineStringToMultiLineString(Object lineString,
			Object multiLineString) {
		((GPEMultiGeometry) multiLineString)
				.addGeometry((GPEGeometry) lineString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.IGPEContentHandler#startMultiPolygon(java.lang.String,
	 * java.lang.String)
	 */
	public Object startMultiPolygon(String id, String srs) {
		super.startMultiPolygon(id, srs);
		return new GPEMultiPolygonGeometry(id, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.IGPEContentHandler#addPolygonToMultiPolygon(java.lang.Object
	 * , java.lang.Object)
	 */
	public void addPolygonToMultiPolygon(Object polygon, Object multiPolygon) {
		((GPEMultiGeometry) multiPolygon).addGeometry((GPEGeometry) polygon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.GPEContentHandler#addCurveToMultiCurve(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addCurveToMultiCurve(Object curve, Object multiCurve) {
		((GPEMultiGeometry) multiCurve).addGeometry((GPEGeometry) curve);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.GPEContentHandler#addSegmentToCurve(java.lang.Object,
	 * java.lang.Object)
	 */
	public void addSegmentToCurve(Object segment, Object curve) {
		((GPECurve) curve).addSegment((GPEGeometry) segment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.parser.GPEContentHandler#startCurve(java.lang.String,
	 * org.gvsig.gpe.parser.ICoordinateIterator, java.lang.String)
	 */
	public Object startCurve(String id, ICoordinateIterator coords, String srs) {
		getErrorHandler().addWarning(new CurveConversionWarning(id));
		GeneralPathX gp = new GeneralPathX();
		double[] buffer = new double[coords.getDimension()];
		try {
			while (coords.hasNext()) {
				coords.next(buffer);
				gp.append(ShapeFactory.createPoint2D(buffer[0], buffer[1]),
						true);
			}
		} catch (IOException e) {
			getErrorHandler().addError(e);
		}
		return new GPEGeometry(id, ShapeFactory.createPolyline2D(gp), srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.GPEContentHandler#startCurve(java.lang.String,
	 * java.lang.String)
	 */
	public Object startCurve(String id, String srs) {
		getErrorHandler().addWarning(new CurveConversionWarning(id));
		return new GPECurve(id, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.GPEContentHandler#startMultiCurve(java.lang.String,
	 * java.lang.String)
	 */
	public Object startMultiCurve(String id, String srs) {
		return new GPEMultiLineGeometry(id, srs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.gpe.GPEContentHandler#addGeometryToMultiGeometry(java.lang.
	 * Object, java.lang.Object)
	 */
	public void addGeometryToMultiGeometry(Object geometry, Object multiGeometry) {
		((GPEMultiGeometry) multiGeometry).addGeometry((GPEGeometry) geometry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gpe.GPEContentHandler#startMultiGeometry(java.lang.String,
	 * java.lang.String)
	 */
	public Object startMultiGeometry(String id, String srs) {
		return new GPEMultiGeometry(id, srs);
	}

}
