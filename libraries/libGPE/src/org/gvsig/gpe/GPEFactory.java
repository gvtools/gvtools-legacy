package org.gvsig.gpe;

import java.net.URI;
import java.util.Iterator;

import org.gvsig.gpe.exceptions.ParserCreationException;
import org.gvsig.gpe.exceptions.ParserFileNotSupportedException;
import org.gvsig.gpe.exceptions.ParserMimetypeNotSupportedException;
import org.gvsig.gpe.exceptions.ParserNameNotFoundException;
import org.gvsig.gpe.exceptions.ParserNotRegisteredException;
import org.gvsig.gpe.exceptions.WriterHandlerCreationException;
import org.gvsig.gpe.exceptions.WriterHandlerMimeTypeNotSupportedException;
import org.gvsig.gpe.exceptions.WriterHandlerNotRegisteredException;
import org.gvsig.gpe.parser.GPEParser;
import org.gvsig.gpe.writer.GPEWriterHandler;
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
 * API entry point to create parsers and writers based on MIME-Type identification.
 * <p>
 * This factory uses a SPI (Service Provider Interface) mechanism to look up for {@link GPEParser}
 * and {@link IGPEWriterHandlerImplementor} implementations that can deal with a requested
 * MIME-Type.
 * </p>
 * <h2>Implementation Lookup</h2>
 * <p>
 * The SPI mechanism keeps parser implementations decoupled from client applications allowing the
 * transparent interchange of implementation, thus defining a format plugin system.
 * </p>
 * <p>
 * Parser and Writer implementations registers themselves lazily by providing a small configuration
 * file bundled together with the rest of the implementation resources. This configuration file
 * consist of a text file at a specific location and with an specific file name, this factory look
 * up mechanism will search for in order to find out the implementation class names.
 * </p>
 * <p>
 * To register a GPEParser implememntation, a file named <code>org.gvsig.gpe.parser.GPEParser</code>
 * shall exist in the class path (hence the ability to have multiple files equally namded budled in
 * different jar files), in the implementation's <code>META-INF/services</code> folder.
 * </p>
 * <p>
 * To register a Writer handler implementation, the same approach shall be followed with a file
 * named <code>META-INF/services/org.gvsig.gpe.writer.IGPEWriterHandlerImplementor</code>
 * </p>
 * <p>
 * The content of both files for a given implementation consists of full qualified class names, one
 * per line. For example, an hypotetical <code>MyFormatParser</code> in the package
 * <code>org.mycompany.gpe</code> and bundled in a jar file called <code>myformat.jar</code>
 * shall provide the following resources:
 * 
 * <pre>
 * <code>
 * $jar tvf myformat.jar
 * META-INF/services/org.gvsig.gpe.parser.GPEParser
 * org/mycompany/gpe/MyFormatParser.class
 * </code>
 * </pre>
 * 
 * And the content of the file <code>META-INF/services/org.gvsig.gpe.parser.GPEParser</code> shall
 * be a single line of text with the <code>org.mycompany.gpe.MyFormatParser</code> class name.
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 */
public class GPEFactory {

	/**
	 * Returns an iterator over instances of the registered parsers.
	 * <p>
	 * NOTE: since GPEParser is an abstract and statefull class rather than an interface, this
	 * method shall stay private in order to ensure we return a new instance for the required format
	 * every time.
	 * </p>
	 * 
	 * @return all the registered parsers
	 */
	private static Iterator availableParsers() {
		Iterator providers = sun.misc.Service.providers(GPEParser.class);
		return providers;
	}

	/**
	 * Returns an iterator over instances of the registered writers.
	 * <p>
	 * NOTE: since GPEWriter is an abstract and statefull class rather than an interface, this
	 * method shall stay private in order to ensure we return a new instance for the required format
	 * every time.
	 * </p>
	 * 
	 * @return all the registered writers
	 */
	private static Iterator availableWriters() {
		Iterator providers = sun.misc.Service.providers(IGPEWriterHandlerImplementor.class);
		return providers;
	}

	/**
	 * Create a new parser from a class name. This method can be used 
	 * if the class name is known but, the common method to get a parser
	 * is using the mime type.     * 
	 * @param prefferredImplClassName
	 * The name of the class that implements {@link GPEParser}
	 * @return
	 * A parser for a concrete format
	 * @throws ParserCreationException
	 * If it is not possible to create a parser
	 */
	public static GPEParser createParserByClass(String prefferredImplClassName) throws ParserCreationException {
		Class prefferredImplClass = null;
		try {
			prefferredImplClass = Class.forName(prefferredImplClassName);
		} catch (ClassNotFoundException e) {
			throw new ParserCreationException(e);
		}

		Iterator providers = availableParsers();
		while (providers.hasNext()) {
			GPEParser next = (GPEParser) providers.next();
			if (prefferredImplClass.isInstance(next)) {
				return next;
			}
		}
		throw new ParserNotRegisteredException(prefferredImplClassName) ;
	}

	/**
	 * Create a new parser from a parser name. This method can be used 
	 * if the parser name is known but, the common method to get a parser
	 * is using the mime type.
	 * @param name 
	 * The name of the parser, that is returned by the {@link GPEParser#getName}
	 * method    
	 * @return
	 * A parser for a concrete format
	 * @throws ParserCreationException
	 * If it is not possible to create a parser 
	 */  
	public static GPEParser createParser(String name) throws ParserCreationException {
		Iterator providers = availableParsers();
		while (providers.hasNext()) {
			GPEParser parser = (GPEParser) providers.next();
			if (parser.getName().compareTo(name) == 0) {
				return parser;
			}
		}
		throw new ParserNameNotFoundException(name);
	}

	/**
	 * Create a new parser from a {@link URI}. Each parser has a method
	 * named {@link GPEParser#accept(URI)} that return true if the
	 * parser can be open the file.
	 * <p>
	 *	This method retrieve all the registered parsers and invoke the
	 * <code>accept</code> method. The first parser that retuns true 
	 * is returned. It means that if there are more that one parser
	 * to open a concrete file the first parser found will be returned. 
	 * </p>
	 * @param uri
	 * The place where the file is located
	 * @return
	 * A parser for a concrete format
	 * @throws ParserCreationException
	 * If it is not possible to create a parser
	 */
	public static GPEParser createParser(URI uri) throws ParserCreationException {
		Iterator providers = availableParsers();
		while (providers.hasNext()) {
			GPEParser parser = (GPEParser) providers.next();
			if (parser.accept(uri)) {
				return parser;
			}
		}
		throw new ParserFileNotSupportedException(uri);
	}

	/**
	 * Create a new parser from a mime type. Each parser has a method
	 * named {@link GPEParser#getFormat()} that returns the mimetype
	 * that the parser can read. One parser only supports one mimetype.
	 * <p>
	 * This method retrieve all the parsers and returns the first
	 * parser that is able to read the mimetype. If there are more
	 * parsers that can open the file will not be used.
	 * </p>
	 * @param mimeType
	 * The mimetype of the file to open
	 * @return
	 * A parser that can parse the mimetype.
	 * @throws ParserCreationException
	 * If it is not possible to create a parser
	 * @see 
	 * <a href="http://www.iana.org/assignments/media-types/">http://www.iana.org/assignments/media-types/</a>
	 */
	public static GPEParser createParserByMimeType(String mimeType) throws ParserCreationException {
		Iterator providers = availableParsers();
		while (providers.hasNext()) {
			GPEParser parser = (GPEParser) providers.next();
			if (parser.getFormat().equals(mimeType)) {
				return parser;
			}
		}
		throw new ParserMimetypeNotSupportedException(mimeType);
	}

	/**
	 * Create a new writer from a writer name. This method can be used 
	 * if the writer name is known but, the common method to get a writer
	 * is using the mime type.
	 * @param name 
	 * The name of the writer, that is returned by the {@link GPEWriterHandler#getName}
	 * method    
	 * @return
	 * A writer for a concrete format 
	 * @throws WriterHandlerCreationException
	 * If it is not possible to create a writer
	 */     
	public static GPEWriterHandler createWriter(String name)
		throws WriterHandlerCreationException {
		Iterator providers = availableWriters();
		while (providers.hasNext()) {
			IGPEWriterHandlerImplementor implementor = (IGPEWriterHandlerImplementor) providers
			.next();
			if (implementor.getName().compareTo(name) == 0) {
				return new GPEWriterHandler(implementor);
			}
		}
		return null;
	}

	/**
	 * Create a new writer from a class name. This method can be used 
	 * if the class name is known but, the common method to get a writer
	 * is using the mime type.     * 
	 * @param prefferredImplClassName
	 * The name of the class that implements {@link GPEWriterHandler}
	 * @return
	 * A writer for a concrete format.
	 * @throws WriterHandlerCreationException
	 * If it is not possible to create a writer
	 */
	public static GPEWriterHandler createWriterByClass(String prefferredImplClassName) 
		throws WriterHandlerCreationException {
		Class prefferredImplClass = null;
		try {
			prefferredImplClass = Class.forName(prefferredImplClassName);
		} catch (ClassNotFoundException e) {
			throw new WriterHandlerCreationException(e);
		}

		Iterator providers = availableWriters();
		while (providers.hasNext()) {
			IGPEWriterHandlerImplementor next = (IGPEWriterHandlerImplementor) providers.next();
			if (prefferredImplClass.isInstance(next)) {
				return new GPEWriterHandler(next);
			}
		}
		throw new WriterHandlerNotRegisteredException(prefferredImplClassName);
	}

	/**
	 * Create a new writer from a mime type. Each writer has a method
	 * named {@link GPEWriterHandler#getFormat()} that returns the mimetype
	 * that the writer can write. One writer only supports one mimetype.
	 * <p>
	 * This method retrieve all the writers and returns the first
	 * one that is able to write the mimetype. If there are more
	 * writer that can write the format will not be used.
	 * </p>
	 * @param mimeType
	 * The mimetype of the file to write
	 * @return
	 * A writer that can write the mimetype.
	 * @throws WriterHandlerCreationException
	 * If it is not possible to create a writer
	 * @see 
	 * <a href="http://www.iana.org/assignments/media-types/">http://www.iana.org/assignments/media-types/</a>
	 */
	public static GPEWriterHandler createWriterByMimeType(String mimeType)
		throws WriterHandlerCreationException {
		Iterator providers = availableWriters();
		while (providers.hasNext()) {
			IGPEWriterHandlerImplementor implementor = (IGPEWriterHandlerImplementor) providers
			.next();
			if (implementor.getFormat().equals(mimeType)) {
				return new GPEWriterHandler(implementor);
			}
		}
		throw new WriterHandlerMimeTypeNotSupportedException(mimeType);
	}

	/**
	 * Return <code>true</code> if there is a parser that is able to 
	 * open the file or <code>false</code> instead.
	 * @param uri
	 * The file to open
	 * @return
	 * <code>true</code> if there is a parser that is able to open the file
	 */
	public static boolean accept(URI uri) {
		Iterator providers = availableParsers();
		while (providers.hasNext()) {
			GPEParser parser = (GPEParser) providers.next();
			if (parser.accept(uri)) {
				return true;
			}
		}
		return false;
	}
}
