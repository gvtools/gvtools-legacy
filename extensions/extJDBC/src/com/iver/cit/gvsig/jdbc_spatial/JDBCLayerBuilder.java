package com.iver.cit.gvsig.jdbc_spatial;

import java.util.Map;

import com.iver.cit.gvsig.fmap.drivers.ConnectionFactory;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGisDriver;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.utiles.extensionPoints.IExtensionBuilder;

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
 * $Id: JDBCLayerBuilder.java 13881 2007-09-19 16:22:04Z jaume $
 * $Log$
 * Revision 1.5  2007-09-19 16:11:32  jaume
 * removed unnecessary imports
 *
 * Revision 1.4  2007/06/27 06:56:31  caballero
 * organizar excepciones
 *
 * Revision 1.3  2007/06/04 07:10:07  caballero
 * connections refactoring
 *
 * Revision 1.2.4.4  2007/05/31 12:07:58  caballero
 * connections
 *
 * Revision 1.2.4.3  2007/02/12 14:35:52  jmvivo
 * Quitado el soporte para Oracle spacial
 *
 * Revision 1.2  2006/04/18 06:19:06  jorpiell
 * Modificada la forma en la que se añade la clase que implementa la factoría. Se tiene que pasar una instancia, y no una clase. Además se la ha añadido un constructor sin parámetros a la factoria.
 *
 * Revision 1.1  2006/04/11 11:54:38  jorpiell
 * Nueva clase que se usa para cargar una capa Postgis usando el mecanismo de extensibilidad
 *
 *
 */
/**
 * Creates a Postgis FLyrVect from a set of params
 * (URL, user name, password, ...). The catalog
 * extension uses this class to load a new layer
 * from a metadata.
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class JDBCLayerBuilder implements IExtensionBuilder{

	public JDBCLayerBuilder(){
		super();
	}

	public Object create() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object create(Object[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object create(Map args) {
		String dbURL = (String) args.get("DBURL");
		String user = (String) args.get((String) "USER");
		String pwd = (String) args.get((String) "PASSWORD");
		String layerName = (String) args.get((String) "NAME");
		String fidField = (String) args.get((String) "ID");
		String sFields = (String) args.get((String) "FIELDS");
		String[] fields = sFields.split(",");
		String geomField = (String) args.get((String) "GEOMFIELD");
		String tableName = (String) args.get((String) "TABLENAME");
		String whereClause = (String) args.get((String) "WHERECLAUSE");


		IConnection conn;
		try {
			conn = ConnectionFactory.createConnection(dbURL, user, pwd);
		} catch (DBException e) {
			e.printStackTrace();
			return null;
		}


		DBLayerDefinition lyrDef = new DBLayerDefinition();
		lyrDef.setName(layerName);
		lyrDef.setTableName(tableName);
		lyrDef.setWhereClause(whereClause);
		lyrDef.setFieldNames(fields);
		lyrDef.setFieldGeometry(geomField);
		lyrDef.setFieldID(fidField);

		PostGisDriver pgd = new PostGisDriver();
		try {
			pgd.setData(conn, lyrDef);
		} catch (DBException e) {
			e.printStackTrace();
			return null;
		}

		return LayerFactory.createDBLayer(pgd, layerName, null);

	}

}
