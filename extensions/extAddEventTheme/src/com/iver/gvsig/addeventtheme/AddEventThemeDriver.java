/*
 * Created on 10-nov-2005
 *
 * gvSIG. Sistema de Informacin Geogrfica de la Generalitat Valenciana
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
 *   Av. Blasco Ibez, 50
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

/*
 * $log$
 */
package com.iver.gvsig.addeventtheme;

import java.awt.geom.Rectangle2D;

import org.gvsig.tools.file.PathGenerator;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.IDataSourceListener;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.utiles.IPersistence;
import com.iver.utiles.XMLEntity;

/**
 * The class AddEventThemeDriver allows to create a new FLayer from a
 * gvSIG DataSource.
 *
 * @author jmorell
 */
public class AddEventThemeDriver implements VectorialDriver, ObjectDriver, BoundedShapes, IPersistence, IDataSourceListener {
    private Rectangle2D fullExtent = null;
    private SelectableDataSource ds;
    private int xFieldIndex;
    private int yFieldIndex;

    /**
     * Initializes this.
     * @param ds
     * @param xFieldIndex
     * @param yFieldIndex
     * @throws ReadDriverException
     * @throws DriverException
     */
    public void setData(DataSource ds, int xFieldIndex, int yFieldIndex) throws ReadDriverException {
        this.ds = new SelectableDataSource(ds);
        this.ds.addDataSourceListener(this);
        this.xFieldIndex = xFieldIndex;
        this.yFieldIndex = yFieldIndex;
    }
    public int[] getFieldsIndex(){
    	int[] n=new int[2];
    	n[0]=xFieldIndex;
    	n[1]=yFieldIndex;
    	return n;
    }
    public int getShapeType() {
        return FShape.POINT;
    }

    public int getShapeCount() throws ReadDriverException {
        return (int) ds.getRowCount();
    }

    public DriverAttributes getDriverAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    private double getX(int row) throws ReadDriverException {
    	try {
    		return (new Double(ds.getFieldValue((int)row, xFieldIndex).toString())).doubleValue();
    	} catch(NumberFormatException e) {
    		return 0;
    	}
    }
    private double getY(int row) throws ReadDriverException {
    	try {
    		return (new Double(ds.getFieldValue((int)row, yFieldIndex).toString())).doubleValue();
    	} catch(NumberFormatException e) {
    		return 0;
    	}
    }

    public Rectangle2D getFullExtent() throws ReadDriverException {
		if (fullExtent == null) {
			for (int i = 0; i < ds.getRowCount(); i++) {
				double x = this.getX(i); // (new
											// Double(((Value)ds.getFieldValue((int)i,
											// xFieldIndex)).toString())).doubleValue();
				double y = this.getY(i); // (new
											// Double(((Value)ds.getFieldValue((int)i,
											// yFieldIndex)).toString())).doubleValue();
				FGeometry geometry = ShapeFactory.createGeometry(new FPoint2D(
						x, y));
				Rectangle2D rect = geometry.getBounds2D();
				if (fullExtent == null) {
					fullExtent = rect;
				} else {
					fullExtent.add(rect);
				}
			}
		}
		return fullExtent;
	}

    public IGeometry getShape(int index) throws ReadDriverException{
        double x;
        double y;
        try {
            x = this.getX(index); // (new
									// Double(((Value)ds.getFieldValue(index,
									// xFieldIndex)).toString())).doubleValue();
            y = this.getY(index); //(new Double(((Value)ds.getFieldValue(index, yFieldIndex)).toString())).doubleValue();
            //System.err.println("La X = "+x+" , La Y = "+y);
            FGeometry geometry = ShapeFactory.createGeometry(new FPoint2D(x, y));
            return geometry;
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return "Add Event Layer Driver";
    }

    public int[] getPrimaryKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    public void write(DataWare dataWare) {
        // TODO Auto-generated method stub

    }

    public void setDataSourceFactory(DataSourceFactory arg0) {
        // TODO Auto-generated method stub

    }

    public Value getFieldValue(long rowIndex, int fieldId) throws ReadDriverException {
        return ds.getFieldValue(rowIndex, fieldId);
    }

    public int getFieldCount() throws ReadDriverException {
        return ds.getFieldCount();
    }

    public String getFieldName(int fieldId) throws ReadDriverException {
        return ds.getFieldName(fieldId);
    }

    public long getRowCount() throws ReadDriverException {
        return ds.getRowCount();
    }

    public int getFieldType(int i) throws ReadDriverException {
        return ds.getFieldType(i);
    }

    public String getClassName() {
        return this.getClass().getName();
    }


    // Para guardar en el xml file
    public XMLEntity getXMLEntity() {
        XMLEntity xml = new XMLEntity();
        xml.putProperty("className", this.getClass().getName());
        xml.putProperty("xFieldIndex", xFieldIndex);
        xml.putProperty("yFieldIndex", yFieldIndex);
        xml.putProperty("tableName", ds.getName());
        XMLEntity dsXML = null;

		dsXML = this.getDataSourceXML();
		if (dsXML != null) {
			xml.putProperty("hasDSInfo", true);
			xml.addChild(dsXML);
		} else {
			xml.putProperty("hasDSInfo", false);
		}
		return xml;
    }

    // Para recuperar del xml file
    public void setXMLEntity(XMLEntity xml) {
    	int xFieldIndex = xml.getIntProperty("xFieldIndex");
    	int yFieldIndex = xml.getIntProperty("yFieldIndex");
    	String tableName = xml.getStringProperty("tableName");
    	DataSource ds;
    	try {
    		try {
    			ds = LayerFactory.getDataSourceFactory().createRandomDataSource(tableName, DataSourceFactory.AUTOMATIC_OPENING);
//  			Intentar reconstruir el DS!!!!!!!!!!
    		} catch (NoSuchTableException e) {
    			if (!xml.contains("hasDSInfo") || !xml.getBooleanProperty("hasDSInfo")) {
    				// No esta registrado el DS y no tenemos su informacion en el
    				// xml (proyecto viejo)... no podemos arreglarlo
    				throw new RuntimeException(e);
    			}
    			try {
					ds = this.getDataSourceFromXML(xml.getChild(0));
				} catch (NoSuchTableException e1) {
					throw new RuntimeException(e);
				}
    		}
    		setData(ds, xFieldIndex, yFieldIndex);
    	} catch (DriverLoadException e) {
    		throw new RuntimeException(e);
    	} catch (ReadDriverException e) {
    		throw new RuntimeException(e);
    	}

    }


	public int getFieldWidth(int i) throws ReadDriverException {
		return ds.getFieldWidth(i);
	}

	public Rectangle2D getShapeBounds(int index) throws ReadDriverException {
		return getShape(index).getBounds2D();
	}

	public int getShapeType(int index) throws ReadDriverException {
		return getShape(index).getGeometryType();
	}
	public boolean isWritable() {
		return true;
	}

	public void reloaded(DataSource dataSource) {
		this.fullExtent = null;

	}

	public void reload() throws ReloadDriverException{
		this.ds.reload();

	}

    private XMLEntity getDataSourceXML() {
    	SourceInfo di = this.ds.getSourceInfo();
    	ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);

    	return ext.getProject().getSourceInfoXMLEntity(di);
    }

    private DataSource getDataSourceFromXML(XMLEntity xml) throws DriverLoadException, ReadDriverException, NoSuchTableException{
		if (xml.getStringProperty("type").equals("otherDriverFile")) {
			LayerFactory.getDataSourceFactory().addFileDataSource(xml.getStringProperty(
					"driverName"), xml.getStringProperty("gdbmsname"),
				PathGenerator.getInstance().getAbsolutePath(xml.getStringProperty("file")));
		} else if (xml.getStringProperty("type").equals("sameDriverFile")) {
		} else if (xml.getStringProperty("type").equals("db")) {
			LayerFactory.getDataSourceFactory().addDBDataSourceByTable(xml.getStringProperty(
					"gdbmsname"), xml.getStringProperty("host"),
				xml.getIntProperty("port"),
				xml.getStringProperty("user"),
				xml.getStringProperty("password"),
				xml.getStringProperty("dbName"),
				xml.getStringProperty("tableName"),
				xml.getStringProperty("driverInfo"));
		}


		DataSource ds = LayerFactory.getDataSourceFactory().createRandomDataSource(xml.getStringProperty(
		"gdbmsname"), DataSourceFactory.AUTOMATIC_OPENING);

		return ds;

    }

}
