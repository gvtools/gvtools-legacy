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
 * $Id: ShpSchemaManager.java 10665 2007-03-08 12:15:33Z caballero $
 * $Log$
 * Revision 1.11  2007-03-08 12:15:18  caballero
 * Exceptions
 *
 * Revision 1.10  2007/03/06 17:08:55  caballero
 * Exceptions
 *
 * Revision 1.9  2006/06/29 07:33:56  fjp
 * Cambios ISchemaManager y IFieldManager por terminar
 *
 * Revision 1.8  2006/04/05 09:00:02  fjp
 * Preparando el driver de escritura PostGIS
 *
 * Revision 1.7  2006/03/15 12:12:22  fjp
 * Creación desde cero de un tema POR FIN.
 *
 * Revision 1.6  2006/03/14 19:28:57  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/03/14 18:17:12  fjp
 * Preparando la creación de cero de un tema
 *
 * Revision 1.4  2006/02/17 10:32:42  caballero
 * cambio de driverManager con writer
 *
 * Revision 1.3  2006/02/07 15:42:05  fjp
 * Uso más genérico y amigable del ShpWriter
 *
 * Revision 1.2  2006/02/06 19:31:14  azabala
 * se ofrece acceso al shp path
 *
 * Revision 1.1  2006/02/06 18:44:24  azabala
 * Firt version in CVS of SHP implementation of ISchemaManager (class that has the responsability to create SHP schemas)
 *
 *
 */
package com.iver.cit.gvsig.fmap.edition;

import java.io.File;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

public class ShpSchemaManager implements ISchemaManager {
	File shpFile;

	/**
	 * Se le pasa el path absoluto al shp file. Pero creo que lo correcto sería
	 * pasarle el path al directorio, y luego funcionar con create, remove o rename
	 * sobre ese directorio por defecto (por hacerlo como MapObjects, creo).
	 * @param shpPath
	 */
	public ShpSchemaManager(String shpPath)
	{
		shpFile = new File(shpPath);
	}
	public void createSchema(ITableDefinition layerDefinition)
			throws SchemaEditionException {
		SHPLayerDefinition definition = (SHPLayerDefinition) layerDefinition;
		try {
		File shpFile = new File(definition.getShpPath());
		ShpWriter shpWriter = (ShpWriter) LayerFactory.getWM().getWriter(
				"Shape Writer");
		shpWriter.setFile(shpFile);

			shpWriter.initialize(definition);
			shpWriter.preProcess();
		} catch (InitializeWriterException e) {
			throw new SchemaEditionException("Shape Writer",e);
		} catch (StartWriterVisitorException e) {
			throw new SchemaEditionException("Shape Writer",e);
		} catch (DriverLoadException e) {
			throw new SchemaEditionException("Shape Writer",e);
		}
	}

	public void removeSchema(String name) throws SchemaEditionException {
		if (!shpFile.delete())
			throw new SchemaEditionException("SHP",null);

		String strFichshx = shpFile.getAbsolutePath().replaceAll("\\.shp", ".shx");
		String shxPath = strFichshx.replaceAll("\\.SHP", ".SHX");
		if (!new File(shxPath).delete())
			throw new SchemaEditionException("SHP",null);

		String strFichDbf = shpFile.getAbsolutePath().replaceAll("\\.shp", ".dbf");
		String dbfPath = strFichDbf.replaceAll("\\.SHP", ".DBF");
		if (!new File(dbfPath).delete())
			throw new SchemaEditionException("SHP",null);
	}

	public void renameSchema(String antName, String newName) {
		String strFichshx = shpFile.getAbsolutePath().replaceAll("\\.shp", ".shx");
		String shxPath = strFichshx.replaceAll("\\.SHP", ".SHX");
		File shxF = new File(shxPath);
		shxF.renameTo(new File(antName + ".shx"));

		String strFichDbf = shpFile.getAbsolutePath().replaceAll("\\.shp", ".dbf");
		String dbfPath = strFichDbf.replaceAll("\\.SHP", ".DBF");
		File dbfF = new File(dbfPath);
		dbfF.renameTo(new File(antName + ".dbf"));

		shpFile.renameTo(new File(antName + ".shx"));

	}

}
