/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2009 Iver T.I.  {{Task}}
 */

package org.gvsig.remoteClient.wfs.filters;

import java.awt.geom.Rectangle2D;

import org.gvsig.remoteClient.gml.GMLTags;

public class GMLBBox {
	private final String namespace = "gml:";

	public String createBBOX(Rectangle2D r2d) {
		StringBuffer gml = new StringBuffer();
		gml.append(createInitLabel(namespace + GMLTags.GML_BOX));
		gml.append(createInitLabel(namespace + GMLTags.GML_COORD));
		gml.append(createInitLabel(namespace + "X"));
		gml.append(r2d.getMinX());
		gml.append(createEndLabel(namespace + "X"));
		gml.append(createInitLabel(namespace + "Y"));
		gml.append(r2d.getMinY());
		gml.append(createEndLabel(namespace + "Y"));
		gml.append(createEndLabel(namespace + GMLTags.GML_COORD));
		gml.append(createInitLabel(namespace + GMLTags.GML_COORD));
		gml.append(createInitLabel(namespace + "X"));
		gml.append(r2d.getMaxX());
		gml.append(createEndLabel(namespace + "X"));
		gml.append(createInitLabel(namespace + "Y"));
		gml.append(r2d.getMaxY());
		gml.append(createEndLabel(namespace + "Y"));
		gml.append(createEndLabel(namespace + GMLTags.GML_COORD));
		gml.append(createEndLabel(namespace + GMLTags.GML_BOX));
		return gml.toString();
	}

	private String createInitLabel(String label) {
		return "<" + label + ">";
	}

	private String createEndLabel(String label) {
		return "</" + label + ">";
	}
}
