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

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.AbstractStyle;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.XMLEntity;

/**
 * Defines methods that allows the user to create an style based in a background
 * file.For this reason, BackgroundFileStyle will have a parameter that will be
 * an string in order to specify this source file.
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public abstract class BackgroundFileStyle extends AbstractStyle {

	public static BackgroundFileStyle createStyleByURL(URL url)
			throws IOException {
		BackgroundFileStyle bgImage;
		String l = url.toString().toLowerCase();
		if (l.startsWith("http://") || l.startsWith("https://")) {
			bgImage = new RemoteFileStyle();
		} else if (l.toLowerCase().endsWith(".svg")) {
			bgImage = new SVGStyle();
		} else {
			bgImage = new ImageStyle();
		}
		bgImage.setSource(url);
		return bgImage;
	}

	protected URL sourceFile;
	protected boolean isRelativePath;

	/**
	 * Sets the file that is used as a source to create the Background
	 * 
	 * @param f
	 *            , File
	 * @throws IOException
	 */
	public abstract void setSource(URL url) throws IOException;

	/**
	 * Gets the bounding <code>Rectangle</code> of this <code>Rectangle</code>.
	 * <p>
	 * This method is included for completeness, to parallel the
	 * <code>getBounds</code> method of {@link Component}.
	 * 
	 * @return a new <code>Rectangle</code>, equal to the bounding
	 *         <code>Rectangle</code> for this <code>Rectangle</code>.
	 * @see java.awt.Component#getBounds
	 * @see #setBounds(Rectangle)
	 * @see #setBounds(int, int, int, int)
	 * @since JDK1.1
	 */
	public abstract Rectangle getBounds();

	public XMLEntity getXMLEntity() {

		XMLEntity xml = new XMLEntity();
		String source = sourceFile.toString();

		// if(isRelativePath)
		// source=sourceFile.toString().substring(SymbologyFactory.SymbolLibraryPath.length()+1);

		xml.putProperty("className", getClassName());
		xml.putProperty("source", source);
		xml.putProperty("desc", getDescription());
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		String strSource = xml.getStringProperty("source");
		URL source = null;
		try {
			try {
				source = new URL(strSource);
			} catch (MalformedURLException e) {
				File fileSource = new File(SymbologyFactory.StyleLibraryPath
						+ File.separator + strSource);
				source = fileSource.toURL();
			}
			setSource(source);
			setDescription(xml.getStringProperty("desc"));
		} catch (IOException ioEx) {
			Logger.getLogger(this.getClass()).error(
					"Can't load image '" + strSource + "'");
		}

	}

	/**
	 * Obtains the source of the file which is used to create the background
	 * 
	 * @return
	 */
	public final URL getSource() {
		return sourceFile;
	}

	public final void drawInsideRectangle(Graphics2D g, Rectangle r)
			throws SymbolDrawingException {
		drawInsideRectangle(g, r, true);
	}

	public abstract void drawInsideRectangle(Graphics2D g, Rectangle r,
			boolean keepAspectRatio) throws SymbolDrawingException;
}
