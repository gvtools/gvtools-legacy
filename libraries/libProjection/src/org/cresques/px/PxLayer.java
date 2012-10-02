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

import java.awt.Graphics2D;

import org.cresques.geo.ViewPortData;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


public class PxLayer extends PxObjList {
    private static int layerSeed = 0x00;
    protected boolean visible = true;
    protected String name = "Capa";
    protected int id = 0;

    public PxLayer(String name, CoordinateReferenceSystem crs) {
        super(crs);
        id = PxLayer.layerSeed++;
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean v) {
        visible = v;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    //	public void draw(Graphics2D g, AffineTransform mat, Extent sz) {
    public void draw(Graphics2D g, ViewPortData vp) {
        if (isVisible()) {
            super.draw(g, vp);
        }
    }
}
