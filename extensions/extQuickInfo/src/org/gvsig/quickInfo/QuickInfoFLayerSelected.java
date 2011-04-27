package org.gvsig.quickInfo;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gvsig.quickInfo.utils.xml.XML_DOM_Utilities;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.layers.FLayer;

/**
 * <p>Stores the information selected of a layer's point.</p>
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class QuickInfoFLayerSelected {
	private FLayer layer;
	private LinkedHashMap layerFields;
	private LinkedHashMap calculatedLayerFields;
	private Vector geometryIDs;
	private final String xslFile = "xsl/quick_information.xsl";
	private boolean anyLayerFieldAdded;
	private boolean anyCalculatedLayerFieldAdded;

	/**
	 * <p>Creates a new <code>QuickInfoFLayerSelected</code>.</p>
	 * 
	 * @param layer the layer which the information will be stored
	 */
	public QuickInfoFLayerSelected(FLayer layer) {
		super();
		
		layerFields = new LinkedHashMap();
		calculatedLayerFields = new LinkedHashMap();
		geometryIDs = new Vector(0, 1);
		anyLayerFieldAdded = false;
		anyCalculatedLayerFieldAdded = false;
		
		this.layer = layer;
	}
	
	/**
	 * <p>Returns a reference to the layer that the other information makes reference.</p>
	 * 
	 * @return reference to the layer
	 */
	public FLayer getLayer() {
		return layer;
	}
	
	/**
	 * <p>Returns a reference to the inner map that stores the layer fields: each key is a layer selected,
	 *  and for each one there is a vector that stores that field value for different geometries.</p>
	 * 
	 * @return reference to the inner map that stores the layer fields
	 */
	public HashMap getLayerFields() {
		return layerFields;
	}
	
	/**
	 * <p>Returns a reference to the inner map that stores the calculated layer fields: each key is a layer selected,
	 *  and for each one there is a vector that stores that field value for different geometries.</p>
	 * 
	 * @return reference to the inner map that stores the calculated layer fields
	 */
	public HashMap getCalculatedLayerFields() {
		return calculatedLayerFields;
	}

	/**
	 * <p>Returns a reference to the inner vector that stores the geometry IDs.</p>
	 * 
	 * @return reference to the inner vector that stores the geometry IDs
	 */
	public Vector getGeometryIDs() {
		return geometryIDs;
	}

	/**
	 * <p>Creates a new XML document with the information stored in this object. The document will have also
	 *  information about the way of representing that data in HTML.</p> 
	 * 
	 * @return document with the information stored in this object
	 */
	public Document getToolTipStyledDocument() {
		try {
			if ((geometryIDs.size() == 0) && (layerFields.size() == 0) && (calculatedLayerFields.size() == 0))
				return null;
			
			if ((!isAnyLayerFieldAdded()) && (!isAnyCalculatedLayerFieldsAdded()))
				return null;
			
		    // Create instance of DocumentBuilderFactory
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		    // Get the DocumentBuilder
		    DocumentBuilder docBuilder = factory.newDocumentBuilder();

			// Create blank DOM Document
			Document doc = docBuilder.newDocument();

			// Create the root element
			Element root = doc.createElement("root");

			// All it to the xml tree
			doc.appendChild(root);

			// Create a comment
			Comment comment = doc.createComment("Document of a Quick Info Tool Tip Text .");

			// Add in the root element
			root.appendChild(comment);
			
			// Set the background color
			root.setAttribute("bgColor", "#F6CEE3");
			
			// Create the child that represents the name of the layer where the fields are
			Element childElement = doc.createElement("layer");
			childElement.setAttribute("name", layer.getName());
			childElement.setAttribute("fontColor", "#0404B4");
			childElement.setAttribute("fontStyle", "BOLD");
			childElement.setAttribute("fontSyze", "3");
			childElement.setAttribute("fontType", "Arial");
			root.appendChild(childElement);

			// Create childs
			if (geometryIDs.size() > 1) {
				root.setAttribute("numChildren", Integer.toString(geometryIDs.size()));
				root.setAttribute("numGeom", Integer.toString(geometryIDs.size()));

				for (int i = 0; i < geometryIDs.size(); i++) {
					childElement = doc.createElement("geometry");
					try {
						childElement.setAttribute("name", geometryIDs.get(i).toString());
					}
					catch(ArrayIndexOutOfBoundsException e) {
						childElement.setAttribute("name", "");
					}
					childElement.setAttribute("fontColor", "#0404B4");
					childElement.setAttribute("fontStyle", "BOLD_AND_ITALIC");
					childElement.setAttribute("fontSyze", "3");
					childElement.setAttribute("fontType", "Arial");
					childElement.setAttribute("numChildren", Integer.toString(layerFields.size() + calculatedLayerFields.size()));
					
					if ( !((GeometryIDInfo)geometryIDs.get(i)).hasInfo() ) {
						Element grandChildrenElement = doc.createElement("geomInfo");
						grandChildrenElement.setAttribute("name", "");
						grandChildrenElement.setAttribute("value", PluginServices.getText(this, "Geometry_without_info"));
						grandChildrenElement.setAttribute("n_fontColor", "#0000FF");
						grandChildrenElement.setAttribute("n_fontSyze", "3");
						grandChildrenElement.setAttribute("n_fontStyle", "BOLD"); 
						grandChildrenElement.setAttribute("n_fontType", "Times");
						grandChildrenElement.setAttribute("v_fontColor", "#000000");
						grandChildrenElement.setAttribute("v_fontSyze", "3");
						grandChildrenElement.setAttribute("v_fontStyle", "BOLD");
						grandChildrenElement.setAttribute("v_fontType", "Arial");
						grandChildrenElement.setAttribute("numChildren", Integer.toString(0));
						childElement.appendChild(grandChildrenElement);
					}	
					else {
						if ((layerFields.size() > 0) || (calculatedLayerFields.size() > 0)) {
							getChildren(childElement, doc, i);
						}
					}

					root.appendChild(childElement);
				}
			}
			else  {
				root.setAttribute("numChildren", Integer.toString(layerFields.size() + calculatedLayerFields.size()));
				root.setAttribute("numGeom", "1");

				getChildren(root, doc, -1);
			}
				
			return doc;
		} catch(Exception e) {
			NotificationManager.showMessageError(PluginServices.getText(null, "Failed_creating_XML_document"), e);
		}
		
		return null;
	}

	/**
	 * <p>Creates and adds XML children nodes with information of the fields stored in this object, and
	 *  prepared to be formatted according a style.</p>
	 * <p>(It's supposed that number of fields and calculated fields stored is correct).</p>
	 * 
	 * @param parent parent node
	 * @param doc document for create new XML nodes
	 * @param index -1 if there is only one geometry, >= 0 if there are more than one. In this second case,
	 *  represents also the index where data is stored in the arrays.
	 */
	private void getChildren(Element parent, Document doc, int index) {
		if (index == -1) { // -1 -> All fields and calculated fields
			// One child per field and calculated field (there is only one value per child)
			
			// Layer fields
			Set lFields = layerFields.keySet();
			Iterator iterator = lFields.iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();

				Element element = doc.createElement("geomInfo");
				element.setAttribute("name", key);
				try {
					element.setAttribute("value", (String) ((Vector) layerFields.get(key)).get(0));
				}
				catch(ArrayIndexOutOfBoundsException e) {
					element.setAttribute("value", "");
				}
				element.setAttribute("n_fontColor", "#0000FF");
				element.setAttribute("n_fontSyze", "3");
				element.setAttribute("n_fontStyle", "BOLD"); 
				element.setAttribute("n_fontType", "Times");
				element.setAttribute("v_fontColor", "#000000");
				element.setAttribute("v_fontSyze", "3");
				element.setAttribute("v_fontStyle", "BOLD");
				element.setAttribute("v_fontType", "Arial");
				element.setAttribute("numChildren", Integer.toString(0));
				parent.appendChild(element);
			}
			
			// Calculated fields
			Set cFields = calculatedLayerFields.keySet();
			iterator = cFields.iterator();
			
			while (iterator.hasNext()) {
				String key = (String) iterator.next();

				Element element = doc.createElement("geomInfo");
				element.setAttribute("name", key);
				try {
					element.setAttribute("value", (String) ((Vector) calculatedLayerFields.get(key)).get(0));
				}
				catch (ArrayIndexOutOfBoundsException e) {
					element.setAttribute("value", "");
				}
				element.setAttribute("n_fontColor", "#0000FF");
				element.setAttribute("n_fontSyze", "3");
				element.setAttribute("n_fontStyle", "BOLD"); 
				element.setAttribute("n_fontType", "Times");
				element.setAttribute("v_fontColor", "#000000");
				element.setAttribute("v_fontSyze", "3");
				element.setAttribute("v_fontStyle", "BOLD");
				element.setAttribute("v_fontType", "Arial");
				element.setAttribute("numChildren", Integer.toString(0));
				parent.appendChild(element);
			}			
		}
		else if (index >= 0) { // >= 0 -> All fields and calculated fields are grouped by a geometry child (that's the parent)
			// Parent will be a geometry that has some fields and calculated fields
			
			// Layer fields
			Set lFields = layerFields.keySet();
			Iterator iterator = lFields.iterator();
			
			while (iterator.hasNext()) {
				String key = (String) iterator.next();

				Element element = doc.createElement("geomInfo");
				element.setAttribute("name", key);
				try {
					element.setAttribute("value", (String) ((Vector) layerFields.get(key)).get(index));
				}
				catch (ArrayIndexOutOfBoundsException e) {
					element.setAttribute("value", "");
				}
				element.setAttribute("n_fontColor", "#0000FF");
				element.setAttribute("n_fontSyze", "3");
				element.setAttribute("n_fontStyle", "BOLD"); 
				element.setAttribute("n_fontType", "Times");
				element.setAttribute("v_fontColor", "#000000");
				element.setAttribute("v_fontSyze", "3");
				element.setAttribute("v_fontStyle", "BOLD");
				element.setAttribute("v_fontType", "Arial");
				element.setAttribute("numChildren", Integer.toString(0));
				parent.appendChild(element);
			}
			
			// Calculated fields
			Set cFields = calculatedLayerFields.keySet();
			iterator = cFields.iterator();
			
			while (iterator.hasNext()) {
				String key = (String) iterator.next();

				Element element = doc.createElement("geomInfo");
				element.setAttribute("name", key);
				try {
					element.setAttribute("value", (String) ((Vector) calculatedLayerFields.get(key)).get(index));
				}
				catch (ArrayIndexOutOfBoundsException e) {
					element.setAttribute("value", "");
				}
				element.setAttribute("n_fontColor", "#0000FF");
				element.setAttribute("n_fontSyze", "3");
				element.setAttribute("n_fontStyle", "BOLD"); 
				element.setAttribute("n_fontType", "Times");
				element.setAttribute("v_fontColor", "#000000");
				element.setAttribute("v_fontSyze", "3");
				element.setAttribute("v_fontStyle", "BOLD");
				element.setAttribute("v_fontType", "Arial");
				element.setAttribute("numChildren", Integer.toString(0));
				parent.appendChild(element);
			}
		}
	}

	/**
	 * <p>Gets an <code>String</code> that represents the information of a layer point stored in this object, and formatted
	 *  in HTML.</p>
	 * 
	 * @return an <code>String</code> that represents the information of a layer point stored in this object
	 */
	public String getToolTipText() {
		try {
			Document document = getToolTipStyledDocument();
			
			if (document == null)
				return null;
			else
				return XML_DOM_Utilities.write_DOM_into_an_String(document, QuickInfoFLayerSelected.class.getClassLoader().getResource(xslFile).toString());
		}
		catch(Exception e) {
			NotificationManager.showMessageError(PluginServices.getText(null, "Failed_transforming_XML_to_String"), e);
		}
		
		return null;
	}

	/**
	 * <p>Determines if any layer field has been added.</p>
	 * 
	 * @return a boolean value
	 */	
	private boolean isAnyLayerFieldAdded() {
		return anyLayerFieldAdded;
	}

	/**
	 * <p>Determines if any calculated layer field has been added.</p>
	 * 
	 * @return a boolean value
	 */	
	private boolean isAnyCalculatedLayerFieldsAdded() {
		return anyCalculatedLayerFieldAdded;
	}

	/**
	 * <p>Sets if any layer field has been added.</p>
	 * 
	 * @param b a boolean value
	 */
	public void setAnyLayerFieldAdded(boolean b) {
		anyLayerFieldAdded = b;
	}

	/**
	 * <p>Sets if any calculated layer field has been added.</p>
	 * 
	 * @param b a boolean value
	 */
	public void setAnyCalculatedLayerFieldsAdded(boolean b) {
		anyCalculatedLayerFieldAdded = b;
	}

	/**
	 * <p>Removes all values added and resets the inner flags <code>anyLayerFieldAdded</code> and
	 *  <code>anyCalculatedLayerFieldAdded</code>.</p>
	 */
	public void clearValues() {
		Set keys = layerFields.keySet();
		Iterator it = keys.iterator();
		
		while(it.hasNext()) {
			((Vector)layerFields.get(it.next())).clear();
		}

		anyLayerFieldAdded = false;

		// Calculated fields
		keys = calculatedLayerFields.keySet();
		it = keys.iterator();
		
		while(it.hasNext()) {
			((Vector)calculatedLayerFields.get(it.next())).clear();
		}
		
		getGeometryIDs().clear();

		anyCalculatedLayerFieldAdded = false;
	}
}
