package com.iver.cit.gvsig.fmap.layers;

import java.util.Vector;

import org.gvsig.remoteClient.gml.schemas.XMLElement;
import org.gvsig.remoteClient.gml.types.GMLGeometryType;
import org.gvsig.remoteClient.gml.types.IXMLType;
import org.gvsig.remoteClient.gml.types.XMLComplexType;
import org.gvsig.remoteClient.utils.BoundaryBox;

/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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
 * $Id: WFSLayerNode.java 21917 2008-06-30 06:50:42Z ppiqueras $
 * $Log$
 * Revision 1.6.2.4  2007-01-25 16:11:15  jorpiell
 * Se han cambiado los imports que hac�an referencia a remoteServices. Esto es as�, porque se han renombrado las clases del driver de GML
 *
 * Revision 1.6.2.3  2006/11/28 09:24:45  jorpiell
 * Si una feature no tiene tipo, se le tiene que informar al usuario de ello
 *
 * Revision 1.6.2.2  2006/11/17 11:28:45  ppiqueras
 * Corregidos bugs y añadida nueva funcionalidad.
 *
 * Revision 1.10  2006/11/02 11:13:03  jorpiell
 * Se ha capturado una nullPointerException al calcular la geomteria
 *
 * Revision 1.9  2006/11/01 17:29:07  jorpiell
 * Se ha elimiado el nodo virtual de la raiz. Adem�s ya se cargan los valores de un campo complejo en la pesta�a del filtro
 *
 * Revision 1.8  2006/10/23 07:37:04  jorpiell
 * Ya funciona el filterEncoding
 *
 * Revision 1.7  2006/10/10 12:55:06  jorpiell
 * Se ha a�adido el soporte de features complejas
 *
 * Revision 1.6  2006/06/21 12:35:45  jorpiell
 * Se ha a�adido la ventana de propiedades. Esto implica a�adir listeners por todos los paneles. Adem�s no se muestra la geomatr�a en la lista de atributos y se muestran �nicamnete los que se van a descargar
 *
 * Revision 1.5  2006/06/15 07:50:58  jorpiell
 * A�adida la funcionalidad de reproyectar y hechos algunos cambios en la interfaz
 *
 * Revision 1.4  2006/05/25 10:31:55  jorpiell
 * Se ha renombrado la clase WFSFields por WFSAttributes porque era algo confusa
 *
 * Revision 1.3  2006/05/23 08:08:17  jorpiell
 * Eliminados los atributos innecesarios
 *
 * Revision 1.2  2006/04/20 16:38:24  jorpiell
 * Ahora mismo ya se puede hacer un getCapabilities y un getDescribeType de la capa seleccionada para ver los atributos a dibujar. Queda implementar el panel de opciones y hacer el getFeature().
 *
 * Revision 1.1  2006/04/19 12:50:16  jorpiell
 * Primer commit de la aplicaci�n. Se puede hacer un getCapabilities y ver el mensaje de vienvenida del servidor
 *
 *
 */
/**
 * This class represents one node of the layer tree of a
 * common WFS service.
 * @author Jorge Piera Llodr� (piera_jor@gva.es)
 */
public class WFSLayerNode {
	/**
	 * @return the nameSpace
	 */
	public String getNameSpace() {
		return nameSpace;
	}
	/**
	 * @param nameSpace the nameSpace to set
	 */
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	private String	name = null;
	private String nameSpace = null;
	private String	title = null;
	private String	_abstract = null;
	private Vector fields = new Vector();
	private Vector selectedFields = new Vector();
	private String filter = null;
	private GMLGeometryType geometry = null;
	private Vector srs = new Vector();
	private BoundaryBox latLonBbox = null;
	private String userName = "";

	
	/**
	 * @return Returns the fields.
	 */
	public Vector getFields() {
		return fields;
	}
	/**
	 * @param fields The fields to set.
	 */
	public void setFields(Vector fields) {
		this.fields = fields;
	}
	
	/**
	 * @param fields The fields to set.
	 */
	public void setFields(Object[] fields) {
		this.fields.clear();
		for (int i=0 ; i<fields.length ; i++){
			this.fields.add(fields[i]);
		}
	}
	
	
	/**
	 * 
	 */
	public String toString(){
		String str;
		if (getName()==null)
			str = getTitle();
		else
			str = "["+getName()+"] "+getTitle();
		return str;
	}
	/**
	 * @return Returns the _abstract.
	 */
	public String getAbstract() {
		return _abstract;
	}
	/**
	 * @param _abstract The _abstract to set.
	 */
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the _abstract.
	 */
	public String get_abstract() {
		return _abstract;
	}
	/**
	 * @param _abstract The _abstract to set.
	 */
	public void set_abstract(String _abstract) {
		this._abstract = _abstract;
	}	
	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return Returns the selectedFields.
	 */
	public Vector getSelectedFields() {
		return selectedFields;
	}
	/**
	 * @param selectedFields The selectedFields to set.
	 */
	public void setSelectedFields(Vector selectedFields) {
		this.selectedFields = selectedFields;
	}
	
	/**
	 * @param fields The fields to set.
	 */
	public void setSelectedFields(Object[] selectedFields) {
		this.selectedFields.clear();
		for (int i=0 ; i<selectedFields.length ; i++){
			this.selectedFields.add(selectedFields[i]);
		}
	}
	/**
	 * @return Returns the srs.
	 */
	public Vector getSrs() {
		return srs;
	}
	/**
	 * @param srs The srs to set.
	 */
	public void setSrs(Vector srs) {
		this.srs = srs;
	}
	/**
	 * @return Returns the latLonBbox.
	 */
	public BoundaryBox getLatLonBbox() {
		return latLonBbox;
	}
	/**
	 * @param latLonBbox The latLonBbox to set.
	 */
	public void setLatLonBbox(BoundaryBox latLonBbox) {
		this.latLonBbox = latLonBbox;
	}
	/**
	 * @return Returns the geometry.
	 */
	public GMLGeometryType getGeometry() {
		if (geometry == null){
			if ((getFields() != null) && (getFields().size()>0)){
				XMLElement element = (XMLElement)getFields().get(0);
				//If it doesn't has type
				if (element.getEntityType() != null){
					if (element.getEntityType().getType() == IXMLType.COMPLEX){
						Vector attributes = ((XMLComplexType)element.getEntityType()).getAttributes();
						for (int i=0 ; i<attributes.size() ; i++){
							XMLElement child = (XMLElement)attributes.get(i);
							if (child.getEntityType() != null){
								if (child.getEntityType().getType() == IXMLType.GML_GEOMETRY){
									geometry = (GMLGeometryType)child.getEntityType();
								}
							}
						}
					}
				}
			}			
		}
		return geometry;
	}
	/**
	 * @param geometry The geometry to set.
	 */
	public void setGeometry(GMLGeometryType geometry) {
		this.geometry = geometry;
	}
	/**
	 * @return Returns the filter.
	 */
	public String getFilter() {
		return filter;
	}
	/**
	 * @param filter The filter to set.
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
