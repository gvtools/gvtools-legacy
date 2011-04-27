/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2007 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.fmap.raster.legend;

import java.awt.Color;

import org.gvsig.raster.datastruct.ColorItem;
import org.gvsig.raster.datastruct.ColorTable;
import org.gvsig.raster.util.MathUtils;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IRasterLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendContentsChangedListener;
import com.iver.cit.gvsig.fmap.rendering.SymbolLegendEvent;
import com.iver.utiles.XMLEntity;
/**
 * Leyenda para tablas de color aplicadas a un raster.
 *
 * @version 27/06/2007
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class ColorTableLegend implements IClassifiedLegend, IRasterLegend {
	ISymbol[] symbols = null;
	String[] desc = null;

	/**
	 * Crea una leyenda de tipo ColorTableLegend a partir de un objeto ColorTable
	 * @param colorTable
	 * @return ColorTableLegend
	 */
	public static ColorTableLegend createLegend(ColorTable colorTable) {
		if ((colorTable == null) || (colorTable.getColorItems() == null))
			return null;

		SimpleLineSymbol line = new SimpleLineSymbol();
		line.setLineColor(Color.BLACK);
		ISymbol[] symbol = new ISymbol[colorTable.getColorItems().size()];
		String[] desc = new String[colorTable.getColorItems().size()];

		for (int i = 0; i < colorTable.getColorItems().size(); i++) {
			SimpleFillSymbol s = new SimpleFillSymbol();
			s.setOutline(line);
			s.setFillColor(((ColorItem) colorTable.getColorItems().get(i)).getColor());
			if (i < (colorTable.getColorItems().size() - 1))
				if ( 	((ColorItem) colorTable.getColorItems().get(i)).getNameClass() != null &&
						((ColorItem) colorTable.getColorItems().get(i)).getNameClass().length() > 0	) {
					/* if we have a class label, then we display that instead of the data range */
					desc[i] = ((ColorItem) colorTable.getColorItems().get(i)).getNameClass();
				} else {
					/* no label? display data range! */
					desc[i] = MathUtils.format(((ColorItem) colorTable.getColorItems().get(i)).getValue(), 2)
					+ " " + PluginServices.getText(new ColorTable(), "hasta") + " "					
					+ MathUtils.format(((ColorItem) colorTable.getColorItems().get(i + 1)).getValue(), 2); 
				}
			else {
				/* same above, but for last legend item */
				if ( 	((ColorItem) colorTable.getColorItems().get(i)).getNameClass() != null &&
						((ColorItem) colorTable.getColorItems().get(i)).getNameClass().length() > 0	) {
					desc[i] = ((ColorItem) colorTable.getColorItems().get(i)).getNameClass(); 
				} else {
					desc[i] = MathUtils.format(((ColorItem) colorTable.getColorItems().get(i)).getValue(), 2) + ""; 
				}
			}			
			symbol[i] = s;
		}

		return new ColorTableLegend(symbol, desc);
	}

	/**
	 * Leyenda para tablas de color raster.
	 * @param s Lista de simbolos
	 * @param d Lista de descripciones de simbolos
	 */
	public ColorTableLegend(ISymbol[] s, String[] d) {
		symbols = s;
		desc = d;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend#getDescriptions()
	 */
	public String[] getDescriptions() {
		return desc;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend#getSymbols()
	 */
	public ISymbol[] getSymbols() {
		return symbols;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.IClassifiedLegend#getValues()
	 */
	public Object[] getValues() {
		return desc;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.ILegend#cloneLegend()
	 */
	public ILegend cloneLegend() throws XMLException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.ILegend#getDefaultSymbol()
	 */
	public ISymbol getDefaultSymbol() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.ILegend#getSLDString(java.lang.String)
	 */
	public String getSLDString(String layerName) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.rendering.ILegend#getXMLEntity()
	 */
	public XMLEntity getXMLEntity() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.utiles.IPersistance#getClassName()
	 */
	public String getClassName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.utiles.IPersistance#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	public void setXMLEntity(XMLEntity xml) {
	}

	public void addLegendListener(LegendContentsChangedListener listener) {
		// TODO Auto-generated method stub
	}

	public void fireDefaultSymbolChangedEvent(SymbolLegendEvent event) {
		// TODO Auto-generated method stub
		
	}

	public LegendContentsChangedListener[] getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void removeLegendListener(LegendContentsChangedListener listener) {
		// TODO Auto-generated method stub
	}
}