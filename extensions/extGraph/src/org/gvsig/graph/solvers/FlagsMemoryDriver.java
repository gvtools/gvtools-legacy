/*
 * Created on 07-nov-2006
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
 * $Id: FlagsMemoryDriver.java 22182 2008-07-10 07:20:11Z fpenarrubia $
 * $Log$
 * Revision 1.3  2007-09-07 11:29:47  fjp
 * Casi compila. Falta arreglar lo de FArrowSymbol y retocar el graphiclist de FMap.
 *
 * Revision 1.2  2006/11/08 19:32:36  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/07 19:49:28  azabala
 * *** empty log message ***
 *
 *
 */
package org.gvsig.graph.solvers;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.sql.Types;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.MemoryDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;

public class FlagsMemoryDriver extends MemoryDriver implements VectorialDriver {

	// TODO Internationalize field names
	static FieldDescription[] fields = new FieldDescription[6];
	static {
		FieldDescription fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDFLAG");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[0] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDARC");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[1] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("PCT");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[2] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COST");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[3] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("DIREC");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[4] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("DESCRIPTION");
		fieldDesc.setFieldType(Types.VARCHAR);
		fieldDesc.setFieldLength(254);
		fieldDesc.setFieldDecimalCount(0);
		fields[5] = fieldDesc;
	}

	/*
	 * TODO Eliminar FlagsMemoryDriver y RouteMemoryDriver y crear una clase que
	 * sea SchemedMemoryDriver. En el constructor habría que pasar: a)
	 * FieldDescription[] (esquema) b) Tipo de geometria
	 * 
	 * o bien un LayerDefinition....
	 */

	List features;

	Rectangle2D fullExtent;

	public FlagsMemoryDriver(List features) {
		this.features = features;
		computeFullExtent();
	}

	void computeFullExtent() {
		if (features.size() > 0) {
			IFeature feature = (IFeature) features.get(0);
			fullExtent = feature.getGeometry().getBounds2D();
			for (int i = 1; i < features.size(); i++) {
				feature = (IFeature) features.get(i);
				Rectangle2D rAux = feature.getGeometry().getBounds2D();
				fullExtent.add(rAux);
			}
		}
	}

	public int getShapeType() {
		return FShape.POINT;
	}

	public String getName() {
		return "";
	}

	public DriverAttributes getDriverAttributes() {
		return null;
	}

	public boolean isWritable() {
		return false;
	}

	public int[] getPrimaryKeys() {
		return null;
	}

	public void write(DataWare dataWare) {
	}

	/**
	 * Returns de field type of the specified field index.
	 * 
	 * @return field type of i field
	 */
	public int getFieldType(int i) {
		return fields[i].getFieldType();
	}

	public int getShapeCount() {
		return features.size();
	}

	public Rectangle2D getFullExtent() {
		return fullExtent;
	}

	public IGeometry getShape(int index) {
		if (index < features.size())
			return ((IFeature) features.get(index)).getGeometry();
		else
			return null;
	}

	public void reload() {
	}

	/**
	 * Devuelve el modelo de la tabla.
	 * 
	 * @return modelo de la tabla.
	 */
	public DefaultTableModel getTableModel() {
		return null;
	}

	/**
	 * Añade un shape.
	 * 
	 * @param geom
	 *            shape.
	 * @param row
	 *            fila.
	 */
	public void addGeometry(IGeometry geom, Object[] row) {
	}

	/**
	 * Método de conveniencia, para poder añadir directamente un shape o una
	 * IGeometry. (Arriba está el de añadir una IGeometry.
	 * 
	 * @param shp
	 * @param row
	 */
	public void addShape(FShape shp, Object[] row) {
	}

	/**
	 * Devuelve el extent a partir de un índice.
	 * 
	 * @param index
	 *            Índice.
	 * 
	 * @return Extent.
	 * 
	 * @throws IOException
	 */
	public Rectangle2D getShapeBounds(int index) {
		return ((IFeature) features.get(index)).getGeometry().getBounds2D();
	}

	public int getShapeType(int index) {
		return ((IFeature) features.get(index)).getGeometry().getGeometryType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId) {
		IFeature feature = (IFeature) features.get((int) rowIndex);
		return feature.getAttributes()[fieldId];
	}

	public int getFieldCount() {
		return fields.length;
	}

	public String getFieldName(int fieldId) {
		return fields[fieldId].getFieldName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getRowCount()
	 */
	public long getRowCount() {
		return features.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory
	 * (com.hardcode.gdbms.engine.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public int getFieldWidth(int fieldId) {
		// TODO
		return 30;
	}
}
