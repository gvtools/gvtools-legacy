/*
 * Cresques Mapping Suite. Graphic Library for constructing mapping applications.
 *
 * Copyright (C) 2004-5.
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
 * cresques@gmail.com
 */
package org.cresques.px;

import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;

import org.cresques.geo.Projected;
import org.cresques.geo.Projection;
import org.cresques.geo.ReProjection;
import org.cresques.geo.ViewPortData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import java.util.Iterator;
import java.util.Vector;


public class PxObjList implements Colored, Drawable, IObjList {
    IProjection proj = null;
    private Color pc = null;
    private Color fColor = null;
    protected Extent extent = null;
    Vector data = null;

    public PxObjList() {
        data = new Vector();
        extent = new Extent();
    }

    public PxObjList(IProjection proj) {
        data = new Vector();
        this.proj = proj;
        extent = new Extent();
    }

    public IProjection getProjection() {
        return proj;
    }

    public void setProjection(IProjection p) {
        proj = p;
    }

    public void reProject(ICoordTrans rp) {
        extent = new Extent();

        Iterator iter = data.iterator();
        Projected obj = null;

        while (iter.hasNext()) {
            obj = (Projected) iter.next();
            obj.reProject(rp);
            extent.add(((Extent.Has) obj).getExtent());
        }

        setProjection(rp.getPDest());
    }

    public Extent getExtent() {
        return extent;
    }

    public Color c() {
        return pc;
    }

    public Color c(Color color) {
        pc = color;

        return pc;
    }

    public void setColor(Color color) {
        pc = color;
    }

    public Color getColor() {
        return pc;
    }

    public void setFillColor(Color color) {
        fColor = color;
    }

    public Color getFillColor() {
        return fColor;
    }

    public void draw(Graphics2D g, ViewPortData vp) {
        System.err.println("draw :" + this + ": " + size() + "objetos.");

        if (pc != null) {
            g.setColor(pc);
        }

        Iterator iter = data.iterator();
        Drawable dwObj = null;

        while (iter.hasNext()) {
            dwObj = (Drawable) iter.next();

            //if (dwObj.getClass() == PxContour.class)
            //	drawPxContour(g, vp, (PxContour) dwObj);
            //else

            /*extent = ((Extent.Has) dwObj).getExtent();
            if (vp.getExtent().minX()> extent.maxX()) continue;
            if (vp.getExtent().minY()> extent.maxY()) continue;
            if (vp.getExtent().maxX()< extent.minX()) continue;
            if (vp.getExtent().maxY()< extent.minY()) continue;*/
            dwObj.draw(g, vp);
        }
    }

    public IObjList getAt(Point2D pt) {
        PxObjList oList = new PxObjList();
        Iterator iter = data.iterator();

        while (iter.hasNext()) {
            PxObj o = (PxObj) iter.next();

            if (o.getExtent().isAt(pt)) {
                oList.add(o);
            }
        }

        return oList;
    }

    public Iterator iterator() {
        return data.iterator();
    }

    public int size() {
        return data.size();
    }

    public void add(Extent.Has obj) {
        if (obj != null) {
            extent.add(obj.getExtent());
            data.add(obj);
        }
    }

    public void remove(Object obj) {
        data.remove(obj);
    }

    public void clear() {
        extent = new Extent();
        data.clear();
    }

    /**
     * Prueba de reproyección.
     *
     */
    public void drawPxContour(Graphics2D g, ViewPortData vp, PxContour obj) {
        IProjection prj = obj.getProjection();
        obj.setColor(pc);
        obj.setFillColor(fColor);

        if (prj != null) {
            if (prj != vp.getProjection()) {
                ICoordTrans rp = null;

                if (proj instanceof Projection) {
                    rp = new ReProjection(((Projection) proj),
                                          ((Projection) vp.getProjection()));
                }

                obj.draw(g, vp, rp);
            } else {
                obj.draw(g, vp);
            }
        } else {
            System.err.println("Proyección nula o inadecuada.");
        }
    }
}
