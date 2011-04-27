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
* $Id: LinePlacementConstraints.java 13606 2007-09-10 15:47:11Z jaume $
* $Log$
* Revision 1.11  2007-09-10 15:47:11  jaume
* *** empty log message ***
*
* Revision 1.10  2007/05/22 10:05:31  jaume
* *** empty log message ***
*
* Revision 1.9  2007/05/08 08:47:40  jaume
* *** empty log message ***
*
* Revision 1.8  2007/04/18 15:35:11  jaume
* *** empty log message ***
*
* Revision 1.7  2007/04/13 11:59:30  jaume
* *** empty log message ***
*
* Revision 1.6  2007/04/12 16:01:11  jaume
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
* Revision 1.1.2.1  2007/02/09 07:47:05  jaume
* Isymbol moved
*
*
*/
package org.gvsig.symbology.fmap.labeling.placements;

import com.iver.cit.gvsig.fmap.rendering.styling.labeling.IPlacementConstraints;
import com.iver.utiles.XMLEntity;
/**
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 *
 */
public class LinePlacementConstraints extends AbstractPlacementConstraints {
//	private static final double HALF_PI = Math.PI * 0.5;
//	private Hashtable textPaths = new Hashtable(), texts = new Hashtable();

	public LinePlacementConstraints() {
		super();
		setPlacementMode(PARALLEL);
		setLocationAlongTheLine(IPlacementConstraints.AT_THE_MIDDLE_OF_THE_LINE);
		setPageOriented(false);
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = super.getXMLEntity();
		xml.putProperty("className", getClassName());
		return xml;
	}


//	private TextPath getTreePath(Integer index, Graphics2D g, FShape shp, char[] text) {
//		TextPath tp = (TextPath) textPaths.get(index);
//		if (tp == null) {
//			tp = new TextPath(g, shp, text);
//			textPaths.put(index, tp);
//		}
//		return tp;
//	}
}
