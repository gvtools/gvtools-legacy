/*
 * Created on 14-dic-2005
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
package com.iver.cit.gvsig.fmap.rendering;

import java.awt.Graphics2D;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;

/**
 * @author fjp
 *
 */
public class FGraphicLabel extends FGraphic {

    private FLabel theLabel;
    /**
     * Le pasas la geometría que quieres etiquetar y el texto
     * con el que quieres etiquetarla.
     * @param geom
     * @param idSymbol
     */
    public FGraphicLabel(IGeometry geom, int idSymbol, String theText) {
        super(geom, idSymbol);
        // TODO: Lo correcto debería ser hacer que FLabel
        // siga el patrón COMPOSITE por ejemplo para que los
        // multipoint se etiqueten bien, no solo el primer punto.
        FLabel[] labels = geom.createLabels(0, true);
        theLabel = labels[0];
        theLabel.setString(theText);
    }
    /**
     * @return Returns the theLabel.
     */
    public FLabel getLabel() {
        return theLabel;
    }
    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.rendering.FGraphic#draw(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.core.v02.FSymbol)
     */
    public void draw(Graphics2D g, ViewPort viewPort, ISymbol theSymbol) {
        super.draw(g, viewPort, theSymbol);
        FPoint2D theShape = new FPoint2D(theLabel.getOrig());
        theLabel.draw(g, viewPort.getAffineTransform(), theShape, theSymbol);
//        FGraphicUtilities.DrawLabel(g, viewPort.getAffineTransform(),
//                theShape, theSymbol, theLabel);
        
    }
    /**
     * @param theLabel The theLabel to set.
     */
    public void setLabel(FLabel theLabel) {
        this.theLabel = theLabel;
    }

}
