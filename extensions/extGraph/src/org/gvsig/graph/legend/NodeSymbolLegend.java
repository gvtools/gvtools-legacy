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
 * 2009 Software Colaborativo (www.scolab.es)   development
 */

package org.gvsig.graph.legend;

import java.awt.Color;

import org.gvsig.graph.core.Network;

import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;

public class NodeSymbolLegend extends SingleSymbolLegend {

	protected SimpleMarkerSymbol sGreen;
	protected SimpleMarkerSymbol sRed;
	protected SimpleMarkerSymbol sOrange;
	private Network net;

	public NodeSymbolLegend() {
		sGreen = createSymbol(6, new Color(0, 255, 0, 80), new Color(0, 255, 0,
				180));
		sOrange = createSymbol(6, new Color(100, 255, 100, 80), new Color(100,
				255, 100, 180));
		sRed = createSymbol(9, new Color(255, 0, 0, 80), new Color(255, 0, 0,
				180));
	}

	public void setNetwork(Network net) {
		this.net = net;
	}

	private SimpleMarkerSymbol createSymbol(int pixelSize, Color fillColor,
			Color outlineColor) {
		SimpleMarkerSymbol s = new SimpleMarkerSymbol();
		s.setCartographicSize(pixelSize, null);
		s.setColor(fillColor);
		sGreen.setIsShapeVisible(false);
		sGreen.setOutlined(true);
		sGreen.setOutlineColor(outlineColor);
		return s;
	}

	@Override
	public ISymbol getSymbol(int recordIndex) {
		return super.getSymbol(recordIndex);
	}

	@Override
	public boolean isSuitableForShapeType(int shapeType) {
		return true;
	}

}
