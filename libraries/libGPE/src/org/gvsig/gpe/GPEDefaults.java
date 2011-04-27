package org.gvsig.gpe;

import java.util.Iterator;
import java.util.Properties;

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
 * $Id: GPEDefaults.java 373 2008-01-10 09:33:05Z jpiera $
 * $Log$
 * Revision 1.10  2007/06/20 09:35:37  jorpiell
 * Add the javadoc comments
 *
 * Revision 1.9  2007/06/07 14:52:28  jorpiell
 * Add the schema support
 *
 * Revision 1.8  2007/05/15 10:39:14  jorpiell
 * Add the number of decimals property
 *
 * Revision 1.7  2007/05/15 09:34:39  jorpiell
 * the tag names cant have blanc spaces
 *
 * Revision 1.6  2007/04/26 14:23:16  jorpiell
 * Add a getStringProperty method
 *
 * Revision 1.5  2007/04/19 11:50:20  csanchez
 * Actualizacion protoripo libGPE
 *
 * Revision 1.4  2007/04/18 11:03:36  jorpiell
 * Add the default schema property
 *
 * Revision 1.3  2007/04/14 16:06:13  jorpiell
 * The writer handler has been updated
 *
 * Revision 1.2  2007/04/12 17:06:42  jorpiell
 * First GML writing tests
 *
 * Revision 1.1  2007/04/12 11:39:20  jorpiell
 * Add the GPEDefaults class
 *
 *
 */
/**
 * This class is used to add properties that can be used
 * by the GPE parsers and writers.
 * <p>
 * It is not possible for the consumer application to have any
 * dependence with a concrete parser or writer. But sometimes it
 * is necessary to establish some configuration parameters
 * (principally to write). This class provides a mechanism to
 * set all these parameters using the SPI (Service Provider Interface) 
 * mechanism  
 * </p>
 * <h2>Implementation Lookup</h2>
 * <p>
 * The SPI provides a mechanism to register a set of properties that
 * both the parsers and the writers need to work. Every parser (or writer)
 * is the responsible to create a set of parameters and register them
 * in this class
 * </p>
 * <p> 
 * To register a set of properties a file named <code>org.gvsig.gpe.IGPEProperties</code>
 * shall exist in the class path in the implementation's <code>META-INF/services</code> folder.
 * </p>
 * <p>
 * The content of the files for a given implementation consists of full qualified 
 * class names, one per line. For example, an hypotetical <code>MyParserProperties</code> 
 * in the package <code>org.gvsi.gpe.format</code> and bundled in a jar file 
 * called <code>org.gvsig.gpe.format.jar</code> shall provide the following 
 * resources:
 *  
 * <pre>
 * <code>
 * $jar tvf org.gvsi.gpe.format.jar
 * META-INF/services/org.gvsig.gpe.IGPEProperties
 * org/gvsig/gpe/MyParserProperties.class
 * </code>
 * </pre>
 * 
 * And the content of the file <code>META-INF/services/org.gvsig.gpe.IGPEProperties</code> 
 * shall be a single line of text with the <code>org.gpe.gpe.format</code> class name.
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 */
public class GPEDefaults {
	private static Properties properties = new Properties();
				
	static{				
		Iterator providers = availableProperties();
		while (providers.hasNext()) {
			IGPEProperties next = (IGPEProperties) providers.next();
			Properties parserProperties = next.getProperties();
			Iterator it = parserProperties.keySet().iterator();
			while (it.hasNext()){
				String key = (String)it.next();				
				properties.put(key, parserProperties.get(key));
			}			
		}
	}
	
	/**
	 * Returns an iterator over instances of the registered GPE properties.
	 * @return all the registered GPE properties
	 */
	private static Iterator availableProperties() {
		Iterator providers = sun.misc.Service.providers(IGPEProperties.class);
		return providers;
	}
	
	/**
	 * Returns an iterator with the name of all the properties that 
	 * has been established.
	 */
	public static Iterator getKeys(){
		return properties.keySet().iterator();
	}
	
	/**
	 * Gets a String property
	 * @param key
	 * Property name
	 * @return
	 */
	public static String getStringProperty(String key){
		Object obj = getProperty(key);
		if (obj == null){
			return null;
		}
		return (String)obj;
	}
	
	/**
	 * Gets a int property
	 * @param key
	 * Property name
	 * @return
	 * The int property or -1
	 */
	public static int getIntPropertyProperty(String key){
		Object obj = getProperty(key);
		if (obj == null){
			return -1;
		}
		if (obj instanceof Integer){
			return ((Integer)obj).intValue();
		}return -1;
	}
	
	/**
	 * Gets a boolean property. If the property doesn't exist
	 * it returns false.
	 * @param key
	 * Property name
	 * @return
	 * The boolean property or false
	 */
	public static boolean getBooleanProperty(String key){
		Object obj = getProperty(key);
		if (obj == null){
			return false;
		}
		if (obj instanceof Boolean){
			return ((Boolean)obj).booleanValue();
		}return false;
	}
	
	/**
	 * Gets a property
	 * @param key
	 * Property name
	 * @return
	 */
	public static Object getProperty(String key){
		return properties.get(key);
	}
	
	/**
	 * Sets a property
	 * @param key
	 * @param value
	 */
	public static void setProperty(String key, Object value){
		properties.put(key, value);
	}
	
	
}
