package org.gvsig.symbology.fmap.styles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.styles.AbstractStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.utiles.XMLEntity;


/**
 *  Specifies the point position for a label
 *
 *
 */
public class PointLabelPositioneer extends AbstractStyle {
	private byte[] preferenceVector = new byte[8];
	private static final Color[] colorVector = new Color[] {
		new Color(140, 140, 140), // gray
		new Color(140, 245, 130), // green
		new Color(130, 170, 245), // light blue
		new Color(100, 100, 255),   // dark blue
	};

	public static final byte FORBIDDEN 		   = 0;
	public static final byte PREFERENCE_HIGH   = 1;
	public static final byte PREFERENCE_NORMAL = 2;
	public static final byte PREFERENCE_LOW    = 3;
	/**
	 * Constructor method
	 *
	 */
	public PointLabelPositioneer() {}

	/**
	 * Constructor method
	 *
	 * @param preferenceVector
	 * @param description
	 */
	public PointLabelPositioneer(byte[] preferenceVector, String description) {
		this.preferenceVector = preferenceVector;
		setDescription(description);
	}

	public void drawInsideRectangle(Graphics2D g, Rectangle r) {
		int size = Math.min(r.width, r.height) / 3;
		int j = -1;
		final int fontSize = (int) (size * 0.8);
		final Font font = new Font("Arial", Font.PLAIN, fontSize);
		RenderingHints old = g.getRenderingHints();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0; i < 9; i++) {
			if (i == 4) continue;
			j++;
			int value = Math.abs(preferenceVector[j] % colorVector.length);
			int col = i % 3;
			int row = i / 3;

			g.setColor(colorVector[value]);
			g.fillRect(size * col, size*row, size, size);
			g.setColor(Color.BLACK);
			g.drawRect(size * col, size*row, size, size);
			g.setFont(font);
			g.drawString(String.valueOf(value),
					(float) ((size/2) - (fontSize/4)) + size * col,
					(float) (size * 0.8) + size*row);
		}
		g.setRenderingHints(old);
	}

	public boolean isSuitableFor(ISymbol symbol) {
		return (symbol.getSymbolType()%FShape.Z) == FShape.POINT;
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("desc", getDescription());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < preferenceVector.length; i++) {
			sb.append(preferenceVector[i]+" ,");
		}
		String s = sb.substring(0, sb.length()-2);
		xml.putProperty("preferenceVector", s);
		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {
		setDescription(xml.getStringProperty("desc"));
		preferenceVector = xml.getByteArrayProperty("preferenceVector");
	}

	public void drawOutline(Graphics2D g, Rectangle r) {
		drawInsideRectangle(g, r);
	}

	public byte[] getPreferenceVector() {
		return preferenceVector;
	}
}
