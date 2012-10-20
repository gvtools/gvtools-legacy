package org.gvsig.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.TimerTask;

import com.iver.cit.gvsig.fmap.MapControl;

public class MyTask extends TimerTask {
	private MapControl mapCtrl;
	private int maxCount;
	private Point2D.Double pR;
	private Color color;

	public MyTask(MapControl mapCtrl, int maxCount, double x, double y) {
		this.mapCtrl = mapCtrl;
		this.maxCount = maxCount;
		this.pR = new Point2D.Double(x, y);
		this.color = Color.RED;

	}

	public MyTask(MapControl mapCtrl, Color color, int maxCount, double x,
			double y) {
		this.mapCtrl = mapCtrl;
		this.maxCount = maxCount;
		this.pR = new Point2D.Double(x, y);
		this.color = color;
	}

	@Override
	public void run() {
		maxCount--;
		if (maxCount < 0) {
			cancel();
			return;
		}

		Graphics2D g2 = (Graphics2D) mapCtrl.getGraphics();
		RenderingHints renderHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(renderHints);

		Point2D p = mapCtrl.getViewPort().fromMapPoint(pR);

		float[] RGBColor = null;
		;
		RGBColor = this.color.getColorComponents(RGBColor);
		Color color = new Color(RGBColor[0], RGBColor[1], RGBColor[2],
				0.2f - 0.025f * maxCount);
		// System.out.println("maxCount = " + maxCount);
		g2.setColor(color);
		g2.fillArc((int) p.getX() - 20, (int) p.getY() - 20, 40, 40, 0, 360);
		if (maxCount == 0) {
			Stroke stroke = new BasicStroke(2.0f);
			g2.setStroke(stroke);

			g2.setColor(this.color);
			g2.drawArc((int) p.getX() - 20, (int) p.getY() - 20, 40, 40, 0, 360);
			// g2.setColor(new Color(150, 255, 150, 40));
			// g2.fillArc((int)p.getX()-21, (int) p.getY()-21, 42, 42, 0, 360);
		}

		// mapCtrl.repaint();
	}

}
