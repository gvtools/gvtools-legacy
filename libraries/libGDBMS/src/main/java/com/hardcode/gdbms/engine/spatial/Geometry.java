package com.hardcode.gdbms.engine.spatial;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.geotools.renderer.style.Style2D;


/**
 * Geometry used to simple operations such as draw
 */
public interface Geometry {
    /**
     * Draws the geometry on the specified Graphics2D with
     * the specified style
     *
     * @param g where to draw
     * @param style symbol to draw
     */
    public void draw(Graphics2D g, Style2D style);

    /**
     * transforms the geometry
     *
     * @param mt transformation
     */
    public void transform(AffineTransform mt);

    /**
     * Returns true if the geometry intersects with the rectangular
     * area defined in r
     *
     * @param r Rectangular area
     *
     * @return boolean
     */
    public boolean intersects(Rectangle2D r);
}
