package org.gvsig.fmap.drivers.gpe.writer;

import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.gvsig.gpe.writer.GPEWriterHandler;
import org.gvsig.remoteClient.gml.schemas.XMLElement;

import com.iver.cit.gvsig.fmap.core.FMultiPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;

/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * @author Jorge Piera LLodr� (jorge.piera@iver.es)
 */
public class ExportGeometry {
	private GPEWriterHandler writer = null;
	//To know if the geometry is multiple
	private boolean isMultiple = false;
	//To reproject geometries
	private IProjection projOrig = null;
	private IProjection projDest = null;
	private ICoordTrans coordTrans = null;
	private String crs = null;

	public ExportGeometry(GPEWriterHandler writer) {
		super();
		this.writer = writer;
	}

	
	/**
	 * It writes a geometry
	 * @param geom
	 * The geometry to write
	 * @param crs
	 * The coordinates reference system
	 */
	public void writeGeometry(IGeometry geom){
		crs = null;
		if (projDest != null){
			crs = projDest.getAbrev();
		}
		if (geom instanceof FMultiPoint2D){
			FMultiPoint2D multi = (FMultiPoint2D)geom;
			for (int i=0 ; i<multi.getNumPoints() ; i++){
				reproject(multi.getPoint(i));
			}
			writeMultiPoint(multi, crs);
			return;
		}
		FShape shp = (FShape)geom.getInternalShape();
		reproject(shp);
		int type = shp.getShapeType() % FShape.Z % FShape.M;
		
		if (type == FShape.POINT){
			writePoint((FPoint2D)shp, crs);
		}else if (type==FShape.LINE){
			writeLine((FPolyline2D)shp, crs);
		}else if (type==FShape.POLYGON){
			writePolygon((FPolygon2D)shp, crs);
		}
	}

	/**
	 * Reproject a geometry
	 * @param shp
	 */
	private void reproject(FShape shp){
		ICoordTrans coordTrans = getCoordTrans();
		if (coordTrans != null){
			try{
				shp.reProject(coordTrans);
			}catch(Exception e){
				//The server is the responsible to reproject
				if (projOrig != null){
					crs = projOrig.getAbrev();
				}
			}
		}
	}

	/**
	 * Writes a point in 2D
	 * @param point
	 * The point to write
	 * @param crs
	 * The coordinates reference system
	 */
	private void writePoint(FPoint2D point, String crs){
		writer.startPoint(null, new CoordinatesSequencePoint(point), crs);
		writer.endPoint();
	}

	/**
	 * Writes a multipoint in 2D
	 * @param point
	 * The point to write
	 * @param crs
	 * The coordinates reference system
	 */
	private void writeMultiPoint(FMultiPoint2D multi, String crs){
		writer.startMultiPoint(null, crs);
		for (int i=0 ; i<multi.getNumPoints() ; i++){
			FPoint2D point = multi.getPoint(i);
			writePoint(point, crs);
		}
		writer.endMultiPoint();
	}

	/**
	 * Writes a line in 2D
	 * @param line
	 * The line to write
	 * @param crs
	 * The coordinates reference system
	 * @param geometries
	 * The parsed geometries
	 */
	private void writeLine(FPolyline2D line, String crs){
		boolean isMultipleGeometry = false;
		if (isMultiple){
			writer.startMultiLineString(null, crs);
		}else{
			isMultipleGeometry = isMultiple(line.getPathIterator(null));
			if (isMultipleGeometry){
				writer.startMultiLineString(null, crs);
			}
		}
		CoordinatesSequenceGeneralPath sequence = new CoordinatesSequenceGeneralPath(line.getPathIterator(null));
		writer.startLineString(null, sequence, crs);
		writer.endLineString();	
		if (isMultiple || isMultipleGeometry){
			while (sequence.hasMoreGeometries()){
				sequence.initialize();
				writer.startLineString(null, sequence, crs);
				writer.endLineString();	
			}
			writer.endMultiLineString();
		}
	}

	/**
	 * Writes a polygon in 2D
	 * @param polygon
	 * The polygon to write
	 * @param crs
	 * The coordinates reference system
	 * @param geometries
	 * The parsed geometries
	 */
	private void writePolygon(FPolygon2D polygon, String crs){
		boolean isMultipleGeometry = false;
		if (isMultiple){
			writer.startMultiPolygon(null, crs);
		}else{
			isMultipleGeometry = isMultiple(polygon.getPathIterator(null));
			if (isMultipleGeometry){
				writer.startMultiPolygon(null, crs);
			}
		}
		CoordinatesSequenceGeneralPath sequence = new CoordinatesSequenceGeneralPath(polygon.getPathIterator(null));
		writer.startPolygon(null, sequence ,crs);
		writer.endPolygon();	
		if (isMultiple || isMultipleGeometry){
			while (sequence.hasMoreGeometries()){
				sequence.initialize();
				writer.startPolygon(null, sequence ,crs);
				writer.endPolygon();
			}
			writer.endMultiPolygon();
		}
	}
	
	/**
	 * Return if the geometry is multiple	
	 * @param path
	 * @return
	 */
	public boolean isMultiple(PathIterator path){
		double[] coords = new double[2];
		int type = 0;
		int numGeometries = 0;
		while (!path.isDone()){
			type = path.currentSegment(coords);
			 switch (type) {
			 	case PathIterator.SEG_MOVETO:
			 		numGeometries++;
			 		if (numGeometries == 2){
			 			return true;
			 		}
			 		break;
			 	case PathIterator.SEG_CLOSE:
			 		return false;			 		
			 	default:
			 		break;
			 }
			 path.next();
		}
		return false;
	}

	/**
	 * @param projOrig the projOrig to set
	 */
	public void setProjOrig(IProjection projOrig) {
		this.projOrig = projOrig;
	}

	/**
	 * @param projDest the projDest to set
	 */
	public void setProjDest(IProjection projDest) {
		this.projDest = projDest;
	}

	/**
	 * @return the coordTrans
	 */
	private ICoordTrans getCoordTrans() {
		if (coordTrans == null){
			if ((projOrig == null) || (projDest == null)){
				return null;
			}
			coordTrans = projOrig.getCT(projDest);
		}
		return coordTrans;
	}

	/**
	 * @param writer the writer to set
	 */
	public void setWriter(GPEWriterHandler writer) {
		this.writer = writer;
	}



	/**
	 * @param geometry the geometry to set
	 */
	public void setGeometry(XMLElement geometry) {
		if (geometry != null){
			if (geometry.getEntityType().getName().toLowerCase().indexOf("multi") > 0){
				isMultiple = true;				
			}else{
				isMultiple = false;	
			}
		}
	}

	/**
	 * @return the isMultiple
	 */
	public boolean isMultiple() {
		return isMultiple;
	}

	/**
	 * @param isMultiple the isMultiple to set
	 */
	public void setMultiple(boolean isMultiple) {
		this.isMultiple = isMultiple;
	}

	/**
	 * @return the projDest
	 */
	public IProjection getProjDest() {
		return projDest;
	}


	public IProjection getProjOrig() {
		return projOrig;
	}


	public Rectangle2D getExtent(Rectangle2D fullExtent) {
		if (getProjDest().getAbrev().compareTo(getProjOrig().getAbrev())!=0){
			coordTrans = getCoordTrans();
			if (coordTrans != null){
				try{
					return coordTrans.convert(fullExtent);
				}catch(Exception e){
					//The server is the responsible to reproject
					if (projOrig != null){
						crs = projOrig.getAbrev();
					}
				}
			}
		}	
		return fullExtent;
	}
}
