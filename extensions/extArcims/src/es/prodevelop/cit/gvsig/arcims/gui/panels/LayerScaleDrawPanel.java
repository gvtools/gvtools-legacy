/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.LayerScaleData;
import es.prodevelop.cit.gvsig.arcims.gui.dialogs.LayerScaleDialog;

/**
 * This is the 'canvas' where layer scale limits status is painted.
 * 
 * @author jldominguez
 * 
 */
public class LayerScaleDrawPanel extends JPanel {
	private static final long serialVersionUID = 0;
	private LayerScaleDialog parentDialog;
	private Vector layerLabelsVector = new Vector();
	private Vector layerInfo = new Vector();
	private float currentScale;
	private int dpi = 96;
	private String fontName = "Dialog";
	private rulerItem[] rulerItems;

	// ----------------------------------------------------------------------------
	private double minScale = 500.0;
	private double maxScale = 10000000.0;
	private int minScaleX = 93; // 88; // +- 8 for less than and more than
	private int maxScaleX = 762;
	private int margin = 8;
	private int rowHeight = 12;
	private int rowSep = 15;
	private int parentDialogMinLimitForNormalTitle = 485;
	private int parentDialogMinLimitForShortTitle = 350;
	private Color carrilColor = new Color(219, 219, 219);
	private Color scaleLineColor = new Color(199, 106, 191);

	// ----------------------------------------------------------------------------
	private double logDist = Math.log(maxScale) - Math.log(minScale);
	private String title = "";
	private double medScale;
	private JLabel updateLabel;

	/**
	 * @param info
	 *            this vector contains the layers' scale info
	 */
	public LayerScaleDrawPanel(Vector info, LayerScaleDialog dlg, JLabel ulabel) {
		super();
		parentDialog = dlg;
		updateLabel = ulabel;

		LayerScaleData theInfo;
		LayerLabel ll;
		currentScale = (float) 1.0;
		setBackground(Color.WHITE);
		setLayout(null);

		// layerInfo = invertedVector(info);
		layerInfo = copyVector(info, true);

		int size = info.size();

		for (int i = 0; i < size; i++) {
			theInfo = (LayerScaleData) layerInfo.get(i);
			ll = new LayerLabel(theInfo.getName(), theInfo.getId());

			ll.setBounds(5, indexDomainToYDomain(i), getLabelsWidth(),
					rowHeight);
			// ll.setBounds(5, indexDomainToYDomain((size - 1) - i),
			// getLabelsWidth(), rowHeight);

			layerLabelsVector.add(ll);
			add(ll);
		}

		layerInfo = copyVector(layerInfo, true);

		rulerItems = getRulerItems();
		medScale = Math.sqrt(maxScale * minScale);
		medScale = Math.sqrt(medScale * minScale);
	}

	public void resetInfo(Vector info) {
		layerLabelsVector.removeAllElements();
		removeAll();

		layerInfo = info;

		LayerScaleData theInfo;
		LayerLabel ll;
		int size = info.size();

		for (int i = 0; i < size; i++) {
			theInfo = (LayerScaleData) layerInfo.get(i);
			ll = new LayerLabel(theInfo.getName(), theInfo.getId());
			ll.setBounds(5, indexDomainToYDomain((size - 1) - i),
					getLabelsWidth(), rowHeight);
			layerLabelsVector.add(ll);
			add(ll);
		}
	}

	private Vector copyVector(Vector v, boolean inverted) {
		Vector res = new Vector();

		if (inverted) {
			for (int i = (v.size() - 1); i >= 0; i--)
				res.add(v.get(i));
		} else {
			for (int i = 0; i < v.size(); i++)
				res.add(v.get(i));
		}

		return res;
	}

	/**
	 * Sets current scale (it is represented as a vertical line)
	 * 
	 * @param s
	 */
	public void setCurrentScale(double s) {
		currentScale = (float) s;
		updateLabel
				.setText(PluginServices.getText(this, "Escala")
						+ "  1 : "
						+ LayerScaleDialog.getFormattedInteger(Math
								.round(currentScale)));
	}

	public void setCurrentScale(float s) {
		currentScale = s;
		updateLabel
				.setText(PluginServices.getText(this, "Escala")
						+ "  1 : "
						+ LayerScaleDialog.getFormattedInteger(Math
								.round(currentScale)));
	}

	public float getCurrentScale() {
		return currentScale;
	}

	private int getLabelsWidth() {
		return minScaleX - margin - 5;
	}

	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;

		maxScaleX = getWidth() - 20;

		if (getParentWidth() < parentDialogMinLimitForNormalTitle) {
			if (getParentWidth() < parentDialogMinLimitForShortTitle) {
				setEmptyTitle();
			} else {
				setShortTitle();
			}
		} else {
			setNormalTitle();
		}

		drawTitle(g2d);

		g2d.setStroke(new BasicStroke(1));
		g2d.setFont(new Font(fontName, Font.PLAIN, 12));

		int size = layerInfo.size();

		for (int i = 0; i < size; i++) {
			LayerScaleData info = (LayerScaleData) layerInfo.get(i);

			int x_min;
			int y_min;
			int w;
			int x_max = this.scaleDomainToXDomain(Math.pow(10.0, 9.0));

			x_min = scaleDomainToXDomain(info.getMinSc());
			w = scaleDomainToXDomain(info.getMaxSc()) - x_min;
			// y_min = indexDomainToYDomain(i);

			y_min = indexDomainToYDomain((size - 1) - i);

			g2d.setColor(carrilColor);
			g2d.fillRect((minScaleX - margin), y_min, x_max
					- (minScaleX - margin), rowHeight);

			g2d.setColor(info.getColor(currentScale));
			g2d.fillRect(x_min, y_min, w, rowHeight);

			g2d.setColor(Color.BLACK);
		}

		drawRuler(g2d, size);
		drawCurrentScale(g2d, size);

		int x = Math.min(getWidth() - 136,
				((getParentWidth() - parentDialogMinLimitForNormalTitle) / 2)
						+ (parentDialogMinLimitForNormalTitle - 136));
		drawLegend(g2d, x, 10);
	}

	private void setEmptyTitle() {
		title = "";
	}

	private void setNormalTitle() {
		title = PluginServices.getText(this, "scale_limits_status");
	}

	private void setShortTitle() {
		title = PluginServices.getText(this, "scale_limits");
	}

	private void drawTitle(Graphics2D g) {
		g.setColor(Color.GRAY);
		g.setFont(new Font(fontName, Font.BOLD, 20));
		g.drawString(title, 20, 55);
	}

	private void drawRuler(Graphics2D g, int i) {
		g.setColor(Color.DARK_GRAY);

		int x_min = this.scaleDomainToXDomain(minScale - 1.0);
		int x_max = this.scaleDomainToXDomain(maxScale + 1.0);
		int y = this.indexDomainToYDomain(0) - 10;
		int offsetFromTagsToRuler = 8;

		g.drawLine(x_min, y, x_max, y);

		// arrows
		int[] xp = { x_min - 2, x_min + 5, x_min + 5 };
		int[] yp = { y, y - 3, y + 3 };
		g.fillPolygon(xp, yp, 3);

		int[] x2p = { x_max + 2, x_max - 5, x_max - 5 };
		g.fillPolygon(x2p, yp, 3);

		// ---------------- scale label -------------------------
		g.setFont(new Font(fontName, Font.BOLD, 12));
		g.drawString(PluginServices.getText(this, "the_scale"), 20, y - 44);
		g.drawString("(DPI = " + dpi + ")", 20, (y + 12) - 44);
		g.setFont(new Font(fontName, Font.PLAIN, 12));

		// ------------------------------------------------------
		rulerItem ruIt;

		g.drawString("1:", scaleDomainToXDomain(minScale) - 30, y
				- offsetFromTagsToRuler);

		AffineTransform oldt = g.getTransform();
		AffineTransform newt;
		AffineTransform rott;
		AffineTransform trat;
		double angulo = -65.0;

		for (int ind = 0; ind < rulerItems.length; ind++) {
			ruIt = this.rulerItems[ind];
			g.drawLine(ruIt.getScaleInXDomain(), y - 3,
					ruIt.getScaleInXDomain(), y + 3);

			int strx = ruIt.getScaleInXDomain();
			int stry = y - offsetFromTagsToRuler;
			trat = AffineTransform.getTranslateInstance(1.0 * strx, 1.0 * stry);
			rott = AffineTransform
					.getRotateInstance((angulo * 3.1415926) / 180.0);
			newt = (AffineTransform) oldt.clone();
			newt.concatenate(trat);
			newt.concatenate(rott);
			g.setTransform(newt);
			g.drawString(ruIt.getTag(), 0, 0);
			g.setTransform(oldt);
		}
	}

	private int indexDomainToYDomain(int i) {
		return Math.round((float) (158.0 + (rowSep * i)));
	}

	private int scaleDomainToXDomain(double d) {
		if (d < minScale) {
			return minScaleX - margin;
		}

		if (d > maxScale) {
			return maxScaleX + margin;
		}

		double dist = Math.log(d) - Math.log(minScale);

		return Math
				.round((float) ((1.0 * minScaleX) + ((dist * (maxScaleX - minScaleX)) / logDist)));
	}

	private void drawCurrentScale(Graphics2D g, int i) {
		int footLength = 90;
		int x = this.scaleDomainToXDomain(currentScale);
		int y_min = indexDomainToYDomain(0) - 10;
		int y_max = indexDomainToYDomain(layerInfo.size()) + 18;

		Stroke old = g.getStroke();
		g.setStroke(new BasicStroke(1));
		g.setColor(scaleLineColor);
		g.drawLine(x, y_min, x, y_max);

		// little square
		int[] xp = { x - 1, x + 2, x + 2, x - 1 };
		int[] yp = { y_min - 1, y_min - 1, y_min + 2, y_min + 2 };
		g.fillPolygon(xp, yp, 4);

		if (currentScale > medScale) { // label to the left
			g.drawLine(x, y_max, x - footLength, y_max);
			g.setColor(LayerScaleData.darker(scaleLineColor));
			g.setFont(new Font(fontName, Font.BOLD, 12));
			g.drawString(PluginServices.getText(this, "current_scale"), x
					- footLength, y_max - 4);
		} else { // label to the right
			g.drawLine(x, y_max, x + 10, y_max);
			g.setColor(LayerScaleData.darker(scaleLineColor));
			g.setFont(new Font(fontName, Font.BOLD, 12));
			g.drawString(PluginServices.getText(this, "current_scale"), x + 15,
					y_max + 4);
		}

		g.setFont(new Font(fontName, Font.PLAIN, 12));
		g.setStroke(old);
	}

	private void drawLegend(Graphics2D g, int orx, int ory) {
		// width = 2 * margin + 2 * sampleW + labelW = 126
		// height = 2 * margin + 4 * rowSep = 76
		int sampleW = 25;
		int margin = 8;
		int labelW = 60;

		// -----------------------------------------
		int correction = -2;
		int smpx = orx + margin + labelW;
		int smpy = ory + margin + (2 * rowSep) + (rowSep - rowHeight);
		int auxx;
		int auxy;

		g.setFont(new Font(fontName, Font.PLAIN, 12));

		auxx = orx + margin;
		auxy = ory + (2 * rowSep) + margin;
		g.setColor(Color.GRAY);
		g.drawString(PluginServices.getText(this, "type"), auxx, auxy
				+ correction);

		auxy = auxy + rowSep;
		g.setColor(Color.BLACK);
		g.drawString(PluginServices.getText(this, "vectorial"), auxx, auxy);

		auxy = auxy + rowSep;
		g.drawString(PluginServices.getText(this, "raster"), auxx, auxy);

		auxx = orx + margin + labelW;
		auxy = ory + rowSep + margin;
		g.setColor(Color.GRAY);
		g.drawString(PluginServices.getText(this, "visible"), auxx, auxy
				+ correction);

		auxy = auxy + rowSep;
		g.setFont(new Font(fontName, Font.PLAIN, 10));
		g.setColor(Color.BLACK);
		g.drawString(PluginServices.getText(this, "YES"), auxx, auxy
				+ correction);

		auxx = auxx + sampleW;
		g.drawString(PluginServices.getText(this, "NO"), auxx, auxy
				+ correction);

		// --------- samples ---------------------
		g.setColor(LayerScaleData.featYesColor);
		g.fillRect(smpx, smpy, sampleW, rowHeight);

		g.setColor(LayerScaleData.featNoColor);
		g.fillRect(smpx + sampleW, smpy, sampleW, rowHeight);

		g.setColor(LayerScaleData.imagYesColor);
		g.fillRect(smpx, smpy + rowSep, sampleW, rowHeight);

		g.setColor(LayerScaleData.imagNoColor);
		g.fillRect(smpx + sampleW, smpy + rowSep, sampleW, rowHeight);

		g.setColor(Color.BLACK);
		g.drawRect(orx, ory, (2 * margin) + (2 * sampleW) + labelW,
				(2 * margin) + (4 * rowSep));

		// ------------------------------------------
	}

	// public void setLayerInfo(Vector v) {
	// layerInfo = invertedVector(v);
	// }
	public void setDpi(int dpi) {
		this.dpi = dpi;
	}

	/**
	 * 
	 * @return Scale items shown on ruler
	 */
	private rulerItem[] getRulerItems() {
		rulerItem[] ri = new rulerItem[12];
		ri[0] = new rulerItem(500);
		ri[1] = new rulerItem(2000);
		ri[2] = new rulerItem(5000);
		ri[3] = new rulerItem(10000);
		ri[4] = new rulerItem(25000);
		ri[5] = new rulerItem(50000);
		ri[6] = new rulerItem(100000);
		ri[7] = new rulerItem(250000);
		ri[8] = new rulerItem(500000);
		ri[9] = new rulerItem(1000000);
		ri[10] = new rulerItem(5000000);
		ri[11] = new rulerItem(10000000); // must be the same as 'private double
											// maxScale'

		return ri;
	}

	private int getParentWidth() {
		if (parentDialog == null) {
			return parentDialogMinLimitForNormalTitle;
		} else {
			return parentDialog.getWidth();
		}
	}

	/**
	 * Utility class used to allow tool tips.
	 * 
	 * @author jldominguez
	 * 
	 */
	public class LayerLabel extends JLabel {
		private static final long serialVersionUID = 0;
		private String theName = "";
		private String theId = "";

		public LayerLabel(String name, String id) {
			theName = name;
			theId = id;
			setToolTipText(toolTipString());
			setText(toString());
			setFont(new Font(fontName, Font.BOLD, 12));
		}

		public String toString() {
			if (theName.length() < 19) {
				return theName;
			}

			return theName.substring(0, 16) + "...";
		}

		public String toolTipString() {
			return "[" + theId + "] " + theName;
		}
	}

	private class rulerItem {
		private int scale;

		public rulerItem(int sc) {
			scale = sc;
		}

		public String getTag() {
			return intToAbbrev(scale);
		}

		public int getScale() {
			return scale;
		}

		public int getScaleInXDomain() {
			return scaleDomainToXDomain(1.0 * scale);
		}

		public int xAxisOffset() {
			return (getTag().length() * 4) + 3;
		}

		private String intToAbbrev(int n) {
			if (n >= 1000000) {
				return String.valueOf(n / 1000000) + " M";
			}

			return String.valueOf(n);
		}
	}
}
