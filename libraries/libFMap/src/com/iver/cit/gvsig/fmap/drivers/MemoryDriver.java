/*
 * Created on 27-dic-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
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
package com.iver.cit.gvsig.fmap.drivers;

import java.awt.geom.Rectangle2D;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.DefaultTableModel;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.FNullGeometry;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.operations.strategies.MemoryShapeInfo;


/**
 * Clase abstracta para Driver en memoria.
 *
 * @author FJP
 */
public abstract class MemoryDriver implements VectorialDriver, ObjectDriver,
	BoundedShapes {
	private MemoryShapeInfo memShapeInfo = new MemoryShapeInfo();
	private ArrayList arrayGeometries = new ArrayList();
	private Rectangle2D fullExtent;
	private int m_Position;
	private DefaultTableModel m_TableModel = new DefaultTableModel();
	private int[] fieldWidth=null;

	/**
	 * Devuelve el modelo de la tabla.
	 *
	 * @return modelo de la tabla.
	 */
	public DefaultTableModel getTableModel() {
		return m_TableModel;
	}

	/**
	 * Añade un shape.
	 *
	 * @param geom shape.
	 * @param row fila.
	 */
	public void addGeometry(IGeometry geom, Object[] row) {
		if (geom == null) {
			return; // No añadimos nada
		}
		if(! (geom instanceof FNullGeometry)){
			 Rectangle2D boundsShp = geom.getBounds2D();
			 memShapeInfo.addShapeInfo (boundsShp, geom.getGeometryType());
			 arrayGeometries.add(geom);
			if (fullExtent == null) {
				fullExtent = (Rectangle2D) boundsShp.clone();
			} else {
				fullExtent.add(boundsShp);
			}
		}
		else
		{
			 Rectangle2D boundsShp = new Rectangle2D.Double();
			 memShapeInfo.addShapeInfo (boundsShp, geom.getGeometryType());
			 arrayGeometries.add(geom);

		}
		if (fieldWidth==null) {
			initializeFieldWidth(row);
		}
		actualizeFieldWidth(row);
		m_TableModel.addRow(row);

		try {
			fullExtent = getFullExtent();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}

		m_Position++;
	}


	/**
	 * Método de conveniencia, para poder añadir directamente un shape
	 * o una IGeometry. (Arriba está el de añadir una IGeometry.
	 * @param shp
	 * @param row
	 */
	public void addShape(FShape shp, Object[] row) {
		if (shp == null) {
			return; // No añadimos nada
		}
		IGeometry geom = ShapeFactory.createGeometry(shp);

		addGeometry(geom, row);
	}

	/**
	 * Devuelve el extent a partir de un índice.
	 *
	 * @param index Índice.
	 *
	 * @return Extent.
	 */
	public Rectangle2D getShapeBounds(int index) throws ReadDriverException {
		return memShapeInfo.getBoundingBox(index);
	}

	/**
	 * Devuelve el tipo del shape.
	 *
	 * @param index Índice.
	 *
	 * @return tipo del shape.
	 */
	public int getShapeType(int index) {
		return memShapeInfo.getType(index);
	}


	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShape(int)
	 */
	public IGeometry getShape(int index) throws ReadDriverException {
		IGeometry geom = (IGeometry) arrayGeometries.get(index);

		return geom.cloneGeometry();
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeCount()
	 */
	public int getShapeCount() throws ReadDriverException {
		return arrayGeometries.size();
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException {
		return fullExtent;
	}


	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getShapeType()
	 */
	public abstract int getShapeType();

	/* (non-Javadoc)
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public abstract String getName();

	/**
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldType(int)
	 */

/*
 * azabala, bug 666
 * Habra que estudiar como hacer, porque si una fila tiene el primer valor
 * (o todos) a NullValue, devolvera como tipo de dato Types.NULL.
 * Quizas, habra que hacer que los MemoryDriver tb tengan un ITableDefinition.
 *
 * DxfMemoryDriver no obstante si que tiene informacion sobre el esquema, asi
 * que debera sobreescribir este metodo para salvar el bug
 * (metodo getTableDefinition)
 *
 *
 *
 *
 */
	public int getFieldType(int i) throws ReadDriverException {
	    // TODO: Revisar esto. Por ejemplo, el long
	    if (getRowCount() >= 1)
        {
            Value val = getFieldValue(0,i);
            if (val == null)
            	return Types.VARCHAR;
            if (val.getSQLType() == Types.INTEGER)
                // Sabemos que es numérico, pero no sabemos
                // si luego habrá otra cosa.
                return Types.FLOAT;
            else
                return val.getSQLType();
        }
        else
        {
            // TODO: ESTO CREO QUE NO TIENE SENTIDO. SIEMPRE DEVUELVE Object.class, lo dice en
            // la documentación. Creo que habría que quitarlo.
    	    if (m_TableModel.getColumnClass(i) == String.class)
    	        return Types.VARCHAR;
    	    if (m_TableModel.getColumnClass(i) == Float.class)
    	        return Types.FLOAT;
    	    if (m_TableModel.getColumnClass(i) == Double.class)
    	        return Types.DOUBLE;
    	    if (m_TableModel.getColumnClass(i) == Double.class)
    	        return Types.INTEGER;
    	    if (m_TableModel.getColumnClass(i) == Float.class)
    	        return Types.INTEGER;
    	    if (m_TableModel.getColumnClass(i) == Boolean.class)
    	        return Types.BIT;
    	    if (m_TableModel.getColumnClass(i) == Date.class)
    	        return Types.DATE;
        }
	    return Types.VARCHAR;
	    // return m_TableModel.getColumnClass(i);
//	    throw new DriverException("Tipo no soportado: " + m_TableModel.getColumnClass(i).getName());
	}
	/* (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
		throws ReadDriverException {
		return (Value) m_TableModel.getValueAt((int) rowIndex, fieldId);
	}

	/* (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return m_TableModel.getColumnCount();
	}

	/* (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return m_TableModel.getColumnName(fieldId);
	}

	/* (non-Javadoc)
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return arrayGeometries.size();
	}

    /* (non-Javadoc)
     * @see com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
     */
    public void setDataSourceFactory(DataSourceFactory dsf) {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#reLoad()
     */
    public void reload() throws ReloadDriverException{
		memShapeInfo = new MemoryShapeInfo();
		arrayGeometries.clear();
		m_TableModel= new DefaultTableModel();
		fullExtent = null;
		m_Position = 0;

    }
    private void initializeFieldWidth(Object[] row) {
    	fieldWidth=new int[row.length];
		for (int i=0;i<row.length;i++) {
			if (row[i] instanceof StringValue && row[i]==null)
				fieldWidth[i]=0;
			else
			{
				if (row[i] == null)
					fieldWidth[i] = 0;
				else
					fieldWidth[i]=((Value)row[i]).getWidth();
			}
		}
    }
    /**
     * Actualize the width fields with StringValue.
     * @param row
     */
    private void actualizeFieldWidth(Object[] row) {
    	for (int i=0;i<row.length;i++) {
			if (row[i] instanceof StringValue) {
				if (row[i]!=null) {
					int width=((StringValue)row[i]).getWidth();
					if (fieldWidth[i]<width) {
						fieldWidth[i]=width;
					}
				}
			}
		}
    }
    public int getFieldWidth(int fieldId){
    	if (fieldWidth==null)
    		return 1;
    	return fieldWidth[fieldId];
    }


}
