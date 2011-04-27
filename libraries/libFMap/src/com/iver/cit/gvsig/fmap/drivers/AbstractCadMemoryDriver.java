/*
 * Created on 26-oct-2006
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
* $Id: AbstractCadMemoryDriver.java 10822 2007-03-20 20:16:09Z azabala $
* $Log$
* Revision 1.4  2007-03-20 20:16:09  azabala
* added Region of Interest to filter features out of them
*
* Revision 1.2  2007/01/12 19:16:09  azabala
* Added getCadSource(int) method
*
* Revision 1.1  2006/10/26 17:05:30  azabala
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.fmap.drivers;

import java.awt.geom.Rectangle2D;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.BadFieldDriverException;

/**
 * Abstract base class for CAD drivers (dxf, dwg) memory drivers.
 * 
 * Features readed with these drivers share the same schema
 * (static FieldDescription[])
 * 
 * @author azabala
 * 
 * */
public abstract class AbstractCadMemoryDriver extends MemoryDriver {
	
	protected final static int ID_FIELD_ID = 0;

	protected final static int ID_FIELD_FSHAPE = 1;

	protected final static int ID_FIELD_ENTITY = 2;

	protected final static int ID_FIELD_LAYER = 3;

	protected final static int ID_FIELD_COLOR = 4;

	protected final static int ID_FIELD_ELEVATION = 5;

	protected final static int ID_FIELD_THICKNESS = 6;

	protected final static int ID_FIELD_TEXT = 7;

	protected final static int ID_FIELD_HEIGHTTEXT = 8;

	protected final static int ID_FIELD_ROTATIONTEXT = 9;

	protected final static FieldDescription[] fields = new FieldDescription[10];
	static{
		FieldDescription fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("ID");
		fieldDesc.setFieldType(Types.INTEGER);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(0);
		fields[0] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("FShape");
		fieldDesc.setFieldType(Types.VARCHAR);
		fieldDesc.setFieldLength(254);
		fieldDesc.setFieldDecimalCount(0);
		fields[1] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Entity");
		fieldDesc.setFieldType(Types.VARCHAR);
		fieldDesc.setFieldLength(254);
		fieldDesc.setFieldDecimalCount(0);
		fields[2] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Layer");
		fieldDesc.setFieldType(Types.VARCHAR);
		fieldDesc.setFieldLength(254);
		fieldDesc.setFieldDecimalCount(0);
		fields[3] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Color");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[4] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Elevation");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[5] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Thickness");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[6] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Text");
		fieldDesc.setFieldType(Types.VARCHAR);
		fieldDesc.setFieldLength(254);
		fieldDesc.setFieldDecimalCount(0);
		fields[7] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Height");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[8] = fieldDesc;

		fieldDesc = new FieldDescription();
		fieldDesc.setFieldName("Rotation");
		fieldDesc.setFieldType(Types.DOUBLE);
		fieldDesc.setFieldLength(20);
		fieldDesc.setFieldDecimalCount(5);
		fields[9] = fieldDesc;
	}
	
	protected List rois = new ArrayList();
	
	/**
	 * Returns de field type of the specified field index.
	 * @return field type of i field
	 */
	public int getFieldType(int i) throws BadFieldDriverException {
	  //azabala: we overwrite MemoryDriver because type resolution
	  //is based in first register value (it could be null)
      if(i >= fields.length)
    	  throw new BadFieldDriverException(getName(),null,String.valueOf(i));
      return fields[i].getFieldType();
	}
	
	/**
	 * Any feature of this kind of driver will be associated to a
	 * cad drawing entity.
	 * This method will return this drawing entity
	 * 
	 * @param index position of the drawing entity
	 * 
	 * 
	 * */
	public abstract Object getCadSource(int index);
	
	/**
	 * As CAD files are designed primary to printing task,
	 * in a cad file (dwg, dgn, dxf, etc.) could exist real
	 * world entities (thinked to work in a GIS system) in real
	 * world coordinates (UTM, Lambert, etc.) and paper entities,
	 * like transversal profiles, electrical circuits, etc. thinked
	 * to do a final print.
	 * This region of interest allow to filter cad entities by a 
	 * zone.
	 * */
	public void addRegionOfInterest(Rectangle2D roi){
		rois.add(roi);
	}
	public Rectangle2D getRegionOfInterest(int index){
		return (Rectangle2D) rois.get(index);
	}
	public  List getRois(){
		return rois;
	}
	public  void setRois(List rois){
		this.rois = rois;
	}
	
	public int getNumOfRois(){
		return rois.size();
	}
	
	
}

