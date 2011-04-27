/*
 * Created on 11-mar-2005
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
package com.iver.cit.gvsig.fmap.drivers.jdbc.mysql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WKBParser2;

/**
 * Iterator over the features of a MySQL driver.
 * 
 *
 * 
 * @author FJP
 */
public class MySqlFeatureIterator implements IFeatureIterator {
    private WKBParser2 parser = new WKBParser2();
    ResultSet rs;
    String strAux;
    IGeometry geom;
    int numColumns;
    int idFieldID = -1;

    int[] relIds;
    private DBLayerDefinition lyrDef;

    private ResultSetMetaData metaData = null;
    Value[] regAtt;
    /**
     * @throws SQLException
     *
     */
    public MySqlFeatureIterator(ResultSet rs) {
        // Debe ser forward only
        this.rs = rs;
        try {
            numColumns = rs.getMetaData().getColumnCount();
            regAtt = new Value[numColumns-1];
            metaData = rs.getMetaData();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#hasNext()
     */
    public boolean hasNext() throws ReadDriverException {
        try {
			if (rs.next())
			    return true;
			closeIterator();
			return false;
		} catch (SQLException e) {
//			SqlDriveExceptionType type = new SqlDriveExceptionType();
//            type.setDriverName("MySQL Driver");
//            try {
//				type.setSql(rs.getStatement().toString());
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
            throw new ReadDriverException("MySQL Driver",e);
//			throw new DriverException(e);
		}
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.jdbc.GeometryIterator#next()
     */
    public IFeature next() throws ReadDriverException {
        byte[] data;
      
		try {
			data = rs.getBytes(1);
			geom = parser.parse(data);

	        for (int fieldId=2; fieldId <= numColumns; fieldId++ )
	        {
	            Value val = null;
	            if (metaData.getColumnType(fieldId) == Types.VARCHAR)
	            {
	                String strAux = rs.getString(fieldId);
	                if (strAux == null) strAux = "";
	                val =  ValueFactory.createValue(strAux);
	            }else if (metaData.getColumnType(fieldId) == Types.FLOAT)
	                val = ValueFactory.createValue(rs.getFloat(fieldId));
	            else if (metaData.getColumnType(fieldId) == Types.DOUBLE)
	                val = ValueFactory.createValue(rs.getDouble(fieldId));
	            else if (metaData.getColumnType(fieldId) == Types.INTEGER)
	                val = ValueFactory.createValue(rs.getInt(fieldId));
	            else if (metaData.getColumnType(fieldId) == Types.BIGINT)
	                val = ValueFactory.createValue(rs.getLong(fieldId));
	            else if (metaData.getColumnType(fieldId) == Types.BIT)
	                val = ValueFactory.createValue(rs.getBoolean(fieldId));
	            else if (metaData.getColumnType(fieldId) == Types.DATE)
	                val = ValueFactory.createValue(rs.getDate(fieldId));

	            regAtt[relIds[fieldId-2]] = val;
	        }
	        IFeature feat = null;
	        if (idFieldID != -1)
	        {//TODO Review if we could find problems when the table has only geom and gid
	        	int fieldId = lyrDef.getIdFieldID();
	        	Value idValue = regAtt[fieldId];
	        	String theID = "";
	        	if(idValue != null)//azabala: sometimes we found problems with gid (null pointer exceptions)
	        		theID = idValue.toString();
	            feat = new DefaultFeature(geom, regAtt, theID);
	        }
	        else
			{
//				// feat = new DefaultFeature(geom, regAtt);
//	        	FeatureWithoutIdExceptionType  type = new FeatureWithoutIdExceptionType();
//	        	type.setSchema(lyrDef);
				throw new ReadDriverException("MySQL Driver",null);
			}
		



	        return feat;
		} catch (SQLException e) {
//			SqlDriveExceptionType type = new SqlDriveExceptionType();
//            type.setDriverName("MySQL Driver");
//            try {
//				type.setSql(rs.getStatement().toString());
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
            throw new ReadDriverException("MySQL Driver",e);
//			throw new DriverException(e);
		}
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.IFeatureIterator#closeIterator()
     */
    public void closeIterator() throws ReadDriverException {
        try {
			rs.close();
		} catch (SQLException e) {
//			SqlDriveExceptionType type = new SqlDriveExceptionType();
//            type.setDriverName("MySQL Driver");
//            try {
//				type.setSql(rs.getStatement().toString());
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
            throw new ReadDriverException("MySQL Driver",e);
//			throw new DriverException(e);
		}
    }

    public void setLyrDef(DBLayerDefinition lyrDef)
    {
        this.lyrDef  =lyrDef;
        // Aunque no nos hayan pedido todos los campos, devolveremos
        // tantos atributos como la capa tiene. Eso sí, puestos a null
        regAtt = new Value[lyrDef.getFieldNames().length];
        //no debería ser numColums - 2 ??
        relIds = new int[numColumns-1];

        try {
//            for (int i=2; i<= metaData.getColumnCount(); i++)
        	for (int i=2; i <= numColumns; i++)
            {
                int idRel = lyrDef.getFieldIdByName(metaData.getColumnName(i));
				if (idRel == -1)
				{
					throw new RuntimeException("No se ha encontrado el nombre de campo " + metaData.getColumnName(i));
				}

                relIds[i-2] = idRel;
                if (lyrDef.getFieldID().equals(metaData.getColumnName(i)))
                {
                    idFieldID = i;
                    break;
                }
            }
        } catch (SQLException e) {
            // Si no está, no pasa nada
        }

    }


}
