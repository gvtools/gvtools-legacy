package es.gva.cit.gvsig.gazetteer.gui;
import javax.swing.JFrame;

import es.gva.cit.gvsig.gazetteer.gui.tools.SearchToolPanel;

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
 * $Id: SearchToolPanelTest.java 9263 2006-12-12 10:20:51Z jorpiell $
 * $Log$
 * Revision 1.1  2006-12-12 10:20:50  jorpiell
 * Se ha creado una interfaz que deben usar las tools del gazetteer
 *
 * Revision 1.1  2006/12/12 08:52:29  jorpiell
 * Primera versión de la tool de búsqueda usando el protocolo ADL
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (piera_jor@gva.es)
 */
public class SearchToolPanelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
            	System.exit(0);
            }
        });
		f.getContentPane().add(new SearchToolPanel());	    
		f.setBounds(100,100,515,225);
		f.setVisible(true);
	}

}
