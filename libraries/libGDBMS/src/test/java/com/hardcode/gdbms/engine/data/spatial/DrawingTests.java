package com.hardcode.gdbms.engine.data.spatial;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.geotools.renderer.style.MarkStyle2D;
import org.geotools.renderer.style.PolygonStyle2D;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SpatialDataSource;
import com.hardcode.gdbms.engine.spatial.Geometry;
import com.hardcode.gdbms.engine.spatial.ViewPort;

public class DrawingTests extends DataSourceTestCase {
	public void testDraw() throws Exception {
		SpatialDataSource d = (SpatialDataSource) ds.createRandomDataSource(
				"dxfprueba", DataSourceFactory.MANUAL_OPENING);

		BufferedImage image = new BufferedImage(600, 600,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		ViewPort vp = new ViewPort(600, 600, g);
		d.start();
		vp.setExtent(d.getFullExtent());

		PolygonStyle2D polygonStyle = new PolygonStyle2D();
		polygonStyle.setStroke(new BasicStroke(2));
		polygonStyle.setContour(Color.RED);
		polygonStyle.setContourComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f));
		polygonStyle.setFillComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f));
		MarkStyle2D markStyle = new MarkStyle2D();
		markStyle.setStroke(new BasicStroke());
		markStyle.setContourComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f));
		markStyle.setFill(new Color(0x808080));
		markStyle.setFillComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 1.0f));
		markStyle.setSize(6);
		markStyle.setRotation(0f);
		// markStyle.setShape(Java2DMark.getWellKnownMark("square"));

		for (int i = 0; i < d.getRowCount(); i++) {
			Geometry geo = (Geometry) d.getGeometry(i);
			if (geo != null) {
				geo.transform(vp.getTransformationMatrix());
				geo.draw(g, polygonStyle);
			}
		}
		d.stop();

		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(new JLabel(new ImageIcon(image)),
				BorderLayout.CENTER);
		f.setSize(100, 100);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		while (true)
			;
	}
}
