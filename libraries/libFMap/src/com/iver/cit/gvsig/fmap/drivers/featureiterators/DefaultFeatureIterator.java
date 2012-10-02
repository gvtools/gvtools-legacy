/*
 * Created on 11-abr-2007
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
 * $Id: DefaultFeatureIterator.java 31155 2009-10-06 11:03:03Z fpenarrubia $
 * $Log$
 * Revision 1.5  2007-09-19 16:25:04  jaume
 * ReadExpansionFileException removed from this context
 *
 * Revision 1.4  2007/06/07 11:49:39  azabala
 * added default constructor
 *
 * Revision 1.3  2007/06/07 10:19:56  azabala
 * closeIterator calls to ReadableVectorial.stop
 *
 * Revision 1.2  2007/06/06 18:03:34  azabala
 * when String[] param is null, instead of return all fields it returns zero fields
 *
 * Revision 1.1  2007/05/29 19:08:11  azabala
 * first version in cvs
 *
 * Revision 1.1  2007/04/19 17:27:58  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap.drivers.featureiterators;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

/**
 * 
 * Iterator over all the features of a vectorial adapter.
 * It is thinked for data sources which dont have capabilities
 * of querying or reprojection.
 * 
 * 
 * @author Alvaro Zabala
 * */
public class DefaultFeatureIterator implements IFeatureIterator {

	/**
	 * Projection of the layer on which this iterator iterates.
	 * TODO Move projection from layer to adapter or driver
	 * */
	protected CoordinateReferenceSystem sourceCrs;
	/**
	 * If its setted, all features returned by this iterator
	 * will be previously reprojected to this target projection
	 */
	protected CoordinateReferenceSystem targetCrs;
	/**
	 * If its setted, returned features only will have these alphanumeric attributes
	 */
	protected String[] fieldNames;

	/**
	 * vectorial data source. It reads geometries
	 * and has the recordset
	 */
	protected ReadableVectorial source;
	/**
	 * recordset, it reads alphanumeric attributes
	 */
	protected SelectableDataSource recordset;

	/**
	 * index of the next feature that will be returned by this
	 * iterator
	 */
	protected int currentFeature;

	/**
	 * Default constructor. 
	 * Creates an iterator which will return features in the data source projection
	 * (without reprojection) and with all the alphanumeric attributes
	 * @throws ReadDriverException 
	 *
	 */
	public DefaultFeatureIterator( ReadableVectorial source) throws ReadDriverException{
		this.source = source;
		//needed for layers in edition status
		this.source.start();
		this.recordset = source.getRecordset();
		currentFeature = 0;
	}

	/**
	 * Constructor.
	 * The iterator will reproject the geometry of the features to the specified target projection,
	 * and with the specified attribute fields.
	 * */
	public DefaultFeatureIterator(ReadableVectorial source, 
			CoordinateReferenceSystem sourceCrs, 
			CoordinateReferenceSystem targetCrs, 
			String[] fieldNames) throws ReadDriverException{
		this(source);
		this.sourceCrs = sourceCrs;
		//check to avoid reprojections with the same projection
		if(targetCrs != null){
			// FJP: Si la capa original no sabemos qué proyección tiene, no hacemos nada
			if (sourceCrs != null) {
				if(!(targetCrs.getName().equals(sourceCrs.getName())))
					this.targetCrs = targetCrs;
			}
		}
		this.fieldNames = fieldNames;
	}

	/**
	 * Default constructor.
	 *
	 */
	public DefaultFeatureIterator(){
	}

	public boolean hasNext() throws ReadDriverException {
		boolean bMore = (currentFeature < source.getShapeCount());
		return bMore;
	}

	public IFeature next() throws ReadDriverException {

		try {
			IGeometry geom = chekIfCloned(source.getShape(currentFeature));
			reprojectIfNecessary(geom);
			Value[] regAtt = getValues(currentFeature);
			IFeature feat  = new DefaultFeature(geom, regAtt, currentFeature + "");
			currentFeature++;
			return feat;
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException("",e);
		} 
	}

	public void closeIterator() throws ReadDriverException {
		this.source.stop();
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public CoordinateReferenceSystem getTargetCrs() {
		return targetCrs;
	}

	public void setTargetCrs(CoordinateReferenceSystem targetCrs) {
		this.targetCrs = targetCrs;
	}

	public CoordinateReferenceSystem getSourceCrs() {
		return sourceCrs;
	}

	public void setSourceCrs(CoordinateReferenceSystem sourceCrs) {
		this.sourceCrs = sourceCrs;
	}

	/**
	 * 
	 * Checks if must reproject the given geom
	 * and reprojects it if true
	 * @param geom
	 */
	protected void reprojectIfNecessary(IGeometry geom){
		if (targetCrs != null && sourceCrs != null
				&&	!targetCrs.getName().equals(sourceCrs.getName())){
			MathTransform trans = ProjectionUtils.getCrsTransform(sourceCrs,
					targetCrs);
			geom.reProject(trans);
		}
	}

	/**
	 * Checks if the geometry must be cloned.
	 * @return
	 * If it must be cloned.
	 */
	protected IGeometry chekIfCloned(IGeometry geom){
		if ((source.getDriverAttributes() != null) &&
			(source.getDriverAttributes().isLoadedInMemory())){			
			return geom.cloneGeometry();
		}
		return geom;
	}

	protected Value[] getValues(int featureIdx) throws ReadDriverException{
		Value[] regAtt = null;
		if(fieldNames == null){
			//TODO Duda: cual es el comportamiento deseado cuando fieldNames sea null
			//devolverlo todo o no devolver nada?????
			//			regAtt = new Value[recordset.getFieldCount()];//igual optimiza reutilizar y hacer copias (en vez de alocar array)
			//			for (int fieldId = 0; fieldId < recordset.getFieldCount(); fieldId++) {
			//				regAtt[fieldId] = recordset.getFieldValue(featureIdx, fieldId);
			//			}
			regAtt = new Value[0];
		}else{
			regAtt = new Value[fieldNames.length];
			for (int fieldId = 0; fieldId < fieldNames.length; fieldId++) {
				int fieldCode = recordset.getFieldIndexByName(fieldNames[fieldId]);
				regAtt[fieldId] = recordset.getFieldValue(featureIdx, fieldCode);
			}
		}
		return regAtt;
	}
	
	/**
	 * Useful if the layer is joined, to allow to retrieve values from joined fields
	 * 
	 * @param rs
	 */
	public void setRecordset(SelectableDataSource rs) {
		this.recordset = rs;
	}

}

