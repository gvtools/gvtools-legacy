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
package com.iver.cit.gvsig.project.documents.layout.fframes;

import com.iver.cit.gvsig.project.documents.layout.gui.Layout;

/**
 * Modelo de la Lista de vistas a seleccionar sin incluir los localizadores.
 * 
 * @author Vicente Caballero Navarro
 */
public class ListWithoutOverViewModel extends ListViewModel {

	/**
	 * Add all fframeviews into a list.
	 * 
	 * @param l
	 *            Layout.
	 */
	public void addViews(Layout l) {
		int num = 0;
		l.getLayoutContext().updateFFrames();
		IFFrame[] fframes = l.getLayoutContext().getFFrames();
		for (int i = 0; i < fframes.length; i++) {
			IFFrame f = fframes[i];

			if (f instanceof FFrameView && !(f instanceof FFrameOverView)) {
				views.add(f);
				((FFrameView) f).setNum(num);
				num++;
			}
		}
	}
}
