package org.gvsig.tableImport.addgeominfo;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

import java.sql.Types;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.gvsig.tableImport.addgeominfo.gui.AddGeometricInfoPanel;
import org.gvsig.tableImport.addgeominfo.util.StringUtilitiesExtended;

import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;

/**
 * <p>
 * Label used in the lists of {@link AddGeometricInfoPanel
 * AddGeometricInfoPanel}, that represent a geometric information of a geometry.
 * </p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class GeomInfo extends JLabel {
	private static final long serialVersionUID = 1313562268056235866L;

	/**
	 * Default subtype.
	 */
	public static final short UNDEFINED = -1;

	/**
	 * Subtype, determines the area of a polygon.
	 */
	public static final short AREA = 0;

	/**
	 * Subtype, determines the perimeter of a polygon.
	 */
	public static final short PERIMETER = 1;

	/**
	 * Subtype, determines the X of a point.
	 */
	public static final short X = 2;

	/**
	 * Subtype, determines the Y of a point.
	 */
	public static final short Y = 3;

	/**
	 * Subtype, determines the Z of a point 3D.
	 */
	public static final short Z = 4;

	/**
	 * Shape geometry type.
	 * 
	 * @see Types
	 */
	private int shapeType;

	/**
	 * <p>
	 * If the geometry type has more than one geometric info, this parameter
	 * identifies each one.
	 * </p>
	 * 
	 * <p>
	 * It's optional, only used by some geometry types, like the polygon or
	 * point.
	 * </p>
	 */
	private int geomSubType; // Only used for POLYGON shapes

	/**
	 * <p>
	 * Determines if the geometric information will be added as a new column.
	 * </p>
	 */
	private boolean isNewColumn;

	/**
	 * <p>
	 * Creates a new <code>GeomInfo</code>.
	 * </p>
	 * 
	 * @param icon
	 *            the icon that represents this geometry type
	 * @param text
	 *            the text of the label
	 * @param name
	 *            the name of the geometric information
	 * @param shapeType
	 *            the type of the geometric information
	 * 
	 * @see Types
	 */
	public GeomInfo(ImageIcon icon, String text, String name, int shapeType) {
		super(text, icon, JLabel.LEFT);
		this.shapeType = shapeType;
		this.isNewColumn = true; // By default, is a new column
		this.geomSubType = UNDEFINED;
		setName(StringUtilitiesExtended
				.replaceAllAccents(StringUtilitiesExtended
						.replaceAllCedilla(StringUtilitiesExtended
								.replaceAllNWithTilde(name))));
	}

	/**
	 * <p>
	 * Creates a new {@link FieldDescription FieldDescription} with the
	 * geometric information.
	 * </p>
	 * 
	 * @param geomInfo
	 *            object that identifies the geometric information
	 * @param type
	 *            SQL type
	 * @param length
	 *            the length of the field
	 * @param decimalCount
	 *            the length of the decimal part of a decimal number
	 * 
	 * @return the new field description, or
	 *         <code>null</p> if any problem succeed
	 */
	public static FieldDescription getFieldDescription(GeomInfo geomInfo,
			int type, int length, short decimalCount) {
		try {
			FieldDescription fD = new FieldDescription();

			// Bug fixed -> data source doesn't display the accents
			String name = StringUtilitiesExtended
					.replaceAllAccents(StringUtilitiesExtended
							.replaceAllCedilla(StringUtilitiesExtended
									.replaceAllNWithTilde(geomInfo.getName())));
			fD.setFieldName(name);
			fD.setFieldAlias(name);
			fD.setFieldType(type);
			fD.setFieldLength(length);

			switch (type) {
			case Types.DOUBLE:
				fD.setDefaultValue(ValueFactory.createValueByType("0", type));
				fD.setFieldDecimalCount(decimalCount);
				break;
			case Types.BIGINT:
				fD.setDefaultValue(ValueFactory.createValueByType("0", type));
				break;
			case Types.VARCHAR:
				fD.setDefaultValue(ValueFactory.createValueByType("", type));
				break;
			default: // Unsupported
				return null;
			}

			return fD;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the shape geometry type.
	 * 
	 * @return the shape geometry type
	 * 
	 * @see Types
	 */
	public int getShapeType() {
		return shapeType;
	}

	/**
	 * Determines if the geometric information will be added as a new column.
	 * 
	 * @return <code>true</code> if the geometric information will be added as a
	 *         new column, otherwise <code>false</code>
	 */
	public boolean isNewColumn() {
		return isNewColumn;
	}

	/**
	 * Sets if the geometric information will be added as a new column, or not.
	 * 
	 * @param b
	 *            <code>true</code> if the geometric information will be added
	 *            as a new column, otherwise <code>false</code>
	 */
	public void setNewColumn(boolean b) {
		isNewColumn = b;
	}

	/**
	 * Identifies with geometric information of the geometry type represents
	 * this object.
	 * 
	 * @return identifier of the geometric information
	 */
	public int getGeomSubType() {
		return geomSubType;
	}

	/**
	 * Sets the geometric information of the geometry type.
	 * 
	 * @param geomSubType
	 *            identifier of the geometric information
	 */
	public void setGeomSubType(int geomSubType) {
		this.geomSubType = geomSubType;
	}
}
