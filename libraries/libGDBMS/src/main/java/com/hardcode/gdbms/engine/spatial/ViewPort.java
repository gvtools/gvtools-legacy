package com.hardcode.gdbms.engine.spatial;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * Defines a visualization of a spatial area
 */
public class ViewPort {
    private int h;
    private int w;
    private Graphics2D g;
    private Rectangle2D extent;
    private Rectangle2D adjustedExtent;
    private AffineTransform mt;

    /**
     * Creates a new DefaultViewPort object.
     *
     * @param width width of the image to be obtained
     * @param height heigth of the image to be obtained
     * @param g Graphics where to draw
     */
    public ViewPort(int width, int height, Graphics2D g) {
        this.h = height;
        this.w = width;
        this.g = g;
    }

    /**
     * Gets the extent adjusted to the width and heigth of the image
     *
     * @return Rectangle2D
     */
    public Rectangle2D getAdjustedExtent() {
        return adjustedExtent;
    }

    /**
     * Gets the area of data to be drawn
     *
     * @return Rectangle2D
     */
    public Rectangle2D getExtent() {
        return extent;
    }

    /**
     * Sets the area of the data to be drawn
     *
     * @param extent Rectangle2D
     */
    public void setExtent(Rectangle2D extent) {
        this.extent = extent;
        calculateMT();
    }

    public int getWidth() {
        // TODO Auto-generated method stub
        return w;
    }

    public int getHeight() {
        // TODO Auto-generated method stub
        return h;
    }

    public Graphics2D getGraphics() {
        // TODO Auto-generated method stub
        return g;
    }

    public void setHeight(int h) {
        this.h = h;
        calculateMT();
    }

    public void setWidth(int w) {
        this.w = w;
        calculateMT();
    }

    /**
     * matrix used to 'bring' the geometries to the image rectangle
     *
     * @return AffineTransform
     */
    public AffineTransform getTransformationMatrix() {
        return mt;
    }

    /**
     * adjusts the extent and calculates the transformation matrix
     */
    private void calculateMT() {
        Point2D d;
        double rx = w / extent.getWidth();
        double ry = h / extent.getHeight();

        double xCenter = extent.getCenterX();
        double yCenter = extent.getCenterY();

        adjustedExtent = new Rectangle2D.Double();

        if (rx < ry) {
            adjustedExtent.setRect(xCenter - (extent.getWidth() / 2.0),
                yCenter - ((h / rx) / 2.0), extent.getWidth(), h / rx);
            mt = AffineTransform.getScaleInstance(rx, -rx);
        } else {
            adjustedExtent.setRect(xCenter - ((w / ry) / 2.0),
                yCenter - (extent.getHeight() / 2.0), w / ry, extent.getHeight());
            mt = AffineTransform.getScaleInstance(ry, -ry);
        }

        mt.concatenate(AffineTransform.getTranslateInstance(
                -adjustedExtent.getX(),
                -adjustedExtent.getY() - adjustedExtent.getHeight()));
    }
}
