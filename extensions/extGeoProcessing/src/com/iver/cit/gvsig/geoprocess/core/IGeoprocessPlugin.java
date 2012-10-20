/*
 * Created on 20-jun-2006
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
 * $Id: IGeoprocessPlugin.java 21230 2008-06-05 14:01:14Z azabala $
 * $Log$
 * Revision 1.4  2007-08-13 11:50:36  jmvivo
 * Extraidas los htm de descripcion de las traducciones a un directorio fuera del jar
 *
 * Revision 1.3  2006/06/29 17:28:24  azabala
 * added comments
 *
 * Revision 1.2  2006/06/27 16:10:14  azabala
 * toString() added to interface to force textual representation of geoprocess plugins
 *
 * Revision 1.1  2006/06/23 19:01:58  azabala
 * first version in cvs
 *
 * Revision 1.1  2006/06/22 17:46:30  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.core;

import java.net.URL;

import com.iver.cit.gvsig.geoprocess.core.gui.IGeoprocessUserEntries;

/**
 * Ofrece acceso a todo lo necesario para añadir un geoproceso al
 * geoprocessmanager: a) geoproceso en si b) panel grafico c) html descriptivo
 * d) imagen descriptiva e) controlador, que lee las entradas de la GUI y se las
 * pasa al geoproceso
 * 
 * ¿Como podemos hacer que cuando un desarrollador externo haga una extension
 * con nuevos geoprocesos, se cosque el GeoprocessManager?
 * 
 * El geoprocessmanager tendrá asociada una extension, y en esta extension se
 * construye el componente y se añaden los geoprocesos del core (union,
 * diferencia, etc). Además, construye un punto de extension con el API
 * ExtensionPoints (IverUtiles)
 * 
 * En el metodo initialize() de la Extension ANDAMI del GeoprocessManager, se
 * crearía el punto de extension "GeoprocessManager".
 * 
 * Si desde otro proyecto se quiere crear un geoproceso, y que se muestre en el
 * geoprocess manager, solo habría que construir una extension de andami, y en
 * su metodo initialize añadir cada geoproceso nuevo al punto de extension.
 * 
 * Luego, desde el GeoprocessManager, en el metodo execute() de la extension
 * asociada ya se haría lo siguiente: ExtensionPoint infoByPoint =
 * (ExtensionPoint)extensionPoints.get("GeoprocessManager"); Iterator
 * infoByPoint =infoByPoint.keySet().iterator(); while( i.hasNext() ) { String
 * nombre = (String)i.next(); } Quizás sea menos elegante que el mecanismo de
 * los drivers (con su propio classloader), pero simplifica la gestión. No hay
 * que echar ningún jar en ningún directorio. Solo crear las extensiones Andami
 * encargadas de registrar los nuevos geoprocesos en el punto de extension.
 * 
 * 
 * @author azabala
 * 
 */

public interface IGeoprocessPlugin {
	public IGeoprocessUserEntries getGeoprocessPanel();

	// JEditorPane htmlPane = new JEditorPane(url);
	// htmlPane.setPage(new URL(url));
	public URL getHtmlDescription();

	/**
	 * @deprecated
	 */
	public URL getImgDescription();

	public IGeoprocessController getGpController();

	/**
	 * Gives access to the geoprocess namespace. Namespaces are artifacts to
	 * identify geoproccesses by a path (similar to xpath), and to organize
	 * them. For example: Analysis Tools/Overlay/Union Data Management
	 * Tools/Generalization/Dissolve Data Management
	 * Tools/Generalization/Dissolve by multiple fields
	 * 
	 * @return
	 */
	public String getNamespace();

	/**
	 * To give a textual representation of the plugin
	 * 
	 * @return
	 */
	public String toString();
}
