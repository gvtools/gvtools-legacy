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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;

/**
 * Style for a SVG files.This is a XML specification and file format for
 * describing two-dimensional vector graphics, both static and animated. SVG can
 * be purely declarative or may include scripting. Images can contain hyperlinks
 * using outbound simple XLinks.It is an open standard created by the World Wide
 * Web Consortium..
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 * 
 */
public class SVGStyle extends BackgroundFileStyle {

	private GVTBuilder gvtBuilder = new GVTBuilder();
	private UserAgentAdapter userAgent;
	private DocumentLoader loader;
	private StaticRenderer renderer = new StaticRenderer();
	private GraphicsNode gvtRoot;
	private BridgeContext ctx;
	private Element elt;

	protected static RenderingHints defaultRenderingHints;
	static {
		defaultRenderingHints = new RenderingHints(null);
		defaultRenderingHints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		defaultRenderingHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	/**
	 * Constructor method
	 * 
	 */
	public SVGStyle() {
		userAgent = new UserAgentAdapter();
		loader = new DocumentLoader(userAgent);
		ctx = new BridgeContext(userAgent, loader);
		renderer.setDoubleBuffered(true);
	}

	public void drawInsideRectangle(Graphics2D g, Rectangle rect,
			boolean keepAspectRatio) throws SymbolDrawingException {
		if (keepAspectRatio) {
			AffineTransform ataux;
			if (elt.hasAttribute("viewBox")) {

				try {
					ataux = ViewBox.getViewTransform(null, elt,
							(float) rect.getWidth(), (float) rect.getHeight(),
							ctx);
				} catch (NullPointerException e) {
					throw new SymbolDrawingException(
							SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS);
				}
			} else {
				Rectangle2D bounds = gvtRoot.getBounds();
				double xOffset = 0;
				double yOffset = 0;
				double xScale = 1;
				double yScale = 1;
				double scale;
				scale = Math.min(rect.getWidth() / bounds.getWidth(),
						rect.getHeight() / bounds.getHeight());
				xOffset = 0.5 * (rect.getWidth() - bounds.getWidth() * scale);
				yOffset = 0.5 * (rect.getHeight() - bounds.getHeight() * scale);
				xScale = yScale = scale;

				ataux = AffineTransform.getTranslateInstance(xOffset, yOffset);

				ataux.concatenate(AffineTransform
						.getScaleInstance(scale, scale));

			}
			RenderingHints renderingHints = new RenderingHints(null);
			renderingHints.putAll(defaultRenderingHints);
			g.setRenderingHints(renderingHints);
			gvtRoot.setTransform(ataux);
			gvtRoot.paint(g);

		} else {

			Rectangle2D bounds = gvtRoot.getBounds();

			double xOffset = 0;
			double yOffset = 0;
			double xScale = 1;
			double yScale = 1;
			xScale = rect.getWidth() / bounds.getWidth();
			yScale = rect.getHeight() / bounds.getHeight();
			xOffset = 0.5 * (rect.getWidth() - bounds.getWidth() * xScale);
			yOffset = 0.5 * (rect.getHeight() - bounds.getHeight() * yScale);

			AffineTransform ataux;

			ataux = AffineTransform.getTranslateInstance(xOffset, yOffset);

			ataux.concatenate(AffineTransform.getScaleInstance(xScale, yScale));

			RenderingHints renderingHints = new RenderingHints(null);
			renderingHints.putAll(defaultRenderingHints);
			g.setRenderingHints(renderingHints);
			gvtRoot.setTransform(ataux);
			gvtRoot.paint(g);
		}
	}

	public boolean isSuitableFor(ISymbol symbol) {
		return true;
	}

	public String getClassName() {
		return getClass().getName();
	}

	public void setSource(URL url) throws IOException {

		File f = new File(url.getFile());
		File fAux = null;
		if (f.isAbsolute()) {
			sourceFile = url;
			fAux = f;
			isRelativePath = false;
		} else {
			sourceFile = new URL(SymbologyFactory.SymbolLibraryPath
					+ File.separator + f.getPath());
			fAux = new File(sourceFile.getFile());
			isRelativePath = true;
		}

		Document svgDoc = loader.loadDocument(fAux.toURI().toString());
		gvtRoot = gvtBuilder.build(ctx, svgDoc);
		renderer.setTree(gvtRoot);
		elt = ((SVGOMDocument) svgDoc).getRootElement();
	}

	public Rectangle getBounds() {
		try {
			Rectangle2D r = gvtRoot.getBounds();
			return new Rectangle((int) r.getX(), (int) r.getY(),
					(int) r.getWidth(), (int) r.getHeight());
		} catch (Exception e) {
			return new Rectangle();
		}
	}

	public void drawOutline(Graphics2D g, Rectangle r)
			throws SymbolDrawingException {
		drawInsideRectangle(g, r);
	}
}
