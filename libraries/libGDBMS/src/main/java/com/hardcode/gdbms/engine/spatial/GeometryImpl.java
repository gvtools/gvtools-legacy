package com.hardcode.gdbms.engine.spatial;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.geotools.renderer.style.Style2D;

import com.hardcode.gdbms.engine.values.AbstractValue;
import com.hardcode.gdbms.engine.values.ValueWriter;


/**
 *
 */
public class GeometryImpl extends AbstractValue implements Geometry {
    private GeneralPath gp;

    /**
     * Creates a new Polyline object.
     *
     * @param gp DOCUMENT ME!
     */
    public GeometryImpl(GeneralPath gp) {
        this.gp = gp;
    }

    public GeneralPath getGp() {
        return gp;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "shape";
    }

    /**
     * @see com.hardcode.gdbms.engine.spatial.Geometry#draw(java.awt.Graphics2D,
     *      org.geotools.renderer.style.Style2D)
     */
    public void draw(Graphics2D g, Style2D style) {
        Renderer.drawShape(g, gp, style);
    }

    /**
     * @see com.hardcode.gdbms.engine.spatial.Geometry#intersects(java.awt.geom.Rectangle2D)
     */
    public boolean intersects(Rectangle2D r) {
        return gp.intersects(r);
    }

	/**
	 * @see com.hardcode.gdbms.engine.spatial.Geometry#transform(java.awt.geom.AffineTransform)
	 */
	public void transform(AffineTransform mt) {
		gp.transform(mt);
	}

    /**
     * @see com.hardcode.gdbms.engine.values.Value#doHashCode()
     */
    public int doHashCode() {
        return gp.doHashCode();
    }

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getStringValue(com.hardcode.gdbms.engine.data.driver.ValueWriter)
     */
    public String getStringValue(ValueWriter writer) {
        //TODO falta por implementar
        throw new UnsupportedOperationException();
    }

    /**
     * @see com.hardcode.gdbms.engine.values.Value#getSQLType()
     */
    public int getSQLType() {
        return PTTypes.GEOMETRY;
    }

	public int getWidth() {
		// TODO Auto-generated method stub
		return gp.numCoords;
	}
}
