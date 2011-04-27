/*
 * Created on 06-feb-2006
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
 * $Id: SHPLayerDefinition.java 10627 2007-03-06 17:10:21Z caballero $
 * $Log$
 * Revision 1.4  2007-03-06 17:08:54  caballero
 * Exceptions
 *
 * Revision 1.3  2006/03/15 12:12:22  fjp
 * Creación desde cero de un tema POR FIN.
 *
 * Revision 1.2  2006/02/13 16:46:20  azabala
 * *** empty log message ***
 *
 * Revision 1.1  2006/02/06 19:26:23  azabala
 * It defines a SHP Layer (first version of cvs)
 *
 *
 */
package com.iver.cit.gvsig.fmap.drivers;

import java.io.File;

/**
 * It has all definition information to create a persistent shapefile datastore.
 * 
 * @author azabala
 * 
 */
public class SHPLayerDefinition extends LayerDefinition {
	/**
	 * path of shp file
	 */
	private String shpPath;

	/**
	 * path of shx file
	 */
	private String shxPath;

	/**
	 * path of dbf file
	 */
	private String dbfPath;

	/**
	 * kind of geometry (shape) that will save the shp file
	 */
	private int shapeType;

	/**
	 * data type of DBF file fields
	 */
	private int[] fieldTypes;

	/**
	 * name of DBF file fields
	 */
	private String[] fieldNames;

	private int[] fieldLenght;

	private int[] decimalCount;
	
	private File shpFile;

	public SHPLayerDefinition(File file) {
		setFile(file);
	}
	
	public SHPLayerDefinition(){
		
	}

	public void setFile(File f) {
		shpFile = f;
		shpPath = f.getAbsolutePath();

		String strFichshx = f.getAbsolutePath().replaceAll("\\.shp", ".shx");
		shxPath = strFichshx.replaceAll("\\.SHP", ".SHX");

		String strFichDbf = f.getAbsolutePath().replaceAll("\\.shp", ".dbf");
		dbfPath = strFichDbf.replaceAll("\\.SHP", ".DBF");

	}
	
	public File getFile()
	{
		return shpFile;
	}

	public String getDbfPath() {
		return dbfPath;
	}

	
	public int[] getDecimalCount() {
		return decimalCount;
	}

	public void setDecimalCount(int[] decimalCount) {
		this.decimalCount = decimalCount;
	}

	public int[] getFieldLenght() {
		return fieldLenght;
	}

	public void setFieldLenght(int[] fieldLenght) {
		this.fieldLenght = fieldLenght;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public int[] getFieldTypes() {
		return fieldTypes;
	}

	public void setFieldTypes(int[] fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	public int getShapeType() {
		return shapeType;
	}

	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}

	public String getShpPath() {
		return shpPath;
	}

	public String getShxPath() {
		return shxPath;
	}
}
