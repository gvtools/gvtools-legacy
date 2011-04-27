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
package org.gvsig.symbology.fmap.styles;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;

/**
 * Controls the style of an image to be correctly painted. This class controls
 * aspects like the source path of the image, creates a rectangle to paint inside 
 * the image, draws the outline of the image and so on.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class ImageStyle extends BackgroundFileStyle {
	private BufferedImage img;
	/**
	 * Creates a rectangle with the dimensions of the buffered image
	 * @return Rectangle
	 */
	public Rectangle getBounds() {
		if (img == null) return new Rectangle();
		return new Rectangle(new Dimension(img.getWidth(), img.getHeight()));
	}
	/**
	 * Defines the source (file) from where the buffered image will be taken.
	 * @param f,File
	 */
	public void setSource(URL url) throws IOException {
		File f = new File(url.getFile());
		if(f.isAbsolute()){
			sourceFile = url;
			img = ImageIO.read(f);
			this.isRelativePath=false;
		}
		else {
			sourceFile = new URL(SymbologyFactory.SymbolLibraryPath + File.separator + f.getPath());
			img = ImageIO.read(f);
			this.isRelativePath=true;	
		}
	}

	public void drawInsideRectangle(Graphics2D g, Rectangle r, boolean keepAspectRatio) {
		if (img != null) {

			double xOffset = 0;
			double yOffset = 0;
			double xScale = 1;
			double yScale = 1;
			if (keepAspectRatio) {
				double scale;
				if (img.getWidth()>img.getHeight()) {
					scale = r.getWidth()/img.getWidth();
					yOffset = 0.5*(r.getHeight() - img.getHeight()*scale);
				} else {
					scale = r.getHeight()/img.getHeight();
					xOffset = 0.5*(r.getWidth() - img.getWidth()*scale);
				}
				xScale = yScale = scale;

			} else {
				xScale = r.getWidth()/img.getWidth();
				yScale = r.getHeight()/img.getHeight();
				yOffset = img.getHeight()*0.5*yScale ;

			}


			AffineTransform at = AffineTransform.getTranslateInstance(xOffset, yOffset);
			at.concatenate(AffineTransform.getScaleInstance(xScale, yScale));
			g.drawRenderedImage(img, at);
		}
	}

	public boolean isSuitableFor(ISymbol symbol) {
		// TODO Implement it
		throw new Error("Not yet implemented!");

	}

	public void drawOutline(Graphics2D g, Rectangle r) throws SymbolDrawingException {
		drawInsideRectangle(g, r);
	}

	public String getClassName() {
		return getClass().getName();
	}


}
