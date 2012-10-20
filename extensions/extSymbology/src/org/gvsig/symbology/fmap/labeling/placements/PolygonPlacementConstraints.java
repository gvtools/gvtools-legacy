/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 * $Id: PolygonPlacementConstraints.java 13606 2007-09-10 15:47:11Z jaume $
 * $Log$
 * Revision 1.11  2007-09-10 15:47:11  jaume
 * *** empty log message ***
 *
 * Revision 1.10  2007/07/18 06:54:34  jaume
 * continuing with cartographic support
 *
 * Revision 1.9  2007/04/26 11:41:00  jaume
 * attempting to let defining size in world units
 *
 * Revision 1.8  2007/04/19 14:21:30  jaume
 * *** empty log message ***
 *
 * Revision 1.7  2007/04/18 15:35:11  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2007/04/13 11:59:30  jaume
 * *** empty log message ***
 *
 * Revision 1.5  2007/04/12 14:28:43  jaume
 * basic labeling support for lines
 *
 * Revision 1.4  2007/04/11 16:01:08  jaume
 * maybe a label placer refactor
 *
 * Revision 1.3  2007/04/02 16:34:56  jaume
 * Styled labeling (start commiting)
 *
 * Revision 1.2  2007/03/09 08:33:43  jaume
 * *** empty log message ***
 *
 * Revision 1.1.2.1  2007/02/21 07:34:08  jaume
 * labeling starts working
 *
 *
 */
package org.gvsig.symbology.fmap.labeling.placements;

import com.iver.utiles.XMLEntity;

/**
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 * 
 */
public class PolygonPlacementConstraints extends AbstractPlacementConstraints {
	public PolygonPlacementConstraints() {
		setPlacementMode(HORIZONTAL);
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = super.getXMLEntity();
		xml.putProperty("className", getClassName());
		return xml;
	}

}
