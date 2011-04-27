/*
 * Created on 20-oct-2006
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
* $Id: HTMLEditorKit.java 22182 2008-07-10 07:20:11Z fpenarrubia $
* $Log$
* Revision 1.2  2006-11-09 12:25:06  fjp
* El fichero de red pasa a llamarse .net y se escribe sobre el directorio temporal del usuario. También se mira si existe ahí para habilitar/deshabilitar la opción de carga.
*
* Revision 1.1  2006/10/20 19:54:01  azabala
* *** empty log message ***
*
*
*/
package org.gvsig.graph.gui;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;

import org.gvsig.graph.GenerateNetworkExtension;



public class HTMLEditorKit extends javax.swing.text.html.HTMLEditorKit {
	 public ViewFactory getViewFactory() {
		    return new HTMLFactoryX();
		  }


		  public static class HTMLFactoryX extends HTMLFactory
		    implements ViewFactory {
		    
		    public View create(Element elem) {
		      Object o = 
		        elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
		      if (o instanceof HTML.Tag) {
			HTML.Tag kind = (HTML.Tag) o;
		        if (kind == HTML.Tag.IMG) 
		          return new ImageView(elem, GenerateNetworkExtension.class );
		      }
		      return super.create( elem );
		    }
		  }
}



