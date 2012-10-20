package org.gvsig.hyperlink.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URI;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.gvsig.hyperlink.AbstractHyperLinkPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;

/**
 * This class extends AbstractHyperLink, and provides suppot to open images of
 * many formats. The common supported formats are JPG, ICO, BMP, TIFF, GIF and
 * PNG. Implements methods from IExtensionBuilder to make it extending.
 * 
 * @author Eustaquio Vercher (IVER)
 * @author Cesar Martinez Izquierdo (IVER)
 */
public class SvgPanel extends AbstractHyperLinkPanel {
	private static final long serialVersionUID = -5200841105188251551L;
	private GVTBuilder gvtBuilder = new GVTBuilder();
	private GraphicsNode gvtRoot = null;
	private BridgeContext ctx = null;
	private StaticRenderer renderer = new StaticRenderer();
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
	 * Default constructor.
	 */
	public SvgPanel(URI doc) {
		super(doc);
		initialize();
	}

	/**
	 * Initializes this panel.
	 */
	void initialize() {
		this.setLayout(new BorderLayout());
		showDocument();
		// this.setSize(600, 400);
	}

	/**
	 * Implements the necessary code to open images in this panel.
	 */
	protected void showDocument() {
		if (!checkAndNormalizeURI()) {
			return;
		}

		ImageIcon image;
		// try {
		image = new ImageIcon(getSvgAsImage(document.toString()));
		this.setPreferredSize(new Dimension(image.getIconWidth(), image
				.getIconHeight()));
		this.setSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
		JLabel label = new JLabel(image);
		this.add(label);
	}

	/**
	 * Allows paint SVG images in the panel.
	 * 
	 * @param file
	 *            , this file has been extracted from the URI
	 */
	private Image getSvgAsImage(String uri) {
		BufferedImage img = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		Rectangle2D rect = new Rectangle2D.Double();
		rect.setFrame(0, 0, 400, 400);
		obtainStaticRenderer(uri);
		drawSVG(g, rect, null);
		return img;
	}

	/**
	 * Render the image to add to the panel.
	 * 
	 * @param file
	 *            , this file has been extracted from the URI
	 */
	private void obtainStaticRenderer(String uri) {
		try {
			UserAgentAdapter userAgent = new UserAgentAdapter();
			DocumentLoader loader = new DocumentLoader(userAgent);
			ctx = new BridgeContext(userAgent, loader);
			// Document svgDoc = loader.loadDocument(file.toURI().toString());
			Document svgDoc = loader.loadDocument(uri);
			gvtRoot = gvtBuilder.build(ctx, svgDoc);
			renderer.setTree(gvtRoot);
			elt = ((SVGDocument) svgDoc).getRootElement();
		} catch (Exception ex) {
			NotificationManager.addWarning(PluginServices.getText(this,
					"Hyperlink_linked_field_doesnot_exist"), ex);
		}
	}

	/**
	 * Draw SVG in the Graphics that receives like parameter.
	 * 
	 * @param g
	 *            Graphics
	 * @param rect
	 *            Rectangle that fills the Graphic.
	 * @param rv
	 *            Rectangle. This forms the visible part in the Layout
	 */
	private void drawSVG(Graphics2D g, Rectangle2D rect, Rectangle2D rv) {
		if ((rv == null) || rv.contains(rect)) {
			AffineTransform ataux = new AffineTransform();

			ataux.translate(rect.getX(), rect.getY());
			try {
				ataux.concatenate(ViewBox.getViewTransform(null, elt,
						(float) rect.getWidth(), (float) rect.getHeight(), ctx));
				gvtRoot.setTransform(ataux);

			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			AffineTransform ataux = new AffineTransform();

			ataux.translate(rect.getX(), rect.getY());
			ataux.concatenate(ViewBox.getViewTransform(null, elt,
					(float) rect.getWidth(), (float) rect.getHeight(), ctx));

			gvtRoot.setTransform(ataux);
		}

		RenderingHints renderingHints = defaultRenderingHints;
		g.setRenderingHints(renderingHints);

		if (gvtRoot != null) {
			gvtRoot.paint(g);
		}
	}

}
