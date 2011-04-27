/*
 * Created on 12-may-2006
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
* $Id: AdjacencyDissolveCriteria.java 13881 2007-09-19 16:22:04Z jaume $
* $Log$
* Revision 1.4  2007-09-19 16:06:59  jaume
* removed unnecessary imports
*
* Revision 1.3  2007/03/06 16:47:58  caballero
* Exceptions
*
* Revision 1.2  2006/08/11 16:27:46  azabala
* *** empty log message ***
*
* Revision 1.1  2006/06/20 18:20:45  azabala
* first version in cvs
*
* Revision 1.1  2006/05/24 21:11:14  azabala
* primera version en cvs despues de refactoring orientado a crear un framework extensible de geoprocessing
*
*
*/
package com.iver.cit.gvsig.geoprocess.impl.dissolve.fmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cresques.cts.ICoordTrans;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.geoprocess.core.fmap.SummarizationFunction;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.vividsolutions.jts.geom.Geometry;

/**
 * OJO!
 * Si las geometrias se crean externamente, asumimos que están
 * reproyectadas.
 *
 * Las que se obtienen internamente sí se reproyectan al vuelo
 *
 *
 *
 * @author azabala
 *
 */
public class AdjacencyDissolveCriteria implements IDissolveCriteria,
											ISpatialDissolveCriteria{

	/**
	 * Reads geometries of the processed features
	 */
	private ReadableVectorial source;

	private IGeometry firstGeometry;
	/**
	 * Cached jts geometry of the dissolve 'seed'
	 */
	private Geometry cachedJts;

	private IGeometry secondGeometry;

	private ICoordTrans ct;

	private AdjacencyFeatureBuilder builder;


	public AdjacencyDissolveCriteria(ReadableVectorial source){
		this.source = source;
		this.builder = new AdjacencyFeatureBuilder();
	}

	public boolean verifyIfDissolve(int featureIndex1, int featureIndex2) {
		try {
			fetchGeometry(featureIndex1);
			secondGeometry = source.getShape(featureIndex2);
			if(ct != null)
				secondGeometry.reProject(ct);
			Geometry secondJts = secondGeometry.toJTSGeometry();
			return cachedJts.intersects(secondJts);
		} catch (ReadDriverException e) {
			return false;
		} 
	}

	/**
	 * Verify if the geometry of the seed feature has been readed,
	 * and reads it if not.
	 * @param index
	 */
	private void fetchGeometry(int index){
		if(cachedJts == null){
			try {
				firstGeometry = this.source.getShape(index);
				if(ct != null)
					firstGeometry.reProject(ct);
				cachedJts = firstGeometry.toJTSGeometry();
			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void clear() {
		cachedJts = null;
	}

	class AdjacencyFeatureBuilder implements IDissolvedFeatureBuilder{

		public IFeature createFeature(IGeometry g, int index, int fid) {
			Value[] values = new Value[1];
			values[0] = ValueFactory.createValue(fid);
			return new DefaultFeature(g, values, new Integer(fid).toString());
		}

		public IFeature createFeature(IGeometry newGeometry,
										List sumarizedValues,
										int newFid,
										int index) {
			int numNumericFields = sumarizedValues.size();
			Value[] values = new Value[numNumericFields + 1];
			values[0] = ValueFactory.createValue(newFid);
			int idx = 1;
			Iterator valIt = sumarizedValues.iterator();
			while(valIt.hasNext()){
				Value val = (Value) valIt.next();
				values[idx] = val;
				idx++;
			}
			return new DefaultFeature(newGeometry, values, new Integer(newFid).toString());
		}

	}

	public IDissolvedFeatureBuilder getFeatureBuilder() {
		return builder;
	}

	public IGeometry getFirstGeometry() {
		return firstGeometry;
	}

	public IGeometry getSecondGeometry() {
		return secondGeometry;
	}

	public void setFirstGeometry(IGeometry g) {
		//we asumed that geometries are reprojected externally
		firstGeometry = g;
		cachedJts = firstGeometry.toJTSGeometry();
	}

	public void setSecondGeometry(IGeometry g) {
//		we asumed that geometries are reprojected externally
		secondGeometry = g;
	}


	public void setCoordTrans(ICoordTrans coordTrans) {
		this.ct = coordTrans;
	}

	public ILayerDefinition createLayerDefinition(Map numFields_SumFunc) {
		SHPLayerDefinition resultLayerDefinition = new SHPLayerDefinition();
		resultLayerDefinition.setShapeType(XTypes.POLYGON);
		ArrayList fields = new ArrayList();
		//first of all: FID
		FieldDescription fidFd = new FieldDescription();
		fidFd.setFieldLength(10);
		fidFd.setFieldName("fid");
		fidFd.setFieldType(XTypes.INTEGER);
		fidFd.setFieldDecimalCount(0);
		fields.add(fidFd);

		if(numFields_SumFunc != null){
			//sumarization of numeric attributes
			Iterator fieldsIt = numFields_SumFunc.keySet().iterator();
			while(fieldsIt.hasNext()){
				String field = (String) fieldsIt.next();
				SummarizationFunction[] functions =
					(SummarizationFunction[]) numFields_SumFunc.get(field);
				for(int i = 0; i < functions.length; i++){
					FieldDescription description =
						new FieldDescription();
					description.setFieldLength(10);
					description.setFieldDecimalCount(4);
					//to avoid truncation of field names (f.example shp)
					//we only catch five first letters
					String shortName = null;
					if(field.length() > 5)
						shortName = field.substring(0,4);
					else
						shortName = field;
					description.setFieldName(
							shortName + "_" + functions[i].toString());
					description.setFieldType(XTypes.DOUBLE);
					fields.add(description);
				}//for
			}//while
		}//if
		FieldDescription[] fieldsDesc = null;
		if(fields.size() == 0){
			fieldsDesc = new FieldDescription[0];
		}else{
			fieldsDesc = new FieldDescription[fields.size()];
			fields.toArray(fieldsDesc);
		}
		resultLayerDefinition.setFieldsDesc(fieldsDesc);
		return resultLayerDefinition;

	}

	public void setFirstJts(Geometry g) {
		cachedJts = g;

	}

	Geometry cachedJts2;

	public void setSecondJts(Geometry g) {
		cachedJts2 = g;
	}

	public Geometry getSecondJts() {
		return cachedJts2;
	}

}

