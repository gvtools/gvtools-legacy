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
package com.iver.cit.gvsig.fmap.edition.rules;

import com.iver.cit.gvsig.fmap.core.FCircle2D;
import com.iver.cit.gvsig.fmap.core.FEllipse2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;

/**
 * @author fjp
 *
 * Si le llega un punto, no pasa.
 * Si le llega una línea cerrada, la convierte en polígono y pasa.
 */
public class RulePolygon extends AbstractRule {

    public boolean validate(IRow row,int sourceType) {
        if (sourceType==EditionEvent.ALPHANUMERIC)
        	return true;
    	IFeature feat = (IFeature) row;
        IGeometry geom = feat.getGeometry();
        if ((geom.getInternalShape() instanceof FCircle2D)
        		|| (geom.getInternalShape() instanceof FEllipse2D)) {
        	return true;
        }
        GeneralPathX gp = new GeneralPathX();
        gp.append(geom.getPathIterator(null, FConverter.FLATNESS), true);

        if (gp.isClosed())
        {
        	boolean bCCW =gp.isCCW();
        	System.out.println("Counter ClockWise  = " + bCCW);
        	if (bCCW)
        	{
//         		gp.ensureOrientation(false);
        		gp.flip();
//            	IGeometry aux = ShapeFactory.createPolygon2D(gp);
//            	Geometry jtsGeom = aux.toJTSGeometry();
//            	geom = FConverter.jts_to_igeometry(jtsGeom);

        		geom = ShapeFactory.createPolygon2D(gp);
        	}
        	else
        	{
        		geom = ShapeFactory.createPolygon2D((FPolyline2D)geom.getInternalShape());
        	}
//        	IGeometry aux = ShapeFactory.createPolygon2D(gp);
//
//        	Geometry jtsGeom = geom.toJTSGeometry();
//        	System.err.println(jtsGeom.toText());
//
//        	geom = FConverter.jts_to_igeometry(jtsGeom);

            // gp.ensureOrientation(false); // Poligono exterior.

//        	Area area = new Area(geom);
//            GeneralPathX gp2 = new GeneralPathX();
//            gp2.append(area.getPathIterator(null), true);
//            geom = ShapeFactory.createPolygon2D(gp2);

            feat.setGeometry(geom);
            return true;
        }

        return false;
    }

}


