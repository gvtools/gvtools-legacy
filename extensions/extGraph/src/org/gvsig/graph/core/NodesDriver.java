/*
 * Created on 06-nov-2006
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
 * $Id: NodesDriver.java 22182 2008-07-10 07:20:11Z fpenarrubia $
 * $Log$
 * Revision 1.3  2007-09-17 11:44:08  fjp
 * Primera compilación que se puede ejecutar con el HEAD. Falta revisar algo del etiquetado, y poner código en el isEnabled e isVisible, pero con esto ya se puede empezar a trabajar.
 *
 * Revision 1.2  2007/09/07 11:29:47  fjp
 * Casi compila. Falta arreglar lo de FArrowSymbol y retocar el graphiclist de FMap.
 *
 * Revision 1.1.2.2  2007/08/09 12:18:27  fjp
 * Area de influencia con leyenda
 *
 * Revision 1.1.2.1  2007/08/08 11:43:40  fjp
 * Principio de giros y area de influencia
 *
 * Revision 1.2  2006/11/08 19:32:36  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/07 19:49:28  azabala
 * *** empty log message ***
 *
 *
 */
package org.gvsig.graph.core;

import java.awt.geom.Rectangle2D;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.cit.gvsig.project.documents.view.legend.gui.AttrInTableLabeling;

/**
 * Driver wrapper around nodes from a network. It may be useful to show the nodes
 * as a layer without consuming more memory.
 * Is a tmpLayer, and when the layer is created, it should be uses with some BeforeSavingListener
 * in order to delete the layer before saving project. See AddLayerNodesExtension (TODO) 
 * 
 * @author Fco. José Peñarrubia
 * 
 */
public class NodesDriver implements VectorialDriver, ObjectDriver, BoundedShapes 
		 {
	static FieldDescription[] fields = new FieldDescription[4];
	static {
		FieldDescription fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("IDNODE");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[0] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("ORDER");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[1] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COSTN1");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[2] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("COSTN2");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[3] = fieldDesc;

	}

	Network net;
	private AttrInTableLabeling labeling;

	Rectangle2D fullExtent;

	public NodesDriver(Network net) {
		this.net = net;
		labeling = new AttrInTableLabeling();
		
	}

	public int getShapeType() {
		return FShape.POINT;
	}

	public String getName() {
		return "NodeDriver";
	}

	public DriverAttributes getDriverAttributes() {
		return null;
	}

	public boolean isWritable() {
		return false;
	}

	public int[] getPrimaryKeys()
			 {
		return null;
	}

	public void write(DataWare dataWare)
			 {
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
		return net.getGraph().numVertices();
	}

	public Rectangle2D getFullExtent() {
		try {
			return net.getLayer().getFullExtent();
		} catch (ExpansionFileReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IGeometry getShape(int index) {
		GvNode n = net.getGraph().getNodeByID(index);
		return ShapeFactory.createPoint2D(n.getX(), n.getY());
	}

	public void reload() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.ReadDriver#getFieldValue(long, int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			 {
		Value val = ValueFactory.createNullValue();
		GvNode n = net.getGraph().getNodeByID((int) rowIndex);
		switch (fieldId) {
		case 0: // idNode
			return ValueFactory.createValue(n.getIdNode());
		case 1: // order
			return ValueFactory.createValue(n.getOutputDegree());
		case 2: // cost node1
			if (n.getBestCost() == Double.MAX_VALUE)
				return ValueFactory.createValue(-1.0);
			else
				return ValueFactory.createValue(n.getBestCost());
		case 3: // cost node2
			if (n.getAccumulatedLength() == Double.MAX_VALUE)
				return ValueFactory.createValue(-1.0);
			else
				return ValueFactory.createValue(n.getAccumulatedLength());

		}
		return val;
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
		return net.getGraph().numVertices();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.gdbms.engine.data.driver.GDBMSDriver#setDataSourceFactory(com.hardcode.gdbms.engine.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public int getFieldWidth(int fieldId) {
		return fields[fieldId].getFieldLength();
	}


	public ILabelingStrategy getDefaultLabelingStrategy() {
		return (ILabelingStrategy) labeling;
	}

	public Rectangle2D getShapeBounds(int index) throws ReadDriverException, ExpansionFileReadException {
		GvNode n = net.getGraph().getNodeByID(index);
		Rectangle2D bound = new Rectangle2D.Double(n.getX(), n.getY(), 0.3, 0.3);
		return bound;
	}

	public int getShapeType(int index) throws ReadDriverException {
		return getShapeType();
	}

}
