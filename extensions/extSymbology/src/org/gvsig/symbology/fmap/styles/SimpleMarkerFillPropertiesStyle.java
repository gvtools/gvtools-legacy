package org.gvsig.symbology.fmap.styles;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;

import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.styles.AbstractStyle;
import com.iver.cit.gvsig.fmap.core.styles.IMarkerFillPropertiesStyle;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SymbolDrawingException;
import com.iver.utiles.XMLEntity;

public class SimpleMarkerFillPropertiesStyle extends AbstractStyle implements
		IMarkerFillPropertiesStyle {
	private IMarkerSymbol sampleSymbol = new SimpleMarkerSymbol();
	private double rotation;
	private double xOffset = 0;
	private double yOffset = 0;
	private double xSeparation = 20;
	private double ySeparation = 20;
	private int fillStyle = MarkerFillSymbol.DefaultFillStyle;

	public void drawInsideRectangle(Graphics2D g, Rectangle r) {
		int s = (int) sampleSymbol.getSize();
		Rectangle rProv = new Rectangle();
		rProv.setFrame(0, 0, s, s);
		Paint resulPatternFill = null;
		BufferedImage bi = null;
		bi = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gAux = bi.createGraphics();
		try {
			sampleSymbol.drawInsideRectangle(gAux, gAux.getTransform(), rProv,
					null);
		} catch (SymbolDrawingException e) {
			if (e.getType() == SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS) {
				try {
					SymbologyFactory
							.getWarningSymbol(
									SymbolDrawingException.STR_UNSUPPORTED_SET_OF_SETTINGS,
									"",
									SymbolDrawingException.UNSUPPORTED_SET_OF_SETTINGS)
							.drawInsideRectangle(gAux, gAux.getTransform(),
									rProv, null);
				} catch (SymbolDrawingException e1) {
					// IMPOSSIBLE TO REACH THIS
				}
			} else {
				// should be unreachable code
				throw new Error(Messages.getString("symbol_shapetype_mismatch"));
			}
		}
		resulPatternFill = new TexturePaint(bi, rProv);
		g.setColor(null);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(resulPatternFill);
		g.fill(r);

	}

	public boolean isSuitableFor(ISymbol symbol) {
		return (symbol.getSymbolType() % FShape.Z) == FShape.POLYGON;
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("rotation", rotation);
		xml.putProperty("xOffset", xOffset);
		xml.putProperty("yOffset", yOffset);
		xml.putProperty("xSeparation", xSeparation);
		xml.putProperty("ySeparation", ySeparation);
		xml.putProperty("fillStyle", fillStyle);
		// please, avoid persist "sampleSymbol" field.
		// it is always initialized by this style's owner
		// when needs it.
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		rotation = xml.getDoubleProperty("rotation");
		xOffset = xml.getDoubleProperty("xOffset");
		yOffset = xml.getDoubleProperty("yOffset");
		xSeparation = xml.getDoubleProperty("xSeparation");
		ySeparation = xml.getDoubleProperty("ySeparation");
		fillStyle = xml.getIntProperty("fillStyle");
		// please, avoid initialize "sampleSymbol" field. It
		// is already controlled by this style's owner.
	}

	public void drawOutline(Graphics2D g, Rectangle r) {
		drawInsideRectangle(g, r);
	}

	/**
	 * <p>
	 * Define an utility symbol to show up a thumbnail by default, this symbol
	 * is a SimpleMarkerSymbol. Thus, the drawInsideRectangle will always work.
	 * But it can be changed with setSampleSymbol(IMakerSymbol).<br>
	 * </p>
	 * <p>
	 * If <b>marker</b> is null, it does nothing
	 * </p>
	 */
	public void setSampleSymbol(IMarkerSymbol marker) {
		if (marker != null)
			this.sampleSymbol = marker;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getXOffset() {
		return xOffset;
	}

	public void setXOffset(double offset) {
		xOffset = offset;
	}

	public double getXSeparation() {
		return xSeparation;
	}

	public void setXSeparation(double separation) {
		xSeparation = separation;
	}

	public double getYOffset() {
		return yOffset;
	}

	public void setYOffset(double offset) {
		yOffset = offset;
	}

	public double getYSeparation() {
		return ySeparation;
	}

	public void setYSeparation(double separation) {
		ySeparation = separation;
	}

	public void setFillStyle(int fillStyle) {
		this.fillStyle = fillStyle;
	}

	public int getFillStyle() {
		return fillStyle;
	}

}
