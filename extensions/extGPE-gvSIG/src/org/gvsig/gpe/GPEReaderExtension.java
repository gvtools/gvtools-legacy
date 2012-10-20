package org.gvsig.gpe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Properties;

import org.gvsig.fmap.drivers.gpe.addlayer.GPEFileOpen;
import org.gvsig.fmap.drivers.gpe.reader.GMLVectorialDriver;
import org.gvsig.gpe.gml.GmlProperties;
import org.gvsig.gpe.xml.XmlProperties;

import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

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
/* CVS MESSAGES:
 *
 * $Id$
 * $Log$
 *
 */
/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class GPEReaderExtension extends Extension {
	private String parsersFile = "gvSIG" + File.separatorChar + "extensiones"
			+ File.separatorChar + "org.gvsig.gpe" + File.separatorChar
			+ "parser.properties";
	private String driversDir = "gvSIG" + File.separatorChar + "extensiones"
			+ File.separatorChar + "org.gvsig.gpe" + File.separatorChar + "lib";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	public void execute(String actionCommand) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		loadParsers();
		loadProperties();
		ExtensionPoints extensionPoints = ExtensionPointsSingleton
				.getInstance();
		extensionPoints.add("FileExtendingOpenDialog", "FileOpenGPE",
				new GPEFileOpen());
		// Register the GML driver in the WFS Driver
		extensionPoints.add("FMAPWFSDriver", "FMAPWFSDriver",
				GMLVectorialDriver.class);
		/*
		 * //PluginServices pluginServices =
		 * PluginServices.getPluginServices(GPEXmlParserFactory.class);
		 * PluginServices pluginServices =
		 * PluginServices.getPluginServices("com.iver.cit.gvsig"); URL[] urls =
		 * pluginServices.getClassLoader().getURLs(); for (int i=0 ;
		 * i<urls.length ; i++){ System.out.println(urls[i]); } try {
		 * pluginServices.getClassLoader().loadClass(
		 * "org.gvsig.gpe.xml.stream.stax.StaxXmlStreamWriter");
		 * pluginServices.getClassLoader
		 * ().loadClass("org.gvsig.gpe.gml.GmlProperties");
		 * GPEXmlParserFactory.setClassLoader(pluginServices.getClassLoader());
		 * GPEXmlParserFactory.getParser("text/xml; subtype=gml/2.1.0", new
		 * FileInputStream("/home/jpiera/output.gml")); } catch
		 * (XmlStreamException e) { e.printStackTrace(); } catch
		 * (FileNotFoundException e) { e.printStackTrace(); } catch
		 * (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	private void loadProperties() {
		loadProperties(new GPEProperties().getProperties());
		loadProperties(new XmlProperties().getProperties());
		loadProperties(new GmlProperties().getProperties());
	}

	private void loadProperties(Properties properties) {
		Iterator it = properties.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			GPEDefaults.setProperty(key, properties.get(key));
		}
	}

	private void loadParsers() {
		File file = new File(parsersFile);
		if (!file.exists()) {
			NotificationManager.addWarning("File not found",
					new FileNotFoundException());
			return;
		}
		try {
			GPERegister.addParsersFile(file);
		} catch (Exception e) {
			NotificationManager.addWarning("GPE parsers file not found",
					new FileNotFoundException());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		return false;
	}

}
