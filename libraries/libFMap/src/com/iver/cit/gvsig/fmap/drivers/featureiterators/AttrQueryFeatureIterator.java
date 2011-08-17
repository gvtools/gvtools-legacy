/*
 * Created on 12-abr-2007
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
 * $Id: AttrQueryFeatureIterator.java 33330 2010-08-24 09:12:44Z vcaballero $
 * $Log$
 * Revision 1.1  2007-05-29 19:08:11  azabala
 * first version in cvs
 *
 * Revision 1.1  2007/04/19 17:27:58  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.fmap.drivers.featureiterators;

import org.cresques.cts.IProjection;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;

/**
 * Iterates over the features of a vectorial data source
 * (readablevectorial, or vectorialadapter) which verify a SQL statement.
 * <br>
 * SQL syntax is very extrict, GDBMS based.
 * (for example, % character is not allowed to build strings expressions,
 * all SQL statement must end with ;, etc)
 *
 *
 *
 *
 * @author azabala
 *
 */
public class AttrQueryFeatureIterator extends DefaultFeatureIterator {

	private String sqlQuery;
	private long[] indexes;

	public AttrQueryFeatureIterator(ReadableVectorial source,
			IProjection sourceProj,
			IProjection targetProj,
			String sqlQuery) throws ReadDriverException {
		super(source,sourceProj,targetProj,source.getRecordset().getFieldNames());
		this.sqlQuery = sqlQuery;

		try {
			if(hasWhere(sqlQuery)){
				DataSource datasource = LayerFactory.getDataSourceFactory().executeSQL(sqlQuery,
						DataSourceFactory.MANUAL_OPENING);
				super.setFieldNames(source.getRecordset().getFieldNames());
				indexes = datasource.getWhereFilter();
			}else{
				//TODO This is not very elegant: rethink
				indexes = new long[source.getShapeCount()];
				for(int i = 0; i < indexes.length; i++){
					indexes[i] = i;
				}
				super.setFieldNames(source.getRecordset().getFieldNames());
			}


			//check to avoid reprojections with the same projection
			if(targetProj != null){
				// FJP: Si la capa original no sabemos que proyeccion tiene, no hacemos nada
				if (sourceProj != null) {
					if(!(targetProj.getAbrev().equalsIgnoreCase(sourceProj.getAbrev())))
						this.targetProjection = targetProj;
				}
			}

		} catch (Exception e){
			throw new ReadDriverException("error ejecutando consulta sql para iterador", e);
		}
	}

	public boolean hasNext(){
		if(indexes != null && currentFeature < indexes.length)
			return true;
		else
			return false;
	}

	private boolean hasWhere(String expression) {
		String subExpression = expression.trim();
		int pos;

		// Remove last ';' if exists
		if (subExpression.charAt(subExpression.length() -1) == ';')
			subExpression = subExpression.substring(0, subExpression.length() -1).trim();

		// If there is no 'where' clause
		if ((pos = subExpression.indexOf("where")) == -1)
			return false;

		// If there is no subexpression in the WHERE clause -> true
		subExpression = subExpression.substring(pos + 5, subExpression.length()).trim(); // + 5 is the length of 'where'
		if ( subExpression.length() == 0 )
			return false;
		else
			return true;
	}

	public IFeature next() throws ReadDriverException {
		IGeometry geom;
		try {
			geom = chekIfCloned(source.getShape((int)indexes[currentFeature]));
			reprojectIfNecessary(geom);
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException("Error accediendo al driver", e);
		}

		Value[] regAtt = getValues((int)indexes[currentFeature]);
		DefaultFeature feat = new DefaultFeature(geom, regAtt, (int)indexes[currentFeature] + "");
		currentFeature++;
		return feat;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

}

