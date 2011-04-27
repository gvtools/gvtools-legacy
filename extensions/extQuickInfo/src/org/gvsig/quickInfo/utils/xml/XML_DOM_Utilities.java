package org.gvsig.quickInfo.utils.xml;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class has some methods that are utilities related to manage XML data or files using the DOM (Document Object Model) API <br>
 *   of Java, according the JAXP (Java API for XML Processing), which other API's (JDOM, DOM, ...) are also according to. <br>
 * JAXP is a Java interface that provides a standard approach to Parsing XML documents. <br>
 * DOM generates in memory a tree with the XML information.
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class XML_DOM_Utilities {
    /**
     * Parses XML data stored in a String and generates a DOM (Document Object Model) document that contains XML data
     *
     * @param xmlData An string with XML content
     *
     * @return Document The generated XML document
     *
     * @throws SAXException org.xml.sax.SAXException
     * @throws IOException java.io.IOException
     * @throws ParserConfigurationException javax.xml.parsers.ParserConfigurationException
     */
    public static Document parse_XML_String_and_create_DOM(String xmlData) throws SAXException, IOException, ParserConfigurationException
    {
    	// Document builder factory instance. This builder allows create new documents
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Instance of a DOM document builder, this object will allow to parse a document
        DocumentBuilder DOM_document_Builder = factory.newDocumentBuilder();

        // Parses the input data and if it's correct returns a new document (Document) object with that data
        return DOM_document_Builder.parse(new ByteArrayInputStream(xmlData.getBytes()));
     }

    /**
     * Parses an XML file and generates a memory document with the tree data of that file
     *
     * @param fileXML Name of the XML file
     *
     * @return Document The generated XML document
     *
     * @throws SAXParseException org.xml.sax.SAXParseException
     * @throws SAXException org.xml.sax.SAXException
     * @throws ParserConfigurationException javax.xml.parsers.ParserConfigurationException
     * @throws IOException java.io.IOException
     */
    public static Document parse_XML_file_and_create_DOM(String fileXML) throws SAXParseException, SAXException, ParserConfigurationException, IOException
    {
       	// Document builder factory instance. This builder allows create new documents
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Instance of a DOM document builder, this object will allow to parse a document
        DocumentBuilder DOM_document_Builder = factory.newDocumentBuilder();

        // Parses the input data and if it's correct returns a new document (Document) object with that data
        return DOM_document_Builder.parse( new File(fileXML) );
    }

    /**
     * Writes a DOM document in a file with XML format
     *
     * @param doc The document to write
     * @param file The name of the output file
     *
     * @throws FileNotFoundException java.io.FileNotFoundException
     * @throws TransformerException javax.xml.transform.TransformerException
     */
    public static void write_DOM_into_an_XML_file(Document doc, String file) throws FileNotFoundException, TransformerException
    {
    	// An instance of a object transformer factory to create 'transformer' objects
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();

        // Holds a tree with the information of a document in the form of a DOM tree
        DOMSource source = new DOMSource(doc);

        /* Creates a StreamResult object associated to a file, transforms the document tree to XML and stores it in a file */
        // Creates the output file
        File newXML = new File(file);

        // Associates the file to a file output stream
        FileOutputStream os = new FileOutputStream(newXML);

        // Associates that stream as 'stream result' object
        StreamResult result = new StreamResult(os);

        // Makes the stream transformation from the source to the result
        transformer.transform(source, result);
    }

    /**
     * Transforms a memory document with XML format into another with HTML format according an XSL file, and stores it in a file
     *
     * @param doc The document to read
     * @param htmlFile The name of the output (HTML) file
     * @param xslFile The name of the XSL file which allows transform XML into HTML according its style
     *
     * @throws FileNotFoundException java.io.FileNotFoundException
     * @throws TransformerException javax.xml.transform.TransformerException
     */
    public static void write_DOM_into_an_HTML_file(Document doc, String htmlFile, String xslFile) throws FileNotFoundException, TransformerException
    {
    	// An instance of a object transformer factory
        TransformerFactory tFactory = TransformerFactory.newInstance();

        // Creates the output file
        File SalidaHTML = new File(htmlFile);

        // Associates the file to a file output stream
        FileOutputStream os = new FileOutputStream(SalidaHTML);

        // Creates a transformer object associated to the XSL file
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));

        // Holds a tree with the information of a document in the form of a DOM tree
        DOMSource sourceId = new DOMSource(doc);

        // Makes the transformation from the source in XML to the output in HML according the transformer in XSL
        transformer.transform(sourceId, new StreamResult(os));
    }

    /**
     * Transforms a memory document with XML format into an string according an XSL file, and stores it in a file
     *
     * @param doc The document to read
     * @param xslFile The name of the XSL file which allows transformate XML into String according its style
     *
     * @return String value of a DOM
     *
     * @throws FileNotFoundException java.io.FileNotFoundException
     * @throws TransformerException javax.xml.transform.TransformerException
     */
    public static String write_DOM_into_an_String(Document doc, String xslFile) throws FileNotFoundException, TransformerException
    {
    	// An instance of a object transformer factory
        TransformerFactory tFactory = TransformerFactory.newInstance();

        // Creates a transformer object associated to the XSL file
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));

        // Holds a tree with the information of a document in the form of a DOM tree
        DOMSource sourceId = new DOMSource(doc);

        // Makes the transformation from the source in XML to the output in stream according the transformer in XSL
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        transformer.transform(sourceId, new StreamResult(bAOS));

        // Returns the string value of the stream
        return bAOS.toString();
    }

    /**
     * Transforms a memory document with XML format into an string according an XSL
     *
     * @param doc The document to read
     * @param xsl An string with the XSL which allows transformate XML into String
     *
     * @return An String with the DOM transformated
     *
     * @throws FileNotFoundException java.io.FileNotFoundException
     * @throws TransformerException javax.xml.transform.TransformerException
     */
    public static String write_DOM_into_an_String_With_An_XSL_String(Document doc, String xsl) throws FileNotFoundException, TransformerException
    {
    	// An instance of a object transformer factory
        TransformerFactory tFactory = TransformerFactory.newInstance();

        // Creates a transformer object associated to the XSL file
        Transformer transformer = tFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xsl.getBytes())));

        // Holds a tree with the information of a document in the form of a DOM tree
        DOMSource sourceId = new DOMSource(doc);

        // Makes the transformation from the source in XML to the output in stream according the transformer in XSL
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        transformer.transform(sourceId, new StreamResult(bAOS));

        // Returns the string value of the stream
        return bAOS.toString();
    }

    /**
     * Transforms a memory document with XML format into an string according an XSL
     *
     * @param doc The document to read
     * @param iS_xsl An InputStream with the XSL which allows transformate XML into String
     *
     * @return An String with the DOM transformated
     *
     * @throws FileNotFoundException java.io.FileNotFoundException
     * @throws TransformerException javax.xml.transform.TransformerException
     */
    public static String write_DOM_into_an_String_With_An_XSL_InputStream(Document doc, InputStream iS_xsl) throws FileNotFoundException, TransformerException
    {
    	// An instance of a object transformer factory
        TransformerFactory tFactory = TransformerFactory.newInstance();

        // Creates a transformer object associated to the XSL file
        Transformer transformer = tFactory.newTransformer(new StreamSource(iS_xsl));

        // Holds a tree with the information of a document in the form of a DOM tree
        DOMSource sourceId = new DOMSource(doc);

        // Makes the transformation from the source in XML to the output in stream according the transformer in XSL
        ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        transformer.transform(sourceId, new StreamResult(bAOS));

        // Returns the string value of the stream
        return bAOS.toString();
    }

    /**
     * Creates a black (empty) DOM document
     *
     * @return The document
     * @throws ParserConfigurationException javax.xml.parsers.ParserConfigurationException
     */
    public static Document createBlankDocument() throws ParserConfigurationException {
        // Creates a instance of DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Gets the DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();

        // Creates a blank DOM Document
        return parser.newDocument();
    }

    /**
     * Adds an element to a document in memory
     *
     * @param doc Document where element will be added
     * @param elementName The name of the element
     *
     * @return org.w3c.dom.Element
     */
    public static Element addElementToDocument(Document doc, String elementName) {
        // Creates the element and adds it to the document
    	Element element = doc.createElement(elementName);
        doc.appendChild(element);

        return element;
    }

    /**
     * Adds an element to another of a document in memory <br>
     * (It's supposed that the parent is one element of the document)
     *
     * @param doc Document of the parent element
     * @param parent Parent element where a child element will be added
     * @param elementName The name of the child element
     *
     * @return org.w3c.dom.Element
     */
    public static Element addElementChildToElement(Document doc, Element parent, String elementName) {
        // Creates the element and adds it to the parent
    	Element element = doc.createElement(elementName);
        parent.appendChild(element);

        return element;
    }

    /**
     * Adds a commentary to an element of a document in memory <br>
     * (It's supposed that the element is one of the document)
     *
     * @param doc Document of the element where the commentary will be added
     * @param element The xml elemnet where the commentary will be added
     * @param commentary The commentary text
     *
     * @return org.w3c.dom.Comment
     */
    public static Comment addCommentary(Document doc, Element element, String commentary) {
        // Creates the commentary and adds it to the element
    	Comment comment = doc.createComment(commentary);
        element.appendChild(comment);

        return comment;
    }

    /**
     * Adds an attribute to an element of a document in memory <br>
     * (It's supposed that the element is one of the document)
     *
     * @param element The xml elemnet where the attibute will be added
     * @param attributeName The name of the attribute
     * @param attributeValue The value of the attribute
     */
    public static void setAttribute(Element element, String attributeName, String attributeValue) {
    	//Adds the atribute to the element
    	element.setAttribute(attributeName, attributeValue);
    }
}
