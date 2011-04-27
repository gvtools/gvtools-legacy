package com.hardcode.gdbms.engine.data.spatial;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import junit.framework.TestCase;

import com.hardcode.gdbms.engine.spatial.ViewPort;

/**
 * 
 */
public class ViewportTests extends TestCase{
    ViewPort vp;
    
    public void setUp(){
        vp = new ViewPort(100, 100, null);
        vp.setExtent(new Rectangle2D.Double(0, 0, 2000, 1000));
    }
    
    public void testAdjustExtent() {
        Rectangle2D adj = vp.getAdjustedExtent();
        assertTrue(adj.getX() == 0);
        assertTrue(adj.getY() == -500);
        assertTrue(adj.getWidth() == 2000);
        assertTrue(adj.getHeight() == 2000);
    }
    
    public void testTransform() {
        AffineTransform at = vp.getTransformationMatrix();
        Point2D src = new Point2D.Double(0, 0);
        Point2D dest = at.transform(src, null);
        assertTrue(dest.getX() == 0);
        assertTrue(dest.getY() == 75);
    }
}
