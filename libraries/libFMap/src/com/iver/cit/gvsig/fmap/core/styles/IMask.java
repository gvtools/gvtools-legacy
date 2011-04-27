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
package com.iver.cit.gvsig.fmap.core.styles;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.utiles.XMLEntity;

/**
 * Interface for the style of a mask.
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public interface IMask extends IStyle {

	public double getSize();

	public void setSize(double size);

	public IFillSymbol getFillSymbol();

	public void setFillSymbol(IFillSymbol fill);

	public FShape getHaloShape(Shape shp);

	/**
	 * Class that implements the methods of the IMask interface showed above.
	 *
	 * @author jaume dominguez faus - jaume.dominguez@iver.es
	 *
	 */
	class BasicMask extends AbstractStyle implements IMask {
		private double size;
		private IFillSymbol fill;


		public double getSize() {
			return size;
		}

		public void setSize(double size) {
			this.size = size;
		}

		public IFillSymbol getFillSymbol() {
			return fill;

		}

		public void setFillSymbol(IFillSymbol fill) {
			this.fill = fill;
		}

		public void drawInsideRectangle(Graphics2D g, Rectangle r) {
			// TODO Implement it
			throw new Error("Not yet implemented!");

		}

		public FShape getHaloShape(Shape shp) {
			BasicStroke stroke = new BasicStroke((int) /*falta CartographicSupport*/getSize());
			Shape myShp = stroke.createStrokedShape(shp);
			FShape haloShape = new FPolygon2D(new GeneralPathX(myShp.getBounds2D()));

			return haloShape;
		}

		public boolean isSuitableFor(ISymbol symbol) {
			// TODO Implement it
			throw new Error("Not yet implemented!");

		}

		public void drawOutline(Graphics2D g, Rectangle r) {
			// TODO Implement it
			throw new Error("Not yet implemented!");

		}

		public String getClassName() {
			return this.getClass().getName();
		}

		public XMLEntity getXMLEntity() {
			// TODO Implement it
			throw new Error("Not yet implemented!");

		}

		public void setXMLEntity(XMLEntity xml) {
			// TODO Implement it
			throw new Error("Not yet implemented!");

		}

	}
}
