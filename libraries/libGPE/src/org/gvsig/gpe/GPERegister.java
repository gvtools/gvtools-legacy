package org.gvsig.gpe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.gvsig.gpe.exceptions.ParserCreationException;
import org.gvsig.gpe.exceptions.ParserNotRegisteredException;
import org.gvsig.gpe.exceptions.WriterHandlerCreationException;
import org.gvsig.gpe.exceptions.WriterHandlerNotRegisteredException;
import org.gvsig.gpe.parser.GPEParser;
import org.gvsig.gpe.writer.GPEWriterHandler;
import org.gvsig.gpe.writer.GPEWriterHandlerImplementor;
import org.gvsig.gpe.writer.IGPEWriterHandlerImplementor;

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
 * $Id: GPERegister.java 282 2007-12-20 11:55:56Z jpiera $
 * $Log$
 * Revision 1.16  2007/06/28 13:04:33  jorpiell
 * The Qname has been updated to the 1.5 JVM machine. The schema validation is made in the GPEWriterHandlerImplementor class
 *
 * Revision 1.15  2007/06/20 09:35:37  jorpiell
 * Add the javadoc comments
 *
 * Revision 1.14  2007/05/16 12:34:55  csanchez
 * GPEParser Prototipo final de lectura
 *
 * Revision 1.13  2007/05/09 06:54:07  jorpiell
 * Change the File by URI
 *
 * Revision 1.12  2007/05/08 12:57:14  jorpiell
 * Add the register exceptions
 *
 * Revision 1.11  2007/05/07 07:06:26  jorpiell
 * Add a constructor with the name and the description fields
 *
 * Revision 1.10  2007/04/19 11:50:20  csanchez
 * Actualizacion protoripo libGPE
 *
 * Revision 1.9  2007/04/19 07:23:20  jorpiell
 * Add the add methods to teh contenhandler and change the register mode
 *
 * Revision 1.8  2007/04/18 12:54:45  csanchez
 * Actualizacion protoripo libGPE
 *
 * Revision 1.7  2007/04/17 07:53:55  jorpiell
 * Before to start a new parsing process, the initialize method of the content handlers is throwed
 *
 * Revision 1.6  2007/04/17 06:26:54  jorpiell
 * Fixed one problem with a index
 *
 * Revision 1.5  2007/04/14 16:06:13  jorpiell
 * The writer handler has been updated
 *
 * Revision 1.4  2007/04/12 17:06:42  jorpiell
 * First GML writing tests
 *
 * Revision 1.3  2007/04/11 11:10:27  jorpiell
 * Cambiado el nombre de getDriver a GetParser
 *
 * Revision 1.2  2007/04/11 08:54:24  jorpiell
 * Añadidos algunos comentarios
 *
 * Revision 1.1  2007/04/11 08:52:55  jorpiell
 * Se puede registrar una clase por nombre
 *
 * Revision 1.1  2007/04/11 08:22:41  jorpiell
 * GPE clase para registrar los drivers
 *
 *
 */
/**
 * This class is used to register the GPE parsers. All the parsers must be
 * registered in this class before to be used for the consumer application
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 */
public class GPERegister {
	private static Hashtable parsers = new Hashtable();
	private static Hashtable writers = new Hashtable();
	private static GPEParser gpeParser = null;

	/**
	 * Adds a new GPE parser
	 * 
	 * @param name
	 *            Driver name. It must be written like FORMAT VERSION
	 * @param description
	 *            Driver description. Just a descriptive text
	 * @param clazz
	 *            The parser class
	 * @throws ParserNotRegisteredException
	 * @throws GPEParserRegisterException
	 */
	public static void addGpeParser(String name, String description, Class clazz)
			throws ParserNotRegisteredException {
		try {
			if (clazz != null) {
				GPEParser parser = (GPEParser) clazz.getConstructor(null)
						.newInstance(null);
				parsers.put(name, parser);
			}
		} catch (Exception e) {
			throw new ParserNotRegisteredException(clazz.getName());
		}
	}

	/**
	 * Adds a new GPE parser
	 * 
	 * @param clazz
	 *            The parser class
	 * @throws ParserNotRegisteredException
	 * @throws GPEParserRegisterException
	 */
	public static void addGpeParser(Class clazz)
			throws ParserNotRegisteredException {
		try {
			if (clazz != null) {
				GPEParser parser = (GPEParser) clazz.getConstructor(null)
						.newInstance(null);
				parsers.put(parser.getName(), parser);
			}
		} catch (Exception e) {
			throw new ParserNotRegisteredException(clazz.getName());
		}
	}

	/**
	 * It loads the parsers of a parsers file. The file is a properties file.
	 * Every line has the structure: Parser=Parser class
	 * 
	 * @param file
	 *            File that contains the parsers list
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ParserNotRegisteredException
	 */
	public static void addParsersFile(File file) throws FileNotFoundException,
			IOException {
		if (!file.exists()) {
			return;
		}
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		for (Enumeration e = properties.keys(); e.hasMoreElements();) {
			String key = e.nextElement().toString();
			Class clazz;
			try {
				clazz = Class.forName(properties.getProperty(key).toString());
				addGpeParser(clazz);
			} catch (ClassNotFoundException ex) {
				// Next class
			} catch (ParserNotRegisteredException ex) {
				// Next class
			}
		}
	}

	/**
	 * It loads the writers of a writers file. The file is a properties file.
	 * Every line has the structure: Writer=Parser class
	 * 
	 * @param file
	 *            File that contains the writers list
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void addWritersFile(File file) throws FileNotFoundException,
			IOException {
		if (!file.exists()) {
			return;
		}
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		for (Enumeration e = properties.keys(); e.hasMoreElements();) {
			String key = e.nextElement().toString();
			Class clazz;
			try {
				clazz = Class.forName(properties.getProperty(key).toString());
				addGpeWriterHandler(clazz);
			} catch (ClassNotFoundException ex) {
				// Next class
			} catch (WriterHandlerNotRegisteredException ex) {
				// Next class
			}
		}
	}

	/**
	 * @return all the registered parsers
	 */
	public static GPEParser[] getAllParsers() {
		GPEParser[] auxParsers = new GPEParser[parsers.size()];
		Iterator it = parsers.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			String key = (String) it.next();
			auxParsers[i] = (GPEParser) parsers.get(key);
			i++;
		}
		return auxParsers;
	}

	/**
	 * Adds a new GPEWriterHandlerImplementor
	 * 
	 * @param name
	 *            Driver name. It must be written like FORMAT VERSION
	 * @param description
	 *            Driver description. Just a descriptive text
	 * @param clazz
	 *            The parser class
	 * @throws WriterHandlerNotRegisteredException
	 * @throws GPEWriterHandlerRegisterException
	 */
	public static void addGpeWriterHandler(String name, String description,
			Class clazz) throws WriterHandlerNotRegisteredException {
		try {
			if (clazz != null) {
				GPEWriterHandlerImplementor writerImplementor = (GPEWriterHandlerImplementor) clazz
						.getConstructor(null).newInstance(null);
				writers.put(name, writerImplementor);
			}
		} catch (Exception e) {
			throw new WriterHandlerNotRegisteredException(clazz.getName());
		}
	}

	/**
	 * Adds a new GPEWriterHandlerImplementor
	 * 
	 * @param clazz
	 *            The parser class
	 * @throws WriterHandlerNotRegisteredException
	 * @throws GPEWriterHandlerRegisterException
	 */
	public static void addGpeWriterHandler(Class clazz)
			throws WriterHandlerNotRegisteredException {
		try {
			if (clazz != null) {
				GPEWriterHandlerImplementor writerImplementor = (GPEWriterHandlerImplementor) clazz
						.getConstructor(null).newInstance(null);
				writers.put(writerImplementor.getName(), writerImplementor);
			}
		} catch (Exception e) {
			throw new WriterHandlerNotRegisteredException(clazz.getName());
		}
	}

	/**
	 * Create a new parser from a name
	 * 
	 * @param name
	 *            GPEParser name
	 * @param contenHandler
	 *            Application contenHandler usett to throw the parsing events
	 * @param errorHandler
	 *            Application errror handler used to put errors and warnings
	 * @throws ParserCreationException
	 * @throws GPEParserCreationException
	 */
	public static GPEParser createParser(String name)
			throws ParserCreationException {
		if ((gpeParser != null) && (gpeParser.getName() == name))
			return gpeParser;
		Object parser = parsers.get(name);
		try {
			if (parser != null) {
				return (GPEParser) parser.getClass().getConstructor(null)
						.newInstance(null);
			} else {
				Exception e = new ParserNotRegisteredException(name);
				throw new ParserCreationException(e);
			}
		} catch (Exception e) {
			throw new ParserCreationException(e);
		}

	}

	/**
	 * Create a new parser from a name
	 * 
	 * @param name
	 *            GPEParser name
	 * @param contenHandler
	 *            Application contenHandler usett to throw the parsing events
	 * @param errorHandler
	 *            Application errror handler used to put errors and warnings
	 * @throws ParserCreationException
	 * @throws GPEParserCreationException
	 */
	public static GPEParser createParser() throws ParserCreationException {
		try {
			if (gpeParser != null) {
				return (GPEParser) gpeParser.getClass().getConstructor(null)
						.newInstance(null);
			} else {
				Throwable e = new Exception("Fail Registering Parser");
				throw new ParserCreationException(e);
			}
		} catch (Exception e) {
			throw new ParserCreationException(e);
		}
	}

	/**
	 * Gets the parser that can open the file (if it exists)
	 * 
	 * @param uri
	 *            File to open
	 * @return Null if the driver doesn't exist
	 * @throws GPEParserCreationException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public static GPEParser createParser(URI uri)
			throws ParserCreationException {
		Iterator keys = parsers.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			GPEParser parser = (GPEParser) parsers.get(key);
			if (parser.accept(uri)) {
				return createParser(key);
			}
		}
		return null;
	}

	/**
	 * Create a new content writer from a name
	 * 
	 * @param name
	 *            GPEWriterHandler name GPEParser name
	 * @param contenHandler
	 *            Application contenHandler usett to throw the parsing events
	 * @param errorHandler
	 *            Application errror handler used to put errors and warnings
	 * @throws GPEWriterHandlerCreationException
	 */
	public static GPEWriterHandler createWriter(String name)
			throws WriterHandlerCreationException {
		Object writer = writers.get(name);
		try {
			if (writer != null) {
				GPEWriterHandlerImplementor writerImplementor = (GPEWriterHandlerImplementor) writer
						.getClass().getConstructor(null).newInstance(null);
				return new GPEWriterHandler(writerImplementor);
			} else {
				Exception e = new WriterHandlerNotRegisteredException(name);
				throw new WriterHandlerCreationException(e);
			}
		} catch (Exception e) {
			throw new WriterHandlerCreationException(e);
		}
	}

	/**
	 * Gets all the writers that can
	 * 
	 * @param format
	 * @return
	 */
	public static ArrayList getWriterHandlerByFormat(String format) {
		Iterator it = writers.keySet().iterator();
		ArrayList possibleWriters = new ArrayList();
		while (it.hasNext()) {
			String key = (String) it.next();
			IGPEWriterHandlerImplementor implementor = (IGPEWriterHandlerImplementor) writers
					.get(key);
			String formats = implementor.getFormat();
			if (formats.toLowerCase().compareTo(format.toLowerCase()) == 0) {
				possibleWriters.add(new GPEWriterHandler(implementor));
			}
		}
		return possibleWriters;
	}

	/**
	 * Return true if exists a driver that can open the file
	 * 
	 * @param uri
	 *            File to open
	 * @return true if the driver exists
	 */
	public static boolean accept(URI uri) {
		Iterator keys = parsers.keySet().iterator();
		while (keys.hasNext()) {
			GPEParser parser = (GPEParser) parsers.get(keys.next());
			if (parser.accept(uri)) {
				gpeParser = parser;
				return true;
			}
		}
		return false;
	}
}
