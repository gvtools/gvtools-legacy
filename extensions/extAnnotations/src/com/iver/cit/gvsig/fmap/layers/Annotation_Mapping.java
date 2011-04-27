
/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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

package com.iver.cit.gvsig.fmap.layers;

import java.awt.Color;
import java.awt.Font;
import java.sql.Types;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;


/**
 * Mapping of annotation's layers.
 *
 * @author Vicente Caballero Navarro
 */
public class Annotation_Mapping {
	public static String DEFAULTTEXT = "New";
    public static String DEFAULTTYPEFONT = "Arial";
    public static int DEFAULTROTATE = 0;
    public static int DEFAULTSTYLEFONT = Font.PLAIN;
    public static int DEFAULTHEIGHT = 10;
    public static int DEFAULTCOLOR = Color.black.getRGB();
    public static String TEXT = "Text";
    public static String ROTATE = "Rotate";
    public static String COLOR = "Color";
    public static String HEIGHT = "Height";
    public static String TYPEFONT = "TypeFont";
    public static String STYLEFONT = "StyleFont";
    private static int TYPETEXT = Types.VARCHAR;
    private static int TYPEROTATE = Types.DOUBLE;
    private static int TYPECOLOR = Types.INTEGER;
    private static int TYPEHEIGHT = Types.DOUBLE;
    private static int TYPETYPEFONT = Types.VARCHAR;
    private static int TYPESTYLEFONT = Types.INTEGER;
    public static int NUMCOLUMNS = 6;
    private int columnText = -1;
    private int columnRotate = -1;
    private int columnColor = -1;
    private int columnHeight = -1;
    private int columnTypeFont = -1;
    private int columnStyleFont = -1;

    public static String DEFAULT_ANNOTATION_TEXT = "default_annotation_text";

	public static String DEFAULT_ANNOTATION_TYPEFONT = "default_annotation_typefont";

	public static String DEFAULT_ANNOTATION_ROTATE = "default_annotation_rotate";

	public static String DEFAULT_ANNOTATION_STYLEFONT = "default_annotation_stylefont";

	public static String DEFAULT_ANNOTATION_HEIGHT = "default_annotation_height";

	public static String DEFAULT_ANNOTATION_COLOR = "default_annotation_color";
	private static PluginServices ps = null;
	static {
		new Annotation_Mapping();
	}
    public Annotation_Mapping() {
    	ps = PluginServices.getPluginServices(this);
		XMLEntity xml = ps.getPersistentXML();

		if (xml.contains(DEFAULT_ANNOTATION_TEXT)) {
			String text=xml.getStringProperty(DEFAULT_ANNOTATION_TEXT);
			Annotation_Mapping.DEFAULTTEXT=text;
		}else{
			Annotation_Mapping.DEFAULTTEXT="";
		}
		if (xml.contains(DEFAULT_ANNOTATION_TYPEFONT)) {
			String text=xml.getStringProperty(DEFAULT_ANNOTATION_TYPEFONT);
			Annotation_Mapping.DEFAULTTYPEFONT=text;
		}else{
			Annotation_Mapping.DEFAULTTYPEFONT="Arial";
		}
		if (xml.contains(DEFAULT_ANNOTATION_STYLEFONT)) {
			int styleFont=xml.getIntProperty(DEFAULT_ANNOTATION_STYLEFONT);
			if (styleFont!=-1){
				Annotation_Mapping.DEFAULTSTYLEFONT=styleFont;
			}
		}else{
			Annotation_Mapping.DEFAULTSTYLEFONT=Font.PLAIN;
		}
		if (xml.contains(DEFAULT_ANNOTATION_COLOR)) {
			String stringColor=xml.getStringProperty(DEFAULT_ANNOTATION_COLOR);
			Color color=StringUtilities.string2Color(stringColor);
			Annotation_Mapping.DEFAULTCOLOR=color.getRGB();
		}else{
			Color color=Color.black;
			Annotation_Mapping.DEFAULTCOLOR=color.getRGB();
		}
		if (xml.contains(DEFAULT_ANNOTATION_HEIGHT)) {
			int height=xml.getIntProperty(DEFAULT_ANNOTATION_HEIGHT);
			Annotation_Mapping.DEFAULTHEIGHT=height;
		}else{
			Annotation_Mapping.DEFAULTHEIGHT=10;
		}
		if (xml.contains(DEFAULT_ANNOTATION_ROTATE)) {
			int rotate=xml.getIntProperty(DEFAULT_ANNOTATION_ROTATE);
			Annotation_Mapping.DEFAULTROTATE=rotate;
		}else{
			Annotation_Mapping.DEFAULTROTATE=0;
		}
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnColor() {
        return columnColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnColor DOCUMENT ME!
     */
    public void setColumnColor(int columnColor) {
        this.columnColor = columnColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnHeight() {
        return columnHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnHeight DOCUMENT ME!
     */
    public void setColumnHeight(int columnHeight) {
        this.columnHeight = columnHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnRotate() {
        return columnRotate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnRotate DOCUMENT ME!
     */
    public void setColumnRotate(int columnRotate) {
        this.columnRotate = columnRotate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnStyleFont() {
        return columnStyleFont;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnStyleFont DOCUMENT ME!
     */
    public void setColumnStyleFont(int columnStyleFont) {
        this.columnStyleFont = columnStyleFont;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnText() {
        return columnText;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnText DOCUMENT ME!
     */
    public void setColumnText(int columnText) {
        this.columnText = columnText;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnTypeFont() {
        return columnTypeFont;
    }

    /**
     * DOCUMENT ME!
     *
     * @param columnTypeFont DOCUMENT ME!
     */
    public void setColumnTypeFont(int columnTypeFont) {
        this.columnTypeFont = columnTypeFont;
    }

    // public XMLEntity getXMLEntity() {
    // XMLEntity xml=new XMLEntity();
    // xml.putProperty("className",this.getClass().getName());
    // xml.putProperty("columnText",columnText);
    // xml.putProperty("columnRotate",columnRotate);
    // xml.putProperty("columnColor",columnColor);
    // xml.putProperty("columnHeight",columnHeight);
    // xml.putProperty("columnTypeFont",columnTypeFont);
    // xml.putProperty("columnStyleFont",columnStyleFont);
    // return xml;
    // }
    // public static Annotation_Mapping createFromXML(XMLEntity xml) {
    // Annotation_Mapping m=new Annotation_Mapping();
    // m.setColumnText(xml.getIntProperty("columnText"));
    // m.setColumnRotate(xml.getIntProperty("columnRotate"));
    // m.setColumnColor(xml.getIntProperty("columnColor"));
    // m.setColumnHeight(xml.getIntProperty("columnHeight"));
    // m.setColumnTypeFont(xml.getIntProperty("columnTypeFont"));
    // m.setColumnStyleFont(xml.getIntProperty("columnStyleFont"));
    // return m;
    // }
    public static int getType(String column) {
        if (column.equals(TEXT)) {
            return TYPETEXT;
        } else if (column.equals(COLOR)) {
            return TYPECOLOR;
        } else if (column.equals(ROTATE)) {
            return TYPEROTATE;
        } else if (column.equals(COLOR)) {
            return TYPECOLOR;
        } else if (column.equals(HEIGHT)) {
            return TYPEHEIGHT;
        } else if (column.equals(TYPEFONT)) {
            return TYPETYPEFONT;
        } else if (column.equals(STYLEFONT)) {
            return TYPESTYLEFONT;
        }

        return Types.VARCHAR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param al DOCUMENT ME!
     * @throws ReadDriverException
     * @throws DriverException
     * @throws FieldNotFoundException
     *
     * @throws com.hardcode.gdbms.engine.data.driver.DriverException DOCUMENT
     *         ME!
     * @throws DriverException
     * @throws FieldNotFoundException
     */
    public static void addAnnotationMapping(Annotation_Layer al) throws ReadDriverException {
        Annotation_Mapping am = new Annotation_Mapping();
        SelectableDataSource sds = al.getSource().getRecordset();
        int numFields = sds.getFieldCount();

        for (int i = 0; i < numFields; i++) {
            String nameField = sds.getFieldName(i);

            if (nameField.equalsIgnoreCase(COLOR)) {
                am.setColumnColor(i);
            } else if (nameField.equalsIgnoreCase(HEIGHT)) {
                am.setColumnHeight(i);
            } else if (nameField.equalsIgnoreCase(ROTATE)) {
                am.setColumnRotate(i);
            } else if (nameField.equalsIgnoreCase(STYLEFONT)) {
                am.setColumnStyleFont(i);
            } else if (nameField.equalsIgnoreCase(TEXT)) {
                am.setColumnText(i);
            } else if (nameField.equalsIgnoreCase(TYPEFONT)) {
                am.setColumnTypeFont(i);
            }
        }

        try {
			al.setMapping(am);
		} catch (LegendLayerException e) {
			throw new ReadDriverException(al.getName(),e);
		} catch (ReadDriverException e) {
			throw new ReadDriverException(al.getName(),e);
		}
		if (am.getColumnText()==-1) {
			throw new ReadDriverException(al.getName(), new RuntimeException("This does not seem an annotation layer"));
		}
    }

	public static void storeValues(Color fontColor, String text, String fontType, int fontStyle, int fontHeight, int fontRotate) {
		XMLEntity xml = ps.getPersistentXML();

		xml.putProperty(DEFAULT_ANNOTATION_COLOR,
			StringUtilities.color2String(fontColor));
		xml.putProperty(DEFAULT_ANNOTATION_TEXT,
			text);
		xml.putProperty(DEFAULT_ANNOTATION_TYPEFONT,
			fontType);
		xml.putProperty(DEFAULT_ANNOTATION_STYLEFONT,
				fontStyle);
		xml.putProperty(DEFAULT_ANNOTATION_HEIGHT,
				fontHeight);
		xml.putProperty(DEFAULT_ANNOTATION_ROTATE,
				fontRotate);
	}
}
