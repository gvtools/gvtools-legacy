/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
* $Id: AbstractMarkerSymbol.java 36362 2011-09-05 14:21:40Z fpuga $
* $Log$
* Revision 1.14  2007-09-21 12:25:32  jaume
* cancellation support extended down to the IGeometry and ISymbol level
*
* Revision 1.13  2007/09/19 16:20:45  jaume
* removed unnecessary imports
*
* Revision 1.12  2007/09/17 09:33:47  jaume
* some multishapedsymbol bugs fixed
*
* Revision 1.11  2007/07/18 06:54:35  jaume
* continuing with cartographic support
*
* Revision 1.10  2007/07/03 10:58:29  jaume
* first refactor on CartographicSupport
*
* Revision 1.9  2007/05/22 10:05:31  jaume
* *** empty log message ***
*
* Revision 1.8  2007/05/09 16:07:26  jaume
* *** empty log message ***
*
* Revision 1.7  2007/05/08 08:47:40  jaume
* *** empty log message ***
*
* Revision 1.6  2007/03/26 14:24:13  jaume
* implemented Print
*
* Revision 1.5  2007/03/21 11:02:17  jaume
* *** empty log message ***
*
* Revision 1.4  2007/03/09 11:20:56  jaume
* Advanced symbology (start committing)
*
* Revision 1.3.2.4  2007/02/16 10:54:12  jaume
* multilayer splitted to multilayerline, multilayermarker,and  multilayerfill
*
* Revision 1.3.2.3  2007/02/15 16:23:44  jaume
* *** empty log message ***
*
* Revision 1.3.2.2  2007/02/09 07:47:05  jaume
* Isymbol moved
*
* Revision 1.3.2.1  2007/02/02 16:21:24  jaume
* start commiting labeling stuff
*
* Revision 1.3  2007/01/25 16:25:23  jaume
* *** empty log message ***
*
* Revision 1.2  2007/01/16 11:50:44  jaume
* *** empty log message ***
*
* Revision 1.1  2007/01/10 16:31:36  jaume
* *** empty log message ***
*
* Revision 1.4  2006/12/04 17:13:39  fjp
* *** empty log message ***
*
* Revision 1.3  2006/11/14 11:10:27  jaume
* *** empty log message ***
*
* Revision 1.2  2006/11/09 18:39:05  jaume
* *** empty log message ***
*
* Revision 1.1  2006/10/31 16:16:34  jaume
* *** empty log message ***
*
* Revision 1.4  2006/10/30 19:30:35  jaume
* *** empty log message ***
*
* Revision 1.3  2006/10/26 16:27:33  jaume
* support for composite marker symbols (not tested)
*
* Revision 1.2  2006/10/26 07:46:58  jaume
* *** empty log message ***
*
* Revision 1.1  2006/10/25 10:50:41  jaume
* movement of classes and gui stuff
*
* Revision 1.1  2006/10/18 07:54:06  jaume
* *** empty log message ***
*
*
*/
package com.iver.cit.gvsig.fmap.core.symbols;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;

import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.IMask;


/**
 * Abstract class that any MARKER SYMBOL should extend.
 * @author  jaume dominguez faus - jaume.dominguez@iver.es
 */
public abstract class AbstractMarkerSymbol extends AbstractSymbol implements IMarkerSymbol {
	private Color color = SymbologyFactory.DefaultSymbolColor;
	private double rotation;
	private Point2D offset = new Point2D.Double();
	private double size;
	private IMask mask;

	public final int getSymbolType() {
		return FShape.POINT;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double r) {
		this.rotation = r;
	}

	public Point2D getOffset() {
		if (offset == null) {
			offset = new Point();
		}
		return offset;
	}

	public void setOffset(Point2D offset) {
		this.offset = offset;
	}

	public boolean isSuitableFor(IGeometry geom) {
		return (geom.getGeometryType()%FShape.Z) == FShape.POINT;
	}

	public int getOnePointRgb() {
		return color.getRGB();
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void getPixExtentPlus(FShape shp, float[] distances, ViewPort viewPort, int dpi) {
		float cs = (float) getCartographicSize(viewPort, dpi, shp);
		distances[0] = cs;
		distances[1] = cs;
	}


	public void print(Graphics2D g, AffineTransform at, FShape shape, PrintRequestAttributeSet properties) {
		double originalSize = getSize();
		double size=originalSize;
		// scale it to size
		PrintQuality pq = (PrintQuality) properties.get(PrintQuality.class);
		if (pq.equals(PrintQuality.NORMAL)){
			size *= (double) 300/72;
		}else if (pq.equals(PrintQuality.HIGH)){
			size *= (double) 600/72;
		}else if (pq.equals(PrintQuality.DRAFT)){
			//	unitFactor *= 72; (which is the same than doing nothing)
		}
//		setSize(size);
		draw(g,at,shape,null);
//		setSize(originalSize);
	}

	public final IMask getMask() {
		return mask;
	}

	public void drawInsideRectangle(Graphics2D g, AffineTransform scaleInstance, Rectangle r, PrintRequestAttributeSet properties) throws SymbolDrawingException {
		if (properties==null)
			draw(g, scaleInstance, new FPoint2D(r.getCenterX(), r.getCenterY()), null);
		else{
			double originalSize = getSize();
			double size=originalSize;
			PrintQuality pq = (PrintQuality) properties.get(PrintQuality.class);
			if (pq.equals(PrintQuality.NORMAL)){
				size *= (double) 300/72;
			}else if (pq.equals(PrintQuality.HIGH)){
				size *= (double) 600/72;
			}else if (pq.equals(PrintQuality.DRAFT)){
				//	unitFactor *= 72; (which is the same than doing nothing)
			}
			setSize(size);
			print(g, scaleInstance, new FPoint2D(r.getCenterX(), r.getCenterY()), properties);
			setSize(originalSize);
		}
	}

	public final void setMask(IMask mask) {
		this.mask = mask;
	}



	public void setCartographicSize(double cartographicSize, FShape shp) {
		setSize(cartographicSize);
	}

	public double toCartographicSize(ViewPort viewPort, double dpi, FShape shp) {
		double oldSize = getSize();
		setCartographicSize(getCartographicSize(
								viewPort,
								dpi,
								shp),
							shp);
		return oldSize;
	}

	public double getCartographicSize(ViewPort viewPort, double dpi, FShape shp) {
		return CartographicSupportToolkit.
					getCartographicLength(this,
										  getSize(),
										  viewPort,
										  dpi);
	}
}
